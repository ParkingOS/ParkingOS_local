/*Block Fucntion 2012-05-25 By Ft
*/
var _clientF = new TQForm({
	formname:"tableview_edit_f",
	fit:[true,true,true],
	dbuttons:true,
	suburl:"taleview.do?action=edit",
	method:"POST",
	Callback:
	function(f,rcd,ret,o){
		if(ret=="1"){
			T.loadTip(1,"保存成功！",2,"")
			_clientT.M()
		}else{
			T.loadTip(1,"保存失败，请重试！",2,o)
		}
	}
});	

/*==============================

  客户列表

==============================*/
var _clientT = new TQTable({
	tabletitle:"我的模板",
	tablename:"tableview_tables",
	dataUrl:"tableviewOrder.do",
	iscookcol:false,
	operatewidth:160,
	param:"action=quickquery&type=myclient",
	fit:[true,true,true],
	beginfun:function(){
		T.pageCover(1,layoutL_M,"#fff");
		T.pageCover(1,layoutL_B_B,"#fff")
	},
	loadfun:function(t,d){
		T.pageCover(0,layoutL_M);
		T.pageCover(0,layoutL_B_B);
		if(!d||d=="gotologin"||typeof(d)!="object"){return};
		if(parseInt(d.total)<1){return};
		T.each(_clientT.tc.tableitems,function(o,j){
			o.fieldvalue = _clientT.GD(d.rows[0].id,o.fieldname);
		});
	},isoperate:getAuthOperator(),
	tableitems:getCleintFields[0].kinditemts,
	buttons :eval(getAuthButtons())
});

function getAuthOperator() {
	var o = [];
	if(auth_edit) {
		o.push({name:"编辑",fun:function(id){
					T.each(_clientT.tc.tableitems,function(o,j){
						o.fieldvalue = _clientT.GD(id,o.fieldname)
					});
					Twin({Id:"bill_page_edit_w"+id,Title:"编辑",Width:750,sysfunI:id,sysfun:function(id,tObj){
							Tform({
								formname: "billpage_edit_f",
								formObj:tObj,
								recordid:"id",
								suburl:"tableviewOrder.do?action=edit&id="+id+"&tud_id="+tud_id,
								method:"POST",
								formAttr:[{
									formitems:[{kindname:"",kinditemts:_clientT.tc.tableitems}]
								}],
								buttons : [//工具
									{name: "cancel", dname: "取消", tit:"取消添加跟进备注",icon:"cancel.gif", onpress:function(){TwinC("bill_page_edit_w"+id);} }
								],
								Callback:
								function(f,rcd,ret,o){
									if(ret=="1"){
										T.loadTip(1,"保存成功！",2,"");
										TwinC("bill_page_edit_w"+id);
										_clientT.M("","",_clientT.tc.cpage);
										
									}else{
										T.loadTip(1,"保存失败，请重试！",2,o)
									}
								}
							});	
						}
					})
				}});
				
		o.push({name:"字段设置",fun:function(id){
					T.each(_clientT.tc.tableitems,function(o,j){
						o.fieldvalue = _clientT.GD(id,o.fieldname)
					});
					Twin({
						Id:"bill_colnum_edit_w"+id,
						Title:"字段设置",
						Width:T.gww()-20,
						Height:T.gwh()-20,
						Drag:false,
						CloseFn:function(){T("#alllayout").style.display = "block";},
						Content:"<iframe src=\"tableUiSet.do?action=preupdate&id="+id+"&tud_id="+tud_id+"&tudidname=&rand="+Math.random()+"\" style=\"width:100%;height:100%\" frameborder=\"0\"></iframe>"
					})
				}});
	}
	return o;
}

