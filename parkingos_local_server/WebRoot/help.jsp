<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>车主端常见问题</title>
<style type="text/css">
.content{width:350px;margin:10px auto;font-family:"微软雅黑", sans-serif, Arial, Verdana;font-size:12px;line-height:20px;}
.title{width:100px;margin:10px auto;line-height:33px;}
.boot{font-size:10px;line-height:14px;}
a{color:#000;text-decoration:none;cursor:pointer;}
hr{color:#5544ff;line-height:1px;}
</style>
</head>
<body>
<div id="content" class="content" >
<span style="margin:3px auto;">
</span>
<div style='line-height:30px;font-size:16px;'>
<a  onclick='goanswer("1")' href="#">如何使用手机支付停车费&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("2")' href="#">VIP卡如何申请及使用&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("3")' href="#">账户如何充值&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("4")' href="#">停车券如何使用&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("5")' href="#">VIP卡丢失怎么办&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("6")' href="#">地图上显示的所有车场都可以手机支付么&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("7")' href="#">所在车场不支持停车宝支付怎么办&nbsp;&gt;</a></br>
<hr />
<a  onclick='goanswer("8")' href="#">停车券如何获取&nbsp;&gt;</a></br>
<hr />
</div>
 
</div>
 <form action="collectorrequest.do?action=uplogfile" enctype='multipart/form-data'>
 选择文件：<input type='file' /></br>
 <input type='submit' name='上传' />
 </form>
</body>

<script type="text/javascript">

function goanswer(id){
	location = "answer.jsp?q="+id;
}
</script>
</html>
