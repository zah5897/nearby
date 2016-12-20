<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="UTF-8">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <meta name="renderer" content="webkit">
    <title></title>  
    <link rel="stylesheet" href="<%=path %>/css/pintuer.css">
    <link rel="stylesheet" href="<%=path %>/css/admin.css">
    <script src="<%=path %>/js/jquery.js"></script>
    <script src="<%=path %>/js/pintuer.js"></script>  
</head>
<body>
<form method="post" action="">
  <div class="panel admin-panel">
    <div class="panel-head"><strong class="icon-reorder">留言板</strong></div>
    <div class="padding border-bottom">
      <ul class="search">
        <li>
          <button type="button"  class="button border-green" id="checkall"><span class="icon-check"></span>全选</button>
          <button type="submit" class="button border-red"><span class="icon-trash-o"></span>批量删除</button>
        </li>
      </ul>
    </div>
    <table class="table table-hover text-center">
      <tr>
        <th width="120">ID</th>
        <th>å§å</th>       
        <th>çµè¯</th>
        <th>é®ç®±</th>
        <th>å¶ä»</th>
        <th width="25%">åå®¹</th>
         <th width="120">çè¨æ¶é´</th>
        <th>æä½</th>       
      </tr>      
        <tr>
          <td><input type="checkbox" name="id[]" value="1" />
            1</td>
          <td>ç¥å¤</td>
          <td>13420925611</td>
          <td>564379992@qq.com</td>  
           <td>æ·±å³å¸æ°æ²»è¡é</td>         
          <td>è¿æ¯ä¸å¥åå°UIï¼åæ¬¢çæåè¯·å¤å¤æ¯æè°¢è°¢ã</td>
          <td>2016-07-01</td>
          <td><div class="button-group"> <a class="button border-red" href="javascript:void(0)" onclick="return del(1)"><span class="icon-trash-o"></span>删除</a> </div></td>
        </tr>
        <tr>
          <td><input type="checkbox" name="id[]" value="1" />
            1</td>
          <td>ç¥å¤</td>
          <td>13420925611</td>
          <td>564379992@qq.com</td>  
           <td>æ·±å³å¸æ°æ²»è¡é</td>         
          <td>è¿æ¯ä¸å¥åå°UIï¼åæ¬¢çæåè¯·å¤å¤æ¯æè°¢è°¢ã</td>
          <td>2016-07-01</td>
          <td><div class="button-group"> <a class="button border-red" href="javascript:void(0)" onclick="return del(1)"><span class="icon-trash-o"></span>删除</a> </div></td>
        </tr>
          <tr>
          <td><input type="checkbox" name="id[]" value="1" />
            1</td>
          <td>ç¥å¤</td>
          <td>13420925611</td>
          <td>564379992@qq.com</td>  
           <td>æ·±å³å¸æ°æ²»è¡é</td>         
          <td>è¿æ¯ä¸å¥åå°UIï¼åæ¬¢çæåè¯·å¤å¤æ¯æè°¢è°¢ã</td>
          <td>2016-07-01</td>
          <td><div class="button-group"> <a class="button border-red" href="javascript:void(0)" onclick="return del(1)"><span class="icon-trash-o"></span>删除</a> </div></td>
        </tr>
          <tr>
          <td><input type="checkbox" name="id[]" value="1" />
            1</td>
          <td>ç¥å¤</td>
          <td>13420925611</td>
          <td>564379992@qq.com</td>  
           <td>æ·±å³å¸æ°æ²»è¡é</td>         
          <td>è¿æ¯ä¸å¥åå°UIï¼åæ¬¢çæåè¯·å¤å¤æ¯æè°¢è°¢ã</td>
          <td>2016-07-01</td>
          <td><div class="button-group"> <a class="button border-red" href="javascript:void(0)" onclick="return del(1)"><span class="icon-trash-o"></span>删除</a> </div></td>
        </tr>
          <tr>
          <td><input type="checkbox" name="id[]" value="1" />
            1</td>
          <td>ç¥å¤</td>
          <td>13420925611</td>
          <td>564379992@qq.com</td>  
           <td>æ·±å³å¸æ°æ²»è¡é</td>         
          <td>è¿æ¯ä¸å¥åå°UIï¼åæ¬¢çæåè¯·å¤å¤æ¯æè°¢è°¢ã</td>
          <td>2016-07-01</td>
          <td><div class="button-group"> <a class="button border-red" href="javascript:void(0)" onclick="return del(1)"><span class="icon-trash-o"></span>删除</a> </div></td>
        </tr>
      <tr>
        <td colspan="8"><div class="pagelist"> <a href="">上一页</a> <span class="current">1</span><a href="">2</a><a href="">3</a><a href="">下一页</a><a href="">尾页</a> </div></td>
      </tr>
    </table>
  </div>
</form>
<script type="text/javascript">

function del(id){
	if(confirm("æ¨ç¡®å®è¦å é¤å?")){
		
	}
}

$("#checkall").click(function(){ 
  $("input[name='id[]']").each(function(){
	  if (this.checked) {
		  this.checked = false;
	  }
	  else {
		  this.checked = true;
	  }
  });
})

function DelSelect(){
	var Checkbox=false;
	 $("input[name='id[]']").each(function(){
	  if (this.checked==true) {		
		Checkbox=true;	
	  }
	});
	if (Checkbox){
		var t=confirm("æ¨ç¡®è®¤è¦å é¤éä¸­çåå®¹åï¼");
		if (t==false) return false; 		
	}
	else{
		alert("è¯·éæ©æ¨è¦å é¤çåå®¹!");
		return false;
	}
}

</script>
</body></html>