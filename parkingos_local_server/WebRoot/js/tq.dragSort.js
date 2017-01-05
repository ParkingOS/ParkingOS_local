/*firefox火狐wind.event事件补丁*/
function __firefox(){
    HTMLElement.prototype.__defineGetter__("runtimeStyle", __element_style);
    window.constructor.prototype.__defineGetter__("event", __window_event);
    Event.prototype.__defineGetter__("srcElement", __event_srcElement);
}
function __element_style(){
    return this.style;
}
function __window_event(){
    return __window_event_constructor();
}
function __event_srcElement(){
    return this.target;
}
function __window_event_constructor(){
    if(document.all){
        return window.event;
    }
    var _caller = __window_event_constructor.caller;
    while(_caller!=null){
        var _argument = _caller.arguments[0];
        if(_argument){
            var _temp = _argument.constructor;
            if(_temp.toString().indexOf("Event")!=-1){
                return _argument;
            }
        }
        _caller = _caller.caller;
    }
    return null;
}
var _isIE = /msie/.test(navigator.userAgent.toLowerCase()) && !/opera/.test(navigator.userAgent.toLowerCase());
if(!_isIE){  
   __firefox();
}
/*end firefox火狐wind.event事件补丁*/
var Class = { 
      create:   function()   { 
          return   function()   { 
              this.initialize.apply(this, arguments); 
          } 
      } 
} 
function $A(iterable) {
	if (!iterable) return [];
	if (iterable.toArray) return iterable.toArray();
	var length = iterable.length, results = new Array(length);
	while (length--) results[length] = iterable[length];
	return results;
}
Function.prototype.bind=function(){
    if (arguments.length < 2 && arguments[0] === undefined) return this;
    var __method = this, args = $A(arguments), object = args.shift();
    return function() {
      return __method.apply(object, args.concat($A(arguments)));
    }
}
var Position = {
	positionedOffset:   function(element)   { 
          var valueT = 0,valueL  = 0,valueST = 0; 
          do{ 
              valueT   +=   element.offsetTop     ||   0; 
              valueL   +=   element.offsetLeft   ||   0; 
              valueST  +=   element.scrollTop || 0;
              element   =   element.offsetParent; 
              if(element){ 
                  p = element.style.position;
                  if(p == 'relative'||p == 'absolute')break; 
              } 
          }while(element); 
          return [valueL,valueT,valueST]; 
      }
};    


