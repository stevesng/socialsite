var gadgets=gadgets||{};
gadgets.MiniMessage=function(B,A){this.numMessages_=0;
this.moduleId_=B||0;
this.container_=typeof A=="object"?A:this.createContainer_()
};
gadgets.MiniMessage.prototype.createContainer_=function(){var B="mm_"+this.moduleId_;
var A=document.getElementById(B);
if(!A){A=document.createElement("div");
A.id=B;
document.body.insertBefore(A,document.body.firstChild)
}return A
};
gadgets.MiniMessage.addCSS_=function(C){var B=document.getElementsByTagName("head")[0];
if(B){var A=document.createElement("style");
A.type="text/css";
if(A.styleSheet){A.styleSheet.cssText=C
}else{A.appendChild(document.createTextNode(C))
}B.insertBefore(A,B.firstChild)
}};
gadgets.MiniMessage.prototype.cascade_=function(A){return A+" "+A+this.moduleId_
};
gadgets.MiniMessage.prototype.dismissFunction_=function(B,A){return function(){if(typeof A=="function"&&!A()){return 
}try{B.parentNode.removeChild(B)
}catch(C){}}
};
gadgets.MiniMessage.prototype.createDismissibleMessage=function(D,A){var C=this.createStaticMessage(D);
var E=document.createElement("td");
E.width=10;
var B=E.appendChild(document.createElement("span"));
B.className=this.cascade_("mmlib_xlink");
B.onclick=this.dismissFunction_(C,A);
B.innerHTML="[x]";
C.rows[0].appendChild(E);
return C
};
gadgets.MiniMessage.prototype.createTimerMessage=function(C,D,A){var B=this.createStaticMessage(C);
window.setTimeout(this.dismissFunction_(B,A),D*1000);
return B
};
gadgets.MiniMessage.prototype.createStaticMessage=function(E){var D=document.createElement("table");
D.id="mm_"+this.moduleId_+"_"+this.numMessages_;
D.className=this.cascade_("mmlib_table");
D.cellSpacing=0;
D.cellPadding=0;
this.numMessages_++;
var B=D.appendChild(document.createElement("tbody"));
var F=B.appendChild(document.createElement("tr"));
var G=F.appendChild(document.createElement("td"));
var A=1;
if(typeof E=="object"&&E.parentNode&&E.parentNode.nodeType==A){var C=E.cloneNode(true);
E.style.display="none";
C.id="";
G.appendChild(C);
E.parentNode.insertBefore(D,E.nextSibling)
}else{if(typeof E=="object"){G.appendChild(E)
}else{G.innerHTML=E
}this.container_.appendChild(D)
}return D
};
gadgets.MiniMessage.prototype.dismissMessage=function(A){this.dismissFunction_(A)()
};
gadgets.MiniMessage.addCSS_([".mmlib_table {","width: 100%;","font: bold 9px arial,sans-serif;","background-color: #fff4c2;","border-collapse: separate;","border-spacing: 0px;","padding: 1px 0px;","}",".mmlib_xlink {","font: normal 1.1em arial,sans-serif;","font-weight: bold;","color: #0000cc;","cursor: pointer;","}"].join(""));
var _IG_MiniMessage=gadgets.MiniMessage;