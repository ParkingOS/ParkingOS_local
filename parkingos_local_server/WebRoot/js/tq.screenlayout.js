/*ScreenLayout Fucntion 2012-05-05
  Version 1.0 2012-06-19
  Version 1.0 2013-05-10 收缩按钮
*/
var lt = "undefined"!=typeof(layouttype)?layouttype:1;//布局类型
var lt_h = lt_h||165;//电话基本信息高
var l_w = 260;;//电话备注等宽度
var fixWidth = T.iev&&T.iev==8?19:15;
if(telinfos.length>2){
	lt_h = lt_h+50;

}else{
	lt_h = lt_h-60;
};
var rc_h = 5;

var b_h = 230;//底部高度
var r_h = PL?58:0;//批量外呼工具条高

var layoutContainer = document.getElementById("alllayout");
var layoutL = document.createElement("div");//左
var layoutR = document.createElement("div");//右
var layoutB = document.createElement("div");//下

var layoutX = document.createElement("div");//X关闭
layoutX.className = "layout_x";
layoutX.style.cursor = "default";
layoutX.innerHTML = "<span title=\"关闭左栏\" id = \"switch_x_o\" class=\"switch_x_o\" onmousedown=\"layoutX_close(this)\" style=\"width:5px;margin-top:133px;\">&nbsp;</span>";
layoutX.style.height = T.gwh() + "px";
//layoutX.style.borderRight = "1px solid #B3BFCA";
layoutX.style.width = "5px";

var layoutY = document.createElement("div");//Y关闭
layoutY.className = "layout_y";
layoutY.style.cursor = "default";
layoutY.innerHTML = "<span title=\"关闭底栏\" id = \"switch_y_o\" class=\"switch_y_o\" onmousedown=\"layoutY_close(this)\" style=\"\">&nbsp;</span>";
layoutY.style.height = rc_h + "px";
layoutY.style.width = T.gww() - l_w - fixWidth  +"px";

