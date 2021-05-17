package cn.edu.uestc.platform.service;

// 修改过的文件：Node,Constants,Port,Link,NodeDao,NodeDaoImpl,PortDaoImpl,LinkDaoImpl
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.edu.uestc.platform.dao.*;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.utils.Constants;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 演示： GET单个OID值
 *
 * blog http://www.micmiu.com
 *
 * @author Michael
 */
public class SnmpService {

    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final int DEFAULT_PORT = 161;
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    public static final int DEFAULT_RETRY = 3;

    /**
     * 创建对象communityTarget，用于返回target
     *
     * @param ip
     * @param community
     * @return CommunityTarget
     */
    public static CommunityTarget createDefault(String ip, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip
                + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
        target.setRetries(DEFAULT_RETRY);
        return target;
    }
    /*根据OID，获取单条消息*/
    public static String snmpGet(String ip, String community, String oid) {

        CommunityTarget target = createDefault(ip, community);
        Snmp snmp = null;
        try {
            PDU pdu = new PDU();
            // pdu.add(new VariableBinding(new OID(new int[]
            // {1,3,6,1,2,1,1,2})));
            pdu.add(new VariableBinding(new OID(oid)));

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            //System.out.println("-------> 发送PDU <-------");
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            //System.out.println("PeerAddress:" + respEvent.getPeerAddress());
            PDU response = respEvent.getResponse();

            if (response == null) {
                //System.out.println("response is null, request time out");
                return null;
            } else {
                // Vector<VariableBinding> vbVect =
                // response.getVariableBindings();
                // System.out.println("vb size:" + vbVect.size());
                // if (vbVect.size() == 0) {
                // System.out.println("response vb size is 0 ");
                // } else {
                // VariableBinding vb = vbVect.firstElement();
                // System.out.println(vb.getOid() + " = " + vb.getVariable());
                // }
                //System.out.println("response pdu size is " + response.size());
                for (int i = 0; i < response.size(); i++) {
                    VariableBinding vb = response.get(i);
                    //System.out.println(vb.getOid() + " = " + vb.getVariable());
                    return vb.getVariable().toString();
                }

            }
            //System.out.println("SNMP GET one OID value finished !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Get Exception:" + e);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }

        }
        return null;
    }

    /*根据OID列表，采用异步方式一次获取多条OID数据，并且以List形式返回*/
    public static void snmpAsynGetList(String ip, String community,List<String> oidList)
    {
        CommunityTarget target = createDefault(ip, community);
        Snmp snmp = null;
        try {
            PDU pdu = new PDU();

            for(String oid:oidList)
            {
                pdu.add(new VariableBinding(new OID(oid)));
            }

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            //System.out.println("-------> 发送PDU <-------");
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            //System.out.println("PeerAddress:" + respEvent.getPeerAddress());
            PDU response = respEvent.getResponse();

            /*异步获取*/
            final CountDownLatch latch = new CountDownLatch(1);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    PDU request = event.getRequest();
                    //System.out.println("[request]:" + request);
                    if (response == null) {
                        System.out.println("[ERROR]: response is null");
                    } else if (response.getErrorStatus() != 0) {
                        System.out.println("[ERROR]: response status"
                                + response.getErrorStatus() + " Text:"
                                + response.getErrorStatusText());
                    } else {
                        //System.out.println("Received response Success!");
                        for (int i = 0; i < response.size(); i++) {
                            VariableBinding vb = response.get(i);
                            //System.out.println(vb.getOid() + " = "
                            //        + vb.getVariable());
                        }
                        //System.out.println("SNMP Asyn GetList OID finished. ");
                        latch.countDown();
                    }
                }
            };

            pdu.setType(PDU.GET);
            snmp.send(pdu, target, null, listener);
            //System.out.println("asyn send pdu wait for response...");

            boolean wait = latch.await(30, TimeUnit.SECONDS);
            //System.out.println("latch.await =:" + wait);

            snmp.close();

