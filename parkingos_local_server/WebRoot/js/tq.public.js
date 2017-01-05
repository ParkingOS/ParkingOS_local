/*Public Fucntion 2012-06-22
 Latest: 2013-11-03 
*/
/*===发送短信===*/
var smsrule = [
				{name:"mobile",type:"",requir:true,warn:"接收号码不能为空",okmsg:""},
				{name:"content",type:"",requir:true,warn:"短信内容不能为空",okmsg:""}
			];
function CsmsHtml(phonenumInfo,obj){
	var coverobj = phonenumInfo[2]? phonenumInfo[2]:null;
	Tform({
		//formtitle:"发送短信",
		formid:"smsSendForm",
		formname:"smsSendForm",
		recordid:"id",
		dbuttons:false,
		formObj:obj,
		fit:[false],
		suburl:"shortmsginner.do",
		updataurl:"",
		formtipbt:"<div class=\"formtip\" style=\"width:312px\">提示：短信发送受到运营商网络影响，并不能保证100%发送成功和即时接收，如果该短信内容对您的客户非常重要，请随后通过短信记录检查短信发送状态。</div>",
		//Callback:function(f,r,c,o){T.loadTip(1,c,2,o)},
		method:"POST",
		formAttr:[{
			formitems:[{kindname:"",kinditemts:[
						{fieldcnname:"管理员ID",fieldname:"admin_uin",fieldvalue:phonenumInfo[0],inputtype:"text",colSpan:2,width:"215",hide:true},
						{fieldcnname:"接收号码",fieldname:"mobile",fieldvalue:phonenumInfo[1],inputtype:"text",colSpan:2,width:"215",edit:false},
						{fieldcnname:"短信内容<br>(最多输入330字)",fieldname:"content",fieldvalue:"",inputtype:"multi",colSpan:2,width:"215",height:60}
					]}],
			rules:smsrule
		}],
		buttons : [//工具
			{name: "send", dname: "确定发送", iconcls: "icon16 icon16sms fl",
			onpress:function(btname,formname,formObj,suburl,updataurl,method){
						Tcheck(formname,smsrule,"sub",formObj);//验证
						if(TFormCheckSign==1){
							function smsformSub(ret){smsSendSub(formname,ret)};
							T.A.sendData(suburl,"POST",Serializ(formname),smsformSub,0,formObj);
						}
					}
},
			{name: "cancel", dname: "取消", iconcls:"icon16 icon16cancel fl", onpress: function(){TwinC("sms_w","","",coverobj)} }
		]
	});
};
function smsSendSub(f,r){
	if(r=="0"){
		T.loadTip(1,"短信发送成功！",2);
		TwinC("sms_w")
	}else if(r=="11"){
		T.loadTip(2,"短信余额不足,请充值！<br>您刚才的发送短信操作已被保存为<br><font style='color:#c00'>&lt;未发送短信记录&gt;</font><br>充值完成后，您可以对此记录进行<font style='color:#c00'>重发</font>")
	}else if(r=='1001'){
		T.loadTip(2,"第三方发送，请到相应服务商后台查询发送结果。");
	}else{
		T.loadTip(2,"发送失败！<br>您刚才的发送短信操作已被保存为<br><font style='color:#c00'>&lt;未发送短信记录&gt;</font><br>短信功能恢复正常后，您可以对此记录进行<font style='color:#c00'>重发</font>")
	}
};
function messageout(o,coverobj){
	T.cancelBub();
	if(typeof(coverobj)=="string"){
		coverobj = coverobj&&T("#"+coverobj).id.substring(0,7) == "winbody"?T("#"+coverobj).parentNode:null;
	};
	var smsc = T(o)?T(o).val():o;
	if(!admin_uin||admin_uin==""){T.loadTip(1,"无法发送短信，管理员帐号错误!",2);return}
	if("undefined"==typeof(sendpower)||sendpower!="true"){T.loadTip(1,"您未开通短信功能或没有发送短信的权限，请联系管理员!",2);return}
	if(smsc==""){
		T.loadTip(1,"号码不能为空!",1,coverobj);
//	}else if(/^(1[4358].{9})|(01[4358].{9})$/.test(smsc)==false){
//		T.loadTip(1,"不是手机号码,不能发送短信",1,coverobj);
	}else{
		Twin({Id:"sms_w",Title:"发送短信",Content:"",sysfun:CsmsHtml,sysfunI:[admin_uin,smsc,coverobj],Width:360,Height:275,Coverobj:coverobj})
	}
};
//var mongourl = "http://127.0.0.1/mj";//本地测试
var mongourl = "http://211.157.150.98:8080/mj/";//线上测试
//var mongourl = "http://mdb.tq.cn/mj";//线上
/*===上传文件===*/
var uploadresult="";//上传到mongojava后，返回IFRAME，请求vip服务，处理请求后给这个变量赋值
function uploadfiles(o,oldfile){
	var adminId = admin_uin;
	var uin = uin;
	var file=oldfile||"";
	var type=typeof(uploadtype)=='undefined'?'bills':uploadtype;
	var uploadurl = "mogoupload.do?file="+file+"&type="+type;
	var fileurl = o+"_fileurl";
	Twin({
		Id:o+"_win",
		Title:"上传",
		Mask:false,
		Width:400,
		Content:"<iframe name='"+o+"' id='"+o+"_f' src='"+uploadurl+"' width='100%' height='150' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>",
		//Content:"上传的URL",
		buttons:[
			{dname:"确定",name:"ok",icon:"ok.gif",onpress:function(){
				if(uploadresult==""){
					T.loadTip(1,"请先上传文件!",1);
					return;
				};
				var getresult = uploadresult[0].name;
				var getresulturl = uploadresult[0].name;
				if(!getresult){
					T.loadTip(1,"上传失败，请重新上传!",1);
				};
				var ov = T("#"+o).value;
				ov = ov==''?getresult:ov+"|"+getresult;
				try{
					while(ov.indexOf('||')!=-1){
						ov = ov.replace('||','|');
					}
				}catch(e){}
				T("#"+o).value = ov;
				var newuplist = document.createElement("div");
				newuplist.style.width = "90%";
				newuplist.style.float = "left";
				newuplist.style.cssFloat = "left";
				newuplist.innerHTML = "<ul class='fl' style='overflow:hidden;width:70%;line-height:18px;border-bottom:1px solid #ddd' title='"+getresult+"'>"+getresult+"</ul><ul class='fl' onclick=\"openmongofiles('"+getresult+"')\" style='padding-left:1px;cursor:pointer' title='查看/下载'><img src='/vip/tqccresource/tq_images/form/grid_look.gif' /></ul><ul class='fl' onclick=\"delmongofiles('"+getresult+"',this,'"+o+"')\" style='padding-left:1px;cursor:pointer' title='删除'><img src='/vip/tqccresource/tq_images/form/delete.png' /></ul>"
				T(o).apch(newuplist,"be");
				var select_id = o.substring(0,o.length-8); //存放名称对应id的input的ID
				if(T("#"+select_id+"fileurl")){
					//alert(T("#"+select_id+"fileurl").name);
					T("#"+select_id+"fileurl").value = getresulturl;
				};
				uploadresult="";
				TwinC(o+"_win","","","",2);
				}
			},
			{dname:"取 消",name:"cancel",icon:"",onpress:function(){TwinC(o+"_win","","","",2)}}
		]
	})
};
function uploadfile(o){
var adminId = admin_uin;
	var uin = uin;
	var file="";
	var type=typeof(uploadtype)=='undefined'?'bills':uploadtype;
	var uploadurl = "mogoupload.do?file="+file+"&type="+type;
	var fileurl = o+"_fileurl";
	Twin({
		Id:o+"_win",
		Title:"上传",
		Mask:false,
		Width:400,
		Content:"<iframe name='"+o+"' id='"+o+"_f' src='"+uploadurl+"' width='100%' height='150' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>",
		//Content:"上传的URL",
		buttons:[
			{dname:"确定",name:"ok",icon:"ok.gif",onpress:function(){
				if(uploadresult==""){
					T.loadTip(1,"请先上传文件!",1);
					return;
				};
				var getresult = uploadresult[0].name;
				var getresulturl = uploadresult[0].name;
				if(!getresult){
					T.loadTip(1,"上传失败，请重新上传!",1);
				};
				var ov = T("#"+o).value;
				ov = ov==''?getresult:ov+"|"+getresult;
				try{
					while(ov.indexOf('||')!=-1){
						ov = ov.replace('||','|');
					}
				}catch(e){}
				T("#"+o).value = ov;
				uploadresult="";
				TwinC(o+"_win","","","",2);
				}
			},
			{dname:"取 消",name:"cancel",icon:"",onpress:function(){TwinC(o+"_win","","","",2)}}
		]
	})

}
/*===打开上传文件===*/
function openupfile(o){
	var url = T("#"+o).value;
	if(!url){
		T.loadTip(1,"请先上传文件!",1);
	}else{
		window.open(mongourl+"filelist.do?adminid="+admin_uin+"&filename="+url+"&type=&token="+token);
	}
};
/*===打开mongo上传文件===*/
function openmongofiles(f){
	window.open(mongourl+"filelist.do?adminid="+admin_uin+"&filename="+f+"&type="+uploadtype+"&token="+token);
};
/*===删除mongo上传文件===*/
function delmongofiles(f,self,o){
	//T.A.sendData(mongourl+"/filelist.do?operate=deletefile&adminid="+admin_uin+"&filename="+f+"&type="+uploadtype+"&token="+token);
	//window.open(mongourl+"/filelist.do?adminid="+admin_uin+"&filename="+f+"&type="+uploadtype+"&token="+token);
	var url = mongourl+"filelist.do?operate=deletefile&adminid="+admin_uin+"&filename="+f+"&type="+uploadtype+"&token="+token;
	Twin({
		Id:"delfile_win",
		Title:"警告信息",
		Mask:false,
		Width:300,
		Height:130,
		Content:"<div class='win_confirm' style='margin-right:5px;'></div><div style='float:left;width:200px;font-weight:700;margin-top:2px;'>确定要删除改文件吗?<iframe name='delfile_iframe' id='delfile_iframe' src='' width='90%' height='40' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe></div>",
		//Content:"上传的URL",
		buttons:[
			{dname:"确定",name:"ok",icon:"ok.gif",onpress:function(){
				var ov = T("#"+o).value;
				if(ov.indexOf(f+"|")!=-1)
					ov = ov.replace(f+"|","");
				else
					ov = ov.replace(f,"");
				T("#"+o).value=ov;
				document.getElementById("delfile_iframe").src = url;
				T("#delfile_win_a_ok").style.display = "none";
				T("#delfile_win_a_cancel").innerHTML = "删除中……";
				T("#delfile_win_a_cancel").onclick = null;
			 	self.parentNode.parentNode?self.parentNode.parentNode.removeChild(self.parentNode):"";
				setTimeout(function(){
					//T.loadTip(1,"删除成功!",1);
					TwinC("delfile_win","","","","0");
				},2000)
			}},
			{dname:"取消",name:"cancel",icon:"cancel.gif",onpress:function(){
				TwinC("delfile_win","","","","0");
			}}
		]
	})
};
/*===外呼===*/
function tq_call_out(phone_num){
	//if(/^(1[4358]\d{9})|(01[4358]\d{9})$/.test(phone_num)){
	//	phone_num.substring(0,1)!="0"?phone_num = "0"+phone_num:""
	//}
	try{window.navigate("app:1234567@"+phone_num+""); } catch(e){};
};
/*function callout(o,hideo){
	alert(o+"_"+hideo)
};
*/
function callout(o,hideo){
	hideo = hideo?hideo:o;
	if(T(o).val()==""){
		T.loadTip(1,"号码不能为空!",1);
	}else{
		T(hideo).val()==""?T.tq_call_out(T(o).val()):T.tq_call_out(T(hideo).val())
	}
};

