var FieldTranslations={};
FieldTranslations.translateServerPersonToJsPerson=function(A){if(A.emails){for(var E=0;
E<A.emails.length;
E++){A.emails[E].address=A.emails[E].value
}}if(A.phoneNumbers){for(var F=0;
F<A.phoneNumbers.length;
F++){A.phoneNumbers[F].number=A.phoneNumbers[F].value
}}if(A.birthday){A.dateOfBirth=A.birthday
}if(A.utcOffset){A.timeZone=A.utcOffset
}if(A.addresses){for(var C=0;
C<A.addresses.length;
C++){A.addresses[C].unstructuredAddress=A.addresses[C].formatted
}}if(A.gender){var D=A.gender=="male"?"MALE":(A.gender=="female")?"FEMALE":null;
A.gender={key:D,displayValue:A.gender}
}FieldTranslations.translateUrlJson(A.profileSong);
FieldTranslations.translateUrlJson(A.profileVideo);
if(A.urls){for(var B=0;
B<A.urls.length;
B++){FieldTranslations.translateUrlJson(A.urls[B])
}}FieldTranslations.translateEnumJson(A.drinker);
FieldTranslations.translateEnumJson(A.lookingFor);
FieldTranslations.translateEnumJson(A.networkPresence);
FieldTranslations.translateEnumJson(A.smoker);
if(A.organizations){A.jobs=[];
A.schools=[];
for(var G=0;
G<A.organizations.length;
G++){var H=A.organizations[G];
if(H.type=="job"){A.jobs.push(H)
}else{if(H.type=="school"){A.schools.push(H)
}}}}if(A.name){A.name.unstructured=A.name.formatted
}};
FieldTranslations.translateEnumJson=function(A){if(A){A.key=A.value
}};
FieldTranslations.translateUrlJson=function(A){if(A){A.address=A.value
}};
FieldTranslations.translateJsPersonFieldsToServerFields=function(A){for(var B=0;
B<A.length;
B++){if(A[B]=="dateOfBirth"){A[B]="birthday"
}else{if(A[B]=="timeZone"){A[B]="utcOffset"
}}}A.push("id");
A.push("displayName")
};