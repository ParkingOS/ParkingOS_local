package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginInfo implements Serializable {
	
	private String token;
	private String name;
	private String info;
	private String role;
	private String logontime;
	private String state;
	
	public LoginInfo() {
		super();
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogontime() {
		return logontime;
	}
	public void setLogontime(String logontime) {
		this.logontime = logontime;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "LoginInfo [token=" + token + ", name=" + name + ", info="
				+ info + ", role=" + role + ", logontime=" + logontime
				+ ", state=" + state + "]";
	}
}

