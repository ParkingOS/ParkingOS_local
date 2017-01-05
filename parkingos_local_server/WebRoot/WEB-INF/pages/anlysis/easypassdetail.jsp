<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>速通卡用户</title>
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
<div id="easypassdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}";
var etime="${etime}";
var total = '${total}';
var parkid=${parkid};
var tip = "速通卡用户";
var _mediaField = [
		{fieldcnname:"编号",fieldname:"comid",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"车场名称",fieldname:"company_name",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"收费员",fieldname:"nickname",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"卡号",fieldname:"nfc_uuid",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"车牌号",fieldname:"car_number",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"注册时间",fieldname:"create_time",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"绑定时间",fieldname:"update_time",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":1,"value_name":"停用"}] , twidth:"100" ,height:"",issort:false,
			process:function(value,trId,colId){
				if(value==1)
					return "<font color='red'>停用</font>";
				else 
					return "正常";
			}}
	];
var _easypassdetailT = new TQTable({
	tabletitle:tip,
	ischeck:false,
	tablename:"easypassdetail_tables",
	dataUrl:"easypass.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=easypassdetail&parkid="+parkid+"&btime="+btime+"&etime="+etime,
	tableObj:T("#easypassdetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:false
});

function coutomsearch(){
	var html=    "时间："+btime+" 至 "+etime+"，合计：<font color='red'>"+total+"</font> 个";
	return html;
}

_easypassdetailT.C();
</script>

</body>
</html>
