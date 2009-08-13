os.Template=function(A){this.templateRoot_=document.createElement("span");
this.id=A||("template_"+os.Template.idCounter_++)
};
os.Template.idCounter_=0;
os.registeredTemplates_={};
os.registerTemplate=function(A){os.registeredTemplates_[A.id]=A
};
os.unRegisterTemplate=function(A){delete os.registeredTemplates_[A.id]
};
os.getTemplate=function(A){return os.registeredTemplates_[A]
};
os.Template.prototype.setCompiledNode_=function(A){os.removeChildren(this.templateRoot_);
this.templateRoot_.appendChild(A)
};
os.Template.prototype.setCompiledNodes_=function(A){os.removeChildren(this.templateRoot_);
for(var B=0;
B<A.length;
B++){this.templateRoot_.appendChild(A[B])
}};
os.Template.prototype.render=function(A,B){if(!B){B=os.createContext(A)
}return os.renderTemplateNode_(this.templateRoot_,B)
};
os.Template.prototype.renderInto=function(C,B,D){if(!D){D=os.createContext(B)
}var A=this.render(B,D);
os.removeChildren(C);
os.appendChildren(A,C);
os.fireCallbacks(D)
};