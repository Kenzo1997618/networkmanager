package cn.edu.uestc.platform.winter.docker;

import org.openstack4j.api.OSClient.OSClientV3;

import cn.edu.uestc.platform.dynamicChange.DynamicNetWorkUtils;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.winter.openstack.ServerUtils;

public class PortUtils {
	/**
	 * 为一个 Docker 节点动态添加端口,同时进入节点修改配置文件(zebra的配置文件)
	 */
	public static void addPort(OSClientV3 os, String nodeName, String portIP) {
		// 如果名为nodeName的虚拟机有 portIP这个ip地址，则不对它进行任何操作
		if (!NetworkUtils.isHaveIP(os, nodeName, portIP)) {
			// 将这个ip添加到节点上
			NetworkUtils.addIPToNode(nodeName, portIP);
			DynamicChangeConfig.changeAddQuguaConf("nova-"+ServerUtils.getServer(nodeName).getId(), portIP);
		}
	}
	
	/**
	 * 为一个 Docker 节点动态删除端口,同时进入节点修改配置文件(zebra的配置文件)
	 */
	public static void delPort(OSClientV3 os, String nodeName, String portIP) {
		if (NetworkUtils.isHaveIP(os, nodeName, portIP)) {
			DynamicChangeConfig.changeDelQuguaConf("nova-"+ServerUtils.getServer(nodeName).getId(), portIP);
//			NetworkUtils.delIPToNode(nodeName, portIP);
			DynamicNetWorkUtils.delIPToNode(nodeName, portIP);
		}
	}

	public static void main(String[] args) {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		addPort(os, "LEO131", "10.30.10.6");
	}
}
