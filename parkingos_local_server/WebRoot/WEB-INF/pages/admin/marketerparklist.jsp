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
<script src="js/tq.form.js?5555" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.newtree.js?1014" type="text/javascript"></script>

</head>
<body>
<div id="marketerparkobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
function getMarketers (){
	var markets = eval(T.A.sendData("marketerpark.do?action=getmarketers"));
	return markets;
}
function getBizcircles(){
	var bizs = eval(T.A.sendData("marketerpark.do?action=getbizs"));
	return bizs;
}
var role=${role};
var marketers=getMarketers();
var bizcircles = getBizcircles();
var states = [{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}]
var add_states = [{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}]

var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"40" ,height:"",issort:false,edit:false},
		{fieldcnname:"名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false},
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"showmap", twidth:"180" ,height:"",issort:false},
		{fieldcnname:"电话",fieldname:"phone",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"联系人",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"联系人手机",fieldname:"cmobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"更新时间",fieldname:"update_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"所属物业",fieldname:"property",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"park_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"地面"},{"value_no":1,"value_name":"地下"},{"value_no":2,"value_name":"占道"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"付费类型",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"付费"},{"value_no":1,"value_name":"免费"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"stop_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"平面排列"},{"value_no":1,"value_name":"立体排列"}],twidth:"60" ,height:"",issort:false},
		{fieldcnname:"车位总数",fieldname:"parking_total",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"分享数量",fieldname:"share_number",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"总金额",fieldname:"total_money",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false,hide:true},
		{fieldcnname:"当前余额",fieldname:"money",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false,hide:true},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"select",noList:marketers, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"所在商圈",fieldname:"biz_id",fieldvalue:'',inputtype:"select",noList:bizcircles, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"客户地区",fieldname:"city",fieldvalue:"",inputtype:"select",noList:[],dataurl:"marketerpark.do?action=localdata",edit:true,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'&&parseInt(value)>0){
					var local = T.A.sendData("marketerpark.do?action=getlocalbycode&code="+value);
					return local;
				}else
					return value;
			}},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"已审核"},{"value_no":2,"value_name":"未审核"}], twidth:"60" ,height:"",issort:false},
		{fieldcnname:"nfc",fieldname:"nfc",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"etc",fieldname:"etc",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"预定",fieldname:"book",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"室内导航",fieldname:"navi",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"支持月卡",fieldname:"monthlypay",fieldvalue:'',inputtype:"select",noList:states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",fhide:true,hide:true,shide:true},
		{fieldcnname:"",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",fhide:true,hide:true,shide:true}
	];
var _addField = [
		{fieldcnname:"名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"showmap", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"电话",fieldname:"phone",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"所属物业",fieldname:"property",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车场类型",fieldname:"marketerpark_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"地面"},{"value_no":1,"value_name":"地下"},{"value_no":2,"value_name":"占道"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"付费类型",fieldname:"type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"付费"},{"value_no":1,"value_name":"免费"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"stop_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"平面排列"},{"value_no":1,"value_name":"立体排列"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车位总数",fieldname:"marketerpark_total",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"分享数量",fieldname:"share_number",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false,hide:true},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"select",noList:marketers, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"所在商圈",fieldname:"biz_id",fieldvalue:'',inputtype:"select",noList:bizcircles, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"客户地区",fieldname:"city",fieldvalue:"",inputtype:"select",noList:[],dataurl:"marketerpark.do?action=localdata",edit:true},
		{fieldcnname:"nfc",fieldname:"nfc",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"etc",fieldname:"etc",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"预定",fieldname:"book",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"室内导航",fieldname:"navi",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"支持月卡",fieldname:"monthlypay",fieldvalue:'',inputtype:"select",noList:add_states, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",fhide:true,hide:true,shide:true},
		{fieldcnname:"",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",fhide:true,hide:true,shide:true}
	];
var rules =[
		{name:"company_name",type:"",url:"",requir:true,warn:"不能为空!",okmsg:""}
		];
var _marketerparkT = new TQTable({
	tabletitle:"已审核停车场",
	ischeck:false,
	tablename:"marketerpark_tables",
	dataUrl:"marketerpark.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#marketerparkobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"注册停车场",icon:"edit_add.png",onpress:function(Obj){
		Twin({Id:"marketerpark_add",Title:"添加停车场",Width:550,sysfun:function(tObj){
				Tform({
					formname: "marketerpark_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"marketerpark.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_addField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("marketerpark_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("marketerpark_add");
							_marketerparkT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_marketerparkT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
			if(o.fieldname=='strid'||o.fieldname=='nickname'||o.fieldname=='cmobile')
				o.shide=true;
		});
		Twin({Id:"marketerpark_search_w",Title:"搜索停车场",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "marketerpark_search_f",
					formObj:tObj,
					formWinId:"marketerpark_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("marketerpark_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_marketerparkT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"已审核停车场",icon:"edit_add.png",onpress:function(Obj){
		//this.dname='<font color="red">已审核停车场</font>';
		_marketerparkT.C({cpage:1,tabletitle:"已审核停车场",
			extparam:"&action=quickquery&state=0"
		})
	
	}},
	{dname:"未审核停车场",icon:"edit_add.png",onpress:function(Obj){
		_marketerparkT.C({cpage:1,tabletitle:"未审核停车场",
			extparam:"&action=quickquery&state=2"
		})
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_marketerparkT.tc.tableitems,function(o,j){
			if(o.fieldname=='city'){
				var code = _marketerparkT.GD(id)[j];
				var local = T.A.sendData("marketerpark.do?action=getlocalbycode&code="+code);	
				o.fieldvalue = code+"||"+local;
			}else
				o.fieldvalue = _marketerparkT.GD(id)[j]
		});
		Twin({Id:"marketerpark_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "marketerpark_edit_f",
					formObj:tObj,
					recordid:"marketerpark_id",
					suburl:"marketerpark.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_marketerparkT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("marketerpark_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("marketerpark_edit_"+id);
							_marketerparkT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}});

	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("marketerpark.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_marketerparkT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	bts.push({name:"设置",fun:function(id){
		Twin({
			Id:"client_detail_"+id,
			Title:"停车场设置  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"parksetting.do?id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
	if(bts.length <= 0){return false;}
	return bts;
}
_marketerparkT.C();
</script>

</body>
</html>
