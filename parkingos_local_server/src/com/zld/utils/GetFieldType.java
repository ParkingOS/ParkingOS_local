package com.zld.utils;

public class GetFieldType {
	
	public static Integer getFieldType(String table ,String field){
		
		if(table.equals("com_info")){
			return getComInfoFieldType(field);
		}else if(table.equals("user_info")){
			return getUserInfoFieldType(field);
		}else if(table.equals("product_package_tb")){
			return getPackageFieldType(field);
		}else if(table.equals("order_tb")){
			return getOrderFieldType(field);
		}else if(table.equals("price_tb")){
			return getPriceFieldType(field);
		}else if(table.equals("money_record")){
			return getMoneyRecordFieldType(field);
		}else if(table.equals("recommend_tb")){
			return getrecomTbFieldType(field);
		}else if(table.equals("nfc_tb")){
			return getNfcTbFieldType(field);
		}else if(table.equals("withdraw")){
			return getWithDrawFieldType(field);
		}else if(table.equals("mobile_tb")){
			return getMobilieFieldType(field);
		}else if(table.equals("c_product")){
			return getCarOwerProduetFieldType(field);
		}else if(table.equals("park_account")){
			return getParkAccountFieldType(field);
		}else if(table.equals("com_praise_tb")){
			return getParkAccountFieldType(field);
		}else if(table.equals("recommend_tb")){
			return getrecommendTbFieldType(field);
		}else if(table.equals("ticket_tb")){
			return getticketTbFieldType(field);
		}else if(table.equals("black_tb")){
			return getBlackFieldType(field);
		}else if(table.equals("car_stops")){
			return getCarStopsFieldType(field);
		}else if(table.equals("carstops_price")){
			return getCarStopsPriceFieldType(field);
		}else if(table.equals("carstops_order")){
			return getCarStopsOrderFieldType(field);
		}else if(table.equals("ibeacon_tb")){
			return getCarIbeaconFieldType(field);
		}else if(table.equals("park_ticket")){
			return getParkTicketFieldType(field);
		}else if(table.equals("qr_code")){
			return getQRFieldType(field);
		}else if(table.equals("tcb_account")){
			return getTcbAccountFieldType(field);
		}else if(table.equals("park_account")){
			return getParkAccountFieldType(field);
		}else if(table.equals("user_account")){
			return getUserAccountFieldType(field);
		}else if(table.equals("puser_account")){
			return getPuserAccountFieldType(field);
		}
		return 12;
	}

	private static Integer getPuserAccountFieldType(String field) {
		if(field.equals("remark"))
			return 12;
		else if(field.equals("amount"))
			return 3;
		return 4;
	}

	private static Integer getUserAccountFieldType(String field) {
		if(field.equals("remark"))
			return 12;
		else if(field.equals("amount"))
			return 3;
		return 4;
	}

	private static Integer getTcbAccountFieldType(String field) {
		if(field.equals("remark"))
			return 12;
		else if(field.equals("amount"))
			return 3;
		return 4;
	}

	private static Integer getQRFieldType(String field) {
		return 4;
	}

	private static Integer getParkTicketFieldType(String field) {
		if(field.equals("money"))
			return 3;
		else
			return 4;
	}

	private static Integer getCarIbeaconFieldType(String field) {
		if(field.equals("lng")||field.equals("lat"))
			return 3;
		else if(field.equals("ibcid"))
			return 12;
		else
			return 4;
	}

	private static Integer getCarStopsOrderFieldType(String field) {
		if(field.equals("amount"))
			return 3;
		else if(field.equals("car_number")||field.equals("pic")||field.equals("keyno")||field.equals("car_local"))
			return 12;
		else
			return 4;
	}

	private static Integer getCarStopsPriceFieldType(String field) {
		if(field.equals("first_price")||field.equals("next_price")
				||field.equals("top_price")
				||field.equals("fav_price"))
			return 3;
		else if(field.equals("creator")||field.equals("resume"))
			return 12;
		else
			return 4;
	}

	private static Integer getCarStopsFieldType(String field) {
		if(field.equals("lng")||field.equals("lat")||field.equals("start_price")
				||field.equals("next_price")||field.equals("max_price"))
			return 3;
		else if(field.equals("name")||field.equals("resume")||field.equals("pic"))
			return 12;
		else
			return 4;
	}

	private static Integer getBlackFieldType(String field) {
		if(field.equals("remark"))
			return 12;
		else {
			return 4;
		}
	}

