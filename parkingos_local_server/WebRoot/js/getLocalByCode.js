var get_user_local_url = "getLocalByCode.do";
var spanid;//span ID
if(typeof(getObj)=='undefined')
	var getObj=function(id){return document.getElementById(id);}
function getLocalByCode(oper,code,sid,oid,userCode){
	url = get_user_local_url+"?oper="+oper+"&code="+code+"&rand="+Math.random();
	spanid=sid;
	send_request_local(url,oid,userCode);
}
 var http_request_local = false;
function send_request_local(url,oid,userCode) 
{//初始化、指定处理函数、发送请求的函数
	http_request_local = false;
	//开始初始化XMLHttpRequest对象
	if(window.XMLHttpRequest) { //Mozilla 浏览器
		http_request_local = new XMLHttpRequest();
		if (http_request_local.overrideMimeType) {//设置MiME类别
			http_request_local.overrideMimeType("text/xml");
		}
	}
	else if (window.ActiveXObject) { // IE浏览器
		try {
			http_request_local = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				http_request_local = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}
	if (!http_request_local) { // 异常，创建对象实例失败
		window.alert("不能创建XMLHttpRequest对象实例.");
		return false;
	}
	http_request_local.onreadystatechange = function(){processRequest_local(oid,userCode)};
	// 确定发送请求的方式和URL以及是否同步执行下段代码
	http_request_local.open("GET", url, true);
	http_request_local.send(null);
}

function showUserLocal(locals,objid,userCode) 
{
	var oid = objid||"";
	var selectid = oid+"input_shi";
	var onchange = "setquxian(this.value,'"+oid+"','"+userCode+"')";
	var code =parseInt(userCode);
	var title ="区或县"
	var initflag=false;
	if(userCode!=''){
		initflag=true;
		if(spanid==oid+'span_shi'){
			code=parseInt(parseInt(userCode)/100);
			title = "市";
		}
	}
	//alert(code);
	if(spanid==oid+'span_quxian'){
		selectid = oid+"input_quxian";
		onchange = "set_localvalue(this.value,'"+oid+"')";
	}
	var temp = '<select class="slct" id="'+selectid+'" title='+title;
	if(typeof(viewtype)!='undefined'&&viewtype=='1')
		temp+='  style="width:86%" ';
	else 
		temp +=' style="width:27%" ';
	temp +='onchange="'+onchange+'">';
	temp += '<OPTION value="">请选择</OPTION>';
	for(i=0;i<locals.length;i++)
	{
		temp += '<OPTION value='+locals[i].value;
		if(initflag&&code==locals[i].value){
			temp +=' selected="selected"'
		}
		temp +=		'>'+locals[i].name+'</OPTION>';
	}
	temp += '</select>';
	//alert(document.getElementById(spanid).innerHTML);
	document.getElementById(spanid).innerHTML=temp;
	if(spanid==oid+'span_shi'){
		//alert(spanid+"--"+parseInt(parseInt(userCode)/100));
		if(typeof(userCode)!='undefined'&&userCode!='')
			getLocalByCode('getquxian',parseInt(parseInt(userCode)/100),oid+'span_quxian',oid,userCode);
	}
}
function processRequest_local(oid,userCode) 
{
	if (http_request_local.readyState == 4) 
	{ // 判断对象状态
		if (http_request_local.status == 200) 
		{ // 信息已经成功返回，开始处理信息
			showUserLocal(eval(http_request_local.responseText),oid,userCode);
		} 
		else 
		{ //页面不正常
			alert("您所请求的页面有异常!");
		}
	}
}
function initLocalName(objid,userCode){
	var oid = objid||"";
	//alert(userCode);
	if(typeof(userCode)=='undefined'||userCode=='')
		return ;
	else
		userCode = parseInt(userCode)
	if(userCode.length==2){
	    userCode = parseInt(userCode)*10000;
	}
	else if(userCode.length==4){
	    userCode = parseInt(userCode)*100;
	}
	getObj(oid).value=userCode
	var sheng  = parseInt(parseInt(userCode)/10000);
	var shi = parseInt(parseInt(userCode)/100);
	getObj(oid+'span_shi').innerHTML='正在查询...';
	getObj(oid+'span_quxian').innerHTML='正在查询...';
	getLocalByCode('getshi',sheng,oid+'span_shi',oid,userCode);
	//setTimeout("getLocalByCode('getquxian',shi,'span_quxian')",2000);
	var input_sheng = getObj(oid+'input_sheng');
	var options= input_sheng.options;
	for(var i=0;i<options.length;i++){
		if(parseInt(parseInt(userCode)/10000)==options[i].value){
			options[i].selected = 'selected';
			break;
		}
	}
	if(typeof(isedit)!='undefined'&&isedit=='1'){//地区不可编辑 
		getObj(oid+'span_shi').disabled=true;
		getObj(oid+'span_quxian').disabled=true;
	}
}
//if(typeof(userCode)!='undefined'&&userCode!=''){
	//alert(userCode);
//	initLocalName();
//}
function setshi(value,objid,userCode){
	var oid = objid||"";
	if(value==''){
		getObj(oid).value ='';
		getObj(oid+'input_quxian').disabled=true;
		getObj(oid+'input_shi').disabled=true;
		return;
	}
	getLocalByCode('getshi',value,oid+'span_shi',oid,userCode);
	getObj(oid+'span_shi').innerHTML='正在查询...';
	getObj(oid).value = parseInt(value)*10000;
	getObj(oid+'input_quxian').disabled=true;
}
function setquxian(value,objid,userCode){
	var oid = objid||"";
	if(value==''){
		getObj(oid).value =parseInt(getObj(oid+'input_sheng').value)*10000;
		getObj(oid+'input_quxian').disabled=true;
		return;
	}
	getLocalByCode('getquxian',value,oid+'span_quxian',oid,userCode);
	getObj(oid).value = parseInt(value)*100;
	getObj(oid+'span_quxian').innerHTML='正在查询...';
}
function set_localvalue(value,oid){
	if(value==''){
		getObj(oid).value =parseInt(getObj(oid+'input_shi').value)*100;
		return;
	}
	getObj(oid).value = value;
}