var telinfo = document.createElement("div");//电话信息
var callfollowup = document.createElement("div");//客户跟进
var relationsI = document.createElement("div");//客户相关信息列表项
var relationsC = document.createElement("div");//客户相关信息展示
if(lt==2){
	var layoutRAll = document.createElement("div");
	layoutRAll.id = "layoutRAll";
	layoutRAll.style.width = T.gww() - l_w - 10  +"px";
	layoutRAll.style.height = T.gwh() - r_h - 2 + "px";
	layoutRAll.className = "layoutrall";
}
//左
layoutL.id = "screenleft";
layoutL.className = "layoutleft";
layoutL.style.width = l_w + 2+"px";
//右
layoutR.id = "screenright";
if(lt!=2){
	layoutR.className = "fieldlayout layoutright";
	layoutR.style.height = T.gwh() - b_h - r_h - rc_h -  8  +"px";
	layoutR.style.width = T.gww() - l_w - fixWidth  +"px";
	layoutR.style.marginLeft ="0px";
}else{
	layoutR.className = "fieldlayout layoutright";
	layoutR.style.height ="auto";
	layoutR.style.width = T.gww() - l_w - 32 + "px";
	(T.iev =="6.0")?layoutR.style.marginLeft = "1px":"";
};
//下
layoutB.className = "fieldlayout layoutbottom";
layoutB.id = "screen_r_b";
layoutB.style.width = (lt==0)?T.gww() - 6 +"px":T.gww() - l_w - fixWidth +"px";
if(lt==2){
	layoutB.style.width = T.gww() - l_w - 32 +"px"
	layoutB.style.height ="auto"
}else{
	(T.iev =="6.0"&&lt==1)?layoutB.style.marginLeft = "2px":"";
	(b_h==0)?layoutB.style.display ="none":layoutB.style.height = b_h - 2 + "px";
}
//整体
layoutContainer.appendChild(layoutL);
if(lt==2){
	layoutRAll.appendChild(layoutR);
	layoutContainer.appendChild(layoutRAll);
}else{
	layoutContainer.appendChild(layoutX);
	layoutContainer.appendChild(layoutR);
	layoutContainer.appendChild(layoutY);
	layoutContainer.appendChild(layoutB);
}
//右上
if(PL)
{
	var PLorder = document.createElement("div");
	PLorder.className = "plorder";
	PLorder.style.height = r_h - 4 + "px";
	var anlvar = "&nbsp;&nbsp;<b>当前任务情况：</b>";
	try{
		var anlysis = eval(T.A.sendData('executeTask.do?task_action=taskanalysis&_taskId='+taskId+'&r='+Math.random()));
		anlvar += "总电话数：<font style=\"color:#c00;\">"+anlysis[0].allcount+"</font>，已拨打：<font style=\"color:#c00;\">"+anlysis[0].execount+"</font>，完成百分比：<font style=\"color:#c00;\">"+anlysis[0].rate+"%</font>";
	}catch(e){};
	
	//var calloutDiv = document.createElement("div");
	var calloutDivUp = document.createElement("div");
	var calloutDivDo = document.createElement("div");
	PLorder.appendChild(calloutDivUp);
	PLorder.appendChild(calloutDivDo);
	
	calloutDivUp.innerHTML = anlvar;
	
	var inputAgain = document.createElement("span");
	var inputNext = document.createElement("span");
	var inputPre = document.createElement("span");
	
	inputAgain.className = "sbutton";
	inputNext.className = "sbutton";
	inputPre.className = "sbutton";
	
	inputAgain.innerHTML = "重新拨打此号码";
	inputNext.innerHTML = "拨打下一个电话";
	inputPre.innerHTML = "预览下一个电话";
	
	inputAgain.onclick = function(){reCallTask()};
	inputNext.onclick = function(){nexttask()};
	inputPre.onclick = function(){preViewNextTask()};
	
	calloutDivDo.appendChild(inputAgain);
	calloutDivDo.appendChild(inputNext);
	calloutDivDo.appendChild(inputPre);
	
	if(nextTaskDetailId==""){calloutDivDo.innerHTML=null;calloutDivDo.innerHTML="<font style=\"cursor:pointer;_cursor:hand;font-weight:700;padding:0px 5px;color:#c00;font-weight:700;font-size:16px;\">任务已经完成!</font>"};
	
	var tipspan = document.createElement("span");
	tipspan.id = "tip_";
	tipspan.innerHTML = "<font style=\"color:#c00;padding-left:10px;\">注意：请挂机后5秒再拔打下一个电话</font></span>";
	calloutDivDo.appendChild(tipspan);
	
	
	if(lt!=2){
		layoutContainer.insertBefore(PLorder,layoutR);
		PLorder.style.width = T.gww() - l_w - fixWidth  +"px"
	}else{
		layoutContainer.insertBefore(PLorder,layoutRAll);
		PLorder.style.width = T.gww() - l_w - fixWidth  +"px";
	}
};
//左CONTENT
var lefthtml=[];
	lefthtml.push("<div class=\"telnumberinfo\">");
	var vinfo="<span id=\"kehuInfo\" style='color:#c00'>";
	for(var i=0;i<teluser.length;i++){
		if(i==0)
			vinfo+=teluser[i];
		else 
			vinfo+=","+teluser[i];
	}
	vinfo +="</span><br>";
	lefthtml.push(vinfo);
	lefthtml.push("<span class=\"fhlight\">"+telnumber[0]+"&nbsp;&nbsp;"+telnumber[1]+"</span>");
	lefthtml.push("</div>");
	if(telinfos.length>0){
		lefthtml.push("<div style=\"float:left;text-align:left;width:100%;padding-top:4px;padding-bottom:2px;border-top:1px solid #D3D3BE;border-bottom:1px solid #D3D3BE\">");
		for(var i=0;i<telinfos.length;i++){
			if(telinfos.length==1)
				lefthtml.push("<li style=\"float:left;margin-left:5px;width:100%;overflow:hidden;white-space:nowrap;\">");
			else 
				lefthtml.push("<li style=\"float:left;margin-left:5px;width:100%;overflow:hidden;white-space:nowrap;\">");
			if(telinfos[i].isurl&&telinfos[i].dvalue.indexOf('http')!=-1)
				lefthtml.push("<b>"+telinfos[i].dname+"</b> : <font title=\""+telinfos[i].dvalue+"\"><a href='"+telinfos[i].dvalue+"' target='_blank' >"+telinfos[i].dvalue+"</a></font>");
			else
				lefthtml.push("<b>"+telinfos[i].dname+"</b> : <font title=\""+telinfos[i].dvalue+"\">"+telinfos[i].dvalue+"</font>");
			lefthtml.push("</li>");
		}
		lefthtml.push("</div>");
	}
