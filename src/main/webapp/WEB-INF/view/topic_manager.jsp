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
  <div class="panel-head"><strong class="icon-reorder">&nbsp;话题列表</strong></div>
   
  <table class="table table-hover text-center">
    <tr>
      <th width="5%">ID</th>
      <th width="10%">小图</th>
      <th width="20%">大图</th>
      <th width="10%">标题</th>
      <th width="10%">描述</th>
      <th width="10%">创建时间</th>
      <th width="10%">动态数量</th>
      <th width="25%">操作</th>
    </tr>
    <!--   
    <tr>
      <td>1</td>     
      <td><img src="<%=path %>/images/11.jpg" alt="" width="120" height="50" /></td>     
      <td></td>
      <td></td>
      <td>1</td>
      <td>1</td>
      <td><div class="button-group">
      <a class="button border-main" href="#add"><span class="icon-edit"></span>编辑</a>
      <a class="button border-red" href="javascript:void(0)" onclick="return del(1,1)"><span class="icon-trash-o"></span>删除</a>
      </div></td>
    </tr>
    -->
  </table>
</div>
<script type="text/javascript">
$(document).ready(function(){ 
	loadTopic();
}); 

function add_topic(){
	var form = new FormData($("form")[0]);  
	
	$.ajax({
		async: false,
        url:"<%=path%>/manager/add_topic",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
        	alert(data);
        	var json=JSON.parse(data);
        	var tr=$("table tr").eq(-1);
		    reviewTableTr(json["topic"],tr);
        },
        error:function(error){
        	alert(0);
        	alert(error);
        }
    });
}
	function loadTopic(){
		$.post('<%=path%>/manager/load_topic',{},  function(data,status){
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
		var pageData=json["topics"];
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
			 //获取table倒数第二行 $("#tab tr").eq(-2)
			 //var last2tr=$("table tr").eq(row);
			 
  	     var currentItem=$("tr#tr_"+pageData["id"]);
			 
			 if(currentItem.length>0){
				 return;
			 }
			 
			 var toAdd="<tr id='tr_"+pageData["id"]+"'>";
			 
			 toAdd+="<td>"+pageData["id"]+"</td>";     
			 toAdd+="<td><img src='"+pageData.icon+"' alt='' width='100' height='40' /></td>";
			 toAdd+="<td><img src='"+pageData.big_icon+"' alt='' width='120' height='50' /></td>";
			 toAdd+="<td>"+pageData.name+"</td>";
			 toAdd+= "<td>"+pageData.description+"</td>";
			 
			 
			 var time=parent.dateFtt("yyyy-MM-dd hh:mm:ss",new Date(pageData["create_time"]));
			 toAdd+="<td>"+time+"</td>";
		 
			 toAdd+=' <td>1</td>';
			 toAdd+='<td><div class="button-group">';
		     toAdd+= '<a class="button border-main" href="#add"><span class="icon-edit"></span>编辑</a>';
		     toAdd+= "<a class='button border-red' href='javascript:void(0)' onclick='return del("+pageData["id"]+")'><span class='icon-trash-o'></span>删除</a>";
		     toAdd+='</div></td>';
			 toAdd+='</tr>';
			 tr.after(toAdd);
		}
	 
	 function del(id){
		 $.post('<%=path%>/manager/del_topic',{'id':id},  function(data,status){
			    var json=JSON.parse(data);
			    if(json.code==0){
			    	$("table tr[id*='tr_"+id+"'").remove();//移除当前的元素
		        }
			  });
	 }
</script>
<div class="panel admin-panel margin-top" id="add">
  <div class="panel-head"><strong><span class="icon-pencil-square-o"></span>添加话题</strong></div>
  <div class="body-content">
    <form method="post" name="form" class="form-x" enctype="multipart/form-data">    
      <div class="form-group">
        <div class="label">
          <label>标题</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" name="name" data-validate="required:请输入标题.." />
          <div class="tips"></div>
        </div>
      </div>
     
      <div class="form-group">
        <div class="label">
          <label>小图</label>
        </div>
        <div class="field">
        <!-- 
         <a class="button input-file" style="width:25%; float:left;" href="javascript:void(0);">+ 请选择上传文件
        <input   data-validate="required:请选择文件,regexp#.+.(jpg|jpeg|png|gif)$:只能上传jpg|gif|png格式文件"
            type="file" />
                </a>
                -->  
                <input   name="small_img" data-validate="required:请选择文件,regexp#.+.(jpg|jpeg|png|gif)$:只能上传jpg|gif|png格式文件"
            type="file" />
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>大图</label>
        </div>
        <div class="field">
        <!-- 
         <a class="button input-file" style="width:25%; float:left;" href="javascript:void(0);">+ 请选择上传文件
        <input   data-validate="required:请选择文件,regexp#.+.(jpg|jpeg|png|gif)$:只能上传jpg|gif|png格式文件"
            type="file" />
                </a>
                -->  
                <input   name="big_img" data-validate="required:请选择文件,regexp#.+.(jpg|jpeg|png|gif)$:只能上传jpg|gif|png格式文件"
            type="file" />
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>描述</label>
        </div>
        <div class="field">
          <textarea type="text" class="input" name="description" style="height:120px;" value=""></textarea>
          <div class="tips"></div>
        </div>
      </div>
    
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button class="button bg-main icon-check-square-o" onclick='add_topic()'>添加</button>
        </div>
      </div>
    </form>
  </div>
</div>
</body></html>