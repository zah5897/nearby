<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="UTF-8">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="renderer" content="webkit">
<title></title>
<link rel="stylesheet" href="<%=path%>/css/pintuer.css">
<link rel="stylesheet" href="<%=path%>/css/admin.css">
<script src="<%=path%>/js/jquery.js"></script>
<script src="<%=path%>/js/pintuer.js"></script>
<script type="text/javascript">
	    $(document).ready(function(){ 
	    	init();
		}); 
		
	    //前一页
		function init() {
			$.post("<%=path%>/manager/get_welcome","",function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	$("#welcome_label").html(json['welcome']);
			        }
		    });
		}
	    function modify(){
			var welcome=$("#welcome_input").val();
			$.post("<%=path%>/manager/set_welcome",{'welcome':welcome},function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	$("#welcome_label").html(json['welcome']);
			        }
		    });
	    }
	</script>
</head>
<body>
<div class="panel admin-panel margin-top">
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span>&nbsp;修改欢迎语</strong></div>
  <div class="body-content">
       <form method="post" class="form-x" action="">   
     <div class="form-group">
        <div class="label">
          <label>当前欢迎语：</label>
        </div>
        <div class="label" style="width: 30%;text-align:left">
          <span id="welcome_label" ></span>
        </div>
      </div>           
      <div class="form-group">
        <div class="label">
          <label>修改欢迎语为：</label>
        </div>
        <div class="field">
          <input id="welcome_input" type="text" class="input w50" name="title" value="" />
          <div class="tips"></div>
        </div>
      </div>        
       
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button id="modify_welome" class="button bg-main icon-check-square-o" onclick="modify()">保存</button>
        </div>
      </div>
      </form>
  </div>
</div>
</body></html>