/*===发邮件===*/
function mailout(o){
	if(T(o).val()==""){
		T.loadTip(1,"邮件不能为空!",1);
	}else{
		window.open("mailto:"+ T(o).val())
		//window.location.href = "mailto:"+ T(o).val()
	}
};
/*===打开网址===*/
function checkeURL(URL){
	var strRegex = "^((https|http)?://)"  
         + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@  
         + "(([0-9]{1,3}\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184  
         + "|" // 允许IP和DOMAIN（域名） 
         + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.  
         + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\." // 二级域名  
        + "[a-z]{2,6})" // first level domain- .com or .museum  
        + "(:[0-9]{1,4})?" // 端口- :80  
        + "((/?)|" // a slash isn't required if there is no file name  
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";  
        var re=new RegExp(strRegex);  
  //re.test() 
        if (re.test(URL)){ 
            return (true);  
        }else{  
            return (false);  
        } 
};
function openurl(o){
	var oUrl = T(o).val();
	if(checkeURL(oUrl)){
		var urltype = oUrl.substr(0,4).toUpperCase();
		//alert(urltype);
		oUrl = (urltype!="HTTP"||urltype!="HTTPS")?"http://"+oUrl:oUrl;
		window.open(oUrl)
	}else{
		T.loadTip(1,"网址格式不对!",1)
	}
};
/*===打开搜索===*/
function wwwsearch(o){
	window.open("http://www.baidu.com/s?wd="+T(o).val()+"");
};
/*===单选员工===*/
function selectuser(o,isall,isdepartment,operate){//isall:1表示返回全部人员，为空表示返回权限内人员
	var div = document.getElementById(o+"_w");
	if(!div){
			div = document.createElement("div");
			div.setAttribute('id',o+"_w");
			div.style.position = "absolute";
			div.style.border = "1px solid #889DAD";
			div.style.width = "353px";
			div.style.height = "200px";
			div.style.zIndex = "9999";
			div.innerHTML = "<iframe name='"+o+"' id='"+o+"_f' src='tqccresource/tq_js/danxuan.html?isall="+isall+"&operate="+operate+"&isdepartment="+isdepartment+"' width='100%' height='200' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>";
		};
		div.style.display = "block";
		var pos = T.gpos(o);
		div.style.left = pos.x  +"px";
		(T.gwh() + T.scrollFix().y - pos.y > 240)?div.style.top = pos.y + pos.height + "px":(div.style.top = "",div.style.bottom = T.gwh() - pos.y + "px");
		if(!document.getElementById(o+"_w")){
			document.body.appendChild(div);
		}else{		
			var iobj = document.getElementById(o+"_f"); 
			var ifobj = iobj.contentWindow.document.getElementById("querytext");
			if(ifobj){(ifobj.value!="")?ifobj.select():ifobj.focus()};
		};
		setTimeout(
			function(){document.body.onclick=function(e){
			 e=window.event?window.event:e;
			 var e_tar=e.srcElement?e.srcElement:e.target;
			 if(e_tar.id.indexOf(o)!=-1)
			 {
				  return;
			 }
			 else
			 {
				 T("#"+o+"_w")?document.body.removeChild(T("#"+o+"_w")):"";
			 };
			document.body.onclick=null;
		}},500)
};
/*===多选员工===*/
function selectusers(o,isall,isdepartment){//isall:1表示返回全部人员，为空表示返回权限内人员
	//var pos = T.gpos(o);
	//var _left = pos.x;
	//var _top = pos.y + 21;
	//(T.gwh() - pos.y < 260)?_top = pos.y - 260:"";
/*	Twin({
		Id:o+"_seluser_win",
		Title:"选择",
		//Left:_left,
		//Top:_top,
		Refer:o,
		Catch:false,
		zIndex:999,
		Width:353,
		Height:260,
		Mask:false,
		Content:"<iframe name='"+o+"' id='"+o+"_f' src='tqccresource/tq_js/duoxuan.html?isall="+isall+"&isdepartment="+isdepartment+"' width='100%' height='205' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>",
		buttons:[
			{dname:"确定选择",name:"ok",icon:"ok.gif",onpress:function(){
				var frameObj = document.getElementById(o+"_f").contentWindow;
				var ret = frameObj.getSels();
				var select_name = o; //存放名称的input的ID
				var select_id = o.substring(0,o.length-5); //存放名称对应id的input的ID
				if(T("#"+select_name)){
					T("#"+select_name).value = ret.select_names;
					T(select_name).acls("txt");
				};
				if(T("#"+select_id)){
					T("#"+select_id).value = ret.selids;
				};
				TwinC(o+"_seluser_win","","","",2);
				}
			},
			{dname:"清 空",name:"deleteall",icon:"cancel.gif",onpress:function(){
				var select_name = o; //存放名称的input的ID
				var select_id = o.substring(0,o.length-5); //存放名称对应id的input的ID
				if(T("#"+select_name)){
					T("#"+select_name).value = "";
					T(select_name).acls("txt");
				};
				if(T("#"+select_id)){
					T("#"+select_id).value = "";
				};
				TwinC(o+"_seluser_win","","","",2);
				}
			},
			{dname:"取 消",name:"cancel",icon:"",onpress:function(){TwinC(o+"_seluser_win","","","",2)}}
		]
	})
*/	
	var div = document.getElementById(o+"_w");
	if(!div){
			div = document.createElement("div");
			div.setAttribute('id',o+"_w");
			div.style.position = "absolute";
			div.style.border = "1px solid #889DAD";
			div.style.width = "353px";
			div.style.height = "235px";
			div.style.zIndex = "100";
			div.innerHTML = "<iframe name='"+o+"' id='"+o+"_f' src='tqccresource/tq_js/duoxuan.html?isall="+isall+"&isdepartment="+isdepartment+"' width='100%' height='235' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>";
		};
		div.style.display = "block";
		var pos = T.gpos(o);
		div.style.left = pos.x  +"px";
		(T.gwh() + T.scrollFix().y - pos.y > 240)?div.style.top = pos.y + pos.height + "px":(div.style.top = "",div.style.bottom = T.gwh() - pos.y + "px");
		if(!document.getElementById(o+"_w")){
			document.body.appendChild(div);
		}else{		
			var iobj = document.getElementById(o+"_f"); 
			var ifobj = iobj.contentWindow.document.getElementById("querytext");
			if(ifobj){(ifobj.value!="")?ifobj.select():ifobj.focus()};
		};
		setTimeout(
			function(){document.body.onclick=function(e){
				 e=window.event?window.event:e;
				 var e_tar=e.srcElement?e.srcElement:e.target;
				 if(e_tar.id.indexOf(o)!=-1)
				 {
					  return;
				 }
				 else
				 {
					T("#"+o+"_w")?document.body.removeChild(T("#"+o+"_w")):"";
					//T("#"+o+"_w").style.display="none";
				 };
				document.body.onclick=null;
			}
		},500)
};
/*===新建客户===*/
function CreateC(url,title){
	Twin({Id:"CreateClient_win",Title:title,Width:"800",Height:"auto",sysfun:function(tObj){
			CreateRecordF = new TQForm({
				formFunId:"CreateRecordF",
				formname: "client_add_f",
				formid:"CreateRecordF",
				formObj:tObj,
				recordid:"id",
				suburl:url,//"clientmanage.do?action=create",
				method:"POST",
				formAttr:[{
					formitems:getCleintFields(1),
					rules:function(){
						var l = CvalidateFields();
						for(var m=0;m<l.length;m++){
							if(!l[m].type){l[m].type=""};
							if(l[m].type.indexOf("ajax")!=-1){
								l[m].url = "visitorInfomation.do?oper=testValue&col="+l[m].name+"&operate=create&value="
							}
						};
						return l
					}()
				}],
				buttons : [//工具
					{name: "cancel", dname: "取 消", tit:"取消"+title,iconcls:"", onpress:function(){TwinC("CreateClient_win");} }
				],
				Callback:
				function(f,rcd,ret,o){
					if(ret=="1"||ret.indexOf('success')!=-1){
						T.loadTip(1,"新建成功！",2,"");
						TwinC("CreateClient_win");
						if(typeof(_clientT)==="object"){
							_clientT.M()
						}
					}else{
						T.loadTip(1,"新建失败，请重试！",2,o)
					}
				}
			});	
			CreateRecordF.C();
		}
	})
};


	
/*===新建产品===*/
function Cproduct(url,title,callback){
	var fitems;
	var fvalue;
	if(typeof(getProductFields)=="function"&&typeof(ProvalidateFields)=="function"){
		fitems = getProductFields(1);
		fvalue = ProvalidateFields()
	}else{
		var ajaxfitem = eval(T.A.sendData("productmanage.do?action=getmodel&r="+Math.random()));
		fitems = ajaxfitem[0].fields[0];
		fvalue = ajaxfitem[0].validateFields;
		fvalue.push({name:'product_name',type:'',url:'',requir:true,warn:'',okmsg:''})
		fvalue.push({name:'product_type',type:'',url:'',requir:true,warn:'',okmsg:''})

	};
	Twin({Id:"CreateProductt_win",Title:title,Width:"750",Height:"auto",sysfun:function(tObj){
			Tform({
				formname: "product_add_f",
				formObj:tObj,
				recordid:"id",
				suburl:url,//"clientmanage.do?action=create",
				method:"POST",
				formAttr:[{
					formitems:fitems,
					rules:function(){
						var l = fvalue;
						return l
					}()
				}],
				buttons : [//工具
					{name: "cancel", dname: "取消", tit:"取消"+title,icon:"cancel.gif", onpress:function(){TwinC("CreateProductt_win");} }
				],
				Callback:
				function(f,rcd,ret,o){
					if(ret=="1"||ret.indexOf('success')!=-1){
						T.loadTip(1,"新建成功！",2,"");
						TwinC("CreateProductt_win");
						callback?callback():"";
					}else{
						T.loadTip(1,"新建失败，请重试！",2,o)
					}
				}
			});	
		}
	})
};
/*===选择客户===*/
function selcetClient(cid,cobj){//cobj:覆屏对象
    Twin({
		Coverobj:cobj,
        Id:"selClient_w",
        Width:600,
        Height:350,
		Title:"选择客户",
		sysfunI:cid,
    	sysfun:function(cid,tObj){
    		T.A.sendData("clientmanage.do","POST","action=getmodel",
				function(r){
					var fielditems = r[0];
					var _SclientT = new TQTable({
		            	tableObj:tObj,
						fieldorder:fielditems.searchSort[0].sort.toString(),
		    			checktype:"radio",
		            	tablename:"client_search_t",
						trclickfun:true,
						dataUrl:"clientmanage.do",
		            	param:"action=quickquery&type=allclient",
						searchitem:[
							{name:"客户姓名",field:"client_name",type:"text",isdefault:true},
							{name:"客户手机",field:"client_mobile",type:"text"},
							{name:"固定电话",field:"client_telephone",type:"text"},
							{name:"公司名称",field:"client_company",type:"text"},
							{name:"客户地址",field:"client_adress",type:"text"}
						],
		    			buttons:[{
		    				name: "ok",
		    				dname: "确定选择",
		    				iconcls: "icon16 icon16ok fl",
		    				tit:"确定选择",
		    				onpress:function(){
		        				if(_SclientT.GS()){
		        					T("#"+cid).value = _SclientT.GS(1);
		    						TwinC("selClient_w","","",cobj)
		        				};
		    				
		    				}
		    			},{
		    				name: "cancel",
		    				dname: "取消",
		    				iconcls: "icon16 icon16cancel fl",
		    				tit:"取消选择",
		    				onpress:function(){
		    					TwinC("selClient_w","","",cobj)
		    				}
		    			}
						],
					tableitems:fielditems.fields[0].kinditemts
		        	});
					_SclientT.C()	
				},2,tObj);
   		}
 	})
};
/*===根据客户id选择联系人===*/
function selcetLinkMan(cid,cobj,pid){//cobj:覆屏对象 pid 父客户id
    Twin({
		Coverobj:cobj,
        Id:"selContact_w",
        Width:600,
        Height:350,
		Title:"选择联系人",
		sysfunI:cid,
    	sysfun:function(cid,tObj){
    		T.A.sendData("contactmanage.do","POST","action=getmodel",
				function(r){
					var fielditems = r[0];
					var _ScontactT = new TQTable({
		            	tableObj:tObj,
						fieldorder:fielditems.searchSort[0].sort.toString(),
		    			checktype:"radio",
		            	tablename:"client_search_t",
						trclickfun:true,
						dataUrl:"contactmanage.do",
		            	param:"action=query&range=&visitor_id="+pid,
						searchitem:[
							{name:"联系人姓名",field:"client_name",type:"text",isdefault:true}
						],
		    			buttons:[{
		    				name: "ok",
		    				dname: "确定选择",
		    				iconcls: "icon16 icon16ok fl",
		    				tit:"确定选择",
		    				onpress:function(){
		        				if(_ScontactT.GS()){
		        					T("#"+cid).value = _ScontactT.GS(1);
		    						TwinC("selContact_w","","",cobj)
		        				};
		    				
		    				}
		    			},{
		    				name: "cancel",
		    				dname: "取消",
		    				iconcls: "icon16 icon16cancel fl",
		    				tit:"取消选择",
		    				onpress:function(){
		    					TwinC("selContact_w","","",cobj)
		    				}
		    			}
						],
					tableitems:fielditems.fields[0].kinditemts
		        	});
					_ScontactT.C()	
				},2,tObj);
   		}
 	})
};
/*===导入客户===*/
function ImportC(){
	var content="<div align=center></br>"+
        	"<font color=RED size=3></font><form name='inportform' id='inportform' method='post'  onsubmit='return checkInput();'"+
        	" action='importVisitorInfo.do' encType='multipart/form-data'> "+
        	"<table>"+
        	"<tr height='40px'><td align='right'>文件名：</td><td align='left'>"+
			"<input type='file' name='upfile' id='upfile' size='35'/>&nbsp;&nbsp;<input type='hidden' name ='oper' value='preimport'/><input type='hidden' name ='type' value='importvisitors'/></td></tr>"+
            "<tr><td align='right'><font color='green'>说明：</font></td><td align='left'>"+
            "<font color='green'>导入前请认真检查文件表头与自定义列是否对应;</font><br/>"+
            "<font color='green'>路径中不能有中文字符;</font><br/>"+
             "<font color='green'>支持Excel2007文件（.xlsx）导入;</font><br/><span id='errroraaa'></span></td></tr>"+
             "<tr><td align='right'></td><td align='right'><button type='submit' class='button'>开始导入</button></td></tr>"+
			"</table></form>"+
			"</div>";
	Twin({Id:"CreateClient_win",Title:"导入客户",Width:"400",Height:"200",Content:content})
};
/*===高级搜索===*/
var tqsearchForm = null;
function supperSerach(formitems,SubAction,SubFun,LoadFun,fieldorder,isInView,tableViewTree){
	//是否显示在当前视图查询
	var _isInView = (isInView == "no")?false:true;
	tableViewTree = tableViewTree?tableViewTree:("undefined"!=typeof(Client_TREE)?Client_TREE:false);
	Twin({Catch:false,Id:"tq_supper_search_win",Title:"高级搜索",Width:"850",Height:"auto",sysfun:function(tObj){
			tqsearchForm = new TQSForm({
				formname: "tq_supper_search_f",
				formObj:tObj,
				formFunId:"tqsearchForm",//必须
				suburl:"action=query",
				method:"POST",
				fieldorder:fieldorder,
				loadfun:LoadFun?LoadFun:null,
				subFun:SubFun?SubFun:null,
				SubAction:SubAction||function(f,o,url){
					var viewCondition = "|type|ownner|days|csqlid|";
					var extargs = "";
					if(tableViewTree&&T("#tq_supper_search_f_range")&&T("#tq_supper_search_f_range").checked == true){
						var nPvalue = tableViewTree.tc.nodeFnArgs;
						var FocusArgs = tableViewTree.getFocus();
						T.each(nPvalue.split(","),function(o,m){
							viewCondition.indexOf("|"+o+"|")!=-1?extargs += "&"+o+"="+T.utf8(FocusArgs[o]):""
						});
					};
					if(typeof(_workBillT)=='object'){
						_workBillT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:url+extargs+"&"+Serializ(o)
						},"","");
					}else{
						_clientT.C({
							cpage:1,
							tabletitle:"高级搜索结果",
							extparam:url+extargs+"&"+Serializ(o)
							//addtionitem:{fieldname:"client_flag",fieldcnname:"所属状态",inputtype:"text",hide:true,process:function(v){
							//	if(v==1){return "<font style=\"color:blue\">回收站</font>"}else if(v==2){return "<font style=\"color:#c00\">客户池</font>"}
							//	else{return "<font style=\"color:green\">有负责人</font>"}}}
						})
					}
				},
				formAttr:[{
					formitems:formitems||getCleintFields()
				}],
				otherformAttr:
				function(){
					if(_isInView){
						return [{kindname:'',kinditemts:[{fieldcnname:"查询范围",fieldname:"range",fieldvalue:'',inputtype:"checkbox",noList:[{value_name:"当前视图中搜索",value_no:"1"}]}]	}]
					}else{
						return false
					}
				}(),
				buttons : [//工具
					{name: "cancel", dname: "取消", tit:"取消查询",iconcls:"icon16 icon16cancel fl", onpress:function(){TwinC("tq_supper_search_win",true);} }
				]
			});	
			tqsearchForm.conditionField = [];
		}
	})
};
/*===自定义视图===*/
function  defineSerach(){
	Twin({Id:"defineSerach_client_win",Title:"添加客户列表快捷视图",Width:"850",Height:"auto",sysfun:function(tObj){
			TSform({
				formname: "defineSerach_client__f",
				formObj:tObj,
				suburl:"action=query",
				method:"POST",
				dbuttons:false,
				formAttr:[{
					formitems:getCleintFields(),
					rules:[{name:'model_name',requir:true,warn:'必须输入模板名称',okmsg:''}]
				}],
				otherformAttr:[
					{
						kindname:'',
						kinditemts:
							[
								{fieldcnname:"模板名称",fieldname:"model_name",fieldvalue:'',inputtype:"text","hide":false}
							]
					}
				],
				buttons : [//工具
					{name: "cancel", dname: "确定添加", tit:"点击确定",icon:"ok.gif", onpress:function(){
						TwinC("defineSerach_client_win");}
					},
					{name: "cancel", dname: "取消", tit:"取消",icon:"cancel.gif", onpress:function(){TwinC("defineSerach_client_win");} }
				],
				Callback:
					function(){
						TwinC("CreateClient_win");
					}
			});	
		}
	})
};
/*根据ID查询并弹出客户信息
  id:客户id
  isedit:是否可编辑
  callback:可编辑时回调函数
*/
var show_visitor_form = "";
function show_visitor(id,isedit,callback,coverobj){
	if(!id){T.loadTip(1,"值为空时，不可查询客户详情",2);return};
	var isedit = isedit||false;
	Twin({Id:"client_detail",Coverobj:coverobj,zIndex:10,Title:"查看客户信息",Width:750,Height:500,sysfunI:id,sysfun:function(id,tObj){
			T.A.sendData("ajaxclient.do?action=getmodel","POST","visitor_id="+id,
				function(f){
					show_visitor_form = new TQForm({
						formname: "client_detail_f",
						formObj:tObj,
						formFunId:"show_visitor_form",
						suburl:"clientmanage.do?action=edit",
						method:"POST",
						formAttr:[{
							formitems:f[0].fields,
							rules:f[0].validateFields
						}],
						buttons : [//工具
							{name: "cancel", dname: "关闭", tit:"关闭",icon:"cancel.gif", onpress:function(){TwinC("client_detail")} }
						],
						formpower:isedit,
						Callback:callback||function(f,rcd,ret,o){
							if(ret=="1"){
								T.loadTip(1,"保存成功！",2,"");
								TwinC("client_detail");
							}else{
								T.loadTip(1,"保存失败，请重试！",2,o)
							}
						}
			    	});	
			    	show_visitor_form.C();
				},2,tObj);
    	}
    })
}
/*根据ID查询并弹出联系人信息
  id:客户id
  isedit:是否可编辑
  callback:可编辑时回调函数
*/
var show_linkman_form = "";
function show_linkman(id,isedit,callback,coverobj){
	if(!id){T.loadTip(1,"值为空时，不可查询联系人详情",2);return};
	var isedit = isedit||false;
	Twin({Id:"contact_detail",Coverobj:coverobj,zIndex:10,Title:"查看联系人信息",Width:750,Height:400,sysfunI:id,sysfun:function(id,tObj){
			T.A.sendData("contactmanage.do?action=getmodel","POST","id="+id,
				function(f){
					show_visitor_form = new TQForm({
						formname: "client_detail_f",
						formObj:tObj,
						formFunId:"show_visitor_form",
						geturl:"contactmanage.do?action=simplequery&colname=id&value="+id,
						suburl:"clientmanage.do?action=edit",
						method:"POST",
						formAttr:[{
							formitems:f[0].fields,
							rules:f[0].validateFields
						}],
						buttons : [//工具
							{name: "cancel", dname: "关闭", tit:"关闭",iconcls:"icon16 icon16cancel fl", onpress:function(){TwinC("contact_detail")} }
						],
						formpower:isedit,
						Callback:callback||function(f,rcd,ret,o){
							if(ret=="1"){
								T.loadTip(1,"保存成功！",2,"");
								TwinC("contact_detail");
							}else{
								T.loadTip(1,"保存失败，请重试！",2,o)
							}
						}
			    	});	
			    	show_visitor_form.C();
				},2,tObj);
    	}
    })
}


