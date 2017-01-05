<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>账务管理</title>
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
<div id="moneyobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var hbonus='${hbonus}';
var htips = "打开节日发送礼包";
if(hbonus=='1'){
	htips = "关闭节日发送礼包";
}
var cmesg='${cmesg}';
var cmesgtips = "打开收费员消息";
if(cmesg=='1'){
	cmesgtips = "关闭收费员消息";
}
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",hide:true},
		{fieldcnname:"日期",fieldname:"create_time",fieldvalue:'',inputtype:"date",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"停车场",fieldname:"company_name",fieldvalue:'',inputtype:"text",twidth:"200" ,height:"",hide:true,shide:true},
		{fieldcnname:"会员车牌",fieldname:"car_number",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",hide:true,hide:true},
		{fieldcnname:"会员电话",fieldname:"mobile",fieldvalue:'',inputtype:"text",twidth:"200" ,height:"",issort:false,hide:true},
		{fieldcnname:"摘要",fieldname:"remark",fieldvalue:'',inputtype:"text",twidth:"270" ,height:"",hide:true},
		{fieldcnname:"支付类型",fieldname:"pay_type",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"账号余额"},{"value_no":1,"value_name":"支付宝"},{"value_no":2,"value_name":"微信"},{"value_no":3,"value_name":"网银"},{"value_no":4,"value_name":"余额+支付宝"},{"value_no":5,"value_name":"余额+微信"},{"value_no":6,"value_name":"余额+网银"}],twidth:"100" ,height:"",hide:true},
		{fieldcnname:"充值",fieldname:"recharge",fieldvalue:'',inputtype:"text",twidth:"100",height:"",issort:false,shide:true},
		{fieldcnname:"消费",fieldname:"consum",fieldvalue:'',inputtype:"text",twidth:"100",height:"",issort:false,shide:true},
		{fieldcnname:"提现",fieldname:"withdraw",fieldvalue:'',inputtype:"text", twidth:"100",height:"",issort:false,shide:true}
	];
var _moneyT = new TQTable({
	tabletitle:"账务管理",
	ischeck:false,
	tablename:"money_tables",
	dataUrl:"account.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#moneyobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_moneyT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"money_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "money_search_f",
					formObj:tObj,
					formWinId:"money_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("money_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_moneyT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:htips,icon:"edit_add.png",onpress:function(Obj){
		Tconfirm({
			Title:"提示信息",
			Ttype:"alert",
			Content:"警告：您确认要 <font color='red'>"+htips+"</font>吗？",
			OKFn:function(){
			T.A.sendData("account.do?action=hbonous&hbonus="+hbonus,"GET","",
				function(ret){
					if(ret=="1"){
						location = "account.do";
					}else{
						T.loadTip(1,"操作失败，请重试！",2,"")
					}
				},0,null)
			}
		});
	}}
	,
	{dname:cmesgtips,icon:"edit_add.png",onpress:function(Obj){
		Tconfirm({
			Title:"提示信息",
			Ttype:"alert",
			Content:"警告：您确认要 <font color='red'>"+cmesgtips+"</font>吗？",
			OKFn:function(){
			T.A.sendData("account.do?action=cmessage&cmesg="+cmesg,"GET","",
				function(ret){
					if(ret=="1"){
						location = "account.do";
					}else{
						T.loadTip(1,"操作失败，请重试！",2,"")
					}
				},0,null)
			}
		});
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	
	if(bts.length <= 0){return false;}
	return bts;
}
_moneyT.C();
</script>

</body>
</html>
