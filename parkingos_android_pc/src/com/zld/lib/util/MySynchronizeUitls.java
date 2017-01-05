//package com.zld.lib.util;
//
//import java.util.List;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxCallback;

//import com.androidquery.callback.AjaxStatus;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.zld.bean.AppInfo;
//import com.zld.lib.constant.Constant;
//import com.zld.local.db.ComInfo_tb;
//import com.zld.local.db.LocalOrderDBManager;
//import com.zld.local.db.MonthCard_tb;
//import com.zld.local.db.Price_tb;
//
//public class MySynchronizeUitls {
//	private static final String TAG = "MySynchronizeUitls";
//	/**
//	 * 访问服务器获得服务器时间。
//	 * 因操作数据库需要在子线程中运行！！
//	 * @param context
//	 */
//	//parkoffline.do?action=synchroTime
//	public static void SynchronizeTime(final Context context) {
//		String url = Constant.requestUrl+"local.do?action=synchroTime";
//		Log.e(TAG, "请求同步服务器时间的url："+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//					long duration = status.getDuration();
//					Log.e(TAG, "服务器返回的时间："+object+"网络请求时间是："+duration);
//					Long differenceTime = TimeTypeUtil.getDifferenceTime(Long.parseLong(object)+duration);
//					SharedPreferencesUtils.setParam(context, "zld_config", "linetime", differenceTime);
//					Log.i(TAG, "同步服务器时间成功---保存差值为："+differenceTime);
//				}
//			}
//		});
//	}
//
//	/**
//	 * 请求服务器同步车场价格策略；
//	 * 因操作数据库需要在子线程中运行！！
//	 * @param context
//	 */
//	public static void SynchronizePrice(final LocalOrderDBManager dao,final Context context){
//		String url = Constant.requestUrl+"local.do?action=synchroPrice&token="+AppInfo.getInstance().getToken();
//		Log.e(TAG, "请求同步价格的url："+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//					Log.e(TAG, "接收的价格表："+object);
//					JSONObject jsoninfo = null;
//					String priceinfo = null;
//					String cominfo = null;
//					try {
//						jsoninfo = new JSONObject(object);
//						priceinfo = jsoninfo.getString("price_tb");
//						cominfo = jsoninfo.getString("com_info_tb");
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					if (! TextUtils.isEmpty(priceinfo) && ! TextUtils.isEmpty(cominfo) ) {
//						Gson gson = new Gson();
//						List<Price_tb> list  = gson.fromJson(priceinfo, new TypeToken<List<Price_tb>>() {}.getType());
//						Log.e(TAG, "解析的价格表："+list.toString());
//						List<ComInfo_tb> cominfos = gson.fromJson(cominfo, new TypeToken<List<ComInfo_tb>>() {}.getType());
//						Log.e(TAG, "解析的cominfo表："+cominfos.toString());
//						//清空价格表
//						dao.clearPrice_tb();
//						dao.clearCominfo_tb();
//						dao.addMorePrice(list);
//						if (cominfos != null && cominfos.size() != 0) {
//							dao.addComInfoTb(cominfos.get(0));
//						}
//						Log.e(TAG, "同步价格表完毕：---------------!!!");
//					}
//				}
//			}
//		});
//	}
//	
//	/**
//	 * 下载月卡车牌号及到期时间
//	 * @param context
//	 */
//	public static void SynchronizeMonthCard(final LocalOrderDBManager dao,final Context context){
//		String url = Constant.requestUrl+Constant.MONTH_CARD_CARNUMBER+"&token="+AppInfo.getInstance().getToken();
//		Log.e(TAG, "请求同步月卡的url："+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//						Gson gson = new Gson();
//						List<MonthCard_tb> list  = gson.fromJson(object, new TypeToken<List<MonthCard_tb>>() {}.getType());
//						/*先清空月卡表*/
//						dao.clearMonthCard_tb();
//						if (list != null && list.size() != 0) {
//							for(int i=0;i<list.size();i++){
//							dao.addMonthCardInfoTb(list.get(i));
//						}
//						Log.e(TAG, "同步月卡表完毕：---------------!!!");
//					}
//				}
//			}
//		});
//	}
//}
