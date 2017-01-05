<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>已校验可支付车场统计</title>
<link href="css/zTreeStyle1.css" rel="stylesheet" type="text/css">
<link href="css/demo.css" rel="stylesheet" type="text/css">
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
<script src="js/jquery.js" type="text/javascript"></script>
<script src="js/jquery.ztree.core-3.5.js" type="text/javascript"></script>
</head>
<body>
<div id="parkinfoanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >

function showmarketers(){
	var setting = {
			view: {
				dblClickExpand: false
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeClick: beforeClick,
				onClick: onClick
			}
		};
	var childs = eval(T.A.sendData("getdata.do?action=markets"));
	var ztreestr = "[{'id':-1,'pId':0,'name':'全部'},";
	for(var i=1;i<childs.length;i++){
		var child = childs[i];
		var id = child.value_no;
		var name = child.value_name;
		ztreestr += "{'id':"+id+",'pId':"+0+",'name':\""+name+"\"},";
	}
	ztreestr = ztreestr.substring(0,ztreestr.length-1);
	ztreestr += "]";
	var zNodes = eval("("+ ztreestr +")");
	$.fn.zTree.init($("#treeDemo"), setting, zNodes);
	var departObj = $("#marketer");
	var departOffset = $("#marketer").offset();
	$("#menuContent").css({left:departOffset.left + "px", top:departOffset.top + departObj.outerHeight() + "px"}).slideDown("fast");
	$("body").bind("mousedown", onBodyDown);
}

function onBodyDown(event) {
	if (!(event.target.id == "marketer" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
		hideMenu();
	}
}

function beforeClick(treeId, treeNode) {
	var check = (treeNode && !treeNode.isParent);
	if (!check) alert("只能选择叶节点...");
	return check;
}

function onClick(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	var nodes = zTree.getSelectedNodes();
	var nodename = nodes[0].name;
	var nodeid = nodes[0].id;
	var id = nodeid;
	var marketObj = $("#marketer");
	var marketvalueObj = $("#marketvalue");
	marketObj.attr("value", nodename);
	marketvalueObj.attr("value", id);
}

function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}

var _mediaField = [
		{fieldcnname:"ID",fieldname:"id",inputtype:"text", twidth:"200" ,issort:false,fhide:true},
		{fieldcnname:"已校验可支付车场总数",fieldname:"park_total",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"车位总数",fieldname:"parking_total",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"收费员总数",fieldname:"ctotal",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"占道车场百分比",fieldname:"zhandao_percent",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"地下车场百分比",fieldname:"dixia_percent",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"地面车场百分比",fieldname:"dimian_percent",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"地上/地下车场百分比",fieldname:"dimian_dixia_percent",inputtype:"text", twidth:"150",issort:false}
	];
var _parkinfoanlysisT = new TQTable({
	tabletitle:"已校验可支付车场统计",
	ischeck:false,
	tablename:"parkinfoanlysis_tables",
	dataUrl:"efparkanlysis.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#parkinfoanlysisobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true
});

function coutomsearch(){
	var html = "校验时间：<input id='coutom_btime' value='' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;市场专员：<input id='marketer' value='' readonly style='width:150px' onClick=\"showmarketers();\"/></select>&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"value=' 查 询 '/>";
	return html;
}

function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	var marketvalue = T("#marketvalue").value;
	var nodename = T("#marketer").value;
	_parkinfoanlysisT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime+"&uid="+marketvalue
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
	T("#marketer").value = nodename;
	T("#marketvalue").value = marketvalue;
}

_parkinfoanlysisT.C();
</script>
<div id="menuContent" class="menuContent" style="display:none; position: absolute;">
	<ul id="treeDemo" class="ztree" style="margin-top:0; width:160px;"></ul>
</div>
<div style="display:none;">
<input type="text" id="marketvalue" value="" />
</div>
</body>
</html>
