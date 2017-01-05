<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>泊车订单管理</title>
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
<div id="ctordersobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
function getCstops(){
	return eval(T.A.sendData("carstopsprice.do?action=getcids"));
}
function getUids(){
	return eval(T.A.sendData("ctorders.do?action=getuids"));
}
var cstops = getCstops();
var states = [{"value_no":-1,"value_name":"全部"},
              {"value_no":0,"value_name":"车主泊车请求"},
              {"value_no":1,"value_name":"泊车员已响应泊车"},
              {"value_no":2,"value_name":"正在泊车"},
              {"value_no":3,"value_name":"泊车完成"},
              {"value_no":4,"value_name":"车主取车请求"},
              {"value_no":5,"value_name":"泊车员已响应取车"},
              {"value_no":6,"value_name":"泊车员正在取车"},
              {"value_no":7,"value_name":"等待支付"},
              {"value_no":8,"value_name":"支付成功"},
              {"value_no":9,"value_name":"订单取消"}
              ];
var paytype = [{"value_no":-1,"value_name":"全部"},
               {"value_no":0,"value_name":"现金"},
               {"value_no":1,"value_name":"余额"},
               {"value_no":2,"value_name":"微信"},
               {"value_no":3,"value_name":"余额+微信"}
               ];
var uids = getUids();

var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"泊车点",fieldname:"cid",inputtype:"select",noList:cstops, twidth:"150" ,issort:false},
		{fieldcnname:"车牌",fieldname:"car_number",inputtype:"text", twidth:"70" ,issort:false},
		{fieldcnname:"车主泊车下单",fieldname:"start_time",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"泊车时间",fieldname:"btime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"接车人",fieldname:"buid",inputtype:"select",noList:uids,twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"车主取车下单 ",fieldname:"end_time",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"取车时间 ",fieldname:"etime",inputtype:"date", twidth:"128" ,issort:false,edit:false},
		{fieldcnname:"取车人",fieldname:"euid",inputtype:"select", noList:uids,twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"状态 ",fieldname:"state",inputtype:"select", twidth:"100",noList: states,issort:false},
		{fieldcnname:"金额",fieldname:"amount",inputtype:"text", twidth:"50" ,issort:false},
		{fieldcnname:"泊车照片",fieldname:"pic",inputtype:"date", twidth:"150" ,edit:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<a href='#' onclick='viewpic(\""+value+"\")'>"+value+"</a>";
				}else
					return value;
			}
		},
		{fieldcnname:"泊车位置",fieldname:"car_local",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"车钥匙编号",fieldname:"keyno",inputtype:"text",twidth:"80" ,issort:false},
		{fieldcnname:"支付方式",fieldname:"pay_type",inputtype:"select",noList:paytype,twidth:"80" ,issort:false}
	];
var rules =[{name:"strid",type:"ajax",url:"ctorders.do?action=check&value=",requir:true,warn:"账号已存在！",okmsg:""}];
function viewpic(name){
	var url = 'viewpic.html?name='+name;
	Twin({Id:"ctorders_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
var _ctordersT = new TQTable({
	tabletitle:"泊车订单管理",
	ischeck:false,
	tablename:"ctorders_tables",
	dataUrl:"ctorders.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#ctordersobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		Twin({Id:"carstopsorder_search_w",Title:"搜索泊车订单",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "carstopsorder_search_f",
					formObj:tObj,
					formWinId:"carstopsorder_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消搜索",icon:"cancel.gif", onpress:function(){TwinC("carstopsorder_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_ctordersT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}}
	]
//	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"",
		rule:function(id){
			var state =_ctordersT.GD(id,"state");
			if(state==8){
				this.name="   ";
			}else if(state>2&&state!=9)
				this.name="现金支付";
			else 
				this.name=" ";
			return true;
		},
		fun:function(id){
			var car_number = _ctordersT.GD(id,"car_number");
			var state =_ctordersT.GD(id,"state");
			var money = eval(T.A.sendData("ctorders.do?action=getmoney&id="+id));
			if(state>2&&state<8){
				Twin({Id:"send_money_"+id,Title:"结算泊车费_"+car_number,Width:310,sysfunI:id,sysfun:function(id,tObj){
					Tform({
						formname: "send_money_f",
						formObj:tObj,
						recordid:"id",
						suburl:"ctorders.do?action=paymoney&id="+id+"&state="+state,
						method:"POST",
						dbuttonname:["确定结算"],
						Coltype:2,
						formAttr:[{
							formitems:[{kindname:"",kinditemts:[
								{fieldcnname:"结算金额",fieldname:"amount",fieldvalue:money,inputtype:"text", twidth:"200" ,height:"",issort:false}]}]
						}],
						buttons : [//工具
							{name: "cancel", dname: "取消", tit:"取消成功",icon:"cancel.gif", onpress:function(){TwinC("send_money_"+id);} }
						],
						Callback:
						function(f,rcd,ret,o){
							if(ret=="1"){
								T.loadTip(1,"发放成功！",2,"");
								TwinC("send_money_"+id);
								_ctordersT.M()
							}else{
								T.loadTip(1,ret,2,"");
								TwinC("send_money_"+id);
							}
						}
					});	
				}
			})
			}else{
				Tconfirm({
					Title:"提示信息",
					Ttype:"alert",
					Content:"警告：您确认要把<font color='red'>"+car_number+"</font> 改为现金支付吗？",
					OKFn:function(){
						T.A.sendData("ctorders.do?action=paymoney&id="+id+"&state="+state,"GET","",
							function(ret){
								if(ret=="1"){
									T.loadTip(1,"修改成功！",2,"");
									_ctordersT.C();
								}else{
									T.loadTip(1,"操作失败，请重试！",2,"")
								}
							},0,null)
					}
				});
			}
				
		}
	});
		bts.push({name:"",
		rule:function(id){
			var state =_ctordersT.GD(id,"state");
			if(state<2)
				this.name="取消";
			else 
				this.name="   ";
		
			return true;
		},
		fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认取消吗",Content:"确认取消吗",OKFn:function(){T.A.sendData("attendant.do?action=cancelorder","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="0"||ret=="1"){
					T.loadTip(1,"取消成功！",2,"");
					_ctordersT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_ctordersT.C();
</script>

</body>
</html>
