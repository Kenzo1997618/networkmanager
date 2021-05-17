package cn.edu.uestc.platform.service;

import java.sql.SQLException;
import java.util.List;

import org.openstack4j.api.OSClient.OSClientV3;

import cn.edu.uestc.platform.controller.LinkController;
import cn.edu.uestc.platform.dao.DeleteDao;
import cn.edu.uestc.platform.dao.DeleteDaoImpl;
import cn.edu.uestc.platform.dao.L2LinkDao;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.dao.PortDao;
import cn.edu.uestc.platform.dao.PortDaoImpl;
import cn.edu.uestc.platform.factory.OSClientFactory;
import cn.edu.uestc.platform.pojo.L2Link;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.Constants;
import cn.edu.uestc.platform.utils.InstanceInfo;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;
import cn.edu.uestc.platform.winter.docker.PortUtils;
import cn.edu.uestc.platform.winter.openstack.ServerUtils;

public class LinkService {
	private static OSClientV3 os = OSClientFactory.authenticate("zph", "123456", Constants.ZPH_PROJECT_ID);

	/*
	 * 创建链路 先在openstack上成功创建了链路 再将信息插入数据库。 需要更新port表的ip 需要调用沛华的创建链路函数在云平台创建链路
	 * 从link中拿到两个port的id号 txPort_id对应于fromIp,rxPort_id对应于toIp
	 */
	public boolean createLink(Link link) {
		LinkDao linkDao = new LinkDaoImpl();
		// 当使用||时，||前面的条件如果已经为真的话，之后的条件则不会进行判断,所以不用担心之后的link会空指针异常
		if (linkDao.ishaveLinkName(link) == false || link.getLinkStatus() == 1) {
			if (link.getLinkStatus() == 0) {
				// 更新port表的ip地址,以及链路状态
				PortDao portDao = new PortDaoImpl();
				Port port1 = portDao.getPortByID(link.getTxPort_id());
				Port port2 = portDao.getPortByID(link.getRxPort_id());

				portDao.addLinkCount(port1.getPt_id());
				portDao.addLinkCount(port2.getPt_id());
				portDao.updateThePortStatusTo1(port1.getPt_id());
				portDao.updateThePortStatusTo1(port2.getPt_id());
				link.setFromNodeIP(port1.getPortIp());
				link.setToNodeIP(port2.getPortIp());
				linkDao.insertLink(link);
			}
			// 在云平台创建一条链路,通过port来获取到fromNodeName和toNodeName
			LinkController controller = new LinkController();
			controller.createLinkMTM(link.getFromNodeName(), link.getFromNodeIP(), link.getToNodeName(),
					link.getToNodeIP());
			return true;

		}
		System.out.println("此场景下的链路名已经存在！");
		return false;
	}

	/*
	 * 获得当前场景下的所有链路列表
	 */
	public List<Link> getLinkList(int s_id) {
		// TODO Auto-generated method stub
		LinkDao dao = new LinkDaoImpl();
		return dao.getLinkList(s_id);
	}

