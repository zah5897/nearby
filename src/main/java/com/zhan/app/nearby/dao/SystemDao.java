package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;
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
	 * 临时决策
	 * @param ignoreUserId
	 * @param gender
	 * @param page_index
	 * @param limit
	 * @return
	 */
	public List<BaseVipUser> loadMaxRateMeiLiRandom(Long ignoreUserId,String gender,int page_index,int limit) {
		if(TextUtils.isEmpty(gender)) {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from   t_user u left join t_found_user_relationship s on u.user_id=s.uid   where s.state=0 and   u.user_id<>? and  u.avatar<>? order by rand() limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {ignoreUserId==null?0:ignoreUserId,"",(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseVipUser>(BaseVipUser.class));
		}else {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from   t_user u  left join t_found_user_relationship s on u.user_id=s.uid   where s.state=0 and u.user_id<>? and u.avatar<>? and u.sex=?  order by rand() limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {ignoreUserId==null?0:ignoreUserId,"",gender,(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseVipUser>(BaseVipUser.class));
		}
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
	
	public List<BaseVipUser> loadMaxMeiLi(Long ignoreUserId,String gender,int page_index,int limit) {
		if(TextUtils.isEmpty( gender)) {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from t_meili_temp t left join t_user u on t.uid=u.user_id where u.avatar<>?    order by temp_meili,t.uid desc limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {"",(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseVipUser>(BaseVipUser.class));
		}else {
			String sql="select u.user_id,u.nick_name,u.avatar,u.sex from t_meili_temp t left join t_user u on t.uid=u.user_id where u.avatar<>? and u.sex=?   order by temp_meili,t.uid desc limit ?,?";
			return jdbcTemplate.query(sql,new Object[] {"",gender,(page_index-1)*limit,limit}, new BeanPropertyRowMapper<BaseVipUser>(BaseVipUser.class));
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

	public int savePersonalInfo(PersonalInfo personal) {
		 return saveObjSimple(jdbcTemplate, "t_personal_info", personal);
	}
	
	public PersonalInfo loadPersonalInfo(long user_id,String aid) {
		List<PersonalInfo>  personalInfos= jdbcTemplate.query("select *from t_personal_info where user_id=? and aid=?",new Object[] {user_id,aid},new BeanPropertyRowMapper<PersonalInfo>(PersonalInfo.class));
	   if(personalInfos.size()>0) {
		   return personalInfos.get(0);
	   }else {
		   return null;
	   }
	}
	public int updatePersonalInfo(long user_id, String aid, String zhifubao_access_number, String mobile) {
		return jdbcTemplate.update("update t_personal_info set mobile=?,zhifubao_access_number=? where user_id=? and aid=?",new Object[] {mobile,zhifubao_access_number,user_id,aid});
	}
	public int updatePersonalInfo(PersonalInfo personalInfo) {
		return jdbcTemplate.update("update t_personal_info set mobile=?,zhifubao_access_number=? ,personal_name=?,personal_id=? where user_id=? and aid=?",new Object[] {personalInfo.getMobile(),personalInfo.getZhifubao_access_number(),personalInfo.getPersonal_name(),personalInfo.getPersonal_id(),personalInfo.getUser_id(),personalInfo.getAid()});
	}
}
