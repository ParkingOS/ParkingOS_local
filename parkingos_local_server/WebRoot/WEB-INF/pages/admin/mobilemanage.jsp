<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>手机管理</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?075417" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
</head>

<body>
<div id="mobilemanageobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var comid = ${comid};
var markets = getMarkets();
function getMarkets(){
	var childs = eval(T.A.sendData("getdata.do?action=markets"));
	return childs;
}
//var parkings= eval(T.A.sendData("parking.do?action=getparkings"));
/*

		 *  id bigint NOT NULL,
			  imei character varying(25), -- 手机串号
			  num character varying(15), -- 手机号码
			  mode character varying(100), -- 手机型号
			  price numeric(5,2), -- 价格
			  create_tiime bigint, -- 入库时间
			  editor character varying(50), -- 入库人
			  distru_date bigint, -- 分配时间
			  uid bigint, -- 市场专员
			  comid bigint, -- 停车场
			  uin bigint, -- 车场签收人帐号
			  money_3 numeric(5,2), -- 近三日结算金额
			  order_3 integer, -- 近三日订单数量
			  CONSTRAINT mobile_tb_pkey PRIMARY KEY (id)
  
		 
*/

var _field = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"手机串号",fieldname:"imei",fieldvalue:'',inputtype:"text", twidth:"160" ,height:"",issort:false},
		{fieldcnname:"手机号码",fieldname:"num",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"手机型号",fieldname:"mode",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"手机价格",fieldname:"price",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"入库时间",fieldname:"create_time",fieldvalue:'',inputtype:"date",twidth:"130" ,height:"",issort:false},
		{fieldcnname:"分配时间",fieldname:"distru_date",fieldvalue:'',inputtype:"date",twidth:"130" ,height:"",issort:false},
		{fieldcnname:"入库人",fieldname:"editor",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"cselect",noList:markets,target:"comid",action:"getpark",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"停车场",fieldname:"comid",fieldvalue:'',inputtype:"cselect",noList:[],target:"uin",action:"getuser",twidth:"160" ,height:"",issort:false,
			process:function(value,pid){
				return setcname(value,pid,'parkname','comid');
			}},
		{fieldcnname:"车场签收人",fieldname:"uin",fieldvalue:'',inputtype:"cselect",noList:[],action:"",twidth:"100" ,height:"",issort:false,
			process:function(value,pid){
				return setcname(value,pid,'parkername','uin');
			}},
		{fieldcnname:"近三日结算金额",fieldname:"money_3",fieldvalue:'',inputtype:"number",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"近三日订单数量",fieldname:"order_3",fieldvalue:'',inputtype:"number",twidth:"100" ,height:"",issort:false}
		
	];
var _addfield = [
		{fieldcnname:"手机型号",fieldname:"mode",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"手机串号",fieldname:"imei",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"手机号码",fieldname:"num",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"手机价格",fieldname:"price",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"入库人",fieldname:"editor",fieldvalue:'',defaultValue:'${nickname}',inputtype:"text",twidth:"160" ,height:"",issort:false,edit:false}
	];
var _editfield = [
		{fieldcnname:"手机串号",fieldname:"imei",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false,edit:false},
		{fieldcnname:"手机号码",fieldname:"num",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"手机型号",fieldname:"mode",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"cselect",noList:markets,target:"comid",action:"getpark",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"停车场",fieldname:"comid",fieldvalue:'',inputtype:"cselect",noList:[],target:"uin",action:"getuser",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"车场签收人",fieldname:"uin",fieldvalue:'',inputtype:"select",noList:[],twidth:"160" ,height:"",issort:false}
	];

var rules =[{name:"nfc_uuid",type:"",url:"",requir:true,warn:"",okmsg:""}];

var _mobilemanageT = new TQTable({
	tabletitle:"手机管理",
	ischeck:false,
	tablename:"mobilemanage_tables",
	dataUrl:"mobilemanage.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#mobilemanageobj"),
	fit:[true,true,true],
	tableitems:_field,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"添加手机",icon:"edit_add.png",onpress:function(Obj){
		T.each(_mobilemanageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"mobilemanage_add",Title:"添加手机",Width:550,sysfun:function(tObj){
				Tform({
					formname: "mobilemanage_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"mobilemanage.do?action=create",
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_addfield}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("mobilemanage_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("mobilemanage_add");
							_mobilemanageT.M();
						}else if(ret==0){
							T.loadTip(1,"添加失败！请稍候再试！",2,"");
						}else{
							T.loadTip(1,"添加失败！",2,"");
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_mobilemanageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"mobilemanage_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "mobilemanage_search_f",
					formObj:tObj,
					formWinId:"mobilemanage_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_field}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("mobilemanage_search_w");} }
					],
					SubAction:
					function(callback,formName){
						//alert(Serializ(formName));
						_mobilemanageT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		var imei = _mobilemanageT.GD(id,"imei");
		var mode = _mobilemanageT.GD(id,"mode");
		var num = _mobilemanageT.GD(id,"num");
		
		Twin({Id:"mobilemanage_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "mobilemanage_edit_f",
					formObj:tObj,
					recordid:"mobilemanage_id",
					suburl:"mobilemanage.do?action=edit&id="+id,
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_editfield}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("mobilemanage_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("mobilemanage_edit_"+id);
							_mobilemanageT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
				T("#mobilemanage_edit_f_imei").value=(imei);
				T("#mobilemanage_edit_f_mode").value=(mode);
				T("#mobilemanage_edit_f_num").value=(num);
			}
		})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
function setcname(value,pid,type,colname){
	if(value&&value!='-1'&&value!=''){
		T.A.C({
			url:"getdata.do?action=getvalue&type="+type+"&id="+value,
    		method:"GET",//POST or GET
    		param:"",//GET时为空
    		async:false,//为空时根据是否有回调函数(success)判断
    		dataType:"0",//0text,1xml,2obj
    		success:function(ret,tipObj,thirdParam){
    			if(ret){
					updateRow(pid,colname,ret);
    			}
				else
					updateRow(pid,colname,value);
			},//请求成功回调function(ret,tipObj,thirdParam) ret结果
    		failure:function(ret,tipObj,thirdParam){
				return false;
			},//请求失败回调function(null,tipObj,thirdParam) 默认提示用户<网络失败>
    		thirdParam:"",//回调函数中的第三方参数
    		tipObj:null,//相关提示父级容器(值为字符串"notip"时表示不进行相关提示)
    		waitTip:"正在获取姓名...",
    		noCover:true
		})
	}else{
		return "无"
	};
	return "<font style='color:#666'>获取中...</font>";
}

/*更新表格内容*/
function updateRow(rowid,name,value){
	//alert(value);
	if(value)
	_mobilemanageT.UCD(rowid,name,value);
}

_mobilemanageT.C();
</script>

</body>
</html>
