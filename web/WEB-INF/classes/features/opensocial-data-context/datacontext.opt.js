var opensocial=opensocial||{};
opensocial.data=opensocial.data||{};
var osd=opensocial.data;
osd.DataContext=function(){var C=[];
var A={};
var E=function(G){if(G["*"]){return true
}for(var F in G){if(typeof A[F]==="undefined"){return false
}}return true
};
var D=function(G,F){if(E(G.keys)){G.callback(F)
}};
var B=function(G){for(var F=0;
F<C.length;
++F){var H=C[F];
if(H.keys[G]||H.keys["*"]){D(H,G)
}}};
return{dataSets_:A,registerListener:function(G,I){var H={keys:{},callback:I};
if(typeof G==="string"){H.keys[G]=true
}else{for(var F=0;
F<G.length;
F++){H.keys[G[F]]=true
}}C.push(H);
if(G!=="*"&&E(H.keys)){window.setTimeout(function(){H.callback()
},1)
}},getDataSet:function(F){return A[F]
},putDataSet:function(H,J){var I=J;
if(typeof I==="undefined"||I===null){return 
}if(I.getData){I=I.getData();
if(I.array_){var F=[];
for(var G=0;
G<I.array_.length;
G++){F.push(I.array_[G].fields_)
}I=F
}else{I=I.fields_||I
}}A[H]=I;
B(H)
},}
}();
osd.getDataContext=function(){return opensocial.data.DataContext
};