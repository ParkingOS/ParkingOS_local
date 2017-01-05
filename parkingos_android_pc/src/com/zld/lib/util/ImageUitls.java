package com.zld.lib.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;

public class ImageUitls {

	public static int getSampleSize(BitmapFactory.Options options){
		return computeSampleSize(options, 1000, 1000 * 1000);
	}

	/**
	 * 图片压缩算法
	 * @param options Bitmap.Options
	 * @param minSideLength 最小显示区
	 * @param maxNumOfPixels 你想要的宽度 * 你想要的高度
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	public static int computeInitialSampleSize(BitmapFactory.Options options,  int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree  = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片 
	 * @param angle 
	 * @param bitmap 
	 * @return Bitmap 
	 */ 
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
		//旋转图片 动作   
		Matrix matrix = new Matrix();;  
		matrix.postRotate(angle);  
		System.out.println("angle2=" + angle);  
		// 创建新的图片   
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
		return resizedBitmap;  
	}

	/**
	 * 将Bitmap转换成InputStream 
	 * @param bm
	 * @param isCompress 是否压缩
	 * @return
	 */
	@SuppressLint("NewApi")
	public static InputStream bitmapToInputStream(Bitmap bm,boolean isCompress){  
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		if(isCompress){
			bm.compress(Bitmap.CompressFormat.WEBP, 10, baos);  
		}else{
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
		}
		InputStream is = new ByteArrayInputStream(baos.toByteArray());  
		return is;  
	}  

	/**
	 * 将图片保存到文件
	 * @param bitmap
	 * @param pPath
	 * @return 
	 */
	@SuppressLint("NewApi")
	public static boolean saveFrameToPath(Bitmap bitmap, String pPath) {
		int BUFFER_SIZE = 1024 * 8;
		try {
			File file = new File(pPath);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
			bitmap.compress(CompressFormat.JPEG, 90, bos);
			bos.flush();
			bos.close();
			fos.close();
			Log.e("ImageUitls", "执行保存图片文件");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void deleteImageFile(String pPath){
		if(pPath!=null){
			File file = new File(pPath);
			if (file!=null&&file.exists()){
				if (file.isFile()){
					file.delete();
				}
			}
		}
	}

	/**
	 * 保存图片到sd卡,保存图片信息到Sqlite
	 * @param sm
	 * @param bitmap
	 * @param uid
	 * @param carNumber
	 * @param orderid
	 * @param lefttop
	 * @param rightbottom
	 * @param PHOTOTYPE 进口 出口
	 * @param width
	 * @param height
	 */
	public static boolean SaveImageInfo(SqliteManager sm,Bitmap bitmap,String uid,String carNumber,
			String orderid,String lefttop,String rightbottom,String PHOTOTYPE,
			String width,String height) {
		boolean isboo = false;
		if(FileUtil.getSDCardPath() != null){
			/*UploadImg selectImage = sm.selectImage(orderid);
			Log.e("ImageUtils", "查询是否存在orderid的订单图片信息"+selectImage.toString());
			if(selectImage != null){
				return;
			}
			Log.e("ImageUtils", "不存在orderid的订单图片信息,执行保存操作");*/
			//保存文件到sd卡
			//保存图片信息到Sqlite:出口的话先查询是否存在
			if(PHOTOTYPE.equals("0")){
				String imgPath = FileUtil.getSDCardPath()+"/tingchebao/image"+orderid+".jpg";
				if (sm != null) {
					sm.insertData(uid, carNumber, orderid, lefttop, rightbottom, 
							PHOTOTYPE, width, height, imgPath,null,"0","0");
				}
				if(bitmap != null){
					isboo = saveFrameToPath(bitmap,imgPath);
				}
			}else if(PHOTOTYPE.equals("1")){
				String imgPath = FileUtil.getSDCardPath()+"/tingchebao/exitimage"+orderid+".jpg";
				if(bitmap != null){
					isboo = saveFrameToPath(bitmap, imgPath);
				}
				if (sm != null) {
					UploadImg selectImg = sm.selectImage(orderid);
					if(selectImg!=null){
						sm.updateSelectImage(orderid, imgPath);
					}else{
						sm.insertData(uid, carNumber, orderid, lefttop, rightbottom, 
								PHOTOTYPE, width, height, null,imgPath,"0","0");
					}	
				}
			}
		}
		Log.e("ImageUitls", "执行保存图片文件"+isboo);
		return isboo;
	}

	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
		final float densityMultiplier = context.getResources().getDisplayMetrics().density;        
		int h= (int) (newHeight*densityMultiplier);
		int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
		photo=Bitmap.createScaledBitmap(photo, w, h, true);
		return photo;
	}

	//图片转字节数组
	public static byte []  bitmapByte(Bitmap bmp){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte [] bitmapByte =baos.toByteArray();
		return bitmapByte;
	}

	//数组转图片
	public static Bitmap byteBitmap(byte [] bitmapByte){
		InputStream is = new ByteArrayInputStream(bitmapByte); 
		Bitmap srcbmp = BitmapFactory.decodeStream(is);
		return srcbmp;

	}
	public static Bitmap getSdImage(String orderid) {
		Bitmap bitmap = null;
		if(FileUtil.getSDCardPath() != null){
			bitmap = BitmapFactory.decodeFile(FileUtil.getSDCardPath()+"/tingchebao/image"+orderid+".jpg");
		}
		return bitmap;
	}
	
	public static List getLOGO() {
		ArrayList list = null;
		Bitmap bitmap = null;
		if(FileUtil.getSDCardPath() != null){
			String path = FileUtil.getSDCardPath()+"/tcb/logo/";
			File file = new File(path);
			File[] listFiles = file.listFiles();
			if(listFiles!=null&&listFiles.length>0){
				for (int i = 0; i < listFiles.length; i++) {
					if(listFiles[i].isFile()&&listFiles[i].getName().endsWith(".jpg")){
						path = FileUtil.getSDCardPath()+"/tcb/logo/"+listFiles[i].getName();
						bitmap = BitmapFactory.decodeFile(path);
						list = new ArrayList();
						list.add(0, bitmap);
						list.add(1,listFiles[i].getName());
						break;
					}
				}
			}
		}
		return list;
	}

	//是否压缩图片
	public static InputStream getBitmapInputStream(String netType,Bitmap resultBitmap) {
		InputStream bitmapToInputStream = null;
		if(netType.equals("0")){
			bitmapToInputStream = ImageUitls.bitmapToInputStream(resultBitmap,true);
		}else if(netType.equals("1")){
			bitmapToInputStream = ImageUitls.bitmapToInputStream(resultBitmap,false);
		}
		return bitmapToInputStream;
	}
	/**
	 * 从网络获取图片并保存
	 * @param path请求地址
	 * @return
	 * @throws IOException 异常后再次获取下
	 */
	public static void getBitmapAndSave(String path) throws IOException{
	    URL url = new URL(path);
	    InputStream inputStream = null;
	    try {
	    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	  	    conn.setConnectTimeout(5000);
	  	    conn.setRequestMethod("GET");
	  	    conn.connect();
	  	    if(conn.getResponseCode() == 200){
	  	    	inputStream = conn.getInputStream();
	  	    	String filename = conn.getHeaderField("Content-disposition");
	  	    	if(filename!=null){
	  	    		filename = URLDecoder.decode(filename, "UTF-8");
	  	    		filename = filename.substring(21);
	  	    	}
	  	    	if(filename==null||"".equals(filename)){
	  	    		return;
	  	    	}
	  		    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	  		    File file = new File(FileUtil.getSDCardPath()+"/tcb/logo");
	  		    if(!file.exists()){
	  		    	file.mkdirs();
	  		    }
	  		    saveFrameToPath(bitmap, FileUtil.getSDCardPath()+"/tcb/logo/"+filename);
	  	    }
		} catch (Exception e) {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	  	    conn.setConnectTimeout(5000);
	  	    conn.setRequestMethod("GET");
	  	    if(conn.getResponseCode() == 200){
	  	    	inputStream = conn.getInputStream();
	  	    	String filename = conn.getHeaderField("Content-disposition");
	  	    	if(filename!=null){
	  	    		filename = URLDecoder.decode(filename, "UTF-8");
	  	    		filename = filename.substring(21);
	  	    	}
	  	    	if(filename==null||"".equals(filename)){
	  	    		return;
	  	    	}
	  		    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		  		File file = new File(FileUtil.getSDCardPath()+"/tcb/logo");
		  		if(!file.exists()){
		  		   file.mkdirs();
		  		}
	  		    saveFrameToPath(bitmap, FileUtil.getSDCardPath()+"/tcb/logo/"+filename);
	  	    }
			e.printStackTrace();
		}finally{
			if(inputStream!=null){
				inputStream.close();
			}
		}
	}

}
