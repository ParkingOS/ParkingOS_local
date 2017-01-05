package com.zld.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zld.R;
/**
 * 
 * <pre>
 * 功能说明: 
 * 日期:	2015年2月9日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年2月9日
 * </pre>
 */
@SuppressLint("ResourceAsColor")
public class AccountDropListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> groups;
	private boolean isCarType;

	public AccountDropListAdapter(Context context,
			ArrayList<String> groups,boolean isCarType) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.groups = groups;
		this.isCarType = isCarType;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groups.isEmpty()?0:groups.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return groups.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView  = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.account_droplist_item, null);
			holder.button = (TextView) convertView.findViewById(R.id.account_droplist_item);
			holder.ivImg = (ImageView) convertView.findViewById(R.id.iv_addaccount);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag() ;
		}
		if (position == groups.size() - 1 && !isCarType){
			holder.button.setText("下班");
		}else 
			if(position == groups.size() - 2 && !isCarType){
			holder.ivImg.setVisibility(View.VISIBLE);		
			holder.button.setText("添加账号");
		}else
		{
			String key = groups.get(position);
			holder.button.setText(key);
			holder.ivImg.setVisibility(View.GONE);
		}
		return convertView;
	}
	private class ViewHolder {
		TextView button;
		ImageView ivImg;
	}
}
