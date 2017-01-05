<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>微信支付宝充值和会员统计</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?0817" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
</head>
<body>
<div id="chargeanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var _mediaField = [
		{fieldcnname:"ID",fieldname:"id",inputtype:"text", twidth:"200" ,issort:false,fhide:true},
		{fieldcnname:"总余额",fieldname:"residuemoney",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"总充值笔数",fieldname:"tcount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"总充值金额",fieldname:"tamount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"支付宝充值笔数",fieldname:"zfbcount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"支付宝充值金额",fieldname:"zfbamount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"微信充值笔数",fieldname:"wxcount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"微信充值金额",fieldname:"wxamount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"微信公众号充值笔数",fieldname:"wxpcount",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"微信公众号充值金额",fieldname:"wxpamount",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"微信支付会员数",fieldname:"wxvip",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"微信公众号支付会员数",fieldname:"wxpvip",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"支付宝支付会员数",fieldname:"zfbvip",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"微信和支付宝支付会员数",fieldname:"bothvip",inputtype:"text", twidth:"150",issort:false}
	];
var _chargeanlysisT = new TQTable({
	tabletitle:"微信支付宝充值和会员统计",
	ischeck:false,
	tablename:"chargeanlysis_tables",
	dataUrl:"charge.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#chargeanlysisobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true
});

function coutomsearch(){
	var html = "时间：<input id='coutom_btime' value='' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"value=' 查 询 '/>";//"&nbsp;&nbsp;总计：900.00元";
	return html;
}

function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	_chargeanlysisT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
}

_chargeanlysisT.C();
</script>
</body>
</html>
