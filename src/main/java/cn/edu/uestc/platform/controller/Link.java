package cn.edu.uestc.platform.controller;

public class Link {
	private String fromNode;
	private String endNode;
	private String status;

	public String getFromNode() {
		return fromNode;
	}

	public void setFromNode(String fromNode) {
		this.fromNode = fromNode;
	}

	public String getEndNode() {
		return endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Link [fromNode=" + fromNode + ", endNode=" + endNode + ", status=" + status + "]";
	}
	
	

}
