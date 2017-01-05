<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>车场交易记录</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>

</head>
<body>
<div id="mobiledetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}";
var etime="${etime}";
var otype='${otype}';
var parkid=${parkid};
var tip = "车场交易记录";
var _mediaField = [
		{fieldcnname:"停车日期",fieldname:"create_time",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"结算日期",fieldname:"end_time",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"停车时长",fieldname:"duration",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"停车场",fieldname:"cname",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"结算金额",fieldname:"total",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"NFC卡号",fieldname:"nfc_uuid",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"收费员",fieldname:"uid",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"车牌号",fieldname:"car_number",inputtype:"text", twidth:"100",issort:false}
	];
var _mobiledetailT = new TQTable({
	tabletitle:tip,
	ischeck:false,
	tablename:"mobiledetail_tables",
	dataUrl:"mobileanlysis.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=mobilepaydetail&parkid="+parkid+"&btime="+btime+"&etime="+etime,
	tableObj:T("#mobiledetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:false
});

function coutomsearch(){
	var html=    "时间："+btime+" 至 "+etime;
	return html;
}

_mobiledetailT.C();
</script>

</body>
</html>
