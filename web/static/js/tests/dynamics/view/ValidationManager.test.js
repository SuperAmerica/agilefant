$(document).ready(function() {
  module("Validation Manager", {
    setup: function() {
      this.mockControl = new MockControl();
      this.element = $('<div />').appendTo(document.body).hide();
    },
    teardown: function() {
      this.element.remove();
      this.mockControl.verify();
    }
  });
  test("is valid - field error", function() {
    
    
    var fakeEditor = this.mockControl.createMock(TableEditors.CommonEditor);
    var fakeEditorElement = $('<div/>').addClass('dynamics-editor-element')
                    .data("editor", fakeEditor)
                    .appendTo(this.element);
    
    fakeEditor.expects().runValidation();
    fakeEditor.expects().runValidation();
    
    var validationManager = new DynamicsValidationManager(this.element, { options: { validators: [] }});
    equals(validationManager.isValid(), true, "field validation works");
    
    var sender = "field";
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    this.element.trigger("validationInvalid", [dynEvent]);
    equals(validationManager.isValid(), false, "field validation works");
  });
  
  test("is valid - composite error", function() {
    var model = {};
    var validatorfunc = function(actual) {
      equals(actual, model);
      throw "Invalid composite";
      return false;
    };
    var options = {
      options: {
          validators: [validatorfunc]
      }
    };
    var validationManager = new DynamicsValidationManager(this.element, options, model);
    equals(validationManager.isValid(), false, "field validation works");

  });
  
  test("is valid -  ok", function() {
    var validationManager = new DynamicsValidationManager(this.element, null);
    var sender = "field";
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    this.element.trigger("validationInvalid", [dynEvent]);
  });
  
  test("clear", function() {
    var validationManager = new DynamicsValidationManager(this.element, null);
    
    var sender = "field";
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    this.element.trigger("validationInvalid", [dynEvent]);
    equals(this.element.find("ul").length, 1);
    equals(this.element.find("ul li").length, 1);
    validationManager.clear();
    equals(this.element.find("ul").length, 0);
  });
  
  test("Catch validationInvalid event", function() {
    var validationManager = new DynamicsValidationManager(this.element, null);
    
    var sender = "field";
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    var anotherDynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Data format", "invalid value"]);

    
    this.element.trigger("validationInvalid", [dynEvent]);
    
    equals(this.element.find("ul").length, 1);
    equals(this.element.find("ul li").length, 1);
    
    this.element.trigger("validationInvalid", [dynEvent]);
    this.element.trigger("validationInvalid", [dynEvent]);
    this.element.trigger("validationInvalid", [dynEvent]);
    
    equals(this.element.find("ul li").length, 1);

    this.element.trigger("validationInvalid", [anotherDynEvent]);

    equals(this.element.find("ul li").length, 2);


  });
  test("Catch validationValid event", function() {
    var validationManager = new DynamicsValidationManager(this.element, null);
    var sender = "field";
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    var validEvt = new DynamicsEvents.ValidationValid(sender);

    
    this.element.trigger("validationInvalid", [dynEvent]);
    
    equals(this.element.find("ul").length, 1);
    equals(this.element.find("ul li").length, 1);
    this.element.trigger("validationValid", [validEvt]);
    equals(this.element.find("ul").length, 0);

  });
  
  test("Catch store requested event", function() {
    var mockModel = this.mockControl.createMock(TaskModel);
    var mockController = this.mockControl.createMock(TaskController);
    var mockEditor = this.mockControl.createMock(TableEditors.CommonEditor);
    
    var closeCalled = false;
    var passCorrectCallback = true;
    var config = {
      options: {
        validators: []
      },
      _close: function() {
        equals(this, mockController, "Controller context matches");
        closeCalled = true;
      },
      getCloseRowCallback: function() {
        closeCalled = false;
        if (passCorrectCallback) {
          return config._close;
        }
        return;
      }
    };
    
    var validationManager = new DynamicsValidationManager(this.element, config, mockModel, mockController);
    
    
    // Close row callback supplied
    mockModel.expects().commit();
    mockEditor.expects().close();
    this.element.trigger("storeRequested", [ mockEditor ]);
    ok(closeCalled, "Close row callback called");

    // Close row callback not supplied
    mockModel.expects().commit();
    mockEditor.expects().close();
    closeCalled = false;
    passCorrectCallback = false;
    this.element.trigger("storeRequested", [ mockEditor ]);
    ok(!closeCalled, "Close row callback not called");
    
    // Invalid validation
    closeCalled = false;
    passCorrectCallback = false;
    validationManager.isValid = function() { return false; };
    this.element.trigger("storeRequested", [ mockEditor ]);
    ok(!closeCalled, "Close row callback not called");
  });
  
  
  test("Catch cancel requested event", function() {
    var mockModel = this.mockControl.createMock(TaskModel);
    var mockController = this.mockControl.createMock(TaskController);
    var mockEditor = this.mockControl.createMock(TableEditors.CommonEditor);
    
    var closeCalled = false;
    var passCorrectCallback = true;
    var config = {
      options: {
        validators: []
      },
      _close: function() {
        equals(this, mockController, "Controller context matches");
        closeCalled = true;
      },
      getCloseRowCallback: function() {
        closeCalled = false;
        if (passCorrectCallback) {
          return config._close;
        }
        return;
      }
    };
    
    var validationManager = new DynamicsValidationManager(this.element, config, mockModel, mockController);
    
    var errorsCleared = 0;
    validationManager.clear = function() {
      errorsCleared++;
    };
    
    // Close row callback supplied
    mockModel.expects().rollback();
    mockEditor.expects().close();
    this.element.trigger("cancelRequested", [ mockEditor ]);
    ok(closeCalled, "Close row callback called");
    equals(errorsCleared, 1, "Errors cleared");
    
    // Close row callback not supplied
    mockModel.expects().rollback();
    mockEditor.expects().close();
    closeCalled = false;
    passCorrectCallback = false;
    this.element.trigger("cancelRequested", [ mockEditor ]);
    ok(!closeCalled, "Close row callback not called");
    equals(errorsCleared, 2, "Errors cleared");
  });
  
  test("multiple composite runs", function() {
    var model = {};
    var isValid = false;
    var validatorfunc = function() {
      if(!isValid) {
        throw "Invalid composite";
      }
    };
    var options = {
      options: {
        validators: [validatorfunc]
      }
    };
    var validationManager = new DynamicsValidationManager(this.element, options, model);
    validationManager.isValid();
    validationManager.isValid();
    validationManager.isValid();
    equals(this.element.find("ul").length, 1);
    equals(this.element.find("ul li").length, 1);
    isValid = true;
    validationManager.isValid();
    equals(this.element.find("ul").length, 0);

  });
});