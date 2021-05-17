package cn.edu.uestc.platform.dealwithstk;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

public class NodeNumFilter {

	// GEO的节点个数
	public static int getGEONum(int s_id) throws IOException {
		Set<String> allNodes = new STKAnalyse().getNodesName(s_id);
		int i = 0;
		for (String node : allNodes) {
			if (node.contains("GEO")) {
				i++;
			}
		}
		return i;
	}

	// Facility的节点个数
	public static int getFacilityNum(int s_id) throws IOException {
		Set<String> allNodes = new STKAnalyse().getNodesName(s_id);
		int i = 0;
		for (String node : allNodes) {
			if (node.contains("Facility")) {
				i++;
			}
		}
		return i;
	}

	// GroundVehicle1的节点个数
	public static int getGroundVehicleNum(int s_id) throws IOException {
		Set<String> allNodes = new STKAnalyse().getNodesName(s_id);
		int i = 0;
		for (String node : allNodes) {
			if (node.contains("GroundVehicle")) {
				i++;
			}
		}
		return i;
	}

	// LEO的节点个数
	public static int getLEONum(int s_id) throws IOException {
		Set<String> allNodes = new STKAnalyse().getNodesName(s_id);
		int i = 0;
		for (String node : allNodes) {
			if (node.contains("LEO")) {
				i++;
			}
		}
		return i;
	}

	// 获取LEO行数
	public static int getLEORow(int s_id) throws IOException {
		Set<String> allNodes = new STKAnalyse().getNodesName(s_id);
		// 用来存最大行数
		int max = 0;
		for (String node : allNodes) {
			if (node.contains("LEO")) {
				if (Integer.parseInt(node.substring(4, 5)) > max) {
					max = Integer.parseInt(node.substring(4, 5));
				}
			}
		}
		return max;
	}

	// 获取LEO列数
	public static int getLEOColumn(int s_id) throws IOException {
		Set<String> allNodes = new STKAnalyse().getNodesName(s_id);
		int max = 0;
		for (String node : allNodes) {
			if (node.contains("LEO")) {
				if (Integer.parseInt(node.substring(5, 6)) > max) {
					max = Integer.parseInt(node.substring(5, 6));
				}
			}
		}

		return max;
	}


}
