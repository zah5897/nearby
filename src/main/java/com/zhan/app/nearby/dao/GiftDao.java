package com.zhan.app.nearby.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.util.ObjectUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("giftDao")
public class GiftDao extends BaseDao {
	public static final String TABLE_NAME = "t_gift";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insert(Gift gift) {
		long id = saveObj(jdbcTemplate, TABLE_NAME, gift);
		gift.setId(id);
		return id;
	}

	public List<Gift> listGifts() {
		return jdbcTemplate.query("select *from " + TABLE_NAME, new BeanPropertyRowMapper<Gift>(Gift.class));
	}

	public void delete(long id) {
		jdbcTemplate.update("delete from " + TABLE_NAME + " where id=?", new Object[] { id });
	}

	public void update(Gift gift) {
		if (TextUtils.isEmpty(gift.getImage_url())) {
			jdbcTemplate.update(
					"update " + TABLE_NAME + " set name=?,price=?,old_price=?,description=?,remark=? where id=?",
					new Object[] { gift.getName(), gift.getPrice(), gift.getOld_price(), gift.getDescription(),
							gift.getRemark(), gift.getId() });
		} else {
			jdbcTemplate.update(
					"update " + TABLE_NAME
							+ " set name=?,price=?,old_price=?,image_url=?,description=?,remark=? where id=?",
					new Object[] { gift.getName(), gift.getPrice(), gift.getOld_price(), gift.getImage_url(),
							gift.getDescription(), gift.getRemark(), gift.getId() });
		}
	}

	public Gift load(int gift_id) {
		List<Gift> gifts = jdbcTemplate.query("select *from " + TABLE_NAME + " where id=?", new Object[] { gift_id },
				new BeanPropertyRowMapper<Gift>(Gift.class));
		if (gifts != null && gifts.size() > 0) {
			return gifts.get(0);
		}
		return null;
	}

	public GiftOwn getOwnGift(long user_id, int gift_id) {
		List<GiftOwn> gifts = jdbcTemplate.query("select * from t_gift_own where user_id=? and gift_id=?",
				new Object[] { user_id, gift_id }, new BeanPropertyRowMapper<GiftOwn>(GiftOwn.class));
		if (gifts != null && gifts.size() > 0) {
			return gifts.get(0);
		}
		return null;
	}

	public List<GiftOwn> getOwnGifts(long user_id) {
		return jdbcTemplate.query(
				"select * from t_gift_own own left join t_gift gift on own.gift_id=gift.id where own.user_id=?",
				new Object[] { user_id }, new BeanPropertyRowMapper<GiftOwn>(GiftOwn.class));
	}

	public int updateOwnCount(long user_id, int gift_id, int newCount) {
		return jdbcTemplate.update("update t_gift_own set count=? where user_id=? and gift_id=?",
				new Object[] { newCount, user_id, gift_id });
	}

	public int addOwn(long user_id, int gift_id) {
		String sql = "insert into t_gift_own (user_id,gift_id,count)  values ( '" + user_id + "','" + gift_id
				+ "','1')";
		return jdbcTemplate.update(sql);
	}

}
