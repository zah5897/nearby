package com.zhan.app.nearby.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
	private static ObjectMapper objectMapper;

	private static ObjectMapper getMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String jsonStr) {
		try {
			return getMapper().readValue(jsonStr, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String writeValueAsString(Object object) throws JsonProcessingException {
		return getMapper().writeValueAsString(object);
	}

	public static JSONObject obj2JSON(Object object) {
		JSONObject jsonObject = (JSONObject) JSON.toJSON(object);
		return jsonObject;
	}

	public static <T> T jsonToList(String jsonStr, TypeReference<T> type) {
		try {
			return getMapper().readValue(jsonStr, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
