package com.media;

public class MP4Recorder {
	public static final int TYPE_VIDEO = 0;
	public static final int TYPE_AUDIO = 1;
	
	/**
	 * �?启录制器
	 * @param strFileName 录制保存的文件名
	 * @return �?启结�?,成功：true；失败：false
	 */
	public native boolean startRecorder(String strFileName);
	/**
	 * 停止录制�?
	 */
	public native void stopRecorder();
	/**
	 * 添加音视频数�?
	 * @param data 音视频数�?
	 * @param length 数据长度
	 * @param type 数据类型：TYPE_VIDEO：视频；TYPE_AUDIO：音�?
	 * @return
	 */
	public native boolean addSample(byte[] data,int length,int type);
	
	static {
		System.loadLibrary("MP4Recorder");
	}
}
