opensocial.Address=function(A){this.fields_=A||{}
};
opensocial.Address.Field={TYPE:"type",UNSTRUCTURED_ADDRESS:"unstructuredAddress",PO_BOX:"poBox",STREET_ADDRESS:"streetAddress",EXTENDED_ADDRESS:"extendedAddress",REGION:"region",LOCALITY:"locality",POSTAL_CODE:"postalCode",COUNTRY:"country",LATITUDE:"latitude",LONGITUDE:"longitude"};
opensocial.Address.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};