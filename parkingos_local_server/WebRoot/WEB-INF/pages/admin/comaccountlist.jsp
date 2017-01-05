<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>提现账号</title>
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
<div id="comaccountobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var comid='${comid}';
var type=${type};//0公司账户 1个人账户 2对公账户
var ishide =true;
if(type==2)//对公账户要显示的字段
	ishide=false;
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"公司编号",fieldname:"comid",fieldvalue:'',defaultValue:comid,inputtype:"text",twidth:"200" ,height:"",issort:false,edit:false,fhide:true},
		{fieldcnname:"姓名(与银行卡登记的一致)",fieldname:"name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:""},
		//{fieldcnname:"收费员",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"100" ,height:""},
		{fieldcnname:"账号",fieldname:"card_number",fieldvalue:'',inputtype:"text",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"账号类型",fieldname:"atype",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"银行卡"},{"value_no":1,"value_name":"支付宝"},{"value_no":2,"value_name":"微信"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户地区",fieldname:"area",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户行",fieldname:"bank_name",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户支行",fieldname:"bank_pint",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		//{fieldcnname:"所属",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"公司"},{"value_no":1,"value_name":"个人"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"已启用"},{"value_no":1,"value_name":"已禁用"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"收款人所在市",fieldname:"city",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:ishide,fhide:ishide},
		{fieldcnname:"结算方式",fieldname:"pay_type",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:ishide,fhide:ishide},
		{fieldcnname:"期望日",fieldname:"pay_date",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:ishide,fhide:ishide},
		{fieldcnname:"用途",fieldname:"use",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:ishide,fhide:ishide},
		{fieldcnname:"收方行号",fieldname:"bank_no",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:ishide,fhide:ishide},
		{fieldcnname:"备注",fieldname:"note",fieldvalue:'',inputtype:"multi",twidth:"200" ,height:"",issort:false}
	];
var rules=[
		{name:"card_number",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""},
		{name:"mobile",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""},
		{name:"bank_name",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""}
		];
var _comaccountT = new TQTable({
	tabletitle:"提现号管理（公司）",
	ischeck:false,
	tablename:"comaccount_tables",
	dataUrl:"comaccount.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	
	//searchitem:true,
	param:"action=query&comid="+comid+"&type=${type}",
	tableObj:T("#comaccountobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	var bus = [];
	bus.push({dname:"添加账号",icon:"edit_add.png",onpress:function(Obj){
		Twin({Id:"comaccount_add",Title:"添加账号",Width:550,sysfun:function(tObj){
				Tform({
					formname: "comaccount_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"comaccount.do?action=create&type=${type}",
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("comaccount_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("comaccount_add");
							_comaccountT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}});
	return bus;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"",
		rule:function(id){
			var state =_comaccountT.GD(id,"state");
			if(state==1){
				this.name="启用";
			}else{
				this.name="禁用";
			}
			return true;
		},
		fun:function(id){
		var state =_comaccountT.GD(id,"state");
		var vname = _comaccountT.GD(id,"name");
		var card_number =  _comaccountT.GD(id,"card_number");
		var type = "禁用";
		if(state==1){
			type = "启用";
		}
		Tconfirm({
			Title:"提示信息",
			Ttype:"alert",
			Content:"警告：您确认要 <font color='red'>"+type+"</font> "+vname+"的账号（"+card_number+"）吗？",
			OKFn:function(){
			T.A.sendData("comaccount.do?action=editstate&id="+id+"&state="+state,"GET","",
				function(ret){
					if(ret=="1"){
						T.loadTip(1,type+"成功！",2,"");
						_comaccountT.C();
					}else{
						T.loadTip(1,"操作失败，请重试！",2,"")
					}
				},0,null)
			}
		});
	}});
	bts.push({name:"编辑",fun:function(id){
		T.each(_comaccountT.tc.tableitems,function(o,j){
			o.fieldvalue = _comaccountT.GD(id)[j]
		});
		Twin({Id:"camera_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "camera_edit_f",
					formObj:tObj,
					recordid:"camera_id",
					suburl:"comaccount.do?action=edit&id="+id,
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_comaccountT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("camera_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("camera_edit_"+id);
							_comaccountT.M()
						}else{
							T.loadTip(1,ret,2,o)
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


_comaccountT.C();
</script>

</body>
</html>
