package cn.edu.uestc.platform.pojo;

import java.io.Serializable;

public class LinkForFilter implements Serializable{

	protected String fromNodeName;
	protected String toNodeName;
	private int LinkStatus;

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

	public int getLinkStatus() {
		return LinkStatus;
	}

	public void setLinkStatus(int linkStatus) {
		LinkStatus = linkStatus;
	}

	@Override
	public String toString() {
		return "LinkForFilter [fromNodeName=" + fromNodeName + ", toNodeName=" + toNodeName + ", LinkStatus="
				+ LinkStatus + "]";
	}
	

//	@Override
//	public boolean equals(Object obj) {
//		// TODO Auto-generated method stub
//		LinkForFilter other = (LinkForFilter) obj;
//		if ((this.fromNodeName.equals(other.getFromNodeName()) && this.toNodeName.equals(other.getToNodeName()))
//				|| ((this.fromNodeName.equals(other.getToNodeName()))
//						&& (this.toNodeName.equals(other.getFromNodeName())))) {
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		LinkForFilter other = (LinkForFilter) obj;
		if ((this.fromNodeName.equals(other.getFromNodeName()) && this.toNodeName.equals(other.getToNodeName()))
				 /*||((this.fromNodeName.equals(other.getToNodeName()))
						&& (this.toNodeName.equals(other.getFromNodeName())))*/) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fromNodeName.hashCode() + toNodeName.hashCode();
		return result;
	}
}
