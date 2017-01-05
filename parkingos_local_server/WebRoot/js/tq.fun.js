function getobj(id){
    return document.getElementById(id);
}

function get_width(o)
{
	var t = getobj(o);
	if(t)
	{
		width = t.offsetWidth;
	}else
	{
		width = t.offsetWidth;
	}
	return width;
}

function get_height(o)
{
	var t = getobj(o);
	if(t)
	{
		height = t.offsetHeight;
	}else
	{
		height = t.offsetHeight;
	}
	return height;
}

function maxwin()
{
	//判断浏览器是否支持window.screen判断浏览器是否支持screen
	if (window.screen)
	{
		var myw = screen.availWidth; //定义一个myw，接受到当前全屏的宽
		var myh = screen.availHeight;  //定义一个myw，接受到当前全屏的高

		window.moveTo(0, 0);     //把window放在左上脚
		window.resizeTo(myw, myh);   //把当前窗体的长宽跳转为myw和myh
	}
}

/**
* 自定义 getheight 函数，用于取得当前浏览器的高度
*
**/
function get_winheight()
{
	//根据浏览器的不同，取得页面高度
	if (!document.all)
	{
		height = window.innerHeight;
	}else
	{
		height = document.body.clientHeight;
	}

	return height;
}

/**
* 自定义 get_bodyheight 函数，用于取得当前页面的高度
*
**/
function get_bodyheight()
{
    var height = 0;
	//根据浏览器的不同，取得页面高度
	if (!document.all)
	{
		height = window.innerHeight - 85;
	}
    else
	{
		height = document.body.clientHeight - 69;
	}

	return height;
}

/**
* 自定义 get_winwidth 函数，用于取得当前浏览器的宽度
**/
function get_winwidth()
{
	//根据浏览器的不同，取得页面宽度
	if (!document.all)
	{
		width = window.innerWidth;
	}else
	{
		width = document.body.clientWidth;
	}

	return width;
}

/**
* 写cookies函数2
**/
function set_cookie(name,value)
{
	var Days = 30; //此 cookie 将被保存 30 天
	var exp  = new Date();  //new Date("December 31, 9998");
	exp.setTime(exp.getTime() + Days*24*60*60*1000);
	document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

/**
* 取cookies函数
**/
function get_cookie(name)
{
	var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
	if(arr != null) return unescape(arr[2]);
	return null;
}

/**
* 自定义 header 函数，用于过滤可能出现的安全隐患
**/
function est_header(string)
{
	if(string != string.replace(/location:/i,''))
	{
		var url = string.replace(/location:/i,'');
		self.location = url;
	}
}

/**
* js插入 js 和css
**/
function $import(path,type){
	var s,i;
	if(type == "js"){
		var ss = document.getElementsByTagName("script");
		for(i =0;i < ss.length; i++)
		{
			if(ss[i].src && ss[i].src.indexOf(path) != -1)return ss[i];
		}
		s      = document.createElement("script");
		s.type = "text/javascript";
		s.src  =path;
	}
	else if(type == "css")
	{
		var ls = document.getElementsByTagName("link");
		for(i = 0; i < ls.length; i++)
		{
			if(ls[i].href && ls[i].href.indexOf(path)!=-1)return ls[i];
		}
		s          = document.createElement("link");
		s.rel      = "stylesheet";
		s.type     = "text/css";
		s.href     = path;
		s.disabled = false;
	}
	else return;
	var head = document.getElementsByTagName("head")[0];
	head.appendChild(s);
	return s;
}

//数据分类处设置颜色
function set_color(id,iid)
{
	jQuery('#'+iid+'>a').css('color','#000000');
	jQuery('#'+id).css('color','red');
}

 //取得当前时间  
function now_time(){
    var now= new Date();   
    var year=now.getYear();   
    var month=now.getMonth()+1;   
    var day=now.getDate();   
    var hour=now.getHours();   
    var minute=now.getMinutes();   
    var second=now.getSeconds();   
    var nowdate=year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;   
    return nowdate;    
 } 

 //冒泡提示  
function tips(obj,html)
{
	getobj(obj).innerHTML = html;
	getobj(obj).style.display = "block";
	getobj(obj).style.left = (get_winwidth() - get_width(obj))/2;
	setTimeout(
		function(){
			getobj(obj).style.display = "none";
		}
		,3000
	)
}

 //删除表格行  id:tableid,n:最少保留行,tid:提示对象
function delRow(id,n,tid)
{
    var tableName = getobj(id);
    var prev = tableName.rows.length;
    if(prev-n > 1){
		tableName.deleteRow(prev-1);
	}else{
		tips(tid,'至少保留<font style="color:#c00;font-weight:bold;">'+n+'</font>条 ');
	}
}

 //重设表单  
function reset_form(nm)
{
	document.forms[nm].reset();
}
 
 //提交表单  
function submit_form(nm)
{
	document.forms[nm].submit();
}

function tq_call_out(phone_num){
	try{window.navigate("app:1234567@"+phone_num+""); } catch(e){};
}
 
 
function bind(el,eventName,fn) 
{
	if (window.addEventListener) {
		el.addEventListener(eventName, fn,false);
	} else if (window.attachEvent) {
		el.attachEvent("on" + eventName, fn);
	} 
}

function addClass(element,value) {
  if (!element.className) {
    element.className = value;
  } else {
    newClassName = element.className;
    newClassName+= " ";
    newClassName+= value;
    element.className = newClassName;
  }
}

function removeClassName(oElm, strClassName){
	var oClassToRemove = new RegExp((strClassName + "\s?"), "i");
	oElm.className = oElm.className.replace(oClassToRemove, "").replace(/^\s?|\s?$/g, "");
}

