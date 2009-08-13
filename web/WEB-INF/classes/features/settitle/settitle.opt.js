var gadgets=gadgets||{};
gadgets.window=gadgets.window||{};
gadgets.window.setTitle=function(A){gadgets.rpc.call(null,"set_title",null,A)
};
var _IG_SetTitle=gadgets.window.setTitle;