function toolbarItem(name){
	dataTypeSelect(name)
};
function dataTypeSelect(name){
	if(_clientT.GridData==null||_clientT.GridData=='err'){T.loadTip(1,"没有数据,不可进行此操作！",2,_clientT.tc.tableObj);return};
	var v = "0";
	var isedit = true;
	var sysfunI = "0";
	var h= 165;
	_clientT.GS(0)?sysfunI = _clientT.GS():(v="1",isedit=false);
	Twin({Id:"export_win",Title:"删除订单模板",Width:350,Height:h,sysfunI:sysfunI,sysfun:function(ids,tObj){
			Tform({
				formname: "opconfirm",
				formObj:tObj,
				dbuttons:false,
				suburl:"tableviewOrder.do?action=export",
				method:"POST",
				formAttr:[{
					formitems:[{kindname:"",kinditemts:getSelectItems(name,v,isedit,_clientT.GS(0),_clientT.GCI())}]
				}],
				buttons : [//工具
					{name: "daochu", dname: "确定要删除", tit:"点击确定删除数据",icon:"delete.png", onpress:function(n,fn,fobj,url){
							T.each(document.forms["opconfirm"]["seldatattype"],function(o,j){
								if(o.checked==true){
									infotype = o.value;
									return
								}
							});
							var info;
							if(infotype=="0"){
								info = "编号\"<b>"+_clientT.GS()+"</b>\"等模板";
							}else if(infotype=="1"){
								info = "<b>本页数据</b>"
							}else{
								info = "<b>全部数据(当前条件下)</b>"
							};
							Tconfirm({
								Title:function(){return "警告信息!"}(),
								Ttype:function(){return "alert"}(),
								Content:"确定要删除"+info+"吗?<br><b style=\"color:#c00\">"+function(){return "彻底删除后无法恢复!"}()+"</b>",
								OKFn:function(){
								T.A.sendData("tableviewOrder.do?action=delete","POST",Serializ(fn).substring(0,Serializ(fn).length-1),
									function(ret){
										if(ret=="1"){
											T.loadTip(1,"删除成功！",2,"");
											_clientT.B.CheckSave(_clientT).value = "";
											_clientT.B.Allbox(_clientT).checked = false;
											_clientT.M("","",_clientT.tc.cpage);
											TwinC("export_win");
										}else{
											T.loadTip(1,"操作失败，请重试！",2,tObj)
										}
									},0,tObj)
								},
								Coverobj:tObj.parentNode
							});
						}
					},
					{name: "cancel", dname: "取消", icon:"cancel.gif", onpress:function(){TwinC("export_win")} }
				]
			});	
		}
	})
}
function getSelectItems(name,v,isedit,ids,allids){
	return [{fieldname:"seldatattype",fieldcnname:"",inputtype:"radio",fieldvalue:[v],noList:[{value_name:"删除选中数据",value_no:"0"}],width:"210",height:"90",colSpan:2,edit:isedit},
		{fieldname:"seldatattype",fieldcnname:"",inputtype:"radio",fieldvalue:[v],noList:[{value_name:"删除本页数据",value_no:"1"}],width:"210",height:"90",colSpan:2},
		//{fieldname:"seldatattype",fieldcnname:"",inputtype:"radio",fieldvalue:[v],noList:[{value_name:"删除全部数据(当前条件下)",value_no:"2"}],width:"210",height:"90",colSpan:2},
		{fieldname:"selids",fieldcnname:"所选ID",inputtype:"text",fieldvalue:ids,width:"210",height:"",colSpan:2,hide:true},
		{fieldname:"pageids",fieldcnname:"当前页所有ID",inputtype:"text",fieldvalue:allids,width:"210",height:"",colSpan:2,hide:true}];
}
function clientT(obj){
	//CclientM();//创建主操作菜单
	CclientS();//创建搜索
	//CclientD();//创建数据分类
	CTableViewD();
};

