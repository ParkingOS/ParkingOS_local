<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>分享详情</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>

</head>
<body>
<div id="sharesortdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script language="javascript">

var details=${details};//{"page":1,"total":2,"rows": [{"id":"1","cell":["通话中","业务咨询"]},{"id":"2","cell":["接入中","售后服务"]}]};

var _sharesortdetailT = new TQTable({
	tabletitle:"分享详情",
	ischeck:false,
	tablename:"sharesortdetail_tables",
	dataUrl:"sharesort.do",
	iscookcol:false,
	dataorign:1,
	hotdata:details,
	quikcsearch:'${tips}',
	dbuttons:false,
	buttons:false,
	searchitem:false,
	param:"action=quickquery",
	tableObj:T("#sharesortdetailobj"),
	allowpage:false,
	fit:[true,true,true],
	tableitems: [
			{fieldcnname:"日期",fieldname:"sdate",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",hide:true},
			{fieldcnname:"分享次数",fieldname:"lala_scroe",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true},
			{fieldcnname:"NFC刷卡",fieldname:"nfc_score",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true},
			{fieldcnname:"照牌积分",fieldname:"pai_score",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true},
			{fieldcnname:"差评扣分",fieldname:"praise_scroe",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true},
			{fieldcnname:"在岗积分",fieldname:"online_scroe",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true},
			{fieldcnname:"推荐积分",fieldname:"recom_scroe",fieldvalue:'',inputtype:"text", twidth:"80" ,height:"",hide:true}
		],
	isoperate:false
});
_sharesortdetailT.C();
</script>
</body>
</html>
