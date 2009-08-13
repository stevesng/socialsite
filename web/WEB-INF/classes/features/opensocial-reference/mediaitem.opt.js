opensocial.MediaItem=function(C,A,B){this.fields_=B||{};
this.fields_[opensocial.MediaItem.Field.MIME_TYPE]=C;
this.fields_[opensocial.MediaItem.Field.URL]=A
};
opensocial.MediaItem.Type={IMAGE:"image",VIDEO:"video",AUDIO:"audio"};
opensocial.MediaItem.Field={TYPE:"type",MIME_TYPE:"mimeType",URL:"url"};
opensocial.MediaItem.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};
opensocial.MediaItem.prototype.setField=function(A,B){return this.fields_[A]=B
};