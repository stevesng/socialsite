opensocial.DataResponse=function(A,B,C){this.responseItems_=A;
this.globalError_=B;
this.errorMessage_=C
};
opensocial.DataResponse.prototype.hadError=function(){return !!this.globalError_
};
opensocial.DataResponse.prototype.getErrorMessage=function(){return this.errorMessage_
};
opensocial.DataResponse.prototype.get=function(A){return this.responseItems_[A]
};