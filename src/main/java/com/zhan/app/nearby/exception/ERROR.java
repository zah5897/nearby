package com.zhan.app.nearby.exception;

public enum ERROR {
	ERR_FAILED(-1, "操作失败"), 
	ERR_NO_ERR(0, "操作成功"),
	ERR_NO_LOGIN(1, "当前账号没登录"), 
	ERR_PARAM(2, "参数异常:"), 
	ERR_SYS(3,"系统错误"), 
	ERR_USER_EXIST(4, "该用户已存在"),
	ERR_FILE_UPLOAD(5, "文件上传失败"),
	ERR_USER_NOT_EXIST(6,"用户不存在"), 
	ERR_PASSWORD(7, "密码错误"),
	ERR_FREUENT(8, "操作频繁"), 
	ERR_NO_AGREE(9,	"非法请求"),
	ERR_NOT_EXIST(10, "数据不存在"),
	ERR_NOT_VIP(11, "非会员"),
	ERR_VIP_EXPIRE(12, "会员过期"),
	ERR_COINS_SHORT(13, "金币不足"),
	ERR_ZHIFUBAO_ACCOUNT_NOT_MATCH(14, "支付宝账号错误，与绑定的支付宝账号不匹配"),
	ERR_ACCOUNT_BLACKLIST(15, "该账号处于黑名单状态"),
	ERR_SMS_CODE_LIMIT(16, "验证码发送次数已超每日限制"),
	ERR_USER_CLOSE(17, "该帐号已注销");
	private int value;
	private String errorMsg;

	private ERROR(int code, String errorMsg) {
		this.value = code;
		this.errorMsg = errorMsg;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
