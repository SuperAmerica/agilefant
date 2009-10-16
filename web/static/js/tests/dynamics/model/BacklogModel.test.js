/*
 * DYNAMICS - MODEL - Backlog Model test
 */

$(document).ready(function() {
  
  module("Dynamics: BacklogModel");
  
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
  
  
  
  module("Dynamics: BacklogModel validation", {
    setup: function() {
      this.mockControl = new MockControl();
      this.model = this.mockControl.createMock(IterationModel);
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
  
  function _setDates(model, startString, endString) {
    model.expects().getStartDate().andReturn(startString);
    model.expects().getEndDate().andReturn(endString);
  };
  
  test("Date validation", function() {
    
    // Valid
    _setDates(this.model, "2009-10-01 12:00", "2009-10-01 12:00");
    BacklogModel.Validators.dateValidator(this.model);
    
    _setDates(this.model, "2008-11-01 12:00", "2009-10-01 12:00");
    BacklogModel.Validators.dateValidator(this.model);
    
    // Invalid
    _setDates(this.model, "2009-11-01 12:01", "2009-10-01 12:00");
    try {
      BacklogModel.Validators.dateValidator(this.model);
      ok(false, "No error thrown");
    }
    catch (e) {
      same(e, "Start date must be before end date", "Correct error message");
    }
    
  });
});
