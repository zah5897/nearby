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
			         <input type="text" placeholder="请输入用户id" name="user_id_input" class="input" style="width:250px; line-height:17px;display:inline-block" />
                     <a href="javascript:void(0)" class="button border-main icon-search" onclick="doSearchById()" > 搜索</a>
                   </li> 
				</ul>
			</div>
		
		
			<table class="table table-hover text-center">
				<tr>
					<th width="10%">用户ID</th>
					<th width="10%">用户昵称</th>
					<th width="20%">用户头像</th>
					<th width="20%">签名内容</th>
					<th width="40%">操作</th>
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
			$.post("<%=path%>/manager/load_signature_update_users",{'page':index,'count':pageSize,'user_id':user_id},function(result){
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
		
	    function showVideo(img){
			   var alt=$(img).attr("alt");
			   var vu=$(img).attr("vu");
			   var url='<%=path %>/manager/forword?path=play_video';
			   
			   url+="&thumb="+alt+"&vu="+vu;
			   
			  // window.open ('play_video.jsp?thumb="+alt+"&vu="+vu', 'newwindow', 'height=100, width=400, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no')  
			   openwindow(url,'短视频播放',800,500);
		}
	    
	    
	    function openwindow(url,name,iWidth,iHeight)
	    {
	          var url; //转向网页的地址;
	         var name; //网页名称，可为空;
	         var iWidth; //弹出窗口的宽度;
	         var iHeight; //弹出窗口的高度;
	    //window.screen.height获得屏幕的高，window.screen.width获得屏幕的宽
	          var iTop = (window.screen.height-30-iHeight)/2; //获得窗口的垂直位置;
	         var iLeft = (window.screen.width-10-iWidth)/2; //获得窗口的水平位置;
	         window.open(url,name,'height='+iHeight+',,innerHeight='+iHeight+',width='+iWidth+',innerWidth='+iWidth+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=auto,resizeable=no,location=no,status=no');
	    }
	    
	  //
      function reviewTableTr(pageData,tr) {
			 var currentItem=$("tr#tr_"+pageData["user_id"]);
			 if(currentItem.length>0){
				 return;
			 }
			 var uid=pageData['user_id'];
			 
			 var toAdd="<tr id='tr_"+uid+"'>";
			 toAdd+="<td>"+uid+"</td>";
			 toAdd+="<td>"+pageData['nick_name']+"</td>";
	 
			 toAdd+="<td><img id='img_"+uid+"' src='"+pageData.avatar+"' alt='"+pageData.origin_avatar+"'  height='50' onclick='show(this)'/></td>";
			 
			 
			 toAdd+="<td>"+pageData.signature+"</td>";
			 //操作单元格
			  toAdd+="<td><div class='button-group'>";
			  //操作单元格
		      toAdd+="<a class='button border-red' href='javascript:void(0)'	onclick='return del("+uid+")'><span class='icon-edit'></span>删除</a>";
			  toAdd+="</div></td></tr>";
			 tr.after(toAdd);
		}
	  
	    function show(img){
	    	parent.showOriginImg(img);
	    }
	   
	    
 	    function del(uid){
			$.post("<%=path%>/manager/delete_user_signature", {
				'uid' : uid,
				'page' : currentPageIndex,
				'user_id':user_id,
				'count' : pageSize
			}, function(result) {
				var json = JSON.parse(result);
				if (json.code == 0) {
					$("table tr[id*='tr_'").each(function(i) {
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

		var current_bottle_id = 0;

		function doSearch() {

			var key = $("[name='bottle_id']").val().replace(/^\s+|\s+$/g, "");

			var intKey;

			intKey = Number(key);

			if (isNaN(intKey)) {
				alert("请输入数字");
				return;
			}

			if (intKey == current_bottle_id) {
				return;
			}
			currentPageIndex = 0;
			current_bottle_id = intKey;
			page(1);
		}
	</script>
</body>
</html>