/*TQtree
	注意:节点数据源参数不能与nodeInit中定义的this.nodes中的参数名相同(除title外)
	必须参数与格式：{pid_id:{id:id,name:name}}  分页扩展： {page:1,total:10,rows:{pid_id:{id:id,name:name}}}
	
	注释中的sourceId为relationIndex以"_"分割后的第二个值,各函数使用sorceId时，必须保证各节点唯一,即同一节点不会被多个节点引用
	2013-03-05 暂未充分扩展checkbox与radio混选的方式
	2013-06-21 扩展checkbox相关功能
	2013-08-31 扩展N多功能
	2013-12-24 增加从服务器搜索 及 分页功能
*/
tqTree = function(o){
	/**********************************
	 基本属性
	**********************************/
	this.tc = T.extend({
		Path:"images/Mtree/",//资源文件路径
		treeId:"TQtree",//唯一标识，不可重复
		treeObj:null,//树依附对象
		keyBoard:false,//是否允许键盘操作
		treeTip:null,//帮助提示
		isSearch:false,//是否显示按名字搜索
		searchLoadAll:true,//搜索时先加载全部数据
		seearchUI:null,//搜索区域,默认在列表区域顶部
		
		isTool:false,//是否显示展开全部/收缩全部工具
		dataUrl:"../tree/demodata.txt",//数据请求地址
		method:"get",//数据请求方式
		param:"",//请求参数,可变
		searchparam:"",//搜索参数 searchvalue=xxxx
		ajaxSearch:false,//从服务器搜索
		dataType:1,//1表示远程读取,0表示本地数据
		localData:null,//dataType为0时,直接赋值
		focusExec:true,//定位到某节点时同时执行点击事件
		nodeClick:null,//全局节点点击事件。亦可单独配置节点fn属性单独执行fn函数,都有时,只执行fn
		nodeFnArgs:"",//节点事件所需参数(id,name,,,,),包括nodeClick以及fn
		
		nodeExt:[{name:"",tit:"上移",type:"up",condition:[{param:"hasChild",value:false}],iconcls:"icon16 icon16up"},{name:"",tit:"下移",type:"down",iconcls:"icon16 icon16down"}],//节点扩展操作按钮(节点文字右边显示)
		//必须是这样的格式
		//注意:conditon为空或未定义时,表示所有节点均添加此扩展操作
		//另外还可以在外部通过动态创建dom来扩展自定义按钮
		//与nodeExtFn配合使用
		nodeExtFn:function(){var args = arguments;var type =args[args.length-1]; alert(type)/*alert(args[0]+"___"+args[1])*/},//扩展按钮对应的事件函数,与nodeExt配合使用
		
		nodeExt:false,
		nodeExtFn:false,
		
		loadfun:false,//树加载完后执行函数
		isCheck:false,
		checkFun:false,//自定义checkbox事件
		expandExeChkF:false,//展开时是否执行自定义checkbox事件
		expandFun:false,//展开时执行函数
		checkType:"checkbox",//全局初始化设置为checkbox,radio,不设置则不参与选择。亦可单独配置子节点属性:cktp(另:初始是否选中属性ckd：true/false)
		checkChild:true,//是否关联选择子项目
		checkParent:true,//是否关联选择父项目
		treeW:"",//值为auto时,表示自动宽度
		treeH:"",//值为auto时,表示自动高度
		MaxH:false,//最大高度(暂未限制,待扩展)
		MinH:200,//最小高度 默认必须是大于30(搜索框高29)正整数
		ajaxTip:false,//动态数据加载时提示区域 dom对象
		cutpage:false,//是否分页
		perpage:300,//每页条数,默认必须大于0
		cpage:1,//初始化当前页
		recordtotal:0 //分页时服务器返回总数  勿改,后台返回,非可定义初始化参数
		
	},o);
	var t = this;
	t._d    = ",";//父子关系字符串集合分隔符
	t.divider   = "_";//父子关系连接字符串,勿改
	t.colors   = //节点选中样式
	{
		"highLight" : "#FFE6B0",
		"highLightText" : "#000000",
		"border" : "1px solid #FFB951"
	};
	t.icons = {};
	t._icons    = { //节点图标配置
		L0        : 'L0.gif',  //┏
		L1        : 'L1.gif',  //┣
		L2        : 'L2.gif',  //┗
		L3        : 'L3.gif',  //━
		L4        : 'L4.gif',  //┃
		PM0       : 'P0.gif',  //＋┏
		PM1       : 'P1.gif',  //＋┣
		PM2       : 'P2.gif',  //＋┗
		PM3       : 'P3.gif',  //＋━
		empty     : 'L5.gif',     //空白
		root      : 'root.gif',   //缺省根节点图标
		folder    : 'folder.gif', //缺省文件夹图标
		file      : 'file.gif',   //缺省文件图标
		wait      : 'wait.gif',   //加载中
		C0        :'checkbox0.gif',  //未选中
		C1        :'checkbox1.gif',  //选中
		C2        :'checkbox2.gif',  //半选中
		R0        :'radio0.gif',   //未选中
		R1        :'radio1.gif',   //选中
		R2        :'radio2.gif',   //半选中
		book      :'book.gif',
		books     :'books.gif',
		bookset   :'bookset.gif',
		user      :'user.gif',
		users     :'users.gif',
		blcokuser :'BlockUser.png',
		rmb       :'RMB.gif',
		object    :'Objects.gif',
		home      :'Home.gif',
		webchat   :'webchat.png',
		api       :'Remote.png',
		seat      :'seat.gif',
		seats     :'seats.gif',
		phone     :'phone.gif',
		call      :'call.gif',
		building  :'building.gif',
		music     :'music.gif',
		message   :'msg.gif',
		tree      :'tree.gif',
		star      :'star.gif',
		syslog    :'earthpaper.gif',
		ivr_voice_avigation   :'music.gif',
		ivr_seat_queue :'seats.gif',
		ivr_did   :'did.gif',
		dnis_number_tb     :'phone.gif',
		book      :'book.gif',
		books     :'books.gif',
		bookset   :'bookset.gif',
		user      :'user.gif',
		users     :'users.gif',
		blcokuser :'BlockUser.png',
		rmb       :'RMB.gif',
		object    :'Objects.gif',
		home      :'Home.gif',
		webchat   :'webchat.png',
		api       :'Remote.png',
		seat      :'seat.gif',
		seats     :'seats.gif',
		phone     :'phone.gif',
		call      :'call.gif',
		call1     :'call1.gif',
		building  :'building.gif',
		music     :'music.gif',
		message   :'msg.gif',
		tree      :'tree.gif',
		star      :'star.gif',
		syslog    :'earthpaper.gif',
		media     :'lookpaper.gif'
	};
	t.iconsExpand = {};
	t._iconsExpand = {  //节点展开时对应图片
		PM0       : 'M0.gif',     //－┏
		PM1       : 'M1.gif',     //－┣
		PM2       : 'M2.gif',     //－┗
		PM3       : 'M3.gif',     //－━
		folder    : 'folderopen.gif'
	};
	t.keydown= function(e){ //按键操作对应事件
		var treeId = t.tc.treeId;
		if(!t.tc.keyBoard)return;
		if(!T("#"+treeId+"_treelist"))return;
		e = window.event || e; var key = e.keyCode || e.which;
		var e_tar=e.srcElement?e.srcElement:e.target;
		if(e_tar.id.split("_")[0]!=t.tc.treeId)
		{
		  return;
		};
		switch(key)
		{
			case 37 : eval(treeId).upperNode(); break;  //左
			case 38 : eval(treeId).pervNode();  break;  //上
			case 39 : eval(treeId).lowerNode(); break;  //右
			case 40 : eval(treeId).nextNode();  break;  //下
		}
	};
	t.currentNode = null;//当前定位节点对象
	t.currentPath = "";//当前定位节点路径
	t.init = function(){
		t.checkedNodes = [];//已选中项的索引ID集合
		t.checkedNode = "";//radio临时单独处理
		t.index = 0;//节点初始索引
		t.nodes = {};//节点集合
		t.relation = "";//父子关系字符串集合
		t.isAllLoad = false;//节点是不是全部都已加载完毕
		t.nodes["0"] = //初始化根节点
		{
			"nodeId": "0",
			"path": "root",
			"isLoad": false,
			"childNodes": [],
			"childPrepose": "",
			"sourcePath": "root",
			"relationIndex": "root"
		};
	}
};
tqTree.prototype = {
	treeData:null,
	orginData:null,
	searchData:null,//搜索结果
	tcBind : function(p){//更改树属性参数辅助
		var l = this.tc;
		this.tc = null;
		this.tc = T.extend(l,p);
		l = null;
	},
	/**********************************
	 生成树/重建树(Createtree)
	 @boolean isFresh 是否为刷新操作
	**********************************/
	C:function(isFresh){
		if(!this.tc.treeObj){T.loadTip(1,"TREE缺少参数",2);return}
		var t = this;
		var tc = this.tc;
		
		t.init();
		
		var treeObj = tc.treeObj;
		treeObj.innerHTML = "";
		if(tc.treeTip){
			treeObj.innerHTML = tc.treeTip
		};
		var treeDiv = document.createElement("div");
		
		var treeObjH = treeObj.offsetHeight||1;
		var _treeObjH = treeObjH < tc.MinH ? tc.MinH:treeObjH;
		var searchH = tc.isSearch?29:0;
		var tooleH = tc.isTool?25:0;
		
		tc.isSearch?this.createSearch(_treeObjH - searchH):"";
		tc.isTool?this.createTool():"";
		
		!tc.treeH?
			(treeDiv.style.height = _treeObjH - searchH - tooleH + "px",treeObj.style.overflow = "hidden")
			:
			(!isNaN(parseInt(tc.treeH))?
				treeDiv.style.height = tc.treeH + "px"
				:""
			);
		!isNaN(parseInt(tc.treeW))?
			treeDiv.style.width = tc.treeW + "px"
			:(treeDiv.style.width = "100%")
		treeDiv.style.overflow = "auto";
		treeDiv.id = this.tc.treeId + "_treelist";
		treeDiv.style.position = "relative";
		treeObj.appendChild(treeDiv);
		
		this.M(isFresh);
	},
	/**********************************
	 列出(MakeTree)
	 @boolean isFresh 是否为刷新操作
	**********************************/
	M:function(isFresh){
		var t = this;
		var tc = this.tc;
		
		var treeDiv = T("#"+this.tc.treeId + "_treelist");
		function callback(data){
			if(!treeDiv)return;
			treeDiv.innerHTML = "";
			if("object"!=typeof(data)){T.loadTip(2,"暂无数据，请添加!<br />或者数据格式错误！",2,tc.ajaxTip||t.tc.treeObj);return};
			//data = {
			//	page:1,
			//	total:2000,
			//	rows:data
			//}
			if(data.page){
				if(data.total==0){
					T.loadTip(2,"暂无数据，请添加!",2,tc.ajaxTip||t.tc.treeObj);
					return
				};
				tc.recordtotal = parseInt(data.total);
				if(tc.cutpage&&tc.recordtotal>tc.perpage){//服务器返回总数大于每页显示数时才显示分页
					if(!T("#"+ tc.treeId + "_page_cut")){
						t.makepageUI();
						treeDiv.style.height = parseInt(treeDiv.style.height) - 27 + "px";
					};
					var totpage = Math.ceil(parseInt(data.total)/parseInt(tc.perpage));
					t.cutPage(parseInt(data.page),totpage,parseInt(data.total));
				}else{
					if(T("#"+ tc.treeId + "_page_cut")){
						treeDiv.style.height = parseInt(treeDiv.style.height) + 27 + "px";
						T("#"+ tc.treeId + "_page_cut").parentNode.removeChild(T("#"+ tc.treeId + "_page_cut"));
					};
				};
				
				data = data.rows;
			};
			
			t.treeData = data;
			t.orginData = data;
			
			treeDiv.innerHTML = t.I(isFresh);
			if(isFresh&&t.currentPath){t.focus(t.currentPath,false,false,false,true)}//刷新后定位到原位置
			tc.loadfun&&!isFresh?setTimeout(t.tc.treeId+".tc.loadfun()",100):"";//加载完后执行函数
		};
		if(tc.dataType==1){
			var lurl = tc.dataUrl;
			var param = tc.searchparam?tc.param+"&"+tc.searchparam:tc.param;
			var _total = tc.recordtotal!=0?"&tqpagetotal="+tc.recordtotal:"";
			if(tc.method.toUpperCase()=="POST"){
				T.A.sendData(lurl,"POST",param+"&pg="+tc.perpage+"&cp="+tc.cpage+_total,callback,2,tc.ajaxTip||tc.treeObj,"","加载中...",true);
			}else{
				lurl = lurl.indexOf("?")!=-1?lurl+"&":lurl+"?";
				T.A.sendData(lurl+param+"&pg="+tc.perpage+"&cp="+tc.cpage+_total,"GET","",callback,2,tc.ajaxTip||tc.treeObj,"","加载中...",true);
			}
		};
		if(tc.dataType==0){
			callback(tc.localData)
		};
	},
	/**********************************
	 初始化(Init)
	**********************************/
	I: function(isFresh) {
		var t = this;
		this.relaFormat();
		isFresh?"":this.setIconPath(this.tc.Path);
		this.loadChildItem("0");
		var rootChild = this.nodes["0"].childNodes;
		var str = "<a id='"+ this.tc.treeId +"_root' href='javascript:void(0)' style='display: none'></a>";

		if(rootChild.length>0){
			this.nodes["0"].hasChild = true;
			for(var i=0; i<rootChild.length; i++)str += this.node2html(rootChild[i], i==rootChild.length-1);
			setTimeout(this.tc.treeId +".expand('"+ rootChild[0].nodeId +"', true); "+ 
			//this.tc.treeId +".focusStyle('"+ rootChild[0].nodeId +"'); "+ this.tc.treeId +".rootIsEmpty()",10);
			this.tc.treeId +".focusStyle(); "+ this.tc.treeId +".rootIsEmpty()",10);
			//this.tc.treeId +".rootIsEmpty()",10);
		}else{
			return "没有数据";
		};
		
		this.tc.keyBoard?T("doc").aevt("keydown",t.keydown):"";//键盘操作
		
		return "<div class='tqtreeview' "+
		"onclick='"+ this.tc.treeId +".clickFun(event)' "+
		"ondblclick='"+ this.tc.treeId +".dblClickFun(event)' "+
		">"+ str +"</div>";
	},
	/**
	构造分页栏
	**/
	makepageUI : function(){
		var TNM = this.tc.treeId;
		var tc =  this.tc;
		var treepageDiv = document.createElement("div");
		treepageDiv.id = tc.treeId + "_page_cut";
		treepageDiv.style.borderTop = "1px solid #ccc";
		var treepageDivc = [];
				treepageDivc.push("<TABLE style=\"border:0px;width:100%\" id=\""+TNM+"_footer\" class=\"t-pager\" cellSpacing=\"0\" cellPadding=\"0\">");
					treepageDivc.push("<TBODY>");
						treepageDivc.push("<TR>");
							treepageDivc.push("<TD style='width:100%'>");
								treepageDivc.push("<div class=\"t-pager-buttons\">");
									treepageDivc.push("<SPAN title=\"首页\" id=\""+TNM+"_pbutton_first\" class=\"button16_a  bg_green_hover border_green fl\" style='width:18px;'>");
										treepageDivc.push("&lt;&lt;");
									treepageDivc.push("<\/SPAN>");
									treepageDivc.push("<SPAN title=\"前一页\" id=\""+TNM+"_pbutton_prev\" class=\"button16_a  bg_green_hover border_green fl\" style='width:18px;'>");
										treepageDivc.push("&lt;");
									treepageDivc.push("<\/SPAN>");
//									treepageDivc.push("<SPAN class=\"t-pager-index fl\">");
//										treepageDivc.push("<INPUT id=\""+TNM+"_pinput_curr\" class=\"t-pager-num fl\" style='width:20px' value=\"0\" type=\"text\" title=\"输入页码,按回车键跳转\">");
//										treepageDivc.push("<SPAN id=\""+TNM+"_pinput_pages\" class=\"t-pager-pages\">\/0<\/SPAN>");
//									treepageDivc.push("<\/SPAN>");
									treepageDivc.push("<SPAN title=\"后一页\" id=\""+TNM+"_pinput_next\" class=\"button16_a  bg_green_hover border_green fl\" style='width:18px;'>");
										treepageDivc.push("&gt;");
									treepageDivc.push("<\/SPAN>");
									treepageDivc.push("<SPAN title=\"末页\" id=\""+TNM+"_pinput_last\" class=\"button16_a  bg_green_hover border_green fl\" style='width:18px;'>");
										treepageDivc.push("&gt;&gt;");
									treepageDivc.push("<\/SPAN>");
								treepageDivc.push("<\/div>");
								
							treepageDivc.push("<div style=\"float:right;padding:2px 5px 0 0\">");
								treepageDivc.push("<span class=\"icon16 icon16tip fl\"></span><span id=\""+TNM+"_pinput_pages\"></span>");
							treepageDivc.push("<\/div>");
								
//							treepageDivc.push("<div style=\"float:right;padding:2px 5px 0 0\">");
//							//treepageDivc.push("总<span id=\""+TNM+"_pages_total\">0</span>条&nbsp;&nbsp;每页");
//								treepageDivc.push("<select id=\""+TNM+"_pages_select\">");
//								for(var p=0;p<tc.pagetype.length;p++){
//									treepageDivc.push("<option value="+tc.pagetype[p]+"");
//									tc.pagetype[p] == tc.perpage?treepageDivc.push(" selected=\"selected\""):"";
//									treepageDivc.push(">"+tc.pagetype[p]+"<\/option>");
//								};
//								treepageDivc.push("<\/select>");
//							//treepageDivc.push("条");
//							treepageDivc.push("<\/div>");
							treepageDivc.push("<\/TD>");
						treepageDivc.push("<\/TR>");
					treepageDivc.push("<\/TBODY>");
				treepageDivc.push("<\/TABLE>");
			treepageDiv.innerHTML = treepageDivc.join("");
			this.tc.treeObj.appendChild(treepageDiv);
			treepageDivc = null
	
	},
	/**********************************
	 构建分页
	 cp:当前页码
	 tp:总页码
	 dt:总记录数
	**********************************/
	cutPage : function(cp,tp,dt){
		var thisT = this;
		var TNM = this.tc.treeId;
		var btnFirs = document.getElementById(TNM+"_pbutton_first");//首页
		var btnPrev = document.getElementById(TNM+"_pbutton_prev");//上一页
		var totPage = document.getElementById(TNM+"_pinput_pages");//当前页
		var btnNext = document.getElementById(TNM+"_pinput_next");//下一页
		var btnLast = document.getElementById(TNM+"_pinput_last");//尾页
		
		//首页按钮事件
		btnFirs.onclick = function(){
			if(cp<2){return};
			thisT.tc.cpage = 1;
			thisT.reloadData();
		};
		if(cp>1){
			btnFirs.title = "上翻到第一页";
			btnFirs.className = "button16_a bg_green_hover border_green fl";
		}else{
			btnFirs.title = "已到最前";
			btnFirs.className = "button16_a button_disable fl"
		};
		//上一页按钮事件
		btnPrev.onclick = function(){
			if(cp<2){return};
			thisT.tc.cpage--;
			thisT.reloadData();
		};
		if(cp>1){
			btnPrev.title = "上翻一页";
			btnPrev.className = "button16_a bg_green_hover border_green fl";
		}else{
			btnPrev.title = "已到最前";
			btnPrev.className = "button16_a button_disable fl"
		};
		//总页数显示
		totPage.parentNode.title = "当前第"+cp+"页,共"+dt+"条记录,"+tp+"页,每页"+thisT.tc.perpage+"条\n数据太多时,请使用搜索";
		totPage.innerHTML = cp;
		//下一页按钮事件
		btnNext.onclick = function(){
			if(cp>tp-1){return};
			thisT.tc.cpage++;
			thisT.reloadData();
		};
		if(cp<tp){
			btnNext.title = "下翻一页";
			btnNext.className = "button16_a bg_green_hover border_green fl";
		}else{
			btnNext.title = "已到最后";
			btnNext.className = "button16_a button_disable fl"
		};
		//最后一页按钮事件
		btnLast.onclick = function(){
			if(cp>tp-1){return};
			thisT.tc.cpage = tp;
			thisT.reloadData();
		};
		if(cp<tp){
			btnLast.title = "下翻到最后一页";
			btnLast.className = "button16_a bg_green_hover border_green fl"
		}else{
			btnLast.title = "已到最后";
			btnLast.className = "button16_a button_disable fl"
		};
		//销毁
		btnFirs = null;
		btnPrev = null;
		totPage = null;
		btnNext = null;
		btnLast = null;
	},
	/**********************************
	重载树
	**********************************/
	reloadData : function(p){
		var t = this;
		if(p){
			t.tcBind(p)
		};
		t.init();
		t.tc.keyBoard?T("doc").revt("keydown",t.keydown):"";//清除原先绑定的事件
		t.M();
	},
	/**********************************
	重载树,定位到原节点
	**********************************/
	refresh : function(p){
		var t = this;if(p){
			t.tcBind(p)
		};
		t.init();
		t.tc.keyBoard?T("doc").revt("keydown",t.keydown):"";//清除原先绑定的事件
		t.M(true);
	},
	/**********************************
	重载树,清除所有操作产生的数据
	**********************************/
	clearData : function(p){
		var t = this;
		if(p){
			t.tcBind(p)
		};
		t.tc.recordtotal = 0;//重置重载前记录总数
		t.tc.searchparam = '';//重置搜索条件
		t.tc.cpage = 1;
		t.init();
		t.tc.keyBoard?T("doc").revt("keydown",t.keydown):"";//清除原先绑定的事件
		t.M();
	},
	/**********************************
	 构建工具
	**********************************/
	createTool : function(){
		var t = this;
		var toolDiv = document.createElement("div");
		toolDiv.id = this.tc.treeId+"_treetool";
		toolDiv.className = "treetool";
		
		var expandSpan = document.createElement("span");
		var shrinkSpan = document.createElement("span");
		expandSpan.className = "button16_a border_blank hover1 fl";
		expandSpan.style.marginLeft = "2px";
		expandSpan.innerHTML = "+展开全部";
		
		shrinkSpan.className = "button16_a border_blank hover1 fl";
		shrinkSpan.style.marginLeft = "5px";
		shrinkSpan.innerHTML = "-收缩全部";
		
		toolDiv.appendChild(expandSpan);
		toolDiv.appendChild(shrinkSpan);
		
		expandSpan.onclick = function(){
			T.cancelBub();
			t.expandAll();
		}
		shrinkSpan.onclick = function(){
			T.cancelBub();
			t.shrinkAll();
		}
		this.tc.treeObj.appendChild(toolDiv);

	},
	/**********************************
	 构建搜索
	**********************************/
	createSearch : function(resultH){
		var searchDiv = document.createElement("div");
		var seearchUI = this.tc.seearchUI||this.tc.treeObj;
		searchDiv.className = "treesearch";
		var queryhtml =  "<div id=\"querypanel\" class=\"titlepanel\">";
        queryhtml += " <input type=\"text\" id=\""+this.tc.treeId+"_querytext\" class=\"querytext\" value=\"输入关键字,按回车键搜索\" onfocus=\"if(this.value=='输入关键字,按回车键搜索'){this.value=''}\" onblur=\"if(this.value==''){this.value='输入关键字,按回车键搜索'}\" nosub=\"true\"";	
        if(T.ie){
        	 queryhtml += " onkeydown=\""+this.tc.treeId+".searchKeyDown()\" ";	  
        }else{
        	 queryhtml += " onkeypress=\"javascript:"+this.tc.treeId+".searchKeyDown(event)\" ";	  
        }
        queryhtml += " >";	
        queryhtml += " <a href=\"javascript:void(0);\" id=\""+this.tc.treeId+"_btnQuery\" onclick=\""+this.tc.treeId+".btnQuery();\">";
        queryhtml += " <span class=\"uquery\" title=\"查找\">&nbsp;<\/span>";
        queryhtml += " <\/a> ";
        queryhtml += " <a href=\"javascript:void(0);\" id=\""+this.tc.treeId+"_btnClearQuery\" style=\"display:none;\" onclick=\""+this.tc.treeId+".btnClearQuery();\">";
        queryhtml += " <span class=\"qdelete\" title=\"取消查找\" >&nbsp;<\/span>";
        queryhtml += " <\/a>";
        queryhtml +=  " <\/div>";
        
		searchDiv.innerHTML = queryhtml;
		seearchUI.appendChild(searchDiv);
        var searchList = document.createElement("div");
        searchList.innerHTML = "<br />searchList<br />";
        searchList.style.display = "none";
        searchList.style.overflow = "auto";
        searchList.style.height = resultH + "px";
        searchList.id = this.tc.treeId + "_searchList";
		this.tc.treeObj.appendChild(searchList);
	},
	/**********************************
	 搜索按钮事件
	**********************************/
	btnQuery : function(){
		T.cancelBub();
		var querytext = T("#"+this.tc.treeId+"_querytext");
		var btn = T("#"+this.tc.treeId+"_btnQuery");
		var Cbtn = T("#"+this.tc.treeId+"_btnClearQuery");
		var Tlist = T("#"+this.tc.treeId+"_treelist");
		var Slist = T("#"+this.tc.treeId+"_searchList");

		//根据搜索内容构造数据
		var searchV = T.trim(querytext.value);
		if(searchV==""||searchV=="输入关键字,按回车键搜索"){
			querytext.value = "";
			querytext.focus();
			T.loadTip(1,"输入关键字,按回车键搜索",1,this.tc.treeObj)
			return
		};
		
		if(this.tc.recordtotal>this.tc.perpage||this.tc.ajaxSearch){//如果服务器返回的记录数大于每页列出数,从服务器搜索
			this.reloadData({
				cpage:1,
				recordtotal:0,
				searchparam:"searchvalue="+searchV
			});
			btn.style.display = "none";
			Cbtn.style.display = "block";
			return;
		};
		
		var result = this.searchByName(searchV,true);//this.searchByName(searchV,false);
		if(result){
			btn.style.display = "none";
			Cbtn.style.display = "block";
			Tlist.style.display = "none";
			Slist.style.display = "block";
			this.tc.isTool?T("#"+this.tc.treeId+"_treetool").style.display = "none":"";
			
			var _nhtml = "<span style='font-style:Oblique;color:#090;padding-bottom:4px;width:100%;float:left;'>查找结果:</span>";
			for(var i in result){
				_nhtml += "<span style='float:left;width:100%;height:20px;overflow:hidden;white-space:nowrap;'>";
				if(this.tc.isCheck&&result[i].checktype){
					var ckicon = (result[i].checktype+result[i].checked).toUpperCase();
					_nhtml += "<img style='float:left;cursor:pointer' src='"+this.icons[ckicon].src+"' ";
					_nhtml += " ckicon='"+ckicon+"'";
					_nhtml += " id='"+this.tc.treeId+"_"+result[i].id+"_search'";
					_nhtml += " onclick=\"return "+ this.tc.treeId +".searchCheck(this,'"+ result[i].sourcePath +"')\" ";
					_nhtml += " />";
				};
					_nhtml += "<span style='white-space:nowrap;overflow:hidden;display:block;line-height:20px;height:20px;margin-left:1px;cursor:pointer'";
					_nhtml += " title='"+result[i].name+"'";
					_nhtml += " onmouseover=\"this.style.background='url(images/commonbgs.gif) repeat-x 0 0px'; \"";
					_nhtml += " onmouseout=\"this.style.background='none';this.style.border='none' \"";
					//_nhtml += " onclick=\"return "+ this.tc.treeId +".nodeClick('"+ result[i].nodeId +"')\" >";
					_nhtml += " onclick=\"return "+ this.tc.treeId +".focusNode('"+ result[i].sourcePath +"',true)\" >";
					_nhtml += result[i].name;
					_nhtml += "</span>"
				_nhtml += "</span>"
			}
			Slist.innerHTML = _nhtml;
			
		}else{
			T.loadTip(1,"无匹配结果。",2,this.tc.treeObj)
		}
		querytext.focus();
	},
	/**********************************
	 取消按钮事件
	**********************************/
	btnClearQuery : function(){
		T.cancelBub();
		var querytext = T("#"+this.tc.treeId+"_querytext");
		var btn = T("#"+this.tc.treeId+"_btnQuery");
		var Cbtn = T("#"+this.tc.treeId+"_btnClearQuery");
		var Tlist = T("#"+this.tc.treeId+"_treelist");
		var Slist = T("#"+this.tc.treeId+"_searchList");
		
		querytext.value = "";
		
		if(this.tc.recordtotal>this.tc.perpage||this.tc.ajaxSearch){//配合从服务器搜索,再取消搜索也从服务器返回初始结果集
			this.clearData();
			btn.style.display = "block";
			Cbtn.style.display = "none";
			return;
		};
		
		Slist.innerHTML = "";
		querytext.focus();
		btn.style.display = "block";
		Cbtn.style.display = "none";
		Tlist.style.display = "block";
		Slist.style.display = "none";
		this.tc.isTool?T("#"+this.tc.treeId+"_treetool").style.display = "block":"";
	},
	/**********************************
	 清空搜索列表（本地搜索时）
	**********************************/
	ClearSearchList : function(){
		var querytext = T("#"+this.tc.treeId+"_querytext");
		var btn = T("#"+this.tc.treeId+"_btnQuery");
		var Cbtn = T("#"+this.tc.treeId+"_btnClearQuery");
		var Tlist = T("#"+this.tc.treeId+"_treelist");
		var Slist = T("#"+this.tc.treeId+"_searchList");
		
		querytext.value = "";
		Slist.innerHTML = "";
		querytext.focus();
		
		btn.style.display = "block";
		Cbtn.style.display = "none";
		Tlist.style.display = "block";
		Slist.style.display = "none";
		this.tc.isTool?T("#"+this.tc.treeId+"_treetool").style.display = "block":"";
	},
	/**********************************
	 搜索结果项checkbox/radio事件
	**********************************/
	searchCheck : function(ckobj,sourcePath){
		this.setCheckByNode(sourcePath);
		var _node = this.getNode(sourcePath);
		var ckicon = (_node.checktype+_node.checked).toUpperCase();
		ckobj.src = this.icons[ckicon].src;
		ckobj.setAttribute("ckicon",ckicon);
	},
	/**********************************
	 搜索框鼠标事件
	**********************************/
	searchKeyDown : function(e){
		var querytext = T("#"+this.tc.treeId+"_querytext");
		var e = e || window.event;
		var key = e.keyCode||e.which;
		if (key.toString() == "13") {
			if(T.trim(querytext.value)==""){
				querytext.focus();
				this.btnClearQuery();
			}else{
				this.btnQuery();
			}
		};
	},
	/**********************************
	 构建父子关系集合(父子节点组合字符串 root_1)
	**********************************/
	relaFormat : function(){
		var a = new Array();
		for (var id in this.treeData) a[a.length] = id;
		this.relation = a.join(this._d + this._d);
		this.totalNode = a.length; a = null;
	},
	/**********************************
	 根节点无子节点处理
	**********************************/
	rootIsEmpty : function(){
		var rootChild = this.nodes["0"].childNodes;
		for(var i=0; i<rootChild.length; i++){
			if(!rootChild[i].isLoad) this.expand(rootChild[i].nodeId);
			if (rootChild[i].name=="")
			{
				var node = rootChild[i].childNodes[0], HasChild  = node.hasChild;
				node.iconExpand  =  rootChild[i].childNodes.length>1 ? HasChild ? "PM0" : "L0" : HasChild ? "PM3" : "L3"
				T("#"+this.tc.treeId +"_expand_"+ node.nodeId).src = this.icons[node.iconExpand].src;
			}
		}
	},
	/**********************************
	加载子节点对象
	@string nodeId  节点唯一标识
	@boolean isUpdate  是否是更新,更新则保留原index
	**********************************/
	loadChildItem : function(nodeId ,isUpdate){
		var node = this.nodes[nodeId], d = this.divider, _d = this._d;
		var sid = node.relationIndex.substr(node.relationIndex.indexOf(d) + d.length);
		var reg = new RegExp("(^|"+_d+")"+ sid +d+"[^"+_d+d +"]+("+_d+"|$)", "g");
		var cns = this.relation.match(reg), tcn = this.nodes[nodeId].childNodes;
		if (cns){
			reg = new RegExp(_d, "g"); 
			
//			for (var i=0; i<cns.length; i++)
//				tcn[tcn.length] = this.nodeInit(cns[i].replace(reg, ""), nodeId);
/*			for (var i=0; i<cns.length; i++){
				var relationIndexI = cns[i].replace(reg, "");
				var sourcePath = this.setPath(relationIndexI, nodeId);
				var childIndex = isUpdate?(this.getNode(sourcePath)?this.getNode(sourcePath).nodeId:false):false;
				tcn[tcn.length] = this.nodeInit(relationIndexI, nodeId, sourcePath, childIndex);
			}
*/		
			for (var i=0; i<cns.length; i++){
				var relationIndexI = cns[i].replace(reg, "");
				//var _sourcePath = node.sourcePath+"_"//this.setPath(relationIndexI, nodeId);
				tcn[tcn.length] = this.nodeInit(relationIndexI, nodeId, isUpdate);
			}

		};
		node.isLoad = true;
	},
	/**********************************
	根据 this.treeData 生成节点所有属性
	@string relationIndex 父子节点组合字符串 root_1
	@string parentId  当前树节点的父节点index
	@boolean isUpdate  是否是更新,更新则保留原index
	**********************************/
	nodeInit : function(relationIndex, parentId, isUpdate){
		var source = this.treeData[relationIndex], d = this.divider;
		var name  = source["name"];
		var title  = source["title"];
		var sid   = relationIndex.substr(relationIndex.indexOf(d) + d.length);
		var sourcePath = this.nodes[parentId].sourcePath+"_"+sid;
		
		var nodeId = isUpdate?(this.getNode(sourcePath)?this.getNode(sourcePath).nodeId:false):false;
		if(!nodeId){
			this.index++
			nodeId = this.index
		};
		
		var scheckType = source["cktp"]==false?false:(source["cktp"]?source["cktp"]:this.tc.checkType);
		var checkType = scheckType == "radio"?"r":(scheckType == "checkbox"?"c":"");
		var checked = source["ckd"]?"1":"0";
		this.nodes[nodeId] = T.extend(
		{//节点数据源参数不能下列参数名相同(除title外)
			"nodeId"    : nodeId,//树索引,由js自动生成
			"title"  : title ? title : name,//鼠标tip提示
			"sourcePath" : sourcePath,//树中唯一标识之一,节点完整路径,由父子数据源id组成root_1_2_3_4
			"path"  : this.nodes[parentId].path + d + nodeId,//节点路径由父子树中唯一nodeId组成root_1_2_3_4
			"isLoad": false,//对应子节点是否已加载
			"isExpand": false,//节点是否已展开
			"parentId": parentId,//父节点ID（树中唯一ID）
			"parentNode": this.nodes[parentId],//父节点对象
			"hasChild":   false,//是否有子节点
			"childNodes": null,//子节点对象
			"relationIndex" : relationIndex,//父子关系,root_1
			"childPrepose" : "",//节点前置图标类型辅助参数
			"checktype" : checkType,//节点选择类型,r(radio)/c(checkbox)
			"checked"   : checked//节点选中状态,0:未选中,1:已选中,2:半选中
		},source);
//		this.nodes[nodeId].nodeId = nodeId;
//		this.nodes[nodeId].title =  title ? title : name;
//		this.nodes[nodeId].path =  this.nodes[parentId].path + d + nodeId;
//		this.nodes[nodeId].isLoad =  false;
//		this.nodes[nodeId].isExpand =  false;
//		this.nodes[nodeId].parentId =  parentId;
//		this.nodes[nodeId].parentNode =  this.nodes[parentId];
//		this.nodes[nodeId].hasChild =  false;
//		this.nodes[nodeId].childNodes =  [];
//		this.nodes[nodeId].relationIndex =  relationIndex;
//		this.nodes[nodeId].childPrepose =  "";
//		this.nodes[nodeId].checktype =  checkType;
//		this.nodes[nodeId].checked =  checked;
		
		this.nodes[nodeId].hasChild = this.relation.indexOf(this._d + sid + d)>-1;
		if(this.nodes[nodeId].hasChild)this.nodes[nodeId].childNodes = [];
		return this.nodes[nodeId];
	},
	/**********************************
	根据节点属性生成html
	@object node 节点对象
	@boolean isLast  是否为父节点下的最后一项
	@boolean isSelf  是否只生成本节点HTML(不包括子节点)
	**********************************/
	node2html : function(node, isLast,isSelf){
		var nodeId   = node.nodeId;
		node.icon = node.type?node.type:node.icon;//增加按type来区分图标,兼容IVR中的设置,这个不好，以后优化
		var HasChild  = node.hasChild, isRoot = node.parentId=="0";
		if(isRoot && node.icon=="") node.icon = "root";
		if(node.icon=="" || typeof(this.icons[node.icon])=="undefined")
			node.icon = HasChild ? "folder" : "file";
		node.iconExpand  = isLast ? "└" : "├";
		
		var html = "";
		if(!isSelf){
			html = "<div nowrap='True' class='nodeWord' nodeId='"+ nodeId +"' title='"+ node.title +"' "
			if(this.tc.nodeExt){
				html += "onmouseover=\"T(\'#"+ this.tc.treeId +"_\'+this.getAttribute(\'nodeId\')+\'_exticon_span\').style.visibility = 'visible'\" " +
						"onmouseout=\"T(\'#"+ this.tc.treeId +"_\'+this.getAttribute(\'nodeId\')+\'_exticon_span\').style.visibility = 'hidden'\"" 
			}
			html += "><nobr>"
		};
		if(!isRoot){
			node.childPrepose = node.parentNode.childPrepose + (isLast ? "　" : "│");
			node.iconExpand  = HasChild ? isLast ? "PM2" : "PM1" : isLast ? "L2" : "L1";
			html += "<span>"+ this.symbol2image(node.parentNode.childPrepose) +"<img "+
			"align='absmiddle' id='"+ this.tc.treeId +"_expand_"+ nodeId +"' "+
			"src='"+ this.icons[node.iconExpand].src +"' style='cursor: "+ (!node.hasChild ? "":
			(T.iev<7? "hand" : "pointer")) +"'></span>";
		};
		html += "<img "+
			"align='absMiddle' "+
			"id='"+ this.tc.treeId +"_icon_"+ nodeId +"' "+
			"src='"+ this.icons[node.icon].src +"' />";
		if (this.tc.isCheck&&node.checktype){
			var ckicon = (node.checktype+node.checked).toUpperCase();
			html += "<img border='0' style='cursor:pointer' ";
			switch(node.checktype){
				case "c" :
				html += "id='" + this.tc.treeId + "_checkbox_" + nodeId + "' "+
				"src='" + this.icons[ckicon].src + "' />";
				break;
				case "r" :
				html += "id='" + this.tc.treeId + "_radio_" + nodeId + "' "+
				"src='" + this.icons[ckicon].src + "' />";
				break
			};
		};

		html +=	"<a class='tqtreeview' hideFocus "+
			"id='"+ this.tc.treeId +"_link_"+ nodeId +"' ";
			
		html +=	T.iev<8?"href='#' ":"href='javascript:void(0)' ";
		html +=	"title='"+ node.title +"' ";
			node.hasChild?html +=	" style='font-weight:700' ":" ";
			html +=	
			//"onfocus=\""+ this.tc.treeId +".focusLink('"+ nodeId +"')\" "+
			//"onclick=\"return "+ this.tc.treeId +".nodeClick('"+ nodeId +"')\" "+ 
			" >"+(node.name.length>20?node.name.substring(0,20)+"..":node.name) +
			"</a></nobr>";
			
		//附加提示信息
			
		html +=" <span id='"+ this.tc.treeId +"_tip_"+ nodeId +"' "+
			"style='color:#c00;font-weight:700 '>"+(node.tip?node.tip:"")+"</span>";//"+(node.tip?"display:block":"display:none")+"//<img border='0' align='absmiddle' src='"+this.icons["wait"].src+"'>
		
		//附加按钮
		if(this.tc.nodeExt){
			html += "<span id='"+ this.tc.treeId +"_"+nodeId+"_exticon_span' class='abs'  style='display:block;right:5px;top:0;visibility:hidden'>"
			var _nE = this.tc.nodeExt;
			T.each(this.tc.nodeExt,function(item,ii){
				if(item.condition){
					var _iC = item.condition;
					var kk =_iC.length;
					var resNum = 0;
					for(var jj=0;jj<kk;jj++){
						if(_iC[jj].value == node[_iC[jj].param]){
							resNum++
						}
					};
					if(resNum == kk){
						html += "<span class='button16_a hover1 border_blank fl' extfn='yes' nodeId='"+nodeId+"' type='"+item.type+"' title='"+(item.tit||item.name)+"'><span class='"+item.iconcls+" fl' extfn='yes' nodeId='"+nodeId+"' type='"+item.type+"' title='"+(item.tit||item.name)+"'></span>"+item.name+"</span>";
					}
				}else{
					html += "<span class='button16_a hover1 border_blank fl' extfn='yes' nodeId='"+nodeId+"' type='"+item.type+"' title='"+(item.tit||item.name)+"'><span class='"+item.iconcls+" fl' extfn='yes' nodeId='"+nodeId+"' type='"+item.type+"' title='"+(item.tit||item.name)+"'></span>"+item.name+"</span>";
				}
				item = null
			})
			html += "</span>"
		};
			
		isSelf?"":html += "</div>";
		if(isRoot && node.name=="") html = "";
		if(!isSelf){
			html = "\r\n<span id='"+ this.tc.treeId +"_tree_"+ nodeId +"' sourcepath='" + node.sourcePath + "'>"+ html 
			html +="<span style='display: none'></span></span>";
		};
		return html;
	},
	/**********************************
	设置节点前置多层级图标
	@string type 图标类型
	**********************************/
	symbol2image : function(type){
		var str = "";
		for(var i=0; i<type.length; i++){
			var img = "";
			switch (type.charAt(i))
			{
				case "│" : img = "L4"; break;
				case "└" : img = "L2"; break;
				case "　" : img = "empty"; break;
				case "├" : img = "L1"; break;
				case "─" : img = "L3"; break;
				case "┌" : img = "L0"; break;
			}
			if(img!="")
				str += "<img align='absMiddle' src='"+ this.icons[img].src +"' height='20'>";
		}
		return str;
	},
	/**********************************
	将所属子节点转成html
	@string nodeId  节点唯一标识
	**********************************/
	buildChild : function(nodeId){
		if(this.nodes[nodeId].hasChild){
			var tcn = this.nodes[nodeId].childNodes, str = "";
			for (var i=0,k=tcn.length; i<k; i++){
				if(T("#"+this.tc.treeId + "_tree_" + tcn[i].nodeId))continue;
				str += this.node2html(tcn[i], i==tcn.length-1);
			}
			var temp = T("#"+this.tc.treeId +"_tree_"+ nodeId).childNodes;
			temp[temp.length-1].innerHTML = str;
		}
	},
	/**********************************
	定位节点样式
	@string nodeId  节点唯一标识
	**********************************/
	focusStyle : function(nodeId){
		if(!this.currentNode){
			this.currentNode = this.nodes["0"];
			this.currentPath = this.currentNode.sourcePath
		};
		if(!nodeId){return};
		//getNodeObj
		var a = T("#"+this.tc.treeId +"_link_"+ nodeId);
		//T.gpos(a).top)
		if(a){
			a.focus();
			var link = T("#"+this.tc.treeId +"_link_"+ this.currentNode.nodeId);
			if(link)with(link.style){color="";backgroundColor="";border="";}
			with(a.style){
				color = this.colors.highLightText;
				backgroundColor = this.colors.highLight;
				border = this.colors.border;
			};
			this.currentNode = this.nodes[nodeId];
			this.currentPath = this.currentNode.sourcePath;
		};

//		if(T("#"+this.tc.treeId + "_treelist").style.display == "none")return;
//		var _curNodeTop = T.gpos(T("#"+this.tc.treeId +"_tree_"+ nodeId)).top + T.gpos(T("#"+this.tc.treeId + "_treelist")).top;
//		var _treeObjHeight = T.gpos(T("#"+this.tc.treeId + "_treelist")).height;
//		if(_curNodeTop > _treeObjHeight){
//			T("#"+this.tc.treeId + "_treelist").scrollTop = _curNodeTop - _treeObjHeight;
//		};
	},
	/**********************************
	节点获得焦点
	@string nodeId  节点唯一标识
	**********************************/
	focusLink : function(nodeId){
		if(this.currentNode && this.currentNode.nodeId==nodeId) return;
		this.focusStyle(nodeId);
	},
	/**********************************
	展开节点
	@string nodeId  节点唯一标识
	@boolean onlyExpand  仅展开
	@boolean onlyShrink  仅收缩
	**********************************/
	expand : function(nodeId, onlyExpand,onlyShrink){
		var node  = this.nodes[nodeId];
		if (onlyExpand && node.isExpand) return;
		if (onlyShrink && !node.isExpand) return;

		if (!node.hasChild) return;
		var area  = T("#"+this.tc.treeId +"_tree_"+ nodeId);
		if (area) area = area.childNodes[area.childNodes.length-1];
		if (area){
			var icon  = this.icons[node.icon];
			var iconE = this.iconsExpand[node.icon];
			var Bool  = node.isExpand = onlyExpand || area.style.display == "none";
			var img   = T("#"+this.tc.treeId +"_icon_"+ nodeId);
			if (img)  img.src = !Bool ? icon.src :typeof(iconE)=="undefined" ? icon.src : iconE.src;
			var exp   = this.icons[node.iconExpand];
			var expE  = this.iconsExpand[node.iconExpand];
			var expand;
			if (expand = T("#"+this.tc.treeId +"_expand_"+ nodeId)){
				expand.src = !Bool ? exp.src : typeof(expE) =="undefined" ? exp.src  : expE.src;
			}
			/*
			//节点收缩时,定位到父节点(不好用)
			if(!Bool && this.currentNode.path.indexOf(node.path)==0 && this.currentNode.nodeId!=nodeId){
				try{T("#"+this.tc.treeId +"_link_"+ nodeId).click();}
				catch(e){this.focusStyle(nodeId);}
			}
			*/
			area.style.display = !Bool ? "none" : "block";
			if(!node.isLoad){
				this.loadChildItem(nodeId);
				if(node.nodeId=="0") return;
				//子节点过多时, 显示正在加载
				if(node.hasChild && node.childNodes.length>200){
					setTimeout(this.tc.treeId +".buildChild('"+ nodeId +"')", 1);
					var temp = T("#"+this.tc.treeId +"_tree_"+ nodeId).childNodes;
					temp[temp.length-1].innerHTML = "<div nowrap><nobr><span>"+ 
					this.symbol2image(node.childPrepose +"└") +"</span>"+
					"<img border='0' align='absmiddle' src='"+this.icons["wait"].src+"'>"+
					"<A style='font-size: 9pt'>加载中,请稍候...</A></nobr></div>";
				}else{
					this.buildChild(nodeId);
				}
			};
			this.setChildCheck(node,this.tc.expandExeChkF);//设置选中状态
			if(this.tc.expandFun){this.tc.expandFun(node)};
		}
	},
	/**********************************
	处理自定义参数nodeFnArgs
	@Object node  节点对象
	**********************************/
	nodeFnArgs : function(node){
		if(!this.tc.nodeFnArgs){return false};
		var nP = this.tc.nodeFnArgs.split(",");
		var nPvalue = [];
		if(nP){
			T.each(nP,function(o,j){
				nPvalue.push(node[o]);
			});
		};
		return nPvalue;
	},
	/**********************************
	节点单击事件,事件参数可自定义(自定义参数名不能与系统保留名相同[除title外],详见nodeInit函数)
	@string nodeId  节点唯一标识
	**********************************/
	nodeClick : function(nodeId){
		if(!nodeId)return false;
		var node = this.nodes[nodeId];
		var fn = node.fn;
		var nC = this.tc.nodeClick;
		if(!(!fn&&!nC)){
			var nPvalue = this.nodeFnArgs(node);
			if(fn){
				fn.apply(this,nPvalue);
			}else if(nC){
				nC.apply(this,nPvalue);
			};
		};
		//this.focusStyle(node.relationIndex);
		return false;
	}
	,
	/**********************************
	获取某节点数据路径(有循环节点时不能确保唯一)
	@string sourceId 数据源节点id
	**********************************/
	getPath: function(sourceId){
		var _d = this._d, d = this.divider;
		var A = new Array(), sidItem=sourceId; A[0] = sidItem;
		while(sidItem!="root" && sidItem!=""){
			var str = "(^|"+_d+")([^"+_d+d+"]+"+d+ sidItem +")("+_d+"|$)";
			if (new RegExp(str).test(this.relation)){
				sidItem = RegExp.$2.substring(0, RegExp.$2.indexOf(d));
				if(T.AindexOf(A,sidItem)>-1) break;
				A[A.length] = sidItem;
			}else break;
		}
		return A.reverse();
	},
	/**********************************
	获取某节点数据唯一路径(确保唯一)
	@string relationIndex 数据源节点索引root_1
	@string parentId 数据源节点对应树中父节点唯一nodeId
	**********************************/
//	setPath : function(relationIndex, parentId){
//		var path=[],d=this.divider;
//		while(parentId!="0"/*&&typeof(parentId)!="undefined"*/){
//			path.push(relationIndex.split(d)[1])
//			relationIndex = this.nodes[parentId].relationIndex
//			parentId = this.nodes[parentId].parentId
//			this.setPath(relationIndex, parentId)
//		};
//		if(parentId=="0"){
//			path.push(relationIndex.split(d)[1])
//			path.push(relationIndex.split(d)[0])
//		};
//		return path.reverse().join("_")
//	},
	/**********************************
	获取节点对象
	@string sourceFlag  数据源sourcePath或者sourceId
	**********************************/
	getNode : function(sourceFlag){
		var nodes = this.nodes,node = false,d = this.divider;
		if(sourceFlag.toString().indexOf("_")==-1&&sourceFlag!="root"){
			for(var i in nodes){
				if(nodes[i].relationIndex.split(d)[1] == sourceFlag){
					node = nodes[i];
					break
				};
			};
		}else{
			for(var i in nodes){
				if(nodes[i].sourcePath == sourceFlag){
					node = nodes[i];
					break
				};
			};
		}
		return node
	},
	/**********************************
	根据sourcePath获取relationIndex
	@string path 节点sourcePath
	**********************************/
	getRelationByPath : function(path){
		var d = this.divider;
		path = path.split(d);
		path1= path[path.length-2]
		path2= path[path.length-1]
		return path1 + d + path2
	},
	/**********************************
	根据relationIndex获取已加载的节点路径列表
	@string relationIndex 节点索引relationIndex(root_1)
	**********************************/
	getPathList : function(relationIndex){
		var allNode = this.nodes,rL = relationIndex.length;ret=[];
		for ( var i in allNode){
			var indexof = allNode[i].sourcePath.indexOf(relationIndex);
			if(indexof>-1&&allNode[i].sourcePath.length == indexof+rL ){
				ret.push(allNode[i].sourcePath)
			}
		};
		return ret
	},
	/**********************************
	选中节点
	@string sourceId 数据源节点id(对应服务器返回的Id),pathMode下则为(root_1_2)
	@boolean onlyExpand  是否展开定位节点
	@boolean onlyShrink  是否收缩定位节点
	@boolean pathMode  是否按节点路径进行定位,若true,则sourceId传入节点对应路径(root_1_2),根节点path为"0"
	**********************************/
	focus : function(sourceId, defer, onlyExpand, onlyShrink ,pathMode){
		if (!defer){
			setTimeout(this.tc.treeId +".focus('"+ sourceId +"', true ,"+ onlyExpand +", "+ onlyShrink +" ,"+ pathMode +")", 100);
			return;
		}
		var path;
		if(pathMode){
			path = sourceId.split("_");
		}else{
			path = this.getPath(sourceId);
		};
		if(path[0]!="root"){
			T.loadTip(2,"<div style=\"width:100%;text-align:left\">初始化定位节点失败。<br />原因1：数据源id "+ sourceId +" 不存在。<br />原因2：数据id没有正确设置关联路径<br />"+
			"id"+sourceId+"的路径为 = "+ path.join(this.divider)+"<br />正确路径首节点id应为root,如:root_1_2</div>",10,this.tc.treeObj);
			return false;
		};
		var root = this.nodes["0"], len = path.length,focusExec = this.tc.focusExec;
		for(var i=1; i<len; i++){
			if(root.hasChild){
				var relationIndex = path[i-1] + this.divider + path[i];
				for (var k=0; k<root.childNodes.length; k++){
					if (root.childNodes[k].relationIndex == relationIndex){
						root = root.childNodes[k];
						if(i<len - 1){
							this.expand(root.nodeId, true);
						}else{
							focusExec?this.nodeClick(root.nodeId):"";//触发点击事件
							onlyExpand?this.expand(root.nodeId,true):"";
							onlyShrink?this.expand(root.nodeId,"", true):"";
							this.focusStyle(root.nodeId);
						};
						break;
					}
				}
			}
		};
		return true
	},
	/**********************************
	节点单击事件(全局定义)
	**********************************/
	clickFun : function(e){
		e = window.event || e; e = e.srcElement || e.target;
		switch(e.tagName.toUpperCase()){
			case "IMG" :
			if(e.id){
			//小图标暂不响应事件
			//        if(e.id.indexOf(this.tc.treeId +"_icon_")==0)
			//          this.focusStyle(e.id.substr(e.id.lastIndexOf("_") + 1));
				if (e.id.indexOf(this.tc.treeId +"_expand_")==0){
					this.expand(e.id.substr(e.id.lastIndexOf("_") + 1));
				}else if (e.id.indexOf(this.tc.treeId + "_checkbox_") == 0) {
					var _node = this.nodes[e.id.substr(e.id.lastIndexOf("_") + 1)];
					this.setCheck(_node);
				}else if (e.id.indexOf(this.tc.treeId + "_radio_") == 0) {
					var _node = this.nodes[e.id.substr(e.id.lastIndexOf("_") + 1)];
					this.setRadioCheck(_node);
				}
			}
			break;
			case "A" :
				if(e.id){
					this.focusStyle(e.id.substr(e.id.lastIndexOf("_") + 1));
					this.nodeClick(e.id.substr(e.id.lastIndexOf("_") + 1))
				};
				break;
			case "SPAN" :
				if(e.className=="pm")
				this.expand(e.id.substr(e.id.lastIndexOf("_") + 1));
				else if(e.getAttribute("extfn")=="yes"){
					if(this.tc.nodeExtFn){
						var curNode = this.nodes[e.getAttribute("nodeId")];
						var _nPvalue = this.nodeFnArgs(curNode);
						_nPvalue.push(e.getAttribute("type"));//最后一个加上自定义type
						this.tc.nodeExtFn.apply(this,_nPvalue);
					};
					//this.tc.nodeExtFn(e.getAttribute("nodeId"),e.getAttribute("type"))
				}
				break;
			case "DIV" :
				if(e.className="nodeWord"){
					T.cancelBub();
					this.focusLink(e.getAttribute("nodeId"));
					this.nodeClick(e.getAttribute("nodeId"))
				};
				break;	
			default :
				if(this.navigator=="netscape") e = e.parentNode;
				if(e.tagName.toUpperCase()=="SPAN" && e.className=="pm")
					this.expand(e.id.substr(e.id.lastIndexOf("_") + 1));
				break;
		}
	},
	/**********************************
	节点双击事件
	**********************************/
	dblClickFun : function(e){
		e = window.event || e; e = e.srcElement || e.target;
		var tg = e.tagName.toUpperCase();
		if((tg=="A" || tg=="IMG")&& e.id){
			var id = e.id.substr(e.id.lastIndexOf("_") + 1);
			if(this.nodes[id].hasChild) this.expand(id);
		}
	},
	/**********************************
	转到当前节点的父节点
	**********************************/
	upperNode : function(){
		if(!this.currentNode) return;
		if(this.currentNode.nodeId=="0" || this.currentNode.parentId=="0") return;
		if (this.currentNode.hasChild && this.currentNode.isExpand)
			this.expand(this.currentNode.nodeId, false);
		else
			this.focusStyle(this.currentNode.parentId);
	},
	/**********************************
	转到第一个子节点
	**********************************/
	lowerNode : function(){
		if (!this.currentNode){
			this.currentNode = this.nodes["0"];
			this.currentPath = this.currentNode.sourcePath;
		};
		if (this.currentNode.hasChild){
			if (this.currentNode.isExpand)
				this.focusStyle(this.currentNode.childNodes[0].nodeId);
			else
				this.expand(this.currentNode.nodeId, true);
		}
	},
	/**********************************
	转到上一节点
	**********************************/
	pervNode : function(){
		if (!this.currentNode) return;
		var e = this.currentNode;
		if (e.nodeId == "0") return;
		var a = this.nodes[e.parentId].childNodes;
		for (var i = 0; i < a.length; i++) {
			if (a[i].nodeId == e.nodeId) {
				if (i > 0) {
					e = a[i - 1];
					while (e.hasChild&&e.isExpand) {
//						this.expand(e.nodeId, true);
						e = e.childNodes[e.childNodes.length - 1];
					}
					this.focusStyle(e.nodeId);
					return;
				} else {
					this.focusStyle(e.parentId);
					return;
				}
			}
		}
	},
	/**********************************
	转到下一节点
	**********************************/
	nextNode : function(){
		var e = this.currentNode;
		if (!e) e = this.nodes["0"];
		if (e.hasChild&&e.isExpand) {
//			this.expand(e.id, true);
			this.focusStyle(e.childNodes[0].nodeId);
			return;
		}
		while (typeof(e.parentId) != "undefined") {
			var a = this.nodes[e.parentId].childNodes;
			for (var i = 0; i < a.length; i++) {
				if (a[i].nodeId == e.nodeId) {
					if (i < a.length - 1) {
						this.focusStyle(a[i + 1].nodeId);
						return;
					}
					else e = this.nodes[e.parentId];
				}
			}
		}
	},
	/**********************************
	加载图标
	@string path 图片存放的路径名,默认为this.tc.Path
	**********************************/
	setIconPath  : function(path){
		for(var i in this._icons){
			var tmp = this._icons[i];
			this.icons[i] = new Image();
			this.icons[i].src = path + tmp;
		}
		for(var i in this._iconsExpand){
			var tmp = this._iconsExpand[i];
			this.iconsExpand[i]=new Image();
			this.iconsExpand[i].src = path + tmp;
		}
	},
	/**********************************
	设置node节点radio状态
	**********************************/
	setRadioCheck : function(node){
		if(!node){return};
		var ckt = node.checktype;
		if(!ckt){return};
		var chk,nodechk,chklist = this.checkedNodes;
		nodechk = node.checked = node.checked != "1" ? "1": "0";
		if (chk = T("#"+ this.tc.treeId + "_radio_" + node.nodeId)) {
			chk.src = this.icons[(ckt + (nodechk!="1" ? 0 : 1)).toUpperCase()].src;
		};
		var chknode = this.checkedNode;
		if(nodechk!="1"){
			chknode = "";
			T.Aremove(chklist,node.nodeId);
		}else{
			T.Apush(chklist,node.nodeId);
			if(!(chknode == node.nodeId)){
				if(chknode){
					T.Aremove(chklist,chknode);
					if (chk = T("#"+ this.tc.treeId + "_radio_" + chknode)) {
						this.nodes[chknode].checked = "0";
						chk.src = this.icons["R0"].src;
					}
				}
				chknode = node.nodeId
			}
		};
		this.checkedNode = chknode;
	},
	/**********************************
	设置node节点checkbox状态
	@object node 节点对象
	@boolean ExeCheckFun 是否执行自定义check函数,为空默认执行
	**********************************/
	setCheck : function(node,ExeCheckFun){
		if(!node){return};
		var ckt = node.checktype;
		if(!ckt){return};
		var ExeCheckFun = typeof(ExeCheckFun)=="boolean"?ExeCheckFun:true;
		var chk,nodechk,chklist = this.checkedNodes;
		nodechk = node.checked = node.checked != "1" ? "1": "0";
		nodechk == "1" ? T.Apush(chklist,node.nodeId) : T.Aremove(chklist,node.nodeId);
		if (chk = T("#"+ this.tc.treeId + "_checkbox_" + node.nodeId)) {
			chk.src = this.icons[(ckt + (nodechk!="1" ? 0 : 1)).toUpperCase()].src;
		};
		if(ExeCheckFun&&this.tc.checkFun){
			var nPvalue = this.nodeFnArgs(node);
			this.tc.checkFun.apply(this,nPvalue);
		};
		this.tc.checkParent?this.setParentCheck(node,ExeCheckFun):"";//父
		this.tc.checkChild?this.setChildCheck(node,ExeCheckFun):"";//子
	},
	/**********************************
	设置node子节点checkbox状态
	**********************************/
	setChildCheck : function(node,ExeCheckFun){
		if(!node.hasChild||node.checked=="2"){return};
		var ExeCheckFun = typeof(ExeCheckFun)=="boolean"?ExeCheckFun:true;
		var childs = node.childNodes,nodechk = node.checked,chk,chklist = this.checkedNodes;
		for (var i = 0, j = childs.length; i < j; i++){
			var ckt = childs[i].checktype;
			if(ckt){
				nodechk == "1" ? T.Apush(chklist,childs[i].nodeId) : T.Aremove(chklist,childs[i].nodeId);
			};
			var cktype = ckt=="r"?"radio":"checkbox";
			childs[i].checked = nodechk;
			if (chk = T("#"+ this.tc.treeId + "_"+ cktype +"_" + childs[i].nodeId)) {
				chk.src = this.icons[ckt.toUpperCase()+nodechk].src;
			};
			if(ExeCheckFun&&this.tc.checkFun){
				var nPvalue = this.nodeFnArgs(childs[i]);
				this.tc.checkFun.apply(this,nPvalue);
			};
			this.setChildCheck(childs[i],ExeCheckFun)
		}
	},
	/**********************************
	设置node父节点checkbox状态
	**********************************/
	setParentCheck : function(node,ExeCheckFun){
		if(!node.parentNode){return};
		var ExeCheckFun = typeof(ExeCheckFun)=="boolean"?ExeCheckFun:true;
		var num=1,chk,parentnode = node.parentNode,chklist = this.checkedNodes;
		var ckt = parentnode.checktype;
		var cktype = ckt=="r"?"radio":"checkbox";
		if (node.relationIndex!="root") {
			for (var i = 0,j = parentnode.childNodes.length; i < j; i++){
				if (parentnode.childNodes[i].checked != node.checked){
					parentnode.checked = "2";
					num=0;
					if (chk = T("#"+ this.tc.treeId + "_"+ cktype +"_" + parentnode.nodeId)){
						chk.src = this.icons[ckt.toUpperCase()+"2"].src;
						break;
					}
				}
			};
			if(num==1){
				parentnode.checked = node.checked;
				if (chk = T("#"+ this.tc.treeId + "_"+ cktype +"_" + parentnode.nodeId))
				chk.src = this.icons[ckt.toUpperCase()+node.checked].src;
			}
			if(ckt){
				parentnode.checked == "1" ? T.Apush(chklist,parentnode.nodeId) : T.Aremove(chklist,parentnode.nodeId);
			};
			if(ExeCheckFun&&this.tc.checkFun){
				var nPvalue = this.nodeFnArgs(parentnode);
				this.tc.checkFun.apply(this,nPvalue);
			};
			this.setParentCheck(parentnode,ExeCheckFun);
		}
	},
	/**********************************
	更新节点
	@string sourceIndex 节点索引relationIndex(root_1)或者sourcePath,配合pathMode
	@object sourceData 与sourceId对应的值{"id":936,"type":"trunk","name":"11_36添加的节点","hasChild":true}
	@boolean pathMode  是否按节点路径进行定位,若true,则sourceIndex传入节点对应路径sourcePath(root_1_2),根节点path为"0"
	**********************************/
	updateNode : function(sourceIndex,sourceData,pathMode){
		if(!sourceData){T.loadTip(1,"无更新数据或数据格式不正确",2,this.tc.treeObj);return};
		var relationIndex = pathMode?this.getRelationByPath(sourceIndex):sourceIndex,node;
		if(this.relation.indexOf(relationIndex)==-1){T.loadTip(1,"节点不存在",2,this.tc.treeObj);return};
		T.extend(this.treeData[relationIndex],sourceData);//更新数据源
		if(pathMode){
			node = this.getNode(sourceIndex);
			if(node){
				T.extend(node,sourceData);
				this._updateNode(node)
			};
		}else{
			var pathList = this.getPathList(sourceIndex);
			for (var i = 0,j=pathList.length;i<j;i++){
				node = this.getNode(pathList[i]);
				T.extend(node,sourceData);
				this._updateNode(node)
			}
		}
	},
	/**********************************
	更新节点主函数
	@object node 节点对象
	**********************************/
	_updateNode : function(node){
		var relationIndex = node.relationIndex,sid = relationIndex.split("_")[1];
		if(!node.isLoad){return};//若未加载则只更新数据源
		var nodeId = node.nodeId,nodeChild = node.childNodes;
		var pnode = this.nodes[node.parentId];
		_isLast = pnode.childNodes[pnode.childNodes.length-1].nodeId == nodeId;//是否是最后一个
		//重设图标等
		node.icon = "";
		node.hasChild = this.relation.indexOf(this._d + sid + this.divider)>-1
		var Obj = T("#"+this.tc.treeId+"_tree_"+nodeId);
		if(!Obj||!Obj.childNodes[0]){return};
		var nodeObj = Obj.childNodes[0];
		nodeObj.innerHTML = this.node2html(node,_isLast,true);
		!node.hasChild?Obj.childNodes[1].style.display = "none":""
	},
	/**********************************
	替换子节点数据
	@string parentIndex 节点父子索引relationIndex(root_1)或者sourcePath
	@object childData {"11_36":{"id":936,"type":"trunk","name":"11_36添加的节点","pid":11},"11_37":{"id":937,"type":"trunk","name":"11_37添加的根节点2","pid":11}}
	@boolean pathMode  是否按节点路径进行定位,若true,则parentIndex传入节点对应路径sourcePath(root_1_2),根节点path为"0"
	**********************************/
	replaceChild : function(parentIndex,childData,pathMode){
		if(!childData){T.loadTip(1,"数据格式不正确",2,this.tc.treeObj);return};
		var relationIndex,d=this.divider,_d=this._d,node;
		relationIndex = pathMode?this.getRelationByPath(parentIndex):parentIndex;
		//清除节点在树中原有的子节点数据
		var sid = relationIndex.substr(relationIndex.indexOf(d) + d.length);
		var reg = new RegExp("(^|"+_d+")"+ sid +d+"[^"+_d+d +"]+("+_d+"|$)", "g");
		var cns = this.relation.match(reg);
		if (cns){
			reg = new RegExp(_d, "g"); 
			for (var i=0; i<cns.length; i++){
				this.relation = this.relation.replace(cns[i].replace(reg, ""),"");
				this.relation = this.relation.replace(_d+_d+_d+_d,_d+_d);
			};
			if(this.relation.length-2==this.relation.lastIndexOf(_d+_d)){
				this.relation = this.relation.substring(0,this.relation.lastIndexOf(_d+_d));
			};
		};
		//把childData加入树初始化数据中
		for (var id in childData){
			this.treeData[id] = childData[id];
			this.relation += _d + _d + id;
			this.totalNode += 1;
		};
		//更新
		if(pathMode){
			node = this.getNode(parentIndex)
			node?this._updateChild(node):""
		}else{
			var pathList = this.getPathList(parentIndex);
			for (var i = 0,j=pathList.length;i<j;i++){
				node = this.getNode(pathList[i])
				this._updateChild(node)
			}
		}
	},
	/**********************************
	更新子节点主函数
	@object node 节点对象
	**********************************/
	_updateChild : function(node){
		//若原子节点未加载,则不加载
		if(node.hasChild&&!node.isLoad){return};
		var nodeId = node.nodeId;
		//重新生成child对象
		node.childNodes = [];
		this.loadChildItem(nodeId,true);
		var childNode = node.childNodes;
		//重新生成child HTML
		var str = "";
		if(childNode.length>0){
			node.hasChild = true;
			for(var i=0; i<childNode.length; i++){
				str += this.node2html(childNode[i], i==childNode.length-1);
			}
		};
		T("#"+this.tc.treeId+"_tree_"+nodeId).childNodes[1].innerHTML  = str;
		this.tc.isCheck?this.setChildCheck(node):"";//若节点可选择
		//node.isExpand?setTimeout(this.tc.treeId +".expand('"+ nodeId +"',true)",1):"";
	},
	/**********************************
	添加节点
	@string parentIndex 节点父子索引relationIndex(root_1)或者sourcePath
	@object childData {"11_36":{"id":936,"type":"trunk","name":"11_36添加的节点","pid":11},"11_37":{"id":937,"type":"trunk","name":"11_37添加的根节点2","pid":11}}
	@boolean pathMode  是否按节点路径进行定位,若true,则parentIndex传入节点对应路径sourcePath(root_1_2),根节点path为"0"
	**********************************/
	addNode : function(parentIndex,childData,pathMode){
		if(!childData){T.loadTip(1,"添加节点数据格式不正确",2,this.tc.treeObj);return};
		var Pnode,d = this._d;
		if(!pathMode){
			if(this.relation.indexOf(parentIndex)==-1){T.loadTip(1,"父节点不存在",2,this.tc.treeObj);return};
			Pnode = this.getNode(parentIndex.split("_")[1]);
		}else{
			Pnode = this.getNode(parentIndex);
			if(!Pnode){T.loadTip(1,"父节点不存在",2,this.tc.treeObj);return}
		};
		for (var id in childData){
			if(T.AindexOf(this.relation.split(d+d),id)>-1){
				T.extend(this.treeData[id],childData[id]);
				continue;
			};
			this.treeData[id] = childData[id];
			this.relation += d + d + id;
			this.totalNode += 1;
		};
		
		this._updateChild(Pnode);
		this._updateNode(Pnode);
	},
	/**********************************
	删除节点
	@string sourceIndex 节点索引relationIndex(root_1)或者sourcePath
	@boolean pathMode  是否按节点路径进行定位,若true,则sourceIndex传入节点对应路径sourcePath(root_1_2),根节点path为"0"
	**********************************/
	delNode : function(sourceIndex,pathMode){
		var relationIndex = pathMode?this.getRelationByPath(sourceIndex):sourceIndex,d = this._d;
		var indexof = this.relation.indexOf(relationIndex);
		if(indexof > -1){
			var _relation = this.relation.split(d+d);
			T.Aremove(_relation,relationIndex)
			this.relation = _relation.join(d+d)
		}else{
			T.loadTip(1,"节点不存在或已被删除",2,this.tc.treeObj);
			return;
		};
		if(pathMode){
			this._delNode(sourceIndex)
		}else{
			var pathList = this.getPathList(sourceIndex);
			for (var i = 0,j=pathList.length;i<j;i++){
				this._delNode(pathList[i])
			}
		}
	},
	/**********************************
	删除节点辅助函数
	@string sourcePath 节点路径
	**********************************/
	_delNode : function(sourcePath){
		var ParentPath = sourcePath.substring(0,sourcePath.lastIndexOf("_")),PnodeChild = this.getNode(ParentPath);
		if(PnodeChild){
			this._updateChild(PnodeChild)
			this._updateNode(PnodeChild)
		};
	},
	/**********************************
	树展开深度
	@number level 展开深度值,>=2(默认已展开至1级)
	**********************************/
	expandLevel : function(level)
	{
		if (!/\d+/.test(level) || level < 1) return;
		var r;
		if ((r = this.nodes["0"]).hasChild){
			for (var i = 0, n = r.childNodes.length; i < n; i++)
				this.ExpandChildLevel(r.childNodes[i],level);
		}
	},
	/**********************************
	节点展开深度
	@object node 已初始化的节点对象
	@number level 展开深度值,>=2(默认已展开1级)
	**********************************/
	ExpandChildLevel : function(node,level){
		if (level < 1) return;
		level--;
		var me = node;
		if (me.hasChild && !me.isExpand) this.expand(me.nodeId);
		if(!me.childNodes){return};
		for (var i = 0, n = me.childNodes.length; i < n; i++){
			var childNode = me.childNodes[i],
			d = childNode.nodeId;
			if (childNode.hasChild)
			this.ExpandChildLevel(this.nodes[d],level)
		}
	},
	/**********************************
	展开所有节点,并定位到指定节点
	@string sourceId  数据源id 为空则展开整棵树,包括对应子节点
	**********************************/
	expandAll : function(sourceId){
		var t = this;
		var sourceId = sourceId?sourceId:"";
		if (sourceId == ""&&this.totalNode > 5000){
			Tconfirm({
				Title:"警告信息!",
				Ttype:"alert",
				Mask:false,
				Content:"数据节点过多！展开很耗时!<br /><b>确定要继续展开吗?</b>",
				OKFn:function(){
					t.ExpandShrinkAll("", true);
					return false;
				}
			});
		}else if(sourceId == ""){
			t.ExpandShrinkAll("", true);
		}else{
			this.focus(sourceId);
			setTimeout(function(){
				var node = t.getNode(sourceId),nodeId = node.nodeId;
				if(!node.isLoad){
					t.loadChildItem(nodeId);
					t.buildChild(nodeId);
				};
				t.ExpandShrinkAll(nodeId,true);
			}, 200);
		};
	},
	/**********************************
	收缩所有节点并定位到指定节点
	@string sourceId  数据源id 为空则收缩整棵树,包括对应子节点
	**********************************/
	shrinkAll : function(sourceId){
		var t = this;
		var sourceId = sourceId?sourceId:"";
		if(sourceId){
			this.focus(sourceId);
			setTimeout(function(){
				var node = t.getNode(sourceId),nodeId = node.nodeId;
				if(!node.isLoad){
					t.loadChildItem(nodeId);
					t.buildChild(nodeId);
				};
				t.ExpandShrinkAll(nodeId,"",true);
				t = null
			}, 200);
		}else{
			this.ExpandShrinkAll("","",true);	
		};
	},
	/**********************************
	配合expandAll/shrinkAll
	**********************************/
	ExpandShrinkAll : function(nodeId, onlyExpand, onlyShrink){
		var nodeId = nodeId?nodeId:"0";
		var e = this.nodes[nodeId].childNodes;
		if((!e)||(!e[0])){T.loadTip(1,"没有子节点",2,this.tc.treeObj);return};
		e = e[0];
		var isdo = t = false;
		while (e.nodeId != nodeId){
			var p = this.nodes[e.parentId].childNodes,
			pn = p.length;
			if (pn > 0 &&(p[pn - 1].nodeId == e.nodeId && (isdo || !e.hasChild))) {
				e = this.nodes[e.parentId];
				isdo = true;
			}else{
				if (e.hasChild && !isdo){
					this.expand(e.nodeId, onlyExpand, onlyShrink),
					t = false;
					for (var i = 0; i < e.childNodes.length; i++){
						if (e.childNodes[i].hasChild) {
							e = e.childNodes[i];
							t = true;
							break;
						}
					}
					if (!t) isdo = true;
				}else{
					isdo = false;
					for (var i = 0; i < pn; i++){
						if (p[i].nodeId == e.nodeId){
							e = p[i + 1];
							break;
						}
					}
				}
			}
		};
		if(nodeId!="0"&&(e.nodeId == nodeId)){
			this.expand(nodeId, onlyExpand, onlyShrink);
		};
		this.isAllLoad = true;
	},
	/**********************************
	展开并定位单个节点
	@string sourceId 数据源中的id 展开对应节点,不包括对应子节点
	@boolean pathMode  是否按节点路径进行定位,若true,则sourceId传入节点对应路径(root_1_2),根节点path为"root"
	**********************************/
	expandNode : function(sourceId,pathMode){
		this.focus(sourceId,false,true,false,pathMode);
	},
	/**********************************
	收缩并定位单个节点
	@string sourceId 数据源中的id 收缩对应节点,不包括对应子节点
	@boolean pathMode  是否按节点路径进行定位,若true,则sourceId传入节点对应路径(root_1_2),根节点path为"root"
	**********************************/
	shrinkNode : function(sourceId,pathMode){
		this.focus(sourceId,false,false,true,pathMode);
	},
	/**********************************
	仅定位单个节点
	@string sourceId 数据源中的id 收缩对应节点,不包括对应子节点
	@boolean pathMode  是否按节点路径进行定位,若true,则sourceId传入节点对应路径(root_1_2),根节点path为"root"
	**********************************/
	focusNode : function(sourceId,pathMode){
		this.focus(sourceId,false,false,false,pathMode);
	},
	/**********************************
	获取选中项集合
	@string key 返回选中项的参数为key的字符串集合(1,2,3),为空则返回树中nodeId字符串集合(1,2,3)
	**********************************/
	getChecked : function(key){
		var chkd = this.checkedNodes;
		if(chkd.length==0){return ""};
		if(key){
			var _chkd = [];
			for(var i=0,len = chkd.length;i<len;i++){
				_chkd.push(this.nodes[chkd[i]][key])
			}
			return _chkd.join(",");
		}
		return chkd.join(",");
	},
	/**********************************
	选中/取消选中某项
	@string nodeORsourceId node对象,也可以是node对应的数据源ID(sourceId)或者唯一路径sourcePath(形如root_1_2)。多个用","分割,如:1，2，3 或 root_1,root_2
	@boolean ExeCheckFun 是否执行自定义check函数,为空默认执行
	**********************************/
	setCheckByNode : function(nodeORsourceId,ExeCheckFun){
		if(!nodeORsourceId){return};
		if(typeof(nodeORsourceId)=="object"){
			this.setCheck(nodeORsourceId,ExeCheckFun)
		}else{
			var _nodes = nodeORsourceId.split(","),_nodesL = _nodes.length,node=null;
			for(var i=0;i<_nodesL;i++){
				node = this.getNode(_nodes[i]);
				this.setCheck(node,ExeCheckFun);
			}
		}
	},
	/**********************************
	获取当前定位节点参数
	@string key 获取参数名为key的值,为空则获取整个对象{}
	**********************************/
	getFocus : function(key){
		var value;
		if(key){
			value = this.currentNode[key]?this.currentNode[key]:false
		}else{
			value = this.currentNode?this.currentNode:false
		};
		return value
	},
	/**********************************
	获取指定节点html对象
	@string sourceId 数据源sourcePath或者sourceId
	**********************************/
	getNodeObj : function(sourceId){
		var node;
		node = this.getNode(sourceId);
		if(!node){return};
		return T("#"+ this.tc.treeId +"_link_"+ node.nodeId +"")
	},
	/**********************************
	获取指定节点附加提示对象
	@string sourceId 数据源sourcePath或者sourceId
	**********************************/
	getTipObj : function(sourceId){
		var node;
		node = this.getNode(sourceId);
		if(!node){return};
		return T("#"+ this.tc.treeId +"_tip_"+ node.nodeId +"")
	},
	/**********************************
	根据节点name属性模糊搜索节点在treeData中的对象
	@string key 节点name属性值
	@boolenn isParent 是否包括有子节点的节点
	**********************************/
	searchByName : function(key,isParent){
		(this.tc.searchLoadAll&&!this.isAllLoad)?this.expandAll():"";
		var _ret = {},count=0;
		var _d = this.nodes;
		for(var i in _d ){
			if(_d[i].name&&_d[i].name.indexOf(key) != -1){
				if(!isParent&&_d[i].childNodes)continue;
				_ret[i] = _d[i];
				count += 1
			}
		};
		count == 0 ? _ret = false:"";
		return _ret
	},
	/**********************************
	根据节点属性项精确匹配节点在nodes中的对象
	@string attrKey 节点属性名称
	@string attrValue 节点属性项对应值
	**********************************/
	searchByAttr : function(attrKey,attrValue){
		if(!attrKey){return;};
		var _ret = false;
		var _d = this.nodes;
		for(var i in _d ){
			if(_d[i][attrKey] && (_d[i][attrKey] == attrValue)){
				_ret = _d[i];
				break;
			}
		};
		return _ret
	}
}