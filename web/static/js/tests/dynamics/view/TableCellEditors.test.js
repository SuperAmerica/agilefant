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
    
  });
  
  test("Date edit validation", function() {
    
  });
  
  test("Exact estimate edit validation", function() {
    
  });
});
