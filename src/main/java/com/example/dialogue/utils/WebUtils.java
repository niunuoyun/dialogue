package com.example.dialogue.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.dialogue.rest.RestTemplateApplicationBean;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebUtils {

	private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);
	
	public static int READ_TIMEOUT = 10000;
	public static int CONNECT_TIMEOUT = 10000;

	public static String appendSegmentToPath(String addr, String path) {
		java.net.URI uri;
		try {
			uri = new java.net.URI (addr);
			return uri.resolve(path).toString();
		} catch (URISyntaxException e) {
			logger.error(String.format("malformed urls %s %s", addr, path));
			return addr + path;
		}
		
	}
	
	public static HttpHeaders createJsonHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return responseHeaders;
	}
	
	public static HttpHeaders createTextHeader() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		return responseHeaders;
	}

	/**
	 * 
	 * 
https://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection

According to http://docs.oracle.com/javase/6/docs/technotes/guides/net/http-keepalive.html and OpenJDK source code.

(When keepAlive == true)

If client called HttpURLConnection.getInputSteam().close(), the later call to HttpURLConnection.disconnect() will NOT close the Socket. i.e. The Socket is reused (cached)

If client does not call close(), call disconnect() will close the InputSteam and close the Socket.

So in order to reuse the Socket, just call InputStream close(). Do not call HttpURLConnection disconnect().
	 * 
	 * @param json
	 * @param link
	 * @return
	 * @throws IOException 
	 */
	@Deprecated
	public static String post2(String link, String json) throws IOException {
		HttpURLConnection httpConnection = null;
		{
			
			JSONObject data = JsonUtils.toJSONObject(json);
			URL url = new URL(link);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "application/json");
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setReadTimeout(10000);
			
			httpConnection.setConnectTimeout(READ_TIMEOUT);
			httpConnection.setReadTimeout(CONNECT_TIMEOUT);
			DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
			wr.write(data.toString().getBytes());
			Integer responseCode = httpConnection.getResponseCode();

			BufferedReader bufferedReader;

			// Creates a reader buffer
			if (responseCode > 199 && responseCode < 300) {
				bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
			}

			// To receive the response
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line).append("\n");
			}
			bufferedReader.close();

			// Prints the response
			return content.toString();

		}


	}

	public static String post(String link, String json) throws IOException {
		return post(link,json,RestTemplateApplicationBean.restTemplate);
	}

	public static String midPost(String link, String json) throws IOException {
		return post(link,json,RestTemplateApplicationBean.midRestTemplate);
	}

	public static String midPost(String link, String json,HttpHeaders headers) throws IOException {
		return post(link,json,RestTemplateApplicationBean.midRestTemplate,headers);
	}

	public static String shortPost(String link, String json) throws IOException {
		return post(link,json,RestTemplateApplicationBean.shortRestTemplate);
	}

	public static String post(String addr,String path, String json) throws IOException {
		String link = WebUtils.appendSegmentToPath(addr, path);
		return post(link,json,RestTemplateApplicationBean.restTemplate);
	}

	public static String post(String link, String json,HttpHeaders headers) throws IOException {
		return post(link,json,RestTemplateApplicationBean.restTemplate,headers);
	}

	public static String postRaw(String addr,String path, String json) throws IOException {
		String link = WebUtils.appendSegmentToPath(addr, path);
		return postRaw(link,json,RestTemplateApplicationBean.midRestTemplate);
	}

	public static String midPost(String addr,String path, String json) throws IOException {
		String link = WebUtils.appendSegmentToPath(addr, path);
		return post(link,json,RestTemplateApplicationBean.midRestTemplate);
	}

	public static String shortPost(String addr,String path, String json) throws IOException {
		String link = WebUtils.appendSegmentToPath(addr, path);
		return post(link,json,RestTemplateApplicationBean.shortRestTemplate);
	}

	/**
	 * json post
	 * @param link
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String post(String link, String json, RestTemplate restTemplate) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		return post(link,json,restTemplate,headers);
	}
	/**
	 * json post
	 * @param link
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String postRaw(String link, String json, RestTemplate restTemplate) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		return postRaw(link,json,restTemplate,headers);
	}
	/**
	 * json post
	 * @param link
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String post(String link, String json, RestTemplate restTemplate, HttpHeaders headers) throws IOException {
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<JsonNode> entity = new HttpEntity<>(JsonUtils.toJsonNode(json), headers);
		try {
            String result2 = restTemplate.postForObject(link, entity, String.class);
			Map<String, Object> context = new HashMap<>();
			context.put("addr", link);
			context.put("query", json);
			context.put("response", result2);
            return result2;
		} catch (Exception ex) {
      		logger.warn("webclient_post_exception, addr : " + link + ", query : " + json + ", ex : " + ex);
			throw ex;
		}
	}

	public static String postRaw(String link, String params, RestTemplate restTemplate, HttpHeaders headers) throws IOException {
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity entity = new HttpEntity<>(params, headers);
		try {
			String result2 = restTemplate.postForObject(link, entity, String.class);
			Map<String, Object> context = new HashMap<>();
			context.put("addr", link);
			context.put("query", params);
			context.put("response", result2);

			return result2;
		} catch (Exception ex) {
			logger.warn("webclient_post_exception, addr : " + link + ", query : " + params + ", ex : " + ex);
			throw ex;
		}
	}

	/**
	 * json post
	 * @param link
	 * @return
	 * @throws IOException
	 */
	public static String delete(String link) throws IOException {
		ResponseEntity<String> result2 = RestTemplateApplicationBean.restTemplate.exchange(link,
				HttpMethod.DELETE,
				null,
				String.class);
		return result2.getBody();
	}
	
	/**
	 * non json
//	 * @param restTemplate
	 * @param link
	 * @return
	 * @throws IOException
	 */
	public static String get(String link) throws IOException {
		String result2 = RestTemplateApplicationBean.restTemplate.getForObject(
				link,
				String.class);
		return result2;
	}
	
	/**
	 * link is in the format "http://www.google.com/search?q=%s&location=%s" ...
	 * determine the number of %s by yourself!
	 * 
	 * @param link
//	 * @param params
	 * @return
	 * @throws IOException 
	 */
	@Deprecated
	public static String get(String link, String param) throws IOException {
		
		URL obj;
		{
			String url = link + param;
			obj = new URL(url);
			HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
			
			httpConnection.setConnectTimeout(READ_TIMEOUT);
			httpConnection.setReadTimeout(CONNECT_TIMEOUT);
			httpConnection.setRequestMethod("GET");


			BufferedReader in = new BufferedReader(
			        new InputStreamReader(httpConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		}
		
		
	}
	
	public static void main(String[] args) {
		String a = appendSegmentToPath("http://localhost:50092/", "/mrc");
		System.out.println(a);
	}
}
