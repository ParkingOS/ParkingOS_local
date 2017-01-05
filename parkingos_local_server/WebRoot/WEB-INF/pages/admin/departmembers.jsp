<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>数据授权</title>
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
<div id="departobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var uin = ${uin};
var departid = ${departid};
var _mediaField = [
         //恢复之前的选中状态，必须放在第一列，选中值必须是"ischecked"
		{fieldcnname:"是否选中",fieldname:"checked",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true,fhide:true,shide:true},
		//其他列
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"角色",fieldname:"role_name",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true,shide:true}
	];
var _departT = new TQTable({
	tabletitle:"数据授权",
	//ischeck:false,
	tablename:"depart_tables",
	dataUrl:"authsetting.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=departmembers&uin="+uin+"&departid="+departid,
	tableObj:T("#departobj"),
	fit:[true,true,true],
	tableitems:_mediaField
});

function getAuthButtons(){
	return [
	{ dname:  "数据授权", icon: "sendsms.gif", onpress:function(Obj){
		var sids = _departT.GS();
		Twin({Id:"save_dataauth_w",Title:"授权",Width:550,sysfun:function(tObj){
			Tform({
				formname: "save_dataauth_ff",
				formObj:tObj,
				recordid:"id",
				suburl:"authsetting.do?action=dataauth&uin="+uin+"&departid="+departid,
				method:"POST",
				Coltype:2,
				dbuttonname:["授权"],
				formAttr:[{
					formitems:[{kindname:"",kinditemts:[
					{fieldcnname:"人员编号",fieldname:"ids",fieldvalue:sids,inputtype:"multi",height:"80",edit:false}]}]
				}],
				buttons : [//工具
					{name: "cancel", dname: "取消", tit:"取消授权",icon:"cancel.gif", onpress:function(){TwinC("save_dataauth_w");} }
				],
				Callback:function(f,rcd,ret,o){
					if(ret=="1"){
						T.loadTip(1,"操作成功！",2,"");
						TwinC("save_dataauth_w");
					}else{
						T.loadTip(1,"操作失败",2,o);
					}
				}
			});	
			}
		})
		
	}}
	]
	return false;
}
_departT.C();
</script>

</body>
</html>
