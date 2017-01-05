package com.zld.utils;

import java.util.List;

public class TableFields {
	
	private String fieldcnname ;
	private String fieldname ;
	private String fieldvalue ;
	private String inputtype ;
	private Integer twidth ;
	private Integer height ;
	private boolean issort ;
	private boolean hide ;
	private boolean fhide ;
	private boolean shide ;
	private boolean edit ;
	private List<NoList> noList;
	
	public TableFields(String fieldcnname, String fieldname, String fieldvalue,
			String inputtype, Integer twidth, Integer height, boolean issort,
			boolean hide, boolean fhide, boolean shide, boolean edit,List<NoList> noList) {
		this.fieldcnname = fieldcnname;
		this.fieldname = fieldname;
		this.fieldvalue = fieldvalue;
		this.inputtype = inputtype;
		this.twidth = twidth;
		this.height = height;
		this.issort = issort;
		this.hide = hide;
		this.fhide = fhide;
		this.shide = shide;
		this.edit = edit;
		this.noList = noList;
	}

	public String getFieldcnname() {
		return fieldcnname;
	}

	public void setFieldcnname(String fieldcnname) {
		this.fieldcnname = fieldcnname;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getFieldvalue() {
		return fieldvalue;
	}

	public void setFieldvalue(String fieldvalue) {
		this.fieldvalue = fieldvalue;
	}

	public String getInputtype() {
		return inputtype;
	}

	public void setInputtype(String inputtype) {
		this.inputtype = inputtype;
	}

	public Integer getTwidth() {
		return twidth;
	}

	public void setTwidth(Integer twidth) {
		this.twidth = twidth;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public boolean isIssort() {
		return issort;
	}

	public void setIssort(boolean issort) {
		this.issort = issort;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean isFhide() {
		return fhide;
	}

	public void setFhide(boolean fhide) {
		this.fhide = fhide;
	}

	public boolean isShide() {
		return shide;
	}

	public void setShide(boolean shide) {
		this.shide = shide;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public List<NoList> getNoList() {
		return noList;
	}

	public void setNoList(List<NoList> noList) {
		this.noList = noList;
	}
}
