package cn.edu.uestc.platform.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;

import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.winter.openstack.ServerUtils;

/**
 * Openstack网络信息工具类
 */
public class NetworkUtils {
	private static Logger logger = Logger.getLogger(NetworkUtils.class);

	/**
	 * 根据传过来的ip地址，生成子网网段===============>无DHCP，适用于静态场景 XXXXXXX 不再使用，使用
	 * DynamicNetWorkUtils.createSubnetByIP
	 */
	public static Subnet createSubnetByIP(String ip) {
		// 必须使用管理员进行认证
		OSClientV3 os = OSClientFactory.authenticate("admin", "123456", Constants.ADMIN_PROJECT_ID);
		//获得openstack项目		f766e0582674439fbd935cedfa404947  项目id
		Project project = os.identity().projects().get("f766e0582674439fbd935cedfa404947");
		
		Network network = os.networking().network().create(Builders.network()
				.name("link" + ip.substring(0, ip.lastIndexOf(".")) + ".0").tenantId(project.getId()).build());// 网络的名称为link+ip   xxx.xxx.xxx+.0
		Subnet subnet = os.networking().subnet()// 无DHCP
				.create(Builders.subnet().name("link" + ip.substring(0, ip.lastIndexOf(".")) + ".0")
						.networkId(network.getId()).tenantId(project.getId()).ipVersion(IPVersionType.V4)
						.cidr(ip.substring(0, ip.lastIndexOf(".")) + ".0/24").build());
		logger.info("成功创建了子网：" + ip.substring(0, ip.lastIndexOf(".")) + ".0/24");
		return subnet;
	}

