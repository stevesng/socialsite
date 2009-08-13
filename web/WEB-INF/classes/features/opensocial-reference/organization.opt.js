opensocial.Organization=function(A){this.fields_=A||{}
};
opensocial.Organization.Field={NAME:"name",TITLE:"title",DESCRIPTION:"description",FIELD:"field",SUB_FIELD:"subField",START_DATE:"startDate",END_DATE:"endDate",SALARY:"salary",ADDRESS:"address",WEBPAGE:"webpage"};
opensocial.Organization.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};