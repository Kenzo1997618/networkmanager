package cn.edu.uestc.platform.action;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.jcraft.jsch.JSchException;

import STKSoftwareController.STKUtil;
import cn.edu.uestc.platform.controller.ThreadController;
import cn.edu.uestc.platform.dao.ComplexNodeDao;
import cn.edu.uestc.platform.dao.ComplexNodeDaoImpl;
import cn.edu.uestc.platform.dao.PhyNodeDao;
import cn.edu.uestc.platform.dealwithstk.DynamicController;
import cn.edu.uestc.platform.dealwithstk.NodeCreater;
import cn.edu.uestc.platform.dealwithstk.STKAnalyse;
import cn.edu.uestc.platform.dealwithstk.STKFileProcessor;
import cn.edu.uestc.platform.dealwithstk.STKScenario;
import cn.edu.uestc.platform.pojo.ComplexNode;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.LinkForFilter;
import cn.edu.uestc.platform.pojo.LinkForInitialScenario;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.pojo.Project;
import cn.edu.uestc.platform.pojo.Scenario;
import cn.edu.uestc.platform.pojo.User;
import cn.edu.uestc.platform.service.ComplexNodeService;
import cn.edu.uestc.platform.service.L2LinkService;
import cn.edu.uestc.platform.service.LinkService;
import cn.edu.uestc.platform.service.NodeService;
import cn.edu.uestc.platform.service.PortService;
import cn.edu.uestc.platform.service.ProjectService;
import cn.edu.uestc.platform.service.ScenarioService;
import cn.edu.uestc.platform.service.UserService;
import cn.edu.uestc.platform.testzk.JSchUtil;
import cn.edu.uestc.platform.utils.JSoneUtils;
import cn.edu.uestc.platform.utils.NetworkUtils;
import cn.edu.uestc.platform.utils.SSHExecutor;
import cn.edu.uestc.platform.utils.SSHExecutorUtils;
import cn.edu.uestc.platform.utils.VNCUtils;

@Controller
public class ActionController {

	// @Autowired
	// NodeDao nodeDao;
	private static Logger logger = Logger.getLogger(ActionController.class);


	/*
	 * 返回所有router和switch
	 */
	@RequestMapping("/selectRouterAndSwitch")
	@ResponseBody
	public String selectRouterAndSwitch() {
		NodeService service = new NodeService();
		List<Node> nodes = service.getRouterAndSwitch();
		return JSoneUtils.ListToJson(nodes).toString();
	}






















	/*
	 * 根据场景返回所有节点
	 */
	@RequestMapping("/selectNodeList")
	@ResponseBody
	public String selectNodeList(int s_id) {
		NodeService service = new NodeService();
		List<Node> nodes = service.findAllNodeByScenarioId(s_id);
		return JSoneUtils.ListToJson(nodes).toString();
	}
	/*
	 * 用户注册
	 * 
	 */
	@ResponseBody
	@RequestMapping("/registerUser")
	public String userRegister(Model model, User user) {
		logger.info("[用户注册]   userName:" + user.getUsername() + " 注册时间: " + new Date());
		UserService userservice = new UserService();
		if (userservice.userRegister(user) == false) {
			System.out.println("用户名已经存在");
			model.addAttribute("message", "用户名已经存在");
			return "false";
		}
		logger.info("[用户注册]:" + user.getUsername() + "   注册成功，注册时间: " + new Date());
		model.addAttribute("message", "注册成功");
		return "success"; // 提示信息后 跳转到用户登录界面
	}

	/*
	 * 用户登陆
	 * 
	 */
	@ResponseBody
	@RequestMapping("/userCheck")
	public String userLogin(Model model, User user, HttpSession session) {
		logger.info("[用户登录]   userName:" + user.getUsername() + " 登录时间: " + new Date());
		UserService service = new UserService();
		System.out.println(service.userLogin(user));

		if (service.userLogin(user).getU_id() == 0) {
			System.out.println("用户未注册");
			model.addAttribute("message", "用户未注册");
			return "用户未注册";
		}
		if (!service.userLogin(user).getPsw().equals(user.getPsw())) {
			System.out.println("密码错误！");
			model.addAttribute("message", "密码错误");
			return "密码错误！";
		}
		session.setAttribute("user", service.userLogin(user));
		model.addAttribute("message", "登录成功！");
		return "登录成功";
	}

