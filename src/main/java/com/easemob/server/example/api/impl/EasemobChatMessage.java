package com.easemob.server.example.api.impl;

import com.easemob.server.example.api.ChatMessageAPI;
import com.easemob.server.example.api.EasemobRestAPI;
import com.easemob.server.example.comm.constant.HTTPMethod;
import com.easemob.server.example.comm.helper.HeaderHelper;
import com.easemob.server.example.comm.wrapper.HeaderWrapper;
import com.easemob.server.example.comm.wrapper.QueryWrapper;

public class EasemobChatMessage extends EasemobRestAPI implements ChatMessageAPI {

	private static final String ROOT_URI = "/chatmessages";

	public Object exportChatMessages(Long limit, String cursor, String query) {
		String url = getContext().getSeriveURL() + getResourceRootURI();
		HeaderWrapper header = HeaderHelper.getDefaultHeaderWithToken();
		QueryWrapper queryWrapper = QueryWrapper.newInstance().addLimit(limit).addCursor(cursor).addQueryLang(query);

		return getInvoker().sendRequest(HTTPMethod.METHOD_GET, url, header, null, queryWrapper);
	}

	public Object exportChatMessages(String timePoint) {
		String url = getContext().getSeriveURL() + getResourceRootURI();
		HeaderWrapper header = HeaderHelper.getDefaultHeaderWithToken();
		url += "/" + timePoint;
		return getInvoker().sendRequest(HTTPMethod.METHOD_GET, url, header, null, null);
	}
	
	public Object downloadAudioFile(String remoteUrl,String secretKey) {
		HeaderWrapper header = HeaderHelper.getDefaultHeaderWithToken();
		header.addHeader("share-secret", secretKey);
		return getInvoker().downloadFile(remoteUrl, header, null);
	}

	@Override
	public String getResourceRootURI() {
		return ROOT_URI;
	}
}
