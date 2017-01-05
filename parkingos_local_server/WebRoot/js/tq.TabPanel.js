/**
  * @description {Class} TabPanel
  * This is the main class of tab panel.
  */
TabPanel = function(config){
  /**
   * @description {Config} renderTo   
   * {String or JQuery object} To specify where tab panel will be placed. It could be a DOM id or jquery object.
   */
  this.renderTo = config.renderTo || $(document.body);
  /**
   * @description {Config} border   
   * {Boolean} To show border or not.
   */
  this.border = config.border;
  this.render = typeof this.renderTo == 'string' ? $('#'+this.renderTo) : this.renderTo;
  /**
   * @description {Config} widthResizable   
   * {Boolean} Whether end user can change panel width by mouse dragging.
   */
  this.widthResizable = config.widthResizable;
  /**
   * @description {Config} heightResizable   
   * {Booean} Whether end user can change panel height by mouse dragging.
   */
  this.heightResizable = config.heightResizable;
  /**
   * @description {Config} autoResizable  
   * {Boolean} Whether panel resizes itself according to content.
   */
  this.autoResizable = config.autoResizable ? true : false;
  /**
   * @description {Config} width   
   * {String} Initialization width. 
   * @sample // width config, in px or percentage.
   * width : '200px'// or '100%'.
   */
  this.width = config.width || '100%';
  /**
   * @description {Config} height  
   * {String} Initialization height. 
   * @sample //heigh config 
   * height : '200px'// or '100%'.
   */
  this.height = config.height || '100%';
  /**
   * @description {Config} items   
   * {Array} Tab items array.
   */
  this.items = config.items;
  /**
   * @description {Config} active   
   * {Number} Active tab index. Base on 0.
   */
  this.active = config.active || 0;
  //this is tab array.
  this.tabs = [];
  this.scrolled = false;
  this.tabWidth = 110 + 4;
  this.fixNum = 2;
  this.scrollFinish = true;
  this.maxLength = config.maxLength || -1;
  this.maxzindex = 0;
  
  this.init();
};

