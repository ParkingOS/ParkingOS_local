<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>专用停车券管理</title>
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
<div id="parkticketobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">

var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",inputtype:"text", twidth:"50" ,issort:false,edit:false},
		{fieldcnname:"停车场",fieldname:"comid",inputtype:"text", twidth:"270" ,issort:false,
			process:function(value,pid){
				return setcname(value,pid,'parkname','comid');
			}},
		{fieldcnname:"数量",fieldname:"tnumber",inputtype:"number", twidth:"88" ,issort:false},
		{fieldcnname:"金额",fieldname:"money",inputtype:"text",target:'pass',action:'getcompass', twidth:"80" ,issort:false},
		{fieldcnname:"有效期（天）",fieldname:"exptime",inputtype:"number", twidth:"80" ,issort:false},
		{fieldcnname:"已领数量",fieldname:"haveget",inputtype:"number",twidth:"80" ,issort:false,edit:false}
	];
var _parkticketT = new TQTable({
	tabletitle:"停车场专用停车券",
	ischeck:false,
	tablename:"parkticket_tables",
	dataUrl:"parkticket.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=query",
	tableObj:T("#parkticketobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});
function getAuthButtons(){
	return [{dname:"添加专用停车券",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkticketT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"parkticket_add",Title:"添加专用停车券",Width:550,sysfun:function(tObj){
				Tform({
					formname: "parkticket_edit_f",
					formObj:tObj,
					recordid:"id",
					suburl:"parkticket.do?action=create",
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parkticket_add");} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"添加成功！",2,"");
							TwinC("parkticket_add");
							_parkticketT.M();
						}else{
							T.loadTip(1,ret,2,o);
						}
					}
				});	
			}
		})
	
	}},
	{dname:"高级查询",icon:"edit_add.png",onpress:function(Obj){
		T.each(_parkticketT.tc.tableitems,function(o,j){
			o.fieldvalue ="";
		});
		Twin({Id:"parkticket_search_w",Title:"搜索专用停车券",Width:550,sysfun:function(tObj){
				TSform ({
					formname: "parkticket_search_f",
					formObj:tObj,
					formWinId:"parkticket_search_w",
					formFunId:tObj,
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_mediaField}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消添加",icon:"cancel.gif", onpress:function(){TwinC("parkticket_search_w");} }
					],
					SubAction:
					function(callback,formName){
						_parkticketT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:"&action=query&"+Serializ(formName)
						})
					}
				});	
			}
		})
	
	}},
	{dname:"返回停车券查看",icon:"edit_add.png",onpress:function(Obj){
			location = "ticket.do";
		}
	}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts = [];
	bts.push({name:"编辑",fun:function(id){
		T.each(_parkticketT.tc.tableitems,function(o,j){
			o.fieldvalue = _parkticketT.GD(id)[j]
		});
		Twin({Id:"parkticket_edit_"+id,Title:"编辑",Width:550,sysfunI:id,sysfun:function(id,tObj){
				Tform({
					formname: "parkticket_edit_f",
					formObj:tObj,
					recordid:"parkticket_id",
					suburl:"parkticket.do?action=edit&id="+id,
					method:"POST",
					formAttr:[{
						formitems:[{kindname:"",kinditemts:_parkticketT.tc.tableitems}]
					}],
					buttons : [//工具
						{name: "cancel", dname: "取消", tit:"取消编辑",icon:"cancel.gif", onpress:function(){TwinC("parkticket_edit_"+id);} }
					],
					Callback:
					function(f,rcd,ret,o){
						if(ret=="1"){
							T.loadTip(1,"编辑成功！",2,"");
							TwinC("parkticket_edit_"+id);
							_parkticketT.M()
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
		Tconfirm({Title:"确认删除吗",Content:"确认删除吗",OKFn:function(){T.A.sendData("parkticket.do?action=delete","post","id="+id_this,
			function deletebackfun(ret){
				if(ret=="1"){
					T.loadTip(1,"删除成功！",2,"");
					_parkticketT.M()
				}else{
					T.loadTip(1,ret,2,"");
				}
			}
		)}})
	}});
	if(bts.length <= 0){return false;}
	return bts;
}
function setcname(value,pid,type,colname){
	if(value&&value!='-1'&&value!=''){
		T.A.C({
			url:"getdata.do?action=getvalue&type="+type+"&id="+value,
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
		return "无"
	};
	return "<font style='color:#666'>获取中...</font>";
}
/*更新表格内容*/
function updateRow(rowid,name,value){
	//alert(value);
	if(value)
	_parkticketT.UCD(rowid,name,value);
}
_parkticketT.C();
</script>

</body>
</html>
