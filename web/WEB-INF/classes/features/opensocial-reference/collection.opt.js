opensocial.Collection=function(C,B,A){this.array_=C||[];
this.offset_=B||0;
this.totalSize_=A||this.array_.length
};
opensocial.Collection.prototype.getById=function(C){for(var A=0;
A<this.size();
A++){var B=this.array_[A];
if(B.getId()==C){return B
}}return null
};
opensocial.Collection.prototype.size=function(){return this.array_.length
};
opensocial.Collection.prototype.each=function(B){for(var A=0;
A<this.size();
A++){B(this.array_[A])
}};
opensocial.Collection.prototype.asArray=function(){return this.array_
};
opensocial.Collection.prototype.getTotalSize=function(){return this.totalSize_
};
opensocial.Collection.prototype.getOffset=function(){return this.offset_
};