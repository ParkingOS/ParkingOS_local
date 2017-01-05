<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>Ibeacon管理</title>
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
<div id="ibeaconobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var parkid=${parkid};
function getcompass(){
	return eval(T.A.sendData("getdata.do?action=getcompass"));
}
function getpark(){
	return eval(T.A.sendData("getdata.do?action=getIbeaconPark"));
}
var passlist = getcompass();
var parklist = getpark();
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"Ibeacon编号",fieldname:"ibcid",inputtype:"text", twidth:"270" ,issort:false},
		{fieldcnname:"注册日期",fieldname:"reg_time",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"所在停车场",fieldname:"comid",inputtype:"cselect",noList :parklist,target:'pass',action:'getcompass', twidth:"180" ,issort:false},
		{fieldcnname:"所在通道",fieldname:"pass",inputtype:"select",noList:passlist, twidth:"180" ,issort:false},
		{fieldcnname:"major",fieldname:"major",inputtype:"text",twidth:"50" ,issort:false},
		{fieldcnname:"minor",fieldname:"minor",inputtype:"text",twidth:"50" ,issort:false},
		{fieldcnname:"状态 ",fieldname:"state",inputtype:"select", twidth:"50",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":1,"value_name":"正常"},{"value_no":0,"value_name":"停用"}] ,issort:false},
		{fieldcnname:"纬度",fieldname:"lat",inputtype:"text", twidth:"80" ,issort:false},
		{fieldcnname:"经度",fieldname:"lng",inputtype:"text", twidth:"80" ,issort:false}
		
	];
var rules =[{name:"strid",type:"ajax",url:"ibeacon.do?action=check&value=",requir:true,warn:"账号已存在！",okmsg:""}];
function viewpic(name){
	var url = 'viewpic.html?name='+name;
	Twin({Id:"ibeacon_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
var _ibeaconT = new TQTable({
	tabletitle:"Ibeacon管理",
	ischeck:false,
	tablename:"ibeacon_tables",
	dataUrl:"ibeacon.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query&parkid="+parkid,
	tableObj:T("#ibeaconobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"注册Ibeacon",icon:"edit_add.png",onpress:function(Obj){
		T.each(_ibeaconT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"ibeacon_add",Title:"添加Ibeacon",Width:550,sysfun:function(tObj){
				Tform({
					formname: "ibeacon_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"ibeacon.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("ibeacon_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("ibeacon_add");
							_ibeaconT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_ibeaconT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"ibeacon_search_w",Title:"搜索Ibeacon",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "ibeacon_search_f",
					formObj:tObj,
					formWinId:"ibeacon_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("ibeacon_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_ibeaconT.C({
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
		T.each(_ibeaconT.tc.tableitems,function(o,j){
			o.fieldvalue = _ibeaconT.GD(id)[j]
		});
		Twin({Id:"ibeacon_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "ibeacon_edit_f",
					formObj:tObj,
					recordid:"ibeacon_id",
					suburl:"ibeacon.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_ibeaconT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("ibeacon_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("ibeacon_edit_"+id);
							_ibeaconT.M()
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
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("ibeacon.do?action=delete","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_ibeaconT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_ibeaconT.C();
</script>

</body>
</html>
