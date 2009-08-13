opensocial.Url=function(A){this.fields_=A||{}
};
opensocial.Url.Field={TYPE:"type",LINK_TEXT:"linkText",ADDRESS:"address"};
opensocial.Url.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};