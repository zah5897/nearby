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

function confirmCharge(){
	
	  
	var r=confirm("确定提交吗？");
	if(!r){
	  return;
	}
	var form = new FormData($("form")[0]);  
	$.ajax({
		async: false,
        url:"<%=path%>/manager/charge_vip",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
        	var json=JSON.parse(data);
            var code=json['code'];
        	
        	if(code==0){
        		alert("充值成功");
        	}else{
        		alert(json['msg']);
        	}
        },
        error:function(error){
        	alert("充值失败");
        }
    });
}
	function loadWordList(){
		$.post('<%=path%>/manager/filter_key_word', {'type' : 0}, function(data, status) {
				var json = JSON.parse(data);
				if (json.code == 0) {
					var path=json["download_path"];
					if(path){
						$("#download").show();
						$('#download').attr('href',path); 
					}else{
						$("#download").hide();
						$("#vip_add_update_btn").show();
					}
				}
			});
		}
	 
	</script>
	<div class="panel admin-panel margin-top" id="add">
		<div class="panel-head">
			<strong><span class="icon-pencil-square-o"></span>人工处理vip充值</strong>
		</div>
		<div class="body-content">
			<a href="#edit"></a>
			<form method="post" name="form" class="form-x">
				<div class="form-group">
				
				<div class="label">
                      <label>用户ID：</label>
                </div>
				
					<div class="field">
						<input type="text" id="user_id" name="user_id" class="input tips"
							style="width: 25%; float: left;" value="" />
					</div>
				</div>
				 <div class="form-group">
				 
				 <div class="label">
                      <label>会员月数</label>
                </div>
					<div class="field">
						<select name="month">
						   <option value="1">1个月</option>
						   <option value="3">3个月</option>
						   <option value="6">6个月</option>
						   <option value="12">12个月</option>
						</select> 
					</div>
				</div>
				
				
				 <div class="form-group">
				 
				 <div class="label">
                      <label>备注</label>
                </div>
					<div class="field">
						<input type="text" id="mark" name="mark" class="input tips"
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
							id="vip_add_update_btn" onclick='confirmCharge()'>提交充值</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>