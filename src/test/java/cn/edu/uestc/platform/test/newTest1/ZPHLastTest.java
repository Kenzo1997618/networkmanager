package cn.edu.uestc.platform.test.newTest1;

import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;

import cn.edu.uestc.platform.dynamicChange.DynamicNetWorkUtils;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.factory.ServerFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.winter.docker.DynamicChangeConfig;
import cn.edu.uestc.platform.winter.docker.PortUtils;
import cn.edu.uestc.platform.winter.openstack.ServerUtils;

public class ZPHLastTest {

	// create docker node test
	@Test
	public void test() {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		int start = 1;
		int end = 5;
		for (int i = start; i  <=end; i++) {
			ServerFactory.createDockerServer(os, "router" + i, "192.168.10." + (100 + i));
			DynamicNetWorkUtils.createSubnetByIP("10.10." + i + "0.6");
		}
	}

	// add a port to a docker container test
	@Test
	public void test1() {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		PortUtils.addPort(os, "router1", "10.10.10.4");
		PortUtils.addPort(os, "router2", "10.10.10.5");
		PortUtils.addPort(os, "router2", "10.10.20.4");
		PortUtils.addPort(os, "router2", "10.10.30.4");
		PortUtils.addPort(os, "router3", "10.10.40.4");
		PortUtils.addPort(os, "router3", "10.10.50.4");
		PortUtils.addPort(os, "router3", "10.10.30.5");
		PortUtils.addPort(os, "router4", "10.10.50.5");
		PortUtils.addPort(os, "router5", "10.10.40.5");
		PortUtils.addPort(os, "router5", "10.10.20.5");
	}

	// last test
	@Test
	public void test3() {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);

		// number of the docker chain
		int start = 16;
		int end = 19;
		for (int i = start; i <= end; i++) {
			ServerFactory.createDockerServer(os, "chain" + i, "192.168.10." + (100 + i));
		}

		for (int i = start; i  <=end; i++) {
			DynamicNetWorkUtils.createSubnetByIP("90.10." + i + ".6");
		}

		for (int i = start; i < end; i++) {
			PortUtils.addPort(os, "chain" + i, "90.10." + i + ".4");
			PortUtils.addPort(os, "chain" + (i + 1), "90.10." + i + ".5");
		}
		
		if(start!=1){
			PortUtils.addPort(os, "chain" + start, "90.10." + (start-1) + ".5");
		}
		
		PortUtils.addPort(os, "chain" + end, "90.10." + end + ".4");
	}
}
