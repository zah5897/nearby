package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.mapper.DynamicMsgMapper;
import com.zhan.app.nearby.bean.type.DynamicMsgStatus;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.MsgState;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;

@Repository("dynamicMsgDao")
public class DynamicMsgDao extends BaseDao<DynamicMessage> {

	/**
	 * 获取未读消息
	 * 
	 * @param user_id
	 * @param last_id
	 * @param type
	 * @return
	 */
	public List<DynamicMessage> loadMsg(Long user_id, long last_id, int type) {
		if (type == 0) {
			String sql = "select msg.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type  from "
					+ getTableName()
					+ " msg left join t_user user on msg.by_user_id=user.user_id where msg.user_id=? and msg.id>? and msg.type<? "
					+ fiflterBlock() + " and msg.isReadNum=? order by msg.id desc";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, 2, user_id, Relationship.BLACK.ordinal(),
					MsgState.NUREAD.ordinal() }, new DynamicMsgMapper());
		} else {
			String sql = "select msg.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type  from "
					+ getTableName()
					+ " msg left join t_user user on msg.by_user_id=user.user_id where msg.user_id=? and msg.id>? and msg.type>? and msg.isReadNum=? order by msg.id desc";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, 1, MsgState.NUREAD.ordinal() },
					new DynamicMsgMapper());
		}
	}

	public DynamicMessage loadMsg(long msg_id) {
		String sql = "select * from " + getTableName() + "  where id=?";
		List<DynamicMessage> msgs = jdbcTemplate.query(sql, new Object[] { msg_id },
				getEntityMapper());
		if (msgs != null && msgs.size() > 0) {
			return msgs.get(0);
		}
		return null;
	}

	public int delete(long msg_id) {
		String sql = "delete from " + getTableName() + " where id=?";
		return jdbcTemplate.update(sql, new Object[] { msg_id });
	}

	public int updateState(long id) {
		String sql = "update " + getTableName() + " set isReadNum=? where id=?";
		return jdbcTemplate.update(sql, new Object[] { MsgState.READED.ordinal(), id });
	}

	public int updateMeetState(long user_id, long target) {
		String sql = "update " + getTableName()
				+ " set isReadNum=? , status=? where user_id=? and by_user_id=? and type=?";
		return jdbcTemplate.update(sql, new Object[] { MsgState.READED.ordinal(),
				DynamicMsgStatus.HAD_Operation.ordinal(), user_id, target, DynamicMsgType.TYPE_MEET.ordinal() });
	}

	private String fiflterBlock() {
		return " and msg.by_user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) ";
	}

	public int clearMeetMsg(long user_id) {
		String sql = "delete from " + getTableName() + " where user_id=? and type=?";
		return jdbcTemplate.update(sql, new Object[] { user_id, DynamicMsgType.TYPE_MEET.ordinal() });
	}

	public int delMeetMsg(long user_id, long id, int type) {
		String sql = "delete from " + getTableName() + " where user_id=? and type=? and id=?";
		return jdbcTemplate.update(sql, new Object[] { user_id, DynamicMsgType.TYPE_MEET.ordinal(), id });
	}

	public List<DynamicMessage> getMyMeetLatest(long user_id) {
		String sql = "select msg.* from  " + getTableName() + " msg  "
				+ " left join t_bottle b on msg.obj_id=b.id  "
				+ " left join t_latest_tip_time la on msg.user_id=la.uid  "
				+ " where msg.user_id=? and msg.type=? and msg.create_time>la.last_time order by msg.create_time";
		return jdbcTemplate.query(sql, new Object[] { user_id, DynamicMsgType.TYPE_MEET.ordinal() },
				getEntityMapper());
	}

	public List<DynamicMessage> loadMsg(long user_id, Long last_id, int count,boolean noMeet) {

		if (last_id == null||last_id<1) {
			last_id = Long.MAX_VALUE;
		}
		
		String sql = "select msg.*,u.nick_name,u.avatar,u.user_id ,u.sex,v.vip_id "
				+ "   from  " + getTableName() + " msg "
				+ " left join t_user u on msg.by_user_id=u.user_id "
				+ "  left join t_user_vip v on msg.by_user_id=v.user_id  "
				+ " where msg.user_id=? and msg.id<?  order by msg.id desc limit ?";
		if(noMeet) {
		  sql = "select msg.*,u.nick_name,u.avatar,u.user_id ,u.sex,v.vip_id "
					+ "   from  " + getTableName() + " msg "
					+ " left join t_user u on msg.by_user_id=u.user_id "
					+ "  left join t_user_vip v on msg.by_user_id=v.user_id  "
					+ " where msg.user_id=? and msg.type<>"+DynamicMsgType.TYPE_MEET.ordinal()+" and msg.id<?  order by msg.id desc limit ?";
		}
		
		return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count },
				new BeanPropertyRowMapper<DynamicMessage>(DynamicMessage.class) {
			@Override
			public DynamicMessage mapRow(ResultSet rs, int rowNumber) throws SQLException {
				DynamicMessage msg= super.mapRow(rs, rowNumber);
				BaseVipUser u = new BaseVipUser();
				u.setUser_id(rs.getLong("by_user_id"));
				u.setNick_name(rs.getString("nick_name"));
				u.setAvatar(rs.getString("avatar"));
				u.setSex(rs.getString("sex"));
				ImagePathUtil.completeAvatarPath(u, true);
				Object vipObj = rs.getObject("vip_id");
				if (vipObj != null && !"null".equals(vipObj.toString())) {
					u.setVip(true);
				}
				msg.setFrom(u);
				return msg;
			}
		});
	}

	public int deleteFrom(long user_id) {
		String sql = "select count(*) from  " + getTableName() + " where user_id=" + user_id;
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int getUnReadMsgCount(long user_id,boolean noMeet) {
		if(noMeet) {
			String sql = "select count(*) from  " + getTableName() + " where user_id=" + user_id+ " and isReadNum=0 and type<>"+DynamicMsgType.TYPE_MEET.ordinal();
			return jdbcTemplate.queryForObject(sql, Integer.class);
		}else {
			String sql = "select count(*) from  " + getTableName() + " where user_id=" + user_id+ " and isReadNum=0";
			return jdbcTemplate.queryForObject(sql, Integer.class);
		}
			
	}

	public void clearMsg(long user_id, long last_id) {
         String sql="delete from "+getTableName()+" where user_id=? and id>? ";
         jdbcTemplate.update(sql,new Object[] {user_id,last_id});
	}

	public List<DynamicMessage> getPraseMsg(long user_id) {
		String sql = "select msg.* from  " + getTableName() + " msg  "
				+ " left join t_user_dynamic d on msg.obj_id=d.id  "
				+ " left join t_latest_praise_tip_time la on msg.user_id=la.uid "
				+ "  where msg.user_id=? and msg.type=? and msg.create_time>la.last_time order by msg.create_time";
		return jdbcTemplate.query(sql, new Object[] { user_id, DynamicMsgType.TYPE_PRAISE.ordinal() },
				getEntityMapper());
	}

	public List<DynamicMessage> getPraseMsg(long user_id, Long last_id, int count, int type) {

		if (last_id == null) {
			last_id = getMarkLastMsgId(user_id, type);
		}
		if (last_id == null) {
			last_id = (long) Integer.MAX_VALUE;
		}

		String sql = "select msg.* from  " + getTableName() + " msg  "
				+ " left join t_user_dynamic d on msg.obj_id=d.id  "
				+ "  where msg.user_id=? and msg.type=? and msg.id<?   order by msg.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, DynamicMsgType.TYPE_PRAISE.ordinal(), last_id, count },
				getEntityMapper());
	}

