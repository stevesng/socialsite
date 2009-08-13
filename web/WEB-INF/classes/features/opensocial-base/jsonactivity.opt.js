var JsonActivity=function(A,B){A=A||{};
if(!B){JsonActivity.constructArrayObject(A,"mediaItems",JsonMediaItem)
}opensocial.Activity.call(this,A)
};
JsonActivity.inherits(opensocial.Activity);
JsonActivity.prototype.toJsonObject=function(){var C=JsonActivity.copyFields(this.fields_);
var D=C.mediaItems||[];
var A=[];
for(var B=0;
B<D.length;
B++){A[B]=D[B].toJsonObject()
}C.mediaItems=A;
return C
};
var JsonMediaItem=function(A){opensocial.MediaItem.call(this,A.mimeType,A.url,A)
};
JsonMediaItem.inherits(opensocial.MediaItem);
JsonMediaItem.prototype.toJsonObject=function(){return JsonActivity.copyFields(this.fields_)
};
JsonActivity.constructArrayObject=function(D,E,B){var C=D[E];
if(C){for(var A=0;
A<C.length;
A++){C[A]=new B(C[A])
}}};
JsonActivity.copyFields=function(A){var B={};
for(var C in A){B[C]=A[C]
}return B
};