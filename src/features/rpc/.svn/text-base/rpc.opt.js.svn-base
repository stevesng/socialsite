var gadgets=gadgets||{};
gadgets.rpc=function(){var N="__cb";
var L="";
var Y="__g2c_rpc";
var E="__c2g_rpc";
var B={};
var S=[];
var C={};
var Q={};
var H={};
var J=0;
var Z={};
var P={};
var D={};
var X={};
if(gadgets.util){X=gadgets.util.getUrlParameters()
}H[".."]=X.rpctoken||X.ifpctok||0;
function U(){return typeof window.postMessage==="function"?"wpm":typeof document.postMessage==="function"?"dpm":navigator.product==="Gecko"?"fe":"ifpc"
}function W(){if(G==="dpm"||G==="wpm"){window.addEventListener("message",function(a){O(gadgets.json.parse(a.data))
},false)
}}var G=U();
W();
B[L]=function(){throw new Error("Unknown RPC service: "+this.s)
};
B[N]=function(b,a){var c=Z[b];
if(c){delete Z[b];
c(a)
}};
function K(a){if(P[a]){return 
}if(G==="fe"){try{var c=document.getElementById(a);
c[Y]=function(d){O(gadgets.json.parse(d))
}
}catch(b){}}P[a]=true
}function R(c){var e=gadgets.json.stringify;
var a=[];
for(var d=0,b=c.length;
d<b;
++d){a.push(encodeURIComponent(e(c[d])))
}return a.join("&")
}function O(b){if(b&&typeof b.s==="string"&&typeof b.f==="string"&&b.a instanceof Array){if(H[b.f]){if(H[b.f]!=b.t){throw new Error("Invalid auth token.")
}}if(b.c){b.callback=function(c){gadgets.rpc.call(b.f,N,null,b.c,c)
}
}var a=(B[b.s]||B[L]).apply(b,b.a);
if(b.c&&typeof a!="undefined"){gadgets.rpc.call(b.f,N,null,b.c,a)
}}}function A(b,c,i,d,g){try{if(i!=".."){var a=window.frameElement;
if(typeof a[Y]==="function"){if(typeof a[Y][E]!=="function"){a[Y][E]=function(e){O(gadgets.json.parse(e))
}
}a[Y](d);
return 
}}else{var h=document.getElementById(b);
if(typeof h[Y]==="function"&&typeof h[Y][E]==="function"){h[Y][E](d);
return 
}}}catch(f){}V(b,c,i,d,g)
}function V(a,b,g,c,d){var f=gadgets.rpc.getRelayUrl(a);
if(!f){throw new Error("No relay file assigned for IFPC")
}var e=null;
if(Q[a]){e=[f,"#",R([g,J,1,0,R([g,b,"","",g].concat(d))])].join("")
}else{e=[f,"#",a,"&",g,"@",J,"&1&0&",encodeURIComponent(c)].join("")
}I(e)
}function I(d){var b;
for(var a=S.length-1;
a>=0;
--a){var f=S[a];
try{if(f&&(f.recyclable||f.readyState==="complete")){f.parentNode.removeChild(f);
if(window.ActiveXObject){S[a]=f=null;
S.splice(a,1)
}else{f.recyclable=false;
b=f;
break
}}}catch(c){}}if(!b){b=document.createElement("iframe");
b.style.border=b.style.width=b.style.height="0px";
b.style.visibility="hidden";
b.style.position="absolute";
b.onload=function(){this.recyclable=true
};
S.push(b)
}b.src=d;
setTimeout(function(){document.body.appendChild(b)
},0)
}function F(b,d){if(typeof D[b]==="undefined"){D[b]=false;
var c=null;
if(b===".."){c=parent
}else{c=frames[b]
}try{D[b]=c.gadgets.rpc.receiveSameDomain
}catch(a){}}if(typeof D[b]==="function"){D[b](d);
return true
}return false
}if(gadgets.config){function T(a){if(a.rpc.parentRelayUrl.substring(0,7)==="http://"){C[".."]=a.rpc.parentRelayUrl
}else{var e=document.location.search.substring(0).split("&");
var d="";
for(var b=0,c;
c=e[b];
++b){if(c.indexOf("parent=")===0){d=decodeURIComponent(c.substring(7));
break
}}C[".."]=d+a.rpc.parentRelayUrl
}Q[".."]=!!a.rpc.useLegacyProtocol
}var M={parentRelayUrl:gadgets.config.NonEmptyStringValidator};
gadgets.config.register("rpc",M,T)
}return{register:function(b,a){if(b==N){throw new Error("Cannot overwrite callback service")
}if(b==L){throw new Error("Cannot overwrite default service: use registerDefault")
}B[b]=a
},unregister:function(a){if(a==N){throw new Error("Cannot delete callback service")
}if(a==L){throw new Error("Cannot delete default service: use unregisterDefault")
}delete B[a]
},registerDefault:function(a){B[""]=a
},unregisterDefault:function(){delete B[""]
},call:function(h,d,i,g){++J;
h=h||"..";
if(i){Z[J]=i
}var f="..";
if(h===".."){f=window.name
}var c={s:d,f:f,c:i?J:0,a:Array.prototype.slice.call(arguments,3),t:H[h]};
if(F(h,c)){return 
}var a=gadgets.json.stringify(c);
var b=G;
if(Q[h]){b="ifpc"
}switch(b){case"dpm":var j=h===".."?parent.document:frames[h].document;
j.postMessage(a);
break;
case"wpm":var e=h===".."?parent:frames[h];
e.postMessage(a,"*");
break;
case"fe":A(h,d,f,a,c.a);
break;
default:V(h,d,f,a,c.a);
break
}},getRelayUrl:function(a){return C[a]
},setRelayUrl:function(b,a,c){C[b]=a;
Q[b]=!!c
},setAuthToken:function(a,b){H[a]=b;
K(a)
},getRelayChannel:function(){return G
},receive:function(a){if(a.length>4){O(gadgets.json.parse(decodeURIComponent(a[a.length-1])))
}},receiveSameDomain:function(a){a.a=Array.prototype.slice.call(a.a);
window.setTimeout(function(){O(a)
},0)
}}
}();
