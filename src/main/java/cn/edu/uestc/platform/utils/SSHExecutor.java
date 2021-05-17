package cn.edu.uestc.platform.utils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHExecutor {
	
	private static int SESSION_TIMEOUT = 30000;// 原本是30000，等待时间过长，减少点吧
	private JSch jsch = null;
	private Session session = null;

	public SSHExecutor(String username, String password, String host) throws JSchException {
		jsch = new JSch();
		
		//zk new add
//		jsch.addIdentity(".id_rsa");
		session = jsch.getSession(username, host, 22);
		session.setPassword(password);
		session.setUserInfo(new MyUserInfo());
		session.setConfig("StrictHostKeyChecking", "no");// 注意：这里是no，如果没有添加的话将会有很多不必要的信息
		session.connect(SESSION_TIMEOUT);
	}

	/**
	 * 主要是在这里执行命令
	 */
	public String exec(String cmd) throws Exception {
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();

		StringBuffer buf = new StringBuffer(1024);
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				buf.append(new String(tmp, 0, i));
			}
			if (channelExec.isClosed()) {
				channelExec.getExitStatus();
				break;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}
		channelExec.disconnect();
		return buf.toString();
	}

	public void close() {
		session.disconnect();
	}

	/*
	 * 自定义UserInfo
	 */
	private class MyUserInfo implements UserInfo {

		public String getPassphrase() {
			return null;
		}

		public String getPassword() {
			return null;
		}

		public boolean promptPassword(String s) {
			return false;
		}

		public boolean promptPassphrase(String s) {
			return false;
		}

		public boolean promptYesNo(String s) {
			System.out.println(s);
			System.out.println("true");
			return true;
		}

		public void showMessage(String s) {
		}
	}
	
	
	@Test
	public void demo() throws Exception{
	
	}
	
	public static void main(String[] args) throws Exception {
//		SSHExecutor ssh = new SSHExecutor("compute3","123456","10.0.0.41");
//		System.out.println(ssh.exec("ls"));
	}
}