	/*
	 * 新建工程
	 * 
	 */
	@ResponseBody
	@RequestMapping("/creatProject")
	public String createProject(Model model, Project project, HttpSession session) {
		logger.info("[新建工程]   projectName:" + project.getProjectName() + " 操作时间: " + new Date());
		User user = (User) session.getAttribute("user");
		project.setUser_id(user.getU_id());// 将当前用户uid给要创建的工程
		ProjectService service = new ProjectService();
		if (service.createProject(project) == true) {
			logger.info("[新建工程]:新建工程成功！   projectName:" + project.getProjectName() + "，操作时间: " + new Date());
			return "创建成功!";
		} else {
			System.out.println("创建失败！");
			return "工程名已存在,请重新输入。";
		}

	}

	/*
	 * 查找当前用户下的所有工程
	 */
	@RequestMapping("/selectProjectList")
	@ResponseBody
	public String selectProjectList(Model model, HttpSession session) {
		ProjectService service = new ProjectService();
		User user = (User) session.getAttribute("user");
		List<Project> projects = service.findAllProjectByUserId(user);
		// System.out.println(JSoneUtils.ListToJson(projects).toString());
		return JSoneUtils.ListToJson(projects).toString();
	}

	/*
	 * 创建场景
	 */
	@RequestMapping("/createScenario")
	@ResponseBody
	public String createScenario(Model model, Scenario scenario, int p_id) {
		logger.info("[新建场景]   scenarioName:" + scenario.getScenarioName() + " 操作时间: " + new Date());
		ScenarioService service = new ScenarioService();
		// 现在假设能从前端拿到场景所属的工程id号。
		scenario.setProject_id(p_id);
		if (service.createScenario(scenario) == false) {
			return "场景名已经存在！";
		}
		return "创建成功！！";
	}

	/*
	 * 根据工程id 查找所有场景
	 */
	@RequestMapping("/selectScenarioList")
	@ResponseBody
	public String selectScenarioList(int p_id) {
		ScenarioService service = new ScenarioService();
		List<Scenario> scenarios = service.findAllScenarioByProjectId(p_id);
		return JSoneUtils.ListToJson(scenarios).toString();
	}

	/*
	 * 创建节点 需要底层启动虚拟机，数据库中不但需插入节点数据，相应的场景的节点总数+1,判断完是简单节点还是复杂节点之后对应类型数目+1
	 * 依赖注入有问题 Node不能直接接受前端发来的消息
	 */
	@RequestMapping("/addNode")
	@ResponseBody
	public String createNode(Node node) {
		logger.info("[新建节点] nodeName:" + node.getNodeName() + " 操作时间: " + new Date());
		NodeService service = new NodeService();
		boolean flag = service.createNode(node);
		if (flag == true) {
			System.out.println("返回成功");
			return "创建成功";
		}
		return "节点名重复！";
	}

	// 192.168.10.x and 192.168.6.x
	// @RequestMapping("/createProxyNode")
	// @ResponseBody
	// create proxynode need user add manageIP
	public String createProxyNode(Node node, String phyNodeIP) {
		NodeService service = new NodeService();
		boolean flag = service.createNode(node);
		PhyNodeDao phyNodeDao = new PhyNodeDao();
		phyNodeDao.insertNode(phyNodeIP, node.getN_id());
		if (flag == true) {
			System.out.println("返回成功");
			return "创建成功";
		}
		return "节点名重复！";
	}

	public String startAssociate(int s_id) {
		// get all phyNode and phyNode-kvm's links

		// enter into phyNode add router rule

		return null;
	}

	// @Test
	// public void dem(){
	// Node node = new Node();
	// node.setManageIp("192.168.10.12");
	// node.setNodeName("fusck");
	// node.setNodeType(1);
	// createProxyNode(node,"192.168.1.123");
	// }

	/*
	 * 根据节点id返回节点
	 */
	@RequestMapping("/getNodeByn_id")
	public String getNode(int n_id) {
		NodeService service = new NodeService();
		Node node = service.getNode(n_id);
		System.out.println(JSoneUtils.ObjToJson(node).toString());
		return JSoneUtils.ObjToJson(node).toString();
	}

