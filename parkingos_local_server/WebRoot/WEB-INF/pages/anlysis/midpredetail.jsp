<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>订单详情</title>
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
<div id="midpredetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var uin="${uin}";
var type="${type}";
var btime="${btime}"
var etime="${etime}";
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",hide:true},
		{fieldcnname:"车牌号",fieldname:"car_number",fieldvalue:'',inputtype:"text",twidth:"150" ,height:"",hide:true},
		{fieldcnname:"停车日期",fieldname:"create_time",fieldvalue:'',inputtype:"date",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"结算日期",fieldname:"end_time",fieldvalue:'',inputtype:"date",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"停车时长",fieldname:"park_time",fieldvalue:'',inputtype:"text",twidth:"150" ,height:"",hide:true},
		{fieldcnname:"支付方式",fieldname:"pay_type",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":4,"value_name":"现金"},{"value_no":5,"value_name":"银联卡"},{"value_no":6,"value_name":"商家卡"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"订单状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"未结算"},{"value_no":1,"value_name":"已结算"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"预付日期",fieldname:"pre_time",fieldvalue:'',inputtype:"date",twidth:"100" ,height:"",hide:true},
		{fieldcnname:"预支付金额",fieldname:"amount",fieldvalue:'',inputtype:"text",twidth:"150" ,height:"",hide:true},
		{fieldcnname:"减免券抵扣金额",fieldname:"umoney",fieldvalue:'',inputtype:"text",twidth:"150" ,height:"",hide:true},
		{fieldcnname:"减免券类型",fieldname:"ticket_type",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":3,"value_name":"减时券"},{"value_no":4,"value_name":"全免券"}],twidth:"100" ,height:"",hide:true},
		{fieldcnname:"减免券额度(小时)",fieldname:"ticket_money",fieldvalue:'',inputtype:"text",twidth:"150" ,height:"",hide:true,
			process:function(value,cid,id){
					if(value == "0"){
						return "";
					}else{
						return value;
					}
				}
		},
		{fieldcnname:"查看减免券图片",fieldname:"ticketid",fieldvalue:'',inputtype:"text",twidth:"150" ,height:"",hide:true,
			process:function(value,cid,id){
					if(value == ""){
						return "";
					}else{
						return "<a href=# onclick=\"viewdetail('hn','"+value+"','"+cid+"')\" style='color:blue'>查看图片</a>";
					}
				}}
	];
var _midpredetailT = new TQTable({
	tabletitle:"商户优惠券",
	ischeck:false,
	tablename:"money_tables",
	dataUrl:"midpreanlysis.do",
	iscookcol:false,
	param:"action=querydetail&uin="+uin+"&type="+type+"&btime="+btime+"&etime="+etime,
	tableObj:T("#midpredetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthIsoperateButtons(){
	var bts = [];
	if(bts.length <= 0){return false;}
	return bts;
}

function viewdetail(type,value,id){
	var light = parent.parent.document.getElementById('visitpic');
	var flag = false;
	if(light == null){
		flag = true;
		var light = parent.parent.parent.document.getElementById('visitpic');
	}
	var childNodes = light.childNodes;
	for(var i=0;i<childNodes.length;i++){
		light.removeChild(childNodes[i]);
	}
	var img1 = new Image();
	img1.src="midpreanlysis.do?action=downloadpic&shopticket_id="+value;
	light.appendChild(img1);
	if(flag){
		parent.parent.parent.document.getElementById('light').style.display='block';
		parent.parent.parent.document.getElementById('fade').style.display='block'
	}else{
		parent.parent.document.getElementById('light').style.display='block';
		parent.parent.document.getElementById('fade').style.display='block'
	}
	return null;
}
_midpredetailT.C();
</script>

</body>
</html>
