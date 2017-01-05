package com.zld.lib.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;

import com.zld.lib.constant.Constant;

import android.os.Environment;
import android.util.Log;

public class FileUtil {

	/**
	 * the traditional io way
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(String filename) throws IOException {

		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	/**
	 * NIO way
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray2(String filename) throws IOException {

		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}

		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static byte[] toByteArray3(String filename) throws IOException {

		FileChannel fc = null;
		try {
			fc = new RandomAccessFile(filename, "r").getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		String result = null;
		try {
			// 判断SDCard是否存在
			boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			Log.e("存在sdcard路径：", "" + sdcardExist);
			if (sdcardExist) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				result = sdcardDir.toString();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 在SD卡上创建文件
	 *
	 * @throws IOException
	 */
	public static File createSDFile() throws IOException {
		File file = new File(getSDCardPath() + "/tcb");
		if (!file.exists()) {
			file.mkdirs();
		}
		File f = new File(file.getAbsoluteFile() + "/data.txt");
		if (f.exists()) {
			f.createNewFile();
		}
		return f;
	}

	// 创建文件夹
	public static void buildFolder() {
		File dumpFolder = new File(Constant.FRAME_DUMP_FOLDER_PATH);
		if (!dumpFolder.exists()) {
			dumpFolder.mkdirs();
		}
	}

	/**
	 * 写入内容到SD卡中的txt文本中 str为内容
	 */
	public static void writeSDFile(String describe, String str) {
		try {
			// createSDFile();
			FileWriter fw = new FileWriter(createSDFile().getAbsolutePath(), true);
			fw.write(TimeTypeUtil.getNowTime() + "  " + describe + "--->>>" + str + "\n");
			fw.flush();
			fw.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 文件定期删除
	 */
	public static void fileRegularDelete() {
		File file = new File(Constant.FRAME_DUMP_FOLDER_PATH);
		if (file != null) {
			File[] listFiles = file.listFiles();
			if (listFiles == null) {
				return;
			}
			int listFilesLength = listFiles.length;
			// Log.e(TAG,
			// "Constant.FRAME_DUMP_FOLDER_PATH：文件个数"+listFilesLength);
			// 如果文件个数大于3500个,5天，最大一天700辆车,图片300k一个，相当于1G
			if (listFilesLength > Constant.DELETE_IMAGE) {
				long currentTime = System.currentTimeMillis();
				ArrayList<File> deleList = new ArrayList<File>();
				for (int i = 0; i < listFilesLength; i++) {
					if (listFiles[i].isFile()) {
						long lastModified = listFiles[i].lastModified();
						// Log.e(TAG, "文件最后修改时间："+lastModified);
						// Log.e(TAG, "当前时间："+currentTime);
						// Log.e(TAG, "时间："+FIVEDAYTAMP);
						// 判断最后修改日期是否小于五天前，是则删除
						if ((currentTime - Constant.ONEDAYTAMP) > lastModified) {
							// Log.e(TAG,"删除文件名："+listFiles[i].getName());
							deleList.add(listFiles[i]);
						}
					}
				}
				int size = deleList.size();
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						deleList.get(i).delete();
					}
				}
			}
		}
	}
}
