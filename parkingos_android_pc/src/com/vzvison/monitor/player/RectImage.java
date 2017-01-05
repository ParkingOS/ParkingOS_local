package com.vzvison.monitor.player;

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

import javax.microedition.khronos.egl.EGLConfig;

import javax.microedition.khronos.opengles.GL10;

public class RectImage extends GLImage {

	  private  FloatBuffer mTriangle1Vertices;
      private static final int BYTES_PER_FLOAT = 4;
      private float[] mMVPMatrix = new float[16];
       private float[] mViewMatrix = new float[16];
       private float[] mModelMatrix = new float[16];
      private float[] mProjectionMatrix = new float[16];
      private int mMVPMatrixHandle;
      private int mPositionHandle;
      private int mColorHandle;
       private final int POSITION_OFFSET = 0;
       private final int COLOR_OFFSET = 3;
      private final int POSITION_DATA_SIZE = 3;
    private final int COLOR_DATA_SIZE = 4;
    private final int STRIDE = 7 * BYTES_PER_FLOAT;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		final float[] triangle1VerticesData = {
	               // X, Y, Z, 
	                 // R, G, B, A
	                 -1.f, -1.f, 0.0f, 
	                  1.0f, 0.0f, 0.0f, 1.0f,
	                  
	                 1.f, -1.f, 0.0f,
	                 0.0f, 0.0f, 1.0f, 1.0f,
	                
	                 1.0f, 1.f, 0.0f, 
	                 0.0f, 1.0f, 0.0f, 1.0f,
	                 
	                 -1.0f, 1.f, 0.0f, 
	                 0.0f, 1.0f, 0.0f, 1.0f
               };
	         
	         mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * BYTES_PER_FLOAT)
	                 .order(ByteOrder.nativeOrder()).asFloatBuffer();
	        mTriangle1Vertices.put(triangle1VerticesData).position(0);
	        
	        
	        
	        // Position the eye behind the origin.
	         final float eyeX = 0.0f;
	        final float eyeY = 0.0f;
	       final float eyeZ = 1.5f;
	
	         // We are looking toward the distance
	         final float lookX = 0.0f;
	     final float lookY = 0.0f;
	        final float lookZ = -5.0f;
	
	       // Set our up vector. This is where our head would be pointing were we holding the camera.
	       final float upX = 0.0f;
	         final float upY = 1.0f;
	         final float upZ = 0.0f;
	 
	         // Set the view matrix. This matrix can be said to represent the camera position.
	         // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
	         // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
	         Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
	        
	         final String vertexShader =
	                 "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix.
	                 
	               + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
	               + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.              
	               
	               + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.
	               
	               + "void main()                    \n"        // The entry point for our vertex shader.
	               + "{                              \n"
	               + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader. 
	                                                           // It will be interpolated across the triangle.
	               + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
	               + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in                                                                      
	               + "}                              \n";    // normalized screen coordinates.
	            
	         final String fragmentShader =
	                 "precision mediump float;       \n"        // Set the default precision to medium. We don't need as high of a 
	                                                         // precision in the fragment shader.                
	               + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the 
	                                                           // triangle per fragment.              
	               + "void main()                    \n"        // The entry point for our fragment shader.
	               + "{                              \n"
	               + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.          
	               + "}                              \n";    
	             
	         int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
	         if(vertexShaderHandle != 0)
	         {
	             GLES20.glShaderSource(vertexShaderHandle, vertexShader);
	             GLES20.glCompileShader(vertexShaderHandle);
	             
	             final int[] compileStatus = new int[1];
	             GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	             
	             if(compileStatus[0] == 0)
	             {
	                 GLES20.glDeleteShader(vertexShaderHandle);
	                 vertexShaderHandle = 0;
	             }
	         }
	         
	         if(vertexShaderHandle == 0)
	         {
	             throw new RuntimeException("failed to creating vertex shader");
	        }
	         
	         int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
	         if(fragmentShaderHandle != 0)
	         {
	             GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
	             GLES20.glCompileShader(fragmentShaderHandle);
	             
	             final int[] compileStatus = new int[1];
	             GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	             
	             if(compileStatus[0] == 0)
	             {
	                 GLES20.glDeleteShader(fragmentShaderHandle);
	                 fragmentShaderHandle = 0;
	             }
	             
	         }
	         
	         if(fragmentShaderHandle == 0)
	         {
	             throw new RuntimeException("failed to create fragment shader");
	         }
	         
	         int programHandle = GLES20.glCreateProgram();
	         if(programHandle != 0)
	         {
	             GLES20.glAttachShader(programHandle, vertexShaderHandle);
	             GLES20.glAttachShader(programHandle, fragmentShaderHandle);
	             
	             GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
	             GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
	             
	             GLES20.glLinkProgram(programHandle);
	             
	             final int[] linkStatus = new int[1];
	             GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
	             
	             if(linkStatus[0] == 0)
	             {
	                 GLES20.glDeleteProgram(programHandle);
	                 programHandle = 0;
	             }
	         }
	         
	         if(programHandle == 0)
	         {
	             throw new RuntimeException("failed to create program");
	         }
	         
	         mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
	         mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
	         mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
	         
	         GLES20.glUseProgram(programHandle);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		  Matrix.setIdentityM(mModelMatrix, 0);
	}
	
	 private void drawTriandle(final FloatBuffer triangleBuffer)
     {
         triangleBuffer.position(POSITION_OFFSET);
         GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, triangleBuffer);
         GLES20.glEnableVertexAttribArray(mPositionHandle);
         
          triangleBuffer.position(COLOR_OFFSET);
          GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, triangleBuffer);
          GLES20.glEnableVertexAttribArray(mColorHandle);
          
         Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
         Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
         
          GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
       //   GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
          
          
          GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4);
      }
}
