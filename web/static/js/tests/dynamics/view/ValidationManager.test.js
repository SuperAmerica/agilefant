$(document).ready(function() {
  module("Validation Manager", {
    setup: function() {
      this.element = $('<div />').appendTo(document.body).hide();
    },
    teardown: function() {
      this.element.remove();
    }
  });
  test("is valid - field error", function() {
    var validationManager = new DynamicsValidationManager(this.element, { options: { validators: [] }});
    equals(validationManager.isValid(), true, "field validation works");
    var sender = "field";
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    this.element.trigger("validationInvalid", [dynEvent]);
    equals(validationManager.isValid(), false, "field validation works");
  });
  
  test("is valid - composite error", function() {
    var model = {};
    var validatorfunc = function() {
      equals(this, model);
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