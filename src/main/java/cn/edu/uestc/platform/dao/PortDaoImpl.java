package cn.edu.uestc.platform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.DBUtiles;

public class PortDaoImpl implements PortDao {
	/*
	 * 插入端口 2021.5.3
	 */
	@Override
	public void insertPort(Port port) {
		// TODO Auto-generated method stub
		String sql = "insert into port(portName,nodeClass,nodeID,portType,portStatus,"
				+ "maximumRate,packetLoss,portIP) "
				+ "values(?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, port.getPortName());
			ps.setInt(2,port.getNodeClass());
			ps.setInt(3, port.getN_id());
			ps.setInt(4,port.getPortType());
			ps.setInt(5, port.getPortStatus());
			ps.setString(6, port.getMaximumRate());
			ps.setFloat(7, port.getPacketLoss());
			ps.setString(8, port.getPortIp());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}

	/*
	 * 根据节点n_id返回所有端口 2021.5.3
	 */
	@Override
	public List<Port> getPortList(int n_id) {
		// TODO Auto-generated method stub
		String sql = "select * from port as p where p.nodeID=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Port> ports = new ArrayList<Port>();
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, n_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				Port port = new Port();
				port.setPt_id(rs.getInt(1));
				port.setPortName(rs.getString(2));
				port.setNodeClass(rs.getInt(3));
				port.setN_id(n_id);
				port.setPortType(rs.getInt(6));
				port.setPortStatus(rs.getInt(7));
				port.setMaximumRate(rs.getString(8));
				port.setPacketLoss(rs.getFloat(9));
				port.setPortIp(rs.getString(10));

				ports.add(port);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(rs, ps, conn);
		}
		return ports;
	}
	@Override
	public void updateThePortStatusTo2(int pt_id) {
		// TODO Auto-generated method stub
		String sql = "update port set portStatus = 2 where pt_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pt_id);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}
	@Override
	public void updateThePortStatusTo1(int pt_id) {
		// TODO Auto-generated method stub
		String sql = "update port set portStatus = 1 where pt_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBUtiles.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pt_id);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtiles.releaseResource(ps, conn);
		}
	}















//	/*
//	 * 插入端口
//	 */
//	@Override
//	public void insertPort(Port port) {
//		// TODO Auto-generated method stub
//		String sql = "insert into port(portName,transmitterPower,portType,portStatus,"
//				+ "transmitterFrequency,transmitterBandwidth,transmitterGain,receiverFrequency,receiverBandwidth,receiverGain,modem,node_id,maximumRate,packetLoss,portIP,isTemplate,isMultiplexing,linkCount) "
//				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, port.getPortName());
//			ps.setString(2, port.getTransmitterPower());
//			ps.setInt(3, port.getPortType());
//			ps.setInt(4, port.getPortStatus());
//			ps.setString(5, port.getTransmitterFrequency());
//			ps.setString(6, port.getTransmitterBandwidth());
//			ps.setString(7, port.getTransmitterGain());
//			ps.setString(8, port.getReceiverFrequency());
//			ps.setString(9, port.getReceiverBandwidth());
//			ps.setString(10, port.getReceiverGain());
//			ps.setString(11, port.getModem());
//			ps.setInt(12, port.getN_id());
//			ps.setString(13, port.getMaximumRate());
//			ps.setString(14, port.getPacketLoss());
//			ps.setString(15, port.getPortIp());
//			ps.setInt(16, port.getIsTemplate());
//			ps.setInt(17, port.getIsMultiplexing());
//			ps.setInt(18, port.getLinkCount());
//			ps.execute();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//	}

