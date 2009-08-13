var gadgets=gadgets||{};
gadgets.pubsubrouter=(function(){var C;
var E={};
var F;
var A;
var D;
function B(M,K,J){var G=this.f;
var H=C(G);
if(H){switch(M){case"subscribe":if(F&&F(G,K)){break
}if(!E[K]){E[K]={}
}E[K][G]=true;
break;
case"unsubscribe":if(A&&A(G,K)){break
}if(E[K]){delete E[K][G]
}break;
case"publish":if(D&&D(G,K,J)){break
}var L=E[K];
if(L){for(var I in L){gadgets.rpc.call(I,"pubsub",null,K,H,J)
}}break;
default:throw new Error("Unknown pubsub command")
}}}return{init:function(G,H){if(typeof G!="function"){throw new Error("Invalid handler")
}if(typeof H==="object"){F=H.onSubscribe;
A=H.onUnsubscribe;
D=H.onPublish
}C=G;
gadgets.rpc.register("pubsub",B)
}}
})();