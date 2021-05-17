package cn.edu.uestc.platform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import cn.edu.uestc.platform.pojo.L2Link;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.PreparedStatement;

import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.utils.DBUtiles;

public class L2LinkDao {

	public static int getL2LinkCount() throws SQLException {
		Connection conn = DBUtiles.getConnection();
		String sql = "select count(*) from l2link";
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		rs = stmt.executeQuery(sql);
		int count = 0;
		while (rs.next()) {
			count = rs.getInt(1);
		}
		return count;
	}

	public static boolean isHaveLink(Link link) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select *from l2link where LinkName=?";
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, link.getLinkName());
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return false;
	}

	public static void insertL2Link(L2Link l2link) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "insert into l2link(LinkName,brName,vxlanName,vxlanId,fromHostIp,toHostIp,"
				+ "fromInstanceName,toInstanceName,fromNodeName,toNodeName,fromEth,toEth,scenario_id,length,logicalFromNodeName,logicalToNodeName) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, l2link.getLinkName());
			ps.setString(2, l2link.getBrName());
			ps.setString(3, l2link.getVxlanName());
			ps.setInt(4, l2link.getVxlanId());
			ps.setString(5, l2link.getFromHostIp());
			ps.setString(6, l2link.getToHostIp());
			ps.setString(7, l2link.getFromInstanceName());
			ps.setString(8, l2link.getToInstanceName());
			ps.setString(9, l2link.getFromNodeName());
			ps.setString(10, l2link.getToNodeName());
			ps.setString(11, l2link.getFromEth());
			ps.setString(12, l2link.getToEth());
			ps.setInt(13, l2link.getScenario_id());
			ps.setString(14, l2link.getLength());
			ps.setString(15, l2link.getLogicalFromNodeName());
			ps.setString(16, l2link.getLogicalToNodeName());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<L2Link> getL2LinkList(int s_id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select *from l2link where scenario_id=?";
		List<L2Link> links = new LinkedList<>();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				L2Link l2link = new L2Link();
				l2link.setL2linkId(rs.getInt(1));
				l2link.setLinkName(rs.getString(2));
				l2link.setBrName(rs.getString(3));
				l2link.setVxlanName(rs.getString(4));
				l2link.setVxlanId(rs.getInt(5));
				l2link.setFromHostIp(rs.getString(6));
				l2link.setToHostIp(rs.getString(7));
				l2link.setFromInstanceName(rs.getString(8));
				l2link.setToInstanceName(rs.getString(9));
				l2link.setFromNodeName(rs.getString(10));
				l2link.setToNodeName(rs.getString(11));
				l2link.setFromEth(rs.getString(12));
				l2link.setToEth(rs.getString(13));
				l2link.setScenario_id(s_id);
				l2link.setLogicalFromNodeName(rs.getString(16));
				l2link.setLogicalToNodeName(rs.getString(17));
				links.add(l2link);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(links);
		return links;
	}

	public static List<L2Link> getL2NodePortList(int s_id, String nodeName) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select *from l2link where scenario_id=? and (fromNodeName=? or toNodeName = ?)";
		List<L2Link> links = new LinkedList<>();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.setString(2, nodeName);
			ps.setString(3, nodeName);
			rs = ps.executeQuery();
			while (rs.next()) {
				L2Link l2link = new L2Link();
				l2link.setL2linkId(rs.getInt(1));
				l2link.setLinkName(rs.getString(2));
				l2link.setBrName(rs.getString(3));
				l2link.setVxlanName(rs.getString(4));
				l2link.setVxlanId(rs.getInt(5));
				l2link.setFromHostIp(rs.getString(6));
				l2link.setToHostIp(rs.getString(7));
				l2link.setFromInstanceName(rs.getString(8));
				l2link.setToInstanceName(rs.getString(9));
				l2link.setFromNodeName(rs.getString(10));
				l2link.setToNodeName(rs.getString(11));
				l2link.setFromEth(rs.getString(12));
				l2link.setToEth(rs.getString(13));
				l2link.setScenario_id(s_id);
				links.add(l2link);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return links;
	}

	public static L2Link getL2Link(int s_id, String linkName) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from l2link where linkName=?";
		L2Link l2link = new L2Link();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, linkName);
			rs = ps.executeQuery();
			while (rs.next()) {
				l2link.setL2linkId(rs.getInt(1));
				l2link.setLinkName(rs.getString(2));
				l2link.setBrName(rs.getString(3));
				l2link.setVxlanName(rs.getString(4));
				l2link.setVxlanId(rs.getInt(5));
				l2link.setFromHostIp(rs.getString(6));
				l2link.setToHostIp(rs.getString(7));
				l2link.setFromInstanceName(rs.getString(8));
				l2link.setToInstanceName(rs.getString(9));
				l2link.setFromNodeName(rs.getString(10));
				l2link.setToNodeName(rs.getString(11));
				l2link.setFromEth(rs.getString(12));
				l2link.setToEth(rs.getString(13));
				l2link.setScenario_id(s_id);
				l2link.setLogicalFromNodeName(rs.getString(16));
				l2link.setLogicalToNodeName(rs.getString(17));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(l2link);
		return l2link;
	}
}