TabPanel.prototype = {
  //initialization
  init : function(){
    
    var tabEntity = this;
	
    if(this.autoResizable){
      this.widthResizable = this.heightResizable = true;
  	  this.render.css('overflow', 'hidden');
  	  $(window).resize(function(){
        window.setTimeout(function(){
          tabEntity.resize();
        }, 200);
  	  });
    }
  
    this.render.width(this.width);
    this.render.height(this.height);

	  var hwFix = this.border!='none'?2:0;

    this.tabpanel = $('<DIV></DIV>');
    this.tabpanel.addClass('tabpanel');
    this.tabpanel.width(this.render.width()-hwFix);
    this.tabpanel.height(this.render.height()-hwFix);
    this.render.append(this.tabpanel);
    
    //construct container
    this.tabpanel_tab_content = $('<DIV></DIV>');
    this.tabpanel_tab_content.addClass('tabpanel_tab_content');
    this.tabpanel_tab_content.appendTo(this.tabpanel);
    
    //construct left scroll button
    this.tabpanel_left_scroll = $('<DIV></DIV>');
    this.tabpanel_left_scroll.bind('click',function(){tabEntity.moveLeft();});
    this.tabpanel_left_scroll.addClass('tabpanel_left_scroll');
    this.tabpanel_left_scroll.addClass('display_none');
    this.tabpanel_left_scroll.bind('mouseover', function(){
      var l = $(this);
      l.addClass('tabpanel_scroll_over');
      l.bind('mouseout', function(){
        l.unbind('mouseout');
        l.removeClass('tabpanel_scroll_over');
      });
    })
    this.tabpanel_left_scroll.appendTo(this.tabpanel_tab_content);
    
    //construct right scroll button
    this.tabpanel_right_scroll = $('<DIV></DIV>');
    this.tabpanel_right_scroll.bind('click',function(){tabEntity.moveRight();});
    this.tabpanel_right_scroll.addClass('tabpanel_right_scroll');
    this.tabpanel_right_scroll.addClass('display_none');
    this.tabpanel_right_scroll.bind('mouseover', function(){
      var r = $(this);
      r.addClass('tabpanel_scroll_over');
      r.bind('mouseout', function(){
        r.unbind('mouseout');
        r.removeClass('tabpanel_scroll_over');
      });
    })
    this.tabpanel_right_scroll.appendTo(this.tabpanel_tab_content);
    
    
    this.tabpanel_move_content = $('<DIV></DIV>');
    this.tabpanel_move_content.addClass('tabpanel_move_content');
    this.tabpanel_move_content.appendTo(this.tabpanel_tab_content);
    
    this.tabpanel_mover = $('<UL></UL>');
    this.tabpanel_mover.addClass('tabpanel_mover');
    this.tabpanel_mover.appendTo(this.tabpanel_move_content);
    
    this.tabpanel_tab_spacer = $('<DIV></DIV>');
    this.tabpanel_tab_spacer.addClass('tabpanel_tab_spacer');
    this.tabpanel_tab_spacer.appendTo(this.tabpanel_tab_content);
    
    //content div
    this.tabpanel_content = $('<DIV></DIV>');
    this.tabpanel_content.addClass('tabpanel_content');
    this.tabpanel_content.appendTo(this.tabpanel);
    
    var t_w = this.tabpanel.width();
    var t_h = this.tabpanel.height();

    if(this.border=='none')
    {
	  this.tabpanel.css('border','none');
    }

	  this.tabpanel_tab_content.width(t_w);
    this.tabpanel_content.width(t_w);
	  this.tabpanel_content.height(t_h-this.tabpanel_tab_content.get(0).offsetHeight);
    
    this.update();

    for(var i=0; i<this.items.length; i++)
    {
	  this.items[i].notExecuteMoveSee = true;
      this.addTab(this.items[i]);
    }
    //activate tab
    if(this.active>=0)
      this.show(this.active, false);
  },
  //scroll left
  moveLeft : function(){
    if(this.scrollFinish)
    {
      this.disableScroll();
      this.scrollFinish = false;
      Fader.apply(this, new Array({
        element:this.tabpanel_mover,
        style:'marginLeft',
        num:this.tabWidth,
        maxMove:this.maxMove,
        onFinish : this.useableScroll
      }));
      this.run();
    }
  },
  //scroll right
  moveRight : function(){
    if(this.scrollFinish)
    {
      this.disableScroll();
      this.scrollFinish = false;
      Fader.apply(this, new Array({
        element:this.tabpanel_mover,
        style:'marginLeft',
        num:this.tabWidth*-1,
        maxMove:this.maxMove,
        onFinish : this.useableScroll
      }));
      this.run();
    }
  },
  //scroll to end of left side
  moveToLeft : function(){
    //no scroll button show
    if(this.scrolled && this.scrollFinish)
    {
      this.disableScroll();
      this.scrollFinish = false;
      var marginLeft = parseInt(this.tabpanel_mover.css('marginLeft'))*-1;
      Fader.apply(this, new Array({
        element : this.tabpanel_mover,
        style : 'marginLeft',
        num : marginLeft, 
        maxMove : this.maxMove,
        interval : 20,
		step : (marginLeft/10)<10?10:marginLeft/10,
        onFinish : this.useableScroll
      }));
      this.run();
    }
  },
  
  //scroll to end of left side
  moveToRight : function(){
    if(this.scrolled && this.scrollFinish)
    {
      this.disableScroll();
      this.scrollFinish = false;
      var marginLeft = parseInt(this.tabpanel_mover.css('marginLeft'))*-1;
      var liWidth = this.tabpanel_mover.children().length*this.tabWidth;
      var cWidth = this.tabpanel_move_content.width();
      var num = (liWidth - cWidth - marginLeft + this.fixNum)*-1;
      Fader.apply(this, new Array({
        element:this.tabpanel_mover,
        style:'marginLeft',
        num:num,
        maxMove:this.maxMove,
		step:(num*-1/10)<10?10:num*-1/10,
        onFinish : this.useableScroll
      }));
      this.run();
    }
  },
  
  //move to visible position/////////////////////////////////////////////////////////
  moveToSee : function(position){
    if(this.scrolled)
    {
      var liWhere = this.tabWidth * position;
      var ulWhere = parseInt(this.tabpanel_mover.css('marginLeft'));
      var moveNum;
      if(ulWhere<=0)
      {
        moveNum = (ulWhere + liWhere)*-1;
        if(((moveNum+ulWhere)*-1) >= this.maxMove)
          this.moveToRight();
        else
        {
          this.disableScroll();
          this.scrollFinish = false;
          Fader.apply(this, new Array({
            element:this.tabpanel_mover,
            style:'marginLeft',
            num:moveNum,
            maxMove:this.maxMove,
			step:(moveNum/10)<10?10:moveNum/10,
            onFinish : this.useableScroll
          }));
          this.run();
        }
      }
      else
      {
        moveNum = (liWhere - ulWhere) * -1;
        if((moveNum*-1) >= this.maxMove)
          this.moveToRight();
        else
        {
          this.disableScroll();
          this.scrollFinish = false;
          Fader.apply(this, new Array({
            element:this.tabpanel_mover,
            style:'marginLeft',
            num:moveNum,
            maxMove:this.maxMove,
            onFinish : this.useableScroll
          }));
          this.run();
        }
      }
    }
  },
  //disable scroll buttons
  disableScroll : function(){
    this.tabpanel_left_scroll.addClass('tabpanel_left_scroll_disabled');
    this.tabpanel_left_scroll.attr('disabled',true);
    this.tabpanel_right_scroll.addClass('tabpanel_right_scroll_disabled');
    this.tabpanel_right_scroll.attr('disabled', true);
  },
  
  //to determin whether we can still scroll
  useableScroll : function(){
    var tabEntity = this;
    if(this.scrolled)
    {
      //we came to the end of left side
      if(parseInt(tabEntity.tabpanel_mover.css('marginLeft')) == 0)
      {
        //disble left scroll button
        tabEntity.tabpanel_left_scroll.addClass('tabpanel_left_scroll_disabled');
        tabEntity.tabpanel_left_scroll.attr('disabled',true);
        //
        tabEntity.tabpanel_right_scroll.removeClass('tabpanel_right_scroll_disabled');
        tabEntity.tabpanel_right_scroll.removeAttr('disabled');
      }
      //we came to the end of right side
      else if(parseInt(tabEntity.tabpanel_mover.css('marginLeft'))*-1 == tabEntity.maxMove)
      {
        tabEntity.tabpanel_left_scroll.removeClass('tabpanel_left_scroll_disabled');
        tabEntity.tabpanel_left_scroll.removeAttr('disabled',true);
        tabEntity.tabpanel_right_scroll.addClass('tabpanel_right_scroll_disabled');
        tabEntity.tabpanel_right_scroll.attr('disabled');
      }
      else
      {
        tabEntity.tabpanel_left_scroll.removeClass('tabpanel_left_scroll_disabled');
        tabEntity.tabpanel_left_scroll.removeAttr('disabled',true);
        tabEntity.tabpanel_right_scroll.removeClass('tabpanel_right_scroll_disabled');
        tabEntity.tabpanel_right_scroll.removeAttr('disabled');
      }
    }
    
    tabEntity.scrollFinish = true;
  },
  //update style
  update : function(){
    var cWidth = this.tabpanel_tab_content.width();
    if(this.scrolled)
      cWidth -= (this.tabpanel_left_scroll.width()+this.tabpanel_right_scroll.width());
    this.tabpanel_move_content.width(cWidth);
    this.maxMove = (this.tabpanel_mover.children().length*this.tabWidth) - cWidth + this.fixNum;
  },
  //to show scroll button if needed.
  showScroll : function(){
    var liWidth = this.tabpanel_mover.children().length*this.tabWidth;
    var tabContentWidth = this.tabpanel_tab_content.width();
    if(liWidth > tabContentWidth && !this.scrolled){
      this.tabpanel_move_content.addClass('tabpanel_move_content_scroll');
      this.tabpanel_left_scroll.removeClass('display_none');
      this.tabpanel_right_scroll.removeClass('display_none');
      this.scrolled = true;
    }
    else if(liWidth < tabContentWidth && this.scrolled)
    {
	    this.moveToLeft();
      this.tabpanel_move_content.removeClass('tabpanel_move_content_scroll');
      this.tabpanel_left_scroll.addClass('display_none');
      this.tabpanel_right_scroll.addClass('display_none');
      this.scrolled = false;
	    this.scrollFinish = true;
    }
  },

  /**
   * @description {Method} addTab To add a new tab.
   * @param {Object} item Object for item profile.
   * @sample  //to add a new tab 
   * addTab({id:"newtabid", 
   *    title:"I am new" ,
   *    html:"some new message goes here", 
   *    closable: true, 
   *    disabled:false, 
   *    icon:"image/new.gif"
   * });   
   */
  addTab : function(tabitem){
	
    if(this.maxLength!=-1 && this.maxLength<=this.tabs.length){
	    return false;
    }
  
    tabitem.id = tabitem.id || Math.uuid();
    
    //if id exist, switch to that one
    if($('#'+tabitem.id).length>0)
    {
      this.show(tabitem.id, false);
    }
    else if(this.scrollFinish)
    {
      var tabEntity = this;
  
      var tab = $('<LI></LI>');
      tab.attr('id', tabitem.id);

      tab.appendTo(this.tabpanel_mover);
  
      var title = $('<DIV></DIV>');
      title.text(tabitem.title);
      title.appendTo(tab);

	    var wFix = tabitem.closable==false ? 0 : 5;
      if(tabitem.icon) {
        title.addClass('icon_title');
        title.css('background-image', 'url("'+tabitem.icon+'")');
        if(title.width()>(this.tabWidth-35-wFix)) {
          title.width((this.tabWidth-50-wFix));
          title.attr('title', tabitem.title);
          tab.append('<DIV>...</DIV>');
        }
      } else {
        title.addClass('title');
        if(title.width()>(this.tabWidth-19-wFix)) {
          title.width((this.tabWidth-30-wFix));
          title.attr('title', tabitem.title);
          tab.append('<DIV>...</DIV>');
        }
      }
      
      var closer = $('<DIV></DIV>');
      closer.addClass('closer');
      closer.attr('title', '关闭');
      closer.appendTo(tab);
      
      var content = $('<DIV></DIV>');
      content.addClass('html_content');
      content.appendTo(this.tabpanel_content);

      var child_frame = content.find('iframe');
      /*
      if(child_frame.length==1)
      {
        child_frame.attr('id', tabitem.id+'Frame');
        child_frame.attr('name', tabitem.id+'Frame');
      }*/
      
      var activedTabIndex = this.tabpanel_mover.children().index(this.tabpanel_mover.find('.active')[0]);
      
      if(activedTabIndex < 0)
        activedTabIndex = 0;
      if(this.tabs.length > activedTabIndex)
        tabitem.preTabId = this.tabs[activedTabIndex].id
      else
      tabitem.preTabId = '';
      tabitem.tab = tab;
      tabitem.title = title;
      tabitem.closer = closer;
      tabitem.content = content;
      tabitem.disable = tabitem.disable==undefined ? false : tabitem.disable;
      tabitem.closable = tabitem.closable==undefined ? true : tabitem.closable;      
      if(tabitem.closable==false)
        closer.addClass('display_none');
      
      if(tabitem.disabled==true) {
        tab.attr('disabled', true);
        title.addClass('.disabled');
      }
  
      this.tabs.push(tabitem);
      
      tab.bind('click', function(position){
        return function(){
          tabEntity.show(position, false);
        };
      }(this.tabs.length-1));
      

      closer.bind('click', function(position){
        return function(){
          tabEntity.kill(position);
        };
      }(this.tabs.length-1));
      
      if(tabitem.closable)
      {
        tab.bind('dblclick', function(position){
          return function(){
            tabEntity.kill(position);
          };
        }(this.tabs.length-1));
      }
      
      if(!tabitem.lazyload) {
        this.show(this.tabs.length-1, tabitem.notExecuteMoveSee);
      }
      
      this.showScroll();
      this.update();

      if(!tabitem.lazyload && !tabitem.notExecuteMoveSee) {
        this.moveToRight();
      }
    }
  },
  /**
   * @description {Method} getTabPosision To get tab index.
   * @param {String} id item id.
   * @return {Number} index of tab.
   */
  getTabPosision : function(tabId){
    if(typeof tabId == 'string')
    {
      for(var i=0; i<this.tabs.length; i++)
      {
        if(tabId == this.tabs[i].id)
        {
          tabId = i;
          break;
        }
      }
    }
    return tabId;
  },
  /**
   * @description {Method} refresh To refresh tab content.
   * @param {String} id item id.
   */
  refresh : function(position)
  {
    position = this.getTabPosision(position);
    if(typeof position == 'string')
      return false;
    else
    {
      //if IFRAME exists, refresh the sub frames
      var iframes = this.tabs[position].content.find('iframe');
      if(iframes.length>0)
      {
        var frameId = this.tabs[position].id+'Frame';
        this.iterateFlush(window.frames[frameId]);
      }
    }
  },
  
  iterateFlush : function(iframeObj) {
    
    if(iframeObj.window.frames.length>0)
    {
      for(var i=0; i<iframeObj.window.frames.length; i++)
      {
        this.iterateFlush(iframeObj.window.frames[i]);
      }
    }
    else
    {
      if(iframeObj.document.forms.length>0)
      {
        for(var i=0; i<iframeObj.document.forms.length; i++)
        {
          try {
            iframeObj.document.forms[i].submit();
          }
          catch(e) {
            iframeObj.location.reload();
          }
        }
      }
      else
      {
        iframeObj.location.reload();
      }
    }
  },
  show : function(position, notExecuteMoveSee){
    if(this.tabs.length<1)
      return false;
    position = this.getTabPosision(position);
    if(typeof position == 'string')
      position = 0;
    if(this.scrollFinish)
    {
      if(position >= this.tabs.length)
      {
        position = 0;
      }
      this.tabs[position].content.css('z-index', ++this.maxzindex);
      if(this.tabs[position].tab.hasClass('active'))
      {
        if(!notExecuteMoveSee)
        {
          this.moveToSee(position);
        }
      }
      else
      {
        //load those never loaded
        if(this.tabs[position].content.html()=='') {
          this.tabs[position].content.html(this.tabs[position].html);
        }
        this.tabpanel_mover.find('.active').removeClass('active');
        this.tabs[position].tab.addClass('active');
        if(!notExecuteMoveSee)
        {
          this.moveToSee(position);
        }
      }
    }
  },
  /**
   * @description {Method} kill To close tab.
   * @param {String} id item id.
   */
  kill : function(position){
  
    var tabEntity = this;
    //get tab index
    position = this.getTabPosision(position);
    
    var preTabId = this.tabs[position].preTabId;
    
    //detroy DOM
    this.tabs[position].closer.remove();
    this.tabs[position].title.remove();
    this.tabs[position].tab.remove();
    this.tabs[position].content.remove();
    //remove from tabs 
    this.tabs.splice(position,1);
    
    //rebind event handler because index changed.
    for(var i=0 ; i<this.tabs.length; i++)
    {
      this.tabs[i].tab.unbind('click');
      this.tabs[i].tab.bind('click', function(i){
        return function(){
          tabEntity.show(i, false);
        };
      }(i));
      this.tabs[i].closer.unbind('click');
      this.tabs[i].closer.bind('click', function(i){
        return function(){
          tabEntity.kill(i);
        };
      }(i));
      if(this.tabs[i].closable)
      {
        this.tabs[i].tab.unbind('dblclick');
        this.tabs[i].tab.bind('dblclick', function(i){
          return function(){
            tabEntity.kill(i);
          };
        }(i));
      }
    }
    //update width
    this.update();
    //to scroll bar 
    this.showScroll();
    //show last 
    this.show(preTabId, false);
  },
  
  /**
   * @description {Method} getTabsCount To get how many tabs are in the panel.
   * @return {Number} Number of tabs .
   */
  getTabsCount : function(){
    return this.tabs.length;
  },

  /**
   * @description {Method} setTitle To set tab title.
   * @param {String} id Item id.
   * @param {String} title Tab title.
   */
  setTitle : function(position,title){
    position = this.getTabPosision(position);
    if(position < this.tabs.length)
      this.tabs[position].title.text(title);
  },

  /**
   * @description {Method} getTitle To get tab title.
   * @param {String} id item id.
   */
  getTitle : function(position){
    position = this.getTabPosision(position);
    return this.tabs[position].title.text();
  },

  /**
   * @description {Method} setContent To set tab title.
   * @param {String} id Item id.
   * @param {String} title Tab inner html.
   */
  setContent : function(position,content){
    position = this.getTabPosision(position);
    if(position < this.tabs.length)
      this.tabs[position].content.html('');
      this.tabs[position].content.html(content);
  },

  /**
   * @description {Method} getContent To get tab inner html.
   * @param {String} id item id.
   */
  getContent : function(position){
    position = this.getTabPosision(position);
    return this.tabs[position].content.html();
  },

  /**
   * @description {Method} setDisable To enable or disable tab.
   * @param {String} id Item id.
   * @param {Booleaan} True for disabled, false for enabled.
   */
  setDisable : function(position,disable){
    position = this.getTabPosision(position);
    if(position < this.tabs.length){
      this.tabs[position].disable = disable;
      if(disable){
        this.tabs[position].tab.attr('disabled',true);
        this.tabs[position].title.addClass('.disabled');
      }else{
        this.tabs[position].tab.removeAttr('disabled');
        this.tabs[position].title.removeClass('.disabled');
      }
    }
  },

  /**
   * @description {Method} getDisable To determine whether tab is disabled or not.
   * @param {String} id item id.
   */
  getDisable : function(position){
    position = this.getTabPosision(position);
    return this.tabs[position].disable;
  },

   /**
   * @description {Method} setClosable To enable or disable end user to close tab.
   * @param {String} id Item id.
   * @param {Booleaan} True for closable, false for not.
   */
  setClosable : function(position,closable){
    position = this.getTabPosision(position);
    if(position < this.tabs.length){
      this.tabs[position].closable = closable;
      if(closable){
        this.tabs[position].closer.addClass('display_none');
      }else{
        this.tabs[position].closer.addClass('closer');
        this.tabs[position].closer.removeClass('display_none');
      }
    }
  },

  /**
   * @description {Method} getClosable To determine whether tab is closable or not.
   * @param {String} id item id.
   */
  getClosable : function(position){
    position = this.getTabPosision(position);
    return this.tabs[position].closable;
  },
	
	/**
   * @description {Method} getActiveIndex To get index of active tab.
   * @return {Number} index of active tab.
   */
	getActiveIndex : function(){
		return this.tabpanel_mover.children().index(this.tabpanel_mover.find('.active')[0]);	
	},
  
  /**
   * @description {Method} getActiveTab To get active tab.
   * @return {Object} Profile of active tab.
   */
  getActiveTab : function(){
    var activeTabIndex = this.tabpanel_mover.children().index(this.tabpanel_mover.find('.active')[0]);
    if(this.tabs.length > activeTabIndex)
      return this.tabs[activeTabIndex];
    else
      return null;
  },
  resize : function(){
  	var hwFix = this.border == 'none' ? 0 : 2;

  	if(this.widthResizable) {
		
  	  this.width = this.render.width();
  	  this.tabpanel.width(this.width-hwFix);
  	  this.tabpanel_tab_content.width(this.width-hwFix);
  	  this.tabpanel_content.width(this.width-hwFix);
  	}
  	if(this.heightResizable) {
      this.height = this.render.height();
  	  this.tabpanel.height(this.height-hwFix);
  	  this.tabpanel_content.height(this.height-this.tabpanel_tab_content.get(0).offsetHeight);
  	}
  
  	this.showScroll();
  	this.useableScroll();
    this.update();
	
  	var entity = this;
  	setTimeout(function(){entity.moveToSee(entity.getActiveIndex());}, 200);
		
  },
  
  /**
   * @description {Method} setRenderWH To set width and height of the panel.
   * @param {Object} wh width and height.
   * @sample //To set tab height and width  
   * setRenderWH({width:'200px', height:'400px'});   
   */
  setRenderWH : function(wh) {
    if(wh) {
      if(wh.width!=undefined) {
        this.render.width(wh.width);
      }
      if(wh.height!=undefined) {
        this.render.height(wh.height);
      }
      this.resize();
    }
  }
};