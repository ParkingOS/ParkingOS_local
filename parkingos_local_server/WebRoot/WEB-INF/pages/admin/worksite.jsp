<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>工作站管理</title>
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
<div id="worksiteobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var comid = ${comid};
var _mediaField = [
		{fieldcnname:"工作站ID",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false,fhide:true},
		{fieldcnname:"名称",fieldname:"worksite_name",fieldvalue:'',inputtype:"text", twidth:"300" ,height:"",issort:false},
		{fieldcnname:"说明",fieldname:"description",fieldvalue:'',inputtype:"text", twidth:"500" ,height:"",issort:false},
		{fieldcnname:"网络类型",fieldname:"net_type",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"流量"},{"value_no":1,"value_name":"宽带"}], twidth:"100" ,height:"",issort:false}
	];
var rules =[{name:"worksite_name",requir:true}];
var _worksiteT = new TQTable({
	tabletitle:"工作站管理",
	ischeck:false,
	tablename:"worksite_tables",
	dataUrl:"worksite.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=worksitequery&comid="+comid,
	tableObj:T("#worksiteobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"添加工作站",icon:"edit_add.png",onpress:function(Obj){
		T.each(_worksiteT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"worksite_add",Title:"添加工作站",Width:550,sysfun:function(tObj){
				Tform({
					formname: "worksite_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"worksite.do?action=create&comid="+comid,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("worksite_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("worksite_add");
							_worksiteT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
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
		T.each(_worksiteT.tc.tableitems,function(o,j){
			o.fieldvalue = _worksiteT.GD(id)[j]
		});
		Twin({Id:"worksite_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "worksite_edit_f",
					formObj:tObj,
					recordid:"worksite_id",
					suburl:"worksite.do?comid="+comid+"&action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_worksiteT.tc.tableitems}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("worksite_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("worksite_edit_"+id);
							_worksiteT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}});
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("worksite.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_worksiteT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_worksiteT.C();
</script>

</body>
</html>