function CclientS(){//创建搜索
	var auth_ct =(auth_add)?"":" disabled=true title='您没有权限'";
	var Cevent = (auth_add)?"CreateBillModel('tableviewOrder.do?action=create')": "return false;";
	var mHtml = "";
	mHtml += "<form name=\"ssform\" action=\"\">";
	mHtml += "<div style=\"width:100%;height:29px;float:left;margin-top:7px;*margin-top:14px;_margin-top:7px;overflow:hidden;\">";
	mHtml += "<div style=\"margin-left:5px;height:35px;padding:0px;overflow:hidden;width:100%;white-space:nowrap\" title=\"不选择此项时,将搜索您的权限范围内的所有工单,包含客户池与回收站。\" >";
	//mHtml += " 工单模板";
	mHtml += "<div onclick=\""+Cevent+"\" style='margin:5 auto;' onmouseover=\"T('#add_tableview_bt').className='button_lh';T('#add_tableview_btr').className='button_rh'\" onmouseout=\"T('#add_tableview_bt').className='button_l';T('#add_tableview_btr').className='button_r'\">";
	mHtml += "<div class=\"button_l\" style=\"width:88px;height:50px;margin-left:60px;\" title=\"新建订单模板\" id=\"add_tableview_bt\">";
	mHtml += "<span class=\"icon_add_c\" style=\"padding-left:10px\" "+auth_ct+">新建订单模板</span></div>";
	mHtml += "<div class=\"button_r\" id=\"add_tableview_btr\" style=\"width:5px\"></div></div>";
	mHtml += "</div>";
	mHtml +="</div>";
	mHtml += "</div>";
	mHtml += "<div style=\"width:100%;float:left;top:25px;margin-top:15px;\">";
	mHtml += "<div class=\"searchbt\" style=\"margin-left:5px;height:21px;border:1px solid #bbb;padding:0px;width:205px;overflow:hidden;\">";	
	mHtml += "<div id=\"ssearch_t\" class=\"ssearch_t\" onClick=\"show_select(\'ssearch_t\',\'ssearch_t_but\',\'t_items\',\'ssearch_type_value\',\'ssform\')\">模板名称<\/div>";
	mHtml += "<div id=\"ssearch_t_but\" class=\"ssearch_t_but\" onClick=\"show_select(\'ssearch_t\',\'ssearch_t_but\',\'t_items\',\'ssearch_type_value\',\'ssform\')\"><\/div>";
	mHtml += "<input type=\"hidden\" id=\"ssearch_type_value\" name=\"colname\" value=\"name\">";
	mHtml += "<input type=\"hidden\" class=\"txt\" name=\"action\" value=\"simplequery\">";
	mHtml += "<div><input type=\"text\" title=\"双击清空\" style=\"width:90px;height:21px;padding:0px;margin:0px;border:0px;float:left\" name=\"value\" autocomplete=\"off\" ondblclick=\"this.value=''\"></div>";
	mHtml += "<div onclick=\"SimpleSearch()\" class=\"searchbt\"  style=\"float:left;vertical-align:top;margin:0px;padding:0px;padding-left:5px;border-left:1px solid #bbb;height:24px;line-height:23px;cursor:pointer;_cursor:hand;\">搜索</div>";
	mHtml += "</div>";	
	mHtml += "</div>";
	mHtml += "<div class=\"ssearch_div\" id=\"ssearch_button_div\">";
	mHtml += "</div>";
	mHtml += "</form>";
	layoutL_M.innerHTML = mHtml;
	var ssHtml = "";
		ssHtml += "  <div class=\"clear\"><\/div>";
		ssHtml += "  <div id=\"t_items\" class=\"t_items\" style=\"display:none\">";
		ssHtml += "    <div class=\'t_items_out\' tid=\"name\">模板名称<\/div>";
		ssHtml += "  <\/div>";
	var mmmm = document.createElement("div");
	mmmm.className = "ssearch_select";
	mmmm.innerHTML = ssHtml;
	document.body.appendChild(mmmm)
};
function SimpleSearch(title){
	var title = title?title:"";
	if(document.forms["ssform"]["value"].value == ""){
		T.loadTip(1,"请填写查询内容",1,T("#ssearch_button_div"));
		document.forms["ssform"]["value"].focus();
		return
	};
	var range = "&tud_id="+tud_id;
	_clientT.C({
		extparam:Serializ("ssform")+range,
		tabletitle:title+">>搜索结果"
	})
};

