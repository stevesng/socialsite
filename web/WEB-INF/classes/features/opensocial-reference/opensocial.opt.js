var opensocial=opensocial||{};
opensocial.requestSendMessage=function(A,D,B,C){opensocial.Container.get().requestSendMessage(A,D,B,C)
};
opensocial.requestShareApp=function(A,D,B,C){opensocial.Container.get().requestShareApp(A,D,B,C)
};
opensocial.requestCreateActivity=function(C,B,A){if(!C||(!C.getField(opensocial.Activity.Field.TITLE)&&!C.getField(opensocial.Activity.Field.TITLE_ID))){if(A){window.setTimeout(function(){A(new opensocial.ResponseItem(null,null,opensocial.ResponseItem.Error.BAD_REQUEST,"You must pass in an activity with a title or title id."))
},0)
}return 
}opensocial.Container.get().requestCreateActivity(C,B,A)
};
opensocial.CreateActivityPriority={HIGH:"HIGH",LOW:"LOW"};
opensocial.hasPermission=function(A){return opensocial.Container.get().hasPermission(A)
};
opensocial.requestPermission=function(B,C,A){opensocial.Container.get().requestPermission(B,C,A)
};
opensocial.Permission={VIEWER:"viewer"};
opensocial.getEnvironment=function(){return opensocial.Container.get().getEnvironment()
};
opensocial.newDataRequest=function(){return opensocial.Container.get().newDataRequest()
};
opensocial.newActivity=function(A){return opensocial.Container.get().newActivity(A)
};
opensocial.newMediaItem=function(C,A,B){return opensocial.Container.get().newMediaItem(C,A,B)
};
opensocial.newMessage=function(A,B){return opensocial.Container.get().newMessage(A,B)
};
opensocial.EscapeType={HTML_ESCAPE:"htmlEscape",NONE:"none"};
opensocial.newIdSpec=function(A){return opensocial.Container.get().newIdSpec(A)
};
opensocial.newNavigationParameters=function(A){return opensocial.Container.get().newNavigationParameters(A)
};
Function.prototype.inherits=function(A){function B(){}B.prototype=A.prototype;
this.superClass_=A.prototype;
this.prototype=new B();
this.prototype.constructor=this
};