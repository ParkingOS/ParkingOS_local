package com.zld.local.bean;

public class Relation {

	private String local;

	private String line;

	public void setLocal(String local) {
		this.local = local;
	}

	public String getLocal() {
		return this.local;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getLine() {
		return this.line;
	}

	@Override
	public String toString() {
		return "Relation [local=" + local + ", line=" + line + "]";
	}
	
	
}
