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
				<strong class="icon-reorder">&nbsp;管理动态</strong>
			</div>
			<div class="padding border-bottom">
				<ul class="search">
				    <li>
			         <input type="text" placeholder="请输入用户id" name="user_id_input" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()" >搜索</a>
                   </li> 
				   <li>
			         <input type="text" placeholder="请输入动态id" name="dy_id" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()" >搜索</a>
                   </li> 
				
					<li>
						<button type="button" class="button border-green" id="checkall">
							<span class="icon-check"></span>全选
						</button>
						<button type="button" id="del_batch" class="button border-red">
							<span class="icon-trash-o"></span>批量删除
						</button>
					</li>
				</ul>
			</div>
			<table class="table table-hover text-center">
				<tr>
					<th width="8%">ID</th>
					<th width="10%">用户ID|昵称</th>
					<th width="10%">图片</th>
					<th width="10%">内容</th>
					<th width="8%">城市</th>
					<th width="10%">时间</th>
					<th width="8%">赞|评</th>
					<th width="21%">操作</th>
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
	    var currentPage = 0;
	    var pageSize = 10;
	    var pageCount = 100;
	    var user_id;
	    
	    var dy_id;
	    
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
	    
		function end(){
		   page(pageCount);
		}
	    
		function doSearch(){
	    	dy_id=$("[name='dy_id']").val().replace(/^\s+|\s+$/g,"");
	    	user_id=$("[name='user_id_input']").val().replace(/^\s+|\s+$/g,"");
	    	currentPage=0;
	    	page(1);
	    }
	     //获取对应页面
		function page(index) {
			if (currentPage == index) {
				return false;
			}
			$.post("<%=path%>/manager/selected_dynamic_list",{'pageIndex':index,'user_id':user_id,'dy_id':dy_id},function(result){
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
			
			currentPage=json["currentPageIndex"]
			if(currentPage==1){
				pageCount=json["pageCount"];
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
			 if(i==currentPage){
         		  return "<a  class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'><span class='current'>"+i+"</span></a>";
         	  }else{
         		  return "<a class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'>"+i+"</a>";
         	  }
		}
		
		function getStartIndex(){
			   if(currentPage-2>0){
				   return currentPage-2;
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
		
		function getItem(currentPage,i){
			 if(i==currentPage){
          		  return "<a  class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'><span class='current'>"+i+"</span></a>";
          	  }else{
          		  return "<a class='pg_flag' href='javascript:void(0)' onclick='return page("+i+")'>"+i+"</a>";
          	  }
		}
		
		function del(id) {
				$.post("<%=path%>/manager/remove_from_selected",{id:id,currentPage:currentPage,'user_id':user_id,'dy_id':dy_id},function(result){
			        $("#tr_"+id).remove();//移除当前的元素
			        
			        var json=JSON.parse(result);
			        var pageData=json["data"];
			        
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
			 
			 
			 
			// var device=parent.getDeviceTxt(pageData['_from']);
			// toAdd+="<td>"+device+"</td>";
			 
			 
			 
			 var nick_name=pageData.user.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 
			 toAdd+="<td>"+pageData.user.user_id+"|"+nick_name+"</td>";
			 toAdd+="<td><img  src='"+pageData.thumb+"' alt='"+pageData.origin+"'  height='50' onclick='show(this)'/></td>";
			 toAdd+="<td>"+pageData["description"]+"</td>";
			 toAdd+="<td>"+pageData["city"]+"</td>";
			 
			 
			 
			 var time=parent.dateFtt("yyyy-MM-dd hh:mm:ss",new Date(pageData["create_time_v2"]*1000));
			 toAdd+="<td>"+time+"</td>";
			 
			 toAdd+="<td>"+pageData["praise_count"]+"|"+pageData["comment_count"]+"</td>";
			 toAdd+="<td><div class='button-group'><a class='button border-red' href='javascript:void(0)'	onclick='return del("+pageData["id"]+")'><span class='icon-trash-o'></span>移除推荐</a></div></td>";
			 toAdd+="</tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
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
		$("#del_batch").click(function() {
			var chk_value =[]; 
			$('input[name="id[]"]:checked').each(function(){ 
			    chk_value.push($(this).val()); 
			});
			if(chk_value.length>0){
			var ids=JSON.stringify(chk_value);
			$.post("<%=path%>/manager/removes_from_selected",
					{'ids' : ids,'user_id' : user_id,'currentPage' : currentPage,'nick_name':keyword},
				    function(result) {
							var json = JSON.parse(result);
							var pageData = json["data"];
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
				
				function imgClick(url){  
			     var obj = '<img src='+url+'/>';
			     alert(obj); 
		        }
	</script>
</body>
</html>