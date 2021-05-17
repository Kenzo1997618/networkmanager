package cn.edu.uestc.platform.testzk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import cn.edu.uestc.platform.dealwithstk.STKAnalyse;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.Caculator;
import cn.edu.uestc.platform.utils.CloneUtil;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;

public class TestMap {

	public static void main(String[] args) throws IOException {
		int s_id = 57;
		HashMap<String, Set<LinkForInitialScenario>> map = new HashMap();
		STKAnalyse stkAnalyse = new STKAnalyse();
		map.put("0", stkAnalyse.getLinkForInitialScenario(s_id, "0"));
		map.put("1", stkAnalyse.getLinkForInitialScenario(s_id, "1"));
		map.put("2", stkAnalyse.getLinkForInitialScenario(s_id, "2"));
		map.put("3", stkAnalyse.getLinkForInitialScenario(s_id, "3"));
		map.put("4", stkAnalyse.getLinkForInitialScenario(s_id, "4"));
		// HashMap<String, Set<LinkForInitialScenario>> copymap =
		// (HashMap<String, Set<LinkForInitialScenario>>) map.clone();
		//
		HashMap<String, Set<LinkForInitialScenario>> copymap = CloneUtil.clone(map);
		System.out.println(map.get("0").size());
		System.out.println(copymap.get("0").size());
		map.get("0").clear();
		System.out.println(map.get("0").size());
		System.out.println(copymap.get("0").size());

	}

	@Test
	public void demo2() throws Exception {
		SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", "10.0.0.41");
		String s = sshExecutor.exec("brctl show br0");
//		System.out.println(s.substring(s.indexOf("vnet"), 4));
		String s1 = s.substring(s.indexOf("vnet"),s.indexOf("vnet")+7);
		String s2 = s.substring(s.lastIndexOf("vnet"));
//		String interfaces[] = s.trim().split(" ");
		System.out.println(s1.trim());
		System.out.println(s2);
		
//		System.out.println(interfaces[0].trim());
//		System.out.println(interfaces[1].trim());
		
		

	}
}