/*===快捷添加===*/
function QuickCreate(eo,o,w,h){//eo鼠标触发对象,o位置对象,w宽度(为空时为位置对象宽度),h高度(为空时为auto
	var c = document.createElement("div");
	var chtml = [];
	//chtml.push("<a href=\"#\" onclick=\"javascript:alert('d')\"><img src=\"FT_images/bg/d4.png\"></a>");
	var auth_cc =(auth_addclient)?"":" disabled=true ";
	chtml.push("<a class=\"qmenu\" href=\"javascript:void(0)\" "+auth_cc+" onclick=\"CreateC()\"><span class=\"icon_client\"></span>新建客户</a>");
	chtml.push("<a class=\"qmenu\" href=\"javascript:void(0)\" onclick=\"CreateC()\"><span class=\"icon_client\"></span>新建联系人</a>");
	chtml.push("<a class=\"qmenu\" href=\"javascript:void(0)\" onclick=\"CreateC()\"><span class=\"icon_plan\"></span>新建日程/任务</a>");
	chtml.push("<a class=\"qmenu\" href=\"javascript:void(0)\" onclick=\"CreateC()\"><span class=\"icon_orders\"></span>新建合同/订单</a>");
	chtml.push("<a class=\"qmenu\" href=\"javascript:void(0)\" onclick=\"CreateC()\"><span class=\"icon_product\"></span>新建服务单</a>");
	c.innerHTML = chtml.join("");	
	var w = w?w:T("#"+o).offsetWidth;
	var div = document.getElementById(eo+"_w");
	if(!div){
			div = document.createElement("div");
			div.setAttribute('id',eo+"_w");
			div.style.position = "absolute";
			div.style.border = "1px solid #98BFE2";
			div.style.background = "#fff";
			div.style.width = w + 1 + "px";
			div.style.overflow = "hidden";
			div.style.height = h?h+"px":"auto";
			div.style.zIndex = "100";
		};
		div.innerHTML = "";	
		div.appendChild(c);
		div.style.display = "block";
		var pos = T.gpos(o);
		div.style.left = pos.x +"px";
		(T.gwh() - pos.y > 40)?div.style.top = pos.y + T("#"+o).offsetHeight - 2 + "px":(div.style.top = "",div.style.bottom = T.gwh() - pos.y + "px");
		if(!document.getElementById(eo+"_w")){
			document.body.appendChild(div);
		};
		setTimeout(
			function(){document.body.onclick=function(e){
			 e=window.event?window.event:e;
			 var e_tar=e.srcElement?e.srcElement:e.target;
			 if(e_tar.id==eo)
			 {
				return;
			 }
			 else
			 {
				T("#"+eo+"_w").style.display="none";
			 }
		}},500)
};


