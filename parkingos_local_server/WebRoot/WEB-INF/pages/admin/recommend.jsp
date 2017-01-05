<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>新推荐查询</title>
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
<div id="recomviewobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true,shide:true},
		{fieldcnname:"推荐人",fieldname:"pid",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"推荐人角色",fieldname:"auth_flag",fieldvalue:'',inputtype:"select",noList:[{"value_no":"-1","value_name":"全部"},{"value_no":"2","value_name":"收费员"},{"value_no":"4","value_name":"车主"}], twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"被推荐人",fieldname:"nid",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"被推荐人角色",fieldname:"type",fieldvalue:'',inputtype:"select",noList:[{"value_no":"-1","value_name":"全部"},{"value_no":"0","value_name":"车主"},{"value_no":"1","value_name":"收费员"}], twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"推荐日期",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"微信公众号",fieldname:"openid",fieldvalue:'',inputtype:"text", twidth:"250" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":"-1","value_name":"全部"},{"value_no":"0","value_name":"推荐中"},{"value_no":"1","value_name":"推荐成功"}],twidth:"100" ,height:"",issort:false,edit:false}
	];
var _recomviewT = new TQTable({
	tabletitle:"新推荐查询",
	ischeck:false,
	tablename:"recomview_tables",
	dataUrl:"recomview.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#recomviewobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [
			{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
			T.each(_recomviewT.tc.tableitems,function(o,j){
				o.fieldvalue ="";
			});
			Twin({Id:"recomview_search_w",Title:"搜索推荐",Width:550,sysfun:function(tObj){
					TSform ({
						formname: "recomview_search_f",
						formObj:tObj,
						formWinId:"recomview_search_w",
						formFunId:tObj,
						formAttr:[{
							formitems:[{kindname:"",kinditemts:_mediaField}]
						}],
						buttons : [//工具
							{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("recomview_search_w");} }
						],
						SubAction:
						function(callback,formName){
							_recomviewT.C({
								cpage:1,
								tabletitle:"高级搜索结果",
								extparam:"&action=highquery&"+Serializ(formName)
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
	if(bts.length <= 0){return false;}
	return bts;
}
_recomviewT.C();
</script>

</body>
</html>
