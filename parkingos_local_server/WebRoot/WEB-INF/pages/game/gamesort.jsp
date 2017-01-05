<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>车神榜</title>
<style type="text/css">
		html,body {
		     padding: 0 !important;
			margin: 0 !important;
		    background-color:#ffffff;
		    width:100%;
		    height:100%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-color:#4DD2A7;
		    background-image: url(images/game/game_b.jpg);
		    background-repeat:no-repeat;
		}
		.fdiv{
			overflow-y:auto;
		}
		</style>
</head>
<body id='body' >
<div id='win' style='position:absolute'><img src='images/game/win.png' id='winimg'/></div>
<div id='tit' style='color:#f2c115;text-align:center'><b>车神排行榜</b></div>
<div id='line' style='position:absolute'><img src='images/game/line.png' id='lineimg'/></div>

</body>
<script>
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
var img = document.getElementById('win').style;
img.top=parseInt(h*0.07)+"px";
img.left=parseInt(w*0.43)+"px";
img.width=parseInt(w*0.4)+'px';

var line = document.getElementById('line').style;
line.top=parseInt(h*0.26)+"px";
line.left=parseInt(h*0.10)+"px";

document.getElementById('lineimg').style.width=parseInt(w*0.68)+"px";

document.getElementById('winimg').style.width=parseInt(w*0.15)+"px";
document.getElementById('tit').style.marginTop=parseInt(h*0.21)+"px";
document.getElementById("tit").style.fontSize=(w*0.045)+'px';

var lh  = parseInt(h*0.04);
var t = parseInt(h*0.29);

var data = eval('${data}');
if(data){
	var fdiv = document.createElement("div");
	fdiv.className='fdiv';
	var fss = fdiv.style;
	fss.marginTop='20px';
	fss.height=parseInt(h*0.65)+'px';
	var sort = ${sort};
	for(var i=0;i<data.length;i++){
		var imgurl = 'images/game/wstar.png';
		var coo ='#FFFFFF';
		if(i==0){
			imgurl='images/game/ystar.png';
			coo='#f2c115';
		}else if(i==1){
			coo='#fc7b7f';
			imgurl='images/game/pstar.png';
		}else if(i==2){
			coo='#06b702';
			imgurl='images/game/gstar.png';
		}
		var dis = document.createElement("div");
		var cs = dis.style;
		cs.width =  parseInt(w*0.65)+'px';
		cs.height =  (lh-5)+'px';
		/*if(i>8){
			cs.left = parseInt(w*0.19)+'px';
			cs.width =  parseInt(w*0.74)+'px';
		}*/
		cs.color=coo;
		//cs.height = parseInt(lh)-15+"px";
		cs.margin='10px auto';
		cs.align ='center';
		if((sort>0&&(i+1)==sort))
			cs.border='1px solid #FFFFFF';
		dis.zIndex = 1;
		var dh  = (lh*0.2)+'px';
		var fs = (lh*0.6)+'px';
		var fss = (lh*0.55)+'px';
		var fw = (lh*6.8)+'px';
		var lw = (lh*1.8)+'px'; 
		var iw =(lh*0.65)+'px';
		var usort = (i+1);
		dis.innerHTML='<div style="width:'+fw+';text-align:left;float:left;font-size:'+fs+';" ><img src='+imgurl+' width="'+iw+'"/>&nbsp;&nbsp; '+usort+'、'+data[i].own+'</div>'+
				'<div style="width:'+lw+';text-align:center;float:right;font-size:'+fss+'" > '+data[i].score+'分</div>';
		fdiv.appendChild(dis);
	}
	document.body.appendChild(fdiv);
}
</script>
</html>