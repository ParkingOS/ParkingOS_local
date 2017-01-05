<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>真来电ETC</title>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<style type="text/css">
	 *{font-size: 24px;}
    body{height: 100%;}
    body{background-color:Transparent;}
    input,select{border-radius:10px;}
    #content{ padding: 50px;}
	.tc{text-align:center;margin-top:20px}
	.ntb th,.ntb td{border:none;}
	.w150{width:150px}
	.p10{padding:10px;}
	.w200{width:200px}
	.ml30{margin-left:30px}
	.c{width: 300px;height: 30px;line-height: 30px;margin: 20px;}
 </style>

</head>
<body>
<div id="content" class="tc">

<form action="nfchandle.do" method="post">   
优先识别省份：<input id="param" type="text" /> 
<input type="button" onClick="subm()" value="确定" />
</form>
</div>
<script type="text/javascript">
var comid = ${comid};
getfirpro();
function getfirpro(){
	var province = T.A.sendData("provincesett.do?action=getprovince&comid="+comid);
	document.getElementById("param").value=province;
}
function subm(){
	var param =document.getElementById("param").value;
	var result = T.A.sendData("provincesett.do?action=firstprovince&param="+param+"&comid="+comid);
	if(result == 1){
		T.loadTip(1,"操作成功！",2,"");
	}else{
		T.loadTip(1,"操作失败！",2,"");
	}
}
</script>
</body>

