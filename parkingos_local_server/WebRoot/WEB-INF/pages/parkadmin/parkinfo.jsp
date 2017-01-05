<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>修改停车场</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
</head>
<body>
<script src="js/tq.js?08137" type="text/javascript">//基本</script>
<script src="js/tq.public.js?08031" type="text/javascript">//公共</script>
<script src="js/tq.window.js?008136" type="text/javascript">//弹窗</script>
<script src="js/tq.form.js?08301" type="text/javascript">//表单</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>

<div id="alllayout">
	<div style="width:100%;float:left;height:60px;border-bottom:1px solid #ccc" id="top"></div>
	<div style="width:100%;float:left;">
    	<div id="right" style="width:auto;border-left:1px solid #ccc;float:left"></div>
	</div>
</div>	
<div id="loadtip" style="display:none;"></div>
<div id="cover" style="display:none;"></div>
</body>
<script type="text/javascript">
//取字段
var add_states = [{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}];
var etc_states=[{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"Ibeacon"},{"value_no":2,"value_name":"通道照牌"},{"value_no":3,"value_name":"手机照牌"}]
var Obj = document.getElementById("alllayout");
var topO = document.getElementById("top");
var rightO = document.getElementById("right");

rightO.style.width = T.gww()  + "px";
rightO.style.height = T.gwh() - 50 + "px";

T.bind(window,"resize",function(){
    rightO.style.width = T.gww() + "px";
    rightO.style.height = T.gwh() - 50 + "px"
})


