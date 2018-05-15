package com.zhan.app.nearby.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.TextUtils;

/**
 * 用户信息缓存
 * 
 * @author youxifuhuaqi
 *
 */
@Service
public class UserCacheService {

	public static final String PERFIX_UPLOAD_TIME = "last_upload_time";
	public static final String PERFIX_BOTTLE_SEND_TIME = "user_bottle_send_time";
	public static final String PERFIX_BOTTLE_KEY_WORD = BottleKeyWordUtil.KEY;
	@Resource
	protected RedisTemplate<String, Serializable> redisTemplate;

	private String welcome;

	// public void cacheLoginToken(BaseUser user) {
	// try {
	// String id = String.valueOf(user.getUser_id());
	// redisTemplate.opsForValue().set(id, user.getToken());
	// redisTemplate.expire(id, 60, TimeUnit.DAYS);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void cacheValidateCode(String mobile, String code) {
		try {
			redisTemplate.opsForValue().set(mobile, code, 60, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCachevalideCode(String mobile) {
		try {
			redisTemplate.persist(mobile);
			Object codeObj = redisTemplate.opsForValue().get(mobile);
			if (codeObj != null) {
				return codeObj.toString();
			}
		} catch (Exception e) {

		}
		return null;
	}

	public boolean valideCode(String mobile, String code) {

		try {
			Object codeObj = redisTemplate.opsForValue().get(mobile);
			if (codeObj == null) {
				return false;
			}
			return codeObj.toString().equals(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void clearCode(String mobile) {
		try {
			redisTemplate.delete(mobile);
		} catch (Exception e) {
			e.printStackTrace();
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
				e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	public void setBottleKeyWord(String keywords) {
		redisTemplate.opsForValue().set(PERFIX_BOTTLE_KEY_WORD, keywords);
	}

	public String getBottleKeyWord() {
		Object obj = redisTemplate.opsForValue().get(PERFIX_BOTTLE_KEY_WORD);
		if (obj != null) {
			return obj.toString();
		}
		return "";
	}

}
