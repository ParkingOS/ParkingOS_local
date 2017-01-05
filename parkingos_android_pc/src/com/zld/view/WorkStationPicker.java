package com.zld.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.zld.R;

public class WorkStationPicker extends FrameLayout{

	private int value;
	private CustomNumberPicker mWorkSpinner;
	private String[] mStationDisplayValues = null;
	private OnWorkStationChangedListener mOnWorkStationChangedListener;

	public WorkStationPicker(Context context){
		super(context);
		inflate(context, R.layout.stationdialog, this);
		mWorkSpinner=(CustomNumberPicker)this.findViewById(R.id.npnum_work);
	}
	
	public WorkStationPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.stationdialog, this);
		mWorkSpinner=(CustomNumberPicker)this.findViewById(R.id.npnum_work);
		/* ÆÁ±Îµã»÷NumberPicker×ÓviewÊ±µ¯³öÈí¼üÅÌ  */
		mWorkSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		
	}

	public WorkStationPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.stationdialog, this);
		mWorkSpinner=(CustomNumberPicker)this.findViewById(R.id.npnum_work);
	}
	
	public void setData(int minValue,int maxValue,String[] displayedValues){
		this.mStationDisplayValues = displayedValues;
		mWorkSpinner.setMinValue(minValue);
		mWorkSpinner.setMaxValue(maxValue);
		mWorkSpinner.setValue(value);
		mWorkSpinner.setDisplayedValues(displayedValues);
		mWorkSpinner.setOnValueChangedListener(mOnStationChangedListener);
		mWorkSpinner.invalidate();
	}
	
	public void setValue(int value){
		this.value = value;
	}

	private NumberPicker.OnValueChangeListener mOnStationChangedListener=new OnValueChangeListener(){
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal){
			onWorkStationChanged(newVal);
		}
	};

	public interface OnWorkStationChangedListener {
		void onWorkStationChanged(String value,int id);
	}

	public void setOnWorkStationChangedListener(OnWorkStationChangedListener callback) {
		mOnWorkStationChangedListener = callback;
	}

	private void onWorkStationChanged(int newVal)  {
		if (mOnWorkStationChangedListener != null){
			mOnWorkStationChangedListener.onWorkStationChanged(mStationDisplayValues[newVal],newVal);
		}
	}
}
