package LILEI;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV3;

import com.jcraft.jsch.JSchException;

import cn.edu.uestc.platform.controller.NodeController;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.SSHExecutor;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;

public class Simu {
	public static String fileUrl = "/usr/zph/20.csv";
	public static final int SLEEP = 5;
	public static final int SIMUTIME = 300;
	public static String destination = null;
	public static String source = null;

//	@Test
//	public void demo1() throws Exception {
//		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
//		Reader in = new FileReader(fileUrl);
//		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
//		int count = 0;
//		Set<String> nodes = new HashSet<>();
//
//		Map<String, LinkedList<String>> links = new HashMap<>();
//		// 拿到所有节点
//		for (CSVRecord record : records) {
//			nodes.add(record.get("Node1"));
//			nodes.add(record.get("Node2"));
//			// get time=0 links
//			if (record.get("SimulationSecond").equals("0")) {
//				Link link = new Link();
//				// link.setFromNodeName(record.get("Node1"));
//				// link.setToNodeName(record.get("Node2"));
//				if (!links.keySet().contains(record.get("Node1"))) {
//					LinkedList<String> toNode = new LinkedList<>();
//					toNode.add(record.get("Node2"));
//					links.put(record.get("Node1"), toNode);
//				} else {
//					links.get(record.get("Node1")).add(record.get("Node2"));
//				}
//			}
//		}
//		for (String s : links.keySet()) {
//			System.out.println(s + "----" + links.get(s));
//		}
//		// System.out.println();
//
//		System.out.println(links.keySet());
//		// create all node
//		NodeController nodeController = new NodeController();
//
//		// create Node Info
//		LinkedList<ArrayList<String>> nodesInfo = new LinkedList<>();
//		SSHExecutor ssh = new SSHExecutor("compute3", "123456", "10.0.0.41");
//
//		int i = 5;
//		for (String node : nodes) {
//			// index 0:nodeId 1:macadd 2:ipadd 3:floatIp 4:odlport
//			ArrayList<String> nodeInfo = new ArrayList<>();
//			nodeInfo.add(node);
//			String nodeId = nodeController.createNode(node, "192.168.10." + i++, "manet");
//			String macAddr = os.compute().servers().get(nodeId).getAddresses().getAddresses().get("net").get(0)
//					.getMacAddr();
//			String odlPort = ssh.exec("sudo ovs-ofctl -O openflow13  show br-int | grep "
//					+ macAddr.substring(3, macAddr.length()) + " | cut -d \"(\" -f 1").trim();
//			String ipAddr = os.compute().servers().get(nodeId).getAddresses().getAddresses().get("net").get(0)
//					.getAddr();
//			String floatIp = os.compute().servers().get(nodeId).getAddresses().getAddresses().get("net").get(1)
//					.getAddr();
//			nodeInfo.add(macAddr);
//			nodeInfo.add(ipAddr);
//			nodeInfo.add(floatIp);
//			nodeInfo.add(odlPort);
//			nodesInfo.add(nodeInfo);
//		}
//		System.out.println("create node end");
//		TimeUnit.SECONDS.sleep(30);
//		System.out.println(nodesInfo);
//		for (ArrayList<String> node : nodesInfo) {
//			LinkedList<String> link = links.get(node.get(0));
//			if (link == null)
//				continue;
//			ArrayList<String> portList = new ArrayList<>();
//			for (String l : link) {
//				for (ArrayList<String> n : nodesInfo) {
//					if (n.get(0).equals(l)) {
//						portList.add(n.get(4));
//					}
//				}
//			}
//			MANET.addFlow(ssh, node.get(4), portList);
//		}
//		// startSimulation(nodesInfo);
//	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		Reader in = new FileReader(fileUrl);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		Map<String, Map<String, LinkedList<Link>>> table = new HashMap<>();
		for (CSVRecord record : records) {
			if (!table.keySet().contains(record.get("SimulationSecond"))) {
				Map<String, LinkedList<Link>> fromNodeToToNodes = new HashMap<>();
				LinkedList<Link> toNodes = new LinkedList<>();
				Link link = new Link();
				link.setFromNodeName(record.get("Node1"));
				link.setToNodeName(record.get("Node2"));
				link.setTime(record.get("SimulationSecond"));
				link.setBw(record.get("BW"));
				link.setDelay(record.get("delay"));
				link.setLoss(record.get("Loss"));
				toNodes.add(link);
				fromNodeToToNodes.put(record.get("Node1"), toNodes);
				table.put(record.get("SimulationSecond"), fromNodeToToNodes);
			} else {
				Map<String, LinkedList<Link>> fromNodeToToNodes = table.get(record.get("SimulationSecond"));
				if (!fromNodeToToNodes.keySet().contains(record.get("Node1"))) {
					LinkedList<Link> toNodes = new LinkedList<>();
					Link link = new Link();
					link.setFromNodeName(record.get("Node1"));
					link.setToNodeName(record.get("Node2"));
					link.setTime(record.get("SimulationSecond"));
					link.setBw(record.get("BW"));
					link.setDelay(record.get("delay"));
					link.setLoss(record.get("Loss"));
					toNodes.add(link);
					fromNodeToToNodes.put(record.get("Node1"), toNodes);
				} else {
					// System.out.println(fromNodeToToNodes);
					LinkedList<Link> toNodes = fromNodeToToNodes.get(record.get("Node1"));
					Link link = new Link();
					link.setFromNodeName(record.get("Node1"));
					link.setToNodeName(record.get("Node2"));
					link.setTime(record.get("SimulationSecond"));
					link.setBw(record.get("BW"));
					link.setDelay(record.get("delay"));
					link.setLoss(record.get("Loss"));
					// System.out.println(toNodes);
					toNodes.add(link);
				}
			}
		}

