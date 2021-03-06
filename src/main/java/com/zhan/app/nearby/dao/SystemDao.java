package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.BGM;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.RankUser;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("systemDao")
public class SystemDao extends BaseDao<Report> {
	@Resource
	private CityDao cityDao;

	public int insertReport(Report report) {

		int count = jdbcTemplate.update(
				"insert into t_report_record (user_id,target_id,tag_id,content,type,create_time,approval_result) values(?,?,?,?,?,?,?)",
				new Object[] { report.getUser_id(), report.getTarget_id(), report.getTag_id(), report.getContent(),
						report.getType(), report.getCreate_time(), report.getApproval_result() });
		return count;
	}

	public boolean existReport(Report report) {
		int count = jdbcTemplate.queryForObject(
				"select count(*) from t_report_record where user_id=? and target_id=? and type=?",
				new Object[] { report.getUser_id(), report.getTarget_id(), report.getType() }, Integer.class);
		return count > 0;
	}

	public List<Report> listReport(int type, int page, int count) {
		if (type == 0) {
			String sql = "select r.*,u.nick_name,u.sex,u.birthday,u.city_id from t_report_record r left join t_user u on r.target_id=u.user_id where r.type=? order by r.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { type, (page - 1) * count, count },
					new BeanPropertyRowMapper<Report>(Report.class) {
						@Override
						public Report mapRow(ResultSet rs, int rowNumber) throws SQLException {
							Report report = super.mapRow(rs, rowNumber);
							BaseUser user = new BaseUser();
							user.setUser_id(report.getTarget_id());
							user.setNick_name(rs.getString("nick_name"));
							user.setSex(rs.getString("sex"));
							user.setBirthday(rs.getDate("birthday"));
							int city_id = rs.getInt("city_id");
							user.setCity_id(city_id);
							user.setCity(cityDao.getCityById(city_id));
							report.setUser(user);
							return report;
						}
					});
		} else {
			String sql = "select *from t_report_record where type=? order by create_time desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { type, (page - 1) * count, count },
					getEntityMapper());
		}
	}

	public Report getReport(int id) {
		List<Report> list = jdbcTemplate.query("select *from t_report_record where id=?", new Object[] { id },
				getEntityMapper());
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public int updateReportState(int id,int state) {
		return jdbcTemplate.update("update t_report_record set approval_result=?,approval_time=? where id=?",
				new Object[] { state, new Date(), id });
	}

	public List<Report> listManagerReport(int page, int count) {
		String sql = "select r.*,t.name as tag_txt from t_report_record r left join t_sys_tag t on t.id=r.tag_id and t.type=8  order by create_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] {  (page - 1) * count, count },
				getEntityMapper());
	}

	public int getReportSizeByApproval(   ) {
		return jdbcTemplate.queryForObject("select count(*) from t_report_record" ,Integer.class);
	}

	public List<Exchange> loadExchangeHistory(long user_id, String aid, int pageIndex, int count) {
		return jdbcTemplate.query(
				"select *from  t_exchange_history where user_id=? and aid=? order by id desc limit ?,?",
				new Object[] { user_id, aid, (pageIndex - 1) * count, count },
				new BeanPropertyRowMapper<Exchange>(Exchange.class));
	}

	public Exchange loadExchange(int id) {
		List<Exchange> es = jdbcTemplate.query("select *from  t_exchange_history where id=?", new Object[] { id },
				new BeanPropertyRowMapper<Exchange>(Exchange.class));

		if (es != null && es.size() > 0) {
			return es.get(0);
		}
		return null;
	}

	public Integer getTotalExchangeRmmbByState(long user_id, String aid, ExchangeState state) {
		Integer total = jdbcTemplate.queryForObject(
				"select sum(rmb_fen) from  t_exchange_history where user_id=? and aid=? and state=?",
				new Object[] { user_id, aid, state.ordinal() }, Integer.class);
		return total;
	}

	/**
	 * 临时决策
	 * 
	 * @param ignoreUserId
	 * @param gender
	 * @param page_index
	 * @param limit
	 * @return
	 */
	public List<BaseUser> loadMaxRateMeiLiRandom(Long ignoreUserId, String gender,  int limit) {
		if (TextUtils.isEmpty(gender)) {
			String sql = "select u.user_id,u.nick_name,u.avatar,u.sex,u.birthday,u.birth_city_id, u.city_id ,u.isvip ,u.city_id as city_id ,c.name as city_name "
					+ " from   t_user u  left join t_sys_city c on c.id=u.city_id  where u.sys_status=0 and   u.user_id<>? and  u.avatar<>? order by rand() limit ?";
			return jdbcTemplate.query(sql,
					new Object[] { ignoreUserId == null ? 0 : ignoreUserId, "",   limit },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		} else {
			String sql = "select u.user_id,u.nick_name,u.avatar,u.sex,u.birthday,u.birth_city_id, u.city_id,u.isvip,u.city_id as city_id ,c.name as city_name  "
					+ " from   t_user u left join t_sys_city c on c.id=u.city_id     where u.sys_status=0 and u.user_id<>? and u.avatar<>? and u.sex=?  order by rand() limit ?";
			return jdbcTemplate
					.query(sql,
							new Object[] { ignoreUserId == null ? 0 : ignoreUserId, "", gender, limit },
							new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}
	}


	public PersonalInfo loadPersonalInfo(long user_id, String aid) {
		List<PersonalInfo> personalInfos = jdbcTemplate.query("select *from t_personal_info where user_id=? and aid=?",
				new Object[] { user_id, aid }, new BeanPropertyRowMapper<PersonalInfo>(PersonalInfo.class));
		if (personalInfos.size() > 0) {
			return personalInfos.get(0);
		} else {
			return null;
		}
	}

	public int updatePersonalInfo(long user_id, String aid, String zhifubao_access_number, String mobile) {
		return jdbcTemplate.update(
				"update t_personal_info set mobile=?,zhifubao_access_number=? where user_id=? and aid=?",
				new Object[] { mobile, zhifubao_access_number, user_id, aid });
	}

	public int updatePersonalInfo(PersonalInfo personalInfo) {
		return jdbcTemplate.update(
				"update t_personal_info set mobile=?,zhifubao_access_number=? ,personal_name=?,personal_id=? where user_id=? and aid=?",
				new Object[] { personalInfo.getMobile(), personalInfo.getZhifubao_access_number(),
						personalInfo.getPersonal_name(), personalInfo.getPersonal_id(), personalInfo.getUser_id(),
						personalInfo.getAid() });
	}

	
	public int insertIpToBlack(String ip) {
		int count = jdbcTemplate.update(
				"insert into t_black_ips (ip) values(?)",new Object[] { ip });
		return count;
	}
	@Cacheable(value="thrity_minute",key="#root.methodName")
	public List<String> loadBlackIPs() {
		return jdbcTemplate.queryForList("select *from t_black_ips", String.class);
	}
	
	public int deleteFromBlackIps(String ip) {
		return jdbcTemplate.update("delete from t_black_ips where ip="+ip);
	}
	

	/**
	 * 插入bgm
	 * @param bmg
	 */
	public int isExist(BGM bmg) {
		 String sql="select count(*) from  t_bgm where author=? and name=?";
		 return jdbcTemplate.queryForObject(sql, new Object[] {bmg.getAuthor(),bmg.getName()},Integer.class);
	}
	
	public void updateBGM(BGM bgm) {
		jdbcTemplate.update("update t_bgm set url=?,create_time=? where author=? and name=? ",new Object[] {bgm.getUrl(),bgm.getCreate_time(),bgm.getAuthor(),bgm.getName()});
	}
	/**
	 * 随机查询bgm
	 * @param count
	 * @return
	 */
	public List<BGM> loadBGM(int count) {
		return jdbcTemplate.query("select *from t_bgm order by rand() limit "+count, new BeanPropertyRowMapper<BGM>(BGM.class)); 
	}

	public int insertTouTiaoUser(long user_id) {
		 jdbcTemplate.update("delete from t_toutiao_user where uid="+user_id);
		 return jdbcTemplate.update(
				"insert into t_toutiao_user (uid,create_time) values(?,?)",
				new Object[] { user_id,new Date()});
	}
	
	public long getTouTiaoFirstUserId() {
		List<Long> ids= jdbcTemplate.queryForList("select uid from t_toutiao_user order by create_time desc limit 1", Long.class);
		if(ids.isEmpty()) {
			return 0;
		}
		return ids.get(0);
	}
	
	
	public List<BaseUser> getTouTiaoUser(int startIndex,int limit){
		String sql="select u.user_id,u.nick_name,u.avatar,u.birthday, u.isvip,u.video_cert_status,u.city_id as city_id ,c.name as city_name  "
				+ " from t_toutiao_user tt "
				+ " left join t_user u on tt.uid=u.user_id left join t_sys_city c on c.id=u.city_id  where u.type<>0 and u.type<>2  and u.avatar <>'' and u.avatar is not null  and u.avatar<>'illegal.jpg' and u.sys_status<>1  order by tt.create_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] {startIndex,limit}, new BeanPropertyRowMapper<BaseUser>(BaseUser.class) {
			@Override
			public BaseUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
				BaseUser u = super.mapRow(rs, rowNumber);
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
	}
	public List<Map<String, Object>> getTouTiaoUserIndexVal(){
		String sql="SELECT (@i:=@i+1) i,uid FROM t_toutiao_user, (SELECT @i:=0) as i order by create_time desc ";
		return jdbcTemplate.queryForList(sql);
	}

	public void insertBlackWord(String w) {
		jdbcTemplate.update("insert ignore into t_black_words(word) values(?)",new Object[] {w});
	}
	public void delBlackWord(String w) {
		jdbcTemplate.update("delete from t_black_words where word=?",new Object[] {w});
	}

	public List<String> loadBlackWords() {
		return jdbcTemplate.queryForList("select *from t_black_words", String.class);		
	}

	public void deleteReport(int id) {
	     jdbcTemplate.update("delete from "+getTableName()+" where id="+id);
	}
}
