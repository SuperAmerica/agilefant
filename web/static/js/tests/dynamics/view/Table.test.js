$(document).ready(function() { 
	module("Dynamics: DynamicTable", {
		setup: function() {
			this.parent = $('<div />').appendTo(document.body);
			this.mockControl = new MockControl();
			this.model = this.mockControl.createMock(CommonModel);
			this.controller = this.mockControl.createMock(CommonController);
		}, teardown: function() {
			this.parent.remove();
		}
	});
	
	test("init", function() {
		var parent = $('<div />');
		var testable = new DynamicTable(this.controller, this.model, null, parent);
		var computeColsCallCount = 0;
		testable.getParentElement = function() { return parent; };
		testable._computeColumns = function() { computeColsCallCount++; };
		testable.initialize();

		equals(testable.container.parent()[0], parent[0], "Container inserted");
		equals(testable.element.parent()[0], testable.container[0], "Table container set");
		equals(computeColsCallCount, 1, "Column data computed");
	});
	
	test("Compute columns ", function() {
		var config = new DynamicTableConfiguration();
		config.addColumnConfiguration(0, {
			minWidth: 40,
			autoScale: true
		});
		config.addColumnConfiguration(1, {
			minWidth: 40,
			autoScale: true
		});
		config.addColumnConfiguration(2, {
			minWidth: 40,
			autoScale: true
		});
		var testable = new DynamicTable(this.controller, this.model, config, this.parent);
		testable._computeColumns();
		var columnConfigs = testable.tableConfiguration.getColumns();
		equals(columnConfigs[0].getWidth(), "32.6%");
		equals(columnConfigs[1].getWidth(), "32.6%");
		equals(columnConfigs[2].getWidth(), "32.6%");
	});
	
	test("Compute columns ", function() {
		var config = new DynamicTableConfiguration();
		config.addColumnConfiguration(0, {
			autoScale: false
		});
		config.addColumnConfiguration(1, {
			minWidth: 40,
			autoScale: true
		});
		config.addColumnConfiguration(2, {
			minWidth: 40,
			autoScale: true
		});
		var testable = new DynamicTable(this.controller, this.model, config, this.parent);
		testable._computeColumns();
		var columnConfigs = testable.tableConfiguration.getColumns();
		equals(columnConfigs[1].getWidth(), "49.1%");
		equals(columnConfigs[2].getWidth(), "49.1%");
	});
	
	test("Compute columns ", function() {
		var config = new DynamicTableConfiguration();
		config.addColumnConfiguration(0, {
			fullWidth: true
		});
		config.addColumnConfiguration(1, {
			minWidth: 40,
			autoScale: true
		});
		config.addColumnConfiguration(2, {
			minWidth: 40,
			autoScale: true
		});
		var testable = new DynamicTable(this.controller, this.model, config, this.parent);
		testable._computeColumns();
		var columnConfigs = testable.tableConfiguration.getColumns();
		equals(columnConfigs[1].getWidth(), "97.8%");
		equals(columnConfigs[1].getWidth(), "49.1%");
		equals(columnConfigs[2].getWidth(), "49.1%");
	});
	
	test("Layout", function() {
		
	});
	
	test("Render", function() {
		
	});
	
	test("createRow", function() {
		
	});
});