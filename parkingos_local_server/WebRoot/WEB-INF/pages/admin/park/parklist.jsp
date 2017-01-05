<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车场管理</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?033434" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.newtree.js?1014" type="text/javascript"></script>

</head>
<body>
<div id="parkingobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
function getMarketers (){
	var markets = eval(T.A.sendData("getdata.do?action=markets"));
	return markets;
}
function getBizcircles(){
	var bizs = eval(T.A.sendData("parking.do?action=getbizs"));
	return bizs;
}
var role=${role};
var marketers=getMarketers();
var bizcircles = getBizcircles();
var states = [{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}]
var add_states = [{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}]
var etc_states=[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"Ibeacon"},{"value_no":2,"value_name":"通道照牌"},{"value_no":3,"value_name":"手机照牌"}]
var etc_add_states=[{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"Ibeacon"},{"value_no":2,"value_name":"通道照牌"},{"value_no":3,"value_name":"手机照牌"}]

var isfixed = false;
if(role==7)
	isfixed=true;
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"number", twidth:"40" ,height:"",issort:false,edit:false},
		{fieldcnname:"名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false},
		{fieldcnname:"所属车场",fieldname:"pid",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='-1'&&parseInt(value)>0){
					var local = T.A.sendData("getdata.do?action=getparkname&id="+value);
					return local;
				}else
					return "无";
			}},
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"showmap", twidth:"180" ,height:"",issort:false},
		{fieldcnname:"停车场电话",fieldname:"phone",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"更新时间",fieldname:"update_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"所属物业",fieldname:"mcompany",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"parking_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"地面"},{"value_no":1,"value_name":"地下"},{"value_no":2,"value_name":"占道"},{"value_no":3,"value_name":"地上/地下"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"付费类型",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"付费"},{"value_no":1,"value_name":"免费"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"最小价格单位",fieldname:"minprice_unit",fieldvalue:'',inputtype:"select", noList:[{"value_no":0.00,"value_name":"默认"},{"value_no":0.50,"value_name":"0.5"},{"value_no":1.00,"value_name":"1"}], twidth:"80" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"stop_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"平面排列"},{"value_no":1,"value_name":"立体排列"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"车位总数",fieldname:"parking_total",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"分享数量",fieldname:"share_number",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"历史总收入",fieldname:"total_money",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false,hide:true},
		{fieldcnname:"当前余额",fieldname:"money",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false,hide:true},
		{fieldcnname:"无市场专员的车场",fieldname:"no_marketer",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"无市场专员"}], twidth:"60" ,height:"",issort:false,fhide:true},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"select",noList:marketers, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"所在商圈",fieldname:"biz_id",fieldvalue:'',inputtype:"select",noList:bizcircles, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"客户地区",fieldname:"city",fieldvalue:"",inputtype:"select",noList:[],dataurl:"parking.do?action=localdata",edit:true,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'&&parseInt(value)>0){
					var local = T.A.sendData("parking.do?action=getlocalbycode&code="+value);
					return local;
				}else
					return value;
			}},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"已审核"},{"value_no":2,"value_name":"未审核"}], twidth:"60" ,height:"",issort:false},
		{fieldcnname:"是否校验",fieldname:"isfixed",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"未校验"},{"value_no":1,"value_name":"已校验"},{"value_no":2,"value_name":"申请校验"},{"value_no":3,"value_name":"一次未通过"},{"value_no":4,"value_name":"二次未通过"},{"value_no":5,"value_name":"三次未通过"}], twidth:"60" ,height:"",issort:false},
		{fieldcnname:"NFC",fieldname:"nfc",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"ETC",fieldname:"etc",fieldvalue:'',inputtype:"select",noList:etc_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"预定",fieldname:"book",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"室内导航",fieldname:"navi",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"支持月卡",fieldname:"monthlypay",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"夜晚停车",fieldname:"isnight",fieldvalue:'',defaultValue:'支持||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"支持"},{"value_no":1,"value_name":"不支持"}] , twidth:"60" ,height:"",issort:false},
		{fieldcnname:"电子支付",fieldname:"epay",fieldvalue:'',defaultValue:'支持||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"支持"}] , twidth:"60" ,height:"",issort:false},
		{fieldcnname:"经度",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",edit:false},
		{fieldcnname:"纬度",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",edit:false},
		{fieldcnname:"收费员在岗状态",fieldname:"is_hasparker",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不在岗"},{"value_no":1,"value_name":"在岗"}], twidth:"60" ,height:"",issort:false},
		{fieldcnname:"地图显示",fieldname:"isview",fieldvalue:'',defaultValue:'显示||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不显示"},{"value_no":1,"value_name":"显示"}] , twidth:"60" ,height:"",issort:false},
		{fieldcnname:"未结算垃圾订单数量",fieldname:"invalid_order",fieldvalue:'',inputtype:"text", twidth:"150" ,height:""},
		{fieldcnname:"区分大小车",fieldname:"car_type",fieldvalue:'',defaultValue:'不区分||0',inputtype:"select", noList:[{"value_no":0,"value_name":"不区分"},{"value_no":1,"value_name":"区分"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"是否允许免费结算订单",fieldname:"passfree",fieldvalue:'',defaultValue:'允许||0',inputtype:"select", noList:[{"value_no":0,"value_name":"允许"},{"value_no":1,"value_name":"不允许"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"申请活动",fieldname:"activity",fieldvalue:'',defaultValue:'全部||-1',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"没有活动"},{"value_no":1,"value_name":"申请活动"},{"value_no":2,"value_name":"申请通过"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"活动内容",fieldname:"activity_content",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false}
		];
var _addField = [
		{fieldcnname:"名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"showmap", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"停车场电话",fieldname:"phone",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		//{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"联系人",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"联系人手机",fieldname:"cmobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"所属物业",fieldname:"mcompany",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车场类型",fieldname:"parking_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"地面"},{"value_no":1,"value_name":"地下"},{"value_no":2,"value_name":"占道"},{"value_no":3,"value_name":"地上/地下"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"付费类型",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"付费"},{"value_no":1,"value_name":"免费"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"最小价格单位",fieldname:"minprice_unit",fieldvalue:'',inputtype:"select", noList:[{"value_no":0.00,"value_name":"默认"},{"value_no":0.50,"value_name":"0.5"},{"value_no":1.00,"value_name":"1"}], twidth:"80" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"stop_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"平面排列"},{"value_no":1,"value_name":"立体排列"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车位总数",fieldname:"parking_total",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"分享数量",fieldname:"share_number",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false,hide:true},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"select",noList:marketers, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"所在商圈",fieldname:"biz_id",fieldvalue:'',inputtype:"select",noList:bizcircles, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"客户地区",fieldname:"city",fieldvalue:"",inputtype:"select",noList:[],dataurl:"parking.do?action=localdata",edit:true},
		{fieldcnname:"NFC",fieldname:"nfc",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"ETC",fieldname:"etc",fieldvalue:'',inputtype:"select",noList:etc_add_states, twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"预定",fieldname:"book",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"室内导航",fieldname:"navi",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"支持月卡",fieldname:"monthlypay",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"夜晚停车",fieldname:"isnight",fieldvalue:'',defaultValue:'支持||0',inputtype:"select", noList:[{"value_no":0,"value_name":"支持"},{"value_no":1,"value_name":"不支持"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"电子支付",fieldname:"epay",fieldvalue:'',defaultValue:'支持||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"支持"}] , twidth:"60" ,height:"",issort:false},
		{fieldcnname:"",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",fhide:true,hide:true,shide:true},
		{fieldcnname:"",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",fhide:true,hide:true,shide:true}
	];
var rules =[
		{name:"company_name",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""}
		];
var _parkingT = new TQTable({
	tabletitle:"已审核停车场",
	ischeck:false,
	tablename:"parking_tables",
	dataUrl:"parking.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#parkingobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	var bus = [];
	if(role!=6&&role!=8)
	bus.push({dname:"注册停车场",icon:"edit_add.png",onpress:function(Obj){
		Twin({Id:"parking_add",Title:"添加停车场",Width:550,sysfun:function(tObj){
				Tform({
					formname: "parking_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"parking.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_addField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parking_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("parking_add");
							_parkingT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}});
	bus.push({dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkingT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
			if(o.fieldname=='strid'||o.fieldname=='nickname'||o.fieldname=='cmobile')
				o.shide=true;
		});
		Twin({Id:"parking_search_w",Title:"搜索停车场",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "parking_search_f",
					formObj:tObj,
					formWinId:"parking_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parking_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_parkingT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}});
	bus.push({dname:"已审核停车场",icon:"edit_add.png",onpress:function(Obj){
		//this.dname='<font color="red">已审核停车场</font>';
		_parkingT.C({cpage:1,tabletitle:"已审核停车场",
			extparam:"&action=quickquery&state=0"
		})
	
	}});
	bus.push({dname:"未审核停车场",icon:"edit_add.png",onpress:function(Obj){
		_parkingT.C({cpage:1,tabletitle:"未审核停车场",
			extparam:"&action=quickquery&state=2"
		})
	}});
	bus.push({dname:"已审核UGC停车场",icon:"edit_add.png",onpress:function(Obj){
		location = "parking.do?action=ugc&state=0";
	}});
	bus.push({dname:"未审核UGC停车场",icon:"edit_add.png",onpress:function(Obj){
		location = "parking.do?action=ugc&state=2";
	}});
	bus.push({dname:"重置非北京缓存",onpress:function(Obj){
		Tconfirm({Title:"确认重置非北京缓存吗",Content:"确认重置缓存吗",OKFn:function(){T.A.sendData("parking.do?action=initnobj","GET","",
			function deletebackfun(ret){
				T.loadTip(1,"重置了"+ret+"个非济南车场",2,"");
			}
		)}})
	}});
	/*bus.push({dname:"导入车场",onpress:function(Obj){
		Tconfirm({Title:"确认导入车场吗",Content:"确认导入车场",OKFn:function(){T.A.sendData("parking.do?action=import","GET","",
			function callbackfun(ret){
				T.loadTip(2,ret,100,"");
			}
		)}})
	}});*/
	return bus;
}
function getAuthIsoperateButtons(){
	var bts = [];
	if(role!=6&&role!=8)
	bts.push({name:"编辑",fun:function(id){
		var fixed = _parkingT.GD(id,"isfixed");
		T.each(_parkingT.tc.tableitems,function(o,j){
			if(o.fieldname=='city'){
				var code = _parkingT.GD(id)[j];
				var local = T.A.sendData("parking.do?action=getlocalbycode&code="+code);	
				o.fieldvalue = code+"||"+local;
			}else
				o.fieldvalue = _parkingT.GD(id)[j]
		});
		;
		Twin({Id:"parking_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "parking_edit_f",
					formObj:tObj,
					recordid:"parking_id",
					suburl:"parking.do?action=modify&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_parkingT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("parking_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("parking_edit_"+id);
							_parkingT.M()
						}else if(ret=="-1"){
							T.loadTip(1,"经纬度重复了",2,o);
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
		if(fixed=='1'&&role!=7){//已定位，不能修改经纬度
			T("#parking_edit_f_address").disabled=true;
			T("#parking_edit_f_address_showmap").disabled=true;
		}
	}});
if(role!=6&&role!=7&&role!=8)
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("parking.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_parkingT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	bts.push({name:"不显示",
		rule:function(id){
				var state =_parkingT.GD(id,"isview");
				if(state==1){
					this.name="不显示";
				}else{
					this.name=" &nbsp;显  示      ";
				}
				return true;
			},
		tit:"设置是否在手机客户端地图上显示",
		fun:function(id){
			var state =_parkingT.GD(id,"isview");
			var type = "显示在手机地图";
			if(state==1){
				type = "不显示在手机地图";
			}
			Tconfirm({
				Title:"提示信息",
				Ttype:"alert",
				Content:"警告：您确认要 <font color='red'>"+type+"</font> 吗？",
				OKFn:function(){
				T.A.sendData("parking.do?action=isview&id="+id+"&isview="+state,"GET","",
					function(ret){
						if(ret=="1"){
							T.loadTip(1,"设置"+type+"成功！",2,"");
							_parkingT.C();
						}else{
							T.loadTip(1,"操作失败，请重试！",2,"")
						}
					},0,null)
				}
			});
		}});
	if(role!=6&&role!=8)
	bts.push({name:"设置",fun:function(id){
		Twin({
			Id:"client_detail_"+id,
			Title:"停车场设置  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"parksetting.do?id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
		
	bts.push({name:"车位趋势",fun:function(id){
		var pname = _parkingT.GD(id,"company_name");
		Twin({
			Id:"client_detail_"+id,
			Title:"车位趋势  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"parklalaanly.do?action=parkidle&pname="+encodeURI(encodeURI(pname))+"&comid="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
	if(bts.length <= 0){return false;}
	return bts;
}
_parkingT.C();
</script>

</body>
</html>
