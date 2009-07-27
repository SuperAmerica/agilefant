var DynamicsComparators = {
  doCompare : function(value1, value2) {
    if (value1 > value2) {
      return 1;
    } else if (value1 < value2) {
      return -1;
    } else {
      return 0;
    }
  },
  genericComparator : function(obj1, obj2, targetMethod) {
    var value1 = targetMethod.call(obj1);
    var value2 = targetMethod.call(obj2);
    return DynamicsComparators.doCompare(value1, value2);
  },
  nameComparator : function(obj1, obj2) {
    return DynamicsComparators.doCompare(obj1.getName(), obj2.getName());
  }
};
