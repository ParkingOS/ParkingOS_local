package com.zld.utils;

import java.util.List;

public class JdbcFieldUtils {


	private String fieldName;
	private int operate;
	private Object start;
	private Object end;
	private int fieldType;
	private Object value;
	private List<Object> valueList;
	/**
	 * 
	 * @param fieldType 字段类型
	 * @param fieldName 字段名
	 * @param operate 操作
	 * @param LongValue 值
	 * @param start 开始值
	 * @param end 结束值
	 */
	public JdbcFieldUtils(int fieldType,String fieldName,int operate,Object value,Object start,Object end){
		this.fieldName = fieldName;
		this.operate = operate;
		this.start = start;
		this.end = end;
		this.value = value;
		this.fieldType = fieldType;
	}
	
	public JdbcFieldUtils(int fieldType,String fieldName,int operate,Object value,Object start,Object end, List valueList) {
		this.fieldName = fieldName;
		this.operate = operate;
		this.start = start;
		this.end = end;
		this.fieldType = fieldType;
		this.value = value;
		this.valueList = valueList;
	}

	public List<Object> getValueList() {
		return valueList;
	}

	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getOperate() {
		return operate;
	}

	public void setOperate(int operate) {
		this.operate = operate;
	}


	public Object getStart() {
		return start;
	}

	public void setStart(Object start) {
		this.start = start;
	}

	public Object getEnd() {
		return end;
	}

	public void setEnd(Object end) {
		this.end = end;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

	
}
