<%@ page language="java" contentType="text/html; charset=utf-8" 
pageEncoding="utf-8"%> 
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    <title>项目详细信息</title>
    <link rel="stylesheet" href="lib/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="css/detailProject.css">
</head>
<body>
<!--头部区域-->
<header>
    <nav class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="javascript:;">天地一体化网络仿真测试平台</a>
            </div>
            <div>
                <!--向右对齐-->
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="register.html"  target="_blank"><span class="glyphicon glyphicon-user"></span> 注册</a></li>
                    <li><a href="login.html"  target="_blank"><span class="glyphicon glyphicon-log-in"></span> 登录</a></li>
                </ul>
            </div>
        </div>
    </nav>
</header>

<div class="contains">
    <div class="row clearfix">
        <!--面包屑导航-->
        <div class="crumbNav">
            <a href="#">首页</a>
            <font>&gt;</font>
            选择
        </div>
        <label class="col-sm-4 control-label">工程列表</label>
        <label class="col-sm-4 control-label">场景列表</label>
        <label class="col-sm-4 control-label">对象列表</label>
    </div>
    <div class="row clearfix">
        <div class="wareSort clearfix">
            <ul id="sort1"></ul>
            <ul id="sort2" style="display: none;"></ul>
            <ul id="sort3" style="display: none;"></ul>
        </div>
        <div class="selectedSort"><b>您当前选择的类别是：</b><i id="selectedSort"></i></div>
        <div class="wareSortBtn">
            <input id="releaseBtn" type="button" class="btn btn-default" value="进入编辑器" disabled="disabled" />
            <input id="add" type="button" class="btn btn-default" value="新建工程"/>
            <input id="delete" type="button" class="btn btn-danger" value="删除工程"/>
        </div>
    </div>
</div>

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">新建工程</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">工程名</label>
                        <div class="col-sm-10">
                            <input type="email" class="form-control" placeholder="请输入工程名" id="projectName">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="addProject" type="button" class="btn btn-primary">提交</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<!--<script type="text/javascript" src="lib/jquery/jquerySession.js"></script>-->
<script type="text/javascript" src="lib/jquery/jquery.js"></script>
<script type="text/javascript" src="lib/bootstrap/js/bootstrap.js"></script>
<script type="text/javascript" src="js/detailProject.js"></script>

</body>
<footer>
    <div class="container">
        <div class="row">
            <p align="center">Copyright &copy; 中电科54所 & 电子科技大学</p>
        </div>
    </div>
</footer><!--页脚结束-->
</html>