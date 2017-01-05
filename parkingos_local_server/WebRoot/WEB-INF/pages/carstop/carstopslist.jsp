<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>泊车点管理</title>
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
<script src="js/tq.newtree.js?1014" type="text/javascript"></script>

</head>
<body>
<div id="carstopsobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"名称",fieldname:"name",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"位置",fieldname:"address",inputtype:"showmap", twidth:"150" ,issort:false},
		{fieldcnname:"所在城市",fieldname:"city",fieldvalue:"",inputtype:"select",noList:[],dataurl:"parking.do?action=localdata",edit:true,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'&&parseInt(value)>0){
					var local = T.A.sendData("parking.do?action=getlocalbycode&code="+value);
					return local;
				}else
					return value;
			}},
		{fieldcnname:"创建日期",fieldname:"ctime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"修改日期 ",fieldname:"utime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"起步价",fieldname:"start_price",inputtype:"text", twidth:"50" ,issort:false},
		{fieldcnname:"最高价",fieldname:"max_price",inputtype:"text",twidth:"50" ,issort:false},
		{fieldcnname:"价格",fieldname:"next_price",inputtype:"text",twidth:"50" ,issort:false},
		{fieldcnname:"状态 ",fieldname:"state",inputtype:"select", twidth:"50",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":5,"value_name":"停用"}] ,issort:false},
		{fieldcnname:"创建 人",fieldname:"creator",inputtype:"text",twidth:"70" ,issort:false,edit:false},
		{fieldcnname:"照片",fieldname:"pic",inputtype:"date", twidth:"150" ,edit:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<a href='#' onclick='viewpic(\""+value+"\")'>"+value+"</a>";
				}else
					return value;
			}
		},
		{fieldcnname:"纬度",fieldname:"latitude",inputtype:"text", twidth:"80" ,issort:false,edit:false},
		{fieldcnname:"经度",fieldname:"longitude",inputtype:"text", twidth:"80" ,issort:false,edit:false},
		{fieldcnname:"备注",fieldname:"resume",inputtype:"text", twidth:"150" ,issort:false}
		
	];
var rules =[{name:"strid",type:"ajax",url:"carstops.do?action=check&value=",requir:true,warn:"账号已存在！",okmsg:""}];
function viewpic(name){
	var url = 'viewpic.html?name='+name;
	Twin({Id:"carstops_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
var _carstopsT = new TQTable({
	tabletitle:"泊车点管理",
	ischeck:false,
	tablename:"carstops_tables",
	dataUrl:"carstops.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#carstopsobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"注册泊车点",icon:"edit_add.png",onpress:function(Obj){
		T.each(_carstopsT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"carstops_add",Title:"添加泊车点",Width:550,sysfun:function(tObj){
				Tform({
					formname: "carstops_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"carstops.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("carstops_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("carstops_add");
							_carstopsT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_carstopsT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"carstops_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "carstops_search_f",
					formObj:tObj,
					formWinId:"carstops_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("carstops_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_carstopsT.C({
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
		T.each(_carstopsT.tc.tableitems,function(o,j){
			o.fieldvalue = _carstopsT.GD(id)[j]
		});
		Twin({Id:"carstops_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "carstops_edit_f",
					formObj:tObj,
					recordid:"carstops_id",
					suburl:"carstops.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_carstopsT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("carstops_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("carstops_edit_"+id);
							_carstopsT.M()
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
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("carstops.do?action=delete","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_carstopsT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	bts.push({name:"上传照片",fun:function(id){
		var url ="upload.html?url=carstops&action=uploadpic&table=carstop_pics&id="+id;
		Twin({Id:"carstops_edit_"+id,Title:"上传照片",Width:350,Height:200,sysfunI:id,
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				_carstopsT.M();
			}
			})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_carstopsT.C();
</script>

</body>
</html>
