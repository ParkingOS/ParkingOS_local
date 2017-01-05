/*TQtab V1.0 2012-04-05
 Histor:2012-07-07
 Latest:2012-10-11
*/
var Ttab = function(o){var nT= new TQtab;nT.TabC(o)};
TQtab = function(){
	this.t={
		Path:"images/icons/",
		mName:"",//String,length:4
		items:false,
		menuI:false,
		menuC:false,
		menuCw:100,
		menuCh:100,
		mtype:"over",
		normalC:"nos",
		selectC:"s",
		sysfuc:false,
		sysredefine:false,
		userfuc:false,
		extClicfuc:false
	}
};
TQtab.prototype = {
TabC:function(menuconfig){
	T.extend(this.t,menuconfig);
	var t = this.t;
			//菜单链接内容
			function jdugemenu(aid){
					var id = aid.substring(7);
					var cheight = t.items[id].height?t.items[id].height+"px":"100%";
					var div = document.createElement("div");
					div.id = t.mName+"_c_"+id;
					div.style.overflow = "hidden";
					div.style.display = "block";
					div.style.cssFloat = "left";
					div.style.width = "100%";
					div.style.position ="relative";
					div.style.height = cheight;
					t.menuC?t.menuC.appendChild(div):"";
					if(t.items[id].sysfuc){
							t.items[id].sysfuc(div,aid,t.items[id].sysfucI);
					}else{
							div.innerHTML = (t.items[id].src)?"<iframe src=\""+t.items[id].src+"\" frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>":""+t.items[id].content+"";
					};
					div = null
			};
			//菜单切换,切换到才加载
			function menuchange(id,type){
				
				var cid= t.mName+"_c_"+id.substring(7);
				var menuoperate = t.menuI.childNodes;
				//var menuoCperate = t.menuC.childNodes;
				for(var j=0;j<menuoperate.length;j++){
					var cidj = t.mName+"_c_"+j;
					if(menuoperate[j].id == id)	{
						menuoperate[j].className=t.selectC;
					}else{
						menuoperate[j].tagName.toLowerCase() =="span"?menuoperate[j].className=t.normalC:"";
						if(T("#"+cidj)/*&&!t.items[id.substring(7)].sysredefine*/){T("#"+cidj).style.display="none"};
					};
					cidj = null
				};
				if(type==null){(!T("#"+cid))?jdugemenu(id):T("#"+cid).style.display="block"};
			};
	
		if(t.menuI){
				for(var i=0;i<t.items.length;i++){
					var a = document.createElement("span");
					a.className = (t.items[i].isdefault==true)?t.selectC:t.normalC;
					a.id = t.mName+"_a_"+i;
					if(typeof(fast_menu_id)!='undefined')fast_menu_id.push(a.id);
					a.userfuc = t.items[i].userfuc;
					a.title = t.items[i].help?t.items[i].help:t.items[i].dname;
					if("undefined" != typeof(t.items[i].sysredefine)){
						a.sysredefine = t.items[i].sysredefine;
						a.onclick = function(){/*menuchange(this.id,1);*/this.sysredefine();}
					}else{
						if(t.mtype == "over"){
							a.onclick = function(){if("undefined" != typeof(this.userfuc)){if(this.userfuc()==true){menuchange(this.id)}}else{t.extClicfuc?t.extClicfuc():"";menuchange(this.id);}}
						}else{
							a.onmouseover = function(){menuchange(this.id)}
						};
					};
					a.innerHTML = t.items[i].icon?"<img src=\""+t.Path+""+t.items[i].icon+"\" border=\"0\">&nbsp;"+t.items[i].dname+"":""+t.items[i].dname+"";
					t.menuI.appendChild(a);
					
					if(t.items[i].isdefault==true){
						if("undefined" == typeof(t.items[i].sysredefine)){jdugemenu(a.id)}else{t.items[i].sysredefine()};
					};
					a = null
					/*增加child*/
					if(t.items[i].child){
						var citems = t.items[i].child;
						for(var j=0;j<citems.length;j++){
							var chld = document.createElement("span");
							chld.className = (citems[j].isdefault==true)?t.selectC:t.normalC;
							chld.style.paddingLeft = 30+"px";
							chld.id = t.mName+"_a_"+i+"_"+j;
							if(typeof(fast_menu_id)!='undefined')fast_menu_id.push(chld.id);
							chld.userfuc = citems[j].userfuc;
							chld.title = citems[j].help?citems[j].help:citems[j].dname;
							if("undefined" != typeof(citems[j].sysredefine)){
								chld.sysredefine = citems[j].sysredefine;
								chld.onclick = function(){/*menuchange(this.id,1);*/this.sysredefine()}
							}else{
								if(t.mtype == "over"){
									chld.onclick = function(){if("undefined" != typeof(this.userfuc)){if(this.userfuc()==true){menuchange(this.id)}}else{menuchange(this.id)}}
								}else{
									chld.onmouseover = function(){menuchange(this.id)}
								};
							};
							
							chld.innerHTML = citems[j].icon?"<img src=\""+t.Path+""+citems[j].icon+"\" border=\"0\">&nbsp;"+citems[j].dname+"":"<a style=\"float:left;\">"+citems[j].dname+"</a>";
							t.menuI.appendChild(chld);
							
							if(citems[j].rfun&&isadmin=='1'){
								var r = document.createElement("a");
								r.style.display = "none";
								r.style.cssFloat = "right";
								r.style.width = "60px";
								r.innerHTML = "<font style=\"color:blue;\">&nbsp;&nbsp;"+citems[j].rfun[0].name+"</font>";
								r.fun = citems[j].rfun[0].fun;
								r.onclick = function(){this.fun()};
								chld.appendChild(r);
								chld.onmouseover = function(){this.childNodes[this.childNodes.length-1].style.display="block"};
								chld.onmouseout = function(){this.childNodes[this.childNodes.length-1].style.display="none"};
								r=null
							}
							
							if(citems[j].isdefault==true){
								if("undefined" == typeof(chld[j].sysredefine)){jdugemenu(chld.id)}else{chld[j].sysredefine()};
							};
						};
					}
					/*增加child结束*/
				};
		}
	}
}