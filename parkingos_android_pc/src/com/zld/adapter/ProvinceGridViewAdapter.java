package com.zld.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zld.R;

public class ProvinceGridViewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> provinces;
	private Boolean isnumber;
	private static final String TAG = "ProvinceGridViewAdapter";


	public ProvinceGridViewAdapter(Context context, ArrayList<String> provinces,Boolean isnumber) {
		super();
		this.context = context;
		this.provinces = provinces;
		this.isnumber = isnumber;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return provinces.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return provinces.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e(TAG, provinces.size() + "  " + position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.input_car_number_item, null);
			holder = new ViewHolder();
			holder.textinfo = (TextView) convertView.findViewById(R.id.tv_textinfo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if ( !isnumber){
			holder.textinfo.setText(provinces.get(position));
			if (position == provinces.size() - 1){
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bt_delete_selector));
			}
		}else {
			String key = provinces.get(position);
			if (key.equals("0")) {
				//				View viewnumber  = View.inflate(context, R.layout.input_car_number_item2, null);
				//				TextView textnumber = (TextView) viewnumber.findViewById(id.tv_textinfo_number);
				//				textnumber.setText(provinces.get(position));
				//				return viewnumber;
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("1")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("2")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("3")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("4")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("5")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("6")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("7")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("8")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else if (key.equals("9")) {
				holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.number_selector));
				holder.textinfo.setText(provinces.get(position));
				holder.textinfo.setTextColor(context.getResources().getColor(R.color.key_font_gray));
				return convertView;
			}else {
				holder.textinfo.setText(provinces.get(position));
				if (position == provinces.size() - 1){
					holder.textinfo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bt_delete_selector));
				}				
			}
		}
		return convertView;
	}

	private static class ViewHolder{
		TextView textinfo;
	}
}
