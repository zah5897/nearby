package com.zhan.app.nearby.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.zhan.app.nearby.util.TextUtils;

/**
 * 用户信息缓存
 * 
 * @author youxifuhuaqi
 *
 */
@Service
public class UserCacheService {
	private static Logger log = Logger.getLogger(UserCacheService.class);
	public static final String PERFIX_UPLOAD_TIME = "last_upload_time";
	public static final String PERFIX_BOTTLE_SEND_TIME = "user_bottle_send_time";
	public static final String PERFIX_BOTTLE_KEY_WORD = "bottle_key_word";
	public static final String PERFIX_U_ONLINE = "user_online";
	public static final String PERFIX_U_SMS_COUNT = "user_sms_count";
	public static final String PERFIX_U_EXCHAGE_COUNT = "user_exchage_count";
	public static final String PERFIX_U_BIND_ZHIFUBAO = "user_bind_zhifubao";

	public static final String MANAGER_AUTH_DATA = "manager_auth_data_";

	@Resource
	protected RedisTemplate<String, Object> redisTemplate;

	private String welcome;

	public int getUserCodeCacheCount(String mobile) {
		Object codeObj = redisTemplate.opsForHash().get(PERFIX_U_SMS_COUNT, mobile);
		if (codeObj == null) {
			return 0;
		} else {
			try {
				int count = Integer.parseInt(codeObj.toString());
				return count;
			} catch (Exception e) {
				return 0;
			}
		}
	}

	public int getExchageCodeCacheCount(String mobile) {
		Object codeObj = redisTemplate.opsForHash().get(PERFIX_U_EXCHAGE_COUNT, mobile);
		if (codeObj == null) {
			return 0;
		} else {
			try {
				int count = Integer.parseInt(codeObj.toString());
				return count;
			} catch (Exception e) {
				return 0;
			}
		}
	}

	// 清理掉缓存次数
	public void clearCacheCount() {
		redisTemplate.delete(PERFIX_U_SMS_COUNT);
		redisTemplate.delete(PERFIX_U_EXCHAGE_COUNT);
	}

	public void cacheRegistValidateCode(String mobile, String code, int flag) {
		try {
			redisTemplate.opsForValue().set(mobile, code, 60, TimeUnit.MINUTES);
			if (flag != -1000) {//
				addTodayMobileSmsCount(mobile);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public boolean valideRegistCode(String mobile, String code) {

		try {
			Object codeObj = redisTemplate.opsForValue().get(mobile);
			if (codeObj == null) {
				return false;
			}
			return codeObj.toString().equals(code);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return true;
	}

	public void clearCode(String mobile) {
		try {
			redisTemplate.delete(mobile);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public String getWelcome() {
		if (TextUtils.isEmpty(welcome)) {
			String temp = "附近发生新鲜事，快来瞧一瞧！";
			try {
				Object cacheWelcome = redisTemplate.opsForValue().get("welcome");
				if (cacheWelcome != null) {
					welcome = cacheWelcome.toString();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if (TextUtils.isEmpty(welcome)) {
				welcome = temp;
			}
		}
		return welcome;
	}

	public boolean setWelcome(String welcome) {
		if (TextUtils.isEmpty(welcome)) {
			return false;
		}
		this.welcome = welcome;
		try {
			redisTemplate.opsForValue().set("welcome", welcome);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}

	public long getLastUploadTime(long user_id) {
		Object lastTime = redisTemplate.opsForValue().get(PERFIX_UPLOAD_TIME + String.valueOf(user_id));
		if (lastTime == null) {
			return 0;
		} else {
			return Long.parseLong(lastTime.toString());
		}
	}

	public void setLastUploadTime(long user_id) {
		try {
			String key = PERFIX_UPLOAD_TIME + String.valueOf(user_id);
			redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis() / 1000), 1,
					TimeUnit.MINUTES);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public long getLastBottleSendTime(long user_id) {
		Object lastTime = redisTemplate.opsForValue().get(PERFIX_BOTTLE_SEND_TIME + String.valueOf(user_id));
		if (lastTime == null) {
			return 0;
		} else {
			return Long.parseLong(lastTime.toString());
		}
	}

	public void setLastBottleSendTime(long user_id) {
		try {
			String key = PERFIX_BOTTLE_SEND_TIME + String.valueOf(user_id);
			redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis() / 1000), 10,
					TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void cacheExchageValidateCode(String mobile, String code, int flag) {
		try {
			redisTemplate.opsForValue().set(mobile, code, 60, TimeUnit.MINUTES);
			if (flag != -1000) {//
				addTodayMobileSmsCount(mobile);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public boolean valideExchageCode(String mobile, String code) {

		try {
			Object codeObj = redisTemplate.opsForValue().get(mobile);
			if (codeObj == null) {
				return false;
			}
			return codeObj.toString().equals(code);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return true;
	}

	public void cacheBindZhiFuBaoCode(String mobile, String code) {
		try {
			redisTemplate.opsForHash().put(PERFIX_U_BIND_ZHIFUBAO, mobile, code);
			addTodayMobileSmsCount(mobile);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public boolean validateBindZhiFuBaoCode(String mobile, String code) {
		try {
			Object codeObj = redisTemplate.opsForHash().get(PERFIX_U_BIND_ZHIFUBAO, mobile);
			if (codeObj == null) {
				return false;
			}
			return codeObj.toString().equals(code);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return true;
	}

	public void test() {
		redisTemplate.opsForValue().set("test", String.valueOf(System.currentTimeMillis()));
	}

	public Object getTest() {
		return redisTemplate.opsForValue().get("test");
	}

	public void addTodayMobileSmsCount(String mobile) {
		int count = getUserCodeCacheCount(mobile);
		redisTemplate.opsForHash().put(PERFIX_U_SMS_COUNT, mobile, String.valueOf(count + 1));
	}

	public void putManagerAuthData(String ip, String name) {
		String key = MANAGER_AUTH_DATA + ip;
		if (redisTemplate.hasKey(key)) {
			redisTemplate.expire(key, 20, TimeUnit.MINUTES);
		} else {
			redisTemplate.opsForValue().set(key, name, 10, TimeUnit.MINUTES); // 设置30分钟过期
		}
	}

	public boolean validateManagerAuthDataActive(String ip) {
		String key = MANAGER_AUTH_DATA + ip;
		if (redisTemplate.hasKey(key)) {
			Long expire = redisTemplate.boundHashOps(key).getExpire();
			if (expire != null && expire > 10) {
				return true;
			}
		}
		return false;
	}

	public String getManagerAuthName(String ip) {
		String key = MANAGER_AUTH_DATA + ip;
		Object obj = redisTemplate.opsForValue().get(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	public void removeManagerAuthData(String ip) {
		String key = MANAGER_AUTH_DATA + ip;
		redisTemplate.delete(key);
	}

	/**
	 * 上锁 将键值对设定一个指定的时间timeout.
	 *
	 * @param key
	 * @param timeout 键值对缓存的时间，单位是秒
	 * @return 设置成功返回true，否则返回false
	 */
	public boolean tryLock(String key, Object value, long timeout) {
		if(redisTemplate.hasKey(key)&&redisTemplate.boundHashOps(key).getExpire()>0) {
              return false;			
		}else {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
			return true;
		}
	}
	
	public Object getLockIP(String key) {
		return redisTemplate.opsForValue().get(key);
	}
}
