<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>中央预支付</title>
<link href="css/zTreeStyle1.css" rel="stylesheet" type="text/css">
<link href="css/demo.css" rel="stylesheet" type="text/css">
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
<script src="js/jquery.js" type="text/javascript"></script>
<script src="js/jquery.ztree.core-3.5.js" type="text/javascript"></script>
</head>
<body>
<div id="midpreanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var role=${role};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var btime="${btime}"
var etime="${etime}";
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"收费员",fieldname:"nickname",inputtype:"text", twidth:"150",issort:false,
			process:function(value,cid,id){
						return "<a href=# onclick=\"work('"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
					}},
		{fieldcnname:"现金订单数",fieldname:"cashcount",inputtype:"text", twidth:"150",issort:false,
			process:function(value,cid,id){
					return "<a href=# onclick=\"detail(4,'"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
				}},
		{fieldcnname:"现金总额",fieldname:"cashamount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"银联卡订单数",fieldname:"upaycount",inputtype:"text", twidth:"150",issort:false,
			process:function(value,cid,id){
					return "<a href=# onclick=\"detail(5,'"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
				}},
		{fieldcnname:"银联卡总额",fieldname:"upayamount",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"商家卡订单数",fieldname:"cardcount",inputtype:"text", twidth:"150",issort:false,
			process:function(value,cid,id){
					return "<a href=# onclick=\"detail(6,'"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
				}},
		{fieldcnname:"商家卡总额",fieldname:"cardamount",inputtype:"text", twidth:"100",issort:false}
	];
var _midpreanlysisT = new TQTable({
	tabletitle:"中央预支付",
	ischeck:false,
	tablename:"midpreanlysis_tables",
	dataUrl:"midpreanlysis.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#midpreanlysisobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true
});

function coutomsearch(){
	var html=    "&nbsp;&nbsp;时间：<input id='coutom_btime' value='"+btime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='"+etime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"value=' 查 询 '/>";//"&nbsp;&nbsp;总计：900.00元";
	return html;
}

function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	_midpreanlysisT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
}


function detail(type,value,id){
	var park =_midpreanlysisT.GD(id,"nickname");
	var tip = "中央预支付订单记录";
	Twin({
		Id:"midpre_detail_"+id,
		Title:tip+"  --> 收费员："+park,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='midpre_detail_'"+id+" id='midpre_detail_'"+id+" src='midpreanlysis.do?action=detail&uin="+id+"&btime="+btime+"&etime="+etime+"&type="+type+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

function work(value,id){
	var uid =_midpreanlysisT.GD(id,"id");
	var nickname =_midpreanlysisT.GD(id,"nickname");
	var tip = "中央预支付班次记录";
	Twin({
		Id:"midpre_detail_"+id,
		Title:tip+"  --> 收费员："+nickname,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='midpre_detail_'"+id+" id='midpre_detail_'"+id+" src='midpreanlysis.do?action=work&uin="+uid+"&btime="+btime+"&etime="+etime+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

_midpreanlysisT.C();
</script>
</body>
</html>
