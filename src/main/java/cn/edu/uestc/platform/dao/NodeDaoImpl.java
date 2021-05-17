package cn.edu.uestc.platform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.utils.DBUtiles;

public class NodeDaoImpl implements NodeDao {

	/*
	 * 插入节点 2021.5.3
	 */
	@Override
	public void insertL3Node(Node node) {
		// TODO Auto-generated method stub
		String sql = "insert into L3Node(nodeName,manageIp,nodeType,hwArch,"
				+ "numberPort,nodeStatus,x,y,iconUrl,serviceStatus,computeLoad,storageStatus) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, node.getNodeName());
			ps.setString(2, node.getManageIp());
			ps.setInt(3, node.getNodeType());
			ps.setInt(4, node.getHardwareArchitecture());
			ps.setInt(5, node.getNumberPort());
			ps.setInt(6, node.getNodeStatus());
			ps.setFloat(7, node.getX());
			ps.setFloat(8, node.getY());
			ps.setString(9, node.getIconUrl());
			ps.setString(10,node.getServiceStatus());
			ps.setFloat(11,node.getComputeLoad());
			ps.setFloat(12,node.getStorageStatus());
			ps.execute();
			rs = ps.getGeneratedKeys();
			if(rs.next()){
				node.setN_id(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}
	@Override
	public void insertL2Node(Node node) {
		// TODO Auto-generated method stub
		String sql = "insert into L2Node(nodeName,nodeType,subnetIP,"
				+ "numberPort,nodeStatus,x,y,iconUrl) "
				+ "values(?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, node.getNodeName());
			ps.setInt(2, node.getNodeType());
			ps.setString(3, node.getSubnetIP());
			ps.setInt(4, node.getNumberPort());
			ps.setInt(5, node.getNodeStatus());
			ps.setFloat(6, node.getX());
			ps.setFloat(7, node.getY());
			ps.setString(8, node.getIconUrl());
			ps.execute();
			rs = ps.getGeneratedKeys();
			if(rs.next()){
				node.setN_id(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}
	/*
	 * update节点状态
	 */
	public boolean updateNodeStatus(int nodeStatus, String manageIP) {
		String sql = "update node set nodeStatus=? where manageIP=?";
		Connection conn = null;
		PreparedStatement ps = null;
		boolean flag = false;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1,nodeStatus);
			ps.setString(2,manageIP);
			flag = ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
		return flag;
	}
	/*
	 *  根据manageIP查询接节点状态
	 */
	public int getNodeStatusByManageIP(String manageIP){
		String sql = "select nodeStatus from L3Node where manageIP=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nodeStatus = 0;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1,manageIP);
			rs = ps.executeQuery();
			if (rs.next()) {
				nodeStatus = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return nodeStatus;
	}

	public List<Node> getRouter(){
		String sql = "select nodeID,nodeName,nodeType from L3Node where nodeType like '_1'";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Node> nodes = new ArrayList<>();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Node node = new Node();
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setNodeType(rs.getInt(4));
				nodes.add(node);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return nodes;
	}

	public List<Node> getSwitch(){
		String sql = "select nodeID,nodeName,nodeType from L2Node";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Node> nodes = new ArrayList<>();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Node node = new Node();
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setNodeType(rs.getInt(3));
				nodes.add(node);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return nodes;
	}
















	/*
	 * 判断节点存在否,当前场景下的节点名不能重复
	 */
	public boolean haveNodeName(Node node) {
		String sql = "select *from node as n where n.nodeName=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, node.getNodeName());
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
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
				+ "operatingSystem,numberPort,numberInternalModule,numberInternalLink,imageName,nodeStatus,scenario_id,x,y,flavorType,uuid,iconUrl,cn_id) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
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
			ps.setFloat(12, node.getX());
			ps.setFloat(13, node.getY());
			ps.setString(14, node.getFlavorType());
			ps.setString(15, node.getUuid());
			ps.setString(16, node.getIconUrl());
			ps.setInt(17, node.getCn_id());
			ps.execute();
			rs = ps.getGeneratedKeys();
			if(rs.next()){
				node.setN_id(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}

	/*
	 * 根据场景Id查所有节点
	 */
	@Override
	public List<Node> findAllNodeByScenarioId(int s_id) {
		// TODO Auto-generated method stub
		String sql = "select * from node as n where n.scenario_id=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Node> nodes = new ArrayList<>();

		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			rs = ps.executeQuery();
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
				node.setImageName(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
				node.setX(rs.getFloat(13));
				node.setY(rs.getFloat(14));
				node.setFlavorType(rs.getString(15));
				node.setUuid(rs.getString(16));
				node.setIconUrl(rs.getString(17));
				node.setCn_id(rs.getInt(18));
				nodes.add(node);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
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
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
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
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}
	/*
	 * 节点的端口数+1
	 */

	public void plusNumberPort(int n_id) {
		String sql = "update node set numberPort = numberPort + 1 where n_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, n_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}

	/*
	 * 根据节点id查找节点
	 */
	@Override
	public Node getNodeByNodeId(int n_id) {
		// TODO Auto-generated method stub
		String sql = "select *from node where n_id = ?";
		Node node = new Node();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, n_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setManageIp(rs.getString(3));
				node.setNodeType(rs.getInt(4));
				node.setHardwareArchitecture(rs.getInt(5));
				node.setOperatingSystem(rs.getInt(6));
				node.setNumberPort(rs.getInt(7));
				node.setNumberInternalModule(rs.getInt(8));
				node.setNumberInternalLink(rs.getInt(9));
				node.setImageName(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
				node.setX(rs.getFloat(13));
				node.setY(rs.getFloat(14));
				node.setFlavorType(rs.getString(15));
				node.setUuid(rs.getString(16));
				node.setIconUrl(rs.getString(17));
				node.setCn_id(rs.getInt(18));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return node;
	}

	/*
	 * 根据节点nodeName和s_id拿节点
	 */
	@Override
	public Node getNodeBynodeName(String nodeName, int s_id) {
		// TODO Auto-generated method stub
		String sql = "select *from node where scenario_id = ? and nodeName = ?";
		Node node = new Node();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.setString(2, nodeName);
			rs = ps.executeQuery();
			while (rs.next()) {
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setManageIp(rs.getString(3));
				node.setNodeType(rs.getInt(4));
				node.setHardwareArchitecture(rs.getInt(5));
				node.setOperatingSystem(rs.getInt(6));
				node.setNumberPort(rs.getInt(7));
				node.setNumberInternalModule(rs.getInt(8));
				node.setNumberInternalLink(rs.getInt(9));
				node.setImageName(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
				node.setX(rs.getFloat(13));
				node.setY(rs.getFloat(14));
				node.setFlavorType(rs.getString(15));
				node.setUuid(rs.getString(16));
				node.setIconUrl(rs.getString(17));
				node.setCn_id(rs.getInt(18));
			}
		} catch (SQLException e) {

		}
		return node;
	}

	/*
	 * ip是否冲突
	 */
	@Override
	public boolean isHaveIp(Node node) {
		// TODO Auto-generated method stub
		String sql = "select *from node where manageIp = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, node.getManageIp());
			rs = ps.executeQuery();
			if (rs.next() == false) {
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return true;
	}

	/*
	 * update节点属性
	 */
	public boolean updataNode(Node node) {
		String sql = " update node set nodeName=?,manageIp=?,flavorType=? where n_id=?";
		Connection conn = null;
		PreparedStatement ps = null;
		boolean flag = false;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, node.getNodeName());
			ps.setString(2, node.getManageIp());
			ps.setString(3, node.getFlavorType());
			ps.setInt(4, node.getN_id());
			flag = ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
		return flag;
	}

	/*
	 * 通过portID拿到节点
	 */
	@Override
	public Node getNodeByPortId(int txPort_id) {
		// TODO Auto-generated method stub
		String sql = "select *from node as n join (select *from port as p where p.pt_id=?) as p"
				+ " where n.n_id=p.node_id";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Node node = new Node();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, txPort_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setManageIp(rs.getString(3));
				node.setNodeType(rs.getInt(4));
				node.setHardwareArchitecture(rs.getInt(5));
				node.setOperatingSystem(rs.getInt(6));
				node.setNumberPort(rs.getInt(7));
				node.setNumberInternalModule(rs.getInt(8));
				node.setNumberInternalLink(rs.getInt(9));
				node.setImageName(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
				node.setX(rs.getFloat(13));
				node.setY(rs.getFloat(14));
				node.setFlavorType(rs.getString(15));
				node.setUuid(rs.getString(16));
				node.setIconUrl(rs.getString(17));
				node.setCn_id(rs.getInt(18));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return node;
	}

	@Override
	public Node getNodeByLink(Link link) {
		String sql = "select *from node where ";

		return null;
	}

	@Override
	public void minusNumberComplexNode(int s_id) {
		String sql = "update scenario set numberNode = numberNode - 1,numberComplexNode"
				+ " = numberComplexNode - 1 where s_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}

	}

	@Override
	public void minusNumberSimpleNode(int s_id) {
		String sql = "update scenario set numberNode = numberNode-1,numberSimpleNode"
				+ " = numberSimpleNode-1 where s_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}

	}

	@Override
	public void deleteNode(int n_id) {
		// TODO Auto-generated method stub
		String sql = "delete from node where n_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, n_id);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}

	}

	@Override
	public void updataNodeUuid(String uuid, int n_id) {
		// TODO Auto-generated method stub
		String sql = "update node set uuid = ? where n_id=?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			ps.setInt(2, n_id);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}

	@Override
	public List<Node> getNodeListByCnid(int cn_id) {
		// TODO Auto-generated method stub
		String sql = "select *from node where cn_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Node> nodes = new ArrayList<Node>();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, cn_id);
			rs = ps.executeQuery();
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
				node.setImageName(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
				node.setX(rs.getFloat(13));
				node.setY(rs.getFloat(14));
				node.setFlavorType(rs.getString(15));
				node.setUuid(rs.getString(16));
				node.setIconUrl(rs.getString(17));
				node.setCn_id(rs.getInt(18));
				nodes.add(node);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return nodes;
	}

	@Override
	public List<String> getAllNodeIp(int s_id) {
		// TODO Auto-generated method stub
		String sql = "select manageIp from node where scenario_id = ?";

		// 用于存放左右IP
		List<String> manageIps = new LinkedList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, s_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				manageIps.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return manageIps;
	}

	public List<String> getAllNodeIp() {
		// TODO Auto-generated method stub
		String sql = "select manageIp from node";

		// 用于存放左右IP
		List<String> manageIps = new LinkedList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtiles.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				manageIps.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, stmt, conn);
		}
		return manageIps;
	}

	@Test
	public void demo1111() {

		System.out.println(getAllNodeIp());
	}

	@Override
	public void updateNodeXY(List<Node> nodes) {
		// TODO Auto-generated method stub

		String sql = "update node set X=?,Y=? where n_id=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			for (Node node : nodes) {
				ps.setFloat(1, node.getX());
				ps.setFloat(2, node.getY());
				ps.setInt(3, node.getN_id());
				ps.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Node getNodeByNodeName(String nodeName) {
		// TODO Auto-generated method stub
		String sql = "select *from node where nodeName = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Node node = new Node();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, nodeName);
			rs = ps.executeQuery();
			while (rs.next()) {
				node.setN_id(rs.getInt(1));
				node.setNodeName(rs.getString(2));
				node.setManageIp(rs.getString(3));
				node.setNodeType(rs.getInt(4));
				node.setHardwareArchitecture(rs.getInt(5));
				node.setOperatingSystem(rs.getInt(6));
				node.setNumberPort(rs.getInt(7));
				node.setNumberInternalModule(rs.getInt(8));
				node.setNumberInternalLink(rs.getInt(9));
				node.setImageName(rs.getString(10));
				node.setNodeStatus(rs.getInt(11));
				node.setS_id(rs.getInt(12));
				node.setX(rs.getFloat(13));
				node.setY(rs.getFloat(14));
				node.setFlavorType(rs.getString(15));
				node.setUuid(rs.getString(16));
				node.setIconUrl(rs.getString(17));
				node.setCn_id(rs.getInt(18));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return node;
	}

	@Override
	public void updateLocation(int s_id, String nodeName, int x, int y) {
		// TODO Auto-generated method stub
		String sql = "update node set X=?,Y=? where nodeName=? and scenario_id=?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, x);
			ps.setInt(2, y);
			ps.setString(3, nodeName);
			ps.setInt(4, s_id);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtiles.releaseResource(ps, conn);
		}
	}
}