	private static Integer getParkPraiseFieldType(String field) {
		return 4;
	}
	
	private static Integer getParkAccountFieldType(String field) {
		if(field.indexOf("time")!=-1)
			return 93;
		else if(field.equals("amount"))
			return 3;
		else if(field.equals("remark"))
			return 12;
		else
			return 4;
	}

	private static Integer getCarOwerProduetFieldType(String field) {
		if(field.indexOf("time")!=-1)
			return 93;
		else if(field.equals("total"))
			return 3;
		else if(field.equals("name")||field.equals("address"))
			return 12;
		else 
			return 4;
	}

	private static Integer getMobilieFieldType(String field) {
		if(field.indexOf("time")!=-1||field.indexOf("date")!=-1)
			return 93;
		else if(field.equals("money_3")||field.equals("price"))
			return 3;
		else if(field.equals("imei")||field.equals("num")
				||field.equals("mode")||field.equals("editor"))
			return 12;
		return 4;
	}

	private static Integer getWithDrawFieldType(String field) {
		if(field.indexOf("time")!=-1)
			return 93;
		else if(field.equals("amount"))
			return 3;
		return 4;
	}

	private static Integer getNfcTbFieldType(String field) {
		if(field.equals("nfc_uuid"))
			return 12;
		return 4;
	}

	private static Integer getrecomTbFieldType(String field) {
			if(field.equals("openid")){
				return 12;
			}else{
				return 4;
			}
	}
	
	private static Integer getrecommendTbFieldType(String field) {
		if(field.equals("create_time")){
			return 93;
		}else 
			return 4;
	}
	
	private static Integer getticketTbFieldType(String field){
		if(field.equals("limit_day")){
			return 93;
		}else if(field.equals("money")){
			return 3;
		}else{
			return 4;
		}
	}

	private static Integer getMoneyRecordFieldType(String field) {
		if(field.equals("amount")){
			return 3;
		}else if(field.equals("create_time")){
			return 93;
		}else if(field.equals("remark") || field.equals("car_number") || field.equals("mobile"))
			return 12;
		else 
			return 4;
	}

	private static Integer getPriceFieldType(String field) {
		if(field.equals("price")){
			return 3;
		}else if(field.equals("create_time")){
			return 93;
		}else 
			return 4;
	}

	private static Integer getPackageFieldType(String field) {
		if(field.equals("valid_time")||field.equals("p_name")){
			return 12;
		}else if(field.equals("limitday")){
			return 93;
		}else if(field.equals("price")||field.equals("old_price")){
			return 3;
		}else 
			return 4;
	}

	private static Integer getOrderFieldType(String field) {
		if(field.equals("end_time")||field.equals("create_time")){
			return 93;
		}else if(field.equals("total")){
			return 3;
		}else if(field.equals("car_number")){
			return 12;
		}else if(field.equals("amount")){
			return 3;
		}else 
			return 4;
	}

	private static int getComInfoFieldType(String field){
		if(field.equals("id")
				||field.equals("parking_type")
				||field.equals("parking_total")
				||field.equals("share_number")
				||field.equals("auto_order")
				||field.equals("type")
				||field.equals("state")
				||field.equals("city")
				||field.equals("uid")
				||field.equals("biz_id")
				||field.equals("nfc")
				||field.equals("etc")
				||field.equals("book")
				||field.equals("navi")
				||field.equals("monthlypay")
				||field.equals("stop_type")
				||field.equals("epay")
				||field.equals("isnight")
				||field.equals("isfixed")
				||field.equals("is_hasparker")
				||field.equals("activity")){
			return 4;
		}else if(field.equals("longitude")||field.equals("latitude")
				||field.equals("total_money")||field.equals("money")){
			return 3;
		}else if(field.equals("create_time")||field.equals("update_time")){
			return 93;
		}else 
			return 12;
	}
	
	private static int getUserInfoFieldType(String field){
		if(field.equals("id")
				||field.equals("sex")
				||field.equals("online_flag")
				||field.equals("comid")
				||field.equals("state")
				||field.equals("auth_flag")
				||field.equals("isview")
				||field.equals("client_type")
				||field.equals("media")
				||field.equals("collector_auditor")){
			return 4;
		}else if(field.equals("balance")||field.equals("firstorderquota")||field.equals("rewardquota")||field.equals("recommendquota")){
			return 3;
		}else if(field.equals("reg_time")||field.equals("logon_time")
				||field.equals("logoff_time")){
			return 93;
		}else 
			return 12;
	}
}