		int count = 0;
		Set<String> nodes = new HashSet<>();

		Map<String, LinkedList<String>> links = new HashMap<>();
		Reader ins = new FileReader(fileUrl);
		Iterable<CSVRecord> recordss = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(ins);
		// 拿到所有节点
		for (CSVRecord record : recordss) {
			nodes.add(record.get("Node1"));
			nodes.add(record.get("Node2"));
			// get time=0 links
			if (record.get("SimulationSecond").equals("0")) {
				Link link = new Link();
				// link.setFromNodeName(record.get("Node1"));
				// link.setToNodeName(record.get("Node2"));
				if (!links.keySet().contains(record.get("Node1"))) {
					LinkedList<String> toNode = new LinkedList<>();
					toNode.add(record.get("Node2"));
					links.put(record.get("Node1"), toNode);
				} else {
					links.get(record.get("Node1")).add(record.get("Node2"));
				}
			}
		}
		for (String s : links.keySet()) {
			System.out.println(s + "----" + links.get(s));
		}
		// System.out.println();

		System.out.println(links.keySet());
		// create all node
		NodeController nodeController = new NodeController();

		// create Node Info
		LinkedList<ArrayList<String>> nodesInfo = new LinkedList<>();
		SSHExecutor ssh = new SSHExecutor("compute3", "123456", "10.0.0.41");

		int i = 5;
		for (String node : nodes) {
			// index 0:nodeId 1:macadd 2:ipadd 3:floatIp 4:odlport
			ArrayList<String> nodeInfo = new ArrayList<>();
			nodeInfo.add(node);
			String nodeId = nodeController.createNode(node, "192.168.10." + i++, "manet");
			String macAddr = os.compute().servers().get(nodeId).getAddresses().getAddresses().get("net").get(0)
					.getMacAddr();
			String odlPort = ssh.exec("sudo ovs-ofctl -O openflow13  show br-int | grep "
					+ macAddr.substring(3, macAddr.length()) + " | cut -d \"(\" -f 1").trim();
			String ipAddr = os.compute().servers().get(nodeId).getAddresses().getAddresses().get("net").get(0)
					.getAddr();
			String floatIp = os.compute().servers().get(nodeId).getAddresses().getAddresses().get("net").get(1)
					.getAddr();
			nodeInfo.add(macAddr);
			nodeInfo.add(ipAddr);
			nodeInfo.add(floatIp);
			nodeInfo.add(odlPort);
			nodesInfo.add(nodeInfo);
			
			if(Integer.parseInt(node) == 15) destination = ipAddr;
			if(Integer.parseInt(node) == 5) source = floatIp;
		}
		System.out.println("create node end");
		TimeUnit.SECONDS.sleep(10);
		
