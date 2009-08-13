os.resolveOpenSocialIdentifier=function(D,C){if(typeof (D[C])!="undefined"){return D[C]
}var G=os.getPropertyGetterName(C);
if(D[G]){return D[G]()
}if(D.getField){var B=D.getField(C);
if(B){return B
}}if(D.get){var E=D.get(C);
if(E&&E.getData){var F=E.getData();
return F.array_||F
}return E
}var A;
return A
};
os.setIdentifierResolver(os.resolveOpenSocialIdentifier);
os.createOpenSocialGetMethods_=function(C,B){if(C&&B){for(var D in B){var E=B[D];
var A=os.getPropertyGetterName(E);
C.prototype[A]=function(){this.getField(D)
}
}}};
os.registerOpenSocialFields_=function(){var A=os.resolveOpenSocialIdentifier.FIELDS;
if(opensocial){if(opensocial.Person){}}};
os.registerOpenSocialFields_();