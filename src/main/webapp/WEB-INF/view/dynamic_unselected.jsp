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
				<strong class="icon-reorder">&nbsp;管理动态</strong>
			</div>
			<div class="padding border-bottom">
				<ul class="search">
					<li>
						<button type="button" class="button border-green" id="checkall">
							<span class="icon-check"></span>全选
						</button>
						<button type="button" class="button border-yellow" id="add_batch"
							onclick="window.location.href='#add'">
							<span class="icon-plus-square-o"></span>批量添加
						</button>
					</li>
				</ul>
			</div>
			<table class="table table-hover text-center">
				<tr>
					<th width="80">ID</th>
					<th>发布者</th>
					<th>图片</th>
					<th>内容</th>
					<th>时间</th>
					<th width="25%">点赞数量</th>
					<th width="120">操作</th>
				</tr>


				<c:forEach var="dy" items="${selecteds}">
					<tr id="tr_${dy.id }">
						<td><input type="checkbox" name="id[]" value="${dy.id }" />${dy.id }</td>
						<td>${dy.user.nick_name }</td>
						<td><img src="${dy.thumb }" alt="" width="120" height="50" /></td>
						<td>${dy.description }</td>
						<td>${dy.create_time }</td>
						<td>${dy.praise_count }</td>
						<td><div class="button-group">
								<a class="button border-green" href="javascript:void(0)"
									onclick="return add(${dy.id})"><span
									class="icon-plus-square-o"></span>添加</a>
							</div></td>
					</tr>
				</c:forEach>

				<tr id="bottom">
					<td colspan="8">
						<div class="pagelist">
							<a href=previous()>上一页</a> <a href=page(1)><span
								class="current">1</span></a><a href="">2</a><a href="">3</a><a
								href="">下一页</a><a href="">尾页</a>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</form>
	<script type="text/javascript">
		var currentPage = 1;
		function previous() {
			if (confirm(currentPath - 1)) {

			}
		}
		function page(index) {

			if (currentPath == index) {
				if (confirm("已经是第一页了")) {

				}
			}

		}

		function add(id) {
				$.post("<%=path%>/manager/add_to_selected",{id:id,currentPage:currentPage},function(result){
			        $("#tr_"+id).remove();//移除当前的元素
			        var pageData=JSON.parse(result)["pageData"];
			        reviewTableTr(pageData);
			    });
		}

	  //把最后一个补上
      function reviewTableTr(pageData) {
			 //获取table倒数第二行 $("#tab tr").eq(-2)
			 //var last2tr=$("table tr").eq(row);
			 var last2tr=$("table tr").eq(-2);
			 if(last2tr.size()==0){
			      alert("指定的table id或行数不存在！");
			     return;
			 }
			 
			 var toAdd="<tr id='tr_"+pageData["id"]+"'>";
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+pageData["id"]+"' />"+pageData["id"]+"</td>";
			 
			  var nick_name=pageData.user.nick_name;
			  nick_name=nick_name==undefined?"":nick_name;
			 
			 toAdd+="<td>"+nick_name+"</td>";
			 toAdd+="<td><img src='"+pageData.thumb+"' alt='' width='120' height='50' /></td>";
			 toAdd+="<td>"+pageData["description"]+"</td>";
			 toAdd+="<td>"+pageData["create_time"]+"</td>";
			 toAdd+="<td>"+pageData["praise_count"]+"</td>";
			 toAdd+="<td><div class='button-group'><a class='button border-green' href='javascript:void(0)'	onclick='return add("+pageData["id"]+")'><span class='icon-plus-square-o'></span>删除</a></div></td>";
			 toAdd+="</tr>";
			 last2tr.after(toAdd);
			  
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
					$.post("<%=path%>/manager/add_batch_to_selected",{ids:ids,currentPage:currentPage},function(result){
			       // $("#tr_"+id).remove();//移除当前的元素
			        //reviewTableTr(result);
			       var pageData=JSON.parse(result)["pageData"];
			       if(pageData.length>0){
			    	   
			    	   for(var i=0;i<chk_value.length;i++){
			    		   $("#tr_"+chk_value[i]).remove();//移除当前的元素
			    	   }
			    	   for(var i=0;i<pageData.length;i++){
			    		   var dy=pageData[i];
			    		   reviewTableTr(dy);
			    	   }
			       }
			       
			    });
				return true;
			} 
		})
		 
	</script>
</body>
</html>