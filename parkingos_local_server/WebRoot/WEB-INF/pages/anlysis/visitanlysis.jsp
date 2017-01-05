<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>拜访记录</title>
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
<div id="visitanlysisobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}"
var etime="${etime}";
var auth_flag = "${auth_flag}";
var department_id = "${department_id}";
var _mediaField = [
		{fieldcnname:"市场专员ID",fieldname:"uid",inputtype:"text", twidth:"200" ,issort:false,fhide:true},
		{fieldcnname:"市场专员",fieldname:"nickname",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"拜访总数",fieldname:"total",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hn','"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"所属部门",fieldname:"dname",inputtype:"text", twidth:"100",issort:false}
	];
var _visitanlysisT = new TQTable({
	tabletitle:"拜访记录",
	ischeck:false,
	tablename:"visitanlysis_tables",
	dataUrl:"visitanlysis.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=query",
	tableObj:T("#visitanlysisobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:true
});

function coutomsearch(){
	var html = "";
	if(auth_flag == "0"){
		html += "部门：<input id='departmentsel' value='' readonly style='width:100px' onClick=\"showdeparts();\"/>"+"-"+"&nbsp;&nbsp;";
	}
	html+= "时间：<input id='coutom_btime' value='"+btime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"
				+" - <input id='coutom_etime' value='"+etime+"' style='width:70px' onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"/>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdata();' "+
				"value=' 查 询 '/>";//"&nbsp;&nbsp;总计：900.00元";
	return html;
}

function showdeparts(){
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
	var zNodes = eval(T.A.sendData("authsetting.do?action=getseldeparts"));
	$.fn.zTree.init($("#treeDemo"), setting, zNodes);
	var departObj = $("#departmentsel");
	var departOffset = $("#departmentsel").offset();
	$("#menuContent").css({left:departOffset.left + "px", top:departOffset.top + departObj.outerHeight() + "px"}).slideDown("fast");
	$("body").bind("mousedown", onBodyDown);
}

function onBodyDown(event) {
	if (!(event.target.id == "departmentsel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
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
	var pid = nodes[0].pId;
	var id = pid + "_" + nodeid;
	var departselObj = $("#departmentsel");
	var departvalueObj = $("#departvalue");
	departselObj.attr("value", nodename);
	departvalueObj.attr("value", id);
}

function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}

function searchdata(){
	btime = T("#coutom_btime").value;
	etime = T("#coutom_etime").value;
	var departvalue = "";
	var nodename = "";
	if(auth_flag == "0"){
		departvalue = T("#departvalue").value;
		nodename = T("#departmentsel").value;
	}else{
		departvalue = department_id;
	}
	//alert(btime);
	//alert(etime);
	_visitanlysisT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=query&btime="+btime+"&etime="+etime+"&departvalue="+departvalue
	})
	T("#coutom_btime").value=btime;
	T("#coutom_etime").value=etime;
	if(auth_flag == "0"){
		T("#departmentsel").value = nodename;
		T("#departvalue").value = departvalue;
	}else{
		department_id = departvalue;
	}
}

function viewdetail(type,value,id){
	//alert(type+","+value);
	var total =_visitanlysisT.GD(id,"total");
	var nickname =_visitanlysisT.GD(id,"nickname");
	var tip = "拜访记录";
		
	Twin({
		Id:"visit_detail_"+id,
		Title:tip+"  --> 市场专员："+nickname,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='visit_detail_'"+id+" id='visit_detail_'"+id+" src='visitanlysis.do?action=detail&uid="+id+"&btime="+btime+"&etime="+etime+"&total="+total+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

_visitanlysisT.C();
</script>
<div id="menuContent" class="menuContent" style="display:none; position: absolute;">
	<ul id="treeDemo" class="ztree" style="margin-top:0; width:160px;"></ul>
</div>
<div style="display:none;">
<input type="text" id="departvalue" value="" />
</div>
</body>
</html>
