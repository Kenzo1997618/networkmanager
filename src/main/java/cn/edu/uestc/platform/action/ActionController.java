package cn.edu.uestc.platform.action;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openstack4j.model.compute.VNCConsole;
import org.openstack4j.openstack.compute.domain.NovaVNCConsole;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.uestc.platform.controller.LinkController;
import cn.edu.uestc.platform.controller.ThreadController;
import cn.edu.uestc.platform.dao.ComplexNodeDao;
import cn.edu.uestc.platform.dao.ComplexNodeDaoImpl;
import cn.edu.uestc.platform.dao.LinkDao;
import cn.edu.uestc.platform.dao.LinkDaoImpl;
import cn.edu.uestc.platform.dao.NodeDaoImpl;
import cn.edu.uestc.platform.pojo.ComplexNode;
import cn.edu.uestc.platform.pojo.Link;
import cn.edu.uestc.platform.pojo.Node;
import cn.edu.uestc.platform.pojo.Port;
import cn.edu.uestc.platform.pojo.Project;
import cn.edu.uestc.platform.pojo.Scenario;
import cn.edu.uestc.platform.pojo.User;
import cn.edu.uestc.platform.service.ComplexNodeService;
import cn.edu.uestc.platform.service.LinkService;
import cn.edu.uestc.platform.service.NodeService;
import cn.edu.uestc.platform.service.PortService;
import cn.edu.uestc.platform.service.ProjectService;
import cn.edu.uestc.platform.service.ScenarioService;
import cn.edu.uestc.platform.service.UserService;
import cn.edu.uestc.platform.utils.JSoneUtils;
import cn.edu.uestc.platform.utils.VNCUtils;

@Controller
public class ActionController {
	private static Logger logger = Logger.getLogger(ActionController.class);

	/*
	 * 用户注册
	 * 
	 */
	@RequestMapping("/register.action")
	public String userRegister(Model model, User user) {
		logger.info("[用户注册]   userName:" + user.getUsername() + " 注册时间: " + new Date());
		UserService userservice = new UserService();
		if (userservice.userRegister(user) == false) {
			System.out.println("用户名已经存在");
			model.addAttribute("message", "用户名已经存在");
			return "##跳转到错误信息提示页面";
		}
		logger.info("[用户注册]:" + user.getUsername() + "   注册成功，注册时间: " + new Date());
		model.addAttribute("message", "注册成功");
		return "/login"; // 提示信息后 跳转到用户登录界面

	}

	/*
	 * 用户登陆
	 * 
	 */
	@RequestMapping("/login.action")
	public String userLogin(Model model, User user, HttpSession session) {
		logger.info("[用户登录]   userName:" + user.getUsername() + " 登录时间: " + new Date());
		UserService service = new UserService();
		// System.out.println(service.userLogin(user));

		if (service.userLogin(user).getU_id() == 0) {
			System.out.println("用户未注册");
			model.addAttribute("message", "用户未注册");
			return "register";
		}
		if (!service.userLogin(user).getPsw().equals(user.getPsw())) {
			System.out.println("密码错误！");
			model.addAttribute("message", "密码错误");
			return "login";
		}
		session.setAttribute("user", service.userLogin(user));
		model.addAttribute("message", "登录成功！");
		return "detailProject";
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
		logger.info("[新建节点]   nodeName:" + node.getNodeName() + " 操作时间: " + new Date());
		NodeService service = new NodeService();
		System.out.println(node.getCn_id());
		boolean flag = service.createNode(node);
		if (flag == true) {
			System.out.println("返回成功");
			return "创建成功";
		}
		return "节点名重复！";
	}

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
		return "修改失败,节点名重复或者未进行任何修改！";
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
	public String createLink(Link link) {
		logger.info("[新建链路]   linkName:" + link.getLinkName() + " 操作时间: " + new Date());
		LinkService linkService = new LinkService();
		System.out.println(link.getLogicalFromNodeName() + "---" + link.getLogicalToNodeName());
		boolean flag = linkService.createLink(link);
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
		System.out.println(node.getS_id() + "   " + complexNodeName);
		ComplexNodeDao complexNodeDao = new ComplexNodeDaoImpl();
		node.setCn_id(
				complexNodeDao.getComplexNodeBys_idAndComplexNodeName(node.getS_id(), complexNodeName).getCn_id());
		NodeService service = new NodeService();
		System.out.println(node.getCn_id());
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
	public String addInnerLink(Link link, String complexNodeName) {
		ComplexNodeDao complexNodeDao = new ComplexNodeDaoImpl();
		link.setCn_id(complexNodeDao.getComplexNodeBys_idAndComplexNodeName(link.getScenario_id(), complexNodeName)
				.getCn_id());
		LinkService linkService = new LinkService();
		boolean flag = linkService.createLink(link);
		if (flag == true) {
			return "创建成功";
		}
		return "创建失败！";
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
	public String OpenConsole(String nodeName, int s_id) {
		// 查出节点的UUID
		System.out.println("执行了VNC");
		System.out.println(nodeName + s_id);
		NodeService nodeService = new NodeService();
		Node node = nodeService.getNodeBynodeName(nodeName, s_id);
		System.out.println(node);
		return VNCUtils.getVNCURL(node.getUuid());
	}

	/*
	 * 删除复杂节点，删除所有链路，删除所有节点cn_id，删除数据库所有数据
	 */
	@RequestMapping("/deleteComplexNode")
	@ResponseBody
	public String deleteComplexNode(ComplexNode complexNode) {
		ComplexNodeService complexNodeService = new ComplexNodeService();
		boolean flag = complexNodeService.deleteComplexNodeService(complexNode);
		return "删除成功";
	}
	
	@Test
	public void demo1(){
		ComplexNode complexNode = new ComplexNode();
		complexNode.setCn_id(8);
		complexNode.setComplexNodeName("complexnode1");
		deleteComplexNode(complexNode);
	}
	
}
