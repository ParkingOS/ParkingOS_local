package com.vzvison.monitor.player;
 
import android.content.Context;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.vzvison.GLToolbox;





 

public class FontImage extends GLImage {
	private int mProgram;
    private int mTexSamplerHandle;
    private int mTexCoordHandle;
    private int mPosCoordHandle;

    private FloatBuffer mTexVertices;
    private FloatBuffer mPosVertices;

    private int mViewWidth;
    private int mViewHeight;

    private int mTexWidth;
    private int mTexHeight;
    
    private Context mContext;
  //  private final Queue<Runnable> mRunOnDraw;
    private int[] mTextures = new int[2];
    int mCurrentEffect;
    private EffectContext mEffectContext;
    private Effect mEffect;
    private int mImageWidth;
    private int mImageHeight;
    private boolean initialized = false;

    private static final String VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
        "attribute vec2 a_texcoord;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_Position = a_position;\n" +
        "  v_texcoord = a_texcoord;\n" +
        "}\n";

    private static final String FRAGMENT_SHADER =
        "precision mediump float;\n" +
        "uniform sampler2D tex_sampler;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
        "}\n";

    private static final float[] TEX_VERTICES = {
        0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };

    private static final float[] POS_VERTICES = {
        -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f
    };

    private static final int FLOAT_SIZE_BYTES = 4;
    
	@Override
	public void init() {
		// TODO Auto-generated method stub
		 // Create program
        mProgram = super.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        // Bind attributes and uniforms
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram,
                "tex_sampler");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texcoord");
        mPosCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_position");

        // Setup coordinate buffers
        mTexVertices = ByteBuffer.allocateDirect(
                TEX_VERTICES.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexVertices.put(TEX_VERTICES).position(0);
        mPosVertices = ByteBuffer.allocateDirect(
                POS_VERTICES.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPosVertices.put(POS_VERTICES).position(0);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		renderTexture(mTextures[0]);
	}
	
	
	public void setFont(String text )
	{
		   Bitmap bmp = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
	        Canvas canvasTemp = new Canvas(bmp);
	        canvasTemp.drawColor(Color.BLACK);
	        Paint p = new Paint();
	        String familyName = "宋体";
	        Typeface font = Typeface.create(familyName, Typeface.BOLD);
	        p.setColor(Color.RED);
	        p.setTypeface(font);
	        p.setTextSize(27);
	        canvasTemp.drawText(text, 0, 100, p);
	        
	        
	        GLES20.glGenTextures(2, mTextures , 0);

	        updateTextureSize(bmp.getWidth(), bmp.getHeight());
	        
	        mImageWidth = bmp.getWidth();
	        mImageHeight = bmp.getHeight();

	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
	                GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
	                GLES20.GL_CLAMP_TO_EDGE);
	}
	  public void updateTextureSize(int texWidth, int texHeight) {
	        mTexWidth = texWidth;
	        mTexHeight = texHeight;
	        computeOutputVertices();
	    }

	    public void updateViewSize(int viewWidth, int viewHeight) {
	        mViewWidth = viewWidth;
	        mViewHeight = viewHeight;
	        computeOutputVertices();
	    }

	    public void renderTexture(int texId) {
	        GLES20.glUseProgram(mProgram);
	        //GLToolbox.checkGlError("glUseProgram");

	        GLES20.glViewport(0, 0, mViewWidth, mViewHeight);
	       // GLToolbox.checkGlError("glViewport");

	        GLES20.glDisable(GLES20.GL_BLEND);

	        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
	                0, mTexVertices);
	        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
	        GLES20.glVertexAttribPointer(mPosCoordHandle, 2, GLES20.GL_FLOAT, false,
	                0, mPosVertices);
	        GLES20.glEnableVertexAttribArray(mPosCoordHandle);
	       // GLToolbox.checkGlError("vertex attribute setup");

	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLToolbox.checkGlError("glActiveTexture");
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);//把已经处理好的Texture传到GL上面
	       // GLToolbox.checkGlError("glBindTexture");
	        GLES20.glUniform1i(mTexSamplerHandle, 0);

	        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    }
	    private void computeOutputVertices() { //调整AspectRatio 保证landscape和portrait的时候显示比例相同，图片不会被拉伸
	        if (mPosVertices != null) {
	            float imgAspectRatio = mTexWidth / (float)mTexHeight;
	            float viewAspectRatio = mViewWidth / (float)mViewHeight;
	            float relativeAspectRatio = viewAspectRatio / imgAspectRatio;
	            float x0, y0, x1, y1;
	            if (relativeAspectRatio > 1.0f) {
	                x0 = -1.0f / relativeAspectRatio;
	                y0 = -1.0f;
	                x1 = 1.0f / relativeAspectRatio;
	                y1 = 1.0f;
	            } else {
	                x0 = -1.0f;
	                y0 = -relativeAspectRatio;
	                x1 = 1.0f;
	                y1 = relativeAspectRatio;
	            }
	            float[] coords = new float[] { x0, y0, x1, y0, x0, y1, x1, y1 };
	            mPosVertices.put(coords).position(0);
	        }
	    }
}
