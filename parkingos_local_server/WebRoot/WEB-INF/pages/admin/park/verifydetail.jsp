<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>评价详情</title>
<style  type="text/css">
*{
padding:0px;font-family:"微软雅黑", sans-serif, Arial, Verdana;font-size:14px;
}
</style>
</head>
<body>
<div id="ugcparkingobj" style="width:100%;height:100%;margin:0px;">
	<div id='title' style ='width:200px;margin:15px auto;'></div>
</div>
<script >
var type = '${type}';
var title = "";
if(type=='isname'){
	title="名称审核详情";
}else if(type='islocal'){
	title="位置审核详情";
}else if(type='isresume'){
	title="描述审核详情";
}else if(type='ispay'){
	title="类型审核详情";
}
document.getElementById("title").innerHTML='<span style="font-size:26px;">'+title+'</span><br/>';
var data = eval('${data}');
if(data){
	for(var i=0;i<data.length;i++){
		var dis = document.createElement("div");
		var cs = dis.style;
		cs.margin='0px auto';
		cs.align ='center';
		cs.borderLeft='1px solid #000000';
		cs.borderRight='1px solid #000000';
		cs.borderTop='1px solid #000000';
		cs.lineHeight='26px';
		cs.overflowY='auto';
		cs.width='97%';
		dis.zIndex = 1;
		var linedata = data[i];
		if(i==0){
			dis.innerHTML='<b><div style="width:34%;text-align:left;float:left;background-color:#E8E8E8" >&nbsp;&nbsp; 车牌</div>'+
				'<div style="width:33%;text-align:left;float:left;background-color:#E8E8E8" >&nbsp;&nbsp; 时间</div>'+
				'<div style="width:33%;text-align:left;float:left;background-color:#E8E8E8" >&nbsp;&nbsp; 是否通过</div></b>';
			dis.innerHTML +='<div style="width:34%;text-align:left;float:left;" >&nbsp;&nbsp;'+linedata[0]+'</div>'+
				'<div style="width:33%;text-align:left;float:left;" >&nbsp;&nbsp; '+linedata[1]+'</div>'+
				'<div style="width:33%;text-align:left;float:left;" >&nbsp;&nbsp; '+linedata[2]+'</div><br/>';
			if(i==data.length-1)
				cs.borderBottom='1px solid #000000';
		}else{
			if(i==data.length-1)
				cs.borderBottom='1px solid #000000';
			dis.innerHTML='<div style="width:34%;text-align:left;float:left;" >&nbsp;&nbsp;'+linedata[0]+'</div>'+
				'<div style="width:33%;text-align:left;float:left;" >&nbsp;&nbsp; '+linedata[1]+'</div>'+
				'<div style="width:33%;text-align:left;float:left;" >&nbsp;&nbsp; '+linedata[2]+'</div><br/>';
		}
		document.body.appendChild(dis);
	}
}
</script>

</body>
</html>
