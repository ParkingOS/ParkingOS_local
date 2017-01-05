<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>补贴和停车费统计</title>
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
<div id="allowanceanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var _mediaField = [
		{fieldcnname:"ID",fieldname:"id",inputtype:"text", twidth:"200" ,issort:false,fhide:true},
		{fieldcnname:"拉拉奖额度",fieldname:"lala",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"拉拉奖百分比",fieldname:"lala_percent",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"实收停车费(去除了停车券)",fieldname:"parking",inputtype:"text", twidth:"250",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					var tmoney =  _allowanceanlysisT.GD(trId,'ticket');
//					alert(value+","+tmoney);
					return value-tmoney;
				}else
					return value;
			}},
		{fieldcnname:"实收停车费百分比",fieldname:"parking_percent",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"车场补贴额",fieldname:"allowance",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"车场补贴额百分比",fieldname:"allowance_percent",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"停车券补贴额",fieldname:"ticket",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"停车券补贴额百分比",fieldname:"ticket_percent",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"普通券补贴额",fieldname:"tcb0ttotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"专用券补贴额",fieldname:"tcb1ttotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"打赏用券补贴额",fieldname:"rewardttotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"三折券补贴额",fieldname:"wx3ttotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"五折券补贴额",fieldname:"wx5ttotal",inputtype:"text", twidth:"100",issort:false}
	];
var _allowanceanlysisT = new TQTable({
	tabletitle:"补贴和停车费统计",
	ischeck:false,
	tablename:"allowanceanlysis_tables",
	dataUrl:"allowance.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#allowanceanlysisobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true
});

function coutomsearch(){
	var html = "时间：<input id='coutom_btime' value='' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;城市<select id='cityflag' ><option value='-1'>全部</option><option value='0'>北京</option><option value='1'>济南</option><option value='2'>青岛</option></select>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"value=' 查 询 '/>";
	return html;
}

function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	var city = T("#cityflag").value;
	var city_b = 0;
	var city_e = 659004;
	if(city == "0"){
		city_b = 110000;
		city_e = 110229;
	}else if(city == "1"){
		city_b = 370100;
		city_e = 370181;
	}else if(city == "2"){
		city_b = 370200;
		city_e = 370214;
	}
	_allowanceanlysisT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime+"&city_b="+city_b+"&city_e="+city_e
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
	T("#cityflag").value = city;
}

_allowanceanlysisT.C();
</script>
</body>
</html>
