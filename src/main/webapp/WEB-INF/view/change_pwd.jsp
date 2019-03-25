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
<title></title>
<link rel="stylesheet" href="<%=path%>/css/pintuer.css">
<link rel="stylesheet" href="<%=path%>/css/admin.css">
<script src="<%=path%>/js/jquery.js"></script>
<script src="<%=path%>/js/pintuer.js"></script>
</head>
<body>
	<div class="panel admin-panel"></div>
	<script type="text/javascript">
$(document).ready(function(){ 
	loadWordList();
}); 

function modify(){
	
	  
	var r=confirm("确定修改管理员密码吗？");
	if(!r){
	  return;
	}
	var form = new FormData($("form")[0]);  
	$.ajax({
		async: false,
        url:"<%=path%>/manager/modify_pwd",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
        	var json=JSON.parse(data);
        	 var code=json['code'];
        	 
        	 if(code==0){
        		 alert("修改成功"); 
        	 }else if(code==1){
        		 alert("管理员没登陆，请刷新页面重新登陆"); 
        	 }else{
        		 alert(json['msg']);
        	 }
        },
        error:function(error){
        	alert("修改失败");
        }
    });
}
	 
	</script>
	<div class="panel admin-panel margin-top" id="add">
		<div class="panel-head">
			<strong><span class="icon-pencil-square-o"></span>修改管理员密码</strong>
		</div>
		<div class="body-content">
			<a href="#edit"></a>
			<form method="post" name="form" class="form-x">
				<div class="form-group">
				
				<div class="label">
                      <label>旧密码：</label>
                </div>
				
					<div class="field">
						<input type="text" id="old_pwd" name="old_pwd" class="input tips"
							style="width: 15%; float: left;" value="" />
					</div>
				</div>
				 <div class="form-group">
				
				<div class="label">
                      <label>新密码：</label>
                </div>
				
					<div class="field">
						<input type="text" id="new_pwd" name="new_pwd" class="input tips"
							style="width: 25%; float: left;" value="" />
					</div>
				</div>
				 <div class="form-group">
				
				<div class="label">
                      <label>确认新密码：</label>
                </div>
				
					<div class="field">
						<input type="text" id="confirm_pwd" name="confirm_pwd" class="input tips"
							style="width: 25%; float: left;" value="" />
					</div>
				</div>
				
				<input type="hidden" value="1" name="type">
				<div class="form-group">
               <div class="label">
                      <label></label>
                </div>
					<div class="field">
						&nbsp;&nbsp;&nbsp;<button class="button bg-main icon-check-square-o"
							id="vip_add_update_btn" onclick='modify()'>修改</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>