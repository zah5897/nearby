package com.weibo;

import com.zhan.app.nearby.util.TextUtils;

import weibo4j.Oauth;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;
import weibo4j.util.BareBonesBrowserLaunch;

public class OAuth4Code {
	Oauth oauth;

	public void getAccessToken(String code) throws WeiboException {
		if (oauth == null) {
			oauth = new Oauth();
		}
		if (TextUtils.isEmpty(code)) {
			BareBonesBrowserLaunch.openURL(oauth.authorize("code"));
		} else {
			AccessToken token=oauth.getAccessTokenByCode(code);
			System.out.println(token.toString());
		}
	}
}
