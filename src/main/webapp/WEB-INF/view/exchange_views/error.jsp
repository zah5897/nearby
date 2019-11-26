<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
%>
<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<title>漂流瓶提现</title>
<style>
body, input, button {
	font: normal 14px "Microsoft Yahei";
	margin: 0;
	padding: 0
}

.odform-tit {
	font-weight: normal;
	font-size: 25px;
	color: #595757;
	line-height: 40px;
	text-align: center;
	border-bottom: 1px solid #c9cacb;
	margin: 0;
	padding: 5% 0
}

.odform-tit img {
	height: 40px;
	vertical-align: middle;
	margin-right: 15px
}

.odform {
	padding: 5%
}

.input-group {
	margin-bottom: 5%;
	position: relative
}

.input-group label {
	padding: 2% 0;
	position: absolute;
	color: #595757
}

.input-group input {
	margin-left: 5em;
	padding: 3% 5%;
	box-sizing: border-box;
	background: #efeff0;
	border: 0;
	border-radius: 5px;
	color: #595757;
	width: 75%
}

.odform button {
	background: #8ec31f;
	color: #fff;
	text-align: center;
	border: 0;
	border-radius: 10px;
	padding: 3%;
	width: 100%;
	font-size: 16px
}

.odform .cal {
	background-image: url(images/daetixian-cal.png);
	background-repeat: no-repeat;
	background-position: 95% center;
	background-size: auto 50%
}

.odform .xl {
	background-image: url(images/daetixian-xl.png);
	background-repeat: no-repeat;
	background-position: 95% center;
	background-size: auto 20%
}
h1,h2,h3{
text-align:center;
color:red;
}
</style>
</head>

<body>
	<h1 class="odform-tit">
		漂流瓶提现-错误
	</h1>
	<div class="odform">
	     <h3>异常：${msg}</h3> 
	</div>

	<div
		style="text-align: center; margin: 50px 0; font: normal 14px/24px 'MicroSoft YaHei';">
		<p>漂流瓶官方邮箱：<a href="mailto:2448742182@qq.com">2448742182@qq.com</a></p>
		<p>客户经理QQ：<a href="http://wpa.qq.com/msgrd?v=3&uin=2448742182&site=qq&menu=yes">2448742182</a></p>
	</div>
</body>
</html>