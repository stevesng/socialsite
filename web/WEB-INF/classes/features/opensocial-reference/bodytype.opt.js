opensocial.BodyType=function(A){this.fields_=A||{}
};
opensocial.BodyType.Field={BUILD:"build",HEIGHT:"height",WEIGHT:"weight",EYE_COLOR:"eyeColor",HAIR_COLOR:"hairColor"};
opensocial.BodyType.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};