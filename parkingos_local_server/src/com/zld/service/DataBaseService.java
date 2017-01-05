package com.zld.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zld.dao.DataBaseDao;
import com.zld.utils.StringUtils;


@Service
public class DataBaseService {
	private Logger logger = Logger.getLogger(DataBaseService.class);

	@Autowired
	private DataBaseDao databasedao;
	

	public List getAll(String sql,Object[] values ){
		return databasedao.getAll(sql, values);
	}
	
	public List getAllMap(String sql,List<Object> values ){
		Object[] objects= null;
		if(values!=null&&values.size()>0){
			objects=new Object[values.size()];
			for(int i=0;i<objects.length;i++){
				objects[i]=values.get(i);
			}
		}
		return databasedao.getAll(sql, objects);
	}
	
	public List getAll(String sql,List<Object> values,int pageNum,int pageSize ){
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
		return databasedao.getAll(sql, objects);
	}
	
	public Map getPojo(String sql,Object[] values){
		Map object = null;
		try {
			object = databasedao.getPojo(sql+" order by id desc limit 1 ", values);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.info(e.getMessage());
			logger.info("查询错误，没有记录：sql:"+sql+":params:"+StringUtils.objArry2String(values));
		}
		return object;
	}
	
	public Map getMap(String sql,Object[] values){
		Map object = null;
		try {
			object = databasedao.getPojo(sql, values);
		} catch (Exception e) {
			logger.info("查询错误，没有记录：sql:"+sql+":params:"+StringUtils.objArry2String(values));
			e.printStackTrace();
		}
		return object;
	}
	public Map getMap(String sql,List values){
		Object[] valObjects =null;
		if(values!=null){
			valObjects= new Object[values.size()];
			for(int i=0;i<values.size();i++){
				valObjects[i]=values.get(i);
			}
		}
		return getMap(sql, valObjects);
	}
	public int update(String sql,Object[] values){
		return databasedao.update(sql, values);
	}
	
	/**
	 * 插入数据
	 * @param sql
	 * @param values
	 * @return
	 */
	public int updateInsert(String sql,Object[] values){
		if(values!=null&&values.length>0){
			String preSql = "";
			for(int i=0;i<values.length;i++){
				if(i!=0)
					preSql+=",";
				preSql +="?";
			}
			sql +=" values ("+preSql+")";
			return update(sql, values);
		}else {
			return -1;
		}
	}
	
	public int update(String sql,List values){
		Object [] _values =null;
		if(values!=null&&values.size()>0){
			_values=new Object[values.size()];
			for(int i=0;i<values.size();i++){
				_values[i]=values.get(i);
			}
		}
		System.err.println(sql+",params:"+StringUtils.objArry2String(_values));
		//return 0;
		return databasedao.update(sql, _values);
	}
	
	public Long getkey(String seqname){
		Long id = databasedao.getLong("SELECT nextval('"+seqname+"'::REGCLASS) AS newid", null);
		return id;
	}
	
	public boolean bathUpdate(List params){
		boolean r = true;
		for(int i=0;i<params.size();i++){
			Map map = (Map)params.get(i);
			String sql = map.get("sql")+"";
			Object[] values = (Object[])map.get("values");
			int result = databasedao.update(sql, values);
			if(result!=1){
				r = false;
				break;
			}
		}
		return r;
	}
	
	public boolean bathUpdate2(List params){
		boolean r = true;
		for(int i=0;i<params.size();i++){
			Map map = (Map)params.get(i);
			String sql = map.get("sql")+"";
			Object[] values = (Object[])map.get("values");
			int result = databasedao.update(sql, values);
			if(result < 0){
				r = false;
				break;
			}
		}
		return r;
	}
	
	public int bathInsert(String sql,List<Object[]> lists,int[] argTypes){
		return databasedao.bathInsert(sql, lists, argTypes);
	}
	
	public Long getLong(String sql,Object[] values){
		return databasedao.getLong(sql, values);
	}
	
	public Object getObject(String sql ,Object[] values ,Class type){
		Object object = null;
		if(sql.indexOf("order by")==-1){
			sql +=" order by id desc limit 1";
		}
		try {
			object = databasedao.getObject(sql, values, type);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("查询错误，没有记录：sql:"+sql+":params:"+StringUtils.objArry2String(values));
		}
		return object;
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
}
