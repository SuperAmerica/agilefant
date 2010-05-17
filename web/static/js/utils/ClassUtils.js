extendObject = function(childClass, baseClass) {
  var TmpBase = function() {};
  TmpBase.prototype = baseClass.prototype;
  childClass.prototype = new TmpBase();
  childClass.prototype._super = baseClass;
};