package cn.edu.uestc.platform.dao;

import java.util.List;

import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;


public interface NodeDao {

	//2021.5.3
	public void insertL3Node(Node node);
	public void insertL2Node(Node node);
	public boolean updateNodeStatus(int nodeStatus,String manageIP);
	public int getNodeStatusByManageIP(String manageIP);
	public List<Node> getRouter();
	public List<Node> getSwitch();

	
	public void insertNode(Node node);
	public boolean haveNodeName(Node node);
	public List<Node> findAllNodeByScenarioId(int s_id);
	public void plusNumberSimpleNode(int s_id);
	public void plusNumberComplexNode(int s_id);
	public Node getNodeByNodeId(int n_id);
	public Node getNodeBynodeName(String nodeName, int s_id);
	public boolean isHaveIp(Node node);
	public boolean updataNode(Node node);
	public void plusNumberPort(int n_id);
	public Node getNodeByPortId(int txPort_id);
	public Node getNodeByLink(Link link);
	public void minusNumberComplexNode(int s_id);
	public void minusNumberSimpleNode(int s_id);
	public void deleteNode(int n_id);
	public void updataNodeUuid(String uuid,int n_id);
	public List<Node> getNodeListByCnid(int cn_id);
	public List<String> getAllNodeIp(int s_id);
	public List<String> getAllNodeIp();
	public void updateNodeXY(List<Node> nodes);
	public Node getNodeByNodeName(String nodeName);
	public void updateLocation(int s_id, String nodeName, int x, int y);
}
