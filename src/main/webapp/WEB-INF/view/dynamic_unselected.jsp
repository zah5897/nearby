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
</head>
<body>
	<form method="post" action="">
		<div class="panel admin-panel">
			<div class="panel-head">
				<strong class="icon-reorder"> 管理动态</strong>
			</div>
			<div class="padding border-bottom">
				<ul class="search">
					<li>
						<button type="button" class="button border-green" id="checkall">
							<span class="icon-check"></span>全选</button>
						<button type="button" id="add_batch" class="button border-yellow">
							<span class="icon-plus-square-o"></span>批量添加
						</button>
					</li>
				</ul>
			</div>
			<table class="table table-hover text-center">
				<tr>
					<th width="80">ID</th>
					<th>发布者</th>
					<th>图片</th>
					<th width="20%">内容</th>
					<th>城市</th>
					<th>时间</th>
					<th width="25%">点赞数量</th>
					<th width="120">操作</th>
				</tr>

				<!--  
				<c:forEach var="dy" items="${selecteds}">
					<tr id="tr_${dy.id }">
						<td><input type="checkbox" name="id[]" value="${dy.id }" />${dy.id }</td>
						<td>${dy.user.nick_name }</td>
						<td><img src="${dy.thumb }" alt="" width="120" height="50" /></td>
						<td>${dy.description }</td>
						<td>${dy.create_time }</td>
						<td>${dy.praise_count }</td>
						<td><div class="button-group">
								<a class="button border-red" href="javascript:void(0)"
									onclick="return del(${dy.id})"><span class="icon-trash-o"></span>删除</a>
							</div></td>
					</tr>
				</c:forEach>
                -->
				<tr id="bottom">
					<td colspan="8">
						<div class="pagelist">
							<a href="javascript:void(0)" onclick="return previous()">上一页</a>
							<!--  
						   <c:choose>
						       <c:when test="${currentPageIndex<10}">
                                   <c:forEach var="i" begin="1" end="10" step="1">   
     						           <c:choose>
     						                <c:when test="${i==currentPageIndex }">
     						                   <a href="javascript:void(0)" onclick="return page(${i})"><span class="current">${i }</span></a>
     						                </c:when>
     						                <c:otherwise>
     						                	<a href="javascript:void(0)" onclick="return page(${i})">${i }</a>
     						                </c:otherwise>
     						           </c:choose>  
						           </c:forEach>
						       </c:when>
						       
						       <c:when test="${currentPageIndex<pageCount-10&&currentPageIndex>=10}">
						         <c:forEach var="i" begin="${currentPageIndex }" end="${currentPageIndex+10 }" step="1">   
     						           <c:choose>
     						                <c:when test="${i==currentPageIndex }">
     						                   <a href="javascript:void(0)" onclick="return page(${i})"><span class="current">${i }</span></a>
     						                </c:when>
     						                <c:otherwise>
     						                	<a href="javascript:void(0)" onclick="return page(${i})">${i }</a>
     						                </c:otherwise>
     						           </c:choose>  
						           </c:forEach>
						           <c:otherwise>
						                <c:forEach var="i" begin="${pageCount-10 }" end="10" step="1">   
     						           <c:choose>
     						                <c:when test="${i==currentPageIndex }">
     						                   <a href="javascript:void(0)" onclick="return page(${i})"><span class="current">${i }</span></a>
     						                </c:when>
     						                <c:otherwise>
     						                	<a href="javascript:void(0)" onclick="return page(${i})">${i }</a>
     						                </c:otherwise>
     						           </c:choose>  
						           </c:forEach>
						           </c:otherwise>
						       </c:when>
						   </c:choose>
						   -->

							<a id="next_flag" href="javascript:void(0)"
								onclick="return next()">下一页</a> <a href="javascript:void(0)"
								onclick="return page(-1)">尾页</a>
							<!--  
							<a href="javascript:void(0)" onclick="return previous()">上一页</a>
							<a href="javascript:void(0)" onclick="page(1)"><span class="current">1</span></a>
							<a href="javascript:void(0)">2</a>
							<a href="javascript:void(0)">3</a>
							<a href="javascript:void(0)" onclick="return next()">下一页</a>
							<a href="javascript:void(0)" onclick="return end()">尾页</a>
							-->
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
		}); 
		
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
			$.post("<%=path%>/manager/unselected_dynamic_list",{'pageIndex':index},function(result){
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
			var pageData=json["selecteds"];
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
		
		 
		
		function add(id) {
				$.post("<%=path%>/manager/add_to_selected",{id:id,currentPage:currentPage},function(result){
			        $("#tr_"+id).remove();//移除当前的元素
			        
			        var json=JSON.parse(result);
			        var pageData=json["pageData"];
			        
			        var last2tr=$("table tr").eq(-2);
					 if(last2tr.size()==0){
					      alert("指定的table id或行数不存在！");
					     return;
					 }
					 if(pageData){
						 reviewTableTr(pageData,last2tr);
					 }else{
						 page(json["currentPageIndex"]);
					 }
			        refreshPageIndex(json["pageCount"],json["currentPageIndex"]);
			    });
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
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+pageData["id"]+"' />"+pageData["id"]+"</td>";
			 
			 var nick_name=pageData.user.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 
			 toAdd+="<td>"+nick_name+"</td>";
			 toAdd+="<td><img src='"+pageData.thumb+"' alt=''   height='50' /></td>";
			 toAdd+="<td>"+pageData["description"]+"</td>";
			 toAdd+="<td>"+pageData["city"]+"</td>";
			 toAdd+="<td>"+pageData["create_time"]+"</td>";
			 toAdd+="<td>"+pageData["praise_count"]+"</td>";
			 toAdd+="<td><div class='button-group'><a class='button border-green' href='javascript:void(0)'	onclick='return add("+pageData["id"]+")'><span class='icon-plus-square-o'></span>添加</a></div></td>";
			 toAdd+="</tr>";
			 tr.after(toAdd);
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
		$("#add_batch").click(function() {
			var chk_value =[]; 
			$('input[name="id[]"]:checked').each(function(){ 
			    chk_value.push($(this).val()); 
			});
			if(chk_value.length>0){
				    var ids=JSON.stringify(chk_value);
					$.post("<%=path%>/manager/add_batch_to_selected", {
							ids : ids,
							currentPage : currentPage
						}, function(result) {

							var json = JSON.parse(result);
							var pageData = json["pageData"];
							for (var i = 0; i < chk_value.length; i++) {
								$("#tr_" + chk_value[i]).remove();//移除当前的元素
							}
							if (pageData) {
								var last2tr = $("table tr").eq(-2);
								if (last2tr.size() == 0) {
									alert("指定的table id或行数不存在！");
									return;
								}
								for (var i = 0; i < pageData.length; i++) {
									var dy = pageData[i];
									reviewTableTr(dy, last2tr);
								}
							} else {
								page(json["currentPageIndex"]);
							}
							refreshPageIndex(json["pageCount"],
									json["currentPageIndex"]);
						});
						return true;
					}
				})
	</script>
</body>
</html>