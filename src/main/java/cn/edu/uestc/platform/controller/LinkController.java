package cn.edu.uestc.platform.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.openstack4j.api.OSClient.OSClientV3;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.uestc.platform.dynamicChange.DynamicFactory;
import cn.edu.uestc.platform.dynamicChange.DynamicNetWorkUtils;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.winter.docker.PortUtils;
import cn.edu.uestc.platform.winter.openstack.PortThread;
import cn.edu.uestc.platform.winter.openstack.ServerUtils;

@Controller
@RequestMapping("/link")
public class LinkController {
	//日志
	private static Logger logger = Logger.getLogger(LinkController.class);

	/**
	 * 添加链路
	 * 
	 * @param fromNode
	 *            from节点名称
	 * @param fromNodeIP
	 *            from节点ip
	 * @param toNode
	 *            to节点名称
	 * @param toNodeIP
	 *            to节点ip
	 */
	@ResponseBody
	@RequestMapping(value = "/addMTM", method = RequestMethod.POST)
	//ResponseEntity标识整个http相应：状态码、头部信息、响应体内容(spring)
	public ResponseEntity<Link> createLinkMTM(String fromNode, String fromNodeIP, String toNode, String toNodeIP) {
		logger.info("进入链路创建部分(多对多)  fromNode " + fromNode + " fromNodeIP " + fromNodeIP + " toNode " + toNode
				+ " toNodeIP " + toNodeIP);
		
		//authenticate：师兄编写的工厂类，功能：完成openstack的keystone认证
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		// 1.拿到fromNode的节点信息，看他是否存在与fromNodeIP相同的网段
		if (NetworkUtils.isHaveIP(os, fromNode, fromNodeIP)) {
			// 2.如果存在，则不生成子网，直接将toNodeIP添加到toNode上
			if (ServerUtils.getServer(toNode).getAvailabilityZone().equals("vm")
					|| ServerUtils.getServer(toNode).getAvailabilityZone().equals("amd")) {// vm
				DynamicFactory.addPort(os, toNode, toNodeIP);
			} else {// docker
				PortUtils.addPort(os, toNode, toNodeIP);
			}
		} else {
			// 3.如果不存在，则生成子网，再将fromNodeIP和toNodeIP添加到fromNode和toNode上边
			// 1.拿到fromNode的ip，截取网络号，生成子网
			DynamicNetWorkUtils.createSubnetByIP(fromNodeIP);
			// 2.进入controller节点为fromNode和toNode添加端口
			// 3.进入fromNode和toNode修改网卡和zebra的配置文件并重启，重启后开启zebra+ospfd
			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new PortThread(toNode, toNodeIP));
			exec.execute(new PortThread(fromNode, fromNodeIP));
			exec.shutdown();
			while (true) {
				if (exec.isTerminated()) {
					System.out.println("链路生成结束了！");
					break;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// if(ServerUtils.getServer(toNode).getAvailabilityZone().equals("vm")){//vm
			// DynamicFactory.addPort(os, toNode, toNodeIP);
			// }else{//docker
			// PortUtils.addPort(os, toNode, toNodeIP);
			// }
			//
			// if(ServerUtils.getServer(fromNode).getAvailabilityZone().equals("vm")){//vm
			// DynamicFactory.addPort(os, fromNode, fromNodeIP);
			// }else{//docker
			// PortUtils.addPort(os, fromNode, fromNodeIP);
			// }
		}
		// 返回的对象
		Link link = new Link();
		link.setFromNode(fromNode);
		link.setEndNode(toNode);
		link.setStatus("1");
		return ResponseEntity.ok(link);
	}

	/**
	 * 删除一条链路
	 */
	@ResponseBody
	@RequestMapping(value = "/delMTM", method = RequestMethod.POST)
	public ResponseEntity<Link> delLinkMTM(String fromNode, String fromNodeIP, String toNode, String toNodeIP) {
		logger.info(
				"fromNode " + fromNode + " fromNodeIP " + fromNodeIP + " toNode " + toNode + " toNodeIP " + toNodeIP);
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		// 目前只支持1对1删除链路
		if (ServerUtils.getServer(fromNode).getAvailabilityZone().equals("vm")) {// vm
			DynamicFactory.delPort(os, fromNode, fromNodeIP);
		} else {// docker
			PortUtils.delPort(os, fromNode, fromNodeIP);
		}

		if (ServerUtils.getServer(toNode).getAvailabilityZone().equals("vm")) {// vm
			DynamicFactory.delPort(os, toNode, toNodeIP);
		} else {// docker
			PortUtils.delPort(os, toNode, toNodeIP);
		}
		// 删除网段
		DynamicNetWorkUtils.delNetworkByIP(fromNodeIP);

		Link link = new Link();
		link.setFromNode(fromNode);
		link.setEndNode(toNode);
		link.setStatus("0");
		return ResponseEntity.ok(link);
	}
}
