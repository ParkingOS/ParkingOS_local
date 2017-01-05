<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车员审核</title>
</head>
<body>
<div style="margin-left:30px;margin-top:20px;width:38%;float:left;">
	<img src="collector.do?action=getpic&id=${picurl}&db=parkuser_account_pics" />
</div>
<div id="alllayout" style="width:50%;float:right;margin-top:50px;margin-right:50px;">
</div>	
<script src="js/tq.js?08137" type="text/javascript">//基本</script>
<script src="js/tq.public.js?08031" type="text/javascript">//公共</script>
<script src="js/tq.window.js?008136" type="text/javascript">//弹窗</script>
<script src="js/tq.form.js?08301" type="text/javascript">//表单</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<style type="text/css">
html,body {
	overflow:auto;
}
</style>
</body>
<script type="text/javascript">
//取字段
var fields = [
		{fieldcnname:"姓名",fieldname:"name",fieldvalue:'${name}',inputtype:"text", twidth:"200" ,height:"",issort:false},
		//{fieldcnname:"类型",fieldname:"atype",fieldvalue:'',inputtype:"select",noList:[{"value_no":0,"value_name":"银行卡"},{"value_no":1,"value_name":"支付宝"},{"value_no":1,"value_name":"微信钱包"}], twidth:"200" ,height:""},
		{fieldcnname:"卡号",fieldname:"card_number",fieldvalue:'${card_number}',inputtype:"text", twidth:"200" ,height:""},
		{fieldcnname:"银行名称",fieldname:"bank_name",fieldvalue:'${bank_name}',inputtype:"select",noList:banks, twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户地区",fieldname:"area",fieldvalue:'${area}',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"开户支行",fieldname:"bank_pint",fieldvalue:'${bank_pint}',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"身份证号",fieldname:"user_id",fieldvalue:'${user_id}',inputtype:"text", twidth:"200" ,height:"",issort:false}
	];

var accountForm =
	new TQForm({
		formname: "opconfirm",
		formObj:T("#alllayout"),
		suburl:"useraccount.do?action=create&uid=${uin}&pid=${id}&from=kefuset",
		method:"POST",
		Callback:function(f,r,c,o){
			//alert(c);
			if(c=='1'){
				T.loadTip(1,"保存成功！",3,null);
			}else{
				T.loadTip(1,"操作失败！",3,null);
			}
		},
		formAttr:[{
			formitems:[{kindname:"",kinditemts:fields}]
		}]
	});
accountForm.C();
</script>
</html>
<script type="text/javascript">
T.maskTip(0,"","");//加载结束
</script>