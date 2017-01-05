/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年5月12日 
 * 
 *******************************************************************************/ 
package com.zld.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zld.R;
import com.zld.bean.MyCameraInfo;

/**
 * <pre>
 * 功能说明: 选择通道对话框
 * 日期:	2015年5月12日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年5月12日
 * </pre>
 */
public class PassDialog extends Dialog {

	private Context mContext;
	private ArrayList<MyCameraInfo> cameraList;
	private OnItemClickDialogListener onItemClickListener;

	public PassDialog(Context context,ArrayList<MyCameraInfo> cameraList) {
		super(context);
		this.mContext = context;
		this.cameraList = cameraList;
	}
	
	public void setOnItemClickDialogListener(OnItemClickDialogListener onItemClickListener){
		this.onItemClickListener = onItemClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout  linear = new LinearLayout(mContext);
		LayoutParams linearparams = new LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		GridView gridview = new GridView(mContext);
		gridview.setPadding(30, 25, 30, 25);
		gridview.setHorizontalSpacing(30);
		gridview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	
		int size = cameraList.size();
		if(size <3){
			gridview.setNumColumns(size);
		}else if(size == 4){
			gridview.setNumColumns(2);
		}else{
			gridview.setNumColumns(3);
		}
		gridview.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				onItemClickListener.onItemClick(
						cameraList.get(position).getCamera_name(),cameraList.get(position).getIp());
				PassDialog.this.dismiss();
			}
		});
		linear.addView(gridview, linearparams);

		setContentView(linear);

		GridAdapter gridAdapter = new GridAdapter();
		gridview.setAdapter(gridAdapter);

		setTitle("拍照通道选择");

	}

	public class GridAdapter extends BaseAdapter{

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cameraList.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cameraList.get(position);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView text = new TextView(mContext);
			text.setWidth(80);
			text.setHeight(160);
			text.setTextSize(40);
			text.setTextColor(mContext.getResources().getColor(R.color.white));
			text.setGravity(Gravity.CENTER);
			text.setBackgroundColor(mContext.getResources().getColor(R.color.bg_green));
			text.setText(""+cameraList.get(position).getCamera_name());
			return text;
		}
	}

	public interface OnItemClickDialogListener{
		public void onItemClick(String name,String ip);
	}

}
