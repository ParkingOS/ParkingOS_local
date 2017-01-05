<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>个人提现帐号</title>
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
<div id="useraccountobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var comid='${comid}';
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"name",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",edit:false},
		{fieldcnname:"收费员",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",edit:false},
		{fieldcnname:"帐号",fieldname:"card_number",fieldvalue:'',inputtype:"text",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"身份证",fieldname:"user_id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"帐号类型",fieldname:"atype",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"银行卡"},{"value_no":1,"value_name":"支付宝"},{"value_no":2,"value_name":"微信"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户行",fieldname:"bank_name",fieldvalue:'',inputtype:"select",noList:banks, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户地",fieldname:"area",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户支行",fieldname:"bank_pint",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"已启用"},{"value_no":1,"value_name":"已禁用"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"备注",fieldname:"note",fieldvalue:'',inputtype:"multi",twidth:"200" ,height:"",issort:false}
	];
var rules=[
		{name:"card_number",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""},
		//{name:"mobile",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""},
		{name:"name",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""}
		];
var _useraccountT = new TQTable({
	tabletitle:"提现帐号管理（个人）",
	ischeck:false,
	tablename:"useraccount_tables",
	dataUrl:"useraccount.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	quikcsearch:coutomsearch(),
	//searchitem:true,
	param:"action=query&comid="+comid,
	tableObj:T("#useraccountobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	var bus = [];
	return false;
}
function getAuthIsoperateButtons(){
	 var bts = [];
	/* bts.push({name:"",
		rule:function(id){
			var state =_useraccountT.GD(id,"state");
			if(state==1){
				this.name="启用";
			}else{
				this.name="禁用";
			}
			return true;
		},
		fun:function(id){
		var state =_useraccountT.GD(id,"state");
		var vname = _useraccountT.GD(id,"name");
		var card_number =  _useraccountT.GD(id,"card_number");
		var type = "禁用";
		if(state==1){
			type = "启用";
		}
		Tconfirm({
			Title:"提示信息",
			Ttype:"alert",
			Content:"警告：您确认要 <font color='red'>"+type+"</font> "+vname+"的帐号（"+card_number+"）吗？",
			OKFn:function(){
			T.A.sendData("useraccount.do?action=editstate&id="+id+"&state="+state,"GET","",
				function(ret){
					if(ret=="1"){
						T.loadTip(1,type+"成功！",2,"");
						_useraccountT.C();
					}else{
						T.loadTip(1,"操作失败，请重试！",2,"")
					}
				},0,null)
			}
		});
	}}); */
	bts.push({name:"编辑",
		fun:function(id){
		T.each(_useraccountT.tc.tableitems,function(o,j){
			o.fieldvalue = _useraccountT.GD(id)[j]
		});
		Twin({Id:"collector_edit_w",Title:"编辑",Width:350,sysfun:function(tObj){
			var uid = _useraccountT.GD(id,"uin");
					Tform({
						formname: "collector_f",
						formObj:tObj,
						recordid:"collector_id",
						suburl:"useraccount.do?action=editacc&from=vip&id="+id+"&uid="+uid,
						method:"POST",
						dbuttonname:["保存"],
						formAttr:[{
							formitems:[{kindname:"",kinditemts:_mediaField}]
						}],
						buttons : [//工具
							{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("collector_edit_w");} }
						],
						Coltype:2,
						Callback:
						function(f,rcd,ret,o){
							if(ret=="1"){
								T.loadTip(1,"处理成功！",2,"");
								TwinC("collector_account_edit_w");
								_useraccountT.M();
							}else{
								T.loadTip(1,"处理失败",2,o)
							}
						}
						});	
					}
				})
	}});
	if(bts.length <= 0){return false;}
	return bts; 
	//return false;
}

function coutomsearch(){
	
	var html=   "<font color='red'>个人帐号在客户端绑定</font> ";//"&nbsp;&nbsp;总计：900.00元";
	return html;
}

_useraccountT.C();
</script>

</body>
</html>
