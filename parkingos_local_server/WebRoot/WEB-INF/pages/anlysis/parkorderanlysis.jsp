<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>车场订单统计</title>
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
<div id="orderanlyobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var role=${role};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var btime="${btime}"
var etime="${etime}";
var viewtype="custom";
var tip = "区间查询";
var _mediaField = [
		{fieldcnname:"收费员",fieldname:"name",inputtype:"text", twidth:"120",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewworkdetail('h','"+value+"','"+cid+"')\" style='color:blue'>"+value+"(上班详情)"+"</a>";
			}},
		{fieldcnname:"帐号",fieldname:"uid",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"日期",fieldname:"sdate",inputtype:"text", twidth:"200" ,issort:false},
		{fieldcnname:"总订单数",fieldname:"scount",inputtype:"text", twidth:"80",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('h','"+value+"','"+cid+"',0)\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"月卡订单数",fieldname:"monthcount",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"现金支付",fieldname:"pmoney",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"停车宝支付",fieldname:"pmobile",inputtype:"text", twidth:"80",issort:false},
		{fieldcnname:"免费金额",fieldname:"free",inputtype:"text", twidth:"100",issort:false},
		/*,
			process:function(value,cid,id){
				if(value==0.0){
					return value;
				}else{
					return "<a href=# onclick=\"viewdetail('h','"+value+"','"+cid+"',8)\" style='color:blue'>"+value+"</a>";
				}
			}*/
		{fieldcnname:"中央预支付",fieldname:"centerprepay",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"减免券支付",fieldname:"ticketpay",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"合计",fieldname:"total",inputtype:"text", twidth:"100",issort:false}
	];
var _orderanlyT = new TQTable({
	tabletitle:"订单统计",
	ischeck:false,
	tablename:"parkorderanlysis_tables",
	dataUrl:"orderanly.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#orderanlyobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:getAuthIsoperateButtons()
});

function coutomsearch(){
	var html=    "<input type='button' onclick='searchtoday();' value='今天'/>&nbsp;&nbsp;<input type='button' onclick='searchtoweek();' value='本周'/>&nbsp;&nbsp;<input type='button' onclick='searchlastweek();' value='上周'/>&nbsp;&nbsp;<input type='button' onclick='searchtomonth();' value='本月'/>&nbsp;&nbsp;时间：<input id='coutom_btime' value='"+btime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='"+etime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' value=' 查 询 '/>&nbsp;&nbsp;<span id='total_money'></span>";//"&nbsp;&nbsp;总计：900.00元";
	return html;
}
function searchtoday(){
	viewtype='today';
	tip = "今天统计";
	_orderanlyT.C({
		cpage:1,
		tabletitle:"今天统计",
		extparam:"&action=query&type=today"
	})
}
function searchtoweek(){
	viewtype='toweek';
	tip ="本周统计";
	_orderanlyT.C({
		cpage:1,
		tabletitle:"本周统计",
		extparam:"&action=query&type=toweek"
	})
}
function searchlastweek(){
	viewtype='lastweek';
	tip ="上周统计";
	_orderanlyT.C({
		cpage:1,
		tabletitle:"上周统计",
		extparam:"&action=query&type=lastweek"
	})
}
function searchtomonth(){
	viewtype='tomonth';
	tip = "本月统计";
	_orderanlyT.C({
		cpage:1,
		tabletitle:"本月统计",
		extparam:"&action=query&type=tomonth"
	})
}
function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	viewtype="custom";
	tip = "区间查询";
	_orderanlyT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
}

function viewdetail(type,value,id,pay_type){
	//alert(type+","+value);
	var total =_orderanlyT.GD(id,"total");
	var count = _orderanlyT.GD(id,"scount");
	var park =_orderanlyT.GD(id,"name");
	var uid = _orderanlyT.GD(id,"uid");
	var pmoney=_orderanlyT.GD(id,"pmoney");
	var pmobile=_orderanlyT.GD(id,"pmobile");
	var free=_orderanlyT.GD(id,"free");
	//alert(uid);
	Twin({
		Id:"parkorder_detail_"+id,
		Title:tip+"  --> 收费员："+park,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='parkorder_detail_'"+id+" id='parkorder_detail_'"+id+" src='orderanly.do?action=detail&otype="+viewtype+"&uid="+uid+"&btime="+btime+"&etime="+etime+"&total="+total+"&count="+count+"&pmobile="+pmobile+"&pmoney="+pmoney+"&free="+free+"&pay_type="+pay_type+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}
function viewworkdetail(type,value,id,pay_type){
	//alert(type+","+value);
	var total =_orderanlyT.GD(id,"total");
	var count = _orderanlyT.GD(id,"scount");
	var park =_orderanlyT.GD(id,"name");
	var uid = _orderanlyT.GD(id,"uid");
	var pmoney=_orderanlyT.GD(id,"pmoney");
	var pmobile=_orderanlyT.GD(id,"pmobile");
	var free=_orderanlyT.GD(id,"free");
	//alert(uid);
	Twin({
		Id:"parkorder_detail_"+id,
		Title:tip+"  --> 收费员："+park,
		Width:T.gww()-50,
		Height:T.gwh()-25,
		sysfunI:id,
		Content:"<iframe name='parkorder_detail_'"+id+" id='parkorder_detail_'"+id+" src='orderanly.do?action=work&otype="+viewtype+"&uid="+uid+"&btime="+btime+"&etime="+etime+"&total="+total+"&count="+count+"&pmobile="+pmobile+"&pmoney="+pmoney+"&free="+free+"&pay_type="+pay_type+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

function getAuthIsoperateButtons(){
	var bts = [];
	if(bts.length <= 0){return false;}
	return bts;
}
_orderanlyT.C();
</script>

</body>
</html>
