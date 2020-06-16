package com.example.dialogue.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * @date May 15, 2018
 * @author Jianwei Xu
 * 
 * Description: 
 *
 */

public class JsonUtils {
	public static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	/**
	 * 
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static JsonNode toJsonNode(String json) throws IOException {
		JsonNode node = mapper.readTree(json);
		return node;
	}
	
	public static JsonNode toJsonNode(Object obj) throws IOException {
		String json = toString(obj);
		return toJsonNode(json);
	}
	
	public static<T> T toObject(String json, Class<T> _class) throws IOException {
		if(json==null) throw new IOException("input is null string");
		return mapper.readValue(json, _class);
	}
	public static<T> T toObject(String json, TypeReference<T> typeReference) throws IOException {
		if(json==null) throw new IOException("input is null string");
		return mapper.readValue(json, typeReference);
	}
	
	public static<T> T toObject(JsonNode json, Class<T> _class) throws IOException {
		return mapper.readValue(json.toString(), _class);
	}
	
	public static JSONObject toJSONObject(String json) {
		return JSONObject.parseObject(json);
	}
	
	public static String toString(Object obj) throws JsonProcessingException {
		if (obj instanceof String) {
			return obj.toString();
		}
		if (obj instanceof Character) {
			return obj.toString();
		}
		String jsonInString = mapper.writeValueAsString(obj);
		return jsonInString;
	}
	
	public static JsonNode convert(JSONObject jo) throws IOException {
		return toJsonNode(jo.toJSONString());
	}
	
	public static ObjectNode createObjectNode() {
		return JsonNodeFactory.instance.objectNode();
	}

	public static ObjectNode createAnswerNode() {
		ObjectNode answer = JsonNodeFactory.instance.objectNode();
		answer.put("reply", "");
		answer.put("score", -1.0f);
		answer.put("source", "unknown");
		return answer;
	}
	
	public static ArrayNode createArrayNode() {
		return JsonNodeFactory.instance.arrayNode();
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void main(String[] args) {
		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
		linkedHashSet.add("a");
		linkedHashSet.add("b");
		
		try {
			String json = JsonUtils.toString(linkedHashSet);
			linkedHashSet = JsonUtils.toObject(json, LinkedHashSet.class);
			System.out.println(linkedHashSet);;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		String c;
		try {
			c = JsonUtils.toString(1);
			System.out.println(c);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
