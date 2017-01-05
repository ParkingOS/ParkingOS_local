<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <meta name="viewport" content="user-scalable=no,target-densitydpi=high-dpi" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>选择停车券</title>
<style type="text/css">
		html,body {
		    padding: 0 !important;
			margin: 0 !important;
		    background-color:#000000;
		    width:100%;
		    height:100%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
		}
		</style>
</head>
<body id='body'  ondblclick="alert('db')">
<div id='tit' style='color:#FFFFFF;width:100%;margin:10px auto;font-size:35px;text-align:center'><b>选择您想翻倍的停车券</b></div>
</body>
<script>
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
var lh = parseInt((h-60)/9);
document.getElementById("tit").style.fontSize=(lh*0.35)+'px'
var tickets = eval('${tickets}');
if(tickets){
	for(var i=0;i<tickets.length;i++){
		var dis = document.createElement("div");
		dis.id=tickets[i].id;
		var cs = dis.style;
		cs.top = parseInt((i+1)*lh)+'px';
		cs.left = parseInt(w*0.05)+'px';
		cs.background='#000000';
		cs.color='#FFFFFF';
		cs.fontSize='30px';
		cs.width =  parseInt(w*0.89)+'px';
		cs.height = parseInt(lh)-10+"px";
		cs.border='1px solid #FFFFFF';
		cs.position ='absolute';
		cs.borderRadius="8px";
		cs.align ='center';
		dis.zIndex = 1;
		var dh  = (lh*0.081)+'px';
		var fs = (lh*0.5)+'px';
		var fss = (lh*0.35)+'px';
		var fw = (lh*1.5)+'px';
		var lw = (lh*2.8)+'px'; 
		var type = tickets[i].type;
		var mo = tickets[i].money+" 元";
		if(type==2)
			mo="<font color='green'>"+tickets[i].money+" 折</font>";
		dis.innerHTML='<div style="height:'+dh+'" ></div>'+
				'<div style="width:'+fw+';text-align:center;float:left;font-size:'+fs+';" > '+mo+'</div>'+
				'<div style="width:'+lw+';text-align:center;float:right;font-size:'+fss+'" > '+tickets[i].lday+'天后到期</div>';
		dis.onclick=function(){play(this.id)};
		document.body.appendChild(dis);
	}
}else{
	document.getElementById("tit").innerHTML="对不起，您已经没有可用的停车券!";
}
	
function play(id){
	//alert(id);
	location.href='cargame.do?action=game&uin=${uin}&id='+id;
}
</script>
</html>