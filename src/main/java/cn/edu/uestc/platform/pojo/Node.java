package cn.edu.uestc.platform.pojo;

import java.util.List;

public class Node {

	private int n_id;
	private String nodeName;
	private String manageIp;
	private int nodeType;
	private int hardwareArchitecture;
	private int operatingSystem;
	private int numberPort;
	private int numberInternalModule;
	private int numberInternalLink;
	private int nodeStatus;
	private String flavorType;
	private String imageName;
	private int s_id;
	private float x;
	private float y;
	private String uuid;
	private List<Port> ports;
	private String iconUrl;
	private int cn_id;
	//2021.5.2
	private String serviceStatus;
	private float computeLoad;
	private float storageStatus;
	private String subnetIP;

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public int getCn_id() {
		return cn_id;
	}

	public void setCn_id(int cn_id) {
		this.cn_id = cn_id;
	}

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getN_id() {
		return n_id;
	}

	public void setN_id(int n_id) {
		this.n_id = n_id;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getManageIp() {
		return manageIp;
	}

	public void setManageIp(String manageIp) {
		this.manageIp = manageIp;
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

	public int getNumberPort() {
		return numberPort;
	}

	public void setNumberPort(int numberPort) {
		this.numberPort = numberPort;
	}

	public int getNumberInternalModule() {
		return numberInternalModule;
	}

	public void setNumberInternalModule(int numberInternalModule) {
		this.numberInternalModule = numberInternalModule;
	}

	public int getNumberInternalLink() {
		return numberInternalLink;
	}

	public void setNumberInternalLink(int numberInternalLink) {
		this.numberInternalLink = numberInternalLink;
	}

	public int getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(int nodeStatus) {
		this.nodeStatus = nodeStatus;
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

	@Override
	public String toString() {
		return "Node [n_id=" + n_id + ", nodeName=" + nodeName + ", manageIp=" + manageIp + ", nodeType=" + nodeType
				+ ", hardwareArchitecture=" + hardwareArchitecture + ", operatingSystem=" + operatingSystem
				+ ", numberPort=" + numberPort + ", numberInternalModule=" + numberInternalModule
				+ ", numberInternalLink=" + numberInternalLink + ", nodeStatus=" + nodeStatus + ", flavorType="
				+ flavorType + ", imageName=" + imageName + ", s_id=" + s_id + ", x=" + x + ", y=" + y + ", uuid="
				+ uuid + ", ports=" + ports + ", iconUrl=" + iconUrl + ", cn_id=" + cn_id + "]";
	}


	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public float getComputeLoad() {
		return computeLoad;
	}

	public void setComputeLoad(float computeLoad) {
		this.computeLoad = computeLoad;
	}

	public float getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(float storageStatus) {
		this.storageStatus = storageStatus;
	}

	public String getSubnetIP() {
		return subnetIP;
	}

	public void setSubnetIP(String subnetIP) {
		this.subnetIP = subnetIP;
	}
}