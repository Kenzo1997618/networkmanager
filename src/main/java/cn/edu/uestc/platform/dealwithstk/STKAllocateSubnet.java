package cn.edu.uestc.platform.dealwithstk;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import cn.edu.uestc.platform.action.ActionController;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dynamicChange.DynamicNetWorkUtils;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;
import cn.edu.uestc.platform.service.LinkService;

public class STKAllocateSubnet {
	private static LinkDao linkDao = new LinkDaoImpl();
	private static Logger logger = Logger.getLogger(ActionController.class);

	@Test
	public void demo5() {
		allocateSubnetForGEOToGEO(6, 56);
	}

	// 分配GEO-GEO的网段 生成 GEO对GEO 卫星表，规则：任意两个GEO属于不同网段，GEO的网段为10.10.1.4开头
	public static void allocateSubnetForGEOToGEO(int GEONum, int s_id) {
		// LinkService linkService = new LinkService();
		logger.info("开始为GEOToGEO创建创建子网段");
		if (GEONum <= 1) {
			System.out.println("GEO数量仅为1 不存在GEO与GEO之间的链路");
		} else if (GEONum == 2) {
			// 两个GEO创建一条链路
			Link link = new Link();
			link.setFromNodeName("GEO11");
			link.setToNodeName("GEO12");
			link.setLogicalFromNodeName("GEO11");
			link.setLogicalToNodeName("GEO12");
			link.setFromNodeIP("10.10.1.4");
			link.setToNodeIP("10.10.1.5");
			// 最开始链路未真实连接 默认距离为999999999
			link.setLinkLength(99999999);
			// 规定stk在创建完网段的时候linkStatus为2
			link.setLinkStatus(2);
			link.setScenario_id(s_id);
			DynamicNetWorkUtils.createSubnetByIP("10.10.1.4");
			linkDao.insertLink(link);
		} else {
			// GEONUM大于2 可以形成环型 所有链路总数等于GEONum数
			for (int i = 1; i < GEONum; i++) {
				Link link = new Link();
				link.setLinkName("GEO1" + i + "-" + "GEO1" + (i + 1));
				link.setFromNodeName("GEO1" + i);
				link.setToNodeName("GEO1" + (i + 1));
				link.setLogicalFromNodeName("GEO1" + i);
				link.setLogicalToNodeName("GEO1" + (i + 1));
				link.setFromNodeIP("10.10." + i + ".4");
				link.setToNodeIP("10.10." + i + ".5");
				link.setLinkLength(99999999);
				link.setLinkStatus(2);
				link.setScenario_id(s_id);
				linkDao.insertLink(link);
				if (i == GEONum - 1) {
					link = new Link();
					link.setLinkName("GEO11" + "-" + "GEO1" + (i + 1));
					link.setFromNodeName("GEO11");
					link.setToNodeName("GEO1" + (i + 1));
					link.setLogicalFromNodeName("GEO11");
					link.setLogicalToNodeName("GEO1" + (i + 1));
					link.setFromNodeIP("10.10." + (i + 1) + ".4");
					link.setToNodeIP("10.10." + (i + 1) + ".5");
					link.setLinkLength(99999999);
					link.setLinkStatus(2);
					link.setScenario_id(s_id);
					linkDao.insertLink(link);
					DynamicNetWorkUtils.createSubnetByIP("10.10." + (i + 1) + ".4");
				}
				DynamicNetWorkUtils.createSubnetByIP("10.10." + i + ".4");
			}
		}
		logger.info("创建子网段结束");
	}

	// // ground对GEO 10.50.1.4
	// public static void allocateSubnetForGroundToGEO(int s_id, int GEONum, int
	// GroundNum) {
	// logger.info("开始为GroundToGEO创建创建子网段");
	// for (int i = 1; i <= GEONum; i++) {
	// for (int j = 1; j <= GroundNum; j++) {
	// Link link = new Link();
	// link.setLinkName("GEO1" + i + "-" + "GroundVehicle1" + j);
	// link.setFromNodeName("GEO1" + i);
	// link.setToNodeName("GroundVehicle1" + j);
	// link.setFromNodeIP("10.50." + i + ".220");
	// link.setToNodeIP("10.50." + i + "." + (j + 4));
	// link.setLinkLength(99999999);
	// link.setLinkStatus(2);
	// link.setScenario_id(s_id);
	// linkDao.insertLink(link);
	// }
	// // 创建网段
	// // DynamicNetWorkUtils.createSubnetByIP("10.50." + i + ".220");
	// }
	// logger.info("GroundToGEO网段分配结束");
	// }

