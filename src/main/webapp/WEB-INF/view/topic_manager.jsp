<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="zh-cn">
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
<div class="panel admin-panel">
  <div class="panel-head"><strong class="icon-reorder"> åå®¹åè¡¨</strong></div>
  <div class="padding border-bottom">  
  <button type="button" class="button border-yellow" onclick="window.location.href='#add'"><span class="icon-plus-square-o"></span> æ·»å åå®¹</button>
  </div>
  <table class="table table-hover text-center">
    <tr>
      <th width="10%">ID</th>
      <th width="20%">å¾ç</th>
      <th width="15%">åç§°</th>
      <th width="20%">æè¿°</th>
      <th width="10%">æåº</th>
      <th width="15%">æä½</th>
    </tr>
   
    <tr>
      <td>1</td>     
      <td><img src="<%=path %>/images/11.jpg" alt="" width="120" height="50" /></td>     
      <td>é¦é¡µç¦ç¹å¾</td>
      <td>æè¿°æå­....</td>
      <td>1</td>
      <td><div class="button-group">
      <a class="button border-main" href="#add"><span class="icon-edit"></span> ä¿®æ¹</a>
      <a class="button border-red" href="javascript:void(0)" onclick="return del(1,1)"><span class="icon-trash-o"></span> å é¤</a>
      </div></td>
    </tr>
    <tr>
      <td>2</td>     
      <td><img src="<%=path %>/images/11.jpg" alt="" width="120" height="50" /></td>     
      <td>é¦é¡µç¦ç¹å¾</td>
      <td>æè¿°æå­....</td>
      <td>1</td>
      <td><div class="button-group">
      <a class="button border-main" href="#add"><span class="icon-edit"></span> ä¿®æ¹</a>
      <a class="button border-red" href="javascript:void(0)" onclick="return del(1,1)"><span class="icon-trash-o"></span> å é¤</a>
      </div></td>
    </tr>
    <tr>
      <td>3</td>     
      <td><img src="<%=path %>/images/11.jpg" alt="" width="120" height="50" /></td>     
      <td>é¦é¡µç¦ç¹å¾</td>
      <td>æè¿°æå­....</td>
      <td>1</td>
      <td><div class="button-group">
      <a class="button border-main" href="#add"><span class="icon-edit"></span> ä¿®æ¹</a>
      <a class="button border-red" href="javascript:void(0)" onclick="return del(1,1)"><span class="icon-trash-o"></span> å é¤</a>
      </div></td>
    </tr>
    
  </table>
</div>
<script type="text/javascript">
function del(id,mid){
	if(confirm("æ¨ç¡®å®è¦å é¤å?")){
	
	}
}
</script>
<div class="panel admin-panel margin-top" id="add">
  <div class="panel-head"><strong><span class="icon-pencil-square-o"></span> å¢å åå®¹</strong></div>
  <div class="body-content">
    <form method="post" class="form-x" action="">    
      <div class="form-group">
        <div class="label">
          <label>æ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" value="" name="title" data-validate="required:è¯·è¾å¥æ é¢" />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>URLï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="url" value=""  />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>å¾çï¼</label>
        </div>
        <div class="field">
          <input type="text" id="url1" name="img" class="input tips" style="width:25%; float:left;"  value="" data-toggle="hover" data-place="right" data-image="" />
          <input type="button" class="button bg-blue margin-left" id="image1" value="+ æµè§ä¸ä¼ "  style="float:left;">
          <div class="tipss">å¾çå°ºå¯¸ï¼1920*500</div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>æè¿°ï¼</label>
        </div>
        <div class="field">
          <textarea type="text" class="input" name="note" style="height:120px;" value=""></textarea>
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>æåºï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="sort" value="0"  data-validate="required:,number:æåºå¿é¡»ä¸ºæ°å­" />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label></label>
        </div>
        <div class="field">
          <button class="button bg-main icon-check-square-o" type="submit"> æäº¤</button>
        </div>
      </div>
    </form>
  </div>
</div>
</body></html>