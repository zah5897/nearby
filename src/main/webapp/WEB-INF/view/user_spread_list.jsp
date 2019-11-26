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
<link rel="stylesheet" href="<%=path %>/css/pintuer.css">
<link rel="stylesheet" href="<%=path %>/css/admin.css">
<script src="<%=path %>/js/jquery.js"></script>
<script src="<%=path %>/js/pintuer.js"></script>
</head>
<body>
<div class="panel admin-panel">
  <div class="panel-head"><strong class="icon-reorder">&nbsp;推广用户</strong></div>
   
  <table class="table table-hover text-center">
    <tr>
      <th width="5%">ID</th>
      <th width="10%">名称</th>
      <th width="40%">头像</th>
      <th width="45%">操作</th>
    </tr>
  </table>
</div>
<script type="text/javascript">
$(document).ready(function(){ 
	loadSpreadList();
}); 

function add_spread_user(){
	
	var uid=$("#uid").val();
	
	if(isNaN(uid)){
		alert("请输入正确的用户ID");
		return false;
	}
	
	$.post('<%=path%>/manager/add_spread_user',{'uid':uid},  function(data,status){
	    var json=JSON.parse(data);
	    if(json.code==0){
        	$("table tr[id*='tr_'").each(function(i){
	        	this.remove();//移除当前的元素
	        })
        	refreshTable(json);
        }else{
        	alert(json.msg);
        }
	  });
	 return false;
   }
   
   
   
	function loadSpreadList(){
		$.post('<%=path%>/manager/list_spread_user',{'_ua':'12345567645454'},  function(data,status){
		    var json=JSON.parse(data);
		    if(json.code==0){
	        	$("table tr[id*='tr_'").each(function(i){
		        	this.remove();//移除当前的元素
		        })
	        	refreshTable(json);
	        }
		  });
	}
	//刷新表格
	function refreshTable(json){
		var pageData=json["users"];
		if(pageData){
			for(var i=0;i<pageData.length;i++){
				var tr;
				if(i==0){
					tr=$("table tr").eq(0);
				}else{
					tr=$("table tr").eq(-1);
				}
				reviewTableTr(pageData[i],tr);
			}
		}
	}
	
	 //
    function reviewTableTr(pageData,tr) {
		  
  	         var currentItem=$("tr#tr_"+pageData["user_id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var jsonStr=JSON.stringify(pageData);
			 var toAdd="<tr id='tr_"+pageData["user_id"]+"'>";
			 toAdd+="<td>"+pageData.user_id+"</td>";
			 toAdd+="<td>"+pageData.nick_name+"</td>";
			 toAdd+= "<td><img  src='"+pageData.avatar+"' alt='"+pageData.origin_avatar+"'  height='50' onclick='show(this)'/></td>";
			 toAdd+='<td><div class="button-group">';
		     toAdd+= "<a class='button border-red' href='javascript:void(0)' onclick='return del("+pageData["user_id"]+")'><span class='icon-trash-o'></span>删除</a>";
		     toAdd+='</div></td>';
			 toAdd+='</tr>';
			 tr.after(toAdd);
		}
	 
	 function del(id){
		 $.post('<%=path%>/manager/del_spread_user',{'uid':id,'_ua':'1234423232323'},  function(data,status){
			    var json=JSON.parse(data);
			    if(json.code==0){
			    	$("table tr[id*='tr_"+id+"'").remove();//移除当前的元素
		        }
			  });
	 }
	 
	 function show(img){
	    	parent.showOriginImg(img);
	    }
</script>
<div class="panel admin-panel margin-top" id="add">
  <div class="panel-head"><strong><span class="icon-pencil-square-o"></span>添加推广用户</strong></div>
  <div class="body-content">
    <a href="#edit"></a>
    <form method="post" name="form" class="form-x" enctype="multipart/form-data">
      
      <div class="form-group">
        <div class="label">
          <label>用户ID</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" id="uid"   name="name" data-validate="required:请输入用户ID.." />
          <div class="tips"></div>
        </div>
      </div>
     
 
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button class="button bg-main icon-check-square-o" id="vip_add_update_btn" onclick='return add_spread_user()'>添加</button>
        </div>
      </div>
    </form>
  </div>
</div>
</body></html>