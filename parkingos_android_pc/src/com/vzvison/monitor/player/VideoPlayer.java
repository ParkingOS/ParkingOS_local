package com.vzvison.monitor.player;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import com.vz.monitor.ui.RealtimePlayActivity;




import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.Matrix;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.nio.IntBuffer;

import android.opengl.GLUtils;

import java.nio.*;

public class VideoPlayer implements Renderer {
	 
	public static final int TYPE_RGB_565 	= 1;
	public static final int TYPE_YUV_420SP 	= 2;
	
	private GLImage image = null;
	private boolean isPlay = false;
	
	private FrameQueue frameQueue;
	private Handler handler;
	private boolean isInit = false;
	
	private GLSurfaceView view;
	
	private FontImage fontImage = null;
	 
    private DrawThread  drawThread = null;
	
	private Frame frame = new Frame();
	private ByteBuffer drawData =null;
	
	public VideoPlayer(GLSurfaceView view) {
		this.view = view;
		
		fontImage = new FontImage();
		
		//drawThread = new DrawThread();
	}
 
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
  
		
	}
	

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		  
		GLES20.glViewport(0, 0, width, height);
	}
	
	

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		if(null != image && isPlay) {
			image.draw();
		}
		else
		{
			
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//| GLES20.GL_DEPTH_BUFFER_BIT);
			
//			fontImage.init();
//			fontImage.setFont("Œﬁ ”Õº");
//			
//			fontImage.draw();
		}
	}
	
 
	 
	public boolean init(int type, int width, int height) {
		image = null;
		switch (type) {
		case TYPE_RGB_565:
			image = new RGB565Image();
			break;
		case TYPE_YUV_420SP:
			image = new YUV420Image();
			break;
		}
		
		if(null != image) {
			image.setResolution(width, height);
			image.init();
			
			return true;
		}
		
		return false;
	}
	
	public void start() {
		isPlay = true;
//		Thread t = new Thread(new DataObtainer());
//		t.start();
        //new DrawThread().start();
		drawThread = new DrawThread();
		drawThread.start();
	}
	
	public void stop() {
		
//		try
//		{
//			drawThread.notify();
//		}
//		catch (IllegalMonitorStateException  e)
//		{
//			
//		}
		
		
		isPlay = false;
		try
		{
			drawThread.join(1000);
			
		}
		catch(InterruptedException e)
		{
			
		}
	//	drawThread.stop();
		
		
		//image = null;
		//isInit = false;
		view.requestRender();
//		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
	
	public void pause()
	{
		if(drawThread.isAlive())
		{
			try
			{
				drawThread.wait();
				
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
	public void resum()
	{
		if(drawThread.isAlive())
		  drawThread.notify();
	}
	
	public void setFrameQueue(FrameQueue frameQueue) {
		this.frameQueue = frameQueue;
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	private synchronized void draw() {
		try {
			//Frame
			frame = frameQueue.getFrameFromQueue();
			 
			if(frame != null) {
				MediaInfo mi = frame.getMediaInfo();
				//frame.getMediaInfo(mi);
				int width = mi.getWidth();
				int height = mi.getHeight();
				int imageWidth = 0;
				int imageHeight = 0;
				if(null != image) {
					imageWidth = image.getWidth();
					imageHeight = image.getHeight();
				}
				
				if(null == image || imageWidth!= width || imageHeight!= height) {
					int initType = TYPE_YUV_420SP;
					if(!isInit) {
						isInit = init(initType, width, height);
					} else {
					//	handler.sendEmptyMessage(RealtimePlayActivity.LONG_TIME_NO_DATA);
						return;
					}
				}
				byte[] data = frame.getData();
//				int datalength = frame.getLength();
//				
//				if( drawData == null || drawData.capacity() < datalength )
//				{ 
//					drawData = ByteBuffer.allocate(datalength  );
//				}
//				 frame.getData(drawData);
				if(null != data && data.length > 0 && null != image) {
					image.put(data);
					view.requestRender();
				}
			}
		} catch (Exception e) {
		}
	}
	
	private class DataObtainer implements Runnable {
		@Override
		public void run() {
			while(isPlay) {
				draw();
				
			}
			frameQueue.clear();
		}
	}
	
	private class DrawThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(isPlay) {
				try
				{
					draw();
					Thread.sleep(30);
				}
				catch(Exception e)
				{
					
				}
				
			}
			frameQueue.clear();
		}
	}

	public boolean snapshot(String path) {
		if(null != image) {
			 return image.saveToJpeg(path);
		}
		return false;
	}
}



