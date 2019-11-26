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
  <div class="panel-head"><strong class="icon-reorder">&nbsp;礼物列表</strong></div>
   
  <table class="table table-hover text-center">
    <tr>
      <th width="20%">图片</th>
      <th width="10%">名称</th>
      <th width="10%">现价</th>
      <th width="10%">原价</th>
      <th width="10%">描述</th>
      <th width="10%">备注</th>
      <th width="30%">操作</th>
    </tr>
  </table>
</div>
<script type="text/javascript">
$(document).ready(function(){ 
	loadGift();
}); 

function add_gift(){
	var form = new FormData($("form")[0]);  
	$.ajax({
		async: false,
        url:"<%=path%>/gift/add",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
        	var json=JSON.parse(data);
        	var tr=$("table tr").eq(-1);
		    reviewTableTr(json["gift"],tr);
			$("#gift_add_update_btn").html('添加');//填充内容
			$("#gift_id").attr("value",pageData['id']);//填充内容
        },
        error:function(error){
        	alert(error);
        }
    });
}
	function loadGift(){
		$.post('<%=path%>/gift/list',{'_ua':'12345567645454','i':'1111'},  function(data,status){
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
		var pageData=json["gifts"];
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
  	         var currentItem=$("tr#tr_"+pageData["id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var jsonStr=JSON.stringify(pageData);
			 var toAdd="<tr id='tr_"+pageData["id"]+"'>";
			 toAdd+="<td><img src='"+pageData.image_url+"' alt='' width='100' height='100' /></td>";
			 toAdd+="<td>"+pageData.name+"</td>";
			 toAdd+= "<td>"+pageData.price+"</td>";
			 toAdd+= "<td>"+pageData.old_price+"</td>";
			 toAdd+= "<td>"+(pageData.description==null?"":pageData.description)+"</td>";
			 toAdd+= "<td>"+(pageData.remark==null?"":pageData.remark)+"</td>";
			 toAdd+='<td><div class="button-group">';
		     toAdd+= "<a class='button border-main' href='javascript:void(0)' onclick='return edit("+jsonStr+")'><span class='icon-edit'></span>编辑</a>";
		     toAdd+= "<a class='button border-red' href='javascript:void(0)' onclick='return del("+pageData["id"]+")'><span class='icon-trash-o'></span>删除</a>";
		     toAdd+='</div></td>';
			 toAdd+='</tr>';
			 tr.after(toAdd);
		}
	 
	 function del(id){
		 $.post('<%=path%>/gift/del',{'id':id,'_ua':'1234423232323','i':'1111'},  function(data,status){
			    var json=JSON.parse(data);
			    if(json.code==0){
			    	$("table tr[id*='tr_"+id+"'").remove();//移除当前的元素
		        }
			  });
	 }
	 function edit(pageData){
		 $("#gift_id").attr("value",pageData['id']);//填充内容
		 $("#gift_name").attr("value",pageData['name']);//填充内容
		 $("#gift_price").attr("value",pageData['price']);//填充内容
		 $("#gift_old_price").attr("value",pageData['old_price']);//填充内容
		 $("#gift_description").attr("value",pageData['description']);//填充内容
		 $("#gift_remark").attr("value",pageData['remark']);//填充内容
		 $("#gift_add_update_btn").html('修改');//填充内容
		// $("input[name='name']")[0].value=pageData['name'];
		 //$("input[name='price']")[0].value=pageData['price'];
		 //$("input[name='old_price']")[0].value=pageData['old_price'];
		 location.href = "#edit";
	 }
</script>
<div class="panel admin-panel margin-top" id="add">
  <div class="panel-head"><strong><span class="icon-pencil-square-o"></span>添加礼物</strong></div>
  <div class="body-content">
    <a href="#edit"></a>
    <form method="post" name="form" class="form-x" enctype="multipart/form-data">
      
      <input type="hidden" id="gift_id" name="id" value="0">    
      <div class="form-group">
        <div class="label">
          <label>名称</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" id="gift_name" value="" name="name" data-validate="required:请输入礼物名称.." />
          <div class="tips"></div>
        </div>
      </div>
     
      <div class="form-group">
        <div class="label">
          <label>图片</label>
        </div>
        <div class="field">
                <input   name="small_img" type="file" />
        </div>
      </div>
      <input type="hidden" value="123456789012" name="_ua"> 
      <div class="form-group">
        <div class="label">
          <label>现价（用于活动折扣）</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="gift_price" name="price" data-validate="required:请输入现价.." />
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label>原价</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="gift_old_price" name="old_price" data-validate="required:请输入原价.." />
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label>描述</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="gift_description" name="description"/>
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label>备注</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="gift_remark" name="remark"/>
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button class="button bg-main icon-check-square-o" id="gift_add_update_btn" onclick='add_gift()'>添加</button>
        </div>
      </div>
    </form>
  </div>
</div>
</body></html>