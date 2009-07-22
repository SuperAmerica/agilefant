$(document).ready(function() { 
	module("Dynamics: DynamicTable", {
		setup: function() {
			this.parent = $('<div />').appendTo(document.body);
			this.mockControl = new MockControl();
			this.model = this.mockControl.createMock(CommonModel);
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
	
	test("_renderHeaderColumn not sortable", function() {
		var config = this.mockControl.createMock(DynamicTableConfiguration);
		var colConfig = this.mockControl.createMock(DynamicTableColumnConfiguration);
		config.expects().getColumnConfiguration(1).andReturn(colConfig);
		colConfig.expects().getWidth().andReturn(null);
		colConfig.expects().isSortable().andReturn(false);
		colConfig.expects().getTitle().andReturn("ColTitle");
		colConfig.expects().getHeaderTooltip().andReturn("tooltipz");
		colConfig.expects().getHeaderTooltip().andReturn("tooltipz");
		
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var header = $('<div />');
		testable.config = config;
		testable.header = header;
		testable._renderHeaderColumn(1);
		same(header.children("div").children("span").text(), "ColTitle", "Title ok");
		same(header.children("div").attr("title"), "tooltipz", "Tooltip ok");
	});
	
	test("_renderHeaderColumn sortable", function() {
		var config = this.mockControl.createMock(DynamicTableConfiguration);
		var colConfig = this.mockControl.createMock(DynamicTableColumnConfiguration);
		config.expects().getColumnConfiguration(1).andReturn(colConfig);
		colConfig.expects().getWidth().andReturn("10px");
		colConfig.expects().getWidth().andReturn("10px");
		colConfig.expects().isSortable().andReturn(true);
		colConfig.expects().getTitle().andReturn("ColTitle");
		colConfig.expects().getHeaderTooltip().andReturn(null);
		
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var header = $('<div />');
		testable.config = config;
		testable.header = header;
		testable._renderHeaderColumn(1);
		var sortByColumnCalled = 0;
		testable._sortByColumn = function(index) {
			sortByColumnCalled++;
			equals(index, 1, "Correct column index passed");
		};
		same(header.children("div").children("a").text(), "ColTitle", "Title ok");
		header.children("div").children("a").click();
		equals(sortByColumnCalled, 1, "Sorter called");

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

	test("removeRow", function() {
		var testable = new DynamicTable(this.controller, this.model, null, this.parent);
		var model = this.mockControl.createMock(CommonModel);
		var row = this.mockControl.createMock(DynamicTableRow);
		row.expects().getModel().andReturn(model);
		model.expects().getHashCode().andReturn("hash");
		
		testable.rowHashes.push("hash");
		testable.middleRows.push(row);
		testable.upperRows.push(row);
		testable.bottomRows.push(row);
		
		testable.removeRow(row);
		equals(testable.middleRows.length, 0, "Row removed");
		equals(testable.rowHashes.length, 0, "Hash removed");
		testable.removeRow(row);
		equals(testable.bottomRows.length, 0, "Row removed");
		testable.removeRow(row);
		equals(testable.upperRows.length, 0, "Row removed");
	});
	
	test("_renderFromDataSource new rows", function() {
	  var existingRowModel = this.mockControl.createMock(CommonModel);
	  var existingRow = this.mockControl.createMock(DynamicTableRow);
	  var newRowModel = this.mockControl.createMock(CommonModel);
	  
	  existingRowModel.expects().getHashCode().andReturn("model-1");
	  newRowModel.expects().getHashCode().andReturn("model-2");  
	  existingRow.expects().getModel().andReturn(existingRowModel);
	  existingRowModel.expects().getHashCode().andReturn("model-1");
	  existingRowModel.expects().getHashCode().andReturn("model-1");
	  newRowModel.expects().getHashCode().andReturn("model-2");


	  var newDataset = [existingRowModel, newRowModel]; 
	  
	  var testable = new DynamicTable(this.controller, this.model, null, this.parent);
	  
	  var dataSourceRowCallCount = 0;
	  
	  testable._dataSourceRow = function(model, config) {
	     dataSourceRowCallCount++;
	     same(newRowModel, model, "Correct model added");
	  };
	  
	  testable.middleRows = [existingRow];
	  testable.rowHashes = ["model-1"];
	  
	  testable._renderFromDataSource(newDataset);
	  equals(dataSourceRowCallCount, 1, "Row added");
	  
	});
	
	test("_renderFromDataSource removed rows", function() {
	  var existingRow1Model = this.mockControl.createMock(CommonModel); 
	  var existingRow1 = this.mockControl.createMock(DynamicTableRow);
	  var existingRow2Model = this.mockControl.createMock(CommonModel);
	  var existingRow2 = this.mockControl.createMock(DynamicTableRow);
	  
	  existingRow1Model.expects().getHashCode().andReturn("model-1");
	  
	  existingRow1.expects().getModel().andReturn(existingRow1Model);
	  existingRow1Model.expects().getHashCode().andReturn("model-1");
	  existingRow2.expects().getModel().andReturn(existingRow2Model);
	  existingRow2Model.expects().getHashCode().andReturn("model-2");
	  existingRow2.expects().remove();
	  
	  existingRow1Model.expects().getHashCode().andReturn("model-1");
	  
	  var newDataSet = [existingRow1Model];
	  
	  var testable = new DynamicTable(this.controller, this.model, null, this.parent);

	  var dataSourceRowCallCount = 0;
    
    testable._dataSourceRow = function(model, config) {
       dataSourceRowCallCount++;
    };
    testable.middleRows = [existingRow1, existingRow2];
    testable.rowHashes = ["model-1", "model-2"];
    var newDataset = [existingRow1Model];
    
    testable._renderFromDataSource(newDataset);
    equals(dataSourceRowCallCount, 0, "no new rows added");
	});
});