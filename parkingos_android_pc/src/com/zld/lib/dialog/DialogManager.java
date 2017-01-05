package com.zld.lib.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogManager {
	private static DialogManager dialogManger = new DialogManager();
	private ProgressDialog dialog;

	private DialogManager(){}

	public static DialogManager getInstance(){
		return dialogManger;
	}

	public void showProgressDialog(Context context, String content){
		if(dialog == null){
			dialog = ProgressDialog.show(context, "加载中...", content, true, true);
		}
	}

	/**
	 * 描述:隐藏通信框
	 */
	public void dissMissProgressDialog() {
		if (dialog != null) {
			if (dialog.isShowing())
				dialog.dismiss();
		}
	}
}
