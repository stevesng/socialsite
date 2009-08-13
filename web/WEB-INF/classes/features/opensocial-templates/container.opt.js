os.Container={};
os.Container.inlineTemplates_=[];
os.Container.domLoadCallbacks_=null;
os.Container.domLoaded_=false;
os.Container.registerDomLoadListener_=function(){var A=window.gadgets;
if(A&&A.util){A.util.registerOnLoadHandler(os.Container.onDomLoad_)
}else{if(navigator.product=="Gecko"){window.addEventListener("DOMContentLoaded",os.Container.onDomLoad_,false)
}}if(window.addEventListener){window.addEventListener("load",os.Container.onDomLoad_,false)
}else{if(!document.body){setTimeout(arguments.callee,0);
return 
}var B=window.onload||function(){};
window.onload=function(){B();
os.Container.onDomLoad_()
}
}};
os.Container.onDomLoad_=function(){if(os.Container.domLoaded_){return 
}while(os.Container.domLoadCallbacks_.length){try{os.Container.domLoadCallbacks_.pop()()
}catch(A){os.log(A)
}}os.Container.domLoaded_=true
};
os.Container.executeOnDomLoad=function(A){if(os.Container.domLoaded_){setTimeout(A,0)
}else{if(os.Container.domLoadCallbacks_==null){os.Container.domLoadCallbacks_=[];
os.Container.registerDomLoadListener_()
}os.Container.domLoadCallbacks_.push(A)
}};
os.Container.registerDocumentTemplates=function(E){var F=E||document;
var B=F.getElementsByTagName(os.Container.TAG_script_);
for(var C=0;
C<B.length;
++C){var D=B[C];
if(os.Container.isTemplateType_(D.type)){var A=D.getAttribute("tag");
if(A){os.Container.registerTagElement_(D,A)
}else{if(D.getAttribute("name")){os.Container.registerTemplateElement_(D,D.getAttribute("name"))
}}}}};
os.Container.compileInlineTemplates=function(A,G){var H=G||document;
var B=H.getElementsByTagName(os.Container.TAG_script_);
for(var D=0;
D<B.length;
++D){var F=B[D];
if(os.Container.isTemplateType_(F.type)){var C=F.getAttribute("name")||F.getAttribute("tag");
if(!C||C.length<0){var E=os.compileTemplate(F);
if(E){os.Container.inlineTemplates_.push({template:E,node:F})
}else{os.warn("Failed compiling inline template.")
}}}}};
os.Container.defaultContext=null;
os.Container.getDefaultContext=function(){if(!os.Container.defaultContext){if(window.gadgets&&gadgets.util.hasFeature("opensocial-data")){os.Container.defaultContext=os.createContext(opensocial.data.DataContext.dataSets_)
}else{os.Container.defaultContext=os.createContext({})
}}return os.Container.defaultContext
};
os.Container.renderInlineTemplates=function(G,H){var J=H||document;
var B=G?os.createContext(G):os.Container.getDefaultContext();
var D=os.Container.inlineTemplates_;
for(var F=0;
F<D.length;
++F){var K=D[F].template;
var E=D[F].node;
var A="_T_"+K.id;
var C=J.getElementById(A);
if(!C){C=J.createElement("div");
C.setAttribute("id",A);
E.parentNode.insertBefore(C,E)
}if(window.gadgets&&gadgets.util.hasFeature("opensocial-data")){var L=E.getAttribute("before")||E.getAttribute("beforeData");
if(L){var M=L.split(/[\, ]+/);
opensocial.data.DataContext.registerListener(M,os.Container.createHideElementClosure(C))
}var I=E.getAttribute("require")||E.getAttribute("requireData");
if(I){var M=I.split(/[\, ]+/);
opensocial.data.DataContext.registerListener(M,os.Container.createRenderClosure(K,C,null,os.Container.getDefaultContext()))
}else{K.renderInto(C,null,B)
}}else{K.renderInto(C,null,B)
}}};
os.Container.createRenderClosure=function(C,B,A,D){var E=function(){C.renderInto(B,A,D)
};
return E
};
os.Container.createHideElementClosure=function(A){var B=function(){displayNone(A)
};
return B
};
os.Container.registerTemplate=function(A){var B=document.getElementById(A);
return os.Container.registerTemplateElement_(B)
};
os.Container.registerTag=function(A){var B=document.getElementById(A);
os.Container.registerTagElement_(B,A)
};
os.Container.renderElement=function(B,D,A){var E=os.getTemplate(D);
if(E){var C=document.getElementById(B);
if(C){E.renderInto(C,A)
}else{os.warn("Element ("+B+") not found to render into.")
}}else{os.warn("Template ("+D+") not registered.")
}};
os.Container.processInlineTemplates=function(A,B){os.Container.compileInlineTemplates(B);
os.Container.renderInlineTemplates(A,B)
};
os.Container.processDocument=function(A,B){os.Container.registerDocumentTemplates(B);
os.Container.processInlineTemplates(A,B)
};
os.Container.executeOnDomLoad(os.Container.processDocument);
os.Container.TAG_script_="script";
os.Container.templateTypes_={};
os.Container.templateTypes_["text/os-template"]=true;
os.Container.templateTypes_["text/template"]=true;
os.Container.isTemplateType_=function(A){return os.Container.templateTypes_[A]!=null
};
os.Container.registerTemplateElement_=function(A,C){var B=os.compileTemplate(A,C);
if(B){os.registerTemplate(B)
}else{os.warn("Could not compile template ("+A.id+")")
}return B
};
os.Container.registerTagElement_=function(D,C){var E=os.Container.registerTemplateElement_(D);
if(E){var B=C.split(":");
var A=os.getNamespace(B[0]);
if(!A){A=os.createNamespace(B[0],null)
}A[B[1]]=os.createTemplateCustomTag(E)
}};