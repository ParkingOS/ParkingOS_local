package com.zld.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zld.service.DataBaseService;

//
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Set;
//
//import net.sf.json.JSONObject;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//
public class pgutil {
	@Autowired
	private DataBaseService daService;
//	public static void main(String[] args) throws Exception {
//		Set<String> set = ImportExcelUtil.generateUserSql(new File("C:\\Users\\drh\\Desktop\\免费停车信息_20150519\\bj.xls"), "bj.xls", 0);
//		for (String s : set) {
//			String a = s.substring(0, 10);
//			String b = s.substring(10, s.length());
//			System.out.println(a+","+b);
//			getAddress(b,a,s);
//		}
////		GetAddress.getAddress("22.68392","114.13107");
//	}
//	public static String getAddress(String latitude,String longitude,String s) throws IOException{
//		StringBuffer sb = new StringBuffer("http://api.map.baidu.com/geocoder/v2/?ak=InjEBUoTWHZWkCKNHSUOZFP8&output=json&pois=1&location=");
//		sb.append(latitude).append(",").append(longitude);//拼接请求参数
//		HttpClient client = new DefaultHttpClient();  
//	    HttpGet get = new HttpGet(sb.toString());   
//	    HttpResponse res=null;
//	    String result = "";
//	    String address = "";
//	    try {  	          
//	        res = client.execute(get);  //请求
//	        if(res.getStatusLine().getStatusCode()==200){//200请求成功
//	        	result=EntityUtils.toString(res.getEntity()).replace("\\", "");
//	        	JSONObject rs = JSONObject.fromObject(result); //转为json对象
//	        	String status = rs.get("status")+"";
//	        	if("0".equals(status)){//判断请求状态   0：成功    状态码说明：http://developer.baidu.com/map/index.php?title=webapi/guide/webservice-geocoding
//	        		address = (String) JSONObject.fromObject(rs.get("result")).get("formatted_address");//取出地址
////		    		System.out.println(address);
//	        	}else{
//	        		System.out.println("结果失败！");
//		        	return null;
//	        	}
//	    		
//	        }else{
//	        	System.out.println("请求失败！");
//	        	return null;
//	        }
//	        
//	    } catch (Exception e) {  
//	        throw new RuntimeException(e);  
//	    }  
//	    
//	    File datafile = new File("D:\\datafile.txt");
//	    boolean flag = false;
//	    if(datafile.exists() && datafile.isFile()){
////	     System.out.println("文件已经存在");
//	    	flag = true;
//	    }else{
//	     try{
//	      //创建文件
//	      datafile.createNewFile();
//	     }catch(IOException e){
//	      System.out.println("创建文件失败,错误信息："+e.getMessage());
//	     }
//	    }
//	    CopyOfGetAddress.appendFile("D:\\datafile.txt",s+"|"+address+System.getProperty("line.separator"));	
//	   
//		return address;
//		
//	}
	public static void appendFile(String fileName, String content) {   
        FileWriter writer = null;  
        try {     
            // 打开一个写文件器，true表示以追加形式写文件     
            writer = new FileWriter(fileName, true);     
            writer.write(content);       
        } catch (IOException e) {     
            e.printStackTrace();     
        } finally {     
            try {     
                if(writer != null){  
                    writer.close();     
                }  
            } catch (IOException e) {     
                e.printStackTrace();     
            }     
        }   
    }    
	public  void export(){
		List list = daService.getAll("select * from com_nfc_tb where id <?", new Object[]{55});
		for (int i = 0; i < list.size(); i++) {
//			  id bigint NOT NULL,
//			  nfc_uuid character varying(35),
//			  comid bigint,
//			  create_time bigint,
//			  state bigint, -- 0正常，1禁用
//			  use_times integer,
//			  uin bigint DEFAULT (-1), -- -- 车主帐号
//			  uid bigint DEFAULT (-1), -- 收费员编号
//			  update_time bigint DEFAULT 0, -- 更新时间,绑定用户时间
//			  nid bigint DEFAULT 0, -- 扫描NFC的二维码号
//			  qrcode character varying, -- 二维码
			Object row1 = ((Map)list.get(i)).get("id");
			Object row2 = ((Map)list.get(i)).get("nfc_uuid");
			Object row3 = ((Map)list.get(i)).get("comid");
			Object row4 = ((Map)list.get(i)).get("create_time");
			Object row5 = ((Map)list.get(i)).get("state");
			Object row6 = ((Map)list.get(i)).get("use_times");
			Object row7 = ((Map)list.get(i)).get("uin");
			Object row8 = ((Map)list.get(i)).get("uid");
			Object row9 = ((Map)list.get(i)).get("update_time");
			Object row10 = ((Map)list.get(i)).get("nid");
			Object row11 = ((Map)list.get(i)).get("qrcode");
			String sql = "insert into com_nfc_tb(id,nfc_uuid,comid,create_time,state,use_times,uin,uid,update_time,nid,qrcode) values (" +
					row1+","+row2+","+row3+","+row4+","+row5+","+row6+","+row7+","+row8+","+row9+","+row10+","+row11+","+")";
			pgutil.appendFile("D:\\datafile.txt",sql+System.getProperty("line.separator"));	
		}
	}
	public static void main(String[] args) {
//	        File file = new File("D:\\nfc.txt");
//	        BufferedReader reader = null;
//	        try {
//	            reader = new BufferedReader(new FileReader(file));
//	            String sql = null;
//	            while ((sql = reader.readLine()) != null) {
//	                // 显示行号
//	                System.out.println(sql);
//	            }
//	            reader.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        } finally {
//	            if (reader != null) {
//	                try {
//	                    reader.close();
//	                } catch (IOException e1) {
//	                }
//	            }
//	        }
//		order_tb();
		com_info_tb();
//		product_package_tb();
//		carower_product();
		user_info_tb();
//		car_info_tb();
//		com_pass_tb();
//		com_worksite_tb();
//		com_led_tb();
//		com_camera_tb();
//		price_tb();
	}
	public static void order_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\order_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
				String[] values = sql.substring(8, sql.length()).split(";");//.replace(";", ",").replace("\"", "'");
//            	String[] values = sql.substring(7, sql.length()).split(";");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}
				}
                System.out.println(value.substring(0,value.length()-1));
                sql = "insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,pre_state,in_passid,out_passid,type)"+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\order_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void com_info_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\com_info_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into com_info_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\com_info_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void product_package_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\product_package_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into product_package_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\product_package_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void carower_product(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\carower_product.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into carower_product "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\carower_product.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void user_info_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\user_info_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into user_info_tb(id,nickname,password,strid,sex,email,phone,mobile,address,resume,reg_time,logon_time,logoff_time,online_flag," +
                		"comid,auth_flag,balance,state,recom_code,md5pass,cid,department_id,media,isview,collector_pics,collector_auditor,imei,client_type,version" +
                		",wxp_openid,wx_name,wx_imgurl,shop_id,credit_limit,is_auth,reward_score,firstorderquota,rewardquota,recommendquota,ticketquota) "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\user_info_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void car_info_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\car_info_tb.txt");
        BufferedReader reader = null;
        try {
//            reader = new BufferedReader(new FileReader(file));
        	InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
         reader = new BufferedReader(isr);
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into car_info_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\car_info_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void com_pass_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\com_pass_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into com_pass_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\com_pass_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void com_worksite_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\com_worksite_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into com_worksite_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\com_worksite_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void com_led_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\com_led_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into com_led_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\com_led_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void com_camera_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\com_camera_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into com_camera_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\com_camera_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static void price_tb(){
//		insert into order_tb(create_time,comid,uin,state,auto_pay,pay_type,nfc_uuid,c_type,uid,car_number,imei,pid,car_type,in_passid,out_passid,pre_state,type) 
//		values(1441507311,14775,-1,0,0,0,'',3,324569,'冀A725Q3','',-1,0,-1,-1,0,0)
		File file = new File("C:\\Users\\drh\\Desktop\\石家庄\\price_tb.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String sql = null;
            int count = 0;
            while ((sql = reader.readLine()) != null) {
//                System.out.println(sql);
            	
                String[] values = sql.split(";");//.replace(";", ",").replace("\"", "'");
                String value = "";
                for (int i = 0; i < values.length; i++) {
					if(values[i]!=null&&values[i].length()>0){
						value += values[i]+",";
					}else{
						value += "null,";
					}
				}
                
                sql = "insert into price_tb "+
                	  "values (" + value.substring(0,value.length()-1).replace("\"", "'")+");";
                pgutil.appendFile("C:\\Users\\drh\\Desktop\\石家庄\\sql\\price_tb.txt",sql+System.getProperty("line.separator"));
            }
            reader.close();
//            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
}
