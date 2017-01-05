/*TQform 2012-04-05
 Latest:2012-07-07
*/
//var userCode;
//时间选项
var hour_select = [];
for(var i=0;i<25;i++){
	hour_select.push({"value_no":i,"value_name":i});
}

var minute_select = [
 {"value_no":0,"value_name":0},
 {"value_no":15,"value_name":15},
 {"value_no":30,"value_name":30},
 //{"value_no":45,"value_name":45},
 {"value_no":60,"value_name":60},
 {"value_no":120,"value_name":120},
 {"value_no":720,"value_name":"12小时"},
 {"value_no":1440,"value_name":"24小时"}];

var pminute_select = [
	 {"value_no":0,"value_name":0},  
	 {"value_no":5,"value_name":5},                  
     {"value_no":10,"value_name":10},
     {"value_no":15,"value_name":15},
     {"value_no":20,"value_name":20},
     {"value_no":25,"value_name":25},
     {"value_no":30,"value_name":30},
     {"value_no":35,"value_name":35},
     {"value_no":40,"value_name":40},
     {"value_no":45,"value_name":45},
     {"value_no":50,"value_name":50},
     {"value_no":55,"value_name":55}]

var fminute_select = [
 {"value_no":0,"value_name":0},
 {"value_no":10,"value_name":10},
 {"value_no":15,"value_name":15},
 {"value_no":30,"value_name":30},
 {"value_no":60,"value_name":60},
 {"value_no":120,"value_name":120},
 {"value_no":180,"value_name":180}];

var Tform = function(o){new TQForm(o).C()};
var callbackfunc = function(fn,FNM,value){
	fn(this,FNM,value);
}
//取字段
var banks =[
    {"value_name":"中国工商银行","value_no":"中国工商银行"},
    {"value_name":"中国建设银行","value_no":"中国建设银行"},
    {"value_name":"中国农业银行","value_no":"中国农业银行"},
	{"value_name":"中国银行","value_no":"中国银行"},
	{"value_name":"招商银行","value_no":"招商银行"},
	{"value_name":"交通银行","value_no":"交通银行"},
	{"value_name":"中国邮政储蓄银行","value_no":"中国邮政储蓄银行"},
	{"value_name":"中信实业银行","value_no":"中信实业银行"},
	{"value_name":"上海浦东发展银行","value_no":"上海浦东发展银行"},
	{"value_name":"民生银行","value_no":"民生银行"},
	{"value_name":"光大银行","value_no":"光大银行"},
	{"value_name":"广东发展银行","value_no":"广东发展银行"},
	{"value_name":"兴业银行","value_no":"兴业银行"},
	{"value_name":"华夏银行","value_no":"华夏银行"},
	{"value_name":"上海银行","value_no":"上海银行"},
	{"value_name":"北京银行","value_no":"北京银行"},
	{"value_name":"北京农村商业银行","value_no":"北京农村商业银行"}
	];
//去除空格和换行
function clearValue(key){
    key = key.replace(/[\r\n]/g, "");
    key = key.replace(/\s+/g, "");
    return key;
} 

TQForm = function(o){
/*表单属性*/
	this.tc = T.extend({
			Path:"images/form/",
			formtitle:false,
			formFunId:null,//与构造对象名相同
			formname:"tqform",
			formpower:true,
			formObj:null,
			geturl:false,//获取数据AjaxUrl
			url:"",//获取表单字段属性AjaxUrl
			fit:[false],
			suburl:"",
			updataurl:"",
			dbuttons:true,
			dbuttonname:["保存","重置"],
			buttonpos:"",//tp或者空 上面  bt 下面 默认上面
			method:"POST",
			action:"",
			recordid:null,
			formAttr:[{}],
			formtip:"",
			formtipbt:"",
			Callback:false,
			Css:false,
			C50Pct:"half",
			C100Pct:"whole",
			nmCls:"l",
			cnCls:"r",
			subFun:false,
			loadfun:false,
	        isEscape:true,
			isArray:true,
			Coltype:false,//(false/1/2)默认false 根据每个字段colSpan属性单独确定显示列数, 否则按Coltype定义值确定每个字段显示列数
			endLable:true,
			islisten:1,//是否监听改变大小标识,勿修改
			curRecordId:""
		},o);
	if(this.tc.url){T.extend(this.tc.formAttr[0],{formitems:eval("["+T.A.sendData(this.tc.url)+"]")})};
};

