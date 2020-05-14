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
					<li>
					   <select id="bottle_status" name="bottle_status" class="input"
						onchange="changeSelectStatus(this)"
						style="line-height: 17px; display: inline-block">
							<option value="0">正常状态</option>
							<option value="1">审核状态</option>
							<option value="2">黑名单</option>
							<option value="3">精选</option>
					   </select>
					</li>
					
					
					<li>类型筛选：</li>
					<li>
					   <select id="bottle_type" name="bottle_type" class="input"
						onchange="changeSelectType(this)"
						style="line-height: 17px; display: inline-block">
							<option value="-1">全部</option>
							<option value="0">文字</option>
							<option value="2">语音</option>
							<option value="3">邂逅</option>
							<option value="4">弹幕文字</option>
							<option value="5">弹幕语音</option>
							<option value="6">我画你猜</option>
							<option value="7">红包</option>
					   </select>
					</li>
					
					<li>
			         <input type="text" placeholder="请输入用户昵称" name="keywords" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()" > 搜索</a>
                   </li> 
                   <li>
			         <input type="text" placeholder="请输入用户id" name="user_id_input" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()" > 搜索</a>
                   </li> 
				</ul>
			</div>



			<table class="table table-hover text-center">
				<tr>
					<th width="10%">设备</th>
					<th width="15%">发送者</th>
					<th width="10%">头像</th>
					<th width="5%">类型</th>
					<th width="10%">内容</th>
					<th width="10%">时间</th>
					<th width="40%">操作</th>
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
	    var status=0;
	    var nick_name;
	    var user_id;
	    //默认加载第一页
	    $(document).ready(function(){ 
		    page(1);
		}); 
	    function doSearch(){
	    	nick_name=$("[name='keywords']").val().replace(/^\s+|\s+$/g,"");
	    	user_id=$("[name='user_id_input']").val().replace(/^\s+|\s+$/g,"");
	    	currentPageIndex=0;
	    	page(1);
	    }
	     
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
			$.post("<%=path%>/manager/list_bottle",{'pageIndex':index,'pageSize':pageSize,'status':status,'type':type,'bottle_id':current_bottle_id,'user_id':user_id,'nick_name':nick_name},function(result){
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
			 
			 var from=pageData['_from'];
			 var channel=pageData['channel'];
			
			 var txtFrom;
			 if(from==1){
				 if(!channel){
					 channel="iPhone"; 
				 }
				 txtFrom=channel;
			 }else if(from==2){
				 txtFrom=channel;
			 }else{
				 txtFrom=channel;
			 }
			 
			 toAdd+="<td>"+txtFrom+"</td>";
			 
			 
			 var nick_name=pageData.sender.nick_name;
			 var uid=pageData.sender.user_id;
			 nick_name=nick_name==undefined?"":nick_name;
			 toAdd+="<td>"+uid+"|"+nick_name+"</td>";
			 toAdd+="<td><img  src='"+pageData.sender.avatar+"' alt='"+pageData.sender.origin_avatar+"'  height='50'/></td>";
			 //类型
			 var type=pageData["type"];
			 var typeStr=type;
			 
			 
			 if(type==0){
				 typeStr="文字";
			 }else if(type==1){
				 typeStr="图片";
			 }else if(type==2){
				 typeStr="语音";
			 }else if(type==3){
				 typeStr="邂逅";
			 }else if(type==4){
				 typeStr="文本弹幕";
			 }else if(type==5){
				 typeStr="语音弹幕";
			 }else if(type==6){
				 typeStr="我画你猜";
			 }else if(type==7){
				 typeStr="红包";
			 }
			 
			 toAdd+="<td>"+typeStr+"</td>";
			 if(type==3||type==6){
				 toAdd+="<td><img  src='"+pageData["content"]+"' alt='"+pageData["content"]+"'  height='50'/></td>";
			 }else{
				 toAdd+="<td>"+pageData["content"]+"</td>";
			 }
			
			 toAdd+="<td>"+pageData['create_time']+"</td>";
			 
			 
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  //操作单元格
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",3)'><span class='icon-edit'></span>精选推荐</a>";
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",2)'><span class='icon-edit'></span>关小黑屋</a>";
			      toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",-2)'><span class='icon-edit'></span>拉黑名单</a>";
			  
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
 
 	    function changeBottleState(id,state){
			$.post("<%=path%>/manager/changeBottleStatus",{'user_id':user_id,'nick_name':nick_name,'id':id,'status':status,'type':type,'pageIndex':currentPageIndex,'pageSize':pageSize,'to_state':state,'bottle_id':current_bottle_id,'nick_name':nick_name},function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	$("table tr[id*='tr_'").each(function(i){
				        	this.remove();//移除当前的元素
				        })
			        	refreshTable(json);
			        }
		    });
		}
		 
		function changeSelectStatus(selectView) {
			var statusSelect = $('#bottle_status option:selected').val();
			if (status != statusSelect) {
				status = statusSelect;
				currentPageIndex = 0;
				page(1);
			}
		}
		
		function changeSelectType(selectView) {
			var typeSelect = $('#bottle_type option:selected').val();
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