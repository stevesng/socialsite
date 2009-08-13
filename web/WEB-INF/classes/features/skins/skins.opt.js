var gadgets=gadgets||{};
gadgets.skins=function(){var A={};
var B={properties:gadgets.config.ExistsValidator};
gadgets.config.register("skins",B,function(C){A=C.skins.properties
});
return{init:function(C){A=C
},getProperty:function(C){return A[C]||""
}}
}();
gadgets.skins.Property=gadgets.util.makeEnum(["BG_IMAGE","BG_COLOR","FONT_COLOR","BG_POSITION","BG_REPEAT","ANCHOR_COLOR"]);