opensocial.NavigationParameters=function(A){this.fields_=A||{}
};
opensocial.NavigationParameters.Field={VIEW:"view",OWNER:"owner",PARAMETERS:"parameters"};
opensocial.NavigationParameters.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};
opensocial.NavigationParameters.prototype.setField=function(A,B){return this.fields_[A]=B
};
opensocial.NavigationParameters.DestinationType={VIEWER_DESTINATION:"viewerDestination",RECIPIENT_DESTINATION:"recipientDestination"};