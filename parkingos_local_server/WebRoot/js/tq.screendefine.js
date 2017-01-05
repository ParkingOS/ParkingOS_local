/*全局定义*/
var Structure = T("#alllayout");
var tplLeft = "<@";
var tplRight = "@>";
T("#alllayout").style.border = "1px solid #ccc";
T("#alllayout").style.width = T.gww()*0.94 + "px";
T("#alllayout").style.height = T.gwh()*0.94 + "px";
T("#alllayout").style.marginLeft = T.gww()*0.03 + "px";
T("#alllayout").style.marginTop = T.gwh()*0.03 + "px";
T("#alllayout").style.overflowX = "hidden";
T("#alllayout").style.overflowY = "auto";
T.bind(window,"resize",function(){
	T("#alllayout").style.width = T.gww()*0.94 + "px";
	T("#alllayout").style.height = T.gwh()*0.94 + "px";
	T("#alllayout").style.marginLeft = T.gww()*0.03 + "px";
	T("#alllayout").style.marginTop = T.gwh()*0.03 + "px";
});

var _common = "common";
var _tab = "tab";
//操作
var _workBillModel = "workBillModel";
var _callInfo = "callInfo";
//var _workBillInfo = "workBillInfo";
//var _orderList = "orderList";
//var _msgList = "msgList";
//var _contactInfo = "contactInfo";
//var _taskInfo = "taskInfo";
//var _userDefineInfo = "userDefineInfo";
var _baseInfo = "baseInfo";
//关联表
//var _markList="markList"
//var _historyCall="historyCall"
//var _contacts="contacts"
//var _scheduleList="scheduleList";
//var _orderList="orderList";
//var _workOrderList="workOrderList";
//var _msgList="msgList";
//var _historyMsg="historyMsg";
//var _historyTalk="historyTalk";

/*模版原型*/
//有的调用原vm页面中函数
//基本信息
var lefthtml=[];
	lefthtml.push("<div class=\"telnumberinfo\" style='width:100%;'>");
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
		lefthtml.push("<div style=\"float:left;text-align:left;width:100%;padding-top:4px;padding-bottom:2px;background:#fff;border-top:1px solid #ccc;border-bottom:1px solid #ccc\">");
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
	
//工单
function CworkBillForm(obj,menuid,param){
	getWorkBillField(param[0]);
	AddWorkOrder(workBillField,obj,param[1],param[1])
}
//电话备注
function callMark(obj){
	Tform({
		formObj:obj,
		formtitle:typeof(lefttabname)=='undefined'?"本次电话情况/沟通备注":lefttabname,
		formid:"callDealForm",
		formname:"callDealForm",
		recordid:"streamRemarkId",
		suburl:"/vip/streamRemark.do?con=screenStrum",
		updataurl:"/vip/streamRemark.do?con=screenStrum",
		fit:[true],
		method:"POST",
		dbuttons:true,
		Callback:function(f,r,c,o){
			if(c!=""){
				if(T(f+"_"+r).val()==""){
					T(f+"_"+r).val(c);
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
	
}


/*模版转换*/
function applyTpl(label){
	var type = label[0];
	var _c = label[1];
	var objArray = label[2].split("|")
	var cobj = T("#"+objArray[0]);
	var tobj = objArray[1]?T("#"+objArray[1]):null;
	switch (type){
		case _common:
			_c = _baseInfo?cobj.innerHTML = lefthtml.join(""):"";
		break;
/*		case _orderList:
			var ret = getClientModels("ajaxorder.do");
			showVisitorOrder(ret,cobj);
		break;
		case _msgList:
			showShortMsssages(cobj);
		break;
*/		case _tab:
			if(_c.split("=")[0] == "operateList"){
				var items = _c.split("=")[1].split("|").join(",");
				var newItem = reSetOperate(items);
				operateTab(newItem,tobj,cobj)
			};
			if(_c.split("=")[0] == "relationList"){
				var items = _c.split("=")[1].split("|").join(",");
				var newItem = reRelationList(items);
				relationTab(newItem,tobj,cobj)
			};
		break;
	}
};

/*主函数*/
var tplCallBack = function(tplStr){
	//var _tplStr = new String(tplStr);
	var tplReg = new RegExp(""+tplLeft+".*?"+tplRight+"", "g");
	
	/*获取模版标签*/   
	var tplArray = tplStr.match(tplReg);
	
	/*获取模板框架HTML*/   
	function getTplStructure(){
		return tplStr.replace(tplReg,"")
	};
	/*先生成框架HTML*/   
	Structure.innerHTML = getTplStructure();
	
	/*解析模版标签*/   
	var _l = tplLeft;
	var _r = tplRight;
	T.each(tplArray,function(o){
		var l = _l.length;
		var r = o.lastIndexOf(_r);
		var label = o.substring(l,r).split(":");
		applyTpl(label)
	});
};
/*服务器部分*/   
T.A.sendData("js/tpldemo.jsp","POST","1=1",tplCallBack);
//返回：<div id='orderdiv'><@orderList:orderdiv@></div><div id='msgdiv'><@msgList:msgdiv@></div>

function operateTab(newItem,tobj,cobj){
	Ttab({
		mName:"oprt",
		items:newItem,
		menuI:tobj,
		menuC:cobj,
		mtype:"over",
		normalC:"nos",
		selectC:"s"
	});
};
function relationTab(newItem,tobj,cobj){
	Ttab({
		mName:"rela",
		items:newItem,
		menuI:tobj,
		menuC:cobj,
		mtype:"over",
		normalC:"nos",
		selectC:"s"
	});
};
//重设操作列出tab
function reSetOperate(items){
	var _rebutton = [];
	for(var i=0,j=telbuttons.length;i<j;i++){
		if(items.indexOf(telbuttons[i].flag)!= -1){
			_rebutton.push(telbuttons[i]);
			if(items.indexOf(telbuttons[i].flag+"^")){
				//改名
				var _items = items.substring(items.indexOf(telbuttons[i].flag+"^"));
				var _newname = _items.split(",")[0].split("^")[1]
				telbuttons[i].dname = _newname
			}
		}
	};
	if(items.indexOf(_callInfo)!= -1){
		//电话备注
		_rebutton.push({dname:"电话备注",sysfuc:callMark});
	};
	if(items.indexOf(_workBillModel)!= -1){
		//工单菜单
		for(var m = 0,n=workBillModel.length;m<n;m++){
			workBillModel[m].dname = workBillModel[m].name;
			workBillModel[m].sysfuc = CworkBillForm;
			workBillModel[m].sysfucI = [workBillModel[m].id,workBillModel[m].dname];
			_rebutton.length == 0? workBillModel[m].isdefault = true:"";
			_rebutton.push(workBillModel[m]);
		}
	};
	
	return _rebutton
};
//重设关联表列出tab
function reRelationList(items){
	var _rebutton = [];
	for(var i=0,j=relationsbutton.length;i<j;i++){
		if(items.indexOf(relationsbutton[i].flag)!= -1){
			_rebutton.push(relationsbutton[i])
		}
	};
	return _rebutton
};
