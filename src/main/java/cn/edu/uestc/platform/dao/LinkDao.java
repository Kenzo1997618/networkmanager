package cn.edu.uestc.platform.dao;

import java.util.List;

import cn.edu.uestc.platform.pojo.ComplexNode;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;

public interface LinkDao {

	public void insertLink(Link link);

	public Link getLink(int s_id, String linkName);

	public boolean ishaveLinkName(Link link);

	public List<Link> getLinkList(int s_id);

	public void updateLinkStatustoDown(int s_id, String linkName);

	public Link getLinkByPortID(int pt_id);

	public void updateLinkStatusUp(int s_id, String linkName);

	public List<Link> getInnerLink(int cn_id);

	public void deleteLinkOnComplexNode(ComplexNode complexNode);

	public List<Link> getLinkOnComplexNode(ComplexNode complexNode);

	public List<String> getAllLinkIP();

	public void updateLinkLength(String linkName, float linklength);

	public List<Link> getLinkListByToNodeName(String toNodeName, int s_id);

	public void updateLinkStatusto2(int s_id, String linkName);

	public void updateLinkStatusto3(int s_id, String linkName);

	public List<Link> getLinkListWhereStatue3(int s_id);
	
	public String getLinkNameByLinkForInitialScenario(LinkForInitialScenario link);
	
	public Link getLinkByFNodeNameAndToNodeName(String fromNodeName,String ToNodeName);
	
	public void editLink(Link link);
}
