var gadgets=gadgets||{};
gadgets.views=function(){var E=null;
var B={};
var D={};
function A(H){if(!H){H=window.event
}var G;
if(H.target){G=H.target
}else{if(H.srcElement){G=H.srcElement
}}if(G.nodeType===3){G=G.parentNode
}if(G.nodeName.toLowerCase()==="a"){var F=G.getAttribute("href");
if(F&&F[0]!=="#"&&F.indexOf("://")===-1){gadgets.views.requestNavigateTo(E,F);
if(H.stopPropagation){H.stopPropagation()
}if(H.preventDefault){H.preventDefault()
}H.returnValue=false;
H.cancelBubble=true;
return false
}}return false
}function C(I){var H=I.views||{};
for(var L in H){if(H.hasOwnProperty(L)){if(L!="rewriteLinks"){var M=H[L];
if(!M){continue
}B[L]=new gadgets.views.View(L,M.isOnlyVisible);
var F=M.aliases||[];
for(var K=0,J;
J=F[K];
++K){B[J]=new gadgets.views.View(L,M.isOnlyVisible)
}}}}var G=gadgets.util.getUrlParameters();
if(G["view-params"]){D=gadgets.json.parse(G["view-params"])||D
}E=B[G.view]||B["default"];
if(H.rewriteLinks){if(document.attachEvent){document.attachEvent("onclick",A)
}else{document.addEventListener("click",A,false)
}}}gadgets.config.register("views",null,C);
return{bind:function(U,S){if(typeof U!="string"){throw new Error("Invalid urlTemplate")
}if(typeof S!="object"){throw new Error("Invalid environment")
}var Q=/^([a-zA-Z0-9][a-zA-Z0-9_\.\-]*)(=([a-zA-Z0-9\-\._~]|(%[0-9a-fA-F]{2}))*)?$/,W=new RegExp("\\{([^}]*)\\}","g"),T=/^-([a-zA-Z]+)\|([^|]*)\|(.+)$/,M=[],P=0,K,J,H,O,L,G,N,R;
function I(Y,X){return S.hasOwnProperty(Y)?S[Y]:X
}function F(X){if(!(J=X.match(Q))){throw new Error("Invalid variable : "+X)
}}function V(b,X,a){var Y,Z=b.split(",");
for(Y=0;
Y<Z.length;
++Y){F(Z[Y]);
if(a(X,I(J[1]),J[1])){break
}}return X
}while(K=W.exec(U)){M.push(U.substring(P,K.index));
P=W.lastIndex;
if(J=K[1].match(Q)){H=J[1];
O=J[2]?J[2].substr(1):"";
M.push(I(H,O))
}else{if(J=K[1].match(T)){L=J[1];
G=J[2];
N=J[3];
R=0;
switch(L){case"neg":R=1;
case"opt":if(V(N,{flag:R},function(Y,X){if(typeof X!="undefined"&&(typeof X!="object"||X.length)){Y.flag=!Y.flag;
return 1
}}).flag){M.push(G)
}break;
case"join":M.push(V(N,[],function(Z,Y,X){if(typeof Y==="string"){Z.push(X+"="+Y)
}}).join(G));
break;
case"list":F(N);
value=I(J[1]);
if(typeof value==="object"&&typeof value.join==="function"){M.push(value.join(G))
}break;
case"prefix":R=1;
case"suffix":F(N);
value=I(J[1],J[2]&&J[2].substr(1));
if(typeof value==="string"){M.push(R?G+value:value+G)
}else{if(typeof value==="object"&&typeof value.join==="function"){M.push(R?G+value.join(G):value.join(G)+G)
}}break;
default:throw new Error("Invalid operator : "+L)
}}else{throw new Error("Invalid syntax : "+K[0])
}}}M.push(U.substr(P));
return M.join("")
},requestNavigateTo:function(F,H,G){if(typeof F!=="string"){F=F.getName()
}gadgets.rpc.call(null,"requestNavigateTo",null,F,H,G)
},getCurrentView:function(){return E
},getSupportedViews:function(){return B
},getParams:function(){return D
}}
}();
gadgets.views.View=function(A,B){this.name_=A;
this.isOnlyVisible_=!!B
};
gadgets.views.View.prototype.getName=function(){return this.name_
};
gadgets.views.View.prototype.getUrlTemplate=function(){return gadgets.config&&gadgets.config.views&&gadgets.config.views[this.name_]&&gadgets.config.views[this.name_].urlTemplate
};
gadgets.views.View.prototype.bind=function(A){return gadgets.views.bind(this.getUrlTemplate(),A)
};
gadgets.views.View.prototype.isOnlyVisibleGadget=function(){return this.isOnlyVisible_
};
gadgets.views.ViewType=gadgets.util.makeEnum(["CANVAS","HOME","PREVIEW","PROFILE","FULL_PAGE","DASHBOARD","POPUP"]);