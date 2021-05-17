package cn.edu.uestc.platform.test.newTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.State;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;

import cn.edu.uestc.platform.dynamicChange.DynamicFactory;
import cn.edu.uestc.platform.dynamicChange.Filters;
import cn.edu.uestc.platform.dynamicChange.NewLink;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.factory.ServerFactory;
import cn.edu.uestc.platform.utils.Constants;

public class NewTest {
	
	public static void main(String[] args) {
		
		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
		List<? extends Port> ports = os.networking().port().list();
		for(Port port :ports){
			 Set<? extends IP> ips = port.getFixedIps();
			 for(IP ip:ips){
				 if(!ip.getIpAddress().contains("192.168")){
					 os.networking().port().delete(port.getId());
					 break;
				 }
			 }
		}
//		
////		System.out.println(os.compute().images().list());
//		
////		Network network = os.networking().network().get("9aa9accd-d7a7-4121-8262-532e7a38f042");
////		System.out.println(network);
////		List<? extends Subnet> subnets = os.networking().subnet().list();
////		System.out.println(subnets);
////		
////		List<? extends Port> ports = os.networking().port().list();
////		System.out.println(ports);
////		
////		os.networking().port().delete("a6b9eb67-d692-4537-badd-5154d21e3a7c");//删除一个端口
//		
////		Port port = os.networking().port().create(Builders.port()
////	              .networkId("9aa9accd-d7a7-4121-8262-532e7a38f042").tenantId("f766e0582674439fbd935cedfa404947")
////	              .fixedIp("10.10.10.12", "fcdb8e60-6556-435c-95b1-b4b7f44ec55e").deviceId("f9896c52-11fa-4698-9b7b-15769060f406")
////	              .deviceOwner("compute:nova").state(State.ACTIVE)
////	              .build());
//		
////		List<? extends Server> servers = os.compute().servers().list();
////		Server server = os.compute().servers().get("f9896c52-11fa-4698-9b7b-15769060f406");
////		System.out.println(server);
//		
////		TreeSet<NewLink> links = Filters.readMinuteMEOToGEO("1");
////		System.out.println(links);
////		OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
////			ServerFactory.createServer(os, "VM1", "192.168.5.10");
////
////		}
////		DynamicFactory.addPort(OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID), "vm1", "10.10.12.3");
	}
}
