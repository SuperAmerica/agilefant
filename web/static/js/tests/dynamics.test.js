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
	
	module("Dynamic Table");
	
	test("insert table", function() { 
		var temp = $("<div />").appendTo(document.body).hide();
		
		temp.dynamicTable();
		
		ok(temp.data("dynamicTable") != undefined, "Table data set");
		ok(typeof(temp.createRow) == "function", "Can create new row");
		
		var row = temp.createRow();
		
		ok(row.parent().parent().get(0) == temp.get(0), "Row has been created within the table");
		ok(typeof(row.createCell) == "function", "Can create new cell");
		
		temp.remove();
	});
});