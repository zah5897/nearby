package com.zhan.app.nearby.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easemob.server.example.HXHistoryMsg;
import com.zhan.app.nearby.dao.HXDao;

@Service
public class HXService {
	@Autowired
	private HXDao hxDao;

	public void insert(HXHistoryMsg msg) {
		hxDao.insert(msg);
	}
	public List<HXHistoryMsg> list(String type, String keywords,int page,int count) {
		return hxDao.loadHXHistoryMsgs(null, null, type, keywords, page, count);
	} 

	public int getCount(String type, String keywords,int page,int count) {
		return hxDao.getCount(null, null, type, keywords, page, count);
	}
	public HXHistoryMsg getHistoryMsgById(String msg_id) {
		return hxDao.getHistoryMsgById(msg_id);
	}
	public void clearExpireHistoryMsg() {
		hxDao.clearExpireHistoryMsg();
	}
}
