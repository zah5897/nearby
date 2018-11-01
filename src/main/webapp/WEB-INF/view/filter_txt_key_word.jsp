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

function editWord(){
	var form = new FormData($("form")[0]);  
	$.ajax({
		async: false,
        url:"<%=path%>/manager/filter_key_word",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
        	var json=JSON.parse(data);
        	 alert("保存成功");
        },
        error:function(error){
        	alert("保存失败");
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
			<strong><span class="icon-pencil-square-o"></span>瓶子敏感詞</strong>
		</div>
		<div class="body-content">
			<a href="#edit"></a>
			<form method="post" name="form" class="form-x"
				enctype="multipart/form-data">
				<div class="form-group">
					<div class="field">
						<input type="file" id="url1" name="keyword" class="input tips"
							style="width: 25%; float: left;" value="" />
					</div>
				</div>
				 <div class="form-group">
					<div class="field">
							&nbsp;&nbsp;&nbsp;<a href="#" id="download">下载敏感词</a>
					</div>
				</div>
				<input type="hidden" value="1" name="type">
				<div class="form-group">

					<div class="field">
						&nbsp;&nbsp;&nbsp;<button class="button bg-main icon-check-square-o"
							id="vip_add_update_btn" onclick='editWord()'>保存</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>