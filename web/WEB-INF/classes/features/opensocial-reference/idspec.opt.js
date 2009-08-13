opensocial.IdSpec=function(A){this.fields_=A||{}
};
opensocial.IdSpec.Field={USER_ID:"userId",GROUP_ID:"groupId",NETWORK_DISTANCE:"networkDistance"};
opensocial.IdSpec.PersonId={OWNER:"OWNER",VIEWER:"VIEWER"};
opensocial.IdSpec.GroupId={SELF:"SELF",FRIENDS:"FRIENDS",ALL:"ALL"};
opensocial.IdSpec.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};
opensocial.IdSpec.prototype.setField=function(A,B){return this.fields_[A]=B
};