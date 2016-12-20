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
<link rel="stylesheet" href="css/pintuer.css">
<link rel="stylesheet" href="css/admin.css">
<script src="js/jquery.js"></script>
<script src="js/pintuer.js"></script>
</head>
<body>
<div class="panel admin-panel">
  <div class="panel-head"><strong class="icon-reorder"> åå®¹åè¡¨</strong></div>
  <div class="padding border-bottom">
    <button type="button" class="button border-yellow" onclick="window.location.href='#add'"><span class="icon-plus-square-o"></span> æ·»å åç±»</button>
  </div>
  <table class="table table-hover text-center">
    <tr>
      <th width="5%">ID</th>
      <th width="15%">ä¸çº§åç±»</th>
      <th width="10%">æåº</th>
      <th width="10%">æä½</th>
    </tr>
    <tr>
      <td>1</td>
      <td>äº§ååç±»</td>
      <td>1</td>
      <td><div class="button-group"> <a class="button border-main" href="cateedit.html"><span class="icon-edit"></span> ä¿®æ¹</a> <a class="button border-red" href="javascript:void(0)" onclick="return del(1,2)"><span class="icon-trash-o"></span> å é¤</a> </div></td>
    </tr>
    <tr>
      <td>1</td>
      <td>äº§ååç±»</td>
      <td>1</td>
      <td><div class="button-group"> <a class="button border-main" href="cateedit.html"><span class="icon-edit"></span> ä¿®æ¹</a> <a class="button border-red" href="javascript:void(0)" onclick="return del(1,2)"><span class="icon-trash-o"></span> å é¤</a> </div></td>
    </tr>
    <tr>
      <td>1</td>
      <td>äº§ååç±»</td>
      <td>1</td>
      <td><div class="button-group"> <a class="button border-main" href="cateedit.html"><span class="icon-edit"></span> ä¿®æ¹</a> <a class="button border-red" href="javascript:void(0)" onclick="return del(1,2)"><span class="icon-trash-o"></span> å é¤</a> </div></td>
    </tr>
    <tr>
      <td>1</td>
      <td>äº§ååç±»</td>
      <td>1</td>
      <td><div class="button-group"> <a class="button border-main" href="cateedit.html"><span class="icon-edit"></span> ä¿®æ¹</a> <a class="button border-red" href="javascript:void(0)" onclick="return del(1,2)"><span class="icon-trash-o"></span> å é¤</a> </div></td>
    </tr>
    <tr>
      <td>1</td>
      <td>äº§ååç±»</td>
      <td>1</td>
      <td><div class="button-group"> <a class="button border-main" href="cateedit.html"><span class="icon-edit"></span> ä¿®æ¹</a> <a class="button border-red" href="javascript:void(0)" onclick="return del(1,2)"><span class="icon-trash-o"></span> å é¤</a> </div></td>
    </tr>
    <tr>
      <td>1</td>
      <td>äº§ååç±»</td>
      <td>1</td>
      <td><div class="button-group"> <a class="button border-main" href="cateedit.html"><span class="icon-edit"></span> ä¿®æ¹</a> <a class="button border-red" href="javascript:void(0)" onclick="return del(1,2)"><span class="icon-trash-o"></span> å é¤</a> </div></td>
    </tr>
  </table>
</div>
<script type="text/javascript">
function del(id,mid){
	if(confirm("æ¨ç¡®å®è¦å é¤å?")){			
		
	}
}
</script>
<div class="panel admin-panel margin-top">
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span>æ·»å åå®¹</strong></div>
  <div class="body-content">
    <form method="post" class="form-x" action="">
      <div class="form-group">
        <div class="label">
          <label>ä¸çº§åç±»ï¼</label>
        </div>
        <div class="field">
          <select name="pid" class="input w50">
            <option value="">è¯·éæ©åç±»</option>
            <option value="">äº§ååç±»</option>
            <option value="">äº§ååç±»</option>
            <option value="">äº§ååç±»</option>
            <option value="">äº§ååç±»</option>
          </select>
          <div class="tips">ä¸éæ©ä¸çº§åç±»é»è®¤ä¸ºä¸çº§åç±»</div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>åç±»æ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="title" />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>æ¹éæ·»å ï¼</label>
        </div>
        <div class="field">
          <textarea type="text" class="input w50" name="tits" style="height:150px;" placeholder="å¤ä¸ªåç±»æ é¢è¯·è½¬è¡"></textarea>
          <div class="tips">å¤ä¸ªåç±»æ é¢è¯·è½¬è¡</div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>å³é®å­æ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_title" />
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>åç±»å³é®å­ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_keywords" />
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>å³é®å­æè¿°ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_desc"/>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>æåºï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="sort" value="0"  data-validate="number:æåºå¿é¡»ä¸ºæ°å­" />
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
</body>
</html>