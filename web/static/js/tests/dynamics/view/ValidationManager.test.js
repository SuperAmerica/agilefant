$(document).ready(function() {
  module("Validation Manager", {
    setup: function() {
      this.element = $('<div />').appendTo(document.body).hide();
    },
    teardown: function() {
      this.element.remove();
    }
  });
  test("Catch validationInvalid event", function() {
    var validationManager = new DynamicsValidationManager(this.element, null);
    
    var sender = {a:1};
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    var anotherDynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Data format", "invalid value"]);

    
    this.element.trigger("validationInvalid", [dynEvent]);
    
    equals(this.element.find("ul").length, 1);
    equals(this.element.find("ul li").length, 1);
    
    this.element.trigger("validationInvalid", [dynEvent]);
    
    equals(this.element.find("ul li").length, 1);

    this.element.trigger("validationInvalid", [anotherDynEvent]);

    equals(this.element.find("ul li").length, 2);


  });
  test("Catch validationValid event", function() {
    var validationManager = new DynamicsValidationManager(this.element, null);
    var sender = {a:1};
    var dynEvent = new DynamicsEvents.ValidationInvalid(sender, ["Invalid value"]);
    var validEvt = new DynamicsEvents.ValidationValid(sender);

    
    this.element.trigger("validationInvalid", [dynEvent]);
    
    equals(this.element.find("ul").length, 1);
    equals(this.element.find("ul li").length, 1);
    this.element.trigger("validationValid", [validEvt]);
    equals(this.element.find("ul").length, 0);

  });
});