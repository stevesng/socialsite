opensocial.Message=function(A,B){this.fields_=B||{};
this.fields_[opensocial.Message.Field.BODY]=A
};
opensocial.Message.Field={TYPE:"type",TITLE:"title",BODY:"body",TITLE_ID:"titleId",BODY_ID:"bodyId"};
opensocial.Message.Type={EMAIL:"email",NOTIFICATION:"notification",PRIVATE_MESSAGE:"privateMessage",PUBLIC_MESSAGE:"publicMessage"};
opensocial.Message.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};
opensocial.Message.prototype.setField=function(A,B){return this.fields_[A]=B
};