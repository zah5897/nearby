package com.zhan.app.nearby.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BeanPropertyBindingResult;

import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.util.TextUtils;

@Repository("systemDao")
public class SystemDao extends BaseDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	public int report(long user_id, long report_to_user_id, String report_tag_id, String content) {

		int count=jdbcTemplate.update("insert into t_report_record (user_id,report_to_user_id,report_tag_id,content) values(?,?,?,?)",
				new Object[] { user_id, report_to_user_id, report_tag_id, content }, new int[] { java.sql.Types.INTEGER,
						java.sql.Types.INTEGER, java.sql.Types.VARCHAR, java.sql.Types.VARCHAR });
		return count;
	}
    
	/**
	 * 增加兑换的钻石
	 * @param user_id
	 * @param aid
	 * @param exchangeDiamondCount
	 * @return 返回现有钻石
	 */
	public int addExchangeDiamond(long user_id,String aid,int exchangeDiamondCount){
		int existCount=getDiamondCount(user_id,aid);
		int nowDiamondCount=exchangeDiamondCount;
		if(existCount==-1){
			jdbcTemplate.update("insert into t_diamond (user_id,diamond,aid,last_exchange_time) values(?,?,?,?)",
					new Object[] { user_id,nowDiamondCount , aid, new Date()});
		}else{
			nowDiamondCount=exchangeDiamondCount+existCount;
			jdbcTemplate.update("update t_diamond set diamond=?,last_exchange_time=? where user_id=? and aid=? ",new Object[]{nowDiamondCount,new Date(),user_id,aid});
		}
		return nowDiamondCount;
	}
	 /**
	  * 获取钻石数量
	  * @param user_id
	  * @param aid
	  * @return 如果不存在记录返回-1
	  */
	public int getDiamondCount(long user_id,String aid){
		try{
		   return jdbcTemplate.queryForObject("select diamond from t_diamond where user_id=? and aid=?",new Object[]{user_id,aid}, Integer.class);
		}catch(EmptyResultDataAccessException e){
			return -1;
		}
	}

	
	public int updateDiamondCount(long user_id,String aid,int nowCount){
		return jdbcTemplate.update("update t_diamond set diamond=?,last_exchange_time=? where user_id=? and aid=? ",new Object[]{nowCount,new Date(),user_id,aid});
	}
	
	public void addExchangeHistory(Exchange exchange) {
          saveObjSimple(jdbcTemplate, "t_exchange_history", exchange);		
	}
	
	public List<Exchange> loadExchangeHistory(long user_id, String aid,int pageIndex,int count){
		return jdbcTemplate.query("select *from  t_exchange_history where user_id=? and aid=? limit ?,?",new Object[]{user_id,aid,(pageIndex-1)*count,count} ,new BeanPropertyRowMapper<Exchange>(Exchange.class));
	}
	
	public Integer getTotalExchangeRmmbByState(long user_id,String aid,ExchangeState state){
		Integer total=
		 jdbcTemplate.queryForObject("select sum(rmb_fen) from  t_exchange_history where user_id=? and aid=? and state=?",new Object[]{user_id,aid,state.ordinal()} ,Integer.class);
		return total;
	}

	/**
	 * 获取最大成长率的人
	 * @param gender
	 * @param limit
	 * @return
	 */
	public List<BaseUser> loadMaxRateMeiLi(Long ignoreUserId,String gender,int page_index,int limit) {
		if(TextUtils.isEmpty(gender)) {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from t_meili_rate_temp rate left join t_user u on rate.uid=u.user_id where  rate.uid<>? and  u.avatar<>?   order by rate.rate,rate.uid desc limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {ignoreUserId==null?0:ignoreUserId,"",(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}else {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from t_meili_rate_temp rate left join t_user u on rate.uid=u.user_id where rate.uid<>? and u.avatar<>? and u.sex=?   order by rate.rate,rate.uid desc limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {ignoreUserId==null?0:ignoreUserId,"",gender,(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}
	 
	}
	
	public List<BaseUser> loadMaxMeiLi(Long ignoreUserId,String gender,int page_index,int limit) {
		if(TextUtils.isEmpty( gender)) {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from t_meili_temp t left join t_user u on t.uid=u.user_id where u.avatar<>?    order by temp_meili,t.uid desc limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {"",(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}else {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from t_meili_temp t left join t_user u on t.uid=u.user_id where u.avatar<>? and u.sex=?   order by temp_meili,t.uid desc limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {"",gender,(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}
	}
	
	
	public int injectRate() {
		
		//清除昨天的rate数据，
		String clearRate="delete from t_meili_rate_temp";
		jdbcTemplate.update(clearRate);
		
		//生成到现在为止的rate数据
		String rateSql="select tm.user_id as uid,(tm.total_meili-coalesce(tt.temp_meili,'0')) as rate from t_meili_total tm left join t_meili_temp tt on tm.user_id=tt.uid ";
		String injectRate="insert into t_meili_rate_temp "+rateSql;
		int rateCount=jdbcTemplate.update(injectRate);

		//刷新昨天的零时美丽总值
		
		String clearMeiliTemp="delete from t_meili_temp";
		jdbcTemplate.update(clearMeiliTemp);
        String injectMeiliTemp="insert into t_meili_temp  select tm.user_id as uid, tm.total_meili as temp_meili from t_meili_total tm"; 		
        jdbcTemplate.update(injectMeiliTemp);
        
		return rateCount;
	}
	
	
	
}
