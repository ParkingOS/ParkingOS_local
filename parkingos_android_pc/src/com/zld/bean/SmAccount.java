package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SmAccount implements Serializable {

	private String account;//”√ªß√˚
	private String username;//’À∫≈
	private String password;//√‹¬Î
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "SmAccount [account=" + account + ", username=" + username
				+ ", password=" + password + "]";
	}
	public SmAccount(String account, String username, String password) {
		super();
		this.account = account;
		this.username = username;
		this.password = password;
	}
	public SmAccount() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
