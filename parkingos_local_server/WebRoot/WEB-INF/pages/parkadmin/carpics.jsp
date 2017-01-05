<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>速通卡用户</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<style type="text/css">
html,body {
	overflow:auto;
}
</style>
</head>
<body>
<div>
<span style="color:red;">入口车辆照片</span>
<div><img src="carpicsup.do?action=downloadpic&comid=0&type=0&orderid=${orderid}" width="600px" height="600px"/></div><br/>
<span style="color:red;">出口车辆图片</span>
<div><img src="carpicsup.do?action=downloadpic&comid=0&type=1&orderid=${orderid}" width="600px" height="600px"/></div>
</div>
</body>
</html>
