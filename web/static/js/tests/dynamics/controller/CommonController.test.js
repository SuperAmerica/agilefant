$(document).ready(function() { 
	module("Dynamics: Common controller");
	
	test("Add child controller", function() {
	  var testable = new CommonController();
	  testable.init();
	  
	  var child = new CommonController();
	  testable.addChildController("type1", child);
	  testable.addChildController("type2", child);
	  equals(testable.childControllers["type1"].length, 1, "Type 1 controller inserted");
    equals(testable.childControllers["type2"].length, 1, "Type 2 controller inserted");
	});
  test("Remove child controller", function() {
    var testable = new CommonController();
    testable.init();    
    var willStay = new CommonController();
    var removeMe = new CommonController();
    testable.addChildController("type1", removeMe);
    testable.addChildController("type1", willStay);
    
    
    equals(testable.childControllers["type1"].length, 2, "Insert OK");
    
    testable.removeChildController("type1", removeMe);
    equals(testable.childControllers["type1"].length, 1, "Correct child count after remove");
    equals(testable.childControllers["type1"][0], willStay, "Correct child remaining");
  });
  test("Call child controllers", function() {
    var testable = new CommonController();
    testable.init();
    
    CommonController.prototype.testMethod = function() {
      this.testMethodCalled = true;
    };
    var child1 = new CommonController();
    var child2 = new CommonController();
    testable.addChildController("type1", child1);
    testable.addChildController("type1", child2);
    testable.callChildcontrollers("type1", CommonController.prototype.testMethod);
    ok(child1.testMethodCalled, "Method called for child 1");
    ok(child2.testMethodCalled, "Method called for child 2");
  });
});