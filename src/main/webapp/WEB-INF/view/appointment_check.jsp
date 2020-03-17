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
							<option value="0">新建</option>
							<option value="1">已审核</option>
							<option value="2">复审</option>
							<option value="3">违规</option>
							<option value="4">已删除</option>
					</select></li>
				</ul>
			</div>



			<table class="table table-hover text-center">
				<tr>
					<th width="5%">ID</th>
					<th width="5%">设备</th>
					<th width="8%">发送者ID</th>
					<th width="10%">图片</th>
					<th width="5%">主题</th>
					<th width="10%">地址</th>
					<th width="10%">日期</th>
					<th width="5%">时间段</th>
					<th width="25%">操作</th>
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
	    var type=0;
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
			$.post("<%=path%>/manager/appointment_list",{'page':index,'count':pageSize,'status':type},function(result){
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
			var pageData=json["data"];
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
				//$("#new_count").text("对应新增用户数量："+json["totalCount"])
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
			 var currentItem=$("tr#tr_"+pageData["id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var id=pageData['id'];
			 
			 var toAdd="<tr id='tr_"+id+"'>";
			 toAdd+="<td>"+id+"</td>";
			 toAdd+="<td>"+pageData['channel']+"</td>";
			 
			// var nick_name=pageData.publisher.nick_name;
			 var uid=pageData.user.user_id;
			// nick_name=nick_name==undefined?"":nick_name;
			 toAdd+="<td>"+uid+"</td>";
			 toAdd+="<td><img  src='"+pageData.images[0]+"' alt='"+pageData.images[0]+"'  height='50'/></td>";
			 //主题
			 toAdd+="<td>"+pageData.theme.name+"</td>";
			 toAdd+="<td>"+pageData.street+"</td>";
			 toAdd+="<td>"+pageData.appointment_time+"</td>";
			 
			 var time_stage=pageData.time_stage;
			 
			 if(time_stage==1){
				 toAdd+="<td>上午</td>";
			 }else if(time_stage==2){
				 toAdd+="<td>下午</td>";
			 }else{
				 toAdd+="<td>晚上</td>";
			 }
			 var state=pageData["status"];
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  //操作单元格
			  if(state==0){
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",1)'><span class='icon-edit'></span>通过</a>";
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",3)'><span class='icon-edit'></span>违规</a>";
			  }else if(state==1) {
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",3)'><span class='icon-edit'></span>违规</a>";
			  }else if(state==2) {
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",3)'><span class='icon-edit'></span>违规</a>";
			  }else if(state==3) {
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",1)'><span class='icon-edit'></span>通过</a>";
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",4)'><span class='icon-edit'></span>删除</a>";
			  }else{
				  toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return changeBottleState("+id+",1)'><span class='icon-edit'></span>通过</a>";
 
			  }
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
	   
	    
 	    function changeBottleState(id,newStatus){
			$.post("<%=path%>/manager/changeAppointMentStatus",{'id':id,'status':type,'page':currentPageIndex,'count':pageSize,'to_state':newStatus},function(result){
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