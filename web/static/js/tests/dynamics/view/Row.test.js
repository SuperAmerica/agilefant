$(document).ready(function() { 
	module("Dynamics: DynamicTableRow", {
		setup: function() {
			this.mockControl = new MockControl();
			var dynamicTable = function() {};
			dynamicTable.prototype = DynamicTable.prototype;
			this.tableMock = this.mockControl.createMock(dynamicTable);
			this.tmpParent = $('<div />').appendTo(document.body);
			this.oldCell = DynamicTableCell;
			this.mockableCell = function() {};
			this.mockableCell.prototype = DynamicTableCell.prototype;
	  }, teardown: function() {
			DynamicTableCell = this.oldCell;
			this.mockControl.verify();
			this.tmpParent.remove();
		}
	});
	
	test("create cell", function() {
		var conf = "conf";
		DynamicTableCell = function(conf,parent) {};
		DynamicTableCell.prototype.getElement = function() {
			return $('<div />');
		};
		var row = new DynamicTableRow(null);
		row.createCell(conf, null);
		equals(row.cells.length, 1, "Cell inserted");
		equals(row.element.children().length, 1, "DOM element inserted");
	});
	
	test("remove", function() {
		var row = new DynamicTableRow(null);
		row.getElement().appendTo(this.tmpParent);
		row.parentView = this.tableMock;
		this.tableMock.expects().removeRow(row);
		row.remove();
		equals(row.element.parent().length, 0, "Element removed");
	});
	
   test("remove with listeners", function() {
      var row = new DynamicTableRow(null);
      var mockModel = this.mockControl.createMock(CommonModel);
      mockModel.expects().addListener(TypeOf.isA(Function));
      this.tableMock.expects().removeRow(row);
      mockModel.expects().removeListener(TypeOf.isA(Function));
     
      row.init(null, mockModel, null);
      row.getElement().appendTo(this.tmpParent);
      row.parentView = this.tableMock;
      
      row.remove();
      equals(row.element.parent().length, 0, "Element removed");
    });
});