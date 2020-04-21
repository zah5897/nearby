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
                   <li>
			         <input type="text" placeholder="请输入收礼用户id" name="user_id_input" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearchById()" > 搜索</a>
                   </li> 
				</ul>
			</div>
		
		
			<table class="table table-hover text-center">
				<tr>
					<th width="10%">礼物ID</th>
					<th width="10%">收礼者ID|昵称</th>
					<th width="10%">送礼者ID|昵称</th>
					<th width="10%">礼物名称</th>
					<th width="20%">数量</th>
					<th width="10%">日期</th>
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
	    var type=0;
	    var user_id;
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
			$.post("<%=path%>/manager/gift_history_list",{'page':index,'count':pageSize,'status':type,'user_id':user_id},function(result){
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
		 function doSearchById(){
		    	var key=$("[name='user_id_input']").val().replace(/^\s+|\s+$/g,"");
		    	if(user_id==''){
		    		if(key==''){
			    		return;
			    	}
		    	}
		    	if(user_id==key){
		    		return;
		    	}
		    	user_id=key;
		    	currentPageIndex=0;
		    	page(1);
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
			 var id=pageData['id'];
			 
			 var toAdd="<tr id='tr_"+id+"'>";
			 toAdd+="<td>"+id+"</td>";
			 toAdd+="<td>"+pageData['receiver'].user_id+"|"+pageData['receiver'].nick_name+"</td>";
			 
			 toAdd+="<td>"+pageData['sender'].user_id+"|"+pageData['sender'].nick_name+"</td>";
	 
			 toAdd+="<td>"+pageData['name']+"</td>";
			 toAdd+="<td>"+pageData.count+"</td>";
			 
			 toAdd+="<td>"+pageData.give_time+"</td>";
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  //操作单元格
			  toAdd+=" <span class='icon-edit'>--</span>";
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }

		 
	</script>
</body>
</html>