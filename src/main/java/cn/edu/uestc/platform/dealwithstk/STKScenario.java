package cn.edu.uestc.platform.dealwithstk;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import STKSoftwareController.STKUtil;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.service.NodeService;
import cn.edu.uestc.platform.winter.openstack.PortThread;

public class STKScenario {
	private static Logger logger = Logger.getLogger(STKScenario.class);

	public void createSTKAllNode(List<NodeCreater> setNodes, int s_id) throws Exception {
		List<Node> nodes = this.settingNodeAttr(setNodes, s_id);
		allcateXY(s_id);
	}

	public List<Node> settingNodeAttr(List<NodeCreater> setNodes, int s_id) throws IOException {
		// NodeDao nodeDao = new NodeDaoImpl();
		STKAnalyse stkAnalyse = new STKAnalyse();
		Set<String> nodesString = stkAnalyse.getNodesName(s_id);
		List<Node> nodes = new LinkedList<>();
		NodeService nodeService = new NodeService();
		for (String nodeString : nodesString) {
			for (NodeCreater setNode : setNodes) {
				if (nodeString.contains(setNode.getNodeName())) {
					Node node = new Node();
					logger.info("开始创建" + setNode.getImageName() + "节点");
					node.setHardwareArchitecture(setNode.getHardwareArchitecture());
					node.setImageName(setNode.getImageName());
					node.setNodeName(nodeString);
					node.setNodeType(setNode.getNodeType());
					node.setOperatingSystem(setNode.getOperatingSystem());
					node.setFlavorType(setNode.getFlavorType());
					node.setIconUrl(setNode.getIconUrl());
					node.setS_id(s_id);
					// 分配IP地址
					logger.info("为节点分配管理网段IP");
					node.setManageIp(this.allocateIP(node));
					logger.info("IP分配结束");
					nodeService.createNode(node);
					logger.info(setNode.getNodeName() + "节点创建结束");
					// nodeDao.insertNode(node);
					nodes.add(node);
					// 已经匹配到nodeString，结束第一层循环
					break;
				}
			}
			// }
		}
		return nodes;
	}

	// 给节点分配ip地址
	public String allocateIP(Node node) {
		NodeDao nodeDao = new NodeDaoImpl();
		// 拿到所有存在的ip
		List<String> IPs = nodeDao.getAllNodeIp();
		List<String> existIP = new LinkedList<>();
		for (String ip : IPs) {
			existIP.add(ip.substring(ip.lastIndexOf(".") + 1, ip.length()));
		}
		Integer num = 10;
		while (true) {
			if (existIP.contains(num.toString())) {
				num++;
				continue;
			} else {
				return "192.168.10." + num;
			}
		}
	}

	// 给节点分配画布区域坐标
	public void allcateXY(int s_id) throws Exception {
		logger.info("为节点在画布区域画点");
		STKAnalyse stkAnalyse = new STKAnalyse();
		Map<String, Integer> typeNum = stkAnalyse.analyseNodeTypeAndNodeNumber(s_id);
		List<Node> nodes = new NodeDaoImpl().findAllNodeByScenarioId(s_id);
		float GEOXGap = 0;
		float MEOXGap = 0;
		float LEOXGap = 0;
		int GEONum = 0;
		int LEONum = 0;
		int MEONum = 0;
		// 拿到场景下的所有节点 为他们分配XY坐标
		NodeDao nodeDao = new NodeDaoImpl();
		// GEO
		if (typeNum.get("GEO1") != null) {
			GEONum = typeNum.get("GEO1");
			GEOXGap = 1153 / (GEONum + 1);
		}
		final float GEOY = 24.0f;

		// MEO
		if (typeNum.get("MEO1") != null) {
			MEONum = typeNum.get("MEO1");
			MEOXGap = 1153 / (MEONum + 1);
		}
		final float MEOY = 43.0f;

		// LEO
		if (typeNum.get("LEO1") != null) {
			LEONum = typeNum.get("LEO1");
			LEOXGap = 1153 / (LEONum + 1);
		}
		final float LEOY = 342.0f;

		// 其他节点
		int otherNodeCount = nodes.size() - GEONum - LEONum - MEONum;
		float otherNodeXGap = 1153 / (otherNodeCount + 1);
		float otherNodeY = 512;

		int GEOCount = 1;
		int MEOCount = 1;
		int LEOCount = 1;
		int otherCount = 1;

		for (Node node : nodes) {
			if (node.getNodeName().contains("GEO")) {
				node.setX(GEOXGap * GEOCount);
				node.setY(GEOY);
				GEOCount++;
			} else if (node.getNodeName().contains("LEO")) {
				node.setX(LEOXGap * LEOCount);
				node.setY(LEOY);
				LEOCount++;
			} else if (node.getNodeName().contains("MEO")) {
				node.setX(MEOXGap * MEOCount);
				node.setY(MEOY);
				MEOXGap++;
			} else {
				node.setX(otherNodeXGap * otherCount);
				node.setY(otherNodeY);
				otherCount++;
			}

		}
		nodeDao.updateNodeXY(nodes);
		logger.info("画布区域描点结束");
	}

