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
				<strong class="icon-reorder">&nbsp;提现管理</strong>
			</div>
			
			<div class="padding border-bottom">
			   
				<ul class="search">
				 <li> 类型：</li>
				  <li>
				    <select id="user_type" name="user_type" class="input" onchange="changeType(this)"  style="line-height:17px;display:inline-block" >
                          <option value="1">所有提现</option>
                          <option value="2"  selected = "selected">待处理提现</option>
                          <option value="3">已审核处理</option>
                      </select>
				  </li>
				</ul>
			</div>
			
		 
			
			<table class="table table-hover text-center">
				<tr>
					<th width="5%">用户ID</th>
					<th width="10%">用户昵称</th>
					<th width="5%">钻石数量</th>
					<th width="10%">RMB金额(元)</th>
					<th width="10%">提现姓名</th>
					<th width="15%">身份证</th>
					<th width="10%">提现支付宝账号</th>
					<th width="10%">提交申请时间</th>
					<th width="10%">处理时间</th>
				    <th width="5%">状态</th>
					<th width="10%">处理</th>
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
	    var type=2;
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
			$.post("<%=path%>/manager/list_exchange_history",{'pageIndex':index,'pageSize':pageSize,'type':type},function(result){
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
			var pageData=json["exchanges"];
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
	  //
      function reviewTableTr(pageData,tr) {
			 var currentItem=$("tr#tr_"+pageData["id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var id=pageData['id'];
			 
			 var toAdd="<tr id='tr_"+id+"'>";
			 toAdd+="<td><input type='checkbox' name='id[]' value='"+id+"' />"+pageData["user_id"]+"</td>";
			 var nick_name=pageData.nick_name;
			 nick_name=nick_name==undefined?"":nick_name;
			 toAdd+="<td>"+nick_name+"</td>";
			 //用户类型
			 toAdd+="<td>"+pageData.diamond_count+"</td>";
			 
			  var num=new Number(pageData.rmb_fen);
			  num=num/100.0;
			  num=num.toFixed(2);
			 
			 toAdd+="<td>"+num+"</td>";
			 toAdd+="<td>"+pageData.personal_name+"</td>";
			 toAdd+="<td>"+pageData.personal_id+"</td>";
			 toAdd+="<td>"+pageData.zhifubao_access_number+"</td>";
			 
		     toAdd+="<td>"+pageData.create_time+"</td>";
		     
		     
				 if(pageData.finish_time){
					 toAdd+="<td>"+pageData.finish_time+"</td>"; 
				 }else{
					 toAdd+="<td>#</td>";
				 }
				 
			 var stateStr;
			 if(pageData.state==0){
				 stateStr="<font color='orange'>等待处理</font>";
			 }else if(pageData.state==1){
				 stateStr="<font color='green'>审核通过</font>";
			 }else if(pageData.state==2){
				 stateStr="<font color='red'>已被拒绝</font>";
			 }else{
				 stateStr="<font color='green'>已通过，正在打款</font>";
			 }
			 toAdd+="<td>"+stateStr+"</td>";
			 
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  
			  
			  if(pageData.state==0){
				  toAdd+="<a class='button border-main' href='javascript:void(0)'	onclick='return handleExchange("+id+",true)'><span class='icon-edit'></span>同意体现</a>";
				  toAdd+="<a class='button border-yellow' href='javascript:void(0)'	onclick='return handleExchange("+id+",false)'><span class='icon-edit'></span>拒绝提现</a>";
			  } 
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	    
	  
	  function handleExchange(id,agreeOrReject){
		  $.post("<%=path%>/manager/exchange_handle",{'id':id,'agreeOrReject':agreeOrReject,'type':type,'pageIndex':currentPageIndex,'pageSize':pageSize},function(result){
				 var json=JSON.parse(result);
				 if(json.code==0){
			        	$("table tr[id*='tr_'").each(function(i){
				        	this.remove();//移除当前的元素
				        })
			        	refreshTable(json);
			        }
		    });
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