/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日
 *******************************************************************************/
package com.zld.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vz.PlateResult;
import com.vz.tcpsdk;
import com.vzvison.database.DeviceInfoTable;
import com.vzvison.database.SnapImageTable;
import com.vzvison.database.plateCallbackInfoTable;
import com.vzvison.database.plateHelper;
import com.vzvison.device.DeviceInfo;
import com.vzvison.device.DeviceSet;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.CarBitmapInfo;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.LeaveOrder;
import com.zld.bean.MyCameraInfo;
import com.zld.bean.MyLedInfo;
import com.zld.bean.ShaerUiInfo;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.fragment.CashFragment;
import com.zld.fragment.EntranceFragment;
import com.zld.fragment.ExitFragment;
import com.zld.fragment.OrderDetailsFragment;
import com.zld.fragment.OrderListFragment;
import com.zld.fragment.OrderListFragment.OrderListListener;
import com.zld.fragment.ParkinfoFragment;
import com.zld.fragment.ParkrecordFragment;
import com.zld.fragment.ParkrecordFragment.ParkRecordListener;
import com.zld.fragment.TitleFragment;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.ComeInCarState;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.BitmapUtil;
import com.zld.lib.util.CameraManager;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.IsServiceStart;
import com.zld.lib.util.LedControl;
import com.zld.lib.util.PollingUtils;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.SocketUtil;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.lib.util.VoicePlayer;
import com.zld.photo.DecodeManager;
import com.zld.photo.UpLoadImage;
import com.zld.service.DetectionServerService;
import com.zld.service.DownLoadService;
import com.zld.service.HomeExitPageService;
import com.zld.service.PollingService;
import com.zld.service.ShareUiService;
import com.zld.view.LineLocalRestartDialog;
import com.zld.view.RestartDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <pre>
 * 功能说明:
 * 日期:	2015年4月13日
 * 开发者:	HZC
 *
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月13日
 * </pre>
 */
