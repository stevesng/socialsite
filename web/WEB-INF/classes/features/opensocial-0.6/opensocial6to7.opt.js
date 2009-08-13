opensocial.requestNavigateTo=function(){return gadgets.views.requestNavigateTo()
};
opensocial.makeRequest=function(){return gadgets.io.makeRequest()
};
opensocial.ContentRequestParameters={METHOD:gadgets.io.RequestParameters.METHOD,CONTENT_TYPE:gadgets.io.RequestParameters.CONTENT_TYPE,AUTHENTICATION:gadgets.io.RequestParameters.AUTHORIZATION,NUM_ENTRIES:gadgets.io.RequestParameters.NUM_ENTRIES,GET_SUMMARIES:gadgets.io.RequestParameters.GET_SUMMARIES};
opensocial.ContentRequestParameters.MethodType={GET:gadgets.io.MethodType.GET,POST:gadgets.io.MethodType.POST};
opensocial.ContentRequestParameters.ContentType={HTML:gadgets.io.ContentType.TEXT,XML:gadgets.io.ContentType.DOM,FEED:gadgets.io.ContentType.JSON};
opensocial.ContentRequestParameters.AuthenticationType={NONE:gadgets.io.AuthorizationType.NONE,SIGNED:gadgets.io.AuthorizationType.SIGNED,AUTHENTICATED:gadgets.io.AuthorizationType.AUTHENTICATED};
opensocial.Person.prototype.getField_v07=opensocial.Person.prototype.getField;
opensocial.Person.prototype.getField=function(A){if(A==opensocial.Person.Field.NAME){return this.getField_v07(opensocial.Person.Field.NAME).getField(opensocial.Name.Field.UNSTRUCTURED)
}else{return this.getField_v07(A)
}};
opensocial.Person.prototype.getDisplayName_v07=opensocial.Person.getDisplayName;
opensocial.Person.prototype.getDisplayName=function(){return this.getField_v07(opensocial.Person.Field.NAME).getField(opensocial.Name.Field.UNSTRUCTURED)
};
opensocial.newActivity_v07=opensocial.newActivity;
opensocial.newActivity=function(B,A){A.title=B;
opensocial.newActivity_v07(A)
};
opensocial.DataRequest.prototype.newFetchGlobalAppDataRequest=function(A){return this.newFetchPersonAppDataRequest(A)
};
opensocial.DataRequest.prototype.newFetchInstanceAppDataRequest=function(C){var B=new gadgets.Prefs().getModuleId();
if(opensocial.Container.isArray(C)){for(var A=0;
A<C.length;
A++){C[A]=B+C[A]
}}else{C=B+C
}return this.newFetchPersonAppDataRequest("OWNER",C)
};
opensocial.DataRequest.prototype.newUpdateInstanceAppDataRequest=function(A,C){var B=new gadgets.Prefs().getModuleId();
return this.newUpdatePersonAppDataRequest("OWNER",B+A)
};
gadgets.views.View.prototype.isPrimaryContent=function(){return this.isOnlyVisibleGadget()
};
opensocial.Environment.prototype.getSurface=function(){return gadgets.views.getCurrentView()
};
opensocial.Environment.prototype.getSupportedSurfaces=function(){return gadgets.views.getSupportedViews()
};
opensocial.Environment.prototype.getParams=function(){return gadgets.views.getParams()
};
opensocial.Environment.prototype.hasCapability=function(){return gadgets.util.hasFeature()
};