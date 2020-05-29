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
<meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="renderer" content="webkit">
<title></title>
<link rel="stylesheet" href="<%=path%>/css/pintuer.css">
<link rel="stylesheet" href="<%=path%>/css/admin.css">
<script src="<%=path%>/js/jquery.js"></script>
<script src="<%=path%>/js/pintuer.js"></script>
<script src="<%=path%>/js/voice-2.0.js"></script>

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
			         <input type="text" placeholder="请输入关键字" name="keywords" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearch()" >搜索</a>
                   </li> 
				</ul>
			</div>



			<table class="table table-hover text-center">
				<tr>
					<th width="10%">发送者</th>
					<th width="10%">发送者头像</th>
					<th width="10%">接收者</th>
					<th width="10%">接收者头像</th>
					<th width="5%">类型</th>
					<th width="10%">内容</th>
					<th width="10%">时间</th>
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
	   RongIMLib.RongIMVoice.init();
	    //页面索引记录
	    var currentPageIndex = 0;
	    var pageSize = 10;
	    var pageCount = 100;
	    var type;
	    var keywords;
	    var playerQT;
	    //默认加载第一页
	    $(document).ready(function(){ 
		    page(1);
		}); 
	    

	    
	    function doSearch(){
	    	keywords=$("[name='keywords']").val().replace(/^\s+|\s+$/g,"");
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
			$.post("<%=path%>/manager/list_hx_chat_history_msgs",{'page':index,'count':pageSize,'type':type, 'keywords':keywords},function(result){
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
			var pageData=json["msgs"];
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
			 var currentItem=$("tr#tr_"+pageData["msg_id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var id=pageData['msg_id'];
			 var toAdd="<tr id='tr_"+id+"'>";
			 
			 toAdd+="<td>"+pageData.from_id+"|"+pageData.from_nick_name+"</td>";
			 toAdd+="<td><img  src='"+pageData.from_avatar+"' alt='"+pageData.from_avatar+"'  height='50'/></td>";
			 
			 
			 toAdd+="<td>"+pageData.to_id+"|"+pageData.to_nick_name+"</td>";
			 toAdd+="<td><img  src='"+pageData.to_avatar+"' alt='"+pageData.to_avatar+"'  height='50'/></td>";
			 
			 var cType=pageData.type;
			 toAdd+="<td>"+cType+"</td>";//类型
			 if(cType=='txt'){
				 toAdd+="<td>"+pageData.content+"</td>"; //文本
			 }else if(cType=='img'){ //图片
				 toAdd+="<td> <button class='icon-audio' style='margin:0 5px;cursor:pointer;' onclick='return showImg("+id+")'>查看</button></td>";
			 }else if(cType=='loc'){ //loc
				 toAdd+="<td>"+pageData.content+"</td>"; //文本
			 }else if(cType=='audio'){ //loc
				 toAdd+="<td> <button class='icon-audio' style='margin:0 5px;cursor:pointer;' onclick='return downloadMsg("+id+")'>播放</button></td>";
			 }else if(cType=='video'){ //loc
				 toAdd+="<td> <button class='icon-audio' style='margin:0 5px;cursor:pointer;' onclick='return downloadMsg("+id+")'>播放</button></td>";
			 }else if(cType=='file'){ //loc
				 toAdd+="<td> <button class='icon-audio' style='margin:0 5px;cursor:pointer;' onclick='return downloadMsg("+id+")'>下载</button></td>";
			 }
			 
			 toAdd+="<td>"+pageData['send_time']+"</td></tr>";
			 tr.after(toAdd);
		}
	    function downloadMsg(id){
	    	 $.get("<%=path%>/manager/download_msg?msg_id="+id, 
	    		  function(result){
	    		  var json=JSON.parse(result);
	    		   if(json.code==0){
	    			   if(json.type=='audio'){
	    				   RongIMLib.RongIMVoice.play(json.file);
	    			   }else if(json.type=='img'){
	    				   showBase64(json.file);
	    			   }
	    	    	 }
	    		  });
	    	
	    	 return false;
        }  
	    function showImg(id){
	    	var url="<%=path%>/manager/download_msg_img?msg_id="+id;
	    	parent.showImg(url);
	    }
		 
	</script>
</body>
</html>