	// LEO TO Ground
	// 10.50.1.4
	public static void allocateSubnetForLEOToGround(int s_id, int LEORow, int LEOColumn, int GroundNum) {
		logger.info("开始为LEOToGround创建创建子网段");
		int index = 4;
		for (int i = 1; i <= GroundNum; i++) {
			for (int j = 1; j <= LEORow; j++) {
				for (int k = 1; k <= LEOColumn; k++) {
					Link link = new Link();
					link.setLinkName("LEO1" + j + k + "-" + "GroundVehicle1" + i);
					link.setFromNodeName("LEO1" + j + k);
					link.setToNodeName("GroundVehicle1" + i);
					
					link.setLogicalFromNodeName("LEO1" + j + k);
					link.setLogicalToNodeName("GroundVehicle1" + i);
					link.setFromNodeIP("10.50." + i + "." + index++);
					link.setToNodeIP("10.50." + i + ".220");
					link.setLinkLength(99999999);
					link.setLinkStatus(2);
					link.setScenario_id(s_id);
					linkDao.insertLink(link);
				}
			}
			index = 4;
			// 创建网段
			DynamicNetWorkUtils.createSubnetByIP("10.50." + i + ".220");
		}
		logger.info("GroundToGEO网段分配结束");
	}

	// GEO与Facility 网段为10.60.1.4开始
	public static void allocateSubnetForGEOToFacility(int s_id, int GEONum, int FacilityNum) {
		logger.info("开始为GEOToFacility创建创建子网段");

		for (int i = 1; i <= GEONum; i++) {
			for (int j = 1; j <= FacilityNum; j++) {
				Link link = new Link();
				link.setLinkName("GEO1" + i + "-" + "Facility" + j);
				link.setFromNodeName("GEO1" + i);
				link.setToNodeName("Facility" + j);
				link.setLogicalFromNodeName("GEO1" + i);
				link.setLogicalToNodeName("Facility" + j);
				link.setFromNodeIP("10.60." + i + ".220");
				link.setToNodeIP("10.60." + i + "." + (j + 4));
				link.setLinkLength(99999999);
				link.setLinkStatus(2);
				link.setScenario_id(s_id);
				linkDao.insertLink(link);
			}
			// 创建网段
			DynamicNetWorkUtils.createSubnetByIP("10.60." + i + ".220");
		}
		logger.info("GEOToFacility网段分配结束");
	}

	// LEO与Facility 网段为10.70.1.4开始
	// 创建网段个数等于Facility的节点数，但是需要给每一个LEO1xy与Facility分配可以连接的链路 需要传入LEO的横纵坐标
	/*
	 * public static void allocateSubnetForFacilityToLEO(int s_id, int LEORow,
	 * int LEOColumn, int FacilityNum) { logger.info("开始为FacilityToLEO创建创建子网段");
	 * 
	 * for (int i = 1; i <= FacilityNum; i++) { int n = 4; for (int j = 1; j <=
	 * LEORow; j++) { for (int m = 1; m <= LEOColumn; m++) { Link link = new
	 * Link(); link.setLinkName("LEO1" + j + m + "-" + "Facility" + i);
	 * link.setFromNodeName("Facility" + i); link.setToNodeName("LEO1" + j + m);
	 * link.setFromNodeIP("10.70." + i + ".220"); link.setToNodeIP("10.70." + i
	 * + "." + n++); link.setLinkLength(99999999); link.setLinkStatus(2);
	 * link.setScenario_id(s_id); linkDao.insertLink(link); } } // 创建网段 //
	 * DynamicNetWorkUtils.createSubnetByIP("10.70." + i + ".220"); }
	 * logger.info("FacilityToLEO网段分配结束"); }
	 */

