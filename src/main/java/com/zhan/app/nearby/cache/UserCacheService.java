package com.zhan.app.nearby.cache;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.util.RedisKeys;

/**
 * 用户信息缓存
 * 
 * @author youxifuhuaqi
 *
 */
@Service
public class UserCacheService {

	@Resource
	protected RedisTemplate<String, Serializable> redisTemplate;

	public void cacheLoginToken(User user) {
		try{
		String id = String.valueOf(user.getUser_id());
		// String json = JSON.toJSONString(user);
		redisTemplate.opsForHash().put(RedisKeys.KEY_LOGIN_TOKEN, id, user.getToken());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void cacheValidateCode(String mobile, String code) {
		try{
		redisTemplate.opsForHash().put(RedisKeys.KEY_CODE, mobile, code);
		redisTemplate.opsForHash().put(RedisKeys.KEY_CODE_TIME, mobile,
				String.valueOf(System.currentTimeMillis() / 1000));// 精确秒级别
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public long getLastCodeTime(String mobile) {
		try{
		Object time = redisTemplate.opsForHash().get(RedisKeys.KEY_CODE_TIME, mobile);
		if (time == null) {
			return 0;
		}
		return Long.parseLong(time.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0L;
	}

	public boolean valideCode(String mobile, String code) {
		
		try{
		
		if (!redisTemplate.opsForHash().hasKey(RedisKeys.KEY_CODE, mobile)) {
			return false; // 不存在该手机号码发送的code
		}
		Object codeObj = redisTemplate.opsForHash().get(RedisKeys.KEY_CODE, mobile);
		if (codeObj == null) {
			return false;
		}

		return codeObj.toString().equals(code);
	}catch(Exception e){
		e.printStackTrace();
	}
		return false;
	}

	public void clearCode(String mobile) {
		try{
		redisTemplate.opsForHash().delete(RedisKeys.KEY_CODE, mobile);
		redisTemplate.opsForHash().delete(RedisKeys.KEY_CODE_TIME, mobile);// 精确秒级别
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getCacheToken(long user_id) {
		try{
		Object tokenObj = redisTemplate.opsForHash().get(RedisKeys.KEY_LOGIN_TOKEN, String.valueOf(user_id));
		if (tokenObj != null) {
			return tokenObj.toString();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public void clearLoginUser(String token, long user_id) {
		try{
		String sid = String.valueOf(user_id);
		redisTemplate.opsForHash().delete(RedisKeys.KEY_LOGIN_TOKEN, sid);
		redisTemplate.opsForHash().delete(RedisKeys.KEY_LOGIN_TOKEN, sid);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
