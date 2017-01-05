<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>备注记录</title>
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
<div id="remarksobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">
var uin="${uin}";
var _mediaField = [
		{fieldcnname:"编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false,
			process:function(value,pid){
					return setname(value,pid,'uid');
				}},
		{fieldcnname:"联系人",fieldname:"contacts",fieldvalue:'',inputtype:"text", twidth:"150" ,height:"",issort:false,
			process:function(value,pid){
						return setname(value,pid,'contacts');
					}},
		{fieldcnname:"日期",fieldname:"create_time",fieldvalue:'',inputtype:"date", twidth:"150" ,height:"",issort:false},
		{fieldcnname:"备注内容",fieldname:"visit_content",fieldvalue:'',inputtype:"text", twidth:"400" ,height:"",issort:false}
	];
var _remarksT = new TQTable({
	tabletitle:"备注记录",
	ischeck:false,
	tablename:"remarks_tables",
	dataUrl:"collector.do",
	iscookcol:false,
	//dbuttons:false,
	buttons:false,
	//searchitem:true,
	param:"action=remarks&uin="+uin,
	tableObj:T("#remarksobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function setname(value,pid,colname){
	if(value&&value!='-1'&&value!=''){
		T.A.C({
			url:"collector.do?action=getname&uin="+value,
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
    		waitTip:"正在获取...",
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
	_remarksT.UCD(rowid,name,value);
}

function getAuthIsoperateButtons(){
	var bts = [];
	if(bts.length <= 0){return false;}
	return bts;
}
_remarksT.C();
</script>

</body>
</html>
