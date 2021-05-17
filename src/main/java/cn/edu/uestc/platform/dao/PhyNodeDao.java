package cn.edu.uestc.platform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.utils.DBUtiles;

public class PhyNodeDao {

	public void insertNode(String phyNodeIP, int proxyNodeID) {
		// TODO Auto-generated method stub
		String sql = "insert into phynode(phyNodeIP,proxyNodeID) " + "values(?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, phyNodeIP);
			ps.setInt(2, proxyNodeID);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}

}