//	public List<DynamicMessage> getPraseMsg (long user_id) {
//		 String sql="select msg.* from  "+TABLE_DYNAMIC_MSG+" msg  left join t_user_dynamic d on msg.dynamic_id=d.id     where msg.user_id=? and msg.type=?   order by msg.create_time";
//		 return jdbcTemplate.query(sql, new Object[] {user_id,DynamicMsgType.TYPE_PRAISE.ordinal()},new BeanPropertyRowMapper<DynamicMessage>(DynamicMessage.class));
//	}
	public List<DynamicComment> getMyDynamicCommentLatest(long user_id) {
		String sql = "select comm.* from  t_dynamic_comment comm  "
				+ " left join t_user_dynamic dy on comm.dynamic_id=dy.id  "
				+ " left join t_latest_tip_time la on dy.user_id=la.uid "
				+ "  where dy.user_id=? and comm.comment_time>la.last_time order by comm.comment_time";
		return jdbcTemplate.query(sql, new Object[] { user_id },
				new BeanPropertyRowMapper<DynamicComment>(DynamicComment.class));
	}

//	public List<DynamicComment> getMyDynamicCommentLatest(long user_id, Long last_id, int count, int type) {
//
//		if (last_id == null) {
//			last_id = getMarkLastMsgId(user_id, type);
//		}
//
//		if (last_id == null || last_id < 1) { // 没有标记
//			last_id = (long) Integer.MAX_VALUE;
//		}
//		String sql = "select comm.* from  t_dynamic_comment comm  "
//				+ " left join t_user_dynamic dy on comm.dynamic_id=dy.id  "
//				+ "  where dy.user_id=? and comm.id< ? order by comm.id desc limit ?";
//		return jdbcTemplate.query(sql, new Object[] { user_id,last_id, count},
//				new BeanPropertyRowMapper<DynamicComment>(DynamicComment.class));
//	}

	public void updateLatestTipTime(long user_id) {

		int count = jdbcTemplate.update("update t_latest_tip_time set last_time=? where uid=?",
				new Object[] { new Date(), user_id });
		if (count == 0) {
			jdbcTemplate.update("insert into  t_latest_tip_time (last_time,uid) values(?,?)",
					new Object[] { new Date(), user_id });
		}
	}

	public void markLastMsgLoadId(long user_id, long id, int type) {

		int count = jdbcTemplate.update("update t_latest_msg_read_mark set last_time=? ,type=? ,id=? where uid=?",
				new Object[] { new Date(), type, id, user_id });
		if (count == 0) {
			jdbcTemplate.update("insert ignore into  t_latest_msg_read_mark (last_time,uid,type,id) values(?,?,?,?)",
					new Object[] { new Date(), user_id, type, id });
		}
	}

	public Long getMarkLastMsgId(long user_id, int type) {
		String sql = "select id from t_latest_msg_read_mark where uid=? and type=? ";
		List<Long> ids = jdbcTemplate.queryForList(sql, new Object[] { user_id, type }, Long.class);
		if (ids.isEmpty()) {
			return null;
		} else {
			return ids.get(0);
		}
	}

	public void updateLatestPraiseTipTime(long user_id) {

		int count = jdbcTemplate.update("update t_latest_praise_tip_time set last_time=? where uid=?",
				new Object[] { new Date(), user_id });
		if (count == 0) {
			jdbcTemplate.update("insert into  t_latest_praise_tip_time (last_time,uid) values(?,?)",
					new Object[] { new Date(), user_id });
		}
	}

	public long getLikeLastOneID(long user_id) {
		List<Long> ids = jdbcTemplate.queryForList(
				"select  by_user_id from t_dynamic_msg where user_id=? and type=? order by create_time desc limit 1",
				new Object[] { user_id, DynamicMsgType.TYPE_MEET.ordinal() }, Long.class);
		if (ids.isEmpty()) {
			return 0;
		}
		return ids.get(0);
	}

	public int getDymanicMsgCount(DynamicMsgType typeMeet, long by_user_id, long dynamic_id, long user_id) {
		String sql = "select count(*) from " + getTableName()
				+ " where user_id=? and type=? and by_user_id =? and dynamic_id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { by_user_id, typeMeet.ordinal(), user_id, dynamic_id },
				Integer.class);
	}

	public void updateMsgStatus(long msg_id, DynamicMsgStatus hadOperation) {
		String sql = "update " + getTableName() + " set status=? where id=?";
		jdbcTemplate.update(sql, new Object[] { hadOperation.ordinal(), msg_id });
	}

}
