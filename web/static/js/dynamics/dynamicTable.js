(function($) {
	var cssClasses = {
		tableRow: "dynamictable-row",
		tableCell: "dynamictable-cell",
		table: "dynamictable"
	};
	/** TABLE **/
	var dynamicTable = function(element, options) {
    this.options = {};
    $.extend(this.options,options);
		this.element = element;
		this.rows = [];
		this.container = $("<div />").appendTo(this.element).addClass(cssClasses.table);
		this.table = $("<div />").appendTo(this.container).hide();
		this.headerRow = null;
		//to resolve border overflow etc. issues
		$("<div />").appendTo(this.container).css("clear","both").width("100%").height("1px").css("margin","0").css("padding","0");
	};
	
	dynamicTable.prototype = {
			createRow: function(model) {
				var newRow = new dynamicTableRow(this, model);
				this.rows.push(newRow);
				return newRow;
			},
			getElement: function() {
				return this.table;
			},
			render: function() {
			  if(this.headerRow == null) {
			    this.renderHeader();
			  }
			  for(var i = 0; i < this.rows.length; i++) {
			    this.rows[i].render();
			  }
			  this.table.show();
			},
			renderHeader: function() {
			  
			}
	};
	
	/** TABLE ROW **/
	var dynamicTableRow = function(table, model, options) {
		this.table = table;
		this.model = model;
		this.cells = [];
		this.options = {};
		$.extend(this.options,options);
		this.row = $("<div />").appendTo(this.table.getElement()).addClass(cssClasses.tableRow);
	};
	
	dynamicTableRow.prototype = {
		createCell: function(options) {
			var newCell = new dynamicTableCell(this, options);
			this.cells.push(newCell);
			return newCell;
		},
		getElement: function() {
			return this.row;
		},
		render: function() {
		  for(var i = 0; i < this.cells.length; i++) {
		    this.cells[i].render();
		  }
		}
	};
	
	/** TABLE CELL **/
	var dynamicTableCell = function(row, options) {
		this.row = row;
		this.options = {};
		$.extend(this.options,options);
		this.cell = $("<div />").appendTo(this.row.getElement()).addClass(cssClasses.tableCell);
	};
	
	dynamicTableCell.prototype = {
		render: function() {
			if(typeof(this.options.get) == "function") {
				this.setValue(this.options.get());
			}
		},
		setValue: function(newValue) {
			this.cell.html(newValue);
		},
		getElement: function() {
			return this.cell;
		}
	};
	
	$.fn.extend({
		//NOTE: WILL NOT RETURN CHAINABLE jQuery OBJECT!
		dynamicTable: function(options) {
			if(this.length == 1) {
				var table;
				if(!this.data("dynamicTable")) {
					table = new dynamicTable(this, options);
					this.data("dynamicTable", table);
				} else {
					table = this.data("dynamicTable");
				}
				return table;
			}
			return null;
		},
		iterationGoalTable: function(options) {
			var ret = this.dynamicTable();
			//TODO: sortable etc stuff
			
			return ret;
		}
	});
})(jQuery);