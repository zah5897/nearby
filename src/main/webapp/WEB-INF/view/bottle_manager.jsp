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
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="renderer" content="webkit">
<title></title>
<link rel="stylesheet" href="<%=path%>/css/pintuer.css">
<link rel="stylesheet" href="<%=path%>/css/admin.css">
<script src="<%=path%>/js/jquery.js"></script>
<script src="<%=path%>/js/pintuer.js"></script>
<style type="text/css">
td {
	word-break: break-all
}
</style>
</head>
<body>
	<form method="post" action="">
		<div class="panel admin-panel">
			<div class="padding border-bottom">
				<ul class="search">
					<li>状态筛选：</li>
					<li><select id="user_type" name="user_type" class="input"
						onchange="changeType(this)"
						style="line-height: 17px; display: inline-block">
							<option value="-1">全部</option>
							<option value="0">正常状态</option>
							<option value="1">黑名单</option>
							<option value="2">审核状态</option>
					</select></li>
					
					<li>
			         <input type="text" placeholder="请输入瓶子ID" name="bottle_id" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()" > 搜索</a>
                   </li> 
                     
				</ul>
			</div>



			<table class="table table-hover text-center">
				<tr>
					<th width="5%">ID</th>
					<th width="5%">自设备</th>
					<th width="5%">发送者</th>
					<th width="10%">头像</th>
					<th width="5%">瓶子类型</th>
					<th width="10%">瓶子内容</th>
					<th width="10%">发送时间</th>
					<th width="5%">状态</th>
					<th width="30%">操作</th>
				</tr>
				<tr id="bottom">
					<td colspan="8">
						<div class="pagelist">
							<a href="javascript:void(0)" onclick="return previous()">上一页</a>
							<a id="next_flag" href="javascript:void(0)" onclick="next()">下一页</a>
							<a href="javascript:void(0)" onclick="end()">尾页</a>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</form>
	<script type="text/javascript">
	    //页面索引记录
	    var currentPageIndex = 0;
	    var pageSize = 10;
	    var pageCount = 100;
	    var type=-1;
	    //默认加载第一页
	    $(document).ready(function(){ 
		    page(1);
		}); 
		
	     
	    //前一页
		function previous() {
			if(currentPageIndex>1){
				page(currentPageIndex-1);
			}
		}
	    //前一页
		function next() {
			if(currentPageIndex<pageCount){
				page(currentPageIndex+1);
			}
		}
	    
	    function end(){
	    	page(pageCount);
	    }
	    
	   
	     //获取对应页面
		function page(index) {
			if (currentPageIndex == index) {
				return false;
			}
			$.post("<%=path%>/manager/list_bottle",{'pageIndex':index,'pageSize':pageSize,'type':type,'bottle_id':current_bottle_id},function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	$("table tr[id*='tr_'").each(function(i){
				        	this.remove();//移除当前的元素
				        })
			        	refreshTable(json);
			        }
		    });
			
			return true;
		}

		//刷新表格
		function refreshTable(json){
			var pageData=json["bottles"];
			if(pageData){
				for(var i=0;i<pageData.length;i++){
					var tr;
					if(i==0){
						tr=$("table tr").eq(0);
					}else{
						tr=$("table tr").eq(-2);
					}
					reviewTableTr(pageData[i],tr);
				}
			}else{
				alert("no data.");
			}
			currentPageIndex=json["currentPageIndex"]
			if(currentPageIndex==1){
				pageCount=json["pageCount"];
				$("#new_count").text("对应新增用户数量："+json["totalCount"])
			}
			refreshPageIndex();		
		}
		
		function refreshPageIndex(){
			var pageIndexHtml="";
			var nextAflag=$("a#next_flag").eq(0);
			var  startIndex=getStartIndex();
			var  endIndex=getEndIndex(startIndex);
			 for(var i=startIndex;i<=endIndex;i++){
           	   pageIndexHtml+=getPageIndexItem(i);
             }		
			 $("a.pg_flag").each(function(){
				 this.remove();//移除当前的元素
			 })
			nextAflag.before(pageIndexHtml);
		}
		 
		
		function getPageIndexItem(i){
			 if(i==currentPageIndex){
          		  return "<a  class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'><span class='current'>"+i+"</span></a>";
          	  }else{
          		  return "<a class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'>"+i+"</a>";
          	  }
		}
		function getStartIndex(){
		   if(currentPageIndex-2>0){
			   return currentPageIndex-2;
		   }else{
			   return 1;
		   }
		}
		function getEndIndex(startIndex){
			if(startIndex+4>pageCount){
				return pageCount;
			}else{
				return startIndex+4;
			}
		}
	  //
      function reviewTableTr(pageData,tr) {
			 var currentItem=$("tr#tr_"+pageData["user_id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var id=pageData['id'];
			 
			 var toAdd="<tr id='tr_"+id+"'>";
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+id+"' />"+id+"</td>";
			 
			 
			 
			 var from=pageData['_from'];
			 var txtFrom;
			 if(from==1){
				 txtFrom="IOS";
			 }else if(from==2){
				 txtFrom="Android";
			 }else{
				 txtFrom="Old";
			 }
			 
			 toAdd+="<td>"+txtFrom+"</td>";
			 
			 
			 var nick_name=pageData.sender.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 toAdd+="<td>"+nick_name+"</td>";
			 toAdd+="<td><img  src='"+pageData.sender.avatar+"' alt='"+pageData.sender.origin_avatar+"'  height='50'/></td>";
			 //类型
			 var type=pageData["type"];
			 var typeStr=type;
			 
			 
			 if(type==0){
				 typeStr="文字瓶子";
			 }else if(type==1){
				 typeStr="图片瓶子";
			 }else if(type==2){
				 typeStr="语音瓶子";
			 }else if(type==3){
				 typeStr="邂逅瓶子";
			 }else if(type==4){
				 typeStr="文本弹幕瓶子";
			 }else if(type==5){
				 typeStr="语音弹幕瓶子";
			 }else if(type==6){
				 typeStr="我画你猜瓶子";
			 }
			 
			 toAdd+="<td>"+typeStr+"</td>";
			 if(type==3||type==6){
				 toAdd+="<td><img  src='"+pageData["content"]+"' alt='"+pageData["content"]+"'  height='50'/></td>";
			 }else{
				 toAdd+="<td>"+pageData["content"]+"</td>";
			 }
			
			 toAdd+="<td>"+pageData['create_time']+"</td>";
			 
			 var state=pageData["state"];
			 
			 var stateStr="正常状态";
			 if(state==1){
				 stateStr="黑名单状态";
			 }else if(state==2){
				 stateStr="苹果审核状态"
			 }
			 
			 toAdd+="<td>"+stateStr+"</td>";
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  //操作单元格
			  if(state==0){
				 
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",2)'><span class='icon-edit'></span>编辑为ios审核状态</a>";
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",1)'><span class='icon-edit'></span>编辑为黑名单状态</a>";
			  }else  {
				  toAdd+="<a class='button border-main' href='javascript:void(0)'	onclick='return changeBottleState("+id+",0)'><span class='icon-edit'></span>编辑为正常状态</a>";
			  } 
			  
			  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return add_to_found_black_list("+pageData["sender"]["user_id"]+",1)'><span class='icon-edit'></span>该用户添加到黑名单</a>";
			  
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
	    
	    function add_to_found_black_list(user_id){
			$.post("<%=path%>/manager/edit_user_found_state",{'user_id':user_id,'fun':1,'state':1},function(result){
				 var json=JSON.parse(result);
				 if(json.code==0){
			        	parent.toast("操作成功！");
			        	$("#found_black_"+user_id).hide();
			        }else{
			        	parent.toast("操作失败！");
			        }
		    });
		}
	    
 	    function changeBottleState(id,state){
			$.post("<%=path%>/manager/changeBottleState",{'id':id,'type':type,'pageIndex':currentPageIndex,'pageSize':pageSize,'to_state':state,'bottle_id':current_bottle_id},function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	$("table tr[id*='tr_'").each(function(i){
				        	this.remove();//移除当前的元素
				        })
			        	refreshTable(json);
			        }
		    });
		}
		 
		function changeType(selectView) {
			var typeSelect = $('#user_type option:selected').val();
			if (type != typeSelect) {
				type = typeSelect;
				currentPageIndex = 0;
				page(1);
			}
		}
		
		var current_bottle_id=0;
		
		 function doSearch(){
			 
		    	var key=$("[name='bottle_id']").val().replace(/^\s+|\s+$/g,"");
		    	 
		    	var intKey;
		    	  
		    		intKey=Number(key);
		    		
		    		if(isNaN(intKey)){
		    			alert("请输入数字");
		    			return;
		    		}
		    		
		    		 
		    		if(intKey==current_bottle_id){
		    			return;
		    		}
		    		currentPageIndex=0;
		    		current_bottle_id=intKey;
		    		page(1);
		    }
		
	</script>
</body>
</html>