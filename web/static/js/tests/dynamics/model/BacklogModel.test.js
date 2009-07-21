/*
 * DYNAMICS - MODEL - Backlog Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Backlog Model");
  
  test("Initialization", function() {
    var commonModelInitialized = false;
    var originalCMInit = CommonModel.prototype.initialize; 
    CommonModel.prototype.initialize = function() {
      commonModelInitialized = true;
    };
    
    var blog = new BacklogModel();
    
    blog.initializeBacklogModel();
    
    ok(commonModelInitialized, "The common model initialize method is called");
    CommonModel.prototype.initialize = originalCMInit;
  });
});