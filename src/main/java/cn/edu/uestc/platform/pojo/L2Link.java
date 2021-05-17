package cn.edu.uestc.platform.pojo;

public class L2Link {
	@Override
	public String toString() {
		return "L2Link [l2linkId=" + l2linkId + ", scenario_id=" + scenario_id + ", linkName=" + linkName + ", brName="
				+ brName + ", vxlanName=" + vxlanName + ", vxlanId=" + vxlanId + ", fromHostIp=" + fromHostIp
				+ ", toHostIp=" + toHostIp + ", fromInstanceName=" + fromInstanceName + ", toInstanceName="
				+ toInstanceName + ", fromNodeName=" + fromNodeName + ", toNodeName=" + toNodeName + ", fromEth="
				+ fromEth + ", toEth=" + toEth + ", length=" + length + ", logicalFromNodeName=" + logicalFromNodeName
				+ ", logicalToNodeName=" + logicalToNodeName + "]";
	}

	private int l2linkId;
	private int scenario_id;

	public int getScenario_id() {
		return scenario_id;
	}

	public void setScenario_id(int scenario_id) {
		this.scenario_id = scenario_id;
	}

	private String linkName;
	private String brName;
	private String vxlanName;
	private int vxlanId;
	private String fromHostIp;
	private String toHostIp;
	private String fromInstanceName;
	private String toInstanceName;
	private String fromNodeName;
	private String toNodeName;
	private String fromEth;
	private String toEth;
	private String length;
	private String logicalFromNodeName;
	private String logicalToNodeName;

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

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getFromEth() {
		return fromEth;
	}

	public void setFromEth(String fromEth) {
		this.fromEth = fromEth;
	}

	public String getToEth() {
		return toEth;
	}

	public void setToEth(String toEth) {
		this.toEth = toEth;
	}

	public L2Link(String linkName, String brName, String vxlanName, int vxlanId, String fromHostIp, String toHostIp,
			String fromInstanceName, String toInstanceName, String fromNodeName, String toNodeName, int scenario_id,
			String length, String logicalFromNodeName, String logicalToNodeName) {

		this.linkName = linkName;
		this.brName = brName;
		this.vxlanName = vxlanName;
		this.vxlanId = vxlanId;
		this.fromHostIp = fromHostIp;
		this.toHostIp = toHostIp;
		this.fromInstanceName = fromInstanceName;
		this.toInstanceName = toInstanceName;
		this.fromNodeName = fromNodeName;
		this.toNodeName = toNodeName;
		this.scenario_id = scenario_id;
		this.length = length;
		this.logicalFromNodeName = logicalFromNodeName;
		this.logicalToNodeName = logicalToNodeName;
	}

	public L2Link() {

	}

	public int getL2linkId() {
		return l2linkId;
	}

	public void setL2linkId(int l2linkId) {
		this.l2linkId = l2linkId;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public String getVxlanName() {
		return vxlanName;
	}

	public void setVxlanName(String vxlanName) {
		this.vxlanName = vxlanName;
	}

	public int getVxlanId() {
		return vxlanId;
	}

	public void setVxlanId(int vxlanId) {
		this.vxlanId = vxlanId;
	}

	public String getFromHostIp() {
		return fromHostIp;
	}

	public void setFromHostIp(String fromHostIp) {
		this.fromHostIp = fromHostIp;
	}

	public String getToHostIp() {
		return toHostIp;
	}

	public void setToHostIp(String toHostIp) {
		this.toHostIp = toHostIp;
	}

	public String getFromInstanceName() {
		return fromInstanceName;
	}

	public void setFromInstanceName(String fromInstanceName) {
		this.fromInstanceName = fromInstanceName;
	}

	public String getToInstanceName() {
		return toInstanceName;
	}

	public void setToInstanceName(String toInstanceName) {
		this.toInstanceName = toInstanceName;
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

}