            //System.out.println("SNMP GET one OID value finished !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Get Exception:" + e);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }

        }
    }
    /*根据targetOID，获取树形数据*/
    public static List<String> snmpWalk(String ip, String community, String targetOid)
    {
        CommunityTarget target = createDefault(ip, community);
        TransportMapping transport = null;
        Snmp snmp = null;
        List<String> list = new ArrayList();
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            PDU pdu = new PDU();
            OID targetOID = new OID(targetOid);
            pdu.add(new VariableBinding(targetOID));

            boolean finished = false;
            //System.out.println("----> demo start <----");
            while (!finished) {
                VariableBinding vb = null;
                ResponseEvent respEvent = snmp.getNext(pdu, target);

                PDU response = respEvent.getResponse();

                if (null == response) {
                    System.out.println("responsePDU == null");
                    list = null; // 无response，就返回null
                    finished = true;
                    break;
                } else {
                    vb = response.get(0);
                }
                // check finish
                finished = checkWalkFinished(targetOID, pdu, vb);
                if (!finished) {
                    //System.out.println("==== walk each vlaue :");
                    //System.out.println(vb.getOid() + " = " + vb.getVariable());

                    // 2021.3.23
                    list.add(vb.getVariable().toString());


                    // Set up the variable binding for the next entry.
                    pdu.setRequestID(new Integer32(0));
                    pdu.set(0, vb);
                } else {
                    //System.out.println("SNMP walk OID has finished.");
                    snmp.close();
                }
            }
            //System.out.println("----> demo end <----");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP walk Exception: " + e);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
        // 2021.3.23
        return list;
    }

    private static boolean checkWalkFinished(OID targetOID, PDU pdu,
                                             VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            //System.out.println("[true] responsePDU.getErrorStatus() != 0 ");
            System.out.println(pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            //System.out.println("[true] vb.getOid() == null");
            finished = true;
        } else if (vb.getOid().size() < targetOID.size()) {
            //System.out.println("[true] vb.getOid().size() < targetOID.size()");
            finished = true;
        } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
            //System.out.println("[true] targetOID.leftMostCompare() != 0");
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            //System.out
            //        .println("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            finished = true;
        } else if (vb.getOid().compareTo(targetOID) <= 0) {
            //System.out.println("[true] Variable received is not "
            //        + "lexicographic successor of requested " + "one:");
            //System.out.println(vb.toString() + " <= " + targetOID);
            finished = true;
        }
        return finished;

    }
    /*根据targetOID，异步获取树形数据*/
    public static void snmpAsynWalk(String ip, String community, String oid)
    {
        final CommunityTarget target = createDefault(ip, community);
        Snmp snmp = null;
        try {
            System.out.println("----> demo start <----");

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            final PDU pdu = new PDU();
            final OID targetOID = new OID(oid);
            final CountDownLatch latch = new CountDownLatch(1);
            pdu.add(new VariableBinding(targetOID));

            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);

                    try {
                        PDU response = event.getResponse();
                        // PDU request = event.getRequest();
                        // System.out.println("[request]:" + request);
                        if (response == null) {
                            System.out.println("[ERROR]: response is null");
                        } else if (response.getErrorStatus() != 0) {
                            System.out.println("[ERROR]: response status"
                                    + response.getErrorStatus() + " Text:"
                                    + response.getErrorStatusText());
                        } else {
                            System.out
                                    .println("Received Walk response value :");
                            VariableBinding vb = response.get(0);

                            boolean finished = checkWalkFinished(targetOID,
                                    pdu, vb);
                            if (!finished) {
                                System.out.println(vb.getOid() + " = "
                                        + vb.getVariable());
                                pdu.setRequestID(new Integer32(0));
                                pdu.set(0, vb);
                                ((Snmp) event.getSource()).getNext(pdu, target,
                                        null, this);
                            } else {
                                System.out
                                        .println("SNMP Asyn walk OID value success !");
                                latch.countDown();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        latch.countDown();
                    }

                }
            };

            snmp.getNext(pdu, target, null, listener);
            System.out.println("pdu 已发送,等到异步处理结果...");

            boolean wait = latch.await(30, TimeUnit.SECONDS);
            System.out.println("latch.await =:" + wait);
            snmp.close();

            System.out.println("----> demo end <----");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Asyn Walk Exception:" + e);
        }
    }

    // 第一次遍历表
    // 返回两个List<String> 第一个list中是要遍历的router的管理IP，第二个list中是要遍历的server的管理IP
    public List<List<String>> infoCollect(String fromIP , String toIP){
        String[] from = fromIP.split("\\.");
        String[] to = toIP.split("\\.");
        int a = Integer.parseInt(from[3]);
        int b = Integer.parseInt(to[3]);
        String pre = from[0] + "." + from[1] + "." +from[2] + "." ;
        String community = "public";
        NodeDao nodeDao = new NodeDaoImpl();
        PortDao portDao = new PortDaoImpl();
        LinkDao linkDao = new LinkDaoImpl();


        int n_id = 0;
        int n_id_l2 = 0;
        int pt_id = 0;
        int link_id = 0;

        List<String> serverManageIPList = new ArrayList<>();
        List<String> routerManageIPList = new ArrayList<>();
        List<List<String>>  pollingList = new ArrayList<>();

        // 用网段做key，value记录该网段每个网口的信息
        // key : 除去127.0.0.1和manageIP的IP地址的网段 192.168.101
        // value : portName , pt_id , nodeName , nodeIP , nodePortNum
        Map<String , List<String[]>> linkMap = new HashMap<>();

        // 获取每个节点信息并写入DB
        for (int i = a; i <= b; i++) {
            n_id++;
            Node node = new Node();
            node.setN_id(n_id);
            String manageIP = pre + i;
            node.setManageIp(manageIP);
            // 获取IP信息
            // list_allIP：所有ip
            List<String> list_allIP = snmpWalk(manageIP, community, Constants.OID_IP);
            // list_IP:去掉127.0.0.1和manageIP剩下的ip
            List<String> list_IP = new ArrayList<String>();


            // ----------------------------------------写L3Node表-------------------------------------------
            System.out.println("----------------------写L3 Node表-----------------------");
            System.out.println("n_id:" + n_id);
            // nodeStatus
            // 如果获取到信息，则开机
            int nodeStatus = 1;

            if (list_allIP==null || list_allIP.size()==0){
                // 关机 或 没有该节点
                node.setNodeStatus(nodeStatus);
                System.out.println("节点宕机");
                continue; // 直接跳出
            }else {
                nodeStatus = 0;
                node.setNodeStatus(nodeStatus);
                // 开机
            }

            List<String> portAllStatus = snmpWalk(manageIP, community, Constants.OID_portStatus);//获取端口状态
            List<String> portStatus = new ArrayList<>();
            // 删除本地环回IP和网管IP--->list_IP,及他们的状态 ----> portStatus
            for (int j = 0; j< list_allIP.size() ; j++) {
                if ( (!list_allIP.get(j).equals("127.0.0.1")) && (!list_allIP.get(j).equals(manageIP))) {
                    list_IP.add(list_allIP.get(j));
                    portStatus.add(portAllStatus.get(j));
                }
            }

            // nodeName 根据网口数量判断节点类型,然后命名
            List<String> serverType = snmpWalk(manageIP, community, Constants.OID_app);
            //nodeName 金凯
            String nodeName = null;
            //nodeType 金凯  （共两位，我写了第二位
            // 1路由 2终端 3FTP 4VLC 5LinPhone
            int nodeType = 0;



            // ip数目大于1，为router          ---【需要修改】---
            if (list_IP.size()>1){
                //nodeName = "router" + routerNum;
                nodeType = 1;
                // routerManageIPAndNodeID 管理IP+节点ID（192.168.31.24.5 前面是ip，5是id)
                String routerManageIPAndNodeID = manageIP + "." + n_id;
                routerManageIPList.add(routerManageIPAndNodeID);
                System.out.println("nodeName=" + nodeName);
            }else {
                // 叶子节点的话，判断是否为服务器及服务类型
                for (String s : serverType) {
                    if (s.contains("vsftpd")){
                        nodeType = 3;
                        serverManageIPList.add(manageIP);
                        break;
                        // 写入DB
                    }else if (s.contains("vlc")){
                        nodeType = 4;
                        serverManageIPList.add(manageIP);
                        break;
                        // DB
                    }else if (s.contains("linphone")){
                        nodeType = 5;
                        serverManageIPList.add(manageIP);
                        break;
                        // DB
                    }else {
                        nodeType = 2;
                        // 只是普通终端，写入DB
                    }
                }
            }


            // manageIP
            System.out.println("manageIP=" + manageIP);


            // nodeType   无法分辨是物理/vm/docker
            System.out.println("nodeType=" + nodeType);

            //hwArch 金凯
            int hwArch = 0;

            //numberPort 除去网管网口的网口数目
            int numberPort = list_IP.size();
            System.out.println("numberPort=" + numberPort);

            // serviceStatus 服务端口号  金凯
            String serviceStatus = null;


            // nodeStatus
            System.out.println("nodeStatus=" + nodeStatus + "(开机0,关机1)");

            // computeLoad
            List<String> computeLoad = snmpWalk(manageIP, community, Constants.OID_computeLoad);
            int comLoad_tem = Integer.parseInt(computeLoad.get(0));
            float comLoad = (float) comLoad_tem/100;
            System.out.println("computeLoad=" + comLoad);

            // storageStatus
            String totalStorage = snmpGet(manageIP, community, Constants.OID_totalStorage);
            List<String> usedStorageList = snmpWalk(manageIP, community, Constants.OID_usedStorage);
            int total = Integer.parseInt(totalStorage);
            int used = Integer.parseInt(usedStorageList.get(0));
            float storageStatus = (float)used/total;
            System.out.println("storageStatus=" + storageStatus);

            // iconUrl 需要的话再写

            node.setNodeType(nodeType);
            node.setHardwareArchitecture(hwArch);
            node.setNumberPort(numberPort);
            node.setNodeStatus(nodeStatus);
            node.setServiceStatus(serviceStatus);
            node.setComputeLoad(comLoad);
            node.setStorageStatus(storageStatus);
            //DB
            nodeDao.insertL3Node(node);



            // --------------------------------------------写Port表----------------------------------------------
            System.out.println("----------------------写Port表(L3)-----------------------");


            for (int j = 0; j < list_IP.size(); j++) {
                Port port = new Port();
                //pt_id 自增，null
                pt_id++;
                System.out.println("pt_id=" + pt_id);

                //portName
                String portName = nodeName+"port"+(j+1);
                System.out.println("portName=" + portName);

                // nodeClass 二层0 三层1(这里能遍历到的节点，都是L3节点
                int nodeClass = 1;
                System.out.println("nodeClass=" + nodeClass);

                // nodeID    --->  n_id
                System.out.println("node_id=" + n_id);

                // nodeType

                // portType 金凯
                int portType = 0;


                // portStatus
                String ps = portStatus.get(j); // 1 up 2 down
                System.out.println("portStatus=" + ps + "(up 1 , down 2)");
                // portIP
                String portIP = list_IP.get(j);
                System.out.println("portIP=" + portIP);

                //maximumRate 金凯
                String maximumRate = null;

                //packetLoss 金凯
                float packetLoss = 0;

                port.setPt_id(pt_id);
                port.setPortName(portName);
                port.setNodeClass(nodeClass);
                port.setN_id(n_id);
                //这里不写nodeType，因为不满足第三范式
                port.setPortType(portType);
                port.setPortStatus(Integer.parseInt(ps));
                port.setMaximumRate(maximumRate);
                port.setPacketLoss(packetLoss); //原本类型String，现在类型float
                port.setPortIp(portIP);
                //DB
                portDao.insertPort(port);




                String[] portIP_split = portIP.split("\\.");
                String netSegment = portIP_split[0] + "." + portIP_split[1] + "." + portIP_split[2];
                // key : 除去127.0.0.1和manageIP的IP地址的网段
                // value : portName , pt_id , nodeName , nodeIP , nodePortNum
                if (linkMap.containsKey(netSegment)){
                    String[] strings = new String[5];
                    strings[0] = portName;
                    strings[1] = Integer.toString(pt_id);
                    strings[2] = nodeName;
                    strings[3] = portIP;
                    strings[4] = Integer.toString(list_IP.size());
                    linkMap.get(netSegment).add(strings);
                }else {
                    String[] strings = new String[5];
                    strings[0] = portName;
                    strings[1] = Integer.toString(pt_id);
                    strings[2] = nodeName;
                    strings[3] = portIP;
                    strings[4] = Integer.toString(list_IP.size());
                    List<String[]> list = new ArrayList<>();
                    list.add(strings);
                    linkMap.put(netSegment,list);
                }
            }
        }
        int numOfL3Ports = pt_id;
        // --------------------------------------------写L2Node表----------------------------------------------
        System.out.println("----------------------写L2 Node表-----------------------");
        for (String key : linkMap.keySet()){
            List<String[]> ports = linkMap.get(key);
            // 当某个网段上的网口数量大于等于200，说明存在交换机
            if (ports.size()>=200){
                n_id_l2++;
                Node node = new Node();
                System.out.println("n_id=" + n_id_l2);
                // 二层节点名n_name  金凯
                String n_name = "switch" + n_id_l2;
                System.out.println("nodeName = " + n_name);
                int nodeType = 0;
                System.out.println("nodeType = " + nodeType);
                String subnetIP = key;
                System.out.println("subnetIP = " + subnetIP);
                int numberPort = ports.size();
                System.out.println("numberPort=" + numberPort);
                int nodeStatus = 0; //0代表ON
                System.out.println("nodeStatus = " + nodeStatus);
                node.setN_id(n_id_l2);
                node.setNodeName(n_name);
                node.setNodeType(nodeType);
                node.setSubnetIP(subnetIP);
                node.setNumberPort(numberPort);
                node.setNodeStatus(nodeStatus);
                //DB
                nodeDao.insertL2Node(node);


                System.out.println("----------------------写Port表(L2)-----------------------");
                // 一个L2节点，只写了一个网口
                pt_id++;
                Port port = new Port();
                System.out.println("pt_id=" + pt_id);
                String portName = n_name + pt_id;
                System.out.println("portName = " + portName);
                int nodeClass = 0;
                System.out.println("nodeClass = " + nodeClass);
                int nodeID = n_id_l2;
                System.out.println("nodeID = " + nodeID);
                //portType 金凯
                int portType = 0;
                int portStatus = 1;
                System.out.println("portStatus = " + portStatus);
                //maximumRate 金凯
                String maximumRate = null;
                //packetLoss 金凯
                float packetLoss = 0;
                String portIP = key;
                port.setPt_id(pt_id);
                port.setPortName(portName);
                port.setNodeClass(nodeClass);
                port.setN_id(nodeID);
                port.setPortType(portType);
                port.setPortStatus(portStatus);
                port.setMaximumRate(maximumRate);
                port.setPacketLoss(packetLoss);
                port.setPortIp(portIP);
                //DB
                portDao.insertPort(port);

//                // key : 除去127.0.0.1和manageIP的IP地址的网段
//                // value : portName , pt_id , nodeName , nodeIP , nodePortNum
//                String[] strings = new String[5];
//                strings[0] = portName;
//                strings[1] = Integer.toString(pt_id);
//                strings[2] = n_name;
//                strings[3] = portIP;
//                strings[4] = Integer.toString(201);
//                linkMap.get(key).add(strings);
            }
        }

        // --------------------------------------------写link表----------------------------------------------
        System.out.println("----------------------写link表-----------------------");
        // key : 除去127.0.0.1和manageIP的IP地址的网段
        // value : portName , pt_id , nodeName , nodeIP
        int l2nodeID = 0;
        for (String key : linkMap.keySet()){
            List<String[]> ports = linkMap.get(key);
            // 假设一个网段只有两个网口
            if (ports.size()==2){
                // 双向链路，写两条
                link_id++;
                Link link = new Link();
                String[] strs1 = ports.get(0);
                String[] strs2 = ports.get(1);
//                String linkName = strs1[0] + strs2[0];
//                System.out.println("linkName=" + linkName);
                int txPortID = Integer.parseInt(strs1[1]);
                int rxPortID = Integer.parseInt(strs2[1]);
                System.out.println("linkID = " + link_id);
                // linkType 金凯
                int linkType = 0;
                // linkStatus 金凯
                int linkStatus = 0;
                // linkDelay 金凯
                int linkDelay = 0;
                System.out.println("linkType = " + linkType);
                int linkClass = 0;
                System.out.println("linkClass = " + linkClass);
                System.out.println("txPortID=" + txPortID + "  rxPortID=" + rxPortID);
                System.out.println("fromNodeName=" + strs1[2] + "  toNodeName=" + strs2[2]);
                System.out.println("fromNodeIP=" + strs1[3] + "  toNodeIP=" + strs2[3]);
                link.setL_id(link_id);
                link.setLinkType(linkType);
                link.setLinkClass(linkClass);
                link.setLinkStatus(linkStatus);
                link.setLinkDelay(linkDelay);
                link.setTxPort_id(txPortID);
                link.setRxPort_id(rxPortID);
                link.setFromNodeName(strs1[2]);
                link.setToNodeName(strs2[2]);
                link.setFromNodeIP(strs1[3]);
                link.setToNodeIP(strs2[3]);
                //  DB
                linkDao.insertLink(link);

                link_id++;
                System.out.println("linkID = " + link_id);
                System.out.println("linkType = " + linkType);
                System.out.println("linkClass = " + linkClass);
                System.out.println("txPortID=" + rxPortID + "  rxPortID=" + txPortID);
                System.out.println("fromNodeName=" + strs2[2] + "  toNodeName=" + strs1[2]);
                System.out.println("fromNodeIP=" + strs2[3] + "  toNodeIP=" + strs1[3]);
                link.setL_id(link_id);
                link.setLinkType(linkType);
                link.setLinkClass(linkClass);
                link.setLinkStatus(linkStatus);
                link.setLinkDelay(linkDelay);
                link.setTxPort_id(rxPortID);
                link.setRxPort_id(txPortID);
                link.setFromNodeName(strs2[2]);
                link.setToNodeName(strs1[2]);
                link.setFromNodeIP(strs2[3]);
                link.setToNodeIP(strs1[3]);
                //  DB
                linkDao.insertLink(link);
            }
            if (ports.size()>=200){
                l2nodeID++;
                for (String[] strs : ports) {
                    // 双向链路， 写两条
                    link_id++;
                    Link link = new Link();
                    System.out.println("linkID = " + link_id);
                    // 怎么获取linkType？
                    int linkType = 0;
                    System.out.println("linkType = " + linkType);
                    int linkClass = 1; // 1:L2->L3
                    System.out.println("linkClass = " + linkClass);
                    // linkStatus linkDelay 金凯
                    int linkStatus = 0;
                    int linkDelay = 0;
                    int txPortID = l2nodeID + numOfL3Ports;
                    System.out.print("txPortID=" + txPortID);
                    int rxPortID = Integer.parseInt(strs[1]);
                    System.out.println("  rxPortID=" + rxPortID);
                    String fromNodeName = "switch" + l2nodeID;
                    System.out.print("fromNodeName=" + fromNodeName);
                    String toNodeName = strs[2];
                    System.out.println("  toNodeName=" + toNodeName);
                    String fromNodeIP = null;
                    System.out.print("fromNodeIP=" + fromNodeIP);
                    String toNodeIP = strs[3];
                    System.out.println("  toNodeIP=" + toNodeIP);
                    link.setL_id(link_id);
                    link.setLinkType(linkType);
                    link.setLinkClass(linkClass);
                    link.setLinkStatus(linkStatus);
                    link.setLinkDelay(linkDelay);
                    link.setTxPort_id(txPortID);
                    link.setRxPort_id(rxPortID);
                    link.setFromNodeName(fromNodeName);
                    link.setToNodeName(toNodeName);
                    link.setFromNodeIP(fromNodeIP);
                    link.setToNodeIP(toNodeIP);
                    // DB
                    linkDao.insertLink(link);

                    link_id++;
                    System.out.println("linkID = " + link_id);
                    System.out.println("linkType = " + linkType);
                    linkClass = 2;  // 2:L3->L2
                    System.out.println("linkClass = " + linkClass);
                    System.out.print("txPortID=" + rxPortID);
                    System.out.println("  rxPortID=" + txPortID);
                    System.out.print("fromNodeName=" + toNodeName);
                    System.out.println("  toNodeName=" + fromNodeName);
                    System.out.print("fromNodeIP=" + toNodeIP);
                    System.out.println("  toNodeIP=" + fromNodeIP);
                    link.setL_id(link_id);
                    link.setLinkType(linkType);
                    link.setLinkClass(linkClass);
                    link.setLinkStatus(linkStatus);
                    link.setLinkDelay(linkDelay);
                    link.setTxPort_id(rxPortID);
                    link.setRxPort_id(txPortID);
                    link.setFromNodeName(toNodeName);
                    link.setToNodeName(fromNodeName);
                    link.setFromNodeIP(toNodeIP);
                    link.setToNodeIP(fromNodeIP);
                    //  DB
                    linkDao.insertLink(link);
                }
            }
        }
        pollingList.add(routerManageIPList);
        pollingList.add(serverManageIPList);
        return pollingList;
    }


    public void pollingInfo(List<List<String>> pollingList){
        List<String> routerManageIPList = pollingList.get(0);
        List<String> serverManageIPList = pollingList.get(1);
        String community = "public";
        NodeDao nodeDao = new NodeDaoImpl();
        PortDao portDao = new PortDaoImpl();
        // 金凯添加轮询链路属性和服务状态信息
        for (String manageIPAndNodeID : routerManageIPList) {
            // 轮询路由器的网口状态信息
            String[] strs = manageIPAndNodeID.split(".");
            String manageIP = strs[0] + "." + strs[1] + "." + strs[2] + "." + strs[3];
            int nodeID = Integer.parseInt(strs[4]);
            List<String> portStatus = snmpWalk(manageIP, community, Constants.OID_portStatus);
            List<Port> portList = portDao.getPortList(nodeID);
            for (int i = 0; i < portStatus.size(); i++) {
                int ps = Integer.parseInt(portStatus.get(i));
                if (ps!=portList.get(i).getPortStatus()){
                    /*
                     * 网口信息变化，向孪生发送信息
                     */
                    if (ps==1) portDao.updateThePortStatusTo1(portList.get(i).getPt_id());
                    if (ps==2) portDao.updateThePortStatusTo2(portList.get(i).getPt_id());
                }
            }
        }
        for (String manageIP : serverManageIPList) {
            // 轮询服务器的节点状态信息
            //随便获取一个信息，如果获取不到就说明节点关机
            String response = snmpGet(manageIP, community, Constants.OID_os);
            if (response==null){

                int nodeStatus = nodeDao.getNodeStatusByManageIP(manageIP);
                if (nodeStatus==0){
                    /*
                     * 节点断开，给孪生网络发送信息
                     */
                    nodeDao.updateNodeStatus(1,manageIP);//0 ON  1 Down
                }
            }else {
                // 如果能够查询到信息，就将查询到的信息与数据库中信息进行比对
                int nodeStatus = nodeDao.getNodeStatusByManageIP(manageIP);
                if (nodeStatus==1){
                    /*
                     * 节点恢复，给孪生网络发送信息
                     */
                    nodeDao.updateNodeStatus(0,manageIP);
                }
            }
        }
    }
    private int k =1;
    public static void main(String[] args) {
        SnmpService s = new SnmpService();
        s.k = 1;
    }
}


