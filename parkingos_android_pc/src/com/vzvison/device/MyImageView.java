package com.vzvison.device;

import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.annotation.SuppressLint;
import android.content.Context;


public class MyImageView extends ImageView {

	public MyImageView(Context context)
	{
		super(context);
	}
	 public MyImageView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	         
	 }
	   
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		super.setLayoutParams(params);
		   
		int a;
		a = 0 ;
		a = 1;
		
	
    }
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		
		int a;
		a = 0;
	  
		 
    }
	 
	
	  @SuppressLint("NewApi")
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		  
		  final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		  
		  if (widthMode != MeasureSpec.EXACTLY) {
           // throw new IllegalStateException("CellLayout can only be used in EXACTLY mode.");
			  int a;
			  a = 0;
        }
         
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
           // throw new IllegalStateException("CellLayout can only be used in EXACTLY mode.");
        	  int a;
			  a = 0;
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            // throw new IllegalStateException("CellLayout can only be used in EXACTLY mode.");
         	  int a;
 			  a = 0;
         }
        if (heightMode == MeasureSpec.AT_MOST) {
            // throw new IllegalStateException("CellLayout can only be used in EXACTLY mode.");
         	  int a;
 			  a = 0;
         }
		
        boolean bflag = this.getAdjustViewBounds();
        
        
		  int layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
	        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
	        
	        
	       //heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)(layoutHeight*0.5),heightMode);
	        
	        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	     

        setMeasuredDimension(layoutWidth, layoutHeight);
	        
	         
	         int heght = this.getMeasuredHeightAndState();
	         
	         LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)  getLayoutParams();
	         
	         int a;
	         a = 0;
	    }
	  
	 
}
