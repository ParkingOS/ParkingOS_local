package com.zld.lib.constant;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class Constant {
	/**
	 * 网络相关常量库
	 */
	/*1、PollingService修改获取离场消息字段
	  2、LoginActivity里修改longinSuccess方法里的
	  Constant.requestUrl  Constant.serverUrl  Constant.pingUrl(ip);*/
	/**
	 * 更新URL 本地线上一致
	 */
	public static final String UPDATE_URL = "http://d.tingchebao.com/update/puserhd/update.xml";
	/**
	 * 手动更新的URL
	 */
//	public static final String UPDATE_URL_HAND = "http://d.tingchebao.com/update/puserhd/update_hand.xml";
	public static String getUpdateUrlHand(){
		String url="";
		if(requestUrl.contains("/zld/")){
			url = requestUrl.replace("/zld/", "");
		}
		url+="/update/puserhd/update_hand.xml";
		Log.e("----", url);
		return url;
	}
//	
//	/*在是否用本地服务器时,默认走线上服务器*/
	public static String requestUrl = "http://s.tingchebao.com/zld/";
//	public static String requestUrl = "http://192.168.199.239/zld/";
//	public static String requestUrl = "http://180.150.188.224:8080/zld/";
//	public static String requestUrl = "http://yxiudongyeahnet.vicp.cc:50803/zld/";
	public static String serverUrl = "http://s.tingchebao.com/mserver/";
//	用于本地服务器时,获取线上的支付消息
//	public static final String mserverline = "http://s.tingchebao.com/mserver/";

	//	public static String mserverline = serverUrl;
	
	// 预上线服务器
//	public static String requestUrl = "http://180.150.188.224:8080/zld/";
//	public static String serverUrl = "http://180.150.188.224:8080/mserver/";
//	// 用于本地服务器时,获取线上的支付消息
//	public static final String mserverline = "http://180.150.188.224:8080/mserver/";

//	 haixiang本地测试
//	public static String requestUrl = "http://192.168.199.239/zld/";
//	public static String serverUrl = "http://192.168.199.239/mserver/";	
//	/*用于本地服务器时,获取线上的支付消息*/
//	public static String mserverline = "http://192.168.199.239/mserver/";

	
//	// 老姚本地测试
//	public static String requestUrl = "http://192.168.199.240/zld/";
//	public static String serverUrl = "http://192.168.199.240/mserver/";	
////	/*用于本地服务器时,获取线上的支付消息*/
//	public static String mserverline = "http://192.168.199.240/mserver/";

//	荣辉本地测试
//	public static String requestUrl = "http://192.168.199.156:8088/zld/";
//	public static String serverUrl = "http://192.168.199.156:8088/mserver/";	
//	/*用于本地服务器时,获取线上的支付消息*/
//	public static String mserverline = "http://192.168.199.156:8088/mserver/";
	
	/*本地服务器时,检测网络,Ping通本地连本地,Ping不通连线上,线上时Ping本地,通则切换到本地*/
//	public static String PING_TEST_LOCAL = "http://192.251:8080/zld/";
	
	/*登录时输入ip,则使用本地服务器,*/
	public static void requestUrl(String ip){
		requestUrl = "http://"+ip+":8080/zld/";
	}
	public static void serverUrl(String ip){
		serverUrl = "http://"+ip+":8080/mserver/";
	}
	
	public static void pingUrl(String ip){
		Log.e("shuyu", "pingUrl"+ip);
		PING_TEST_LOCAL = "http://"+ip+":8080/zld/worksiteinfo.do?comid=&action=queryworksite";
	}
	
	/*用于平板本地化,检测网络*/
	public static String PING_TEST_LOCAL = "http://s.tingchebao.com/zld/worksiteinfo.do?comid=&action=queryworksite";
//	public static String PING_TEST_LOCAL = "http://192.168.199.251:8080/zld/";
	// 老姚
//	public static String PING_TEST_LOCAL = "http://192.168.199.240/zld/";
//	public static String PING_TEST_LOCAL = "http://192.168.199.239/zld/";
	
	// 预上线服务器
//	public static String PING_TEST_LOCAL = "http://180.150.188.224:8080/zld/";
	
	//	/**更新版本*/
	//	public static final String UPDATE_URL = requestUrl;
	//	/**PING请求测试判断网络*/
//		public static final String PING_TEST = "http://192.168.199.251:8080/zld/";

	/**区分链接是线上还是本地*/
//	public static final String LINE_LOCAL = "192.168.199.251";
//	public static final String LINE_LOCAL = "192.168.199.240";
//	public static final String LINE_LOCAL = "192.168.199.239";
	
//	/**区分链接是线上还是本地*/
	public static final String LINE_LOCAL = "s.tingchebao.com";

//	/**区分链接是线上还是本地*/  预上线
//	public static final String LINE_LOCAL = "180.150.188.224:8080";

	public static final int DELETE_IMAGE = 500;
	public static long ONEDAYTAMP = 1*24*60*60*1000;// 10分 以内的毫秒

	/**进入后台*/
	public static final String INTO_BACK = "http://s.tingchebao.com/zld/";
	/**进入后台*/
//	public static final String INTO_BACK = "http://180.150.188.224:8080/zld/";
//	public static final String INTO_BACK = "http://192.168.199.251:8080/zld/";
//	public static final String INTO_BACK = "http://192.168.199.240/zld/";

	/**查询订单*/
	public static final String QUERY_ORDER = "cobp.do?action=queryorder";
	/**当前订单*/
	public static final String GET_CURRORDER = "cobp.do?action=getcurrorder";
	/**离场订单*/
	public static final String ORDER_HISTORY = "collectorrequest.do?action=orderhistory";
	/**当前订单详情*/
	public static final String CAT_ORDER = "cobp.do?action=catorder";
	/**修改大小车计费策略*/
	public static final String CHANGE_CAR_TYPE = "cobp.do?action=changecartype";
	/**修改大小车计费策略*/
//	public static final String GET_CAR_TYPE = "cobp.do?action=getcartype";
	/**收费员信息*/
	public static final String COLLECTOR_INFO = "collectorrequest.do?action=getnewincome";
	/**结算订单*/
	public static final String COMPLETE_ORDER = "nfchandle.do?action=completeorder";
	/**修改订单*/
	public static final String MODIFY_ORDER = "cobp.do?action=addcarnumber";
	/**免费订单*/
	public static final String FREE_ORDER = "collectorrequest.do?action=freeorder";
	/**生成订单*/
	public static final String MADE_ORDER = "cobp.do?action=preaddorder";
	/**抬杆动作记录*/
	public static final String LIFT_ORDER = "collectorrequest.do?action=liftrodrecord";
//	/**抬杆动作原因*/
//	public static final String LIFT_ORDER_REASON = "collectorrequest.do?action=liftrodreason";
//	/**抬杆动作图片*/
//	public static final String LIFT_ORDER_PICTURE = "collectorrequest.do?action=liftroduppic";
	/**出场时减免小时**/
	public static final String HD_DERATE = "nfchandle.do?action=hdderate";
	/**更改垃圾订单数*/
	public static final String CHANG_INVALIDORDER = "collectorrequest.do?action=invalidorders";
	/**获取车场信息*/
	public static final String COMINFO = "collectorrequest.do?action=cominfo";
//	/**获取道闸信息*/
//	public static final String CONTROLINFO = "worksiteinfo.do?action=getbrake";
//	/**获取摄像头信息*/
//	public static final String CAMERAINFO = "worksiteinfo.do?action=querycamera";
//	/**获取LED信息*/
//	public static final String LEDINFO = "worksiteinfo.do?action=queryled";
	/**获取工作站下所有摄像头和LED信息*/
	public static final String WORKINFO = "worksiteinfo.do?action=getpassinfo";
	/**强制生成订单*/
	public static final String ADD_CAR = "cobp.do?action=addorder";
	/**预支付*/
	public static final String PRE_PAY = "nfchandle.do?action=doprepayorder";
	/**下班*/
	public static final String AFTER_WORK = "collectorrequest.do?action=gooffwork";
	/**获取工作站信息*/
	public static final String QUERY_WORKSITE = "worksiteinfo.do?action=queryworksite";
	/**获取对应工作站通道信息*/
	public static final String QUERY_PASS_INFO = "worksiteinfo.do?action=querypass";
	/**获取登录信息*/
	public static final String LOGIN = "collectorlogin.do?";
	/**获取离场订单信息*/
	public static final String GET_LEAVE_MESG = "getmesg.do?";
	/**获取分享信息*/
	public static final String GET_SHARE = "getshare.do?";
	/**下载图片*/
	public static final String DOWNLOAD_IMAGE = "carpicsup.do?action=downloadpic";
	/**下载log*/
	public static final String DOWNLOAD_LOGO_IMAGE = "carpicsup.do?action=downloadlogpic";
//	/**所有月卡车牌号*/
//	public static final String MONTH_CARD_CARNUMBER = "local.do?action=synchroVip";
//	/**同步订单*/
//	public static final String SYNCHRO_ORDER = "local.do?action=synchroOrder";

	/**获取到线上支付后回调*/
	public static final String PAY_BACK = "cobp.do?action=line2local";
//	/**上传摄像头状态*/
//	public static final String UPLOAD_CAMERA_STATE = "parkinter.do?action=uploadcamerastate";
//	/**上传道闸状态*/
//	public static final String UPLOAD_BRAKE_STATE = "parkinter.do?action=uploadbrakestate";
//	/**上传LED状态*/
//	public static final String UPLOAD_LED_STATE = "parkinter.do?action=uploadledstate";

	/**图片类型入口*/
	public static final int HOME_PHOTOTYPE = 0;
	/**图片类型出口*/
	public static final int EXIT_PHOTOTYPE = 1;

	/**
	 * 消息相关常量库
	 */
	/**离场订单消息*/
	public static final int LEAVEORDER_MSG = 1;
	/**入场车辆消息*/
	public static final int PARKING_NUMS_MSG = 2;
	public static final int SHOWVIDEO_MSG = 60;
	public static final int OPENCAMERA_SUCCESS_MSG = 61;
	public static final int PICUPLOAD_FILE = 62;
	public static final int SHOWPIC_ONRIGHT_MSG = 63;
	public static final int OPENCAMERA_FAIL_MSG = 64;
	public static final int COMECAR_MSG = 65;

	public static final int KEEPALIVE = 66;
	public static final int KEEPALIVE_TIME = 67;
	public static final int HOME_DELAYED_TIME = 68;
	public static final int EXIT_DELAYED_TIME = 69;
	public static final int STOP = 70;
	public static final int NONETWORK_MSG = 80;	

	public static final int DELAY_UPLOAD = 81;	
	public static final int LIST_REFRESH = 82;	
	public static final int CLEAR_ORDER = 83;
	public static final int UPPOLE_IMAGR_SUCCESS = 84;
	public static final int UPPOLE_IMAGR_ERROR = 85;
	public static final int REFRESH_NOMONTHCAR_IMAGE = 86;
	public static final int REFRESH_NOMONTHCAR2_IMAGE = 87;
	public static final int HOME_CAR_OUTDATE_ICON = 89;
	public static final int PLAY_PULL = 88;

//	public final static int ADDORDER_SUCCESS = 10;
//	public final static int ADDORDER_ERROR = 11;
//	public final static int LED_CONN_ERROR = 12;

//	public static final int MORE_CLICK = 3;
//	public static final int CALCULATE_TIME = 4;
	public static final int KEEP_TIME = 5;
	public static final int RESTART_YES = 6;

	/*同步订单间隔时间*/
	public static final long time = 1000*60*1;

	//车辆相关常量值
	/*#define LT_UNKNOWN  0   //未知车牌
	#define LT_BLUE     1   //蓝牌小汽车
	#define LT_BLACK    2   //黑牌小汽车
	#define LT_YELLOW   3   //单排黄牌
	#define LT_YELLOW2  4   //双排黄牌（大车尾牌，农用车）
	#define LT_POLICE   5   //警车车牌
	#define LT_ARMPOL   6   //武警车牌
	#define LT_INDIVI   7   //个性化车牌
	#define LT_ARMY     8   //单排军车牌
	#define LT_ARMY2    9   //双排军车牌
	#define LT_EMBASSY  10  //使馆车牌
	#define LT_HONGKONG 11  //香港进出中国大陆车牌
	#define LT_TRACTOR  12  //农用车牌
	#define LT_COACH	13  //教练车牌
	#define LT_MACAO	14  //澳门进出中国大陆车牌
	#define LT_ARMPOL2   15 //双层武警车牌
	#define LT_ARMPOL_ZONGDUI 16  // 武警总队车牌
	#define LT_ARMPOL2_ZONGDUI 17 // 双层武警总队车牌*/
	public static final int LT_POLICE = 5;
	public static final int LT_ARMPOL = 6;
	public static final int LT_ARMY = 8;
	public static final int LT_ARMY2 = 9;
	public static final int LT_ARMPOL2 = 15;
	public static final int LT_ARMPOL_ZONGDUI = 16;
	public static final int LT_ARMPOL2_ZONGDUI = 17;

	public static final String INTENT_KEY = "intentkey";
	//车牌号的正常长度
//	public static final int CAR_PLATE_LENTH = 7;

	//保存图片文件夹路径
	public static final String FRAME_DUMP_FOLDER_PATH = Environment
			.getExternalStorageDirectory() + File.separator + "tingchebao/";
	
//	/**摄像头连接状态:成功*/
//	public static final String CAMERA_STATE_SUCCESS = Constant.sOne;
//	/**摄像头连接状态:断开*/
//	public static final String CAMERA_STATE_FAILE = Constant.sZero;

	public static final int StopVedio = 0x20001;
	public static final int StartVedio = 0x20002;
 
	public static final int SelectVedio = 0x20009;
	public static final int ConfigDeivce = 0x20010;
	public static final int DClickVedio = 0x200011;
	public static final int PlateImage = 0x200012;

	public static final String sZero = "0";
	public static final String sOne = "1";
	public static final String sTwo = "2";
	public static final String sThree = "3";
	public static final String sNine = "9";

	public static final int BerthHandlerWhat = 1219;
}
