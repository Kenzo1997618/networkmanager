/*定义三级分类数据*/
//一级分类
var province = [];
var projectId = [];
//二级分类
var city = [];
var scenarioId = [];
//三级分类
var node = [];
var nodeId = [];

//测试用工程对象列表的json
//var json = '[{"p_id":1,"projectName":"wohaoniubi","projectStatus":0,"scenarios":[],"user_id":1},{"p_id":2,"projectName":"worinidaye","projectStatus":0,"scenarios":[],"user_id":1},{"p_id":3,"projectName":"worinimdaye","projectStatus":0,"scenarios":[],"user_id":1},{"p_id":4,"projectName":"模式数","projectStatus":0,"scenarios":[],"user_id":1},{"p_id":5,"projectName":"牛逼","projectStatus":0,"scenarios":[],"user_id":1},{"p_id":7,"projectName":"我日你大爷","projectStatus":0,"scenarios":[],"user_id":1}]';

//解析工程对象列表的json，初始化到province里
function praseProjectList(data) {
    province = [];
    projectId = [];
    var objs = jQuery.parseJSON(data);
    //alert(objs[0].projectName);
    for (var i = 0; i < objs.length; i++){
        province[i] = objs[i].projectName;
        projectId[i] = objs[i].p_id;
    }
    intProvince();
}

//解析场景列表的json，初始化
function praseScenarioList(data) {
    city = [];
    scenarioId = [];
    var objs = jQuery.parseJSON(data);
    for (var i = 0; i < objs.length; i++){
        city[i] = objs[i].scenarioName;
        scenarioId[i] = objs[i].s_id;
    }
}

//解析节点列表json
function praseNodeList(data) {
    node = [];
    nodeId = [];
    var objs = jQuery.parseJSON(data);
    for (var i = 0; i < objs.length; i++){
        node[i] = objs[i].nodeName;
        nodeId[i] = objs[i].n_id;
    }
}

//预读
$(document).ready(function(){
    //测试用
    //praseProjectList();
    //发送ajax查询工程列表并显示
    $.ajax({
        url: '/NetworkSimulation/selectProjectList',
        data: {

        },
        type: 'post',
        dataType: 'json',
        async: false,
        success: function (data) {
            //alert(data);
            praseProjectList(data);
        },
        error: function () {

        }
    });
});

var expressP, expressC, expressD, expressArea, areaCont;
var arrow = " <font>&gt;</font> ";

/*初始化一级目录*/
function intProvince() {
	areaCont = "";
	for (var i = 0; i < province.length; i++) {
		areaCont += '<li onClick="selectP(' + i + ');"><a href="javascript:void(0)">' + province[i] + '</a></li>';
	}
	$("#sort1").html(areaCont);
}

/*选择一级目录*/
function selectP(p) {
    //发送ajax查询场景列表
    $.ajax({
        url: '/NetworkSimulation/selectScenarioList',
        data: {
            p_id : projectId[p]
        },
        type: 'post',
        dataType: 'json',
        async: false,
        success: function (data) {
          //  alert(data);
            praseScenarioList(data);
        },
        error: function () {

        }
    });
    //显示出场景列表
	areaCont = "";
	for (var i = 0; i < city.length; i++) {
		areaCont += '<li onClick="selectC(' + i + ');"><a href="javascript:void(0)">' + city[i] + '</a></li>';
	}
	$("#sort2").html(areaCont).show();
	$("#sort3").hide();
	$("#sort1 li").eq(p).addClass("active").siblings("li").removeClass("active");
	expressP = province[p];
	$("#selectedSort").html(expressP);
	$("#releaseBtn").removeAttr("disabled");
	//打开工程编辑器
	document.getElementById("releaseBtn").onclick =  function () {
        //location.href="projectEdit.html";
        window.open(encodeURI("projectEdit.html?projectId=" + projectId[p] + "&projectName=" + expressP));
    };
	//删除工程
	$("#delete").click(function () {
        $.ajax({
            url: '/NetworkSimulation/deleteProject',
            data: {
                projectId : projectId[p]
            },
            type: 'post',
            dataType: 'json',
            async: false,
            success: function (msg) {
                alert(msg);
                //刷新页面
                window.location.reload();
            },
            error: function () {

            }
        });
    });
}

/*选择二级目录*/
function selectC(p) {
    //发送ajax查询节点列表
    $.ajax({
        url: '/NetworkSimulation/selectNodeList',
        data: {
            s_id : scenarioId[p]
        },
        type: 'post',
        dataType: 'json',
        async: false,
        success: function (data) {
          //  alert(data);
            praseNodeList(data);
        },
        error: function () {

        }
    });
    //显示出节点列表
	areaCont = "";
	expressC = "";
	for (var i = 0; i < node.length; i++) {
		areaCont += '<li onClick="selectD(' + i + ');"><a href="javascript:void(0)">' + node[i] + '</a></li>';
	}
	$("#sort3").html(areaCont).show();
	$("#sort2 li").eq(p).addClass("active").siblings("li").removeClass("active");
	expressC = city[p];
	$("#selectedSort").html(expressC);
	//打开场景编辑器
    document.getElementById("releaseBtn").onclick =  function () {
        //location.href="index3.html";
        window.open(encodeURI("index3.html?scenarioId=" + scenarioId[p] + "&scenarioName=" + city[p] + "&projectName=" + expressP));
    };
    //删除场景
    $("#delete").click(function () {
        $.ajax({
            url: '/NetworkSimulation/deleteScenario',
            data: {
                scenarioId : scenarioId[p]
            },
            type: 'post',
            dataType: 'json',
            async: false,
            success: function (msg) {
                alert(msg);
                //刷新页面
                window.location.reload();
            },
            error: function () {

            }
        });
    });
}

/*选择三级目录*/
function selectD(p) {
	$("#sort3 li").eq(p).addClass("active").siblings("li").removeClass("active");
	expressD = node[p];
	$("#selectedSort").html(expressD);
	//打开节点编辑器
    document.getElementById("releaseBtn").onclick =  function () {
        window.open(encodeURI("nodeEdit.html?nodeId=" + nodeId[p]));
    };
    //删除节点
    $("#delete").click(function () {
        $.ajax({
            url: '/NetworkSimulation/deleteNode',
            data: {
                nodeId : nodeId[p]
            },
            type: 'post',
            dataType: 'json',
            async: false,
            success: function (msg) {
                alert(msg);
                //刷新页面
                window.location.reload();
            },
            error: function () {

            }
        });
    });
}

/*编辑工程*/
$("#releaseBtn").click(function() {
	var releaseS = $(this).prop("disabled");
	if (releaseS == false) {//未被禁用
		//location.href = "商品发布-详细信息.html";//跳转到下一页
	}
});

//新建工程
$("#add").click(function () {
    $("#myModal").modal();
});

//新建工程提交
$("#addProject").click(function () {
    $.ajax({
        url: '/NetworkSimulation/creatProject',
        data: {
            projectName : $("#projectName").val()
        },
        type: 'get',
        dataType: 'json',
        async: false,
        success: function (msg) {
            alert(msg);
            //刷新页面
            $("#myModal").hide();
            window.location.reload();
        },
        error: function () {

        }
    });
});