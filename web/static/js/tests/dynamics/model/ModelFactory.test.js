
$(document).ready(function() {
  
  module("Dynamics: Model factory",{
    setup: function() {
      this.original = ModelFactory;
    },  
    teardown: function() {
      ModelFactory = this.original;
    }
  });
  
  
  test("Get instance", function() {
    ModelFactory.instance = null;
    var instance = ModelFactory.getInstance();
    
    ok(ModelFactory.instance, "Instance has been created");
    
    var anotherInstance = ModelFactory.getInstance();
    equals(anotherInstance, instance, "Instance is singleton");
  });
});