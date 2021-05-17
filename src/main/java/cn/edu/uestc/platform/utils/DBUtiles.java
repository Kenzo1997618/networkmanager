package cn.edu.uestc.platform.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import cn.edu.uestc.platform.action.ActionController;

public class DBUtiles {
	static String url = "jdbc:mariadb://controller:3306/networksimulation";
	static String username = "root";
	static String password = "123456";
	private static Logger logger = Logger.getLogger(DBUtiles.class);
	static {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			logger.info("[加载Mysql驱动]  执行时间: " + new Date());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	public static void releaseResource(Statement ps, Connection conn) {
		if (null != ps) {
			try {
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	public static void releaseResource(ResultSet rs, Statement ps, Connection conn) {
		if (rs != null)
			try {
				rs.close();
			} catch (Exception e2) {
			}
		if (ps != null)
			try {
				ps.close();
			} catch (Exception e2) {
			}
		if (conn != null)
			try {
				conn.close();
			} catch (Exception e2) {
			}
	}
}