	/*
	 * 根据节点名nodeName和场景s_id查找节点
	 */
	@RequestMapping("/getNodeBynodeName")
	@ResponseBody
	public String getNodeBynodeName(String nodeName, int s_id) {
		NodeService service = new NodeService();
		// System.out.println(s_id + "-----" + nodeName);
		Node node = service.getNodeBynodeName(nodeName, s_id);
		System.out.println(JSoneUtils.ObjToJson(node).toString());
		return JSoneUtils.ObjToJson(node).toString();
	}


	/*
	 * 编辑工程名，此处之后需要从前端传入u_id，以便用户长时间操作 造成session中无user，发生空指针异常。
	 */
	@RequestMapping("/editProject")
	@ResponseBody
	public String updateProjectName(Project project, HttpSession session) {
		logger.info("[编辑工程]  ProjectName:" + project.getProjectName() + "，	操作时间: " + new Date());
		ProjectService service = new ProjectService();
		User user = (User) session.getAttribute("user");
		project.setUser_id(user.getU_id());
		if (service.updataProjectName(project) == true) {
			return "修改工程名成功！";
		}
		return "工程名已经存在或未做任何修改！";
	}

	/*
	 * 编辑节点名
	 */

	@RequestMapping("/editNode")
	@ResponseBody
	public String editNode(Node node) {
		logger.info("[编辑节点]   ProjectName:" + node.getNodeName() + " 操作时间: " + new Date());
		NodeService service = new NodeService();
		boolean flag = service.editNode(node);
		if (flag == true) {
			return "修改成功！";
		}
		//return "修改失败,节点名重复或者未进行任何修改！";
		return "修改成功！";
	}

	/*
	 * 新建端口
	 */
	@RequestMapping("/addPort")
	@ResponseBody
	public String createPort(Port port) {
		logger.info("[新建端口]   PortName:" + port.getPortName() + " 操作时间: " + new Date());
		PortService service = new PortService();
		boolean flag = service.createPort(port);
		return "创建成功";
	}

	/*
	 * edit Port
	 */
	@RequestMapping("/editPort")
	@ResponseBody
	public String editPort(Port port) {
		PortService portService = new PortService();
		portService.editPort(port);
		return null;
	}

	/*
	 * edit Link
	 */
	public String editLink(Link link) {
		LinkService linkService = new LinkService();
		linkService.editLink(link);

		return null;
	}

	/*
	 * 获取端口列表（根据n_id）
	 */
	@RequestMapping("/getPortList")
	@ResponseBody
	public String getPortList(int n_id) {
		PortService service = new PortService();
		List<Port> ports = service.getPortList(n_id);
		return JSoneUtils.ListToJson(ports).toString();
	}

	/*
	 * get port info
	 */
	@RequestMapping("/getPort")
	@ResponseBody
	public String getPort(int pt_id) {
		PortService service = new PortService();
		Port port = service.getPort(pt_id);
		return JSoneUtils.ObjToJson(port).toString();
	}

	/*
	 * 获取端口列表(根据场景s_id和节点名称nodeName)
	 */
	@RequestMapping("/getPortBynodeName")
	@ResponseBody
	public String getPortListBynodeName(int s_id, String nodeName) {
		PortService service = new PortService();
		List<Port> ports = service.getPortListBynodeName(s_id, nodeName);
		System.out.println(JSoneUtils.ListToJson(ports).toString());
		return JSoneUtils.ListToJson(ports).toString();
	}

	/*
	 * 创建链路
	 */
	@RequestMapping("/addLink")
	@ResponseBody
	public String createLink(Link link, int onlyPort) throws Exception {
		logger.info("[新建链路]   linkName:" + link.getLinkName() + " 操作时间: " + new Date());
		LinkService linkService = new LinkService();
		System.out.println(link.getLogicalFromNodeName() + "---" + link.getLogicalToNodeName());
		boolean flag;
		if (link.getLinkType() == 5 || link.getLinkType() == 6 || link.getLinkType() == 7) {
			System.out.println("create l2 link");
			flag = linkService.createL2Link(link, onlyPort);
		} else if (link.getLinkType() == 11 || link.getLinkType() == 12 || link.getLinkType() == 13) {
			System.out.println("create docker l2 link");
			flag = linkService.createDockerL2Link(link, onlyPort);
		} else {
			flag = linkService.createLink(link);
		}
		if (flag == true) {
			return "创建成功";
		}
		return "创建失败！";
	}

