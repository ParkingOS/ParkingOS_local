<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车宝账户</title>
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
<div id="tcbaccountanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}"
var etime="${etime}";
var type =[{"value_no":"-1","value_name":"请选择"},{"value_no":"0","value_name":"收入"},{"value_no":"1","value_name":"支出"}]
var utype =[{"value_no":"-1","value_name":"请选择"},{"value_no":"0","value_name":"停车代金券"},{"value_no":"1","value_name":"支付宝服务费"}
,{"value_no":"2","value_name":"微信服务费"}
//,{"value_no":"3","value_name":"车场返现"}
//,{"value_no":"4","value_name":"车主返现"}
,{"value_no":"5","value_name":"微信公众号充值手续费"}
,{"value_no":"6","value_name":"停车券退款"},{"value_no":"7","value_name":"车主用停车券打赏"}];
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"200" ,issort:false,fhide:true,shide:true},
		{fieldcnname:"交易日期",fieldname:"create_time",inputtype:"date", twidth:"200",issort:false},
		{fieldcnname:"金额",fieldname:"amount",inputtype:"number", twidth:"100",issort:false},
		{fieldcnname:"类型",fieldname:"utype",inputtype:"select",noList:utype, twidth:"100",issort:false},
		{fieldcnname:"收入/支出",fieldname:"type",inputtype:"select",noList:type, twidth:"100",issort:false},
		{fieldcnname:"说明",fieldname:"remark",inputtype:"text", twidth:"500",issort:false}
	];
var _tcbaccountanlysisT = new TQTable({
	tabletitle:"停车宝交易记录",
	ischeck:false,
	tablename:"tcbaccountanlysis_tables",
	dataUrl:"tcbaccountaly.do",
	iscookcol:false,
	buttons:getAuthButtons(),
	//quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#tcbaccountanlysisobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true,
	isoperate:getAuthIsoperateButtons()
});
function getAuthIsoperateButtons(){
	var bts = [];
	
	if(bts.length <= 0){return false;}
	return bts;
}

function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_tcbaccountanlysisT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"paccount_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "paccount_search_f",
					formObj:tObj,
					formWinId:"paccount_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("paccount_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_tcbaccountanlysisT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}}]
}

_tcbaccountanlysisT.C();



</script>

</body>
</html>
