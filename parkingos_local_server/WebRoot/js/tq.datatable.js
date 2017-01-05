/*TQtable 2012-06-25
 version1.1:2012-07-07
 Latest:2012-08-08
 Latest:2012-10-26 增加动态添加删除行/表格编辑模式 BY FT
 Latest:2012-12-18 中间若干版本/增加编辑模式/增加多表头/根据查询模版重排顺序 BY FT
 默认页码与每页显示数参数传递名为page和rp，字段参数名应避免与其冲突
 20130930 修正N个兼容行问题 增加自动适应单元行操作菜单宽度
*/
var Ttable = function(o){new TQTable(o).C()};
TQTable = function(o){
/*表格属性*/
	this.tc = T.extend({
			Path:"css/images/form/",
			tableFunId:"",//表格对象,如:var tableFunId = new TQTable();
			tablename:"TQtable",//唯一标识
			tabletitle:false,//标题
			tableitems:false,//系统字段集合
			tableFields:false,//表头项
			addtionitem:false,//额外表头项y
			remainadditem:false,//保留额外表头项
			checktype:"checkbox",//选择类型:checkbox/radio
			toolespower:true,//是否显示工具栏
			headrows:false,//多表头
			dbuttons:[true,true],//默认工具
			buttons:[],//自定义工具
			searchitem:null,//自定义搜索
			allowpage:true,//是否显示分页
			pagetype:[5,10,20,30,50,100,200,300],//分页页数选择项
			dataUrl:"",//数据请求地址
			method:"POST",//请求方式GET or POST
			param:"1=1",//数据查询条件,method为POST时提交参数，默认不能为空1=1&2=2
			extparam:false,//重设数据查询条件,格式同param
			fit:[false],//是否随窗口大小改变自适应父对象大小
			autoH:false,//是否根据行数自动高度
			rpage:20,//默认每页显示数
			cpage:1,//当前页码
			isodbyserver:true,//点击表头时,是否从服务器排序
			sortTip:"点击排序",
			quikcsearch:false,
			isidentifier:true,//是否显示索引
			ischeck:true,//是否允许选择
			isDargcol:true,//是否允许拖动列
			isResize:false,//是否允许手动调整大小
			isoperate:false,//是否允许列操作
			operatewidth:false,//操作区域宽度，默认自动
			iscookcol:false,//是否允许列设置并cookies保存
			trclickfun:false,//是否开放单击行选中事件
			trfun:false,//tr自定义单击事件
			beginfun:false,
			fieldorder:null,//根据查询模版重排顺序
			loadfun:true,//表格加载完毕后的自定义事件
			checkfun:false,//自定义选择框事件
			editmode:false,//可编辑表格模式
			nodatatip:"按照您的指令查询过了，暂时没有数据。",
			dataorign:0,//数据来源.0从服务器,1自定义。默认是0,勿改
			hotdata:null,//{"page":1,"total":45,"rows": [{"id":"1","cell":["通话中","业务咨询","张三","15210208937","北京市移动","2012-09-23 08:54","1022||唐智","1"]},{"id":"2","cell":["接入中","售后服务","王五","15210208937","北京市移动","2012-09-23 08:54","1022||王金真","1"]},{"id":"3","cell":["等待中","业务咨询","李四","15210208937","北京市移动","2012-09-23 08:54","","1"]},{"id":"4","cell":["通话中","","张三","15210208937","北京市移动","2012-09-23 08:54","1022||唐智","0"]},{"id":"5","cell":["未接听","","刘三","15210208937","北京市移动","2012-09-23 08:54","1022||唐智","0"]}]}
			orderfield:false,//默认排序字段
			orderby:"desc",//默认排序方式
			countitem:false,//合计项
			toolsMode:1,//数据工具显示模式 0不固定(动态) 1固定 默认为1
			hoverin:false//toolsMode为0时,鼠标悬停操作元素所在单元格字段名
		},o);
};

