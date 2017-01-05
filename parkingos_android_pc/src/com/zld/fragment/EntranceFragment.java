/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日
 *******************************************************************************/
package com.zld.fragment;

import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vzvison.device.DeviceInfo;
import com.vzvison.device.DeviceSet;
import com.vzvison.device.VedioSetVeiw;
import com.zld.R;
import com.zld.bean.MyCameraInfo;
import com.zld.db.SqliteManager;
import com.zld.lib.constant.Constant;
import com.zld.lib.state.EntranceOrderState;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.service.HomeExitPageService;
import com.zld.ui.ZldNewActivity;
import com.zld.view.PassDialog;
import com.zld.view.PassDialog.OnItemClickDialogListener;
import com.zld.view.SelectLiftPole;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * <pre>
 * 功能说明: 入口Fragment
 * 日期:	2015年4月13日
 * 开发者:	HZC
 *
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月13日
 * </pre>
 */
public class EntranceFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = "EntranceFragment";
    private static final String INTENT_KEY = "intentkey";
    private Intent intent;
    //	private AlarmManager am;
    private Button btn_photo;
    //	private PendingIntent sender;
    private ImageView iv_entrance;
    private Button btn_entrance_open_pole;
    private SelectLiftPole selectLiftPole;
    //	private String poleRecordID; // 抬杆记录返回的ID
    public EntranceOrderState entranceOrderState;
    public RelativeLayout lnentrance;
    public ImageView imgentrance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.entrance_page, container, false);
        initView(rootView);
        onClickEvent();
        initIntent();
        handlerNumber.postDelayed(runnable, 300);
//        initFrame();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                openPole();
            }
        };
        IntentFilter filter = new IntentFilter("HAND_OPEN_POLE");
        getActivity().registerReceiver(receiver,filter);
        return rootView;
    }
    BroadcastReceiver receiver;
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    Handler handlerNumber = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initFrame();
        }
    };
    DeviceSet ds;
    SqliteManager sqliteManager;

    public void initFrame() {
        // public void initFrame(String cameraIp) {
        // TODO Auto-generated method stub
        sqliteManager = activity.sqliteManager;
        ArrayList<MyCameraInfo> selectCamera = sqliteManager.selectCamera(SqliteManager.PASSTYPE_IN);
        int i = 0;
        DeviceInfo di = new DeviceInfo(10 + i);
        if (selectCamera != null && selectCamera.size() > 0) {
            di.setIp(selectCamera.get(0).getIp());
            activity.m_DeviceInfoTable.GetCallbackInfo(10 + i, di);

            VedioSetVeiw vsv = new VedioSetVeiw(activity);
            VZhandler handler = new VZhandler((ZldNewActivity) getActivity());
            vsv.sethandle(handler);
            vsv.setId(di.id);

            ds = new DeviceSet(di, vsv);

            ds.setPlateInfoCallBack(activity, 1);

            // celllayout.addView(vsv, i);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            vsv.setLayoutParams(lp);
            lnentrance.addView(vsv);
            activity.vedioGroup.put(10 + i, ds);
            ds.select();
            ds.playVideo();
        } else {
            lnentrance.setVisibility(View.GONE);
            btn_entrance_open_pole.setVisibility(View.GONE);
            btn_photo.setVisibility(View.GONE);
        }

    }

    /**
     * 初始化控件
     */
    private void initView(View rootView) {
        lnentrance = (RelativeLayout) rootView.findViewById(R.id.ln_entrance);
        imgentrance = (ImageView) rootView.findViewById(R.id.img_entrance);
        iv_entrance = (ImageView) rootView.findViewById(R.id.iv_entrance);
        btn_photo = (Button) rootView.findViewById(R.id.btn_photo);
        btn_entrance_open_pole = (Button) rootView.findViewById(R.id.btn_entrance_open_pole);
        entranceOrderState = new EntranceOrderState();
        selectLiftPole = new SelectLiftPole(activity, btn_entrance_open_pole, this);
    }

    /**
     * 控件点击事件
     */
    private void onClickEvent() {
        btn_entrance_open_pole.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
    }

    /**
     * service intent init
     */
    private void initIntent() {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(getActivity(), HomeExitPageService.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            /**
             * 点击拍照 单个通道-直接操作 多个通道-弹出对话框，显示各个入口通道， 入口通道对应ip地址，根据IP地址下发拍照命令
             */
            case R.id.btn_photo:
                Log.e(TAG, "设置出口来车状态为：补录来车");
                // this.entranceOrderState.setState(EntranceOrderState.ADD_CAR_ORDER_STATE);
                // Log.e(TAG, "补录来车拍照生成订单,Service里让摄像头抓取图片");
                // takeHomePhotoChoice();
                if (ds != null) {
                    int result = ds.forceTrigger();
                    Log.e("tag", result + "");
                }
                break;
            case R.id.btn_entrance_open_pole:
                openPole();
                break;
            default:
                break;
        }
    }
    private void openPole(){
        if (activity.selectCameraIn != null) {
            if (activity.selectCameraIn.size() > 1) {
                buildPolePassDialog(activity.selectCameraIn);
            } else if (activity.selectCameraIn.size() == 1) {
                MyCameraInfo myCameraInfo = activity.selectCameraIn.get(0);
                if (myCameraInfo != null) {
                    callServiceMethod("openPole", myCameraInfo.getIp());
                    selectLiftPole.showLiftPoleView(myCameraInfo.getIp(), true);
                }
            }
        }
    }
