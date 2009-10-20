$(document).ready(function() {
  function testBlurAndFocus(editor, element, editorElement) {
    var blurCaptured = 0;
    var focusCaptured = 0;
    element.bind("DynamicsFocus", function() {
      focusCaptured++;
    });
    element.bind("DynamicsBlur", function() {
      blurCaptured++;
    });
    editorElement.focus();
    equals(focusCaptured, 1, "DynamicsFocus fired");
    ok(editor.isFocused(), "Editor remembers focused state");
    
    editorElement.blur();
    equals(blurCaptured, 1, "Dynamics blur fired");
    ok(!editor.isFocused(), "Editor remembers focus state");
    
    ok(editorElement.hasClass('dynamics-editor-element'), "Editor has class: dynamics-editor-element");
  }
  
  module("Dynamics: DynamicCellEditors", {
    setup : function() {
      this.mockControl = new MockControl();
      this.mockableCell = function() {
      };
      this.mockableCell.prototype = DynamicTableCell.prototype;
      this.cell = this.mockControl.createMock(this.mockableCell);
      this.element = $('<div />').appendTo(document.body);
    },
    teardown : function() {
      this.mockControl.verify();
      this.element.remove();
    }
  });
  
  
  test("Open on row edit test", function() {
    // Normal get function
    ok(!TableEditors.openOnRowEdit("Autocomplete"), "Autocomplete editor should not open");
    ok(TableEditors.openOnRowEdit("Text"), "Text editor should not open");
    
    var originalGetter = TableEditors.getEditorClassByName;
    
    // Extended dialog
    var ExtendedDialogEditor = function() {};
    ExtendedDialogEditor.prototype = new TableEditors.DialogEditor();
    
    // Extended text field
    var ExtendedTextEditor = function() {};
    ExtendedTextEditor.prototype = new TableEditors.TextFieldEditor();
    
    TableEditors.getEditorClassByName = function(name) {
      if (name === "Dialog") {
        return ExtendedDialogEditor;
      }
      return ExtendedTextEditor;
    };
    
    // Other editors
    ok(!TableEditors.openOnRowEdit("Dialog"), "Dialog editor should not open");
    ok(TableEditors.openOnRowEdit("TextField"), "Text field editor should not open");
    
    TableEditors.getEditorClassByName = originalGetter;
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
  
  test("CommonEditor run validation", function() {
    var editor = new TableEditors.CommonEditor();
    var setterCallCount = 0;
    editor.options = {
        fieldName: "Foo",
        set: function() {
          setterCallCount++;
        }
    };
    
    editor.element = this.element; 
    
    var invalidCount = 0;
    this.element.bind("validationInvalid", function(event, dynamicsEvent) {
      ok(dynamicsEvent instanceof DynamicsEvents.ValidationInvalid, "Event type correct");
      invalidCount++;
    });
    var validCount = 0;
    this.element.bind("validationValid", function(event, dynamicsEvent) {
      ok(dynamicsEvent instanceof DynamicsEvents.ValidationValid, "Event type correct");
      validCount++;
    });
    
    var valid = true;
    var validateCallCount = 0;
    editor._validate = function() {
      validateCallCount++;
      return valid;
    };
    
    valid = true;
    editor.runValidation();
    equals(setterCallCount, 1, "Setter function called");
    
    valid = false;
    editor.runValidation();
    equals(setterCallCount, 1, "Setter function not called");
    
    equals(validateCallCount, 2, "Validation called");
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
      fieldType: "text"
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
  
  test("TextEditor - events", function() {
    var editor = new TableEditors.Text(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  /*
   * DATE EDITOR
   */
  
  
  test("Date edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery, "Mock element");
    var context = { textField: mockElement, addErrorMessage: function() {} };
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
    var context = {textField: mockElement, addErrorMessage: function() {}};
    
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
  
  test("Date - events", function() {
    var editor = new TableEditors.Date(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  
  test("Password edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery, "Mock element");
    var context = {textField: mockElement, addErrorMessage: function() {}};
    
    context.options = { required: true };
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("pass1");

    ok(!TableEditors.Password.prototype._validate.call(context), "'' invalid");
    ok(TableEditors.Password.prototype._validate.call(context), "'pass1' valid");
  });
  
 
  test("Password - events", function() {
    var editor = new TableEditors.Password(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  /*
   * SELECTION EDITOR
   */
  
  
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
  
  test("Selection - events", function() {
    var editor = new TableEditors.Selection(this.element, null, {});
    var input = this.element.find("select");
    testBlurAndFocus(editor, this.element, input);
    
    var saveCallCount = 0;
    this.element.bind("saveRequested", function() {
      saveCallCount++;
    });
    
    input.change();
       
    setTimeout(function() {
      equals(saveCallCount, 1, "SaveRequested event fired once");
    }, 1);
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
  test("Email - events", function() {
    var editor = new TableEditors.Email(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  test("Number validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var errorMessageCount = 0;
    var context = {
      textField: mockElement,
      options: {},
      addErrorMessage: function(message) {
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
    ok(TableEditors.Number.prototype._validate.call(context), "Valid (not required): '  '");
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid (not required): 'a'");
    
    context.options = { required: true };
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: ''");
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: '  '");
    
    context.options = { required: true, minValue: 2, maxValue: 7 };
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: '1'");
    ok(TableEditors.Number.prototype._validate.call(context), "Valid: '2'");
    ok(!TableEditors.Number.prototype._validate.call(context), "Invalid: '10'");
    
    same(errorMessageCount, 5, "Five error messages");
  });
  test("Number - events", function() {
    var editor = new TableEditors.Number(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  test("Exact estimate edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var context = {textField: mockElement, addErrorMessage: function() {} };
    context.showError = function() {};
    
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("");
    mockElement.expects().val().andReturn("15min");
    mockElement.expects().val().andReturn("15min");
    mockElement.expects().val().andReturn("1h");
    mockElement.expects().val().andReturn("1h");
    mockElement.expects().val().andReturn("1h 15min");
    mockElement.expects().val().andReturn("1h 15min");
    mockElement.expects().val().andReturn("1.5h");
    mockElement.expects().val().andReturn("1.5h");
    mockElement.expects().val().andReturn("1.5");
    mockElement.expects().val().andReturn("1.5");
    mockElement.expects().val().andReturn("1.2h 10min");
    mockElement.expects().val().andReturn("1.2h 10min");
    mockElement.expects().val().andReturn("10min 1h");
    mockElement.expects().val().andReturn("10min 1h");
    mockElement.expects().val().andReturn("10x 15min");
    mockElement.expects().val().andReturn("10x 15min");
    mockElement.expects().val().andReturn("-15min");
    mockElement.expects().val().andReturn("-15min");
    
    mockElement.expects().val().andReturn("-15min");
    mockElement.expects().val().andReturn("-15min");
    mockElement.expects().val().andReturn("-1h");
    mockElement.expects().val().andReturn("-1h");
    mockElement.expects().val().andReturn("-1h 15min");
    mockElement.expects().val().andReturn("-1h 15min");
    mockElement.expects().val().andReturn("-1.5h");
    mockElement.expects().val().andReturn("-1.5h");
    mockElement.expects().val().andReturn("-1.5");
    mockElement.expects().val().andReturn("-1.5");
    mockElement.expects().val().andReturn("-");
    mockElement.expects().val().andReturn("-");
    
    context.options = {required: false};
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "Not required and empty");
    context.options = {required: true};
    ok(!TableEditors.ExactEstimate.prototype._validate.call(context), "Required and empty");

    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "15min");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "1h");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "1h 15min");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "1.5h");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "1.5");
    
    ok(!TableEditors.ExactEstimate.prototype._validate.call(context), "1.2h 10min");
    ok(!TableEditors.ExactEstimate.prototype._validate.call(context), "10mn 1h");
    ok(!TableEditors.ExactEstimate.prototype._validate.call(context), "10x 15min");
    
    ok(!TableEditors.ExactEstimate.prototype._validate.call(context), "-15min (no negatives)");
    
    context.options.acceptNegative = true;
    
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "-15min");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "-1h");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "-1h 15min");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "-1.5h");
    ok(TableEditors.ExactEstimate.prototype._validate.call(context), "-1.5");
    
    ok(!TableEditors.ExactEstimate.prototype._validate.call(context), "-");
  });
  
  test("ExactEstimate - events", function() {
    var editor = new TableEditors.ExactEstimate(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
//  test("Estimate validation", function() {
//    var mockElement = this.mockControl.createMock(jQuery);
//    var errorMessageCount = 0;
//    var context = {
//      textField: mockElement,
//      options: {},
//      addErrorMessage: function() {
//        errorMessageCount++;
//      }
//    };
//    
//
//    mockElement.expects().val().andReturn("");
//    mockElement.expects().val().andReturn("");
//    mockElement.expects().val().andReturn("  ");
//    mockElement.expects().val().andReturn("  ");
//    mockElement.expects().val().andReturn("a");
//    mockElement.expects().val().andReturn("a");
//    
//    mockElement.expects().val().andReturn("");
//    mockElement.expects().val().andReturn("");
//    mockElement.expects().val().andReturn("  ");
//    mockElement.expects().val().andReturn("  ");
//    
//    context.options = { required: false };
//    ok(TableEditors.Estimate.prototype._validate.call(context), "Valid (not required): ''");
//    
//    ok(!TableEditors.Estimate.prototype._validate.call(context), "Invalid (not required): '  '");
//    ok(!TableEditors.Estimate.prototype._validate.call(context), "Invalid (not required): 'a'");
//    
//    context.options = { required: true };
//    ok(!TableEditors.Estimate.prototype._validate.call(context), "Invalid: ''");
//    ok(!TableEditors.Estimate.prototype._validate.call(context), "Invalid: '  '");
//        
//    same(errorMessageCount, 4, "Six error messages");
//  });
  
  
  test("Estimate edit validation", function() {
    var mockElement = this.mockControl.createMock(jQuery);
    var context = {textField: mockElement, addErrorMessage: function() {}};
    context.showError = function() {};
    
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn(" ");
    mockElement.expects().val().andReturn("10")
    mockElement.expects().val().andReturn("10")
    mockElement.expects().val().andReturn("0");
    mockElement.expects().val().andReturn("0");
    mockElement.expects().val().andReturn("0pt");
    mockElement.expects().val().andReturn("0pt");
    mockElement.expects().val().andReturn("0points");
    mockElement.expects().val().andReturn("0points");
    mockElement.expects().val().andReturn("692pt");
    mockElement.expects().val().andReturn("692pt");
    mockElement.expects().val().andReturn("aae");
    mockElement.expects().val().andReturn("aae");
    mockElement.expects().val().andReturn("xpt");
    mockElement.expects().val().andReturn("xpt");
    
    context.options = {required: false};
    ok(TableEditors.Estimate.prototype._validate.call(context), "Not required and empty");

    context.options = {required: true};
    ok(!TableEditors.Estimate.prototype._validate.call(context), "Required and empty");
    ok(TableEditors.Estimate.prototype._validate.call(context), "10");
    ok(TableEditors.Estimate.prototype._validate.call(context), "0");
    ok(TableEditors.Estimate.prototype._validate.call(context), "0pt");
    ok(TableEditors.Estimate.prototype._validate.call(context), "0points");
    ok(TableEditors.Estimate.prototype._validate.call(context), "692pt");
    ok(!TableEditors.Estimate.prototype._validate.call(context), "invalid");
    ok(!TableEditors.Estimate.prototype._validate.call(context), "invalid");
  });
  
  test("Estimate - events", function() {
    var editor = new TableEditors.Number(this.element, null, {});
    var input = this.element.find("input");
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  /**
   * WYSIWYG EDITOR
   */
  
  test("Wysiwyg - events", function() {
    var editor = new TableEditors.Wysiwyg(this.element, null, {});
    var input = $(this.element.find("iframe")[0].contentWindow);
    testBlurAndFocus(editor, this.element, input);
  });
  
  
  
  
  /*
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
  */

  


  
  /*
   * For general testing purposes
   */
  var ExtendedDialog = function(element, model, options) {
    this.init(element, model, options);
  };
  ExtendedDialog.prototype = new TableEditors.DialogEditor();

  test("DialogEditor: Initialize and close a dialog editor - autoShow true", function() {
    var element = $('<div/>');
    
    var dialogEditor = new ExtendedDialog(element, new CommonModel(), { autoShow: true });
    var actualDialog = $(document.body).find('div.ui-dialog-content').get(0);
    equals(dialogEditor.dialog.get(0), actualDialog, "Dialog element matches");
    
    dialogEditor._closeDialog();
    actualDialog = $(document.body).find('div.ui-dialog-content').get(0);
    ok(!dialogEditor.dialog, "Dialog removed from editor");
    ok(!actualDialog, "Dialog removed from DOM");
  });
  
  test("DialogEditor: initialize - autoshow false", function() {
    var element = $('<div/>');
    
    var dialogEditor = new ExtendedDialog(element, new CommonModel(), { autoShow: false});
    
    // Dialog should not exist
    var actualDialog = $(document.body).find('div.ui-dialog-content').get(0);
    ok(!actualDialog, "Dialog not created");
    
    // Dialog should exist
    dialogEditor._openDialog();
    actualDialog = $(document.body).find('div.ui-dialog-content').get(0);
    equals(dialogEditor.dialog.get(0), actualDialog, "Dialog element matches");
    
    dialogEditor._closeDialog();
    actualDialog = $(document.body).find('div.ui-dialog-content').get(0);
    ok(!dialogEditor.dialog, "Dialog removed from editor");
    ok(!actualDialog, "Dialog removed from DOM");
  });
  
  test("DialogEditor - events", function() {
    
  });
  
  
});
