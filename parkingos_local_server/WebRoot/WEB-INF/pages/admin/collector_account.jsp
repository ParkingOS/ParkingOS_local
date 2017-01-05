<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车员账户设置</title>
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
<div id="collector_accountobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var role=${role};
var datalist=${datalist};
//未审核收费员
var _mediaField = [
		{fieldcnname:"车场编号",fieldname:"comid",fieldvalue:'',inputtype:"text", twidth:"50" ,height:"",hide:true},
		{fieldcnname:"停车场",fieldname:"cname",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",hide:true},
		{fieldcnname:"姓名",fieldname:"anme",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"账号",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"状态",fieldname:"state",fieldvalue:'',inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"未处理"},{"value_no":1,"value_name":"已处理"},{"value_no":2,"value_name":"审核中"},{"value_no":3,"value_name":"无价值"}] ,twidth:"80" ,height:"",issort:false},
		{fieldcnname:"上传日期",fieldname:"ctime",fieldvalue:'',inputtype:"date", twidth:"140" ,height:"",issort:false},
		{fieldcnname:"处理日期",fieldname:"utime",fieldvalue:'',inputtype:"date", twidth:"140" ,height:"",issort:false},
		{fieldcnname:"图片名",fieldname:"pic_name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"审核人",fieldname:"auditor",fieldvalue:'',inputtype:"cselect",noList:[],action:"",twidth:"100" ,height:"",issort:false,shide:true,
			process:function(value,pid){
				return setname(value,pid,'auditor');
			}}
	];
var _collector_accountT = new TQTable({
	tabletitle:"审核停账户设置",
	ischeck:false,
	tablename:"collector_account_tables",
	dataUrl:"collector.do",
	iscookcol:false,
	dataorign:1,
	hotdata:datalist,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=vquery",
	tableObj:T("#collector_accountobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function setname(value,pid,colname){
	if(value&&value!='-1'&&value!='null'){
		T.A.C({
			url:"collector.do?action=getname&uin="+value,
    		method:"GET",//POST or GET
    		param:"",//GET时为空
    		async:false,//为空时根据是否有回调函数(success)判断
    		dataType:"0",//0text,1xml,2obj
    		success:function(ret,tipObj,thirdParam){
    			if(ret&&ret!='null'){
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
    		waitTip:"正在获取审核人...",
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
	_collector_accountT.UCD(rowid,name,value);
}

function getAuthButtons(){
	return [
	{dname:"查询车场",icon:"edit_add.png",onpress:function(Obj){
		T.each(_collector_accountT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"collector_account_search_w",Title:"搜索收费员",Width:350,sysfun:function(tObj){
				TSform ({
					formname: "collector_account_search_f",
					formObj:tObj,
					formWinId:"collector_account_search_w",
					formFunId:tObj,
					Coltype:2,
					dbuttonname:["开始查询"],
					formAttr:[{
						formitems:[{kindname:"",kinditemts:[{fieldcnname:"车场编号",fieldname:"comid",fieldvalue:'',inputtype:"text", twidth:"50" ,height:"",hide:true}]}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",onpress:function(){TwinC("collector_account_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_collector_accountT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=vquery&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"返回停车员管理",icon:"edit_add.png",onpress:function(Obj){
		location = "collector.do?action=";
		}
	}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
		bts.push({name:"添加账户",
			rule:function(id){
				var state =_collector_accountT.GD(id,"state");
				if(state==1){
					this.name="修改账户";
				}else{
					this.name="添加账户";
				}
				return true;
			},
			fun:function(id){
			Twin({
				Id:"client_detail_"+id,
				Title:"添加收费员账户  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
				Content:"<iframe src=\"collector.do?action=adduseracc&id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
				Width:T.gww()-100,
				Height:T.gwh()-50,
				CloseFn:function(){location='collector.do?action=useraccount';}
			})
		}});
		bts.push({name:"编辑",
			rule:function(id){
				var state =_collector_accountT.GD(id,"state");
				if(state==1){
					return false;
				}else
					return true;
			},
			fun:function(id){
			var state =_collector_accountT.GD(id,"state");
			Twin({Id:"collector_account_edit_w",Title:"编辑",Width:350,sysfun:function(tObj){
					Tform({
						formname: "collector_account_f",
						formObj:tObj,
						recordid:"collector_account_id",
						suburl:"collector.do?action=editpuseracc&id="+id,
						method:"POST",
						dbuttonname:["保存"],
						formAttr:[{
							formitems:[{kindname:"",kinditemts:[{fieldcnname:"处理状态",fieldname:"state",fieldvalue:state,inputtype:"select", noList:[{"value_no":-1,"value_name":"全部"},{"value_no":0,"value_name":"未处理"},{"value_no":1,"value_name":"已处理"},{"value_no":2,"value_name":"审核中"},{"value_no":3,"value_name":"无价值"}] ,twidth:"200" ,height:"",issort:false}]}]
						}],
						buttons : [//工具
							{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("collector_account_edit_w");} }
						],
						Coltype:2,
						Callback:
						function(f,rcd,ret,o){
							if(ret=="1"){
								T.loadTip(1,"处理成功！",2,"");
								TwinC("collector_account_edit_w");
								location='collector.do?action=useraccount';
							}else{
								T.loadTip(1,"处理失败",2,o)
							}
						}
						});	
					}
				})
			
		}});
	if(bts.length <= 0){
		return false;
	}
	return bts;
}
_collector_accountT.C();
</script>

</body>
</html>
