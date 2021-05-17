package cn.edu.uestc.platform.dealwithstk;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV3;

import STKSoftwareController.STKUtil;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;
import cn.edu.uestc.platform.utils.CloneUtil;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.TCUtils;
import cn.edu.uestc.platform.winter.docker.PortUtils;
import cn.edu.uestc.platform.winter.openstack.PortThread;

public class DynamicController {
	private static Logger logger = Logger.getLogger(DynamicController.class);
	private int s_id;
	private Integer currentTime;
	private OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
	LinkDao linkDao = new LinkDaoImpl();
	List<Link> dblinks = linkDao.getLinkList(s_id);

	public DynamicController(int s_id, Integer currentTime) {
		this.s_id = s_id;
		this.currentTime = currentTime;

	}

	/*
	 * map:上5秒的拓扑 (此链路在数据库中不一定有 ） CopyedMap：备份map 此备份的map将一直记录上一秒的链路状况， 用于return
	 * linksOncurrentTime linksOncurrentTime 存储当前时间的链路
	 * 
	 */
	public Map startSimulation(HashMap<String, Set<LinkForInitialScenario>> map) throws IOException {
		LinkDaoImpl linkDaoImpl = new LinkDaoImpl();
		// 备份map 此备份的map将一直记录上一秒的链路状况
		// Map<String, Set<LinkForInitialScenario>> CopyedMap = (Map<String,
		// Set<LinkForInitialScenario>>) map.clone();

		STKAnalyse stkAnalyse = new STKAnalyse();
		// 当前时间点的链路（僅用於遍歷和作为返回的集合）
		HashSet<LinkForInitialScenario> linksOncurrentTime = stkAnalyse.getLinkForInitialScenario(this.s_id,
				currentTime.toString());
		// 当前时间点链路的备份(深度拷貝)
		HashSet<LinkForInitialScenario> copyCurrentTimes = CloneUtil.clone(linksOncurrentTime);

		// if (currentTime % 5 == 1) {
		// 第一秒的时候 不用跟上一秒比，仅需要查看这一秒的链路有没有可能是0秒时候的链路，如果是 就断开 如果不是 就创建链路
		if (1 <= currentTime && currentTime <= 4) {
			Iterator<LinkForInitialScenario> it = map.get(currentTime.toString()).iterator();
			while (it.hasNext()) {
				// 去除之前数据库中状态为3的链路
				LinkForInitialScenario link = it.next();
				portConnect.CreateConnect(link, s_id);

				STKUtil.addLine(link.getFromNodeName(), link.getToNodeName());
				// new PortThread(it., nodeIp)
				for (Link l : dblinks) {
					if (((l.getFromNodeName().equals(link.getFromNodeName()))
							&& (l.getFromNodeName().equals(link.getToNodeName())))
							|| ((l.getFromNodeName().equals(link.getToNodeName()))
									&& (l.getToNodeName().equals(link.getFromNodeName())))) {
						Thread thread1 = new Thread(new PortThread(l.getFromNodeName(), l.getFromNodeIP()));
						Thread thread2 = new Thread(new PortThread(l.getToNodeName(), l.getToNodeIP()));
						thread1.start();
						thread2.start();
						try {
							thread1.join();
							thread2.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		} else {
			for (LinkForInitialScenario copyCurrentTime : linksOncurrentTime) {
				if (map.get(new Integer(currentTime % 5).toString()).contains(copyCurrentTime)) {
					logger.info("上一个5min包含了 " + copyCurrentTime + "+ 这条链路，无链路增删，开始更新此链路的长度。。。。");
					linkDaoImpl.updateLinkLength(linkDaoImpl.getLinkNameByLinkForInitialScenario(copyCurrentTime),
							Float.parseFloat(copyCurrentTime.getRange()));
					logger.info("链路" + copyCurrentTime + "的长度更新完毕,正在执行长度相关的TC操作");
					if(copyCurrentTime.getFromNodeName().equals("")){
						TCUtils.addTC(copyCurrentTime.getFromNodeName(), copyCurrentTime.getFromIp(), "bw", "loss", "distance");
					}
					logger.info("链路" + copyCurrentTime + "TC长度更新完毕");
					// 去掉交集wo
					map.get(new Integer(currentTime % 5).toString()).remove(copyCurrentTime);
					copyCurrentTimes.remove(copyCurrentTime);
				}
			}
			logger.info("与上5min的交集链路处理完毕 ，开始删除前5min中断开的链路");
			// 删除已经断开的链路
			deleteLink(map.get(new Integer(currentTime % 5).toString()));
			// 去除数据库中存在的链路
			logger.info("刪除開始已經存在的鏈路");
			this.deleteLinkExistInDB(copyCurrentTimes);
			logger.info("刪除重複鏈路結束");
			// 添加新生成的链路
			this.addLink(copyCurrentTimes);
			logger.info("新产生的链路生成完毕");

		}
		// 更新开始copy的map为下一分钟做准备
		// 此时的map已经不是最开始传进来的map了 因为在上面的map中已经对map中的某个键下的集合中的成员进行了删除，当然此函数每执行一次
		// 只删除某一个值的集合中的元素
		// 那我现在拿到这个键对应的集合 清空 重新put新的集合进去就ok
		// 清空元素
		map.get(new Integer(currentTime % 5).toString()).clear();
		linksOncurrentTime.addAll(copyCurrentTimes);
		map.put(new Integer(currentTime % 5).toString(), linksOncurrentTime);

		return map;
	}

	/*
	 * map中多出来的链路 有可能是 if(这个链路在数据中的状态是2 则本来就未创建 不作处理){
	 * 
	 * }else{ 删除这条链路。 1、删除链路两端的端口 2、更新数据库状态 3、。。。。 }
	 */
	public void deleteLink(Set<LinkForInitialScenario> links) {
		LinkDaoImpl linkDaoImpl = new LinkDaoImpl();
		List<Link> dblinks = linkDaoImpl.getLinkListWhereStatue3(s_id);

		List<Link> willDelLink = new LinkedList<>();
		// 看看新增加的鏈路裏面有沒有之前就存在的鏈路 如果存在就斷開這條鏈路
		for (Link dblink : dblinks) {
			for (LinkForInitialScenario link : links) {
				if ((link.getFromNodeName().equals(dblink.getFromNodeName())
						&& link.getToNodeName().equals(dblink.getToNodeName()))
						|| ((link.getFromNodeName().equals(dblink.getToNodeName()))
								&& (link.getToNodeName().equals(dblink.getFromNodeName())))) {
					portConnect.delPort(s_id, dblink);
					STKUtil.deleteLine(dblink.getFromNodeName(), dblink.getToNodeName());
					// willDelLink.add(dblink);
					PortUtils.delPort(os, dblink.getFromNodeName(), dblink.getFromNodeIP());
					PortUtils.delPort(os, dblink.getToNodeName(), dblink.getToNodeIP());
				}
			}
		}

	}

	// 添加链路
	public void addLink(Set<LinkForInitialScenario> links) {
		boolean issuccess = false;
		for (LinkForInitialScenario link : links) {
			issuccess = portConnect.CreateConnect(link, s_id);
			if (issuccess) {
				STKUtil.addLine(link.getFromNodeName(), link.getToNodeName());
				Link l = linkDao.getLinkByFNodeNameAndToNodeName(link.getFromNodeName(), link.getToNodeName());
				Thread thread1 = new Thread(new PortThread(l.getFromNodeName(), l.getFromNodeIP()));
				Thread thread2 = new Thread(new PortThread(l.getToNodeName(), l.getToNodeIP()));
				thread1.start();
				thread2.start();
				try {
					thread1.join();
					thread2.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * 
	 * @param links
	 *            新增加的链路（包含部分可能是之前某个时间断开的链路）
	 */
	public void deleteLinkExistInDB(HashSet<LinkForInitialScenario> links) {
		LinkDaoImpl linkDaoImpl = new LinkDaoImpl();
		List<Link> dblinks = linkDaoImpl.getLinkListWhereStatue3(s_id);
		Set<LinkForInitialScenario> alreadyExitlinks = new HashSet<>();

		// 对其深度克隆 仅用于遍历
		HashSet<LinkForInitialScenario> copylinks = CloneUtil.clone(links);

		// 看看新增加的鏈路裏面有沒有之前就存在的鏈路 如果存在就斷開這條鏈路
		for (Link dblink : dblinks) {
			for (LinkForInitialScenario link : copylinks) {
				if ((link.getFromNodeName().equals(dblink.getFromNodeName())
						&& link.getToNodeName().equals(dblink.getToNodeName()))
						|| ((link.getFromNodeName().equals(dblink.getToNodeName()))
								&& (link.getToNodeName().equals(dblink.getFromNodeName())))) {
					links.remove(link);
					alreadyExitlinks.add(link);
				}
			}
		}
		logger.info("开始删除前5min中某个时间点断开的链路");
		for (LinkForInitialScenario l : alreadyExitlinks) {
			logger.info("删除链路:" + l.getFromNodeName() + "-" + l.getToNodeName());
		}
		this.deleteLink(alreadyExitlinks);
		logger.info("删除断开的链路结束");
	}

	@Test
	public void demo() throws IOException {
		// for(startSimulation(56, 5);
		// startSimulation(56, 0);
	}

}
