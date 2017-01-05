function TqCookie(formKey){
	this.form = {};
	if(typeof(this.form[formKey]) == "undefined"){
        this.form[formKey] = null;
    } else {
    	this.form[formKey] = formKey;
    }
}

TqCookie.prototype.setCookie=function(name,value,option){
   var  str=name+"="+escape(value); 
   if(option){
   	 //如果设置了过期时间 
     if(option.expireDays){ 
        var date=new Date(); 
        var ms=option.expireDays*24*3600*1000; 
        date.setTime(date.getTime()+ms); 
        str+=";expires="+date.toGMTString(); 
      }
     if(option.path) str+=";path="+path;       //设置访问路径 
     if(option.domain)str+=";domain"+domain;   //设置访问主机 
     if(option.secure)str+=";true";            //设置安全性 
   } 
   document.cookie=str;
};

TqCookie.prototype.getCookie=function(name){ 
    var cookieValue = "";
	var search = key + "=";
	if(document.cookie.length > 0){
		offset = document.cookie.indexOf(name+"\=");
		if (offset != -1){ 
			offset += search.length;
			end = document.cookie.indexOf(";", offset);
			if (end == -1) 
				end = document.cookie.length;
			cookieValue = unescape(document.cookie.substring(offset, end))
		}
	}
    return cookieValue;
};

TqCookie.prototype.deleteCookie=function(name){
	//将过期时间设置为过去来删除一个cookie 
     this.setCookie(name,"",{expireDays:-1});
}
