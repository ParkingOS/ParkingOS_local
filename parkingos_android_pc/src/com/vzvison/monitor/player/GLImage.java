package com.vzvison.monitor.player;

import android.opengl.GLES20;
import android.util.Log;

public abstract class GLImage {
	private int width = 0;
	private int height = 0;
	
	public abstract void init();
	
	public void put(byte[] data) {
		System.out.println("put data to image");
	}
	
	public void put(String fileName) {
		System.out.println("put file to image");
	}
	
	public abstract void draw();
	
	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public static final int loadShader(int type, String shaderSrc) {
		int shader = 0;
	    int[] compiled = new int[1];

	    // Create the shader object
	    shader = GLES20.glCreateShader(type);
	    if (shader == 0) {
	        return 0;
	    }
	    // Load the shader source
	    GLES20.glShaderSource(shader, shaderSrc);
	    // Compile the shader
	    GLES20.glCompileShader(shader);
	    // Check the compile status
	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

	    if (compiled[0] == 0) {
	        Log.e("ESShader", GLES20.glGetShaderInfoLog(shader));
	        GLES20.glDeleteShader(shader);
	        return 0;
	    }
	    return shader;
	}
	
	public static final int loadProgram(final int vertexShader, final int fragmentShader) {
		if(0 == vertexShader || 0 == fragmentShader) {
			return 0;
		}
		
		int programObject = 0;
		int[] linked = new int[1];
		
		// Create the program object
		programObject = GLES20.glCreateProgram();
		
		if (programObject == 0) {
			return 0;
		}
		
		GLES20.glAttachShader(programObject, vertexShader);
		GLES20.glAttachShader(programObject, fragmentShader);
		
		// Link the program
		GLES20.glLinkProgram(programObject);
		
		// Check the link status
		GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);
		
		if (linked[0] == 0) {
			Log.e("ESShader", "Error linking program:");
			Log.e("ESShader", GLES20.glGetProgramInfoLog(programObject));
			GLES20.glDeleteProgram(programObject);
			return 0;
		}
		
		// Free up no longer needed shader resources
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		
		return programObject;
	} 
	
	public static final int loadProgram(final String vertexShaderCode, final String fragmentShaderCode) {
	    // Load the vertex/fragment shaders
	    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	    if (vertexShader == 0) {
	        return 0;
	    }

	    int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	    if (fragmentShader == 0) {
	        GLES20.glDeleteShader(vertexShader);
	        return 0;
	    }
	    
	    return loadProgram(vertexShader, fragmentShader);  
	}
	
	public synchronized boolean saveToJpeg(String name) {
		return false;
	}

}
