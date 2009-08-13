gadgets.Prefs.prototype.set=function(D,E){if(arguments.length>2){var F={};
for(var C=0,B=arguments.length;
C<B;
C+=2){F[arguments[C]]=arguments[C+1]
}gadgets.Prefs.setInternal_(F)
}else{gadgets.Prefs.setInternal_(D,E)
}var A=[null,"set_pref",null,gadgets.util.getUrlParameters().ifpctok||0].concat(Array.prototype.slice.call(arguments));
gadgets.rpc.call.apply(gadgets.rpc,A)
};
gadgets.Prefs.prototype.setArray=function(C,D){for(var B=0,A=D.length;
B<A;
++B){if(typeof D[B]!=="number"){D[B]=D[B].replace(/\|/g,"%7C")
}}this.set(C,D.join("|"))
};