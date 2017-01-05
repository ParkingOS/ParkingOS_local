function addTabContent(id,title,content){
tqtabpanel.addTab({
	 id:id,  
     title:title ,  
     html:content,  
     closable: true
  }); 
};

function addTabUrl(id,title,url){
tqtabpanel.addTab({
	 id:id,
     title:title,
     html:'<iframe id="iframe_'+id+'" src="'+url+'" width="100%" height="100%" frameborder="0"></iframe>',  
     closable: true
  }); 
};

function changeTabContent(id,title,content){
	if (title != '' && title != null)
	{
		tqtabpanel.setTitle(id,title)
	};
	tqtabpanel.setContent(id,content)
}

function creatleftemenu(leftmenudata){
	var menustr = '';
	for (var i=0;i<leftmenudata.length;i++){
		menustr += "<div class='menutitle'>"+leftmenudata[i].t+"</div>"
		menustr +="<div class='menucontent'>";
			for(var j=0;j<leftmenudata[i].c.length;j++){
				menustr += "<li><a href='#' onclick='addTabUrl(\""+leftmenudata[i].c[j].id+"\",\""+leftmenudata[i].c[j].name+"\",\""+leftmenudata[i].c[j].src+"\")'><span class='"+leftmenudata[i].c[j].icon+"'>&nbsp;</span>"+leftmenudata[i].c[j].name+"</a></li>"
			}
		menustr +="</div>";
	}
	getobj("leftmenu").innerHTML = menustr
}