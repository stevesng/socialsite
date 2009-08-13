var opensocial=window.opensocial||{};
opensocial.xmlutil=opensocial.xmlutil||{};
opensocial.xmlutil.parser_=null;
opensocial.xmlutil.parseXML=function(B){if(typeof (DOMParser)!="undefined"){opensocial.xmlutil.parser_=opensocial.xmlutil.parser_||new DOMParser();
var A=opensocial.xmlutil.parser_.parseFromString(B,"text/xml");
if(A.firstChild&&A.firstChild.tagName=="parsererror"){throw A.firstChild.firstChild.nodeValue
}return A
}else{var A=new ActiveXObject("MSXML2.DomDocument");
A.validateOnParse=false;
A.loadXML(B);
if(A.parseError&&A.parseError.errorCode){throw A.parseError.reason
}return A
}};
opensocial.xmlutil.NSMAP={os:"http://opensocial.org/"};
opensocial.xmlutil.getRequiredNamespaces=function(A){var C=[];
for(var B in opensocial.xmlutil.NSMAP){if(A.indexOf("<"+B+":")>=0&&A.indexOf("xmlns:"+B+":")<0){C.push(" xmlns:");
C.push(B);
C.push('="');
C.push(opensocial.xmlutil.NSMAP[B]);
C.push('"')
}}return C.join("")
};
opensocial.xmlutil.ENTITIES='<!ENTITY nbsp "&#160;">';
opensocial.xmlutil.prepareXML=function(A){var B=opensocial.xmlutil.getRequiredNamespaces(A);
return"<!DOCTYPE root ["+opensocial.xmlutil.ENTITIES+']><root xml:space="preserve"'+B+">"+A+"</root>"
};