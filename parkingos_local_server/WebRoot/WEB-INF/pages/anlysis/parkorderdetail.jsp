<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>NFC记录</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>

</head>
<body>
<div id="nfcdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var role=${role};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var btime="${btime}";
var etime="${etime}";
var otype='${otype}';
var total = '${total}';
var pmoney = '${pmoney}';
var pmobile = '${pmobile}';
var pay_type = "${pay_type}";
var uid=${uid};
var count=${count};
var amount;
var sum;
//0:帐户支付,1:现金支付,2:手机支付,3:月卡,4中央预支付现金,5中央预支付银联卡,5中央预支付商家卡
var payType=[{'value_no':0,value_name:'帐户支付'},{'value_no':1,value_name:'现金'},{'value_no':2,value_name:'手机支付'},{'value_no':3,value_name:'月卡'},{'value_no':4,value_name:'中央预支付现金'},{'value_no':5,value_name:'中央预支付银联卡'},{'value_no':6,value_name:'中央预支付商家卡'},{'value_no':8,value_name:'免费'}];
var tip = "订单详情";
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"停车日期",fieldname:"create_time",inputtype:"text", twidth:"200" ,issort:false},
		{fieldcnname:"结算日期",fieldname:"end_time",inputtype:"text", twidth:"200" ,issort:false},
		{fieldcnname:"结算金额",fieldname:"total",inputtype:"text", twidth:"100",issort:false,
		process:function(value,cid,id){
			sum = value
			return value;
		}},
		{fieldcnname:"现金支付",fieldname:"amount",inputtype:"text", twidth:"100" ,issort:false,
			process:function(value,cid,id){
				if(value>0){
					amount = value;
					return value;
				}else{
					amount = 0.00;
					return 0.00;
				}
				
				
			}},
		{fieldcnname:"中央预支付",fieldname:"center",inputtype:"text", twidth:"100" ,issort:false},
			/*process:function(value,cid,id){
				if(amount>0){
					return sum-amount;
				}else{
					return 0.00;
				}
				
			}},*/
		{fieldcnname:"减免券支付",fieldname:"bmoney",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"停车时长",fieldname:"duration",inputtype:"text", twidth:"200" ,issort:false},
		{fieldcnname:"支付方式",fieldname:"pay_type",inputtype:"select", noList:payType,twidth:"100",issort:false},
		
		{fieldcnname:"NFC卡号",fieldname:"nfc_uuid",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"车牌号",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false},
		{fieldcnname:"查看车辆图片",fieldname:"id",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hn','"+value+"','"+cid+"')\" style='color:blue'>查看车辆图片</a>";
			}}
	];
var _nfcdetailT = new TQTable({
	tabletitle:tip,
	ischeck:false,
	tablename:"nfcdetail_tables",
	dataUrl:"orderanly.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=orderdetail&uid="+uid+"&btime="+btime+"&etime="+etime+"&otype="+otype+"&count="+count+"&pay_type="+pay_type,
	tableObj:T("#nfcdetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:false
});

function coutomsearch(){
	var tip = "时间："+btime+" 至 "+etime;
	if(otype=='today')
	 tip="今日订单";
	else if(otype=='toweek')
	 tip="本周订单";
	else if(otype=='lastweek')
	 tip="上周订单";
	else if(otype=='tomonth')
	 tip="本月订单";
	if(pay_type==7){
		var html=   tip+" ，合计免费：<font color='red'>"+total+"</font> 元 ";//"&nbsp;&nbsp;合计免费：900.00元";
	}else{
		var html=   tip+" ，合计：<font color='red'>"+total+"</font> 元，其中现金支付 ：<font color='red'>"+pmoney+"</font>元，手机支付 ：<font color='red'>"+pmobile+"</font>元，共<font color='red'> "+count+" </font>条 ";//"&nbsp;&nbsp;总计：900.00元";
	}
	return html;
}

function viewdetail(type,value,id){
	var car_number =_nfcdetailT.GD(id,"car_number");
	var tip = "车辆图片";
	Twin({
		Id:"carpics_detail_"+id,
		Title:tip+"  --> 车牌："+car_number,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='carpics_detail_'"+id+" id='carpics_detail_'"+id+" src='order.do?action=carpics&orderid="+id+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

_nfcdetailT.C();
</script>

</body>
</html>