	// LEO与LEO上下的链路 10.30.1.4
	public static void allocateSubnetForLEOToLEOUD(int s_id, int LEORow, int LEOColumn) {
		logger.info("开始为LEOToLEOUD创建创建子网段");
		int index = 1;
		for (int i = 1; i < LEORow; i++) {
			for (int j = 1; j <= LEOColumn; j++) {
				Link link = new Link();
				link.setFromNodeName("LEO1" + i + j);
				link.setToNodeName("LEO1" + (i + 1) + j);
				link.setLogicalFromNodeName("LEO1" + i + j);
				link.setLogicalToNodeName("LEO1" + (i + 1) + j);
				link.setLinkName("LEO1" + i + j + "-" + "LEO1" + (i + 1) + j);
				link.setFromNodeIP("10.30." + index + "." + 4);
				link.setToNodeIP("10.30." + index + "." + 5);
				link.setLinkLength(99999999);
				link.setLinkStatus(2);
				link.setScenario_id(s_id);
				linkDao.insertLink(link);
				DynamicNetWorkUtils.createSubnetByIP("10.30." + index + ".220");
				index++;
			}
		}
		// 如果到最后一行了 那就只有跟第一行的LEO相连
		for (int j = 1; j <= LEOColumn; j++) {
			Link link = new Link();
			link.setFromNodeName("LEO1" + 1 + j);
			link.setToNodeName("LEO1" + LEORow + j);
			link.setLogicalFromNodeName("LEO1" + 1 + j);
			link.setLogicalToNodeName("LEO1" + LEORow + j);
			link.setLinkName("LEO1" + 1 + j + "-" + "LEO1" + LEORow + j);
			link.setFromNodeIP("10.30." + index + "." + 4);
			link.setToNodeIP("10.30." + index + "." + 5);
			link.setLinkLength(99999999);
			link.setLinkStatus(2);
			link.setScenario_id(s_id);
			linkDao.insertLink(link);
			DynamicNetWorkUtils.createSubnetByIP("10.30." + index + ".220");
			index++;
		}
		System.out.println(index);
		// 分配第一行与最后一行的网段
		logger.info("LEOToLEOUD网段分配结束");
	}

	// LEO与LEO左右的链路 9.26暂时先认为左右坐标 之后改为距离最近的链路
	// 之后在生成网段之前 先计算出其中一个节点跟左边轨道最近的节点的差值 那么这个差值将对应于每根轨道。
	// 10.40.1.4
	public static void allocateSubnetForLEOToLEOLR(int s_id, int LEORow, int LEOColumn) {
		logger.info("开始为LEOToLEOLR创建创建子网段");
		int index = 1;
		for (int i = 1; i <= LEORow; i++) {
			for (int j = 1; j < LEOColumn; j++) {
				Link link = new Link();
				link.setFromNodeName("LEO1" + i + j);
				link.setToNodeName("LEO1" + i + (j + 1));
				link.setLogicalFromNodeName("LEO1" + i + j);
				link.setLogicalToNodeName("LEO1" + i + (j + 1));
				link.setLinkName("LEO1" + i + j + "-" + "LEO1" + i + (j + 1));
				link.setFromNodeIP("10.40." + index + "." + 4);
				link.setToNodeIP("10.40." + index + "." + 5);
				link.setLinkLength(99999999);
				link.setLinkStatus(2);
				link.setScenario_id(s_id);
				linkDao.insertLink(link);
				DynamicNetWorkUtils.createSubnetByIP("10.40." + index + ".220");
				index++;
			}
		}
		logger.info("LEOToLEOLR网段分配结束");

	}

	/*
	 * 生成GEO对LEO GEO对LEO的网段为10.90.1.4开头(每个GEO为一个网段，参看前边的IPCreateFactory)
	 */
	public static void allocateSubnetForGEOToLEO(int s_id, int GEONum, int LEORow, int LEOColumn) {

		int index = 5;
		logger.info("开始为GEOToLEO创建创建子网段");
		for (int i = 1; i <= GEONum; i++) {
			for (int j = 1; j <= LEORow; j++) {
				for (int k = 1; k <= LEOColumn; k++) {
					Link link = new Link();
					link.setFromNodeName("GEO1" + i);
					link.setToNodeName("LEO1" + j + k);
					link.setLogicalFromNodeName("GEO1" + i);
					link.setLogicalToNodeName("LEO1" + j + k);
					link.setLinkName("GEO1" + i + "-" + "LEO1" + j + k);
					link.setFromNodeIP("10.90." + i + "." + 4);
					link.setToNodeIP("10.90." + i + "." + (index));
					link.setLinkLength(99999999);
					link.setLinkStatus(2);
					link.setScenario_id(s_id);
					linkDao.insertLink(link);
					index++;
				}
			}
			DynamicNetWorkUtils.createSubnetByIP("10.90." + i + ".220");
			index = 5;
		}
	}

}
