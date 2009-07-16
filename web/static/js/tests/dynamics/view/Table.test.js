$(document).ready(function() { 
	module("Dynamics: DynamicTable", {
		setup: function() {
			this.parent = $('<div />').appendTo(document.body);
		}, teardown: function() {
			this.parent.remove();
		}
	});
	
	test("init", function() {
		var testable = new DynamicTable();
		var parent = $('<div />');
		testable.getParentElement = function() { return parent; };
		var computeColsCallCount = 0;
		testable._computeColumns = function() { computeColsCallCount++; };
		testable.init();
		equals(testable.container.parent()[0], parent[0], "Container inserted");
		equals(testable.element.parent()[0], testable.container[0], "Table container set");
		equals(computeColsCallCount, 1, "Column data computed");
	});
	
	test("Layout", function() {
		
	});
	
	test("Render", function() {
		
	});
	
	test("createRow", function() {
		
	});
});