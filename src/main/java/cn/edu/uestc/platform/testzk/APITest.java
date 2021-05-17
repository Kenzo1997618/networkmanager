package cn.edu.uestc.platform.testzk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import cn.edu.uestc.platform.dao.NodeDao;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;

public class APITest {
	public static final String GET_URL = "http://112.4.27.9/mall-back/if_user/store_list?storeId=32";
	public static final String POST_URL = "http://192.168.1.143:8181/restconf/config/opendaylight-inventory:nodes/node/openflow:171592210022736/flow-node-inventory:table/60/flow/5"
			+ "{'flow': [ {'id': '5','match': {'in-port': '5'},'instructions': {'instruction': [{'order': '0','go-to-table': {'table_id': 80'}}]},'barrier': 'false',"
			+ "'priority': '60000'," + "'idle-timeout': '0'," + "'hard-timeout': '0'," + "'table_id': '60'" + "}]}";

	/**
	 * 接口调用 GET
	 */
	public static void httpURLConectionGET() {
		try {
			URL url = new URL(GET_URL); // 把字符串转换为URL请求地址
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
			connection.connect();// 连接会话
			// 获取输入流
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {// 循环读取流
				sb.append(line);
			}
			br.close();// 关闭流
			connection.disconnect();// 断开连接
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("失败!");
		}
	}

	/**
	 * 接口调用 POST
	 */
	public static void httpURLConnectionPOST() {
		try {
			URL url = new URL(POST_URL);

			// 将url 以 open方法返回的urlConnection 连接强转为HttpURLConnection连接
			// (标识一个url所引用的远程对象连接)
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 此时cnnection只是为一个连接对象,待连接中

			// 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
			connection.setDoOutput(true);

			// 设置连接输入流为true
			connection.setDoInput(true);

			// 设置请求方式为post
			connection.setRequestMethod("POST");

			// post请求缓存设为false
			connection.setUseCaches(false);

			// 设置该HttpURLConnection实例是否自动执行重定向
			connection.setInstanceFollowRedirects(true);

			// 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)
			// application/x-javascript text/xml->xml数据
			// application/x-javascript->json对象
			// application/x-www-form-urlencoded->表单数据
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// 建立连接
			// (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
			connection.connect();

			// 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
			DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
			String parm = "storeId=" + URLEncoder.encode("32", "utf-8"); // URLEncoder.encode()方法
																			// 为字符串进行编码

			// 将参数输出到连接
			dataout.writeBytes(parm);

			// 输出完成后刷新并关闭流
			dataout.flush();
			dataout.close(); // 重要且易忽略步骤 (关闭流,切记!)

			System.out.println(connection.getResponseCode());

			// 连接发起请求,处理服务器响应 (从连接获取到输入流并包装为bufferedReader)
			BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder(); // 用来存储响应数据

			// 循环读取流,若不到结尾处
			while ((line = bf.readLine()) != null) {
				sb.append(bf.readLine());
			}
			bf.close(); // 重要且易忽略步骤 (关闭流,切记!)
			connection.disconnect(); // 销毁连接
			System.out.println(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 就是这个可以用的ODL
	public static void main(String[] args) throws UnsupportedEncodingException {

		String str = "{\"flow\": [{\"id\": \"5\",\"match\": {\"in-port\": \"5\"},\"instructions\": {\"instruction\": [{\"order\": \"0\",\"go-to-table\": {\"table_id\": \"80\"}}]},\"barrier\": \"false\",\"priority\": \"60000\",\"idle-timeout\": \"0\",\"hard-timeout\": \"0\",\"table_id\": \"60\"}]}";
		String b = new String(str.getBytes("ISO-8859-1"), "UTF-8");
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 用户名:密码
		String encoding = new String(Base64.encodeBase64(StringUtils.getBytesUtf8("admin:admin")));
		StringBuilder result = new StringBuilder();
		try {
			HttpPut putRequest = new HttpPut(
					"http://121.48.175.200:8080/restconf/config/opendaylight-inventory:nodes/node/openflow:171592210022736/flow-node-inventory:table/60/flow/5");
			putRequest.addHeader("Authorization", "Basic " + encoding);
			putRequest.addHeader("Content-Type", "application/json;charset=UTF-8");
			putRequest.addHeader("Accept", "application/json"); // JSONObject
			StringEntity input = null;
			try {
				input = new StringEntity(b);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			putRequest.setEntity(input);
			HttpResponse response = httpClient.execute(putRequest);
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			while ((output = br.readLine()) != null) {
				result.append(output);
			}
			System.out.println(output);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
