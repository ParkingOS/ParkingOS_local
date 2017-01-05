package com.mserver.dao;

import java.util.List;

public interface PgOnlyReadDao {

	
	public List getAll(String sql,Object[] values );
	
	public int updateUser(String sql,Object[] values );
	
	public Long getLong(String sql,Object[] values );
	
	public String getField(String sql,Object[] values );
	
	public List getPage(String sql,Object[] values, int pageNum,int pageSize);
	
	//²éÄ³Ò»×Ö¶Î
	public Object getObject(String sql,Object[] values,Class type);
	
}
