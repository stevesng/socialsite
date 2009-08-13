var gadgets=gadgets||{};
gadgets.flash=gadgets.flash||{};
gadgets.flash.getMajorVersion=function(){var C=0;
if(navigator.plugins&&navigator.mimeTypes&&navigator.mimeTypes.length){var B=navigator.plugins["Shockwave Flash"];
if(B&&B.description){C=parseInt(B.description.match(/[0-9]+/)[0],10)
}}else{for(var A=10;
A>0;
A--){try{new ActiveXObject("ShockwaveFlash.ShockwaveFlash."+A);
return A
}catch(D){}}}return C
};
gadgets.flash.swfContainerId_=0;
gadgets.flash.embedFlash=function(E,K,J,C){switch(typeof K){case"string":K=document.getElementById(K);
case"object":if(K&&(typeof K.innerHTML=="string")){break
}default:return false
}switch(typeof C){case"undefined":C={};
case"object":break;
default:return false
}var G=gadgets.flash.getMajorVersion();
if(G){var L=parseInt(J,10);
if(isNaN(L)){L=0
}if(G>=L){if(!C.width){C.width="100%"
}if(!C.height){C.height="100%"
}if(typeof C.base!="string"){C.base=E.match(/^[^?#]+\//)[0]
}if(typeof C.wmode!="string"){C.wmode="opaque"
}while(!C.id){var D="swfContainer"+gadgets.flash.swfContainerId_++;
if(!document.getElementById(D)){C.id=D
}}var F;
if(navigator.plugins&&navigator.mimeTypes&&navigator.mimeTypes.length){C.type="application/x-shockwave-flash";
C.src=E;
F="<embed";
for(var B in C){if(!/^swf_/.test(B)){F+=" "+B+'="'+C[B]+'"'
}}F+=" /></embed>"
}else{C.movie=E;
var H={width:C.width,height:C.height,classid:"clsid:D27CDB6E-AE6D-11CF-96B8-444553540000"};
if(C.id){H.id=C.id
}F="<object";
for(var I in H){F+=" "+I+'="'+H[I]+'"'
}F+=">";
for(var A in C){if(!/^swf_/.test(A)&&!H[A]){F+='<param name="'+A+'" value="'+C[A]+'" />'
}}F+="</object>"
}K.innerHTML=F;
return true
}}return false
};
gadgets.flash.embedCachedFlash=function(){var A=Array.prototype.slice.call(arguments);
A[0]=gadgets.io.getProxyUrl(A[0]);
return gadgets.flash.embedFlash.apply(this,A)
};
var _IG_GetFlashMajorVersion=gadgets.flash.getMajorVersion;
var _IG_EmbedFlash=function(C,B,A){return gadgets.flash.embedFlash(C,B,A.swf_version,A)
};
var _IG_EmbedCachedFlash=function(C,B,A){return gadgets.flash.embedCachedFlash(C,B,A.swf_version,A)
};