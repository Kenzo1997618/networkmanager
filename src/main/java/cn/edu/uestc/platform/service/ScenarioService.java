package cn.edu.uestc.platform.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import cn.edu.uestc.platform.action.ActionController;
import cn.edu.uestc.platform.controller.LinkController;
import cn.edu.uestc.platform.controller.NodeController;
import cn.edu.uestc.platform.dao.ComplexNodeDao;
import cn.edu.uestc.platform.dao.ComplexNodeDaoImpl;
import cn.edu.uestc.platform.dao.DeleteDao;
import cn.edu.uestc.platform.dao.DeleteDaoImpl;
import cn.edu.uestc.platform.dao.L2LinkDao;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.dao.PortDao;
import cn.edu.uestc.platform.dao.PortDaoImpl;
import cn.edu.uestc.platform.dao.ScenarioDao;
import cn.edu.uestc.platform.dao.ScenarioDaoImpl;
import cn.edu.uestc.platform.pojo.ComplexNode;
import cn.edu.uestc.platform.pojo.L2Link;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.pojo.Scenario;

public class ScenarioService {
	private static Logger logger = Logger.getLogger(ScenarioService.class);

	/*
	 * 新建场景
	 * 
	 */
	public boolean createScenario(Scenario scenario) {
		// 在插入数据之前 要先判断一下场景名是否已存在
		ScenarioDao scenarioDao = new ScenarioDaoImpl();
		if (scenarioDao.haveScenarioName(scenario) == false) {
			scenarioDao.insertScenario(scenario);
			logger.info("新建场景成功!");
			return true;
		} else {
			logger.info("场景名:" + scenario.getScenarioName() + "已存在！");
			return false;
		}
	}

	/*
	 * 查到当前工程下的所有场景
	 */
	public List<Scenario> findAllScenarioByProjectId(int p_id) {
		// TODO Auto-generated method stub
		ScenarioDao scenarioDao = new ScenarioDaoImpl();
		return scenarioDao.findAllScenarioByProjectId(p_id);
	}

	/*
	 * 仅删除Openstack上的节点和链路
	 */
	public void deleteScenariosOnlyOpenstack(int s_id) {
		// 拿到所有的链路
		LinkDao linkDao = new LinkDaoImpl();
		List<Link> links = linkDao.getLinkList(s_id);

		// 删除Openstack上的所有链路
		logger.info("开始删除Openstack上的所有链路 ，操作时间:" + new Date());
		LinkController linkController = new LinkController();
		for (Link link : links) {
			linkController.delLinkMTM(link.getFromNodeName(), link.getFromNodeIP(), link.getToNodeName(),
					link.getToNodeIP());
		}
		//delete all bridge
		
		L2LinkDao l2Dao = new L2LinkDao();
		List<L2Link> l2LinkList = l2Dao.getL2LinkList(s_id);
		
		//TODO 
		//DELETE ALL bridge
		
		
		logger.info("链路删除结束，结束时间:" + new Date() + "'/n'开始删除Openstack上的节点");
		// 拿到所有的节点
		NodeDao nodeDao = new NodeDaoImpl();
		List<Node> nodes = nodeDao.findAllNodeByScenarioId(s_id);
		NodeController nodeController = new NodeController();
		for (Node node : nodes) {
			nodeController.deleteNode(node.getUuid());
		}
		logger.info("节点删除结束，结束时间:" + new Date() + "'/n'更新场景状态");
		// 更新场景状态
		ScenarioDao scenarioDao = new ScenarioDaoImpl();
		scenarioDao.updateScenarioStatusToDown(s_id);
	}

	/*
	 * 删除此场景下一切
	 */
	public void deleteScenarioAll(int s_id) {
		// TODO Auto-generated method stub
		NodeDao nodeDao = new NodeDaoImpl();
		ComplexNodeDao complexNodeDao = new ComplexNodeDaoImpl();
		// 拿到所有的链路
		LinkDao linkDao = new LinkDaoImpl();
		DeleteDao deleteDao = new DeleteDaoImpl();
		List<Link> links = linkDao.getLinkList(s_id);

		// 删除Openstack上的所有链路
		logger.info("开始删除Openstack上的所有链路 ，操作时间:" + new Date());
		LinkController linkController = new LinkController();
		for (Link link : links) {
			linkController.delLinkMTM(link.getFromNodeName(), link.getFromNodeIP(), link.getToNodeName(),
					link.getToNodeIP());
			deleteDao.deleteLink(link);
		}
		NodeService nodeService = new NodeService();
		List<Node> nodes = nodeDao.findAllNodeByScenarioId(s_id);
		System.out.println("开始删除场景了");
		for (Node node : nodes) {
			nodeService.deleteNode(node.getS_id(), node.getNodeName());
		}
		List<ComplexNode> complexNodes = complexNodeDao.selectComplexNodeList(s_id);
		for (ComplexNode complexNode : complexNodes) {
			complexNodeDao.deleteComplexNode(complexNode);
		}
		ScenarioDao Scenariosdao = new ScenarioDaoImpl();
		Scenariosdao.deletescenario(s_id);
		System.out.println("删除场景结束！");
	}

	/*
	 * 从数据库中恢复底层Openstack上的节点和链路
	 */
	public void recoveryScenario(int s_id) {
		// 恢复节点
		NodeDao nodeDao = new NodeDaoImpl();
		List<Node> nodes = nodeDao.findAllNodeByScenarioId(s_id);
		NodeController nodeController = new NodeController();
		logger.info("开始创建Openstack上的所有节点，操作时间:" + new Date());
		for (Node node : nodes) {
			String uuid = null;
			if (node.getNodeType() == 1) {
				// 在Openstack上创建节点成功后返回uuid，写回数据库
				uuid = nodeController.createNode(node.getNodeName(), node.getManageIp(), "vm");
				// 更新节点的uuid
				nodeDao.updataNodeUuid(uuid, node.getN_id());

			} else {
				uuid = nodeController.createNode(node.getNodeName(), node.getManageIp(), "docker");
				// update uuid
				nodeDao.updataNodeUuid(uuid, node.getN_id());
			}
		}
		logger.info("创建Openstack上的节点结束 ，操作时间:" + new Date());
		logger.info("开始创建Openstack上的所有链路，操作时间:" + new Date());
		LinkDao linkDao = new LinkDaoImpl();
		List<Link> links = linkDao.getLinkList(s_id);
		LinkController linkController = new LinkController();
		for (Link link : links) {
			if (link.getLinkStatus() == 0) {
				linkController.createLinkMTM(link.getFromNodeName(), link.getFromNodeIP(), link.getToNodeName(),
						link.getToNodeIP());
			}
		}
		logger.info("创建Openstack上的链路结束 ，操作时间:" + new Date());
		logger.info("更新场景状态，操作时间:" + new Date());
		ScenarioDao scenariodao = new ScenarioDaoImpl();
		scenariodao.updateScenarioStatusToUp(s_id);
		logger.info("恢复场景结束，操作时间:" + new Date());
	}

	public void addDynamicTopologyFile(String path, int s_id) {
		// TODO Auto-generated method stub
		ScenarioDao scenarioDao = new ScenarioDaoImpl();
		scenarioDao.insertDynamicTopologyFile(path, s_id);

	}

	public String getDynamicTopologyFile(int s_id) {
		// TODO Auto-generated method stub
		return new ScenarioDaoImpl().findDynamicTopologyFileBySid(s_id);
	}
}