/*
	tq.dragSort.js
	拖动排序 2013-06-20
*/
var makeDragSort = function(initArg){
	var t = this;
	var initArg = initArg||{};
	t.uiObj = null;//拖动控件输出的对象
	t.sortedIds = "";//初始列出项ID集合(id1,id2,id3) 暂时没有用
	t.valueObj = null;//排序后结果集输出对象(表单对象,如<input>)
	t.onlyFlag = "tqdragsort";//唯一标识,字符串,以防止多个同类控件冲突,不能包含"_",【【【必须与函数对象名相同】】】
	t.isDrag = true;//是否拖动
	t.startDragFun = null;//拖动开始时执行函数
	t.endDragFun = null;//拖动成功结束后执行函数
	t.stopDragFun = null;//拖动停止后执行函数
	t.delFun = null;//删除单个列出项函数,传入值为列出项ID
	t.ajaxDelFun = null;//删除后再回调时,如ajax删除
	t.clearFun = null;//清空列出项执行函数
	t.globleTip = "按住拖动排序/拖出列表框进行删除&nbsp;";//底部提示
	t.cellTip = "按住拖动排序/拖出列表框进行删除";//单元提示
	t.delArea = null;//拖放释放事件有效区域DOM ID
	t.minLimit = 0;//最少列出项限制
	t.maxLimit = false;//最多列出项限制
	t.clearBt = true;//是否显示清空按钮
	t.buttons = null;//自定义按钮
	
	t.customerArgList = "";//自定义结果集
	t.itembuttons = [{name:"编辑",tit:"编辑",type:"up",condition:[{valueIndex:0,value:"userdefine"}],iconcls:"",cls:""},{name:"删除",tit:"删除",type:"down",iconcls:"",cls:""}],//节点扩展操作按钮(节点文字右边显示)
	//必须是这样的格式
	//注意:conditon为空或未定义时,表示所有记录均添加此操作
	//与itembuttonsFun配合使用
	t.itembuttonsFun = function(id,btType){alert(id)},//扩展按钮对应的事件函数,id 数据唯一ID btType 为itembuttons中的type属性
	
	t.itembuttons = false;//单元记录操作按钮
	t.itembuttonsFun = false;//单元记录操作按钮函数
	
	
	T.extend(t,initArg);
	t.minLimit = parseInt(t.minLimit);
	t.maxLimit = parseInt(t.maxLimit);
	if(!t.uiObj||!t.valueObj){return};
	var _Opos = T.gpos(t.uiObj);
	var _w = _Opos.width;
	var _h = _Opos.height;
	
	var _rTitle = document.createElement("div");
	var _rContent = document.createElement("div");
	
	_rTitle.className = "drag_sort_title";
	_rTitle.id = t.onlyFlag+"_drag_sort_title_obj";
	_rTitle.innerHTML = "<span style='float:left;'></span>";
	if(t.clearBt){
		_rTitle.innerHTML += "<span class='curhand fr' style='margin:3px 3px 0 0' onclick='"+t.onlyFlag.toString()+".clear()' title='清空'><span class='icon16 icon16delete hover1 fl'></span></span>";
	};
	_rContent.style.height = _h - 54>0?_h - 54 + "px":"auto";
	_rContent.style.overflow = "auto";
	_rContent.id = t.onlyFlag+"_drag_sort_list_obj";
	
	t.uiObj.style.overflow = "hidden";
	t.uiObj.appendChild(_rTitle);
	t.uiObj.appendChild(_rContent);
	if(t.globleTip){
		var _rTip = document.createElement("div");
		_rTip.id = t.onlyFlag+"_drag_sort_tip_obj";
		_rTip.style.height = "23px";
		_rTip.style.color = "#999";
		_rTip.style.lineHeight = "24px";
		_rTip.style.textAlign = "right";
		_rTip.style.borderTop = "1px solid #ccc";
		_rTip.innerHTML = t.globleTip;
		_rTip.onmouseover = function(){this.style.color="#c00"};
		_rTip.onmouseout = function(){this.style.color="#999"};
		t.uiObj.appendChild(_rTip);
	};
	var _rContentH = "";
	_rContentH += "<div class='drag_sort_list'>"
	_rContentH += "<ul class='sort_div' id='"+t.onlyFlag+"_sort_div'>";
	_rContentH += "</ul>";
	_rContentH += "</div>";
	_rContent.innerHTML = _rContentH;

	t.sortResults = t.sortedIds!=""?t.sortedIds.split(","):[];
	t.total = 0;
	t.customerResults = [];
	
	/*网上找的Drag函数*/
	var Drag = {
	    // 对这个element的引用，一次只能拖拽一个Element
	    obj: null , 
	    /**
	    * @param: elementHeader    used to drag..
	    * @param: element            used to follow..
	    */
	    init: function(elementHeader, element) {
	        // 将 start 绑定到 onmousedown 事件，按下鼠标触发 start
	        elementHeader.onmousedown = Drag.start;
	        // 将 element 存到 header 的 obj 里面，方便 header 拖拽的时候引用
	        elementHeader.obj = element;
	        // 初始化绝对的坐标，因为不是 position = absolute 所以不会起什么作用，但是防止后面 onDrag 的时候 parse 出错了
	        if(isNaN(parseInt(element.style.left))) {
	            element.style.left = "0px";
	        }
	        if(isNaN(parseInt(element.style.top))) {
	            element.style.top = "0px";
	        }
	        // 挂上空 Function，初始化这几个成员，在 Drag.init 被调用后才帮定到实际的函数
	        element.onDragStart = new Function();
	        element.onDragEnd = new Function();
	        element.onDrag = new Function();
	    },
	    // 开始拖拽的绑定，绑定到鼠标的移动的 event 上
	    start: function(event) {
	        var element = Drag.obj = this.obj;
	        // 解决不同浏览器的 event 模型不同的问题
	        event = Drag.fixE(event);
	        // 看看是不是左键点击
	        if(event.which != 1){
	            // 除了左键都不起作用
	            return true ;
	        }
	        // 参照这个函数的解释，挂上开始拖拽的钩子
	        element.onDragStart();
	        // 记录鼠标坐标
	        element.lastMouseX = event.clientX;
	        element.lastMouseY = event.clientY;
	        // 绑定事件
	        document.onmouseup = Drag.end;
	        document.onmousemove = Drag.drag;
	        return false ;
	    }, 
	    // Element正在被拖动的函数
	    drag: function(event) {
	        event = Drag.fixE(event);
//	        if(event.which == 0 ) {
//	             return Drag.end();
//	        }
	        // 正在被拖动的Element
	        var element = Drag.obj;
	        // 鼠标坐标
	        var _clientX = event.clientX;
	        var _clientY = event.clientY;
	        // 如果鼠标没动就什么都不作
	        if(element.lastMouseX == _clientX && element.lastMouseY == _clientY) {
	            return    false ;
	        }
	        // 刚才 Element 的坐标
	        var _lastX = parseInt(element.style.left);
	        var _lastY = parseInt(element.style.top);
	        // 新的坐标
	        var newX, newY;
	        // 计算新的坐标：原先的坐标+鼠标移动的值差
	        newX = _lastX + _clientX - element.lastMouseX;
	        newY = _lastY + _clientY - element.lastMouseY;
	        // 修改 element 的显示坐标
	        element.style.left = newX + "px";
	        element.style.top = newY + "px";
	        // 记录 element 现在的坐标供下一次移动使用
	        element.lastMouseX = _clientX;
	        element.lastMouseY = _clientY;
	        // 参照这个函数的解释，挂接上 Drag 时的钩子
	        element.onDrag(newX, newY);
	        return false;
	    },
	    // Element 正在被释放的函数，停止拖拽
	    end: function(event) {
	        event = Drag.fixE(event);
	        // 解除事件绑定
	        document.onmousemove = null;
	        document.onmouseup = null;
	        // 先记录下 onDragEnd 的钩子，好移除 obj
	        var _onDragEndFuc = Drag.obj.onDragEnd();
	        // 拖拽完毕，obj 清空
	        Drag.obj = null ;
	        return _onDragEndFuc;
	    },
	    // 解决不同浏览器的 event 模型不同的问题
	    fixE: function(ig_) {
	        if( typeof ig_ == "undefined" ) {
	            ig_ = window.event;
	        }
	        if( typeof ig_.layerX == "undefined" ) {
	            ig_.layerX = ig_.offsetX;
	        }
	        if( typeof ig_.layerY == "undefined" ) {
	            ig_.layerY = ig_.offsetY;
	        }
	        if( typeof ig_.which == "undefined" ) {
	            ig_.which = ig_.button;
	        }
	        return ig_;
	    }
	};

	var DragDrop = Class.create();
	DragDrop.prototype = {
	    initialize: function(elementHeader_id , element_id){
	        var element = document.getElementById(element_id);
	        var elementHeader = document.getElementById(elementHeader_id);
	        this.isDragging = false;
	        this.elm = element;
	        //this.hasIFrame = this.elm.getElementsByTagName("IFRAME").length > 0;
	        if(!t.isDrag)return;
	        if( elementHeader){
	            elementHeader.style.cursor = "move";
	            Drag.init(elementHeader, this.elm);
	            this.elm.onDragStart = this._dragStart.bind(this);
	            this.elm.onDrag = this._drag.bind(this);
	            this.elm.onDragEnd = this._dragEnd.bind(this);
	        }
	    },
	    // 开始拖拽
		_dragStart :function(event){
			if(t.startDragFun)t.startDragFun();
		    DragUtil.reCalculate(this);    // 重新计算所有可拖拽元素的位置
		    this.origNextSibling = this.elm.nextSibling;
		    var _ghostElement = DragUtil.getGhostElement();
		    var offH = this.elm.offsetHeight;
		    var offW = this.elm.offsetWidth
			//if(DragUtil.isGecko){ // 修正 Gecko
		        offH -= parseInt(_ghostElement.style.borderTopWidth) *  2 ;
		    	offW -= parseInt(_ghostElement.style.borderLeftWidth) * 2 ;
			//}
		    var position = Position.positionedOffset(this.elm);
		    var offLeft = position[0];
		    var offTop = position[1];
		    var scrollST = _rContent.scrollTop;
		    var scrollSL = _rContent.scrollLeft;
		    //在元素的前面插入占位虚线框
		    _ghostElement.style.width = offW  + "px";
		    _ghostElement.style.height = offH + "px";
		    this.elm.parentNode.insertBefore(_ghostElement, this.elm.nextSibling);
		    //设置元素样式属性
		    //this.elm.style.width = offW + "px";
		    this.elm.style.position = "absolute";
		    this.elm.style.zIndex = 100;
		   	var tmpMarginL = parseInt(T(this.elm).gtcs("marginLeft"));
		    var tmpMarginT = parseInt(T(this.elm).gtcs("marginTop"));
//		    this.elm.style.left = offLeft - tmpMarginL/2 + 'px';
			this.elm.style.left = t.uiObj.style.position ? offLeft -  tmpMarginL/2 - scrollSL   +'px': T.mousePos(event).x + "px";;
		    this.elm.style.top = t.uiObj.style.position ? offTop -  tmpMarginT/2 - scrollST   +'px': T.mousePos(event).y + "px";//offTop - tmpMarginT/2 - scrollST   +'px';
		    this.isDragging = false;
		    DragUtil.lastLeft = parseInt(this.elm.style.left);
		    DragUtil.lastTop = parseInt(this.elm.style.top);
		    //修正值为：水平超过元素的宽度的一半，垂直超过元素的高度的一半
		    DragUtil.rangeX = parseInt(offW / 2);
		    DragUtil.rangeY = parseInt(offH / 2);
		    return false;
		},
		// 拖动时触发这个函数（每次鼠标坐标变化时）
		_drag: function(clientX , clientY){
		    if (!this.isDragging){    // 第一次移动鼠标，设置它的样式
		        this.elm.style.filter = "alpha(opacity=70)";
		        this.elm.style.opacity = 0.7;
		        this.isDragging = true;
		    }
		    // 计算离当前鼠标位置最近的可拖拽的元素，把该元素放到 found 变量中
		    var found = null;
		    var max_distance = 100000000;
		    for(var i = 0 ; i < DragUtil.dragArray.length; i++) {
		        var ele = DragUtil.dragArray[i];
		        DragUtil.curLeft = parseInt(this.elm.style.left);
		        DragUtil.curTop = parseInt(this.elm.style.top);
		        var distance = Math.sqrt(Math.pow(clientX - ele.elm.pagePosLeft, 2 ) + Math.pow(clientY - ele.elm.pagePosTop, 2 ) ) ;//+ Math.pow(clientY  + _rContent.scrollTop, 2 )

		        if(isNaN(distance)){
		            continue;
		        }
		        if(distance < max_distance){
		            max_distance = distance;
		            found = ele;
		        }
		    }
		    if(DragUtil.curTop > (DragUtil.lastTop+DragUtil.rangeY) || DragUtil.curTop >= (DragUtil.lastTop-DragUtil.rangeY) && 
		
		DragUtil.curLeft > (DragUtil.lastLeft+DragUtil.rangeX)) {
		        direction = 1;
		    } else if(DragUtil.curTop < (DragUtil.lastTop-DragUtil.rangeY) || DragUtil.curTop >= (DragUtil.lastTop-DragUtil.rangeY) 
		
		&& DragUtil.curLeft < (DragUtil.lastLeft-DragUtil.rangeX)) {
		        direction = -1;
		    } else return;
		    // 把虚线框插到 found 元素的前面
		    var _ghostElement = DragUtil.getGhostElement();
			//if(found != null && _ghostElement.nextSibling != found.elm) {
		    if(found != null) {
		    	try{
			        if(direction == -1) {
			            found.elm.parentNode.insertBefore(_ghostElement, found.elm);
			        } else if(direction == 1) {
			            found.elm.parentNode.insertBefore(_ghostElement, found.elm.nextSibling);
			        }
		        }catch(e){};
		        direction = '';
		        if(DragUtil.isOpera){//修正Opera
		            document.body.style.display = "none";
		            document.body.style.display = "";
		        }
		    }
		},
		// 结束拖拽
		_dragEnd :function(e){
		    var curId = this.elm.getAttribute("rid");
		    var curName = this.elm.firstChild.innerHTML;
		    var clsName = this.elm.className;
			if(t.stopDragFun)t.stopDragFun(""+curId);//拖拽停止后操作
		    if(this._afterDrag()){// 拖拽成功后的操作
			   if(t.endDragFun)t.endDragFun(""+curId);
		    };
		    var e = e||window.event;
			var e_tar = e.srcElement?e.srcElement:e.target;
			if(t.delArea){
				if(e_tar.id.indexOf(t.delArea)!=-1)
				{
					t._delFun(""+curId,""+curName,""+clsName,e_tar.id);
				};
			}else if(t.delArea!=false){
				if(e_tar.id.split("_")[0]!=t.onlyFlag)
				{
					t._delFun(""+curId,""+curName,""+clsName,e_tar.id);
				};
			};
			t.sortResults = [];
		    var tmpElements = document.getElementById(t.onlyFlag+"_sort_div").childNodes;
		    for(var i=0;i<tmpElements.length;i++){
				t.sortResults.push(tmpElements[i].getAttribute("rid"));
		    };
			t.save_sort();
		    return true;
		},
		// 结束拖拽时调用的函数
		_afterDrag : function(){
		    var returnValue = false;
		    // 把拖动的元素的样式回复到原来的状态
		    DragUtil.curTop = 0;
		    DragUtil.curLeft = 0;
		    DragUtil.lastTop = 0;
		    DragUtil.lastLeft = 0;
		
		    this.elm.style.position = "";
		    this.elm.style.top = "";
		    this.elm.style.left = "";
		    this.elm.style.width = "";
		    this.elm.style.zIndex = "";
		    this.elm.style.filter = "";
		    this.elm.style.opacity = "";
		    // 在虚线框的地方插入拖动的这个元素
		    var ele = DragUtil.getGhostElement();
		    if(ele.nextSibling != this.origNextSibling) {
		        ele.parentNode.insertBefore(this.elm, ele.nextSibling);
		        //需要对dragArray相应调整
		        returnValue = true;
		    }
		    //删除虚线框
		    ele.parentNode.removeChild(ele);
		    if(DragUtil.isOpera) {
		        document.body.style.display = "none";
		        document.body.style.display = "" ;
		    }
		    return returnValue;
		}
	};
	
	var DragUtil = new Object();
	// 获得浏览器信息
	DragUtil.getUserAgent = navigator.userAgent;
	DragUtil.isGecko = DragUtil.getUserAgent.indexOf("Gecko") != -1;
	DragUtil.isOpera = DragUtil.getUserAgent.indexOf("Opera") != -1;
	// 计算每个可拖拽的元素的坐标
	DragUtil.reCalculate = function(el) {
	    for( var i = 0 ; i < DragUtil.dragArray.length; i++ ) {
	        var ele = DragUtil.dragArray[i];
	        var position = Position.positionedOffset(ele.elm);
	        ele.elm.pagePosLeft = position[0];
	        ele.elm.pagePosTop = position[1] - _rContent.scrollTop;
	    }
	};
	// 拖动元素时显示的占位框
	DragUtil.ghostElement = null ;
	DragUtil.getGhostElement = function(){
	    if(!DragUtil.ghostElement){
	        DragUtil.ghostElement = document.createElement("DIV");
	        DragUtil.ghostElement.className = "modbox";
	        DragUtil.ghostElement.id = t.onlyFlag+"_modbox";
	        DragUtil.ghostElement.style.border = "3px dashed #aaa";
	        DragUtil.ghostElement.innerHTML = "&nbsp;";
	    }
	    return DragUtil.ghostElement;
	};

	DragUtil.curTop = 0, DragUtil.curLeft = 0; // 当前拖拽元素的位置
	DragUtil.lastTop = 0, DragUtil.lastLeft = 0; // 开始拖拽的元素的位置
	DragUtil.rangeX = 0, DragUtil.rangeY = 0; // 拖动的响应范围的修正值
	DragUtil.dragArray = new Array();
	// 初始化所有可拖拽的元素，及列出值，可拖拽的部分的 id 为该元素 id 加上 _h
	t.initDrag = function(tmpHeaderElementId,tmpElementId) {
		DragUtil.dragArray[t.total] = new DragDrop(tmpHeaderElementId , tmpElementId);
		t.total+=1;
	};
	t.add_sort = function(name,id,cls,args,extArgValue){
		var args = args||{};
		if(t.maxLimit&&t.sortResults.length==t.maxLimit){
			T.loadTip(1,"最大限制"+t.maxLimit+"项",3,t.uiObj);
			return;
		};
		if(T.AindexOf(t.sortResults,id)==-1){t.sortResults.push(id);};
		if(document.getElementById(t.onlyFlag+"_"+id)){return};
		var obj = document.getElementById(t.onlyFlag+"_sort_div");
		var addDiv = document.createElement("li");
		addDiv.id = t.onlyFlag+"_"+id;
		addDiv.setAttribute("rid",id);
		
		var _extArgs = t.customerArgList?t.customerArgList.split(","):false;
		if(_extArgs&&extArgValue)
			T.each(_extArgs,function(p,q){
				addDiv.setAttribute(p,extArgValue[q]);//必须要对应
			});
			
			
		addDiv.className = cls||"drag_li";
		addDiv.style.left = "0px";
		addDiv.style.top = "0px";
		
		var dragDiv = document.createElement("div");
		dragDiv.id = t.onlyFlag+"_"+id+"_h";
		t.cellTip?dragDiv.title = t.cellTip:"";
		dragDiv.className = "drag_div";
		dragDiv.innerHTML = name;
		addDiv.appendChild(dragDiv);
			
		if(t.itembuttons){
			var btDiv = document.createElement("div");
			btDiv.className = "drag_btdiv";
			
			var itembuttons = t.itembuttons;
			var _ibl = itembuttons.length;
			
			for(var i = 0;i<_ibl;i++){
				var item = itembuttons[i];
				if(item.condition){
					var _iC = item.condition;
					var kk =_iC.length;
					var resNum = 0;
					for(var jj=0;jj<kk;jj++){
						if(_iC[jj].value == args[_iC[jj].valueIndex]){
							resNum++
						}
					};
					if(resNum == kk){
						var _bs = document.createElement("span");
						var _cls = item.cls;
						_cls = _cls?_cls:"fl button16_a border_gray bg_gray_hover";
						var iconcls = item.iconcls||"";
						_bs.className = _cls;
						_bs.setAttribute("itembt","yes");
						_bs.setAttribute("rid",id);
						_bs.setAttribute("type",item.type);
						_bs.title = item.tit?item.tit:item.name;
						_bs.innerHTML = "<span class='"+iconcls+"'></span>"+item.name;
						_bs.onpress = item.onpress;
						_bs.onclick = function(){
							t.btClick()
						};
						btDiv.appendChild(_bs);
					}
				}else{
					var _bs = document.createElement("span");
					var _cls = item.cls;
					_cls = _cls?_cls:"fl button16_a border_gray bg_gray_hover";
					var iconcls = item.iconcls||"";
					_bs.className = _cls;
					_bs.setAttribute("itembt","yes");
					_bs.setAttribute("rid",id);
					_bs.setAttribute("type",item.type);
					_bs.title = item.tit?item.tit:item.name;
					_bs.innerHTML = "<span class='"+iconcls+"'></span>"+item.name;
					_bs.onpress = item.onpress;
					_bs.onclick = function(){
						t.btClick()
					};
					btDiv.appendChild(_bs);
				}
			};
			addDiv.appendChild(btDiv);
		};
		
		obj.appendChild(addDiv);
		t.initDrag(t.onlyFlag+"_"+id+"_h",t.onlyFlag+"_"+id);
		t.save_sort();
	}
	t._delFun = function(id,name,cls,e_tar){
		if(!isNaN(t.minLimit)&&t.sortResults.length<t.minLimit+1){
			T.loadTip(1,"至少保留"+t.minLimit+"项",3,t.uiObj);
			return;
		};
		if(t.ajaxDelFun){
			t.ajaxDelFun(id,name,cls,e_tar,t.del_sort);
		}else{
			t.del_sort(id);
			t.delFun?t.delFun(id,name,cls,e_tar):"";
		};
	};
	t.del_sort = function(id){
		if(T.AindexOf(t.sortResults,id)!=-1){T.Aremove(t.sortResults,id);};
		if(!document.getElementById(t.onlyFlag+"_"+id)){return};
		var obj = document.getElementById(t.onlyFlag+"_sort_div");
		var addDiv = document.getElementById(t.onlyFlag+"_"+id);
		if(!addDiv){return};
		obj.removeChild(addDiv);
		t.save_sort();
	}
	t.save_sort = function(jurl,jdiv,jfunc){
		this.valueObj.value = t.sortResults.join(",");
		T("#"+t.onlyFlag+"_drag_sort_title_obj").firstChild.innerHTML = "&nbsp;共&nbsp;<font style='color:#c00;font-weight:700'>"+t.sortResults.length+"</font>&nbsp;个";
	}
	t.clear = function(){
		var tmpElements = document.getElementById(t.onlyFlag+"_sort_div");
		var ids = t.sortResults.join(",");
		if(t.minLimit>0){
			T.loadTip(1,"至少保留"+t.minLimit+"项",3,t.uiObj);
			var _s = t.sortResults;
			var _sl = _s.length;
			for (var m=_sl-1;m>t.minLimit-1;m--){
				var _o = document.getElementById(t.onlyFlag+'_'+_s[m]+'_h');
				t._delFun(_s[m],_o.innerHTML,_o.parentNode.className)
			};
			ids = ids.replace(t.sortResults.join(","),"").replaceAll(",,",",");
			ids.lastIndexOf(",")==ids.length-1?ids=ids.substring(0,ids.length-1):"";
		}else{
			tmpElements.innerHTML = "";
			t.sortResults = [];
			t.save_sort();
			DragUtil.dragArray = null;
			DragUtil.dragArray = new Array();
			t.total = 0;
		};
		t.clearFun?t.clearFun(ids):"";	
		ids = null;
	},
	t.clearAll = function(){
		var tmpElements = document.getElementById(t.onlyFlag+"_sort_div");
		var ids = t.sortResults.join(",");
			tmpElements.innerHTML = "";
			t.sortResults = [];
			t.save_sort();
			DragUtil.dragArray = null;
			DragUtil.dragArray = new Array();
			t.total = 0;
		//t.clearFun?t.clearFun(ids):"";	
		ids = null;
	},
	t.btClick = function(e){
		e = window.event || e; e = e.srcElement || e.target;
		switch(e.tagName.toUpperCase()){
			case "SPAN" :
				if(e.getAttribute("itembt")=="yes"){
					if(t.itembuttonsFun){
						var rid = e.getAttribute("rid");
						var btType = e.getAttribute("type");
						t.itembuttonsFun(rid,btType);
					};
				}
				break;
		}
	},
	t.customerArgs = function(){
		if(!t.customerArgList)return;
		t.customerResults = [];
		var _arg = t.customerArgList.split(",");
		var tmpElements = document.getElementById(t.onlyFlag+"_sort_div").childNodes;
		T.each(_arg,function(o,m){
			var _r = [];
		    for(var i=0;i<tmpElements.length;i++){
				_r.push(tmpElements[i].getAttribute(o));
		    };
		    t.customerResults.push(_r);
		});
		return T.Obj2Str(t.customerResults);	
	}
}


