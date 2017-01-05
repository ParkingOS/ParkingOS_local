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
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"60" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"角色",fieldname:"role_name",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true,shide:true},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"正常"},{"value_no":1,"value_name":"禁用"},{"value_no":2,"value_name":"新增"},{"value_no":3,"value_name":"待补充"},{"value_no":4,"value_name":"待跟进"},{"value_no":5,"value_name":"无价值"}] ,twidth:"50" ,height:"",issort:false},
		{fieldcnname:"注册日期",fieldname:"reg_time",fieldvalue:'',inputtype:"date", twidth:"150" ,height:"",issort:false,edit:false},
		{fieldcnname:"所属停车场",fieldname:"comname",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false,shide:true},
		{fieldcnname:"余额",fieldname:"balance",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"首单额度",fieldname:"firstorderquota",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"打赏用券额度",fieldname:"rewardquota",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"推荐奖额度",fieldname:"recommendquota",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"停车用券额度",fieldname:"ticketquota",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1.00,"value_name":"无限制"},{"value_no":0,"value_name":"不可用券"},{"value_no":1,"value_name":"1"},{"value_no":2,"value_name":"2"}],twidth:"80" ,height:"",issort:false},
		{fieldcnname:"在岗状态",fieldname:"online_flag",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":23,"value_name":"在岗"}] ,twidth:"80" ,height:"",issort:false},
		{fieldcnname:"审核人",fieldname:"collector_auditor",fieldvalue:'',inputtype:"select",noList:getauditor(),action:"",twidth:"100" ,height:"",issort:false},
		{fieldcnname:"推荐审核",fieldname:"recommend",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"推荐审核"}] ,twidth:"80" ,height:"",issort:false,fhide:true}
	];
var _editField = [
		
		{fieldcnname:"首单额度",fieldname:"firstorderquota",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"打赏用券额度",fieldname:"rewardquota",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"推荐奖额度",fieldname:"recommendquota",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"停车用券额度",fieldname:"ticketquota",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1.00,"value_name":"无限制"},{"value_no":0,"value_name":"不可用券"},{"value_no":1,"value_name":"1"},{"value_no":2,"value_name":"2"}],twidth:"80" ,height:"",issort:false}
	];
var _collectorT = new TQTable({
	tabletitle:"停车员管理",
	//ischeck:false,
	tablename:"collector_tables",
	dataUrl:"collector.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
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
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},{ dname:  "发送短信", icon: "sendsms.gif", onpress:function(Obj){
		var sids = _collectorT.GS();
		var ids="";
		if(!sids){
			T.loadTip(1,"请先选择手机",2,"");
			return;
		}
		/* else {
			ids=sids.split(",");
			if(ids.length>100){
				T.loadTip(1,"手机号码最多100个",2,"");
				return;
			}
		} */
		Twin({Id:"send_message_w",Title:"发送短信",Width:550,sysfun:function(tObj){
			Tform({
				formname: "send_message_ff",
				formObj:tObj,
				recordid:"id",
				suburl:"collector.do?action=sendmesg",
				method:"POST",
				Coltype:2,
				dbuttonname:["发送"],
				formAttr:[{
					formitems:[{kindname:"",kinditemts:[
					{fieldcnname:"停车员编号",fieldname:"ids",fieldvalue:sids,inputtype:"multi",height:"80",edit:false},
					{fieldcnname:"发送内容",fieldname:"message",fieldvalue:'',inputtype:"multi",height:"100",issort:false}]}]
				}],
				buttons : [//工具
					{name: "cancel", dname: "取消", tit:"取消发送",icon:"cancel.gif", onpress:function(){TwinC("send_message_w");} }
				],
				Callback:function(f,rcd,ret,o){
					if(ret=="1"){
						T.loadTip(1,"发送成功！",2,"");
						TwinC("send_message_w");
					}else{
						T.loadTip(1,"发送失败",2,o);
					}
				}
			});	
			}
		})
		
	}},
	{dname:"提现停车员",icon:"edit_add.png",onpress:function(Obj){
			_collectorT.C({
				cpage:1,
				tabletitle:"提现停车员",
				extparam:"&action=withdraw"
			})}
		
	},
	{dname:"待审核停车员",icon:"edit_add.png",onpress:function(Obj){
		location = "collector.do?action=validateuser";
		}
	},
	{dname:"待绑定停车员账户",icon:"edit_add.png",onpress:function(Obj){
		location = "collector.do?action=useraccount";
		}
	}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_collectorT.tc.tableitems,function(o,j){
			o.fieldvalue = _collectorT.GD(id)[j]
		});
		Twin({Id:"collector_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "collector_edit_f",
					formObj:tObj,
					recordid:"collector_id",
					suburl:"collector.do?action=editquota&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_collectorT.tc.tableitems}],
						//rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("member_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("collector_edit_"+id);
							_collectorT.M();
						}else{
							T.loadTip(1,"编辑失败！",2,o)
						}
					}
				});	
			}
		})
	}});
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("collector.do?action=delcollector","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_collectorT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	
	bts.push({name:"",
		rule:function(id){
			var state =_collectorT.GD(id,"state");
			if(state==1){
				this.name="启用";
			}else{
				this.name="禁用";
			}
			return true;
		},
		fun:function(id){
		var state =_collectorT.GD(id,"state");
		var vname = _collectorT.GD(id,"nickname");
		var type = "禁用";
		if(state==1){
			type = "启用";
		}
		Tconfirm({
			Title:"提示信息",
			Ttype:"alert",
			Content:"警告：您确认要 <font color='red'>"+type+"</font> "+vname+"吗？",
			OKFn:function(){
			T.A.sendData("collector.do?action=delete&id="+id+"&state="+state,"GET","",
				function(ret){
					if(ret=="1"){
						T.loadTip(1,type+"成功！",2,"");
						_collectorT.C();
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
_collectorT.C();
</script>

</body>
</html>
