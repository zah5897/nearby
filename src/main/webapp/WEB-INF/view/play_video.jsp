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
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="renderer" content="webkit">
<title>播放器</title>
    <style type="text/css">
      html, body {width:100%;height:100%;margin:auto;overflow: hidden;}
      body {display:flex;}
      #mse {flex:auto;}
    </style>
    <script type="text/javascript">
      window.addEventListener('resize',function(){document.getElementById('mse').style.height=window.innerHeight+'px';});
    </script>
  </head>
  <body>
   <video id="mse" src="${url }" controls ="true"></video>
  </body>
</html>