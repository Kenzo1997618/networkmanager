package cn.edu.uestc.platform.service;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import cn.edu.uestc.platform.controller.NodeController;
import cn.edu.uestc.platform.dao.ComplexNodeDaoImpl;
import cn.edu.uestc.platform.dao.DeleteDao;
import cn.edu.uestc.platform.dao.DeleteDaoImpl;
import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.dao.PortDao;
import cn.edu.uestc.platform.dao.PortDaoImpl;
import cn.edu.uestc.platform.dealwithstk.STKScenario;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;

public class NodeService {

	/*
	 * 查找当前场景下的所有节点
	 */
	public List<Node> findAllNodeByScenarioId(int s_id) {
		NodeDao nodeDao = new NodeDaoImpl();
		return nodeDao.findAllNodeByScenarioId(s_id);
	}

	/*
	 * 新建节点，先在云平台启动成功 再插入数据库
	 */
	public boolean createNode(Node node) {
		NodeDao nodeDao = new NodeDaoImpl();
		NodeController nodecontroller = new NodeController();
		String uuid = null;
		STKScenario allocatIP = new STKScenario();
		// 判断节点名称是否重复
		if (nodeDao.haveNodeName(node) == false) {
			node.setManageIp(allocatIP.allocateIP(node));
			System.out.println(node);
			// 判断节点类型,根据类型对节点所属场景表的节点计数字段进行自增。
			if (node.getNodeType() == 1 || node.getNodeType() == 2) {
				// 如果在此处出现错误咋办？(例如用户输入的ip地址已经存在)
				System.out.println("创建节点vm");
				uuid = nodecontroller.createNode(node.getNodeName(), node.getManageIp(), "amd");
				// 更新此节点对应场景的节点数量
				nodeDao.plusNumberSimpleNode(node.getS_id());
				// 此处需要把数据库的回滚加进去，判断节点类型
				System.out.println("启动vm虚拟机成功！");
			} else if (node.getNodeType() == 0 || node.getNodeType() == 3) {
				System.out.println("创建节点docker");
				uuid = nodecontroller.createNode(node.getNodeName(), node.getManageIp(), "docker");
				System.out.println("启动docker成功！");
				nodeDao.plusNumberSimpleNode(node.getS_id());
			}
			node.setUuid(uuid);
			nodeDao.insertNode(node);
			return true;
		}
		System.out.println("节点名冲突！");
		return false;
	}

	/*
	 * 根据n_id查节点
	 */
	public Node getNode(int n_id) {
		// TODO Auto-generated method stub
		NodeDao dao = new NodeDaoImpl();
		return dao.getNodeByNodeId(n_id);
	}

	/*
	 * 根据节点名和s_id查节点
	 */
	public Node getNodeBynodeName(String nodeName, int s_id) {
		// TODO Auto-generated method stub
		NodeDao dao = new NodeDaoImpl();
		return dao.getNodeBynodeName(nodeName, s_id);
	}

	/*
	 * 编辑节点
	 */
	public boolean editNode(Node node) {
		// TODO Auto-generated method stub
		NodeDao dao = new NodeDaoImpl();
		// boolean flag = dao.updataNode();
		if (dao.haveNodeName(node)) { // ip或者nodeName其中一个为true则冲突，返回false
			return false;
		}
		dao.updataNode(node);
		return true;
	}

	/*
	 * 删除节点 1、先删除节点上的所有链路 2、再删除节点上的所有端口 3、更新节点所属场景的节点数量信息 4、删除节点
	 */
//	public void deleteNode(int s_id, String nodeName) {
//		// 拿到属于该节点的所有port
//		PortDao portDao = new PortDaoImpl();
//		List<Port> ports = portDao.getPortListBynodeName(s_id, nodeName);
//		NodeDao nodeDao = new NodeDaoImpl();
//		Node node = nodeDao.getNodeBynodeName(nodeName, s_id);
//
//		// 调用portservice 删除port(包括port上的链路)
//		PortService portService = new PortService();
//		System.out.println("开始删除该节点中所有端口" + new Date());
//		for (Port port : ports) {
//			portService.deletePort(port.getPt_id());
//		}
//		System.out.println("删除端口结束" + new Date());
//		// 在Openstack上删除节点
//		System.out.println("开始删除Openstack层上的节点" + new Date());
//		NodeController nodeController = new NodeController();
//		System.out.println(node.getUuid());
//		nodeController.deleteNode(node.getUuid());
//		System.out.println("删除Openstack层上的节点结束" + new Date());
//		// 更新节点所属场景的节点数量信息
//		if (node.getNodeType() == 2) {
//			nodeDao.minusNumberComplexNode(s_id);
//		} else {
//			nodeDao.minusNumberSimpleNode(s_id);
//		}
//		// 在数据库中删除节点
//		DeleteDao deleteDao = new DeleteDaoImpl();
//		deleteDao.deleteSimpleNode(node);
//	}

//	public void deleteNodeOnlyOpenstack(int s_id, String nodeName) {
//		// 拿到属于该节点的所有port
//		PortDao portDao = new PortDaoImpl();
//		List<Port> ports = portDao.getPortListBynodeName(s_id, nodeName);
//		NodeDao nodeDao = new NodeDaoImpl();
//		Node node = nodeDao.getNodeBynodeName(nodeName, s_id);
//
//		// 调用portservice 删除port(包括port上的链路)
//		PortService portService = new PortService();
//		System.out.println("开始删除该节点中所有端口" + new Date());
//		for (Port port : ports) {
//			portService.deletePortOnlyOpenstack(port.getPt_id());
//		}
//		System.out.println("删除端口结束" + new Date());
//		// 在Openstack上删除节点
//		System.out.println("开始删除Openstack层上的节点" + new Date());
//		NodeController nodeController = new NodeController();
//		System.out.println(node.getUuid());
//		nodeController.deleteNode(node.getUuid());
//		System.out.println("删除Openstack层上的节点结束" + new Date());
//
//	}

	public List<Node> getInnerNodeList(int s_id, String complexNodeName) {
		// TODO Auto-generated method stub
		NodeDao nodedao = new NodeDaoImpl();
		return nodedao.getNodeListByCnid(
				new ComplexNodeDaoImpl().getComplexNodeBys_idAndComplexNodeName(s_id, complexNodeName).getCn_id());
	}

	public void updateLocation(int s_id, String nodeName, int x, int y) {
		// TODO Auto-generated method stub
		NodeDao nodeDao = new NodeDaoImpl();
		nodeDao.updateLocation(s_id, nodeName, x, y);

	}



	/*
	 * 2021.5.17
	 */
	public List<Node> getRouterAndSwitch(){
		NodeDao nodeDao = new NodeDaoImpl();
		List<Node> nodes = nodeDao.getRouter();
		nodes.addAll(nodeDao.getSwitch());
		return nodes;
	}



}
