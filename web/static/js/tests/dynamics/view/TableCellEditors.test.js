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
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 27});
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 13});
    testObj.options = {editRow: true};
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 27});
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, {keyCode: 13});
    ok(true, "Callbacks called"); //verify only mock calls
 });

});
