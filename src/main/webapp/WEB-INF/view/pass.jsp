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
<div class="panel admin-panel">
  <div class="panel-head"><strong><span class="icon-key"></span> ä¿®æ¹ä¼åå¯ç </strong></div>
  <div class="body-content">
    <form method="post" class="form-x" action="">
      <div class="form-group">
        <div class="label">
          <label for="sitename">ç®¡çåå¸å·ï¼</label>
        </div>
        <div class="field">
          <label style="line-height:33px;">
           admin
          </label>
        </div>
      </div>      
      <div class="form-group">
        <div class="label">
          <label for="sitename">åå§å¯ç ï¼</label>
        </div>
        <div class="field">
          <input type="password" class="input w50" id="mpass" name="mpass" size="50" placeholder="è¯·è¾å¥åå§å¯ç " data-validate="required:è¯·è¾å¥åå§å¯ç " />       
        </div>
      </div>      
      <div class="form-group">
        <div class="label">
          <label for="sitename">æ°å¯ç ï¼</label>
        </div>
        <div class="field">
          <input type="password" class="input w50" name="newpass" size="50" placeholder="è¯·è¾å¥æ°å¯ç " data-validate="required:è¯·è¾å¥æ°å¯ç ,length#>=5:æ°å¯ç ä¸è½å°äº5ä½" />         
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label for="sitename">ç¡®è®¤æ°å¯ç ï¼</label>
        </div>
        <div class="field">
          <input type="password" class="input w50" name="renewpass" size="50" placeholder="è¯·åæ¬¡è¾å¥æ°å¯ç " data-validate="required:è¯·åæ¬¡è¾å¥æ°å¯ç ,repeat#newpass:ä¸¤æ¬¡è¾å¥çå¯ç ä¸ä¸è´" />          
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