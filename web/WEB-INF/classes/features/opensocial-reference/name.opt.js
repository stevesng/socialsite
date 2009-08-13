opensocial.Name=function(A){this.fields_=A||{}
};
opensocial.Name.Field={FAMILY_NAME:"familyName",GIVEN_NAME:"givenName",ADDITIONAL_NAME:"additionalName",HONORIFIC_PREFIX:"honorificPrefix",HONORIFIC_SUFFIX:"honorificSuffix",UNSTRUCTURED:"unstructured"};
opensocial.Name.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};