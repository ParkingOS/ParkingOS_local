package com.vzvison.monitor.player;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLFont {
	private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;
    private int mPosition;
    private int mTexCoord;
    private int mTexture;
    
    private int[] textureId;
	
	private int width;
	private int height;
	
	private Bitmap bitmap;
	
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
    	"uniform sampler2D u_samplerTexture; \n" +
    	"void main() { \n" +	
    		"gl_FragColor = texture2D(u_samplerTexture, v_texCoord); \n" +  
    	"}" ;
    
	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}
    
	public void init() {
		vertexBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(squareCoords).position(0);

		drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		drawListBuffer.put(drawOrder).position(0);
	}

	public synchronized void put(byte[] data) {
		bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		
		Paint paint = new Paint();   
        paint.setColor(Color.RED);
        paint.setTextSize(40);
        
//        Rect rect = new Rect();
        String text = "Hell,Opengl es2.0  " + Math.random();
//        paint.getTextBounds(text, 0, text.length(), rect);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.CYAN);
        canvas.drawText(text, 30, 200, paint);
	}

	public synchronized void draw() {
/*
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
	    GLES20.glVertexAttribPointer(mPosition, 3, GLES20.GL_FLOAT, false, 5*4, vertexBuffer);
	    // Load the texture coordinate
	    vertexBuffer.position(3);
	    GLES20.glVertexAttribPointer(mTexCoord, 2, GLES20.GL_FLOAT, false, 5*4, vertexBuffer);

	    GLES20.glEnableVertexAttribArray(mPosition);
	    GLES20.glEnableVertexAttribArray(mTexCoord);

	    GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
	    		width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
	    GLES20.glUniform1i(mTexture, 0);

	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
	    long t2 = System.currentTimeMillis();
	    System.out.println("draw frame time: " + (t2-t1));
*/
		
		
		if(bitmap == null || bitmap.isRecycled()) {
			return ;
		}
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
	    GLES20.glEnableVertexAttribArray(mPosition);
	    GLES20.glEnableVertexAttribArray(mTexCoord);
	    // Set the sampler to texture unit 0
	    GLES20.glUniform1i(mTexture, 0);

	    // Load the vertex position
	    vertexBuffer.position(0);
	    GLES20.glVertexAttribPointer(mPosition, 3, GLES20.GL_FLOAT, false, 5*4, vertexBuffer);
	    // Load the texture coordinate
	    vertexBuffer.position(3);
	    GLES20.glVertexAttribPointer(mTexCoord, 2, GLES20.GL_FLOAT, false, 5*4, vertexBuffer);

	    GLES20.glEnableVertexAttribArray(mPosition);
	    GLES20.glEnableVertexAttribArray(mTexCoord);
	    
	    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	    
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
	    GLES20.glUniform1i(mTexture, 0);

	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
	    long t2 = System.currentTimeMillis();
	    System.out.println("draw frame time: " + (t2-t1));
	}

	private boolean createTexture() {
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		// Active the texture unit 0
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		// Get the attribute locations
		mPosition = GLES20.glGetAttribLocation(mProgram, "a_position");
		mTexCoord = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
		mTexture = GLES20.glGetUniformLocation(mProgram, "u_samplerTexture");
	 
		textureId = new int[1];
		// Generate a texture object
		GLES20.glGenTextures(1, textureId, 0);
		// Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
		
		return true;
	}
}
