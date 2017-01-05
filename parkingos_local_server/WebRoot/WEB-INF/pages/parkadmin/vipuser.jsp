<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>会员管理</title>
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
<div id="vipuserobj" style="width:100%;height:100%;margin:0px;"></div>
<iframe src="" id ="exportiframe" frameborder="0" style="width:0px;height:0px;"></iframe>
<script language="javascript">
var role=${role};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var comid = ${comid};
var pnames= eval(T.A.sendData("getdata.do?action=getpname&comid="+comid));
var month_select = [];
for(var i=1;i<36;i++){
	month_select.push({"value_no":i,"value_name":i});
}
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"number", twidth:"60" ,height:"",issort:false,edit:false},
		{fieldcnname:"包月产品名称",fieldname:"p_name",fieldvalue:'',inputtype:"select",noList:pnames, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车主手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车主账户",fieldname:"uin",fieldvalue:'',inputtype:"number", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"名字",fieldname:"name",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"地址",fieldname:"address",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false},
		{fieldcnname:"车牌号码",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"230" ,height:"",issort:false,edit:false,
			process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
					return setname(trId,'car_number');
				}},
		{fieldcnname:"购买时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"150" ,height:""},
		{fieldcnname:"开始时间",fieldname:"b_time",fieldvalue:'',inputtype:"date",twidth:"150" ,height:"",issort:false},
		{fieldcnname:"结束时间",fieldname:"e_time",fieldvalue:'',inputtype:"date", twidth:"150" ,height:"",edit:false},
		{fieldcnname:"金额",fieldname:"total",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",edit:false},
		{fieldcnname:"月数",fieldname:"months",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",edit:false,fhide:true,shide:true},
		{fieldcnname:"备注",fieldname:"remark",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false}
	];
var _addField = [
		{fieldcnname:"包月产品",fieldname:"p_name",fieldvalue:'',inputtype:"select", noList:pnames,twidth:"180" ,height:"",issort:false},
		{fieldcnname:"车主手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"名字(选填)",fieldname:"name",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
		{fieldcnname:"地址(选填)",fieldname:"address",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false},
		//{fieldcnname:"车牌号码",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"起始时间",fieldname:"b_time",fieldvalue:'',inputtype:"sdate",twidth:"150" ,height:"",issort:false},
		{fieldcnname:"购买月数",fieldname:"months",fieldvalue:'',inputtype:"select",noList:month_select, twidth:"150" ,height:"",issort:false},
		//{fieldcnname:"金额",fieldname:"total",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		//{fieldcnname:"车牌已存在时",fieldname:"flag",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"提示车牌不一样"},{"value_no":1,"value_name":"保存现车牌"}], twidth:"180" ,height:"",issort:false}
		{fieldcnname:"备注",fieldname:"remark",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false}
	];
var _editField = [
         		{fieldcnname:"包月产品",fieldname:"p_name",fieldvalue:'',inputtype:"select", noList:pnames,twidth:"180" ,height:"",issort:false},
         		{fieldcnname:"车主手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false,edit:false},
         		{fieldcnname:"名字(选填)",fieldname:"name",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false},
				{fieldcnname:"地址(选填)",fieldname:"address",fieldvalue:'',inputtype:"text", twidth:"180" ,height:"",issort:false},
         		//{fieldcnname:"车牌号码",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
         		{fieldcnname:"起始时间",fieldname:"b_time",fieldvalue:'',inputtype:"sdate",twidth:"150" ,height:"",issort:false},
         		{fieldcnname:"购买月数",fieldname:"months",fieldvalue:'',inputtype:"select",noList:month_select, twidth:"150" ,height:"",issort:false},
         		//{fieldcnname:"金额",fieldname:"total",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
         		//{fieldcnname:"车牌已存在时",fieldname:"flag",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"提示车牌不一样"},{"value_no":1,"value_name":"保存现车牌"}], twidth:"180" ,height:"",issort:false}
         		{fieldcnname:"备注",fieldname:"remark",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false}
         	];
var rules =[{name:"p_name",type:"",url:"",requir:true,warn:"请选择产品",okmsg:""},
			{name:"mobile",type:"ajax",url:"vipuser.do?action=checkmobile&mobile=",requir:true,warn:"请填写用户真实手机号码，这样车主用手机号下载登录停车宝后，每新增一个用户奖励车场5元",okmsg:""},
			{name:"car_number",type:"",url:"",requir:true,warn:"请输入车牌",okmsg:""},
			{name:"b_time",type:"",url:"",requir:true,warn:"请选择时间",okmsg:""}
			//{name:"total",type:"",url:"",requir:true,warn:"请输入金额",okmsg:""}
			];
var _vipuserT = new TQTable({
	tabletitle:"VIP会员管理",
	ischeck:false,
	tablename:"vipuser_tables",
	dataUrl:"vipuser.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query&comid="+comid,
	tableObj:T("#vipuserobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	if(parseInt(role)==15||parseInt(role)==3){
		return [{dname:"注册会员 ",icon:"edit_add.png",onpress:function(Obj){
		T.each(_vipuserT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"vipuser_add",Title:"注册会员<font style='color:red;'>（填写真实车主手机，车主下载登录停车宝立即奖励车场5元！）</font>",Width:550,sysfun:function(tObj){
				Tform({
					formname: "vipuser_add_f",
					formObj:tObj,
					formWinId:"vipuser_add_w",
					formFunId:tObj,
					recordid:"id",
					Coltype:2,
					suburl:"vipuser.do?action=create&comid="+comid,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_addField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("vipuser_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("vipuser_add");
							_vipuserT.M();
						}else if(ret==-1){
							T.loadTip(1,"添加失败！",2,"");
						}else if(ret==-2){
						 	T.loadTip(2,"产品已超出有效期，请重新选择产品或更改购买月数！",7,"");
						}else 
							T.loadTip(2,ret,7,"");
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_vipuserT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"vipuser_search_w",Title:"搜索会员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "vipuser_search_f",
					formObj:tObj,
					formWinId:"vipuser_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("vipuser_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_vipuserT.C({
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
	}else{
		return [{dname:"注册会员 ",icon:"edit_add.png",onpress:function(Obj){
		T.each(_vipuserT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"vipuser_add",Title:"注册会员<font style='color:red;'>（填写真实车主手机，车主下载登录停车宝立即奖励车场5元！）</font>",Width:550,sysfun:function(tObj){
				Tform({
					formname: "vipuser_add_f",
					formObj:tObj,
					formWinId:"vipuser_add_w",
					formFunId:tObj,
					recordid:"id",
					Coltype:2,
					suburl:"vipuser.do?action=create&comid="+comid,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_addField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("vipuser_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("vipuser_add");
							_vipuserT.M();
						}else if(ret==-1){
							T.loadTip(1,"添加失败！",2,"");
						}else if(ret==-2){
						 	T.loadTip(2,"产品已超出有效期，请重新选择产品或更改购买月数！",7,"");
						}else 
							T.loadTip(2,ret,7,"");
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_vipuserT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"vipuser_search_w",Title:"搜索会员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "vipuser_search_f",
					formObj:tObj,
					formWinId:"vipuser_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("vipuser_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_vipuserT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&comid="+comid+"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},{dname:"导出会员",icon:"toxls.gif",onpress:function(Obj){
	
		Twin({Id:"vipuser_export_w",Title:"导出会员<font style='color:red;'>（如果没有设置，默认全部导出!）</font>",Width:480,sysfun:function(tObj){
				 TSform ({
					formname: "vipuser_export_f",
					formObj:tObj,
					formWinId:"vipuser_export_w",
					formFunId:tObj,
					dbuttonname:["确认导出"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[{fieldcnname:"包月产品名称",fieldname:"p_name",fieldvalue:'',inputtype:"select",noList:pnames}
						,{fieldcnname:"购买时间",fieldname:"create_time",fieldvalue:'',inputtype:"date"},{fieldcnname:"开始时间",fieldname:"b_time",fieldvalue:'',inputtype:"date"}
						,{fieldcnname:"结束时间",fieldname:"e_time",fieldvalue:'',inputtype:"date"}
						]}],
					}],
					//formitems:[{kindname:"",kinditemts:_excelField}],
					SubAction:
					function(callback,formName){
						T("#exportiframe").src="vipuser.do?action=exportExcel&comid="+comid+"&rp="+2147483647+"&fieldsstr="+"id__p_name__mobile__uin__car_number__create_time__b_time__e_time__total__months__remark"+Serializ(formName)
						TwinC("vipuser_export_w");
						T.loadTip(1,"正在导出，请稍候...",2,"");
					}
				});	
			}
		})
	}}
	]
	}
	
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		var p_name = _vipuserT.GD(id,"p_name");
		var mobile = _vipuserT.GD(id,"mobile");
		var name = _vipuserT.GD(id,"name");
		var address = _vipuserT.GD(id,"address");
		//var car_number = _vipuserT.GD(id,"car_number");
		var b_time = _vipuserT.GD(id,"b_time");
		var months = _vipuserT.GD(id,"months");
		var remark = _vipuserT.GD(id,"remark");
		
		Twin({Id:"vipuser_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "vipuser_edit_f",
					formObj:tObj,
					recordid:"vipuser_id",
					suburl:"vipuser.do?comid="+comid+"&action=edit&id="+id,
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_editField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("vipuser_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("vipuser_edit_"+id);
							_vipuserT.M();
						}else if(ret==-1){
							T.loadTip(1,"编辑失败！",2,"");
						}else if(ret==-2){
						 	T.loadTip(2,"产品已超出有效期，请重新选择产品或更改购买月数！",7,"");
						}else 
							T.loadTip(2,ret,7,"");
					}
				});	
				T("#vipuser_edit_f_p_name").value=p_name;
				T("#vipuser_edit_f_mobile").value=mobile;
				T("#vipuser_edit_f_name").value=name;
				T("#vipuser_edit_f_address").value=address;
				//T("#vipuser_edit_f_car_number").value=car_number;
				T("#vipuser_edit_f_b_time").value=b_time;
				T("#vipuser_edit_f_months").value=months;
				T("#vipuser_edit_f_remark").value=remark;
			}
		})
	}}); 
	bts.push({name:"修改车牌",fun:function(id){
		var uin =_vipuserT.GD(id,"uin");
		var cars = T.A.sendData("vipuser.do?action=getcar&uin="+uin);
		Twin({Id:"vipuser_addcar_"+id,Title:"修改车牌",Width:450,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "vipuser_addcar_f",
					formObj:tObj,
					recordid:"vipuser_id",
					suburl:"vipuser.do?action=addcar&uin="+uin,
					method:"POST",
					Coltype:2,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[{fieldcnname:"车牌号码(多个车牌，用,隔开)",fieldname:"carnumber",inputtype:"text",width:"300"}]}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加车牌",icon:"cancel.gif", onpress:function(){TwinC("vipuser_addcar_"+id);} }
					],
					Callback:function(f,rcd,ret,o){
						if(parseInt(ret)>0){
							T.loadTip(1,"修改了"+ret+"个车牌！",2,"");
							TwinC("vipuser_addcar_"+id);
							_vipuserT.M();
						}else {
							T.loadTip(2,ret,7,"");
						}
					}
				});	
			}
		})
		T("#vipuser_addcar_f_carnumber").value=cars;
	}});
	bts.push({name:"删除",fun:function(id){
		var id_this = id ;
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("vipuser.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_vipuserT.M();
				}else if(ret=="-1"){
					T.loadTip(1,"此车主已购买了其它车场包月产品",2,"");
				}else if(ret=="-2"){
					T.loadTip(1,"此车主不能删除，月卡已删除",2,"");
				}else if(ret=="-3"){
					T.loadTip(1,"删除车主失败，月卡已删除",2,"");
				}else {
					T.loadTip(1,ret,2,"");
				}
				_vipuserT.M();
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}

function setname(pid,colname){
	var uin = _vipuserT.GD(pid,"uin");
	T.A.C({
		url:"vipuser.do?action=getcar&uin="+uin,
	  		method:"GET",//POST or GET
	  		param:"",//GET时为空
	  		async:false,//为空时根据是否有回调函数(success)判断
	  		dataType:"0",//0text,1xml,2obj
	  		success:function(ret,tipObj,thirdParam){
	  			if(ret&&ret!='null'){
					updateRow(pid,colname,ret);
	  			}
		},//请求成功回调function(ret,tipObj,thirdParam) ret结果
	  		failure:function(ret,tipObj,thirdParam){
			return false;
		},//请求失败回调function(null,tipObj,thirdParam) 默认提示用户<网络失败>
	  		thirdParam:"",//回调函数中的第三方参数
	  		tipObj:null,//相关提示父级容器(值为字符串"notip"时表示不进行相关提示)
	  		waitTip:"正在获取审核数据...",
	  		noCover:true
	})
	return "<font style='color:#666'>获取中...</font>";
}

/*更新表格内容*/
function updateRow(rowid,name,value){
	//alert(value);
	if(value)
		_vipuserT.UCD(rowid,name,value);
}

_vipuserT.C();
</script>

</body>
</html>
