package com.vzvison.monitor.player;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.media.MediaConverter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLES20;

public class YUV420Image extends GLImage {
	private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;
    private int mPosition;
    private int mTexCoord;
    
    private int yTexture;
	private int uTexture;
	private int vTexture;
	private int[] yTextureNames;
	private int[] uTextureNames;
	private int[] vTextureNames;
	
	private int width;
	private int height;
	
	private int yDataLen;
	private int uDataLen;
	private int vDataLen;
	private int uIndex;
	private int vIndex;
	
	private ByteBuffer yBuffer;
	private ByteBuffer uBuffer;
	private ByteBuffer vBuffer;
	private byte[] yData;
	private byte[] uData;
	private byte[] vData;
	
	private byte[] yuvData;
	
	private boolean isCreateTexture = false;
	
    private final float[] squareCoords = { 
			-1.f, 1.f, 0.0f, // Position 0
			0.0f, 0.0f, // TexCoord 0
			-1.f, -1.f, 0.0f, // Position 1
			0.0f, 1.0f, // TexCoord 1
			1.f, -1.f, 0.0f, // Position 2
			1.0f, 1.0f, // TexCoord 2
			1.f, 1.f, 0.0f, // Position 3
			1.0f, 0.0f // TexCoord 3
	};
	private final short[] drawOrder = { 0, 1, 2, 0, 2, 3 };
	
	private final String vertexShaderCode = 
			"attribute vec4 a_position; \n" +
			"attribute vec2 a_texCoord; \n" +
			"varying vec2 v_texCoord; \n" +
			"void main() { \n" +
				"gl_Position = a_position; \n" +
				"v_texCoord = a_texCoord; \n" +
			"}" ;

    private final String fragmentShaderCode =
    	"#ifdef GL_ES \n" +
    	"precision highp float; \n" +
    	"#endif \n" +
    	"\r\n" +
    	"varying vec2 v_texCoord; \n" +
    	"uniform sampler2D s_texture; \n" +
    	"uniform sampler2D y_texture; \n" +
    	"uniform sampler2D u_texture; \n" +
    	"uniform sampler2D v_texture; \n" +
    	"void main() { \n" +	
    		"vec4 c = vec4((texture2D(y_texture, v_texCoord).r - 16./255.) * 1.164); \n" +  
    		"vec4 U = vec4(texture2D(u_texture, v_texCoord).r - 128./255.); \n" +
    		"vec4 V = vec4(texture2D(v_texture, v_texCoord).r - 128./255.); \n" + 
    		"c += V * vec4(1.596, -0.813, 0, 0); \n" +
    		"c += U * vec4(0, -0.392, 2.017, 0); \n" +
    		"c.a = 1.0; \n" +
    		"gl_FragColor = c.zyxw; \n" +
    	"}" ;
	
	@Override
	public void setResolution(int width, int height) {
		super.setResolution(width, height);
		this.width = width;
		this.height = height;
	}

	@Override
	public synchronized void init() {
		yDataLen = width*height;
		uDataLen=vDataLen = (width/2)*(height/2);
		uIndex = yDataLen;
		vIndex = uIndex+uDataLen;
		
		yBuffer = (ByteBuffer) ByteBuffer.allocateDirect(yDataLen).position(0);
		uBuffer = (ByteBuffer) ByteBuffer.allocateDirect(uDataLen).position(0);
		vBuffer = (ByteBuffer) ByteBuffer.allocateDirect(vDataLen).position(0);
		
		yData = new byte[yDataLen];
		uData = new byte[uDataLen];
		vData = new byte[vDataLen];
		
		yuvData = new byte[width*height*3/2];
		
		vertexBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(squareCoords).position(0);

		drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		drawListBuffer.put(drawOrder).position(0);
	}

	@Override
	public synchronized void put(byte[] data) {
		System.arraycopy(data, 0, this.yuvData, 0, this.yuvData.length);
		
		System.arraycopy(data, 0, yData, 0, yDataLen);
	    yBuffer.put(yData);
	    yBuffer.position(0);

	    System.arraycopy(data, uIndex, uData, 0, uDataLen);
	    uBuffer.put(uData);
	    uBuffer.position(0);

	    System.arraycopy(data, vIndex, vData, 0, vDataLen);
	    vBuffer.put(vData);
	    vBuffer.position(0);
	}

	@Override
	public synchronized void draw() {
		long t1 = System.currentTimeMillis();
	    // Clear the color buffer
	    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	    
	    if(mProgram == 0) {
			mProgram = GLImage.loadProgram(vertexShaderCode, fragmentShaderCode);
	    }
	    
	    if(!isCreateTexture) {
	    	isCreateTexture = createTexture();
	    }
	    
	    // Use the program object
	    GLES20.glUseProgram(mProgram);
	    
	    // Load the vertex position
	    vertexBuffer.position(0);
	    GLES20.glEnableVertexAttribArray(mPosition);
	    GLES20.glEnableVertexAttribArray(mTexCoord);
	    
	    GLES20.glVertexAttribPointer(mPosition, 3, GLES20.GL_FLOAT, false, 5*4, vertexBuffer);
	    // Load the texture coordinate
	    vertexBuffer.position(3);
	    GLES20.glVertexAttribPointer(mTexCoord, 2, GLES20.GL_FLOAT, false, 5*4, vertexBuffer);

	    GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
	    		width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureNames[0]);
	    GLES20.glUniform1i(yTexture, 0);

	    GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
	    		width/2, height/2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, uBuffer);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureNames[0]);
	    GLES20.glUniform1i(uTexture, 2);

	    GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
	    		width/2, height/2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, vBuffer);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureNames[0]);
	    GLES20.glUniform1i(vTexture, 1);

	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
	    
	    long t2 = System.currentTimeMillis();
	   // System.out.println("draw frame time: " + (t2-t1));
	}

	private boolean createTexture() {
		mPosition = GLES20.glGetAttribLocation(mProgram, "a_position");
		mTexCoord = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
		
		if(yTextureNames!=null) {
			GLES20.glDeleteTextures(1, yTextureNames, 0);
		}
		if(uTextureNames!=null) {
			GLES20.glDeleteTextures(1, uTextureNames, 0);
		}
		if(vTextureNames!=null) {
			GLES20.glDeleteTextures(1, vTextureNames, 0);
		}
		
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		yTexture = GLES20.glGetUniformLocation(mProgram, "y_texture");
		yTextureNames = new int[1];
		GLES20.glGenTextures(1, yTextureNames, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureNames[0]);
		
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		uTexture = GLES20.glGetUniformLocation(mProgram, "u_texture");
		uTextureNames = new int[1];
		GLES20.glGenTextures(1, uTextureNames, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureNames[0]);
		
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		vTexture = GLES20.glGetUniformLocation(mProgram, "v_texture");
		vTextureNames = new int[1];
		GLES20.glGenTextures(1, vTextureNames, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureNames[0]);
		
		return true;
	}
	
	@Override
	public synchronized boolean saveToJpeg(String name) {
		super.saveToJpeg(name);
		try {
			byte[] rgb565 = new byte[width*height*2];
			MediaConverter converter = new MediaConverter();
			int result = converter.YUV420SP2RGB565(yuvData, rgb565, width, height);
			if(result > 0) {
				Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
				ByteBuffer buffer = ByteBuffer.wrap(rgb565);
				bitmap.copyPixelsFromBuffer(buffer); //bmp从缓冲区获得数据
				FileOutputStream stream = new FileOutputStream(name);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