TQForm.prototype = {
/*创建表单*/
	C : function(p){
		if(p){
			T.extend(this.tc,p)
		};
		var t = this;
		var tc = this.tc;
		if(tc.geturl){
			var setValue = function(data){
				if(typeof(data)=="object"&&data.total!="0"){
					t.SFD(data.rows[0].cell);
				};
				t.M();
			};
			var field_names = this.GF(true);
			T.A.sendData(tc.geturl,"POST",tc.param+"&fieldsstr="+field_names,setValue,2,tc.formObj);
		}else{
			t.M();
		};
	},
	/**********************************
	
	 GetFields:获取字段
	 union:是否判断关联字段信息
	 	 
	**********************************/
	GF : function(union){
		var field_names= "";
		var formFields = this.tc.formAttr[0].formitems;
		for(var i=0,j=formFields.length;i<j;i++){
			var items = formFields[i].kinditemts;
			for(var m=0,n=items.length;m<n;m++){
				var unionSel = union?(items[m].unionSel?"@"+items[m].unionSel:""):"";
				if(items[m].nouse){continue};
				if(field_names==""){
					field_names = items[m].fieldname + unionSel;
				}else{
					field_names +="__"+items[m].fieldname + unionSel;
				}
			}
		};
		return field_names
	},
	/**********************************
	
	 GetFieldDate:根据服务器返回数据获取字段value值
	 
	**********************************/
	GFD : function(data,fieldname){
		if(!data)return "";
		fieldname = fieldname.indexOf("@")?fieldname.split("@")[0]:fieldname;
		var t = this;
		var field_names = this.GF().split("__");
		var filedIndex = T.AindexOf(field_names,fieldname);
		return data[filedIndex];
	},
	/**********************************
	
	 SetFieldDate:更新字段对象feildvalue
	 
	**********************************/
	SFD : function(data){
		var t = this;
		var ti = this.tc.formAttr[0].formitems;
		T.each(ti,function(p,i){
			T.each(p.kinditemts,function(o,j){
				!o.nouse?o.fieldvalue = t.GFD(data,o.fieldname):"";
			})
		});
	},
	/**********************************
	
	 GetFieldAttribue:获取字段某个属性
	 
	**********************************/
	GFA : function(fieldname,fieldattr){
		var t = this;
		var ret = false;
		var ti = this.tc.formAttr[0].formitems;
		T.each(ti,function(p,i){
			T.each(p.kinditemts,function(o,j){
				if(o.fieldname == fieldname){
					ret = o[fieldattr];
				}
			})
		});
		return ret
	},
	M : function(p){
		if(p){
			T.extend(this.tc,p)
		};
		var t = this;
		var tc = this.tc;

		var formObj = tc.formObj;
		var FNM= tc.formname;
		if(T("#"+FNM)){tc.islisten=0};	
		formObj.innerHTML = "";
		var formdiv = document.createElement("form");//创建表单
		var formtit = document.createElement("div");//创建表单标题
		var formbt = document.createElement("div");//创建工具按钮
		var inputdiv = document.createElement("div");//创建表单输入
		formbt.id = FNM + "_form_button";
		formbt.className = "";
		
		inputdiv.id = FNM + "_input_div";
		inputdiv.className = "tqForm";
		tc.fit[0]?inputdiv.style.overflow = "auto":"";

		if(tc.formAttr[0].formitems){
			if(tc.otherformAttr){
				//var formattrLength = tc.formAttr[0].formitems.length;
				var kinditemtsLength = tc.formAttr[0].formitems[0].kinditemts.length;
				for(var of=0;of<tc.otherformAttr[0].kinditemts.length;of++){
					++kinditemtsLength;
					tc.formAttr[0].formitems[0].kinditemts[kinditemtsLength-1]=tc.otherformAttr[0].kinditemts[of];
				}
				//T.extend(tc.formAttr[0].formitems,tc.otherformAttr)
			}
			//创建表单
			formdiv.name = FNM;
			formdiv.setAttribute("name",FNM);
			formdiv.id = FNM;
			formdiv.action = tc.action;
			//创建表单标题
			if(tc.formtitle)
			{
			formtit.id = FNM + "_form_title";
			formtit.className = "formtitle";
			formtit.innerHTML = "<span style='padding-left:5px;'>"+tc.formtitle+"</span>";
			formdiv.appendChild(formtit);
			};
			
			if(tc.formpower){//表单权限
				//创建工具按钮
				if(tc.dbuttons){
					if(tc.dbuttonname[0]){
						var a = document.createElement("span");
						a.id = FNM+"_sub";
						a.className = "button24_a bg_gray_hover border_gray fl";
						a.style.marginLeft = "5px";
						a.setAttribute("title",tc.dbuttonname[0]);
						a.innerHTML = "<span class=\"icon16 icon16save fl\"></span>"+tc.dbuttonname[0]+"";
						a.onclick = (tc.Callback)?function(){t.TCSubimtForm(tc.Callback,FNM,tc.suburl,tc.method,tc.recordid)}:function(){t.TCSubimtForm(t.TCFormBack,FNM,tc.suburl,tc.method,tc.recordid);};
						formbt.appendChild(a);
						a = null
					}
					if(tc.dbuttonname[1]){
						var b = document.createElement("span");
						b.className = "button24_a bg_gray_hover border_gray fl";
						b.style.marginLeft = "5px";
						b.setAttribute("title",tc.dbuttonname[1]);
						b.innerHTML = "<span class=\"icon16 icon16cancel1 fl\"></span>"+tc.dbuttonname[1]+"";
						b.onclick = function(){Tconfirm({Title:"提示信息",Ttype:"info",Content:"确定"+tc.dbuttonname[1]+"吗？<br><font style='color:#c00'>您刚修改的内容将被还原</font>",Catch:false,Coverobj:function(){if(formObj.id.substring(0,7) == "winbody"){return formObj.parentNode}else{return null}}(),OKFn:function(){if(tc.recordid){var hid = T(FNM+"_"+tc.recordid).val();document.forms[FNM].reset();T(FNM+"_"+tc.recordid).val(hid)}else{document.forms[FNM].reset();}}})};
						formbt.appendChild(b);
						b = null
						};
					formbt.className = "formbutton";
				};
				if(tc.buttons){
					for(var n=0;n<tc.buttons.length;n++)
					{
						var a = document.createElement("span");
						a.id = "a_"+tc.buttons[n].name;
						a.className = tc.buttons[n].cls?tc.buttons[n].cls:"button24_a bg_gray_hover border_gray fl";
						a.style.marginLeft = "5px";
						var btnimg = tc.buttons[n].icon?"<img src=\""+tc.Path+""+tc.buttons[n].icon+"\">":"";
						var iconspan = tc.buttons[n].iconcls?"<span class=\""+tc.buttons[n].iconcls+"\"></span>":"";
						a.title = tc.buttons[n].tit||tc.buttons[n].dname;
						a.innerHTML = ""+btnimg+""+iconspan+""+tc.buttons[n].dname+"";
						var tbtn = tc.buttons[n];
						a.name = tbtn.name;
						a.onpress = tbtn.onpress;
						a.onclick = (function(){
							this.onpress(this.name,FNM,formObj,tc.suburl,tc.updataurl,tc.method,tc.recordid);
						});
						formbt.appendChild(a);
						a = null
					};
				formbt.className = "formbutton";
				};
			};
			tc.buttonpos!="bt"?formdiv.appendChild(formbt):"";
			//创建表单输入区
			var inputhtml = [];
			//表单提示
			tc.formtip!=""?inputhtml.push(""+tc.formtip+""):"";
			for(var m=0;m<tc.formAttr[0].formitems.length;m++){
				var items = tc.formAttr[0].formitems[m].kinditemts;
					(tc.formAttr[0].formitems[m].kindname) ? inputhtml.push("<div style=\"width:100%;float:left;border-bottom:1px solid #ddd;margin-bottom:20px;padding-top:20px;font-size:12px;font-weight:700\">&nbsp;"+tc.formAttr[0].formitems[m].kindname+"</div>") : inputhtml.push("<div style=\"margin-top:5px;float:left;width:100%;height:1px;\"></div>");
					inputhtml.push( "<div id=\"div_"+FNM+"_"+m+"\" style=\"width:100%;border:none;overflow:auto;\">");
					for (var i=0;i<items.length;i++)
					{
						var Fnm = items[i].fieldname;
						var Remark = items[i].remark;
						var preRemark = items[i].premark;
						var Edi = items[i].edit;
						var iW = items[i].width?"width:"+items[i].width+"px;":"";
						var iH = items[i].height?"height:"+items[i].height+"px;":"";
						var hd = items[i].hide;
						var Dvalue = items[i].fieldvalue||(items[i].defaultValue?items[i].defaultValue:"");
						var nosub = items[i].nosub?"nosub=\"true\"":"";
						var colSpan = tc.Coltype?tc.Coltype:(items[i].colSpan?items[i].colSpan:1);
						if(items[i].inputtype == "multi"&&"undefined" == typeof(items[i].height)){iH = "height:90px;"};
						var Fst;
						var _Fst;
						var margin = "";//T.iev&&T.iev<8?"margin-left:0px;":"margin-left:0px;";
						Fst = (Edi==false)?""+nosub+"  style=\""+margin+"" + iW + "" + iH + "background:none;color:#666;border:1px solid #f0f0f0;\" disabled=\"disabled\" ":""+nosub+"  style=\""+margin+";" + iW + ";" + iH + "\" ";
						_Fst = (Edi==false)?""+nosub+"  style=\""+margin+"" + iW + "" + iH + "background:none;color:#666;border:1px solid #f0f0f0;\" disabled=\"disabled\" ":""+nosub+"  style=\""+margin+";" + iW + ";" + iH + "\" ";
						var Dfn = items[i].inputtype=="radio"||items[i].inputtype=="checkbox"?"":"onfocus=\"T.addcls(this,'h')\" onblur=\"T.remcls(this,'h')\"";
						var _Fn = "";
						if(items[i].fn){
							var eF = items[i].fn[0];
							Dfn += " "+eF.type+"=javascript:callbackfunc("+eF.fun+",'"+FNM+"',this.value)";
						};
						
						if(items[i].Fn){
							var eF = items[i].Fn[0];
							Dfn += " "+eF.type+"=javascript:"+eF.fun+"('"+FNM+"',this.value,this)";
							_Fn += " "+eF.type+"=javascript:"+eF.fun+"('"+FNM+"',this.value,this)";
						};
						
						if(items[i].rvalue){
							Dvalue = items[i].rvalue(Dvalue)
						};
						if(hd!=true){
							if(colSpan == 1){
								inputhtml.push("<div id=\"div_" + FNM + "_" + Fnm + "\" class=\"" + tc.C50Pct + "\">") 
							}else if(colSpan == 2){
								inputhtml.push("<div id=\"div_" + FNM + "_" + Fnm + "\" class=\"" + tc.C100Pct + "\">");
							}else if(colSpan == 3){
								inputhtml.push("<div id=\"div_" + FNM + "_" + Fnm + "\" style=\"width:auto;float:left\">");
							};
							if(items[i].fieldcnname){
								if(colSpan == 3){
									inputhtml.push( "<div class=\"" + tc.nmCls + "\" style=\"padding-right:5px;\">" + items[i].fieldcnname + "</div>");
									inputhtml.push( "<div style=\"width:auto;float:left;padding:0px;margin:0px;\">");
								}else{
									inputhtml.push( "<div class=\"" + tc.nmCls + "\" style=\"padding-right:5px;\">" + items[i].fieldcnname + "</div>");
									inputhtml.push( "<div class=\"" + tc.cnCls + "\">");
								}
							}else{
								inputhtml.push( "<div style=\"line-height:24px;\">");
							};
						};
						var extrahtml;
//						if(items[i].refn&&!hd){
						if(items[i].refn){
							var _id = this.tc.curRecordId;
							extrahtml = items[i].refn(FNM,Fnm,Dvalue,_id)
							inputhtml.push(extrahtml);
						}else{
							if(preRemark){
								inputhtml.push( "<span class='remark fl'>"+preRemark+"</span>");
							}
							switch (items[i].inputtype){
								case "word":
								inputhtml.push(items[i].word);
								break;
								case "text":
								case "doub":
								case "number":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input class=\"txt\" "+Dfn+" type=\"text\" "+Fst+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"");
								inputhtml.push(">");
								break;
								case "password":
									if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
									inputhtml.push( "<input class=\"txt\" "+Dfn+" type=\"password\" "+Fst+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"");
									inputhtml.push(">");
								break;
								case "checkbox":
									if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
									for(var j=0;j<items[i].noList.length;j++){
										inputhtml.push( "<input "+Dfn+" class=\"chk\" type=\"checkbox\" "+nosub+" name=\"" + Fnm + "\" value=\"" + items[i].noList[j].value_no + "\" id=\""+FNM+"_" + Fnm + "_" + items[i].noList[j].value_no + "\"");
										(Edi==false)?inputhtml.push(" disabled=\"disabled\" "):"";
										var fvalues = Dvalue;
										if(typeof(fvalues)!="object"&&fvalues!=""){
											fvalues = fvalues.toString().split(",");
										};
										for(var n=0;n<fvalues.length;n++){
											var fvalue = fvalues[n];
											if(fvalue == items[i].noList[j].value_no||fvalue == items[i].noList[j].value_name){inputhtml.push(" checked");}
										}
										inputhtml.push( ">");
										inputhtml.push( "<label class=\"lbl\" for=\""+FNM+"_" + Fnm + "_" + items[i].noList[j].value_no + "\"");
										T.ie?"":inputhtml.push(" style=\"padding-right:3px;padding-top:3px\"");
										inputhtml.push( ">" + items[i].noList[j].value_name + "</label>");
									}
									break;
								case "select":
									if(items[i].dataurl){
										var cname = "",cvalue = "";
										if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
											cname = Dvalue.split("||")[1];
											cvalue =  Dvalue.split("||")[0];
										}
										if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + cvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
										inputhtml.push( "<input class=\"iconinput sel bg_gray_hover border_gray\" "+_Fn+" readonly=\"readonly\" type=\"text\" nosub=\"true\" "+_Fst+" value=\"" + cname + "\" id=\""+FNM+"_" + Fnm + "_text\" name=\"" + Fnm + "_text\" onClick=\"treeSelect('"+tc.formFunId+"',this,'"+ items[i].dataurl+"');\"");
										if(Edi==false){
											inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\">")
										}else{
											inputhtml.push( " title=\"点击选择\" >");
											inputhtml.push( "<span title=\"点击选择\" id=\""+FNM+"_" + Fnm + "_text_clickspan\" class='button24_a bg_gray_hover border_gray fl' style='border-left:none' onClick=\"treeSelect('"+tc.formFunId+"',this.previousSibling,'"+ items[i].dataurl+"');\"> ")
											inputhtml.push( "<span id=\""+FNM+"_" + Fnm + "_text_clickicon\" class='icon16 icon16arrowdown fl' ></span>");
											inputhtml.push( "</span>") 
										};
										inputhtml.push( "<input type=\"hidden\" "+nosub+" value=\"" + cvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\">");
									}else{
										if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
											Dvalue = Dvalue.split("||")[1];
										}
										if(hd==true){
											inputhtml.push( "<select style=\"display:none\"  "+nosub+" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
										}else{
											//TODO
											inputhtml.push( "<select "+Dfn+" "+nosub+" style='width:110px' class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
										};
										if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
										if(items[i].width)
											inputhtml.push(" style='width:"+items[i].width+"px;'>");
										else
											inputhtml.push(">");
										for(var j=0;j<items[i].noList.length;j++){
											inputhtml.push( "<option value=\"" + items[i].noList[j].value_no + "\" ");
											(Dvalue == items[i].noList[j].value_no||Dvalue == items[i].noList[j].value_name)?inputhtml.push( " selected"):"";
											inputhtml.push( ">" + items[i].noList[j].value_name + "");
											inputhtml.push( "</option>");
										}
										inputhtml.push( "</select>");
									}
								break;
								case "selusers"://多选
									var names = Dvalue.split('||')[0];
									var ids = "";
									Dvalue.split('||')[1]?ids = Dvalue.split('||')[1]:"";
									inputhtml.push( "<input "+_Fn+" readonly  "+_Fst+"  value=\"" + names + "\" id=\""+FNM+"_" + Fnm + "_text\" name=\"" + Fnm + "_text\" ");
									if(hd==true){
										inputhtml.push(" type=\"hidden\" />")
									}else if(Edi==false){
										inputhtml.push( " class='txt' />");
									}else{
										inputhtml.push( "class=\"iconinput sel bg_gray_hover border_gray\" type=\"text\" onclick=\"selectusers('"+FNM+"_" + Fnm +"_text','1');\" />")
										inputhtml.push( "<span id=\""+FNM+"_" + Fnm + "_text_clickspan\" title=\"点击选择人员\" class='button24_a bg_gray_hover border_gray fl' style='border-left:none' onclick=\"selectusers('"+FNM+"_" + Fnm +"_text','1');\"> ")
										inputhtml.push( "<span id=\""+FNM+"_" + Fnm + "_text_clickicon\" class='icon16 icon16user1 fl'></span>");
										inputhtml.push( "</span>")
									}
									inputhtml.push( "<input "+nosub+" type=\"hidden\" value=\"" + ids + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\">");
									names = null;
									ids = null;
									break;
								case "date":
									if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
									inputhtml.push( "<input "+_Fn+" readonly=\"readonly\" type=\"text\" "+_Fst+" value=\"" + (Dvalue) + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"  onClick=\"WdatePicker({startDate:'%y-%M-01 00:00:00',alwaysUseStartDate:true});\"");
									if(Edi==false){inputhtml.push(" class=\"txt\" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\">")}else{
									inputhtml.push( "class=\"iconinput sel bg_gray_hover border_gray\" title=\"点击选择\" >");
									inputhtml.push( "<span title=\"点击选择\" onClick=\"WdatePicker({startDate:'%y-%M-01 00:00:00',alwaysUseStartDate:true,el:'"+FNM+"_" + Fnm + "'});\" class='button24_a bg_gray_hover border_gray fl' style='border-left:none'> ")
									inputhtml.push( "<span class='icon16 icon16calendar fl' ></span>");
									inputhtml.push( "</span>") 
									};
								break;
								case "sdate":
									if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
									inputhtml.push( "<input "+_Fn+" readonly=\"readonly\" type=\"text\" "+_Fst+" value=\"" + (Dvalue) + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"  onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true});\"");
									if(Edi==false){inputhtml.push(" class=\"txt\" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\">")}else{
									inputhtml.push( "class=\"iconinput sel bg_gray_hover border_gray\" title=\"点击选择\" >");
									inputhtml.push( "<span title=\"点击选择\" onClick=\"WdatePicker({dateFmt:'yyyy-MM-dd',startDate:'%y-%M-01',alwaysUseStartDate:true,el:'"+FNM+"_" + Fnm + "'});\" class='button24_a bg_gray_hover border_gray fl' style='border-left:none'> ")
									inputhtml.push( "<span class='icon16 icon16calendar fl' ></span>");
									inputhtml.push( "</span>") 
									};
								break;
								case "showmap":
									if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"> ");break};
									inputhtml.push( "<input "+Dfn+" type=\"text\" class=\"txt\" "+Fst+" value=\"" + Dvalue + "\" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\"");
									if(Edi==false){
										inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\">")
									}else{
										inputhtml.push( ">");
										inputhtml.push("<input onclick=\"showtqmap('"+FNM+"')\" id=\""+FNM+"_" + Fnm + "_showmap\" type=\"button\" value =\"地图标注\"/>");
									};
									break;
								case "hour":
									if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
										Dvalue = Dvalue.split("||")[1];
									}
									if(hd==true){
										inputhtml.push( "<select style=\"display:none\" "+nosub+" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									}else{
										//TODO
										inputhtml.push( "<select "+Dfn+" "+nosub+" style=\"width:110px\" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									};
									if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
									inputhtml.push(">");
									for(var j=0;j<hour_select.length;j++){
										inputhtml.push( "<option value=\"" + hour_select[j].value_no + "\" ");
										(Dvalue == hour_select[j].value_no||Dvalue == hour_select[j].value_name)?inputhtml.push( " selected"):"";
										inputhtml.push( ">" + hour_select[j].value_name + "");
										inputhtml.push( "</option>");
									}
									inputhtml.push( "</select>");
									break;
									
								case "minute":
									if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
										Dvalue = Dvalue.split("||")[1];
									}
									if(hd==true){
										inputhtml.push( "<select style=\"display:none\" "+nosub+" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									}else{
										//TODO
										inputhtml.push( "<select "+Dfn+" "+nosub+" style=\"width:110px\" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									};
									if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
									inputhtml.push(">");
									for(var j=0;j<minute_select.length;j++){
										inputhtml.push( "<option value=\"" + minute_select[j].value_no + "\" ");
										(Dvalue == minute_select[j].value_no||Dvalue == minute_select[j].value_name)?inputhtml.push( " selected"):"";
										inputhtml.push( ">" + minute_select[j].value_name + "");
										inputhtml.push( "</option>");
									}
									inputhtml.push( "</select>");
									break;
								case "fminute":
									if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
										Dvalue = Dvalue.split("||")[1];
									}
									if(hd==true){
										inputhtml.push( "<select style=\"display:none\" "+nosub+" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									}else{
										//TODO
										inputhtml.push( "<select "+Dfn+" "+nosub+" style=\"width:110px\" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									};
									if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
									inputhtml.push(">");
									for(var j=0;j<fminute_select.length;j++){
										inputhtml.push( "<option value=\"" + fminute_select[j].value_no + "\" ");
										(Dvalue == fminute_select[j].value_no||Dvalue == fminute_select[j].value_name)?inputhtml.push( " selected"):"";
										inputhtml.push( ">" + fminute_select[j].value_name + "");
										inputhtml.push( "</option>");
									}
									inputhtml.push( "</select>");
									break;
								case "pminute":
									if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
										Dvalue = Dvalue.split("||")[1];
									}
									if(hd==true){
										inputhtml.push( "<select style=\"display:none\" "+nosub+" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									}else{
										//TODO
										inputhtml.push( "<select "+Dfn+" "+nosub+" style=\"width:110px\" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									};
									if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
									inputhtml.push(">");
									for(var j=0;j<pminute_select.length;j++){
										inputhtml.push( "<option value=\"" + pminute_select[j].value_no + "\" ");
										(Dvalue == pminute_select[j].value_no||Dvalue == pminute_select[j].value_name)?inputhtml.push( " selected"):"";
										inputhtml.push( ">" + pminute_select[j].value_name + "");
										inputhtml.push( "</option>");
									}
									inputhtml.push( "</select>");
									break;
								case "price_tactic":
									if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
										Dvalue = Dvalue.split("||")[1];
									}
									inputhtml.push( "<select "+Dfn+" "+nosub+" style=\"width:110px\" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
									inputhtml.push(">");
									for(var j=0;j<minute_select.length;j++){
										inputhtml.push( "<option value=\"" + minute_select[j].value_no + "\" ");
										(Dvalue == minute_select[j].value_no||Dvalue == minute_select[j].value_name)?inputhtml.push( " selected"):"";
										inputhtml.push( ">" + minute_select[j].value_name + "");
										inputhtml.push( "</option>");
									}
									inputhtml.push( "</select>");
									break;	
								case "multi":
									var mulvalue = Dvalue;
									var regex = new RegExp("<br />", "g");
									mulvalue = mulvalue.replace(regex,"\n");
									mulvalue = typeof(mulvalue)=="object"?T.Obj2Str(mulvalue):mulvalue;//对象格式采用textarea
									if(hd==true){inputhtml.push(" <textarea style=\"display:none\" "+nosub+" id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\" >" + mulvalue + "</textarea> ");break};
									inputhtml.push( "<textarea id=\""+FNM+"_" + Fnm + "\" name=\"" + Fnm + "\" class=\"txt\" "+Dfn+" "+Fst+">" + mulvalue + "</textarea>");
									break;
								case "cselect":
									if(Dvalue!=""&&Dvalue.indexOf("||")!=-1){
										Dvalue = Dvalue.split("||")[1];
									}
									var targetid =FNM+"_" + items[i].target; 
									var action = items[i].action;
//									alert(targetid);
									if(hd==true){
										inputhtml.push( "<select style=\"display:none\"  "+nosub+" class=\"slct\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									}else{
										inputhtml.push( "<select "+Dfn+" "+nosub+" style=\"width:210px\" class=\"slct\" onchange=\"cactic_Select(this.value,'"+targetid+"','"+action+"')\" name=\"" + Fnm + "\" id=\""+FNM+"_" + Fnm + "\"")
									};
									if(Edi==false){inputhtml.push(" disabled=\"disabled\" style=\"background:#f7f7f7;color:#000;\" ")};
									inputhtml.push(">");
									for(var j=0;j<items[i].noList.length;j++){
										inputhtml.push( "<option value=\"" + items[i].noList[j].value_no + "\" ");
										(Dvalue == items[i].noList[j].value_no||Dvalue == items[i].noList[j].value_name)?inputhtml.push( " selected"):"";
										inputhtml.push( ">" + items[i].noList[j].value_name + "");
										inputhtml.push( "</option>");
									}
									inputhtml.push( "</select>");
									break;
							}
						};					
						if(hd!=true){
							//验证提示DOM
							inputhtml.push( "<span style='width:80%;clear:both;line-height:14px;*margin-left:0px;display:none' id = '"+FNM+"_" + Fnm + "_t'></span>");
							if(Remark){
								inputhtml.push( "<span class='remark'>"+Remark+"</span>");
							};
							inputhtml.push( "</div>");
							inputhtml.push( "</div>");
						}
					};
					inputhtml.push( "</div>");
						
			};
			//表单底部提示
			tc.formtipbt!=""?inputhtml.push(""+tc.formtipbt+""):"";
			tc.endLable?inputhtml.push( "<div class=\"clear\" style=\"width:100%;height:20px;overflow:auto;font-style:italic;color:#c7c7c7;margin-top:10px;border-top:1px solid #e7e7e7;text-align:left\">真来电（北京）</div>"):"";

			//设置输入区宽高
			var extheight = 0;
			var linshiH = 0;
			var fObjW = tc.formObj.offsetWidth||200;
			var fObjH = tc.formObj.offsetHeight||100;
			linshiH =(fObjH<30)?30:0;
			(tc.formtitle)? extheight += 30:"";
			(tc.formpower)? extheight += 40:extheight -= 1;
			extheight += linshiH;
			inputdiv.style.width = tc.fit[0]?fObjW - 2 + "px":"100%";
			inputdiv.style.height =linshiH==0?fObjH - extheight + "px":"auto";
			tc.fit[2]?"":inputdiv.style.height = "auto";
			if(tc.islisten == 1 && tc.fit[0]==true){
				var formFitObj = T.iev&&T.iev<9?tc.formObj:window;
				T.bind(formFitObj,"resize",function(){
					setTimeout(function(){
						try{
							if(tc.fit[1]||"undefined" == typeof(tc.fit[1]))T("#"+tc.formname+"_input_div").style.width = formObj.offsetWidth!==0? formObj.offsetWidth - 2 + "px":formObj.parentNode.offsetWidth - 2 + "px";
							if(tc.fit[2]||"undefined" == typeof(tc.fit[2]))T("#"+tc.formname+"_input_div").style.height = (formObj.offsetHeight!==0) ? formObj.offsetHeight - extheight + linshiH + "px":formObj.parentNode.offsetHeight - extheight + linshiH + "px"
						}catch(e){}
					},100)
				});	
			};
			inputdiv.innerHTML = inputhtml.join("");
			formdiv.appendChild(inputdiv);
			tc.buttonpos=="bt"?formdiv.appendChild(formbt):"";
		};
	
		//设置表单CSS样式
		if(tc.Css){T.LJC("/css/tq_style/"+tc.Css+"","c")}
		
		//输出表单
		tc.formObj.appendChild(formdiv);
		
		//高度修正
		setTimeout(function(){
			try{
				if(tc.height != "auto"){
					T("#"+tc.formname+"_input_div").style.height = (tc.formObj.offsetHeight!==0) ? tc.formObj.offsetHeight - extheight + linshiH + "px":tc.formObj.parentNode.offsetHeight - extheight + linshiH + "px";
				}
			}catch(e){}
		},500);
		//是否验证
		if(tc.formAttr[0].rules&&tc.formAttr[0].rules.length>0){
			var rcd = "";
			tc.recordid?rcd = FNM+"_"+tc.recordid:"";
			setTimeout(function(){Tcheck(FNM,tc.formAttr[0].rules,"",tc.formObj,rcd)},100);
		};
		//初始化地区
		if(tc.areaData&&tc.areaData!=""){
			//alert(tc.areaData);
			initLocalName(tc.areaData.split("||")[0],tc.areaData.split("||")[1]);
		};
		if(tc.loadfun){
			tc.loadfun(FNM);	
		}
		//销毁
		formdiv = null;
		formtit = null;
		formbt = null;
		inputdiv = null
	},
	//设置表单大小
	SizeForm : function(t){
		var t = t||this;
		var tc = t.tc;
		var formObj = tc.formObj;
		var extheight;
		var fObjW = tc.formObj.offsetWidth||formObj.parentNode.offsetWidth;
		var fObjH = tc.formObj.offsetHeight||formObj.parentNode.offsetHeight;
		var extheight = 0;
		(tc.formtitle)? extheight += 30:"";
		(tc.formpower)? extheight += 40:"";
		//var formFitObj = T.iev?formObj:window;
		try{
			setTimeout(function(){
			if(tc.fit[1]||"undefined" == typeof(tc.fit[1]))T("#"+tc.formname+"_input_div").style.width = formObj.offsetWidth!==0? formObj.offsetWidth - 2 + "px":formObj.parentNode.offsetWidth - 2 + "px";
			if(tc.fit[2]||"undefined" == typeof(tc.fit[2]))T("#"+tc.formname+"_input_div").style.height = (formObj.offsetHeight!==0) ? formObj.offsetHeight - extheight + "px":formObj.parentNode.offsetHeight - extheight + "px"
			},100)
		}catch(e){};
	},
	//验证
	checkForm: function(){
		//var TFormCheckSign = 0;
		var _rcd = "";
		this.tc.recordid?_rcd = this.tc.formname+"_"+this.tc.recordid:"";
		if(this.tc.formAttr[0].rules&&this.tc.formAttr[0].rules.length>0){
			Tcheck(this.tc.formname,this.tc.formAttr[0].rules,"sub",T("#"+this.tc.formname).parentNode,_rcd)
		}else{
			TFormCheckSign=1;
		};
		return TFormCheckSign
	},
	subForm: function(){
		var fn,f,suburl,meth,rcd,thirdparam;
		fn = this.tc.Callback?this.tc.Callback:this.TCFormBack;
		f = this.tc.formname;
		suburl = this.tc.suburl;
		meth = this.tc.method;
		rcd = this.tc.recordid;
		thirdparam = this.tc.thirdparam;
		
		var _rcd = "";
		rcd?_rcd = f+"_"+rcd:"";
		
		if(T("#"+this.tc.formname+"_"+rcd)){
			T("#"+this.tc.formname+"_"+rcd).value==""?"":suburl=this.tc.updataurl||suburl;
		};
		if(this.tc.subFun){
			var _isCan = this.tc.subFun(f);
			if(!_isCan)
				return;
		};
		function tfromSub(ret,o,thirdparam){fn(f,rcd,ret,o,thirdparam)};
		(meth == "POST")?T.A.sendData(suburl,"POST",Serializ(f),tfromSub,0,T("#"+this.tc.formname).parentNode,thirdparam):T.A.sendData(suburl+"&"+Serializ(f),"GET","",tfromSub,0,T("#"+this.tc.formname).parentNode,thirdparam);
	},
	//提交表单
	TCSubimtForm : function(){//fn,f,suburl,meth,rcd
		if(this.checkForm()){//验证
			this.subForm();//提交
		}
	},
	//默认回调
	TCFormBack : function(f,rcd,r,o){
		(r==0)?T.loadTip(1,"操作失败，请重试！",2,o):(T.loadTip(1,"操作成功！",2,o));try{T(f+"_"+rcd).val(r)}catch(e){}
	},
	
	//隐藏某个字段
	hideField : function(fieldname){
		T("#div_"+this.tc.formname+"_"+fieldname).style.display = "none";
	},
	//显示某个字段
	showField : function(fieldname){
		T("#div_"+this.tc.formname+"_"+fieldname).style.display = "block";
	},
	//取得某个字段的页面对象
	getField : function(fieldname){
		return T("#"+this.tc.formname+"_"+fieldname)
	},
	//设置某个字段的值
	setValue : function(fieldname,value){
		T("#"+this.tc.formname+"_"+fieldname).value = value;
	},
	//取得某个字段在页面的显示值
	getValue : function(fieldname){
		if(T("#"+this.tc.formname+"_"+fieldname)){
			return T("#"+this.tc.formname+"_"+fieldname).value
		}else{//checkbox or radio
			var ret = []
			var nodes = document.forms[this.tc.formname];
			for(var i=0;i<nodes.length;i++){
				if(nodes[i].name == fieldname){
					if(nodes[i].checked==true){
						ret.push(nodes[i].value)
					}
				}
			};
			return ret.join(",")
		};
	}
}

function showtqmap(fid){
	var value =document.getElementById(fid+"_address").value;
	var _longitude = document.getElementById(fid+"_longitude").value;
	var _latitude = document.getElementById(fid+"_latitude").value;
	Twin({
		Id:"tqmap_id",
		Title:"地图标注",
		Content:"<iframe id=\"tqmapwin\" src=\"tq_map.html?address="+value+"&longi="+_longitude+"&lati="+_latitude+"&r="+Math.random()+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>",
		Width:T.gww()/2,
		Height:T.gwh()-200,
		buttons:[{icon:"ok.gif",dname:"确定  ",onpress:function(id){
			var iframeWin = document.getElementById("tqmapwin").contentWindow;
				var lon = iframeWin.longitude;
				var lat =iframeWin.latitude;
				//alert(lon+","+lat)
				var local = iframeWin.document.getElementById("text_2").value;
				if(local=='')
					local = iframeWin.document.getElementById("text_1").value;
				if(lon==''||lat==''){
					T.loadTip(1,"请选择地点！",2,null);
				}else{
					T(fid+"_longitude").val(lon);
					T(fid+"_latitude").val(lat);
					T(fid+"_address").val(local);
					TwinC("tqmap_id");
				}
			}},
			{icon:"cancel.gif",dname:"取消  ",onpress:function(id){
				TwinC("tqmap_id");
			}
		}]
	})
}




