jmaki.namespace("jmaki.ext");

jmaki.ext.loadImage = function(_src) {

    // used for transparency filter setting  
    var isIE6 = /MSIE 6/i.test(navigator.userAgent);        
       var img;
       if (isIE6 && /.png/i.test(_src)) {
           img = document.createElement("div");
           img.style.height = "15px";
           img.style.width = "15px";
           img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader (src='" + _src + "', sizingMethod='image')";
       } else {
         img = document.createElement("img");
         img.src = _src;
       }
       return img;
};    

jmaki.namespace("jmaki.extensions.jmaki.lightboxManager");
    
jmaki.extensions.jmaki.lightboxManager.Extension = function(wargs) {
    _widget = this;
    _widget.lightboxes = {};
    //jmaki.loadStyle(wargs.widgetDir + "/lightbox.css");
    var publish = "/jmaki/lightboxManager";
    var subscribe = ["/jmaki/lightboxManager"];
    if (wargs.publish) publish = wargs.publish;
    var themes = {
    	  kame : 'green',
    	  ocean : 'blue'
    	};
    var currentTheme = themes['ocean'];
    if (jmaki.config && jmaki.config.globalTheme)  {
        if (themes[jmaki.config.globalTheme]) currentTheme = themes[jmaki.config.globalTheme];
    }
    if (wargs.args && wargs.args.theme) {
        currentTheme = wargs.args.theme;
    }
    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);
    }  
    
    this.postLoad = function() {
        if (wargs.subscribe){
            if (typeof wargs.subscribe == "string") {
                subscribe = [];
                subscribe.push(wargs.subscribe);
            } else {
                subscribe = wargs.subscribe;
            }
        }          
        _widget.subs = [];
        
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/addLightbox", _widget.addLightbox);
            doSubscribe(subscribe[_i]  + "/hideLightbox", _widget.hideLightbox);
            doSubscribe(subscribe[_i]  + "/removeLightbox", _widget.removeLightbox);
            doSubscribe(subscribe[_i]  + "/showLightbox", _widget.showLightbox);
        } 
    };
    
    this.addLightbox= function(o) {   	
        var i;
        if (o.message) o = o.message;
        else i = o; 
        if (i.value) i = i.value;
        if (!i.id) i.id = jmaki.genId();

        var frame = document.createElement("div");  
        if (i.startWidth)frame.style.width = i.startWidth + "px"; 
        frame.className = "jmk-wt-lightbox";
        
        var titleBar = document.createElement("div");
        titleBar.className = "jmk-wt-lightbox-titlebar jmk-wt-lightbox-titlebar-" + currentTheme;
        
        i.titleNode = document.createElement("div");
        titleBar.appendChild(i.titleNode);
        
        i.titleNode.className = "jmk-wt-lightbox-title";
        i.titleNode.innerHTML = i.label;       
        frame.appendChild(titleBar);        
        // create the icons
        var icons = document.createElement("div");
        icons.className = "jmk-wt-lightbox-icons";
        titleBar.appendChild(icons);
        
        var close = jmaki.ext.loadImage(wargs.widgetDir + "/images/lb-close.png");
        close.frameId = i.id;
        close.onclick = function(e) {
            var _t;
            if (!e) _t = window.event.srcElement;
            else _t = e.target;
            if (_t.frameId){
                _widget.hideLightbox(_t.frameId);  
                _widget.removeLightbox(_t.frameId);  
            }
        };
        icons.appendChild(close);     
        if (i.content || i.include) {
            cPane = document.createElement("div");
            cPane.id = i.id;
            var _overflowX = i.overflowX;
            var _overflowY = i.overflowY;
            var _overflow = i.overflow;
            if (!_overflowX) _overflowX = "hidden";
            if (!_overflowY) _overflowY = "hidden";
            if (!_overflow) _overflow = "hidden";
            i.dcontainer = new jmaki.DContainer(
            {target:  cPane,
             useIframe : i.iframe,
             overflow : _overflow,
             overflowY : _overflowY,
             overflowX : _overflowX,
             content : i.content,
             startHeight : i.startHeight -30,
             startWidth : i.startWidth -2,
             autosize : false});
             i.contentNode = cPane;
        } else if (i.contentNode){
            cPane = i.contentNode
        } else if (i.widget) {
        	cPane = document.createElement("div");
            var _w = document.createElement("div");
            cPane.appendChild(_w);
            var wf = jmaki.getExtension("widgetFactory");
            wf.loadWidget(
                { widget : i.widget,
                  container :_w
                });
          //  _w.style.height = i.startHeight - 2 + "px";            
        } else {
           jmaki.log("Could not create Lightbox. Need a content, widget, or include");
           return null;
        }
        cPane.style.clear = "both";
        frame.appendChild(cPane);
        
        if (i.startHeight) {
            frame.style.height = i.startHeight + "px";
            frame.startHeight = i.startHeight;
        }             
        if (i.include && i.dcontainer)  i.dcontainer.loadURL(i.include);
        i.node = frame;
        _widget.lightboxes[i.id] = i;
        frame.style.visibility = "hidden";
        document.body.appendChild(frame);
        var wd = _widget.getWindowDimensions();
        var _t =  (wd.h/2) - (frame.clientHeight/2) + wd.scrollY;        
        if (_t < 1) _t = 1;
        frame.style.top = _t + "px";
        frame.style.left = (wd.w/2) - (frame.clientWidth /2) + wd.scrollX + "px";
        frame.style.visibility = "visible";    

        i.hide = function(){
        	if (i.modal) _widget.disableBlocker();
            frame.style.display = "none";
            jmaki.publish(publish + "/lightboxHidden", { targetId : i.id});
        };
        i.show = function(){       	
        	if (i.modal)_widget.enableBlocker(); 
            frame.style.display = "block";
        };       
        if (i.modal)_widget.enableBlocker();        
        return i;
    };

    this.removeLightbox = function(o) {
        var targetId;
        if (o.message) o = o.message;
        if (o.targetId) targetId = o.targetId;
        else targetId = o;      
        var _f = _widget.lightboxes[targetId];
        if (_f) {
            jmaki.clearWidgets(_f.node);            
            _f.node.parentNode.removeChild(_f.node);
            if (typeof _f.destroy == "function")_f.destroy();
            delete _widget.lightboxes[targetId];
        }
    };     
    
    this.hideLightbox = function(o) {
        var targetId;
        if (o.message) o = o.message;
        if (o.targetId) targetId = o.targetId;
        else targetId = o;      
        var _f = _widget.lightboxes[targetId];
        if (_f) {
            _f.hide();
        }
    };

    this.enableBlocker = function(opacity) {   	
       opacity = 50;
       if (!_widget.blocker) {
           _widget.blocker = document.createElement("div");
           _widget.blocker.id = wargs.uuid + "_blocker";
           _widget.blocker.style.background = "#000";
           _widget.blocker.style.opacity = opacity / 100;
           _widget.blocker.style.filter = "alpha(opacity='" + opacity + "')";
           _widget.blocker.style.position = "absolute";
           document.body.appendChild(_widget.blocker);

       }
       var _dim = _widget.getWindowDimensions();
       var _h = _dim.docHeight;
       if (_dim.h > _h) _h = _dim.h;
       _widget.blocker.style.width = (_dim.w) + "px";
       _widget.blocker.style.height = _h + "px";        
       _widget.blocker.style.zIndex = 9996;
       _widget.blocker.style.left ="0px";
       _widget.blocker.style.top  = "0px"; 
       _widget.blocker.style.display = "block";
    };
   
    this.disableBlocker = function() {
        if (_widget.blocker) _widget.blocker.style.display = "none";
    };
    
    this.showLightbox = function(o) {
        var targetId;
        if (o.message) o = o.message;
        if (o.targetId) targetId = o.targetId;
        else targetId = o;            
        var _f = _widget.lightboxes[targetId];
        if (_f) {
        	if (_widget.modal)_widget.enableBlocker();  
            _f.show();
        }
    };
   /**
     * Return the dimensions and the region of the page scrolled to.
    */  
    this.getWindowDimensions = function() {
        var _w = 0;
        var _h = 0;
        var _sx = 0;
        var _sy = 0;
        var _docHeight;
        if (document.body && document.body.clientHeight){
        	_docHeight = document.body.clientHeight;
        }
        if (window.innerWidth) {
            _w = window.innerWidth;
            _h = window.innerHeight; 
        } else if (document.documentElement &&
            document.documentElement.clientHeight) {      	
            _w = document.documentElement.clientWidth;
            _h = document.documentElement.clientHeight;
        } else if (document.body) {    
            _w = document.body.clientWidth;
            _h = document.body.clientHeight;
        }
        if (window.pageYOffset) {          
            _sx = window.pageXOffset;
            _sy = window.pageYOffset;
        } else if (document.documentElement &&
            document.documentElement.scrollTop) {
            _sx = document.documentElement.scrollLeft;
            _sy = document.documentElement.scrollTop;            
        } else if (document.body) {           
            _sx = document.body.scrollLeft;
            _sy = document.body.scrollTop;
        }
        return {w : _w, h: _h, docHeight :_docHeight,
                scrollX : _sx, scrollY : _sy};
    };
};
