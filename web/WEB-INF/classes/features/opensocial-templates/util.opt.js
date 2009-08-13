os.trim=function(A){return A.replace(/^\s+/,"").replace(/\s+$/,"")
};
os.isAlphaNum=function(A){return((A>="a"&&A<="z")||(A>="A"&&A<="Z")||(A>="0"&&A<="9")||A=="_")
};
os.removeChildren=function(A){while(A.firstChild){A.removeChild(A.firstChild)
}};
os.appendChildren=function(A,B){while(A.firstChild){B.appendChild(A.firstChild)
}};
os.replaceNode=function(D,C){var B=D.parentNode;
if(!B){throw"Error in replaceNode() - Node has no parent: "+D
}if(C.nodeType==DOM_ELEMENT_NODE||C.nodeType==DOM_TEXT_NODE){B.replaceChild(C,D)
}else{if(isArray(C)){for(var A=0;
A<C.length;
A++){B.insertBefore(C[A],D)
}B.removeChild(D)
}}};
os.getPropertyGetterName=function(B){var A="get"+B.charAt(0).toUpperCase()+B.substring(1);
return A
};
os.convertToCamelCase=function(E){var D=E.toLowerCase().split("_");
var A=[];
A.push(D[0].toLowerCase());
for(var B=1;
B<D.length;
++B){var C=D[B].charAt(0).toUpperCase()+D[B].substring(1);
A.push(C)
}return A.join("")
};