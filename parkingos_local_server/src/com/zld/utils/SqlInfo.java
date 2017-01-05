package com.zld.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * sql信息类 
 * @author Administrator
 *
 */
public class SqlInfo {

	/**
	 * 
	 * @param sql  preSql语句 
	 * @param values  参数值
	 * @param argTypes 参数类型
	 */
	public SqlInfo(String sql, Object[] values) {
		this.sql = sql;
		this.values = values;
	}
	private String sql;
	private Object[] values;
	private String orderBy;
	private List<Object> params;
	public List<Object> getParams() {
		if(values!=null&&values.length>0){
			params = new ArrayList<Object>();
			for(Object object : values){
				params.add(object);
			}
		}
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getValues() {
		return values;
	}
	public void setValues(Object[] values) {
		this.values = values;
	}
	
	/**
	 * 合并两个语句信息
	 * @param sqlInfo1 
	 * @param sqlInfo2
	 * @param join 连接符，1:包含  2:and 3:where 
	 * @return
	 */
	public static SqlInfo joinSqlInfo(SqlInfo sqlInfo1,SqlInfo sqlInfo2,int join){
		if(sqlInfo1==null||sqlInfo2==null||join==0)
			return null;
		String sql1 = sqlInfo1.getSql();
		Object[] v1 = sqlInfo1.getValues();
		String sql2 = sqlInfo2.getSql();
		Object[] v2 = sqlInfo2.getValues();
		String usql ="";
		if(join==3)
		    usql = sql1 + " where " + sql2;
		else if(join==1){
			usql = sql1 + " and ( " + sql2+") ";
		}else if(join==2){
			usql = sql1 + " and " + sql2+" ";
		}
		int length1 = v1.length + v2.length;
		Object[] nv = new Object[length1];
		int[] na = new int[length1];
		for (int i = 0; i < v1.length; i++) {
			nv[i] = v1[i];
		}
		for (int j = v1.length; j < length1; j++) {
			nv[j] = v2[j - v1.length];
		}
		return new SqlInfo(usql, nv);
	}
	
	@Override
	public String toString() {
		return "SqlInfo [sql=" + sql + ", values=" + Arrays.toString(values)+"]";
	}
}
