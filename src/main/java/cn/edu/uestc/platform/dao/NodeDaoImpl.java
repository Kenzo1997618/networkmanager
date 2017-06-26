package cn.edu.uestc.platform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Project;
import cn.edu.uestc.platform.pojo.Scenario;
import cn.edu.uestc.platform.utils.DBUtiles;

public class NodeDaoImpl implements NodeDao {

	/*
	 * 判断节点存在否,当前场景下的节点名不能重复，需要传入当前场景s_id和节点名
	 */

	public boolean haveNodeName(Node node) {
		String sql = "select *from node as n where n.nodeName=? and n.scenario_id=?";
		Connection conn;
		try {
			conn = DBUtiles.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, node.getNodeName());
			ps.setInt(2, node.getS_id());
			ResultSet rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * 插入节点
	 */
	@Override
	public void insertNode(Node node) {
		// TODO Auto-generated method stub
		String sql = "insert into node(nodeName,manageIp,nodeType,hardwareArchitecture,"
				+ "operatingSystem,numberPort,numberInternalModule,numberInternalLink,imageName,nodeStatus,scenario_id,x,y,flavorType) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn;
		try {
			conn = DBUtiles.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, node.getNodeName());
			ps.setString(2, node.getManageIp());
			ps.setInt(3, node.getNodeType());
			ps.setInt(4, node.getHardwareArchitecture());
			ps.setInt(5, node.getOperatingSystem());
			ps.setInt(6, node.getNumberPort());
			ps.setInt(7, node.getNumberInternalModule());
			ps.setInt(8, node.getNumberInternalLink());
			ps.setString(9, node.getImageName());
			ps.setInt(10, node.getNodeStatus());
			ps.setInt(11, node.getS_id());
			ps.setInt(12, 0);
			ps.setInt(13, 0);
			ps.setString(14, node.getFlavorType());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 根据场景Id查所有节点
	 */
	@Override
	public List<Node> findAllNodeByScenarioId(int s_id) {
		// TODO Auto-generated method stub
		String sql = "select * from Node as n where n.scenario_id=?";
		Connection conn;
		List<Node> nodes = new ArrayList<>();

		try {
			conn = DBUtiles.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Node node = new Node();
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setManageIp(rs.getString(3));
				node.setNodeType(rs.getInt(4));
				node.setHardwareArchitecture(rs.getInt(5));
				node.setOperatingSystem(rs.getInt(6));
				node.setNumberPort(rs.getInt(7));
				node.setNumberInternalModule(rs.getInt(8));
				node.setNumberInternalLink(rs.getInt(9));
//				node.setImagePath(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
//				node.setX(rs.getInt(13));
//				node.setY(rs.getInt(14));
				nodes.add(node);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodes;
	}

	/*
	 * 总节点数和简单节点数+1
	 */
	@Override
	public void plusNumberSimpleNode(int s_id) {
		// TODO Auto-generated method stub
		String sql = "update scenario set numberNode = numberNode + 1,numberSimpleNode"
				+ " = numberSimpleNode + 1 where s_id = ?";
		Connection conn;
		try {
			conn = DBUtiles.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * 总节点数和复杂节点数+1
	 */
	@Override
	public void plusNumberComplexNode(int s_id) {
		// TODO Auto-generated method stub
		String sql = "update scenario set numberNode = numberNode + 1,numberComplexNode"
				+ " = numberComplexNode + 1 where s_id = ?";
		Connection conn;
		try {
			conn = DBUtiles.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
