
$(document).ready(function() {
  
  module("Dynamics: User model");
  
  test("Construction", function() {
    var commonModelInitialized = false;
    var originalCommonInit = CommonModel.prototype.initialize;
    CommonModel.prototype.initialize = function() {
      commonModelInitialized = true;
    };
    
    var user = new UserModel();
    
    ok(commonModelInitialized, "Common model initialized");
    
    CommonModel.prototype.initialize = originalCommonInit;
  });
  
});