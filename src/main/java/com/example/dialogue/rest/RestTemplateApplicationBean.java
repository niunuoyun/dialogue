package com.example.dialogue.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 
 * Description: 
 *
 * @date Sep 20, 2018
 * @author Jianwei Xu
 *
 */

@Configuration
@Slf4j
public class RestTemplateApplicationBean {
	
	public static int connectionRequestTimeout = 4000;
	public static int midConnectionRequestTimeout = 500;
	public static int shortConnectionRequestTimeout = 150;
	public static int connectTimeout = 4000;
	public static int midConnectTimeout = 500;
	public static int shortConnectTimeout = 150;
	public static int readTimeout = 4000;
	public static int midReadTimeout = 500;
	public static int shortReadTimeout = 150;
	public static int defaultMaxPerRoute = 10000;
	public static int maxTotal = 100000;
	public static boolean isSimple = false;
	public static String charset = "utf-8";

	public static RestTemplate restTemplate;
	public static RestTemplate midRestTemplate;
	public static RestTemplate shortRestTemplate;

	@Bean
	@Primary
	public RestTemplate restTemplate(@Qualifier("interceptors") List<ClientHttpRequestInterceptor> interceptors) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
		if (isSimple) {
			SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
			restTemplate.setRequestFactory(schrf);
			setTemplateCharset(restTemplate);
			return restTemplate;
		} else {
			PoolingHttpClientConnectionManager phcm = new PoolingHttpClientConnectionManager();
			phcm.setDefaultMaxPerRoute(defaultMaxPerRoute);
			phcm.setMaxTotal(maxTotal);
			CloseableHttpClient builder = HttpClientBuilder
					.create()
					.setConnectionManager(phcm)
					.build();
			HttpComponentsClientHttpRequestFactory hcchf = new HttpComponentsClientHttpRequestFactory(builder);
			hcchf.setConnectionRequestTimeout(connectionRequestTimeout);
			hcchf.setConnectTimeout(connectTimeout);
			hcchf.setReadTimeout(readTimeout);
			restTemplate.setRequestFactory(hcchf);
			setTemplateCharset(restTemplate);
			return restTemplate;
		}

	}

	@Bean(name = "midRestTemplate")
	public RestTemplate midRestTemplate(@Qualifier("interceptors") List<ClientHttpRequestInterceptor> interceptors) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
        if (isSimple) {
            SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
            restTemplate.setRequestFactory(schrf);
            setTemplateCharset(restTemplate);
            return restTemplate;
        } else {
			PoolingHttpClientConnectionManager phcm = new PoolingHttpClientConnectionManager();
			phcm.setDefaultMaxPerRoute(defaultMaxPerRoute);
			phcm.setMaxTotal(maxTotal);
			CloseableHttpClient builder = HttpClientBuilder
					.create()
					.setConnectionManager(phcm)
					.build();
			HttpComponentsClientHttpRequestFactory hcchf = new HttpComponentsClientHttpRequestFactory(builder);
			hcchf.setConnectionRequestTimeout(midConnectionRequestTimeout);
			hcchf.setConnectTimeout(midConnectTimeout);
			hcchf.setReadTimeout(midReadTimeout);
			restTemplate.setRequestFactory(hcchf);
			setTemplateCharset(restTemplate);
			return restTemplate;
		}

	}

	@Bean(name = "shortRestTemplate")
	public RestTemplate shortRestTemplate(@Qualifier("interceptors") List<ClientHttpRequestInterceptor> interceptors) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
        if (isSimple) {
            SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
            restTemplate.setRequestFactory(schrf);
            setTemplateCharset(restTemplate);
            return restTemplate;
        } else {
			PoolingHttpClientConnectionManager phcm = new PoolingHttpClientConnectionManager();
			phcm.setDefaultMaxPerRoute(defaultMaxPerRoute);
			phcm.setMaxTotal(maxTotal);
			CloseableHttpClient builder = HttpClientBuilder
					.create()
					.setConnectionManager(phcm)
					.build();
			HttpComponentsClientHttpRequestFactory hcchf = new HttpComponentsClientHttpRequestFactory(builder);
			hcchf.setConnectionRequestTimeout(shortConnectionRequestTimeout);
			hcchf.setConnectTimeout(shortConnectTimeout);
			hcchf.setReadTimeout(shortReadTimeout);
			restTemplate.setRequestFactory(hcchf);
			setTemplateCharset(restTemplate);
			return restTemplate;
		}

	}

	public void setTemplateCharset(RestTemplate templateCharset){
		List<HttpMessageConverter<?>> list = templateCharset.getMessageConverters();
		for (HttpMessageConverter<?> converter : list) {
			if(converter instanceof StringHttpMessageConverter) {
				((StringHttpMessageConverter) converter).setDefaultCharset(Charset.forName(charset));
			}
		}
	}

	/**
	 * 
	 * 
	public static int connectionRequestTimeout = 5000;
	public static int connectTimeout = 5000;
	public static int readTimeout = 5000;
	public static int per = 60;
	public static int maxTotal = 60;
	public static boolean isSimple = false;
	 * 
	 * @param isSimple
	 * @param connectionRequestTimeout
	 */
	public static void reset(boolean isSimple,
			int connectionRequestTimeout,
			int connectTimeout,
			int readTimeout,
			int defaultMaxPerRoute,
			int maxTotal
			) {
		if (isSimple) {
			SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
			if (connectTimeout > 0) {
				schrf.setConnectTimeout(connectTimeout);
				RestTemplateApplicationBean.connectTimeout = connectTimeout;
			} else {
				schrf.setConnectTimeout(RestTemplateApplicationBean.connectTimeout);
			}
			if (readTimeout > 0) {
				schrf.setReadTimeout(readTimeout);
				RestTemplateApplicationBean.readTimeout = readTimeout;
			} else {
				schrf.setReadTimeout(RestTemplateApplicationBean.readTimeout);
			}
			restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplateApplicationBean.isSimple = isSimple;
			return;
		}
		
		RestTemplateApplicationBean.isSimple = isSimple;
		boolean reset = false;
		PoolingHttpClientConnectionManager phcm = new PoolingHttpClientConnectionManager();
		if (defaultMaxPerRoute > 0) {
			phcm.setDefaultMaxPerRoute(defaultMaxPerRoute);
			RestTemplateApplicationBean.defaultMaxPerRoute = defaultMaxPerRoute;
			reset = true;
		} else {
			phcm.setDefaultMaxPerRoute(RestTemplateApplicationBean.defaultMaxPerRoute);
		}
		
		if (maxTotal > 0) {
			phcm.setMaxTotal(maxTotal);
			RestTemplateApplicationBean.maxTotal = maxTotal;
			reset = true;
		} else {
			phcm.setMaxTotal(RestTemplateApplicationBean.maxTotal);
		}
		
		CloseableHttpClient builder = HttpClientBuilder
				.create()
				.setConnectionManager(phcm)
				.build();
		HttpComponentsClientHttpRequestFactory hcchf = new HttpComponentsClientHttpRequestFactory(builder);
		if (connectionRequestTimeout > 0) {
			hcchf.setConnectionRequestTimeout(connectionRequestTimeout);
			RestTemplateApplicationBean.connectionRequestTimeout = connectionRequestTimeout;
			reset = true;
		} else {
			hcchf.setConnectionRequestTimeout(RestTemplateApplicationBean.connectionRequestTimeout);
		}
		if (connectTimeout > 0) {
			hcchf.setConnectTimeout(connectTimeout);
			RestTemplateApplicationBean.connectTimeout = connectTimeout;
			reset = true;
		} else {
			hcchf.setConnectTimeout(RestTemplateApplicationBean.connectTimeout);
		}
		if (readTimeout > 0) {
			hcchf.setReadTimeout(readTimeout);
			RestTemplateApplicationBean.readTimeout = readTimeout;
			reset = true;
		} else {
			hcchf.setReadTimeout(RestTemplateApplicationBean.readTimeout);
		}
		
		if (reset) {
			restTemplate.setRequestFactory(hcchf);
		}
	}

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		RestTemplateApplicationBean.restTemplate = restTemplate;
	}

	@Resource
	public void setShortRestTemplate(RestTemplate shortRestTemplate) {
		RestTemplateApplicationBean.shortRestTemplate = shortRestTemplate;
	}

	@Resource
	public void setMidRestTemplate(RestTemplate midRestTemplate) {
		RestTemplateApplicationBean.midRestTemplate = midRestTemplate;
	}

	
}
