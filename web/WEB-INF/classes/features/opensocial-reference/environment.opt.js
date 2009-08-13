opensocial.Environment=function(B,A){this.domain=B;
this.supportedFields=A
};
opensocial.Environment.prototype.getDomain=function(){return this.domain
};
opensocial.Environment.ObjectType={PERSON:"person",ADDRESS:"address",BODY_TYPE:"bodyType",EMAIL:"email",NAME:"name",ORGANIZATION:"organization",PHONE:"phone",URL:"url",ACTIVITY:"activity",MEDIA_ITEM:"mediaItem",MESSAGE:"message",MESSAGE_TYPE:"messageType",SORT_ORDER:"sortOrder",FILTER_TYPE:"filterType"};
opensocial.Environment.prototype.supportsField=function(A,C){var B=this.supportedFields[A]||[];
return !!B[C]
};