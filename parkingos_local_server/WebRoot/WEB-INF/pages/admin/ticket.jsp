<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车券查询</title>
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
<div id="ticketobj" style="width:100%;height:100%;margin:0px;"></div>
<iframe src="" id ="exportiframe" frameborder="0" style="width:0px;height:0px;"></iframe>
<script language="javascript">
var ticket_type = [{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"普通停车券"},{"value_no":1,"value_name":"专用停车券"},{"value_no":2,"value_name":"微信打折券"},{"value_no":3,"value_name":"减时券"},{"value_no":4,"value_name":"全免券"}];
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"金额",fieldname:"money",fieldvalue:'',inputtype:"number", twidth:"50" ,height:"",issort:false},
		{fieldcnname:"抵扣金额",fieldname:"umoney",fieldvalue:'',inputtype:"number", twidth:"70" ,height:"",issort:false},
		{fieldcnname:"到期时间",fieldname:"limit_day",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true},
		{fieldcnname:"使用时间",fieldname:"utime",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",hide:true,
			process:function(value,cid,id){
				if(value==0){
					return "";
				}else{
					return value;
				}
			}},
		{fieldcnname:"车主",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":"-1","value_name":"全部"},{"value_no":"0","value_name":"未使用"},{"value_no":"1","value_name":"已使用"}], twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车牌号",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"停车券类型",fieldname:"type",fieldvalue:'',inputtype:"select",noList:ticket_type,twidth:"100" ,height:"",issort:false}
	];
var _ticketT = new TQTable({
	tabletitle:"停车券查询",
	ischeck:false,
	tablename:"ticket_tables",
	dataUrl:"ticket.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#ticketobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:false
});
function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_ticketT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"ticket_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "ticket_search_f",
					formObj:tObj,
					formWinId:"ticket_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("ticket_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_ticketT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"车场专用停车券",icon:"edit_add.png",onpress:function(Obj){
			location = "parkticket.do";
		}
	},{dname:"导出停车券",icon:"toxls.gif",onpress:function(Obj){
	
		Twin({Id:"ticket_export_w",Title:"导出停车券<font style='color:red;'>（如果没有设置，默认全部导出!）</font>",Width:480,sysfun:function(tObj){
				 TSform ({
					formname: "ticket_export_f",
					formObj:tObj,
					formWinId:"ticket_export_w",
					formFunId:tObj,
					dbuttonname:["确认导出"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
					}],
					//formitems:[{kindname:"",kinditemts:_excelField}],
					SubAction:
					function(callback,formName){
						T("#exportiframe").src="ticket.do?action=exportExcel&rp="+2147483647+"&fieldsstr="+"id__money__umoney__limit_day__uin__state__car_number__type&"+Serializ(formName)
						TwinC("ticket_export_w");
						T.loadTip(1,"正在导出，请稍候...",2,"");
					}
				});	
			}
		})
	}}
	]
	return false;
}
_ticketT.C();
</script>

</body>
</html>
