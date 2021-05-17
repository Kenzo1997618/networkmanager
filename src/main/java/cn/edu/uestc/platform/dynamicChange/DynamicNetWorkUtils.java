package cn.edu.uestc.platform.dynamicChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;

import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;
import cn.edu.uestc.platform.utils.TCUtils;

public class DynamicNetWorkUtils {
	private static Logger logger = Logger.getLogger(DynamicNetWorkUtils.class);

	/**
	 * 根据传过来的ip地址，生成子网网段===============>DHCP，适用于动态场景
	 */
	public static Subnet createSubnetByIP(String ip) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		Project project = os.identity().projects().get(Constants.ZPH_PROJECT_ID);// zph
		System.out.println(project.getId());
//		Network network = os.networking().network()
//				.create(Builders.network().name("link" + ip.substring(0, ip.lastIndexOf(".")) + ".0")
//						.tenantId(project.getId()).adminStateUp(true).build());// 网络的名称为link+ip
//		Subnet subnet = os.networking().subnet()
//				.create(Builders.subnet().name("link" + ip.substring(0, ip.lastIndexOf(".")) + ".0").enableDHCP(true)// 有DHCP
//						.networkId(network.getId()).tenantId(project.getId()).ipVersion(IPVersionType.V4)
//						.cidr(ip.substring(0, ip.lastIndexOf(".")) + ".0/24").build());
//		logger.info("成功创建了子网：" + ip.substring(0, ip.lastIndexOf(".")) + ".0/24");
//		return subnet;
		return null;
	}

	@Test
	public void demo() {
		createSubnetByIP("");
	}

	/**
	 * 根据传过来的ip 删除该网段
	 * 
	 * @param delIp
	 * @return
	 */
	public static void delNetworkByIP(String delIP) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		// 1.先删除端口
		List<? extends Port> ports = os.networking().port().list();
		for (Port port : ports) {
			Set<? extends IP> ips = port.getFixedIps();
			for (IP ip : ips) {
				if (ip.getIpAddress().contains(delIP.substring(0, delIP.lastIndexOf(".")))) {
					os.networking().port().delete(port.getId());
				}
			}
		}
		// 2.删除子网
		List<? extends Subnet> subnets = os.networking().subnet().list();
		for (Subnet subnet : subnets) {
			if (subnet.getName().equals("link" + delIP.substring(0, delIP.lastIndexOf(".")) + ".0")) {
				os.networking().subnet().delete(subnet.getId());
			}
		}
		// 3.删除网段
		List<? extends Network> networks = os.networking().network().list();
		for (Network network : networks) {
			if (network.getName().equals("link" + delIP.substring(0, delIP.lastIndexOf(".")) + ".0")) {
				os.networking().network().delete(network.getId());
			}
		}
	}

	/**
	 * 添加路由软件的配置信息，由于开启了DHCP服务，需要读取ifconfig文件中网卡的配置信息
	 */
	public static void changeAddQuguaConf(String floatIp, String addIp,String nodeName,  String delay, String bw, String loss) {
		System.out.println("exe zhangyang  luyou");
		ArrayList<String> list = new ArrayList<>();
		SSHExecutorUtils ssh = new SSHExecutorUtils("router", "123456", floatIp);
		String match = "";
		try {
			while (list.indexOf(addIp) < 0) {
				System.out.println("sixunhuan1");
				list.clear();
				TimeUnit.SECONDS.sleep(1);
				match = ssh.exec("ifconfig");
				// System.out.println(match);
				while (match.indexOf("inet 地址:") > 0) {
					System.out.println("sixunhuan2");
					if (match.indexOf("  广播:") > 0) {
						list.add(match.substring(match.indexOf("inet 地址:") + 8, match.indexOf("  广播:")));
						match = match.substring(match.indexOf("  广播:") + 8, match.length());
					} else {
						match = match.substring(match.indexOf("inet 地址:") + 8, match.length());
					}
				}
			}
			System.out.println("该虚拟机 " + floatIp + " 网卡为：" + list + " ,新添加的网卡为eth" + list.indexOf(addIp));
			// 修改配置文件网卡+zebra
			String a = ssh.exec("sudo ./login_zebra_fix.sh eth" + list.indexOf(addIp) + " " + addIp + "/24");
			System.out.println(a);
			TimeUnit.SECONDS.sleep(1);
			String b = ssh.exec("sudo ./login_rip_fix.sh " + addIp.substring(0, addIp.lastIndexOf(".")) + ".0/24");
			System.out.println(b);
			TimeUnit.MILLISECONDS.sleep(500);
			// ssh.exec("sudo tc qdisc add dev eth"+list.indexOf(addIp)+" root
			// neten delay 26ms");
			// TCUtils.addTC(nodeName, ip, delay, bw, loss);

			// 3.查看传来的ip对应的是eth几
			String ethNumber = "eth" +list.indexOf(addIp) ;
			System.out.println(ethNumber);
			logger.info(nodeName + " 的  " + addIp + " 对应的网为  " + ethNumber);
			// 4.执行TC命令
//			ssh.exec("sudo tc qdisc add dev " + ethNumber + " root handle 1: htb");
//			ssh.exec("sudo tc class add dev " + ethNumber + " parent 1: classid 1:1 htb rate " + bw + "mbit ceil "
//					+ (Integer.parseInt(bw) + 1) + "mbit burst 50kbit");
//			ssh.exec("sudo tc qdisc add dev " + ethNumber + " parent 1:1 handle 10: netem delay " + delay + "ms"
//					+ " loss " + (int)(Double.parseDouble(loss) * 100) + "%");
//			ssh.exec("sudo tc filter add dev " + ethNumber
//					+ " parent 1:0 protocol ip prio 1 u32 match ip dst 0.0.0.0/0 flowid 1:1");
//			logger.info("执行完了TC的命令");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}

	/**
	 * 删除路由软件的配置信息，由于开启了DHCP服务，需要读取ifconfig文件中网卡的配置信息
	 */
	public static void changeDelQuguaConf(String floatIp, String delIp) {
		ArrayList<String> list = new ArrayList<>();
		SSHExecutorUtils ssh = new SSHExecutorUtils("router", "123456", floatIp);
		String match = "";
		try {
			match = ssh.exec("ifconfig");
			while (match.indexOf("inet 地址:") > 0) {
				System.out.println("sixunhuan");
				if (match.indexOf("  广播:") > 0) {
					list.add(match.substring(match.indexOf("inet 地址:") + 8, match.indexOf("  广播:")));
					match = match.substring(match.indexOf("  广播:") + 8, match.length());
				} else {
					match = match.substring(match.indexOf("inet 地址:") + 8, match.length());
				}
			}
			logger.info("该虚拟机 " + floatIp + " 网卡为：" + list + " ,要删除的网卡为eth" + list.indexOf(delIp));
			// 修改配置文件网卡+zebra
			String a = ssh.exec("sudo ./delete_zebra_ip.sh eth" + list.indexOf(delIp) + " " + delIp + "/24");
			// System.out.println(a);
			TimeUnit.MILLISECONDS.sleep(500);
			String b = ssh.exec("sudo ./delete_rip_network.sh " + delIp.substring(0, delIp.lastIndexOf(".")) + ".0/24");
			// System.out.println(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}

	/**
	 * 为一个虚拟机删除端口
	 * 
	 * @param nodeName
	 *            虚拟机的名称
	 * @param ip
	 *            要删除的端口ip
	 */
	public static void delIPToNode(String nodeName, String ip) {
		SSHExecutorUtils ssh = new SSHExecutorUtils(Constants.CONTROLLER_NODE_NAME, "123456",
				Constants.CONTROLLER_IP_ADDRESS);
		try {
			// 1.先解绑端口
			ssh.exec("source zph-openrc.sh\nnova interface-detach " + nodeName + " " + NetworkUtils.getPortIdByIp(ip)
					+ "\n");// 这块还没有测试
			// 2.删除端口
			ssh.exec("source zph-openrc.sh\nneutron port-delete " + NetworkUtils.getPortIdByIp(ip) + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ssh.close();
	}

	public static void main(String[] args) {
		DynamicNetWorkUtils.createSubnetByIP("10.10.5.10");
	}

}
