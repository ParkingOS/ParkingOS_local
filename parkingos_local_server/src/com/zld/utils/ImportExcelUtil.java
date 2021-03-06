/**
 * @author drh
 * Excel数据导入数据库
 * @version 1.0
 */
package com.zld.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportExcelUtil {
	
	/**
	 * 操作Excel表格的功能类
	 */
    private static Workbook wb;
    private static Sheet sheet;
    private static Row row;
    
    /**
	 * 导入报表Excel数据，生成用户表的数据库导入语句
	 * @param File formFile：上传的文件
	 *        String formFileName：上传的文件名（获取后缀）判断是2007（.xlsx）还是2003（.xls）
	 *        int isTitle:是否有标题，有则是1，无则0
	 * @return ArrayList<Object[]>
	 * @throws Exception,Set<String> set
	 */
	public static ArrayList<Object[]> generateUserSql(File formFile,String formFileName,int isTitle,Set<String> set)
			throws Exception {
		FileInputStream in = null;
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		Map<String, String> localMap = readLocal();
		try {
			if (formFile == null) {
				throw new Exception("文件为空！");
			}
			
			in = new FileInputStream(formFile);//将文件读入到输入流中
			
			//从输入流中获取WorkBook对象，加载选中的excel文件
			String suffix = formFileName.substring(formFileName.lastIndexOf("."));  // 文件后辍.
			String area = "";
			if(formFileName.indexOf("bj")!=-1){
				area = "110000";
			}
			if(formFileName.indexOf("sz")!=-1){
				area = "440300";
			}
			if(formFileName.indexOf("gz")!=-1){
				area = "440100";
			}
			if(formFileName.indexOf("sh")!=-1){
				area = "310000";
			}
			//支持office2007
			if (".xlsx".equals(suffix.toLowerCase())) {
				wb = new XSSFWorkbook(in);
			}
			else{
				//支持office2003
//	        	wb = new HSSFWorkbook(in);
			}
			
			for (int i=0; i<wb.getNumberOfSheets(); i++) {//获取每个Sheet表
	             sheet = wb.getSheetAt(i);
	             if(sheet!=null){
	            	 int count = i+1;
	            	 System.err.println(">>>>>文件行数 ："+sheet.getPhysicalNumberOfRows());
	            	 for (int j=isTitle; j<sheet.getPhysicalNumberOfRows(); j++) {//获取每行，j=isTitle表示从第j行开始获取数据
//	            		 Object[] valStr = new String[row.getPhysicalNumberOfCells()];//用数组来存放每一行的数据，9表示每一行的数据不能超过9，可以<=9
	            		 ArrayList<Object> arrayList = new ArrayList<Object>();
		                 row = sheet.getRow(j);
		                 StringBuffer str = new StringBuffer();
		                 for (int k=0; k<row.getPhysicalNumberOfCells(); k++) {//获取每个单元格
		                     String content = getCellFormatValue(row.getCell(k)).trim();
		                     if(StringUtils.isNotNull(content)){
//		                    	 valStr[k] = content;//将excel获取到的值赋值给object类型的数组
		                    	 arrayList.add(content);
		                    	 if(k == 1||k==2){
		                    		 String pattern = "#0.000000";
		               			  	 DecimalFormat formatter = new DecimalFormat();
		               			  	 formatter.applyPattern(pattern);
		                    		 String resString = formatter.format(Double.parseDouble(content));
		                    		 str.append(resString);
		                    	 }
		                     }
		                 }
		                 Boolean flag = set.add(str.toString());
		                 if(!flag){
		                	 System.out.println(">>>>重复经纬度:"+str.toString());
		                 }
//		                 Boolean flag = set.add(str.toString());
//		                 str.delete(0, str.length()-1);
//		                 if(flag){
//		                	 continue;
//		                 }
		                 //调整字段顺序，避免出现org.postgresql.util.PSQLException: 未设定参数值 *的内容。
		                 if(arrayList.size()<4){
		                	 continue;
		                 }
		                 ArrayList<Object> arrayListRet = new ArrayList<Object>();
		                 arrayListRet.add(arrayList.get(0));
		                 arrayListRet.add(localMap.get(str.toString()));
		                 arrayListRet.add(arrayList.get(3));
		                 arrayListRet.add(TimeTools.getLongMilliSeconds());
		                 arrayListRet.add(TimeTools.getLongMilliSeconds());
		                 arrayListRet.add(Double.parseDouble(arrayList.get(1)+""));
		                 arrayListRet.add(Double.parseDouble(arrayList.get(2)+""));
		                 arrayListRet.add(1);
		                 arrayListRet.add(0);
		                 arrayListRet.add(Integer.parseInt(area));
		                 list.add(arrayListRet.toArray());
		             }
	             }
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

    /**
     * 根据HSSFCell类型设置数据
     * @param cell
     * @return
     */
    private static String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
            case HSSFCell.CELL_TYPE_NUMERIC:
            case HSSFCell.CELL_TYPE_FORMULA: {
                // 判断当前的cell是否为Date
//                if (HSSFDateUtil.isCellDateFormatted(cell)) {
//                    // 如果是Date类型则，转化为Data格式
//                    
//                    //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
//                    //cellvalue = cell.getDateCellValue().toLocaleString();
//                    
//                    //方法2：这样子的data格式是不带带时分秒的：2011-10-12
//                    Date date = cell.getDateCellValue();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                    cellvalue = sdf.format(date);
//                    
//                }
//                // 如果是纯数字
//                else {
//                    // 取得当前Cell的数值
//                    cellvalue = String.valueOf(cell.getNumericCellValue());
//                }
                break;
            }
            // 如果当前Cell的Type为STRIN
            case HSSFCell.CELL_TYPE_STRING:
                // 取得当前的Cell字符串
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            // 默认的Cell值
            default:
                cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }

    
    public  static List<Object[]> importExcelFile(Set<String> set){
    	String name = "d://sz.xls";
    	File file = new File(name);
    	name  = "sz.xls";
    	List<Object[]> resultList=new ArrayList<Object[]>();
    	List<Object[]>  list = null;
		try {
			list = generateUserSql(file, name, 0,set);
			if(list!=null)
				resultList.addAll(list);
//			name = "d://sz.xls";
//			file= new File(name);
//			name= "sz.xls";
//			list = generateUserSql(file, name, 0,set);
//			if(list!=null)
//				resultList.addAll(list);
//			name = "d://sh.xls";
//			file= new File(name);
//			name= "sh.xls";
//			list = generateUserSql(file, name, 0,set);
//			if(list!=null)
//				resultList.addAll(list);
//			name = "d://gz.xls";
//			file= new File(name);
//			name= "gz.xls";
//			list = generateUserSql(file, name, 0,set);
//			if(list!=null)
//				resultList.addAll(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return resultList;
	}
    
    private static Map<String, String> readLocal(){
    	BufferedReader br =null;
    	Map<String, String>  resultMap = new HashMap<String, String>();
    	String data ="";
    	try {
    		br =new BufferedReader(new FileReader("d:\\datafile.txt"));  
			data = br.readLine();
			while( data!=null){  
				
				resultMap.put(data.split("\\|")[0], data.split("\\|")[1]);
				data = br.readLine(); //接着读下一行  
			} 
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//一次读入一行，直到读入null为文件结束
    	return resultMap;
    }
    
    public static void main(String[] args) {
    	try {
    		//System.err.println(readLocal().size());
			//ArrayList<Object[]> values = ImportExcelUtil.generateUserSql(new File("C:\\Users\\drh\\Desktop\\免费停车信息_20150519\\bj.xls"), "bj.xls", 0);
			//String sql = "insert into com_info_tb(company_name,resume,create_time,longitude,latitude,type,state,city) values(?,?,?,?,?,?,?,?)";
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
