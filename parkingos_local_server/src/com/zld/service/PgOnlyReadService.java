package com.zld.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zld.dao.PgOnlyReadDao;

@Service
public class PgOnlyReadService {

	@Autowired
	private PgOnlyReadDao userDao;
	

	public List<Map<String, Object>> getAll(String sql,Object[] values ){
		return userDao.getAll(sql, values);
	}
	
	public String find(String sql,Object[] values){
		List<Map<String, Object>> list = userDao.getAll(sql, values);
		if(list!=null&&list.size()==1)
			return "1";
		return "0";
	}
	
	public Map<String,Object> getMap(String sql,Object[] values){
		List<Map<String, Object>> list = userDao.getAll(sql, values);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;	
	}
	
	public Map<String,Object> getMap(String sql,List values){
		Object[] valObjects =null;
		if(values!=null){
			valObjects= new Object[values.size()];
			for(int i=0;i<values.size();i++){
				valObjects[i]=values.get(i);
			}
		}
		return getMap(sql, valObjects);	
	}
	
	
	public Long getLong(String sql,Object[] values){
		return userDao.getLong(sql, values);
	}
	
	public Long getLong(String sql,List values){
		Object[] valObjects =null;
		if(values!=null){
			valObjects= new Object[values.size()];
			for(int i=0;i<values.size();i++){
				valObjects[i]=values.get(i);
			}
		}
		return userDao.getLong(sql, valObjects);
	}
	
	public Long getCount(String sql,List<Object> values){
		Object[] valObjects =null;
		if(values!=null){
			valObjects= new Object[values.size()];
			for(int i=0;i<values.size();i++){
				valObjects[i]=values.get(i);
			}
		}
		return getLong(sql, valObjects);
	}
	
	public List<Map<String, Object>> getPage(String sql,Object[] values, int pageNum,int pageSize){
		return userDao.getPage(sql, values, pageNum, pageSize);
	}
	
	public Object getObject(String sql, Object[] values, Class type) {
		return userDao.getObject(sql, values,type);
	}
	public List<Map<String, Object>> getAllMap(String sql,List<Object> values ){
		Object[] objects= null;
		if(values!=null&&values.size()>0){
			objects=new Object[values.size()];
			for(int i=0;i<objects.length;i++){
				objects[i]=values.get(i);
			}
		}
		return userDao.getAll(sql, objects);
	}
	
	public List<Map<String, Object>> getAll(String sql,List<Object> values,int pageNum,int pageSize ){
		if(values==null)
			values= new ArrayList<Object>();
		if(pageSize>0){
			int end = pageSize;
			int start = (pageNum - 1) * pageSize;
			sql =sql +" limit ? offset ?";
			
			values.add(end);
			values.add(start);
		}
		Object[] objects= null;
		if(values.size()>0){
			objects=new Object[values.size()];
			for(int i=0;i<objects.length;i++){
				objects[i]=values.get(i);
			}
		}
		return userDao.getAll(sql, objects);
	}
	
	public Map<String, Object> getPojo(String sql,Object[] values){
		return getMap(sql, values);
	}
}
