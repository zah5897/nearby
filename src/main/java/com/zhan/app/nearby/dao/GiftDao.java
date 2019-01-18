package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("giftDao")
public class GiftDao extends BaseDao {

	public static final String TABLE_NAME = "t_gift";
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private VipDao vipDao;

	public long insert(Gift gift) {
		long id = saveObj(jdbcTemplate, TABLE_NAME, gift);
		gift.setId(id);
		return id;
	}

	public List<Gift> listGifts() {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " order by price",
				new BeanPropertyRowMapper<Gift>(Gift.class));
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
				"select * from t_gift_own own left join t_gift gift on own.gift_id=gift.id where own.user_id=? order by give_time desc",
				new Object[] { user_id }, new BeanPropertyRowMapper<GiftOwn>(GiftOwn.class));
	}

	public int updateOwnCount(long user_id, int gift_id, int newCount) {
		return jdbcTemplate.update("update t_gift_own set count=? where user_id=? and gift_id=?",
				new Object[] { newCount, user_id, gift_id });
	}

	public int addOwn(long user_id, int gift_id, long from_user_id, int count) {
		String sql = "insert into t_gift_own (user_id,gift_id,count,from_uid,give_time)  values (?,?,?,?,?)";
		return jdbcTemplate.update(sql, new Object[] { user_id, gift_id, count, from_user_id, new Date() });
	}

	public List<GiftOwn> loadGiftNotice(int page, int count) {
		String sql = "select go.*,re.nick_name as re_name,re.avatar as re_avatar,se.nick_name as se_name,se.avatar as se_avatar,g.name,g.image_url,g.price,g.old_price"
				+ " from t_gift_own go left join t_gift g on g.id=go.gift_id "
				+ " left join t_user re on re.user_id=go.user_id " + " left join t_user se on se.user_id=go.from_uid "
				+ " order by give_time desc limit ?,?";

		int offset = (page - 1) * count;
		return jdbcTemplate.query(sql, new Object[] { offset, count }, new RowMapper<GiftOwn>() {
			@Override
			public GiftOwn mapRow(ResultSet rs, int rowNum) throws SQLException {
				GiftOwn own = new GiftOwn();
				own.setUser_id(rs.getLong("user_id"));
				own.setId(rs.getLong("gift_id"));
				own.setName(rs.getString("name"));

				own.setImage_url(rs.getString("image_url"));
				own.setGive_time(rs.getTimestamp("give_time"));
				own.setCount(rs.getInt("count"));
				own.setPrice(rs.getInt("price"));
				own.setOld_price(rs.getInt("old_price"));

				BaseUser receiver = new BaseUser();
				receiver.setUser_id(rs.getLong("user_id"));
				receiver.setNick_name(rs.getString("re_name"));
				receiver.setAvatar(rs.getString("re_avatar"));
				ImagePathUtil.completeAvatarPath(receiver, true);
				own.setReceiver(receiver);

				BaseUser sender = new BaseUser();
				sender.setUser_id(rs.getLong("from_uid"));
				sender.setNick_name(rs.getString("se_name"));
				sender.setAvatar(rs.getString("se_avatar"));

				ImagePathUtil.completeAvatarPath(sender, true);
				own.setSender(sender);

				ImagePathUtil.completeGiftPath(own, true);
				return own;
			}
		});
	}

	/**
	 * 已改为一周 获取今日魅力榜
	 * 
	 * @param count
	 * @param pageIndex
	 * 
	 * @return
	 */
	public List<MeiLi> loadNewRegistUserMeiLi(int pageIndex, int count) {
		String t_total_meili = "select m.*,u.nick_name,u.avatar from t_meili_new_regist m left join t_user u on m.user_id=u.user_id  limit ?,?";
		List<MeiLi> meilis = jdbcTemplate.query(t_total_meili, new Object[] { (pageIndex - 1) * count, count },
				new RowMapper<MeiLi>() {

					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						m.setValue(rs.getInt("week_meili"));
						m.setShanbei(rs.getInt("amount"));
						m.setBe_like_count(rs.getInt("like_count"));

						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);

						m.setIs_vip(vipDao.isVip(user.getUser_id()));
						return m;
					}

				});
		return meilis;
	}

	/**
	 * 获取魅力总榜
	 * 
	 * @param count
	 * @param pageIndex
	 * 
	 * @return
	 */
	public List<MeiLi> loadTotalMeiLi(int pageIndex, int count) {
		// (select @rowno:=0) t
		String t_total_meili = "select m.*,u.nick_name,u.avatar from t_meili_total m left join t_user u on m.user_id=u.user_id limit ?,?";
		List<MeiLi> meilis = jdbcTemplate.query(t_total_meili, new Object[] { (pageIndex - 1) * count, count },
				new RowMapper<MeiLi>() {

					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						m.setValue(rs.getInt("total_meili"));
						m.setShanbei(rs.getInt("amount"));
						m.setBe_like_count(rs.getInt("like_count"));

						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);

						m.setIs_vip(vipDao.isVip(user.getUser_id()));

						return m;
					}

				});
		return meilis;
	}

	/**
	 * 获取土豪榜
	 * 
	 * @param count
	 * @param pageIndex
	 * 
	 * @return
	 */
	public List<MeiLi> loadTuHao(int pageIndex, int count) {

		String t_gift_send_amount = "select send.from_uid as user_id,send.count*g.price as amount from t_gift_own send left join t_gift g on send.gift_id=g.id";
		String t_tuhao_total = "select sum(amount) as tuhao_val ,send.user_id from (" + t_gift_send_amount
				+ ") as send group by send.user_id";

		String leftJoinUser = "select tuhao.*,u.nick_name,u.avatar from (" + t_tuhao_total
				+ ") tuhao left join t_user u on tuhao.user_id=u.user_id order by tuhao_val desc limit ?,?";
		List<MeiLi> meilis = jdbcTemplate.query(leftJoinUser, new Object[] { (pageIndex - 1) * count, count },
				new RowMapper<MeiLi>() {

					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						m.setValue(rs.getInt("tuhao_val"));
						m.setShanbei(m.getValue());
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);

						m.setIs_vip(vipDao.isVip(user.getUser_id()));
						return m;
					}

				});
		return meilis;
	}

	public int getUserMeiLiVal(long user_id) {
		String t_total_meili = "select total_meili  from t_meili_total where user_id=?";
		try {
			return jdbcTemplate.queryForObject(t_total_meili, new Object[] { user_id }, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}

	public int getUserBeLikeVal(long user_id) {
		String beLikeCount = "select count(*) from t_user_relationship where with_user_id=? and relationship=? group by with_user_id";
		try {
			int count = jdbcTemplate.queryForObject(beLikeCount, new Object[] { user_id, Relationship.LIKE.ordinal() },
					Integer.class);
			return count;
		} catch (Exception e) {
			return 0;
		}
	}

	public int getVal(long user_id) {
		String sql = "select coins from t_gift_coins where uid=?";
		try {
			Integer r = jdbcTemplate.queryForObject(sql, new Object[] { String.valueOf(user_id) }, Integer.class);
			return r == null ? 0 : r;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return 0;
	}

	public int addGiftCoins(long user_id, int gift_coins) {
		String tableName = "t_gift_coins";
		Integer count = jdbcTemplate.queryForObject("select count(*) from " + tableName + " where uid=" + user_id,
				Integer.class);
		if (count > 0) {
			String sql = "select coins from t_gift_coins where uid=?";
			int newCoins = gift_coins;
			try {
				Integer coins = jdbcTemplate.queryForObject(sql, Integer.class,
						new Object[] { String.valueOf(user_id) });
				newCoins = gift_coins + coins;
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			updateGiftCoins(user_id, newCoins);
			return newCoins;
		} else {
			String sql = "insert into " + tableName + " (uid,coins) values (?,?)";
			jdbcTemplate.update(sql, new Object[] { user_id, gift_coins });
			return gift_coins;
		}
	}

	public int updateGiftCoins(long user_id, int newCoins) {
		return jdbcTemplate.update("update t_gift_coins set coins=? where uid=?", new Object[] { newCoins, user_id });
	}

	public List<GiftOwn> getGifNotice(long user_id, int page, int count) {
		String sql = "select o.*,o.gift_id as id,g.price as price,o.from_uid as give_uid ,g.image_url as image_url,g.name as name from t_gift_own o left join t_gift g on o.gift_id=g.id where o.user_id=? order by o.give_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { user_id, (page - 1) * count, count },
				new BeanPropertyRowMapper<GiftOwn>(GiftOwn.class));

	}

	public void addExchangeHistory(Exchange exchange) {
		String sql = "select diamond from t_user_diamond where uid=? limit 1";
		List<Integer> diamondCounts = jdbcTemplate.queryForList(sql, new Object[] { exchange.getUser_id() }, Integer.class);
		if (diamondCounts.isEmpty()) {
			jdbcTemplate.update("insert into t_user_diamond(uid,diamond) values(?,?)",
					new Object[] { exchange.getUser_id(), exchange.getDiamond_count() });
		} else {
			jdbcTemplate.update("update  t_user_diamond set diamond=? where uid=?",
					new Object[] {  (exchange.getDiamond_count()+diamondCounts.get(0)),exchange.getUser_id()});
		}
		saveObjSimple(jdbcTemplate, "t_exchange_history", exchange);
	}

	public List<Exchange> loadExchangeDiamondHistory(long user_id, int i, int j) {
		return jdbcTemplate.query("select *from t_exchange_history where user_id=? order by create_time desc limit ?,?", new Object[] {user_id,(i-1)*j,j},new BeanPropertyRowMapper<Exchange>(Exchange.class));
	}
}