function CTableViewD(){//
	layoutL_B_T.innerHTML = "<span style=\"clear:both;padding-left:10px;float:left;\">视图分类</span>";
	layoutL_B_B.innerHTML = ""
	var menucount = T.A.sendData("tableviewOrder.do?action=loadcount&tud_id="+tud_id+"&rand="+Math.random());
	var mc = menucount.split(',')
	var bLD  ={
			"root_10":{"id":1,"type":"root","name":"常用分类",cktp:false,ckd:false,"pid":0,icon:"root"}, 
			"10_55":{"id":55,"type":"trunk","name":"全部模板","pid":10,cktp:false,ckd:false,"mtype":"all","tud_id":tud_id,"tip":mc[0]}, 
			"55_552":{"id":552,"name":"编辑模板","pid":55,"mtype":"edit","tud_id":tud_id,"tip":mc[1]},
			"55_553":{"id":553,"name":"查询模板","pid":55,"mtype":"search","tud_id":tud_id,"tip":mc[2]}
		};
	
	tableViewTree = new tqTree({ treeId:"tableViewTree", dataType:0, 
		localData:bLD,
		treeObj:layoutL_B_B,
		focusExec:true, 
		nodeFnArgs:"id,name,mtype,tud_id,tip", 
		nodeClick:function(){
			var args = arguments;
			if(args[0]==1){return}
			_clientT.C({tableObj:layoutR_M,extparam:"action=query&type="+args[2]+"&tud_id="+args[3]+"&total="+args[4]})
		},
		
		isCheck:false, 
		loadfun:function(v){
			tableViewTree.focusNode(55);
			tableViewTree.expandLevel(3);
		}})
	tableViewTree.C();
};

function CreateBillModel(url){
	if(tud_id==''){
		T.loadTip(1,"请先创建订单表自定义字段（系统自定义->订单表自定义->订单表自定义字段）！",10,"");
		return;
	}
		
	Twin({Id:"Createbillmodel_win",Title:"新建模板",Width:"750",Height:"auto",sysfun:function(tObj){
			Tform({
				formname: "bill_add_f",
				formObj:tObj,
				recordid:"id",
				suburl:url,
				method:"POST",
				formAttr:[{
					formitems:function(){
						var a = getCleintFields;
						var items = a[0].kinditemts;
						for(var i=0;i<items.length;i++){
							items[i].fieldvalue='';
							if(items[i].fieldname=='create_time')
								items[i].fieldvalue=T.curt();
							else if(items[i].fieldname=='creator')
								items[i].fieldvalue=nickname;
							else if(items[i].fieldname=='tud_id')
								items[i].fieldvalue=tud_id;
						}
						return a;
					}()
				}],
				buttons : [//工具
					{name: "cancel", dname: "取消", tit:"取消",icon:"cancel.gif", onpress:function(){TwinC("Createbillmodel_win");} }
				],
				Callback:
				function(f,rcd,ret,o){
					if(ret=="1"||ret.indexOf('success')!=-1){
						T.loadTip(1,"新建成功！",2,"");
						TwinC("Createbillmodel_win");
						if(typeof(_clientT)==="object"){
							CTableViewD();
						}
					}else{
						T.loadTip(1,"新建失败，请重试！",2,o)
					}
				}
			});	
		}
	})

};
 function selectResModel(id){
 	var fielditems = getCleintFields;
    Twin({
		//Coverobj:cobj,
        Id:"sel_R_M_w",
        Width:800,
        Height:400,
        Top:100,
        Mask:true,
		Title:"选择模板",
		//sysfunI:cid,
    	sysfun:function(tObj){
            var _SclientT = new TQTable({
            	tableObj:tObj,
				//fieldorder:fielditems.searchSort[0].sort.toString(),
    			checktype:"radio",
            	tablename:"_R_M_search_t",
				trclickfun:true,
				dataUrl:"tableviewOrder.do",
            	param:"action=query4select&tud_id="+tud_id+"&id="+id+"&rand="+Math.random(),
    			buttons:[{
    				name: "ok",
    				dname: "确定选择",
    				icon: "ok.gif",
    				tit:"确定选择",
    				onpress:function(){
        				if(_SclientT.GS()){
        					//alert(_SclientT.GS(1));
    						var res =T.A.sendData("tableviewOrder.do?action=setresmodel&id="+id+"&resid="+_SclientT.GS(1)+"&rand="+Math.random());
    						if(res=='1'){
    							T.loadTip(1,"绑定成功！",2,"");
    							TwinC("sel_R_M_w");
    							_clientT.M();
    						}
        				};
    				
    				}
    			},{
    				name: "cancel",
    				dname: "取消",
    				icon: "cancel.gif",
    				tit:"取消选择",
    				onpress:function(){
    					TwinC("sel_R_M_w");
    				}
    			}
				],
			tableitems:fielditems[0].kinditemts
        	});
			_SclientT.C()
   		}
 	})
 }

