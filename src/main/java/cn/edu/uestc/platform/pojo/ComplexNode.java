package cn.edu.uestc.platform.pojo;

import java.util.List;

//复杂节点
public class ComplexNode {
	private int cn_id;
	private String complexNodeName;
	private float x;
	private float y;
	private int s_id;
	private String iconUrl;
	private List<Node> nodes;
	private List<Link> links;

	public int getCn_id() {
		return cn_id;
	}

	public void setCn_id(int cn_id) {
		this.cn_id = cn_id;
	}

	public String getComplexNodeName() {
		return complexNodeName;
	}

	public void setComplexNodeName(String complexNodeName) {
		this.complexNodeName = complexNodeName;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	@Override
	public String toString() {
		return "ComplexNode [cn_id=" + cn_id + ", complexNodeName=" + complexNodeName + ", x=" + x + ", y=" + y
				+ ", s_id=" + s_id + ", iconUrl=" + iconUrl + ", nodes=" + nodes + ", links=" + links + "]";
	}

}
