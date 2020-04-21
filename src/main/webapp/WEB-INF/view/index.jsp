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
   
  </ul>   
  
   <h2><span class="icon-user"></span>用户管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=user_all" target="right"><span class="icon-caret-right"></span>所有用户</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_new" target="right"><span class="icon-caret-right"></span>新增用户</a></li>
    
    <li><a href="<%=path %>/manager/forword?path=avatar_confirm" target="right"><span class="icon-caret-right"></span>变动头像审核</a></li>
    <li><a href="<%=path %>/manager/forword?path=avatar_checked" target="right"><span class="icon-caret-right"></span>变动头像已审核通过</a></li>
    <li><a href="<%=path %>/manager/forword?path=avatar_re_confirm" target="right"><span class="icon-caret-right"></span>变动头像人工复审</a></li>
    <li><a href="<%=path %>/manager/forword?path=avatar_search" target="right"><span class="icon-caret-right"></span>用户头像搜索</a></li>
    
    <li><a href="<%=path %>/manager/forword?path=user_found_list" target="right"><span class="icon-caret-right"></span>发现用户列表</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_found_black_list" target="right"><span class="icon-caret-right"></span>黑名单用户</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_meet_bottle_recommend" target="right"><span class="icon-caret-right"></span>邂逅瓶推荐用户</a></li>
  </ul>  
  
  
   <h2><span class="icon-user"></span>评论管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=dynamic_comment_check" target="right"><span class="icon-caret-right"></span>动态评论</a></li>
  </ul> 
  
   <h2><span class="icon-user"></span>礼物</h2>
   <ul>
     <li><a href="<%=path %>/manager/forword?path=gift_history" target="right"><span class="icon-caret-right"></span>礼物清单</a></li>
     <li><a href="<%=path %>/manager/forword?path=gift_manager" target="right"><span class="icon-caret-right"></span>礼物管理</a></li>
    </ul> 
  
  
   <h2><span class="icon-user"></span>会员管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=vip_manager" target="right"><span class="icon-caret-right"></span>会员类型</a></li>
  </ul>
  
  <!-- 
   <h2><span class="icon-user"></span>约会管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=appointment_check" target="right"><span class="icon-caret-right"></span>约会审核</a></li>
  </ul>  
   <h2><span class="icon-user"></span>短视频管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=shortvideo_check" target="right"><span class="icon-caret-right"></span>短视频审核</a></li>
    <li><a href="<%=path %>/manager/forword?path=user_shortvideo_cert" target="right"><span class="icon-caret-right"></span>短视频认证</a></li>
   </ul>  
   <h2><span class="icon-user"></span>金币管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=rule_manager" target="right"><span class="icon-caret-right"></span>金币购买规则项</a></li>
  </ul>  
   <h2><span class="icon-user"></span>提现管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=exchange_manager" target="right"><span class="icon-caret-right"></span>提现审批</a></li>
  </ul> 
  --> 
   <h2><span class="icon-user"></span>举报管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=report_manager" target="right"><span class="icon-caret-right"></span>举报列表</a></li>
  </ul> 
  
  
  <h2><span class="icon-user"></span>瓶子管理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=bottle_manager" target="right"><span class="icon-caret-right"></span>瓶子列表</a></li>
    <li><a href="<%=path %>/manager/forword?path=filter_txt_key_word" target="right"><span class="icon-caret-right"></span>敏感词管理</a></li>
  </ul> 
  
  <!--  
    <h2><span class="icon-user"></span>推广</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=user_spread_list" target="right"><span class="icon-caret-right"></span>推广用户</a></li>
  </ul> 
  -->
  
  
    <h2><span class="icon-user"></span>人工处理</h2>
   <ul>
    <li><a href="<%=path %>/manager/forword?path=charge_vip" target="right"><span class="icon-caret-right"></span>充值会员</a></li>
    <!--<li><a href="<%=path %>/manager/forword?path=charge_coin" target="right"><span class="icon-caret-right"></span>充值扇贝</a></li>-->
    <li><a href="<%=path %>/manager/forword?path=change_pwd" target="right"><span class="icon-caret-right"></span>修改管理员密码</a></li>
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
     var parentdiv=$('<img height="600"></img>');        //创建一个父div
     parentdiv.attr('src',alt);        //给父div设置id
 	 $(parentdiv).dialog({
 		title : "",
 	 });
}


function dateFtt(fmt,date)   
{ //author: meizz   
  var o = {   
    "M+" : date.getMonth()+1,                 //月份   
    "d+" : date.getDate(),                    //日   
    "h+" : date.getHours(),                   //小时   
    "m+" : date.getMinutes(),                 //分   
    "s+" : date.getSeconds(),                 //秒   
    "q+" : Math.floor((date.getMonth()+3)/3),  //季度   
    "S"  : date.getMilliseconds()           //毫秒  
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
} 

function showUserInfo(avatar){
    var alt=avatar;
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




function toast(msg){
    setTimeout(function(){
        document.getElementsByClassName('toast-wrap')[0].getElementsByClassName('toast-msg')[0].innerHTML=msg;
        var toastTag = document.getElementsByClassName('toast-wrap')[0];
        toastTag.className = toastTag.className.replace('toastAnimate','');
        setTimeout(function(){
            toastTag.className = toastTag.className + ' toastAnimate';
        }, 100);
    },500);
  }


function getDeviceTxt(from){
	 var fromTxt;
	 if(from==1){
		 fromTxt="IOS"; 
	 }else if(from==2){
		 fromTxt="AND"; 
	 }else{
		 fromTxt="Old"
	 }
	return fromTxt;
}


</script>
<ul class="bread">
  <li><a href="<%=path %>/manager/forword?path=welcome" target="right"  class="icon-home"> 首页</a></li>
  <li><a href="##" id="a_leader_txt">网站信息</a></li>
</ul>
<div class="admin">
  <iframe scrolling="auto" rameborder="0" src="<%=path %>/manager/forword?path=welcome" name="right" width="100%" height="100%"></iframe>
  
  <div class="toast-wrap">
            <span class="toast-msg"></span>
  </div>
  
</div>
</body>
</html>