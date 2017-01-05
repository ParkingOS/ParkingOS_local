package com.zld.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
public class ExportExcelUtil {

static Logger logger = Logger.getLogger(ExportExcelUtil.class);
	
	public  String excelName="报表";
	public  String[] headBody = null;
	public  List<List<String>> bodyList = null;
	/**
	 * 
	 * @param excelName 文件名
	 * @param headBody 表头
	 * @param bodyList 内容
	 * @param isEncrypt 电话是否加密
	 */
	public ExportExcelUtil(String excelName,String[] headBody,List<List<String>> bodyList){
		this.excelName=excelName;
		this.headBody = headBody;
		this.bodyList = bodyList;
	}
	public void createExcelFile(OutputStream os) throws IOException {
	    try {
            //创建一个文件
			WritableWorkbook workbook = Workbook.createWorkbook(os);
	        //使用第一张工作表
	        WritableSheet sheet = workbook.createSheet(excelName, 0); 
	        //创建表头
	        for (int i=0;i<headBody.length;i++) {
	        	Label cell= new Label(i, 0, headBody[i]);
		        sheet.addCell(cell);
			}
	        //插入数据
	        if(bodyList != null) {     
	        	logger.info("开始创建文件");
	        	for(int i = 0,j=1; i < bodyList.size(); i++,j++) {
	        		//获取写入数据
	        		List<String > dateList=bodyList.get(i);	              				        	
	        		//写入数据
	        		for(int k=0 ;k<dateList.size();k++){
	        			String value = dateList.get(k);//处理导出客户电话后有特殊字符的问题
	        			value = (value==null||value.equals("null"))?"":value;
	        			Label label = new Label(k,j,value);
	 	    	        sheet.addCell(label);
	        		}
	        	}
	        }
	        logger.info("创建文件结束");
	        //关闭对象，释放资源
	        workbook.write();
	        workbook.close();
	        os.close();
		} catch (Exception e) {
			os.close();
			logger.info(e);
		}
	}
}
