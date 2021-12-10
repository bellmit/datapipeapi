package cn.hy.gxpipeapi.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.httpclient.HttpClient;
import sun.net.util.URLUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 公共的请求类
 * 
 * @author zqb
 * @date 2018年5月31日
 */
public class HttpClientUtil {
	private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

	public static final String HTTP_UTF = "utf-8";

	private static final String MSG = "http请求,url:{},msg:{}";

	private HttpClientUtil() {

	}

	/**
	 * 发送post请求，参数用map接收
	 * @param url 地址
	 * @param map 参数
	 * @return 返回值
	 */
	public static String postMap(String url,Map<String,String> map) {
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for(Map.Entry<String,String> entry : map.entrySet())
		{
			pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
		}
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs,"UTF-8"));
			response = httpClient.execute(post);
			if(response != null && response.getStatusLine().getStatusCode() == 200)
			{
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity,"gbk");
			}
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
				if(response != null)
				{
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public static String doPostJson(String url,String json) {
	    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
	    HttpPost post = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
		post.setConfig(requestConfig);
	    String response = null;
	    try {
			post.addHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk");
//			post.setHeader("Accept", "application/json");
			StringEntity s = new StringEntity(json, Charset.forName("GBK"));
			post.setEntity(s);
			log.info("POST: {},{}", url, json);
			HttpResponse res = httpclient.execute(post);
			response = EntityUtils.toString(res.getEntity(),"gbk");
		} catch (Exception e) {
	    	log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	public static String doPostJsonWithToken(String url,String json,String token) {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
		post.setConfig(requestConfig);
		String response = null;
		try {
			post.addHeader("Content-type","application/json; charset=utf-8");
			post.setHeader("Accept", "application/json");
			post.setHeader("Authorization", "Bearer " + token);
			StringEntity s = new StringEntity(json, Charset.forName("UTF-8"));
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			String result = EntityUtils.toString(res.getEntity());// 返回json格式：
			response = result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	public static String doPostJsonWithAuthorization(String url,String json,String authorization) {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
		post.setConfig(requestConfig);
		String response = null;
		try {
			post.addHeader("Content-type","application/json; charset=utf-8");
			post.setHeader("Accept", "application/json");
			post.setHeader("Authorization", authorization);
			StringEntity s = new StringEntity(json, Charset.forName("UTF-8"));
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			String result = EntityUtils.toString(res.getEntity());// 返回json格式：
			response = result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	/**
	 * @param url
	 * @param paramList
	 *            构造NameValuePair的内容塞到HttpPost中
	 * @return
	 */

	public static String doPostDataTablePage(String url, List<NameValuePair> paramList) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){

			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
			// 模拟表单
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
			httpPost.setConfig(requestConfig);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
			httpPost.setEntity(entity);
			String msgStr = paramList!=null && !paramList.isEmpty() ? JSON.toJSONString(paramList) : null;
			log.info(MSG, url, msgStr);
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(),HTTP_UTF);
		} catch (Exception e) {
			log.error(MSG, url,e.getMessage(),e);
		}
		return null;

	}

	public static String doPostFormData(String url, Map<String, String> mapData) {
		CloseableHttpResponse response = null;
		// 创建httppost
		HttpPost httpPost = new HttpPost(url);
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			// 设置提交方式
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=gbk");
			// 添加参数
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			if (mapData.size() != 0) {
				// 将mapData中的key存在set集合中，通过迭代器取出所有的key，再获取每一个键对应的值
				for (Map.Entry<String, String> entry : mapData.entrySet()) {
					nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"gbk"));
			// 执行http请求
			response = httpClient.execute(httpPost);
			// 获得http响应体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 响应的结果
				return EntityUtils.toString(entity, "gbk");
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new RuntimeException("doPostFormData Error!");
		}
		throw new RuntimeException("doPostFormData Error!");
	}


	/**
	 * get请求，参数拼接在地址上
	 * @param url 请求地址加参数
	 * @return 响应
	 */
	public static String get(String url)
	{
		log.info("GET请求：{}", url);
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		get.setHeader("Content-Type","application/x-www-form-urlencoded; charset=gbk");
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(get);
			if(response != null && response.getStatusLine().getStatusCode() == 200)
			{
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity,"gbk");// 返回gbk格式
			}
			log.info("GET请求：{}", result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
				if(response != null)
				{
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getJSON(String url)
	{
		log.info("GET请求：{}", url);
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		get.setHeader("Content-Type","application/json;charset=utf-8");
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(get);
			if(response != null && response.getStatusLine().getStatusCode() == 200)
			{
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity,"utf-8");// 返回gbk格式
			}
			log.info("GET请求：{}", result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
				if(response != null)
				{
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
//		get("127.0.0.1:10318/OpenAPI/gettoken?" + URLEncoder.encode("username=广西省厅终端设备&password=ZB1lPm+5Tg/QCuXCIZ9seHUr7MqJ9lu21FfPFmbxezs=&appKey=SodqyXWscBYrRb1SEtIqjw==","utf-8"));
		getJSON("http://127.0.0.1:10318/OpenAPI/gettoken?username=广西省厅终端设备&password=" + URLEncoder.encode("ZB1lPm+5Tg/QCuXCIZ9seHUr7MqJ9lu21FfPFmbxezs=", "utf-8")
				+ "&appKey=" + URLEncoder.encode("SodqyXWscBYrRb1SEtIqjw==", "utf-8"));
	}

}
