<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
%>
<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<title>漂流瓶提现</title>
<style>
body, input, button {
	font: normal 14px "Microsoft Yahei";
	margin: 0;
	padding: 0
}

.odform-tit {
	font-weight: normal;
	font-size: 25px;
	color: #595757;
	line-height: 40px;
	text-align: center;
	border-bottom: 1px solid #c9cacb;
	margin: 0;
	padding: 5% 0
}

.odform-tit img {
	height: 40px;
	vertical-align: middle;
	margin-right: 15px
}

.odform {
	padding: 5%
}

.input-group {
	margin-bottom: 5%;
	position: relative
}

.input-group label {
	padding: 2% 0;
	position: absolute;
	color: #595757
}

.input-group input {
	margin-left: 7em;
	padding: 3% 5%;
	box-sizing: border-box;
	background: #efeff0;
	border: 0;
	border-radius: 5px;
	color: #595757;
	width: 75%
}


.input-group .input_short {
	margin-left: 7em;
	padding: 3% 5%;
	box-sizing: border-box;
	background: #efeff0;
	border: 0;
	border-radius: 5px;
	color: #595757;
	width: 30%
}

.odform button {
	background: #8ec31f;
	color: #fff;
	text-align: center;
	border: 0;
	border-radius: 10px;
	padding: 3%;
	width: 100%;
	font-size: 16px
}

.odform .cal {
	background-image: url(images/daetixian-cal.png);
	background-repeat: no-repeat;
	background-position: 95% center;
	background-size: auto 50%
}

.odform .xl {
	background-image: url(images/daetixian-xl.png);
	background-repeat: no-repeat;
	background-position: 95% center;
	background-size: auto 20%
}
.a_btn{
    background: #8ec31f;
	color: #fff;
	text-align: center;
	border: 0;
	border-radius: 10px;
	font-size: 16px;
	text-decoration:none
	
    margin-left: 7em;
	padding: 3% 5%;
	box-sizing: border-box;
	width: 10%
}

.odform .sms_btn {
	background: #8ec31f;
	color: #fff;
	text-align: center;
	border: 0;
	border-radius: 10px;
	padding: 3%;
	width: 30%;
	font-size: 16px
}


.m-toast-pop {display: none; position: fixed; width: 100%;top: 0;bottom: 0;right: 0;overflow: auto;text-align: center;}      
.m-toast-inner {position: absolute;left:50%;top:50%;width: 100%; transform:translate(-50%,-50%);-webkit-transform:translate(-50%,-50%);text-align: center;}
.m-toast-inner-text{display: inline-block;margin: 0 22px; padding: 19px 21px;font-size: 16px;color: #FFFFFF;letter-spacing: 0;line-height: 22px;background: rgba(0,0,0,0.72);border-radius: 10px;}   
 
</style>
 <script src="<%=path%>/js/jquery.js"></script>
 <script type="text/javascript">
 
       var mobile;
    // Firefox, Google Chrome, Opera, Safari, Internet Explorer from version 9
        function OnInput (event) {
    	   var val=event.target.value;
    	   getRmbVal(val);
        }
    // Internet Explorer
        function OnPropChanged (event) {
            if (event.propertyName.toLowerCase () == "value") {
                var val=event.srcElement.value
                getRmbVal(val);
            }
        }
    
        function getRmbVal(val){
        	var rmb=val*0.03
        	rmb = rmb.toFixed(2);
        	document.getElementById("rmb_input").value=rmb+"(元)";
        }
        
        function isnull(val) {
	        var str = val.replace(/(^\s*)|(\s*$)/g, '');//去除空格;
	        if (str == '' || str == undefined || str == null) {
	        	 //console.log('空')
	            return true;
	        } else {
	        	//console.log('非空');
	            return false;
	        }
	    }

      
    var time = 60;
    var t; //倒计时对象
     function getcode() {
    	 var zhifubao=document.getElementById("zhifubao").value;
    	 if(isnull(zhifubao)){
    		 showToast("输入的支付宝帐号不能为空");
    		return false; 
    	 }
         if(time!=60){
             return false;
         }
          send_sms();
           t = setInterval(function () {
                        countdown()
                    }, 1000);
           countdown();
        return false;
     }
    
     function send_sms(){
          var uid=document.getElementById("user_id").value;
          var token=document.getElementById("token").value;
          $.post('<%=path%>/exchange/sned_bind_zhifubao_sms', {'user_id' : uid,'token':token}, function(data, status) {
				var json = JSON.parse(data);
				if (json.code == 0) {
					document.getElementById("mobile").value=json.mobile;
					showToast("短信验证码已经发送至漂流瓶app注册所用手机号码，请查收");
				}else{
				    showToast(json.msg);
				}
			});
           return false;
        }
    
    function countdown(){
        if (time == 0) {
            document.getElementById("smsBtn").innerText="获取验证码";
            time = 60;
            clearInterval(t);
        }else{
            document.getElementById("smsBtn").innerHTML="重新发送"+time;
            time--;
        }
    }
  function showToast(msg){
        $('#m-toast-inner-text').text(msg);    
        $('#m-toast-pop').fadeIn();    
        setTimeout(function() {    
         $('#m-toast-pop').fadeOut();    
        }, 2000); 
 
      }
    </script>

</head>

<body>
 
	<h1 class="odform-tit">
		 漂流瓶提现-绑定支付宝账号
	</h1>
	<div class="odform">
	 
		<form action="<%=path%>/exchange/do_bind_zhifubao" method="post">
			 
			<div class="input-group">
				<label for="khname">支付宝帐号：</label>
				<input  id="zhifubao"  name="zhifubao" placeholder="请输入支付宝帐号">
			</div>
			<input type="hidden" id="user_id"  name="user_id"  value="${user_id }">
			<input type="hidden" id="token"  name="token"  value="${token }"  >
			<input type="hidden" id="mobile"  name="mobile"   >
			<div class="input-group">
				<label for="khname">短信验证码：</label>
				<input  id="khname" class="input_short" name="code" placeholder="请输入短信验证码"  type="number" pattern="[0-9]*">
				<button type="button"  onclick="getcode()" class="sms_btn"  id="smsBtn">获取验证码</button>
				<!--<a id="sms_code" class="a_btn" href="#" onclick="send_sms()">获取验证码</a>  -->
			</div>
			<button>绑定支付宝帐号</button>
		</form>
	</div>

	<div
		style="text-align: center; margin: 50px 0; font: normal 14px/24px 'MicroSoft YaHei';">
		<p>漂流瓶官方邮箱：<a href="mailto:2448742182@qq.com">2448742182@qq.com</a></p>
		<p>客户经理QQ：<a href="http://wpa.qq.com/msgrd?v=3&uin=2448742182&site=qq&menu=yes">2448742182</a></p>
	</div>
	
	
	   <div id="m-toast-pop" class="m-toast-pop">      
          <div class="m-toast-inner"><div class="m-toast-inner-text" id="m-toast-inner-text">复制成功</div></div>      
       </div>  
	
</body>
</html>