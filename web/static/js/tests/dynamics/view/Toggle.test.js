$(document).ready(function() { 
	module("Dynamics: DynamicTableToggle", {
		setup: function() {
			this.parent = $('<div />').appendTo(document.body);
			this.mockControl = new MockControl();
			this.cell = this.mockControl.createMock(DynamicView);
			this.cell.expects().getElement().andReturn(this.parent);
			
		}, teardown: function() {
			this.mockControl.verify();
			this.parent.remove();
		}
	});
	
	test("create closed", function() {
	  var collapseCalled = 0;
	  var expandCalled = 0;
	  var options = {
	    collapse: function() {
	      collapseCalled++;
	    },
	    expand: function() {
	      expandCalled++;
	    }
	  };
	  var testable = new DynamicTableToggleView(options, this, this.cell);
	  equals(collapseCalled, 1, "Correct collapse call count");
	  equals(expandCalled, 0, "Correct expand call count");
	  ok(testable.button.hasClass("dynamictable-expand"), "Expandable");
	  testable.button.click();
	  ok(testable.button.hasClass("dynamictable-collapse"), "Collapsable");
	  equals(collapseCalled, 1, "Correct collapse call count");
	  equals(expandCalled, 1, "Correct expand call count");
	  testable.button.click();
	  ok(testable.button.hasClass("dynamictable-expand"), "Expandable");
	  equals(collapseCalled, 2, "Correct collapse call count");
    equals(expandCalled, 1, "Correct expand call count");
	});
	
	 test("create closed", function() {
	    var collapseCalled = 0;
	    var expandCalled = 0;
	    var options = {
	      collapse: function() {
	        collapseCalled++;
	      },
	      expand: function() {
	        expandCalled++;
	      },
	      expanded: true
	    };
	    var testable = new DynamicTableToggleView(options, this, this.cell);
	    equals(collapseCalled, 0, "Correct collapse call count");
	    equals(expandCalled, 1, "Correct expand call count");
	    ok(testable.button.hasClass("dynamictable-collapse"), "Collapsable");
	    testable.button.click();
	    ok(testable.button.hasClass("dynamictable-expand"), "Expandable");
	    equals(collapseCalled, 1, "Correct collapse call count");
	    equals(expandCalled, 1, "Correct expand call count");
	    testable.button.click();
	    ok(testable.button.hasClass("dynamictable-collapse"), "Collapsable");
	    equals(collapseCalled, 1, "Correct collapse call count");
	    equals(expandCalled, 2, "Correct expand call count");
	  });
});