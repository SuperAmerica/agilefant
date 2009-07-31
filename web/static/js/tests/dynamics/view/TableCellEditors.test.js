$(document).ready(function() {
  module("Dynamics: DynamicCellEditors", {
    setup : function() {
      this.mockControl = new MockControl();
      this.mockableCell = function() {
      };
      this.mockableCell.prototype = DynamicTableCell.prototype;
      this.cell = this.mockControl.createMock(this.mockableCell);
      this.elementMock = this.mockControl.createMock(jQuery);
    },
    teardown : function() {
      this.mockControl.verify();
    }
  });
  
  test("register events", function() {
    var testable = new TableEditors.CommonEditor();
    testable.cell = this.cell;
    testable.element = this.elementMock;
    testable.options = {};
    
    this.elementMock.expects().keypress(TypeOf.isA(Function));
    this.elementMock.expects().blur(TypeOf.isA(Function));
    testable._registerEvents();
    
    this.elementMock.expects().keypress(TypeOf.isA(Function));
    testable.options = {editRow: true};
    testable._registerEvents();
    ok(true, "Events registered"); //verify only mock calls
  });
  
  test("handle events", function() {
    var testObj = this.mockControl.createMock(TableEditors.CommonEditor);
    testObj.options = {};
    testObj.expects().close();
    testObj.expects().save();
    testObj.expects().saveRow();
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 27});
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 13});
    testObj.options = {editRow: true};
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 27});
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 13});
    ok(true, "Callbacks called"); //verify only mock calls
 });
  
  test("Text edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    
    var empty = "";
    var greater = "aaadddd";
    var less = "aa";
    var context = {element: mockElement};
    context.showError = function() {};
    
    mockElement.expects().val().andReturn(empty);
    mockElement.expects().val().andReturn(greater);
    mockElement.expects().val().andReturn(empty);
    mockElement.expects().val().andReturn(greater);
    mockElement.expects().val().andReturn(empty);
    mockElement.expects().val().andReturn(greater);
    mockElement.expects().val().andReturn(less);
    mockElement.expects().val().andReturn(empty);
    mockElement.expects().val().andReturn(greater);
    mockElement.expects().val().andReturn(less);
    
    context.options = {required: false, minlength: 0};

    ok(TableEditors.Text.prototype.isValid.call(context), "Empty string without validation");
    ok(TableEditors.Text.prototype.isValid.call(context), "String without validation");
    
    context.options = {required: true, minlength: 0};
    ok(!TableEditors.Text.prototype.isValid.call(context), "Empty string with required");
    ok(TableEditors.Text.prototype.isValid.call(context), "String with required");
    
    context.options = {required: false, minlength: 3};
    ok(!TableEditors.Text.prototype.isValid.call(context), "Empty string with min length");
    ok(TableEditors.Text.prototype.isValid.call(context), "OK string with min length");
    ok(!TableEditors.Text.prototype.isValid.call(context), "Too short string with min length");
    
    context.options = {required: true, minlength: 3};
    ok(!TableEditors.Text.prototype.isValid.call(context), "Empty string with min length and required");
    ok(TableEditors.Text.prototype.isValid.call(context), "OK string with min length and required");
    ok(!TableEditors.Text.prototype.isValid.call(context), "Too short string with min length and required");
    
  });
  
  test("Estimate edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var context = {element: mockElement};
    context.showError = function() {};
    
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn("10")
    mockElement.expects().val().andReturn("0");
    mockElement.expects().val().andReturn("0pt");
    mockElement.expects().val().andReturn("0points");
    mockElement.expects().val().andReturn("692pt");
    mockElement.expects().val().andReturn("aae");
    mockElement.expects().val().andReturn("xpt");
    
    
    context.options = {required: false};
    ok(TableEditors.Estimate.prototype.isValid.call(context), "Not required and empty");

    context.options = {required: true};
    ok(!TableEditors.Estimate.prototype.isValid.call(context), "Required and empty");
    ok(TableEditors.Estimate.prototype.isValid.call(context), "10");
    ok(TableEditors.Estimate.prototype.isValid.call(context), "0");
    ok(TableEditors.Estimate.prototype.isValid.call(context), "0pt");
    ok(TableEditors.Estimate.prototype.isValid.call(context), "0points");
    ok(TableEditors.Estimate.prototype.isValid.call(context), "692pt");
    ok(!TableEditors.Estimate.prototype.isValid.call(context), "invalid");
    ok(!TableEditors.Estimate.prototype.isValid.call(context), "invalid");
  });
  
  test("Date edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var context = {element: mockElement};
    context.showError = function() {};
    
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn("2001-1-1");
    mockElement.expects().val().andReturn("2001-01-01");
    mockElement.expects().val().andReturn("2001-10-10");
    mockElement.expects().val().andReturn("2001_10-01");
    mockElement.expects().val().andReturn("2001-10");
    mockElement.expects().val().andReturn("01-01-01");

    context.options = {required: false};
    ok(TableEditors.Date.prototype.isValid.call(context), "Not required and empty");

    context.options = {required: true};
    ok(!TableEditors.Date.prototype.isValid.call(context), "Required and empty");
    ok(!TableEditors.Date.prototype.isValid.call(context), "Required and empty");
    
    ok(TableEditors.Date.prototype.isValid.call(context), "1-1-2001");
    ok(TableEditors.Date.prototype.isValid.call(context), "01-01-2001");
    ok(TableEditors.Date.prototype.isValid.call(context), "10-10-2001");
    
    ok(!TableEditors.Date.prototype.isValid.call(context), "10_10-2001");
    ok(!TableEditors.Date.prototype.isValid.call(context), "-10-2001");
    ok(!TableEditors.Date.prototype.isValid.call(context), "10-10-01");
  });
  
  test("Date edit validation with time", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var context = {element: mockElement};
    context.showError = function() {};
    
    mockElement.expects().val().andReturn("2001-1-1 1:1");
    mockElement.expects().val().andReturn("2001-01-01 01:01");
    mockElement.expects().val().andReturn("2001-10-10 21:30");


    context.options = {withTime: true};
    ok(TableEditors.Date.prototype.isValid.call(context), "1-1-2001 1:1");
    ok(TableEditors.Date.prototype.isValid.call(context), "01-01-2001 01:01");
    ok(TableEditors.Date.prototype.isValid.call(context), "10-10-2001 21:30");

  });
  
  test("Exact estimate edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var context = {element: mockElement};
    context.showError = function() {};
    
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn("15min");
    mockElement.expects().val().andReturn("1h");
    mockElement.expects().val().andReturn("1h 15min");
    mockElement.expects().val().andReturn("1.5h");
    mockElement.expects().val().andReturn("1.5");
    mockElement.expects().val().andReturn("1.2h 10min");
    mockElement.expects().val().andReturn("10min 1h");
    mockElement.expects().val().andReturn("10x 15min");
    
    context.options = {required: false};
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "Not required and empty");
    context.options = {required: true};
    ok(!TableEditors.ExactEstimate.prototype.isValid.call(context), "Required and empty");

    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "15min");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "1h");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "1h 15min");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "1.5h");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "1.5");
    
    ok(!TableEditors.ExactEstimate.prototype.isValid.call(context), "1.2h 10min");
    ok(!TableEditors.ExactEstimate.prototype.isValid.call(context), "10mn 1h");
    ok(!TableEditors.ExactEstimate.prototype.isValid.call(context), "10x 15min");

  });
});
