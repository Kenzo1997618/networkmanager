package cn.edu.uestc.platform.pojo;

import java.io.Serializable;
import java.util.HashSet;

import org.junit.Test;

public class LinkForInitialScenario extends LinkForFilter implements Serializable{

	private String fromIp;
	private String toIp;
	private String range;
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getFromIp() {
		return fromIp;
	}

	public void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}

	public String getToIp() {
		return toIp;
	}

	public void setToIp(String toIp) {
		this.toIp = toIp;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		LinkForFilter other = (LinkForFilter) obj;
		if ((this.fromNodeName.equals(other.getFromNodeName()) && this.toNodeName.equals(other.getToNodeName()))
				|| ((this.fromNodeName.equals(other.getToNodeName()))
						&& (this.toNodeName.equals(other.getFromNodeName())))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		
		return super.toString().substring(0, super.toString().length()-1)+"fromIp=" + fromIp + ", toIp=" + toIp + ", range=" + range + ", time=" + time
				+ "]";
	};
	
}
