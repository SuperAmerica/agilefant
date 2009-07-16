$(document).ready(function() { 
	module("Dynamics: DynamicTable", {
		setup: function() {
			this.parent = $('<div />').appendTo(document.body);
			this.mockControl = new MockControl();
			this.model = this.mockControl.createMock(CommonModel);
			this.model.expects().addListener(TypeOf.isA(Function));
			this.model.expects().addListener(TypeOf.isA(Function));
			this.controller = this.mockControl.createMock(CommonController);
		}, teardown: function() {
			this.parent.remove();
			this.mockControl.verify();
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
		var columnConfigs = testable.config.getColumns();
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
		var columnConfigs = testable.config.getColumns();
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
		var columnConfigs = testable.config.getColumns();
		equals(columnConfigs[0].getWidth(), "97.8%");
		equals(columnConfigs[1].getWidth(), "49.1%");
		equals(columnConfigs[2].getWidth(), "49.1%");
	});
	
	test("Layout", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);

	});
	
	test("Render", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);

	});
	
	test("createRow", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var createRowCalled = false;
		testable._createRow = function(a,b,c,d) {
			createRowCalled = true;
			ok(a instanceof DynamicTableRow, "Correct argument type");
			equals(b, 5, "Controller passed");
			equals(c, 6, "Model passed");
			equals(d, "pos", "Position passed");
		};
		testable.createRow(5,6, "pos");
		ok(createRowCalled, "Delegate called");
	});
	
	test("_createRow to top", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var model = this.mockControl.createMock(CommonModel);
		var firstRow = this.mockControl.createMock(DynamicView);
		var secondRow = this.mockControl.createMock(DynamicView);
		firstRow.expects().init(null, model, testable);
		secondRow.expects().init(null, model, testable);
		testable._createRow(firstRow, null, model, "top");
		testable._createRow(secondRow, null, model, "top");
		equals(testable.upperRows.length, 2, "Correct number of rows");
		ok(testable.upperRows[0] === secondRow, "1. row ok");
		ok(testable.upperRows[1] === firstRow, "2. row ok");
	});
	test("_createRow new to data", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var model = new CommonModel(); 
	    model.getHashCode = function() { return "a-1"; };
	    
		var row = this.mockControl.createMock(DynamicView);
		row.expects().init(null, model, testable);
		testable._createRow(row, null, model);
		equals(testable.middleRows.length, 1, "Correct row count");
		ok(testable.middleRows[0] == row, "Correct row insterted");
	});
	
	test("_createRow duplicate to data", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var model = new CommonModel(); 
	    model.getHashCode = function() { return "a-1"; };
		var row = this.mockControl.createMock(DynamicView);
		row.expects().init(null, model, testable);
		testable._createRow(row, null, model);
		testable._createRow(row, null, model);
		testable._createRow(row, null, model);
		equals(testable.middleRows.length, 1, "Correct row count");
	});
	

	test("_createRow non model driven row to data", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var model = new CommonModel(); 
	    model.getHashCode = function() { return "a-1"; };
		var row = this.mockControl.createMock(DynamicView);
		var nonModel = {};
		row.expects().init(null, model, testable);
		row.expects().init(null, nonModel, testable);
		testable._createRow(row, null, model);
		testable._createRow(row, null, nonModel);
		equals(testable.middleRows.length, 2, "Correct row count");
	});
	test("_createRow to bottom", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var model = this.mockControl.createMock(CommonModel);
		var firstRow = this.mockControl.createMock(DynamicView);
		var secondRow = this.mockControl.createMock(DynamicView);
		firstRow.expects().init(null, model, testable);
		secondRow.expects().init(null, model, testable);
		testable._createRow(firstRow, null, model, "bottom");
		testable._createRow(secondRow, null, model, "bottom");
		equals(testable.bottomRows.length, 2, "Correct number of rows");
		ok(testable.bottomRows[0] === firstRow, "1. row ok");
		ok(testable.bottomRows[1] === secondRow, "2. row ok");
	});
	test("_dataSourceRow", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);

	});
});