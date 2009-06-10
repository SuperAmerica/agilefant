$(document).ready(function() {
	module("Dynamics model");
	test("Story (iteration) data test", function() {
	  var data = {
			  name: "Test",
			  id: 1,
			  description: "foofaa",
			  priority: 1
	  };
	  var ig = new StoryModel(data);
	  same(ig.getName(), data.name, "Name ok" );
	  same(ig.getId(), data.id, "Id ok" );
	  same(ig.getPriority(), data.priority, "Priority ok" );
	  same(ig.getDescription(), data.description, "Description ok" );
	  
	});
	
	module("Dynamics utils");
	test("hour entry to string", function() {
		var noData = "";
		var halfhour = 1800;
		var hour = 3600;
		
		same(agilefantParsers.hourEntryToString(noData),"&mdash;","No data to be shown");
		same(agilefantParsers.hourEntryToString(halfhour),"0.5h","30min");
		same(agilefantParsers.hourEntryToString(null),"&mdash;","null");
		same(agilefantParsers.hourEntryToString(""),"&mdash;","empty");
		same(agilefantParsers.hourEntryToString(NaN),"&mdash;","NaN");
		same(agilefantParsers.hourEntryToString(null, true),"0.0h","null");
		same(agilefantParsers.hourEntryToString("", true),"0.0h","empty");
		same(agilefantParsers.hourEntryToString(NaN, true),"","NaN");
	});
	
	test("is hour entry", function() {
		ok(agilefantParsers.isHourEntryString("1h"), "1h");
		ok(agilefantParsers.isHourEntryString("1,5h"), "1,5h");
		ok(agilefantParsers.isHourEntryString("1.5h"), "1.5h");
		ok(agilefantParsers.isHourEntryString("1,5"), "1,5");
		ok(agilefantParsers.isHourEntryString("1.5"), "1.5");
		ok(agilefantParsers.isHourEntryString("1"), "1");
		ok(agilefantParsers.isHourEntryString("1h 10min"), "1h 10min");
		ok(agilefantParsers.isHourEntryString("10min"), "10min");
		ok(!agilefantParsers.isHourEntryString("1,5h 10min"), "1,5h 10min");
		ok(!agilefantParsers.isHourEntryString("10min 1h"), "10min 1h");
		ok(!agilefantParsers.isHourEntryString("daadaa 1h"), "daadaa 1h");
		ok(!agilefantParsers.isHourEntryString("1h daadaa"), "1h daadaa");
		ok(!agilefantParsers.isHourEntryString("1h d 10min"), "1h d 10min");
	});
	/*
	test("aftime to millis", function() {
		equals(agilefantUtils.aftimeToMillis("1h"), 3600,"1h");
		equals(agilefantUtils.aftimeToMillis("1,5h"), 5400 ,"1,5h");
		equals(agilefantUtils.aftimeToMillis("1.5h"), 5400 ,"1.5h");
		equals(agilefantUtils.aftimeToMillis("1,5"),  5400, "1,5");
		equals(agilefantUtils.aftimeToMillis("1.5"),  5400,"1.5");
		equals(agilefantUtils.aftimeToMillis("1"), 3600 ,"1");
		equals(agilefantUtils.aftimeToMillis("1h 10min"), 4200 ,"1h 10min");
		equals(agilefantUtils.aftimeToMillis("10min"), 600 ,"10min");
		equals(agilefantUtils.aftimeToMillis("  "),0,"empty");
		equals(agilefantUtils.aftimeToMillis(""),0,"empty");
		equals(agilefantUtils.aftimeToMillis("0"),0,"0");
	});*/

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
		
		var table = temp.genericTable();
		
		ok(temp.data("DynamicTable") != undefined && table, "Table data set");
		ok(typeof(table.createRow) == "function", "Can create new row");	
		var row = table.createRow();
		
		ok(row.getElement().parent().parent().parent().get(0) == temp.get(0), "Row has been created within the table");
		ok(typeof(row.createCell) == "function", "Can create new cell");
	});
	
	test("One row one cell", function() {
		var table = $("#testTable").DynamicTable();
		var row = table.createRow();
		var cell = row.createCell();
		
		ok(cell.getElement().parent().get(0) == row.getElement().get(0), "Cell has correct parent");
	});
	
	test("Cell value callback", function() {
		var table = $("#testTable").DynamicTable();
		var row = table.createRow();
		var getter = function() {
			return 11;
		}
		var cell = row.createCell({get: getter});
		table.render();
		
		equals("11", cell.getElement().text(), "Cell has correct value");
	});
	
	test("Test column width calculation", function() {
	  var table = $('#testTable').DynamicTable();
	  var widths = table.calculateColumnWidths([
	                                             {
	                                               minwidth: 39.6,
	                                               auto: true
	                                             },
	                                             {
                                                 minwidth: 39.6,
                                                 auto: true
                                               },
	                                             {
                                                 minwidth: 18.6,
                                                 auto: true
                                               }
	                                             ]);
	  same(widths, [39.6, 39.6, 18.6], "Column widths calculated correctly");
	  widths = table.calculateColumnWidths([
                                              {
                                                minwidth: 39.6,
                                                auto: true
                                              },
                                              {
                                                minwidth: 39.6,
                                                auto: true
                                              },
                                              {
                                                minwidth: 18.6,
                                                auto: true
                                              },
                                              {
                                                setMaxWidth: true,
                                                auto: false
                                              }
                                              ]);
   same(widths, [39.6, 39.6, 18.6, 98.6], "Column widths calculated correctly");
	});
});