package cn.edu.uestc.platform.test.newTest1;

import org.junit.Test;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;

import cn.edu.uestc.platform.dynamicChange.DynamicFactory;
import cn.edu.uestc.platform.dynamicChange.DynamicNetWorkUtils;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.factory.ServerFactory;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.NetworkUtils;

public class ZPHLastTest2 {
	// last test
	@Test
	public void test3() {
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);

		// number of the docker chain
		int start = 17;
		int end = 20;
		for (int i = start; i <= end; i++) {
			ServerCreate sc = Builders.server().name("chain" + i).flavor(Constants.NOVA_FLAVOR).image(Constants.GLANCE_IMAGE)
					.availabilityZone("amd").build();

			System.out.println(sc);
			// 添加固定外网端口的IP，用与外部通信ssh进入虚拟机
			sc.addNetwork(NetworkUtils.getIdByIp("192.168.10." + (100 + i)), "192.168.10." + (100 + i));
			Server server = os.compute().servers().boot(sc);
			os.compute().servers().action(server.getId(), Action.START);
			// 为这个实例添加浮动ip
			ServerFactory.addFloatingIP(os, server);
		}

		for (int i = start; i  <=end; i++) {
			DynamicNetWorkUtils.createSubnetByIP("90.10." + i + ".6");
		}

		for (int i = start; i < end; i++) {
			
			DynamicFactory.addPort(os, "chain" + i, "90.10." + i + ".4");
			DynamicFactory.addPort(os, "chain" + (i + 1), "90.10." + i + ".5");
		}
		
		if(start!=1){
			DynamicFactory.addPort(os, "chain" + start, "90.10." + (start-1) + ".5");
		}
		
		DynamicFactory.addPort(os, "chain" + end, "90.10." + end + ".4");
	}
}
