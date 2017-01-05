<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>人员</title>
<link href="css/zTreeStyle1.css" rel="stylesheet" type="text/css">
<link href="css/demo.css" rel="stylesheet" type="text/css">
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?033434" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.newtree.js?1014" type="text/javascript"></script>
<script src="js/jquery.js" type="text/javascript"></script>
<script src="js/jquery.ztree.core-3.5.js" type="text/javascript"></script>
</head>
<body>
<div id="personobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
function showroles(){
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
				onClick: onClick1
			}
		};
	var zNodes = eval(T.A.sendData("authsetting.do?action=getselroles"));
	$.fn.zTree.init($("#treeDemo"), setting, zNodes);
	var roleObj = $("#rolesel");
	var roleOffset = $("#rolesel").offset();
	$("#menuContent").css({left:roleOffset.left + "px", top:roleOffset.top + roleObj.outerHeight() + "px"}).slideDown("fast");
	$("body").bind("mousedown", onBodyDown1);
}

function onBodyDown1(event) {
	if (!(event.target.id == "rolesel"|| event.target.id == "departmentsel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
		hideMenu();
	}
}

function onBodyDown2(event) {
	if (!(event.target.id == "departmentsel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
		hideMenu();
	}
}

function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}

function beforeClick(treeId, treeNode) {
	var check = (treeNode && !treeNode.isParent);
	if (!check) alert("只能选择叶节点...");
	return check;
}
		
function onClick1(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	var nodes = zTree.getSelectedNodes();
	var nodename = nodes[0].name;
	var nodeid = nodes[0].id;
	var roleselObj = $("#rolesel");
	var rolevalueObj = $("#rolevalue");
	roleselObj.attr("value", nodename);
	rolevalueObj.attr("value", nodeid);
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
				onClick: onClick2
			}
		};
	var zNodes = eval(T.A.sendData("authsetting.do?action=getseldeparts"));
	$.fn.zTree.init($("#treeDemo"), setting, zNodes);
	var departObj = $("#departmentsel");
	var departOffset = $("#departmentsel").offset();
	$("#menuContent").css({left:departOffset.left + "px", top:departOffset.top + departObj.outerHeight() + "px"}).slideDown("fast");
	$("body").bind("mousedown", onBodyDown2);
}

function onClick2(e, treeId, treeNode) {
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

var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"角色",fieldname:"auth_flag",fieldvalue:"",inputtype:"select",noList:[],dataurl:"authsetting.do?action=getroles",edit:true,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'&&parseInt(value)>0){
					var local = T.A.sendData("authsetting.do?action=getrolename&roleid="+value);
					return local;
				}else
					return value;
			}},
		{fieldcnname:"部门",fieldname:"department_id",fieldvalue:"",inputtype:"select",noList:[],dataurl:"authsetting.do?action=getdepartments",edit:true,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'&&parseInt(value)>0){
					var local = T.A.sendData("authsetting.do?action=getdepartmentname&departmentid="+value);
					return local;
				}else
					return value;
			}}
	];
var _personT = new TQTable({
	tabletitle:"人员设置",
	ischeck:false,
	tablename:"person_tables",
	dataUrl:"authsetting.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	//searchitem:true,
	param:"action=person",
	tableObj:T("#personobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function coutomsearch(){
	var html=    "按角色查询：<input id='rolesel' value='' readonly style='width:100px' onClick=\"showroles();\"/>"+"&nbsp;&nbsp;<input type='button' onclick='searchrole();' value=' 查 询 '/>"
				+" - 按部门查询：<input id='departmentsel' value='' readonly style='width:100px' onClick=\"showdeparts();\"/>"+
				"&nbsp;&nbsp;<input type='button' onclick='searchdepart();' value=' 查 询 '/>";
	return html;
}

function searchrole(){
	var rolevalue = T("#rolevalue").value;
	var nodename = T("#rolesel").value;
	_personT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=person&rolevalue="+rolevalue
	})
	T("#rolevalue").value = rolevalue;
	T("#rolesel").value = nodename;
}

function searchdepart(){
	var departvalue = T("#departvalue").value;
	var nodename = T("#departmentsel").value;
	_personT.C({
		cpage:1,
		tabletitle:"搜索结果",
		extparam:"&action=person&departvalue="+departvalue
	})
	T("#departmentsel").value = nodename;
	T("#departvalue").value = departvalue;
}

function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"数据授权",fun:function(id){
		Twin({
			Id:"client_detail_"+id,
			Title:" &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"authsetting.do?action=todepartpage&id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
	bts.push({name:"编辑",fun:function(id){
		T.each(_personT.tc.tableitems,function(o,j){
			if(o.fieldname=='auth_flag'){
				var roleid = _personT.GD(id)[j];
				var rolename = T.A.sendData("authsetting.do?action=getrolename&roleid="+roleid);	
				o.fieldvalue = roleid+"||"+rolename;
			}else if(o.fieldname=='department_id'){
				var departmentid = _personT.GD(id)[j];
				var getdepartmentname = T.A.sendData("authsetting.do?action=getdepartmentname&departmentid="+departmentid);	
				o.fieldvalue = departmentid+"||"+getdepartmentname;
			}else
				o.fieldvalue = _personT.GD(id)[j]
		});
		Twin({Id:"person_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "person_edit_f",
					formObj:tObj,
					recordid:"person_id",
					suburl:"authsetting.do?action=personedit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_personT.tc.tableitems}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("person_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("person_edit_"+id);
							_personT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_personT.C();
</script>
<div id="menuContent" class="menuContent" style="display:none; position: absolute;">
	<ul id="treeDemo" class="ztree" style="margin-top:0; width:160px;"></ul>
</div>
<div style="display:none;">
<input type="text" id="rolevalue" value="" />
<input type="text" id="departvalue" value="" />
</div>
</body>
</html>
