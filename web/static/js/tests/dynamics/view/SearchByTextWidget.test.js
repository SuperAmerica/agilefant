$(document).ready(function() {
  module("SearchByText widget", {
    setup: function() {
      this.element = $('<div/>').appendTo(document.body);
    },
    teardown: function() {
      this.element.remove();
    }
  });
  
  
  
  test("Initialization", function() {
    var sbt = new SearchByTextWidget(this.element, {});
    
    same(1, this.element.children('.searchByText').size(),            "A child with the class 'searchByText' added");
    same(1, this.element.find('.searchByText > input').size(),        "The input element is added");
    same(1, this.element.find('.searchByText > .clearButton').size(), "The clear button element is added");
    same(0, this.element.find('.clearButton:visible').size(),         "The clear button is hidden");
  });
  
  
  
  test("Show clear button", function() {
    var sbt = new SearchByTextWidget(this.element, {});
    
    same(0, this.element.find('.clearButton:visible').size(), "The clear button is hidden");
    
    sbt.input.val('Foo');
    sbt.input.keyup();
    
    same(1, this.element.find('.clearButton:visible').size(), "The clear button is visible after typing text");
    
    sbt.input.val('');
    sbt.input.keyup();
    
    same(0, this.element.find('.clearButton:visible').size(), "The clear button is visible after clearing text");
  });
  
  
  
  test("Clear button event", function() {
    var sbt = new SearchByTextWidget(this.element, {});
    var input = sbt.input;
    
    input.val("Falleraa");
    
    same("Falleraa", input.val(), "Correct start value");
    sbt.clearButton.click();
    
    same("", input.val(), "Input is cleared");
  });
  
  
  
  test("Clear on esc key", function() {
    var sbt = new SearchByTextWidget(this.element, {});
    
    var keyEvent = jQuery.Event("keyup");
    keyEvent.keyCode = 27;
    
    sbt.input.val('Test value');
    sbt.input.trigger(keyEvent);
    
    same(sbt.input.val(), '', "The input is cleared");
  });
  
  
  
  test("Search callback", function() {
    var callbackCalled = false;
    var sbt = new SearchByTextWidget(this.element, {
      searchCallback: function(value) {
        callbackCalled = true;
        same(value, 'Test value', "The value is correct");
      }
    });
    
    sbt.input.val('Test value')
    var keyEvent = jQuery.Event("keyup");
    keyEvent.keyCode = 13;
    
    sbt.input.trigger(keyEvent);    
    
    ok(callbackCalled, "The search callback is called");
  });

});