TQTable.prototype = {
	/**********************************
	
	 一些基本属性(BaseAttr)
	 	 
	**********************************/
	exhH:0,
	contentWidth:0,//表格主体表头/内容宽
	B : {
		thisT:this,
		newP : function(p,t){//表格属性参数
			var l = t.tc;
			t.tc = null;
			t.tc = T.extend(l,p);
			l = null;
		},
		frozW : function(t){//固定列的宽度(选择框列和索引列)
			var fw = 0;
//			if(t.tc.isoperate&&(t.tc.toolsMode == 1||!t.tc.hoverin)){
//				var _w = T.iev<9?55:44;
//				(t.tc.operatewidth)?fw +=t.tc.operatewidth:fw +=_w*t.tc.isoperate.length;
//			};
			(t.tc.ischeck)?fw += 23:"";
			(t.tc.isidentifier)?fw += 32:"";
			return fw
		},
		extH : function(t,xH){//除去主体区的固定高度
			var extheight = 27;//表格列头高度
			(t.tc.tabletitle)? extheight += 29:"";
			(t.tc.searchitem)? extheight += 26:"";
			(t.tc.toolespower)? extheight += 36:"";
			(t.tc.allowpage)? extheight += 34:"";
			(t.tc.headrows)? extheight += 26:"";
			(t.tc.countitem)? extheight += 28:"";
			//!isNaN(parseInt(t.tc.tableObj.style.borderTopWidth))?extheight += 1:"";
			//!isNaN(parseInt(t.tc.tableObj.style.borderBottomWidth))?extheight += 1:"";
			extheight += t.exhH;
			return extheight
		},
		CheckSave:function(t){//全局选中的ID存贮对象
			return document.getElementById(t.tc.tablename+"_checklist")
		},
		Childboxs:function(t){//当前页所有行checkbox所在行对象/tr行对象
			return document.getElementById(t.tc.tablename+"_froze_body").tBodies[0].rows
		},
		Allbox:function(t){//当前页全选checbox对象
			return document.getElementById(t.tc.tablename+"_checkall")
			//return document.getElementById(t.tc.tablename+"_froze_header").tBodies[0].rows[0].cells[0].firstChild
		},
		headerObj:function(t){
			return document.getElementById(t.tc.tablename + "_header_div")	
		},
		tbodyObj:function(t){
			return document.getElementById(t.tc.tablename + "_body_div")	
		},
		tbodyObjR:function(t){
			return document.getElementById(t.tc.tablename + "_body_div_r")	
		},
		footerObj:function(t){
			return document.getElementById(t.tc.tablename + "_footer_div")	
		},
		countObj:function(t){
			return document.getElementById(t.tc.tablename + "_count_div")	
		},
		titleObj:function(t){
			return document.getElementById(t.tc.tablename + "_title_div")	
		},
		toolObj:function(t){
			return document.getElementById(t.tc.tablename + "_tooles_div")	
		},
		frozeObj:function(t){
			return document.getElementById(t.tc.tablename + "_froze_div")	
		},
		frozehObj:function(t){
			return document.getElementById(t.tc.tablename + "_froze_header")	
		},
		frozehdObj:function(t){
			return document.getElementById(t.tc.tablename + "_froze_header_div")	
		},
		tabledivObj:function(t){
			return document.getElementById(t.tc.tablename + "alldiv")	
		},
		tablechkSpan:function(t){
			if(t.tc.tabletitle){
				return document.getElementById(t.tc.tablename + "_checknum_span")
			}else{return false}
		}
	},
	/**********************************
	
	 页面缓存表格数据(GridData)
	 被修改过的数据(ModifyData)
	 原始表格数据(oGridData)
	 	 
	**********************************/
	GridData   : null,
	oGridData  : null,
	isOrigin   : true,
	ModifyData : {total:0,rows:[]},
	/*  {
			total:3,rows:{
				{"type","add","id":"2","cell":["2","80121632","北京2"]},
				{"type","mod","id":"3","cell":["3","80121632","北京2"]},
				{"type","del","id":"4","cell":["4","80121632","北京2"]}
			}
		}
	*/

	/**********************************
	
	 表格生成主函数(Createtable)
	 p:自定义参数，注意格式,默认为空
	 	 
	**********************************/
	C : function(p){
		if(this.tc.addtionitem){
			!this.tc.remainadditem?T.Aremove(this.tc.tableFields,this.tc.addtionitem):""
		};
		if(p){
			this.B.newP(p,this)
		};
		if(!this.tc.tableObj)return;
		this.GridData = null;
		this.oGridData = null;
		this.isOrigin = true;
		this.ModifyData = {total:0,rows:[]};
		
		if(T.gcok("tqgridmode")){
			this.tc.toolsMode = T.gcok("tqgridmode");
		};
		
		this.tc.tableObj.innerHTML="";
		var tableObj = this.tc.tableObj;//表格依附对象
		var t = this;
		var tc = this.tc;
		var TNM = tc.tablename;

		var tablediv = document.createElement("div");//表格容器
		tableObj.appendChild(tablediv);
		var tabletit = document.createElement("div");//创建表格标题
		var tablebt = document.createElement("div");//创建工具按钮
		var tableheader = document.createElement("div");//创建表格表头
		var tablesearch = document.createElement("div");//创建表格搜索
		var tablebody = document.createElement("div");//创建表格主体
		var tablebodyL = document.createElement("div");//创建表格主体左侧
		var tablebodyR = document.createElement("div");//创建表格主体右侧
		var tableCount = document.createElement("div");//创建合计结果列出
		var tablebottom = document.createElement("div");//创建表格底部
		var tableresize = document.createElement("div");//创建调整表格大小拖动
		var tablefrozehead = document.createElement("div");//固定列头
		var tablefrozediv = document.createElement("div");//固定列
		
		tablediv.className = "t-grid t-datagrid";
		//tablediv.id = TNM + "alldiv";
		tablediv.id = TNM;
		
		//创建表格开始
		if(!tc.tableitems){return};
		this.headOS();//设置表头顺序
		if(tc.addtionitem){
			//tc.tableFields.push(tc.addtionitem);
			tc.tableFields.unshift(tc.addtionitem);
		};
		//创建表格标题=======================================
		if(tc.tabletitle)
		{
			tabletit.id = TNM + "_title_div";
			tabletit.className = "tabletitle";
			tabletit.innerHTML = "";
			tablediv.appendChild(tabletit);
			this.Ti(null,tabletit);//填充标题
		};
		
		//创建表格搜索=======================================
		if(tc.searchitem)
		{
			tablesearch.id = TNM + "_search_div";
			tablesearch.className = "tablesearch";
			tablesearch.innerHTML = "";
			tablediv.appendChild(tablesearch);
			this.Sh(null,tablesearch);//填充搜索
		};
		
		
		//创建工具按钮=======================================
		if(tc.toolespower){
			tablebt.id = TNM + "_tooles_div";
			tablebt.className = "tablebutton";
			tablediv.appendChild(tablebt);
			this.To(null,tablebt);//填充表格其它相关工具栏
		};
		
		
		//创建固定表头=======================================
		//固定列表头
		tablefrozehead.id = TNM + "_froze_header_div"; 
		tablefrozehead.className = "t-grid-froze-headercell";
		//tablefrozehead.style.zIndex = T.iev&&T.iev<8?"1":"0";
		
		tablebodyL.id = TNM + "_grid-body-l";
		tablebodyL.className = "t-grid-body-l";
		tablebodyL.style.width = "auto";
		tablebodyL.appendChild(tablefrozehead);
		tablediv.appendChild(tablebodyL);
		this.Fh(null,tablefrozehead);//填充固定列表头



		//表格表头
		tableheader.id = TNM + "_header_div";
		tableheader.className = "t-grid-header";
		//var tableheaderinner = document.createElement("div");
		//tableheaderinner.id = TNM + "_header_innerdiv";
		//tableheaderinner.className = "t-grid-header-innerdiv";
		//tableheader.appendChild(tableheaderinner);
		tablebodyR.id =TNM + "_body_div_r";
		tablebodyR.className = "t-grid-body-r";
		//tablebodyR.setAttribute("class","t-grid-body-r");
		
		tablediv.appendChild(tablebodyR);
		tablebodyR.appendChild(tableheader);
		this.Th();
		
		//创建表格主体=======================================
		//固定列框架
		tablefrozediv.id = TNM + "_froze_div";
		tablefrozediv.className = "t-grid-froze-header";
		//tablefrozediv.style.cssFloat = "left"; 
		var tablefrozecol = [];
		tablefrozecol.push("<div style=\"float:left;margin:0px;padding:0px;position:relative;overflow:auto;\">");
		tablefrozecol.push("</div>");
		tablefrozediv.innerHTML = tablefrozecol.join("");
		//tablediv.appendChild(tablefrozediv);
		
		tablebodyL.appendChild(tablefrozediv);
		
		//表格数据框架
		//tablebody.style.cssFloat = "left";
		tablebody.className = "t-grid-body";
		tablebody.id = TNM + "_body_div";
		
		var tablebodyinner = document.createElement("div");
		tablebodyinner.id = TNM + "_body_innerdiv";
		//tablebodyinner.style.float =T.iev&&T.iev==7?"":"left";
		tablebodyinner.className = "t-grid-body-innerdiv";
		tablebody.appendChild(tablebodyinner);
		
		//var tablecontent = [];
		//tablecontent.push("<table cellSpacing=\"0\" cellPadding=\"0\" class=\"t-grid-table\"><tbody>");
		//tablecontent.push("</tbody></table>");
		//tablebody.innerHTML = tablecontent.join("");
		
		//tablediv.appendChild(tablebody);
		
		tablebodyR.appendChild(tablebody);
		//表头/固定列与主体一起滚动
		tablebody.onscroll = function(){
			t.B.headerObj(t).style.right = t.B.tbodyObj(t).scrollLeft + "px";
			t.B.frozeObj(t).firstChild.style.bottom = t.B.tbodyObj(t).scrollTop + "px";
		};
		
		//设置/监听主体区宽高
		var extheight = this.B.extH(this);
		var fObjW = tableObj.offsetWidth;
		var fObjH = tableObj.offsetHeight;
		var frozW = this.B.frozW(this);
		if(this.tc.isoperate&&(this.tc.toolsMode == 1||!this.tc.hoverin)){
			frozW += 205;
		};
		try{
			tableheader.style.width = this.contentWidth + "px";
			tablebodyinner.style.width = this.contentWidth + "px";
			tablebodyL.style.width = frozW + (T.iev&&T.iev<8?0:1) + "px";
			tablebody.style.width = tableObj.offsetWidth - frozW - 3 + "px";
			tablebodyR.style.width = tableObj.offsetWidth - frozW - 4 + "px";
			tablebody.style.height = tc.autoH?"auto":fObjH - extheight  + "px";
			tablefrozediv.style.height = tc.autoH?"auto":fObjH - extheight + "px"
		}catch(e){};
		this.bindFO();
		
		//合计结果列出
		if(this.tc.countitem){
			tableCount.id = TNM + "_count_div";
			tableCount.className = "t-grid-count";
			//tableCount.style.width = tableObj.offsetWidth - 1 + "px"
			tableCount.innerHTML = "<span class=\"counttitle\" title=\"当前条件下\">合计项目&nbsp;&nbsp;<font style=\"color:#c00\">0</font></span>"
			tablediv.appendChild(tableCount);
		};
		
		//表格底部=======================================
		if(tc.allowpage){
		tablebottom.id = TNM + "_footer_div";
		tablebottom.className = "t-grid-footer";
		//tablebottom.style.width = tableObj.offsetWidth - 1 + "px"
			var tablebottomc = [];
				tablebottomc.push("<TABLE style=\"border:0px;width:100%\" id=\""+TNM+"_footer\" class=\"t-pager\" cellSpacing=\"0\" cellPadding=\"0\">");
					tablebottomc.push("<TBODY>");
						tablebottomc.push("<TR>");
							tablebottomc.push("<TD style='width:100%'>");
								tablebottomc.push("<div class=\"t-pager-buttons\">");
									tablebottomc.push("<SPAN  id=\""+TNM+"_pbutton_first\" class=\"button24_a  bg_green_hover border_green fl\" style='width:40px;'>");
										tablebottomc.push("首页");
									tablebottomc.push("<\/SPAN>");
									tablebottomc.push("<SPAN  id=\""+TNM+"_pbutton_prev\" class=\"button24_a  bg_green_hover border_green fl\" style='width:40px;'>");
										tablebottomc.push("上一页");
									tablebottomc.push("<\/SPAN>");
									tablebottomc.push("<SPAN id=\""+TNM+"_pinput_next\" class=\"button24_a  bg_green_hover border_green fl\" style='width:40px;'>");
										tablebottomc.push("下一页");
									tablebottomc.push("<\/SPAN>");
									tablebottomc.push("<SPAN id=\""+TNM+"_pinput_last\" class=\"button24_a  bg_green_hover border_green fl\" style='width:40px;'>");
										tablebottomc.push("尾页");
									tablebottomc.push("<\/SPAN>");
									tablebottomc.push("<SPAN class=\"t-pager-index\">");
										tablebottomc.push("<INPUT id=\""+TNM+"_pinput_curr\" class=\"t-pager-num\" value=\"0\" type=\"text\" title=\"输入页码,按回车键跳转\">");
										tablebottomc.push("<SPAN id=\""+TNM+"_pinput_pages\" class=\"t-pager-pages\">\/0<\/SPAN>");
									tablebottomc.push("<\/SPAN>");
								tablebottomc.push("<\/div>");
							tablebottomc.push("<div style=\"float:right;padding:2px 5px 0 0\">");
							tablebottomc.push("总<span id=\""+TNM+"_pages_total\">0</span>条&nbsp;&nbsp;每页");
								tablebottomc.push("<select id=\""+TNM+"_pages_select\">");
								for(var p=0;p<tc.pagetype.length;p++){
									tablebottomc.push("<option value="+tc.pagetype[p]+"");
									tc.pagetype[p] == tc.rpage?tablebottomc.push(" selected=\"selected\""):"";
									tablebottomc.push(">"+tc.pagetype[p]+"<\/option>");
								};
								tablebottomc.push("<\/select>");
							tablebottomc.push("条");
							tablebottomc.push("<\/div>");
							tablebottomc.push("<\/TD>");
						tablebottomc.push("<\/TR>");
					tablebottomc.push("<\/TBODY>");
				tablebottomc.push("<\/TABLE>");
			tablebottom.innerHTML = tablebottomc.join("");
			tablediv.appendChild(tablebottom);
			tablebottomc = null
		};
		
		//改变表格大小(尚未实现)=======================================
		if(this.tc.isResize){
			tableresize.id = TNM + "_resize_div";
			tableresize.className = "t-grid-resizegrid";
			tableresize.innerHTML = "";
			tablediv.appendChild(tableresize)
		};
		
		//填充数据内容,框架生成完毕之后再填充数据=======================================
		this.M(null,tablebodyinner,0,true);

		//this.GridData = null
		//创建表格完毕,清除页面缓存GridData
		//销毁=======================================
		tablediv = null;
		tabletit = null;
		tablebt = null;
		tableheader = null;
		tablebody = null;
		tablebottom = null;
		tableresize = null;
		tablefrozehead = null;
		tablefrozediv = null;
		hdiv = null;
		
	},
	/**********************************
	
	 headOrderSet表头顺序设定
	 
	**********************************/
	headOS:function(){
		var tc = this.tc;
		if(tc.fieldorder){
			var fieldorder = tc.fieldorder.split(",");
			var colItems = tc.tableitems.concat();
			var newItems = [];
			for(var i=0;i<fieldorder.length;i++){
				for(var k=0;k<colItems.length;k++){
					if(fieldorder[i]==colItems[k].fieldname){
						newItems.push(colItems[k]);
						colItems.splice(k,1);
					}
				}
			};
			for(var j=0;j<colItems.length;j++){
				newItems.push(colItems[j]);
			};
			tc.tableFields = newItems;
			colItems = null
		}else{
			tc.tableFields = tc.tableitems;
		};
	},
	/**********************************
	
	 绑定resize
	 
	**********************************/
	bindFO:function(){
		var t = this;
		var BindAsEventListener = function(object, fun, args) {
			return function() {    
				return fun.apply(object,args||[]);    
			}  
		};
		if(this.tc.fit[0]==true){
			var tableFitObj = T.iev&&T.iev<9?this.tc.tableObj:window;
			//var tableFitObj = window;
			//tableFitObj.onresize = BindAsEventListener(this,t.FO);
			T.bind(window,"resize",function(){t.FO()});	
			//T.bind(window,"resize",ResizeLayout)
		};
	},
	/**********************************
	
	 解除绑定resize
	 
	**********************************/
	unbindFO:function(){
		var t = this;
		if(t.tc.fit[0]==true){
			var tableFitObj = T.iev&&T.iev<9?t.tc.tableObj:window;
			//var tableFitObj = window;
			tableFitObj.onresize = null;
		};
	},
	/**********************************
	
	 适合附加对象大小(FitOutOb)
	 
	**********************************/
	FO:function(t){
		var t = t||this;
		var tc = t.tc;
		var TNM = tc.tablename;
		var _time = T.iev&&T.iev<9?300:100;
		setTimeout(function(){
			if(!T("#"+TNM))return;
			T("#"+TNM + "_grid-body-l").style.width = "auto";//重新初始化宽度，必须
			
			var identifierNode = T("#"+TNM + "_identifier_header");
			if(identifierNode != null) {
				identifierNode.style.width = T.iev&&T.iev<8? 32 + "px":33 + "px";//重新初始化宽度，必须
				identifierNode.style.width = 32 + "px";
			}
			
			var tableObj = tc.tableObj;
			var frozW = T.gow(T("#"+TNM + "_froze_div"));//t.B.frozW(t);
			var operateW = frozW - t.B.frozW(t) - 3;
			var extheight = t.B.extH(t);
			T("#"+TNM + "_grid-body-l").style.width = frozW + (T.iev&&T.iev<8?0:1) + "px";
			T("#"+TNM + "_operate")&&operateW>0?T("#"+TNM + "_operate").style.width = operateW + "px":"";
		
			if(tc.fit[1]||"undefined" == typeof(tc.fit[1])){
				try{
					if(tableObj.offsetWidth!==0){
						t.B.tbodyObj(t).style.width = tableObj.offsetWidth - frozW - 3 + "px"
						t.B.tbodyObjR(t).style.width = tableObj.offsetWidth - frozW - 4 + "px"
					}else{ 
					
						t.B.tbodyObj(t).style.width = tableObj.parentNode.offsetWidth - frozW - 3 + "px";
						t.B.tbodyObjR(t).style.width = tableObj.parentNode.offsetWidth - frozW - 4 + "px";
					}
				}catch(e){};
			};
			if(tc.fit[2]||"undefined" == typeof(tc.fit[2])){
				try{
					if(tableObj.offsetHeight!==0){
						t.B.tbodyObj(t).style.height = tableObj.offsetHeight - extheight + "px";
						t.B.frozeObj(t).style.height = tableObj.offsetHeight - extheight + "px";
					}else{
						t.B.tbodyObj(t).style.height = tableObj.parentNode.offsetHeight - extheight + "px";
						t.B.frozeObj(t).style.height = tableObj.parentNode.offsetHeight - extheight + "px";
					}
				}catch(e){};
			}else{
				try{
				t.B.tbodyObj(t).style.height = "auto";
				t.B.frozeObj(t).style.height = "auto";
				}catch(e){};
			};
		},_time)
		
	},
	/**********************************
	
	 表格标题(Title)
	 
	**********************************/
	Ti:function(p,t){
		if(p){
			this.B.newP(p,this)
		};
		var Tit = t||document.getElementById(this.tc.tablename + "_title_div");
		Tit.innerHTML = "<span class=\"t_grid_tabletitle\" >"+this.tc.tabletitle+"</span><span id=\""+this.tc.tablename + "_checknum_span\" class=\"t_grid_checknum\" ></span>";
		
	},
	/**********************************
	
	 表格搜索(Search)
	 
	**********************************/
	Sh:function(p,t){
		if(p){
			this.B.newP(p,this)
		};
		var thisT = this;
		var Srh = t||document.getElementById(this.tc.tablename + "_search_div");
		var searchname = this.tc.tabletitle || "当前列表";
			
		var spon = document.createElement("span");
		var sptw = document.createElement("span");
		var fr = document.createElement("form");
		var divon = document.createElement("div");
		var divtw = document.createElement("div");
		var divth = document.createElement("div");
		var divfo = document.createElement("div");
		
//		spon.innerHTML = "<span style=\"float:left;padding-left:5px;\">在<"+searchname+">中筛选:</span>";
		sptw.className = "grid_searchblock";
		
		fr.setAttribute("name",""+thisT.tc.tablename+"_filter_form");
		fr.id = thisT.tc.tablename+"_filter_form";
		fr.setAttribute("onkeydown","if(event.keyCode==13){return false;}");
		fr.action = "";
		
		divon.id = ""+thisT.tc.tablename+"_filter_t";
		divon.className = "grid_ssearch_t";
		divon.onclick = function(){thisT.Sselect(""+thisT.tc.tablename+"_filter_t",""+thisT.tc.tablename+"_filter_t_but",""+thisT.tc.tablename+"_filter_items",""+thisT.tc.tablename+"_filter_type_value",""+thisT.tc.tablename+"_filter_form")}
			
		divtw.id = ""+thisT.tc.tablename+"_filter_t_but";
		divtw.className = "grid_ssearch_t_but";
		divtw.onclick = function(){thisT.Sselect(""+thisT.tc.tablename+"_filter_t",""+thisT.tc.tablename+"_filter_t_but",""+thisT.tc.tablename+"_filter_items",""+thisT.tc.tablename+"_filter_type_value",""+thisT.tc.tablename+"_filter_form")}
		
		divfo.innerHTML = "查询";
		divfo.onclick = function(){
			var strings = Serializ(thisT.tc.tablename+"_filter_form");
			if(strings.indexOf("value=&")!=-1){
				thisT.M({extparam:false});
				return
			};
			thisT.M({
				extparam:Serializ(thisT.tc.tablename+"_filter_form")
			})
		};
		divfo.className = "grid_searchbt";
		
		sptw.appendChild(fr);
		fr.appendChild(divon);
		fr.appendChild(divtw);
		fr.appendChild(divth);
		fr.appendChild(divfo);

		Srh.appendChild(spon);
		Srh.appendChild(sptw);
			
		
		var ssHtml = "";
		var divthHtml = "<input type=\"hidden\" class=\"txt\" name=\"action\" value=\"simplequery\"><input type=\"text\" style=\"width:90px;height:21px;padding:0px;margin:0px;border:0px;float:left\" name=\"value\" autocomplete=\"off\">";
		ssHtml += "  <div class=\"clear\"><\/div>";
		ssHtml += "  <div id=\""+thisT.tc.tablename+"_filter_items\" class=\"t_items\" style=\"display:none\">";
		var sI = thisT.tc.searchitem;
		for(var m=0;m<sI.length;m++){
			if(sI[m].isdefault == true){
				divon.innerHTML = sI[m].name;
				divthHtml += "<input type=\"hidden\" id=\""+thisT.tc.tablename+"_filter_type_value\" name=\"colname\" value=\""+sI[m].field+"\">";
			};
			ssHtml += "<div class=\'t_items_out\' tid=\""+sI[m].field+"\">"+sI[m].name+"<\/div>";
		};
		divth.innerHTML = divthHtml;
		var mmmm = document.createElement("div");
		mmmm.className = "grid_ssearch_select";
		mmmm.style.display = "none";
		mmmm.innerHTML = ssHtml;
		document.body.appendChild(mmmm)
		//extparam
	},
	/**********************************
	
	 显示搜索项(ShowSelect)
	 
	**********************************/
	Sselect : function(input, btn, option, value,formid) {
		inputobj = document.getElementById(input);
		btnobj = document.getElementById(btn);
		optionobj = document.getElementById(option);
		valueobj = document.getElementById(value);
		optionobj.parentNode.style.display = "block";
		optionobj.style.display = optionobj.style.display == "" ? "none": "";
		optionobj.style.left = T.gpos(inputobj).left - 1 +"px";
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
				document.forms[formid]["value"].focus();
				optionobj.blur();
			}
		}
},
	/**********************************
	
	 表格工具栏(Tooles)
	 
	**********************************/
	To:function(p,t){
		if(p){
			this.B.newP(p,this)
		};
		var thisT = this;
		var Too = t||document.getElementById(this.tc.tablename + "_tooles_div");
		Too.innerHTML = "";
		if(this.tc.quikcsearch){
			var a = document.createElement("span");
			a.style.marginLeft = "5px";
			a.style.marginTop = "2px";
			a.style.float = "left";
			a.innerHTML =this.tc.quikcsearch;
			Too.appendChild(a);
			a = null
		}else if(this.tc.buttons){
			for(var n=0;n<this.tc.buttons.length;n++)
			{
				var tbtn = this.tc.buttons[n];
				if(tbtn.rule){
					if(!tbtn.rule()){continue}
				};
				var a = document.createElement("span");
				a.id = thisT.tc.tablename+"_bt_"+this.tc.buttons[n].name;
				a.className = this.tc.buttons[n].cls?this.tc.buttons[n].cls:"button24_a bg_gray_hover border_gray fl";
				a.style.marginLeft = "5px";
				a.style.marginTop = "2px";
				var bhtml = "";
				bhtml = "<span";
				bhtml += this.tc.buttons[n].tit?" title=\""+this.tc.buttons[n].tit+"\"":" title=\""+this.tc.buttons[n].dname+"\"";
				bhtml += ">";
				//<img src=\""+this.tc.Path+""+this.tc.buttons[n].icon+"\">
				this.tc.buttons[n].iconcls?bhtml += "<span class=\""+this.tc.buttons[n].iconcls+"\"><\/span>":"";
				bhtml += ""+this.tc.buttons[n].dname+"<\/span>";
				a.innerHTML = bhtml;
				a.name = tbtn.name;
				a.onpress = tbtn.onpress;
				a.onclick = (function(){
					this.onpress(this.name,thisT.tc.tablename);
				});
				Too.appendChild(a);
				a = null
			};
		};
		if(this.tc.dbuttons){//默认工具栏：刷新、列设置
			if(this.tc.iscookcol&&this.tc.dbuttons[0]){
				var a = document.createElement("span");
				a.id = this.tc.tablename +"_sub";
				a.style.marginTop = "2px";
				a.className = "button24_a border_blank hover1 fr";
				a.innerHTML = "<span title=\"设置表格列是否显示\" class=\"icon16 icon16table1 fl\"><\/span>列设置";
				a.onclick = function(){thisT.VH(thisT.tc.tablename)};
				Too.appendChild(a);
				a = null
			};
			if(this.tc.dbuttons[1]){
				var b = document.createElement("span");
				b.style.marginTop = "2px";
				b.className = "button24_a border_blank hover1 fr";
				b.innerHTML = "<span title=\"重新载入当前条&#10件下表格数据\" class=\"icon16 icon16fresh fl\"><\/span>刷新";
				if(thisT.tc.ischeck){
					b.onclick = function(){thisT.B.Allbox(thisT).checked = false;thisT.B.CheckSave(thisT).value = "";thisT.M({cpage:1})}
				}else{
					b.onclick = function(){thisT.M({cpage:1});try{thisT.B.CheckSave(thisT).value = "";}catch(e){}}
				};
				Too.appendChild(b);
				b = null
			}
		};
		if(this.tc.isoperate&&this.tc.hoverin){
			var _mode = this.tc.toolsMode;
			var _class = _mode==0?"icon16 icon16uncheck fl":"icon16 icon16check fl";
			var c = document.createElement("span");
			c.style.marginTop = "2px";
			c.title = "是否固定数据操作列\n不固定则动态显示";
			c.className = "button24_a border_blank hover1 fr";
			c.innerHTML = "<span class=\""+_class+"\"><\/span>固定";
			c.onclick = function(){
				thisT.tc.toolsMode == 1?thisT.tc.toolsMode = 0:thisT.tc.toolsMode = 1;
				T.scok("tqgridmode",thisT.tc.toolsMode)
				thisT.C();
			};
			Too.appendChild(c);
			c = null
		}
	},
	/**********************************
	
	 固定列表头(FrozeHead)
	 
	**********************************/
	Fh:function(p,t){
		if(p){
			this.B.newP(p,this)
		};
		var checktype = this.tc.checktype;
		var TNM = this.tc.tablename;
		var frozH = this.tc.headrows?"height:54px;":"";
		var Frh = t||document.getElementById(this.tc.tablename + "_froze_header_div");
		var tablefrozeheadt = [];
		tablefrozeheadt.push("<table id=\""+TNM+"_froze_header\" cellSpacing=\"0\" cellPadding=\"0\" class=\"t-grid-table\" style=\"width:auto;display:table;"+frozH+"\"><tbody><tr>");
		if(this.tc.ischeck){
			tablefrozeheadt.push("<td class=\"t-grid-headercell t-grid-cell-chx\" id=\""+TNM+"_check\" style=\"width:22px\">");
			checktype == "checkbox"?tablefrozeheadt.push("<input title=\"全选当前页所有记录\" class=\"chx\" id=\""+TNM+"_checkall\" type=\""+checktype+"\">"):tablefrozeheadt.push("<input class=\"chx\" id=\""+TNM+"_checkall\" type=\""+checktype+"\" disabled>");
			tablefrozeheadt.push("<input type=\"hidden\" value=\"\" id=\""+TNM+"_checklist\">");
			tablefrozeheadt.push("</td>")
		};
		if(this.tc.isoperate&&(this.tc.toolsMode == 1||!this.tc.hoverin)){
			tablefrozeheadt.push("<td class=\"t-grid-headercell\" style=\"width:200px\" id=\""+TNM+"_operate\"><div class=\"tddiv\">操作</div></td>");
		};
		tablefrozeheadt.push("</tr></tbody></table>");
		
		if(this.tc.isidentifier){
			tablefrozeheadt.push("<table id=\""+TNM+"_identifier_header\" cellSpacing=\"0\" cellPadding=\"0\" class=\"t-grid-table\" style=\"width:auto;display:table;"+frozH+"\"><tbody><tr>");
			tablefrozeheadt.push("<td class=\"t-grid-identifier\" style=\"width:32px;\"><div class=\"tddiv\" title=\"当前页记录快捷索引\">索引</div></td>");
			tablefrozeheadt.push("</tr></tbody></table>");
		};
		if(this.tc.ischeck||(this.tc.isoperate&&(this.tc.toolsMode == 1||!this.tc.hoverin))||this.tc.isidentifier){Frh.innerHTML = tablefrozeheadt.join("")};
		
	},
	/**********************************
	
	 数据表格表头(TableHead)
	 
	**********************************/
	Th:function(p,t){
	if(p){
		this.B.newP(p,this)
	};
	this.contentWidth = 0;
	if(this.tc.headrows){this.Ths(p,t);return};
	var TNM = this.tc.tablename;
	var items = this.tc.tableFields;
	var Thead = t||document.getElementById(this.tc.tablename + "_header_div");
	if(!Thead)return;
	var TableMain = TNM+"_body_div";
	Thead.innerHTML="";
	
	var thisT = this;
	var tb = document.createElement("table");
	var tbbody = document.createElement("tbody");
	var tbtr = document.createElement("tr");
	tb.cellPadding = "0";
	tb.cellSpacing = "0";
	tb.className = "t-grid-table";
	T.iev&&T.iev<8?tb.style.display = "":"table";//IE67不支持,但是忘了是哪个浏览器需要此属性支持
	var ckey = TNM;
	var cvalues = "";
	if(this.tc.iscookcol){
		var gcok = T.gcok(ckey);
		if(gcok==null||gcok==""){var isscok = 1}else{var isscok = 0;};
	};

	for(var i=0;i<items.length;i++)
	{
		var tbtd = document.createElement("td");
		var m = parseInt(i);
		var Fenm = items[i].fieldname;
		var Fcnm = items[i].fieldcnname;
		var hd = items[i].shide||items[i].fhide;
		var fhide = items[i].fhide;
		var dataT = items[i].inputtype;
		
		T.dcok(TNM+ "_"+Fenm);//清除原来的cookies，以后再删掉这一行代码
		
		var iW ;
		items[i].twidth !=""&&items[i].twidth?iW = items[i].twidth:iW = 130;
		if(this.tc.iscookcol){
		//判断cookies
			if(isscok == 0){
				for(var z=0;z<gcok.split("||").length;z++){
					if(gcok.split("||")[z].split(",")[0]== Fenm){
						gcok.split("||")[z].split(",")[1]=="1"?hd = false:hd = true;
						gcok.split("||")[z].split(",")[2]!=""?iW = gcok.split("||")[z].split(",")[2]:"";
						break
					}
				}
			};
			if(isscok == 1){
				var val = [];
				if(items[i].shide){
					if(!items[i].fhide){
						val = Fenm +",0,"+iW+"||"
					}
				}else{
					if(!items[i].fhide){
						val = Fenm +",1,"+iW+"||"
					}
				};
				cvalues+=val;
			};
		};
		if(!fhide){this.contentWidth += parseInt(iW)};
		tbtd.id = TNM+"_"+Fenm;
		tbtd.headid = m;
		tbtd.setAttribute("headid",m);
		items[i].noquery?tbtd.setAttribute("noquery","true"):"";
		tbtd.process = items[i].process;
		tbtd.className = "t-grid-headercell";
		tbtd.style.width = iW+"px";
		tbtd.style.display = fhide?"none":"";
		var outdiv = document.createElement("div");
		outdiv.style.overflow = "hidden";
		outdiv.style.position = "relative";
		outdiv.style.width = "100%";
		outdiv.style.cssFloat = "left";
		var indiv = document.createElement("div");
		indiv.cid = m;
		indiv.setAttribute("cid",m)
		indiv.style.position = "relative";
		if(items[i].issort=="undefined"||items[i].issort != false){
			indiv.className = "tddiv sort";
			indiv.title = thisT.tc.sortTip;
			indiv.innerHTML = "<span class=\"o\">&nbsp;</span>"+Fcnm+"";
			indiv.dataT = dataT;
			indiv.fid = Fenm;
			indiv.setAttribute("fid",Fenm);
			indiv.onclick = function(){thisT.So(TableMain,0,this.cid,this.dataT,this)};
		}else{
			indiv.className = "tddiv";
			indiv.innerHTML = ""+Fcnm+"";
		};
		var dragdiv = document.createElement("div");
		dragdiv.id = m;
		dragdiv.className = "t-grid-splitter";
		dragdiv.onmouseover  = function(){thisT.Rc(TNM,this)};
		
		outdiv.appendChild(indiv);
		thisT.tc.isDargcol?outdiv.appendChild(dragdiv):"";
		tbtd.appendChild(outdiv);
		tbtr.appendChild(tbtd);
		
		tbtd = null;
		outdiv = null;
		indiv = null;
		dragdiv = null
	};

	this.tc.iscookcol&&isscok == 1?T.scok(ckey,cvalues.substring(0,cvalues.length-2)):"";//写cookie
	tbbody.appendChild(tbtr);
	tb.appendChild(tbbody);
	Thead.appendChild(tb);
	
	tbtr = null;
	tbbody = null;
	tb = null
	},
	/**********************************
	
	 多表头数据表格表头(TableHeads)
	 
	**********************************/
	_M:0,
	Ths:function(p,t){
	if(p){
		this.B.newP(p,this)
	};
	
	this._M = 0;
	this.contentWidth = 0;
	var TNM = this.tc.tablename;
	var tableFields = this.tc.tableFields;
	var Thead = t||document.getElementById(this.tc.tablename + "_header_div");
	
	var TableMain = TNM+"_body_div";
	Thead.innerHTML="";
	
	var thisT = this;
	var tb = document.createElement("table");
	var tbbody = document.createElement("tbody");
	var tbtr = document.createElement("tr");
	var _tbtr = document.createElement("tr");
	tb.cellPadding = "0";
	tb.cellSpacing = "0";
	tb.className = "t-grid-table";
	T.iev>7||!T.iev?tb.style.display = "table":"";//IE67不支持,但是忘了是哪个浏览器需要此属性支持
	var ckey = TNM;
	var cvalues = "";
	if(this.tc.iscookcol){
		var gcok = T.gcok(ckey);
		if(gcok==null||gcok==""){var isscok = 1}else{var isscok = 0;};
	};
	var _nohides = false;
	for(var l=0;l<tableFields.length;l++){
		var items = tableFields[l].kinditemts;
		if(!items){alert("表格类型有可能设置错误");break;}
 		var itemname = tableFields[l].kindname;
		if(itemname==""){
			var _m = this._M;
			for(var i=0;i<items.length;i++)
			{
				this._M+=1;
				var tbtd = document.createElement("td");
				var m = parseInt(i)+_m;
				var Fenm = items[i].fieldname;
				var Fcnm = items[i].fieldcnname;
				var hd = items[i].shide||items[i].fhide;
				var dataT = items[i].inputtype;
				T.dcok(TNM+ "_"+Fenm);//清除原来的cookies，以后再删掉这一行代码

				var iW ;
				items[i].twidth !=""&&items[i].twidth?iW = items[i].twidth:iW = 130;
				
				if(this.tc.iscookcol){
				//判断cookies
					if(isscok == 0){
						for(var z=0;z<gcok.split("||").length;z++){
							if(gcok.split("||")[z].split(",")[0]== Fenm){
								gcok.split("||")[z].split(",")[1]=="1"?hd = false:hd = true;
								gcok.split("||")[z].split(",")[2]!=""?iW = gcok.split("||")[z].split(",")[2]:"";
								break
							}
						}
					};
					if(isscok == 1){
						var val = [];
						if(items[i].shide){
							if(!items[i].fhide){
								val = Fenm +",0,"+iW+"||"
							}
						}else{
							if(!items[i].fhide){
								val = Fenm +",1,"+iW+"||"
							}
						};
						cvalues+=val;
					};
				};
				if(!hd){this.contentWidth += parseInt(iW)};
				tbtd.id = TNM+"_"+Fenm;
				tbtd.headid = m;
				tbtd.setAttribute("headid",m);
				items[i].noquery?tbtd.setAttribute("noquery","true"):"";
				tbtd.rowSpan = 2;
				tbtd.setAttribute("rowSpan",2)
				tbtd.process = items[i].process;
				tbtd.className = "t-grid-headercell";
				tbtd.style.width = iW+"px";
				tbtd.style.display = hd?"none":"";
				var outdiv = document.createElement("div");
				outdiv.style.overflow = "hidden";
				outdiv.style.position = "relative";
				outdiv.style.width = "100%";
				outdiv.style.cssFloat = "left";
				var indiv = document.createElement("div");
				indiv.cid = m;
				indiv.setAttribute("cid",m)
				indiv.style.position = "relative";
				
				if(items[i].issort=="undefined"||items[i].issort != false){
					indiv.className = "tddiv sort";
					indiv.title = thisT.tc.sortTip;
					indiv.innerHTML = "<span class=\"o\">&nbsp;</span>"+Fcnm+"";
					indiv.fid = Fenm;
					indiv.setAttribute("fid",Fenm);
					indiv.dataT = dataT;
					indiv.onclick = function(){thisT.So(TableMain,0,this.cid,this.dataT,this)};
				}else{
					indiv.className = "tddiv";
					indiv.innerHTML = ""+Fcnm+"";
				};
				var dragdiv = document.createElement("div");
				dragdiv.id = m;
				dragdiv.className = "t-grid-splitter";
				dragdiv.onmouseover  = function(){thisT.Rc(TNM,this)};
				
				outdiv.appendChild(indiv);
				thisT.tc.isDargcol?outdiv.appendChild(dragdiv):"";
				tbtd.appendChild(outdiv);
				tbtr.appendChild(tbtd);

				tbtd = null;
				outdiv = null;
				indiv = null;
				dragdiv = null
			}
		}else{
			var _tbtd = document.createElement("td");
			_tbtd.colSpan = items.length;
			_tbtd.className = "t-grid-headercell";
			_tbtd.innerHTML = "<div class=\"tddiv\" style=\"text-align:center;float:none;\">"+itemname+"</div>";
			_tbtd.setAttribute("colSpan",items.length);
			
			var _m = this._M;
			var _nohide = 0;
			for(var i=0;i<items.length;i++)
			{
				this._M+=1;
				var tbtd = document.createElement("td");
				var m = parseInt(i)+ _m;
				var Fenm = items[i].fieldname;
				var Fcnm = items[i].fieldcnname;
				var hd = items[i].shide||items[i].fhide;
				var dataT = items[i].inputtype;
		
				T.dcok(TNM+ "_"+Fenm);//清除原来的cookies，以后再删掉这一行代码
				
				var iW ;
				items[i].twidth&&items[i].twidth !=""?iW = items[i].twidth:iW = 130;
				
				if(this.tc.iscookcol){
				//判断cookies
					if(isscok == 0){
						for(var z=0;z<gcok.split("||").length;z++){
							if(gcok.split("||")[z].split(",")[0]== Fenm){
								gcok.split("||")[z].split(",")[1]=="1"?hd = false:hd = true;
								gcok.split("||")[z].split(",")[2]!=""?iW = gcok.split("||")[z].split(",")[2]:"";
								break
							}
						}
					};
					if(isscok == 1){
						var val = [];
						if(items[i].shide){
							if(!items[i].fhide){
								val = Fenm +",0,"+iW+"||"
							}
						}else{
							if(!items[i].fhide){
								val = Fenm +",1,"+iW+"||"
							}
						};
						cvalues+=val;
					};
				};
				
				if(!hd){this.contentWidth += parseInt(iW);_nohide+=1};
				tbtd.id = TNM+"_"+Fenm;
				tbtd.headid = m;
				tbtd.setAttribute("headid",m);
				items[i].noquery?tbtd.setAttribute("noquery","true"):"";
				tbtd.process = items[i].process;
				tbtd.className = "t-grid-headercell";
				tbtd.style.width = iW+"px";
				tbtd.style.display = hd?"none":"";
				var outdiv = document.createElement("div");
				outdiv.style.overflow = "hidden";
				outdiv.style.position = "relative";
				outdiv.style.width = "100%";
				outdiv.style.cssFloat = "left";
				var indiv = document.createElement("div");
				indiv.cid = m;
				indiv.setAttribute("cid",m)
				indiv.style.position = "relative";
				if(items[i].issort=="undefined"||items[i].issort != false){
					indiv.className = "tddiv sort";
					indiv.title = thisT.tc.sortTip;
					indiv.innerHTML = "<span class=\"o\">&nbsp;</span>"+Fcnm+"";
					indiv.fid = Fenm;
					indiv.setAttribute("fid",Fenm);
					indiv.dataT = dataT;
					indiv.onclick = function(){thisT.So(TableMain,0,this.cid,this.dataT,this)};
				}else{
					indiv.className = "tddiv";
					indiv.innerHTML = ""+Fcnm+"";
				};
				var dragdiv = document.createElement("div");
				dragdiv.id = m;
				dragdiv.className = "t-grid-splitter";
				dragdiv.onmouseover = function(){thisT.Rc(TNM,this)};
				
				outdiv.appendChild(indiv);
				thisT.tc.isDargcol?outdiv.appendChild(dragdiv):"";
				tbtd.appendChild(outdiv);
				_tbtr.appendChild(tbtd);
				tbtr.appendChild(_tbtd);
				
				tbtd = null;
				outdiv = null;
				indiv = null;
				dragdiv = null
			};
			//alert( _nohide)
			//_nohide!=0?_tbtd.colSpan = _nohide:"";
			//_tbtd.style.display =_nohide==0?"none":"";
			if(_nohide==0){
				_tbtd.style.display = "none";
				//this.B.frozehObj(this).style.height = "54px";
			}else{
				_nohides = true;
				_tbtd.colSpan = _nohide;
				_tbtd.style.display = "";
				//this.B.frozehObj(this).style.height = "";
			};
			//_nohide = 0;
		};
	};
	if(_nohides){
		this.exhH = 0;
		this.B.frozehObj(this)?this.B.frozehObj(this).style.height = "54px":"";
	}else{
		this.exhH = -27;
		this.B.frozehObj(this)?this.B.frozehObj(this).style.height = "":"";
	};
	this.tc.iscookcol&&isscok == 1?T.scok(ckey,cvalues.substring(0,cvalues.length-2)):"";//写cookie
	tbbody.appendChild(tbtr);
	tbbody.appendChild(_tbtr);
	
	tb.appendChild(tbbody);
	Thead.appendChild(tb);
	tbtr = null;
	tbbody = null;
	tb = null
	
	},
	/**********************************
	
	 填充表格内容(MakeContent)
	 p:自定义参数，注意格式,默认为空
	 t:数据填充对象,外部调用此函数时默认为空
	 cp:当前页码,默认是1
	 isinit:是否初始化加载
	 
	**********************************/
	M:function(p,t,cp,isinit){
		
		if(p){
			this.B.newP(p,this)
		};
		var cp = parseInt(cp)?cp:this.tc.cpage;//当前页码
		var thisT = this;
		this.LastSort = null;
		
		//触发加载前执行的函数=======================================
		if(thisT.tc.beginfun){
			thisT.tc.beginfun(thisT.tc.tablename);//给自定义函数传入参数：表格名/表格数据
		};
		
		//this.B.tablechkSpan(this)?this.B.tablechkSpan(this).innerHTML = "":"";
		//this.B.CheckSave(this)?this.B.CheckSave(this).value="":"";
		
		var tc = this.tc;
		var TNM = tc.tablename;
		var checktype = tc.checktype;
		var TableDiv = t||document.getElementById(TNM+"_body_innerdiv");
		if(!document.getElementById(TNM+"_froze_div"))return;
		var TableFDiv = document.getElementById(TNM+"_froze_div").firstChild;

		
		var callback = function(data){
			thisT.GridData = data;//存储表格数据
			thisT.isOrigin?thisT.oGridData = T.CloneObj(data):"";
			//触发加载后执行的函数=======================================
//			if(thisT.tc.loadfun){
//				thisT.tc.loadfun(thisT.tc.tablename,data);//给自定义函数传入参数：表格名/表格数据
//			};
//			if(data==null||typeof(data)!="object"){T.loadTip(1,"数据格式错误！",2,"");return};
			if(data&&typeof(data)=="object"&&data.total&&parseInt(data.total)>0){
				TableDiv.innerHTML="";
				TableFDiv.innerHTML = "";
				if(data.money){
					if(!T("#total_money")){
						var paatool = document.getElementById(TNM+"_tooles_div");
						var  a = document.createElement("div");
						a.setAttribute('id','total_money');
						a.style.marginLeft = "5px";
						a.style.marginTop = "7px";
						a.style.float = "left";
						paatool.appendChild(a);
					}
					T("#total_money").innerHTML="合计：<font color='red'>"+data.money+"</font>";
				}
				if(!document.getElementById(TNM+"_header_div"))return;
				var Thead = document.getElementById(TNM+"_header_div").firstChild;
				var TheadBody = Thead.tBodies[0];
				var localindex=0;//add
				var uinindex=0;//add
				var THash =new TQHash();
				var tcell = TheadBody.rows[0].cells;
				for (var i=0;i<tcell.length;i++){
					if(tcell[i].id!="undefined"&&tcell[i].id!=null&&tcell[i].id!=""){
						if(tcell[i].id.indexOf('client_region')!=-1)//处理地区//add
						localindex=i;//add
						if(tcell[i].id.indexOf('uin')!=-1)//处理所属座席//add
						uinindex=i;//add
						var headid = tcell[i].getAttribute("headid");
						THash.setItem("tid"+headid,tcell[i].id);
						THash.setItem("twh"+headid,tcell[i].style.width);
						THash.setItem("hd"+headid,tcell[i].style.display);
						THash.setItem("process"+headid,tcell[i].process||false);
					}else{
						if(!TheadBody.rows[1]){return};
						var _tcell = TheadBody.rows[1].cells;
						for(var m=0;m<_tcell.length;m++){
							var headid = _tcell[m].getAttribute("headid");
							THash.setItem("tid"+headid,_tcell[m].id);
							THash.setItem("twh"+headid,_tcell[m].style.width);
							THash.setItem("hd"+headid,_tcell[m].style.display);
							THash.setItem("process"+headid,_tcell[m].process||false);
						}
					}
				};
				
				var tablecontent = [];
				tablecontent.push("<table id=\""+TNM+"_body\"  cellSpacing=\"0\" cellPadding=\"0\" class=\"t-grid-table\"><tbody>");
				//固定列tablefrozecol
				var tablefrozecol = [];
				var tableidentifier = [];//索引
				tablefrozecol.push("<table id=\""+TNM+"_froze_body\" cellSpacing=\"0\" cellPadding=\"0\" style=\"float:left;width:auto;display:table\"><tbody>");
				tableidentifier.push("<table id=\""+TNM+"_identifier_body\" cellSpacing=\"0\" cellPadding=\"0\" style=\"float:left;width:32px;display:table\"><tbody>");
				var tds = data.rows;
				for (var i=0;i<tds.length;i++)
				{
					
					//生成固定列
					tablefrozecol.push("<tr id=\""+TNM+"_"+tds[i].id+"_ftr\" rid=\""+tds[i].id+"\">");
					tc.ischeck?tablefrozecol.push("<td class=\"t-grid-cell t_grid_frozecell t-grid-cell-chx\" style=\"width:22px\"><input class=\"chx\" id=\""+TNM+"_"+tds[i].id+"_ck\" name=\""+TNM+"_checkinput\" type=\""+checktype+"\" value = \""+tds[i].id+"\"></td>"):"";
					if(tc.isoperate&&(tc.toolsMode == 1||!tc.hoverin)){
						tablefrozecol.push("<td class=\"t-grid-cell t_grid_frozecell t_grid_operation\"><div class=\"tddiv\">");
						tablefrozecol.push("</div></td>")
					};
					tableidentifier.push("<td class=\"t-grid-cell t_grid_frozecell\" style=\"width:32px\"><div class=\"tddiv\">"+parseInt(i+1)+"</div></td>");//添加索引
					tablefrozecol.push("</tr>");
					tableidentifier.push("</tr>");
					
					//普通列
					tablecontent.push("<tr id=\""+TNM+"_"+tds[i].id+"_tr\" rid=\""+tds[i].id+"\" class=\"t-grid-row\"");
					T.iev==6?tablecontent.push(" onmouseover=\"this.className='t-grid-cell-hover'\" onmouseout=\"this.className='t-grid-row'\""):"";
					tablecontent.push(">");
					for(var j=0;j<tds[i].cell.length;j++){
						var value=tds[i].cell[j]; //add
						if(localindex!=0&&localindex==j&&value.indexOf('|')!=-1){//add
							value=value.split('|')[1]//add
						};//add
						if(uinindex!=0&&uinindex==j&&value.indexOf('||')!=-1){//add
							value=value.split('||')[0]//add
						};//add
						//这样不好扩展，以后再改
						
						var _tdW =THash.getItem("twh"+j);
						if(!_tdW){break};
						var _fieldid = THash.getItem("tid"+j);
						var _hoverIn = "";
						var _ishide = THash.getItem("hd"+j);
						if(thisT.tc.toolsMode == 0 && thisT.tc.hoverin && (TNM + "_"+thisT.tc.hoverin == _fieldid)){
							parseInt(_tdW)<180?(_tdW = "180px",T("#"+_fieldid).style.width = _tdW):"";
							_hoverIn = "<span class=\"opspan\" id=\""+TNM+"_opspan_"+tds[i].id+"\" rid=\""+tds[i].id+"\"></span>";
							_ishide != "none"?"":(_ishide="",T("#"+_fieldid).style.display = "block");
						};
						
						tablecontent.push("<td class=\"t-grid-cell\" id=\""+_fieldid+"_"+tds[i].id+"_td\"");
						_ishide!="none"?tablecontent.push(" style=\"width:"+_tdW+"\""):tablecontent.push(" style=\"display:none;width:"+_tdW+"\"");
						tablecontent.push(">");
						if(tc.editmode){
							value = thisT.EMTS(""+_fieldid,value,tds[i].id);
							tablecontent.push("<div class=\"tddiv\" ov=\""+tds[i].cell[j]+"\">"+value+"</div>");
						}else{
							if(THash.getItem("process"+j)){//add
								tablecontent.push("<div class=\"tddiv\">"+THash.getItem("process"+j)(value,tds[i].id,_fieldid)+"</div>");//参数:value,valueid,fieldid
							}else{//add
								//根据字段类型转换显示格式
								value = thisT.TS(""+_fieldid,value,tds[i].id);
								typeof(value)=="object"?tablecontent.push("<div class=\"tddiv\"><font style=\"color:#c00\">返回值错误，不应为对象</font>"+_hoverIn+"</div>"):tablecontent.push("<div class=\"tddiv\">"+value+""+_hoverIn+"</div>");//add
							};
						};
						tablecontent.push("</td>");
					};
					tablecontent.push("</tr>");
		
				};
				tablefrozecol.push("</tbody></table>");
				tableidentifier.push("</tbody></table>");
				tablecontent.push("</tbody></table>");
				
				tc.isidentifier?tablefrozecol.push(tableidentifier.join("")):"";
				
				TableDiv.innerHTML = tablecontent.join("");
				TableFDiv.innerHTML = tablefrozecol.join("");
				
				THash.clear();
				tablecontent = null;
				tablefrozecol = null;
				
				//Thead.parentNode.parentNode.style.right = document.getElementById(TNM+"_body_div_r").scrollLeft + "px";
				//TableFDiv.style.bottom = document.getElementById(TNM+"_body_div").scrollTop + "px";
				
				//添加列选择事件=======================================
				if(thisT.tc.ischeck){
					thisT.CF();
					thisT.RC()//设置选中项状态
				};
				//添加行操作事件=======================================
				if(thisT.tc.isoperate&&(thisT.tc.toolsMode == 1||!tc.hoverin)){
					thisT.MT();
				};
				//添加行TR事件=======================================
				if(thisT.tc.trfun!=false||thisT.tc.trclickfun!=false){
					thisT.MTr();
				};
				//合计结果列出=======================================
				if(thisT.tc.countitem){
					var _countitem = thisT.tc.countitem;
					var countDiv = document.getElementById(thisT.tc.tablename + "_count_div");
					var cDHtml = "<span class=\"counttitle\" title=\"当前条件下\">合计项目（当前页/全部）</span>";
					
					for(var _c=0,_cl=_countitem.length;_c<_cl;_c++){
						//模拟
						data.countlist = data.countlist?data.countlist:{client_name:[560,1000]};
						//模拟
						cDHtml += "<span class=\"countname\">"+_countitem[_c].fieldcnname+":</span><span class=\"countvalue\">"+data.countlist[_countitem[_c].fieldname][0]+"/"+data.countlist[_countitem[_c].fieldname][1]+"</span>"
					}
					countDiv.innerHTML = cDHtml
				}
				//设置底部分页等参数=======================================
				if(thisT.tc.allowpage){
					var totpag = Math.ceil(parseInt(data.total)/parseInt(thisT.tc.rpage));
					thisT.MF(data.page,totpag,parseInt(data.total));
					document.getElementById(TNM+"_pinput_curr").onkeydown = function(e){
						var e = e ||event;
						if(e.keyCode == 13){
							var rpg = parseInt(this.value)?parseInt(this.value):0;
							if(isNaN(this.value)||parseInt(this.value)<1){rpg=thisT.tc.cpage};
							if(!isNaN(this.value)&&parseInt(this.value)>totpag){rpg=totpag};
							thisT.M("","",parseInt(rpg));
						}
					}
				};

			}else if(parseInt(data.total)==0){
				TableDiv.innerHTML="";
				TableFDiv.innerHTML = "";
				thisT.GridData = null;
				thisT.oGridData = null;
				document.getElementById(thisT.tc.tablename+"_body_innerdiv").innerHTML ="<span style=\"color:#666;padding-left:5px\">"+thisT.tc.nodatatip+"</span>";	//勿修改<span>
				//T.loadTip(1,"暂无数据",2,thisT.tc.tableObj);
			}else if(data=="gotologin"){
				T.maskTip(1,"您已长时间未进行操作,为保证数据安全,请重新登录。",3,thisT.tc.tableObj);
				data = false;
				//return
			}else{
				T.maskTip(2,"请检查您的数据格式!请点击表格[<font style=\"color:#c00\">刷新</font>]重试！或联系管理员。",3,thisT.tc.tableObj);
				data = false;
				//return
			};
			//触发加载后执行的函数=======================================
			if(thisT.tc.loadfun && thisT.tc.ischeck){
				thisT.RS(thisT,thisT.tc.tablename,data);//给自定义函数传入参数：表格名/表格数据
			};
			
			if(data&&thisT.tc.isoperate&&thisT.tc.toolsMode == 0&&thisT.tc.hoverin){
				thisT.TRHF();
			};
			
			thisT.FO();//初始化时根据内容再调整一次大小
			//isinit?thisT.FO():"";
						
		};
		if(tc.dataorign==0){
			if(!this.tc.dataUrl){alert("请设置表格数据地址");return}
			//由服务器获取数据
			var params = this.tc.param;
			var sortdefineStr = "";
			if(this.tc.orderfield&&this.tc.orderby){
				sortdefineStr = "&orderfield="+this.tc.orderfield+"&orderby="+this.tc.orderby+""
			};
			var countlistStr = [];
			if(this.tc.countitem){
				for(var _co=0,_col=this.tc.countitem.length;_co<_col;_co++){
					countlistStr.push(this.tc.countitem[_co].fieldname); 
				}
				countlistStr = "&countfieldsstr="+countlistStr.join("__");
			};
			if(this.tc.extparam&&this.tc.extparam!=""){
				params = this.tc.extparam;
			};
			params = params+sortdefineStr+countlistStr;
			if(this.tc.method.toUpperCase()=="POST"){
				var tableFields = this.tc.tableFields;
				var field_names= this.GF();
				T.A.sendData(this.tc.dataUrl,"POST",params+"&page="+cp+"&rp="+thisT.tc.rpage+"&fieldsstr="+field_names,callback,2,this.tc.tableObj);
			}else{
				var tableFields = this.tc.tableFields;
				var field_names = this.GF();
				var lurl = this.tc.dataUrl;
				lurl = lurl.indexOf("?")!=-1?lurl+"&":lurl+"?";
				T.A.sendData(lurl+params+"&page="+cp+"&rp="+thisT.tc.rpage+"&fieldsstr="+field_names,"GET","",callback,2,this.tc.tableObj);
			};
			//this.tc.extparam=false;
		}else{
			callback(tc.hotdata||{"page":1,"total":0,"rows": []})
		};
	},
	
	/**********************************
	
	 恢复每一行的选中状态(ResumeSelected)
	 	 
	**********************************/
	RS : function(thisT,tablename,data){
		var Allbox = this.B.Allbox(this);//全选checbox对象
		var Savebox = this.B.CheckSave(this);//选中的ID存贮对象
		var rows = data.rows;
		for(var i=0;i<rows.length;i++){
			var cell = rows[i].cell;
			var checked = cell[0];
			if(checked == "ischecked"){
				var d = T("#"+tablename+"_"+rows[i].id+"_ck");
				d.checked = true;
				thisT.SC(Allbox,d,Savebox);
			}
		}
	},
	
	/**********************************
	
	 获取字段(GetField)
	 	 
	**********************************/
	GF : function(){
		var field_names= "";
		var fieldPr = this.tc.tablename.toString().length+1;
		var Thead = this.B.headerObj(this).firstChild;
		var TheadBody = Thead.tBodies[0];
		var tcell = TheadBody.rows;//[0].cells
		for(var i=0;i<tcell.length;i++){
			var _tcell = tcell[i].cells;
			for(var j=0;j<_tcell.length;j++){
				if(_tcell[j].getAttribute("headid")==null||_tcell[j].getAttribute("noquery")=="true"){continue};
				if(field_names==""){
					field_names=_tcell[j].id.substring(fieldPr);
				}else{
					field_names +="__"+_tcell[j].id.substring(fieldPr);
				}
			}
		};
		
//		var tableFields = this.tc.tableFields;
//		var field_names= "";
//		for(var i=0;i<tableFields.length;i++){
//			if(field_names==""){
//				field_names=tableFields[i].fieldname;
//			}else{
//				field_names +="__"+tableFields[i].fieldname;
//			}
//		};
		return field_names
	},
	/**********************************
	
	 获取字段属性对象(GetFieldObj)
	 	 
	**********************************/
	GFO : function(fieldname){
		var ret = null;
		var _items = this.tc.tableFields;
		if(this.tc.headrows){
			for(var m=0,n=_items.length;m<n;m++){
			var items = _items[m].kinditemts;
				for (var i=0;i<items.length;i++){
					if(items[i].fieldname == fieldname){
						ret = items[i]
					}
				}
			};
		}else{
			for (var i=0;i<_items.length;i++){
				if(_items[i].fieldname == fieldname){
						ret = _items[i]
					}
			}
		}
		return ret
	},
	/**********************************
	
	 创建行点击事件(MakeTrOperation)
	 	 
	**********************************/
	MTr : function(){
		var thisT = this;
		var Allbox = this.B.Allbox(this);//全选checbox对象
		var Savebox = this.B.CheckSave(this);//选中的ID存贮对象
		var Childboxs = this.B.Childboxs(this);//所有行checkbox对象
		var tbtr = document.getElementById(this.tc.tablename+"_body").tBodies[0].rows;
		T.each(tbtr,function(tt,j){
			tt.onclick = function(){
				if(thisT.tc.trfun!=false){
					thisT.tc.trfun(this.rid||this.getAttribute("rid"))
				};
				if(thisT.tc.trclickfun!=false){
					T("#"+this.id.substring(0,this.id.length-3)+"_ck").checked = T("#"+this.id.substring(0,this.id.length-3)+"_ck").checked == true?false:true;
					thisT.SC(Allbox,T("#"+this.id.substring(0,this.id.length-3)+"_ck"),Savebox)
				}
			};
			tt = null
		})
	},
	TRHF : function(){
		//fttableopdiv opfield
		if(!document.getElementById(this.tc.tablename+"_body"))return;
		var thisT = this;
		var TNM = thisT.tc.tablename;
		var FArray = thisT.tc.isoperate;
		var opDoms = T.gbycls("opspan");
		T.each(opDoms,function(_tt){
			for(var it=0;it<FArray.length;it++){
				var modiparam = null;
				if(FArray[it].rule){
					modiparam = FArray[it].rule(_tt.getA_ttribute("rid")||_tt.rid);
					if(!modiparam){continue}
					//if(!FArray[it].rule(_tt.getAttribute("rid")||_tt.rid)){continue}
				}
				n = FArray[it];
				a = document.createElement("span");
				a.className = n.iconcls||"button16_a border_blank hover1 fl";
				a.title = n.tit?n.tit:n.name;
				a.fun = n.fun;
				a.modiparam = modiparam;
				a.setAttribute("type",n.type||"");
				//a.rid = _tt.getAttribute("rid")||_tt.rid;
				a.setAttribute("rid",_tt.getAttribute("rid")||_tt.rid);
				a.innerHTML = n.name;
				//a.onclick = function(){this.fun(this.rid,this.modiparam);this.className=="rtool"?this.className = "rtooled":"";};
				a.onclick = function(){this.fun(this.getAttribute("rid"),this.getAttribute("type"),this.modiparam);this.className += " rtooled";};
				_tt.appendChild(a)
			};
			_tt = null
		});
		var tbtr = document.getElementById(this.tc.tablename+"_body").tBodies[0].rows;
		T.each(tbtr,function(tt,j){
			tt.onmouseover = function(){
					var id = (this.rid||this.getAttribute("rid"));
					var opdiv = T("#"+TNM+"_opspan_"+id);
					opdiv.style.visibility = "visible";
			};
			tt.onmouseout = function(){
					var id = (this.rid||this.getAttribute("rid"));
					var opdiv = T("#"+TNM+"_opspan_"+id);
					opdiv.style.visibility = "hidden";
			};
			tt = null
		})
	},
	/**********************************
	
	 创建行记录操作工具(MakeopDoms),编辑/删除等
	 	 
	**********************************/
	MT : function(){
		var thisT = this;
		var tb = document.getElementById(this.tc.tablename+"_froze_body");
		var trTool = this.B.Childboxs(this);
		var n;
		var a;
		T.each(trTool,function(tt){
			var toolobj = thisT.tc.ischeck!="undefined"&&thisT.tc.ischeck!=false?tt.cells[1].firstChild:tt.cells[0].firstChild;
			for(var it=0;it<thisT.tc.isoperate.length;it++){
				var modiparam = null;
				if(thisT.tc.isoperate[it].rule){
					modiparam = thisT.tc.isoperate[it].rule(tt.getAttribute("rid")||tt.rid);
					if(!modiparam){continue}
					//if(!thisT.tc.isoperate[it].rule(tt.getAttribute("rid")||tt.rid)){continue}
				}
				n = thisT.tc.isoperate[it];
				a = document.createElement("span");
				a.className = n.iconcls||"button16_a border_blank hover1 fl";
				a.title = n.tit?n.tit:n.name;
				a.fun = n.fun;
				a.setAttribute("type",n.type||"");
				a.modiparam = modiparam;
				a.setAttribute("rid",tt.getAttribute("rid")||tt.rid);
				//a.rid = tt.getAttribute("rid")||tt.rid;
				a.innerHTML = n.name;
				//alert(n.name);
				a.onclick = function(){this.fun(this.getAttribute("rid"),this.getAttribute("type"),this.modiparam);this.className += " rtooled";};
				toolobj.appendChild(a)
			};
			tt = null
		});
		n = null;
		a = null
	},
	/**********************************
	
	 设置底部分页等参数(MakeFooter)
	 cp:当前页码
	 tp:总页码
	 dt:总记录数
	 	 
	**********************************/
	MF : function(cp,tp,dt){
		var thisT = this;
		var TNM = this.tc.tablename;
		var btnFirs = document.getElementById(TNM+"_pbutton_first");//首页
		var btnPrev = document.getElementById(TNM+"_pbutton_prev");//上一页
		var inpCurr = document.getElementById(TNM+"_pinput_curr");//当前页码
		var totPage = document.getElementById(TNM+"_pinput_pages");//总页数
		var btnNext = document.getElementById(TNM+"_pinput_next");//下一页
		var btnLast = document.getElementById(TNM+"_pinput_last");//尾页
		var dataTot = document.getElementById(TNM+"_pages_total");//总条数
		var selPage = document.getElementById(TNM+"_pages_select");//页数选择框
		
		
		//首页按钮事件
		btnFirs.onclick = function(){
			if(cp<2){return};
			//thisT.M("","",1)
			var plength = thisT.tc.param.length; 
			thisT.M({
				param:thisT.tc.param+"&tqpagetotal="+dt
			},"",1);
			thisT.tc.param = thisT.tc.param.substring(0,plength)
			
		};
		if(cp>1){
			btnFirs.title = "上翻到第一页";
			btnFirs.className = "button24_a bg_green_hover border_green fl";
		}else{
			btnFirs.title = "已到最前";
			btnFirs.className = "button24_a button_disable fl"
		};
		//上一页按钮事件
		btnPrev.onclick = function(){
			if(cp<2){return};
			//thisT.M("","",cp-1)
			var plength = thisT.tc.param.length;
			thisT.M({
				param:thisT.tc.param+"&tqpagetotal="+dt
			},"",cp-1);
			thisT.tc.param = thisT.tc.param.substring(0,plength)
			
		};
		if(cp>1){
			btnPrev.title = "上翻一页";
			btnPrev.className = "button24_a bg_green_hover border_green fl";
		}else{
			btnPrev.title = "已到最前";
			btnPrev.className = "button24_a button_disable fl"
		};
		//当前页码输入框
		inpCurr.value = cp;
		//总页数显示
		totPage.innerHTML = "\/"+tp;
		//下一页按钮事件
		btnNext.onclick = function(){
			if(cp>tp-1){return};
			//thisT.M("","",cp+1)
			var plength = thisT.tc.param.length;
			thisT.M({
				param:thisT.tc.param+"&tqpagetotal="+dt
			},"",cp+1);
			thisT.tc.param = thisT.tc.param.substring(0,plength)
		};
		if(cp<tp){
			btnNext.title = "下翻一页";
			btnNext.className = "button24_a bg_green_hover border_green fl";
		}else{
			btnNext.title = "已到最后";
			btnNext.className = "button24_a button_disable fl"
		};
		//最后一页按钮事件
		btnLast.onclick = function(){
			if(cp>tp-1){return};
			//thisT.M("","",tp)
			var plength = thisT.tc.param.length;
			thisT.M({
				param:thisT.tc.param+"&tqpagetotal="+dt
			},"",tp);
			thisT.tc.param = thisT.tc.param.substring(0,plength)
		};
		if(cp<tp){
			btnLast.title = "下翻到最后一页";
			btnLast.className = "button24_a bg_green_hover border_green fl"
		}else{
			btnLast.title = "已到最后";
			btnLast.className = "button24_a button_disable fl"
		};
		//总数据条数显示
		dataTot.innerHTML = dt;
		//单页数据数选择事件
		selPage.onchange = function(){
			thisT.tc.rpage = this.value;//更新每页显示数
			//thisT.B.CheckSave(thisT).value = "";//切换时清除选择的记录ID
			thisT.M("","",1)
		};
		this.tc.cpage = cp
		//销毁
		btnFirs = null;
		btnPrev = null;
		inpCurr = null;
		totPage = null;
		btnNext = null;
		btnLast = null;
		dataTot = null;
		selPage = null;
	},
	/**********************************
	
	 存储表格当前排序列(LastSort)
	 	 
	**********************************/
	LastSort :null,
	/**********************************
	
	 表格排序(SortGrid),目前支持排序类型：number,float,date,String
	 TableMain,0,this.cid,this.dataT,this

	**********************************/
	So:function(sTableID, bTR, sortCol, sDataType, sTd){
		//从服务器排序
		if(this.tc.isodbyserver){
			//处理最后一次排序列
			var LastSort = this.LastSort;
			if(LastSort!=null&&LastSort!=sTd){
				LastSort.firstChild.className = "o";
			};
			
			var fid = sTd.fid||sTd.getAttribute("fid");
			var orderby = "asc";
			if(sTd.firstChild.className == "o asc"){
				sTd.firstChild.className = "o desc";
				orderby = "desc"
			}else{
				sTd.firstChild.className = "o asc"
			};
			this.M({orderfield:""+fid+"",orderby:""+orderby+""});
			
			//记录最后一次排序列
			this.LastSort = sTd;
			
			//this.tc.extparam = false;
			return
		};
		//当前页排序
		//表格排序
		var Stable = document.getElementById(sTableID).firstChild.firstChild;
		if (!Stable||!this.GridData){T.loadTip(1,"当前无数据，不可排序！",2,this.tc.tableObj);return;}
		var sTBody = Stable.tBodies[0];
		var sTR = [];
		var Stablef = document.getElementById(sTableID.substr(0,sTableID.length-9)+"_froze_div").firstChild.firstChild;//固定列
		var sTBodyf = Stablef.tBodies[0];
		var sTRf = [];
		
		//处理最后一次排序列
		var LastSort = this.LastSort;
		if(LastSort!=null&&LastSort!=sTd){
			LastSort.firstChild.className = "o";
			T.each(sTBody.rows,function(eR){
				eR.cells[LastSort.cid].className = "t-grid-cell";
			});
		};
		//把当前列放入数组
		for (var i = bTR; i < sTBody.rows.length; i++) {
			sTR[i - bTR] = sTBody.rows[i];
			sTRf[i - bTR] = sTBodyf.rows[i];//固定列
		};
		//若最后一次排序列与当前排序列相同，则用reverse()逆序,并改变表头标志
		if (LastSort == sTd){
			sTd.firstChild.className = sTd.firstChild.className == "o asc"?"o desc":"o asc";
			sTR.reverse();
			sTRf.reverse();//固定列
		} else {
			sTd.firstChild.className = "o asc";
			//冒泡排序
			var rTemp;
			var rTempf;
			for (var i = 0; i < sTR.length; i++) {
				for (var j = i + 1; j < sTR.length; j++) {
					if (Convert(sTR[i].cells[sortCol].firstChild.innerHTML, sDataType) > Convert(sTR[j].cells[sortCol].firstChild.innerHTML, sDataType)) {
						rTemp = sTR[i];
						sTR[i] = sTR[j];
						sTR[j] = rTemp;
						//固定列
						rTempf = sTRf[i];
						sTRf[i] = sTRf[j];
						sTRf[j] = rTempf;
					}
				}
			};
		};
		var frag=document.createDocumentFragment();
		var fragf=document.createDocumentFragment();//固定列
		for(var i=0,l=sTR.length;i<l;i++)
		{
			frag.appendChild(sTR[i]);
			sTBody.appendChild(frag);
			//固定列
			fragf.appendChild(sTRf[i]);
			sTBodyf.appendChild(fragf);
		};
		frag = null;
		fragf = null
		T.each(sTBody.rows,function(o){//高亮当前排序列
			o.cells[sortCol].className = "t-grid-cell t-grid-cell-hover";
			o = null
		});
		
		//处理IE6排序时input值被清空的bug
		this.tc.ischeck?this.RC(1):"";	
		
		//记录最后一次排序列
		this.LastSort = sTd;
		
		function Convert(sValue, sDataType) {//排序类型转换：number,float,date,String
			sValue = sValue.replace(/<[^>].*?>/g,"");
			switch (sDataType) {
				case "number"://数字
					return parseInt(sValue);
				case "float"://浮点
					return parseFloat(sValue);
				case "date"://日期
					var dtime = sValue.replace(/[^\d]+/g, "-");
					var eTime = [];
					var nTime;
					eTime = dtime.split('-');
					if (T.iev) {
						nTime = new Date(eTime[1] + "-" + eTime[2] + "-" + eTime[0]);
					} else {
						nTime = new Date(eTime[0] + "-" + eTime[1] + "-" + eTime[2])
					}
					return nTime;
				default://字符串
					return sValue.toString();
			}
		}
	},
	/**********************************
	
	 调整列宽(ResizeColm)
	 
	**********************************/
	Rc : function(tbn,o){
		var thisT = this;
		var tdcontainerX = T("#"+tbn+"_header_div");
		var tdcontainerY = T("#"+tbn+"_body_innerdiv");
		
		var mObj;//拖动对象
		if(!document.getElementById("tablecoldragdiv"))
		{
			mObj = document.createElement("div");
			mObj.id = "tablecoldragdiv";
			mObj.style.cssText="border-left:dotted 1px #000;z-index:9999;position:absolute;left:-30px;top:-1px;width:20px;height:1px;cursor:e-resize";
			document.body.appendChild(mObj);
		}else{
			mObj = document.getElementById("tablecoldragdiv")
		};
		o.onmousedown = function(evt){//开始
			//禁止拖动时复制
			if(!T.iev||(T.iev>8)){
				if(typeof userSelect === "string"){
					return document.documentElement.style[userSelect] = "none";
				}
				document.unselectable  = "on";
				document.onselectstart = function(){
					return false;
				}
			};
			if(!thisT.GridData){T.loadTip(1,"当前表格无数据，不可调整列宽",1,thisT.tc.tableObj);return};
			var oldpos = this.offsetLeft;
			var evt = evt || window.event;
			var preX = evt.clientX;
			var pos = T.gpos(this);
			var preLeft = preX + this.offsetWidth - 5;
			mObj.style.left = preLeft + "px";
			mObj.style.top = T.gpos(T("#"+tbn+"_body_div_r")).y  + "px";
			//mObj.style.top = T.gpos(T("#"+tbn+"_body_div_r")).y + T.scrollFix().y + "px";
			mObj.style.height=T("#"+tbn+"_body_div_r").offsetHeight + 2 + "px";
			if ((!window.captureEvents)&&(!evt.preventDefault)) { //若IE
				mObj.setCapture(); 
				mObj.onmousemove = function(evt){//拖动
					var evt = evt || window.event; 
					var newX = evt.clientX;
					mObj.style.left=preLeft + newX - preX;
				};
				mObj.onmouseup = IeChangCol;
				//焦点丢失时处理
				//T.bind(mObj, "losecapture",IeChangCol);
			}else { //非IE及IE9.0
				//document.addEventListener("mouseup",NotIeChangCol,"")
				//document.addEventListener("mousemove",NotIeDragCol,"")
				T("doc").aevt("mousemove",NotIeDragCol,"");
				T("doc").aevt("mouseup",NotIeChangCol,"");
				//焦点丢失时阻止默认动作
				//T.bind(window, "blur", NotIeChangCol);
				evt.preventDefault()
			};
			function IeChangCol(evt){ //IE改变列宽
				mObj.releaseCapture(); 
				mObj.onmousemove = null;
				mObj.onmouseup = null;
				mObj.style.left="-30px";
				//var evt = arguments.length==0 ? event : arguments[0];
				var evt = event;
				var newX = evt.clientX;
				var newWidth =  newX - preX + oldpos + 8;
				var tdobj = o.parentNode.parentNode;
				if(newWidth<50) newWidth = 50;
				tdobj.style.width = newWidth + "px";
				thisT.contentWidth = thisT.contentWidth + parseInt(newWidth) - parseInt(oldpos)-6;
				tdcontainerX.style.width = thisT.contentWidth + "px";
				tdcontainerY.style.width = thisT.contentWidth + "px";
				
				if(thisT.tc.iscookcol&&T.gcok(thisT.tc.tablename)!=""){
					var tcoks = T.gcok(thisT.tc.tablename);
					var tcoksp = tcoks.split("||");
					var cvalues = "";
					for(var k=0;k<tcoksp.length;k++){
						if(tcoksp[k].split(",")[0]!=tdobj.id.replace(thisT.tc.tablename+"_","")){
							cvalues += tcoksp[k]+"||"
						};
					};
					cvalues += tdobj.id.replace(thisT.tc.tablename+"_","")+",1,"+newWidth+"||";
					T.scok(thisT.tc.tablename,cvalues.substring(0,cvalues.length-2));
				};
				
				//thisT.tc.iscookcol?T.scok(tdobj.id,["1",newWidth]):"";//保存cookie
				try{
					var obj = document.getElementById(tbn+"_body").childNodes.item(0).childNodes;
					for(var i=0;i<obj.length;i++){
						obj.item(i).childNodes.item(o.id).style.width = newWidth + "px"	
					};
					//var tdobjDiv = o.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode;
					//tdobjDiv.style.right = document.getElementById(tbn+"_body").parentNode.scrollLeft + "px";//上下对齐
					thisT.B.headerObj(thisT).style.right = thisT.B.tbodyObj(thisT).scrollLeft + "px";//上下对齐

					
					
					tdobjDiv = null;
				}catch(e){};
				tdobj = null;
				if(typeof userSelect === "string"){
					return document.documentElement.style[userSelect] = "text";
				}
				document.unselectable  = "off";
				document.onselectstart = null
			};
			function NotIeDragCol(evt){//非IE拖动
				//window.getSelection ? window.getSelection().removeAllRanges():document.selection.empty();
				var newX = evt.clientX;
				mObj.style.left=preLeft+newX-preX + "px"
			};
			function NotIeChangCol(evt){//非IE改变列宽
				T("doc").revt("mouseup",NotIeChangCol,"");
				T("doc").revt("mousemove",NotIeDragCol,"");
				//document.removeEventListener("mouseup",NotIeChangCol,"")
				//document.removeEventListener("mousemove",NotIeDragCol,"")
				document.getElementById("tablecoldragdiv").style.left= "-30px";
				var newX = evt.clientX;
				var newWidth =  newX - preX + oldpos + 8;
				if(newWidth<50) newWidth = 50;
				var tdobj = o.parentNode.parentNode;
				tdobj.style.width = newWidth + "px";
				thisT.contentWidth = thisT.contentWidth + parseInt(newWidth) - parseInt(oldpos)-6;
				tdcontainerX.style.width = thisT.contentWidth + "px";
				tdcontainerY.style.width = thisT.contentWidth + "px";
				
				if(thisT.tc.iscookcol&&T.gcok(thisT.tc.tablename)!=""){
					var tcoks = T.gcok(thisT.tc.tablename);
					var tcoksp = tcoks.split("||");
					var cvalues = "";
					for(var k=0;k<tcoksp.length;k++){
						if(tcoksp[k].split(",")[0]!=tdobj.id.replace(thisT.tc.tablename+"_","")){
							cvalues += tcoksp[k]+"||"
						};
					};
					cvalues += tdobj.id.replace(thisT.tc.tablename+"_","")+",1,"+newWidth+"||";
					T.scok(thisT.tc.tablename,cvalues.substring(0,cvalues.length-2))
				}

				//thisT.tc.iscookcol?T.scok(tdobj.id,["1",newWidth]):"";//保存cookie
				var obj = document.getElementById(tbn+"_body").childNodes.item(0).childNodes;
				for(var i=0;i<obj.length;i++){
					obj.item(i).childNodes.item(o.id).style.width = newWidth + "px"	
				};
				//var tdobjDiv = o.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode;
				//tdobjDiv.style.right = document.getElementById(tbn+"_body").parentNode.scrollLeft + "px";//上下对齐
				thisT.B.headerObj(thisT).style.right = thisT.B.tbodyObj(thisT).scrollLeft + "px";//上下对齐
				
				tdobjDiv = null;
				tdobj = null;
				if(typeof userSelect === "string"){
					return document.documentElement.style[userSelect] = "text";
				}
				document.unselectable  = "off";
				document.onselectstart = null
			}
		}
	},
	/**********************************
	
	 显示隐藏列(Visible Or Hide),目前通过cookies保存设置
	 
	**********************************/
	VH : function(t){
		var thisT = this;
		var colItems = this.tc.tableFields;
		var _collist = function(colItem){
			var colArray=[];
			for(var m=0;m<colItem.length;m++){
				var n = colItem[m].fieldcnname;
				var f = colItem[m].fieldname;
				var h = colItem[m].fhide;
				var hd = colItem[m].shide||colItem[m].fhide;
				var cookiename = t +"_"+ f;
				//var v = T.gcok(cookiename)!=null&&T.gcok(cookiename)!=""?T.gcok(cookiename).split(",")[0]:(hd?"0":"1");
				var v = null;
					
				if(thisT.tc.iscookcol){
					var gcoksp = T.gcok(t).split("||");
					for(var nn=0;nn<gcoksp.length;nn++){
						if(gcoksp[nn].split(",")[0]== f){
							v = gcoksp[nn].split(",")[1];
							break
						}
					}
				};
				if(v==null||v==""){
					v = hd?"0":"1"
				};
				!h?colArray.push({"fieldcnname":n,"fieldname":f,"fieldvalue":[v],"noList":[{value_name:"显示",value_no:"1"}],"inputtype":"checkbox"}):"";
				n = null,f=null,h=null,hd=null,cookiename=null,v=null
			};
			return colArray
		};
		var collist = function(){
			var _colArray = [];
			if(thisT.tc.headrows){
				for(var k=0;k<colItems.length;k++){
					var coli = {};
					coli.kindname = colItems[k].kindname;
					coli.kinditemts = _collist(colItems[k].kinditemts);
					_colArray.push(coli);
				}
				
			}else{
				var coli = {};
				coli.kindname = "";
				coli.kinditemts = _collist(colItems);
				_colArray.push(coli);
			};
			return _colArray
		};
		Twin({Id:t+"_set_win",Title:"表格列(显示/隐藏)设置",Width:"390",Height:"auto",sysfun:
			function(tObj){
				Tform({
					formname: t + "_ColSetForm",
					dbuttons:false,
					fit:[false],
					formObj:tObj,
					formpower:false,
					formtipbt:"<div class=\"formtip\" style=\"width:88%\">提示：当前表格列的显示属性设置保存于您本地电脑的cookies中，仅当您使用此电脑时，以上配置方能生效。并且，您需要确定您使用的浏览器设置允许接受第三方cookies。</div>",
					method:"POST",
					formAttr:[{
						formitems:collist()
					}],
					/*buttons : [//工具
						{name: "ok", dname: "确定",tit:"确定设置并保存于您本地电脑", icon: "tbtn_save.gif",onpress:function(){
							 var selItem = Serializ(t + "_ColSetForm");
							 selItem = selItem.substring(0,selItem.length-1).split("&");
							 var cvalues = "";
							 for(var i=0;i<selItem.length;i++)
							 {
								 var key = t +"_"+ selItem[i].split("=")[0];
								 var keystyle = document.getElementById(key).style.width;
								 var kwidth = keystyle.substring(0,keystyle.length-2);
									var val = [];
									if(selItem[i].split("=")[1]!=""){
										val = selItem[i].split("=")[0] +",1,"+kwidth+"||"
									}else{
										val = selItem[i].split("=")[0] +",0,"+kwidth+"||"
									};
								cvalues+=val;
							 };
							 T.scok(t,cvalues.substring(0,cvalues.length-2));
							 thisT.Th();
							 thisT.C();
							 TwinC(t+"_set_win")
							}
						},
						{name: "cancel", dname: "取消", tit:"取消本次设置",icon:"cancel.gif", onpress:function(){TwinC(t+"_set_win")} }
					],*/
					buttons:false
				});
			},	
		buttons:[{ name: "ok", dname: "确定", icon: "ok.gif", tit:"确定设置并保存于您本地电脑",onpress:function(){
				 var selItem = Serializ(t + "_ColSetForm");
				 selItem = selItem.substring(0,selItem.length-1).split("&");
				 var cvalues = "";
				 for(var i=0;i<selItem.length;i++)
				 {
					var key = t +"_"+ selItem[i].split("=")[0];
					var keystyle = document.getElementById(key).style.width;
					var kwidth = keystyle.substring(0,keystyle.length-2);
					var val = [];
					if(selItem[i].split("=")[1]!=""){
						val = selItem[i].split("=")[0] +",1,"+kwidth+"||"
					}else{
						val = selItem[i].split("=")[0] +",0,"+kwidth+"||"
					};
					cvalues+=val;
				 };
				 T.scok(t,cvalues.substring(0,cvalues.length-2));
				 thisT.Th();
				 thisT.C();
				 TwinC(t+"_set_win")
				}
			},
			{ name: "cancel",dname: "取消",tit:"取消本次设置",onpress:function(){TwinC(t+"_set_win")}}]
				
		})
	},
	/**********************************
	
	 Checkbox事件(CheckFunction)
	 
	**********************************/
	CF : function(){
		var thisT = this;
		var tbn = this.tc.tablename;//表格名
		var Allbox = this.B.Allbox(this);//全选checbox对象
		var Savebox = this.B.CheckSave(this);//选中的ID存贮对象
		var Childboxs = this.B.Childboxs(this);//所有行checkbox对象
		var checktype =this.tc.checktype;
		
		if(checktype=="checkbox"){
			//当前页全选处理
			//T.bind(Allbox,"click",function(){
				//thisT.CA(Allbox,Childboxs,Savebox)
			//});
			Allbox.onclick = function(){
				thisT.CA(Allbox,Childboxs,Savebox)
			}
		};
		//单行选择处理
		T.each(Childboxs,function(it){
			T.bind(it.cells[0].firstChild,"click",function(){
				thisT.SC(Allbox,it.cells[0].firstChild,Savebox)
			});
		})
	},
	/**********************************
	
	 单个选框事件(SingCheck)
	 
	**********************************/
	SC : function(parent,child,saveobj){
		var thisT = this;
		var parent = parent || thisT.B.Allbox(thisT);
		var saveobj = saveobj || thisT.B.CheckSave(thisT);
		var cval = saveobj.value;//已选ID值序列
		var chxvalues = cval==""?[]:cval.split(",");
		var checktype =this.tc.checktype;
		var Childboxs = this.B.Childboxs(this);//所有行checkbox对象
		var trobj = document.getElementById(this.tc.tablename+"_"+child.value+"_tr");
		
		if(checktype=="checkbox"){
			if(child.checked == true){
				if(T.AindexOf(chxvalues,child.value)<0){
					chxvalues.push(child.value)	
				}
			}else{
				if(T.AindexOf(chxvalues,child.value)>-1){
					T.Aremove(chxvalues,child.value)
				}
			};
			thisT.St(child);//设置对应行的颜色
		}else{
			T.each(Childboxs,function(rd){
				var chd = rd.cells[0].firstChild;
				if(chd.checked==true){
					if(T.AindexOf(chxvalues,chd.value)<0){
						chxvalues.push(chd.value)	
					};
					thisT.St(chd,1);//设置对应行的颜色
				}else{
					if(T.AindexOf(chxvalues,chd.value)>-1){
						T.Aremove(chxvalues,chd.value)
					};
					thisT.St(chd,0);//设置对应行的颜色
				};
			})
		};
		saveobj.value = chxvalues.join(",");
		if(this.tc.checktype=="checkbox"){
			var clength = 0;
			T.each(Childboxs,function(Ci){
				clength += T.AindexOf(chxvalues,Ci.cells[0].firstChild.value)<0?0:1
			});
			parent.checked = (clength == Childboxs.length)?true:false
		};
		this.B.tablechkSpan(this)?this.B.tablechkSpan(this).innerHTML = chxvalues.length!=0?"已选择："+chxvalues.length+"条记录":"":""
		//alert(chxvalues.length)
	},

	/**********************************
	
	 全选事件(CheckAll)
	 parent为全选checkbox;child为子checkbox所在行集合,saveobj为存储选中值的input对象
	 
	**********************************/
	CA : function(parent,child,saveobj){
		var thisT = this;
		var cval = saveobj.value;//已选ID值序列
		var chxvalues = cval==""?[]:cval.split(",");
		
		if(parent.checked == true){
			T.each(child,function(it){
				var c = it.cells[0].firstChild;
				c.checked = true;
				thisT.St(c);//设置对应行的颜色
				if(T.AindexOf(chxvalues,c.value)<0){
					chxvalues.push(c.value)	 
				};
				it = null
			})
		}else{
			T.each(child,function(it){
				var c = it.cells[0].firstChild;
				c.checked = false;
				thisT.St(c);//设置对应行的颜色
				if(T.AindexOf(chxvalues,c.value)>-1){
					T.Aremove(chxvalues,c.value)
				};
				it = null
			})
		};
	saveobj.value = chxvalues.join(",");
	this.B.tablechkSpan(this)?this.B.tablechkSpan(this).innerHTML = chxvalues.length!=0?"已选择："+chxvalues.length+"条记录":"":""
	//alert(this.GS(false).split(",").length)
	},
	/**********************************
	
	 根据保存选中值设置行checkox状态IDs(ReCheckStatus)
	 
	**********************************/
	RC : function(Sort){//Sort=1,排序时不响应自定义选择框事件
		var thisT = this;
		var SaveVal = this.B.CheckSave(this).value;//转化选中的ID存贮对象的值为数组
		var Childboxs = this.B.Childboxs(this);//所有行checkbox对象
		var clength = 0;
		var ic;
		if(SaveVal == ""){
			if(Sort!=1&&this.tc.checkfun){//响应自定义点击checkbox函数
				T.each(Childboxs,function(i){
					ic = i.cells[0].firstChild;
					thisT.St(ic)
				});
			};
			return
		}else{
			SaveVal = SaveVal.split(",");
			T.each(Childboxs,function(i){
				ic = i.cells[0].firstChild;
				T.AindexOf(SaveVal,ic.value)> -1 ? (ic.checked = true,clength = clength+1) :"";
				//ic.checked==true?thisT.St(ic):"";//设置对应行的颜色
				thisT.St(ic)
			});
			if(this.tc.checktype=="checkbox"){
				this.B.Allbox(this).checked = (clength == Childboxs.length)?true:false
			};
		};
		ic = null
		SaveVal = null;
		Childboxs = null
	},
	/**********************************
	
	 设置行选中/取消选中行的样式(SetTrStyle)以及响应自定义点击checkbox函数
	 child:选择对象
	 r:radio专用,1为选中,0为未选中
	 
	**********************************/
	St : function(child,r){
		var thisT = this;
		var trobj = document.getElementById(this.tc.tablename+"_"+child.value+"_tr");
		if(child.checked == true||(r&&r==1)){
			trobj.className = "t-grid-row-sel";
			T.iev == 6?(trobj.onmouseout = null,trobj.onmouseout = function(){this.className = "t-grid-row-sel"}):"";
		}else{
			trobj.className = "t-grid-row";
			T.iev == 6?(trobj.onmouseout = null,trobj.onmouseout = function(){this.className = "t-grid-row"}):"";
		};
		if(this.tc.checkfun){//响应自定义点击checkbox函数
			this.tc.checkfun(child)
		}
	},
	/**********************************
	
	 获取选中行IDs(GetSelected)
	 
	**********************************/
	GS : function(tip){
		var SaveVal = this.B.CheckSave(this).value;//选中的ID存贮对象的值
		if(SaveVal!=""){
			return SaveVal
		}else{
			tip==null||tip=="undefined"||tip==true?T.loadTip(1,"您未选中任何数据，至少选择<b style=\"color:#c00\">1</b>条",2,this.tc.tableObj):"";
			return false
		}
	},
	/**********************************
	
	 根据选中行IDs获取某字段值(GetSelectedByFieldName)
	 
	**********************************/
	GSByField : function(fieldname,tip){
		var SaveVal = this.B.CheckSave(this).value;//选中的ID存贮对象的值
		var ret = [];
		if(SaveVal!=""){
			SaveVal = SaveVal.split(",")
			for(var i=0;i<SaveVal.length;i++){
				if(this.GD(SaveVal[i],fieldname)==''){
					ret.push('null');
				}else{
					ret.push(this.GD(SaveVal[i],fieldname))
				}
			}
			return ret.join(",")
		}else{
			tip==null||tip=="undefined"||tip==true?T.loadTip(1,"您未选中任何数据，至少选择<b style=\"color:#c00\">1</b>条",2,this.tc.tableObj):"";
			return false
		}
	},
	/**********************************
	
	获取当前页所有行IDs(GetCurrentId)
	
	**********************************/
	GCI : function(symbol){
		var symbol = symbol?symbol:",";
		if(this.GridData){
			data = this.GridData.rows
		}else{
			return false
		};
		var allids = [];
		for(var j = 0;j<data.length;j++)
		{
			allids.push(data[j].id)
		};
		allids = allids.length>0?allids.join(symbol):"";
		return allids
	},	
	/**********************************
	
	 获取表格某字段某行的值(_GetGridData),生成表格时用
	 tid:记录ID
	 col:字段名,为空则获取整行数据{字段1:value1,字段2:value2}…
	 type:返回行的原有完整数据格式true表示是,fasle或者为空表示否
	 origin:编辑模式下获取字段原始值(true/false)默认false
	 
	**********************************/
	GD : function(tid,col,type,origin){
		var data;
		data = origin?this.oGridData:this.GridData;
		if(!data){return false};
		var tds = data.rows;
		if(col){
			var j = this.GFI(col);
			for (var i=0;i<tds.length;i++)
			{
				if(tid == tds[i].id){
					return tds[i].cell[j];
					break;
				}
			}
		}else{
			for (var i=0;i<tds.length;i++)
			{
				if(tid == tds[i].id){
					if(type){
						return tds[i]
					}else{
						return tds[i].cell
					};
					break;
				}
			}
		};
		return false
	},
	/**********************************
	
	 GetRow获取选中行,并返回rows数组rows:[{id:,cell:[{}]}]
	 ids:选择的id集合(id,id,id)
	 
	**********************************/
	GROW : function(ids){
		if(!ids){return};
		var ids = ids.split(",");
		var result = [];
		for (var i=0;i<ids.length;i++)
		{
			result.push(this.GD(ids[i],"",true))
		};
		return result
	},
	/**********************************
	
	 销毁
	 
	**********************************/
	Destroy : function(){
		var t = this;
		/*
		purge(this.B.CheckSave(this));
		purge(this.B.footerObj(this));
		purge(this.B.headerObj(this));
		purge(this.B.tbodyObj(this));
		purge(this.B.frozeObj(this));
		purge(this.B.titleObj(this));
		purge(this.B.toolObj(this));
		purge(this.B.frozehObj(this));
		purge(this.B.tabledivObj(this));
		
		this.B.CheckSave(this).value = "";
		this.B.footerObj(this).innerHTML = "";
		this.B.headerObj(this).innerHTML = "";
		this.B.tbodyObj(this).innerHTML = "";
		this.B.frozeObj(this).innerHTML = "";
		this.B.titleObj(this).innerHTML = "";
		this.B.toolObj(this).innerHTML = "";
		this.B.frozehdObj(this).innerHTML = "";
		this.B.tabledivObj(this).innerHTML = "";
		*/
		//this.unbindFO();
		
		this.B.Allbox(this)?this.B.Allbox(this).onclick = null:"";
		this.tc.tableObj?(this.unbindFO(),this.tc.tableObj.innerHTML = ""):"";
	},
	/**********************************
	
	 转换显示格式
	 TranslateStyle
	 
	**********************************/
	TS : function(fname,value,id){
		value = typeof(value)=="object"?T.Obj2Str(value):value;//对象格式采用textarea
		var fname = fname.substring(this.tc.tablename.length+1)
		var t = this;
		var tc = this.tc;
		if(fname==""){return};
		var inputhtml = [];
		var _items = this.GFO(fname);
		var FInputtype = _items.inputtype;
		switch (FInputtype){
			case "select":
			for(var j=0;j<_items.noList.length;j++){
				(value!="请选择"&&(value == _items.noList[j].value_no||value == _items.noList[j].value_name))?inputhtml.push(_items.noList[j].value_name):"";
			}
			break;
			case "cselect":
				if(_items.noList&&_items.noList.length>0){
					for(var j=0;j<_items.noList.length;j++){
						(value!="请选择"&&value!="-1"&&(value == _items.noList[j].value_no||value == _items.noList[j].value_name))?inputhtml.push(_items.noList[j].value_name):"";
					}
				}else{
					/*var act = _items.action;
					T.A.sendData("getdata.do?action=getvalue&type="+act+"&id="+value,"GET","",function(ret){
						//alert(ret);
						//inputhtml.push(ret);
						this.SV(id,fname,ret);
					});*/
					inputhtml.push(value);
				}
				break;
			/*case "mobile":
			case "telphone":
			var telphoneFvalue = value;
			
			if(telphoneFvalue.indexOf("||")!="-1"){
				var ftelphoneFvalue = telphoneFvalue.split("||")[0];
				var stelphoneFvalue =  telphoneFvalue.split("||")[1];
			}else{
				var ftelphoneFvalue = telphoneFvalue;
				var stelphoneFvalue = telphoneFvalue;
			};
			if(/^(1[4358].{9})|(01[4358].{9})$/.test(telphoneFvalue)){
				inputhtml.push( ftelphoneFvalue+"&nbsp;<img title=\"发送短信\" class=\"ordericon2\" style='cursor:pointer' src=\""+tc.Path+"sendsms.gif\" onclick=\"javascript:messageout('"+stelphoneFvalue+ "','"+tc.tableObj.id+"');\">");
			}else
				inputhtml.push(ftelphoneFvalue);
			if(value!=''&&("undefined"!=typeof(iskehuduan)&&iskehuduan))
				inputhtml.push("&nbsp;<img title=\"点击呼叫\" class=\"ordericon\" style='cursor:pointer'  src=\""+tc.Path+"phone.gif\" onclick=\"javascript:tq_call_out('"+stelphoneFvalue+ "');\">");
			break;*/
			default:
				inputhtml.push(value);
			break;
		};
		_items = null;
		return inputhtml.join("")
	},
	/**********************************
	
	 编辑模式下转换显示格式
	 EditModelTranslateStyle
	 
	**********************************/
	EMTS : function(fname,value,id){
		value = typeof(value)=="object"?T.Obj2Str(value):value;//对象格式采用textarea
		var fname = fname.substring(this.tc.tablename.length+1)
		var t = this;
		var tc = this.tc;
		var FNM= tc.tablename;
		
		if(fname==""){return};

		var inputhtml = [];
			var items = tc.tableFields;
				for (var i=0;i<items.length;i++)
				{

					var FInputtype = items[i].inputtype;
					var Fnm = items[i].fieldname;
					if(Fnm == fname){
						var Fnm = items[i].fieldname;
						var Edi = items[i].edit;
						var iW = items[i].width;
						var iH = items[i].height;
						var hd = items[i].shide;
						var nosub = items[i].nosub?"nosub=\"true\"":"";
						var _fid = FNM+"_" + Fnm +"_"+id+"";
						var colSpan=1;
						if(items[i].inputtype == "multi"&&"undefined" == typeof(items[i].height)){iH = 60};
						var Fst;
						if(iW||iH){
							Fst = (Edi==false)?""+nosub+" class=\"txt\" style=\"width:" + iW + "px;height:" + iH + "px;margin-left:0px;background:#DDE4E6;color:#000\" readonly ":""+nosub+" class=\"txt\" style=\"width:" + iW + "px;height:" + iH + "px;margin-left:0px;\" ";
						}else{
							Fst = (Edi==false)?""+nosub+" class=\"txt\" style=\"margin-left:0px;background:#DDE4E6;color:#000\" readonly ":""+nosub+" class=\"txt\" style=\"margin-left:0px;\" ";
						}
						var Dfn = "onfocus=\"T.addcls(this,'h')\"  onblur=\"T.remcls(this,'h')\" ";
						if(items[i].fn){
							var eF = items[i].fn[0];
							Dfn = "onfocus=\"T.addcls(this,'h')\" onblur=\"T.remcls(this,'h')\" "+eF.type+"="+eF.fun+"('"+FNM+"','"+id+"')";
						};
						var _fvalue = value//items[i].fieldvalue;
						if(items[i].Fn){
							var eF = items[i].Fn[0];
							Dfn += " "+eF.type+"=javascript:"+eF.fun+"('"+FNM+"',this.value,'"+id+"')";
						};
						var extrahtml;
						if(items[i].trefn){
							extrahtml = items[i].trefn(FNM,Fnm,_fvalue,id)
							inputhtml.push(extrahtml);
						}else{
							switch (FInputtype){
								case "text":
								case "number":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\">");
								//inputhtml.push(""+fname+","+value+","+id+"")
								break;
								case "multi":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<textarea id=\""+_fid+"\" name=\"" + Fnm + "\" "+Dfn+" "+Fst+"");
								inputhtml.push(">" + _fvalue + "</textarea>");
								//inputhtml.push( "<img title=\"添加备注\" class=\"ordericon2\" src=\""+tc.Path+"sendsms.gif\" onclick=\"javascript:messageout('"+_fid+"');\">");
								break;
								case "checkbox":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								for(var j=0;j<items[i].noList.length;j++){
									inputhtml.push( "<input class=\"chk\" "+nosub+" type=\"checkbox\" name=\"" + Fnm + "\" value=\"" + items[i].noList[j].value_no + "\" id=\"_fid_" + items[i].noList[j].value_no + "\"");
									T.ie?inputhtml.push(" style=\"margin-left:5px;\""):inputhtml.push(" style=\"margin:0px;\"");
									(Edi==false)?inputhtml.push(" disabled "):"";
									var fvalues = _fvalue;
									typeof(fvalues)!="object"?fvalues = [fvalues]:"";
									for(var n=0;n<fvalues.length;n++){
										var fvalue = fvalues[n];
										if(fvalue == items[i].noList[j].value_no||fvalue == items[i].noList[j].value_name){inputhtml.push(" checked");}
									}
									inputhtml.push( ">");
									inputhtml.push( "<label class=\"lbl\" for=\"_fid_" + items[i].noList[j].value_no + "\"");
									T.ie?"":inputhtml.push(" style=\"padding-right:3px;padding-top:3px\"");
									inputhtml.push( ">" + items[i].noList[j].value_name + "</label>");
								}
								break;
								case "radio":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								for(var j=0;j<items[i].noList.length;j++){
									inputhtml.push( "<input class=\"chk\" "+nosub+" type=\"radio\" name=\"" + Fnm + "\" value=\"" + items[i].noList[j].value_no + "\" id=\"_fid_" + items[i].noList[j].value_no + "\"");
									T.ie?inputhtml.push(" style=\"margin-left:5px;\""):inputhtml.push(" style=\"margin:0px;\"");
									(Edi==false)?inputhtml.push(" disabled "):"";
									var fvalues = fvalue;
									typeof(fvalues)!="object"?fvalues = [fvalues]:"";
									for(var n=0;n<fvalues.length;n++){
										var fvalue = fvalues[n];
										if(fvalue == items[i].noList[j].value_no||fvalue == items[i].noList[j].value_name){inputhtml.push(" checked");}
									}
									inputhtml.push( ">");
									inputhtml.push( "<label class=\"lbl\" for=\"_fid_" + items[i].noList[j].value_no + "\"");
									T.ie?"":inputhtml.push(" style=\"padding-right:3px;padding-top:3px\"");
									inputhtml.push( ">" + items[i].noList[j].value_name + "</label>");
								}
								break;
								case "select":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<select "+Dfn+" class=\"slct\" "+nosub+" name=\"" + Fnm + "\" id=\""+_fid+"\"");
								if(Edi==false){inputhtml.push(" disabled style=\"background:#DDE4E6;color:#000\" ")};
								T.ie?inputhtml.push(" style=\"margin-left:5px;\""):"";
								inputhtml.push(">");
								for(var j=0;j<items[i].noList.length;j++){
									inputhtml.push( "<option value=\"" + items[i].noList[j].value_no + "\" ");
									(_fvalue == items[i].noList[j].value_no||_fvalue == items[i].noList[j].value_name)?inputhtml.push( " selected"):"";
									inputhtml.push( ">" + items[i].noList[j].value_name + "");
									inputhtml.push( "</option>");
								}
								inputhtml.push( "</select>");
								break;
								
								case "ivrlist":
								items[i].ubox_id = items[i].ubox_id||1000;
								items[i].nodeId = items[i].nodeId||0;
								inputhtml.push( "<input "+Dfn+" type=\"text\" class = \"txt\" style=\"float:left;width:150px\" uboxid = \""+items[i].ubox_id+"\" value=\"" + getTreeNodeName(_fvalue,items[i].ubox_id) + "\" id=\""+_fid+ "_text\" name=\"" + Fnm + "_text\" nosub=\"true\" onclick=\"ivrNodeSelect('"+tc.formFunId+"',this)\" title=\"点击选择\" style=\"cursor:pointer;\"/>");
								inputhtml.push( "<input type=\"hidden\" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\" />");
								inputhtml.push( "&nbsp;<span class=\"button2\" style=\"width:80px\" title=\"新增呼叫流程节点\" onclick=\"AddIvrNode('"+FNM+"_" + Fnm + "','"+items[i].ubox_id+"','"+items[i].nodeId+"')\"/>+新增节点</span>");
								//&nbsp;<span class=\"button2\" style=\"width:80px\" title=\"删除当前显示节点\" onclick=\"DelIvrNode('"+FNM+"_" + Fnm + "')\">-删除节点</span>
								break;
								
								case "localselect":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								if(_fvalue!=""){
									tc.areaData = ""+_fid+"||"+_fvalue+""
								}else{
									tc.areaData = "";
								};
								inputhtml.push("<input type='hidden' id=\""+_fid+"\" "+nosub+" name=\"client_region\" value=\"\"/>");
								inputhtml.push( "<select "+nosub+" style=\"float:left\" name=\"f_s_" + Fnm + "\" id=\"_fidinput_sheng\" onchange='setshi(this.value,\""+_fid+"\")'");
								inputhtml.push(">");
								for(var j=0;j<items[i].noList.length;j++){
									inputhtml.push( "<option value=\"" + items[i].noList[j].value_no + "\" ");
									(_fvalue == items[i].noList[j].value_no)?inputhtml.push( " selected"):"";
									inputhtml.push( ">" + items[i].noList[j].value_name + "");
									inputhtml.push( "</option>");
								};
								inputhtml.push( "</select>");
								inputhtml.push("<span id=\"_fidspan_shi\"><select "+nosub+" id=\"_fidinput_shi\"  onchange=\"setquxian(this.value,'"+_fid+"')\"><option >请选择</option></select></span>");
								inputhtml.push("<span id=\"_fidspan_quxian\"><select "+nosub+" id=\"_fidinput_quxian\"  onchange=\"setquxian(this.value,'"+_fid+"')\"><option >请选择</option></select></span>");
								break;
								case "telphone":
								var telphoneFvalue = _fvalue;
								if(telphoneFvalue.indexOf("||")!="-1"){
									var ftelphoneFvalue = telphoneFvalue.split("||")[0];
									var stelphoneFvalue =  telphoneFvalue.split("||")[1];
								}else{
									var ftelphoneFvalue = telphoneFvalue;
									var stelphoneFvalue =  items[i].valueencry&&items[i].valueencry!=""?items[i].valueencry:telphoneFvalue;
								};
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + ftelphoneFvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input type=\"hidden\" "+nosub+" value=\"" + stelphoneFvalue + "\" id=\""+_fid+"_encry\" name=\"" + Fnm + "_encry\">");	
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + ftelphoneFvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"");
								if(Edi==false){inputhtml.push(" readonly style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( ">");
								inputhtml.push( "<img title=\"点击呼叫\" class=\"ordericon\" src=\""+tc.Path+"phone.gif\" onclick=\"javascript:callout('"+_fid+"','"+_fid+"_encry');\">");};
								break;
								case "mobile":
								var mobileFvalue = _fvalue;
								if(mobileFvalue.indexOf("||")!="-1"){
									var fmobileFvalue = mobileFvalue.split("||")[0];
									var smobileFvalue =  mobileFvalue.split("||")[1];
								}else{
									var fmobileFvalue = mobileFvalue;
									var smobileFvalue =  items[i].valueencry&&items[i].valueencry!=""?items[i].valueencry:mobileFvalue;
								};
								if(hd==true){
									inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + fmobileFvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break;
								};
								inputhtml.push( "<input type=\"hidden\" "+nosub+" value=\"" + smobileFvalue + "\" id=\""+_fid+"_encry\" name=\"" + Fnm + "_encry\">");	
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + fmobileFvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"");
								if(Edi==false){inputhtml.push(" readonly style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( ">");
								inputhtml.push( "<img title=\"点击呼叫\" class=\"ordericon\" src=\""+tc.Path+"phone.gif\" onclick=\"javascript:callout('"+_fid+"','"+_fid+"_encry');\" />");
								smobileFvalue!=""?inputhtml.push( "<img title=\"发送短信1\" class=\"ordericon2\" src=\""+tc.Path+"sendsms.gif\" onclick=\"javascript:messageout('"+_fid+"_encry','"+tc.tableObj.id+"');\" />"):inputhtml.push( "<img title=\"发送短信\" class=\"ordericon2\" src=\""+tc.Path+"sendsms.gif\" onclick=\"javascript:messageout('"+_fid+"','"+tc.tableObj.id+"');\" />");
								};
								break;
								case "email":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"");
								if(Edi==false){inputhtml.push(" readonly style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( ">");
								inputhtml.push( "<img title=\"发送邮件\" class=\"ordericon\" src=\""+tc.Path+"email.gif\" onclick=\"javascript:mailout('"+_fid+"');\">");
								};
								break;
								case "wwwnet":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"");
								if(Edi==false){inputhtml.push(" readonly style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( ">");
								inputhtml.push( "<img title=\"打开网址\" class=\"ordericon\" src=\""+tc.Path+"openurl.png\" onclick=\"javascript:openurl('"+_fid+"');\">");
								};
								break;
								case "wwwsearch":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"");
								if(Edi==false){inputhtml.push(" readonly style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( ">");
								inputhtml.push( "<img title=\"百度搜索\" class=\"ordericon\" src=\""+tc.Path+"baidu.gif\" onclick=\"javascript:wwwsearch('"+_fid+"');\">");
								};
								break;
								case "uploadfile":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input readonly "+Dfn+" type=\"text\" "+Fst+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"");
								if(Edi==false){inputhtml.push(" readonly style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( ">");
								inputhtml.push( "<img title=\"上传文件\" class=\"ordericon\" src=\""+tc.Path+"upload.gif\" onclick=\"javascript:upload('"+_fid+"');\">");
								};
								break;
								case "seluser":
								inputhtml.push( "<input "+Dfn+" readonly "+Fst+" value=\"" + _fvalue.split('||')[0] + "\" id=\"_fid_text\" name=\"" + Fnm + "_text\"  onclick=\"selectuser(this.id);\" style=\"cursor:pointer;_cursor:hand\"");
								(hd==true)?inputhtml.push(" type=\"hidden\""):inputhtml.push(" type=\"text\"");
								inputhtml.push(" title='点击选择'>");
								inputhtml.push( "<input "+Dfn+" type=\"hidden\" "+nosub+"  value=\"" + _fvalue.split('||')[1] + "\" id=\""+_fid+"\" name=\"" + Fnm + "\">");
								break;
								case "selalluser":
								inputhtml.push( "<input "+Dfn+" readonly "+Fst+" value=\"" + _fvalue.split('||')[0] + "\" id=\"_fid_text\" name=\"" + Fnm + "_text\"  onclick=\"selectuser(this.id,'1');\" style=\"cursor:pointer;_cursor:hand\"");
								(hd==true)?inputhtml.push(" type=\"hidden\""):inputhtml.push(" type=\"text\"");
								inputhtml.push(" title='点击选择'>");
								inputhtml.push( "<input "+Dfn+" type=\"hidden\" "+nosub+"  value=\"" + _fvalue.split('||')[1] + "\" id=\""+_fid+"\" name=\"" + Fnm + "\">");
								break;
								case "date":
								if(hd==true){inputhtml.push(" <input type=\"hidden\" "+nosub+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"> ");break};
								inputhtml.push( "<input "+Dfn+" type=\"text\" "+Fst+" value=\"" + _fvalue + "\" id=\""+_fid+"\" name=\"" + Fnm + "\"  onClick=\"WdatePicker({startDate:'%y-%M-01 00:00:00',alwaysUseStartDate:true});\"");
								if(Edi==false){inputhtml.push(" disabled style=\"background:#DDE4E6;color:#000\">")}else{
								inputhtml.push( " title=\"点击选择\" style=\"cursor:pointer;_cursor:hand\">");
								//inputhtml.push( "<img title=\"点击选择\" class=\"ordericon\" src=\""+tc.Path+"date.png\" onClick=\"WdatePicker({el:'_fid_"+i+"'})\">");
								};
								break;
							}
						};
						continue
					};
				}
		return inputhtml.join("")
	},
	/**********************************
	
	 获取字段index
	 GetFieldIndex
	 
	**********************************/
	GFI : function(fname){
		var Thead = this.B.headerObj(this).firstChild;
		var TheadBody = Thead.tBodies[0];
		var tcell = TheadBody.rows;//[0].cells
		for(var i=0;i<tcell.length;i++){
			var _tcell = tcell[i].cells;
			for(var j=0;j<_tcell.length;j++){
				if (_tcell[j].id == this.tc.tablename+"_"+fname){
					return _tcell[j].getAttribute("headid");
					break
				}
			}
		};
		
/*		var fitem = this.tc.tableFields;
		for(var i=0;i<fitem.length;i++){
			if (fitem[i].fieldname == fname){
				return i;
				break
			}
		};
*/		return false
	},/**********************************
	
	 添加行
	 AddRow
	 data:[{id:,cell:[{}]}]
	 limitfield:不允许重复值的字段，若重复则不增加，若为空就允许重复
	 
	**********************************/
	AR : function(data,limitfield){
		var t = this;
		var oData = this.GridData||{"page":1,"total":0,"rows": []};
		var _oData = oData.rows;
		if(typeof(data)!="object"){alert("行数据格式错误");return};
		var compare = function(limitValue){
			if(limitValue==false){return true};
			var flag = true;
			for (var m = 0;m<_oData.length;m++){
				if(limitValue==t.GD(_oData[m].id,limitfield)){
					flag = false;
					break
				};
			};
			return flag
		};
		if(oData.rows.length > 0){
			for(var i=0;i<data.length;i++){
				if(!limitfield||compare(data[i].cell[t.GFI(limitfield)],limitfield)){
					this.SM(data[i],"add");
					oData.rows.unshift(data[i]);
					oData.total += 1;
					t.isOrigin = false;//原始表格数据已被更改
				}
			};
		}else{
			for(var i=0;i<data.length;i++){
				this.SM(data[i],"add");
				oData.rows.unshift(data[i]);
				oData.total += 1;
				t.isOrigin = false;//原始表格数据已被更改
			};
		};
		var dorign = this.tc.dataorign;
		this.M({hotdata:oData,dataorign:1});
		this.tc.dataorign=dorign;
	},
	
	/**********************************
	
	 删除行
	 DeleteRow
	 
	**********************************/
	DR : function(ids){
		var oData = this.GridData;
		if(typeof(oData)!="object"){alert("无数据");return};
		var data = oData.rows;
		var id = ids.split(",");
		for (var m = 0;m<id.length;m++){
			for(var i=0;i<data.length;i++){
				if(data[i].id==id[m]){
					this.SM(data[i],"del",data[i].type=="add");
					data.splice(i,1);	
					oData.total -= 1;
					this.isOrigin = false;//原始表格数据已被更改
					break;
				};
			};
		};
		var dorign = this.tc.dataorign;
		this.M({hotdata:oData,dataorign:1},"",this.tc.cpage);
		this.tc.dataorign=dorign;
	},
	/**********************************
	
	 获取修改值并生成json。仅编辑模式下。
	 GetModify
	 tid:记录id（行id）,为空则更新表格当前页所有数据,已经变动过记录由type标记
	 
	**********************************/
	GM : function(tid){
		if(!this.tc.editmode){alert("非编辑模式表格，无此功能");return};
		var cols = this.tc.tableFields;
		if(tid){
			for(var n=0;n<cols.length;n++){
				this.SD(tid,cols[n].fieldname)
			};
		}else{
			var data;
			if(this.GridData){
				tds = this.GridData.rows
			}else{
				return false
			};
			
			for(var m=0;m<tds.length;m++){
				for(var n=0;n<cols.length;n++){
					this.SD(tds[m].id,cols[n].fieldname)
				};
			};
		}
	},
	/**********************************
	
	 编辑模式下更新更改的值
	 SetData
	 tid:记录ID
	 col:字段名
	 
	**********************************/
	SD : function(tid,col){
		//if(!this.tc.editmode){alert("非编辑模式表格，无此功能");return};
		var data;
		if(this.GridData){
			data = this.GridData
		}else{
			return false
		};
		var tds = data.rows;
		var tinput = T("#"+this.tc.tablename+"_"+col+"_"+tid+"");
		if(col){
			var cols = this.tc.tableFields;
			for (var j=0;j<cols.length;j++)
			{
				if(col == cols[j].fieldname){break;}
			};
			for (var i=0;i<tds.length;i++)
			{
				if(tid == tds[i].id){
					tds[i].cell[j] = tinput.value;
					if(tds[i].type=="add"){
						this.SM(tds[i],"add")
					}else if(this.GD(tid,col,"",true)!=tinput.value){
						this.SM(tds[i],tds[i].type||"mod")
					}else{
						this.SM(tds[i],tds[i].type||"mod",true)
					};
					break;
				}
			}
		}else{
			return false
		};
	},
	
	/**********************************
	
	 根据ID设置tableitems的fieldvalue值(编辑某条记录的时候有用)
	 id:记录ID
	 
	**********************************/
	SDBID : function(id){
		var t = this;
		if(this.tc.headrows){
			var ti = t.tc.tableitems;
			T.each(ti,function(p,i){
				T.each(p.kinditemts,function(o,j){
					o.fieldvalue = t.GD(id,o.fieldname)
				})
			})
		}else{
			T.each(t.tc.tableitems,function(o,j){
				o.fieldvalue = t.GD(id,o.fieldname)
			});
		};
	},
	/**********************************
	
	 清除tableitems的fieldvalue值(编辑某条记录的时候有用)
	 id:记录ID
	 
	**********************************/
	ClearFV : function(){
		var t = this;
		if(this.tc.headrows){
			var ti = t.tc.tableitems;
			T.each(ti,function(p,i){
				T.each(p.kinditemts,function(o,j){
					o.fieldvalue = ""
				})
			})
		}else{
			T.each(t.tc.tableitems,function(o,j){
				o.fieldvalue = ""
			});
		};
	},
	/**********************************
	 添加行（不刷新整个表格）
	 NewAddRow
	 data:[{id:,cell:[{}]}]
	 limitfield:不允许重复值的字段，若重复则不增加，若为空就允许重复
	 
	**********************************/
	NAR : function(data,limitfield){
		var t = this;
		var oData = this.GridData||{"page":1,"total":0,"rows": []};
		var _oData = oData.rows;
		if(typeof(data)!="object"){alert("行数据格式错误");return};
		var compare = function(limitValue){
			if(limitValue==false){return true};
			var flag = true;
			for (var m = 0;m<_oData.length;m++){
				if(limitValue==t.GD(_oData[m].id,limitfield)){
					flag = false;
					break
				};
			};
			return flag
		};
		if(oData.rows!=""){
			for(var i=0;i<data.length;i++){
				if(!limitfield||compare(data[i].cell[t.GFI(limitfield)],limitfield)){
					oData.rows.unshift(data[i]);
					oData.total += 1;
				}
			};
		}else{
			for(var i=0;i<data.length;i++){
				oData.rows.unshift(data[i]);
				oData.total += 1;
			};
		};
		var dorign = this.tc.dataorign;
		this.M({hotdata:oData,dataorign:1},"",this.tc.cpage);
		this.tc.dataorign=dorign;
	},
	/**********************************
	
	 设置被修改数据集合(SetModifyData)
	 row 被修改的单行数据对象{"id":"2","cell":["2","80121632","北京2"]}
	 type 操作类型(add/mod/del)
	 recover true/false 是否是复原
	 	 
	**********************************/
	SM : function(row,type,recover){
		if(typeof(row)!="object" || typeof(type)!= "string")return;
		var recover = recover||false;
		var tM = this.ModifyData;
		var tMrowType = "";
		for(var i=0;i<tM.rows.length;i++){
			if(tM.rows[i].id == row.id){
				tMrowType = tM.rows[i].type;
				tM.rows.splice(i,1);
				tM.total -= 1;
				break;
			}
		};
		if(!recover&&tMrowType!="del"){
			row.type = type;
			tM.rows.push(row);
			tM.total += 1;
		}
	},
	/**********************************
	
	 更新单元格值(updateCellData)
	 	 
	**********************************/
	UCD: function(rid,fieldname,value) {
		var value = value?value:"";
		var cellObj = T("#"+this.tc.tablename+"_"+fieldname+"_"+rid+"_td")?T("#"+this.tc.tablename+"_"+fieldname+"_"+rid+"_td"):null;
		if(cellObj==null)return;
		cellObj.getElementsByTagName("div")[0].innerHTML = value;
    },
	/**********************************
	
	 根据rid获取单元格的显示值(GetCellValue)不是数据库中的值
	 	 
	**********************************/
	GCV: function(rid,fieldname) {
		if(!rid)return;
		var cellObj = T("#"+this.tc.tablename+"_"+fieldname+"_"+rid+"_td")?T("#"+this.tc.tablename+"_"+fieldname+"_"+rid+"_td"):null;
		if(cellObj==null)return;
		return cellObj.getElementsByTagName("div")[0].innerHTML;
    }
};
