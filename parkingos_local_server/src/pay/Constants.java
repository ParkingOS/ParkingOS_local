package pay;

public class Constants {
	
	
	
	
	public static  String Domain = "http://192.168.199.251/zld";
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static  String WXPUBLIC_APPID = "no";
	public static  String WXPUBLIC_SECRET = "no";
	public static  String WXPUBLIC_REDIRECTURL = "no";
	
	public static  String WXPUBLIC_S_DOMAIN = "no";
	
	public static final String WXPUBLIC_MCH_ID = "no";
	public static final String WXPUBLIC_APPKEY = "no";
	//获取access_token
	public static String WXPUBLIC_GETTOKEN_URL = "no"
			+ WXPUBLIC_APPID + "&secret=" + WXPUBLIC_SECRET;
	//统一支付接口
	public static String WXPUBLIC_UNIFIEDORDER = "no";
	//通知地址
	public static String WXPUBLIC_NOTIFY_URL = "no";
	
	//退款地址
	public static String WXPUBLIC_BACK_URL = "no";
	
	
	public static String WXPUBLIC_SUCCESS_NOTIFYMSG_ID = "no";//订单支付成功
	
	public static String WXPUBLIC_FAIL_NOTIFYMSG_ID = "no";//订单支付失败
	
	public static String WXPUBLIC_BONUS_NOTIFYMSG_ID = "no";//打赏通知
	//未付款订单通知
	public static String WXPUBLIC_ORDER_NOTIFYMSG_ID = "no";
	
	public static String WXPUBLIC_BACK_NOTIFYMSG_ID = "no";//退款
	
	public static String WXPUBLIC_TICKET_ID = "no";//获得代金券通知
	
	public static String WXPUBLIC_AUDITRESULT_ID = "no";//审核结果通知
	
	public static String WXPUBLIC_FLYGMAMEMESG_ID = "no";//名片交换通知，打灰机加好机友
	
	public static String WXPUBLIC_LEAVE_MESG_ID = "no";//留言
	
	public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
}
