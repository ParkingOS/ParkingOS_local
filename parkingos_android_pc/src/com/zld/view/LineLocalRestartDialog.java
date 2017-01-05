package com.zld.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zld.R;
import com.zld.lib.constant.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <pre>
 * 功能说明: 本地服务器与线上服务器切换,重启计时对话框
 * 日期:	2015年10月14日
 * 开发者:	HZC
 *
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年10月14日
 * </pre>
 */
public class LineLocalRestartDialog extends Dialog {
    private int i = 5;
    private Button bt_ok;
    private Button bt_after;
    private TextView tv_linelocal_hint;
    private TextView tv_timing;
    private Handler handler;
    private Timer timer;
    private String hint = "切换服务器";
    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_timing.setText("" + i--);
                    if (i < 0) {
                        restart();        //重启程序
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public LineLocalRestartDialog(Context context) {
        super(context);
    }

    public LineLocalRestartDialog(Context context, int theme, Handler handler, boolean isLine) {
        super(context, theme);
        this.handler = handler;
        this.context = context;
        if (isLine) {//true线下切线上      false 线上切线下
            hint = "本地服务器异常,确定切换到线上服务器吗？";
        } else {
            hint = "本地服务器畅通,确定切换到本地吗？";
        }
    }

    Context context;
    String content, cancel, ok;

    public LineLocalRestartDialog(Context context, int theme, Handler handler, String content, String cancel, String ok) {
        super(context, theme);
        this.context = context;
        this.handler = handler;
        this.cancel = cancel;
        this.content = content;
        this.ok = ok;
    }

    public void setI(int i) {
        this.i = i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.linelocal_dialog_restart);
        initView();
        setVeiw();
        initTimer();
        satrtTiming();
    }

    public void initView() {
        tv_linelocal_hint = (TextView) findViewById(R.id.tv_linelocal_hint);
        tv_timing = (TextView) findViewById(R.id.tv_timing);
        bt_after = (Button) findViewById(R.id.bt_after);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        if (TextUtils.isEmpty(content))
            tv_linelocal_hint.setText(hint);
        else
            tv_linelocal_hint.setText(content);
    }

    public void setVeiw() {
        if (!TextUtils.isEmpty(cancel)) {
            bt_after.setText(cancel);
        }
        bt_after.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();    //关闭掉计时器
                }
                LineLocalRestartDialog.this.dismiss();
            }
        });

        if (TextUtils.isEmpty(ok)) {
            bt_ok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (timer != null) {
                        timer.cancel();    //关闭掉计时器
                    }
                    LineLocalRestartDialog.this.dismiss();
                    restart();
                }
            });
        } else {
            bt_ok.setText(ok);
            bt_ok.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("HAND_OPEN_POLE");
                    context.sendBroadcast(intent);
                    LineLocalRestartDialog.this.dismiss();
                }
            });
        }

        LineLocalRestartDialog.this.dismiss();
    }

    public void initTimer() {
        // TODO Auto-generated method stub
        if (timer == null) {
            timer = new Timer();
        }
    }

    /**
     * 执行定时任务
     */
    public void satrtTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    public void setText(String text) {
        if (tv_linelocal_hint != null) {
            tv_linelocal_hint.setText(text);
        }
    }

    private void restart() {
        if (handler != null) {
            Message message = new Message();
            message.what = Constant.RESTART_YES;
            handler.sendMessage(message);
            if (timer != null) {
                timer.cancel();
            }
        }else{
            if (timer != null) {
                timer.cancel();
            }
            dismiss();
        }
    }

    public void cancle() {
        timer.cancel();
        this.cancel();
    }
}

