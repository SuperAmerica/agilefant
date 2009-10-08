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
  
  test("CommonEditor init", function() {
    var elem = $('<div/>').appendTo(document.body);
    
    var editorOpeningEvent = false;
    elem.bind('editorOpening', function() {
      editorOpeningEvent = true;
    });
    
    var editor = new TableEditors.CommonEditor();
    editor.init(elem, new CommonModel(), {});
    
    ok(editorOpeningEvent, "EditorOpening event fired");
    
    elem.remove();
  });
  
  test("CommonEditor close", function() {
    var elem = $('<div/>');
    
    var editorClosingEvent = false;
    elem.bind('editorClosing', function() {
      editorClosingEvent = true;
    });
    
    var editor = new TableEditors.TextFieldEditor();
    editor.init(elem, new CommonModel(), {});
    
    editor.close();
    
    ok(editorClosingEvent, "EditorClosing event fired");
  });
  
  test("CommonEditor on change", function() {
    var editor = new TableEditors.CommonEditor();
    
    var validateCalled = false;
    editor._validate = function() {
      validateCalled = true;
    };
    
    editor._onChange($('<div/>'), new CommonModel(), {});
    
    ok(validateCalled, "Validation called");
  });
  
  
  /*
   * TEXT FIELD EDITOR
   */
  
  
  test("TextFieldEditor close", function() {
    var elem = $('<div/>');
    
    var editorClosingEvent = false;
    elem.bind('editorClosing', function() {
      editorClosingEvent = true;
    });
    
    var editor = new TableEditors.TextFieldEditor();
    editor.init(elem, new CommonModel(), {});
    
    same(elem.find('input').length, 1, "Input element appended");
    
    editor.close();
    
    same(elem.find('input').length, 0, "Input element removed");
    ok(editorClosingEvent, "EditorClosing event fired");
  });
  
  test("TextFieldEditor init", function() {
    var originalCommon = TableEditors.CommonEditor.prototype.init;
    
    var initCalled = false;
    TableEditors.CommonEditor.prototype.init = function(element, model, options) {
      this.options = options;
      initCalled = true;
    };
    
    var editor = new TableEditors.TextFieldEditor();
    editor.init();
    ok(initCalled, "CommonEditor init called");
    ok(editor.textField, "Text field added");
    same(editor.textField.css('width'), editor.options.size, "Editor width set");
    
    TableEditors.CommonEditor.prototype.init = originalCommon;
  });

  
  test("TextFieldEditor - required validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var errorMessageCount = 0;
    var context = {textField: mockElement, addErrorMessage: function() { errorMessageCount++; }};
    
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("marmor");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("marmor");

    context.options = {required: false};
    ok(TableEditors.TextFieldEditor.prototype._validate.call(context), "Not required and empty");
    ok(TableEditors.TextFieldEditor.prototype._validate.call(context), "Not required and not empty");

    context.options = {required: true};
    ok(!TableEditors.TextFieldEditor.prototype._validate.call(context), "Required and empty");
    ok(TableEditors.TextFieldEditor.prototype._validate.call(context), "Required and not empty");
    
    same(errorMessageCount, 1, "One error message added");
  });
  

  
  /*
   * TEXT EDITOR
   */
  
  test("Text init", function() {
    var originalCommon = TableEditors.CommonEditor.prototype.init;
    
    var elem = $('<div/>');
    var expected = new CommonModel();
    var opts = {
      field: "value"  
    };
    var expectedOpts = {
      field: "value",
      size:  "95%",
      required: false,
    };
    
    var initCalled = false;
    TableEditors.CommonEditor.prototype.init = function(element, model, options) {
      this.options = options;
      same(element, elem, "Passed element matches");
      same(expected, model, "Passed model matches");
      same(options, expectedOpts, "Passed options match");
      this.options.get = function() {};
      initCalled = true;
    };
    
    var editor = new TableEditors.Text(elem, expected, opts);
    ok(initCalled, "CommonEditor init called");
    ok(editor.textField, "Text field added");
    
    TableEditors.CommonEditor.prototype.init = originalCommon;
  });
  
  
  /*
   * DATE EDITOR
   */
  
  
  test("Date edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery, "Mock element");
    var context = {textField: mockElement};
    context.options = { withTime: false };
    
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn("2001-1-1");
    mockElement.expects().val().andReturn("2001-1-1");
    mockElement.expects().val().andReturn("2001-01-01");
    mockElement.expects().val().andReturn("2001-01-01");
    mockElement.expects().val().andReturn("2001-10-10");
    mockElement.expects().val().andReturn("2001-10-10");
    mockElement.expects().val().andReturn("2001_10-01");
    mockElement.expects().val().andReturn("2001_10-01");
    mockElement.expects().val().andReturn("2001-10");
    mockElement.expects().val().andReturn("2001-10");
    mockElement.expects().val().andReturn("01-01-01");
    mockElement.expects().val().andReturn("01-01-01");

    ok(!TableEditors.Date.prototype._validate.call(context), "Empty invalid");
    ok(!TableEditors.Date.prototype._validate.call(context), "Whitespace invalid");
    
    ok(TableEditors.Date.prototype._validate.call(context), "2001-1-1 valid");
    ok(TableEditors.Date.prototype._validate.call(context), "2001-01-01 valid");
    ok(TableEditors.Date.prototype._validate.call(context), "2001-10-10 valid");
    
    ok(!TableEditors.Date.prototype._validate.call(context), "2001_10-01");
    ok(!TableEditors.Date.prototype._validate.call(context), "2001-10");
    ok(!TableEditors.Date.prototype._validate.call(context), "01-01-01");
  });
  
  test("Date edit validation with time", function() {
    var mockElement = this.mockControl.createMock(jQuery, "Mock element");
    var context = {textField: mockElement};
    
    mockElement.expects().val().andReturn("2001-01-01 01:01");
    mockElement.expects().val().andReturn("2001-01-01 01:01");
    mockElement.expects().val().andReturn("2001-10-10 21:30");
    mockElement.expects().val().andReturn("2001-10-10 21:30");
    
    mockElement.expects().val().andReturn("2001-1-1 1:1");
    mockElement.expects().val().andReturn("2001-1-1 1:1");
    mockElement.expects().val().andReturn("2001-10-10 xx:yy");
    mockElement.expects().val().andReturn("2001-10-10 xx:yy");

    context.options = {withTime: true, required: true};
    ok(TableEditors.Date.prototype._validate.call(context), "2001-01-01 01:01 valid");
    ok(TableEditors.Date.prototype._validate.call(context), "2001-10-10 21:30 valid");
    
    ok(!TableEditors.Date.prototype._validate.call(context), "2001-1-1 1:1 invalid");
    ok(!TableEditors.Date.prototype._validate.call(context), "2001-10-10 xx:yy invalid");
  });
  
  
  test("SelectionEditor - setEditorValue", function() {
    var parent = $('<div/>');
    var opts = {
      items: {
        "bar": "Value",
        "foo": "Bar",
        "fubar": "Fubar"
      },
      get: function() { return "foo"; }
    };
    var editor = new TableEditors.Selection(parent, new CommonModel(), opts);
    
    same(parent.find('option').length, 3, "Three children added");
    
    var selected = parent.find('option:selected');
    same(selected.length, 1, "Only one item selected");
    same(selected.val(), "foo", "'foo' selected");
    
    editor.setEditorValue("fubar");
    selected = parent.find('option:selected');
    same(selected.length, 1, "Only one item selected");
    same(selected.val(), "fubar", "'fubar' selected");
  });
  
  
  
  test("Email validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var errorMessageCount = 0;
    var context = {textField: mockElement, options: {}, addErrorMessage: function() { errorMessageCount++; }};
    
    // Invalid
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn("abc");
    mockElement.expects().val().andReturn("abc");
    mockElement.expects().val().andReturn("abc@abc");
    mockElement.expects().val().andReturn("abc@abc");
    mockElement.expects().val().andReturn("@abc.com");
    mockElement.expects().val().andReturn("@abc.com");
    mockElement.expects().val().andReturn("abc@.com");
    mockElement.expects().val().andReturn("abc@.com");
    
    // Valid
    mockElement.expects().val().andReturn("abc@abc.info");
    mockElement.expects().val().andReturn("abc@abc.info");
    
    ok(!TableEditors.Email.prototype._validate.call(context), "Not valid: ''");
    ok(!TableEditors.Email.prototype._validate.call(context), "Not valid: ' '");
    ok(!TableEditors.Email.prototype._validate.call(context), "Not valid: 'abc'");
    ok(!TableEditors.Email.prototype._validate.call(context), "Not valid: 'abc@abc'");
    ok(!TableEditors.Email.prototype._validate.call(context), "Not valid: '@abc.com'");
    ok(!TableEditors.Email.prototype._validate.call(context), "Not valid: 'abc@.com'");
    
    ok(TableEditors.Email.prototype._validate.call(context), "Valid: 'abc@abc.info'");
    
    same(errorMessageCount, 6, "Six error messages");
  });
  
  
  test("Number validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var errorMessageCount = 0;
    var context = {
      textField: mockElement,
      options: {},
      addErrorMessage: function() {
        errorMessageCount++;
      }
    };
    

    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("  ");
    mockElement.expects().val().andReturn("  ");
    mockElement.expects().val().andReturn("a");
    mockElement.expects().val().andReturn("a");
    
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("  ");
    mockElement.expects().val().andReturn("  ");
    
    mockElement.expects().val().andReturn("1");
    mockElement.expects().val().andReturn("1");
    mockElement.expects().val().andReturn("2");
    mockElement.expects().val().andReturn("2");
    mockElement.expects().val().andReturn("10");
    mockElement.expects().val().andReturn("10");
    
    context.options = { required: false };
    ok(TableEditors.Number.prototype._validate.call(context), "Valid (not required): ''");
    
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid (not required): '  '");
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid (not required): 'a'");
    
    context.options = { required: true };
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: ''");
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: '  '");
    
    context.options = { required: true, minValue: 2, maxValue: 7 };
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: '1'");
    ok(TableEditors.Number.prototype._validate.call(context), "Valid: '2'");
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: '10'");
    
    same(errorMessageCount, 6, "Six error messages");
  });
  
  /*
  test("register events", function() {
    var testable = new TableEditors.CommonEditor();
    testable.cell = this.cell;
    testable.element = this.elementMock;
    testable.options = {};
    
    this.elementMock.expects().keydown(TypeOf.isA(Function)).andReturn(this.elementMock);
    this.elementMock.expects().blur(TypeOf.isA(Function)).andReturn(this.elementMock);
    this.elementMock.expects().focus(TypeOf.isA(Function)).andReturn(this.elementMock);
//    this.elementMock.expects().blur(TypeOf.isA(Function)).andReturn(this.elementMock);
    testable._registerEvents();
    
    this.elementMock.expects().keydown(TypeOf.isA(Function));
    this.elementMock.expects().blur(TypeOf.isA(Function)).andReturn(this.elementMock);
    this.elementMock.expects().focus(TypeOf.isA(Function)).andReturn(this.elementMock);
    testable.options = {editRow: true};
    testable._registerEvents();
    ok(true, "Events registered"); //verify only mock calls
  });
  
  test("handle events", function() {
    var testObj = this.mockControl.createMock(TableEditors.CommonEditor);
    testObj.options = {};
    //mocking dom event
    var mockEventClass = function() {};
    mockEventClass.prototype = {
      stopPropagation: function() {},
      preventDefault: function() {}
    };
    var mockEvent = this.mockControl.createMock(mockEventClass);
    mockEvent.expects().stopPropagation();
    mockEvent.expects().preventDefault();
    testObj.expects().close();
    
    mockEvent.expects().stopPropagation();
    mockEvent.expects().preventDefault();
    testObj.expects().save();
    
    mockEvent.expects().stopPropagation();
    mockEvent.expects().preventDefault();
    testObj.expects().saveRow();
    
    //single field esc, close edit
    mockEvent.keyCode = 27;
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, mockEvent);
    //single field enter, save field
    mockEvent.keyCode = 13;
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, mockEvent);
    
    //whole row esc, nothing should happen
    testObj.options = {editRow: true};
    mockEvent.keyCode = 27;
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, mockEvent);
    //whole row enter, should save row
    mockEvent.keyCode = 13;
    TableEditors.CommonEditor.prototype._handleKeyEvent.call(testObj, mockEvent);
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
  */

  /*
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
    
    mockElement.expects().val().andReturn("-15min");
    mockElement.expects().val().andReturn("-1h");
    mockElement.expects().val().andReturn("-1h 15min");
    mockElement.expects().val().andReturn("-1.5h");
    mockElement.expects().val().andReturn("-1.5");
    
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
    
    context.options.acceptNegative = true;
    
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "-15min");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "-1h");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "-1h 15min");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "-1.5h");
    ok(TableEditors.ExactEstimate.prototype.isValid.call(context), "-1.5");
    

  });
  
  

  */
});
