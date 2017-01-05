<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>套餐管理</title>
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
<div id="packageobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var comid = ${comid};
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"名称",fieldname:"p_name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:""},
		{fieldcnname:"开始小时 ",fieldname:"b_time",fieldvalue:'',inputtype:"hour",defaultValue:'8||8', twidth:"80" ,height:""},
		{fieldcnname:"开始分钟",fieldname:"bmin",fieldvalue:'',inputtype:"pminute",defaultValue:'0||0', twidth:"80" ,height:""},
		{fieldcnname:"结束小时",fieldname:"e_time",fieldvalue:'',inputtype:"hour",defaultValue:'8||8', twidth:"80" ,height:"",issort:false},
		{fieldcnname:"结束分钟",fieldname:"emin",fieldvalue:'',inputtype:"pminute",defaultValue:'0||0', twidth:"80" ,height:""},
		{fieldcnname:"剩余数量",fieldname:"remain_number",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"价格",fieldname:"price",fieldvalue:'',inputtype:"doub", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"原价",fieldname:"old_price",fieldvalue:'',inputtype:"doub", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"有效期至",fieldname:"limitday",fieldvalue:'',inputtype:"sdate", twidth:"150" ,height:"",issort:false},
		{fieldcnname:"描述",fieldname:"resume",fieldvalue:'',inputtype:"multi", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"有效"},{"value_no":1,"value_name":"无效"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"类型",fieldname:"type",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"全天包月"},{"value_no":1,"value_name":"夜间包月"},{"value_no":2,"value_name":"日间包月"}] ,twidth:"100" ,height:"",issort:false}
	];
var rules =[
		{name:"remain_number",type:"number",url:"",requir:true,warn:"请输入数字!",okmsg:""},
		{name:"b_time",type:"number",url:"",requir:true,warn:"请输入数字!",okmsg:""},
		{name:"e_time",type:"number",url:"",requir:true,warn:"请输入数字!",okmsg:""},
		{name:"price",type:"doub",url:"",requir:true,warn:"请输入价格!",okmsg:""},
		{name:"limitday",type:"date",url:"",requir:true,warn:"请输入有效期!",okmsg:""}];
var _packageT = new TQTable({
	tabletitle:"套餐管理",
	ischeck:false,
	tablename:"package_tables",
	dataUrl:"package.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery&comid="+comid,
	tableObj:T("#packageobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"添加套餐",icon:"edit_add.png",onpress:function(Obj){
		T.each(_packageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"package_add",Title:"添加停车场",Width:550,sysfun:function(tObj){
				Tform({
					formname: "package_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"package.do?action=create&comid="+comid,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("package_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("package_add");
							_packageT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_packageT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"package_search_w",Title:"搜索套餐",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "package_search_f",
					formObj:tObj,
					formWinId:"package_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("package_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_packageT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&comid="+comid+"&action=query&"+Serializ(formName)
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
		T.each(_packageT.tc.tableitems,function(o,j){
			o.fieldvalue = _packageT.GD(id)[j]
		});
		Twin({Id:"package_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "package_edit_f",
					formObj:tObj,
					recordid:"package_id",
					suburl:"package.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_packageT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("package_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("package_edit_"+id);
							_packageT.M()
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
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("package.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_packageT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}}); */
	if(bts.length <= 0){return false;}
	return bts;
}
_packageT.C();
</script>

</body>
</html>
