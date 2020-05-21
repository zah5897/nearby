package com.zhan.app.nearby.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.easemob.server.example.HXHistoryMsg;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.TextUtils;

@Repository
public class HXDao extends BaseDao<HXHistoryMsg> {

	public List<HXHistoryMsg> loadHXHistoryMsgs(Long from, Long to, String type, String keywords, int page, int count) {

		String sql = "select msg.*,u.nick_name as from_nick_name ,u.avatar as from_avatar ,tu.nick_name as to_nick_name ,tu.avatar as to_avatar from " + getTableName() + " msg left join t_user u on msg.from_id=u.user_id left join t_user tu on tu.user_id=msg.to_id  where 1=1 ";

		List<Object> param = new ArrayList<Object>();
		if (TextUtils.isNotEmpty(type)) {
			sql += " and msg.type=?";
			param.add(type);
		}

		if ("txt".equals(type) && TextUtils.isNotEmpty(keywords)) {
			sql += " and msg.content like ?";
			param.add("%" + keywords + "%");
		}

		if (from != null) {
			sql += " and msg.from_id = ?";
			param.add(from);
		}
		if (to != null) {
			sql += " and msg.to_id = ?";
			param.add(to);
		}

		sql += " order by msg.send_time desc limit ?,?";
		param.add((page - 1) * count);
		param.add(count);

		return jdbcTemplate.query(sql, param.toArray(), getEntityMapper());
	}

	
	public int getCount(Long from, Long to, String type, String keywords, int page, int count) {

		String sql = "select count(*) from " + getTableName() + " msg  where 1=1 ";

		List<Object> param = new ArrayList<Object>();
		if (TextUtils.isNotEmpty(type)) {
			sql += " and msg.type=?";
			param.add(type);
		}

		if ("txt".equals(type) && TextUtils.isNotEmpty(keywords)) {
			sql += " and msg.content like ?";
			param.add("%" + keywords + "%");
		}

		if (from != null) {
			sql += " and msg.from_id = ?";
			param.add(from);
		}
		if (to != null) {
			sql += " and msg.to_id = ?";
			param.add(to);
		}

		sql += " order by msg.send_time desc limit ?,?";
		param.add((page - 1) * count);
		param.add(count);

		return jdbcTemplate.queryForObject(sql, param.toArray(),Integer.class);
	}

	public HXHistoryMsg getHistoryMsgById(String msg_id) {
		List<HXHistoryMsg> msgs= jdbcTemplate.query("select * from "+getTableName()+" where msg_id="+msg_id, getEntityMapper());
		return msgs.get(0);
	}
	public void clearExpireHistoryMsg() { 
		jdbcTemplate.update("delete from "+getTableName()+" where DATE_SUB(CURDATE(), INTERVAL 7 DAY) < date(send_time)");
	}
	
	
}