function checkInput(){
	var value=document.getElementById('upfile').value;
	var errormsg = "";
	if(value=='')
		errormsg="请选择文件！";
	else{
		var fur = value.substring(value.length-4);
		if(fur.indexOf('xls')==-1&&fur.indexOf('xlsx')==-1)
			errormsg ='文件名不合法！';
	}
	if(value.length!=value.replace(/[^\x00-\xff]/g,"**").length)
		errormsg="文件名不能包含中文字符！";
	if(errormsg!=''){
		document.getElementById('errroraaa').innerHTML ="<font color=red>"+errormsg+"</font>";
		return false;
	}else
		document.getElementById('errroraaa').innerHTML ="<font color=red>正在导入，请稍候....</font>";
	return true;
};
String.prototype.gb=function(){
	return escape(this.toString());
};
String.prototype.utf8=function(){
	return  escape(encodeURIComponent(this.toString()));
};

//序列化表单
function Serializ(nodes,charset,updateFieldStr){//noFieldStr 为ture时提交updatefieldstr参数给后台,为空或未定义时不提交
		charset = charset?charset.toLowerCase():"utf-8";
		nodes = document.forms[nodes];
		var data="";
		var updatefieldstr = []; 
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].getAttribute("nosub")=="true"){continue;}
			if(T.trim(nodes[i].name)==""){continue;}
			updatefieldstr.join(",").indexOf(nodes[i].name)==-1?updatefieldstr.push(nodes[i].name):"";
			if(nodes[i].type.toLowerCase()=="checkbox"){
				if(nodes[i].checked==true){
					if(charset=="utf-8"){
						data += nodes[i].name + "=" + (T.trim(nodes[i].value)=="" ? "": nodes[i].value.utf8()) + "&";
					}else{
						data += nodes[i].name + "=" + (T.trim(nodes[i].value)=="" ? "": nodes[i].value).gb() + "&";
					}
				}else{
					data += nodes[i].name + "=&";
				}
			}else if(nodes[i].type.toLowerCase()=="radio"){
				if(nodes[i].checked==true){
					if(charset=="utf-8"){
						data += nodes[i].name + "=" + (T.trim(nodes[i].value)=="" ? "": nodes[i].value.utf8()) + "&";
					}else{
						data += nodes[i].name + "=" + (T.trim(nodes[i].value)=="" ? "": nodes[i].value).gb() + "&";
					}
				}
			}
			else{
				if(charset=="utf-8"){
					data += nodes[i].name + "=" + T.trim(nodes[i].value).utf8() + "&";
				}else{
					data += nodes[i].name + "=" + T.trim(nodes[i].value).gb() + "&";	
				};
			}		
		};
		updateFieldStr?data += "updatefieldstr="+updatefieldstr.join("__")+"&":"";
		return data; 
	};
	
