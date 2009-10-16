
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
  
});