var opensocial=opensocial||{};
opensocial.template=opensocial.template||{};
var os=opensocial.template;
os.log=function(B){var A=window.console;
if(A&&A.log){A.log(B)
}};
if(typeof log!="undefined"){log=os.log
}else{window.log=os.log
}os.warn=function(A){os.log("WARNING: "+A)
};
os.ATT_customtag="customtag";
os.VAR_my="$my";
os.VAR_cur="$cur";
os.VAR_node="$node";
os.VAR_msg="Msg";
os.VAR_parentnode="$parentnode";
os.VAR_uniqueId="$uniqueId";
os.VAR_identifierresolver="$_ir";
os.VAR_callbacks="$callbacks_";
os.regExps_={ONLY_WHITESPACE:/^[ \t\n]*$/,VARIABLE_SUBSTITUTION:/^([\w\W]*?)(\$\{[^\}]*\})([\w\W]*)$/};
os.compileTemplate=function(B,D){if(typeof (B)=="string"){return os.compileTemplateString(B,D)
}D=D||B.id;
var C=B.value||B.innerHTML;
C=os.trim(C);
var A=os.compileTemplateString(C,D);
return A
};
os.compileTemplateString=function(B,C){B=opensocial.xmlutil.prepareXML(B);
var A=opensocial.xmlutil.parseXML(B);
return os.compileXMLDoc(A,C)
};
os.renderTemplateNode_=function(C,A){var B=domCloneElement(C);
if(B.removeAttribute){B.removeAttribute(STRING_id)
}jstProcess(A,B);
return B
};
os.elementIdCounter_=0;
os.createTemplateCustomTag=function(A){return function(D,E,C){C.setVariable(os.VAR_my,D);
C.setVariable(os.VAR_node,D);
C.setVariable(os.VAR_uniqueId,os.elementIdCounter_++);
var B=A.render(E,C);
os.markNodeToSkip(B);
return B
}
};
os.createNodeAccessor_=function(A){return function(B){return os.getValueFromNode_(A,B)
}
};
os.gadgetPrefs_=null;
if(window.gadgets&&window.gadgets["Prefs"]){os.gadgetPrefs_=new window.gadgets["Prefs"]()
}os.getPrefMessage=function(A){if(!os.gadgetPrefs_){return null
}return os.gadgetPrefs_.getMsg(A)
};
os.globalDisallowedAttributes_={data:1};
os.customAttributes_={};
os.registerAttribute=function(B,A){os.customAttributes_[B]=A
};
os.doAttribute=function(C,B,D,A){var E=os.customAttributes_[B];
if(!E){return 
}E(C,C.getAttribute(B),D,A)
};
os.doTag=function(C,H,J,F,A){var D=os.getCustomTag(H,J);
if(!D){os.warn("Custom tag <"+H+":"+J+"> not defined.");
return 
}for(var B=C.firstChild;
B;
B=B.nextSibling){if(B.nodeType==DOM_ELEMENT_NODE){jstProcess(A,B);
os.markNodeToSkip(B)
}}var K=D.call(null,C,F,A);
if(!K&&typeof (K)!="string"){throw"Custom tag <"+H+":"+J+"> failed to return anything."
}if(typeof (K)=="string"){C.innerHTML=K?K:""
}else{if(isArray(K)&&K.nodeType!=DOM_TEXT_NODE){os.removeChildren(C);
for(var E=0;
E<K.length;
E++){if(K[E].nodeType&&(K[E].nodeType==DOM_ELEMENT_NODE||K[E].nodeType==DOM_TEXT_NODE)){C.appendChild(K[E]);
if(K[E].nodeType==DOM_ELEMENT_NODE){os.markNodeToSkip(K[E])
}}}}else{var G=A.getVariable(os.VAR_callbacks);
var I=null;
if(K.nodeType&&K.nodeType==DOM_ELEMENT_NODE){I=K
}else{if(K.root&&K.root.nodeType&&K.root.nodeType==DOM_ELEMENT_NODE){I=K.root
}}if(I&&I!=C&&(!I.parentNode||K.parentNode.nodeType==DOM_DOCUMENT_FRAGMENT_NODE)){os.removeChildren(C);
C.appendChild(I);
os.markNodeToSkip(I)
}if(K.onAttach){G.push(K)
}}}};
os.setContextNode_=function(B,A){if(B.nodeType==DOM_ELEMENT_NODE){A.setVariable(os.VAR_node,B)
}};
os.markNodeToSkip=function(A){A.setAttribute(ATT_skip,"true");
A.removeAttribute(ATT_select);
A.removeAttribute(ATT_eval);
A.removeAttribute(ATT_values);
A.removeAttribute(ATT_display);
A[PROP_jstcache]=null;
A.removeAttribute(ATT_jstcache)
};