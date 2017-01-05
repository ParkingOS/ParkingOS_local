<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>客户管理</title>
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
<div id="carowerobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
function getbonustypes (){
	var bonustypes = eval(T.A.sendData("getdata.do?action=getbonustypes"));
	return bonustypes;
}
var bonustypes =getbonustypes();
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"50" ,height:"",hide:true},
		//{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车牌",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"注册日期",fieldname:"reg_time",fieldvalue:'',inputtype:"date", twidth:"140" ,height:"",issort:false},
		{fieldcnname:"最近登录日期",fieldname:"logon_time",fieldvalue:'',inputtype:"date", twidth:"140" ,height:"",issort:false},
		{fieldcnname:"媒体来源",fieldname:"media",fieldvalue:'',inputtype:"select", noList:bonustypes ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"微信公众号",fieldname:"wxp_openid",fieldvalue:'',inputtype:"text", twidth:"250" ,height:"",issort:false},
		{fieldcnname:"是否有车牌",fieldname:"hascarnum",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"有车牌"},{"value_no":1,"value_name":"无车牌"}] ,twidth:"200" ,height:"",issort:false,fhide:true},
		{fieldcnname:"客户端",fieldname:"client_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"Android"},{"value_no":1,"value_name":"Ios"}] ,twidth:"80" ,height:"",issort:false},
		{fieldcnname:"版本号",fieldname:"version",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"是否已认证",fieldname:"isauth",fieldvalue:'',inputtype:"select", noList:[{"value_no":-2,"value_name":"无效车牌 "},{"value_no":-1,"value_name":"认证未通过"},{"value_no":0,"value_name":"未认证"},{"value_no":1,"value_name":"已认证"},{"value_no":2,"value_name":"认证中"}] ,twidth:"100" ,height:"",issort:false,shide:true},
		{fieldcnname:"备注",fieldname:"remark",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,shide:true},
		{fieldcnname:"行驶照1",fieldname:"pic_url1",fieldvalue:'',inputtype:"date", twidth:"240" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<a href='#' onclick='viewpic(\""+value+"\")'>"+value+"</a>";
				}else
					return value;
			}},
		{fieldcnname:"行驶照2",fieldname:"pic_url2",fieldvalue:'',inputtype:"text", twidth:"250" ,height:"",issort:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<a href='#' onclick='viewpic(\""+value+"\")'>"+value+"</a>";
				}else
					return value;
			}},
		{fieldcnname:"停车券情况",fieldname:"ticket_state",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"无停车券"},{"value_no":1,"value_name":"停车券将到期"}] ,twidth:"200" ,height:"",issort:false,fhide:true}
//		{fieldcnname:"未使用的停车券数量",fieldname:"id",fieldvalue:'',inputtype:"cselect",noList:[],action:"",twidth:"200" ,height:"",issort:false,shide:true,
//			process:function(value,pid){
//				return setcname(value,pid,'id');
//				return "<a href=# onclick=\"viewdetail('hn','"+setcname(value,pid,'id')+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
//			}}
	];