//	/*
//	 * 根据节点n_id返回所有端口
//	 */
//	@Override
//	public List<Port> getPortList(int n_id) {
//		// TODO Auto-generated method stub
//		String sql = "select *from port as p where p.node_id=?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		List<Port> ports = new ArrayList<Port>();
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, n_id);
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				Port port = new Port();
//				port.setPt_id(rs.getInt(1));
//				port.setPortName(rs.getString(2));
//				port.setTransmitterPower(rs.getString(3));
//				port.setPortType(rs.getInt(4));
//				port.setPortStatus(rs.getInt(5));
//				port.setTransmitterFrequency(rs.getString(6));
//				port.setTransmitterBandwidth(rs.getString(7));
//				port.setTransmitterGain(rs.getString(8));
//				port.setReceiverFrequency(rs.getString(9));
//				port.setReceiverBandwidth(rs.getString(10));
//				port.setReceiverGain(rs.getString(11));
//				port.setModem(rs.getString(12));
//				port.setN_id(n_id);
//				port.setMaximumRate(rs.getString(14));
//				port.setPacketLoss(rs.getString(15));
//				port.setPortIp(rs.getString(16));
//				port.setIsTemplate(rs.getInt(17));
//				port.setIsMultiplexing(rs.getInt(18));
//				port.setLinkCount(rs.getInt(19));
//				ports.add(port);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(rs, ps, conn);
//		}
//		return ports;
//	}

//	/*
//	 * 根据场景s_id和节点名查询到节点所包含的port
//	 */
//	@Override
//	public List<Port> getPortListBynodeName(int s_id, String nodeName) {
//		// TODO Auto-generated method stub
//		String sql = "select *from port as p join (select *from node as n where n.nodeName=? "
//				+ "and n.scenario_id = ?) as n on p.node_id=n.n_id;";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		List<Port> ports = new ArrayList<Port>();
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, nodeName);
//			ps.setInt(2, s_id);
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				Port port = new Port();
//				port.setPt_id(rs.getInt(1));
//				port.setPortName(rs.getString(2));
//				port.setTransmitterPower(rs.getString(3));
//				port.setPortType(rs.getInt(4));
//				port.setPortStatus(rs.getInt(5));
//				port.setTransmitterFrequency(rs.getString(6));
//				port.setTransmitterBandwidth(rs.getString(7));
//				port.setTransmitterGain(rs.getString(8));
//				port.setReceiverFrequency(rs.getString(9));
//				port.setReceiverBandwidth(rs.getString(10));
//				port.setReceiverGain(rs.getString(11));
//				port.setModem(rs.getString(12));
//				port.setN_id(rs.getInt(13));
//				port.setMaximumRate(rs.getString(14));
//				port.setPacketLoss(rs.getString(15));
//				port.setPortIp(rs.getString(16));
//				port.setIsTemplate(rs.getInt(17));
//				port.setIsMultiplexing(rs.getInt(18));
//				port.setLinkCount(rs.getInt(19));
//				ports.add(port);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(rs, ps, conn);
//		}
//		return ports;
//	}

//	/*
//	 * 更新端口IP,以及端口状态
//	 */
//	@Override
//	public void updatePortIP(int Port_id, String IP) {
//		// TODO Auto-generated method stub
//		String sql = "update port as p set p.portIP = ?,p.portStatus = ? where p.pt_id=?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, IP);
//			ps.setInt(2, 1);
//			ps.setInt(3, Port_id);
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//
//	}
//
//	/*
//	 * 通过链路查链路两端的portID,返回一个长度为2的整型数组。若无链路，则返回{0,0}
//	 */
//	@Override
//	public int[] getPortIdsOnSameLinkByPort(Port port) {
//		// TODO Auto-generated method stub
//		String sql = "select txPortID,rxPortID from link where txPortID =? or rxPortID =?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		int portId[] = new int[2];
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, port.getPt_id());
//			ps.setInt(2, port.getPt_id());
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				portId[0] = rs.getInt(1);
//				portId[1] = rs.getInt(2);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(rs, ps, conn);
//		}
//		return portId;
//	}

	/*
	 * 
	 * 通过链路拿到链路两端的port
	 * 
	 * //+----------+----------+--------------+-----------------+ // | l_id |
	 * txPortID | rxPortID | fromNodeName | toNodeName | //
	 * +------+----------+----------+------------+------------+- // | 50 | 106 |
	 * 107 | node1 | node2 | // | 51 | 108 | 109 | node3 | node4 | //
	 * +------+----------+----------+------------+------------+--
	 * 
	 * 根据链路表如上图，txPortID对应 fromNodeName rxPortID对应toNodeName 在mysql数据库中 select
	 * *from port where pt_id = ? or pt_id = ?"; 查出来的数据是按如下排列的
	 * +-------+----------+---- | pt_id || portIP | +-------+----------+------ |
	 * 106 | | 192.168.7.4 | | 107 | | 192.168.7.6 | +-------+----------+------
	 * 查出来的PortId永远是先
	 * 
	 * 
	 */
