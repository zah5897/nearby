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
			         <input type="text" placeholder="请输入用户昵称" name="nick_name_input" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearchByNickname()" > 搜索</a>
                   </li>
				  <li>
			         <input type="text" placeholder="请输入用户手机号" name="mobile_input" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearchByMobile()" > 搜索</a>
                   </li>
				</ul>
			</div>
			
		 
			
			<table class="table table-hover text-center">
				<tr>
					<th width="10%">ID</th>
					<th width="8%">昵称</th>
					<th width="12%">头像</th>
					<th width="5%">性别</th>
		            <th width="5%">类型</th>
					<th width="30%">操作</th>
				</tr>
				<tr id="bottom">
					<td colspan="8">
						<div class="pagelist">
							<a href="javascript:void(0)" onclick="return previous()">上一页</a>
							<a id="next_flag" href="javascript:void(0)"
								onclick="next()">下一页</a>
							<a href="javascript:void(0)"
								onclick="end()">尾页</a>
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
	    var nick_name;
	    var mobile;
	    //默认加载第一页
	    $(document).ready(function(){ 
		    page(1);
		}); 
		
	     
	    //前一页
		function previous() {
			alert('previous');
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
	    function doSearchByMobile(){
	    	var key=$("[name='mobile_input']").val().replace(/^\s+|\s+$/g,"");
	    	if(mobile==''){
	    		if(key==''){
		    		return;
		    	}
	    	}
	    	if(mobile==key){
	    		return;
	    	}
	    	mobile=key;
	    	currentPageIndex=0;
	    	page(1);
	    }
	    function doSearchByNickname(){
	    	var key=$("[name='nick_name_input']").val().replace(/^\s+|\s+$/g,"");
	    	if(nick_name==''){
	    		if(key==''){
		    		return;
		    	}
	    	}
	    	if(nick_name==key){
	    		return;
	    	}
	    	nick_name=key;
	    	currentPageIndex=0;
	    	page(1);
	    }
	   
	     //获取对应页面
		function page(index) {
			if (currentPageIndex == index) {
				return false;
			}
			$.post("<%=path%>/manager/list_user_black",{'pageIndex':index,'pageSize':pageSize,'mobile':mobile,'nick_name':nick_name},function(result){
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
		function del_from_found_user_black(uid){
			$.post("<%=path%>/manager/remove_user_black",{'mobile':mobile,'nick_name':nick_name,'uid':uid,'pageIndex':currentPageIndex,'pageSize':pageSize},function(result){
				 var json=JSON.parse(result);
				 if(json.code==0){
			        	$("table tr[id*='tr_'").each(function(i){
				        	this.remove();//移除当前的元素
				        })
			        	refreshTable(json);
			        }
		    });
		}
      function reviewTableTr(pageData,tr) {
			 var currentItem=$("tr#tr_"+pageData["user_id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var user_id=pageData['user_id'];
			 
			 var toAdd="<tr id='tr_"+user_id+"'>";
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+pageData["user_id"]+"' />"+pageData["user_id"]+"</td>";
			 var nick_name=pageData.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 toAdd+="<td>"+nick_name+"</td>";
			 
			 if(pageData.avatar!=null){
				 toAdd+="<td><img  src='"+pageData.avatar+"' alt='"+pageData.origin_avatar+"'  height='50' onclick='show(this)'/></td>";
			 }else{
				 toAdd+="<td><img  src='#' alt='#'  height='50'/></td>";
			 }
			
			 toAdd+="<td>"+(pageData["sex"]==0?"女":"男")+"</td>";
			 
			 //用户类型
			 var type=pageData["type"];
			 var typeStr;
			 if(type==0){
				 typeStr="游客" 
			 }else if(type==1){
				 typeStr="正式" 
			 }else if(type==3){
				 typeStr="第三方" 
			 }else{
				 typeStr="非正式" 
			 }
			 toAdd+="<td>"+typeStr+"</td>";
			 
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  
			  //toAdd+="<a class='button border-main' href='javascript:void(0)'	onclick='return edit_fun("+user_id+")'><span class='icon-edit'></span>编辑限制</a>";
			  toAdd+="<a class='button border-yellow' href='javascript:void(0)'	onclick='return del_from_found_user_black("+user_id+")'><span class='icon-edit'></span>移除</a>";
			  //toAdd+="<a class='button border-main' href='javascript:void(0)'	onclick='return add_to_meet_bottle("+user_id+")'><span class='icon-edit'></span>添加到邂逅瓶待选区</a>";
			  
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
		 
		$("#edit_to_visible").click(function() {
			editState(0);	 
		})
		$("#edit_to_gone").click(function() {
			editState(1);
		})
				
		 
		function changeType(selectView){
		   var	typeSelect=$('#user_type option:selected') .val();
		   if(type!=typeSelect){
			   type=typeSelect;
			   currentPageIndex=0;
			   page(1);
		   }
		}
	</script>
</body>
</html>