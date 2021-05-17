package cn.edu.uestc.platform.dealwithstk;

import java.util.Arrays;

/*
此类仅用于将前端设置的大类规则Json字符串转换为对象 从而来设置每个节点
 */
public class NodeCreater {
	private String nodeName;
	private int nodeType;
	private int hardwareArchitecture;
	private int operatingSystem;
	private String flavorType;
	private String imageName;
	private int s_id;
	private int innerRules[];
	private String iconUrl;

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@Override
	public String toString() {
		return "NodeCreater [nodeName=" + nodeName + ", nodeType=" + nodeType + ", hardwareArchitecture="
				+ hardwareArchitecture + ", operatingSystem=" + operatingSystem + ", flavorType=" + flavorType
				+ ", imageName=" + imageName + ", s_id=" + s_id + ", innerRules=" + Arrays.toString(innerRules) + "]";
	}

	public int[] getInnerRules() {
		return innerRules;
	}

	public void setInnerRules(int[] innerRules) {
		this.innerRules = innerRules;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public int getHardwareArchitecture() {
		return hardwareArchitecture;
	}

	public void setHardwareArchitecture(int hardwareArchitecture) {
		this.hardwareArchitecture = hardwareArchitecture;
	}

	public int getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(int operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getFlavorType() {
		return flavorType;
	}

	public void setFlavorType(String flavorType) {
		this.flavorType = flavorType;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

}
