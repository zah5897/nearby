package com.zhan.app.nearby.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
	private static Logger log = Logger.getLogger(JSONUtil.class);
	private static ObjectMapper objectMapper;

	private static ObjectMapper getMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
	
	
	
	
	public static void setSerializationInclusion(JsonInclude.Include incl) {
		getMapper().setSerializationInclusion(incl);
	}

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> jsonToMap(String jsonStr) {
		try {
			return getMapper().readValue(jsonStr, LinkedHashMap.class);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return null;
	}

	public static Map<String, Object> jsonToMap(Object obj) {
		try {
			return jsonToMap(writeValueAsString(obj));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return null;
	}

	public static String writeValueAsString(Object object) {
		try {
			return getMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		return "";
	}

	public static <T> T jsonToObj(String jsonStr, TypeReference<T> type) {
		try {
			return getMapper().readValue(jsonStr, type);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static <T> T jsonToList(String jsonStr, TypeReference<T> type) {
		try {
			return getMapper().readValue(jsonStr, type);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
}
