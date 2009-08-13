var gadgets=gadgets||{};
gadgets.util=function(){function F(){var K;
var J=document.location.href;
var H=J.indexOf("?");
var I=J.indexOf("#");
if(I===-1){K=J.substr(H+1)
}else{K=[J.substr(H+1,I-H-1),"&",J.substr(I+1)].join("")
}return K.split("&")
}var D=null;
var C={};
var E=[];
var A={0:false,10:true,13:true,34:true,39:true,60:true,62:true,92:true,8232:true,8233:true};
function B(H,I){return String.fromCharCode(I)
}function G(H){C=H["core.util"]||{}
}if(gadgets.config){gadgets.config.register("core.util",null,G)
}return{getUrlParameters:function(){if(D!==null){return D
}D={};
var K=F();
var N=window.decodeURIComponent?decodeURIComponent:unescape;
for(var I=0,H=K.length;
I<H;
++I){var M=K[I].indexOf("=");
if(M===-1){continue
}var L=K[I].substring(0,M);
var J=K[I].substring(M+1);
J=J.replace(/\+/g," ");
D[L]=N(J)
}return D
},makeClosure:function(K,M,L){var J=[];
for(var I=2,H=arguments.length;
I<H;
++I){J.push(arguments[I])
}return function(){var N=J.slice();
for(var P=0,O=arguments.length;
P<O;
++P){N.push(arguments[P])
}return M.apply(K,N)
}
},makeEnum:function(I){var K={};
for(var J=0,H;
H=I[J];
++J){K[H]=H
}return K
},getFeatureParameters:function(H){return typeof C[H]==="undefined"?null:C[H]
},hasFeature:function(H){return typeof C[H]!=="undefined"
},registerOnLoadHandler:function(H){E.push(H)
},runOnLoadHandlers:function(){for(var I=0,H=E.length;
I<H;
++I){E[I]()
}},escape:function(H,L){if(!H){return H
}else{if(typeof H==="string"){return gadgets.util.escapeString(H)
}else{if(typeof H==="array"){for(var K=0,I=H.length;
K<I;
++K){H[K]=gadgets.util.escape(H[K])
}}else{if(typeof H==="object"&&L){var J={};
for(var M in H){if(H.hasOwnProperty(M)){J[gadgets.util.escapeString(M)]=gadgets.util.escape(H[M],true)
}}return J
}}}}return H
},escapeString:function(L){var I=[],K,M;
for(var J=0,H=L.length;
J<H;
++J){K=L.charCodeAt(J);
M=A[K];
if(M===true){I.push("&#",K,";")
}else{if(M!==false){I.push(L.charAt(J))
}}}return I.join("")
},unescapeString:function(H){return H.replace(/&#([0-9]+);/g,B)
}}
}();
gadgets.util.getUrlParameters();