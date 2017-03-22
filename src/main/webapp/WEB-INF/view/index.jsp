<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <meta name="renderer" content="webkit">
    <title>后台管理中心</title>  
    <link rel="stylesheet" href="<%=path %>/css/pintuer.css">
    <link rel="stylesheet" href="<%=path %>/css/admin.css">
    <script src="<%=path %>/js/jquery.js"></script>   
</head>
<body style="background-color:#f2f9fd;">
<div class="header bg-main">
  <div class="logo margin-big-left fadein-top">
    <h1><img src="<%=path %>/images/y.jpg" class="radius-circle rotate-hover" height="50" alt="" />后台管理中心</h1>
  </div>
  <div class="head-l"><a class="button button-little bg-green" href="<%=path %>/manager/" ><span class="icon-home"></span> 前台首页</a> <!--  &nbsp;&nbsp;<a href="##" class="button button-little bg-blue"><span class="icon-wrench"></span> 清除缓存</a>--> &nbsp;&nbsp;<a class="button button-little bg-red" href="<%=path %>/manager/logout"><span class="icon-power-off"></span> 退出登录</a> </div>
</div>
<div class="leftnav">
  <div class="leftnav-title"><strong><span class="icon-list"></span>菜单列表</strong></div>
  <h2><span class="icon-user"></span>基本设置</h2>
  <ul style="display:block">
    <li><a href="<%=path %>/manager/forword?path=dynamic_selected" target="right"><span class="icon-caret-right"></span>首页推荐</a></li>
    <li><a href="<%=path %>/manager/forword?path=dynamic_unselected" target="right"><span class="icon-caret-right"></span>用户动态</a></li>
    <li><a href="<%=path %>/manager/forword?path=topic_manager" target="right"><span class="icon-caret-right"></span>话题管理</a></li>
    <li><a href="<%=path %>/manager/forword?path=update_welcome" target="right"><span class="icon-caret-right"></span>系统设置</a></li>
    <!--  
    <li><a href="<%=path %>/manager/forword?path=info" target="right"><span class="icon-caret-right"></span>网站设置</a></li>
    <li><a href="<%=path %>/manager/forword?path=pass" target="right"><span class="icon-caret-right"></span>修改密码</a></li>
    <li><a href="<%=path %>/manager/forword?path=page" target="right"><span class="icon-caret-right"></span>单页管理</a></li>  
    <li><a href="<%=path %>/manager/forword?path=adv" target="right"><span class="icon-caret-right"></span>首页轮播</a></li>   
    <li><a href="<%=path %>/manager/forword?path=book" target="right"><span class="icon-caret-right"></span>留言管理</a></li>     
    <li><a href="<%=path %>/manager/forword?path=column" target="right"><span class="icon-caret-right"></span>栏目管理</a></li>
    -->
  </ul>   
  
  <!--
  <h2><span class="icon-pencil-square-o"></span>栏目管理</h2>
  <ul>
    <li><a href="<%=path %>/list.html" target="right"><span class="icon-caret-right"></span>内容管理</a></li>
    <li><a href="<%=path %>/add.html" target="right"><span class="icon-caret-right"></span>添加内容</a></li>
    <li><a href="<%=path %>/cate.html" target="right"><span class="icon-caret-right"></span>分类管理</a></li>        
  </ul>  
  -->
</div>
<script type="text/javascript">
$(function(){
  $(".leftnav h2").click(function(){
	  $(this).next().slideToggle(200);	
	  $(this).toggleClass("on"); 
  })
  $(".leftnav ul li a").click(function(){
	    $("#a_leader_txt").text($(this).text());
  		$(".leftnav ul li a").removeClass("on");
		$(this).addClass("on");
  })
});
</script>
<ul class="bread">
  <li><a href="<%=path %>/manager/forword?path=welcome" target="right"  class="icon-home"> 首页</a></li>
  <li><a href="##" id="a_leader_txt">网站信息</a></li>
</ul>
<div class="admin">
  <iframe scrolling="auto" rameborder="0" src="<%=path %>/manager/forword?path=welcome" name="right" width="100%" height="100%"></iframe>
</div>
</body>
</html>