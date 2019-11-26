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
    <title>ç½ç«ä¿¡æ¯</title>  
    <link rel="stylesheet" href="css/pintuer.css">
    <link rel="stylesheet" href="css/admin.css">
    <script src="js/jquery.js"></script>
    <script src="js/pintuer.js"></script>  
</head>
<body>
<div class="panel admin-panel">
  <div class="panel-head"><strong class="icon-reorder"> åå®¹åè¡¨</strong></div>
  <div class="padding border-bottom">  
  <a class="button border-yellow" href=""><span class="icon-plus-square-o"></span> æ·»å åå®¹</a>
  </div> 
  <table class="table table-hover text-center">
    <tr>
      <th width="5%">ID</th>     
      <th>æ ç®åç§°</th>  
      <th>æåº</th>     
      <th width="250">æä½</th>
    </tr>
   
    <tr>
      <td>17</td>      
      <td>å¬å¸ç®ä»</td>  
      <td>1</td>      
      <td>
      <div class="button-group">
      <a type="button" class="button border-main" href="#"><span class="icon-edit"></span>ä¿®æ¹</a>
       <a class="button border-red" href="javascript:void(0)" onclick="return del(17)"><span class="icon-trash-o"></span> å é¤</a>
      </div>
      </td>
    </tr> 
    
    <tr>
      <td>17</td>      
      <td>äº§åä¸­å¿</td>  
      <td>1</td>      
      <td>
      <div class="button-group">
      <a type="button" class="button border-main" href="#"><span class="icon-edit"></span>ä¿®æ¹</a>
       <a class="button border-red" href="javascript:void(0)" onclick="return del(17)"><span class="icon-trash-o"></span> å é¤</a>
      </div>
      </td>
    </tr>  
    
    <tr>
      <td>17</td>      
      <td>æ°é»èµè®¯</td>  
      <td>1</td>      
      <td>
      <div class="button-group">
      <a type="button" class="button border-main" href="#"><span class="icon-edit"></span>ä¿®æ¹</a>
       <a class="button border-red" href="javascript:void(0)" onclick="return del(17)"><span class="icon-trash-o"></span> å é¤</a>
      </div>
      </td>
    </tr>  
    
    <tr>
      <td>17</td>      
      <td>äººææè</td>  
      <td>1</td>      
      <td>
      <div class="button-group">
      <a type="button" class="button border-main" href="#"><span class="icon-edit"></span>ä¿®æ¹</a>
       <a class="button border-red" href="javascript:void(0)" onclick="return del(17)"><span class="icon-trash-o"></span> å é¤</a>
      </div>
      </td>
    </tr>  
    
    <tr>
      <td>17</td>      
      <td>èç³»æä»¬</td>  
      <td>1</td>      
      <td>
      <div class="button-group">
      <a type="button" class="button border-main" href="#"><span class="icon-edit"></span>ä¿®æ¹</a>
       <a class="button border-red" href="javascript:void(0)" onclick="return del(17)"><span class="icon-trash-o"></span> å é¤</a>
      </div>
      </td>
    </tr>   
    
  </table>
</div>
<script>
function del(id){
	if(confirm("æ¨ç¡®å®è¦å é¤å?")){
		
	}
}
</script>
<div class="panel admin-panel margin-top">
  <div class="panel-head" id="add"><strong><span class="icon-pencil-square-o"></span>å¢å åå®¹</strong></div>
  <div class="body-content">
    <form method="post" class="form-x" action="">   
      <input type="hidden" name="id"  value="" />  
      <div class="form-group">
        <div class="label">
          <label>æ ç®åç§°ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="title" value="" data-validate="required:è¯·è¾å¥æ é¢" />         
          <div class="tips"></div>
        </div>
      </div> 
      <div class="form-group">
        <div class="label">
          <label>æ ç®å¾çï¼</label>
        </div>
        <div class="field">
          <input type="text" id="url1" name="banner" class="input tips" style="width:25%; float:left;"  value="" data-toggle="hover" data-place="right" data-image="" />
          <input type="button" class="button bg-blue margin-left" id="image1" value="+ æµè§ä¸ä¼ "  style="float:left;">
          <div class="tipss">å¾çå°ºå¯¸ï¼1920*200</div>
        </div>
      </div>
       <div class="form-group">
        <div class="label">
          <label>è±ææ é¢ï¼</label>
        </div>
        <div class="field">
          <input type="text" class="input w50" name="entitle" value="" />         
          <div class="tips"></div>
        </div>
      </div>       
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
          <label>æ ç®å³é®å­ï¼</label>
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
          <textarea type="text" class="input" name="s_desc" style="height:100px;" ></textarea>        
        </div>
     </div>
    
     <div class="form-group">
        <div class="label">
          <label>æ¾ç¤ºï¼</label>
        </div>
        <div class="field">
          <div class="button-group radio">
          
          <label class="button active">
         	  <span class="icon icon-check"></span>             
              <input name="isshow" value="1" type="radio" checked="checked">æ¯             
          </label>             
        
          <label class="button active"><span class="icon icon-times"></span>          	
              <input name="isshow" value="0"  type="radio" checked="checked">å¦
          </label>         
           </div>       
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