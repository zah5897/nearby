package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.RankUser;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

import io.swagger.models.auth.In;

@Repository("giftDao")
public class GiftDao extends BaseDao<Gift> {
	@Resource
	private VipDao vipDao;

	public List<Gift> listGifts() {
		return jdbcTemplate.query("select *from " + getTableName() + " order by price", getEntityMapper());
	}

	public void delete(long id) {
		jdbcTemplate.update("delete from " + getTableName() + " where id=?", new Object[] { id });
	}

	public void update(Gift gift) {
		if (TextUtils.isEmpty(gift.getImage_url())) {
			jdbcTemplate.update(
					"update " + getTableName() + " set name=?,price=?,old_price=?,description=?,remark=? where id=?",
					new Object[] { gift.getName(), gift.getPrice(), gift.getOld_price(), gift.getDescription(),
							gift.getRemark(), gift.getId() });
		} else {
			jdbcTemplate.update(
					"update " + getTableName()
							+ " set name=?,price=?,old_price=?,image_url=?,description=?,remark=? where id=?",
					new Object[] { gift.getName(), gift.getPrice(), gift.getOld_price(), gift.getImage_url(),
							gift.getDescription(), gift.getRemark(), gift.getId() });
		}
	}

	public Gift load(int gift_id) {
		List<Gift> gifts = jdbcTemplate.query("select *from " + getTableName() + " where id=?",
				new Object[] { gift_id }, getEntityMapper());
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
		String sql = "select go.*,re.nick_name as re_name,re.isvip as re_is_vip,re.avatar as re_avatar,se.nick_name as se_name,se.isvip as se_is_vip,se.avatar as se_avatar,g.name,g.image_url,g.price,g.old_price"
				+ " from t_gift_own go left join t_gift g on g.id=go.gift_id "
				+ " left join t_user re on re.user_id=go.user_id  left join t_user se on se.user_id=go.from_uid  where re.sys_status<>? and se.sys_status<>? "
				+ " order by give_time desc limit ?,?";

		int offset = (page - 1) * count;
		return jdbcTemplate.query(sql, new Object[] {SysUserStatus.BLACK.ordinal(),SysUserStatus.BLACK.ordinal(), offset, count }, new RowMapper<GiftOwn>() {
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
				receiver.setIsvip(rs.getInt("re_is_vip"));
				receiver.setAvatar(rs.getString("re_avatar"));
				ImagePathUtil.completeAvatarPath(receiver, true);
				own.setReceiver(receiver);

				BaseUser sender = new BaseUser();
				sender.setUser_id(rs.getLong("from_uid"));
				sender.setNick_name(rs.getString("se_name"));
				sender.setAvatar(rs.getString("se_avatar"));
				sender.setIsvip(rs.getInt("se_is_vip"));

				ImagePathUtil.completeAvatarPath(sender, true);
				own.setSender(sender);

				ImagePathUtil.completeGiftPath(own, true);
				return own;
			}
		});
	}

	/**
	 * 获取魅力总榜
	 * 
	 * @param count
	 * @param pageIndex
	 * 
	 * @return
	 */
	@Cacheable(value = "one_hour", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<MeiLi> loadTotalMeiLi(int page, int count) {
		String sql = "select u.user_id ,u.nick_name, u.avatar,u.isvip,gift.tval as sanbei, gift.tval*5+lk.like_count as mli  from "
				+ "t_user u left join "
				+ " (select tg.user_id ,sum(tg.val) as tval from (select o.*,o.count*g.price as val from  t_gift_own o left join t_gift g on o.gift_id=g.id) as tg group by tg.user_id) gift "
				+ "on u.user_id=gift.user_id "
				+ " left join  (select count(*) as like_count ,with_user_id from t_user_relationship where relationship=?  group by  with_user_id) lk  "
				+ " on u.user_id=lk.with_user_id "
				+ "where  (u.type=? or u.type=?) and  u.sys_status<>1  and DATE_SUB(CURDATE(), INTERVAL 365 DAY) <= date(create_time) order by mli desc limit ?,?";

		List<MeiLi> users = jdbcTemplate.query(sql, new Object[] { Relationship.LIKE.ordinal(),
				UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },
				new RowMapper<MeiLi>() {
					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						m.setValue(rs.getInt("mli"));
						m.setShanbei(rs.getInt("sanbei"));
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);
						int is_vip = rs.getInt("isvip");
						m.setIs_vip(is_vip > 0);
						user.setIsvip(is_vip);
						return m;
					}

				});
		return users;
	}
	
	@Cacheable(value = "one_hour", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<RankUser> loadTotalMeiLiV2(int page, int count) {
		String sql = "select u.user_id ,u.nick_name,u.lat,u.lng, u.avatar,u.isvip,ifnull(gift.tval,'0') as shanbei, u.meili,u.city_id as city_id,c.name as city_name from t_user u"
				+ "  left join "
				+ " (select tg.user_id ,sum(tg.val) as tval,tg.give_time from (select o.*,o.count*g.price as val from  t_gift_own o left join t_gift g on o.gift_id=g.id) as tg group by tg.user_id) gift "
				+ " on u.user_id=gift.user_id  left join t_sys_city c on c.id=u.city_id "
				+ " where    (u.type=? or u.type=?) and u.sys_status<>1  and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(gift.give_time) order by u.meili desc limit ?,?";
		List<RankUser> users = jdbcTemplate.query(sql, new Object[] {  UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },new BeanPropertyRowMapper<RankUser>(RankUser.class) {
			@Override
			public RankUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
				RankUser u = super.mapRow(rs, rowNumber);
				try {
					City c = new City();
					c.setId(rs.getInt("city_id"));
					c.setName(rs.getString("city_name"));
					u.setCity(c);
				} catch (Exception e) {
				}
				return u;
			}
		});
		return users;
	}
	

	/**
	 * 获取土豪榜
	 * 
	 * @param count
	 * @param pageIndex
	 * 
	 * @return
	 */
	@Cacheable(value = "one_hour", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<MeiLi> loadTuHao(int page, int count) {
		String sql = "select u.user_id ,u.nick_name, u.avatar,u.isvip,ifnull(gift.tval,'0') as sanbei from "
				+ "t_user u left join "
				+ "(select tg.from_uid ,sum(tg.val) as tval from (select o.*,o.count*g.price as val from  t_gift_own o left join t_gift g on o.gift_id=g.id) as tg group by tg.from_uid) gift "
				+ "on u.user_id=gift.from_uid "
				+ "where  (u.type=? or  u.type=?) and  u.sys_status<>1  order by sanbei desc limit ?,?";

		List<MeiLi> users = jdbcTemplate.query(sql,
				new Object[] { UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },
				new RowMapper<MeiLi>() {
					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						m.setValue(rs.getInt("sanbei"));
						m.setShanbei(rs.getInt("sanbei"));
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);
						int is_vip = rs.getInt("isvip");
						m.setIs_vip(is_vip > 0);
						user.setIsvip(is_vip);
						return m;
					}

				});
		return users;
	}

	@Cacheable(value = "one_hour", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<RankUser> loadTuHaoV2(int page, int count) {
		String sql = "select u.user_id ,u.nick_name,u.lat,u.lng, u.avatar,u.isvip,ifnull(gift.tval,'0') as shanbei,u.city_id as city_id,c.name as city_name from "
				+ "t_user u left join "
				+ "(select tg.from_uid ,sum(tg.val) as tval,tg.give_time from (select o.*,o.count*g.price as val from  t_gift_own o left join t_gift g on o.gift_id=g.id) as tg group by tg.from_uid) gift "
				+ "on u.user_id=gift.from_uid  left join t_sys_city c on c.id=u.city_id "  
				+ "where  (u.type=? or  u.type=?) and  u.sys_status<>1  and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(gift.give_time) order by shanbei desc limit ?,?";

		List<RankUser> users = jdbcTemplate.query(sql,
				new Object[] { UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },new BeanPropertyRowMapper<RankUser>(RankUser.class) {
			@Override
			public RankUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
				RankUser u = super.mapRow(rs, rowNumber);
				try {
					City c = new City();
					c.setId(rs.getInt("city_id"));
					c.setName(rs.getString("city_name"));
					u.setCity(c);
				} catch (Exception e) {
				}
				return u;
			}
		});
		return users;
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
		String sql = "select o.*,o.gift_id as id,g.price as price,o.from_uid as give_uid ,g.image_url as image_url,g.name as name from t_gift_own o"
				+ "  left join t_gift g on o.gift_id=g.id where o.user_id=? order by o.give_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { user_id, (page - 1) * count, count },
				new BeanPropertyRowMapper<GiftOwn>(GiftOwn.class));

	}

	public void addExchangeHistory(Exchange exchange) {
		String sql = "select diamond from t_user_diamond where uid=? limit 1";
		List<Integer> diamondCounts = jdbcTemplate.queryForList(sql, new Object[] { exchange.getUser_id() },
				Integer.class);
		if (diamondCounts.isEmpty()) {
			jdbcTemplate.update("insert into t_user_diamond(uid,diamond) values(?,?)",
					new Object[] { exchange.getUser_id(), exchange.getDiamond_count() });
		} else {
			jdbcTemplate.update("update  t_user_diamond set diamond=? where uid=?",
					new Object[] { (exchange.getDiamond_count() + diamondCounts.get(0)), exchange.getUser_id() });
		}
		insertObject(exchange);
	}

	public List<Exchange> loadExchangeDiamondHistory(long user_id, int i, int j) {
		return jdbcTemplate.query("select *from t_exchange_history where user_id=? order by create_time desc limit ?,?",
				new Object[] { user_id, (i - 1) * j, j }, new BeanPropertyRowMapper<Exchange>(Exchange.class));
	}

	public Integer getTotalExchangeDiamond(long user_id) {
		Integer total = jdbcTemplate.queryForObject(
				"select sum(diamond_count) from  t_exchange_history where user_id=?", new Object[] { user_id },
				Integer.class);
		if (total == null) {
			return 0;
		} else {
			return (int) (total * 0.3);
		}
	}

	public int getGiftHistoryCount(Long send_user_id) {
		if(send_user_id!=null) {
			return jdbcTemplate.queryForObject("select count(*) from t_gift_own where from_uid="+send_user_id, Integer.class);
		}
		return jdbcTemplate.queryForObject("select count(*) from t_gift_own", Integer.class);
	}

	public List<GiftOwn> getGifNoticeByManager(Long send_user_id, int page, int count) {
		
		
		BeanPropertyRowMapper<GiftOwn> mapper=new BeanPropertyRowMapper<GiftOwn>(GiftOwn.class) {
			@Override
			public GiftOwn mapRow(ResultSet rs, int rowNumber) throws SQLException {
				// TODO Auto-generated method stub
				GiftOwn giftO= super.mapRow(rs, rowNumber);
				BaseUser sender=new BaseUser();
				sender.setUser_id(rs.getLong("from_uid"));
				sender.setNick_name(rs.getString("sname"));
				sender.setAvatar(rs.getString("savatar"));
				giftO.setSender(sender);
				
				BaseUser receiver=new BaseUser();
				receiver.setUser_id(rs.getLong("user_id"));
				receiver.setNick_name(rs.getString("rname"));
				receiver.setAvatar(rs.getString("ravatar"));
				giftO.setReceiver(receiver);
				return giftO;
			}
		};
		
		if(send_user_id!=null) {
			String sql = "select o.*,s.nick_name as sname ,s.avatar as savatar ,r.nick_name as rname ,r.avatar as ravatar,o.gift_id as id,g.price as price,o.from_uid as give_uid ,g.image_url as image_url,g.name as name "
					+ " from t_gift_own o "
					+ " left join t_gift g on o.gift_id=g.id "
					+ " left join t_user s on o.from_uid=s.user_id "
					+ " left join t_user r on o.user_id=r.user_id "
					+ " where o.user_id=? order by o.give_time desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { send_user_id, (page - 1) * count, count },
					mapper);
		}
		
		String sql = "select o.*,s.nick_name as sname ,s.avatar as savatar ,r.nick_name as rname ,r.avatar as ravatar,o.gift_id as id,g.price as price,o.from_uid as give_uid ,g.image_url as image_url,g.name as name "
				+ " from t_gift_own o "
				+ " left join t_gift g on o.gift_id=g.id "
				+ " left join t_user s on o.from_uid=s.user_id "
				+ " left join t_user r on o.user_id=r.user_id "
				+ "   order by o.give_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] {(page - 1) * count, count },
				mapper);

	}
}
