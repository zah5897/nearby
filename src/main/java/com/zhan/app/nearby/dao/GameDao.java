package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.GameScore;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.UserType;
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
		return jdbcTemplate.queryForObject("select count(*) from "+ TABLE_GAME_SCORE +" where uid=? and gid=?",new Object[] {score.getUid(),score.getGid()} ,Integer.class)>0;
	}
	
	public int updateScore(GameScore score) {
		return jdbcTemplate.update("update "+ TABLE_GAME_SCORE +" set score=? where uid=? and gid=? and score<?",new Object[] {score.getScore(),score.getUid(),score.getGid(),score.getScore()});
	}
	public List<GameScore> rankList(int gid, int start, int count) {
		String sql="select gs.* ,g.name as game_name,u.nick_name,u.avatar,u.sex  from "+TABLE_GAME_SCORE +" gs left join t_game g on gs.gid=g.id left join t_user u on gs.uid=u.user_id where gs.gid=? and u.type=? order by gs.score desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] {gid,UserType.OFFIEC.ordinal(),start,count},new BeanPropertyRowMapper<GameScore>(GameScore.class) {
			
			@Override
			public GameScore mapRow(ResultSet rs, int rowNumber) throws SQLException {
				GameScore score= super.mapRow(rs, rowNumber);
				BaseUser user=new BaseUser();
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

	 
}
