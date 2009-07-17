$(document).ready(function() { 
	module("Dynamics: DynamicTableRow");
	
	test("create cell", function() {
		var conf = "conf";
		var row = new DynamicTableRow(null);
		row.init();
		row.createCell(conf);
		equals(row.cells.length, 1, "Cell inserted");
		equals(row.element.children().length, 1, "DOM element inserted");
	});
	
	test("auto create cells", function() {
		
	});
});