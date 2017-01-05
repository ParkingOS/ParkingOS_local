<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>黑名单管理</title>
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
<div id="blackusersobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"账号",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":1,"value_name":"已漂白"}] ,twidth:"200" ,height:"",issort:false},
		{fieldcnname:"新建日期",fieldname:"ctime",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"修改日期",fieldname:"utime",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",issort:false}
	];
var _blackusersT = new TQTable({
	tabletitle:"黑名单管理",
	//ischeck:false,
	tablename:"blackusers_tables",
	dataUrl:"blackuser.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#blackusersobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_blackusersT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"blackusers_search_w",Title:"搜索黑名单",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "blackusers_search_f",
					formObj:tObj,
					formWinId:"blackusers_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("blackusers_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_blackusersT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"重置黑名单缓存",onpress:function(Obj){
		Tconfirm({Title:"确认重置黑名单缓存吗",Content:"确认重置缓存吗",OKFn:function(){T.A.sendData("blackuser.do?action=reload","GET","",
			function deletebackfun(ret){
				T.loadTip(1,ret,2,"");
			}
		)}})
	}}
	,
	{dname:"查看黑名单缓存",onpress:function(Obj){
		Tconfirm({Title:"确认查看黑名单缓存吗",Content:"确认查看缓存吗",OKFn:function(){T.A.sendData("blackuser.do?action=viewmemcache","GET","",
			function deletebackfun(ret){
				T.loadTip(2,ret,100,"");
			}
		)}})
	}}
	,
	{dname:"重置白名单缓存",onpress:function(Obj){
		Tconfirm({Title:"确认重置白名单缓存吗",Content:"确认重置白名单缓存吗",OKFn:function(){T.A.sendData("blackuser.do?action=reloadwhite","GET","",
			function deletebackfun(ret){
				T.loadTip(1,ret,2,"");
			}
		)}})
	}}
	,
	{dname:"查看白名单缓存",onpress:function(Obj){
		Tconfirm({Title:"确认查看白名单缓存吗",Content:"确认查看缓存吗",OKFn:function(){T.A.sendData("blackuser.do?action=viewwhitememcache","GET","",
			function deletebackfun(ret){
				T.loadTip(2,ret,100,"");
			}
		)}})
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"",
		rule:function(id){
			var state =_blackusersT.GD(id,"state");
			if(state==1){
				this.name="还原";
			}else{
				this.name="漂白";
			}
			return true;
		},
		fun:function(id){
		var state =_blackusersT.GD(id,"state");
		var vname = _blackusersT.GD(id,"mobile");
		var uin = _blackusersT.GD(id,"uin");
		var type = "漂白";
		if(state==1){
			type = "还原";
		}
		Tconfirm({
			Title:"提示信息",
			Ttype:"alert",
			Content:"警告：您确认要 <font color='red'>"+type+"</font> "+vname+"吗？",
			OKFn:function(){
			T.A.sendData("blackuser.do?action=edit&id="+id+"&state="+state+"&uin="+uin,"GET","",
				function(ret){
					if(ret=="1"){
						T.loadTip(1,type+"成功！",2,"");
						_blackusersT.C();
					}else{
						T.loadTip(1,"操作失败，请重试！",2,"")
					}
				},0,null)
			}
		});
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_blackusersT.C();
</script>

</body>
</html>
