var gadgets=gadgets||{};
gadgets.window=gadgets.window||{};
(function(){var A;
gadgets.window.getViewportDimensions=function(){var B,C;
if(self.innerHeight){B=self.innerWidth;
C=self.innerHeight
}else{if(document.documentElement&&document.documentElement.clientHeight){B=document.documentElement.clientWidth;
C=document.documentElement.clientHeight
}else{if(document.body){B=document.body.clientWidth;
C=document.body.clientHeight
}else{B=0;
C=0
}}}return{width:B,height:C}
};
gadgets.window.adjustHeight=function(F){var C=parseInt(F,10);
if(isNaN(C)){var H=gadgets.window.getViewportDimensions().height;
var B=document.body;
var G=document.documentElement;
if(document.compatMode=="CSS1Compat"&&G.scrollHeight){C=G.scrollHeight!=H?G.scrollHeight:G.offsetHeight
}else{var D=G.scrollHeight;
var E=G.offsetHeight;
if(G.clientHeight!=E){D=B.scrollHeight;
E=B.offsetHeight
}if(D>H){C=D>E?D:E
}else{C=D<E?D:E
}}}if(C!=A){A=C;
gadgets.rpc.call(null,"resize_iframe",null,C)
}}
}());
var _IG_AdjustIFrameHeight=gadgets.window.adjustHeight;