package com.zhan.app.nearby.cache;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.Tag;
import com.zhan.app.nearby.util.JSONUtil;

/**
 * 用户信息缓存
 * 
 * @author youxifuhuaqi
 *
 */
@Service
public class InfoCacheService {

	public static final String GIFT_SEND_NOTICE = "gift_send_notice";
	@Resource
	protected RedisTemplate<String, Serializable> redisTemplate;

	public List<Tag> getTagsByKey(String key) {
		Object obj = redisTemplate.opsForValue().get(key);
		if (obj != null) {
			List<Tag> tags = JSON.parseArray(obj.toString(), Tag.class);
			return tags;
		}
		return null;
	}

	public void setTagsByKey(String key, List<Tag> tags) {
		if (tags != null) {
			redisTemplate.opsForValue().set(key, JSON.toJSONString(tags));
		}
	}

	public List<City> getCities(String key) {
		Object obj = redisTemplate.opsForValue().get(key);
		if (obj != null) {
			List<City> cities = JSON.parseArray(obj.toString(), City.class);
			return cities;
		}
		return null;
	}

	public void setCities(String key, List<City> cities) {
		if (cities != null) {
			redisTemplate.opsForValue().set(key, JSON.toJSONString(cities));
		}
	}

	public void clear(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cacheGiftSendNotice(String json) {
		redisTemplate.opsForValue().set(GIFT_SEND_NOTICE, json);
	}

	public List<GiftOwn> getGiftSendNoticeCache() {
		Object obj = redisTemplate.opsForValue().get(GIFT_SEND_NOTICE);
		if (obj == null) {
			return null;
		}
		String json = obj.toString();
		List<GiftOwn> owns = JSONUtil.jsonToList(json, new TypeReference<List<GiftOwn>>() {
		});
		return owns;
	}
}
