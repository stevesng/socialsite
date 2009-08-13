opensocial.Phone=function(A){this.fields_=A||{}
};
opensocial.Phone.Field={TYPE:"type",NUMBER:"number"};
opensocial.Phone.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};