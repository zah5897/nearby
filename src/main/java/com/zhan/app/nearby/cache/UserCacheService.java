package com.zhan.app.nearby.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

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
		redisTemplate.opsForValue().set(id, user.getToken());
		redisTemplate.expire(id, 60, TimeUnit.DAYS);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void cacheValidateCode(String mobile, String code) {
		try{
	    redisTemplate.opsForValue().set(mobile, code);
	    redisTemplate.expire(mobile, 60, TimeUnit.SECONDS);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public String getCachevalideCode(String mobile) {
		try{
			redisTemplate.persist(mobile);
		Object codeObj = redisTemplate.opsForValue().get(mobile);
		if(codeObj!=null){
			return codeObj.toString();
		}
		 }catch(Exception e){
			
		}
		return null;
		}
		public boolean valideCode(String mobile, String code) {
		
		try{
		Object codeObj = redisTemplate.opsForValue().get(mobile);
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
     		redisTemplate.delete(mobile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getCacheToken(long user_id) {
		try{
		Object tokenObj = redisTemplate.opsForValue().get(user_id);
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
		redisTemplate.delete(sid);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
