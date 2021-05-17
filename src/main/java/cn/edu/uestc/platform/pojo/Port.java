package cn.edu.uestc.platform.pojo;

public class Port {

	private int pt_id;
	private int n_id;
	private String portName;
	private String portIp;
	private int portType;
	private int portStatus;
	private int isTemplate;
	private int isMultiplexing;
	private int linkCount;
	
	private String transmitterPower;
	private String transmitterFrequency;
	private String transmitterBandwidth;
	private String transmitterGain;
	private String receiverFrequency;
	private String receiverBandwidth;
	private String receiverGain;
	private String modem;
	private String maximumRate;
	//private String packetLoss;
	//2021.5.2 董维熙
	private int nodeClass;
	private float packetLoss;

	
	

	public int getIsMultiplexing() {
		return isMultiplexing;
	}

	public void setIsMultiplexing(int isMultiplexing) {
		this.isMultiplexing = isMultiplexing;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public int getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(int isTemplate) {
		this.isTemplate = isTemplate;
	}

	public int getPt_id() {
		return pt_id;
	}

	public void setPt_id(int pt_id) {
		this.pt_id = pt_id;
	}

	public int getN_id() {
		return n_id;
	}

	public void setN_id(int n_id) {
		this.n_id = n_id;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPortIp() {
		return portIp;
	}

	public void setPortIp(String portIp) {
		this.portIp = portIp;
	}

	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	public int getPortStatus() {
		return portStatus;
	}

	public void setPortStatus(int portStatus) {
		this.portStatus = portStatus;
	}

	public String getTransmitterPower() {
		return transmitterPower;
	}

	public void setTransmitterPower(String transmitterPower) {
		this.transmitterPower = transmitterPower;
	}

	public String getTransmitterFrequency() {
		return transmitterFrequency;
	}

	public void setTransmitterFrequency(String transmitterFrequency) {
		this.transmitterFrequency = transmitterFrequency;
	}

	public String getTransmitterBandwidth() {
		return transmitterBandwidth;
	}

	public void setTransmitterBandwidth(String transmitterBandwidth) {
		this.transmitterBandwidth = transmitterBandwidth;
	}

	public String getTransmitterGain() {
		return transmitterGain;
	}

	public void setTransmitterGain(String transmitterGain) {
		this.transmitterGain = transmitterGain;
	}

	public String getReceiverFrequency() {
		return receiverFrequency;
	}

	public void setReceiverFrequency(String receiverFrequency) {
		this.receiverFrequency = receiverFrequency;
	}

	public String getReceiverBandwidth() {
		return receiverBandwidth;
	}

	public void setReceiverBandwidth(String receiverBandwidth) {
		this.receiverBandwidth = receiverBandwidth;
	}

	public String getReceiverGain() {
		return receiverGain;
	}

	public void setReceiverGain(String receiverGain) {
		this.receiverGain = receiverGain;
	}

	public String getModem() {
		return modem;
	}

	public void setModem(String modem) {
		this.modem = modem;
	}

	public String getMaximumRate() {
		return maximumRate;
	}

	public void setMaximumRate(String maximumRate) {
		this.maximumRate = maximumRate;
	}

//	public String getPacketLoss() {
//		return packetLoss;
//	}
//
//	public void setPacketLoss(String packetLoss) {
//		this.packetLoss = packetLoss;
//	}

	@Override
	public String toString() {
		return "Port [pt_id=" + pt_id + ", n_id=" + n_id + ", portName=" + portName + ", portIp=" + portIp
				+ ", portType=" + portType + ", portStatus=" + portStatus + ", transmitterPower=" + transmitterPower
				+ ", transmitterFrequency=" + transmitterFrequency + ", transmitterBandwidth=" + transmitterBandwidth
				+ ", transmitterGain=" + transmitterGain + ", receiverFrequency=" + receiverFrequency
				+ ", receiverBandwidth=" + receiverBandwidth + ", receiverGain=" + receiverGain + ", modem=" + modem
				+ ", maximumRate=" + maximumRate + ", packetLoss=" + packetLoss + "]";
	}
	public int getNodeClass() {
		return nodeClass;
	}

	public void setNodeClass(int nodeClass) {
		this.nodeClass = nodeClass;
	}

	public void setPacketLoss(float packetLoss) {
		this.packetLoss = packetLoss;
	}

	public float getPacketLoss(){
		return packetLoss;
	}

}
