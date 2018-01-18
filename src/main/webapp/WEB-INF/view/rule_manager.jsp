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
  <div class="panel-head"><strong class="icon-reorder">&nbsp;金币购买项列表</strong></div>
  <div class="padding border-bottom">
			   
				<ul class="search">
				 <li> AID：</li>
				  <li>
			         <input type="text" placeholder="请输入aid" name="keywords" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()">搜索</a>
                   </li> 
				</ul>
			</div>
   
  <table class="table table-hover text-center">
    <tr>
      <th width="5%">ID</th>
      <th width="10%">名称</th>
      <th width="10%">金币数量</th>
      <th width="15%">赠送金币数量</th>
      <th width="10%">人民币（分）</th>
      <th width="15%">描述</th>
      <th width="15%">所属AID</th>
      <th width="20%">操作</th>
    </tr>
  </table>
</div>
<script type="text/javascript">
var keyword;
$(document).ready(function(){ 
	loadRuleList();
}); 

function add_rule(){
	var form = new FormData($("form")[0]);  
	$.ajax({
		async: false,
        url:"<%=path%>/rule/save",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
			$("#rule_add_update_btn").html('添加');//填充内容
			$("#rule_id").attr("value",'0');//填充内容
			loadRuleList();
			$("html,body").animate({scrollTop: $(".panel-head").offset().top}, 500);
			$('#edit_rule')[0].reset();
			
			$(':input','#edit_rule')  
			 .not(':button, :submit, :reset, :hidden')  
			 .val(''); 
        },
        error:function(error){
        	alert(error);
        }
    });
	return false;
}
	function loadRuleList(){
		$.post('<%=path%>/rule/list',{'_ua':'12345567645454','aid':keyword},  function(data,status){
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
		var pageData=json["rules"];
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
			 toAdd+="<td>"+pageData.id+"</td>";
			 toAdd+="<td>"+pageData.name+"</td>";
			 toAdd+= "<td>"+pageData.coins+"</td>";
			 toAdd+= "<td>"+pageData.coins_free+"</td>";
			 toAdd+= "<td>"+pageData.rmb+"</td>";
			 toAdd+= "<td>"+(pageData.description==null?"":pageData.description)+"</td>";
			 toAdd+= "<td>"+pageData.aid+"</td>";
			 toAdd+='<td><div class="button-group">';
		     toAdd+= "<a class='button border-main' href='javascript:void(0)' onclick='return edit("+jsonStr+")'><span class='icon-edit'></span>编辑</a>";
		     toAdd+= "<a class='button border-red' href='javascript:void(0)' onclick='return del("+pageData["id"]+")'><span class='icon-trash-o'></span>删除</a>";
		     toAdd+='</div></td>';
			 toAdd+='</tr>';
			 tr.after(toAdd);
		}
	 
	 function del(id){
		 
		 if(!confirm("确定删除该购买项？")){
			 return;
		 }
		 $.post('<%=path%>/rule/del',{'id':id,'_ua':'1234423232323'},  function(data,status){
			    var json=JSON.parse(data);
			    if(json.code==0){
			    	$("table tr[id*='tr_"+id+"'").remove();//移除当前的元素
		        }
			  });
	 }
	 function edit(pageData){
		 $("#rule_id").attr("value",pageData['id']);//填充内容
		 $("#name").attr("value",pageData['name']);//填充内容
		 $("#coins").attr("value",pageData['coins']);//填充内容
		 $("#coins_free").attr("value",pageData['coins_free']);//填充内容
		 $("#rmb").attr("value",pageData['rmb']);//填充内容
		 $("#description").attr("value",pageData['description']);//填充内容
		 $("#aid").attr("value",pageData['aid']);//填充内容
		 $("#app_name").attr("value",pageData['app_name']);//填充内容
		// $("#term_mount").val(pageData['term_mount']);//填充内容
		 $("#rule_add_update_btn").html('修改');//填充内容
		// $("input[name='name']")[0].value=pageData['name'];
		 //$("input[name='price']")[0].value=pageData['price'];
		 //$("input[name='old_price']")[0].value=pageData['old_price'];

		$("html,body").animate({scrollTop: $("#edit").offset().top}, 500);
	 }
	  
	 function doSearch(){
	    	var key=$("[name='keywords']").val().replace(/^\s+|\s+$/g,"");
	    	if(keyword==''){
	    		if(key==''){
		    		return;
		    	}
	    	}
	    	if(keyword==key){
	    		return;
	    	}
	    	keyword=key;
	    	loadRuleList();
	    }
</script>
<div class="panel admin-panel margin-top" id="add">
  <div class="panel-head"><strong><span class="icon-pencil-square-o"></span>添加金币购买项</strong></div>
  <div class="body-content">
    <a href="edit" id="edit"></a>
    <form method="post" id="edit_rule" name="form" class="form-x" enctype="multipart/form-data">
      
      <input type="hidden" id="rule_id" name="rule_id" value="0">    
       <input type="hidden" value="123456789012" name="_ua"> 
      <div class="form-group">
        <div class="label">
          <label>名称</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" id="name" value="" name="name" data-validate="required:请输入名称.." />
          <div class="tips"></div>
        </div>
      </div>
     
      
      <div class="form-group">
        <div class="label">
          <label>金币数量</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="coins" name="coins" data-validate="required:请输入.." />
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label>赠送数量</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="coins_free" name="coins_free" data-validate="required:请输入原价.." />
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label>人民币（分）</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="rmb" name="rmb" data-validate="required:请输入.." />
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label>描述</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="description" name="description"/>
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>所属AppID</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="aid" name="aid"/>
          <div class="tips"></div>
        </div>
        
         <div class="label">
          <label>所属App名称</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" id="app_name" name="app_name"/>
          <div class="tips"></div>
        </div>
      </div>
      
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
         <a href="javascript:void(0)" id="rule_add_update_btn"  class="button bg-main icon-check-square-o" onclick='add_rule()'>添加</a>
        </div>
      </div>
    </form>
  </div>
</div>
</body></html>