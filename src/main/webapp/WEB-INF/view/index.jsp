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
    <link rel="stylesheet" href="<%=path%>/css/jquery.dialog.css">
    <script src="<%=path%>/js/jquery.js"></script>
    <script src="<%=path%>/js/jquery.dialog.js"></script>
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
  <ul>
    <li><a href="<%=path %>/manager/forword?path=dynamic_selected" target="right"><span class="icon-caret-right"></span>首页推荐动态</a></li>
    <li><a href="<%=path %>/manager/forword?path=dynamic_unselected" target="right"><span class="icon-caret-right"></span>非首页动态(已过审)</a></li>
    <li><a href="<%=path %>/manager/forword?path=dynamic_check" target="right"><span class="icon-caret-right"></span>待审核动态</a></li>
    <li><a href="<%=path %>/manager/forword?path=dynamic_illegal" target="right"><span class="icon-caret-right"></span>违规动态</a></li>
    <li><a href="<%=path %>/manager/forword?path=topic_manager" target="right"><span class="icon-caret-right"></span>话题管理</a></li>
   
    <li><a href="<%=path %>/manager/forword?path=gift_manager" target="right"><span class="icon-caret-right"></span>礼物管理</a></li>
   
  </ul>   
  
   <h2><span class="icon-user"></span>用户管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=user_all" target="right"><span class="icon-caret-right"></span>所有用户</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_new" target="right"><span class="icon-caret-right"></span>新增用户</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_found_list" target="right"><span class="icon-caret-right"></span>发现用户列表</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_found_black_list" target="right"><span class="icon-caret-right"></span>黑名单用户</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_meet_bottle_recommend" target="right"><span class="icon-caret-right"></span>邂逅瓶推荐用户</a></li>
  </ul>  
  
  
   <h2><span class="icon-user"></span>会员管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=vip_manager" target="right"><span class="icon-caret-right"></span>会员类型</a></li>
  </ul>   
   <h2><span class="icon-user"></span>金币管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=rule_manager" target="right"><span class="icon-caret-right"></span>金币购买规则项</a></li>
  </ul>   
   <h2><span class="icon-user"></span>提现管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=exchange_manager" target="right"><span class="icon-caret-right"></span>提现审批</a></li>
  </ul> 
  
   <h2><span class="icon-user"></span>举报管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=report_manager" target="right"><span class="icon-caret-right"></span>举报列表</a></li>
  </ul> 
  
  
  <h2><span class="icon-user"></span>瓶子管理（池部分）</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=bottle_manager" target="right"><span class="icon-caret-right"></span>瓶子列表</a></li>
  </ul> 
  
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


function showOriginImg(img){
	 var alt=$(img).attr("alt");
     var parentdiv=$('<img></img>');        //创建一个父div
     parentdiv.attr('src',alt);        //给父div设置id
 	 $(parentdiv).dialog({
 		title : "",
 	 });
}

function showAuth(img){
	 var alt=$(img).attr("alt");
    var parentdiv=$('<img></img>');        //创建一个父div
    parentdiv.attr('src',alt);        //给父div设置id
	 $(parentdiv).dialog({
		title : "",
	 });
}



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