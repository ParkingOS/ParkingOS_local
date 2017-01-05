<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>NFC管理</title>
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
<div id="nfcmanageobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var comid = ${comid};
//var parkings= eval(T.A.sendData("parking.do?action=getparkings"));
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"收费员",fieldname:"nickname",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:true},
		{fieldcnname:"停车场",fieldname:"company_name",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false,shide:true},
		{fieldcnname:"卡号",fieldname:"nfc_uuid",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false},
		{fieldcnname:"车牌号",fieldname:"car_number",fieldvalue:'',inputtype:"text",twidth:"160" ,height:"",issort:false,shide:true},
		{fieldcnname:"注册时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",issort:false,hide:true},
		{fieldcnname:"绑定时间",fieldname:"update_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",issort:false,hide:true},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":1,"value_name":"停用"}] , twidth:"100" ,height:"",issort:false,
			process:function(value,trId,colId){
				if(value==1)
					return "<font color='red'>停用</font>";
				else 
					return "正常";
			}}
	];
var rules =[{name:"nfc_uuid",type:"",url:"",requir:true,warn:"",okmsg:""}];
var _nfcmanageT = new TQTable({
	tabletitle:"NFC管理",
	ischeck:false,
	tablename:"nfcmanage_tables",
	dataUrl:"nfcmanage.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#nfcmanageobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"添加NFC",icon:"edit_add.png",onpress:function(Obj){
		T.each(_nfcmanageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"nfcmanage_add",Title:"添加NFC",Width:550,sysfun:function(tObj){
				Tform({
					formname: "nfcmanage_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"nfcmanage.do?action=create",
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("nfcmanage_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("nfcmanage_add");
							_nfcmanageT.M();
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
		T.each(_nfcmanageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"nfcmanage_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "nfcmanage_search_f",
					formObj:tObj,
					formWinId:"nfcmanage_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("nfcmanage_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_nfcmanageT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&comid="+comid+"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"速通卡用户",icon:"edit_add.png",onpress:function(Obj){
			_nfcmanageT.C({
				cpage:1,
				tabletitle:"全部推荐",
				extparam:"&action=sutong"
			})}
		
		}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_nfcmanageT.tc.tableitems,function(o,j){
			o.fieldvalue = _nfcmanageT.GD(id)[j]
		});
		Twin({Id:"nfcmanage_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "nfcmanage_edit_f",
					formObj:tObj,
					recordid:"nfcmanage_id",
					suburl:"nfcmanage.do?action=edit&id="+id,
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_nfcmanageT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("nfcmanage_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("nfcmanage_edit_"+id);
							_nfcmanageT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}});
	bts.push({name:"启用/停用",
		rule:function(id){
			var state =_nfcmanageT.GD(id,"state");
			if(state==0){//状态：可用
				this.name="停用";
			}else
				this.name="启用";
			return true
		 },
		fun:function(id){
		var id_this = id ;
		var state =_nfcmanageT.GD(id,"state");
		var conmessage="启用";
		if(state==0){//状态：可用
			conmessage="停用";
		}
		Tconfirm({Title:"确认"+conmessage+"吗",Content:"确认"+conmessage+"吗",OKFn:function(){T.A.sendData("nfcmanage.do?action=modify","post","selids="+id_this+"&state="+state,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,conmessage+"成功！",2,"");
					_nfcmanageT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_nfcmanageT.C();
</script>

</body>
</html>
