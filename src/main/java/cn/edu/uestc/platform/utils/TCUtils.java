package cn.edu.uestc.platform.utils;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV3;

import cn.edu.uestc.platform.factory.OSClientFactory;

/**
 * 操作TC工具类
 */
public class TCUtils {
	private static Logger logger = Logger.getLogger(TCUtils.class);

	public static void addTC(String nodeName, String ip, String delay, String bw, String loss) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		if (!NetworkUtils.isHaveIP(os, nodeName, ip)) {
			logger.error(nodeName + " 没有此ip " + ip);
			throw new RuntimeException(nodeName + " 没有此ip " + ip);
		}

		// 1.找到nodeName对应的浮动ip
		String floatIP = NetworkUtils.getFloatIpByNodeName(nodeName);
		// 2.通过浮动ip进入后查看/etc/network/interfaces文件
		SSHExecutorUtils ssh = new SSHExecutorUtils("router", "123456", floatIP);
		try {
			String interfaces = ssh.exec("cat /etc/network/interfaces");// 拿到interfaces文件
			// 3.查看传来的ip对应的是eth几
			String ethNumber = "eth" + interfaces.substring(interfaces.indexOf(" inet static\naddress " + ip) - 1,
					interfaces.indexOf(" inet static\naddress " + ip));
			logger.info(nodeName + " 的  " + ip + " 对应的网为  " + ethNumber);
			// 4.执行TC命令
			ssh.exec("sudo tc qdisc add dev " + ethNumber + " root handle 1: htb");
			ssh.exec("sudo tc class add dev " + ethNumber + " parent 1: classid 1:1 htb rate " + bw + "mbit ceil "
					+ (bw + 1) + "mbit burst 50kbit");
			ssh.exec("sudo tc qdisc add dev " + ethNumber + " parent 1:1 handle 10: netem delay " + delay + "ms"
					+ " loss " + Integer.parseInt(loss) * 100 + "%");
			ssh.exec("sudo tc filter add dev " + ethNumber
					+ " parent 1:0 protocol ip prio 1 u32 match ip dst 0.0.0.0/0 flowid 1:1");
			logger.info("执行完了TC的命令");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ssh.close();
	}

	@Test
	public void fun() {
		// addTC("node1", "10.10.10.2", "23");
	}
}