		for (ArrayList<String> node : nodesInfo) {
			System.out.println(node);
		}

	/*for (ArrayList<String> node : nodesInfo) {
			System.out.println(node);
			SSHExecutor nodessh = new SSHExecutor("root", null, node.get(3));
			MANET.initTC(nodessh, nodesInfo.size());
			MANET.setTCParam(nodessh, table.get("0").get(node.get(0)), nodesInfo);
		}*/
		
/*	for (ArrayList<String> node : nodesInfo) {
			System.out.println(node);
			SSHExecutor nodessh = new SSHExecutor("root", null, node.get(3));
			MANET.setTCParamStaticDemo(nodesInfo.size(), nodessh, table.get("0").get(node.get(0)), nodesInfo);
		}*/
		
		MANET.delAllLink(ssh, nodesInfo);

		System.out.println(nodesInfo);
		for (ArrayList<String> node : nodesInfo) {
			LinkedList<String> link = links.get(node.get(0));
			if (link == null) {
				ArrayList<String> temp = new ArrayList<>();
				temp.add("1000");
				MANET.addFlow(ssh, node.get(4), temp);
				continue;
			}
			ArrayList<String> portList = new ArrayList<>();
			for (String l : link) {
				for (ArrayList<String> n : nodesInfo) {
					if (n.get(0).equals(l)) {
						portList.add(n.get(4));
					}
				}
			}
			MANET.addNeighborLinkInit(ssh, node.get(4), link, nodesInfo);
			MANET.addFlow(ssh, node.get(4), portList);
			
		}
		
