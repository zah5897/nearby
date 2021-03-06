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
				<strong class="icon-reorder">&nbsp;所有用户</strong>
			</div>
			
			<div class="padding border-bottom">
			   
				<ul class="search">
				 <li> 用户类型：</li>
				  <li>
				    <select id="user_type" name="user_type" class="input" onchange="changeType(this)"  style="line-height:17px;display:inline-block">
                          <option value="-1">所有用户</option>
                          <option value="1">正式用户</option>
                          <!-- 
                           <option value="0">游客用户</option>
                           <option value="2">非正式用户</option>
                           -->
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
			
		 
			
			<table class="table table-hover text-center" style="">
				<tr>
					<th width="10%">ID</th>
					<th width="5%">设备</th>
					<th width="8%">昵称</th>
					<th width="10%">头像</th>
					<th width="5%">性别</th>
					<th width="5%">类型</th>
					<th width="57%">操作</th>
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
	    var type=-1;
	    var keyword;
	    var user_id;
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
	    
	    function doSearch(){
	    	keyword=$("[name='keywords']").val().replace(/^\s+|\s+$/g,"");
	    	user_id=$("[name='user_id_input']").val().replace(/^\s+|\s+$/g,"");
	    	currentPageIndex=0;
	    	page(1);
	    }
	    
	     //获取对应页面
		function page(index) {
			if (currentPageIndex == index) {
				return false;
			}
			$.post("<%=path%>/manager/list_user_all",{'pageIndex':index,'pageSize':pageSize,'type':type,'keyword':keyword,'user_id':user_id},function(result){
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
		function edit_fun(user_id){
			alert(user_id);
		}
		
		function add_user_black(user_id){
			$.post("<%=path%>/manager/add_user_black",{'user_id':user_id,'fun':1},function(result){
				 var json=JSON.parse(result);
				 if(json.code==0){
			        	parent.toast("操作成功！");
			        	$("#black_"+user_id).hide();
			        }else{
			        	parent.toast("操作失败！");
			        }
		    });
		}
		
		
		function edit_avatar_state(user_id){
			$.post("<%=path%>/manager/edit_avatar_state_to_illegal",{'user_id':user_id},function(result){
				 
				 var json=JSON.parse(result);
				 if(json.code==0){
		        	 $("#"+user_id).hide();
		        	 $("#img_"+user_id).hide();
		        	 parent.toast("操作成功！");
		        }else{
		        	parent.toast("操作失败！");
		        }
		    });
		}
		
		function nick_name_illegal(user_id){
			$.post("<%=path%>/manager/nick_name_illegal",{'user_id':user_id},function(result){
				 var json=JSON.parse(result);
				 if(json.code==0){
					 $("#td_nick_name_"+user_id).html(json.nick_name);
			        }else{
			        	parent.toast("操作失败！");
			        }
		    });
		}
		function add_to_meet_bottle(user_id){
			$.post("<%=path%>/manager/add_user_meet_bottle",{'user_id':user_id,'fun':1},function(result){
				 var json=JSON.parse(result);
			        if(json.code==0){
			        	parent.toast("操作成功！");
			        	$("#meet_"+user_id).hide();
			        }else{
			        	parent.toast("操作失败！");
			        }
		    });
		}
	  //
      function reviewTableTr(pageData,tr) {
			 var currentItem=$("tr#tr_"+pageData["user_id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 
			 var state=pageData['state'];
			 
			 var user_id=pageData['user_id'];
			 
			 var toAdd="<tr id='tr_"+user_id+"'>";
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+pageData["user_id"]+"' />"+pageData["user_id"]+"</td>";
			 
			 
			 var channel=pageData["channel"];
			 if(!channel){
				 channel="iPhone" 
			 }
			 toAdd+="<td>"+channel+"</td>";
			 
			 var nick_name=pageData.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 if(state==1){
				 toAdd+="<td id='td_nick_name_"+user_id+"'>"+nick_name+"</td>";
			 }else{
				 toAdd+="<td id='td_nick_name_"+user_id+"'>"+nick_name+"</td>";
			 }
			 
			 
			 if(pageData.avatar!=null){
				 toAdd+="<td><img id='img_"+user_id+"' src='"+pageData.avatar+"' alt='"+pageData.origin_avatar+"'  height='50' onclick='show(this)'/></td>";
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
			  toAdd+="<a class='button border-main' href='javascript:void(0)'	onclick='return nick_name_illegal("+user_id+")'><span class='icon-edit'></span>昵称违规</a>";
		
			 
			  
			  if(state==0){
				  toAdd+="<a class='button border-yellow' id='black_"+user_id+"' href='javascript:void(0)'	onclick='return add_user_black("+user_id+")'><span class='icon-edit'></span>加入黑名单</a>";
			  }
			  
			  if(pageData.avatar&&pageData.avatar.indexOf('illegal.jpg')==-1){
				  toAdd+="<a id="+user_id+" class='button border-yellow' href='javascript:void(0)'	onclick='return edit_avatar_state("+user_id+")'><span class='icon-edit'></span>头像违法</a>";
			  }
			  
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
		 
		 
		 
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