function DragFun(obj,t,fixfun){//0x,1y,fixfun其它事件(调整表格大小,表单大小等)
	obj.onmousedown = function(evt){//开始
		var mObj;
		if(t==0){
			var xDrag = document.getElementById("Layout_xDrag");
			if(!xDrag){
				xDrag = document.createElement("div");
				xDrag.id = "Layout_xDrag";
				xDrag.style.cssText="display:block;border-left:dotted 1px #000;z-index:9999;position:absolute;width:5px;height:2000px;top:0px;left:-10px;cursor:e-resize";
				document.body.appendChild(xDrag)
			};
			mObj = xDrag;
		}else if(t==1){
			var yDrag = document.getElementById("Layout_yDrag");
			if(!yDrag){
				yDrag = document.createElement("div");
				yDrag.id = "Layout_yDrag";
				yDrag.style.cssText="display:block;border-top:dotted 1px #000;z-index:9999;position:absolute;width:2000px;height:5px;top:-10px;left:0px;cursor:s-resize;";
				document.body.appendChild(yDrag)
			};
			mObj = yDrag;
		}else{
			return	
		};
		
		var oldposX = this.offsetLeft;
		var oldposY = this.offsetTop;
		var evt = evt || window.event;
		var pos = T.gpos(this);
		mObj.style.display = "block";
		if(t==0){
			var preX = evt.clientX;
			mObj.style.left = preX + "px";
		}else{
			var preY = evt.clientY;
			mObj.style.left = pos.x + "px";
			mObj.style.top = preY + "px";
		};
		if ((!window.captureEvents)&&(!evt.preventDefault)) { //若IE
			mObj.setCapture(); 
			mObj.onmousemove = function(evt){//拖动
				var evt = evt || window.event; 
				var newX = evt.clientX;
				var newY = evt.clientY;
				t==0?mObj.style.left=newX+"px":mObj.style.top=newY+"px";
			};
			mObj.onmouseup = IeDragX;
		}else { //非IE及IE9.0
			T("doc").aevt("mousemove",NotIeDragCol,"");
			T("doc").aevt("mouseup",NotIeDragX,"");
			evt.preventDefault()
		};
		function IeDragX(evt){ //IE改变列宽
			mObj.releaseCapture(); 
			mObj.onmousemove = null;
			mObj.onmouseup = null;
			var evt = event;
			if(t==0){
				var newX = evt.clientX;
				var newWidth =  newX - preX + oldposX + 4;
				if(newWidth<50) newWidth = 50;
				if(newWidth>T.gww()-200) newWidth = T.gww() - 200;
				mObj.style.left = "-10px";
				layoutL.style.width = newWidth + "px";
				T.scok(layoutL.id,newWidth);//保存cookie
			}else{
				var newY = evt.clientY;
				var newHeight =  T.gwh() - newY;
				if(newHeight<100) newHeight = 100;
				if(newHeight>T.gwh()-150) newHeight = T.gwh() - 150;
				//mObj.style.top = "-10px";
				mObj.style.display = "none";
				layoutR_B.style.height = newHeight - bheight + "px";
				T.scok(layoutR_B.id,newHeight - bheight);//保存cookie
			};
			ResizeLayout();
			if(fixfun){
				T.each(fixfun,function(o,j){
					o.f(o.p)
				})
			};
			document.unselectable  = "off";
			document.onselectstart = null
		};
		function NotIeDragCol(evt){//非IE拖动
			var newX = evt.clientX;
			var newY = evt.clientY;
			t==0?mObj.style.left=newX + "px":mObj.style.top=newY+"px";
		};
		function NotIeDragX(evt){//非IE改变宽高
			T("doc").revt("mouseup",NotIeDragX,"");
			T("doc").revt("mousemove",NotIeDragCol,"");
			if(t==0){
				var newX = evt.clientX;
				var newWidth =  newX - preX + oldposX - 2;
				if(newWidth<50) newWidth = 50;
				if(newWidth>T.gww()-200) newWidth = T.gww() - 200;
				mObj.style.left = "-10px";
				layoutL.style.width = newWidth + "px";
				T.scok(layoutL.id,newWidth);//保存cookie
			}else{
				var newY = evt.clientY;
				var newHeight =  T.gwh() - newY;
				if(newHeight<100) newHeight = 100;
				if(newHeight>T.gwh()-150) newHeight = T.gwh() - 150;
				//mObj.style.top = "-10px";
				mObj.style.display = "none";
				layoutR_B.style.height = newHeight - bheight + "px";
				T.scok(layoutR_B.id,newHeight - bheight);//保存cookie
			};
			ResizeLayout();
			if(fixfun){
				T.each(fixfun,function(o,j){
					o.f()//o.f(o.p)
				})
			};
			if(typeof userSelect === "string"){
				return document.documentElement.style[userSelect] = "text";
			}
			document.unselectable  = "off";
			document.onselectstart = null
		}
	}
};
//新版下拉树选择
//clickfun 若为匿名函数,函数过程中不能用双引号

