package com.zld.lib.http;

//import com.androidquery.callback.AjaxStatus;
import com.zld.bean.LoginInfo;

/**
 * 描述：通信回调类
 * @author  lulogfei
 *
 */
public interface HttpCallBack {
	/**
	 * 描述：通信成功的回调
	 * @param object 回调数据
	 * @param url 请求标示
	 * @return
	 */
	public boolean doSucess(String url, String object);
	
	/**
	 * 描述：通信成功的回调
	 * @param object 回调数据
	 * @param url 请求标示
	 * @param worksiteId 通道id/carnumber
	 * @return
	 */
	public boolean doSucess(String url, String object, String str);
	
	/**
	 * 描述：通信成功的回调
	 * @param object 回调数据
	 * @param url 请求标示
	 * @param worksiteId 通道id/carnumber
	 * @return
	 */
	public boolean doSucess(String url, String object, String str1, String str2);
	
	/**
	 * 描述：通信成功的回调
	 * @param object 回调数据
	 * @param url 请求标示
	 * @param buffer 登录信息
	 * @return
	 */
	public boolean doSucess(String url, String object, byte[] buffer);
	
	/**
	 * 描述：通信成功的回调
	 * @param object 回调数据
	 * @param url 请求标示
	 * @param buffer 登录信息
	 * @param username 账号
	 * @param password 密码
	 * @return
	 */
	public boolean doSucess(String url, String object, byte[] buffer,String username,String password);

	/**
	 * 描述：通信成功的回调
	 * @param object 回调数据
	 * @param url 请求标示
	 * @param username 账号
	 * @param password 密码
	 * @param info 登录的信息
	 * @return
	 */
	public boolean doSucess(String url,String object,String username,String password,LoginInfo info);
	
	
	/**
	 * 带通道id的通信回调
	 * @param url
	 * @param isSingle 
	 * @param passid
	 * @param object
	 * @param object2 
	 * @param i 
	 * @return
	 */
	public boolean doSucess(String url,boolean isSingle, String passid, String object, int i, String object2);
	
	/**
	 * 描述：通信异常的回调
	 */
//	public boolean doFailure(String url, AjaxStatus status);
	public boolean doFailure(String url, String status);

	public boolean doSucess(String requestUrl, byte[] buffer);

	public boolean doSucess(String requestUrl, String username2, String password2,
			LoginInfo info2);
	
	public boolean doSucess(String requestUrl,byte[] buffer, String username2, String password2);

	public void timeout(String url);

	public void timeout(String url, String str);
	
	public void timeout(String url, String str,String str2);
}