//	private void takeHomePhotoChoice() {
//		if (activity.selectCameraIn != null) {
//			if (activity.selectCameraIn.size() > 1) {
//				Log.e(TAG, "activity.selectCameraIn" + activity.selectCameraIn.toString());
//				buildCameraPassDialog(activity.selectCameraIn);
//			} else if (activity.selectCameraIn.size() == 1) {
//				MyCameraInfo myCameraInfo = activity.selectCameraIn.get(0);
//				if (myCameraInfo != null) {
//					takePhotos(myCameraInfo.getIp());
//				}
//			}
//		}
//	}

    /**
     * 点击拍照
     */
    public void takePhotos(String ip) {
        Log.e(TAG, "设置出口来车状态为：补录来车");
        this.entranceOrderState.setState(EntranceOrderState.ADD_CAR_ORDER_STATE);
        Log.e(TAG, "补录来车拍照生成订单,Service里让摄像头抓取图片");
        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("cameraip", ip);
        serIntent(intent1, bundle, "catchimage");
        if (activity.getPoleIDInList().size() > 0) {
            initCarNumber();
        }
    }
//
//	/***
//	 * 拍照时的通道选择,及回调
//	 *
//	 * @param
//	 */
//	private void buildCameraPassDialog(ArrayList<MyCameraInfo> cameraList) {
//		PassDialog passDialog = new PassDialog(activity, cameraList);
//		passDialog.setOnItemClickDialogListener(new OnItemClickDialogListener() {
//
//			@Override
//			public void onItemClick(String name, String ip) {
//				// TODO Auto-generated method stub
//				takePhotos(ip);
//			}
//		});
//		passDialog.show();
//	}

    /***
     * 抬杆时的通道选择,及回调
     */
    private void buildPolePassDialog(ArrayList<MyCameraInfo> cameraList) {
        PassDialog passDialog = new PassDialog(activity, cameraList);
        passDialog.setOnItemClickDialogListener(new OnItemClickDialogListener() {

            @Override
            public void onItemClick(String name, String ip) {
                // TODO Auto-generated method stub
                callServiceMethod("openPole", ip);
            }
        });
        passDialog.show();
    }

    private void initCarNumber() {
        boolean isFirstphoto = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "CarNumberInfo",
                "firstphoto", true);
        if (isFirstphoto) {
            SharedPreferencesUtils.setParam(activity.getApplicationContext(), "CarNumberInfo", "firstphoto", false);
            SharedPreferencesUtils.setParam(activity.getApplicationContext(), "CarNumberInfo", "carnumber", Constant.sZero);
        }
    }

    /**
     * 向Service发送信息
     */
    @SuppressWarnings("static-access")
    private void serIntent(Intent intent, Bundle bundle, String action) {
        intent.setClass(activity, HomeExitPageService.class);
        bundle.putString(INTENT_KEY, action);
        if (activity.getPoleIDInList().size() > 0) {
            bundle.putString("POLE", "TRUE");// 抬杆拍照的标识
//			bundle.putString("POLEID", poleRecordID); // 抬杆拍照订单返回的ID
            bundle.putString("POLEID", ""); // 抬杆拍照订单返回的ID 没有赋值过，直接给""
        }
        intent.putExtras(bundle);
        // sender=PendingIntent.getService(
        // activity,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // if(am == null){
        // am = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        // }
        Log.e(TAG, "发起服务区拍照");
        activity.startService(intent);
        // am.set(AlarmManager.RTC,System.currentTimeMillis()-30000,sender);
    }

    /**
     * 入口抬杆
     */
    private void callServiceMethod(String action, String ip) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.INTENT_KEY, action);
        bundle.putString("poleip", ip);
        intent.putExtras(bundle);
        getActivity().startService(intent);
    }

    public void refreshView(Bitmap bitmap) {
        iv_entrance.setImageBitmap(bitmap);
    }
    static class VZhandler extends Handler{
        WeakReference<ZldNewActivity> activitys;
        VZhandler(ZldNewActivity zldNewActivity){
            activitys = new WeakReference<ZldNewActivity>(zldNewActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ZldNewActivity activity = activitys.get();
            switch (msg.what) {
                case Constant.SelectVedio: {
                    // Toast.makeText(activity, Constant.SelectVedio,
                    // Toast.LENGTH_LONG).show();
                    int vediosetid = msg.arg1;

                    java.util.Iterator it = activity.vedioGroup.entrySet().iterator();
//                    while (it.hasNext()) {
                    while (it.hasNext()) {
                        java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
                        DeviceSet ds = (DeviceSet) entry.getValue();
                        if ((Integer) entry.getKey() == vediosetid) {
                            // MainActivity.this.selectId = vediosetid;
                            ds.select();
                        } else {
                            ds.unselect();
                            // vsv.setVisibility(View.GONE);
                        }
                    }
                }
                break;
                case Constant.DClickVedio: {
                    // Toast.makeText(activity, Constant.DClickVedio,
                    // Toast.LENGTH_LONG).show();
                    // int vediosetid = msg.arg1;
                    //
                    // DeviceSet ds = getDeviceSetFromId(vediosetid);
                    //
                    // ViewSetInnerType type = (ViewSetInnerType)msg.obj;
                    //
                    // if(!m_zoomInFlag)
                    // {
                    // celllayout.ZoomIn(vediosetid-9 );
                    //
                    // if(ds != null)
                    // {
                    // if(type == ViewSetInnerType.Vedio)
                    // {
                    // ds.ZoomInVedio();
                    // }
                    // else
                    // {
                    // ds.ZoomInImage();
                    // }
                    // }
                    //
                    //
                    // m_zoomInFlag = true;
                    // }
                    // else
                    // {
                    // if(ds != null)
                    // if(type == ViewSetInnerType.Vedio)
                    // {
                    // ds.ZoomOutVedio();
                    // }
                    // else
                    // {
                    // ds.ZoomOutImage();
                    // }
                    //
                    //
                    // celllayout.recover();
                    // m_zoomInFlag = false;
                    // }

                }
                break;

                case Constant.ConfigDeivce: {
                    Toast.makeText(activity, Constant.ConfigDeivce + "", Toast.LENGTH_LONG).show();
                    // int vediosetid = msg.arg1;
                    //
                    // DeviceSet ds = getDeviceSetFromId(vediosetid);
                    //
                    // if(ds != null )
                    // {
                    // Intent intent = new Intent(
                    // MainActivity.this,DeviceActivity.class);
                    //
                    // intent.putExtra(deviceNameLabel,
                    // ds.getDeviceInfo().DeviceName);
                    // intent.putExtra(deviceIpLabel, ds.getDeviceInfo().ip);
                    // intent.putExtra(devicePortLabel, ds.getDeviceInfo().port);
                    // intent.putExtra(UserNameLabel, ds.getDeviceInfo().username);
                    // intent.putExtra(UserPasswordLabel,
                    // ds.getDeviceInfo().userpassword);
                    //
                    // MainActivity.this.startActivityForResult(intent, 0);
                    // break;
                    // }

                }
                case Constant.StopVedio: {
                    int vediosetid = msg.arg1;

                    DeviceSet ds = activity.getDeviceSetFromId(vediosetid);
                    if (ds != null) {
                        ds.stopVideo();
                    }

                }
                break;
                case Constant.StartVedio: {
                    int vediosetid = msg.arg1;

                    DeviceSet ds = activity.getDeviceSetFromId(vediosetid);
                    if (ds != null) {
                        ds.playVideo();
                    }
                }
                break;
                case Constant.PlateImage: {
                    Bitmap bmp = (Bitmap) msg.obj;

                    DeviceSet ds = activity.getDeviceSetFromId(msg.arg1);

                    if (bmp != null) {

                        ds.setPlateImage(bmp);
                    }

                    Bundle bundle = msg.getData();

                    ds.setTrriglePlateText(bundle.getString("plate"));
                    // imgentrance.setImageBitmap(bmp);
                }
                break;
                default:
                    Toast.makeText(activity, "未知消息", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
//    private Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case Constant.SelectVedio: {
//                    // Toast.makeText(activity, Constant.SelectVedio,
//                    // Toast.LENGTH_LONG).show();
//                    int vediosetid = msg.arg1;
//
//                    java.util.Iterator it = activity.vedioGroup.entrySet().iterator();
//                    while (it.hasNext()) {
//                        java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
//                        DeviceSet ds = (DeviceSet) entry.getValue();
//                        if ((Integer) entry.getKey() == vediosetid) {
//                            // MainActivity.this.selectId = vediosetid;
//                            ds.select();
//                        } else {
//                            ds.unselect();
//                            // vsv.setVisibility(View.GONE);
//                        }
//                    }
//                }
//                break;
//                case Constant.DClickVedio: {
//                    // Toast.makeText(activity, Constant.DClickVedio,
//                    // Toast.LENGTH_LONG).show();
//                    // int vediosetid = msg.arg1;
//                    //
//                    // DeviceSet ds = getDeviceSetFromId(vediosetid);
//                    //
//                    // ViewSetInnerType type = (ViewSetInnerType)msg.obj;
//                    //
//                    // if(!m_zoomInFlag)
//                    // {
//                    // celllayout.ZoomIn(vediosetid-9 );
//                    //
//                    // if(ds != null)
//                    // {
//                    // if(type == ViewSetInnerType.Vedio)
//                    // {
//                    // ds.ZoomInVedio();
//                    // }
//                    // else
//                    // {
//                    // ds.ZoomInImage();
//                    // }
//                    // }
//                    //
//                    //
//                    // m_zoomInFlag = true;
//                    // }
//                    // else
//                    // {
//                    // if(ds != null)
//                    // if(type == ViewSetInnerType.Vedio)
//                    // {
//                    // ds.ZoomOutVedio();
//                    // }
//                    // else
//                    // {
//                    // ds.ZoomOutImage();
//                    // }
//                    //
//                    //
//                    // celllayout.recover();
//                    // m_zoomInFlag = false;
//                    // }
//
//                }
//                break;
//
//                case Constant.ConfigDeivce: {
//                    Toast.makeText(activity, Constant.ConfigDeivce + "", Toast.LENGTH_LONG).show();
//                    // int vediosetid = msg.arg1;
//                    //
//                    // DeviceSet ds = getDeviceSetFromId(vediosetid);
//                    //
//                    // if(ds != null )
//                    // {
//                    // Intent intent = new Intent(
//                    // MainActivity.this,DeviceActivity.class);
//                    //
//                    // intent.putExtra(deviceNameLabel,
//                    // ds.getDeviceInfo().DeviceName);
//                    // intent.putExtra(deviceIpLabel, ds.getDeviceInfo().ip);
//                    // intent.putExtra(devicePortLabel, ds.getDeviceInfo().port);
//                    // intent.putExtra(UserNameLabel, ds.getDeviceInfo().username);
//                    // intent.putExtra(UserPasswordLabel,
//                    // ds.getDeviceInfo().userpassword);
//                    //
//                    // MainActivity.this.startActivityForResult(intent, 0);
//                    // break;
//                    // }
//
//                }
//                case Constant.StopVedio: {
//                    int vediosetid = msg.arg1;
//
//                    DeviceSet ds = activity.getDeviceSetFromId(vediosetid);
//                    if (ds != null) {
//                        ds.stopVideo();
//                    }
//
//                }
//                break;
//                case Constant.StartVedio: {
//                    int vediosetid = msg.arg1;
//
//                    DeviceSet ds = activity.getDeviceSetFromId(vediosetid);
//                    if (ds != null) {
//                        ds.playVideo();
//                    }
//                }
//                break;
//                case Constant.PlateImage: {
//                    Bitmap bmp = (Bitmap) msg.obj;
//
//                    DeviceSet ds = activity.getDeviceSetFromId(msg.arg1);
//
//                    if (bmp != null) {
//
//                        ds.setPlateImage(bmp);
//                    }
//
//                    Bundle bundle = msg.getData();
//
//                    ds.setTrriglePlateText(bundle.getString("plate"));
//                    // imgentrance.setImageBitmap(bmp);
//                }
//                break;
//                default:
//                    Toast.makeText(activity, "未知消息", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//
//        }
//    };
}