var fields = [
		{fieldcnname:"车场编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false,edit:false},
		{fieldcnname:"名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"所属物业",fieldname:"property",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车场类型",fieldname:"parking_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"地面"},{"value_no":1,"value_name":"地下"},{"value_no":2,"value_name":"占道"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"付费类型",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"付费"},{"value_no":1,"value_name":"免费"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"stop_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"平面排列"},{"value_no":1,"value_name":"立体排列"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车位总数",fieldname:"parking_total",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"分享车位数",fieldname:"share_number",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"未结算的垃圾订单数量",fieldname:"invalid_order",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"区分大小车",fieldname:"car_type",fieldvalue:'',defaultValue:'不区分||0',inputtype:"select", noList:[{"value_no":0,"value_name":"不区分"},{"value_no":1,"value_name":"区分"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"免费结算订单",fieldname:"passfree",fieldvalue:'',defaultValue:'允许||0',inputtype:"select", noList:[{"value_no":0,"value_name":"允许"},{"value_no":1,"value_name":"不允许"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"HD结算按钮",fieldname:"ishidehdbutton",fieldvalue:'',defaultValue:'显示||0',inputtype:"select", noList:[{"value_no":0,"value_name":"显示"},{"value_no":1,"value_name":"隐藏"}] , twidth:"100" ,height:"",issort:false},
		//13
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"showmap", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"经度",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",edit:false},
		{fieldcnname:"纬度",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",edit:false},
		{fieldcnname:"是否已定位",fieldname:"isfixed",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}], twidth:"200" ,height:"",edit:false},
		//17
		{fieldcnname:"电话",fieldname:"phone",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		//{fieldcnname:"联系人",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		//{fieldcnname:"联系人手机",fieldname:"cmobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		//19
		{fieldcnname:"NFC",fieldname:"nfc",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"ETC",fieldname:"etc",fieldvalue:'',inputtype:"select",noList:etc_states, twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"预定",fieldname:"book",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"室内导航",fieldname:"navi",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"支持月卡",fieldname:"monthlypay",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"夜晚停车",fieldname:"isnight",fieldvalue:'',defaultValue:'支持||0',inputtype:"select", noList:[{"value_no":0,"value_name":"支持"},{"value_no":1,"value_name":"不支持"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"电子支付",fieldname:"epay",fieldvalue:'',defaultValue:'支持||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"支持"}] , twidth:"60" ,height:"",issort:false},
		{fieldcnname:"优先识别省份",fieldname:"firstprovince",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false}
		//24
		//{fieldcnname:"历史总收入",fieldname:"total_money",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",edit:false},
		//{fieldcnname:"账号余额",fieldname:"money",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",edit:false}
		
	];
var comid="";
var maxmoney = 0;
var total=0;
var cominfo= eval('${cominfo}');
for(var i=0;i<cominfo.length;i++){
		if(cominfo[i].name=="id"){
			comid =cominfo[i].value;
		}
		if(cominfo[i].name=="money"){
			maxmoney =cominfo[i].value;
		}
		if(cominfo[i].name=="total_money"){
			total =cominfo[i].value;
		}
}
var bHtml = "<div style='margin-top:20px;margin-buttom:20px;margin-left:29px;width:595px;overflow:hidden;'>";
	bHtml += "编辑账户信息    &nbsp;&nbsp;&nbsp;&nbsp;历史总收入:<font color='red'>"+total+"</font>，账号当前余额：<font color='red'>"+maxmoney+"</font>，账号可提现余额：<font color='red' size=3>"+maxmoney+"</font>";
	bHtml += "</div>";
topO.innerHTML=bHtml;

function getEditFields(){
	var e_f = [];
	for(var j=0;j<fields.length;j++){
		for(var i=0;i<cominfo.length;i++){
			if(cominfo[i].name==fields[j].fieldname){
				fields[j].fieldvalue=cominfo[i].value;
				e_f.push(fields[j]);
				break;
			}
			if(fields[j].inputtype=='select')
				fields[j].width=200;
		}
	}
	return e_f;
}

function getFields(){
	var fs = getEditFields();
	var mfs = [
		{kindname:"基本信息",kinditemts:fs.slice(0,13)},
		{kindname:"位置信息",kinditemts:fs.slice(13,17)},
		{kindname:"联系方式",kinditemts:fs.slice(17,19)},
		{kindname:"服务项目",kinditemts:fs.slice(19,26)},
		{kindname:"识别参数",kinditemts:fs.slice(26,27)}
		//{kindname:"账户信息",kinditemts:fs.slice(21)}
		];
	return mfs;
}



var accountForm =
new TQForm({
	formname: "opconfirm",
	formObj:rightO,
	suburl:"parkinfo.do?action=edit&id="+comid,
	method:"POST",
	//dbbuttons:[true,false],
	buttons:getTopButtons(),
	Callback:function(f,r,c,o){
		if(c=='1'){
			T.loadTip(1,"修改成功！",3,null);
		}else
			T.loadTip(1,"操作失败！",3,null);
	},
	formAttr:[{
		formitems:getFields()
	}]
});
accountForm.C();
var _mField = [{fieldcnname:"请输入金额(最大:"+maxmoney+")",fieldname:"money",defaultValue:(parseInt(maxmoney)),inputtype:"number",width:200}];

function getTopButtons(){
	var bus = [];
	if(maxmoney>=100)
	bus.push({dname:"提现申请",icon:"edit_add.png",onpress:function(Obj){
		Twin({Id:"user_account_add",Title:"提现申请",Width:280,Height:190,sysfun:function(tObj){
				Tform({
					formname: "user_account_edit_f",
					formObj:tObj,
					recordid:"id",
					Coltype:2,
					nmCls:"r",
					//dbbuttons:[true,false],
					suburl:"parkinfo.do?action=withdraw",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消提现",icon:"cancel.gif", onpress:function(){TwinC("user_account_add");} }
					],
					Callback:function(f,r,c,o){
						if(c=='1'){
							accountForm.M();
							T.loadTip(1,"提现申请成功！",3,null);
							TwinC("user_account_add");
							location.href="parkinfo.do";
						}else if(c=='-1'){
							T.loadTip(1,"您还未绑定支付账号，请联系真来电公司为您绑定账号！",3,null);
							TwinC("user_account_add");
						}else
							T.loadTip(1,"操作失败！",3,null);
					}
				});	
			}
		})
	
	}});
	return bus;
}

var isfixed = T("#opconfirm_isfixed").value;
if(isfixed==1){
	T("#opconfirm_address").disabled=true;
	T("#opconfirm_address_showmap").disabled=true;
}

</script>


</html>
<script type="text/javascript">
T.maskTip(0,"","");//加载结束
</script>