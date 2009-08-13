opensocial.Enum=function(B,A){this.key=B;
this.displayValue=A
};
opensocial.Enum.prototype.getKey=function(){return gadgets.util.escape(this.key)
};
opensocial.Enum.prototype.getDisplayValue=function(){return gadgets.util.escape(this.displayValue)
};
opensocial.Enum.Smoker={NO:"NO",YES:"YES",SOCIALLY:"SOCIALLY",OCCASIONALLY:"OCCASIONALLY",REGULARLY:"REGULARLY",HEAVILY:"HEAVILY",QUITTING:"QUITTING",QUIT:"QUIT"};
opensocial.Enum.Drinker={NO:"NO",YES:"YES",SOCIALLY:"SOCIALLY",OCCASIONALLY:"OCCASIONALLY",REGULARLY:"REGULARLY",HEAVILY:"HEAVILY",QUITTING:"QUITTING",QUIT:"QUIT"};
opensocial.Enum.Gender={MALE:"MALE",FEMALE:"FEMALE"};
opensocial.Enum.LookingFor={DATING:"DATING",FRIENDS:"FRIENDS",RELATIONSHIP:"RELATIONSHIP",NETWORKING:"NETWORKING",ACTIVITY_PARTNERS:"ACTIVITY_PARTNERS",RANDOM:"RANDOM"};
opensocial.Enum.Presence={AWAY:"AWAY",CHAT:"CHAT",DND:"DND",OFFLINE:"OFFLINE",ONLINE:"ONLINE",XA:"XA"};