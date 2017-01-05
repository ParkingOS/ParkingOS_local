<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>订单红包统计</title>
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
<div id="orderbonusanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}"
var etime="${etime}";
var type =[["-1","请选择"],["0","充值"],["1","提现"],["2","返现"]];
var source =[["-1","请选择"],["0","停车费"],["1","返现"],["2","泊车费"]];
function setSelects(data){
	var d = [];
	for(var i=0;i<data.length;i++){
		d.push({"value_no":data[i][0],"value_name":data[i][1]});
	}
	return d;
}
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"200" ,issort:false,fhide:true,shide:true},
		{fieldcnname:"交易日期",fieldname:"create_time",inputtype:"date", twidth:"200",issort:false},
		{fieldcnname:"停车场",fieldname:"comid",inputtype:"number", twidth:"100",issort:false},
		{fieldcnname:"金额",fieldname:"amount",inputtype:"number", twidth:"100",issort:false},
		{fieldcnname:"类型",fieldname:"type",inputtype:"select",noList:setSelects(type), twidth:"100",issort:false},
		{fieldcnname:"来源/去向",fieldname:"source",inputtype:"select",noList:setSelects(source), twidth:"100",issort:false},
		{fieldcnname:"说明",fieldname:"remark",inputtype:"text", twidth:"500",issort:false}
	];
var _orderbonusanlysisT = new TQTable({
	tabletitle:"停车宝交易记录",
	ischeck:false,
	tablename:"orderbonusanlysis_tables",
	dataUrl:"orderbonusaly.do",
	iscookcol:false,
	buttons:getAuthButtons(),
	//quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#orderbonusanlysisobj"),
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
		T.each(_orderbonusanlysisT.tc.tableitems,function(o,j){
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
						_orderbonusanlysisT.C({
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

_orderbonusanlysisT.C();



</script>

</body>
</html>
