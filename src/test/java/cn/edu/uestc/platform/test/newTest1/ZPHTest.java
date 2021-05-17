package cn.edu.uestc.platform.test.newTest1;

import org.junit.Test;

import cn.edu.uestc.platform.controller.LinkController;
import cn.edu.uestc.platform.controller.NodeController;

// jie mian bu neng yong le, yong zhe ge chuang jian jie dian he lian lu
public class ZPHTest {

	@Test
	public void createNode() {
		NodeController node = new NodeController();
		node.createNode("pc1", "192.168.10.5", "docker");
	}

	@Test
	public void createLink() {
		LinkController link = new LinkController();
		link.createLinkMTM("node2", "10.61.20.4", "node3", "10.61.20.5");
	}
}
