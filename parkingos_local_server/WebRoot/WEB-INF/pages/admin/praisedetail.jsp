<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>评价列表</title>
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
<div id="parkdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var otype='${otype}';
var parkid='${parkid}';
var tip = "好评列表";
if(otype == 'b'){
	tip = "差评列表";
}
var _mediaField = [
		{fieldcnname:"编号",fieldname:"comid",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"车主ID",fieldname:"uin",inputtype:"text", twidth:"150" ,issort:false,fhide:true},
		{fieldcnname:"车场",fieldname:"company_name",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"车主手机号",fieldname:"mobile",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"车主照牌",fieldname:"car_number",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"评价",fieldname:"praise",fieldvalue:'',inputtype:"text",twidth:"100" ,height:"",issort:false,
			process:function(value,trId,colId){
				if(value==1){
					return "赞";
				}else if(value==0){
					return "贬";
				}else{
					return "";
				}
			}},
		{fieldcnname:"评论条数",fieldname:"mcount",inputtype:"text", twidth:"200",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('"+value+"','"+cid+"')\" style='color:blue'>"+value+"</a>";
			}},
		{fieldcnname:"日期",fieldname:"create_time",inputtype:"text", twidth:"150" ,issort:false}
	];
var _parkdetailT = new TQTable({
	tabletitle:tip,
	ischeck:false,
	tablename:"parkdetail_tables",
	dataUrl:"parkpraise.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=parkdetail&parkid="+parkid+"&otype="+otype,
	tableObj:T("#parkdetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:false
});

function coutomsearch(){
	var html= "";
	return html;
}

function viewdetail(value,id){
	var company_name =_parkdetailT.GD(id,"company_name");
	var tip = "评论列表";
	Twin({
		Id:"parkcomment_detail_"+id,
		Title:tip+"  --> 停车场："+company_name,
		Width:T.gww()-100,
		Height:T.gwh()-50,
		sysfunI:id,
		Content:"<iframe name='parkcomment_detail_'"+id+" id='parkcomment_detail_'"+id+" src='parkpraise.do?action=comment&parkid="+parkid+"&uin="+id+"' width='100%' height='100%' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>"
	})
}

_parkdetailT.C();
</script>

</body>
</html>
