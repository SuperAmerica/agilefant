extendObject = function(childClass, baseClass) {
  var tmpBase = function() {};
  tmpBase.prototype = baseClass.prototype;
  childClass.prototype = new tmpBase();
  childClass.prototype._super = baseClass;
};