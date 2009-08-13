var JsonPerson=function(A){A=A||{};
JsonPerson.constructObject(A,"bodyType",opensocial.BodyType);
JsonPerson.constructObject(A,"currentLocation",opensocial.Address);
JsonPerson.constructObject(A,"dateOfBirth",Date);
JsonPerson.constructObject(A,"name",opensocial.Name);
JsonPerson.constructObject(A,"profileSong",opensocial.Url);
JsonPerson.constructObject(A,"profileVideo",opensocial.Url);
JsonPerson.constructArrayObject(A,"addresses",opensocial.Address);
JsonPerson.constructArrayObject(A,"emails",opensocial.Email);
JsonPerson.constructArrayObject(A,"jobs",opensocial.Organization);
JsonPerson.constructArrayObject(A,"phoneNumbers",opensocial.Phone);
JsonPerson.constructArrayObject(A,"schools",opensocial.Organization);
JsonPerson.constructArrayObject(A,"urls",opensocial.Url);
JsonPerson.constructEnum(A,"gender");
JsonPerson.constructEnum(A,"smoker");
JsonPerson.constructEnum(A,"drinker");
JsonPerson.constructEnum(A,"networkPresence");
JsonPerson.constructEnumArray(A,"lookingFor");
opensocial.Person.call(this,A,A.isOwner,A.isViewer)
};
JsonPerson.inherits(opensocial.Person);
JsonPerson.constructEnum=function(B,C){var A=B[C];
if(A){B[C]=new opensocial.Enum(A.key,A.displayValue)
}};
JsonPerson.constructEnumArray=function(C,D){var B=C[D];
if(B){for(var A=0;
A<B.length;
A++){B[A]=new opensocial.Enum(B[A].key,B[A].displayValue)
}}};
JsonPerson.constructObject=function(C,D,A){var B=C[D];
if(B){C[D]=new A(B)
}};
JsonPerson.constructArrayObject=function(D,E,B){var C=D[E];
if(C){for(var A=0;
A<C.length;
A++){C[A]=new B(C[A])
}}};
JsonPerson.prototype.getDisplayName=function(){return this.getField("displayName")
};