telinfo.id = "telinfoid";
telinfo.className = "fieldlayout telinfo";
telinfo.style.height = lt_h +"px";
telinfo.innerHTML = lefthtml.join("");
//构造操作TAB
var tTab = document.createElement("div");
tTab.id = "teldealediv";
tTab.className ="teldeale";
telinfo.appendChild(tTab);
layoutL.appendChild(telinfo);
layoutL.appendChild(callfollowup);
Ttab({
	mName:"busi",
	items:telbuttons,
	menuI:tTab,
	menuC:layoutR,
	menuCw:T.gww() - l_w - fixWidth,
	menuCh:T.gwh() - b_h - 10,
	mtype:"over",
	normalC:"nos",
	selectC:"s"
});
//跟进备注表单（默认加载）
callfollowup.className = "fieldlayout telfollowup";
callfollowup.id = "callDealFormDiv";
callfollowup.style.height = (lt==0)?T.gwh() - lt_h - b_h - 13 +"px":T.gwh() - lt_h - 9 +"px";
Tform({
	formObj:callfollowup,
	formtitle:typeof(lefttabname)=='undefined'?"本次电话情况/沟通备注":lefttabname,
	formid:"callDealForm",
	formname:"callDealForm",
	recordid:"streamRemarkId",
	suburl:"streamRemark.do?con=screenStrum",
	updataurl:"streamRemark.do?con=screenStrum",
	fit:[true],
	dbuttonname:["保存",false],
	method:"POST",
	dbuttons:true,
	Callback:function(f,r,c,o){
		if(c!=""){
			var sid=c;
			var pid='';
			if(c.indexOf('_')!=-1){
				sid = c.split('_')[0];
				pid = c.split('_')[1];
			}
			if(pid!=-1&&phonerecord_id&&phonerecord_id=='')
				phonerecord_id=pid;
			if(pid!=-1&&T(f+"_remarkPhoneRecordId").val()==""&&pid!=''){
				T(f+"_remarkPhoneRecordId").val(pid);
			}
			if(T(f+"_"+r).val()==""){
				T(f+"_"+r).val(sid);
				T.loadTip(1,"更新成功！",2,o);
			}else{
				T.loadTip(1,"更新成功！",2,o)
			};
		}else{
			T.loadTip(1,"更新成功！",2,o);//备注为空时会返回为空值
		};
		setTimeout(reloacremark,2000);
		//T("#rela_c_0")?(T("#rela_c_0").innerHTML="",showStreamRemark(T("#rela_c_0")),unionText()):""
	},
	buttons:followupBtn,
	formAttr:[{
		formitems:followupItem,
		rules : followupRule		
	}]

});
//下CONTENT
//构造相关信息TAB
if(lt!=2){
	relationsI.className = "relationmenu";
	relationsC.className = "relationcontainer"
	relationsC.style.width = "100%";
	relationsC.style.height = b_h==0?"auto":b_h - 27 + "px";
	layoutB.appendChild(relationsI);
	layoutB.appendChild(relationsC);
	Ttab({
		mName:"rela",
		items:relationsbutton,
		menuI:relationsI,
		menuC:relationsC,
		mtype:"over",
		normalC:"nos",
		selectC:"s",
		extClicfuc:tabExtFun
	});
}else{
	var layoutRAllb = document.createElement("div")
	layoutRAllb.style.width = (T.iev == "6.0")?T.gww() - l_w - 37 + "px":T.gww() - l_w - 32 + "px";
	var rlen = relationsbutton.length;
	for(var i = 0;i<rlen;i++)
	{
		var c = document.createElement("div")
		c.innerHTML = "<span style=\"float:left;width:100%;height:29px;line-height:29px;padding-left:5px;font-weight:700;border-bottom:1px solid #ccc\">"+relationsbutton[i].dname+"</span>";
		c.className = "layoutbottom";
		c.style.marginTop = "4px";
		c.style.border = "1px solid #889DAD";
		c.style.cssFloat = "left";
		c.style.width = "100%";
		c.style.height = "auto";

		var t = document.createElement("div");
		var cheight = relationsbutton[i].height?t.items[id].height+"px":"100%";
		t.style.height = cheight;
		t.style.width = "100%";
		t.style.background = "#fff";
		t.style.overflow = "hidden";
		t.style.display = "block";
		t.style.cssFloat = "left";
		if(relationsbutton[i].sysfuc){
			relationsbutton[i].sysfuc(t);
		}else{
			t.innerHTML = (relationsbutton[i].src)?"<iframe src=\""+relationsbutton[i].src+"\" frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>":""+relationsbutton[i].content+"";
		};
		c.appendChild(t);
		layoutRAllb.appendChild(c);
	};
	layoutRAll.appendChild(layoutRAllb);
	setTimeout(
		function()
		{
			for(var i=0;i<telbuttons.length;i++)
				{
					T("busi_a_"+i).aevt("click",function(){
						layoutRAll.scrollTop = "0"
					})
				}
		},100)
};
/*监听调整布局大小*/
function ResizeLayout(){
    //b_h = T.gcok("screen_r_b_c")=="0"||T.gcok("screen_r_b_c")==null?32:230;//底部高度
    l_w = T.gcok("screen_l")=="0"?0:260;//左栏宽度
    b_h = T.gcok("screen_r_b_c")=="0"?32:230;//底部高度
	try{
		callfollowup.style.height = (lt==0)?T.gwh() - lt_h - b_h - 13 +"px":T.gwh() - lt_h - 9 + "px";
		PL?PLorder.style.width = T.gww() - l_w - fixWidth  +"px":"";
		layoutY.style.width = T.gww() - l_w - fixWidth  +"px";
		if(lt!=2){
			layoutR.style.height = T.gwh() - b_h - r_h - rc_h -  8  +"px";
			layoutR.style.width = T.gww() - l_w - fixWidth  +"px"
			};
			layoutB.style.width = (lt==0)?T.gww() - 6 +"px":T.gww() - l_w - fixWidth +"px";
		if(lt==2){
			//layoutR.style.height = "auto";
			layoutRAllb.style.width = (T.iev == "6.0")?T.gww() - l_w - 37 + "px":T.gww() - l_w - 32 + "px";
			layoutR.style.width = T.gww() - l_w - 32 + "px";
			layoutRAll.style.width = T.gww() - l_w - 10 +"px";
			layoutRAll.style.height = T.gwh() - r_h - 2 + "px";
		}else{
			(b_h==0)?layoutB.style.display ="none":layoutB.style.height = b_h - 2 + "px";
			layoutR.style.width = T.gww() - l_w - fixWidth  +"px";
			layoutL.style.width = l_w + 2 + "px";
		};
		_clientEditF.SizeForm();
		//beizhuTable.FO();
	}catch(e){};
};
T.bind(window,"resize",ResizeLayout);
ResizeLayout();
/*关闭右下栏*/
function layoutY_close(t){
	if(b_h == 32){
		b_h = 230;
		T.scok("screen_r_b_c","1");
		t.title = "关闭底栏";
		t.className = "switch_y_o";
	}else{
		b_h = 32;//底部高度
		T.scok("screen_r_b_c","0");
		layoutY.onmousedown = null;
		t.title = "打开底栏";
		t.className = "switch_y_c";
	};
	ResizeLayout();
};
/*关闭左栏*/
function layoutX_close(t){
	if(l_w == 0){
		l_w = 260;
		T.scok("screen_l","1");
		t.title = "关闭左栏";
		t.className = "switch_x_o";
	}else{
		l_w = 0;//左栏宽度
		T.scok("screen_l","0");
		layoutY.onmousedown = null;
		t.title = "打开左栏";
		t.className = "switch_x_c";
	};
	ResizeLayout();
};
/*关联信息选项卡扩展事件*/
function tabExtFun(){
	var t = T("#switch_y_o");
	if(b_h == 32){
		b_h = 230;
		T.scok("screen_r_b_c","1");
		t.title = "关闭底栏";
		t.className = "switch_y_o";
	}
	ResizeLayout();
};
