$(document).ready(function() { 
  module("Dynamics: DynamicTableCaption", {
    setup: function() {
      this.mockControl = new MockControl();
      this.parent = $('<div />').appendTo(document.body);
    }, teardown: function() {
      this.mockControl.verify();
      this.parent.remove();
    }
  });
  
  test("add caption item default attributes", function() {
    var conf = this.mockControl.createMock(DynamicTableCaptionItemConfiguration);
    conf.expects().getName().andReturn("item_name");
    conf.expects().getText().andReturn("item_text");
    conf.expects().getCssClass().andReturn(null);
    conf.expects().isVisible().andReturn(true);
    
    var config = {
        captionItems: [conf]
    };
    
    var testable = new DynamicTableCaption(this.parent, config, "", window);
    var clickCallCount = 0;
    testable._click = function() {
      clickCallCount++;
    };
    
    equals(this.parent.children("div").length, 1, "Caption text container ok");
    equals(this.parent.children("ul").length, 1, "Caption button container ok");
    
    equals(this.parent.children("ul").children("li").length, 1, "Caption button inserted");
    equals(this.parent.children("ul").children("li").text(), "item_text", "Caption button text ok");
    ok(testable.captionItems["item_name"], "Item inserted");
    testable.captionItems["item_name"].click();
    equals(clickCallCount, 1, "Click event ok");
    
  });
  
  test("add caption item", function() {
    var conf = this.mockControl.createMock(DynamicTableCaptionItemConfiguration);
    conf.expects().getName().andReturn("item_name");
    conf.expects().getText().andReturn("item_text");
    conf.expects().getCssClass().andReturn("myClass");
    conf.expects().getCssClass().andReturn("myClass");
    conf.expects().isVisible().andReturn(false);
    
    
    var testable = new DynamicTableCaption(this.parent, {captionItems:[conf]}, "", window);
    var clickCallCount = 0;
    
    
    equals(this.parent.children("ul").children("li").length, 1, "Caption button inserted");
    ok(this.parent.children("ul").children("li").is(":hidden"), "Caption button is hidden");
    ok(this.parent.children("ul").children("li").hasClass("myClass"), "Caption css class ok");

  });
  
  test("click item", function() {
    var conf = this.mockControl.createMock(DynamicTableCaptionItemConfiguration);
    conf.expects().getName().andReturn("item_name");
    conf.expects().getText().andReturn("item_text");
    conf.expects().getCssClass().andReturn(null);
    conf.expects().isVisible().andReturn(true);
    conf.expects().getName().andReturn("item_name");
    conf.expects().getConnected().andReturn(null);

    var callbackCalled = 0;
    conf.expects().getCallback().andReturn(function() {
      callbackCalled++;
    });
    
    var testable = new DynamicTableCaption(this.parent, {captionItems:[conf]}, "", window);
    
    this.parent.children("ul").children("li").click();
    equals(callbackCalled, 1, "Callback called once");
  });
  
  test("click item with connected item", function() {
    var conf = this.mockControl.createMock(DynamicTableCaptionItemConfiguration);
    conf.expects().getName().andReturn("item_name");
    conf.expects().getText().andReturn("item_text");
    conf.expects().getCssClass().andReturn(null);
    conf.expects().isVisible().andReturn(true);
    conf.expects().getName().andReturn("item_name");
    conf.expects().getConnected().andReturn("connected");
    conf.expects().getConnected().andReturn("connected");

    var callbackCalled = 0;
    conf.expects().getCallback().andReturn(function() {
      callbackCalled++;
    });
    
    var testable = new DynamicTableCaption(this.parent, {captionItems:[conf]}, "", window);
    var connectedToggleCalled = 0;
    testable.captionItems["connected"] = {
        toggle: function() { connectedToggleCalled++; }
    };
    ok(this.parent.children("ul").children("li").is(":visible"), "Item visible");
    this.parent.children("ul").children("li").click();
    equals(connectedToggleCalled, 1, "Connected item toggle called once");
    ok(this.parent.children("ul").children("li").is(":hidden"), "Item hidden");
  });
  
});