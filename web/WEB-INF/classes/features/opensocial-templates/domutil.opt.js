var domutil={};
domutil.isVisible=function(B){if(B.style.display=="none"||B.style.visibility=="hidden"){return false
}var A=this.findEffectiveStyleProperty(B,"visibility");
var C=this.findEffectiveStyleProperty(B,"display");
return A!="hidden"&&C!="none"
};
domutil.findEffectiveStyleProperty=function(B,D){var C=this.findEffectiveStyle(B);
var A=C[D];
if(A=="inherit"&&B.parentNode.style){return this.findEffectiveStyleProperty(B.parentNode,D)
}return A
};
domutil.findEffectiveStyle=function(A){if(!A.style){return undefined
}if(window.getComputedStyle){return window.getComputedStyle(A,null)
}if(A.currentStyle){return A.currentStyle
}throw new Error("cannot determine effective stylesheet in this browser")
};
domutil.getVisibleText=function(C){var B;
var A=[];
domutil.getVisibleText_(C,A,true);
B=A.join("");
B=B.replace(/\xAD/g,"");
B=B.replace(/ +/g," ");
if(B!=" "){B=B.replace(/^\s*/,"")
}return B
};
domutil.getVisibleTextTrim=function(A){return domutil.getVisibleText(A).replace(/^[\s\xa0]+|[\s\xa0]+$/g,"")
};
domutil.getVisibleText_=function(E,C,B){var D={SCRIPT:1,STYLE:1,HEAD:1,IFRAME:1,OBJECT:1};
var A={IMG:" ",BR:"\n"};
if(E.nodeName in D){}else{if(E.nodeType==3){if(B){C.push(String(E.nodeValue).replace(/(\r\n|\r|\n)/g,""))
}else{C.push(E.nodeValue)
}}else{if(!domutil.isVisible(E)){}else{if(E.nodeName in A){C.push(A[E.nodeName])
}else{var F=E.firstChild;
while(F){domutil.getVisibleText_(F,C,B);
F=F.nextSibling
}}}}}};