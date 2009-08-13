osd.ATTR_KEY="key";
osd.SCRIPT_TYPE="text/os-data";
osd.NSMAP={};
osd.VAR_REGEX=/^([\w\W]*?)(\$\{[^\}]*\})([\w\W]*)$/;
osd.RequestDescriptor=function(C){this.tagName=C.tagName;
this.tagParts=this.tagName.split(":");
this.attributes={};
this.dependencies=false;
for(var B=0;
B<C.attributes.length;
++B){var A=C.attributes[B].nodeName;
if(A){var D=C.getAttribute(A);
if(A&&D){this.attributes[A]=D;
this.computeNeededKeys_(D)
}}}this.key=this.attributes[osd.ATTR_KEY];
this.register_()
};
osd.RequestDescriptor.prototype.hasAttribute=function(A){return !!this.attributes[A]
};
osd.RequestDescriptor.prototype.getAttribute=function(B){var A=this.attributes[B];
if(!A){return A
}var C=opensocial.data.parseExpression_(A);
if(!C){return A
}return opensocial.data.DataContext.evalExpression(C)
};
osd.parseExpression_=function(C){if(!C.length){return null
}var A=opensocial.data.VAR_REGEX;
var F=C;
var E=[];
var B=F.match(A);
if(!B){return null
}while(B){if(B[1].length>0){E.push(opensocial.data.transformLiteral_(B[1]))
}var D=B[2].substring(2,B[2].length-1);
E.push("("+D+")");
F=B[3];
B=F.match(A)
}if(F.length>0){E.push(opensocial.data.transformLiteral_(F))
}return E.join("+")
};
osd.transformLiteral_=function(A){return"'"+A.replace(/'/g,"\\'").replace(/\n/g," ")+"'"
};
osd.RequestDescriptor.prototype.sendRequest=function(){var B=opensocial.data.NSMAP[this.tagParts[0]];
var A=null;
if(B){A=B[this.tagParts[1]]
}if(!A){throw"Data handler undefined for "+this.tagName
}A(this)
};
osd.RequestDescriptor.prototype.getSendRequestClosure=function(){var A=this;
return function(){A.sendRequest()
}
};
osd.RequestDescriptor.prototype.computeNeededKeys_=function(E){var A=opensocial.data.VAR_REGEX;
var B=E.match(A);
while(B){var D=B[2].substring(2,B[2].length-1);
var C=D.split(".")[0];
if(!this.neededKeys){this.neededKeys={}
}this.neededKeys[C]=true;
B=B[3].match(A)
}};
osd.RequestDescriptor.prototype.register_=function(){opensocial.data.registerRequestDescriptor(this)
};
osd.DataContext.evalExpression=function(A){return(new Function("context","with (context) return "+A))(opensocial.data.DataContext.dataSets_)
};
osd.requests_={};
osd.registerRequestDescriptor=function(A){if(osd.requests_[A.key]){throw"Request already registered for "+A.key
}opensocial.data.requests_[A.key]=A
};
osd.currentAPIRequest_=null;
osd.currentAPIRequestKeys_=null;
osd.currentAPIRequestCallbacks_=null;
osd.getCurrentAPIRequest=function(){if(!osd.currentAPIRequest_){opensocial.data.currentAPIRequest_=opensocial.newDataRequest();
opensocial.data.currentAPIRequestKeys_=[];
opensocial.data.currentAPIRequestCallbacks_={}
}return opensocial.data.currentAPIRequest_
};
osd.addToCurrentAPIRequest=function(C,B,A){opensocial.data.getCurrentAPIRequest().add(C,B);
opensocial.data.currentAPIRequestKeys_.push(B);
if(A){opensocial.data.currentAPIRequestCallbacks_[B]=A
}window.setTimeout(osd.sendCurrentAPIRequest_,0)
};
osd.sendCurrentAPIRequest_=function(){if(osd.currentAPIRequest_){opensocial.data.currentAPIRequest_.send(osd.createSharedRequestCallback_());
opensocial.data.currentAPIRequest_=null
}};
osd.createSharedRequestCallback_=function(){var B=opensocial.data.currentAPIRequestKeys_;
var A=opensocial.data.currentAPIRequestCallbacks_;
return function(C){opensocial.data.onAPIResponse(C,B,A)
}
};
osd.onAPIResponse=function(F,E,D){for(var B=0;
B<E.length;
B++){var A=E[B];
var C=F.get(A);
if(D[A]){D[A](A,C)
}else{opensocial.data.DataContext.putDataSet(A,C)
}}};
osd.registerRequestHandler=function(B,D){var A=B.split(":");
var C=opensocial.data.NSMAP[A[0]];
if(!C){if(!opensocial.xmlutil.NSMAP[A[0]]){opensocial.xmlutil.NSMAP[A[0]]=null
}C=opensocial.data.NSMAP[A[0]]={}
}else{if(C[A[1]]){throw"Request handler "+A[1]+" is already defined."
}}C[A[1]]=D
};
osd.processDocumentMarkup=function(D){var E=D||document;
var A=E.getElementsByTagName("script");
for(var B=0;
B<A.length;
++B){var C=A[B];
if(C.type==opensocial.data.SCRIPT_TYPE){opensocial.data.loadRequests(C)
}}opensocial.data.registerRequestDependencies();
opensocial.data.executeRequests()
};
if(window.gadgets&&window.gadgets["util"]){gadgets.util.registerOnLoadHandler(osd.processDocumentMarkup)
}osd.loadRequests=function(A){if(typeof (A)=="string"){opensocial.data.loadRequestsFromMarkup_(A);
return 
}var B=A;
A=B.value||B.innerHTML;
opensocial.data.loadRequestsFromMarkup_(A)
};
osd.loadRequestsFromMarkup_=function(A){A=opensocial.xmlutil.prepareXML(A);
var C=opensocial.xmlutil.parseXML(A);
var B=C.firstChild;
while(B.nodeType!=1){B=B.nextSibling
}opensocial.data.processDataNode_(B)
};
osd.processDataNode_=function(A){for(var C=A.firstChild;
C;
C=C.nextSibling){if(C.nodeType==1){var B=new opensocial.data.RequestDescriptor(C)
}}};
osd.registerRequestDependencies=function(){for(var A in opensocial.data.requests_){var C=opensocial.data.requests_[A];
var E=C.neededKeys;
var D=[];
for(var B in E){if(osd.DataContext.getDataSet(B)==null&&opensocial.data.requests_[B]){D.push(B)
}}if(D.length>0){opensocial.data.DataContext.registerListener(D,C.getSendRequestClosure());
C.dependencies=true
}}};
osd.executeRequests=function(){for(var A in opensocial.data.requests_){var B=opensocial.data.requests_[A];
if(!B.dependencies){B.sendRequest()
}}};
osd.transformSpecialValue=function(A){if(A.substring(0,1)=="@"){return A.substring(1).toUpperCase()
}return A
};
(function(){osd.registerRequestHandler("os:ViewerRequest",function(B){var A=opensocial.data.getCurrentAPIRequest().newFetchPersonRequest("VIEWER");
opensocial.data.addToCurrentAPIRequest(A,B.key)
});
osd.registerRequestHandler("os:OwnerRequest",function(B){var A=opensocial.data.getCurrentAPIRequest().newFetchPersonRequest("OWNER");
opensocial.data.addToCurrentAPIRequest(A,B.key)
});
osd.registerRequestHandler("os:PeopleRequest",function(E){var C=E.getAttribute("userId");
var B=E.getAttribute("groupId")||"@self";
var A={};
A.userId=opensocial.data.transformSpecialValue(C);
if(B!="@self"){A.groupId=opensocial.data.transformSpecialValue(B)
}var D=opensocial.data.getCurrentAPIRequest().newFetchPeopleRequest(opensocial.newIdSpec(A));
opensocial.data.addToCurrentAPIRequest(D,E.key)
});
osd.registerRequestHandler("os:ActivitiesRequest",function(E){var C=E.getAttribute("userId");
var B=E.getAttribute("groupId")||"@self";
var A={};
A.userId=opensocial.data.transformSpecialValue(C);
if(B!="@self"){A.groupId=opensocial.data.transformSpecialValue(B)
}var D=opensocial.data.getCurrentAPIRequest().newFetchActivitiesRequest(opensocial.newIdSpec(A));
opensocial.data.addToCurrentAPIRequest(D,E.key)
});
osd.registerRequestHandler("os:HttpRequest",function(C){var A=C.getAttribute("href");
var B=C.getAttribute("format")||"json";
var D={};
D[gadgets.io.RequestParameters.CONTENT_TYPE]=B.toLowerCase()=="text"?gadgets.io.ContentType.TEXT:gadgets.io.ContentType.JSON;
D[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
gadgets.io.makeRequest(A,function(E){opensocial.data.DataContext.putDataSet(C.key,E.data)
},D)
})
})();
(osd.populateParams_=function(){if(window.gadgets&&gadgets.util.hasFeature("views")){opensocial.data.DataContext.putDataSet("ViewParams",gadgets.views.getParams())
}})();