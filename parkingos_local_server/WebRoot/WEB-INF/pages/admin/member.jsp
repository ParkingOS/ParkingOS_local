<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>收费员管理</title>
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
<div id="memberobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var comid = ${comid};
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"登录账号",fieldname:"strid",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"首单额度",fieldname:"firstorderquota",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false},
		{fieldcnname:"打赏用券额度",fieldname:"rewardquota",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"推荐奖额度",fieldname:"recommendquota",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"停车用券额度",fieldname:"ticketquota",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1.00,"value_name":"无限制"},{"value_no":0,"value_name":"不可用券"},{"value_no":1,"value_name":"1"},{"value_no":2,"value_name":"2"}],twidth:"80" ,height:"",issort:false},
		{fieldcnname:"电话",fieldname:"phone",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"角色",fieldname:"auth_flag",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":1,"value_name":"管理员"},{"value_no":2,"value_name":"收费员"},{"value_no":3,"value_name":"财务"},{"value_no":15,"value_name":"月卡操作员"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"创建时间",fieldname:"reg_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",edit:false},
		{fieldcnname:"最近登录时间",fieldname:"logon_time",fieldvalue:'',inputtype:"date", twidth:"130" ,height:"",edit:false},
		{fieldcnname:"收费",fieldname:"isview",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"不可收费"},{"value_no":1,"value_name":"可收费"}] , twidth:"60" ,height:"",issort:false}
	];
var rules =[{name:"strid",type:"ajax",url:"member.do?action=check&value=",requir:true,warn:"账号已存在！",okmsg:""}];
var _memberT = new TQTable({
	tabletitle:"员工管理",
	ischeck:false,
	tablename:"member_tables",
	dataUrl:"member.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery&comid="+comid,
	tableObj:T("#memberobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"注册员工",icon:"edit_add.png",onpress:function(Obj){
		T.each(_memberT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"member_add",Title:"添加员工",Width:550,sysfun:function(tObj){
				Tform({
					formname: "member_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"member.do?action=create&comid="+comid,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("member_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("member_add");
							_memberT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_memberT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"member_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "member_search_f",
					formObj:tObj,
					formWinId:"member_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("member_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_memberT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&comid="+comid+"&action=query&"+Serializ(formName)
						})
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
		T.each(_memberT.tc.tableitems,function(o,j){
			o.fieldvalue = _memberT.GD(id)[j]
		});
		Twin({Id:"member_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "member_edit_f",
					formObj:tObj,
					recordid:"member_id",
					suburl:"member.do?comid="+comid+"&action=adminedit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_memberT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("member_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("member_edit_"+id);
							_memberT.M();
						}else if(ret=="2"){
							T.loadTip(1,"一个车场只能有一个管理员，请编辑之前的管理员为其他角色",30,"");
							TwinC("member_edit_"+id);
							_memberT.M();
						}else{
							T.loadTip(1,"编辑失败！",2,o)
						}
					}
				});	
			}
		})
	}});
	bts.push({name:"不可收费",
		rule:function(id){
				var state =_memberT.GD(id,"isview");
				if(state==1){
					this.name="不可收费";
				}else{
					this.name=" &nbsp;<font color='red'>可收费</font>&nbsp;      ";
				}
				return true;
			},
		tit:"设置是否可以收费",
		fun:function(id){
			var state =_memberT.GD(id,"isview");
			var type = "可收费";
			var isview = 1;
			if(state==1){
				type = "不可收费";
				isview = 0;
			}
			Tconfirm({
				Title:"提示信息",
				Ttype:"alert",
				Content:"警告：您确认要 <font color='red'>"+type+"</font> 吗？",
				OKFn:function(){
				T.A.sendData("member.do?action=isview&id="+id+"&isview="+isview,"GET","",
					function(ret){
						if(ret=="1"){
							T.loadTip(1,"设置"+type+"成功！",2,"");
							_memberT.C();
						}else{
							T.loadTip(1,"操作失败，请重试！",2,"")
						}
					},0,null)
				}
			});
		}});
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("member.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_memberT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	
	bts.push({name:"修改密码",fun:function(id){
		T.each(_memberT.tc.tableitems,function(o,j){
			o.fieldvalue = _memberT.GD(id)[j]
		});
		Twin({Id:"member_pass_"+id,Title:"修改密码",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "member_pass_f",
					formObj:tObj,
					recordid:"member_id",
					suburl:"member.do?action=editpass&comid="+comid+"&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[
							{fieldcnname:"新密码",fieldname:"newpass",fieldvalue:'',inputtype:"password", twidth:"200" ,height:"",issort:false},
							{fieldcnname:"确认密码",fieldname:"confirmpass",fieldvalue:'',inputtype:"password", twidth:"200" ,height:"",issort:false}]}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消成功",icon:"cancel.gif", onpress:function(){TwinC("member_pass_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"修改成功！",2,"");
							TwinC("member_pass_"+id);
							_memberT.M()
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
_memberT.C();
</script>

</body>
</html>