	/*
	 * 返回对应场景下的所有链路
	 */

	@RequestMapping("/getLinkList")
	@ResponseBody
	public String getLinkList(int s_id) {
		LinkService service = new LinkService();
		List<Link> links = service.getLinkList(s_id);
		return JSoneUtils.ListToJson(links).toString();
	}

	/*
	 * 挂起链路
	 */
	@RequestMapping("/cutLink")
	@ResponseBody
	public String pauseLink(int scenario_id, String linkName, int delayTime) {
		logger.info("[挂起链路]   linkName:" + linkName + " 操作时间: " + new Date());
		ThreadController threadController = new ThreadController();
		threadController.run(linkName, delayTime);
		LinkService service = new LinkService();
		service.pauseLink(scenario_id, linkName);
		return "断开成功";
	}

	@RequestMapping("/cutL2Link")
	@ResponseBody
	public String pauseL2Link(int scenario_id, String linkName, int delayTime) {
		logger.info("[挂起L2链路]   linkName:" + linkName + " 操作时间: " + new Date());
		L2LinkService service = new L2LinkService();
		service.pauseLink(scenario_id, linkName);
		return "断开成功";
	}

	/*
	 * 恢复链路
	 */
	@RequestMapping("/connectLink")
	@ResponseBody
	public String recoveryLink(int scenario_id, String linkName) {
		logger.info("[恢复链路]   linkName:" + linkName + " 操作时间: " + new Date());
		LinkService service = new LinkService();
		service.recoveryLink(scenario_id, linkName);
		return "恢复成功";
	}

	@RequestMapping("/connectL2Link")
	@ResponseBody
	public String recoveryL2Link(int scenario_id, String linkName) {
		logger.info("[恢复L2链路]   linkName:" + linkName + " 操作时间: " + new Date());
		L2LinkService service = new L2LinkService();
		service.recoveryLink(scenario_id, linkName);
		return "恢复成功";
	}

	/*
	 * 挂起场景
	 */
	@RequestMapping("/keepScenario")
	@ResponseBody
	public String pauseScenario(int s_id) {
		logger.info("[挂起场景]   ScenarioID:" + s_id + " 操作时间: " + new Date());
		ScenarioService scenarioService = new ScenarioService();
		scenarioService.deleteScenariosOnlyOpenstack(s_id);
		return "挂起成功";
	}

	/*
	 * 恢复场景
	 */
	@RequestMapping("/recoverScenario")
	@ResponseBody
	public String recoveryScenario(int s_id) {
		logger.info("[恢复场景]   ScenarioID:" + s_id + " 操作时间: " + new Date());
		ScenarioService scenarioService = new ScenarioService();
		scenarioService.recoveryScenario(s_id);
		return "恢复成功";
	}

	/*
	 * 创建复杂节点
	 */
	@RequestMapping("/addComplexNode")
	@ResponseBody
	public String createComplexNode(ComplexNode complexNode) {
		logger.info("[创建复杂节点]   complexNodeID:" + complexNode.getCn_id() + " 操作时间: " + new Date());
		ComplexNodeService complexNodeService = new ComplexNodeService();
		boolean flag = complexNodeService.createComplexNode(complexNode);
		if (flag == true) {
			return "创建成功";
		}
		return "复杂节点名重复";
	}

	/*
	 * 查询复杂节点
	 */
	@RequestMapping("/selectComplexNodeList")
	@ResponseBody
	public String selectComplexNodeList(int s_id) {
		ComplexNodeService complexNodeService = new ComplexNodeService();
		List<ComplexNode> complexNodes = complexNodeService.selectComplexNodeList(s_id);
		return JSoneUtils.ListToJson(complexNodes).toString();
	}

	/*
	 * 根据s_id和complexNodeName查找节点
	 */
	@RequestMapping("/getComplexNodeBynodeName")
	@ResponseBody
	public String getComplexNodeBynodeName(int s_id, String complexNodeName) {
		ComplexNodeService complexNodeService = new ComplexNodeService();
		ComplexNode complexNode = complexNodeService.getCompleNode(s_id, complexNodeName);
		return JSoneUtils.ObjToJson(complexNode).toString();
	}

