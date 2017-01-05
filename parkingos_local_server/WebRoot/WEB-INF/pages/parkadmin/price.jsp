<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>价格管理</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.form.js?08555" type="text/javascript">//表单</script>
<script src="js/tq.searchform.js?0817" type="text/javascript">//查询表单</script>
<script src="js/tq.window.js?0817" type="text/javascript">//弹窗</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
</head>

<body>
<div id="priceobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var assist_unit=${assist_unit};
var assist_price=${assist_price};
var assist_id=${assist_id};
if(parseInt(role)==15||parseInt(role)==3){
	window.onload = jslimit()
}
var comid = ${comid};
function getCarType(){
	var cartypes = eval(T.A.sendData("price.do?action=getcartypes&comid="+comid));
	return cartypes;
}
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"类型",fieldname:"pay_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"按时段"},{"value_no":1,"value_name":"按次"}] ,twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开始小时",fieldname:"b_time",fieldvalue:'',defaultValue:'7||7',inputtype:"hour", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"结束小时",fieldname:"e_time",fieldvalue:'',defaultValue:'21||21',inputtype:"hour", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"时间单位(分钟)",fieldname:"unit",fieldvalue:'',defaultValue:'15',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"单价",fieldname:"price",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"首优惠时段",fieldname:"first_times",fieldvalue:'',defaultValue:'0||0',inputtype:"fminute", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"首优惠价格",fieldname:"fprice",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"24小时封顶价",fieldname:"total24",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,
			process:function(value,pid){
				if(parseFloat(value)>0)
					return value;
				else
					return "无封顶";
			}
		},
		//{fieldcnname:"零头计费时长",fieldname:"countless",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',defaultValue:'有效||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":1,"value_name":"禁用"},{"value_no":0,"value_name":"有效"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"是否可编辑",fieldname:"isedit",fieldvalue:'',defaultValue:'否||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"可编辑"}] , twidth:"100" ,height:"",issort:false},
		//{fieldcnname:"是否优惠",fieldname:"is_sale",fieldvalue:'',defaultValue:'否||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"免费时长",fieldname:"free_time",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"超免费时长计费方式",fieldname:"fpay_type",fieldvalue:'',defaultValue:'收费||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"收费"},{"value_no":1,"value_name":"免费"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"是否补全日间时长",fieldname:"is_fulldaytime",fieldvalue:'',defaultValue:'补全||0',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"补全"},{"value_no":1,"value_name":"不补全"}] , twidth:"100" ,height:"",issort:false},
		{fieldcnname:"创建时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",edit:false},
		{fieldcnname:"区分大小车",fieldname:"car_type",fieldvalue:'',defaultValue:'通用||0',inputtype:"select", noList:getCarType() , twidth:"100" ,height:"",issort:false}
	];
var _assistField = [
		//{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false,edit:false},
		//{fieldcnname:"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",fieldname:"id",fieldvalue:'',inputtype:"", twidth:"100" ,height:"",issort:false,edit:false},
		{fieldcnname:"首计费时长（分钟）",fieldname:"assist_unit",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"首时长价格",fieldname:"assist_price",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
	];
var rules =[{name:"price",type:"",url:"",requir:true,warn:"",okmsg:""}];
var _priceT = new TQTable({
	tabletitle:"价格管理",
	ischeck:false,
	tablename:"price_tables",
	dataUrl:"price.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery&comid="+comid,
	tableObj:T("#priceobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"添加价格",icon:"edit_add.png",onpress:function(Obj){
		T.each(_priceT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"price_add",Title:"添加价格",Width:550,sysfun:function(tObj){
				Tform({
					formname: "price_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"price.do?action=create&comid="+comid,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("price_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("price_add");
							_priceT.M();
						}else if(ret==0){
							T.loadTip(1,"添加失败！请稍候再试！",2,"");
						}else{
							T.loadTip(1,"添加失败！",2,"");
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_priceT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"price_search_w",Title:"搜索收费员",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "price_search_f",
					formObj:tObj,
					formWinId:"price_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("price_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_priceT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&comid="+comid+"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},{dname:"编辑辅助价格",icon:"edit_add.png",onpress:function(Obj){
		//$("#assist_unit").text() = assist_unit;
		//$("#assist_price").text() = assist_price;
		Twin({Id:"price_add",Title:"添加辅助价格&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='red'>(辅助价格仅支持全天一个价格模式，不支持多段计价)</font>",Width:550,sysfun:function(tObj){
				Tform({
					formname: "price_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"price.do?action=createassist&comid="+comid+"&assist_id="+assist_id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_assistField}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("price_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						var r = ret;
						T.loadTip(1,r.split(",")[0],2,"");
						assist_unit = r.split(",")[1];
						assist_price = r.split(",")[2];
					}
				});	
			}
		})
		document.getElementById("price_edit_f_assist_unit").value=assist_unit;
		document.getElementById("price_edit_f_assist_price").value=assist_price;
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_priceT.tc.tableitems,function(o,j){
			o.fieldvalue = _priceT.GD(id)[j]
		});
		Twin({Id:"price_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "price_edit_f",
					formObj:tObj,
					recordid:"price_id",
					suburl:"price.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_priceT.tc.tableitems}],
						rules:rules
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("price_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("price_edit_"+id);
							_priceT.M()
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
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("price.do?action=delete","post","selids="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_priceT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
_priceT.C();
</script>

</body>
</html>
