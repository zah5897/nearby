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
   td {word-break:break-all}
</style>
</head>
<body>
	<form method="post" action="">
		<div class="panel admin-panel">
			<div class="panel-head">
				<strong class="icon-reorder">&nbsp;新增用户</strong>
			</div>
			
			<div class="padding border-bottom">
				<ul class="search">
					<li>
						<button type="button" class="button border-green" id="checkall">
							<span class="icon-check"></span>全选
						</button>
						<button type="button" id="edit_to_visible" class="button border-green">
							<span class="icon-check"></span>编辑为可被推荐
						</button>
						<button type="button" id="edit_to_gone" class="button border-red">
							<span class="icon-trash-o"></span>编辑为不被推荐
						</button>
					</li>
				</ul>
			</div>
			
			<div class="padding border-bottom">
				<ul class="search">
					<li>
						 今日新增用户：<span id="new_user_count">0</span>
					</li>
				</ul>
			</div>
			
			<table class="table table-hover text-center">
				<tr>
					<th width="10%">ID</th>
					<th width="15%">昵称</th>
					<th width="50%">头像</th>
					<th width="15%">性别</th>
					<th width="5%">类型</th>
					<th width="5%">推荐状态</th>
				</tr>
				<tr id="bottom">
					<td colspan="8">
						<div class="pagelist">
							<a href="javascript:void(0)" onclick="return previous()">上一页</a>
							<a id="next_flag" href="javascript:void(0)"
								onclick="return next()">下一页</a> <a href="javascript:void(0)"
								onclick="return page(-1)">尾页</a>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</form>
	<script type="text/javascript">
	    //页面索引记录
	    var currentPage = 0;
	    var pageSize = 10;
	    var pageCount = 100;
	    //默认加载第一页
	    $(document).ready(function(){ 
		   page(1);
		   getNewUserCount();
		}); 
		
	    
	    function getNewUserCount() {
	    	$.post("<%=path%>/manager/new_user_count",{'param':0},function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	 $("#new_user_count").html(json['count']);
			        }
		    });
			return true;
		}

	    
	    //前一页
		function previous() {
			if (currentPage-1<1) {
                return false;
			}
			page(currentPage-1)
		}
	    //前一页
		function next() {
			if (currentPage+1>pageCount) {
                return false;
			}
			page(currentPage+1)
		}
	    
	     //获取对应页面
		function page(index) {
			if (currentPage == index) {
				return false;
			}
			$.post("<%=path%>/manager/list_new_user",{'pageIndex':index},function(result){
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
			var pageData=json["users"];
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
				page(json["currentPageIndex"]);
			}
			refreshPageIndex(json["pageCount"],json["currentPageIndex"]);		
		}
		
		function refreshPageIndex(page_count,currentPageIndex){
			currentPage=currentPageIndex;
			this.pageCount=page_count;
			
			var pageIndexHtml="";
			var nextAflag=$("a#next_flag").eq(0);
			if(currentPage<=pageSize){
				var end=pageSize;
				if(pageCount<pageSize){
					end=pageCount;
				} 
                  for(var i=1;i<=end;i++){
                	  pageIndexHtml+=getItem(currentPage,i);
                  }				
			}else{
				//11
				
				if(currentPage==pageCount){
					for(var i=pageCount-pageSize+1;i<=pageCount;i++){
	                	  pageIndexHtml+=getItem(currentPage,i);
	                  }	
				}else if(currentPage+5>pageCount){
					for(var i=pageCount-pageSize+1;i<=pageCount;i++){
	                	  pageIndexHtml+=getItem(currentPage,i);
	                  }	
				}else{
					for(var i=pageCount-4;i<=pageCount+5;i++){
	                	  pageIndexHtml+=getItem(currentPage,i);
	                  }	
				}
				
			}
			
			 $("a.pg_flag").each(function(){
				 this.remove();//移除当前的元素
			 })
			nextAflag.before(pageIndexHtml);
		}
		 
		
		function getItem(currentPage,i){
			 if(i==currentPage){
          		  return "<a  class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'><span class='current'>"+i+"</span></a>";
          	  }else{
          		  return "<a class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'>"+i+"</a>";
          	  }
		}

	  //
      function reviewTableTr(pageData,tr) {
			 //获取table倒数第二行 $("#tab tr").eq(-2)
			 //var last2tr=$("table tr").eq(row);
			 
			 var currentItem=$("tr#tr_"+pageData["user_id"]);
			 
			 if(currentItem.length>0){
				 return;
			 }
			 
			 var toAdd="<tr id='tr_"+pageData["user_id"]+"'>";
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+pageData["user_id"]+"' />"+pageData["user_id"]+"</td>";
			 
			 var nick_name=pageData.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 
			 toAdd+="<td>"+nick_name+"</td>";
			 toAdd+="<td><img  src='"+pageData.avatar+"' alt='"+pageData.origin_avatar+"'  height='50' onclick='show(this)'/></td>";
			 toAdd+="<td>"+pageData["sex"]+"</td>";
			 toAdd+="<td>"+pageData["type"]+"</td>";
			 toAdd+="<td id='"+pageData['user_id']+"'>"+(pageData["state"]==0?'<font color="green">可被推荐</font>':'<font color="red">不被推荐</font>')+"</td>";
			 toAdd+="</tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
		function imgClick(url){  
			     var obj = '<img src='+url+'/>';
			     alert(obj); 
		}
		
		
		
		
		$("#checkall").click(function() {
			$("input[name='id[]']").each(function() {
				if (this.checked) {
					this.checked = false;
				} else {
					this.checked = true;
				}
			});
		})
		$("#edit_to_visible").click(function() {
			editState(0);	 
		})
		$("#edit_to_gone").click(function() {
			editState(1);
		})
				
		function editState(state){
			var chk_value =[]; 
			$('input[name="id[]"]:checked').each(function(){ 
			    chk_value.push($(this).val()); 
			});
			if(chk_value.length>0){
				    var ids=JSON.stringify(chk_value);
					$.post("<%=path%>/manager/edit_user_from_found_list", {
							ids : ids,
							state : state,
							currentPage : currentPage
						}, function(result) {
							var json = JSON.parse(result);
							var pageData = json["users"];
							if (pageData) {
								for (var i = 0; i < pageData.length; i++) {
									var dy = pageData[i];
									var id=dy['user_id'];
									$("#"+id).html((dy["state"]==0?'<font color="green">可被推荐</font>':'<font color="red">不被推荐</font>'));
								}
							}  
						});
						return true;
					}
		        }
	</script>
</body>
</html>