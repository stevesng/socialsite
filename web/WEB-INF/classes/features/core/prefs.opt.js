var gadgets=gadgets||{};
(function(){var H=null;
var I={};
var D={};
var G={};
var E="en";
var B="US";
var A=0;
function C(){var K=gadgets.util.getUrlParameters();
for(var J in K){if(K.hasOwnProperty(J)){if(J.indexOf("up_")===0&&J.length>3){I[J.substr(3)]=String(K[J])
}else{if(J==="country"){B=K[J]
}else{if(J==="lang"){E=K[J]
}else{if(J==="mid"){A=K[J]
}}}}}}}function F(){for(var J in G){if(!I[J]){I[J]=G[J]
}}}gadgets.Prefs=function(){if(!H){C();
F();
H=this
}return H
};
gadgets.Prefs.setInternal_=function(K,L){if(typeof K==="string"){I[K]=L
}else{for(var J in K){if(K.hasOwnProperty(J)){I[J]=K[J]
}}}};
gadgets.Prefs.setMessages_=function(J){D=J
};
gadgets.Prefs.setDefaultPrefs_=function(J){G=J
};
gadgets.Prefs.prototype.getString=function(J){return I[J]?gadgets.util.escapeString(I[J]):""
};
gadgets.Prefs.prototype.getInt=function(J){var K=parseInt(I[J],10);
return isNaN(K)?0:K
};
gadgets.Prefs.prototype.getFloat=function(J){var K=parseFloat(I[J]);
return isNaN(K)?0:K
};
gadgets.Prefs.prototype.getBool=function(J){var K=I[J];
if(K){return K==="true"||K===true||!!parseInt(K,10)
}return false
};
gadgets.Prefs.prototype.set=function(J,K){throw new Error("setprefs feature required to make this call.")
};
gadgets.Prefs.prototype.getArray=function(N){var O=I[N];
if(O){var J=O.split("|");
var K=gadgets.util.escapeString;
for(var M=0,L=J.length;
M<L;
++M){J[M]=K(J[M].replace(/%7C/g,"|"))
}return J
}return[]
};
gadgets.Prefs.prototype.setArray=function(J,K){throw new Error("setprefs feature required to make this call.")
};
gadgets.Prefs.prototype.getMsg=function(J){return D[J]||""
};
gadgets.Prefs.prototype.getCountry=function(){return B
};
gadgets.Prefs.prototype.getLang=function(){return E
};
gadgets.Prefs.prototype.getModuleId=function(){return A
}
})();