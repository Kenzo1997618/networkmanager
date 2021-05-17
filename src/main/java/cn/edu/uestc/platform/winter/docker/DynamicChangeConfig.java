package cn.edu.uestc.platform.winter.docker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;

import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dao.PortDaoImpl;
import cn.edu.uestc.platform.test.newTest1.ZPHTest;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;
import cn.edu.uestc.platform.winter.openstack.ServerUtils;

/**
 * 启动docker实例的quagga程序
 */
public class DynamicChangeConfig {

	private static Logger logger = Logger.getLogger(DynamicChangeConfig.class);
	/**
	 * docker 添加路由软件的配置信息，需要读取ifconfig文件中网卡的配置信息
	 */
	public static void changeAddQuguaConf(String nodeName, String addIp) {
		ArrayList<String> list = new ArrayList<>();// 所有IP地址按照顺序排列
		SSHExecutorUtils ssh = new SSHExecutorUtils(Constants.DOCKER_NODE_USERNAME, "123456",
				Constants.DOCKER_NODE_IP_ADDRESS);
		String match = "";
		String first = "";
		String eth = "";// 网卡名称
		int count = 20;
		try {
			while (list.indexOf(addIp) < 0 && count > 0) {
				count--;
				list.clear();
				TimeUnit.SECONDS.sleep(1);
				match = ssh.exec("sudo docker exec " + nodeName + " ifconfig");
				first = match.substring(match.indexOf("inet addr:") + 10, match.length());
				System.out.println("防死循环。。。。1");
				while (first.indexOf("inet addr:") > 0) {
					System.out.println("防死循环。。。。2");
					if (first.indexOf("  Bcast:") > 0) {
						String ip = first.substring(first.indexOf("inet addr:") + 10, first.indexOf("  Bcast:"));
						list.add(ip);
						if (ip.equals(addIp)) {
							eth = first.substring(first.indexOf("\nns") + 1, first.indexOf(" Link encap:Ethernet"));
						}
						first = first.substring(first.indexOf("  Bcast:") + 8, first.length());
					} else {
						first = first.substring(first.indexOf("inet addr:") + 10, first.length());
					}
				}
				System.out.println("防死循环。。。。3");
			}
			if (count > 0) {
				System.out.println("新增加的网卡为 " + eth + " " + addIp);
				// 成功拿到网卡的名称,执行zebra脚本
				ssh.exec("sudo docker exec -d " + nodeName + " ./rip_add.sh " + eth + " " + addIp + "/24 "
						+ addIp.substring(0, addIp.lastIndexOf(".")) + ".0/24");

				// 3.查看传来的ip对应的是eth几
				double delay = new LinkDaoImpl().getLinkByPortID(new PortDaoImpl().getPortByIP(addIp).getPt_id())
						.getLinkLength() / (3.0E+02);

				String ethNumber = "eth" + list.indexOf(addIp);
				System.out.println(ethNumber);
				 logger.info(nodeName + " 的 " + addIp + " 对应的网为 " +
				 ethNumber);
				System.out.println(nodeName + " 的  " + addIp + " 对应的网为  " + ethNumber);
//				 4.执行TC命令
//				ssh.exec("sudo tc qdisc add dev " + ethNumber + " root handle 1: htb");
//				ssh.exec("sudo tc class add dev " + ethNumber + " parent 1: classid 1:1 htb rate " + 200 + "mbit ceil "
//						+ (201) + "mbit burst 50kbit");
//				ssh.exec("sudo tc qdisc add dev " + ethNumber + " parent 1:1 handle 10: netem delay " + delay + "ms"
//						+ " loss 0.01%");
//				ssh.exec("sudo tc filter add dev " + ethNumber
//						+ " parent 1:0 protocol ip prio 1 u32 match ip dst 0.0.0.0/0 flowid 1:1");
//				
				ssh.exec("sudo docker exec -d tc qdisc add dev " + eth + " root netem delay " + delay + "ms" );
				 logger.info("执行完了TC的命令");
				System.out.println("执行完了TC的命令");

			} else {
				System.out.println("fail to add" + eth + " " + addIp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}

	/**
	 * docker 删除路由软件的配置信息，需要读取ifconfig文件中网卡的配置信息
	 */
	public static void changeDelQuguaConf(String nodeName, String delIp) {
		ArrayList<String> list = new ArrayList<>();// 所有IP地址按照顺序排列
		SSHExecutorUtils ssh = new SSHExecutorUtils(Constants.DOCKER_NODE_USERNAME, "123456",
				Constants.DOCKER_NODE_IP_ADDRESS);
		String match = "";
		String first = "";
		String eth = "";// 网卡名称
		try {
			while (list.indexOf(delIp) < 0) {
				list.clear();
				TimeUnit.SECONDS.sleep(1);
				match = ssh.exec("sudo docker exec " + nodeName + " ifconfig");
				first = match.substring(match.indexOf("inet addr:") + 10, match.length());
				while (first.indexOf("inet addr:") > 0) {
					if (first.indexOf("  Bcast:") > 0) {
						String ip = first.substring(first.indexOf("inet addr:") + 10, first.indexOf("  Bcast:"));
						list.add(ip);
						if (ip.equals(delIp)) {
							eth = first.substring(first.indexOf("\nns") + 1, first.indexOf(" Link encap:Ethernet"));
						}
						first = first.substring(first.indexOf("  Bcast:") + 8, first.length());
					} else {
						first = first.substring(first.indexOf("inet addr:") + 10, first.length());
					}
				}
			}
			System.out.println("要删除的网卡为 " + eth + " " + delIp);
			// 成功拿到网卡的名称,执行zebra脚本
			ssh.exec("sudo docker exec -d " + nodeName + " ./rip_del.sh " + eth + " " + delIp + "/24 "
					+ delIp.substring(0, delIp.lastIndexOf(".")) + ".0/24");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}

	public static void zphdockerDemo(String nodeName, String addIp) {
		ArrayList<String> list = new ArrayList<>();// 所有IP地址按照顺序排列
		SSHExecutorUtils ssh = new SSHExecutorUtils("compute6", "123456", "10.0.0.71");
		String match = "";
		String first = "";
		String eth = "";// 网卡名称
		int count = 20;
		try {
			while (list.indexOf(addIp) < 0 && count > 0) {
				count--;
				list.clear();
				TimeUnit.SECONDS.sleep(1);
				match = ssh.exec("sudo docker exec " + nodeName + " ifconfig");
				first = match.substring(match.indexOf("inet addr:") + 10, match.length());
				System.out.println("防死循环。。。。1");
				while (first.indexOf("inet addr:") > 0) {
					System.out.println("防死循环。。。。2");
					if (first.indexOf("  Bcast:") > 0) {
						String ip = first.substring(first.indexOf("inet addr:") + 10, first.indexOf("  Bcast:"));
						list.add(ip);
						if (ip.equals(addIp)) {
							eth = first.substring(first.indexOf("\nns") + 1, first.indexOf(" Link encap:Ethernet"));
						}
						first = first.substring(first.indexOf("  Bcast:") + 8, first.length());
					} else {
						first = first.substring(first.indexOf("inet addr:") + 10, first.length());
					}
				}
				System.out.println("防死循环。。。。3");
			}
			if (count > 0) {
				System.out.println("新增加的网卡为 " + eth + " " + addIp);
				// 成功拿到网卡的名称,执行zebra脚本
				ssh.exec("sudo docker exec -d " + nodeName + " ./rip_add.sh " + eth + " " + addIp + "/24 "
						+ addIp.substring(0, addIp.lastIndexOf(".")) + ".0/24");

				// 3.查看传来的ip对应的是eth几
				double delay = new LinkDaoImpl().getLinkByPortID(new PortDaoImpl().getPortByIP(addIp).getPt_id())
						.getLinkLength() / (3.0E+02);

				String ethNumber = "eth" + list.indexOf(addIp);
				System.out.println(ethNumber);
				// logger.info(nodeName + " 的 " + addIp + " 对应的网为 " +
				// ethNumber);
				System.out.println(nodeName + " 的  " + addIp + " 对应的网为  " + ethNumber);
			} else {
				System.out.println("fail to add" + eth + " " + addIp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}

	public static void main(String[] args) {
		// changeAddQuguaConf("nova-add4223b-3419-46a0-8482-6cdb003c6fc4",
		// "10.10.20.3");
		zphdockerDemo("nova-" + ServerUtils.getServer("4").getId(), "10.90.10.3");
		zphdockerDemo("nova-" + ServerUtils.getServer("5").getId(), "10.90.10.4");
		zphdockerDemo("nova-" + ServerUtils.getServer("5").getId(), "10.90.20.3");
		zphdockerDemo("nova-" + ServerUtils.getServer("6").getId(), "10.90.20.4");

	}
}