function treeSelect(formid,inputobj,dataurl,clickfun){
		var _dataType = (dataurl!="undefined"&&dataurl!=""&&dataurl!="null"&&dataurl.indexOf('.do')!=-1)?"1":"0";
		var o = inputobj.id;
		var n = inputobj.name;
		n = n.substring(0,n.length-5);
		var select_name = o; //存放名称的input的ID
		var select_id = o.substring(0,o.length-5); //存放名称对应id的input的ID
		var expandLevel=2;
		if(dataurl.indexOf('localdata')!=-1)//客户地区，只展示一级
			expandLevel=1;
		var isTool = _dataType == 1?true:false;
		var isSearch=true;
		if(_dataType=='0'){
			isSearch=false;
			var formFunction = eval(formid);
			var treeData = {},dataObj = formFunction.GFA(n,"noList");
			var id,name,_i=0;
			for(var i=0,j=dataObj.length;i<j;i++){
				id = dataObj[i].value_no;
				name = dataObj[i].value_name;
				treeData["root_"+dataObj[i].value_no] = {id:id,name:name};
				_i += 1;
			};
			_i>15?isSearch=true:"";
		}
		//alert(isSearch);
		var _Twin = new Twindow({
			Id:o+"_win",
			Title:"选择",
			TitleH:26,
			Refer:inputobj,
			Width:200,
			MaxH:280,
			MinH:false,
			//Coverobj:formFunction.tc.formObj,
			zIndex:100001,
			Mask:false,
			sysfun:function(tObj){
				tObj.style.overflow = "auto";
				tObj.style.background = "#EDF1F8";
				tObj.style.borderBottom = "1px solid #ccc";
				function selOK(selid,seltext){
					var sname = (selid!=""&&seltext!="请选择")?seltext:"";
					T("#"+select_name)?T("#"+select_name).value = sname:"";
					T("#"+select_id)?T("#"+select_id).value = selid:"";
					if(clickfun)clickfun(selid,seltext);
					TwinC(o+"_win",false,"","",2);
				};
				eval(""+o+"_tree_select = new tqTree({"+
					"treeId:\""+o+"_tree_select\","+
					"treeObj:tObj,"+
					"isSearch:"+isSearch+","+
					"isTool:"+isTool+","+
					"dataType:"+_dataType+","+
					"dataUrl:dataurl,"+
					"ajaxTip:T('#"+o+"').parentNode,"+
					"MinH:200,"+
					"localData:treeData,"+
					"nodeFnArgs:\"id,name\","+
					"loadfun:function(id,name){"+
					"eval(o+'_tree_select').expandLevel("+expandLevel+");"+
					//"	T('#winbody_"+o+"_win').style.height='200px'"+
					"},"+
					"nodeClick:function(id,name){"+
					"	selOK(id,name)"+
					"}"+
				"});");
				eval(o+"_tree_select").C();
			},
			CloseFn : function(){
				eval(""+o+"_tree_select = null");
			},
			buttons:[
			{dname:"x 清空",tit:"清空当前输入项的值",iconcls:"",onpress:function(){
				T("#"+select_name)?T("#"+select_name).value = "":"";
				T("#"+select_id)?T("#"+select_id).value = "":"";
				if(clickfun)clickfun();
				TwinC(o+"_win",false,"","",2);
			}},
			{dname:"取 消",iconcls:"",onpress:function(){
				eval(""+o+"_tree_select = null");
				TwinC(o+"_win",false,"","",2);
			}}
			]
		});
		_Twin.C();
		setTimeout(
			function(){document.body.onclick=function(e){
			 e=window.event?window.event:e;
			 var e_tar=e.srcElement?e.srcElement:e.target;
			 if(e_tar.id.indexOf(o)!=-1)
			 {
				  return;
			 }
			 else
			 {
			 	TwinC(o+"_win",false,"","",2);
			 };
			document.body.onclick=null;
		}},500)
};
/*===下拉选择层cactic===*/
function selectCactic(o,isall,dataurl,type){//isall:1表示返回全部数据，为空表示返回权限内数据,tree的值请求地址ajax
	if(type=="win"&&dataurl!="undefined"){
		Twin({
			Id:o+"_cactic_win",
			Title:"选择",
			TitleH:26,
			Refer:T("#"+o),
			Width:200,
			Height:200,
			Mask:false,
			sysfun:function(tObj){
				tObj.style.overflow = "auto";
				var select_name = o; //存放名称的input的ID
				var select_id = o.substring(0,o.length-5); //存放名称对应id的input的ID
				function selOK(selid,seltext){
					//var sname = selid!=""?seltext+"("+selid+")":"";
					var sname = selid!=""?seltext:"";
					T("#"+select_name)?T("#"+select_name).value = sname:"";
					T("#"+select_id)?T("#"+select_id).value = selid:"";
					TwinC(o+"_cactic_win",false,"","",2);
				};

				var treeDataUrl = dataurl||"demodata.txt";
				var t = new TQTree({
					treeid:o+"_tree",
					treename:"请选择",
					allopen:true,
					treeObj:tObj,
					dataUrl:treeDataUrl,
					rootClick:function(id,name){
						selOK("","")
					},
					nodeClick:function(id,name){
						selOK(id,name)
					}
				});
				t.C();
			}
		})
	}else{
		var div = document.getElementById(o+"_w");
		if(!div){
				div = document.createElement("div");
				div.setAttribute('id',o+"_w");
				div.style.position = "absolute";
				div.style.border = "1px solid #889DAD";
				div.style.width = "180px";
				div.style.height = "200px";
				div.style.zIndex = "100";
				div.innerHTML = "<iframe name='"+o+"' id='"+o+"_f' src='/vip/tqccresource/tq_js/tqtree.html?isall="+isall+"' width='100%' height='200' frameborder='0' scrolling='no' style='overflow:hidden;' ></iframe>";
			};
			div.style.display = "block";
			var pos = T.gpos(o);
			div.style.left = pos.x  +"px";
			(T.gwh() + T.scrollFix().y - pos.y > 240)?div.style.top = pos.y + pos.height  + "px":(div.style.top  = pos.y - pos.height - 177 + "px");
			if(!document.getElementById(o+"_w")){
				document.body.appendChild(div);
			};
		setTimeout(
			function(){document.body.onclick=function(e){
				 e=window.event?window.event:e;
				 var e_tar=e.srcElement?e.srcElement:e.target;
				 if(e_tar.id.indexOf(o)!=-1)
				 {
					  return;
				 }
				 else
				 {
					 T("#"+o+"_w")?document.body.removeChild(T("#"+o+"_w")):"";
				 };
				document.body.onclick=null;
			}},500)
	}
};