//	@Override
//	public List<Port> getPortByLink(Link link) {
//		String sql = "select *from port where pt_id = ? or pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		List<Port> ports = new ArrayList<Port>();
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, link.getTxPort_id());
//			ps.setInt(2, link.getRxPort_id());
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				Port port = new Port();
//				port.setPt_id(rs.getInt(1));
//				port.setPortName(rs.getString(2));
//				port.setTransmitterPower(rs.getString(3));
//				port.setPortType(rs.getInt(4));
//				port.setPortStatus(rs.getInt(5));
//				port.setTransmitterFrequency(rs.getString(6));
//				port.setTransmitterBandwidth(rs.getString(7));
//				port.setTransmitterGain(rs.getString(8));
//				port.setReceiverFrequency(rs.getString(9));
//				port.setReceiverBandwidth(rs.getString(10));
//				port.setReceiverGain(rs.getString(11));
//				port.setModem(rs.getString(12));
//				port.setN_id(rs.getInt(13));
//				port.setMaximumRate(rs.getString(14));
//				port.setPacketLoss(rs.getString(15));
//				port.setPortIp(rs.getString(16));
//				port.setIsTemplate(rs.getInt(17));
//				port.setIsMultiplexing(rs.getInt(18));
//				port.setLinkCount(rs.getInt(19));
//				ports.add(port);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return ports;
//	}

//	@Override
//	public Port getPortByID(int pt_id) {
//		// TODO Auto-generated method stub
//		String sql = "select *from port where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		Port port = new Port();
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, pt_id);
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				port.setPt_id(rs.getInt(1));
//				port.setPortName(rs.getString(2));
//				port.setTransmitterPower(rs.getString(3));
//				port.setPortType(rs.getInt(4));
//				port.setPortStatus(rs.getInt(5));
//				port.setTransmitterFrequency(rs.getString(6));
//				port.setTransmitterBandwidth(rs.getString(7));
//				port.setTransmitterGain(rs.getString(8));
//				port.setReceiverFrequency(rs.getString(9));
//				port.setReceiverBandwidth(rs.getString(10));
//				port.setReceiverGain(rs.getString(11));
//				port.setModem(rs.getString(12));
//				port.setN_id(rs.getInt(13));
//				port.setMaximumRate(rs.getString(14));
//				port.setPacketLoss(rs.getString(15));
//				port.setPortIp(rs.getString(16));
//				port.setIsTemplate(rs.getInt(17));
//				port.setIsMultiplexing(rs.getInt(18));
//				port.setLinkCount(rs.getInt(19));
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(rs, ps, conn);
//		}
//
//		return port;
//	}

//	@Override
//	public List<Port> getportListByPortIDs(int[] portID) {
//		String sql = "select *from port where pt_id = ? or pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		List<Port> ports = new ArrayList<Port>();
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, portID[0]);
//			ps.setInt(2, portID[1]);
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				Port port = new Port();
//				port.setPt_id(rs.getInt(1));
//				port.setPortName(rs.getString(2));
//				port.setTransmitterPower(rs.getString(3));
//				port.setPortType(rs.getInt(4));
//				port.setPortStatus(rs.getInt(5));
//				port.setTransmitterFrequency(rs.getString(6));
//				port.setTransmitterBandwidth(rs.getString(7));
//				port.setTransmitterGain(rs.getString(8));
//				port.setReceiverFrequency(rs.getString(9));
//				port.setReceiverBandwidth(rs.getString(10));
//				port.setReceiverGain(rs.getString(11));
//				port.setModem(rs.getString(12));
//				port.setN_id(rs.getInt(13));
//				port.setMaximumRate(rs.getString(14));
//				port.setPacketLoss(rs.getString(15));
//				port.setPortIp(rs.getString(16));
//				port.setIsTemplate(rs.getInt(17));
//				port.setIsMultiplexing(rs.getInt(18));
//				port.setLinkCount(rs.getInt(19));
//				ports.add(port);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ports;
//	}

