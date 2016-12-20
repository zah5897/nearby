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
<title></title>
<link rel="stylesheet" href="css/pintuer.css">
<link rel="stylesheet" href="css/admin.css">
<script src="js/jquery.js"></script>
<script src="js/pintuer.js"></script>
</head>
<body>
<div class="panel admin-panel margin-top">
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span>ä¿®æ¹åç±»</strong></div>
  <div class="body-content">
    <form method="post" class="form-x" action="">        
      <div class="form-group">
        <div class="label">
          <label>åç±»æ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="title" value="" />
          <div class="tips"></div>
        </div>
      </div>        
      <div class="form-group">
        <div class="label">
          <label>å³é®å­æ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_title" value=""/>         
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>åç±»å³é®å­ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_keywords" value=""/>         
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>å³é®å­æè¿°ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_desc" value=""/>        
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>æåºï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="sort" value="1"  data-validate="number:æåºå¿é¡»ä¸ºæ°å­" />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button class="button bg-main icon-check-square-o" type="submit"> æäº¤</button>
        </div>
      </div>
    </form>
  </div>
</div>
</body></html>