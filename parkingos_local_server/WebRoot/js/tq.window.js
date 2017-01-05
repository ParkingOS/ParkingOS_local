/*TQwindow 2012-04-05
  Latest:2012-08-05
  Talert:仅有确定按钮
  Tconfirm:包括确定和取消按钮
  样式(Ttype):alert/info/error/success
  Twin:全自定义窗口
*/
var Twindow = function(o){
   this.tc = T.extend({//默认值
		Path:"images/form/",
	    WinType:"Window",
        Id: "tqwin",
		Ttype:"alert",
		OKFn:false,
		NOFn:false,
		CloseFn:false,//关闭按钮自定义函数
		buttonsH:0,//初始化button区域高度
        Width: 300,
        Height: "auto",
        Left: null,
        Top: null,
        Refer:null,//位置参照对象
        Title: "TQwidow",
        TitleH: 31,
        MinW: 300,
        MinH: 120,
        MaxH: null,
        CancelIco: true,
        Content: "内容",
        zIndex: 2,
		Drag:true,
        Mask: true,
		Catch:false,
		Resize:false,
		Coverobj:null,//弹窗后的覆盖对象
		buttons:false,
		sysfun:false,
		sysfunI:false,
		Cobj:null,
		windowObj:null
    },o);
	var tc = this.tc;
	tc.Width = T.gww()<parseInt(tc.Width)?T.gww()-3:tc.Width;
	tc.Height = (tc.Height!="auto"&&(T.gwh())<tc.Height)?T.gwh():tc.Height;
	tc.buttonsH = (tc.WinType!="Window"||tc.buttons)?34:0;
	var t = this;
        t._dragobj = null;
        t._resize = null;
        t._cancel = null;
        t._body = null;
        t._x = 0;
        t._y = 0;
        t._fM = this.BindAsEventListener(this, t.Move);
        t._fS = this.Bind(this, t.Stop);
        t._isdrag = null;
        t._Css = null;
};
Twindow.prototype = {
    Bind: function(object, fun, args) {
        return function() {
            return fun.apply(object, args || []);
        }
    },
    BindAsEventListener: function(object, fun) {
        var args = Array.prototype.slice.call(arguments).slice(2);
        return function(event) {
            return fun.apply(object, [event || window.event].concat(args));
        }
    },
    create: function(elm, parent, fn) {
        var element = document.createElement(elm);
        fn && fn(element);
        parent && parent.appendChild(element);
        return element
    },
    C : function(){
    	var t = this;
		var tc = this.tc;
	    if(T("#"+tc.Id)) {
	    	 if(T("#"+tc.Id).style.display == "none"){
	       		T("#"+tc.Id).style.display = "block";
	       		tc.Mask?t.ShowMask():"";
	       	}else{
	       		T("#"+tc.Id).style.display = "none";
	       		tc.Mask?t.HideMask():"";
	       	};
	    }else{
			this.initialize();
			if(!tc.sysfun){return};
			//tc.Cobj.innerHTML = "<center style=\"padding:20px;\"><img src=\""+tc.Path+"wait.gif\" />请稍后...</center>";
			if(tc.sysfunI){
				tc.sysfun(tc.sysfunI,tc.Cobj)
			}else{
				tc.sysfun(tc.Cobj)
			};
			setTimeout(function(){
				
				//tc.Cobj.innerHTML = "";
				if(tc.Height=="auto") {//height为auto时进行修正
					t._main.style.zIndex = tc.Coverobj?tc.zIndex+tc.Coverobj.style.zIndex+10:tc.zIndex;
					var offH = T.gpos(t._main).height;
					offH = offH>(T.gwh() - tc.TitleH - tc.buttonsH - 3)?T.gwh()  - tc.TitleH - tc.buttonsH - 10:offH -  tc.TitleH - tc.buttonsH -3;
					var _bodyHeight = tc.MaxH?(tc.MaxH > offH ? offH: tc.MaxH):offH;
					_bodyHeight = tc.MinH?(_bodyHeight > tc.MinH ? _bodyHeight: tc.MinH):_bodyHeight;
					if(tc.Refer){
						var pos = T.gpos(tc.Refer);
						var _top = (T.gwh()  - pos.height +  T.scrollFix().y - pos.y >  (_bodyHeight + pos.height))? pos.y + pos.height  - 2 : pos.y - _bodyHeight - pos.height ;
							_top = _top>0?_top:0;
						t._main.style.top = _top  + "px" ;
					}else{
						if(tc.Top==null){
							var _top = T.gwh() / 2 - (offH + tc.TitleH + tc.buttonsH)/2 - 2;
							t._main.style.top = (_top>0?_top:0) + "px";
						}else{
							t._main.style.top = (tc.Top>0?tc.Top:0) + "px";
						};
					};
					t._body.style.height =  _bodyHeight + "px";
				};
				t._main.style.visibility = "visible";
			},100)
	    }
    },
    initialize: function() {
		var t = this;
		var tc = this.tc;
        Twindow.zIndex = tc.zIndex;
        //构造Twindow
        if (tc.Mask) {
            t.ShowMask();
        };
        var obj = ['wincontainter_'+tc.Id+'', 'wintitle_'+tc.Id+'', 'wintitleTitle_'+tc.Id+'', 'wintitleico_'+tc.Id+'', 'winbody_'+tc.Id+'', 'winbottom_'+tc.Id+'', 'buttons_'+tc.Id+''];
            for (var i = 0; i < obj.length; i++) {
                obj[i] = t.create('div', null, 
                function(elm) {
						elm.id = obj[i];
						(i==0)?elm.className = "wincontainter":"";
						(i==1)?elm.className = "wintitle":"";
						(i==2)?elm.className = "wintitleTitle":"";
						(i==3)?elm.className = "wintitleico":"";
						(i==4)?elm.className = "winbody":"";
						(i==5)?elm.className = "winbottom":"";
						(i==6)?elm.className = "button_div":"";
                });
            };
            obj[1].style.height = tc.TitleH + "px";

     		if(tc.WinType!="Window"){
				tc.Catch = false;
				tc.buttons = !tc.buttons?true:tc.buttons;
				var Adiv = document.createElement("div");
				var Idiv = document.createElement("div");
				var Cdiv = document.createElement("div");
				var BtOK = document.createElement("span");
				var BtNO = document.createElement("span");
				tc.Cobj = Cdiv;
				Adiv.appendChild(Idiv);
				Adiv.appendChild(Cdiv);
				obj[4].appendChild(Adiv);
				Adiv.style.padding = "5px";
				Idiv.style.marginRight = "5px";
				switch (tc.Ttype){
				case "alert": 
					Idiv.className = "win_confirm";
					break;
				case "success": 
					Idiv.className = "win_success";
					break;
				case "error": 
					Idiv.className = "win_error";
					break;
				case "info": 
					Idiv.className = "win_alert";
					break;
				};
				Cdiv.style.width = tc.Width - 80 + "px";
				Cdiv.style.float = "right";
				tc.sysfun?tc.sysfun(Cdiv):Cdiv.innerHTML = tc.Content;
				
				var _defBt = document.createElement("span");
				_defBt.className = "fr";
				
				BtOK.className = "imgbutton button24_a bg_gray_hover border_gray fl";
				BtOK.title = "确定此操作";
				BtOK.innerHTML = "<span class=\"icon16 icon16ok fl\" ></span>确 定";
				BtNO.className = "imgbutton button24_a  bg_gray_hover border_gray fl";
				BtNO.title = "取消此操作";
				BtNO.innerHTML = "取 消";
	
				_defBt.appendChild(BtOK);
				if(tc.WinType=="Confirm"){
					(typeof(tc.NOFn)=="function")?T.bind(BtNO,"click",tc.NOFn):"";
					_defBt.appendChild(BtNO);
				};
				tc.OKFn?T.bind(BtOK,"click",tc.OKFn):"";
				obj[6].appendChild(_defBt);
			};
			obj[2].innerHTML = tc.Title;
            obj[1].appendChild(obj[2]);
			obj[3].title = "关闭";
            obj[1].appendChild(obj[3]);
            obj[0].id = tc.Id;
            obj[0].appendChild(obj[1]);
            obj[0].appendChild(obj[4]);
			
            obj[0].appendChild(obj[5]);
            tc.windowObj?tc.windowObj.appendChild(obj[0]):document.body.appendChild(obj[0]);
            //document.body.appendChild(obj[0]);
			t._main = obj[0];
            t._dragobj = obj[1];
            t._resize = obj[5];
            t._cancel = obj[3];
            t._body = obj[4];
			t._dragobj.title = "双击关闭";
			t._dragobj.style.cursor = (tc.Drag)?"move":"";
			//设置长宽,left ,top
			with(t._main.style) {
				if(tc.sysfun){
					visibility = "hidden";
				};
				width = tc.Width + "px";
				height = tc.Height!="auto"? tc.Height + "px":"auto";
				zIndex = tc.Coverobj?tc.zIndex+tc.Coverobj.style.zIndex+10:tc.zIndex;
				if(tc.Refer){
					var pos = T.gpos(tc.Refer);
					width = tc.Width?tc.Width+"px":pos.width + "px"; 
					left = pos.x + "px";
					if(tc.Height!="auto"){
						top = (T.gwh() - pos.y > tc.Height)? pos.y + pos.height + "px":pos.y - tc.Height + "px";
					}else{
						top = "-1px";
					}
				}else{
					if(tc.Top==null){
						top = tc.Height!="auto"?T.gwh() / 2 - tc.Height / 2 + T.scrollFix().y + "px": T.scrollFix().y + 2 + "px";
					}else{
						top = tc.Top + "px";
					};
					if(tc.Left==null){
						left = T.gww() / 2 - tc.Width / 2 - 2+ "px";
					}else{
						left = tc.Left + "px";
					};
				}
			};
			var bodyHeight = tc.Height!="auto"?tc.Height - tc.TitleH - tc.buttonsH  + "px":"auto"
            t._body.style.height = bodyHeight;
            t._body.style.position = "relative";
			if(tc.WinType=="Window"){//先加载主体内容
				if(tc.sysfun){
					tc.Cobj = obj[4];
					//tc.sysfunI?tc.sysfun(tc.sysfunI,obj[4]):tc.sysfun(obj[4])
				}else{
					obj[4].innerHTML = tc.Content
				}
			};
			if(tc.Height=="auto"&&!tc.sysfun) {//height为auto时进行修正
				t._main.style.zIndex = tc.Coverobj?tc.zIndex+tc.Coverobj.style.zIndex+10:tc.zIndex;
				var offH = T.gpos(t._main).height;
				offH = offH>(T.gwh() - tc.TitleH - tc.buttonsH)?T.gwh() - tc.TitleH - tc.buttonsH:offH-20;
				var _bodyHeight = tc.MaxH?(tc.MaxH > offH ? offH: tc.MaxH):offH;
				if(tc.Refer){
					var pos = T.gpos(tc.Refer);
					t._main.style.top = (T.gwh() - pos.y >  _bodyHeight + pos.height)? pos.y + pos.height + "px": pos.y - _bodyHeight - pos.height + "px";
				}else{
					if(tc.Top==null){
						t._main.style.top = T.gwh() / 2 - (offH + tc.TitleH + tc.buttonsH)/2 + "px";
					}else{
						t._main.style.top = tc.Top + "px";
					};
				}
				t._body.style.height =  _bodyHeight + "px";
			};
			if(tc.buttons){
				var _a = document.createElement("span");
				_a.className = "fr";
				for(var n=0;n<tc.buttons.length;n++)
				{
					var tbtn = tc.buttons[n];
					var btnimg = tc.buttons[n].icon?"<img src=\""+tc.Path+""+tc.buttons[n].icon+"\">":"";

					var a = document.createElement("span");
					var iconspan = tc.buttons[n].iconcls?"<span class=\""+tc.buttons[n].iconcls+"\"></span>":"";
					a.id = tc.Id+"_a_"+tc.buttons[n].name;
					a.className = tc.buttons[n].cls?"imgbutton " + tc.buttons[n].cls:"imgbutton button24_a bg_gray_hover border_gray fl";
					a.title = tc.buttons[n].tit?tc.buttons[n].tit:(tc.buttons[n].dname?tc.buttons[n].dname:"");
					var bhtml = "";
					bhtml = ""+btnimg+""+iconspan+""+(tc.buttons[n].dname||"")+"";
					a.innerHTML = bhtml;
					a.name = tbtn.name;
					a.onpress = tbtn.onpress;
					a.onclick = (function(){
						this.onpress(this.name,tc.Id);
					});
				_a.appendChild(a);
				};
				obj[6].appendChild(_a);
				obj[0].appendChild(obj[6]);
			};
            //事件 
            (tc.Drag)?T.bind(t._dragobj, 'mousedown', t.BindAsEventListener(this, t.Start, true)):"";
            T.bind(t._dragobj, 'dblclick', t.BindAsEventListener(this, t.Desdroy));
            T.bind(t._cancel, 'mouseover', t.Bind(this, t.Changebg, [t._cancel, '0px 0px', '-21px 0px']));
            T.bind(t._cancel, 'mouseout', t.Bind(this, t.Changebg, [t._cancel, '0px 0px', '-21px 0px']));
            T.bind(t._cancel, 'mousedown', t.BindAsEventListener(this, t.Desdroy));
            (tc.WinType!="Window")?(T.bind(BtOK, 'click', t.BindAsEventListener(this, t.Desdroy)),T.bind(BtNO, 'click', t.BindAsEventListener(this, t.Desdroy))):"";
            T.bind(t._body, 'mousedown', t.BindAsEventListener(this, t.Cancelbubble));
            (tc.Resize)?T.bind(t._resize, 'mousedown', t.BindAsEventListener(this, t.Start, false)):T(t._resize).acls("uwinbottom",false);
        
    },
    Desdroy: function(e) {
		var t = this;
		var tc = this.tc;
        t.Cancelbubble(e);
		(tc.Catch)?T(tc.Id).stcs("display","none"):(T.removech(t._dragobj),t._main.innerHTML="",T.removech(t._main));//IeIframeBug
        if (tc.Mask) {t.HideMask()};
		if(tc.CloseFn){tc.CloseFn()}

    },
    Cancelbubble: function(e) {
		var t = this;
       // t._dragobj.style.zIndex = ++Twindow.zIndex;
        document.all ? (e.cancelBubble = true) : (e.stopPropagation())
    },
    Changebg: function(o, x1, x2) {
        o.style.backgroundPosition = (o.style.backgroundPosition == x1) ? x2: x1;

    },
    Start: function(e,isdrag) {
		var t = this;
        t._isdrag = isdrag;
        //if (!isdrag) {
            t.Cancelbubble(e);
        //};
        this._Css = isdrag ? {
            x: "left",
            y: "top"
        }: {
            x: "width",
            y: "height"
        };
		//alert(t._dragobj.style.zIndex)
       // t._dragobj.style.zIndex = ++Twindow.zIndex;
        t._x = isdrag ? (e.clientX - t._main.offsetLeft || 0) : (t._main.offsetLeft || 0);
        t._y = isdrag ? (e.clientY - t._main.offsetTop || 0) : (t._main.offsetTop || 0);
        if (T.iev)
        {
            T.bind(t._dragobj, "losecapture", t._fS);
            t._dragobj.setCapture();
        }
        else
        {
            e.preventDefault();
            T.bind(window, "blur", t._fS);

        };
        T.bind(document, 'mousemove', t._fM);
        T.bind(document, 'mouseup', t._fS)
    },
    Move: function(e) {
		var t = this;
		var tc = this.tc;
        window.getSelection ? window.getSelection().removeAllRanges() : document.selection.empty();
        var i_x = e.clientX - t._x,
        i_y = e.clientY - t._y;
		(i_x<T.gww()-30)?t._main.style[t._Css.x] = (t._isdrag ? Math.max(i_x,0) : Math.max(i_x, tc.MinW)) + 'px':'';
		(i_y - T.scrollFix().y<T.gwh()-25)?t._main.style[t._Css.y] = (t._isdrag ? Math.max(i_y,0) : Math.max(i_y, tc.MinH)) + 'px':'';
        if (!t._isdrag){
        	t._body.style.height = Math.max(i_y - tc.TitleH, tc.MinH - tc.TitleH) - tc.buttonsH + 'px'
		};
    },
    Stop: function() {
		var t = this;
        T.unbind(document, 'mousemove', t._fM);
        T.unbind(document, 'mouseup', t._fS);
        if (T.iev)
        {
            T.unbind(t._dragobj, "losecapture", t._fS);
            t._dragobj.releaseCapture();
        }
        else
        {
            T.unbind(window, "blur", t._fS);
        };
    },
	ShowMask:function(){var tc = this.tc;return T.pageCover(1,tc.Coverobj)},
	HideMask:function(){var tc = this.tc;return T.pageCover(0,tc.Coverobj)}
};
var Twin = function(o,mI){new Twindow(o).C()};
var Talert = function(o,mI){var a =({WinType:"Alert",Drag:true}); a = T.extend(a,o);new Twindow(a).C()};
var Tconfirm = function(o,mI){var a =({WinType:"Confirm",Drag:true}); a = T.extend(a,o);new Twindow(a).C()};
var TwinC = function(o,c,p,coverobj,_ismask){
	if(!T("winbody_"+o))return;
	var coverobj = coverobj?coverobj:null;
	var o = T("#"+o);
	var ismask = _ismask||1;
	_ismask == false?ismask=false:"";
	try{
		if(p){
			(c==true)?p.T(o).stcs("display","none"):(o.innerHTML = "",p.T.removech(o));if(ismask==1){try{p.T.pageCover(0,coverobj)}catch(e){}}
		}else{
			(c==true)?T(o).stcs("display","none"):(o.innerHTML = "",T.removech(o));if(ismask==1){try{T.pageCover(0,coverobj)}catch(e){}}
		}
	}catch(e){}
	
	
}