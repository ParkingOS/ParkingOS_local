package com.zld.dao;

import java.util.List;
import java.util.Map;

public interface DataBaseDao {
	
		
		/**
		 * 增、删、改
		 * @param sql
		 * @param values
		 * @return
		 */
		//更新 --增、删、改
		public int update(String sql,Object[] values );
		//批量添加 --批量添加
		public int bathInsert(String sql,List<Object[]> lists,int[] argTypes);
		
		/**
		 * 查询方法
		 * @param sql
		 * @param values
		 * @return
		 */
		//取数量 
		public Long getLong(String sql,Object[] values );
		//所有记录
		public List getAll(String sql,Object[] values );
		//查某一字段
		public Object getObject(String sql,Object[] values,Class type);
		//取一条记录
		public Map getPojo(String sql,Object[] values);
}
