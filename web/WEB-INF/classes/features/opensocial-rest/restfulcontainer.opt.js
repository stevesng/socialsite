var RestfulContainer=function(E,G,F){opensocial.Container.call(this);
var D={};
for(var B in F){if(F.hasOwnProperty(B)){D[B]={};
for(var C=0;
C<F[B].length;
C++){var A=F[B][C];
D[B][A]=true
}}}this.environment_=new opensocial.Environment(G,D);
this.baseUrl_=E;
this.securityToken_=shindig.auth.getSecurityToken()
};
RestfulContainer.inherits(opensocial.Container);
RestfulContainer.prototype.getEnvironment=function(){return this.environment_
};
RestfulContainer.prototype.requestCreateActivity=function(D,B,A){A=A||function(){};
var C=opensocial.newDataRequest();
var E=new opensocial.IdSpec({userId:"VIEWER"});
C.add(this.newCreateActivityRequest(E,D),"key");
C.send(function(F){A(F.get("key"))
})
};
RestfulContainer.prototype.requestData=function(E,I){I=I||function(){};
var B=E.getRequestObjects();
var G=B.length;
if(G===0){window.setTimeout(function(){I(new opensocial.DataResponse({},true))
},0);
return 
}var J={};
var C=0;
for(var D=0;
D<G;
D++){var H=B[D];
if(!H.key){H.key="systemKey"+C;
while(J[H.key]){C++;
H.key="systemKey"+C
}}J[H.key]={url:H.request.url,method:H.request.method};
if(H.request.postData){J[H.key].postData=H.request.postData
}}var A=function(V){if(V.errors[0]||V.data.error){RestfulContainer.generateErrorResponse(V,B,I);
return 
}V=V.data;
var R=V.responses||[];
var L=false;
var U={};
for(var M=0;
M<B.length;
M++){var O=B[M];
var N=R[O.key];
var K=N.response;
var S=N.error;
var Q=N.errorMessage;
var P=O.request.processResponse(O.request,K,S,Q);
L=L||P.hadError();
U[O.key]=P
}var T=new opensocial.DataResponse(U,L);
I(T)
};
var F={CONTENT_TYPE:"JSON",METHOD:"POST",AUTHORIZATION:"SIGNED",POST_DATA:gadgets.json.stringify(J)};
gadgets.io.makeNonProxiedRequest(this.baseUrl_+"/rest/jsonBatch?st="+encodeURIComponent(shindig.auth.getSecurityToken()),A,F,"application/json")
};
RestfulContainer.generateErrorResponse=function(A,D,F){var B=RestfulContainer.translateHttpError(A.errors[0]||A.data.error)||opensocial.ResponseItem.Error.INTERNAL_ERROR;
var E={};
for(var C=0;
C<D.length;
C++){E[D[C].key]=new opensocial.ResponseItem(D[C].request,null,B)
}F(new opensocial.DataResponse(E,true))
};
RestfulContainer.translateHttpError=function(A){if(A=="Error 501"){return opensocial.ResponseItem.Error.NOT_IMPLEMENTED
}else{if(A=="Error 401"){return opensocial.ResponseItem.Error.UNAUTHORIZED
}else{if(A=="Error 403"){return opensocial.ResponseItem.Error.FORBIDDEN
}else{if(A=="Error 400"){return opensocial.ResponseItem.Error.BAD_REQUEST
}else{if(A=="Error 500"){return opensocial.ResponseItem.Error.INTERNAL_ERROR
}else{if(A=="Error 404"){return opensocial.ResponseItem.Error.BAD_REQUEST
}}}}}}};
RestfulContainer.prototype.makeIdSpec=function(A){return new opensocial.IdSpec({userId:A})
};
RestfulContainer.prototype.translateIdSpec=function(A){var D=A.getField("userId");
var C=A.getField("groupId");
if(D=="OWNER"){D="@owner"
}else{if(D=="VIEWER"){D="@viewer"
}else{if(opensocial.Container.isArray(A)){for(var B=0;
B<A.length;
B++){}}}}if(C=="FRIENDS"){C="@friends"
}else{if(C=="SELF"||!C){C="@self"
}}return D+"/"+C
};
RestfulContainer.prototype.getNetworkDistance=function(A){var B=A.getField("networkDistance")||"";
return"networkDistance="+B
};
RestfulContainer.prototype.newFetchPersonRequest=function(D,C){var A=this.newFetchPeopleRequest(this.makeIdSpec(D),C);
var B=this;
return new RestfulRequestItem(A.url,A.method,null,function(E){return B.createPersonFromJson(E.entry)
})
};
RestfulContainer.prototype.newFetchPeopleRequest=function(A,D){var B="/people/"+this.translateIdSpec(A);
FieldTranslations.translateJsPersonFieldsToServerFields(D.profileDetail);
B+="?fields="+(D.profileDetail.join(","));
B+="&startIndex="+(D.first||0);
B+="&count="+(D.max||20);
B+="&orderBy="+(D.sortOrder||"topFriends");
B+="&filterBy="+(D.filter||"all");
B+="&"+this.getNetworkDistance(A);
var C=this;
return new RestfulRequestItem(B,"GET",null,function(H){var G;
if(H.entry){G=H.entry
}else{G=[H]
}var F=[];
for(var E=0;
E<G.length;
E++){F.push(C.createPersonFromJson(G[E]))
}return new opensocial.Collection(F,H.startIndex,H.totalResults)
})
};
RestfulContainer.prototype.createPersonFromJson=function(A){FieldTranslations.translateServerPersonToJsPerson(A);
return new JsonPerson(A)
};
RestfulContainer.prototype.getFieldsList=function(A){if(this.hasNoKeys(A)||this.isWildcardKey(A[0])){return""
}else{return"fields="+A.join(",")
}};
RestfulContainer.prototype.hasNoKeys=function(A){return !A||A.length===0
};
RestfulContainer.prototype.isWildcardKey=function(A){return A=="*"
};
RestfulContainer.prototype.newFetchPersonAppDataRequest=function(A,D,C){var B="/appdata/"+this.translateIdSpec(A)+"/@app?"+this.getNetworkDistance(A)+"&"+this.getFieldsList(D);
return new RestfulRequestItem(B,"GET",null,function(E){return opensocial.Container.escape(E.entry,C,true)
})
};
RestfulContainer.prototype.newUpdatePersonAppDataRequest=function(E,B,D){var A="/appdata/"+this.translateIdSpec(this.makeIdSpec(E))+"/@app?fields="+B;
var C={};
C[B]=D;
return new RestfulRequestItem(A,"POST",C)
};
RestfulContainer.prototype.newRemovePersonAppDataRequest=function(C,B){var A="/appdata/"+this.translateIdSpec(this.makeIdSpec(C))+"/@app?"+this.getFieldsList(B);
return new RestfulRequestItem(A,"DELETE")
};
RestfulContainer.prototype.newFetchActivitiesRequest=function(A,C){var B="/activities/"+this.translateIdSpec(A)+"?appId=@app&"+this.getNetworkDistance(A);
return new RestfulRequestItem(B,"GET",null,function(E){E=E.entry;
var F=[];
for(var D=0;
D<E.length;
D++){F.push(new JsonActivity(E[D]))
}return new opensocial.Collection(F)
})
};
RestfulContainer.prototype.newActivity=function(A){return new JsonActivity(A,true)
};
RestfulContainer.prototype.newMediaItem=function(C,A,B){B=B||{};
B.mimeType=C;
B.url=A;
return new JsonMediaItem(B)
};
RestfulContainer.prototype.newCreateActivityRequest=function(A,C){var B="/activities/"+this.translateIdSpec(A)+"/@app?"+this.getNetworkDistance(A);
return new RestfulRequestItem(B,"POST",C.toJsonObject())
};
var RestfulRequestItem=function(C,D,A,B){this.url=C;
this.method=D;
this.postData=A;
this.processData=B||function(E){return E
};
this.processResponse=function(E,H,G,F){return new opensocial.ResponseItem(E,G?null:this.processData(H),G,F)
}
};