@SuppressLint("HandlerLeak")
public class ZldNewActivity extends BaseActivity implements OrderListListener,
        ParkRecordListener, tcpsdk.OnDataReceiver {
    private String TAG = "ZldNewActivity";
    private Intent serviceIntent;
    public static ZldNewActivity instance;
    public CashFragment cashFragment;
    public ExitFragment exitFragment;
    public TitleFragment titleFragment;
    public OrderListFragment listFragment;
    //    private FragmentManager fragmentManager;
    public EntranceFragment entranceFragment;
    public ParkinfoFragment parkinfoFragment;
    private ParkrecordFragment recordFragment;
    public OrderDetailsFragment detailsFragment;
    private MyBroadCaseReceiver myBroadCaseReceiver;

    public final static int REFRESH = 1;
    public final static int SHOW = 2;
    public final static int SHOW_DIALOG = 3;
    public final static int RESTART = 4;
    public final static int HOME_CAMERA_ERROR_DIALOG = 5;
    public final static int CANCEL_HOME_CAMERA_ERROR_DIALOG = 6;
    public final static int SPEAK = 7;
    public final static int POLE_UP_IMAGE = 8;

    public final static int FULL = 10;
    private static final String INTENT_KEY = "intentkey";

    private SocketUtil socketUtil;
    private MyLedInfo homeledinfo;
    private MyLedInfo exitledinfo;
    private String exitLedFirShow;
    public SqliteManager sqliteManager;

    /**
     * 通道类型
     */
//    private String type;
    public String passid;
    private long time = 0;
    private Timer timer;
    //    private boolean isCameraOk = false;
    private RestartDialog restartDialog;
    private RestartDialog homerestartDialog;
    private String cameraExitIp;
    public Bitmap resultBitmap;
    //    private int nType = 0;
//    private int x, y, width, height;
    public boolean isExitComeinCar = false;
    private ArrayList<MyLedInfo> myLedInfoList;
    private HashMap<String, MyLedInfo> cameraipPassid;
    public String freeCarNumber = Constant.sZero;
    private long comeIntime;
    private ArrayList<String> poleIDInList;
    private ArrayList<String> poleIDOutList;

    /**
     * 获取入口Camera
     */
    public ArrayList<MyCameraInfo> selectCameraIn;
    /**
     * 获取出口Camera
     */
    public ArrayList<MyCameraInfo> selectCameraOut;
    /**
     * 获取入口同一通道下的cameraip和MyLedInfo
     */
    public HashMap<String, MyLedInfo> selectIpIn;
    /**
     * 获取出口同一通道下的cameraip和MyLedInfo
     */
    public HashMap<String, MyLedInfo> selectIpOut;

    public static int editFreeCarNumber = -10000; // 获取是否车位已满

//    DetectionServerService detectionServerService;
    PollingService pollingService;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            boolean isCameraOk;
            switch (msg.what) {
                case Constant.LEAVEORDER_MSG:
                    setLeaveOrder(msg);
                    break;
                case Constant.COMECAR_MSG:
                    try {
                        if (poleIDOutList.size() > 0) {
                            uploadExitPolePhoto(msg, false);
                        } else {
                            showExitInfo(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constant.SHOWVIDEO_MSG:

                    break;
                case Constant.PARKING_NUMS_MSG:
                    // 获取到车位信息,刷新界面
                    ShaerUiInfo info = (ShaerUiInfo) msg.obj;
                    freeCarNumber = info.getFree();
                    if (parkinfoFragment.getTime().equals("")) {
                        parkinfoFragment.getChargePeopleInfo();
                    }
                    break;
                case Constant.OPENCAMERA_SUCCESS_MSG:
                    // 打开摄像头成功
//                    isCameraOk = true;
                    updateExitCameraState(true);
                    if (restartDialog != null) {
                        //摄像头重新连接后,如果还有延时让弹框的,则false 不发消息弹框
                        restartDialog.setOk(false);
                    }
                    showToast("打开出口摄像头设备成功");
                    Log.e(TAG, "设置不让重启");
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (selectCameraOut.size() > 0) {
//                                uploadCameraState(selectCameraOut.get(0), Constant.CAMERA_STATE_SUCCESS);
//                            }
//                        }
//                    }).start();
                    break;
                case Constant.OPENCAMERA_FAIL_MSG:
                    Log.e(TAG, "出口摄像头连接出错");
                    if (msg.arg1 == -1) {//当摄像头网线断开,重新接上后,回调到这里
                        showToast("出口摄像头连接出错");
                        CameraManager.reOpenCamera();
                    } else if (msg.arg2 == 0) {
//                        isCameraOk = false;
                        updateExitCameraState(false);
                        showToast("出口摄像头开启失败，请检查设备连接是否完好");
                    }
                    FileUtil.writeSDFile("出口摄像头连接出错", "exit camera is close");
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (selectCameraOut.size() > 0) {
//                                uploadCameraState(selectCameraOut.get(0), Constant.CAMERA_STATE_FAILE);
//                            }
//                        }
//                    }).start();
                    break;
                case Constant.KEEP_TIME:
                    initLedShowAd();
                    break;
                case Constant.SHOWPIC_ONRIGHT_MSG:
                    //上传图片成功回调
                    Log.e(TAG, "查询数据库,删除图片文件及数据库图片信息");
                    deleteOrderIamgeInfo((String) msg.obj);
                    break;
                case Constant.RESTART_YES:
                    closeAndRestart();
                    break;
                case Constant.KEEPALIVE:
                    Log.e(TAG, "出口心跳时间：" + time);
                    Log.e("-----", "界面的KEEPALIVEKEEPALIVE");
//                    isCameraOk = true;
                    time = System.currentTimeMillis();
                    updateExitCameraState(true);
                    if (restartDialog != null) {
                        //摄像头重新连接后,如果还有延时让弹框的,则false 不发消息弹框
                        restartDialog.setOk(false);
                    }
                    break;
                case Constant.KEEPALIVE_TIME:
//                    isCameraOk = false;
                    updateExitCameraState(false);
                    Log.e(TAG, "摄像头连接断开出错");
                    if (selectCameraOut != null && selectCameraOut.size() > 0) {
                        showToast("出口摄像头连接断开出错");
                        restartDialog = new RestartDialog(ZldNewActivity.this, com.zld.R.style.nfcnewdialog, handler, 0);
                        restartDialog.setI(5);
                        restartDialog.setOk(true);
                        restartDialog.initTimer();
                        restartDialog.satrtTiming();
                        restartDialog.show();
                    } else {
                        break;
                    }

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (selectCameraOut.size() > 0)
//                                uploadCameraState(selectCameraOut.get(0), Constant.CAMERA_STATE_FAILE);
//                        }
//                    }).start();
                    break;
                case Constant.EXIT_DELAYED_TIME://出口延时弹框
                    time = System.currentTimeMillis() + 20000;
                    break;
                case Constant.HOME_DELAYED_TIME://入口延时弹框
                    updateTime();
                    break;
                case Constant.STOP:
                    break;
                case Constant.LIST_REFRESH:
                    @SuppressWarnings("unchecked")
                    ArrayList<AllOrder> localorders = (ArrayList<AllOrder>) msg.obj;
                    int type = msg.arg1;
                    localOrderShow(localorders, type);
                    break;
                case Constant.CLEAR_ORDER:
                    initAllFragmentView();
                    hideClearOrder();
                    break;
                case Constant.UPPOLE_IMAGR_SUCCESS:
                    showToast("抬杆记录上传成功");
                    break;
                case Constant.UPPOLE_IMAGR_ERROR:
                    showToast("抬杆记录上传失败");
                    break;
            }
        }
    };

    //	private static ZldNewActivity zldNewActivity=null;
//	public ZldNewActivity(){}
    public static ZldNewActivity getInstance() {
//		if(zldNewActivity==null){
//			zldNewActivity = new ZldNewActivity();
//		}
//		return zldNewActivity;
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        initSqliteManager();
//        initvz();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.zld_new_layout);
//        Intent intent = new Intent(this, DetectionServerService.class);
//        /** 进入Activity开始服务 */
//        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Intent intentPoll = new Intent(this, PollingService.class);
        bindService(intentPoll, connPoll, Context.BIND_AUTO_CREATE);
        initvz();
		((application) getApplication()).setZldNewActivity(this);
        initImei();
        initView();
        initDevices();
        initLEDs();
        setLedConn();
        startPollService();
        getChargeInfo();
        initCameraInfo();
        initTimer();
        getCameraIpOutPassid();
        getCameraInList();
        getCameraOutList();
        getLedCameraIpIn();
        getLedCameraIpOut();
        /* 隐藏免费和收费完成按钮 */
        hideFreeAndChargeBtn();
        passid = getPassid();
        satrtTiming();
        //周期删除文件和删除大日志文件也放到这里面执行
        handler.sendEmptyMessageDelayed(Constant.KEEP_TIME, 10000);
//        handlerNumber.postDelayed(runnable, 5000);
        berthHandler = new BerthHandler(this);
        berthHandler.sendEmptyMessageDelayed(Constant.BerthHandlerWhat,5000);
        FileUtil.writeSDFile("加载时间", "379-->" + System.currentTimeMillis());
    }

    public application m_gb = null;
    public DeviceInfoTable m_DeviceInfoTable = null;
    public DisplayMetrics dm;
    public Map<Integer, DeviceSet>  vedioGroup = new HashMap<Integer, DeviceSet>();

    private void initvz() {

        tcpsdk.getInstance().setup();

        //requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        plateHelper so = new plateHelper(ZldNewActivity.this, "yitijiDatabase.db", null, 1);
        plateCallbackInfoTable pct = new plateCallbackInfoTable();
        pct.setDataBaseHelper(so);

        SnapImageTable sit = new SnapImageTable();
        sit.setDataBaseHelper(so);

        android.app.Application ct = (android.app.Application) getApplicationContext();

        m_gb = (application) ct;

        m_gb.setplateCallbackInfoTable(pct);
        m_gb.setSnapImageTable(sit);

        m_DeviceInfoTable = new DeviceInfoTable();
        m_DeviceInfoTable.setDataBaseHelper(so);


        m_gb.getplateCallbackInfoTable().ClearAll();
        m_gb.getSnapImageTable().ClearAll();

        //	m_DeviceInfoTable.ClearAll();

//        int count = m_DeviceInfoTable.getRowCount();


        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

    }

//    /**
//     * 上传摄像头状态
//     * @param cameraid  摄像头id
//     * @param camerastate 摄像头状态
//     */
//    public void uploadCameraState(MyCameraInfo cameraid, String camerastate) {
////		RequestParams params = new RequestParams();
////		params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_CAMERA_STATE);
////		params.setUrlParams("cameraid", cameraid.getCameraid());
////		params.setUrlParams("state", camerastate);
////		String url = params.getRequstUrl();
////		Log.e(TAG, "上传摄像头状态url---------------->>" + url);
////		HttpManager.requestGET(this, url,this);
//    }
//
//    /**
//     * 上传道闸状态
//     * @param cameraid  摄像头id
//     * @param brakestate 摄像头状态
//     */
//    public void uploadBrakeState(MyCameraInfo cameraid, String brakestate) {
//		RequestParams params = new RequestParams();
//		params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_BRAKE_STATE);
//		params.setUrlParams("passid", cameraid.getPassid());
//		params.setUrlParams("cameraid", cameraid.getCameraid());
//		params.setUrlParams("state", brakestate);
//		String url = params.getRequstUrl();
//		Log.e(TAG, "上传道闸状态url---------------->>" + url);
//		HttpManager.requestGET(this, url,this);		
//    }


    protected void localOrderShow(ArrayList<AllOrder> localorders, int type2) {
        if (listFragment != null) {
            listFragment.localOrderShow(localorders, type2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean param = SharedPreferencesUtils.getParam(
                getApplicationContext(), "zld_config", "isupdate", false);
        if (param) {
            listFragment.getOrderInPark(true);
        }
    }

    private void initImei() {
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        AppInfo.getInstance().setImei(telephonyManager.getDeviceId());
    }

    private void initView() {
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        titleFragment = (TitleFragment) fragmentManager
                .findFragmentById(R.id.title_fragment);
        listFragment = (OrderListFragment) fragmentManager
                .findFragmentById(R.id.order_list_fragment);
        detailsFragment = (OrderDetailsFragment) fragmentManager
                .findFragmentById(R.id.order_details_fragment);
        cashFragment = (CashFragment) fragmentManager
                .findFragmentById(R.id.cash_fragment);
        entranceFragment = (EntranceFragment) fragmentManager
                .findFragmentById(R.id.entrance_fragment);
        recordFragment = (ParkrecordFragment) fragmentManager
                .findFragmentById(R.id.park_record_fragment);
        parkinfoFragment = (ParkinfoFragment) fragmentManager
                .findFragmentById(R.id.park_info_fragment);
        exitFragment = (ExitFragment) fragmentManager
                .findFragmentById(R.id.exit_fragment);
    }


//    private ServiceConnection conn = new ServiceConnection() {
//        /** 获取服务对象时的操作 */
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            detectionServerService = ((DetectionServerService.ServiceBinder) service).getService();
//        }
//
//        /** 无法获取到服务对象时的操作 */
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            detectionServerService = null;
//        }
//
//    };

    private ServiceConnection connPoll = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pollingService = ((PollingService.ServiceBinder) service).getService();

        }

        /** 无法获取到服务对象时的操作 */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            pollingService = null;
        }

    };

    /**
     * 开启入口进程
     */
    private void initDevices() {
        String type;
        type = SharedPreferencesUtils.getParam(getApplicationContext(), "set_workStation", "workStationType", "");
        if (type != null) {
            if (type.equals("入口") || type.equals("出入口")) {
                startBroadcast();
                startService();
            }
        }
    }

    /**
     * 初始化LEDSocket
     */
    private void initLEDs() {
        // TODO Auto-generated method stub
        if (socketUtil == null) {
            socketUtil = new SocketUtil();
        }
    }

    /**
     * 初始化数据库管理
     */
    private void initSqliteManager() {
        if (sqliteManager == null) {
//			sqliteManager = new SqliteManager(this);
//            sqliteManager = ((application) getApplication()).getSqliteManager();
            sqliteManager = ((application) getApplication()).getSqliteManager(ZldNewActivity.this);
        }
    }

    /**
     * 初始化计时器
     */
    private void initTimer() {
        // TODO Auto-generated method stub
        if (timer == null) {
            timer = new Timer();
        }
    }

    /**
     * 开启LED显示屏
     */
    private void setLedConn() {
        if (myLedInfoList == null) {
            myLedInfoList = sqliteManager.selectLed(SqliteManager.PASSTYPE_ALL);
        }
        if (myLedInfoList.size() != 0) {
            LedControl.getLedinstance().startLedConn();
        } else {
            showToast("请后台设置LED");
        }
    }


    /**
     * 获取收费员收费信息
     */
    public void getChargeInfo() {
        // TODO Auto-generated method stub
        if (parkinfoFragment != null) {
            parkinfoFragment.getChargePeopleInfo();
        }
    }

    /**
     * 获取Camera信息
     */
    private void initCameraInfo() {
        // TODO Auto-generated method stub
        /** 获取通道类型为1出口的所有摄像头信息 */
        ArrayList<MyCameraInfo> selectCamera =
                sqliteManager.selectCamera(SqliteManager.PASSTYPE_OUT);
        if (selectCamera.size() != 0) {
            for (int i = 0; i < selectCamera.size(); i++) {
                final MyCameraInfo myCameraInfo = selectCamera.get(i);
                if (myCameraInfo.getIp() != null) {
                    /** 不能连续开启摄像头,隔3s再开启另一个 */
                    TimerTask task = new TimerTask() {
                        public void run() {
                            Log.e(TAG, "开启摄像头IP：" + myCameraInfo.getIp());
                            CameraManager.openCamera(
                                    handler, myCameraInfo.getIp());
//							entranceFragment.initFrame(myCameraInfo.getIp());
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 3000);
                }
            }
        }
        // 初始化道闸抬杆记录ID
        poleIDInList = new ArrayList<String>();
        poleIDOutList = new ArrayList<String>();
    }

    private void initLedShowAd() {
        if (myLedInfoList == null) {
            myLedInfoList = sqliteManager.selectLed(SqliteManager.PASSTYPE_ALL);
        }
        for (int i = 0; i < myLedInfoList.size(); i++) {
            MyLedInfo myLedInfo = myLedInfoList.get(i);
            if (myLedInfo != null) {
                String leduid = myLedInfo.getLeduid();
                String matercont = myLedInfo.getMatercont();
                socketUtil.sendLedData(myLedInfo, leduid, matercont, null,
                        false);
            }
        }
        try {
            File filelog = new File(FileUtil.getSDCardPath() + "/tcb/data.txt");
            if (filelog.exists() && filelog.isFile()) {
                Long length = filelog.length();
                if (length > 20 * 1024 * 1024) {
                    // 大于10M时删除
                    filelog.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUtil.fileRegularDelete();
    }

    /**
     * 注册广播，接收service中启动的线程发送过来的信息，同时更新UI
     */
    private void startBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.exit");
        filter.addAction("com.zld.action.startservicereceiver");
        filter.addAction("com.zld.action.restartservicereceiver");
        myBroadCaseReceiver = new MyBroadCaseReceiver();
        this.registerReceiver(myBroadCaseReceiver, filter);
    }

    private void startService() {
        serviceIntent = new Intent(this, HomeExitPageService.class);
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_KEY, "init");
        bundle.putString("comid", AppInfo.getInstance().getComid());
        bundle.putString("uid", AppInfo.getInstance().getUid());
        serviceIntent.putExtras(bundle);
        this.startService(serviceIntent);
    }

    /**
     * 入口显示车位数
     */
//    Handler handlerNumber = new Handler();
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (freeCarNumber != null) {
//                showFreeCarNumber();
//            }
//            handlerNumber.postDelayed(this, 6000);
//        }
//    };
    private static BerthHandler berthHandler;
    static class BerthHandler extends Handler{
        WeakReference<ZldNewActivity> weakReference;
        BerthHandler(ZldNewActivity zldNewActivity){
            weakReference = new WeakReference<ZldNewActivity>(zldNewActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ZldNewActivity zldNewActivity = weakReference.get();
            if (zldNewActivity.freeCarNumber != null) {
                zldNewActivity.showFreeCarNumber();
            }
            berthHandler.removeMessages(Constant.BerthHandlerWhat);
            berthHandler.sendEmptyMessageDelayed(Constant.BerthHandlerWhat, 6000);
        }
    }

    public void showFreeCarNumber() {
        if (System.currentTimeMillis() - comeIntime < 6000) {
            return;
        }

        ShaerUiInfo info = new ShaerUiInfo();
        int number = Integer.valueOf(freeCarNumber); // 真实车位
        info.setFree(freeCarNumber);
        if (editFreeCarNumber == 0) {
            info.setFree(String.valueOf(number)); // 调整为0表示不调整
        } else if (editFreeCarNumber != -10000) {
            number += editFreeCarNumber; //
            info.setFree(String.valueOf(number));
        }
        if (number <= 0) {
            editFreeCarNumber -= number;
            number = 0;
        }

        parkinfoFragment.setShare(info);
        SharedPreferencesUtils.setParam(getApplicationContext(), "carNumber", "carNumber", number + "");
        if (homeledinfo == null) {
            return;
        }
        if (!TextUtils.isEmpty(homeledinfo.getType()) && homeledinfo.getType().equals(Constant.sOne)) {
            String content;

            if (number <= 0) {
                content = "车位已满";

            } else if (number > 0 && number < 10) {
                content = "余位000" + number;
            } else if (number > 10 && number < 100) {
                content = "余位00" + number;
            } else if (number > 100 && number < 1000) {
                content = "余位0" + number;
            } else if (number > 1000 && number < 10000) {
                content = "余位" + number;
            } else {
                return;
            }
//			PollingUtils.stopPollingService(this, ShareUiService.class,
//					"com.zld.service.ShareUi_Temp");

//			socketUtil.sendLedData(homeledinfo, "42", content, "欢迎光临", false);
            if (homeledinfo.getHeight().equals("16")) {
                socketUtil.sendLedData(homeledinfo, "41", content, "欢迎光临", false);
            } else {
                socketUtil.sendLedData(homeledinfo, "42", content, "欢迎光临", false);
            }
        } else {
            if (!TextUtils.isEmpty(homeledinfo.getHeight()) && homeledinfo.getHeight().equals("16")) {
                socketUtil.sendLedData(homeledinfo, "41", "欢迎光临", "欢迎光临", false);
            } else {
                socketUtil.sendLedData(homeledinfo, "42", "欢迎光临", "欢迎光临", false);
            }
        }
    }

    /**
     * 功能说明：展示入口信息 日期: 2015年3月14日 开发者:
     *
     * @param bundle 参数
     * @throws IOException
     */
    private void showHomeInfo(Bundle bundle) throws IOException {
        int height = bundle.getInt("carPlateheight");
        int width = bundle.getInt("carPlatewidth");
        int x = bundle.getInt("xCoordinate");
        int y = bundle.getInt("yCoordinate");
        String carPlate = bundle.getString("carPlate");
        /*int billingType = bundle.getInt("billingType");// 大小车,黄牌为大车
        int nType = bundle.getInt("nType");
		int resType = bundle.getInt("resType");*/
        byte[] byteArray = bundle.getByteArray("bitmap");
        String ledContent = bundle.getString("led_content");
        String cameraIp = bundle.getString("cameraIp");
        long time = System.currentTimeMillis();
        Log.e(TAG, "入口保存的车牌：" + carPlate + " 时间：" + time);
        SharedPreferencesUtils.setParam(
                getApplicationContext(), "zld_config", "carPlate", carPlate);
        SharedPreferencesUtils.setParam(
                getApplicationContext(), "zld_config", "current_time", time);
		/* 根据摄像头ip地址获取ledip地址,根据ledip地址获取ledinfo对象,然后发送 */
        if (selectIpIn == null || selectIpIn.size() == 0) {
            showToast("请设置后台摄像头和LED屏的信息！");
        } else {
            homeledinfo = selectIpIn.get(cameraIp);
            Timer timer = new Timer();//实例化Timer类
            timer.schedule(new TimerTask() {
                public void run() {
                    socketUtil.sendLedData(null, homeledinfo.getLedip(), null, null, false);
                    this.cancel();
                }
            }, 200);//百毫秒


            if (ledContent != null) {
                if (homeledinfo != null) {
                    String passtype = homeledinfo.getPasstype();
                    if (passtype.equals(Constant.sZero)) {
                        Log.e(TAG, "homeledinfo:" + homeledinfo);
                        comeIntime = System.currentTimeMillis();
                        if (null != homeledinfo.getLeduid() && homeledinfo.getLeduid().equals("41")) {
                            socketUtil.sendLedData(
                                    homeledinfo, "42", ledContent, "欢迎光临", true);
                        } else {
                            socketUtil.sendLedData(
                                    homeledinfo, "190351508", ledContent, "欢迎光临", true);
                        }
                        //**
//			PollingUtils.startPollingService(this, 0, 1, ShareUiService.class,
//					"com.zld.service.ShareUi_Temp");
                    }
                }
            }
        }
        Bitmap homeBitmap = ImageUitls.byteBitmap(byteArray);
        homeBitmap = BitmapUtil.zoomImg(homeBitmap, 1280, 720);

        entranceFragment.refreshView(homeBitmap);
        setDetailInCarState(ComeInCarState.ENTRANCE_COME_IN_CAR_STATE);

        if (listFragment != null) {
            Log.e("OrderListState", "入口来车显示小图前状态：" + OrderListState.getInstance().getState());
            if (OrderListState.getInstance().isOrderFinishState()) {//不是结算完成，未点击收费免费前
                if (x + width <= homeBitmap.getWidth()
                        && y + height <= homeBitmap.getHeight()) {
                    if (x < 10 && y < 10 && width < 10 && height < 10) {
                        detailsFragment.refreshCarPlate(null);
                    } else {
                        Log.e(TAG, "其中某个参数可能为负数");
                        if (x > 0 && y > 0 && width > 0 && height > 0) {
                            Bitmap smallCarPlateBmp = Bitmap
                                    .createBitmap(homeBitmap, x, y, width,
                                            height);
                            detailsFragment.refreshCarPlate(smallCarPlateBmp);
                        }
                    }
                } else {
                    detailsFragment.refreshCarPlate(null);
                }
            }
        }
    }

    /**
     * 上传出口抬杆照片
     */
    private void uploadExitPolePhoto(Message msg, boolean isIn) {
        InputStream bitmapToInputStream =
                ImageUitls.getBitmapInputStream(Constant.sZero,
                        (Bitmap) msg.obj);
        super.isInternet(this);
        UpLoadImage upLoadImage = new UpLoadImage();

        upLoadImage.setmHandler(handler);
        upLoadImage.setComid(AppInfo.getInstance().getComid());

        upLoadImage.uploadPoleImage(bitmapToInputStream, this, isIn);
//		liftPoleState.setState(LiftPoleState.LIFTPOLESTATE_NO);
    }

    /**
     * 功能说明：展示出口信息 日期: 2015年4月17日 开发者:
     *
     * @param msg param
     * @throws IOException
     */
    private void showExitInfo(Message msg) throws IOException {
        int x, y, width, height, nType;
        Log.e(TAG, "隐藏搜索车牌号键盘");
        CarBitmapInfo exitCarBmpInfo = new CarBitmapInfo();
        detailsFragment.hidePopupWindow();
        if (msg.obj instanceof Bitmap) {
            resultBitmap = (Bitmap) msg.obj;
            Bundle bd = msg.getData();
//			int billingType = bd.getInt("billingType");
            nType = bd.getInt("nType");
            String carPlate = bd.getString("carPlate");
            x = bd.getInt("xCoordinate");
            y = bd.getInt("yCoordinate");
            width = bd.getInt("carPlatewidth");
            height = bd.getInt("carPlateheight");
            exitCarBmpInfo.setCarPlate(carPlate);
            exitCarBmpInfo.setBitmap(resultBitmap);
            exitCarBmpInfo.setCarPlateheight(height);
            exitCarBmpInfo.setCarPlatewidth(width);
            exitCarBmpInfo.setxCoordinate(x);
            exitCarBmpInfo.setyCoordinate(y);
            exitCarBmpInfo.setNtype(nType);
            int resType = bd.getInt("resType");
            cameraExitIp = bd.getString("cameraIp");
            if (!carPlate.contains("无")) {
                String param = SharedPreferencesUtils.getParam(
                        getApplicationContext(), "zld_config", "carPlate", null);
                long currentTimeMillis = System.currentTimeMillis();
                long param2 = SharedPreferencesUtils.getParam(
                        getApplicationContext(), "zld_config", "current_time", currentTimeMillis);
                Log.e(TAG, "入场的车" + param + "=时间：" + currentTimeMillis);
                // 车牌号不为null 和时间不是当前时间
                if (param != null && param2 != 0L && param2 != currentTimeMillis) {
                    //车牌号相同 且当前时间-保存的时间 是否小于 1分钟
                    //是的话 则return 不结算
                    long time = currentTimeMillis - param2;
                    Log.e(TAG, "时间戳差：" + time);
                    if (carPlate.equals(param) && (currentTimeMillis - param2) < 30 * 1000) {
                        Log.e(TAG, "刚进场的车" + carPlate);
                        return;
                    }
                }
            }
            exitledinfo = selectIpOut.get(cameraExitIp);
            if (cameraipPassid.get(cameraExitIp) != null) {
                passid = cameraipPassid.get(cameraExitIp).getPassid();
            }
            exitFragment.refreshAllView(exitCarBmpInfo);
            if (resType == 8) {
                setDetailInCarState(ComeInCarState.EXIT_COME_IN_CAR_STATE);
				/*上一辆车结算后未操作,下一辆车来刷新收费员收费金额*/
                beforeRefresh();
                //是否是本地服务器
                if (!AppInfo.getInstance().getIsLocalServer(ZldNewActivity.this)) {//不是本地服务器则没有平板本地化的感念
                    // 本地化相关
                    boolean param = SharedPreferencesUtils.getParam(
                            getApplicationContext(), "nettype", "isLocal", false);
                    Log.e("isLocal", "ZldNewActivity showExitInfo get isLocal " + param);
                    if (param) {
//						getLocalOrderDBManager();
                        if (listFragment != null && listFragment.orderListState != null) {
                            listFragment.orderListState.setState(OrderListState.AUTO_SEARCH_STATE);
                        }
//						doAutoSearchTimeOut(resultBitmap,billingType, carPlate);
//						return;
                    }
                }
                hideSeal();
				/* 隐藏预支付,显示车费,显示结算按钮*/
                hidePrepay();
                showCost();
                setDetailInCarState(ComeInCarState.EXIT_COME_IN_CAR_STATE);
                Log.e(TAG, "出口来车状态：" + detailsFragment.comeInCarState.getState());
                isExitComeinCar = true;
				/* 搜索对应车牌号订单 来车后 搜索*/
                FileUtil.writeSDFile("LOG", "流程开始>>>>>>>>>：showExitInfo  " + exitCarBmpInfo.getCarPlate());
//				HttpManager.UpLogs(ZldNewActivity.this,"流程开始>>>>>>>>>：showExitInfo  "+exitCarBmpInfo.getCarPlate());
                search(exitCarBmpInfo.getCarPlate());

            } else if (resType == 4) {
				/* 手动触发 */
				/*如果手动结算后，图片回调的非常快，则takePhotoLinster为null或上一辆车的信息，
					则保存不了图片,需要在结算后直接拿当前的图片保存上传*/
                if (takePhotoLinster != null) {
                    takePhotoLinster.setTakePhotoLinster(resultBitmap);
                }
            }
        }
    }

    /**
     * 获取对应item订单
     */
    public AllOrder getItemOrder() {
        if (listFragment == null) {
            return null;
        }
        return listFragment.getItemOrder();
    }

    /**
     * 获取对应订单详情
     */
    public void cashOrder() {
        if (listFragment == null || getItemOrder() == null) {
            return;
        }
        String orderid = getItemOrder().getId();
        String localid = getItemOrder().getLocalid();
        listFragment.cashOrder(0, orderid, localid);
    }

    /**
     * 当前状态为当前订单状态
     */
    public void setCurrentOrderState() {
        OrderListState.getInstance().setState(OrderListState.PARK_IN_STATE);
    }

    /**
     * 当前状态为当前修改状态
     */
    public void setModifyState() {
        OrderListState.getInstance().setState(OrderListState.MODIFY_ORDER_STATE);
    }

    /**
     * 刷新订单列表
     */
    public void refreshListOrder() {
        if (listFragment != null) {
            Log.e("OrderListState", "刷新订单列表前判断当前状态：" + OrderListState.getInstance().getState());
            if (OrderListState.getInstance().isParkInState()
                    || OrderListState.getInstance().isClearFinishState()) {
				/*设置来车状态为结算完自动刷新状态,这样自动刷新之后,获取的列表第一条为月卡用户的话,就不播报了*/
                setDetailInCarState(ComeInCarState.AUTO_REFRESH_ORDER_LIST);
                listFragment.getOrderInPark(true);
            }
        }
    }

    public void initAllFragmentView() {
        if (entranceFragment != null) {
            entranceFragment.refreshView(null);
        }
        if (detailsFragment != null) {
            detailsFragment.refreshView(null);
        }
        if (cashFragment != null) {
            cashFragment.refreshView(null);
        }
    }

    /**
     * 上一辆车免费
     */
    @SuppressWarnings("unused")
    private void beforeCar() {
        Log.e(TAG, "上一辆车免费：" + isCashFree());
        if (isCashFree()) {
            cashFragment.freeOrder(false, null);
        }
        cashFragment.setFree(true);
    }

    /**
     * isCashFree不用了,改为是否刷新.逻辑一样.上一辆车结算后未操作,下一辆车来刷新收费员收费金额
     */
    private void beforeRefresh() {
        Log.e(TAG, "上一辆车是否未操作：" + isCashFree());
        if (isCashFree()) {
            getChargeInfo();
        }
        cashFragment.setFree(true);
    }


    /**
     * 入口来车状态,ListItem有为月卡用户,也不播报。 列表手动刷新,不播报 出口播报来车页面自动刷新,播报
     */
    public void setDetailInCarState(int state) {
        if (detailsFragment != null) {
            if (detailsFragment.comeInCarState != null) {
                detailsFragment.comeInCarState.setState(state);
            }
        }
    }

    public void setUserName() {
        parkinfoFragment.setChargePeople(AppInfo.getInstance().getName());
        parkinfoFragment.getChargePeopleInfo();
		/*切换账号后更新Uid信息*/
        updateUid();
    }

    @Override
    public void refreshDetailView(CarNumberOrder order) {
        Log.e(TAG, "Activity里的refreshDetailView订单详情：" + detailsFragment + "==" + order);
        if (detailsFragment != null) {
            detailsFragment.refreshView(order);
        }
    }

    @Override
    public void refreshCashView(CarNumberOrder order) {
        // TODO Auto-generated method stub
        Log.e(TAG, "Activity里的refreshCashView订单详情：" + cashFragment + "==" + order);
        if (cashFragment != null) {
            cashFragment.refreshView(order);
        }
    }

    @Override
    public void refreshRecordView(CarNumberOrder order) {
        // TODO Auto-generated method stub
        Log.e(TAG, "Activity里的refreshRecordView订单详情：" + recordFragment + "==" + order);
        if (recordFragment != null) {
            recordFragment.refreshView(order);
        }
    }

    @Override
    public void refreshSmallCarPlate(Bitmap bitmap) {
        if (detailsFragment != null) {
            detailsFragment.refreshCarPlate(bitmap);
        }
    }

    public void search(String carNumber) {
        if (listFragment != null) {
            listFragment.searchCarNumber(carNumber,
                    OrderListState.AUTO_SEARCH_STATE);
        }
    }

    public class MyBroadCaseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            try {
                // 接收Service发送过来的消息
                Log.e(TAG, "广播接收到的intent:" + intent);
                if (intent != null) {
                    Log.e(TAG, "Activity的广播接收到的action为：--" + intent.getAction());
                    if (intent.getAction().equals("android.intent.action.exit")) {
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            switch (bundle.getInt("receiver_key")) {
                                case POLE_UP_IMAGE:
                                    byte[] bitmapByte = bundle.getByteArray("bitmap");
                                    Bitmap birmap = ImageUitls.byteBitmap(bitmapByte);
                                    Message msg = new Message();
                                    msg.obj = birmap;
                                    if (poleIDInList.size() > 0) {
                                        uploadExitPolePhoto(msg, true);
                                    }
                                    break;
                                case REFRESH:
                                    refreshListOrder();
                                    break;
                                case SHOW:
                                    showHomeInfo(bundle);
                                    break;
                                case SHOW_DIALOG:
                                    FileUtil.writeSDFile("提示", "入口摄像头死机弹对话框" +
                                            TimeTypeUtil.getComplexStringTime(System.currentTimeMillis()));
                                    break;
                                case HOME_CAMERA_ERROR_DIALOG:
                                    Log.e(TAG, "入口错误 弹框");
                                    if (restartDialog != null && restartDialog.isShowing()) {
                                        break;
                                    }
                                    if (selectCameraIn != null && selectCameraIn.size() > 0) {
                                        homerestartDialog = new RestartDialog(
                                                ZldNewActivity.this, com.zld.R.style.nfcnewdialog, handler, 1);
                                        homerestartDialog.setI(5);
                                        homerestartDialog.setOk(true);
                                        homerestartDialog.initTimer();
                                        homerestartDialog.satrtTiming();
                                        homerestartDialog.show();
                                    } else {
                                        break;
                                    }

                                    break;
                                case CANCEL_HOME_CAMERA_ERROR_DIALOG:
                                    if (homerestartDialog != null) {
                                        //摄像头重新连接后,如果还有延时让弹框的,则false 不发消息弹框
                                        homerestartDialog.setOk(false);
                                    }
                                    break;
                                case SPEAK:
                                    String content = bundle.getString("led_content");
                                    String collect = bundle.getString("led_collect");
                                    if (homeledinfo.getHeight().equals("16")) {
                                        socketUtil.sendLedData(homeledinfo, "41", content, collect, true);
                                    } else {
                                        socketUtil.sendLedData(homeledinfo, "42", collect, content, true);
                                    }
//								sendLedShow(collect, content);
                                    break;
                                case Constant.REFRESH_NOMONTHCAR_IMAGE:
                                    refershImg("home_nomonthcar_icon");
                                    break;
                                case Constant.REFRESH_NOMONTHCAR2_IMAGE:
                                    refershImg("home_month2car_icon");
                                    break;
                                case Constant.HOME_CAR_OUTDATE_ICON:
                                    refershImg("home_car_outdate_icon");
                                    break;
                                case FULL:
                                    String show = bundle.getString("led_content");
                                    String play = bundle.getString("led_collect");
                                    playVoice(play);
                                    if (homeledinfo.getHeight().equals("16")) {
                                        socketUtil.sendLedData(homeledinfo, "41", show, play, true);
                                    } else {
                                        socketUtil.sendLedData(homeledinfo, "42", show, play, true);
                                    }
//								sendLedShow(collect, content);
                                    break;
                                case 1221:
                                    String str = bundle.getString("orderid");
                                    showToast(str);
                                    LineLocalRestartDialog dialog = new LineLocalRestartDialog(ZldNewActivity.this,com.zld.R.style.nfcnewdialog,null,str,"取消","抬杆");
                                    dialog.show();
                                    break;
                            }
                        }
                    } else if (intent.getAction().equals("com.zld.action.startservicereceiver")) {
                        Log.e(TAG, "登录成功开启service");
                        startPollService();
                    } else if (intent.getAction().equals("com.zld.action.restartservicereceiver")) {
                        Bundle bundle = intent.getExtras();
                        Log.e(TAG, "bundle是否为null：" + bundle);
                        if (bundle != null) {
                            boolean isLine = bundle.getBoolean("isLine", false);
                            Log.e("linelocal", "重新登录时判断是否要切换为线上服务器" + isLine);
                            LineLocalRestartDialog restartDialog =
                                    new LineLocalRestartDialog(ZldNewActivity.this, com.zld.R.style.nfcnewdialog, handler, isLine);
                            restartDialog.show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止 30秒轮询获取空闲车位 5秒轮询获取离场订单 5分钟下载一次图片
     */
    public void stopPollService() {
        // TODO Auto-generated method stub
		PollingUtils.stopPollingService(this, PollingService.class,
				"com.zld.service.Polling_Service");
        PollingUtils.stopPollingService(this, ShareUiService.class,
                "com.zld.service.ShareUi_Service");
        PollingUtils.stopPollingService(this, DownLoadService.class,
                "com.zld.service.DownLoadImage_Service");
		PollingUtils.stopPollingService(this, DetectionServerService.class,
				"com.zld.service.DetectionServer_Service");
    }

    /**
     * 上传图片到服务器
     */
    public void upload(InputStream bitmapToInputStream, String orderId, int type) {
        super.isInternet(this);
        UpLoadImage upLoadImage = new UpLoadImage();
        upLoadImage.setPhotoType(type);
        upLoadImage.setmHandler(handler);
        upLoadImage.setComid(AppInfo.getInstance().getComid());
        upLoadImage.upload(bitmapToInputStream, orderId,
                exitFragment.exitCarBmpInfo.getxCoordinate() + "",
                exitFragment.exitCarBmpInfo.getyCoordinate() + "",
                exitFragment.exitCarBmpInfo.getCarPlatewidth() + "",
                exitFragment.exitCarBmpInfo.getCarPlateheight() + "",
                exitFragment.exitCarBmpInfo.getCarPlate());

    }

    /**
     * 查询数据库,删除图片文件及数据库图片信息
     *
     * @param orderId param
     */
    private void deleteOrderIamgeInfo(String orderId) {
        if (orderId == null) {
            return;
        }
        UploadImg selectImage = this.sqliteManager.selectImage(orderId);
        if (selectImage != null) {
            String imgexitpath = selectImage.getImgexitpath();
            String imghomepath = selectImage.getImghomepath();
            if (imgexitpath != null) {//出口路径
                ImageUitls.deleteImageFile(imgexitpath);
                Log.e(TAG, "数据库对应订单信息" + orderId);
                this.sqliteManager.deleteData(orderId);
            }
            if (imghomepath != null) {
                ImageUitls.deleteImageFile(imghomepath);
            }
        }
    }

    public void showToast(String text) {
        super.showToast(text);
    }

    /**
     * 列表状态为离场列表状态时,隐藏结算订单、免费、收费完成3个按钮 其他状态,则显示
     */
    public void showOrHideBtn() {
        if (listFragment != null) {
            Log.e("OrderListState", "当前状态：" + OrderListState.getInstance().getState());
            if (OrderListState.getInstance().isParkOutState()) {
                detailsFragment.hideBtn();
                hideFreeAndChargeBtn();
            } else {
				/*结算订单状态才显示免费和收费完成按钮*/
                if (OrderListState.getInstance().isClearFinishState()) {
                    showFreeAndChargeBtn();
                }
            }
        }
    }

    public void showFreeHideChargeFinish() {
        if (cashFragment != null) {
            cashFragment.showFreeHideChargeFinish();
        }
    }

    /**
     * 设置离场订单
     *
     * @param msg `
     */
    public void setLeaveOrder(Message msg) {
        LeaveOrder order = (LeaveOrder) msg.obj;
        Log.e(TAG, "离场订单获取到了消息");
//        if (order == null) {
//            // setNullViewYes();
//        }
        if (!TextUtils.isEmpty(order.getOrderid())) {
            setLeaveState(order);
        }
    }

    @SuppressLint("NewApi")
    public void setLeaveState(LeaveOrder order) {
        Log.e("-------", "setLeaveState");
        if (order.getState() != null && Integer.parseInt(order.getState()) == 1) {
            // 0 代表等待支付
            cashFragment.hideSeal();
        } else if (order.getState() != null) {
            if (Integer.parseInt(order.getState()) == 2) {
                payBack(order.getOrderid());//离场支付回调
                CarNumberOrder currenOrder = detailsFragment.getCurrenOrder();
                AllOrder itemOrder = getItemOrder();
                if (currenOrder != null && itemOrder != null) {
                    /** 当前结算订单与离场订单车牌号一致 */
                    if (currenOrder.getCarnumber().equals(order.getCarnumber()) ||
                            itemOrder.getCarnumber().equals(order.getCarnumber())) {
                        String collect = order.getTotal();
                        String carNumber = order.getCarnumber();
                        carUserPayed(carNumber, collect);
                    }
                }
            } else if (Integer.parseInt(order.getState()) == 0) {
                // 2 代表现金支付
                cashFragment.hideSeal();
            } else if (Integer.parseInt(order.getState()) == -1) {
                // -1 代表支付失败
                showToast("车主支付失败");
            } else {
                showToast("支付状态为：" + order.getState());
            }
        }
    }

    /**
     * 离场支付回调
     */
    public void payBack(String orderid) {
        RequestParams params = new RequestParams();
        params.setUrlHeader(Constant.requestUrl + Constant.PAY_BACK);
        params.setUrlParams("orderid", orderid);
        params.setUrlParams("comid", AppInfo.getInstance().getComid());
        String url = params.getRequstUrl();
        Log.e(TAG, "离场支付回调url---------------->>" + url);
        HttpManager.requestGET(this, url, this);
    }

    /**
     * 车主手机支付或预支付
     */
    Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            carUserPayeds((String) msg.obj);
        }
    };

    public void carUserPayed(String carNumber, String collect) {
        Log.e("--", carNumber);
        Message msg = new Message();
        msg.obj = collect;
        handle.sendMessage(msg);
    }

    public void carUserPayeds(String collect) {
        cashFragment.hideFreeBtn();

        if (getItemOrder().getShopticketid() != null) {
            cashFragment.hideCost();
            cashFragment.setPrepayed(null);
        } else {
            cashFragment.showSeal();
        }
		
		/* 停车宝手机支付或预支付,出口下一辆车来,此辆车不免费 */
        cashFragment.setFree(false);
        VoicePlayer.getInstance(this).playVoice("车主已手机支付");
        if (collect.endsWith(Constant.sZero) && Double.parseDouble(collect) > 0.00f) {
            collect = collect.substring(0, collect.length() - 2);
        }
        controlExitPole();
		/*设置列表为结算并且道闸开启状态*/
        OrderListState.getInstance().setState(OrderListState.ORDER_FINISH_UPPOLE_STATE);
        if (exitledinfo != null && Integer.parseInt(exitledinfo.getWidth()) > 64) {
            sendLedShow(exitledinfo.getMatercont(), "		一路顺风", "云车牌系统手机支付" + collect
                    + "元一路顺风");
        } else if (exitledinfo != null) {
            sendLedShow(exitledinfo.getMatercont(), "一路顺风", "云车牌系统手机支付" + collect
                    + "元一路顺风");
        }

        getChargeInfo();
    }

    /***
     * OrderDetailsFrament 结算订单后显示Led内容
     */
    public void sendLedShow(String collectFir, String collectSec, String content) {
        Log.e(TAG, "出口显示ＬＥＤ:" + exitledinfo);
        String ip = "";
        //临时获取一个出口的摄像头ip,再获取出口的LED对象(出口只有一个摄像头ip);
        if (exitledinfo == null) {
            if (selectCameraOut != null) {
                for (int i = 0; i < selectCameraOut.size(); i++) {
                    MyCameraInfo myCameraInfo = selectCameraOut.get(i);
                    if (myCameraInfo != null) {
                        String passtype = myCameraInfo.getPasstype();
                        if (passtype != null && passtype.equals(Constant.sOne)) {
                            ip = myCameraInfo.getIp();
                        }
                    }
                }
            }
            exitledinfo = selectIpOut.get(ip);
        }
        if (exitledinfo != null) {
            String passtype = exitledinfo.getPasstype();
            if (passtype != null && passtype.equals(Constant.sOne)) {
                Log.e(TAG, "结算订单后显示LED:" +
                        "  collect:" + collectSec + "  content:" + content);
                if (null != exitledinfo.getLeduid() &&
                        exitledinfo.getLeduid().equals("41")) {
                    if (collectFir != null) {
                        exitLedFirShow = collectFir;
                        Timer timer = new Timer();//实例化Timer类
                        timer.schedule(new TimerTask() {
                            public void run() {
                                socketUtil.sendLedData(
                                        exitledinfo, "41", exitLedFirShow, null, false);
                                exitLedFirShow = "";
                                this.cancel();
                            }
                        }, 100);//百毫秒

                    }
                    socketUtil.sendLedData(
                            exitledinfo, "42", collectSec, content, true);
                } else {
                    socketUtil.sendLedData(
                            exitledinfo, "190351508", collectSec, content, true);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.e("shuyu", "onDestroy杀服务");
        super.onDestroy();
        stopPollService();
//        this.unbindService(conn);
        LedControl.getLedinstance().destory();
        if (myBroadCaseReceiver != null) {
            this.unregisterReceiver(myBroadCaseReceiver);
            Log.e(TAG, "解除注册广播");
        }
        if (serviceIntent != null) {
            stopService(serviceIntent);
        }
        Intent intent = new Intent(this, DetectionServerService.class);
        stopService(intent);

        DecodeManager.getinstance().stopYitiji();
        tcpsdk.getInstance().cleanup();
        m_gb.getplateCallbackInfoTable().ClearAll();
        m_gb.getSnapImageTable().ClearAll();
    }


    /**
     * 返回键退出时提示
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //		showToast("键盘空格键");
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getKeyCode() == KeyEvent.KEYCODE_SPACE) {
            cashFragment.setFocus();
            return true;
        }
        return event.getKeyCode() == KeyEvent.KEYCODE_ENTER || super.dispatchKeyEvent(event);
    }

    public void updateUid() {
        Bundle bundle = new Bundle();
        bundle.putString("uid", AppInfo.getInstance().getUid());
        serIntent(new Intent(), bundle, "updateuid");
    }

    public void updateExitCameraState(boolean isCameraOk) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCameraOk", isCameraOk);
        serIntent(new Intent(), bundle, "exitcamerastate");
    }

    public void updateTime() {
        Bundle bundle = new Bundle();
        serIntent(new Intent(), bundle, "updatetime");
    }

    /**
     * 根据Camera的ip地址抬杆 出口抬杆
     */
    public void controlExitPole() {
        Log.e(TAG, "cameraExitIp:" + cameraExitIp);
        if (cameraExitIp != null) {
            DecodeManager.getinstance().controlPole(DecodeManager.openPole,
                    cameraExitIp);
            exitledinfo = selectIpOut.get(cameraExitIp);
            Timer timer = new Timer();//实例化Timer类
            timer.schedule(new TimerTask() {
                public void run() {
                    if (socketUtil == null || exitledinfo == null) {
                        return;
                    }
                    socketUtil.sendLedData(null, exitledinfo.getLedip(), null, null, false);
                    this.cancel();
                }
            }, 200);//百毫秒

            new Thread(new Runnable() {
                // 遇到了抬杆偶然失效的问题，先加个重发机制，后面用确认发送成功来条换掉这个
                public void run() {
                    try {
                        Thread.sleep(2000);
                        DecodeManager.getinstance().controlPole(DecodeManager.openPole,
                                cameraExitIp);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
			
			/*先留着做参考*/
//			Log.e("taigan","抬杆结果:"+result);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					uploadBrakeState(selectCameraOut.get(0),result+"");
//				}
//			}).start();
        }
    }

    /**
     * 根据Camera的ip地址拍照 出口拍照
     */
    public void controlExitCamera() {
        Log.e(TAG, "cameraExitIp:" + cameraExitIp);
        if (cameraExitIp != null) {
            DecodeManager.getinstance().getOneImg(cameraExitIp);
        }
    }

    /**
     * 向Service发送信息
     *
     * @param intent 1
     * @param bundle 2
     * @param action 3
     */
    private void serIntent(Intent intent, Bundle bundle, String action) {
        intent.setClass(ZldNewActivity.this, HomeExitPageService.class);
        bundle.putString(INTENT_KEY, action);
        intent.putExtras(bundle);
        PendingIntent sender = PendingIntent.getService(ZldNewActivity.this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), sender);
    }

    private void getCameraInList() {
        selectCameraIn = sqliteManager.selectCamera(SqliteManager.PASSTYPE_IN);
    }

    private void getCameraOutList() {
        selectCameraOut = sqliteManager.selectCamera(SqliteManager.PASSTYPE_OUT);
    }

    private void getLedCameraIpIn() {
        selectIpIn = sqliteManager.selectIp(SqliteManager.PASSTYPE_IN);
        if (selectIpIn != null && selectIpIn.size() > 0) {
            homeledinfo = selectIpIn.get(selectCameraIn.get(0).getIp());
        }
    }

    private void getLedCameraIpOut() {
        selectIpOut = sqliteManager.selectIp(SqliteManager.PASSTYPE_OUT);
    }

    private void getCameraIpOutPassid() {
        cameraipPassid = sqliteManager.selectIp(SqliteManager.PASSTYPE_OUT);
    }

    /**
     * 显示免费和收费完成按钮
     */
    public void showFreeAndChargeBtn() {
        cashFragment.showFreeAndChargeBtn();
    }

    /**
     * 隐藏免费和收费完成按钮
     */
    public void hideFreeAndChargeBtn() {
        cashFragment.hideFreeAndChargeBtn();
    }

    /**
     * 隐藏已支付印章
     */
    public void hideSealBtn() {
        cashFragment.hideSeal();
    }

    /**
     * 結算是否免費
     */
    public boolean isCashFree() {
        return cashFragment.isFree();
    }

    /**
     * 军警车则抬杆,生成结算刷新
     */
    public boolean isPolicePole(int nType) {
		/* 军警车流程 */
        if (nType == Constant.LT_ARMPOL || nType == Constant.LT_ARMPOL2 ||
                nType == Constant.LT_ARMPOL2_ZONGDUI || nType == Constant.LT_ARMPOL_ZONGDUI ||
                nType == Constant.LT_ARMY || nType == Constant.LT_ARMY2 || nType == Constant.LT_POLICE) {
			/* controlExitPole();*/
            clickFreeOrder(true);
            return true;
        }
        return false;
    }

    /**
     * isPolice 警车不计入免费统计
     */
    public void clickFreeOrder(boolean isPolice) {
        if (cashFragment != null)
            cashFragment.freeActionHandle(isPolice, null);
    }

    /**
     * 改变RadioButton的颜色
     */
    public void changeRadioBtnColor(int position) {
        if (listFragment != null)
            listFragment.changeRadioBtnColor(position);
    }

    /**
     * 显示车费
     */
    public void showCost() {
        if (cashFragment != null)
            cashFragment.ShowCost();
    }

    /**
     * 隐藏预支付金额
     */
    public void hidePrepay() {
        if (cashFragment != null)
            cashFragment.hidePrepay();
    }

    /**
     * 隐藏已支付印章
     */
    public void hideSeal() {
        if (cashFragment != null)
            cashFragment.hideSeal();
    }

//    /**
//     * 隐藏搜索按钮
//     */
//    public void hideSearch() {
//        if (exitFragment != null)
//            exitFragment.hideSearch();
//    }

//    /**
//     * 显示搜索按钮
//     */
//    public void showSearch() {
//        if (exitFragment != null)
//            exitFragment.showSearch();
//    }

//    /**
//     * 出口抬杆是否可用
//     */
//    public void setOutPoleEnable(Boolean enable) {
//        exitFragment.setOpenPoleTouchEnable(enable);
//    }

    /**
     * 隐藏结算订单按钮
     */
    public void hideClearOrder() {
        if (detailsFragment != null)
            detailsFragment.hideBtn();
    }
//
//    /**显示结算订单按钮*/
//    public void showClearOrder() {
//        if (detailsFragment != null)
//            detailsFragment.showBtn();
//    }
//
//    public ArrayList<AllOrder> selectMonthUser(ArrayList<AllOrder> localorders) {
//        if (listFragment != null) {
////			localorders = listFragment.selectMonthUser(localorders);
//        }
//        return localorders;
//    }

    /**
     * 获取通道ID
     */
    public String getPassid() {
        if (selectCameraOut.size() > 0) {
            MyCameraInfo myCameraInfo = selectCameraOut.get(0);
            if (myCameraInfo != null) {
                cameraExitIp = myCameraInfo.getIp();
                if (cameraExitIp != null) {
                    passid = myCameraInfo.getPassid();
                    Log.e(TAG, "-->>passid:" + passid);
                    return passid;
                }
            }
        } else if (selectCameraIn.size() > 0) {
            MyCameraInfo myCameraInfo = selectCameraIn.get(0);
            if (myCameraInfo != null) {
                String cameraInIp = myCameraInfo.getIp();
                if (cameraInIp != null) {
                    passid = myCameraInfo.getPassid();
                    Log.e(TAG, "-->>passid:" + passid);
                    return passid;
                }
            }
        }
        return null;
    }

//    public void saveImage(Bitmap bitmap, final String carnumber, String orderid) {
//        if (carnumber == null || orderid == null) {
//            return;
//        }
//        //本地保存图片及图片信息---原图
//        Log.e(TAG, "carnumber:" + carnumber + " orderid:" + orderid);
//        ImageUitls.SaveImageInfo(sqliteManager, bitmap,
//                AppInfo.getInstance().getUid(), carnumber, orderid,
//                x + "", y + "", Constant.EXIT_PHOTOTYPE + "", width + "", height + "");
//    }
//
//    /**
//     * 五分钟同步获取收费员金额
//     */
//    public void getMoney() {
//        if (parkinfoFragment != null) {
//            parkinfoFragment.getTollManMoney();
//        }
//    }

    /**
     * 本地加or减收费员金额
     *
     * @param total  1
     * @param mobpay 2
     * @param boo    true 加  false 减
     */
    public void addTollmanMoney(String total, String mobpay, boolean boo) {
        Float money;
        Float mobilemoney;
        if (total == null || total.equals("null")) {
            total = Constant.sZero;
        }
        if (mobpay == null || mobpay.equals("null")) {
            mobpay = Constant.sZero;
        }
        String cashpay = SharedPreferencesUtils.getParam(
                getApplicationContext(), "userinfo", "cashpay", Constant.sZero);
        String mobilepay = SharedPreferencesUtils.getParam(
                getApplicationContext(), "userinfo", "mobilepay", Constant.sZero);

        Log.e(TAG, "现金金额：" + total + "== 电子支付金额：" + mobpay + "== 之前保存的金额：" + cashpay);
        if (boo) {
            money = Float.parseFloat(cashpay) + Float.parseFloat(total);
            mobilemoney = Float.parseFloat(mobilepay) + Float.parseFloat(mobpay);
        } else {
            money = Float.parseFloat(cashpay) - Float.parseFloat(total);
            mobilemoney = Float.parseFloat(mobilepay) - Float.parseFloat(mobpay);
        }

        SharedPreferencesUtils.setParam(
                getApplicationContext(), "userinfo", "cashpay", "" + StringUtils.formatFloat(money));
        SharedPreferencesUtils.setParam(
                getApplicationContext(), "userinfo", "mobilepay", "" + StringUtils.formatFloat(mobilemoney));
        Log.e(TAG, "结算后的：--->>>现金金额：" + money + "== 电子支付金额：" + mobilemoney);
    }

//    /**
//     * 减收费员金额
//     */
//    public void subTollmanMoney(String total, String mobpay) {
//        total = (total == null) ? Constant.sZero : total;
//        String cashpay = SharedPreferencesUtils.getParam(
//                getApplicationContext(), "userinfo", "cashpay", Constant.sZero);
//        Log.e(TAG, "现金金额：" + total + "== 电子支付金额：" + mobpay + "== 之前保存的金额：" + cashpay);
//        Float money = Float.parseFloat(cashpay) - Float.parseFloat(total);
//        SharedPreferencesUtils.setParam(
//                getApplicationContext(), "userinfo", "cashpay", "" + StringUtils.formatFloat(money));
//
//        mobpay = (mobpay == null) ? Constant.sZero : mobpay;
//        String mobilepay = SharedPreferencesUtils.getParam(
//                getApplicationContext(), "userinfo", "mobilepay", Constant.sZero);
//        Float mobilemoney = Float.parseFloat(mobilepay) - Float.parseFloat(mobpay);
//        SharedPreferencesUtils.setParam(
//                getApplicationContext(), "userinfo", "mobilepay", "" + StringUtils.formatFloat(mobilemoney));
//        Log.e(TAG, "结算后的：--->>>现金金额：" + money + "== 电子支付金额：" + mobilemoney);
//    }

    private TakePhotoLinster takePhotoLinster;

//    public void setTakePhotoLinster(TakePhotoLinster takePhotoLinster) {
//        this.takePhotoLinster = takePhotoLinster;
//    }

    public interface TakePhotoLinster {
        void setTakePhotoLinster(Bitmap bitmap);
    }

    public void setMoneyAndTime(String logontime) {
        if (parkinfoFragment != null) {
            parkinfoFragment.getLocalMoney();
            if (logontime != null) {
                String time = TimeTypeUtil.getStringTime(Long.valueOf(logontime + "000"));
                parkinfoFragment.setTime(time);
            }
        }
    }

    public MyLedInfo getExitledinfo() {
        return exitledinfo;
    }

//    public void setExitledinfo(MyLedInfo exitledinfo) {
//        this.exitledinfo = exitledinfo;
//    }

    private void closeAndRestart() {
        titleFragment.closeAndRestart();
    }

    /**
     * 执行定时任务
     */
    private void satrtTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                long currentTimeMillis = System.currentTimeMillis();
                if (time == 0 || (currentTimeMillis - time) > 30000) {
                    Message message = new Message();
                    message.what = Constant.KEEPALIVE_TIME;
                    handler.sendMessage(message);
                }
            }
        };
        timer.schedule(task, 20000, 10000);
    }

    /**
     * 开启 30秒轮询获取空闲车位 5秒轮询获取离场订单 5分钟下载一次图片
     */
    public void startPollService() {
        // TODO Auto-generated method stub
        if (!IsServiceStart.isServiceRunning(this,
                "com.zld.service.ShareUi_Service")) {
            Log.e(TAG, "获取分享界面信息服务--->ShareUi_Service");
            PollingUtils.startPollingService(this, 0, 30, ShareUiService.class,
                    "com.zld.service.ShareUi_Service");
        }
//		if (!IsServiceStart.isServiceRunning(this,
//				"com.zld.service.Polling_Service")) {
//			Log.e(TAG, "开启获取支付信息之前--->Polling_Service");
//			PollingUtils.startPollingService(this, 0, 5, PollingService.class,
//					"com.zld.service.Polling_Service");
//		}
        if (!IsServiceStart.isServiceRunning(this,
                "com.zld.service.DownLoadImage_Service")) {
            Log.e(TAG, "后台服务,5分钟下载一次图片--->DownLoadImage_Service");
            PollingUtils.startPollingService(this, 0, 5 * 60,
                    DownLoadService.class,
                    "com.zld.service.DownLoadImage_Service");
        }
		if (!IsServiceStart.isServiceRunning(this,
				"com.zld.service.DetectionServer_Service")) {
			Log.e(TAG, "后台服务,1分钟ping一次服务器--->DetectionServer_Service");
			PollingUtils.startPollingService(this, 0, 10,
					DetectionServerService.class,
					"com.zld.service.DetectionServer_Service");
		}
    }

//    /**
//     * 上传led状态
//     *
//     * @param
//     * @param
//     */
//    public void uploadLEDState(String ledip, String ledstate) {
////		Log.e("LedServerRunnable", "上传ledip---------------->>" + ledip);
////		String ledid = null;
////		ArrayList<MyLedInfo> list = sqliteManager.selectLedByAddress(ledip);
////		if(list!=null&&list.size()>0){
////			MyLedInfo myled = list.get(0);
////			if(myled!=null){
////				ledid = myled.getId();
////			}
////		}
////		if(ledid!=null){
////			RequestParams params = new RequestParams();
////			params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_LED_STATE);
////			params.setUrlParams("ledid", ledid);
////			params.setUrlParams("state", ledstate);
////			String url = params.getRequstUrl();
////			Log.e("LedServerRunnable", "上传led状态url---------------->>" + url);
////			HttpManager.requestGET(this, url,this);
////		}
//    }

    /**
     * 刷新入口图片
     */
    public void refershImg(String imgname) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(imgname, "drawable", appInfo.packageName);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resID);
        entranceFragment.refreshView(bitmap);
    }

    public ArrayList<String> getPoleIDInList() {
        return poleIDInList;
    }

    public ArrayList<String> getPoleIDOutList() {
        return poleIDOutList;
    }

    // -1是代表原始数字   0是车位已满
    public void setHaveFreeCarNumber(int editFreeCarNumbers) {
        if (editFreeCarNumbers != -1 && editFreeCarNumbers != 0) {
            if (editFreeCarNumber == -10000) {
                editFreeCarNumber = 0;
            }
            editFreeCarNumber += editFreeCarNumbers;
        } else if (editFreeCarNumbers == 0) {
            if (freeCarNumber != null)
                editFreeCarNumber = -Integer.valueOf(freeCarNumber);
        } else {
            editFreeCarNumber = editFreeCarNumbers;
        }
        showFreeCarNumber();
    }

    public void playVoice(String content) {
        VoicePlayer.getInstance(this).playVoice(content);
    }

    @Override
    public void onDataReceive(int handle, PlateResult plateResult, int uNumPlates, int eResultType, byte[] pImgFull,
                              int nFullSize, byte[] pImgPlateClip, int nClipSize) {
        try {
            DeviceSet ds = this.getDeviceSetFromHandle(handle);

            if (ds == null) {
                Toast.makeText(ZldNewActivity.this, "车牌回调数据失败:未找到设备", Toast.LENGTH_SHORT).show();
            }

            DeviceInfo di = ds.getDeviceInfo();

            String dateText = "";

            dateText += plateResult.struBDTime.bdt_year;
            dateText += "/";

            dateText += plateResult.struBDTime.bdt_mon;
            dateText += "/";

            dateText += plateResult.struBDTime.bdt_mday;
            dateText += " ";

            dateText += plateResult.struBDTime.bdt_hour;
            dateText += ":";

            dateText += plateResult.struBDTime.bdt_min;
            dateText += ":";

            dateText += plateResult.struBDTime.bdt_sec;

            String plateText = new String(plateResult.license, "GBK");

            if (!m_gb.getplateCallbackInfoTable().addCallbackInfo(di.DeviceName, plateText, dateText, pImgFull, pImgPlateClip)) {
                Toast.makeText(ZldNewActivity.this, "添加车牌回调数据失败", Toast.LENGTH_SHORT).show();
            }

            Log.i("visizion", "decodeByteArray begin");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;//图片宽高都为原来的二分之一，即图片为原来的四分之一 
            options.inInputShareable = true;
            Bitmap bmp;
            try {

                bmp = BitmapFactory.decodeByteArray(pImgFull, 0, pImgFull.length, options);
                if (bmp != null) {
                    Message msg = new Message();

                    msg.what = PlateImage;

                    msg.arg1 = ds.getDeviceInfo().id;
                    msg.obj = bmp;
                    Bundle data = new Bundle();
                    data.putString("plate", plateText);
                    msg.setData(data);

                    handler.sendMessage(msg);
                }
            } catch (OutOfMemoryError e) {
                Log.e("Map", "Tile Loader (241) Out Of Memory Error " + e.getLocalizedMessage());
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.i("visizion", "decodeByteArray end");
            }


        } catch (UnsupportedEncodingException e) {

            Toast.makeText(ZldNewActivity.this, "不支持的解码异常", Toast.LENGTH_SHORT).show();
        }
    }

    public static final int PlateImage = 0x200012;

    public DeviceSet getDeviceSetFromHandle(int handle) {
        java.util.Iterator it = vedioGroup.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            DeviceSet ds = (DeviceSet) entry.getValue();

            if ((ds != null) && (handle == ds.getDeviceInfo().handle)) {
                return ds;
            }
        }


        return null;
    }

    public DeviceSet getDeviceSetFromId(int id) {
        java.util.Iterator it = vedioGroup.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            DeviceSet ds = (DeviceSet) entry.getValue();

            if ((Integer) entry.getKey() == id) {
                return ds;
            }
        }


        return null;
    }

}
