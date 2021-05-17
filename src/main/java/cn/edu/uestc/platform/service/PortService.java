package cn.edu.uestc.platform.service;

import java.util.Date;
import java.util.List;
import cn.edu.uestc.platform.controller.LinkController;
import cn.edu.uestc.platform.dao.DeleteDao;
import cn.edu.uestc.platform.dao.DeleteDaoImpl;
import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.dao.PortDao;
import cn.edu.uestc.platform.dao.PortDaoImpl;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;

public class PortService {

	/*
	 * 新建端口,端口名一样是否允许？最大端口数是否限制？
	 */
	public boolean createPort(Port port) {
		// TODO Auto-generated method stub
		PortDao pdao = new PortDaoImpl();
		NodeDao ndao = new NodeDaoImpl();
		// port表插入port
		pdao.insertPort(port);
		// 节点表的portnumber字段+1
		ndao.plusNumberPort(port.getN_id());
		return true;
	}

	/*
	 * 返回端口列表
	 */
	public List<Port> getPortList(int n_id) {
		PortDao dao = new PortDaoImpl();
		return dao.getPortList(n_id);
	}

	/*
	 * 通过节点名称获取对应的所有端口列表
	 */
	public List<Port> getPortListBynodeName(int s_id, String nodeName) {
		PortDao pdao = new PortDaoImpl();
		return pdao.getPortListBynodeName(s_id, nodeName);
	}

	/*
	 * 通过端口id删除对应的端口,如果端口上连接了链路，同时删除对应的openstack上的链路。
	 */
	public void deletePort(Integer pt_id) {

		PortDao portDao = new PortDaoImpl();
		NodeDao nodeDao = new NodeDaoImpl();
		DeleteDao deteledao = new DeleteDaoImpl();
		// 拿到被删除的port
		Port port = portDao.getPortByID(pt_id);
		// 拿到删除port另一端的port,若链路不存在则返回｛0，0 ｝。
		int portID[] = portDao.getPortIdsOnSameLinkByPort(port);
		// 只要portID数组中任意端口号不为0 说明存在链路
		if (portID[0] != 0) {
			List<Port> ports = portDao.getportListByPortIDs(portID);
			Node fromNode = nodeDao.getNodeByPortId(portID[0]);
			Node toNode = nodeDao.getNodeByPortId(portID[1]);
			// 先删除端口所对应的链路
			System.out.println("开始删除Openstack层上的链路" + new Date());
			LinkController controller = new LinkController();
			// 什么时候删除链路，当且仅当两个端口同时被占用的时候才有链路存在，此时才有链路可删
			System.out.println("开始删除链路了");
			controller.delLinkMTM(fromNode.getNodeName(), ports.get(0).getPortIp(), toNode.getNodeName(),
					ports.get(1).getPortIp());
			System.out.println("删除Openstack层上的链路结束" + new Date());
			// 再删除端口
			deteledao.deletePortIncludeLink(port, portID[0], portID[1]);
		} else { // 否则不存在链路，那就直接删除此端口
			deteledao.deletePortIncludeLink(port, portID[0], portID[1]);
		}
	}

	/*
	 * 仅仅删除Openstack上的端口
	 */
	public void deletePortOnlyOpenstack(Integer pt_id) {
		PortDao portDao = new PortDaoImpl();
		NodeDao nodeDao = new NodeDaoImpl();
		// 拿到被删除的port
		Port port = portDao.getPortByID(pt_id);
		// 拿到删除port另一端的port,若链路不存在则返回｛0，0 ｝。
		int portID[] = portDao.getPortIdsOnSameLinkByPort(port);
		// 只要portID数组中任意端口号不为0 说明存在链路
		if (portID[0] != 0) {
			List<Port> ports = portDao.getportListByPortIDs(portID);
			Node fromNode = nodeDao.getNodeByPortId(portID[0]);
			Node toNode = nodeDao.getNodeByPortId(portID[1]);
			// 先删除端口所对应的链路
			System.out.println("开始删除Openstack层上的链路" + new Date());
			LinkController controller = new LinkController();
			// 什么时候删除链路，当且仅当两个端口同时被占用的时候才有链路存在，此时才有链路可删
			System.out.println("开始删除链路了");
			controller.delLinkMTM(fromNode.getNodeName(), ports.get(0).getPortIp(), toNode.getNodeName(),
					ports.get(1).getPortIp());
			System.out.println("删除Openstack层上的链路结束" + new Date());
			// 再删除端口
		}
	}

	public void editPort(Port port) {
		PortDao portDao = new PortDaoImpl();
		portDao.editPort(port);

		// TODO
	}

	public Port getPort(int pt_id) {
		// TODO Auto-generated method stub

		return new PortDaoImpl().getPortByID(pt_id);
	}

}