//	@Override
//	public void deletePort(Port port) {
//		// TODO Auto-generated method stub
//		String sql = "delete from port where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, port.getPt_id());
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//
//	}

//	@Override
//	public Port getPortByIP(String ip) {
//		// TODO Auto-generated method stub
//		String sql = "select *from port where portIP = ?";
//		Port port = new Port();
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, ip);
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				port.setPt_id(rs.getInt(1));
//				port.setPortName(rs.getString(2));
//				port.setTransmitterPower(rs.getString(3));
//				port.setPortType(rs.getInt(4));
//				port.setPortStatus(rs.getInt(5));
//				port.setTransmitterFrequency(rs.getString(6));
//				port.setTransmitterBandwidth(rs.getString(7));
//				port.setTransmitterGain(rs.getString(8));
//				port.setReceiverFrequency(rs.getString(9));
//				port.setReceiverBandwidth(rs.getString(10));
//				port.setReceiverGain(rs.getString(11));
//				port.setModem(rs.getString(12));
//				port.setN_id(rs.getInt(13));
//				port.setMaximumRate(rs.getString(14));
//				port.setPacketLoss(rs.getString(15));
//				port.setPortIp(rs.getString(16));
//				port.setIsTemplate(rs.getInt(17));
//				port.setIsMultiplexing(rs.getInt(18));
//				port.setLinkCount(rs.getInt(19));
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return port;
//	}

//	@Override
//	public void addLinkCount(int pt_id) {
//		// TODO Auto-generated method stub
//		String sql = "update port set linkCount = linkCount +1 where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, pt_id);
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//	}

//	@Override
//	public void subLinkCount(int pt_id) {
//		// TODO Auto-generated method stub
//		String sql = "update port set linkCount = linkCount -1 where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, pt_id);
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//	}

//	@Override
//	public void updateThePortStatusTo1(int pt_id) {
//		// TODO Auto-generated method stub
//		String sql = "update port set portStatus = 1 where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, pt_id);
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//	}

//	@Override
//	public void updateThePortStatusTo0(int pt_id) {
//		// TODO Auto-generated method stub
//		String sql = "update port set portStatus = 0 where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setInt(1, pt_id);
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//	}

//	@Override
//	public void editPort(Port port) {
//		// TODO Auto-generated method stub
//		String sql = "update port set portName = ?,transmitterFrequency=?,transmitterBandwidth=?,"
//				+ "transmitterGain=?,receiverFrequency=?,receiverBandwidth=?,receiverGain=?,modem=?"
//				+ ",maximumRate=?,packetLoss=? where pt_id = ?";
//		Connection conn = null;
//		PreparedStatement ps = null;
//
//		try {
//			conn = DBUtiles.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, port.getPortName());
//			ps.setString(2, port.getTransmitterFrequency());
//			ps.setString(3, port.getTransmitterBandwidth());
//			ps.setString(4, port.getTransmitterGain());
//			ps.setString(5, port.getReceiverFrequency());
//			ps.setString(6, port.getReceiverBandwidth());
//			ps.setString(7, port.getReceiverGain());
//			ps.setString(8, port.getModem());
//			ps.setString(9, port.getMaximumRate());
//			ps.setString(10, port.getPacketLoss());
//			ps.setInt(11, port.getPt_id());
//			ps.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtiles.releaseResource(ps, conn);
//		}
//
//	}

}
