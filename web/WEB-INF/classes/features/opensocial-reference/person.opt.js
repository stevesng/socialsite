opensocial.Person=function(A,B,C){this.fields_=A||{};
this.isOwner_=B;
this.isViewer_=C
};
opensocial.Person.Field={ID:"id",NAME:"name",NICKNAME:"nickname",THUMBNAIL_URL:"thumbnailUrl",PROFILE_URL:"profileUrl",CURRENT_LOCATION:"currentLocation",ADDRESSES:"addresses",EMAILS:"emails",PHONE_NUMBERS:"phoneNumbers",ABOUT_ME:"aboutMe",STATUS:"status",PROFILE_SONG:"profileSong",PROFILE_VIDEO:"profileVideo",GENDER:"gender",SEXUAL_ORIENTATION:"sexualOrientation",RELATIONSHIP_STATUS:"relationshipStatus",AGE:"age",DATE_OF_BIRTH:"dateOfBirth",BODY_TYPE:"bodyType",ETHNICITY:"ethnicity",SMOKER:"smoker",DRINKER:"drinker",CHILDREN:"children",PETS:"pets",LIVING_ARRANGEMENT:"livingArrangement",TIME_ZONE:"timeZone",LANGUAGES_SPOKEN:"languagesSpoken",JOBS:"jobs",JOB_INTERESTS:"jobInterests",SCHOOLS:"schools",INTERESTS:"interests",URLS:"urls",MUSIC:"music",MOVIES:"movies",TV_SHOWS:"tvShows",BOOKS:"books",ACTIVITIES:"activities",SPORTS:"sports",HEROES:"heroes",QUOTES:"quotes",CARS:"cars",FOOD:"food",TURN_ONS:"turnOns",TURN_OFFS:"turnOffs",TAGS:"tags",ROMANCE:"romance",SCARED_OF:"scaredOf",HAPPIEST_WHEN:"happiestWhen",FASHION:"fashion",HUMOR:"humor",LOOKING_FOR:"lookingFor",RELIGION:"religion",POLITICAL_VIEWS:"politicalViews",HAS_APP:"hasApp",NETWORK_PRESENCE:"networkPresence"};
opensocial.Person.prototype.getId=function(){return this.getField(opensocial.Person.Field.ID)
};
var ORDERED_NAME_FIELDS_=[opensocial.Name.Field.HONORIFIC_PREFIX,opensocial.Name.Field.GIVEN_NAME,opensocial.Name.Field.FAMILY_NAME,opensocial.Name.Field.HONORIFIC_SUFFIX,opensocial.Name.Field.ADDITIONAL_NAME];
opensocial.Person.prototype.getDisplayName=function(){var B=this.getField(opensocial.Person.Field.NAME);
if(B){var E=B.getField(opensocial.Name.Field.UNSTRUCTURED);
if(E){return E
}var D="";
for(var C=0;
C<ORDERED_NAME_FIELDS_.length;
C++){var A=B.getField(ORDERED_NAME_FIELDS_[C]);
if(A){D+=A+" "
}}return D.replace(/^\s+|\s+$/g,"")
}return this.getField(opensocial.Person.Field.NICKNAME)
};
opensocial.Person.prototype.getField=function(A,B){return opensocial.Container.getField(this.fields_,A,B)
};
opensocial.Person.prototype.isViewer=function(){return !!this.isViewer_
};
opensocial.Person.prototype.isOwner=function(){return !!this.isOwner_
};