var _carowerT = new TQTable({
	tabletitle:"会员管理",
	//ischeck:false,
	tablename:"carower_tables",
	dataUrl:"carower.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#carowerobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function viewpic(name){
	var url = 'viewpic.html?name='+name+'&db=user_dirvier_pics'+'&r='+Math.random();
	Twin({Id:"carstops_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
function setcname(value,pid,colname){
	if(value&&value!='-1'&&value!=''){
		T.A.C({
			url:"camera.do?action=getname&passid="+value,
    		method:"GET",//POST or GET
    		param:"",//GET时为空
    		async:false,//为空时根据是否有回调函数(success)判断
    		dataType:"0",//0text,1xml,2obj
    		success:function(ret,tipObj,thirdParam){
    			if(ret){
					updateRow(pid,colname,ret);
    			}
				else
					updateRow(pid,colname,value);
			},//请求成功回调function(ret,tipObj,thirdParam) ret结果
    		failure:function(ret,tipObj,thirdParam){
				return false;
			},//请求失败回调function(null,tipObj,thirdParam) 默认提示用户<网络失败>
    		thirdParam:"",//回调函数中的第三方参数
    		tipObj:null,//相关提示父级容器(值为字符串"notip"时表示不进行相关提示)
    		waitTip:"正在获取券个数...",
    		noCover:true
		})
	}else{
		return "无"
	};
	return "<font style='color:#666'>获取中...</font>";
}

/*更新表格内容*/
function updateRow(rowid,name,value){
	//alert(value);
	if(value)
	_carowerT.UCD(rowid,name,value);
}

function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_carowerT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"carower_search_w",Title:"搜索客户",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "carower_search_f",
					formObj:tObj,
					formWinId:"carower_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("carower_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_carowerT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{ dname:  "发送短信", icon: "sendsms.gif", onpress:function(Obj){
		var sids = _carowerT.GS();
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
				suburl:"carower.do?action=sendmesg",
				method:"POST",
				Coltype:2,
				dbuttonname:["发送"],
				formAttr:[{
					formitems:[{kindname:"",kinditemts:[
					{fieldcnname:"客户编号",fieldname:"ids",fieldvalue:sids,inputtype:"multi",height:"80",edit:false},
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
	{ dname:  "删除测试账户", icon: "sendsms.gif", onpress:function(Obj){
		Twin({Id:"delete_user_w",Title:"删除测试账户",Width:550,sysfun:function(tObj){
			Tform({
				formname: "delete_user_f",
				formObj:tObj,
				recordid:"id",
				suburl:"carower.do?action=deleteuser",
				method:"POST",
				Coltype:2,
				dbuttonname:["删除"],
				formAttr:[{
					formitems:[{kindname:"",kinditemts:[
					{fieldcnname:"手机号码",fieldname:"mobiles",fieldvalue:'',inputtype:"checkbox",height:"80",noList:[{"value_no":"13641309140","value_name":"13641309140"},{"value_no":"15210810614","value_name":"15210810614"},{"value_no":"15210932334","value_name":"15210932334"},{"value_no":"13331000064","value_name":"13331000064"},{"value_no":"15801482643","value_name":"15801482643"}]}]}]
				}],
				buttons : [//工具
					{name: "cancel", dname: "取消", tit:"取消发送",icon:"cancel.gif", onpress:function(){TwinC("delete_user_w");} }
				],
				Callback:function(f,rcd,ret,o){
					if(ret=="1"){
						T.loadTip(1,"删除成功！",2,"");
						TwinC("delete_user_w");
					}else{
						T.loadTip(1,"发送失败",2,o);
					}
				}
			});	
			}
		})
		
	}},
	{dname:"已审核车牌",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do?type=1";
	}},
	{dname:"待审核车牌",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do?type=2";
	}},
	{dname:"审核未通过车牌",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do?type=-1";
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"认证通过",fun:function(id){
		var state =_carowerT.GD(id,"is_auth");
		var car_number = _carowerT.GD(id,"car_number");
		if(state==1){//状态：可用
			T.loadTip(1,"已认证过，不能重复认证！",2,"");
			return ;
		}
		if('${userid}'!='huxuelian'){
			T.loadTip(1,"您没有权限！",2,"");
			return ;
		}
		Tconfirm({Title:"认证通过",Content:"确认认证通过吗",OKFn:function(){T.A.sendData("carower.do?action=authuser","post","uin="+id+"&car_number="+encodeURI(encodeURI(car_number))+"&isauth=1",
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"认证成功！",2,"");
					_carowerT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	/* bts.push({name:"车牌",fun:function(id){
		Twin({
			Id:"car_number_"+id,
			Title:"车牌信息",Width:350,
			sysfunI:id,
			Content:"<iframe name='car_number_'"+id+" id='car_number_'"+id+" src='carower.do?action=carnumber&id="+id+"' width='100%' height='150' frameborder='0'  style='overflow:hidden;' ></iframe>"
		})
	}});
	bts.push({name:"订单",fun:function(id){
		Twin({
			Id:"car_number_"+id,
			Title:"订单信息",Width:650,
			sysfunI:id,
			Content:"<iframe name='order_'"+id+" id='order_'"+id+" src='carower.do?action=orderinfo&id="+id+"' width='100%' height='150' frameborder='0'  style='overflow:hidden;' ></iframe>"
		})
	}}); 
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("carower.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_carowerT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});*/
	if(bts.length <= 0){return false;}
	return bts;
}
_carowerT.C();
</script>

</body>
</html>
