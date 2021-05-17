package LILEI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import cn.edu.uestc.platform.utils.SSHExecutor;

public class MANET {
	public static void initTC(SSHExecutor ssh, int nodeCount) throws Exception {

		System.out.println(ssh.exec("insmod sch_netem"));
		System.out.println("insmod sch_netem");
		
		String createQueue = "tc qdisc add dev eth0 root handle 1: htb";

		System.out.println(ssh.exec(createQueue));
		System.out.println(createQueue);

		String createRootClass = "tc class add dev eth0 parent 1:0 classid 1:1 htb rate 100mbit";

		System.out.println(createRootClass);
		System.out.println(ssh.exec(createRootClass));

		System.out.println(ssh.exec("tc filter add dev eth0 parent 1:0 protocol ip prio 100 route"));
		for (int i = 0; i < nodeCount; ++i) {
			String createSubclass = "tc class add dev eth0 parent 1:0 classid 1:" + (i + 2) + " htb rate 100mbit";
			System.out.println(createSubclass);
			System.out.println(ssh.exec(createSubclass));
			String createRoute = "tc filter add dev eth0 parent 1:0 protocol ip prio 100 route to " + (i+2) +" flowid 1:"
					+ (i + 2);
			System.out.println(createRoute);
			System.out.println(ssh.exec(createRoute));
			
			String setInitParam = "tc qdisc add dev eth0 parent 1:" + (i + 2) + " netem delay 0s loss 0%";
			System.out.println(setInitParam);
			System.out.println(ssh.exec(setInitParam));	
		}
		System.out.println("start capturing package");
		String capPacket = "tcpdump -c 10 -i eth0 -w node.cap";
		ssh.exec(capPacket);
		System.out.println("command end");
		
	}

	public static void setTCParam(SSHExecutor ssh, LinkedList<Link> links, LinkedList<ArrayList<String>> nodesInfo)
			throws Exception {
		System.out.println("---------"+links);
		if (links != null) {
			for (Link l : links) {
				String changeClass = "tc class change dev eth0 parent 1:" + (Integer.parseInt(l.getToNodeName()) + 2) + " htb rate " + l.getBw() + "mbit";
				System.out.println(changeClass);
				//ssh.exec(changeClass);
				String setParam = "tc qdisc change dev eth0" + " parent 1:" + (Integer.parseInt(l.getToNodeName()) + 2)
						+ " netem delay " + l.getDelay() + "s " + "loss " + l.getLoss();
				System.out.println(setParam);
				ssh.exec(setParam);

				String ipadd = null;
				String neiboripadd = null;
				for (ArrayList<String> nodeinfo : nodesInfo) {

					if (nodeinfo.get(0).equals(l.getFromNodeName())) {
						ipadd = nodeinfo.get(2);
					}
					if (nodeinfo.get(0).equals(l.getToNodeName())) {
						neiboripadd = nodeinfo.get(2);
					}

				}
				String setRoute = "ip route add " + neiboripadd + " dev eth0" + " via " + neiboripadd + " realm "
						+ (Integer.parseInt(l.getToNodeName()) + 2);
				System.out.println(setRoute);
				ssh.exec(setRoute);
			}
		}

	}

	public static void setTCParamStaticDemo(int nodeCount, SSHExecutor ssh, LinkedList<Link> links, LinkedList<ArrayList<String>> nodesInfo)
			throws Exception {
		
		System.out.println(ssh.exec("insmod sch_netem"));
		System.out.println("insmod sch_netem");
		
		String createQueue = "tc qdisc add dev eth0 root handle 1: htb default 1000 r2q 1000";

		System.out.println(ssh.exec(createQueue));
		System.out.println(createQueue);

		String createRootClass = "tc class add dev eth0 parent 1:0 classid 1:1 htb rate 100mbit";

		System.out.println(createRootClass);
		System.out.println(ssh.exec(createRootClass));

		System.out.println(ssh.exec("tc filter add dev eth0 parent 1:0 protocol ip prio 100 route"));
		System.out.println("---------"+links);
		
		String ipadd = null;
		if (links != null) {
			int i = 2;
			for (Link l : links) {
				
				String changeClass = "tc class add dev eth0 parent 1:0 classid 1:" + i + " htb rate " + l.getBw() +"mbit";
				System.out.println(changeClass);
				ssh.exec(changeClass);
				
				String createRoute = "tc filter add dev eth0 parent 1:0 protocol ip prio 100 route to " + i+" flowid 1:"
						+ i;
				System.out.println(createRoute);
				System.out.println(ssh.exec(createRoute));
				
				String setParam = "tc qdisc add dev eth0" + " parent 1:" + i
						+ " netem delay " + l.getDelay() + "s " + "loss " + l.getLoss();
				System.out.println(setParam);
				ssh.exec(setParam);


				String neiboripadd = null;
				for (ArrayList<String> nodeinfo : nodesInfo) {

					if (nodeinfo.get(0).equals(l.getFromNodeName())) {
						ipadd = nodeinfo.get(2);
					}
					if (nodeinfo.get(0).equals(l.getToNodeName())) {
						neiboripadd = nodeinfo.get(2);
					}

				}
				String setRoute = "ip route add " + neiboripadd + " dev eth0" + " via " + neiboripadd + " realm "
						+ i;
				System.out.println(setRoute);
				ssh.exec(setRoute);
				
				String delRoute1 = "route del -net " + neiboripadd + " netmask 255.255.255.255 gw " + neiboripadd + " metric 2 eth0"; 
				System.out.println(delRoute1);
				//ssh.exec(delRoute1);

				i++;
			}
			
			if(ipadd.equals("192.168.10.9")) return;
			else{
			String delRoute2 = "route del -net 192.168.10.1 gw 0.0.0.0 netmask 255.255.255.255 eth0"; 
			System.out.println(delRoute2);
			ssh.exec(delRoute2);
			
			String delRoute3 = "route del -net 0.0.0.0 gw 192.168.10.1 netmask 0.0.0.0 eth0"; 
			System.out.println(delRoute3);
			ssh.exec(delRoute3);
			
			String delRoute4 = "route del -net 192.168.10.0 gw 0.0.0.0 netmask 255.255.255.0 eth0"; 
			System.out.println(delRoute4);
			ssh.exec(delRoute4);
			}
		}

	}

