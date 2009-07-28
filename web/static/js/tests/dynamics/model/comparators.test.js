$(document).ready(function() { 
  module("Dynamics: Comparators", {
    setup: function() {
      this.testObj = function(val) {
        this.val = val;
      };
      this.testObj.prototype.getProp = function() {
        return this.val;
      };
    }, teardown: function() {
      
    }
  });
  test("do compare", function() {
    equals(DynamicsComparators.doCompare(1, 2), -1, "Smaller than");
    equals(DynamicsComparators.doCompare(2, 2), 0, "Equals to");
    equals(DynamicsComparators.doCompare(3, 2), 1, "Greater than");
  });
  
  test("generic comparator", function() {
    var value2 = new this.testObj(2);
    var value1 = new this.testObj(0);
    var value3 = new this.testObj(3);
    
    equals(DynamicsComparators.genericComparator(value1, value2, this.testObj.prototype.getProp), -1, "Smaller than");
    equals(DynamicsComparators.genericComparator(value2, value2, this.testObj.prototype.getProp), 0, "Equals to");
    equals(DynamicsComparators.genericComparator(value3, value2, this.testObj.prototype.getProp), 1, "Greater than");
  });
  
  test("value comparator factory", function() {
    var comparator = new DynamicsComparators.valueComparatorFactory(this.testObj.prototype.getProp);
    ok(typeof comparator === "function", "Correct type");
  });
});