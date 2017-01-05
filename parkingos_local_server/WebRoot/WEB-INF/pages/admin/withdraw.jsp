<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>提现管理</title>
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
<div id="parkwithdrawobj" style="width:100%;height:100%;margin:0px;"></div>
<iframe src="" id ="exportiframe" frameborder="0" style="width:0px;height:0px;"></iframe>
<script language="javascript">
var role=${role};
var _mediaField = [
		{fieldcnname:"车场名称",fieldname:"cname",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",hide:true},
		{fieldcnname:"提现人",fieldname:"name",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
			/* process:function(value,pid){
				return setcname(value,pid,'uin');
			}}, */
		{fieldcnname:"类型",fieldname:"wtype",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":1,"value_name":"个人提现"},{"value_no":0,"value_name":"公司提现"},{"value_no":2,"value_name":"对公提现"}], twidth:"200" ,height:"",hide:true},
		{fieldcnname:"提现金额",fieldname:"amount",fieldvalue:'',inputtype:"number", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"申请时间",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",hide:true},
		{fieldcnname:"处理日期",fieldname:"update_time",fieldvalue:'',inputtype:"date", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"等待处理"},{"value_no":2,"value_name":"处理中"},{"value_no":3,"value_name":"已支付"},{"value_no":4,"value_name":"提现失败"},{"value_no":5,"value_name":"延迟处理"}], twidth:"200" ,height:"",issort:false
			/* ,process:function(value,trId,colId){//值、行ID(记录ID)、列ID(字段名称)
				if(value!=''&&value!='null'){
					return "<font color='green'>"+value+"</font>";
				}else
					return value;
			} */
			}
	];
var _parkwithdrawT = new TQTable({
	tabletitle:"提现管理",
	//ischeck:false,
	tablename:"parkwithdraw_tables",
	dataUrl:"parkwithdraw.do",
	iscookcol:false,
	//checktype:"checkbox",
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=quickquery",
	tableObj:T("#parkwithdrawobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
var editField=[{fieldcnname:"处理状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"等待处理"},{"value_no":2,"value_name":"处理中"},{"value_no":3,"value_name":"已支付"},{"value_no":4,"value_name":"提现失败"},{"value_no":5,"value_name":"延迟处理"}], twidth:"200" ,height:"",issort:false}];
var excelState=[{"value_no":0,"value_name":"个人等待处理"},
		{"value_no":2,"value_name":"个人处理中"},
		{"value_no":3,"value_name":"个人已支付"},
		{"value_no":4,"value_name":"个人提现失败"},
		{"value_no":5,"value_name":"个人延迟处理"},
		
		{"value_no":6,"value_name":"公司等待处理"},
		{"value_no":8,"value_name":"公司处理中"},
		{"value_no":9,"value_name":"公司已支付"},
		{"value_no":10,"value_name":"公司提现失败"},
		{"value_no":11,"value_name":"公司延迟处理"},
		
		{"value_no":12,"value_name":"对公提现等待处理"},
		{"value_no":14,"value_name":"对公提现处理中"},
		{"value_no":15,"value_name":"对公提现已支付"},
		{"value_no":16,"value_name":"对公提现失败"},
		{"value_no":17,"value_name":"对公延迟处理"}
	];
function getAuthButtons(){
	return [
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkwithdrawT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"parkwithdraw_search_w",Title:"搜索",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "parkwithdraw_search_f",
					formObj:tObj,
					formWinId:"parkwithdraw_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parkwithdraw_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_parkwithdrawT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"导出申请",icon:"toxls.gif",onpress:function(Obj){
	
		Twin({Id:"parkwithdraw_search_w",Title:"导出申请",Width:480,sysfun:function(tObj){
				 TSform ({
					formname: "parkwithdraw_export_f",
					formObj:tObj,
					formWinId:"parkwithdraw_export_w",
					formFunId:tObj,
					dbuttonname:["确认导出"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select",noList:excelState}]}]
					}],
					SubAction:
					function(callback,formName){
						T("#exportiframe").src="parkwithdraw.do?action=excle&"+Serializ(formName)
						TwinC("parkwithdraw_search_w");
						T.loadTip(1,"正在导出，请稍候...",2,"");
					}
				});	
			}
		})
	}},
	{ dname:  "批量处理", icon: "sendsms.gif", onpress:function(Obj){
		var sids = _parkwithdrawT.GS();
		if(!sids){
			T.loadTip(1,"请先选择要处理的数据",2,"");
			return ;
		}
		Twin({Id:"muli_edit_w",Title:"批量处理",Width:250,sysfun:function(tObj){
			Tform({
				formname: "muli_edit_ff",
				formObj:tObj,
				recordid:"id",
				suburl:"parkwithdraw.do?action=multiedit&ids="+sids,
					method:"POST",
					Coltype:2,
					dbuttonname:["处理"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:editField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消处理",icon:"cancel.gif", onpress:function(){TwinC("muli_edit_w");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="-1"){
							T.loadTip(1,"处理失败！",2,"");
						}else if(parseInt(ret)>0){
							T.loadTip(1,"成功处理"+ret+"条记录！",2,"");
							TwinC("muli_edit_w");
							_parkwithdrawT.M()
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
	bts.push({name:"处理",fun:function(id){
		T.each(_parkwithdrawT.tc.tableitems,function(o,j){
			o.fieldvalue = _parkwithdrawT.GD(id)[j]
		});
		Twin({Id:"account_edit_"+id,Title:"处理",Width:300,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "account_edit_f",
					formObj:tObj,
					recordid:"account_id",
					suburl:"parkwithdraw.do?action=edit&id="+id,
					method:"POST",
					Coltype:2,
					dbuttonname:["保存"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:editField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("account_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("account_edit_"+id);
							_parkwithdrawT.M()
						}else{
							T.loadTip(1,ret,2,o)
						}
					}
				});	
			}
		})
	}
	});
	if(bts.length <= 0){return false;}
	return bts;
}

function setcname(value,pid,colname){
	if(value&&value!='-1'&&value!=''){
		T.A.C({
			url:"parkwithdraw.do?action=getusername&uin="+value,
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
    		waitTip:"正在获取姓名...",
    		noCover:true
		})
	}else{
		return ""
	};
	return "<font style='color:#666'>获取中...</font>";
}

/*更新表格内容*/
function updateRow(rowid,name,value){
	//alert(value);
	if(value)
	_parkwithdrawT.UCD(rowid,name,value);
}

_parkwithdrawT.C();
</script>

</body>
</html>