	/*
	 * 仅作为给Node的Cn_id字段赋值
	 */
	@RequestMapping("/addInnerNode")
	@ResponseBody
	public String addInnerNode(Node node, String complexNodeName) {
		logger.info("[创建内部节点]   nodeName:" + node.getNodeName() + " 操作时间: " + new Date());
		ComplexNodeDao complexNodeDao = new ComplexNodeDaoImpl();
		node.setCn_id(
				complexNodeDao.getComplexNodeBys_idAndComplexNodeName(node.getS_id(), complexNodeName).getCn_id());
		NodeService service = new NodeService();
		boolean flag = service.createNode(node);
		if (flag == true) {
			return "创建成功";
		}
		return "节点名重复！";
	}

	/*
	 * selectInnerNodeList 查询所有属于该复杂节点的节点
	 */
	@RequestMapping("/selectInnerNodeList")
	@ResponseBody
	public String selectInnerNodeList(int s_id, String complexNodeName) {
		NodeService nodeservice = new NodeService();
		List<Node> nodes = nodeservice.getInnerNodeList(s_id, complexNodeName);
		return JSoneUtils.ListToJson(nodes).toString();
	}

	@RequestMapping("/addInnerLink")
	@ResponseBody
	public String addInnerLink(Link link, String complexNodeName, int onlyPort) throws Exception {
		logger.info("[创建内部链路]   linkName:" + link.getLinkName() + " 操作时间: " + new Date());

		ComplexNodeDao complexNodeDao = new ComplexNodeDaoImpl();
		link.setCn_id(complexNodeDao.getComplexNodeBys_idAndComplexNodeName(link.getScenario_id(), complexNodeName)
				.getCn_id());
		LinkService linkService = new LinkService();

		boolean flag;
		if (link.getLinkType() == 5 || link.getLinkType() == 6 || link.getLinkType() == 7) {
			System.out.println("create l2 link");
			flag = linkService.createL2Link(link, onlyPort);
		} else {
			System.out.println("create l3 link");
			flag = linkService.createLink(link);
		}
		if (flag == true) {
			return "创建成功";
		}
		return "创建失败！";
		// boolean flag = linkService.createLink(link);
		// if (flag == true) {
		// return "创建成功";
		// }
		// return "创建失败！";
	}

	/*
	 * 获取复杂节点ID
	 */
	@RequestMapping("/getComplexNodeId")
	@ResponseBody
	public int getComplexNodeId(int s_id, String complexNodeName) {
		ComplexNodeService cplxNodeservice = new ComplexNodeService();
		System.out.println("拿到了cnid" + cplxNodeservice.getCompleNode(s_id, complexNodeName).getCn_id());
		return cplxNodeservice.getCompleNode(s_id, complexNodeName).getCn_id();
	}

	/*
	 * 获取内部链路
	 */
	@ResponseBody
	@RequestMapping("/getInnerLinkList")
	public String getInnerLink(int cn_id) {
		LinkService linkService = new LinkService();
		List<Link> links = linkService.getInnerLink(cn_id);
		return JSoneUtils.ListToJson(links).toString();
	}

	/*
	 * 打开控制台
	 */
	@RequestMapping("/openConsole")
	@ResponseBody
	public String OpenConsole(String nodeName, int s_id) {
		logger.info("[打开vm控制台]   nodeName:" + nodeName + " 操作时间: " + new Date());

		// 查出节点的UUID
		NodeService nodeService = new NodeService();
		Node node = nodeService.getNodeBynodeName(nodeName, s_id);
		// System.out.println(node.getUuid());
		// System.out.println(VNCUtils.getVNCURL(node.getUuid()));
		return VNCUtils.getVNCURL(node.getUuid());
		// return
		// "http://121.48.175.200:6080/vnc_auto.html?token=e94abc7c-82fd-43fe-8f90-5d93bbd09c6f&title=CMDTest(21b9cd22-a57f-432b-b3b7-3a812ff3e0fb)";
	}

