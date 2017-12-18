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
import com.zhan.app.nearby.comm.ExchangeState;

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
	
}
