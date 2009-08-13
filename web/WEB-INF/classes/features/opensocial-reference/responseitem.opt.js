opensocial.ResponseItem=function(A,C,B,D){this.originalDataRequest_=A;
this.data_=C;
this.errorCode_=B;
this.errorMessage_=D
};
opensocial.ResponseItem.prototype.hadError=function(){return !!this.errorCode_
};
opensocial.ResponseItem.Error={NOT_IMPLEMENTED:"notImplemented",UNAUTHORIZED:"unauthorized",FORBIDDEN:"forbidden",BAD_REQUEST:"badRequest",INTERNAL_ERROR:"internalError",LIMIT_EXCEEDED:"limitExceeded"};
opensocial.ResponseItem.prototype.getErrorCode=function(){return this.errorCode_
};
opensocial.ResponseItem.prototype.getErrorMessage=function(){return this.errorMessage_
};
opensocial.ResponseItem.prototype.getOriginalDataRequest=function(){return this.originalDataRequest_
};
opensocial.ResponseItem.prototype.getData=function(){return this.data_
};