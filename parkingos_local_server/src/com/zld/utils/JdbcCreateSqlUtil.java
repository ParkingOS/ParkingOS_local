package com.zld.utils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class JdbcCreateSqlUtil {

	private List<JdbcFieldUtils> fieldLists;
	private List<List<JdbcFieldUtils>> orFieldlists;
	private String [] fields;
	private Object[] values;
	private String join;
	private String tbaleAlia="";//数据库表名
	

	public String getJoin() {
		return join;
	}
	public void setJoin(String join) {
		this.join = join;
	}
	
	public JdbcCreateSqlUtil() {

	}
	public String getTbaleAlia() {
		return tbaleAlia;
	}
	public void setTbaleAlia(String tbaleAlia) {
		this.tbaleAlia = tbaleAlia;
	}
	/**
	 * 
	 * @param fieldLists 生成条件的信息
	 * @param orFieldlists 生成或条件的信息
	 * @param fields 新建或更新字段
	 * @param values 新建或更新字段对应的值
	 * @param argTypes 新建或更新字段对应的值对应的类型 3,double 4,int|long 12,string 93,date
	 */
	public JdbcCreateSqlUtil(List<JdbcFieldUtils> fieldLists,
			List<List<JdbcFieldUtils>> orFieldlists, String[] fields,
			Object[] values) {
		super();
		this.fieldLists = fieldLists;
		this.orFieldlists = orFieldlists;
		this.fields = fields;
		this.values = values;
	}



	/**
	 * 生成新建语句
	 */
	
	public SqlInfo createInsertSql(String table){
		if (fields == null || values == null
				|| (fields.length != values.length))
			return null;
		String insertFields = " (";
		String preValues=" values(";
		for (int i = 0; i <fields.length; i++) {
			Object field = fields[i];
			if (i == 0) {
				insertFields += field ;
				preValues +="?";
			} else {
				insertFields += "," + field ;
				preValues +=",?";
			}
		}
		values = filterValues(values);
		insertFields =insertFields+")"+preValues+")";
		SqlInfo upsqlInfo = new SqlInfo(insertFields, values);
		return upsqlInfo;
	}
	
	/**
	 * 过滤值为“”
	 * @param values
	 * @return
	 */
	private Object[] filterValues(Object[] values){
		Object[] vObjects = new Object[values.length];
		for(int i =0;i<values.length;i++){
			Object o = values[i];
			if(o instanceof String){
				if("".equals(o.toString()))
					vObjects[i]=null;
				else {
					vObjects[i]=o;
				}
			}else
				vObjects[i]=o;
			
		}
		return vObjects;
	}
	
	/**
	 * 生成更新语句
	 * @return
	 */
	public SqlInfo CreateUpdateSql() {
		if (fields == null || values == null
				|| (fields.length != values.length))
			return null;
		values = filterValues(values);
		String fieldSetSql = " set ";
		for (int i = 0; i <fields.length; i++) {
			Object field = fields[i];
			if (i == 0) {
				fieldSetSql += field + "=?";
			} else {
				fieldSetSql += "," + field + "=?";
			}
		}
		SqlInfo upsqlInfo = new SqlInfo(fieldSetSql, values);
		SqlInfo sqlInfo = preCreateSearchSql(" and  ", fieldLists);
		if(sqlInfo!=null)
			upsqlInfo = SqlInfo.joinSqlInfo(upsqlInfo, sqlInfo, 3);

		if (orFieldlists != null && !orFieldlists.isEmpty()) {
			SqlInfo orSqlInfo =null;
			for(List<JdbcFieldUtils> subList : orFieldlists){
				if(subList!=null&&!subList.isEmpty()){
					SqlInfo info=preCreateSearchSql(" or ", subList);
					if(orSqlInfo!=null)
						orSqlInfo = SqlInfo.joinSqlInfo(orSqlInfo, info, 2);
					else 
						orSqlInfo = info;
				}
			}
			// 合并sql
			if (orSqlInfo != null) {
				sqlInfo = SqlInfo.joinSqlInfo(upsqlInfo, orSqlInfo, 2);
			}
		}
		return upsqlInfo;
	}
	
	/**
	 * 生成更新语句
	 * @return
	 */
	public SqlInfo CreateUpdateSql(String table) {
		if (fields == null || values == null
				|| (fields.length != values.length))
			return null;
		values = filterValues(values);
		String fieldSetSql = " set ";
		for (int i = 0; i <fields.length; i++) {
			Object field = fields[i];
			if (i == 0) {
				fieldSetSql += field + "=?";
			} else {
				fieldSetSql += "," + field + "=?";
			}
		}
		SqlInfo upsqlInfo = new SqlInfo(fieldSetSql, values);
		SqlInfo sqlInfo = preCreateSearchSql(" and  ", fieldLists);
		if(sqlInfo!=null)
			upsqlInfo = SqlInfo.joinSqlInfo(upsqlInfo, sqlInfo, 3);

		if (orFieldlists != null && !orFieldlists.isEmpty()) {
			SqlInfo orSqlInfo =null;
			for(List<JdbcFieldUtils> subList : orFieldlists){
				if(subList!=null&&!subList.isEmpty()){
					SqlInfo info=preCreateSearchSql(" or ", subList);
					if(orSqlInfo!=null)
						orSqlInfo = SqlInfo.joinSqlInfo(orSqlInfo, info, 2);
					else 
						orSqlInfo = info;
				}
			}
			// 合并sql
			if (orSqlInfo != null) {
				sqlInfo = SqlInfo.joinSqlInfo(upsqlInfo, orSqlInfo, 2);
			}
		}
		return upsqlInfo;
	}
	
	/**
	 * 生成查询语句
	 * @return
	 */
	public SqlInfo CreateSearchSql() {
		if (fieldLists == null)
			return null;
		SqlInfo sqlInfo = preCreateSearchSql(" and  ", fieldLists);
		if (orFieldlists != null && !orFieldlists.isEmpty()) {
			SqlInfo orSqlInfo =null;
			for(List<JdbcFieldUtils> subList : orFieldlists){
				if(subList!=null&&!subList.isEmpty()){
					SqlInfo info=preCreateSearchSql(" or ", subList);
					if(orSqlInfo!=null)
						orSqlInfo = SqlInfo.joinSqlInfo(orSqlInfo, info, 2);
					else {
						orSqlInfo = info;
					}
				}
			}
			// 合并sql
			if (orSqlInfo != null) {
				sqlInfo = SqlInfo.joinSqlInfo(sqlInfo, orSqlInfo, 2);
			}
		}
		return sqlInfo;
	}

	private SqlInfo preCreateSearchSql(String join, List<JdbcFieldUtils> lists) {
		if(lists==null||lists.size()==0)
			return null;
		List<String> subSql = new ArrayList<String>();
		List<Object> valueList = new ArrayList<Object>();
		List<Integer> argTypes = new ArrayList<Integer>();
		for (JdbcFieldUtils jfu : lists) {
			subSql.add(createByType(jfu));
			switch (jfu.getFieldType()) {
				case JdbcTypeOperate.TYPE_INT:
					if (jfu.getOperate() == JdbcTypeOperate.OPER_BT) {
						valueList.add((Long) jfu.getStart());
						valueList.add((Long) jfu.getEnd());
						argTypes.add(JdbcTypeOperate.TYPE_INT);
						argTypes.add(JdbcTypeOperate.TYPE_INT);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_IN
							|| jfu.getOperate() == JdbcTypeOperate.OPER_NIN) {
						for (Object object : jfu.getValueList()) {
							valueList.add(object);
							argTypes.add(JdbcTypeOperate.TYPE_INT);
						}
					} else if (jfu.getOperate() != JdbcTypeOperate.OPER_NU
							&& jfu.getOperate() != JdbcTypeOperate.OPER_NN) {
						valueList.add(jfu.getValue());
						argTypes.add(JdbcTypeOperate.TYPE_INT);
					}
					break;
				case JdbcTypeOperate.TYPE_DOUBLE:
					if (jfu.getOperate() == JdbcTypeOperate.OPER_BT) {
						valueList.add((Double) jfu.getStart());
						valueList.add((Double) jfu.getEnd());
						argTypes.add(JdbcTypeOperate.TYPE_DOUBLE);
						argTypes.add(JdbcTypeOperate.TYPE_DOUBLE);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_IN
							|| jfu.getOperate() == JdbcTypeOperate.OPER_NIN) {
						for (Object object : jfu.getValueList()) {
							valueList.add((Double) object);
							argTypes.add(JdbcTypeOperate.TYPE_DOUBLE);
						}
					} else if (jfu.getOperate() != JdbcTypeOperate.OPER_NU
							&& jfu.getOperate() != JdbcTypeOperate.OPER_NN) {
						valueList.add(jfu.getValue());
						argTypes.add(JdbcTypeOperate.TYPE_DOUBLE);
					}
					break;
				case JdbcTypeOperate.TYPE_STRING:
					if (jfu.getOperate() == JdbcTypeOperate.OPER_BT) {
						valueList.add((String) jfu.getStart());
						valueList.add((String) jfu.getEnd());
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_IN
							|| jfu.getOperate() == JdbcTypeOperate.OPER_NIN) {
						for (Object object : jfu.getValueList()) {
							valueList.add(object);
							argTypes.add(JdbcTypeOperate.TYPE_STRING);
						}
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_LK) {
						valueList.add("%" + jfu.getValue() + "%");
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_LLK) {
						valueList.add("%" + jfu.getValue());
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_RLK) {
						valueList.add(jfu.getValue() + "%");
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_NLK) {
						valueList.add("%" + jfu.getValue() + "%");
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
					} else if (jfu.getOperate() != JdbcTypeOperate.OPER_NU
							&& jfu.getOperate() != JdbcTypeOperate.OPER_NN) {
						valueList.add(jfu.getValue());
						argTypes.add(JdbcTypeOperate.TYPE_STRING);
					}
					break;
				case JdbcTypeOperate.TYPE_DATE:
					if (jfu.getOperate() == JdbcTypeOperate.OPER_BT) {
						valueList.add((Date) jfu.getStart());
						valueList.add((Date) jfu.getEnd());
						argTypes.add(JdbcTypeOperate.TYPE_DATE);
						argTypes.add(JdbcTypeOperate.TYPE_DATE);
					} else if (jfu.getOperate() == JdbcTypeOperate.OPER_IN
							|| jfu.getOperate() == JdbcTypeOperate.OPER_NIN) {
						for (Object object : jfu.getValueList()) {
							valueList.add((Date) object);
							argTypes.add(JdbcTypeOperate.TYPE_DATE);
						}
					} else if (jfu.getOperate() != JdbcTypeOperate.OPER_NU
							&& jfu.getOperate() != JdbcTypeOperate.OPER_NN) {
						valueList.add(jfu.getValue());
						argTypes.add(JdbcTypeOperate.TYPE_DATE);
					}
					break;
				default:
					break;
			}
		}
		return createSqlByList(subSql, valueList, argTypes, join);
	}

	private String createByType(JdbcFieldUtils jfu) {
		String subSql = "";
		if(!tbaleAlia.equals("") && !tbaleAlia.contains(".")){
			tbaleAlia += ".";
		}
		switch (jfu.getOperate()) {
		case JdbcTypeOperate.OPER_EQ:
			subSql = tbaleAlia+jfu.getFieldName() + " = ? ";
			break;
		case JdbcTypeOperate.OPER_NE:
			subSql = tbaleAlia+jfu.getFieldName() + " <> ? ";
			break;
		case JdbcTypeOperate.OPER_LT:
			subSql = tbaleAlia+jfu.getFieldName() + " < ? ";
			break;
		case JdbcTypeOperate.OPER_GT:
			subSql = tbaleAlia+jfu.getFieldName() + " > ? ";
			break;
		case JdbcTypeOperate.OPER_LE:
			subSql = tbaleAlia+jfu.getFieldName() + " <= ? ";
			break;
		case JdbcTypeOperate.OPER_GE:
			subSql = tbaleAlia+jfu.getFieldName() + " >= ? ";
			break;
		case JdbcTypeOperate.OPER_BT:
			subSql = tbaleAlia+jfu.getFieldName() + " between ? and ? ";
			break;
		case JdbcTypeOperate.OPER_NU:
			subSql = tbaleAlia+jfu.getFieldName() + "  is null ";
			break;
		case JdbcTypeOperate.OPER_NN:
			subSql = tbaleAlia+jfu.getFieldName() + " is not null ";
			break;
		case JdbcTypeOperate.OPER_LK:
		case JdbcTypeOperate.OPER_RLK:
		case JdbcTypeOperate.OPER_LLK:
			subSql = tbaleAlia+jfu.getFieldName() + " like ? ";
			break;
		case JdbcTypeOperate.OPER_NLK:
			subSql = tbaleAlia+jfu.getFieldName() + " not like ? ";
			break;
		case JdbcTypeOperate.OPER_IN:
			int size = jfu.getValueList().size();
			String vs = "";
			for (int i = 0; i < size; i++) {
				if (i == 0)
					vs += "?";
				else {
					vs += ",?";
				}
			}
			subSql = tbaleAlia+jfu.getFieldName() + " in(" + vs + ") ";
			break;
		case JdbcTypeOperate.OPER_NIN:
			int s = jfu.getValueList().size();
			String v = "";
			for (int i = 0; i < s; i++) {
				if (i == 0)
					v += "?";
				else {
					v += ",?";
				}
			}
			subSql = tbaleAlia+jfu.getFieldName() + " not in(" + v + ") ";
			break;
		default:
			break;
		}
		return subSql;
	}
	/**
	 * 
	 * @param subSql
	 * @param valueList
	 * @param argTypes
	 * @param join 连接符，and|or
	 * @return
	 */
	private SqlInfo createSqlByList(List<String> subSql,
			List<Object> valueList, List<Integer> argTypes, String join) {
		StringBuffer sbBuffer = new StringBuffer();
		//连成sql
		String subJoin =this.join;
		if(this.join==null)
			subJoin = join;
		for (int i = 0; i < subSql.size(); i++) {
			String sub = subSql.get(i);
			if (sub != null && !"".equals(sub)) {
				if (i == 0)
					sbBuffer.append(sub);
				else {
					sbBuffer.append(subJoin + sub);
				}
			}
		}
		//值集合转为数组
		Object[] values = new Object[valueList.size()];
		for (int j = 0; j < valueList.size(); j++) {
			values[j] = valueList.get(j);
		}
		String sql = sbBuffer.toString();
		if(join.indexOf("or")!=-1){
			sql ="("+sql+")";
		}
		return new SqlInfo(sql, values);
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}


	public List<JdbcFieldUtils> getFieldLists() {
		return fieldLists;
	}

	public void setFieldLists(List<JdbcFieldUtils> fieldLists) {
		this.fieldLists = fieldLists;
	}


	public List<List<JdbcFieldUtils>> getOrFieldlists() {
		return orFieldlists;
	}
	public void setOrFieldlists(List<List<JdbcFieldUtils>> orFieldlists) {
		this.orFieldlists = orFieldlists;
	}
}
