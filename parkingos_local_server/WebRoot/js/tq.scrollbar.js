function Scroller (o, w ,containerObj) {
	var self = this;
	var list = o.getElementsByTagName("div");
	for (var i = 0; i < list.length; i++) {
		if (list[i].className.indexOf("Scroller-Container") > -1) {
			o = list[i];
		}
	}
	var h = containerObj.offsetHeight;
	//Private methods
	this._setPos = function (x, y) {
		if (x < this.viewableWidth - this.totalWidth) 
			x = this.viewableWidth - this.totalWidth;
		if (x > 0) x = 0;
		if (y < this.viewableHeight - this.totalHeight) 
			y = this.viewableHeight - this.totalHeight;
		if (y > 0) y = 0;
		this._x = x;
		this._y = y;
		with (o.style) {
			left = this._x +"px";
			top  = this._y +"px";
		}
	};
	
	//Public Methods
	this.reset = function () {
		this.viewableHeight = containerObj.offsetHeight;
		this.content = o;
		this.totalHeight = o.offsetHeight;
		this.totalWidth	 = o.offsetWidth;
		this._x = 0;
		this._y = 0;
		with (o.style) {
			left = "0px";
			top  = "0px";
		}
	};
	this.scrollBy = function (x, y) {
		this._setPos(this._x + x, this._y + y);
	};
	this.scrollTo = function (x, y) {
		this._setPos(-x, -y);
	};
	this.stopScroll = function () {
		if (this.scrollTimer) window.clearInterval(this.scrollTimer);
	};
	this.startScroll = function (x, y) {
		this.stopScroll();
		this.scrollTimer = window.setInterval(
			function(){ self.scrollBy(x, y); }, 40
		);
	};
	this.swapContent = function (c, w, h) {
		o = c;
		var list = o.getElementsByTagName("div");
		for (var i = 0; i < list.length; i++) {
			if (list[i].className.indexOf("Scroller-Container") > -1) {
				o = list[i];
			}
		}
		if (w) this.viewableWidth  = w;
		if (h) this.viewableHeight = h;
		this.reset();
	};
	
	//variables
	this.content = o;
	this.viewableWidth  = w;
	this.viewableHeight = h;
	this.totalWidth	 = o.offsetWidth;
	this.totalHeight = o.offsetHeight;
	this.scrollTimer = null;
	this.reset();
};
////////////////////////////////////////////////////////
function Scrollbar (o, s, a, ev) {
	var self = this;
	
	this.reset = function () {
		this._Mask = T("#scrollMask");
		//Arguments that were passed
		this._parent = o;
		this._src    = s;
		this.auto    = a ? a : false;
		this.eventHandler = ev ? ev : function () {};
		//Component Objects
		this._up   = this._findComponent("Scrollbar-Up", this._parent);
		this._down = this._findComponent("Scrollbar-Down", this._parent);
		this._yTrack  = this._findComponent("Scrollbar-Track", this._parent);
		this._yHandle = this._findComponent("Scrollbar-Handle", this._yTrack);
		//Height and position properties
		this._trackTop = findOffsetTop(this._yTrack);
		this._trackHeight  = this._yTrack.offsetHeight;
		this._handleHeight = this._yHandle.offsetHeight;
		this._x = 0;
		this._y = 0;
		//Misc. variables
		this._scrollDist  = 5;
		this._scrollTimer = null;
		this._selectFunc  = null;
		this._grabPoint   = null;
		this._tempTarget  = null;
		this._tempDistX   = 0;
		this._tempDistY   = 0;
		this._disabled    = false;
		this._ratio = (this._src.totalHeight - this._src.viewableHeight)/(this._trackHeight - this._handleHeight);
		
		this._yHandle.ondragstart  = function () {return false;};
		this._yHandle.onmousedown = function () {return false;};
		this._addEvent(this._src.content, "mousewheel", this._scrollbarWheel);
		this._removeEvent(this._parent, "mousedown", this._scrollbarClick);
		this._addEvent(this._parent, "mousedown", this._scrollbarClick);
		
		this._src.reset();
		with (this._yHandle.style) {
			//top  = "0px";
			left = "0px";
		};
		var initY = this._yHandle.style.top?this._yHandle.style.top.substring(0,this._yHandle.style.top.length-2):0;
		this._scroll(false,initY)
		//this._moveContent();
		
		if (this._src.totalHeight < this._src.viewableHeight) {
			this._disabled = true;
			this._yHandle.style.visibility = "hidden";
			if (this.auto) this._parent.style.visibility = "hidden";
		} else {
			this._disabled = false;
			this._yHandle.style.visibility = "visible";
			this._parent.style.visibility  = "visible";
		}
	};
	this._addEvent = function (o, t, f) {
		if (o.addEventListener) o.addEventListener(t, f, false);
		else if (o.attachEvent) o.attachEvent('on'+ t, f);
		else o['on'+ t] = f;
	};
	this._removeEvent = function (o, t, f) {
		if (o.removeEventListener) o.removeEventListener(t, f, false);
		else if (o.detachEvent) o.detachEvent('on'+ t, f);
		else o['on'+ t] = null;
	};
	this._findComponent = function (c, o) {
		var kids = o.childNodes;
		for (var i = 0; i < kids.length; i++) {
			if (kids[i].className && kids[i].className == c) {
				return kids[i];
			}
		}
	};
	
	function findOffsetTop (o) {
		var t = 0;
		if (o.offsetParent) {
			while (o.offsetParent) {
				t += o.offsetTop;
				o  = o.offsetParent;
			}
		}
		return t;
	};
	this._scrollbarClick = function (e) {
		if (self._disabled) return false;
		self._Mask.style.display = "block";
		e = e ? e : event;
		if (!e.target) e.target = e.srcElement;
		
		if (e.target.className.indexOf("Scrollbar-Up") > -1) self._scrollUp(e);
		else if (e.target.className.indexOf("Scrollbar-Down") > -1) self._scrollDown(e);
		else if (e.target.className.indexOf("Scrollbar-Track") > -1) self._scrollTrack(e);
		else if (e.target.className.indexOf("Scrollbar-Handle") > -1) self._scrollHandle(e);
		
		self._tempTarget = e.target;
		self._selectFunc = document.onselectstart;
		document.onselectstart = function () {return false;};
		
		self.eventHandler(e.target, "mousedown");
		self._addEvent(document, "mouseup", self._stopScroll, false);
		
		return false;
	};
	this._scrollbarDrag = function (e) {
		e = e ? e : event;
		var t = parseInt(self._yHandle.style.top);
		var v = e.clientY + document.body.scrollTop - self._trackTop;
		with (self._yHandle.style) {
			if (v >= self._trackHeight - self._handleHeight + self._grabPoint)
				top = self._trackHeight - self._handleHeight +"px";
			else if (v <= self._grabPoint) top = "0px";
			else top = v - self._grabPoint +"px";
			self._y = parseInt(top);
		}
		
		self._moveContent();
	};
	this._scrollbarWheel = function (e) {
		e = e ? e : event;
		var dir = 0;
		if (e.wheelDelta >= 120) dir = -1;
		if (e.wheelDelta <= -120) dir = 1;
		
		self.scrollBy(0, dir * 20);
		e.returnValue = false;
	};
	this._startScroll = function (x, y) {
		this._tempDistX = x;
		this._tempDistY = y;
		this._scrollTimer = window.setInterval(function () {
			self.scrollBy(self._tempDistX, self._tempDistY); 
		}, 40);
	};
	this._stopScroll = function () {
		self._Mask.style.display = "none";
		self._removeEvent(document, "mousemove", self._scrollbarDrag, false);
		self._removeEvent(document, "mouseup", self._stopScroll, false);
		
		if (self._selectFunc) document.onselectstart = self._selectFunc;
		else document.onselectstart = function () { return true; };
		
		if (self._scrollTimer) window.clearInterval(self._scrollTimer);
		self.eventHandler (self._tempTarget, "mouseup");
	};
	this._scrollUp = function (e) {this._startScroll(0, -this._scrollDist);};
	this._scrollDown = function (e) {this._startScroll(0, this._scrollDist);};
	this._scrollTrack = function (e) {
		var curY = e.clientY + document.body.scrollTop;
		this._scroll(0, curY - this._trackTop - this._handleHeight/2);
	};
	this._scrollHandle = function (e) {
		var curY = e.clientY + document.body.scrollTop;
		this._grabPoint = curY - findOffsetTop(this._yHandle);
		this._addEvent(document, "mousemove", this._scrollbarDrag, false);
	};
	this._scroll = function (x, y) {
		if (y > this._trackHeight - this._handleHeight) 
			y = this._trackHeight - this._handleHeight;
		if (y < 0) y = 0;
		
		this._yHandle.style.top = y +"px";
		this._y = y;
		
		this._moveContent();
	};
	this._moveContent = function () {
		this._src.scrollTo(0, Math.round(this._y * this._ratio));
	};
	
	this.scrollBy = function (x, y) {
		this._scroll(0, (-this._src._y + y)/this._ratio);
	};
	this.scrollTo = function (x, y) {
		this._scroll(0, y/this._ratio);
	};
	this.swapContent = function (o, w, h) {
		this._removeEvent(this._src.content, "mousewheel", this._scrollbarWheel, false);
		this._src.swapContent(o, w, h);
		this.reset();
	};
	
	this.reset();
};

