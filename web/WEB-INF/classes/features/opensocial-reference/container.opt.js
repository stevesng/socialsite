opensocial.Container=function(){};
opensocial.Container.container_=null;
opensocial.Container.setContainer=function(A){opensocial.Container.container_=A
};
opensocial.Container.get=function(){return opensocial.Container.container_
};
opensocial.Container.prototype.getEnvironment=function(){};
opensocial.Container.prototype.requestSendMessage=function(A,D,B,C){if(B){window.setTimeout(function(){B(new opensocial.ResponseItem(null,null,opensocial.ResponseItem.Error.NOT_IMPLEMENTED,null))
},0)
}};
opensocial.Container.prototype.requestShareApp=function(A,D,B,C){if(B){window.setTimeout(function(){B(new opensocial.ResponseItem(null,null,opensocial.ResponseItem.Error.NOT_IMPLEMENTED,null))
},0)
}};
opensocial.Container.prototype.requestCreateActivity=function(C,B,A){if(A){window.setTimeout(function(){A(new opensocial.ResponseItem(null,null,opensocial.ResponseItem.Error.NOT_IMPLEMENTED,null))
},0)
}};
opensocial.Container.prototype.hasPermission=function(A){return false
};
opensocial.Container.prototype.requestPermission=function(B,C,A){if(A){window.setTimeout(function(){A(new opensocial.ResponseItem(null,null,opensocial.ResponseItem.Error.NOT_IMPLEMENTED,null))
},0)
}};
opensocial.Container.prototype.requestData=function(A,B){};
opensocial.Container.prototype.newFetchPersonRequest=function(B,A){};
opensocial.Container.prototype.newFetchPeopleRequest=function(A,B){};
opensocial.Container.prototype.newFetchPersonAppDataRequest=function(A,C,B){};
opensocial.Container.prototype.newUpdatePersonAppDataRequest=function(C,A,B){};
opensocial.Container.prototype.newRemovePersonAppDataRequest=function(B,A){};
opensocial.Container.prototype.newFetchActivitiesRequest=function(A,B){};
opensocial.Container.prototype.newCollection=function(C,B,A){return new opensocial.Collection(C,B,A)
};
opensocial.Container.prototype.newPerson=function(A,B,C){return new opensocial.Person(A,B,C)
};
opensocial.Container.prototype.newActivity=function(A){return new opensocial.Activity(A)
};
opensocial.Container.prototype.newMediaItem=function(C,A,B){return new opensocial.MediaItem(C,A,B)
};
opensocial.Container.prototype.newMessage=function(A,B){return new opensocial.Message(A,B)
};
opensocial.Container.prototype.newIdSpec=function(A){return new opensocial.IdSpec(A)
};
opensocial.Container.prototype.newNavigationParameters=function(A){return new opensocial.NavigationParameters(A)
};
opensocial.Container.prototype.newResponseItem=function(A,C,B,D){return new opensocial.ResponseItem(A,C,B,D)
};
opensocial.Container.prototype.newDataResponse=function(A,B){return new opensocial.DataResponse(A,B)
};
opensocial.Container.prototype.newDataRequest=function(){return new opensocial.DataRequest()
};
opensocial.Container.prototype.newEnvironment=function(B,A){return new opensocial.Environment(B,A)
};
opensocial.Container.isArray=function(A){return A instanceof Array
};
opensocial.Container.getField=function(A,B,C){var D=A[B];
return opensocial.Container.escape(D,C,false)
};
opensocial.Container.escape=function(C,B,A){if(B&&B.escapeType=="none"){return C
}else{return gadgets.util.escape(C,A)
}};
var cajita;
var ___;
var attachDocumentStub;
var uriCallback={rewrite:function rewrite(B,A){B=String(B);
if(/^#/.test(B)){return"#"+encodeURIComponent(decodeURIComponent(B.substring(1)))
}else{if(/^\/(?:[^\/][^?#]*)?$/.test(B)){return encodeURI(decodeURI(B))
}}return null
}};
opensocial.Container.prototype.enableCaja=function(){___=window.___;
cajita=window.cajita;
valijaMaker=window.valijaMaker;
attachDocumentStub=window.attachDocumentStub;
var A=___.copy(___.sharedImports);
A.outers=A;
var D=document.createElement("div");
D.className="g___";
attachDocumentStub("-g___",uriCallback,A,D);
A.$v=valijaMaker.CALL___(A.outers);
A.htmlEmitter___=new HtmlEmitter(D);
document.body.appendChild(D);
___.getNewModuleHandler().setImports(A);
A.outers.gadgets=gadgets;
A.outers.opensocial=opensocial;
var C={c_gadgets:{c_MiniMessage:{m_createDismissibleMessage:0,m_createStaticMessage:0,m_createTimerMessage:0,m_dismissMessage:0},c_Prefs:{m_getArray:0,m_getBool:0,m_getCountry:0,m_getFloat:0,m_getInt:0,m_getLang:0,m_getMsg:0,m_getString:0,m_set:0,m_setArray:0},c_Tab:{m_getCallback:0,m_getContentContainer:0,m_getIndex:0,m_getName:0,m_getNameContainer:0},c_TabSet:{m_addTab:0,m_alignTabs:0,m_displayTabs:0,m_getHeaderContainer:0,m_getSelectedTab:0,m_getTabs:0,m_removeTab:0,m_setSelectedTab:0,m_swapTabs:0},c_flash:{s_embedCachedFlash:0,s_embedFlash:0,s_getMajorVersion:0},c_io:{c_AuthorizationType:{s_NONE:0,s_OAUTH:0,s_SIGNED:0},c_ContentType:{s_DOM:0,s_FEED:0,s_JSON:0,s_TEXT:0},c_MethodType:{s_DELETE:0,s_GET:0,s_HEAD:0,s_POST:0,s_PUT:0},c_ProxyUrlRequestParameters:{s_REFRESH_INTERVAL:0},c_RequestParameters:{s_AUTHORIZATION:0,s_CONTENT_TYPE:0,s_GET_SUMMARIES:0,s_HEADERS:0,s_METHOD:0,s_NUM_ENTRIES:0,s_POST_DATA:0},s_encodeValues:0,s_getProxyUrl:0,s_makeRequest:0},c_json:{s_parse:0,s_stringify:0},c_pubsub:{s_publish:0,s_subscribe:0,s_unsubscribe:0},c_rpc:{s_call:0,s_register:0,s_registerDefault:0,s_unregister:0,s_unregisterDefault:0},c_skins:{c_Property:{s_ANCHOR_COLOR:0,s_BG_COLOR:0,s_BG_IMAGE:0,s_FONT_COLOR:0},s_getProperty:0},c_util:{s_escapeString:0,s_getFeatureParameters:0,s_hasFeature:0,s_registerOnLoadHandler:0,s_unescapeString:0},c_views:{c_View:{m_bind:0,m_getUrlTemplate:0,m_isOnlyVisibleGadget:0},c_ViewType:{s_CANVAS:0,s_HOME:0,s_PREVIEW:0,s_PROFILE:0},s_bind:0,s_getCurrentView:0,s_getParams:0,s_requestNavigateTo:0},c_window:{s_adjustHeight:0,s_getViewportDimensions:0,s_setTitle:0}},c_opensocial:{c_Activity:{c_Field:{s_APP_ID:0,s_BODY:0,s_BODY_ID:0,s_EXTERNAL_ID:0,s_ID:0,s_MEDIA_ITEMS:0,s_POSTED_TIME:0,s_PRIORITY:0,s_STREAM_FAVICON_URL:0,s_STREAM_SOURCE_URL:0,s_STREAM_TITLE:0,s_STREAM_URL:0,s_TEMPLATE_PARAMS:0,s_TITLE:0,s_TITLE_ID:0,s_URL:0,s_USER_ID:0},m_getField:0,m_getId:0,m_setField:0},c_Address:{c_Field:{s_COUNTRY:0,s_EXTENDED_ADDRESS:0,s_LATITUDE:0,s_LOCALITY:0,s_LONGITUDE:0,s_POSTAL_CODE:0,s_PO_BOX:0,s_REGION:0,s_STREET_ADDRESS:0,s_TYPE:0,s_UNSTRUCTURED_ADDRESS:0},m_getField:0},c_BodyType:{c_Field:{s_BUILD:0,s_EYE_COLOR:0,s_HAIR_COLOR:0,s_HEIGHT:0,s_WEIGHT:0},m_getField:0},c_Collection:{m_asArray:0,m_each:0,m_getById:0,m_getOffset:0,m_getTotalSize:0,m_size:0},c_CreateActivityPriority:{s_HIGH:0,s_LOW:0},c_DataRequest:{c_DataRequestFields:{s_ESCAPE_TYPE:0},c_FilterType:{s_ALL:0,s_HAS_APP:0,s_TOP_FRIENDS:0},c_PeopleRequestFields:{s_FILTER:0,s_FILTER_OPTIONS:0,s_FIRST:0,s_MAX:0,s_PROFILE_DETAILS:0,s_SORT_ORDER:0},c_SortOrder:{s_NAME:0,s_TOP_FRIENDS:0},m_add:0,m_newFetchActivitiesRequest:0,m_newFetchPeopleRequest:0,m_newFetchPersonAppDataRequest:0,m_newFetchPersonRequest:0,m_newRemovePersonAppDataRequest:0,m_newUpdatePersonAppDataRequest:0,m_send:0},c_DataResponse:{m_get:0,m_getErrorMessage:0,m_hadError:0},c_Email:{c_Field:{s_ADDRESS:0,s_TYPE:0},m_getField:0},c_Enum:{c_Drinker:{s_HEAVILY:0,s_NO:0,s_OCCASIONALLY:0,s_QUIT:0,s_QUITTING:0,s_REGULARLY:0,s_SOCIALLY:0,s_YES:0},c_Gender:{s_FEMALE:0,s_MALE:0},c_LookingFor:{s_ACTIVITY_PARTNERS:0,s_DATING:0,s_FRIENDS:0,s_NETWORKING:0,s_RANDOM:0,s_RELATIONSHIP:0},c_Presence:{s_AWAY:0,s_CHAT:0,s_DND:0,s_OFFLINE:0,s_ONLINE:0,s_XA:0},c_Smoker:{s_HEAVILY:0,s_NO:0,s_OCCASIONALLY:0,s_QUIT:0,s_QUITTING:0,s_REGULARLY:0,s_SOCIALLY:0,s_YES:0},m_getDisplayValue:0,m_getKey:0},c_Environment:{c_ObjectType:{s_ACTIVITY:0,s_ACTIVITY_MEDIA_ITEM:0,s_ADDRESS:0,s_BODY_TYPE:0,s_EMAIL:0,s_FILTER_TYPE:0,s_MESSAGE:0,s_MESSAGE_TYPE:0,s_NAME:0,s_ORGANIZATION:0,s_PERSON:0,s_PHONE:0,s_SORT_ORDER:0,s_URL:0},m_getDomain:0,m_supportsField:0},c_EscapeType:{s_HTML_ESCAPE:0,s_NONE:0},c_IdSpec:{c_Field:{s_GROUP_ID:0,s_NETWORK_DISTANCE:0,s_USER_ID:0},c_PersonId:{s_OWNER:0,s_VIEWER:0},m_getField:0,m_setField:0},c_MediaItem:{c_Field:{s_MIME_TYPE:0,s_TYPE:0,s_URL:0},c_Type:{s_AUDIO:0,s_IMAGE:0,s_VIDEO:0},m_getField:0,m_setField:0},c_Message:{c_Field:{s_BODY:0,s_BODY_ID:0,s_TITLE:0,s_TITLE_ID:0,s_TYPE:0},c_Type:{s_EMAIL:0,s_NOTIFICATION:0,s_PRIVATE_MESSAGE:0,s_PUBLIC_MESSAGE:0},m_getField:0,m_setField:0},c_Name:{c_Field:{s_ADDITIONAL_NAME:0,s_FAMILY_NAME:0,s_GIVEN_NAME:0,s_HONORIFIC_PREFIX:0,s_HONORIFIC_SUFFIX:0,s_UNSTRUCTURED:0},m_getField:0},c_NavigationParameters:{c_DestinationType:{s_RECIPIENT_DESTINATION:0,s_VIEWER_DESTINATION:0},c_Field:{s_OWNER:0,s_PARAMETERS:0,s_VIEW:0},m_getField:0,m_setField:0},c_Organization:{c_Field:{s_ADDRESS:0,s_DESCRIPTION:0,s_END_DATE:0,s_FIELD:0,s_NAME:0,s_SALARY:0,s_START_DATE:0,s_SUB_FIELD:0,s_TITLE:0,s_WEBPAGE:0},m_getField:0},c_Permission:{s_VIEWER:0},c_Person:{c_Field:{s_ABOUT_ME:0,s_ACTIVITIES:0,s_ADDRESSES:0,s_AGE:0,s_BODY_TYPE:0,s_BOOKS:0,s_CARS:0,s_CHILDREN:0,s_CURRENT_LOCATION:0,s_DATE_OF_BIRTH:0,s_DRINKER:0,s_EMAILS:0,s_ETHNICITY:0,s_FASHION:0,s_FOOD:0,s_GENDER:0,s_HAPPIEST_WHEN:0,s_HAS_APP:0,s_HEROES:0,s_HUMOR:0,s_ID:0,s_INTERESTS:0,s_JOBS:0,s_JOB_INTERESTS:0,s_LANGUAGES_SPOKEN:0,s_LIVING_ARRANGEMENT:0,s_LOOKING_FOR:0,s_MOVIES:0,s_MUSIC:0,s_NAME:0,s_NETWORK_PRESENCE:0,s_NICKNAME:0,s_PETS:0,s_PHONE_NUMBERS:0,s_POLITICAL_VIEWS:0,s_PROFILE_SONG:0,s_PROFILE_URL:0,s_PROFILE_VIDEO:0,s_QUOTES:0,s_RELATIONSHIP_STATUS:0,s_RELIGION:0,s_ROMANCE:0,s_SCARED_OF:0,s_SCHOOLS:0,s_SEXUAL_ORIENTATION:0,s_SMOKER:0,s_SPORTS:0,s_STATUS:0,s_TAGS:0,s_THUMBNAIL_URL:0,s_TIME_ZONE:0,s_TURN_OFFS:0,s_TURN_ONS:0,s_TV_SHOWS:0,s_URLS:0},m_getDisplayName:0,m_getField:0,m_getId:0,m_isOwner:0,m_isViewer:0},c_Phone:{c_Field:{s_NUMBER:0,s_TYPE:0},m_getField:0},c_ResponseItem:{c_Error:{s_BAD_REQUEST:0,s_FORBIDDEN:0,s_INTERNAL_ERROR:0,s_LIMIT_EXCEEDED:0,s_NOT_IMPLEMENTED:0,s_UNAUTHORIZED:0},m_getData:0,m_getErrorCode:0,m_getErrorMessage:0,m_getOriginalDataRequest:0,m_hadError:0},c_Url:{c_Field:{s_ADDRESS:0,s_LINK_TEXT:0,s_TYPE:0},m_getField:0},s_getEnvironment:0,s_hasPermission:0,s_newActivity:0,s_newDataRequest:0,s_newIdSpec:0,s_newMediaItem:0,s_newMessage:0,s_newNavigationParameters:0,s_requestCreateActivity:0,s_requestPermission:0,s_requestSendMessage:0,s_requestShareApp:0}};
function B(I,J){if(!J){return 
}for(var F in I){if(I.hasOwnProperty(F)){var E=F.match(/^([mcs])_(\w+)$/);
var H=E[1],G=E[2];
switch(H){case"c":___.grantRead(J,G);
B(I[F],J[G]);
break;
case"m":___.grantCall(J.prototype,G);
break;
case"f":___.grantRead(J.prototype,G);
break;
case"s":if("function"===typeof J[G]){___.grantCall(J,G)
}else{___.grantRead(J,G)
}break
}}}}B(C,window)
};