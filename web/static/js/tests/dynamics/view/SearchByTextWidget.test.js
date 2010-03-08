$(document).ready(function() {
  module("SearchByText widget", {
    setup: function() {
      this.element = $('<div/>').appendTo(document.body);
      this.mockControl = new MockControl();
    },
    teardown: function() {
      this.mockControl.verify();
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

});