	// 创建t0时刻的初始拓扑
	public void createInitScenario(int s_id) throws IOException {
		// 拿到不重复的0時刻的链路
		Set<LinkForInitialScenario> links = new STKAnalyse().getLinkForInitialScenario(s_id, "0");

		// GEO TO GEO
		STKAllocateSubnet.allocateSubnetForGEOToGEO(NodeNumFilter.getGEONum(s_id), s_id);

		// GEO TO Facility
		STKAllocateSubnet.allocateSubnetForGEOToFacility(s_id, NodeNumFilter.getGEONum(s_id),
				NodeNumFilter.getFacilityNum(s_id));

		// GEO TO LEO
		STKAllocateSubnet.allocateSubnetForGEOToLEO(s_id, NodeNumFilter.getGEONum(s_id), NodeNumFilter.getLEORow(s_id),
				NodeNumFilter.getLEOColumn(s_id));

		// LEO左右
		STKAllocateSubnet.allocateSubnetForLEOToLEOLR(s_id, NodeNumFilter.getLEORow(s_id),
				NodeNumFilter.getLEOColumn(s_id));

		// LEO上下
		STKAllocateSubnet.allocateSubnetForLEOToLEOUD(s_id, NodeNumFilter.getLEORow(s_id),
				NodeNumFilter.getLEOColumn(s_id));

		// LEO TO Ground
		STKAllocateSubnet.allocateSubnetForLEOToGround(s_id, NodeNumFilter.getLEORow(s_id),
				NodeNumFilter.getLEOColumn(s_id), NodeNumFilter.getGroundVehicleNum(s_id));

		for (LinkForInitialScenario link : links) {
			portConnect.CreateConnect(link, s_id);
		}
	}


	public void createConnection(int s_id) throws InterruptedException {
		LinkDao linkDao = new LinkDaoImpl();
		List<Link> links = linkDao.getLinkListWhereStatue3(s_id);
		PortThread portThread[] = new PortThread[2];
		Thread threads[] = new Thread[2];
		for (Link l : links) {
			STKUtil.addLine(l.getFromNodeName(),l.getToNodeName());
			portThread[0] = new PortThread(l.getFromNodeName(), l.getFromNodeIP());
			portThread[1] = new PortThread(l.getToNodeName(), l.getToNodeIP());
			threads[0] = new Thread(portThread[0]);
			threads[1] = new Thread(portThread[1]);
			threads[0].start();
			threads[1].start();
			threads[0].join();
			threads[1].join();
		}
	}
	
	
	

	@Test
	public void demo2() throws IOException {
		// 拿到不重复的0時刻的链路
		Set<LinkForInitialScenario> links = new STKAnalyse().getLinkForInitialScenario(56, "0");
		STKAllocateSubnet.allocateSubnetForGEOToLEO(56, NodeNumFilter.getGEONum(56), NodeNumFilter.getLEORow(56),
				NodeNumFilter.getLEOColumn(56));
		for (LinkForInitialScenario link : links) {
			portConnect.CreateConnect(link, 56);
		}
	}
}
