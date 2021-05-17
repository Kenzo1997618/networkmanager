package cn.edu.uestc.platform.zwn;

import org.junit.Test;

import cn.edu.uestc.platform.controller.LinkController;
import cn.edu.uestc.platform.utils.NetworkUtils;

public class ZHTest {

	@Test
	public void fun(){
		LinkController ll = new LinkController();
		ll.createLinkMTM("test1", "10.10.10.4", "test2", "10.10.10.5");
	}
	

//	@Test
//	public void fun1(){
//		NetworkUtils nn = new NetworkUtils();
//		System.out.println(nn.getFloatIpByNodeName("test1"));
//	}
}
