<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>泊车员管理</title>
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
<div id="parkattendantobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var _mediaField = [
		{fieldcnname:"账号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"姓名",fieldname:"nickname",inputtype:"text", twidth:"150" ,issort:false,edit:false},
		{fieldcnname:"所属停车场",fieldname:"comid",inputtype:"showmap", twidth:"150" ,issort:false,edit:false},
		{fieldcnname:"创建日期",fieldname:"reg_time",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"最近登录 ",fieldname:"logon_time",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"修改日期 ",fieldname:"utime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"驾龄",fieldname:"driver_year",inputtype:"text", twidth:"50" ,issort:false},
		{fieldcnname:"状态 ",fieldname:"state",inputtype:"select", twidth:"50",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":5,"value_name":"停用"}] ,issort:false},
		{fieldcnname:"证件照",fieldname:"driver_pic",inputtype:"text",twidth:"150" ,issort:false,
		process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<a href='#' onclick='viewpic(\""+value+"\")'>"+value+"</a>";
				}else
					return value;
			}},
		{fieldcnname:"照片",fieldname:"pic_url",inputtype:"date", twidth:"150" ,edit:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<a href='#' onclick='viewpic(\""+value+"\")'>"+value+"</a>";
				}else
					return value;
			}
		},
		{fieldcnname:"备注",fieldname:"resume",inputtype:"text", twidth:"150" ,issort:false}
		
	];
var rules =[{name:"strid",type:"ajax",url:"parkattendant.do?action=check&value=",requir:true,warn:"账号已存在！",okmsg:""}];
function viewpic(name){
	var url = 'viewpic.html?name='+name;
	Twin({Id:"parkattendant_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
var _parkattendantT = new TQTable({
	tabletitle:"泊车员管理",
	ischeck:false,
	tablename:"parkattendant_tables",
	dataUrl:"parkattendant.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#parkattendantobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	/* return [{dname:"注册泊车员",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkattendantT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"parkattendant_add",Title:"添加泊车员",Width:550,sysfun:function(tObj){
				Tform({
					formname: "parkattendant_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"parkattendant.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parkattendant_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("parkattendant_add");
							_parkattendantT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}}
	,
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkattendantT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"parkattendant_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "parkattendant_search_f",
					formObj:tObj,
					formWinId:"parkattendant_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parkattendant_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_parkattendantT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}}
	] */
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_parkattendantT.tc.tableitems,function(o,j){
			o.fieldvalue = _parkattendantT.GD(id)[j]
		});
		Twin({Id:"parkattendant_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "parkattendant_edit_f",
					formObj:tObj,
					recordid:"parkattendant_id",
					suburl:"parkattendant.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_parkattendantT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("parkattendant_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("parkattendant_edit_"+id);
							_parkattendantT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}});
	/* bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("parkattendant.do?action=delete","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_parkattendantT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}}); */
	bts.push({name:"上传照片",fun:function(id){
		var url ="upload.html?url=parkattendant&action=uploadpic&table=carstop_pics&type=0&id="+id;
		Twin({Id:"parkattendant_edit_"+id,Title:"上传照片",Width:350,Height:200,sysfunI:id,
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				_parkattendantT.M();
			}
			})
	}});
	bts.push({name:"上传证件照",fun:function(id){
		var url ="upload.html?url=parkattendant&action=uploadpic&table=carstop_pics&type=1&id="+id;
		Twin({Id:"parkattendant_edit_"+id,Title:"上传证件照",Width:350,Height:200,sysfunI:id,
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				_parkattendantT.M();
			}
			})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_parkattendantT.C();
</script>

</body>
</html>
