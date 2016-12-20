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
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span>å¢å åå®¹</strong></div>
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
          <label>å¾çï¼</label>
        </div>
        <div class="field">
          <input type="text" id="url1" name="img" class="input tips" style="width:25%; float:left;"  value=""  data-toggle="hover" data-place="right" data-image="" />
          <input type="button" class="button bg-blue margin-left" id="image1" value="+ æµè§ä¸ä¼ "  style="float:left;">
          <div class="tipss">å¾çå°ºå¯¸ï¼500*500</div>
        </div>
      </div>     
      
      <if condition="$iscid eq 1">
        <div class="form-group">
          <div class="label">
            <label>åç±»æ é¢ï¼</label>
          </div>
          <div class="field">
            <select name="cid" class="input w50">
              <option value="">è¯·éæ©åç±»</option>
              <option value="">äº§ååç±»</option>
              <option value="">äº§ååç±»</option>
              <option value="">äº§ååç±»</option>
              <option value="">äº§ååç±»</option>
            </select>
            <div class="tips"></div>
          </div>
        </div>
        <div class="form-group">
          <div class="label">
            <label>å¶ä»å±æ§ï¼</label>
          </div>
          <div class="field" style="padding-top:8px;"> 
            é¦é¡µ <input id="ishome"  type="checkbox" />
            æ¨è <input id="isvouch"  type="checkbox" />
            ç½®é¡¶ <input id="istop"  type="checkbox" /> 
         
          </div>
        </div>
      </if>
      <div class="form-group">
        <div class="label">
          <label>æè¿°ï¼</label>
        </div>
        <div class="field">
          <textarea class="input" name="note" style=" height:90px;"></textarea>
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>åå®¹ï¼</label>
        </div>
        <div class="field">
          <textarea name="content" class="input" style="height:450px; border:1px solid #ddd;"></textarea>
          <div class="tips"></div>
        </div>
      </div>
     
      <div class="clear"></div>
      <div class="form-group">
        <div class="label">
          <label>å³é®å­æ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_title" value="" />
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>åå®¹å³é®å­ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input" name="s_keywords" value=""/>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>å³é®å­æè¿°ï¼</label>
        </div>
        <div class="field">
          <textarea type="text" class="input" name="s_desc" style="height:80px;"></textarea>
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
          <label>åå¸æ¶é´ï¼</label>
        </div>
        <div class="field"> 
          <script src="js/laydate/laydate.js"></script>
          <input type="text" class="laydate-icon input w50" name="datetime" onclick="laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})" value=""  data-validate="required:æ¥æä¸è½ä¸ºç©º" style="padding:10px!important; height:auto!important;border:1px solid #ddd!important;" />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>ä½èï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="authour" value=""  />
          <div class="tips"></div>
        </div>
      </div>
      <div class="form-group">
        <div class="label">
          <label>ç¹å»æ¬¡æ°ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="views" value="" data-validate="member:åªè½ä¸ºæ°å­"  />
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