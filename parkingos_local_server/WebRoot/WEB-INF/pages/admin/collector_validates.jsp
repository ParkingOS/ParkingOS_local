<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车员管理</title>
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
<div id="collectorobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
function getauditor (){
	var auditors = eval(T.A.sendData("getdata.do?action=getauditors"));
	return auditors;
}
//未审核收费员
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":1,"value_name":"禁用"},{"value_no":2,"value_name":"新增"},{"value_no":3,"value_name":"待补充"},{"value_no":4,"value_name":"待跟进"},{"value_no":5,"value_name":"无价值"}] ,twidth:"200" ,height:"",issort:false},
		{fieldcnname:"注册日期",fieldname:"reg_time",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"收费员上传图片个数",fieldname:"collector_pics",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",hide:true,shide:true},
		{fieldcnname:"审核人",fieldname:"collector_auditor",fieldvalue:'',inputtype:"select",noList:getauditor(),action:"",twidth:"160" ,height:"",issort:false,shide:true},
		{fieldcnname:"验证时间",fieldname:"logon_time",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",hide:true,shide:true}
	];
var _collectorT = new TQTable({
	tabletitle:"审核停车员",
	ischeck:false,
	tablename:"collector_tables",
	dataUrl:"collector.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=vquery",
	tableObj:T("#collectorobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_collectorT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"collector_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "collector_search_f",
					formObj:tObj,
					formWinId:"collector_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("collector_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_collectorT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=vquery&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"返回停车员管理",icon:"edit_add.png",onpress:function(Obj){
		location = "collector.do?action=";
		}
	}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"备注",fun:function(id){
		Twin({
			Id:"client_detail_"+id,
			Title:"备注记录  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"collector.do?action=getremarks&id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
	bts.push({name:"审核",fun:function(id){
		Twin({
			Id:"client_detail_"+id,
			Title:"收费员  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"collector.do?action=vuser&id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
	if(bts.length <= 0){return false;}
	return bts;
}
_collectorT.C();
</script>

</body>
</html>