	/*
	 * 删除复杂节点，删除所有链路，删除所有节点cn_id，删除数据库所有数据
	 */
	@RequestMapping("/deleteComplexNode")
	@ResponseBody
	public String deleteComplexNode(ComplexNode complexNode) {
		logger.info("[删除复杂节点]   ComplexNodeName:" + complexNode.getComplexNodeName() + " 操作时间: " + new Date());
		ComplexNodeService complexNodeService = new ComplexNodeService();
		complexNode = complexNodeService.getCompleNode(complexNode.getS_id(), complexNode.getComplexNodeName());
		boolean flag = complexNodeService.deleteComplexNodeService(complexNode);
		return "删除成功";
	}

	/*
	 * docker exec
	 */

	@RequestMapping("/sendCommand")
	@ResponseBody
	public String ExecuteCMD(String command, String uuid) throws Exception {
		JSchUtil jschUtil = new JSchUtil("compute2", "123456", "10.0.0.31", 22);
		String msg = "";
		System.out.println(uuid + "-----" + command);
		try {
			jschUtil.connect();
			msg = jschUtil.execCmd("sudo docker exec -i nova-" + uuid + " " + command);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	/*
	 * 采用spring提供的上传文件的方法
	 */
	@RequestMapping("/springUpload")
	@ResponseBody
	public String springUpload(HttpServletRequest request, int s_id) throws IllegalStateException, IOException {
		long startTime = System.currentTimeMillis();
		String path = "";
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				// 一次遍历所有文件
				MultipartFile file = multiRequest.getFile(iter.next().toString());
				if (file != null) {
					path = "/home/controller/" + file.getOriginalFilename();
					// 上传
					file.transferTo(new File(path));
				}
			}
		}
		long endTime = System.currentTimeMillis();
		ScenarioService scenarioService = new ScenarioService();
		scenarioService.addDynamicTopologyFile(path, s_id);
		System.out.println("方法三的运行时间：" + String.valueOf(endTime - startTime) + "ms");
		return "上传成功!";
	}

	/*
	 * 返回树状图所需的不重复链路
	 */
	@RequestMapping("/getStkLink")
	@ResponseBody
	public String getSTKLink(int s_id) throws IOException {
		STKAnalyse stkAnalyse = new STKAnalyse();
		System.out.println("返回了不重复链路");
		return JSoneUtils.SetToJson(stkAnalyse.getLinkForFilter(s_id)).toString();
	}

	/*
	 * 返回树状图中的大节点
	 */
	@RequestMapping("/getStkNode")
	@ResponseBody
	public String getStkNode(int s_id) throws IOException {
		STKAnalyse stkAnalyse = new STKAnalyse();
		System.out.println("返回了大类");
		return JSoneUtils.SetToJson(stkAnalyse.getBigNodeLink(s_id)).toString();

	}

	/*
	 * 接受规则
	 */
	@RequestMapping("/submitRegulation")
	@ResponseBody
	public String submitRegulation(String linkJson, int s_id, String regulationOption)
			throws IOException, InterruptedException {
		// 将JSon字符串转换为集合
		// List<LinkForFilter> linkForFilters =
		// JSoneUtils.JSonToCollection(linkJson);
		List<LinkForFilter> linkForFilters = JSoneUtils.JSonToCollection(linkJson, new LinkForFilter());

		List<LinkForFilter> filterRegulation = new LinkedList<>();
		// 拿到过滤掉的链路
		for (LinkForFilter l : linkForFilters) {
			if (l.getLinkStatus() == 1) {
				filterRegulation.add(l);
			}
		}
		ScenarioService scenarioService = new ScenarioService();
		String path = scenarioService.getDynamicTopologyFile(s_id);
		// 创建一个新文件来准备存储过滤之后的文件
		STKFileProcessor stkFileProcessor = new STKFileProcessor();
		String newPath = stkFileProcessor.STKFileCreater(path);
		// 开始生成过滤后的文件
		stkFileProcessor.STKFileRewrite(path, newPath, filterRegulation);
		Thread.sleep(1000);
		STKAnalyse stkAnalyse = new STKAnalyse();
		return stkAnalyse.getBigClassName(s_id);
	}

	@RequestMapping("/submitBigRules")
	@ResponseBody
	public String submitBigRules(Node node) {

		System.out.println(node);
		return "返回成功";
	}