////////////////////////////////////////////////////////
function ScrollerTween (o, t, s) {
	var self = this;
	
	this._tweenTo = function (y) {
		if (self._idle) {
			var tHeight = self._parent._src ? self._parent._src.totalHeight : self._parent.totalHeight;
			var vHeight = self._parent._src ? self._parent._src.viewableHeight : self._parent.viewableHeight;
			var scrollY = self._parent._src ? self._parent._src._y : self._parent._y;
			
			if (y < 0) y = 0;
			if (y > tHeight - vHeight) y = tHeight - vHeight;
			
			var dist = y - (-scrollY);
			
			self._inc = 0;
			self._timer = null;
			self._values = [];
			self._idle = false;
			for (var i = 0; i < self.steps.length; i++) {
				self._values[i] = Math.round((-scrollY) + dist * (self.steps[i] / 100));
			}
			self._timer = window.setInterval(function () {
				self._parent.scrollTo(0, self._values[self._inc]);
				if (self._inc == self.steps.length-1) {
					window.clearTimeout(self._timer);
					self._idle = true;
				} else self._inc++;
			}, self.stepDelay);
		}
	};
	this._tweenBy = function (y) {
		var scrollY = self._parent._src ? self._parent._src._y : self._parent._y;
		self._tweenTo(-scrollY + y);
	};
	this._trackTween = function (e) {
		e = e ? e : event;
		self._parent.canScroll = false;
		var curY = e.clientY + document.body.scrollTop;
		self._tweenTo((curY - self._parent._trackTop - self._parent._handleHeight/2) * self._parent._ratio);
	};
	
	this.stepDelay = 40;
	this.steps   = s?s:[0,25,50,70,85,95,97,99,100];
	this._values = [];
	this._parent = o;
	this._timer  = [];
	this._idle   = true;
	
	o.tweenTo = this._tweenTo;
	o.tweenBy = this._tweenBy;
	o.trackTween = this._trackTween;
	
	if (t) o._scrollTrack = function (e) {
		this.trackTween(e);
	};
};