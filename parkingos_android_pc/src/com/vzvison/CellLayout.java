package com.vzvison;

import com.zld.R;

import android.content.Context;

import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

import android.content.res.TypedArray;
/**
 * 
 * @className: CellLayout
 * @description: 给ViewPager每一页使用的GridLayout, 均分屏幕 默认是3行3列 可以设置行距,
 *               不能在ScrollView中使用
 * @author: gaoshuai
 * @date: 2015年9月25日 上午10:46:55
 */
public class CellLayout extends ViewGroup
{
    private int lineNum = 1;
    private int columnNum = 2;
    private int vecitcalSpace=0;
    private int horizontalSpace=0;
    private int mCellWidth;
    private int mCellHeight;
    private int ZoomInNum = 0;
    
    public CellLayout(Context context) {
        super(context);
       
    }
    
    public CellLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
       
    }
    
    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context, attrs);
    }
    
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CellDefinedAttr  );
        lineNum = ta.getInt(R.styleable.CellDefinedAttr_lineNum, 1);
        columnNum = ta.getInt(R.styleable.CellDefinedAttr_columnNum, 2);
        ta.recycle();
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawingTime = getDrawingTime();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            drawChild(canvas, getChildAt(i), drawingTime);
        }
        
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        if (widthMode != MeasureSpec.EXACTLY) {
//            throw new IllegalStateException("CellLayout can only be used in EXACTLY mode.");
//        }
//         
//        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        if (heightMode != MeasureSpec.EXACTLY) {
//            throw new IllegalStateException("CellLayout can only be used in EXACTLY mode.");
//        }
        //        int layoutWidth = this.getWidth();
        //        int layoutHeight = this.getHeight();
        int layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        mCellWidth = 0;
        mCellHeight = 0;
        if (this.columnNum > 0) {
            //cellWidth = layoutWidth/this.columnNum - (this.columnNum + 1) * this.horizontalSpace;
            mCellWidth = (layoutWidth - (this.columnNum + 1) * this.horizontalSpace) / this.columnNum;
        }
        if (this.lineNum > 0) {
            mCellHeight = (layoutHeight - (this.lineNum + 1) * this.vecitcalSpace) / this.lineNum;
            
        }
        
        
        if(ZoomInNum == 0)
        {
        	// The children are given the same width and height as the workspace
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                //getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            	 
                getChildAt(i).measure(MeasureSpec.makeMeasureSpec(mCellWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mCellHeight, MeasureSpec.EXACTLY));
            }	
        }
        else
        {
        	 final int count = getChildCount();
             for (int i = 0; i < count; i++) {
                 //getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
             	 
            	 if(ZoomInNum - 1 != i)
            	 {
            		 
            	 }
                   // getChildAt(i).measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
            	 else
                    getChildAt(ZoomInNum - 1).measure(MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY));
             }	
        	
        }
        
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	 final int count = getChildCount();
	     int index = 0;
	        
    	if(ZoomInNum == 0)
    	{
    		
    	        for (int i = 0; i < this.lineNum; i++) {
    	            for (int j = 0; j < this.columnNum; j++) {
    	                if (index >= count) {
    	                    break;
    	                }
    	                
    	                int x = j * mCellWidth + (j + 1) * this.horizontalSpace;
    	                int y = i * mCellHeight + (i + 1) * this.vecitcalSpace;
    	                int width = mCellWidth;
    	                int height = mCellHeight;
    	                
    	                final View child = getChildAt(index);
    	                
    	                if (child.getVisibility() != View.GONE) {
    	                    child.layout(x, y, x + width, y + height);
    	                }
    	                
    	                index++;
    	            }
    	        }
    	}
    	else
    	{
    		//for(int i = 0 ; i < count ; i++ )
    		{
    			final View child = getChildAt(ZoomInNum-1);
    			
    			if( child != null)
    			{
    				 child.layout(0, 0, r,b);
    			}
    			 
    		}
    		 
    	}
    	
       
    }
    
    public void setHorizontalSpace(int space) {
        this.horizontalSpace = space;
    }
    
    public void setVecticalSpace(int space) {
        this.vecitcalSpace = space;
    }
    
    public int getLineNum() {
        return lineNum;
    }
    
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
    
    public int getColumnNum() {
        return columnNum;
    }
    
    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }
    
    //num从1开始
    public boolean ZoomIn(int num)
    {  
    	if(ZoomInNum > 0)
    	  return false	;
    	final int count = getChildCount();
    	
    	if(num > count)
    		return false;
    	
    	ZoomInNum = num;
    	 
	     int index = 0;
	     for( ;index < count; index++ )
	     {
	    	 final View child = getChildAt(index);
	    	 
	    	 if ( index != ZoomInNum-1 )
	    	 {
	    		 if( child != null)
	 			 {
	 				child.setVisibility(View.GONE);
	 			 }
	    	 }
	    	 else
	    	 {  
	    		 child.setVisibility(View.VISIBLE);
	    	 }
	    	  
	     }
    	
	     this.requestLayout();
    	return true ;
    }
    
    public void  recover()
    {
    	ZoomInNum = 0;
    	
    	 final int count = getChildCount();
	     int index = 0;
	     for( ;index < count; index++ )
	     {
	    	 final View child = getChildAt(index);
 			
 			 if( child != null)
 			 {
 				child.setVisibility(View.VISIBLE);
 			 }
	     }
	     
	     this.requestLayout();
    }
}