function getParam(paramName)
{
        paramValue = "";
        isFound = false;
        if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=")>1)
        {
            arrSource = unescape(this.location.search).substring(1,this.location.search.length).split("&");
            i = 0;
            while (i < arrSource.length && !isFound)
            {
                if (arrSource[i].indexOf("=") > 0)
                {
                     if (arrSource[i].split("=")[0].toLowerCase()==paramName.toLowerCase())
                     {
                        paramValue = arrSource[i].split("=")[1];
                        isFound = true;
                     }
                }
                i++;
            }   
        }
   return paramValue;
};
function banKeySpace(e){   
    var ev = e || window.event;//获取event对象   
    var obj = ev.target || ev.srcElement;//获取事件源   
    var t = obj.type || obj.getAttribute('type');//获取事件源类型  
    //获取作为判断条件的事件类型
    var vReadOnly = obj.getAttribute('readonly');
    var vEnabled = obj.getAttribute('enabled');
    //处理null值情况
    vReadOnly = (vReadOnly == null) ? false : vReadOnly;
    vEnabled = (vEnabled == null) ? true : vEnabled;
    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，
    //并且readonly属性为true或enabled属性为false的，则退格键失效
    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea") && (vReadOnly==true || vEnabled!=true))?true:false;
    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效
    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")?true:false;        
    //判断
    if(flag2){
		ev.keyCode=0;
        return false;
    }
    if(flag1){   
        ev.keyCode=0;
		return false;   
    };
	if((ev.keyCode==116)||//屏蔽F5  
		(ev.ctrlKey  &&  ev.keyCode==82)){//Ctrl+R  
		ev.keyCode=0;  
		return false;  
	};
	if(ev.keyCode==122){ev.keyCode=0;return false}; //屏蔽F11  
	if(ev.ctrlKey  &&  ev.keyCode==78){return false}; //屏蔽  Ctrl+n  
	if(ev.shiftKey  &&  ev.keyCode==121){return false}//屏蔽  shift+F10  
	
};
function show_select(input, btn, option, value,formid ,conditioninput) {
	conditioninput = conditioninput||"value";
    inputobj = document.getElementById(input);
    btnobj = document.getElementById(btn);
    optionobj = document.getElementById(option);
    valueobj = document.getElementById(value);
    optionobj.parentNode.style.display = "block";
    optionobj.style.display = optionobj.style.display == "" ? "none": "";
	optionobj.style.left =T.gpos(inputobj).left +"px";
	optionobj.style.top =T.gpos(inputobj).top + T.gpos(inputobj).height +"px";
    optionobj.onblur = function() {
    	optionobj.parentNode.style.display = "none";
        optionobj.style.display = "none";
    }
    for (var i = 0; i < optionobj.childNodes.length; i++) {
        optionobj.focus();
        optionobj.childNodes[i].onmouseover = function() {
            this.className = "t_items_over"
        }
        optionobj.childNodes[i].onmouseout = function() {
            this.className = "t_items_out"
        }
        optionobj.childNodes[i].onclick = function() {
            optionobj.style.display = "none";
            inputobj.innerHTML = this.innerHTML;
			valueobj.value = this.tid||this.getAttribute("tid");
			var conditionObj = document.forms[formid][conditioninput];
			var conditionParent = conditionObj.parentNode;
			var inputtype = this.getAttribute("inputtype")||"text";
			var nHtml = "";
			switch(inputtype){
				case "date":
					nHtml = "<input type=\"text\" title=\"双击清空\" style=\"width:90px;height:21px;padding:0px;margin:0px;border:0px;float:left\" name=\""+conditioninput+"\" id=\""+(formid+"_"+conditioninput)+"\" autocomplete=\"off\" ondblclick=\"this.value=''\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'{%y-1}-%M-%d',maxDate:'{%y+1}-%M-%d',readOnly:true});\">"
					break;
				default:
				nHtml = "<input type=\"text\" title=\"双击清空\" style=\"width:90px;height:21px;padding:0px;margin:0px;border:0px;float:left\" name=\""+conditioninput+"\" id=\""+(formid+"_"+conditioninput)+"\" autocomplete=\"off\" ondblclick=\"this.value=''\">"
			}
			conditionParent.innerHTML = nHtml;
			conditionParent.firstChild.focus();
			
            optionobj.blur();
        }
    }
};
//根据客户ID获取客户姓名
function GetClientName(id){
	var clientInfo = eval(T.A.sendData("/vip/ajaxclient.do?action=getmodel&visitor_id="+id+"&r="+Math.random()+""));
	clientInfo = clientInfo[0].fields[0].kinditemts;
	var rvalue = "";
	for (var i = 0;i<clientInfo.length;i++){
		if(clientInfo[i].fieldname=="client_name"){
			rvalue += clientInfo[i].fieldvalue
		};
	};
	return rvalue
}
//IVR节点选择
function ivrNodeSelect(formFunId,inputobj,ivrtype,seatId){
		var o = inputobj.id;
		var n = inputobj.name;
		var uboxid = inputobj.getAttribute("uboxid");
		n = n.substring(0,n.length-5);
		var select_name = o; //存放名称的input的ID
		var select_id = o.substring(0,o.length-5); //存放名称对应id的input的ID
		
		var formFunction = eval(formFunId);
		var treeData = {},dataObj = IvrNodeList[uboxid].nodeList;
		joinNode(dataObj);//拼接挂断 无效标识
		if(typeof(ivrtype)=="undefined"){
			if(typeof(seatId)=="undefined"){//无坐席工号
				dataObj=dnisNodeList[uboxid].nodeList;
			}
		}else{
			if(ivrtype=="ivr_voice_avigation"){
				dataObj.unshift({"value_name":"无效按键","value_no":-1});
			}else if(ivrtype=="dnis_number_tb"){
				
			}else{
				dataObj.unshift({"value_name":"挂断","value_no":-2});
			}
		}
		var id,name,isSearch=false,_i=0;
		for(var i=0,j=dataObj.length;i<j;i++){
			id = dataObj[i].value_no;
			name = dataObj[i].value_name;
			treeData["root_"+dataObj[i].value_no] = {id:id,name:name};
			_i += 1;
		};
		_i>15?isSearch=true:"";
		Twin({
			Id:o+"_win",
			Title:"选择",
			TitleH:26,
			Refer:inputobj,
			Width:200,
			MaxH:280,
			//Coverobj:formFunction.tc.formObj,
			zIndex:100001,
			Mask:false,
			sysfun:function(tObj){
				tObj.style.overflow = "auto";
				tObj.style.background = "#EDF1F8";
				tObj.style.borderBottom = "1px solid #ccc";
				function selOK(selid,seltext){
					var sname = (selid!=""&&seltext!="请选择")?seltext:"";
					//alert(sname)
					T("#"+select_name)?T("#"+select_name).value = sname:"";
					T("#"+select_id)?T("#"+select_id).value = selid:"";
					TwinC(o+"_win",false,"","",2);
				};
				eval(""+o+"_tree = new tqTree({"+
					"treeId:\""+o+"_tree\","+
					"treeObj:tObj,"+
					"isSearch:"+isSearch+","+
					"dataType:0,"+
					//"dataUrl:dataurl,"+
					"localData:treeData,"+
					"nodeFnArgs:\"id,name\","+
					"nodeClick:function(id,name){"+
					"	selOK(id,name)"+
					"}"+
				"});");
				eval(o+"_tree").C();
			},
			CloseFn : function(){
				eval(""+o+"_tree = null");
			},
			buttons:[{dname:"x 清空",tit:"清空当前输入项的值",iconcls:"",onpress:function(){
				T("#"+select_name)?T("#"+select_name).value = "":"";
				T("#"+select_id)?T("#"+select_id).value = "":"";
				TwinC(o+"_win",false,"","",2);
			}},
			{dname:"取 消",iconcls:"",onpress:function(){
				TwinC(o+"_win",false,"","",2);
			}}]
		});
		setTimeout(
			function(){document.body.onclick=function(e){
			 e=window.event?window.event:e;
			 var e_tar=e.srcElement?e.srcElement:e.target;
			 if(e_tar.id.indexOf(o)!=-1)
			 {
				  return;
			 }
			 else
			 {
				TwinC(o+"_win",false,"","",2);
			 };
			document.body.onclick=null;
		}},500)
};
function getTreeNodeName(nodeid,uboxid){
	var _nodeList = IvrNodeList[uboxid].nodeList,ret="";
	for (var i=0,j=_nodeList.length;i<j;i++){
		if(nodeid == _nodeList[i].value_no){
			ret = _nodeList[i].value_name		
		}
	};
	return ret
};


function AddIvrNode(obj,uboxid,nodeId){
	var nodeid = T("#"+obj).value;
	Twin({
		Id:"addivrnode",
		Title:"新增呼叫节点",
		Content:"",
		Width:390,
		Height:155,
		sysfunI:obj,
		sysfun:function(obj,Obj){
			Tform({
				formname:"addivrnodef",
				formtitle:"",
				formObj:Obj,
				method:"POST",
				suburl:"ivrmanage.do?action=addIvrNode&ubox_id="+uboxid+"&selfId="+nodeId,
				formAttr:[{
					formitems:[{kindname:"",kinditemts:[
						{fieldcnname:"节点名称",fieldname:"node_name",fieldvalue:"",inputtype:"text",colSpan:2,remark:""},
						{fieldcnname:"节点类型",fieldname:"node_type_id",fieldvalue:"",inputtype:"select",colSpan:2,noList:typeList,remark:""}
					]}],
					rules:[
						{name:"node_name",type:"",requir:true,warn:"",okmsg:""}
					]
				}],
				Callback:function(f,rcd,ret,o){
					if(ret=="0"){
						T.loadTip(1,"保存失败,请重试!",2);
					}else{
						T.loadTip(1,"保存成功",2);
						var data = ret.split("||");
						var array = eval("("+data[0]+")");
						if(data[1]!="2"){//did
							dnisNodeList[uboxid].nodeList.push(array);
						}						
						IvrNodeList[uboxid].nodeList.push(array);
						//ivrTree.refresh();
						TwinC("addivrnode");
						//回填
					}
					
				}
			})
		}	
	})
};

