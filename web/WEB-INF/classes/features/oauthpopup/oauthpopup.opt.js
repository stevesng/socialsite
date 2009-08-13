var gadgets=gadgets||{};
gadgets.oauth=gadgets.oauth||{};
gadgets.oauth.Popup=function(A,D,B,C){this.destination_=A;
this.windowOptions_=D;
this.openCallback_=B;
this.closeCallback_=C;
this.win_=null
};
gadgets.oauth.Popup.prototype.createOpenerOnClick=function(){var A=this;
return function(){A.onClick_()
}
};
gadgets.oauth.Popup.prototype.onClick_=function(){this.win_=window.open(this.destination_,"_blank",this.windowOptions_);
if(this.win_){var A=this;
var B=function(){A.checkClosed_()
};
this.timer_=window.setInterval(B,100);
this.openCallback_()
}return false
};
gadgets.oauth.Popup.prototype.checkClosed_=function(){if((!this.win_)||this.win_.closed){this.win_=null;
this.handleApproval_()
}};
gadgets.oauth.Popup.prototype.handleApproval_=function(){if(this.timer_){window.clearInterval(this.timer_);
this.timer_=null
}if(this.win_){this.win_.close();
this.win_=null
}this.closeCallback_();
return false
};
gadgets.oauth.Popup.prototype.createApprovedOnClick=function(){var A=this;
return function(){A.handleApproval_()
}
};