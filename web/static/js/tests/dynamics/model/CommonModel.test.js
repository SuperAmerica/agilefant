/*
 * DYNAMICS - MODEL - Common Model test
 */

$(document).ready(function() {
  
  module("Dynamics: Common Model", {
    setup: function() {
      this.commonModel = new CommonModel();
    },
    teardown: function() {
      
    }
  });
  
  test("Abstract reload", function() {
    var exceptionThrown = false;
    try {
      this.commonModel.reload();
    }
    catch(e) {
      same(e,"Abstract method called", "Abstract method exception was thrown");
      exceptionThrown = true;
    }
  });
  
});