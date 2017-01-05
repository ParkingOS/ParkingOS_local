package com.mserver.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mserver.dao.PgDao;

@Repository
public class PgImpl extends JdbcTemplate implements PgDao {
	
	@Autowired
	@Override
	public void setDataSource(@Qualifier("dataSource")DataSource dataSource) {
		// TODO Auto-generated method stub
		super.setDataSource(dataSource);
	}
	public List getAll(String sql,Object[] values ) {
		List list = queryForList(sql,values);
		return list;
	}
	public int updateUser(String sql,Object[] values) {
		int r = super.update(sql, values);
		if(sql.trim().substring(0, 6).toUpperCase().equals("INSERT") 
				&& r == 0){//分区表插入数据时，因为插入了字表，父表受影响行数为0
			r = 1;
		}
		return r;
	}
	
	public Long getLong(String sql,Object[] values ){
		return queryForLong(sql,values);
		
	}
	public String getField(String sql, Object[] values) {
		queryForObject(sql, values, String.class);
		return null;
	}
	public List getPage(String sql, Object[] values, int pageNum, int pageSize) {
		int end = pageSize;
		int start = (pageNum - 1) * pageSize;
		sql =sql +" limit ? offset ?";
		int length =2;
		
		Object [] _params=null;
		if(values!=null){
			length= values.length+2;
			_params=new Object[length];
			for(int i=0;i<values.length;i++){
				_params[i]=values[i];
			}
			_params[length-2]=end;
			_params[length-1]=start;
		}else {
			_params=new Object[2];
			_params[0]=end;
			_params[1]=start;
		}
		return getAll(sql, _params);
	}
	public Object getObject(String sql, Object[] values, Class type) {
		return queryForObject(sql, values,type);
	}
	
}
