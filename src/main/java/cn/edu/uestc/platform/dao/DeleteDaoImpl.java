package cn.edu.uestc.platform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;

import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.DBUtiles;

public class DeleteDaoImpl implements DeleteDao {

	@Override
	// 删除链路时，链路两端对应的两个端口对应的ip地址清0，让端口的状态为0(未占用)
	public void deleteLink(Link link) {
		// TODO Auto-generated method stub
		String sql1 = "delete from link where l_id=?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql1);
			ps.setInt(1, link.getL_id());
			ps.execute();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				System.out.println("删除链路发生错误，回滚数据库。");
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}

	}

	// 删除端口时，同时需要删除端口对应的链路，同时将链路的另一端的端口ip清0并让其状态为0，并且让节点的numberPort减1
	@SuppressWarnings("resource")
	// 此函数的两个id参数，是通过查一条链路的两端port得到的。
	@Override
	public void deletePortIncludeLink(Port port, int portID1, int PortID2) {

		Connection conn = null;
		PreparedStatement ps = null;

		// 先给端口对应的链路的两端端口状态置0，ip置0
		String sql1 = "update port as p set p.portStatus = 0,p.portIP = 0 where p.pt_id =? or p.pt_id = ?";

		// 删除连接此端口的链路
		String sql2 = "delete from link where txPortID = ? or rxPortID = ?";

		// 删除端口
		String sql3 = "delete from port where pt_id=? ";

		// 节点表中numberPort -1
		String sql4 = "update node as n set n.numberPort=n.numberPort-1 where n.n_id = ?";

		try {
			conn = DBUtiles.getConnection();
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(sql1);
			ps.setInt(1, portID1);
			ps.setInt(2, PortID2);
			ps.execute();

			ps = conn.prepareStatement(sql2);
			ps.setInt(1, port.getPt_id());
			ps.setInt(2, port.getPt_id());
			ps.execute();

			ps = conn.prepareStatement(sql3);
			ps.setInt(1, port.getPt_id());
			ps.execute();

			ps = conn.prepareStatement(sql4);
			ps.setInt(1, port.getN_id());
			ps.execute();
			conn.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}

	}

	@Override
	/*
	 * 删除节点,删除节点包含的所有端口，给场景表中统计节点的次数减1.
	 */
	public void deleteSimpleNode(Node node) {
		String sql = "delete from node where n_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, node.getN_id());
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}

	}

}
