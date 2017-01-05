package com.mserver.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mserver.dao.PgDao;

@Service
public class PgService {

	@Autowired
	private PgDao userDao;
	

	public List getAll(String sql,Object[] values ){
		return userDao.getAll(sql, values);
	}
	
	public String find(String sql,Object[] values){
		List list = userDao.getAll(sql, values);
		if(list!=null&&list.size()==1)
			return "1";
		return "0";
	}
	
	public Map getMap(String sql,Object[] values){
		List list = userDao.getAll(sql, values);
		Object banlance = null;
		if(list!=null&&list.size()>0){
			return (Map)list.get(0);
		}
		return null;	
	}
	
	public int update(String sql,Object[] values){
		return userDao.updateUser(sql, values);
	}
	
	public boolean bathUpdate(List params){
		boolean r = true;
		for(int i=0;i<params.size();i++){
			Map map = (Map)params.get(i);
			String sql = map.get("sql")+"";
			Object[] values = (Object[])map.get("values");
			int result = userDao.updateUser(sql, values);
			if(result!=1){
				r = false;
				break;
			}
		}
		return r;
	}
	
	public Long getLong(String sql,Object[] values){
		return userDao.getLong(sql, values);
	}
	
	public List getPage(String sql,Object[] values, int pageNum,int pageSize){
		return userDao.getPage(sql, values, pageNum, pageSize);
	}
	
	public Object getObject(String sql, Object[] values, Class type) {
		return userDao.getObject(sql, values,type);
	}
	
}
