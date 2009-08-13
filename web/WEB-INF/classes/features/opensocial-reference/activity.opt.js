opensocial.Activity=function(A){this.fields_=A
};
opensocial.Activity.Field={TITLE_ID:"titleId",TITLE:"title",TEMPLATE_PARAMS:"templateParams",URL:"url",MEDIA_ITEMS:"mediaItems",BODY_ID:"bodyId",BODY:"body",EXTERNAL_ID:"externalId",STREAM_TITLE:"streamTitle",STREAM_URL:"streamUrl",STREAM_SOURCE_URL:"streamSourceUrl",STREAM_FAVICON_URL:"streamFaviconUrl",PRIORITY:"priority",ID:"id",USER_ID:"userId",APP_ID:"appId",POSTED_TIME:"postedTime"};
opensocial.Activity.prototype.getId=function(){return this.getField(opensocial.Activity.Field.ID)
};
opensocial.Activity.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};
opensocial.Activity.prototype.setField=function(A,B){return this.fields_[A]=B
};