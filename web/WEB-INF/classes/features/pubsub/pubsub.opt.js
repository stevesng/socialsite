var gadgets=gadgets||{};
gadgets.pubsub=(function(){var B={};
function A(E,C,D){var F=B[E];
if(typeof F==="function"){F(C,D)
}}return{publish:function(D,C){gadgets.rpc.call("..","pubsub",null,"publish",D,C)
},subscribe:function(C,D){B[C]=D;
gadgets.rpc.register("pubsub",A);
gadgets.rpc.call("..","pubsub",null,"subscribe",C)
},unsubscribe:function(C){delete B[C];
gadgets.rpc.call("..","pubsub",null,"unsubscribe",C)
}}
})();