	/*
	 * 删除链路，删除链路函数用于删除链路状态为0的链路，挂起链路 状态为0的链路
	 * 一切删除，状态为1的链路调用此函数仅用于删除Openstack上的链路而不操作数据库。
	 */
	public boolean deleteLink(int s_id, String linkName) {

		// 先根据s_id和linkName查找到对应的link
		LinkDao linkDao = new LinkDaoImpl();
		Link link = linkDao.getLink(s_id, linkName);

		System.out.println("此链路为:" + link);
		// 根据link拿到link两端的端口IP，以及端口所属的节点。
		PortDao portDao = new PortDaoImpl();
		List<Port> ports = portDao.getPortByLink(link);
		System.out.println("端口号为:" + ports);

		// 根据portId拿到节点
		NodeDao nodeDao = new NodeDaoImpl();
		Node fromNode = nodeDao.getNodeByPortId(ports.get(0).getPt_id());
		Node toNode = nodeDao.getNodeByPortId(ports.get(1).getPt_id());
		// 拿到link后，调用deleteDao 删除link,只有非挂起状态的link才能删除数据库
		// 删除底层云平台上的链路
		// 删除链路需要链路两端的节点名和两端的端口IP。
		LinkController controller = new LinkController();
		// if the link is one to one;
		if ((ports.get(0).getIsMultiplexing() == 0 && ports.get(1).getIsMultiplexing() == 0)) {
			System.out.println("delete the link is one to one");
			controller.delLinkMTM(fromNode.getNodeName(), ports.get(0).getPortIp(), toNode.getNodeName(),
					ports.get(1).getPortIp());

			portDao.updateThePortStatusTo0(ports.get(0).getPt_id());
			portDao.updateThePortStatusTo0(ports.get(1).getPt_id());

		} else if (ports.get(0).getLinkCount() == 1 && ports.get(1).getLinkCount() == 1) {
			System.out.println("delete the link is mulituy to one last link");
			controller.delLinkMTM(fromNode.getNodeName(), ports.get(0).getPortIp(), toNode.getNodeName(),
					ports.get(1).getPortIp());
			portDao.updateThePortStatusTo0(ports.get(0).getPt_id());
			portDao.updateThePortStatusTo0(ports.get(1).getPt_id());
		} else {
			if (ports.get(0).getLinkCount() == 1) {
				PortUtils.delPort(os, fromNode.getNodeName(), ports.get(0).getPortIp());
				portDao.updateThePortStatusTo0(ports.get(0).getPt_id());
			} else {
				PortUtils.delPort(os, toNode.getNodeName(), ports.get(1).getPortIp());
				portDao.updateThePortStatusTo0(ports.get(1).getPt_id());
			}
		}
		if (link.getLinkStatus() == 0) {
			DeleteDao deleteDao = new DeleteDaoImpl();
			deleteDao.deleteLink(link);
		}
		portDao.subLinkCount(ports.get(0).getPt_id());
		portDao.subLinkCount(ports.get(1).getPt_id());
		return true;
	}

	public void pauseLink(int s_id, String linkName) {
		// 将link设置为挂起态
		LinkDao dao = new LinkDaoImpl();
		dao.updateLinkStatustoDown(s_id, linkName);
		// 删除openstack上的链路
		this.deleteLink(s_id, linkName);
		System.out.println("挂起链路成功！");
	}

	/*
	 * 恢复链路
	 */
	public void recoveryLink(int s_id, String linkName) {
		LinkDao linkDao = new LinkDaoImpl();
		PortDao portDao = new PortDaoImpl();
		Link link = linkDao.getLink(s_id, linkName);
		// 先创建openstack上的链路
		this.createLink(link);
		// 修改数据库中链路的状态
		linkDao.updateLinkStatusUp(s_id, linkName);
	}

	public List<Link> getInnerLink(int cn_id) {
		// TODO Auto-generated method stub
		LinkDao linkDao = new LinkDaoImpl();
		return linkDao.getInnerLink(cn_id);
	}

	public void editLink(Link link) {
		LinkDao linkDao = new LinkDaoImpl();
		linkDao.editLink(link);
	}

