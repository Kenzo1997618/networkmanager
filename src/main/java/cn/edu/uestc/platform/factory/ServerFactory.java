package cn.edu.uestc.platform.factory;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.network.NetFloatingIP;

import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.NetworkUtils;

/**
 * Openstack Server实例工厂类
 */
public class ServerFactory {
	private static Logger logger = Logger.getLogger(ServerFactory.class);

	/**
	 * 生产一个虚拟机实例对象,同时添加浮动ip
	 * 
	 * @param os
	 *            认证对象
	 * @param name
	 *            虚拟机的名称
	 * @param extIp
	 *            虚拟机的外部ip,应该为192.168.5.0网段，必须存在，因为要SSH进入虚拟机
	 * @return 创建好的虚拟机Server实例
	 */
	public static Server createServer(OSClientV3 os, String name, String extIp) {
		// ServerCreate sc =
		// Builders.server().name(name).flavor(Constants.NOVA_FLAVOR).image(Constants.GLANCE_IMAGE).availabilityZone(Constants.VM_ZONE)
		// .build();
		
		//leiside 
		ServerCreate sc = Builders.server().name(name).flavor(Constants.NOVA_FLAVOR).image(Constants.GLANCE_IMAGE)
				.availabilityZone("amd").build();

		System.out.println(sc);
		// 添加固定外网端口的IP，用与外部通信ssh进入虚拟机
		sc.addNetwork(NetworkUtils.getIdByIp(extIp), extIp);
		System.out.println("exe add ip");
		Server server = os.compute().servers().boot(sc);

		os.compute().servers().action(server.getId(), Action.START);
		// 为这个实例添加浮动ip
		if(extIp.contains("192.168.10")){
			addFloatingIP(os, server);
		}else{
			// 添加浮动IP,要加锁
			FloatingIP floatingIP = os.compute().floatingIps().allocateIP("ext_zk");
			NetFloatingIP netFloatingIP = os.networking().floatingip().get(floatingIP.getId());
			// 将此浮动IP添加到server上,增加一个while循环进行判断
			int flag = 400;
			ActionResponse as = null;
			while (flag == 400) {
				try {
					TimeUnit.MILLISECONDS.sleep(1000);// 每隔1s检测一下是否将分配好了浮动ip添加到server上
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				as = os.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());
				flag = as.getCode();
			}
			logger.info("为 " + server + " 节点分配好了浮动ip : " + netFloatingIP.getFloatingIpAddress());
		}
		return server;
	}

	/**
	 * 生产一个Docker实例对象 (docker不需要浮动IP)
	 * 
	 * @param os
	 *            认证对象
	 * @param name
	 *            docker的名称
	 * @param extIp
	 *            虚拟机的外部ip,应该为192.168.5.0网段
	 * @return 创建好的虚拟机Server实例
	 */
	public static Server createDockerServer(OSClientV3 os, String name, String extIp) {
		ServerCreate sc = Builders.server().name(name).flavor(Constants.DOCKER_FLAVOR).image(Constants.DOCKER_IMAGE)
				.availabilityZone(Constants.DOCKER_ZONE).build();
		// 添加固定外网端口的IP，用与外部通信ssh进入虚拟机
		sc.addNetwork(NetworkUtils.getIdByIp(extIp), extIp);
		Server server = os.compute().servers().boot(sc);
		os.compute().servers().action(server.getId(), Action.START);
		return server;
	}

	/**
	 * 为一个Server节点添加浮动ip
	 * 
	 * @return 返回成功添加浮动ip的地址
	 */
	public static String addFloatingIP(OSClientV3 os, Server server) {
		// 添加浮动IP,要加锁
		FloatingIP floatingIP = NetworkUtils.createFloatingIp(os);
		NetFloatingIP netFloatingIP = os.networking().floatingip().get(floatingIP.getId());
		// 将此浮动IP添加到server上,增加一个while循环进行判断
		int flag = 400;
		ActionResponse as = null;
		while (flag == 400) {
			try {
				TimeUnit.MILLISECONDS.sleep(1000);// 每隔1s检测一下是否将分配好了浮动ip添加到server上
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			as = os.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());
			flag = as.getCode();
		}
		logger.info("为 " + server + " 节点分配好了浮动ip : " + netFloatingIP.getFloatingIpAddress());
		return netFloatingIP.getFloatingIpAddress();
	}

	/**
	 * 为一个节点动态添加端口,同时进入节点修改配置文件
	 */
	public static void addPort(OSClientV3 os, String nodeName, String portIP) {
		// 如果名为nodeName的虚拟机有 portIP这个ip地址，则不对它进行任何操作
		if (!NetworkUtils.isHaveIP(os, nodeName, portIP)) {
			// 将这个ip添加到节点上
			NetworkUtils.addIPToNode(nodeName, portIP);
			// 进入节点修改配置文件
			String floatIP = NetworkUtils.getFloatIpByNodeName(nodeName);// 通过虚拟机的名称得到浮动ip的地址
			System.out.println("虚拟机 " + nodeName + " 的浮动ip地址为： " + floatIP);
			NetworkUtils.changeNetworkConfig(floatIP, portIP);// 通过浮动ip进入虚拟机修改配置文件
		}
	}

	/**
	 * 为一个节点删除指定端口，同时进入节点修改配置文件----->待实现
	 */
	public static void delPort(OSClientV3 os, String nodeName, String portIP) {
		// 如果名为nodeName的虚拟机有 portIP这个ip地址，则删除这个端口
		if (NetworkUtils.isHaveIP(os, nodeName, portIP)) {
			NetworkUtils.delIPToNode(nodeName, portIP);
		}
	}
}
