package cn.edu.uestc.platform.dao;

import java.util.List;

import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Port;

public interface PortDao {
	
//	//public void insertPort(Port port);
//	//public List<Port> getPortList(int n_id);
//	public List<Port> getPortListBynodeName(int s_id, String nodeName);
//	public void updatePortIP(int Port_id, String IP);
//	public int[] getPortIdsOnSameLinkByPort(Port port);
//	public List<Port> getPortByLink(Link link);
//	public Port getPortByID(int pt_id);
//	public List<Port> getportListByPortIDs(int[] portID);
//	public void deletePort(Port port);
//	public Port getPortByIP(String ip);
//	public void addLinkCount(int pt_id);
//	public void subLinkCount(int pt_id);
//	//public void updateThePortStatusTo1(int pt_id);
//	public void updateThePortStatusTo0(int pt_id);
//	public void editPort(Port port);

	// 2021.5.3
	public void updateThePortStatusTo2(int pt_id);
	public void updateThePortStatusTo1(int pt_id);
	public void insertPort(Port port);
	public List<Port> getPortList(int n_id);
	

}
