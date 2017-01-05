<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>二维码管理</title>
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
<div id="qrmanageobj" style="width:100%;height:100%;margin:0px;"></div>
<iframe src="" id ="exportiframe" frameborder="0" style="width:0px;height:0px;"></iframe>
<script language="javascript">
var role=${role};
function getworksite(){
	return eval(T.A.sendData("getdata.do?action=getworksite"));
}
var worksites = getworksite();
function getpark(){
	return eval(T.A.sendData("getdata.do?action=getWorksitePark"));
}
var parklist = getpark();
var worksites = getworksite();
var typelist = [{"value_no":-1,"value_name":"全部"},{"value_no":1,"value_name":"收费员"},{"value_no":0,"value_name":"NFC"},{"value_no":2,"value_name":"车位二维码"},{"value_no":3,"value_name":"泊车员"},{"value_no":4,"value_name":"车场二维码"}];
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"二维码",fieldname:"code",inputtype:"text", twidth:"270" ,issort:false},
		{fieldcnname:"注册日期",fieldname:"ctime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"所在停车场",fieldname:"comid",inputtype:"cselect",noList :parklist,target:'wid',action:'getworksite', twidth:"180" ,issort:false},
		{fieldcnname:"所在工作站",fieldname:"wid",inputtype:"select",noList:worksites, twidth:"80" ,issort:false},
		{fieldcnname:"收费员",fieldname:"uid",inputtype:"text", twidth:"180" ,issort:false},
		{fieldcnname:"状态 ",fieldname:"state",inputtype:"select", twidth:"50",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":1,"value_name":"停用"},{"value_no":0,"value_name":"正常"}] ,issort:false},
		{fieldcnname:"类型",fieldname:"type",inputtype:"select", twidth:"150",noList:typelist ,issort:false},
		{fieldcnname:"是否使用",fieldname:"isuse",inputtype:"select",noList:[{"value_no":"1","value_name":"已使用"},{"value_no":"0","value_name":"未使用"}]}
	];
var _qrmanageT = new TQTable({
	tabletitle:"二维码管理",
	ischeck:false,
	tablename:"qrmanage_tables",
	dataUrl:"qrmanage.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#qrmanageobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"注册二维码",icon:"edit_add.png",onpress:function(Obj){
		T.each(_qrmanageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"qrmanage_add_w",Title:"添加二维码",Width:550,sysfun:function(tObj){
				var fields = _mediaField;
				if(fields.length<10)
					fields.push({fieldcnname:"数量",fieldname:"count",inputtype:"text",twidth:"80"});
				Tform({
					formname: "qrmanage_addt_f",
					formObj:tObj,
					recordid:"id",
					suburl:"qrmanage.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:fields}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("qrmanage_add_w");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(parseInt(ret)>1){
							T.loadTip(1,"成功添加"+ret+"条！",2,"");
							TwinC("qrmanage_add");
							_qrmanageT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_qrmanageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"qrmanage_search_w",Title:"搜索二维码",Width:550,sysfun:function(tObj){
			var f = _mediaField;
			if(f==10)
				f[9].hide=true;
				TSform ({
					formname: "qrmanage_search_f",
					formObj:tObj,
					formWinId:"qrmanage_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:f}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("qrmanage_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_qrmanageT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},{dname:"导出二维码",icon:"toxls.gif",onpress:function(Obj){
	
		Twin({Id:"parkwithdraw_search_w",Title:"导出二维码",Width:480,sysfun:function(tObj){
				 TSform ({
					formname: "parkwithdraw_export_f",
					formObj:tObj,
					formWinId:"parkwithdraw_export_w",
					formFunId:tObj,
					dbuttonname:["确认导出"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[{fieldcnname:"类型",fieldname:"type",fieldvalue:'',inputtype:"select",noList:typelist}]}]
					}],
					SubAction:
					function(callback,formName){
						T("#exportiframe").src="qrmanage.do?action=excle&"+Serializ(formName)
						TwinC("parkwithdraw_search_w");
						T.loadTip(1,"正在导出，请稍候...",2,"");
					}
				});	
			}
		})
	}},{dname:"修改使用状态",icon:"toxls.gif",onpress:function(Obj){
	
		Twin({Id:"qr_edit_w",Title:"修改使用状态",Width:480,sysfun:function(tObj){
		
			Tform({
					formname: "qr_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"qrmanage.do?action=isuse",
					method:"POST",
					dbuttonname:["确认修改"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[
						{fieldcnname:"是否使用",fieldname:"isuse",fieldvalue:'1',inputtype:"select",noList:[{"value_no":"1","value_name":"已使用"},{"value_no":"0","value_name":"未使用"}]},
						{fieldcnname:"开始编号",fieldname:"bid",inputtype:"text"},
						{fieldcnname:"结束编号",fieldname:"eid",inputtype:"text"}
						]}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消修改",icon:"cancel.gif", onpress:function(){TwinC("qr_edit_w");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(parseInt(ret)>1){
							T.loadTip(1,"成功添加"+ret+"条！",2,"");
							TwinC("qr_edit_w");
							_qrmanageT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
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
		T.each(_qrmanageT.tc.tableitems,function(o,j){
			o.fieldvalue = _qrmanageT.GD(id)[j]
		});
		Twin({Id:"qrmanage_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "qrmanage_edit_f",
					formObj:tObj,
					recordid:"qrmanage_id",
					suburl:"qrmanage.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_qrmanageT.tc.tableitems}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("qrmanage_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("qrmanage_edit_"+id);
							_qrmanageT.M()
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
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("qrmanage.do?action=delete","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_qrmanageT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_qrmanageT.C();
</script>

</body>
</html>
