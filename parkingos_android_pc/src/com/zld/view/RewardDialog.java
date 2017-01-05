package com.zld.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zld.R;
import com.zld.bean.LeaveOrder;

public class RewardDialog extends Dialog {
	private LeaveOrder order;
	private TextView tv_carnumber;
	private TextView tv_money;
	private Button bt_ok;

	public RewardDialog(Context context) {
		super(context);
	}

	public RewardDialog(Context context,int theme,LeaveOrder order) {
		super(context,theme);
		this.order = order;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_reward);
		initView();
		if (this.order != null) {
			setVeiw();
		}
	}

	public void initView() {
		tv_carnumber = (TextView) findViewById(R.id.tv_reward_carnumber);
		tv_money = (TextView) findViewById(R.id.tv_reward_money);
		bt_ok = (Button) findViewById(R.id.bt_reward_ok);
	}

	public void setVeiw() {
		if (order.getCarnumber() != null) {
			tv_carnumber.setText("车主：" + order.getCarnumber());
		}
		if (order.getTotal() != null) {
			tv_money.setText("打赏：" + order.getTotal() + "元");
		}
		bt_ok.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				RewardDialog.this.dismiss();
			}
		});
	}
}
