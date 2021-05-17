package cn.edu.uestc.platform.dealwithstk;

import java.util.List;

import org.apache.log4j.Logger;
import org.openstack4j.api.OSClient.OSClientV3;

import STKSoftwareController.STKUtil;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.winter.docker.DynamicChangeConfig;
import cn.edu.uestc.platform.winter.docker.PortUtils;

public class portConnect {

	private static LinkDao linkDao = new LinkDaoImpl();

	private static OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);

	// private static PortThread portThread = new PortThread(nodeName, nodeIp)
	private static Logger logger = Logger.getLogger(portConnect.class);

	// 链路创建
	public static boolean CreateConnect(LinkForInitialScenario link, int s_id) {
		List<Link> links = linkDao.getLinkList(s_id);
		linkDao = new LinkDaoImpl();
		float shortestLink = Float.parseFloat(link.getRange());
		boolean flag = false;
		for (Link dblink : links) {
			// 存在這條鏈路，現在
			if ((dblink.getLinkName().equals((link.getFromNodeName()) + "-" + link.getToNodeName()))
					|| (dblink.getLinkName().equals(link.getToNodeName() + "-" + link.getFromNodeName()))) {
				flag = true;
				// 有这条链路 更新这条链路的长度
				linkDao.updateLinkLength(dblink.getLinkName(), Float.parseFloat(link.getRange()));

				// GEO 与 Facility
				if ((link.getFromNodeName().contains("GEO") && link.getToNodeName().contains("Facility"))
						|| ((link.getFromNodeName().contains("Facility") && link.getToNodeName().contains("GEO")))) {
					if (isShortestLink(link, s_id, shortestLink)) {
						delLinkIsNotShortest(dblink.getToNodeName(), s_id);
						createPort(s_id, dblink);
						return true;
						// 如果之前已经存在一条其他的Facility链路 就删除之前的链路
					}
				} // LEO 与 GroundVehicl
				else if ((link.getFromNodeName().contains("LEO") && link.getToNodeName().contains("GroundVehicl"))
						|| ((link.getFromNodeName().contains("GroundVehicl"))
								&& (link.getToNodeName().contains("LEO")))) {
					if (isShortestLink(link, s_id, shortestLink)) {
						delLinkIsNotShortest(dblink.getToNodeName(), s_id);
						createPort(s_id, dblink);
						return true;
					}
				} // GEO 与 GEO
				else if ((link.getFromNodeName().contains("GEO") && link.getToNodeName().contains("GEO"))) {
					createPort(s_id, dblink);
					return true;
				} // LEO 与 LEO
				else if (link.getFromNodeName().contains("LEO") && link.getToNodeName().contains("LEO")) {
					createPort(s_id, dblink);
					return true;
				}
				// GEO 与 LEO
				else if ((link.getFromNodeName().contains("GEO") && link.getToNodeName().contains("LEO"))
						|| ((link.getFromNodeName().contains("LEO")) && (link.getToNodeName().contains("GEO")))) {
					if (isShortestLink(link, s_id, shortestLink)) {
						delLinkIsNotShortest(dblink.getToNodeName(), s_id);
						createPort(s_id, dblink);
						return true;
					}
				}
			}
		}
		if (flag == false) {
			logger.info("链路" + link.getFromNodeName() + "-" + link.getToNodeName() + "在数据库中不存在，不符合链路规则，此条链路不予创建");
			return false;
		}
		return false;

	}

	public static void createPort(int s_id, Link dblink) {
		logger.info("此链路在数据库中是存在的，符合链路规则，开始为" + dblink.getLinkName() + "链路创建端口");
		linkDao.updateLinkStatusto3(s_id, dblink.getLinkName());
		// STKUtil.addLine(dblink.getFromNodeName(), dblink.getToNodeName());
		logger.info("创建端口成功，" + dblink.getLinkName() + "已经成功被创建");
	}

	// 此函数有待改进 2017.10.06--zk
	private static boolean isShortestLink(LinkForInitialScenario link, int s_id, float shortestLink) {
		// LEO与GroundVehicl
		if ((link.getToNodeName().contains("GroundVehicl") && (link.getFromNodeName().contains("LEO")))) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(link.getToNodeName(), s_id);
			for (Link l : linkList) {
				// 发现其他GEO与Facility的距离更短 直接停止创建链路
				if (l.getLinkLength() < shortestLink && (l.getLinkStatus() == 3)) {
					logger.info("已经存在" + l.getFromNodeName() + "比" + link.getFromNodeName() + "距离"
							+ link.getToNodeName() + "更近，此条链路不创建");
					return false;
				}
			}
		} else if ((link.getToNodeName().contains("LEO") && (link.getFromNodeName().contains("GroundVehicl")))) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(link.getFromNodeName(), s_id);
			for (Link l : linkList) {
				// 发现其他GEO与Facility的距离更短 直接停止创建链路
				if (l.getLinkLength() < shortestLink && (l.getLinkStatus() == 3)) {
					logger.info("已经存在" + l.getFromNodeName() + "比" + link.getFromNodeName() + "距离"
							+ link.getToNodeName() + "更近，此条链路不创建");
					return false;
				}
			}
		}

		// LEO与Facility
		if ((link.getToNodeName().contains("Facility") && (link.getFromNodeName().contains("GEO")))) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(link.getToNodeName(), s_id);
			for (Link l : linkList) {
				// 发现其他GEO与Facility的距离更短 直接停止创建链路
				if ((l.getLinkLength() < shortestLink) && (l.getLinkStatus() == 3)) {
					logger.info("已经存在" + l.getFromNodeName() + "比" + link.getFromNodeName() + "距离"
							+ link.getToNodeName() + "更近，此条链路不创建");
					return false;
				}
			}
		} else if ((link.getToNodeName().contains("GEO") && (link.getFromNodeName().contains("Facility")))) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(link.getFromNodeName(), s_id);
			for (Link l : linkList) {
				// 发现其他GEO与Facility的距离更短 直接停止创建链路
				if (l.getLinkLength() < shortestLink && (l.getLinkStatus() == 3)) {
					logger.info("已经存在" + l.getFromNodeName() + "比" + link.getFromNodeName() + "距离"
							+ link.getToNodeName() + "更近，此条链路不创建");
					return false;
				}
			}
		}

		// LEO与GEO的
		if (link.getToNodeName().contains("LEO") && link.getFromNodeName().contains("GEO")) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(link.getToNodeName(), s_id);
			for (Link l : linkList) {
				// 发现其他GEO与Facility的距离更短 直接停止创建链路
				if ((l.getLinkLength() < shortestLink) && (l.getLinkStatus() == 3)
						&& (l.getLinkName().contains("GEO"))) {
					logger.info("已经存在" + l.getFromNodeName() + "比" + link.getFromNodeName() + "距离"
							+ link.getToNodeName() + "更近，此条链路不创建");
					return false;
				}
			}
		} else if (link.getToNodeName().contains("GEO") && link.getFromNodeName().contains("LEO")) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(link.getFromNodeName(), s_id);
			for (Link l : linkList) {
				// 发现其他GEO与Facility的距离更短 直接停止创建链路
				if ((l.getLinkLength() < shortestLink) && (l.getLinkStatus() == 3)
						&& (l.getLinkName().contains("GEO"))) {
					logger.info("已经存在" + l.getFromNodeName() + "比" + link.getToNodeName() + "距离"
							+ link.getFromNodeName() + "更近，此条链路不创建");
					return false;
				}
			}
		}
		return true;
	}

	// 刪除端口
	public static void delPort(int s_id, Link dblink) {
		logger.info("开始为" + dblink.getLinkName() + "链路创建端口");
		// STKUtil.deleteLine(dblink.getFromNodeName(), dblink.getToNodeName());
		linkDao.updateLinkStatusto2(s_id, dblink.getLinkName());
		logger.info("更新鏈路狀態" + s_id + "---" + dblink.getLinkName());

	}

	// 在数据库中 Facility GroundVehicl 这类节点名 都是存在ToNodeName中的
	// 删除不是最短距离的链路 实际上通过 Facility 或者GroundVehicl 把其对应的链路找出来 然后删除这条链路
	public static void delLinkIsNotShortest(String toNodeName, int s_id) {
		if (toNodeName.contains("Facility") || toNodeName.contains("GroundVehicl")) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(toNodeName, s_id);
			for (Link l : linkList) {
				if (l.getLinkStatus() == 3) {
					logger.info("发现之前已经存在一条距离稍长的链路 现在开始断开这条链路" + l.getLinkName());
					// 将这条链路状态变为2
					// STKUtil.deleteLine(l.getFromNodeName(),
					// l.getToNodeName());
					linkDao.updateLinkStatusto2(s_id, l.getLinkName());
					logger.info(l.getLinkName() + "链路删除结束");
				}
			}
		} else if (toNodeName.contains("LEO")) {
			List<Link> linkList = linkDao.getLinkListByToNodeName(toNodeName, s_id);
			for (Link l : linkList) {
				if (l.getLinkName().contains("GEO") && l.getLinkName().contains("LEO")) {
					logger.info("发现之前已经存在一条距离稍长的链路 现在开始断开这条链路" + l.getLinkName());
					// 将这条链路状态变为2
					// STKUtil.deleteLine(l.getFromNodeName(),
					// l.getToNodeName());
					linkDao.updateLinkStatusto2(s_id, l.getLinkName());
					logger.info(l.getLinkName() + "链路删除结束");
				}
			}
		}
	}

}
