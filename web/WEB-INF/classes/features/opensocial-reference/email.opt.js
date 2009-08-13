opensocial.Email=function(A){this.fields_=A||{}
};
opensocial.Email.Field={TYPE:"type",ADDRESS:"address"};
opensocial.Email.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};