	public static void addFlow(SSHExecutor ssh, String localPort, ArrayList<String> portList) throws Exception {

		String portid = "";
		for (String s : portList.toString().substring(1, portList.toString().length() - 1).split(" ")) {
			portid += s;
		}
		System.out.println(localPort.trim());
		String add = "sudo ovs-ofctl -O openflow13 add-flow br-int table=0,in_port=" + localPort.trim()
				+ ",priority=60000,dl_dst=ff:ff:ff:ff:ff:ff,action=output:" + portid;
		System.out.println(add);
		ssh.exec(add);
	}
	
	public static void delAllLink(SSHExecutor ssh, LinkedList<ArrayList<String>> nodesInfo) throws Exception{
		for(int i = 0; i < nodesInfo.size(); ++i){
			for(int j = 0; j < nodesInfo.size(); ++j){
				if(i == j) continue;
				String command = "sudo ovs-ofctl -O openflow13 add-flow br-int table=0,in_port=" 
				+ nodesInfo.get(i).get(4) + ",priority=60000,dl_dst=" + nodesInfo.get(j).get(1) + ",action=drop";
				System.out.println(command);
				ssh.exec(command);
			}
		}
	}
	
	public static void addNeighborLink(SSHExecutor ssh, String localPort, LinkedList<Link> links,LinkedList<ArrayList<String>> nodesInfo) throws Exception{
		for(Link link : links){
			for (ArrayList<String> n : nodesInfo) {
					if (link.getToNodeName().equals(n.get(0))) {
						String command = "sudo ovs-ofctl -O openflow13 del-flows br-int table=0,in_port=" + localPort.trim() + ",dl_dst=" + n.get(1).trim();
						System.out.println(command);
						ssh.exec(command);
					}
			}	
		}
	}
	
	public static void addNeighborLinkInit(SSHExecutor ssh, String localPort, LinkedList<String> links,LinkedList<ArrayList<String>> nodesInfo) throws Exception{
		for(String link : links){
			for (ArrayList<String> n : nodesInfo) {
					if (link.equals(n.get(0))) {
						String command = "sudo ovs-ofctl -O openflow13 del-flows br-int table=0,in_port=" + localPort.trim() + ",dl_dst=" + n.get(1).trim();
						System.out.println(command);
						ssh.exec(command);
					}
			}	
		}
	}
	
	public static void delNeighborLink(SSHExecutor ssh, String localPort, LinkedList<Link> links,LinkedList<ArrayList<String>> nodesInfo) throws Exception{
		if(links == null) return;
		for(Link link : links){
			for (ArrayList<String> n : nodesInfo) {
					if (link.getToNodeName().equals(n.get(0))) {
						String command = "sudo ovs-ofctl -O openflow13 add-flow br-int table=0,in_port=" + localPort.trim() + ",dl_dst=" + n.get(1).trim() + ",action=drop";
						System.out.println(command);
						ssh.exec(command);
					}
			}	
		}
	}

	/**
	 * 切换场景时在每个节点上先执行删除流表命令
	 * 
	 * @throws Exception
	 */
	public static void deleteFlow(SSHExecutor ssh, String localPort) throws Exception {
		String delete = "sudo ovs-ofctl -O openflow13 del-flows br-int table=0,in_port=" + localPort.trim()
				+ ",dl_dst=ff:ff:ff:ff:ff:ff";
		System.out.println(localPort.trim());
		System.out.println(delete);
		ssh.exec(delete);
	}
	
	public static void ping(SSHExecutor ssh, String ipAddr) throws Exception{
		String ping = "ping " + ipAddr.trim() + " -c 10 >> ping.txt";
		System.out.println(ping);
		ssh.exec(ping);
	}

}