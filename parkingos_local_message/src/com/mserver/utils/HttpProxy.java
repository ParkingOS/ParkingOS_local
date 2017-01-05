package com.mserver.utils;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class HttpProxy
{
  public String doGetInit(String url)
  {
    HttpClient httpClient = new HttpClient();
    HttpMethod method = new GetMethod(url);
    try {
      httpClient.setConnectionTimeout(20000);
      httpClient.executeMethod(method);
      if (method.getStatusCode() == 200)
        return method.getResponseBodyAsString();
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (method != null)
        method.releaseConnection();
    }
    if (method != null) {
      method.releaseConnection();
    }
    return null;
  }

  public String doGet(String url)
  {
    HttpClient httpClient = new HttpClient();
    HttpMethod method = new GetMethod(url);
    try {
      httpClient.setConnectionTimeout(5000);
      httpClient.getHttpConnectionManager().getParams().setSoTimeout(5000);
      httpClient.executeMethod(method);
      if (method.getStatusCode() == 200)
        return method.getResponseBodyAsString();
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (method != null)
        method.releaseConnection();
    }
    if (method != null) {
      method.releaseConnection();
    }
    return null;
  }

  public String doPost(String url, Map<String, String> params)
  {
    HttpClient httpClient = new HttpClient();
    PostMethod post = new PostMethod(url);
    int state = 0;
    String result = "";
    try
    {
      NameValuePair[] pairs = new NameValuePair[params.size()];
      int i = 0;
      for (String key : params.keySet()) {
        pairs[i] = new NameValuePair(key, (String)params.get(key));
        i++;
      }
      post.setRequestBody(pairs);
      httpClient.setConnectionTimeout(5000);
      httpClient.getHttpConnectionManager().getParams().setSoTimeout(5000);
      state = httpClient.executeMethod(post);
      if (state == 200) {
        result = post.getResponseBodyAsString();
      }
      post.releaseConnection();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (post != null)
        post.releaseConnection();
    }
    return result;
  }

  public String doPost(String url, Map<String, String> params, Integer timeout)
  {
    HttpClient httpClient = new HttpClient();
    PostMethod post = new PostMethod(url);
    int state = 0;
    String result = "";
    try
    {
      NameValuePair[] pairs = new NameValuePair[params.size()];
      int i = 0;
      for (String key : params.keySet()) {
        pairs[i] = new NameValuePair(key, (String)params.get(key));
        i++;
      }
      post.setRequestBody(pairs);
      httpClient.setConnectionTimeout(timeout.intValue());
      httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout.intValue());
      state = httpClient.executeMethod(post);
      if (state == 200) {
        result = post.getResponseBodyAsString();
      }
      post.releaseConnection();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (post != null)
        post.releaseConnection();
    }
    return result;
  }

  public String doPostJson(String url, Map<String, Object> params) {
    HttpClient httpClient = new HttpClient();
    PostMethod post = new PostMethod(url);
    int state = 0;
    String result = "";
    try {
      String content = StringUtils.createJson(params);
      System.out.println(content);
      RequestEntity requestEntity = new StringRequestEntity(content, null, "utf-8");
      post.setRequestEntity(requestEntity);
      post.setRequestHeader("Accept", "application/json");
      post.setRequestHeader("Content-type", "application/json");

      httpClient.setConnectionTimeout(20000);
      state = httpClient.executeMethod(post);
      if (state == 200) {
        result = post.getResponseBodyAsString();
      }
      post.releaseConnection();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (post != null)
        post.releaseConnection();
    }
    return result;
  }
  public static void main(String[] args) {
    String res = "1";
    if ((res != null) && (res.length() > 0))
      System.out.println("sss");
  }
}