
$(document).ready(function() {
  
  module("Dynamics: UserModel");
  
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
  
  
  module("Dynamics: UserModel validation", {
    setup: function() {
      this.mockControl = new MockControl();
      this.model = this.mockControl.createMock(UserModel);
    },
    teardown: function() {
      this.mockControl.verify();
    }
  });
  
  function _expectPasswords(model, pass1, pass2) {
    model.expects().getPassword1().andReturn(pass1);
    model.expects().getPassword2().andReturn(pass2);
  }
  
  function _passwordValidate(model, shouldPass) {
    try {
      UserModel.Validators.passwordValidator(model);
      ok(shouldPass, "Validated correctly");
    } catch (e) {
      same(e, "Passwords don't match", "Error message correct");
    } 
  }
  
  test("Password validation", function() {
    
    // Valid
    _expectPasswords(this.model, "Foo", "Foo");
    _passwordValidate(this.model, true);
    
    // Invalid
    _expectPasswords(this.model, "Foo", "foo");
    _passwordValidate(this.model, false);

    _expectPasswords(this.model, " ", "");
    _passwordValidate(this.model, false);
    
    _expectPasswords(this.model, "Completely", "Different");
    _passwordValidate(this.model, false);
    
    _expectPasswords(this.model, "Ä", "ä");
    _passwordValidate(this.model, false);
  });
  
  
  test("Login name validation", function() {
    /*
     * We expect that the AJAX connection works.
     */
    var ajaxCall = 0;
    var originalAjax = UserModel.Validators._ajaxCheckLoginName;
    UserModel.Validators._ajaxCheckLoginName = function(name) {
      ajaxCall++;
      return name.toLowerCase() !== "paavo";
    };
       
    function expectLoginNames(model, persisted, current) {
      model.expects().getPersistedData().andReturn({
        loginName: persisted
      });
      model.expects().getCurrentData().andReturn({
        loginName: current
      });
    };
    
    // Should not call ajax, when login name does not change
    expectLoginNames(this.model, "paavo", "paavo");
    try {
      UserModel.Validators.loginNameValidator(this.model);
      ok(true, "Validation passed");
    }
    catch (e) {
      ok(false, "Valid validation should not throw exception");
    }
    same(ajaxCall, 0, "Ajax function not called");

    
    // Ok validation
    expectLoginNames(this.model, "", "martti");
    try {
      UserModel.Validators.loginNameValidator(this.model);
      ok(true, "Validation passed");
    }
    catch (e) {
      ok(false, "Valid validation should not throw exception");
    }
    same(ajaxCall, 1, "Ajax function called");


    // Throws exception
    expectLoginNames(this.model, "", "paavo");
    try {
      UserModel.Validators.loginNameValidator(this.model);
      ok(false, "Validation did not throw exception");
    } catch (e) {
      same(e, "Login name already in use", "Correct error message");
    }
    same(ajaxCall, 2, "Ajax function called");
    
    UserModel.Validators._ajaxCheckLoginName = originalAjax;
  });
});