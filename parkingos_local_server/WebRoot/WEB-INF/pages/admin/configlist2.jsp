<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>修改停车场</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
</head>
<body>
<div id="opconfirm_input_div" class="tqForm" style="width: 100%; height: 815px;">

	<div style="width:100%;float:left;border-bottom:1px solid #ddd;margin-bottom:20px;padding-top:20px;font-size:12px;font-weight:700">&nbsp;基本信息</div>
	<div style="width:100%;border:none;overflow:auto;" id="div_opconfirm_0">
	<form action="">
		<div id="div_opconfirm_comid" >
			<div style="padding-right:5px;" class="l">车场编号</div>
			<div class="r">
				<input type="text" name="comid" id="opconfirm_comid" value="15989" style=";;" onblur="T.remcls(this,'h')" onfocus="T.addcls(this,'h')" class="txt">
				<span id="opconfirm_comid_t" style="width:80%;clear:both;line-height:14px;*margin-left:0px;display:none"></span>
			</div>
		</div>
		<div  id="div_opconfirm_secret">
			<div style="padding-right:5px;" class="l">车场密钥</div>
			<div class="r">
			<input type="text" name="secret" id="opconfirm_secret" value="zldtingchebao15989" style=";;" onblur="T.remcls(this,'h')" onfocus="T.addcls(this,'h')" class="txt">
			<span id="opconfirm_secret_t" style="width:80%;clear:both;line-height:14px;*margin-left:0px;display:none"></span>
		</div>
	</form>
	</div>
	<br/>
	<button >提交修改</button>

</div>
<div style="width:100%;height:20px;overflow:auto;font-style:italic;color:#c7c7c7;margin-bottom:0px;border-top:1px solid #e7e7e7;text-align:left" class="clear">真来电（北京）</div></div>


</body>

</html>
