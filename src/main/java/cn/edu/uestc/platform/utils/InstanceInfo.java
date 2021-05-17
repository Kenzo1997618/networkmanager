package cn.edu.uestc.platform.utils;

import java.util.HashMap;

import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV3;

import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.factory.OSClientFactory;

public class InstanceInfo {

	private static OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);
	static HashMap<String, String> hostIpMap = new HashMap<>();

	static {
		hostIpMap.put("compute3-System-Product-Name", "10.0.0.41");
		hostIpMap.put("compute4-System-Product-Name", "10.0.0.51");
		hostIpMap.put("compute5-System-Product-Name", "10.0.0.61");
		hostIpMap.put("compute7-System-Product-Name", "10.0.0.81");
		hostIpMap.put("compute8-AB350-Gaming", "10.0.0.91");
	}

	// get instance name
	public static String getInstanceName(String nodeName) {
		NodeDao nodeDao = new NodeDaoImpl();
		return os.compute().servers().get(nodeDao.getNodeByNodeName(nodeName).getUuid()).getInstanceName();
	}

	// get hypervisorName
	public static String getHypervisorIp(String nodeName) {
		System.out.println(nodeName+"````````````");
		NodeDao nodeDao = new NodeDaoImpl();
		String hostName = os.compute().servers().get(nodeDao.getNodeByNodeName(nodeName).getUuid()).getHost();
		
		return hostIpMap.get(hostName);
	}
	
	
	@Test
	public void demo1(){
		
//		System.out.println(this.getHypervisorIp(null));
	}

}