function DelIvrNode(obj,ubox_id,ivrType,defaultnode){
	var objSelect = T("#"+obj);
	var nodeid = objSelect.value;
	if(nodeid==''){
		T.loadTip(1,"当前没有选中节点",2,"");
		return false;
	}
	if(defaultnode==nodeid){
		T.loadTip(1,"默认节点标识不允许删除!",2,"");
		return false;
	}
	if(nodeid=='-1'||nodeid=="-2"){
		T.loadTip(1,"系统节点,无法删除",2,"");
		return false;
	}
	Tconfirm({
		Title:"警告信息!",
		Ttype:"alert",
		Content:"确定要删除此节点吗?<br><b>删除后无法恢复！</b>",
		OKFn:function(){
			T.A.sendData("ivrmanage.do?action=del","POST","id="+nodeid+"&ubox_id="+ubox_id+"&ivrType="+ivrType,
				function(ret){
					if(ret=="1"){
						T.loadTip(1,"删除成功！",2,"");
						ivrTree.refresh();
						//删除select对应项
						for(var i in IvrNodeList[ubox_id].nodeList){
							if(IvrNodeList[ubox_id].nodeList[i].value_no==nodeid){
								IvrNodeList[ubox_id].nodeList.splice(i,1);
								break;
							}
						}
					}else{
						T.loadTip(1,"操作失败，请重试！",2,"");
					}
				},0,"");
		 				
		}
	});
};
function joinNode(dataObj){
	for(var i in dataObj){//按键标识
		if(dataObj[i].value_no==-1){
			dataObj.splice(i,1);
		}
	};
	for(var i in dataObj){//挂断
		if(dataObj[i].value_no==-2){
			dataObj.splice(i,1);
		}
	};
}
//IVR语音试听
function tryIvrVoice(obj,ubox_id){
	var url= "ivrmanage.do?action=getUrl&ubox_id="+ubox_id;
	var strURL = obj.previousSibling.value;
	strURL = T.A.sendData(url)+strURL;
	var selText = obj.previousSibling.options[obj.previousSibling.selectedIndex].text
	if(strURL==""){
		strURL = obj.previousSibling.getAttribute("defaulturl");
	};
	strURL = encodeURI(strURL);
	if(obj.className == "icon16 icon16stop hover1"){
		obj.className = "icon16 icon16play hover1";
		obj.title = "试听";
		obj.nextSibling.nextSibling.innerHTML = "";
		obj.nextSibling.innerHTML = "";
	}else{
		var xmlhttp = T.A.getXmlhttp();
	    xmlhttp.onreadystatechange = function(){
		    if(xmlhttp.readyState==4){  
			    if(xmlhttp.status==200 || xmlhttp.status==0){
			    	obj.nextSibling.innerHTML = "《"+selText+"》";
					obj.className = "icon16 icon16stop hover1";
					obj.title = "停止试听《"+selText+"》";
					obj.nextSibling.nextSibling.style.left = parseInt(obj.firstChild.offsetLeft) + "px";
					obj.nextSibling.nextSibling.style.top  = parseInt(obj.firstChild.offsetTop) + 20 + "px";
					obj.nextSibling.nextSibling.innerHTML = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab' width='80' height='20'><param name='movie' value='/vip/swf/singlemp3player/singlemp3player.swf?file="+strURL+"&amp;autoStart=true&amp;backColor=6BB15C&amp;frontColor=ffffff&amp;repeatPlay=no&amp;songVolume=100&amp;showDownload=true'><param name='wmode' value='transparent'><embed wmode='transparent' src=\"/vip/swf/singlemp3player/singlemp3player.swf?file="+strURL+"&amp;autoStart=true&amp;backColor=6BB15C&amp;frontColor=ffffff&amp;repeatPlay=no&amp;songVolume=100&amp;showDownload=true\" type='application/x-shockwave-flash' pluginspage='http://www.macromedia.com/go/getflashplayer' width='130' height='130'></object>";
					//T.loadTip(1,"开始播放",1,obj);
			    }else{
			    	T.loadTip(1,"《"+selText+"》对应语音文件不存在!",2,obj)
		       }  
			};
		}
	    xmlhttp.open("GET","proxy.do?per=tryIvrVoice&url="+strURL,true);   
	    xmlhttp.send(null); 
		//xmlhttp = null
	};
}
/*===选择Dnis===*/
function seldnisNumber(FNM,Fnm,cobj,checktype){//cobj:覆屏对象
	var cid = FNM +"_" + Fnm;
	var phoneSeat_id = T("#"+FNM+"_id");
	var ubox_id = T("#"+FNM+"_ubox_id");
	var extparams = "";
	if(phoneSeat_id){
		extparams = "&phoneseat_id="+phoneSeat_id.value;
	};
	if(ubox_id){
		extparams += "&ubox_id="+ubox_id.value;
	};
	var fielditems = [
						{"fieldcnname":"中继号编号","fieldname":"id","fieldvalue":"","hide":false,"inputtype":"text"},
						{"fieldcnname":"中继号码","fieldname":"dnis_number","fieldvalue":"","hide":false,"inputtype":"text",twidth:120},
						{"fhide":true,"fieldcnname":"节点信息","fieldname":"ivr_node_id","fieldvalue":"","hide":true,"inputtype":"text",twidth:120},
						{"fhide":true,"fieldcnname":"坐席信息","fieldname":"phoneseat_id","fieldvalue":"","hide":true,"inputtype":"text",twidth:120},
						{"fieldcnname":"坐席工号","fieldname":"seat_id","fieldvalue":"","hide":true,"inputtype":"text",twidth:120},
						{"fieldcnname":"节点名称","fieldname":"node_name","fieldvalue":"","hide":true,"inputtype":"text",twidth:120}
					]
    Twin({
		Coverobj:cobj,
        Id:"selDnis_w",
        Width:600,
        Height:400,
		Title:"选择中继",
		sysfunI:cid,
    	sysfun:function(cid,tObj){
            var _SclientT = new TQTable({
            	tableObj:tObj,
    			checktype:"checkbox",
            	tablename:"dnis_search_t",
				trclickfun:true,
				dataUrl:"ivrmanage.do?otherParams=2",
            	param:"action=showDis"+extparams,
				searchitem:[
					{name:"中继号",field:"dnis_number",type:"text",isdefault:true}
				],
    			buttons:[{
    				name: "ok",
    				dname: "确定选择",
    				icon: "ok.gif",
    				tit:"确定选择",
    				onpress:function(){
        				if(_SclientT.GS()){
        					var dnisNumber_value = _SclientT.GSByField("dnis_number");//Dnis_number
        					var regex = new RegExp(",", "g");
							var numS = dnisNumber_value.replace(regex,"_");
        					T("#"+cid).value =numS;
        					T("#"+cid+"_text").value =  _SclientT.GS(1);		//ID
        					T("#"+cid+"_node").value =_SclientT.GSByField("ivr_node_id");//ivr_node_id
    						TwinC("selDnis_w","","",cobj)
        				};
    				}
    			},{
    				name: "cancel",
    				dname: "取消",
    				icon: "cancel.gif",
    				tit:"取消选择",
    				onpress:function(){
    					TwinC("selDnis_w","","",cobj)
    				}
    			}
				],
			tableitems:fielditems
        	});
			_SclientT.C()
   		}
 	})
};
function clearDnisNumber(cid,cobj){//cobj:覆屏对象 
	T("#"+cid).value  = "";
	T("#"+cid+"_text").value =  "";
	T("#"+cid+"_node").value = "";
}

/*关联选择*/
function cactic_Select(value,target,action){
	if(value==-1)
		return;
	var childs =getChildById(action,value);
	var selectform = document.getElementById(target);
	selectform.options.length = 0;
	if(childs&&childs.length>0){
		for(var i=0;i<childs.length;i++){
			var varItem = new Option(childs[i].value_name, childs[i].value_no);      
			selectform.options.add(varItem);     
		}
	}
	//alert(document.getElementById(target).innerText);
}

function getChildById(action,id){
	var childs = eval(T.A.sendData("getdata.do?action="+action+"&id="+id));
	return childs;
}
function jslimit(){
    with(document.body) {
      oncontextmenu=function(){return false}
      ondragstart=function(){return false}
      onselectstart=function(){return false}
      onbeforecopy=function(){return false}
      onselect=function(){return false}
      oncopy=function(){return false}
	  onselectstart=function(){return false}
    }
}
