package cn.edu.uestc.platform.service;

import java.util.List;
import cn.edu.uestc.platform.dao.L2LinkDao;
import cn.edu.uestc.platform.pojo.L2Link;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;

public class L2LinkService {

	public List<L2Link> getL2LinkListById(int s_id) {
		return L2LinkDao.getL2LinkList(s_id);
	}

	public List<L2Link> getL2NodePortList(int s_id, String nodeName) {
		return L2LinkDao.getL2NodePortList(s_id, nodeName);
	}

	public L2Link getL2LinkByName(int s_id, String linkName) {
		return L2LinkDao.getL2Link(s_id, linkName);
	}

	// setting TC
	public void setTC(String floatIP, String ethNumber, Port port, String length) throws Exception {
		SSHExecutorUtils ssh = new SSHExecutorUtils("router", "123456", floatIP);
		ssh.exec("sudo tc qdisc add dev " + ethNumber + " root handle 1: htb");
		ssh.exec("sudo tc class add dev " + ethNumber + " parent 1: classid 1:1 htb rate " + port.getMaximumRate()
				+ "mbit ceil " + (port.getMaximumRate() + 1) + "mbit burst 50kbit");
		ssh.exec("sudo tc qdisc add dev " + ethNumber + " parent 1:1 handle 10: netem delay "
				+ /* (Double.parseDouble(length)) */9000 / (3.0E+02) + "ms" + " loss "
				+ Integer.parseInt(port.getPacketLoss()) * 100 + "%");
		ssh.exec("sudo tc filter add dev " + ethNumber
				+ " parent 1:0 protocol ip prio 1 u32 match ip dst 0.0.0.0/0 flowid 1:1");
		System.out.println("tc success");
		ssh.close();
	}

	public void pauseLink(int scenario_id, String linkName) {
		L2Link l2link = getL2LinkByName(scenario_id, linkName);
		SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", l2link.getFromHostIp());
		try {
			sshExecutor.exec("ifconfig " + l2link.getBrName() + " down");
		} catch (Exception e) {
			e.printStackTrace();
		}
		sshExecutor = new SSHExecutorUtils("root", "123456", l2link.getToHostIp());
		try {
			sshExecutor.exec("ifconfig " + l2link.getBrName() + " down");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recoveryLink(int scenario_id, String linkName) {
		L2Link l2link = getL2LinkByName(scenario_id, linkName);
		SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", l2link.getFromHostIp());
		try {
			sshExecutor.exec("ifconfig " + l2link.getBrName() + " up");
			sshExecutor.exec("ifconfig " + l2link.getBrName() + " promisc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		sshExecutor = new SSHExecutorUtils("root", "123456", l2link.getToHostIp());
		try {
			sshExecutor.exec("ifconfig " + l2link.getBrName() + " up");
			sshExecutor.exec("ifconfig " + l2link.getBrName() + " promisc");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