	@RequestMapping("/setBigNodeAttr")
	public String setBigNodeAttr(String bigNodeConfigArray, int s_id) throws Exception {
		STKScenario stkScenario = new STKScenario();
		System.out.println(bigNodeConfigArray);
		@SuppressWarnings("unchecked")
		List<NodeCreater> nodeCreater = JSoneUtils.JSonToCollection(bigNodeConfigArray, new NodeCreater());
		long beforecreatetime = new Date().getTime();
		stkScenario.createSTKAllNode(nodeCreater, s_id);
		System.out.println("创建节点花费：" + (new Date().getTime() - beforecreatetime) + "ms");
		Thread.sleep(3000);
		try {
			stkScenario.createInitScenario(s_id);
		} catch (Exception e) {
			System.out.println(e);
		}
		stkScenario.createConnection(s_id);
		logger.info("初始拓扑建立完毕。\n 创建初始拓扑花费时间：" + (new Date().getTime() - beforecreatetime) / (1000 * 60) + "分");
		return "返回成功";
	}

	/**
	 * 开始仿真 1、从数据库里面拿出现在已经有的链路（状态为0的链路） 2、获取STK文件在分钟数为1时候的链路 3、看看stk在分数数为1时候的链路
	 * 跟之前的链路哪些增加了 哪些删除了
	 */
	@RequestMapping("/startSimulation")
	public void startSimulaiton(int s_id) throws IOException, InterruptedException {
		Integer maxTime = STKAnalyse.getMaxSimulationTime(s_id);
		int currentTime = 5;
		HashMap<String, Set<LinkForInitialScenario>> map = STKAnalyse.getInitZeroToFourMinFromSTK(s_id);
		STKUtil.start();
		while (currentTime < maxTime) {
			long beforeworkTime = new Date().getTime();
			logger.info("第" + (currentTime - 1) + "----" + currentTime + "min仿真开始。");
			// DynamicController.startSimulation(currentTime,s_id);
			DynamicController dynamicController = new DynamicController(s_id, currentTime);
			map = (HashMap<String, Set<LinkForInitialScenario>>) dynamicController.startSimulation(map);
			// dynamicController.startSimulation();
			// 剩余休眠时间
			currentTime++;
			Thread.sleep(1000 * 60 - (new Date().getTime() - beforeworkTime));
			logger.info("第" + (currentTime - 1) + "----" + currentTime + "min仿真结束,开始休眠....");
		}
		logger.info("仿真结束");
	}

	@RequestMapping("/createL2Link")
	public String createL2Link(Link link) {
		LinkService linkService = new LinkService();
		// linkService.createL2Link(link);
		return null;
	}

	@RequestMapping("/getL2LinkList")
	@ResponseBody
	public String getL2LinkList(int s_id) {
		// System.out.println("getL2LinkList");
		L2LinkService l2linkservice = new L2LinkService();
		// System.out.println(JSoneUtils.ListToJson(l2linkservice.getL2LinkListById(s_id)).toString());
		System.out.println(l2linkservice.getL2LinkListById(s_id));
		return JSoneUtils.ListToJson(l2linkservice.getL2LinkListById(s_id)).toString();

	}

	@RequestMapping("/updateNodeLocation")
	public void updateNodeLocation(int s_id, String nodeName, int x, int y) {
		NodeService nodeService = new NodeService();
		nodeService.updateLocation(s_id, nodeName, x, y);
	}

	@RequestMapping("/getL2LinkListByNodeName")
	@ResponseBody
	public String getL2NodePortList(int s_id, String nodeName) {
		L2LinkService l2linkservice = new L2LinkService();
		return JSoneUtils.ListToJson(l2linkservice.getL2NodePortList(s_id, nodeName)).toString();
	}

//	@RequestMapping("/editEth")
//	@ResponseBody
//	public String editEth(String maximumRate, String packetLoss, String ethName, String nodeName) throws Exception {
//		Port port = new Port();
//		port.setMaximumRate(maximumRate);
//		port.setPacketLoss(packetLoss);
//		L2LinkService l2linkservice = new L2LinkService();
//		String floatIP = NetworkUtils.getFloatIpByNodeName(nodeName);
//		// set l2 tc
//		l2linkservice.setTC(floatIP, ethName, port, "1111");
//		return "TC success";
//	}
}
