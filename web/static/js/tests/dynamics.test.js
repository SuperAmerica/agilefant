$(document).ready(function() {
	module("Dynamics model");
	test("Iteration goal data test", function() {
	  var data = {
			  name: "Test",
			  id: 1,
			  description: "foofaa",
			  priority: 1
	  };
	  var ig = new iterationGoalModel(data);
	  same(ig.getName(), data.name, "Name ok" );
	  same(ig.getId(), data.id, "Id ok" );
	  same(ig.getPriority(), data.priority, "Priority ok" );
	  same(ig.getDescription(), data.description, "Description ok" );
	  
	});
	
	module("Dynamics utils");
	test("aftime to string", function() {
		var noData = "";
		var halfhour = 1800;
		var hour = 3600;
		
		same(agilefantUtils.aftimeToString(noData),"&mdash;","No data to be shown");
		same(agilefantUtils.aftimeToString(halfhour),"0.5h","30mins");
		same(agilefantUtils.aftimeToString(hour),"1.0h","one hour");
	});
	test("is aftime", function() {
		
	});
	test("aftime to string", function() {
		
	});
	
	module("Dynamic Table", {
		setup: function() {
			$("<div />").appendTo(document.body).attr("id","testTable").hide();
		},
		teardown: function() {
			$("#testTable").remove();
		}
	});
	
	test("insert table", function() { 
		var temp = $("#testTable");
		
		var table = temp.dynamicTable();
		
		ok(temp.data("dynamicTable") != undefined && table, "Table data set");
		ok(typeof(table.createRow) == "function", "Can create new row");
		
		var row = table.createRow();
		
		ok(row.getElement().parent().parent().get(0) == temp.get(0), "Row has been created within the table");
		ok(typeof(row.createCell) == "function", "Can create new cell");
	});
	
	test("One row one cell", function() {
		var table = $("#testTable").dynamicTable();
		var row = table.createRow();
		var cell = row.createCell();
		
		ok(cell.getElement().parent().get(0) == row.getElement().get(0), "Cell has correct parent");
	});
	
	test("Cell value callback", function() {
		var table = $("#testTable").dynamicTable();
		var row = table.createRow();
		var getter = function() {
			return 11;
		}
		var cell = row.createCell({get: getter});
		
		equals("11", cell.getElement().text(), "Cell has correct value");
	});
});