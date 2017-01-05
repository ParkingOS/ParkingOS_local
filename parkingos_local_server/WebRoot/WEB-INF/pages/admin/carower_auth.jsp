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
//var details=${datas};
var type=${atype};
var title ="待审核车牌";
if(type==1)
	title="已审核车牌";
else if(type==-1)
	title="审核未通过";
var _mediaField = [
		{fieldcnname:"车主账户",fieldname:"uin",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true},
		{fieldcnname:"手机",fieldname:"mobile",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"注册日期",fieldname:"reg_time",fieldvalue:'',inputtype:"date", twidth:"140" ,height:"",issort:false},
		{fieldcnname:"车牌",fieldname:"car_number",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
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
		{fieldcnname:"上传日期",fieldname:"create_time",fieldvalue:'',inputtype:"date",twidth:"200" ,height:"",issort:false},
		{fieldcnname:"备注",fieldname:"remark",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",issort:false}
	];
var _carowerT = new TQTable({
	tabletitle:title,
	ischeck:false,
	tablename:"carower_tables",
	dataUrl:"carower.do",
	iscookcol:false,
	//hotdata:details,
	//dataorign:1,
	//dbuttons:false,
	buttons:getAuthButtons(),
	//searchitem:true,
	param:"action=auth&type=${atype}",
	tableObj:T("#carowerobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	isoperate:getAuthIsoperateButtons()
});

function getAuthButtons(){
	return [
	{dname:"已审核车牌",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do?type=1";
	}},
	{dname:"待审核车牌",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do?type=2";
	}},
	{dname:"审核未通过车牌",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do?type=-1";
	}},
	{dname:"返回会员管理",icon:"edit_add.png",onpress:function(Obj){
		location = "carower.do";
	}}
	]
	return false;
}
function getAuthIsoperateButtons(){
	var bts=[];
	if(type==2)
	bts.push({name:"审核车牌",fun:function(id){
		Twin({
			Id:"client_detail_"+id,
			Title:"车牌审核  &nbsp;&nbsp;&nbsp;&nbsp;<font color='red'> 提示：双击关闭此对话框</font>",
			Content:"<iframe src=\"carower.do?action=preauthuser&id="+id+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
			Width:T.gww()-100,
			Height:T.gwh()-50
		})
		}});
	if(bts.length<1)return false;
	return bts;
}
function viewpic(name){
	var url = 'viewpic.html?name='+name+'&db=user_dirvier_pics'+'&r='+Math.random();
	Twin({Id:"carstops_edit_pic",Title:"查看照片",Width:850,Height:600,sysfunI:"v_pic",
			Content:"<iframe id='tactic_iframe' src='"+url+"' style='width:100%;height:100%;' frameborder='0' ></iframe>",
			buttons :[],
			CloseFn:function(){
				
			}
		})
}
_carowerT.C();

</script>

</body>
</html>
