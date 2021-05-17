package cn.edu.uestc.platform.dynamicChange;

import java.util.concurrent.TimeUnit;

import org.openstack4j.api.OSClient.OSClientV3;

import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.dao.PortDao;
import cn.edu.uestc.platform.dao.PortDaoImpl;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.Caculator;
import cn.edu.uestc.platform.utils.NetworkUtils;

public class DynamicFactory {
	/**
	 * 为一个节点动态添加端口,同时进入节点修改配置文件(zebra的配置文件)
	 */
	public static void addPort(OSClientV3 os, String nodeName, String portIP) {
		NodeDao nodeDao = new NodeDaoImpl();
		// 如果名为nodeName的虚拟机有 portIP这个ip地址，则不对它进行任何操作
		if (!NetworkUtils.isHaveIP(os, nodeName, portIP)) {
			// 将这个ip添加到节点上
			NetworkUtils.addIPToNode(nodeName, portIP);
			// 进入节点修改配置文件
			String floatIP = NetworkUtils.getFloatIpByNodeName(nodeName);// 通过虚拟机的名称得到浮动ip的地址
			System.out.println("虚拟机 " + nodeName + " 的浮动ip地址为： " + floatIP);
			// NetworkUtils.changeNetworkConfig(floatIP, portIP);

			// Node node = nodeDao.getNodeByNodeName(nodeName);
			PortDao portDao = new PortDaoImpl();
			Port port = portDao.getPortByIP(portIP);

			// Port port = portDao.getPortListBynodeName(node.getS_id(),
			// node.getNodeName());
			LinkDao linkDao = new LinkDaoImpl();
			Link link = linkDao.getLinkByPortID(port.getPt_id());
			System.out.println("********************************");
			System.out.println(port);
			System.out.println(link);

			// try {
			// System.out.println(Double.parseDouble(port.getTransmitterFrequency()));
			// TimeUnit.SECONDS.sleep(1);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// if (port.getPortType() == 1) {
//			String[] dbarr = Caculator.method(Double.parseDouble(port.getTransmitterFrequency()),
//					Double.parseDouble(port.getTransmitterBandwidth()), Double.parseDouble(port.getReceiverFrequency()),
//					Double.parseDouble(port.getReceiverBandwidth()), link.getLinkLength(),
//					Double.parseDouble(port.getTransmitterPower()), Double.parseDouble(port.getTransmitterGain()),
//					Double.parseDouble(port.getReceiverGain()), link.getLinkNoise(), Integer.parseInt(port.getModem()));
			// System.out.println("jijiang exe luyouxieyi");
			// System.out.println(dbarr[0]+"----"+dbarr[1]+"---------"+
			// dbarr[2]);
			// DynamicNetWorkUtils.changeAddQuguaConf(floatIP, portIP, nodeName,
			// dbarr[0], dbarr[1], dbarr[2]);
			// } else {
			DynamicNetWorkUtils.changeAddQuguaConf(floatIP, portIP, nodeName, port.getMaximumRate(),
					port.getPacketLoss(), new Double(link.getLinkLength() / (3.0E+02)).toString());
		}
		// String[] dbarr = {"10","10","0.01"};
		// 通过浮动ip进入虚拟机修改配置文件
	}

	/**
	 * 为一个节点动态删除端口，同时进入节点修改配置文件(zebra)=====>TODO
	 */
	public static void delPort(OSClientV3 os, String nodeName, String portIP) {
		// 如果名为nodeName的虚拟机有 portIP这个ip地址，则对它进行删除操作
		if (NetworkUtils.isHaveIP(os, nodeName, portIP)) {
			System.out.println("准备删除了！");
			// 进入节点修改配置文件
			String floatIP = NetworkUtils.getFloatIpByNodeName(nodeName);// 通过虚拟机的名称得到浮动ip的地址
			System.out.println("虚拟机 " + nodeName + " 的浮动ip地址为： " + floatIP);
			// 通过浮动ip进入虚拟机修改配置文件
			DynamicNetWorkUtils.changeDelQuguaConf(floatIP, portIP);
			// 将这个节点 删除这个portIP
			DynamicNetWorkUtils.delIPToNode(nodeName, portIP);// 这块还没有测试------------**************************一定要先测试了这个方法！！！！！
		}
	}
}
