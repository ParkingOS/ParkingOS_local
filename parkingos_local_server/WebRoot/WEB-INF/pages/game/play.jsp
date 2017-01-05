<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml"> 
<head> 
<title>SwfObject2.2Demo</title> 
<script type="text/javascript" src="swfobject.js"></script> 
<script type="text/javascript"> 
var flashvars = { 
}; 
var params = { 
wmode: "transparent" 
}; 
var attributes = {}; 
swfobject.embedSWF("sound.swf", "sound", "1", "1", "9.0.0", "expressInstall.swf", flashvars, params, attributes); 

function play(c) { 
var sound = swfobject.getObjectById("sound"); 
if (sound) { 
sound.SetVariable("f", c); 
sound.GotoFrame(1); 
} 
} 
</script> 
</head> 
<body> 
<div id="sound"> 
</div> 
<input type="button" value="²¥·Å" onclick="play('1.mp3')" /> 
</body> 
</html> 
