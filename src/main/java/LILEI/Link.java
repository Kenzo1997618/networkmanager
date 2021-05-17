package LILEI;

public class Link {
	
	private String fromNodeName;
	private String toNodeName;
	private String time;
	private String delay;
	private String loss;
	private String bw;
	
	public String getDelay() {
		return delay;
	}
	public void setDelay(String delay) {
		float temp = Float.parseFloat(delay);
		//temp = temp/2;
		this.delay = String.valueOf(temp);
	}
	public String getLoss() {
		return loss;
	}
	public void setLoss(String loss) {
		float temp = Float.parseFloat(loss.substring(0, loss.length()-2));
		//temp /= 2;
		this.loss = String.valueOf(temp) + "%";
	}
	public String getBw() {
		return bw;
	}
	public void setBw(String bw) {
		this.bw = bw;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
		return "Link [fromNodeName=" + fromNodeName + ", toNodeName=" + toNodeName + ", time=" + time + ", delay="
				+ delay + ", loss=" + loss + ", bw=" + bw + "]";
	}

	

}
