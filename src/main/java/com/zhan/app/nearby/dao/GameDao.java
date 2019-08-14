package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.GameScore;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.util.ImagePathUtil;

@Repository("gameDao")
public class GameDao extends BaseDao {
	public static final String TABLE_GAME_SCORE = "t_game_score";
	@Resource
	private JdbcTemplate jdbcTemplate;

	// ---------------------------------------bottle-------------------------------------------------
	public void insert(GameScore game) {
		saveObjSimple(jdbcTemplate, TABLE_GAME_SCORE, game);
	}

	public boolean isExist(GameScore score) {
		return jdbcTemplate.queryForObject("select count(*) from " + TABLE_GAME_SCORE + " where uid=? and gid=?",
				new Object[] { score.getUid(), score.getGid() }, Integer.class) > 0;
	}

	public int updateScore(GameScore score) {
		return jdbcTemplate.update("update " + TABLE_GAME_SCORE + " set score=?,create_time=? where uid=? and gid=?",
				new Object[] { score.getScore(), new Date(), score.getUid(), score.getGid() });
	}

	public List<GameScore> rankList(String gid, int start, int count) {
		String sql = "select gs.*,u.nick_name,u.avatar,u.sex , (@rowNum:=@rowNum+1) AS rowNo from " + TABLE_GAME_SCORE
				+ " gs inner join (select(@rowNum:=0)) b  left join t_user u on gs.uid=u.user_id where gs.gid=?   order by gs.score desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { gid, start, count },
				new BeanPropertyRowMapper<GameScore>(GameScore.class) {
					@Override
					public GameScore mapRow(ResultSet rs, int rowNumber) throws SQLException {
						GameScore score = super.mapRow(rs, rowNumber);
						score.setPosition(rs.getInt("rowNo"));
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("uid"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						user.setSex(rs.getString("sex"));
						ImagePathUtil.completeAvatarPath(user, true);
						score.setUser(user);
						return score;
					}
				});
	}

	public List<Map<String, Object>> getGames() {
		return jdbcTemplate.query("select *from t_game", new RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", rs.getObject("id"));
				map.put("name", rs.getObject("name"));
				return map;
			}

		});
	}

	public int clearStep(long user_id, String gid) {
		return jdbcTemplate.update("delete from t_game_step where uid=? and gid=?", new Object[] { user_id, gid });
	}

	public int getGameStep(long user_id, String gid) {
		List<Integer> steps = jdbcTemplate.queryForList("select step  from t_game_step where uid=? and gid=? limit 1",
				new Object[] { user_id, gid }, Integer.class);
		if (steps.isEmpty()) {
			return 0;
		} else {
			return steps.get(0);
		}
	}

	public int updateStep(long user_id, String gid, int step) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_game_step where uid=? and gid=?",
				new Object[] { user_id, gid }, Integer.class);
		if (count == 0) {
			return jdbcTemplate.update("insert into   t_game_step (gid ,uid,step,update_time) values(?,?,?,?)",
					new Object[] { gid, user_id, step, new Date() });
		} else {
			return jdbcTemplate.update("update t_game_step  set step=? ,update_time=? where gid=? and uid=?",
					new Object[] { step, new Date(), gid, user_id });
		}
	}

	/**
	 * 获取该用户的对应游戏的排行
	 * 
	 * @param gid
	 * @param uid
	 * @return
	 */
	public int getCurrentUserPosition(String gid, long uid) {
		String sql = "select rowNo from (select uid,(@rowNum:=@rowNum+1) AS rowNo"
				+ " from t_game_score,(select(@rowNum:=0)) b  where gid=? ORDER BY score DESC) c where uid=?";
		List<Integer> r = jdbcTemplate.queryForList(sql, new Object[] { gid, uid }, Integer.class);

		if (r.isEmpty()) {
			return -1;
		} else {
			return r.get(0);
		}
	}

	public int getMyScore(String gid, long uid) {
		String sql = "select score from t_game_score where gid=? and  uid=?";
		List<Integer> r = jdbcTemplate.queryForList(sql, new Object[] { gid, uid }, Integer.class);
		if (r.isEmpty()) {
			return 0;
		} else {
			return r.get(0);
		}
	}

}
