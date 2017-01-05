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
<script src="js/tq.js?08137" type="text/javascript">//基本</script>
<script src="js/tq.public.js?08031" type="text/javascript">//公共</script>
<script src="js/tq.window.js?008136" type="text/javascript">//弹窗</script>
<script src="js/tq.form.js?08301" type="text/javascript">//表单</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<script src="js/jquery.js" type="text/javascript">//验证</script>

<div id="alllayout">
	<div style="width:100%;float:left;height:0px;border-bottom:1px solid #ccc" id="top"></div>
	<div>
</div>
	<div style="width:100%;float:left;">
    	<div id="right" style="width:auto;border-left:1px solid #ccc;float:left"></div>
	</div>
</div>	
</div>
	<button id='init'>初始化</button>
</div>	


</body>
<script type="text/javascript">
$("#init").click(function(){
	$.post("http://localhost:8080/zld/initLocal.do", 
		{},
		function(data){
   			alert(data);
  		}
  	);
});
//取字段
var add_states = [{"value_no":0,"value_name":"否"},{"value_no":1,"value_name":"是"}];
var etc_states=[{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"Ibeacon"},{"value_no":2,"value_name":"通道照牌"},{"value_no":3,"value_name":"手机照牌"}]
var Obj = document.getElementById("alllayout");
var topO = document.getElementById("top");
var rightO = document.getElementById("right");

rightO.style.width = T.gww()  + "px";
rightO.style.height = T.gwh() - 50 + "px";

T.bind(window,"resize",function(){
    rightO.style.width = T.gww() + "px";
    rightO.style.height = T.gwh() - 50 + "px"
})


var fields = [
		{fieldcnname:"车场编号",fieldname:"comid",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"车场密钥",fieldname:"secret",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
	];


var comid="";
var maxmoney = 0;
var total=0;
var cominfo= eval('${cominfo}');
for(var i=0;i<cominfo.length;i++){
		if(cominfo[i].name=="id"){
			comid =cominfo[i].value;
		}
		if(cominfo[i].name=="money"){
			maxmoney =cominfo[i].value;
		}
		if(cominfo[i].name=="total_money"){
			total =cominfo[i].value;
		}
}

function getEditFields(){
	var e_f = [];
	for(var j=0;j<fields.length;j++){
		for(var i=0;i<cominfo.length;i++){
			if(cominfo[i].name==fields[j].fieldname){
				fields[j].fieldvalue=cominfo[i].value;
				e_f.push(fields[j]);
				break;
			}
			if(fields[j].inputtype=='select')
				fields[j].width=200;
		}
	}
	return e_f;
}

function getFields(){
	var fs = getEditFields();
	var mfs = [
		{kindname:"基本信息",kinditemts:fs.slice(0,2)},
		];
	return mfs;
}



var accountForm =
new TQForm({
	formname: "opconfirm",
	formObj:rightO,
	suburl:"config.do?action=edit&id="+comid,
	method:"POST",
	//dbbuttons:[true,false],
	buttons:getTopButtons(),
	Callback:function(f,r,c,o){
		
			T.loadTip(1,"修改成功！",3,null);
		
	},
	formAttr:[{
		formitems:getFields()
	}]
});
accountForm.C();

function getTopButtons(){
	var bus = [];
	
	return bus;
}



</script>


</html>
<script type="text/javascript">
T.maskTip(0,"","");//加载结束
</script>