	public boolean createL2Link(Link link, int onlyPort) throws Exception {
		if (!L2LinkDao.isHaveLink(link)) {
			String fromHostIP = InstanceInfo.getHypervisorIp(link.getFromNodeName());
			String toHostIP = InstanceInfo.getHypervisorIp(link.getToNodeName());

			String fromInstanceName = InstanceInfo.getInstanceName(link.getFromNodeName());
			String toInstanceName = InstanceInfo.getInstanceName(link.getToNodeName());

			int count = L2LinkDao.getL2LinkCount();

			// generate L2link Object
			L2Link l2link = new L2Link(link.getLinkName(), "br" + count, "vxlan" + count, count, fromHostIP, toHostIP,
					fromInstanceName, toInstanceName, link.getFromNodeName(), link.getToNodeName(),
					link.getScenario_id(), Double.toString(link.getLinkLength()), link.getLogicalFromNodeName(),
					link.getLogicalToNodeName());

			// at the same Host
			if (fromHostIP.equals(toHostIP)) {
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromHostIP);
				try { // add eth to instance and add bridge
					sshExecutor.exec("brctl addbr " + l2link.getBrName());
					sshExecutor.exec("ifconfig " + l2link.getBrName() + " up");
					sshExecutor.exec("ifconfig " + l2link.getBrName() + " promisc");

					sshExecutor.exec("virsh attach-interface " + l2link.getFromInstanceName()
							+ " --type bridge --source " + l2link.getBrName() + " --model virtio");
					// need to get the new eth number
					String fromNodeInterfaceName = NetworkUtils.getInterfaceName(link.getFromNodeName());
					l2link.setFromEth(fromNodeInterfaceName);

					sshExecutor.exec("virsh attach-interface " + l2link.getToInstanceName() + " --type bridge --source "
							+ l2link.getBrName() + " --model virtio");
					// need to get the new eth number
					String toNodeInterfaceName = NetworkUtils.getInterfaceName(link.getToNodeName());
					l2link.setToEth(toNodeInterfaceName);

					sshExecutor.exec("echo -e \"\n\" >>/etc/network/interfaces");
					sshExecutor.exec("echo \"auto " + l2link.getBrName() + "\" >>/etc/network/interfaces");
					sshExecutor
							.exec("echo \"iface " + l2link.getBrName() + " inet dhcp" + "\" >>/etc/network/interfaces");
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("added eth to instance and added bridge");
			} else { // at the different host
				// ssh to from host
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromHostIP);
				try {
					sshExecutor.exec("brctl addbr " + l2link.getBrName());
					sshExecutor.exec("ifconfig " + l2link.getBrName() + " up");
					sshExecutor.exec("ifconfig " + l2link.getBrName() + " promisc");
					sshExecutor.exec("ip link add " + l2link.getVxlanName() + " type vxlan id " + l2link.getVxlanId()
							+ " remote " + l2link.getToHostIp() + " dev eth2");
					sshExecutor.exec("ip link set " + l2link.getVxlanName() + " up");
					sshExecutor.exec("brctl addif " + l2link.getBrName() + " " + l2link.getVxlanName());
					sshExecutor.exec("virsh attach-interface " + l2link.getFromInstanceName()
							+ " --type bridge --source " + l2link.getBrName() + " --model virtio");
					// need to get the new eth number
					String fromNodeInterfaceName = NetworkUtils.getInterfaceName(link.getFromNodeName());
					l2link.setFromEth(fromNodeInterfaceName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("done with from host");
				// ssh to to host
				SSHExecutorUtils sshExecutor1 = new SSHExecutorUtils("root", "123456", toHostIP);
				try {
					sshExecutor1.exec("brctl addbr " + l2link.getBrName());
					sshExecutor1.exec("ifconfig " + l2link.getBrName() + " up");
					sshExecutor1.exec("ifconfig " + l2link.getBrName() + " promisc");
					sshExecutor1.exec("ip link add " + l2link.getVxlanName() + " type vxlan id " + l2link.getVxlanId()
							+ " remote " + l2link.getFromHostIp() + " dev eth2");
					sshExecutor1.exec("ip link set " + l2link.getVxlanName() + " up");
					sshExecutor1.exec("brctl addif " + l2link.getBrName() + " " + l2link.getVxlanName());
					sshExecutor1.exec("virsh attach-interface " + l2link.getToInstanceName()
							+ " --type bridge --source " + l2link.getBrName() + " --model virtio");
					// need to get the new eth number
					String toNodeInterfaceName = NetworkUtils.getInterfaceName(link.getToNodeName());
					l2link.setToEth(toNodeInterfaceName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("done with to host");
			}
			if (link.getLinkType() == 5 && onlyPort == 0) { // l2 to l2, don't
															// need to set ip
				String fromIp = NetworkUtils.getFloatIpByNodeName(link.getFromNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromIp);
				sshExecutor.exec("brctl addbr br" + link.getTxPort_id());
				sshExecutor.exec("ifconfig br" + link.getTxPort_id() + " up");
				sshExecutor.exec("ifconfig br" + link.getTxPort_id() + " promisc");
				sshExecutor.exec("brctl addif br" + link.getTxPort_id() + " " + l2link.getFromEth());

				String toIp = NetworkUtils.getFloatIpByNodeName(link.getToNodeName());
				sshExecutor = new SSHExecutorUtils("root", "123456", toIp);
				sshExecutor.exec("brctl addbr br" + link.getRxPort_id());
				sshExecutor.exec("ifconfig br" + link.getRxPort_id() + " up");
				sshExecutor.exec("ifconfig br" + link.getRxPort_id() + " promisc");
				sshExecutor.exec("brctl addif br" + link.getRxPort_id() + " " + l2link.getToEth());
			}
			if (link.getLinkType() == 5 && onlyPort == 2) {
				String toIp = NetworkUtils.getFloatIpByNodeName(link.getToNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", toIp);
				sshExecutor.exec("brctl addbr br" + link.getRxPort_id());
				sshExecutor.exec("ifconfig br" + link.getRxPort_id() + " up");
				sshExecutor.exec("ifconfig br" + link.getRxPort_id() + " promisc");
				sshExecutor.exec("brctl addif br" + link.getRxPort_id() + " " + l2link.getToEth());
			}
			if (link.getLinkType() == 5 && onlyPort == 3) {
				String fromIp = NetworkUtils.getFloatIpByNodeName(link.getFromNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromIp);
				sshExecutor.exec("brctl addbr br" + link.getTxPort_id());
				sshExecutor.exec("ifconfig br" + link.getTxPort_id() + " up");
				sshExecutor.exec("ifconfig br" + link.getTxPort_id() + " promisc");
				sshExecutor.exec("brctl addif br" + link.getTxPort_id() + " " + l2link.getFromEth());
			}
			if (link.getLinkType() == 6 && onlyPort == 0) { // l2 to l3, l3 need
															// to set ip
				String fromIp = NetworkUtils.getFloatIpByNodeName(link.getFromNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromIp);
				sshExecutor.exec("brctl addbr br" + link.getTxPort_id());
				sshExecutor.exec("ifconfig br" + link.getTxPort_id() + " up");
				sshExecutor.exec("ifconfig br" + link.getTxPort_id() + " promisc");
				sshExecutor.exec("brctl addif br" + link.getTxPort_id() + " " + l2link.getFromEth());

				String toIp = NetworkUtils.getFloatIpByNodeName(link.getToNodeName());
				sshExecutor = new SSHExecutorUtils("root", "123456", toIp);
				sshExecutor.exec("echo -e \"\n\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"auto " + l2link.getToEth() + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"iface " + l2link.getToEth() + " inet static" + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"address " + link.getToNodeIP() + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"netmask 255.255.255.0\" >>/etc/network/interfaces");
				sshExecutor.exec("reboot");

			}
			if (link.getLinkType() == 6 && onlyPort == 2) {
				String toIp = NetworkUtils.getFloatIpByNodeName(link.getToNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", toIp);
				sshExecutor.exec("echo -e \"\n\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"auto " + l2link.getToEth() + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"iface " + l2link.getToEth() + " inet static" + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"address " + link.getToNodeIP() + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"netmask 255.255.255.0\" >>/etc/network/interfaces");
				sshExecutor.exec("reboot");
			}
			if (link.getLinkType() == 7 && onlyPort == 0) { // l3 to l2, l3 need
															// to set ip
				String fromIp = NetworkUtils.getFloatIpByNodeName(link.getFromNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromIp);
				sshExecutor.exec("echo -e \"\n\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"auto " + l2link.getFromEth() + "\" >>/etc/network/interfaces");
				sshExecutor
						.exec("echo \"iface " + l2link.getFromEth() + " inet static" + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"address " + link.getFromNodeIP() + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"netmask 255.255.255.0\" >>/etc/network/interfaces");
				sshExecutor.exec("reboot");

				String toIp = NetworkUtils.getFloatIpByNodeName(link.getToNodeName());
				sshExecutor = new SSHExecutorUtils("root", "123456", toIp);
				sshExecutor.exec("brctl addbr br" + link.getRxPort_id());
				sshExecutor.exec("ifconfig br" + link.getRxPort_id() + " up");
				sshExecutor.exec("ifconfig br" + link.getRxPort_id() + " promisc");
				sshExecutor.exec("brctl addif br" + link.getRxPort_id() + " " + l2link.getToEth());
			}
			if (link.getLinkType() == 7 && onlyPort == 3) {
				String fromIp = NetworkUtils.getFloatIpByNodeName(link.getFromNodeName());
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromIp);
				sshExecutor.exec("echo -e \"\n\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"auto " + l2link.getFromEth() + "\" >>/etc/network/interfaces");
				sshExecutor
						.exec("echo \"iface " + l2link.getFromEth() + " inet static" + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"address " + link.getFromNodeIP() + "\" >>/etc/network/interfaces");
				sshExecutor.exec("echo \"netmask 255.255.255.0\" >>/etc/network/interfaces");
				sshExecutor.exec("reboot");
			}
			L2LinkDao.insertL2Link(l2link); // write into DB

			return true;
		} else {
			System.out.println("link exist");
			return false;
		}
	}

	public boolean createDockerL2Link(Link link, int onlyPort) throws Exception {
		if (!L2LinkDao.isHaveLink(link)) {
			String fromHostIP = InstanceInfo.getHypervisorIp(link.getFromNodeName());
			String toHostIP = InstanceInfo.getHypervisorIp(link.getToNodeName());
			String fromInstanceName = "nova-" + ServerUtils.getServer(link.getFromNodeName()).getId();
			String toInstanceName = "nova-" + ServerUtils.getServer(link.getToNodeName()).getId();
			System.out.println(fromInstanceName);
			System.out.println(toInstanceName);
			int count = L2LinkDao.getL2LinkCount();
			// generate L2link Object
			L2Link l2link = new L2Link(link.getLinkName(), "br" + count, "vxlan" + count, count, fromHostIP, toHostIP,
					fromInstanceName, toInstanceName, link.getFromNodeName(), link.getToNodeName(),
					link.getScenario_id(), Double.toString(link.getLinkLength()), link.getLogicalFromNodeName(),
					link.getLogicalToNodeName());

			l2link.setFromEth("eth" + count);
			l2link.setToEth("eth" + count);

			if (fromHostIP.equals(toHostIP)) { // at the same Host
				SSHExecutorUtils sshExecutor = new SSHExecutorUtils("root", "123456", fromHostIP);
				try { // add eth to instance and add bridge
					sshExecutor.exec("brctl addbr " + l2link.getBrName());
					sshExecutor.exec("ifconfig " + l2link.getBrName() + " up");
					sshExecutor.exec("ifconfig " + l2link.getBrName() + " promisc");
					System.out.println("=====added bridge in dockerHost=====");

					String pid = sshExecutor.exec("docker inspect -f '{{.State.Pid}}' " + fromInstanceName);
					pid = pid.trim();
					System.out.println(pid);
					sshExecutor.exec("ip link add veth" + count + " type veth peer name vethx" + count);
					sshExecutor.exec("brctl addif " + l2link.getBrName() + " veth" + count);
					sshExecutor.exec("ip link set veth" + count + " up");
					String cid = sshExecutor.exec("docker inspect " + fromInstanceName + " | grep Id");
					cid = cid.substring(15, cid.length() - 3);
					System.out.println(cid);
					sshExecutor.exec("ln -s /proc/" + pid + "/ns/net /var/run/netns/" + cid);
					sshExecutor.exec("ip link set dev vethx" + count + " name eth" + count + " netns " + pid);
					sshExecutor.exec("ip netns exec " + pid + " ip link set eth" + count + " up");

					String pid1 = sshExecutor.exec("docker inspect -f '{{.State.Pid}}' " + toInstanceName);
					pid1 = pid1.trim();
					System.out.println(pid1);
					sshExecutor.exec("ip link add teth" + count + " type veth peer name tethx" + count);
					sshExecutor.exec("brctl addif " + l2link.getBrName() + " teth" + count);
					sshExecutor.exec("ip link set teth" + count + " up");
					String cid1 = sshExecutor.exec("docker inspect " + toInstanceName + " | grep Id");
					cid1 = cid1.substring(15, cid1.length() - 3);
					System.out.println(cid1);
					sshExecutor.exec("ln -s /proc/" + pid1 + "/ns/net /var/run/netns/" + cid1);
					sshExecutor.exec("ip link set dev tethx" + count + " name eth" + count + " netns " + pid1);
					sshExecutor.exec("ip netns exec " + pid1 + " ip link set eth" + count + " up");
					if (link.getLinkType() == 11) { // l2 docker to l2 docker
						sshExecutor
								.exec("docker exec " + fromInstanceName + " ifconfig " + l2link.getFromEth() + " up");
						sshExecutor.exec(
								"docker exec " + fromInstanceName + " ifconfig " + l2link.getFromEth() + " promisc");
						sshExecutor.exec("docker exec " + toInstanceName + " ifconfig " + l2link.getToEth() + " up");
						sshExecutor
								.exec("docker exec " + toInstanceName + " ifconfig " + l2link.getToEth() + " promisc");
					} else if (link.getLinkType() == 12) { // l2 to l3 docker
						sshExecutor
								.exec("docker exec " + fromInstanceName + " ifconfig " + l2link.getFromEth() + " up");
						sshExecutor.exec(
								"docker exec " + fromInstanceName + " ifconfig " + l2link.getFromEth() + " promisc");
						// l3 set ip

					} else if (link.getLinkType() == 13) { // l3 to l2 docker
						// l3 set ip

						sshExecutor.exec("docker exec " + toInstanceName + " ifconfig " + l2link.getToEth() + " up");
						sshExecutor
								.exec("docker exec " + toInstanceName + " ifconfig " + l2link.getToEth() + " promisc");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("=====added eth to docker=====");
			} else { // not the same host

			}
			L2LinkDao.insertL2Link(l2link); // write into DB
			return true;
		} else {
			System.out.println("link exist");
			return false;
		}
	}

}
