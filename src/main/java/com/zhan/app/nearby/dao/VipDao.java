package com.zhan.app.nearby.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Vip;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.dao.base.BaseDao;

@SuppressWarnings("unchecked")
@Repository("vipDao")
public class VipDao extends BaseDao<Vip> {
	public static final String TABLE_NAME_VIP = "t_vip_data";
	public static final String TABLE_NAME_VIP_USER = "t_user_vip";

	public List<Vip> listVipData() {
		return jdbcTemplate.query("select *from " + TABLE_NAME_VIP, getEntityMapper());
	}

	public void delete(long id) {
		jdbcTemplate.update("delete from " + TABLE_NAME_VIP + " where id=?", new Object[] { id });
	}

	public void update(Vip vip) {
		jdbcTemplate.update(
				"update " + TABLE_NAME_VIP
						+ " set name=?,amount=?,old_amount=?,description=?,term_mount=?,aid=? where id=?",
				new Object[] { vip.getName(), vip.getAmount(), vip.getOld_amount(), vip.getDescription(),
						vip.getTerm_mount(), vip.getAid(), vip.getId() });

	}

	public Vip load(int id) {
		List<Vip> gifts = jdbcTemplate.query("select *from " + TABLE_NAME_VIP + " where id=?", new Object[] { id },
				getEntityMapper());
		if (gifts != null && gifts.size() > 0) {
			return gifts.get(0);
		}
		return null;
	}

	// -------------user vip--------------
	public VipUser loadUserVip(long user_id) {
		List<VipUser> vipUsers = jdbcTemplate
				.query("select vip.*,TIMESTAMPDIFF(DAY,now(),vip.end_time) as dayDiff from " + TABLE_NAME_VIP_USER
						+ " vip where vip.user_id=?", new Object[] { user_id }, getEntityMapper(VipUser.class));
		if (vipUsers != null && vipUsers.size() > 0) {
			return vipUsers.get(0);
		}
		return null;
	}

	public boolean isVip(long user_id) {
		int count = jdbcTemplate.queryForObject(
				"select count(*) from " + TABLE_NAME_VIP_USER + " vip where vip.user_id=?", new Object[] { user_id },
				Integer.class);
		return count > 0;
	}

	public int delUserVip(long user_id) {
		return jdbcTemplate.update("delete from " + TABLE_NAME_VIP_USER + " where user_id=?", new Object[] { user_id });
	}

	public int updateUserVip(VipUser userVip) {
		return jdbcTemplate.update(
				"update " + TABLE_NAME_VIP_USER + " set last_order_no=?,end_time=? where user_id=? and vip_id=?",
				new Object[] { userVip.getLast_order_no(), userVip.getEnd_time(), userVip.getUser_id(),
						userVip.getVip_id() });

	}
	
	
 
	

	public List<Long> loadExpireVip() {
		String sql = "select user_id from " + TABLE_NAME_VIP_USER
				+ " vip where TIMESTAMPDIFF(DAY,now(),vip.end_time) < 0";
		return jdbcTemplate.queryForList(sql, Long.class);
	}

	public List<Integer> getVipIdByMonth(int month) {
		return jdbcTemplate.queryForList("select id from " + TABLE_NAME_VIP + " where term_mount=" + month,
				Integer.class);
	}
}