		new Simu().startSimulation(nodesInfo, table);
		System.out.println("simulation end ");
	}

	public void startSimulation(LinkedList<ArrayList<String>> nodesInfo,
			Map<String, Map<String, LinkedList<Link>>> table) throws Exception {

		System.out.println("start simulation");
		SSHExecutor ssh = new SSHExecutor("compute3", "123456", "10.0.0.41");
		Reader in = new FileReader(fileUrl);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);

		System.out.println("tuopu duquwanbi");
		TimeUnit.SECONDS.sleep(SLEEP);
		
		/*for (ArrayList<String> node : nodesInfo) {
			System.out.println("ping");
			SSHExecutor nodesssh = new SSHExecutor("root", null, node.get(3));
			System.out.println("ping to " + nodesInfo.get(nodesInfo.size()/2).get(2));
			MANET.ping(nodesssh, nodesInfo.get(nodesInfo.size()/2).get(2));
		}*/
		int nodeCount = 0;
		/*
		for (ArrayList<String> node : nodesInfo) {
			System.out.println("capture");
			SSHExecutor nodesssh = new SSHExecutor("root", null, node.get(3));
			nodesssh.exec("tcpdump -c 10 -i eth0 -w node" + (nodeCount) + ".cap");
		}*/
		
		int time = 60;
		while (time <= SIMUTIME) {
			
			//System.out.println("source " + source + "; destination " + destination);
			//SSHExecutor nodesssh = new SSHExecutor("root", null, source);
			//MANET.ping(nodesssh, destination);
			
			nodeCount++;
			System.out.println("del link");
			for (ArrayList<String> node : nodesInfo) {
				MANET.deleteFlow(ssh, node.get(4));
				MANET.delNeighborLink(ssh, node.get(4), table.get(String.valueOf(time-60)).get(String.valueOf(node.get(0))), nodesInfo);
			}
			System.out.println("bbbbbbbbbbbbbbbbbb");
			System.out.println("time=" + time);
			Map<String, LinkedList<Link>> fromNodeToTonodes = table.get(String.valueOf(time));
			int i = 0;
			for (String node : fromNodeToTonodes.keySet()) {
				System.out.println(node);
				LinkedList<Link> roundnodes = fromNodeToTonodes.get(node);
				String fromNodeOdl = null;
				for (ArrayList<String> n : nodesInfo) {

					if (n.get(0).equals(node)) {
						fromNodeOdl = n.get(4);
						//System.out.println("aaaaaaaaaaaaaaaaaaa-----" + n);
					}
				}

				ArrayList<String> portList = new ArrayList<>();

				while (!node.equals(String.valueOf(i)) && i < nodesInfo.size()) {
					String fromNodeOdltemp = null;
					portList.add("1000");
					for (ArrayList<String> n : nodesInfo) {
						if (n.get(0).equals(String.valueOf(i))) {
							fromNodeOdltemp = n.get(4);
							//System.out.println("bbbbbbbbbbbbbbbb-----" + n);
						}
					}
					MANET.addFlow(ssh, fromNodeOdltemp, portList);
					portList.clear();
					i++;
				}
				i++;
				portList.clear();

				for (ArrayList<String> n : nodesInfo) {
					for (Link link : roundnodes) {
						System.out.println(link);
						if (link.getToNodeName().equals(n.get(0))) {
							portList.add(n.get(4));
						}
					}
					System.out.println(node + "portList:" + portList);
				}
				//System.out.println("cccccccccccccccccc");
				MANET.addFlow(ssh, fromNodeOdl, portList);
				MANET.addNeighborLink(ssh, fromNodeOdl, roundnodes, nodesInfo);

				System.out.println("-----------setTCParam---------------");
				
				/*for (ArrayList<String> nodeinfo : nodesInfo) {
					SSHExecutor sssssh = new SSHExecutor("root", null, nodeinfo.get(3));
					MANET.setTCParam(sssssh, table.get(String.valueOf(time)).get(nodeinfo.get(0)), nodesInfo);
				}*/

			}

			TimeUnit.SECONDS.sleep(SLEEP);
			/*for (ArrayList<String> node : nodesInfo) {
				//System.out.println("ping");
				SSHExecutor nodesssh = new SSHExecutor("root", null, node.get(3));
				System.out.println("ping to " + nodesInfo.get(nodesInfo.size()/2).get(2));
				MANET.ping(nodesssh, nodesInfo.get(nodesInfo.size()/2).get(2));
			}*/
			/*for (ArrayList<String> node : nodesInfo) {
				System.out.println("capture");
				SSHExecutor nodesssh = new SSHExecutor("root", null, node.get(3));
				nodesssh.exec("tcpdump -c 10 -i eth0 -w node" + (nodeCount) + ".cap");
			}*/
			TimeUnit.SECONDS.sleep(SLEEP);
			time += 60;
		}

		//System.out.println("del link");
		for (ArrayList<String> node : nodesInfo) {
			//MANET.deleteFlow(ssh, node.get(4));
			//MANET.delNeighborLink(ssh, node.get(4), table.get(String.valueOf(time)).get(String.valueOf(node.get(0))), nodesInfo);
		}

	}

}

// for (ArrayList<String> node : nodesInfo) {
// LinkedList<String> link123 = links.get(node.get(0));
// if (link123 == null)
// continue;
// ArrayList<String> portList = new ArrayList<>();
// for (String l : link123) {
// for (ArrayList<String> n : nodesInfo) {
// if (n.get(0).equals(l)) {
// portList.add(n.get(4));
// }
// }
// }
// MANET.addFlow(ssh, node.get(4), portList);
// }
// } else {
// TimeUnit.SECONDS.sleep(50);
// MANET.deleteFlow(ssh, "");
// count += 60;
// continue;
//
// }
// }}
//
// @Test
// public void demo() throws Exception {
// SSHExecutor ssh = new SSHExecutor("compute3", "123456", "10.0.0.41");
//
// System.out.println(
// ssh.exec("sudo ovs-ofctl -O openflow13 show br-int | grep
// fe:16:3e:68:c7:ad | cut -d \"(\" -f 1"));
// }

// @Test
// public void dem211212() {
// LinkedList<String> link = new LinkedList<>();
// link.add("1");
// link.add("2");
// link.add("3");
// link.add("4");
// System.out.println(link.toString().substring(1, link.toString().length()
// - 1).split(", "));
// for(String s :link.toString().substring(1, link.toString().length() -
// 1).split(" ").){
// System.out.print(s);
// }
// new String(link.toString().substring(1, link.toString().length() -
// 1).split(" "));
//
//
// }
