package cn.edu.uestc.platform.utils;

/**
 * 常量池，放置IP地址/Openstack网络地址/登陆的账户及其密码
 */
public class Constants {

	public static final String NOVA_FLAVOR = "2";// id=2, name=m1.small
	public static final String GLANCE_IMAGE = "2dd6daaa-4f3b-4dbf-a78c-33eddba5de8a";// 虚拟机模板Router_v1.6.1
	public static final String OLSR_IMAGE = "6c0425c3-2c8d-4446-a74c-d5d0436cac28";// OLSR
	public static final String BATMANL3_IMAGE = "2c1f05b7-d3a4-4346-b5b5-d4a14252d50d";// BATMAND
	public static final String BABEL = "5eb74719-364e-46ae-9885-f8a77f833506";// BABEL
	
	
	
	public static final String MANET_FLAVOR = "1";// id=2, name=m1.small
	public static final String LEISI_IMAGE = "0489c560-4385-4180-869c-6b0fd6b10f4d";// LEISI IMAGE
	
	public static final String DOCKER_FLAVOR = "1";// id=2, name=m1.small
	public static final String DOCKER_IMAGE = "0bac47ca-08b7-409d-85fe-b5d153abe900";//docker镜像模板zph/new:14.12
	
	

	public static final String LOGIN_URL = "http://10.0.0.11:5000/v3";//张珂注释的
//	public static final String LOGIN_URL = "http://192.168.1.140:5000/v3";//张珂增加的
	//public static final String ZPH_PROJECT_ID = "76c92bd33cfd496ab6b84114e8a94e79";// zph项目id
	public static final String ZPH_PROJECT_ID = "7e7433b3b12240ee9be565ae54b1b81e";// zph项目id
	public static final String ADMIN_PROJECT_ID = "2b9da55571c949468f18b39c8b8abec3";// admin项目id
	
	public static final String CONTROLLER_IP_ADDRESS = "10.0.0.11";// controller节点的IP 张珂注释的
//	public static final String CONTROLLER_IP_ADDRESS = "192.168.1.140";//张珂增加的
	public static final String DOCKER_NODE_IP_ADDRESS = "10.0.0.61";// docker节点的IP
	public static final String DOCKER_NODE_USERNAME = "compute5";// docker节点的IP
	
	public static final String VM_ZONE = "nova";
	public static final String DOCKER_ZONE = "docker";
	public static final String AMD_ZONE = "amd";
	public static final String MANET_ZONE = "manet";
	
	public static final String CONTROLLER_NODE_NAME = "controller";
	//snmp
	public static final String OID_IP = "1.3.6.1.2.1.4.20.1.1"; //walk
	public static final String OID_mask = "1.3.6.1.2.1.4.20.1.3"; //walk
	public static final String OID_ethName = "1.3.6.1.2.1.2.2.1.2"; //walk lo/eth0/eth1
	public static final String OID_process = "1.3.6.1.2.1.25.4.2.1.2"; //walk
	public static final String OID_app = "1.3.6.1.2.1.25.6.3.1.2"; //walk
	public static final String OID_os = "1.3.6.1.2.1.1.5.0"; //get 获取机器名 返回ubuntu
	public static final String OID_portStatus = "1.3.6.1.2.1.2.2.1.8"; // walk 1是up。2是down
	public static final String OID_computeLoad="1.3.6.1.2.1.25.3.3.1.2";// walk 当前cpu负载50%，则返回50
	public static final String OID_totalStorage="1.3.6.1.2.1.25.2.2.0"; // get 总存储（仅返回数目，单位KB
	public static final String OID_usedStorage="1.3.6.1.2.1.25.2.3.1.6"; // walk 已使用存储（仅返回数字，我们取第一个值即可
}
