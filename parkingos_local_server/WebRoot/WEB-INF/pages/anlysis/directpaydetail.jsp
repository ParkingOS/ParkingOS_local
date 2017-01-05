<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>直付列表</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<style>
a { text-decoration:none; color:#f30; }
</style>

</head>
<body>
<div id="directdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}";
var etime="${etime}";
var parkid="${parkid}";

	var states = [ {
		"value_no" : -1,
		"value_name" : "其他"
	}, {
		"value_no" : 0,
		"value_name" : "余额"
	}, {
		"value_no" : 1,
		"value_name" : "支付宝"
	}, {
		"value_no" : 2,
		"value_name" : "微信"
	}, {
		"value_no" : 3,
		"value_name" : "网银"
	}, {
		"value_no" : 4,
		"value_name" : "余额+支付宝"
	}, {
		"value_no" : 5,
		"value_name" : "余额+微信"
	}, {
		"value_no" : 6,
		"value_name" : "余额+网银"
	}, {
		"value_no" : 7,
		"value_name" : "停车宝充值"
	}, {
		"value_no" : 8,
		"value_name" : "停车宝奖励"
	}, {
		"value_no" : 9,
		"value_name" : "微信公众号"
	}, {
		"value_no" : 10,
		"value_name" : "微信公众号+余额"
	}, {
		"value_no" : 11,
		"value_name" : "微信打折券"
	}, {
		"value_no" : 12,
		"value_name" : "预支付返款"
	} ];
	var _mediaField = [ {
		fieldcnname : "ID",
		fieldname : "id",
		inputtype : "text",
		twidth : "150",
		issort : false,
		fhide : true
	}, {
		fieldcnname : "收费员",
		fieldname : "nickname",
		inputtype : "text",
		twidth : "100",
		issort : false
	}, {
		fieldcnname : "车主编号",
		fieldname : "uin",
		inputtype : "text",
		twidth : "100",
		issort : false
	}, {
		fieldcnname : "车牌号",
		fieldname : "car_number",
		inputtype : "text",
		twidth : "100",
		issort : false
	}, {
		fieldcnname : "车主手机号",
		fieldname : "mobile",
		inputtype : "text",
		twidth : "100",
		issort : false
	}, {
		fieldcnname : "金额",
		fieldname : "amount",
		inputtype : "text",
		twidth : "150",
		issort : false
	}, {
		fieldcnname : "支付时间",
		fieldname : "create_time",
		inputtype : "text",
		twidth : "200",
		issort : false
	}, {
		fieldcnname : "支付类型",
		fieldname : "pay_type",
		fieldvalue : '',
		inputtype : "select",
		noList : states,
		twidth : "200",
		height : "",
		issort : false
	}, ];
	var _directdetailT = new TQTable({
		tabletitle : "直付列表",
		ischeck : false,
		tablename : "directdetail_tables",
		dataUrl : "mobileanlysis.do",
		iscookcol : false,
		buttons : false,
		param : "action=directpaydetail&btime=" + btime + "&etime=" + etime
				+ "&parkid=" + parkid,
		tableObj : T("#directdetailobj"),
		fit : [ true, true, true ],
		tableitems : _mediaField,
		allowpage : false,
		isoperate : false
	});

	_directdetailT.C();
</script>

</body>
</html>
