package cn.edu.uestc.platform.pojo;

public class Link {

	private int l_id;
	private int linkType;
	private int linkStatus;
	private int channelModel;
	private int scenario_id;
	private int txPort_id;
	private int rxPort_id;
	private String fromNodeName;
	private String toNodeName;
	private String linkName;
	private double linkLength;
	private double linkNoise;
	private double linkInterference;
	private String fromNodeIP;
	private String toNodeIP;
	private int cn_id;
	private String logicalFromNodeName;
	private String logicalToNodeName;
	private int isTemplate;

	//2021.5.3
	private int linkClass;
	private int linkDelay;

	public int getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(int isTemplate) {
		this.isTemplate = isTemplate;
	}

	public String getLogicalFromNodeName() {
		return logicalFromNodeName;
	}

	public void setLogicalFromNodeName(String logicalFromNodeName) {
		this.logicalFromNodeName = logicalFromNodeName;
	}

	public String getLogicalToNodeName() {
		return logicalToNodeName;
	}

	public void setLogicalToNodeName(String logicalToNodeName) {
		this.logicalToNodeName = logicalToNodeName;
	}

	public int getCn_id() {
		return cn_id;
	}

	public void setCn_id(int cn_id) {
		this.cn_id = cn_id;
	}

	public String getFromNodeIP() {
		return fromNodeIP;
	}

	public void setFromNodeIP(String fromNodeIP) {
		this.fromNodeIP = fromNodeIP;
	}

	public String getToNodeIP() {
		return toNodeIP;
	}

	public void setToNodeIP(String toNodeIP) {
		this.toNodeIP = toNodeIP;
	}

	public int getL_id() {
		return l_id;
	}

	public void setL_id(int l_id) {
		this.l_id = l_id;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public int getLinkStatus() {
		return linkStatus;
	}

	public void setLinkStatus(int linkStatus) {
		this.linkStatus = linkStatus;
	}

	public int getChannelModel() {
		return channelModel;
	}

	public void setChannelModel(int channelModel) {
		this.channelModel = channelModel;
	}

	public int getScenario_id() {
		return scenario_id;
	}

	public void setScenario_id(int scenario_id) {
		this.scenario_id = scenario_id;
	}

	public int getTxPort_id() {
		return txPort_id;
	}

	public void setTxPort_id(int txPort_id) {
		this.txPort_id = txPort_id;
	}

	public int getRxPort_id() {
		return rxPort_id;
	}

	public void setRxPort_id(int rxPort_id) {
		this.rxPort_id = rxPort_id;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public double getLinkLength() {
		return linkLength;
	}

	public void setLinkLength(double linkLength) {
		this.linkLength = linkLength;
	}

	public double getLinkNoise() {
		return linkNoise;
	}

	public void setLinkNoise(double linkNoise) {
		this.linkNoise = linkNoise;
	}

	public double getLinkInterference() {
		return linkInterference;
	}

	public void setLinkInterference(double linkInterference) {
		this.linkInterference = linkInterference;
	}

	public String getFromNodeName() {
		return fromNodeName;
	}

	public void setFromNodeName(String fromNodeName) {
		this.fromNodeName = fromNodeName;
	}

	public String getToNodeName() {
		return toNodeName;
	}

	public void setToNodeName(String toNodeName) {
		this.toNodeName = toNodeName;
	}

	@Override
	public String toString() {
		return "Link [l_id=" + l_id + ", linkType=" + linkType + ", linkStatus=" + linkStatus + ", channelModel="
				+ channelModel + ", scenario_id=" + scenario_id + ", txPort_id=" + txPort_id + ", rxPort_id="
				+ rxPort_id + ", fromNodeName=" + fromNodeName + ", toNodeName=" + toNodeName + ", linkName=" + linkName
				+ ", linkLength=" + linkLength + ", linkNoise=" + linkNoise + ", linkInterference=" + linkInterference
				+ ", fromNodeIP=" + fromNodeIP + ", toNodeIP=" + toNodeIP + ", cn_id=" + cn_id
				+ ", logicalFromNodeName=" + logicalFromNodeName + ", logicalToNodeName=" + logicalToNodeName + "]";
	}

	public int getLinkClass() {
		return linkClass;
	}

	public void setLinkClass(int linkClass) {
		this.linkClass = linkClass;
	}

	public int getLinkDelay() {
		return linkDelay;
	}

	public void setLinkDelay(int linkDelay) {
		this.linkDelay = linkDelay;
	}

}
