package cn.edu.uestc.platform.winter.openstack;

import org.openstack4j.api.OSClient.OSClientV3;

import cn.edu.uestc.platform.dynamicChange.DynamicFactory;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.TCUtils;
import cn.edu.uestc.platform.winter.docker.PortUtils;

public class PortThread implements Runnable {
	private String nodeName;
	private String nodeIp;

	public PortThread(String nodeName, String nodeIp) {
		this.nodeName = nodeName;
		this.nodeIp = nodeIp;
	}

	@Override
	public void run() {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		if (ServerUtils.getServer(nodeName).getAvailabilityZone().equals("vm")
				|| ServerUtils.getServer(nodeName).getAvailabilityZone().equals("amd")) {// vm
			DynamicFactory.addPort(os, nodeName, nodeIp);
			
		} else {// docker
			if(nodeName.equals("")){
				TCUtils.addTC(nodeName, nodeIp, "bw", "loss", "distance");
			}
			PortUtils.addPort(os, nodeName, nodeIp);
		}
	}
}