	/**
	 * 根据传来的ip地址，获取到该ip地址对应的网段的id---网络id
	 */
	public static String getIdByIp(String ip) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Subnet> subnets = os.networking().subnet().list();
		for (Subnet subnet : subnets) {
			if (subnet.getCidr().substring(0, subnet.getCidr().lastIndexOf("."))
					.equals(ip.substring(0, ip.lastIndexOf(".")))) {
				return subnet.getNetworkId();
			}
		}
		return null;
	}

	/**
	 * 根据传来的ip地址，获取到该ip地址对应的端口的id---端口id
	 */
	public static String getPortIdByIp(String ip) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Port> ports = os.networking().port().list();
		for (Port port : ports) {
			// System.out.println(port.getFixedIps());
			for (IP name : port.getFixedIps()) {
				if (name.getIpAddress().equals(ip)) {
					return port.getId();
				}
			}
		}
		return null;
	}

	/**
	 * 根据传来的ip地址，获取到该ip地址对应的网段的名称
	 */
	public static String getNameByIp(String ip) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Subnet> subnets = os.networking().subnet().list();
		for (Subnet subnet : subnets) {
			if (subnet.getCidr().substring(0, subnet.getCidr().lastIndexOf("."))
					.equals(ip.substring(0, ip.lastIndexOf(".")))) {
				return subnet.getName();
			}
		}
		return null;
	}

	/**
	 * 返回一个浮动ip，用于添加到Server，这里必须加锁，防止冲突
	 * 
	 * @param os
	 */
	public static FloatingIP createFloatingIp(OSClientV3 os) {
		FloatingIP ip = null;
		synchronized (NetworkUtils.class) {// 对当前类的Class的加锁，由于每个类都是Class的一个实例，所以可以对当前类的Class加锁
			ip = os.compute().floatingIps().allocateIP("ext_zwn");   //ext_zwn : pool  一个网络池
		}
		return ip;
	}

	/**
	 * 通过节点的名称得到节点的浮动ip
	 * 
	 * @param name
	 *            节点名称
	 * @return 节点的浮动ip
	 */
	public static String getFloatIpByNodeName(String name) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Server> servers = os.compute().servers().list();
		for (Server server : servers) {
			if (server.getName().equals(name)) {
				Map<String, List<? extends Address>> all = server.getAddresses().getAddresses();
				List<? extends Address> floatIps = all.get("net");// net为浮动ip所绑定的网段，一定要注意最后要修改这块，否则一直出错
				System.out.println(floatIps);
				for (Address floatIp : floatIps) {
					if (floatIp.getType().equals("floating")) {
						return floatIp.getAddr();
					}
				}
			}
		}
		return null;
	}
	// @Test
	// public void demo1(){
	// getFloatIpByNodeName("vm3");
	// }

	/**
	 * 给定ip和虚拟机的名称，判断虚拟机是否有这个ip地址
	 * 
	 * @param name
	 *            虚拟机名称
	 * @param ip
	 *            给定的ip地址
	 */
	public static boolean isHaveIP(OSClientV3 os, String name, String ip) {
		// Server server = os.compute().servers().get("");// 查找到一个名称所对应的虚拟机
		// 查api 应该含有通过名字 最后得到ServerID的函数 从而去掉一层循环
		// 暂时用ServerUtils中的方法。本质还是三重循环，但去掉了if判断
		Server server = ServerUtils.getServer(name);
		Map<String, List<? extends Address>> map = server.getAddresses().getAddresses();
		Collection<List<? extends Address>> values = map.values();
		for (List<? extends Address> list : values) {
			for (Address address : list) {
				if (address.getAddr().equals(ip)) {
					logger.info("虚拟机 " + name + " 有此 " + ip);
					return true;
				}
			}
		}

		logger.info("虚拟机 " + name + " 没有此 " + ip);
		return false;
	}

	/**
	 * 为一个虚拟机添加端口
	 * 
	 * @param nodeName
	 *            虚拟机的名称
	 * @param ip
	 *            要添加的ip
	 */
	public static void addIPToNode(String nodeName, String ip) {
		SSHExecutorUtils ssh = new SSHExecutorUtils(Constants.CONTROLLER_NODE_NAME, "123456",
				Constants.CONTROLLER_IP_ADDRESS);
		String match;
		try {
			System.out.println(
					"source zph-openrc.sh\nneutron port-create " + getNameByIp(ip) + " --fixed-ip ip_address=" + ip);
			// 相当于给Openstack上的云上添加端口
			match = ssh.exec("source zph-openrc.sh\nneutron port-create " + getNameByIp(ip) + " --fixed-ip ip_address="
					+ ip + "\n");
			// 该命令行输出为固定格式
			String id = match.substring(match.indexOf(" id                    | ") + 25,
					match.indexOf(" id                    | ") + 25 + 36);
			logger.info("成功为虚拟机 " + nodeName + " 分配了id为 " + id + " 的 ip: " + ip);
			// 相当于给Openstack上的实例连接上上面创建的端口(把虚拟机挂在云上)
			System.out.println("source zph-openrc.sh\nnova interface-attach --port-id " + id + " " + nodeName);
			ssh.exec("source zph-openrc.sh\nnova interface-attach --port-id " + id + " " + nodeName + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}

	/**
	 * 进入floatIp，修改网卡配置文件+zebra配置文件---->这里是添加一张网卡
	 * 
	 * @param floatIp
	 *            浮动IP的地址
	 * @param addIp
	 *            要添加的IP
	 */
	public static void changeNetworkConfig(String floatIp, String addIp) {
		ArrayList<String> list = new ArrayList<>();
		SSHExecutorUtils ssh = new SSHExecutorUtils("router", "123456", floatIp);
		String match = "";
		try {
			match = ssh.exec("cat /etc/network/interfaces");
			while (match.indexOf("address ") > 0) {
				list.add(match.substring(match.indexOf("address ") + 8, match.indexOf("\nnetmask")));
				match = match.substring(match.indexOf("\nnetmask") + 8, match.length());
			}
			logger.info("该虚拟机 " + floatIp + " 原网卡为：" + list + ",新添加的网卡为:" + addIp);
			list.add(addIp);
			logger.info("该虚拟机 " + floatIp + " 所有网卡为：" + list);
			// 修改配置文件网卡+zebra
			String[] comds = DataFormatter.changeNetwork(list);
			for (int i = 0; i < comds.length - 1; i++) {
				ssh.exec(comds[i]);
			}
			// 可能是执行重启操作太快了，应该等上边的命令执行完毕后在执行重启操作
			TimeUnit.MILLISECONDS.sleep(500);
			ssh.exec(comds[6]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ssh.close();
	}

	/**
	 * 进入floatIp，修改网卡配置文件+zebra配置文件---->这里是删除一张网卡
	 */
	public static void changeDelNetworkConfig(String floatIp, String addIp) {
		ArrayList<String> list = new ArrayList<>();
		SSHExecutorUtils ssh = new SSHExecutorUtils("router", "123456", floatIp);
		String match = "";
		try {
			match = ssh.exec("cat /etc/network/interfaces");
			while (match.indexOf("address ") > 0) {
				list.add(match.substring(match.indexOf("address ") + 8, match.indexOf("\nnetmask")));
				match = match.substring(match.indexOf("\nnetmask") + 8, match.length());
			}
			logger.info("该虚拟机 " + floatIp + " 原网卡为：" + list + ",新添加的网卡为:" + addIp);
			list.remove(addIp);
			logger.info("该虚拟机 " + floatIp + " 所有网卡为：" + list);
			// 修改配置文件网卡+zebra
			String[] comds = DataFormatter.changeNetwork(list);
			for (int i = 0; i < comds.length - 1; i++) {
				ssh.exec(comds[i]);
			}
			// 可能是执行重启操作太快了，应该等上边的命令执行完毕后在执行重启操作
			TimeUnit.MILLISECONDS.sleep(500);
			ssh.exec(comds[6]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ssh.close();
	}

	/**
	 * 通过虚拟机的名称找到对应ip地址的mac地址
	 * 
	 * @param nodeName
	 *            虚拟机的名称
	 * @param ip
	 *            找到该ip的mac地址
	 */
	public static String findMac(String nodeName, String ip) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Server> servers = os.compute().servers().list();// 查找到一个名称所对应的虚拟机
		for (Server server : servers) {
			Map<String, List<? extends Address>> map = server.getAddresses().getAddresses();
			Collection<List<? extends Address>> values = map.values();
			for (List<? extends Address> list : values) {
				for (Address address : list) {
					if (address.getAddr().equals(ip)) {
						return address.getMacAddr();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 通过ServerID找到一个虚拟机所属的nova节点
	 * 
	 * @param serverID
	 */
	public static String findNoveName(String serverID) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		Server server = os.compute().servers().get(serverID);// 查找到一个名称所对应的虚拟机
		return server.getHypervisorHostname();
	}

	/**
	 * 通过虚拟机的名称和ip找到一个虚拟机Server的ID
	 * 
	 * @param nodeName
	 * @param ip
	 */
	public static String findServerID(String nodeName, String ip) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Server> servers = os.compute().servers().list();// 查找到一个名称所对应的虚拟机
		for (Server server : servers) {
			Map<String, List<? extends Address>> map = server.getAddresses().getAddresses();
			Collection<List<? extends Address>> values = map.values();
			for (List<? extends Address> list : values) {
				for (Address address : list) {
					if (address.getAddr().equals(ip)) {
						return server.getId();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 为一个虚拟机删除端口------>待实现
	 * 
	 * @param nodeName
	 *            虚拟机的名称
	 * @param ip
	 *            要删除的端口ip
	 *            
	 *            
	 *            ssh.exec(cmd) cmd：linux命令行操作指令
	 *            
	 */
	public static void delIPToNode(String nodeName, String ip) {
		SSHExecutorUtils ssh = new SSHExecutorUtils(Constants.CONTROLLER_NODE_NAME, "123456",
				Constants.CONTROLLER_IP_ADDRESS);// 注意controller拼写
		try {
			// 1.先解绑端口
			ssh.exec("source zph-openrc.sh\nnova interface-detach " + nodeName + " " + getPortIdByIp(ip) + "\n");
			// 2.删除端口
			ssh.exec("source zph-openrc.sh\nneutron port-delete " + getPortIdByIp(ip) + "\n");
			// 3.进入虚拟机修改配置文件
			changeDelNetworkConfig(getFloatIpByNodeName(nodeName), ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ssh.close();
	}

	/*
	 * get instance last interfaceName
	 */
	public static String getInterfaceName(String nodeName) {
		SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456",
				NetworkUtils.getFloatIpByNodeName(nodeName));
		try {
			String out = sshExecutor.exec("ifconfig");
			int count = out.lastIndexOf("eth");
			return out.substring(count, count + 5).trim();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
