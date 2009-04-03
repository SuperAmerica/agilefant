(function($) {
	var cssClasses = {
		tableRow: "dynamictable-row",
		tableCell: "dynamictable-cell",
		table: "dynamictable"
	};
	/** TABLE **/
	var dynamicTable = function(element, options) {
		this.options = options;
		this.element = element;
		this.table = $("<div />").appendTo(this.element).addClass(cssClasses.table);
		jQuery.extend(this,this.table);
	};
	
	dynamicTable.prototype = {
			createRow: function(model) {
				var newRow = new dynamicTableRow(this, model);
				return newRow;
			},
			getElement: function() {
				return this.table;
			}
	};
	
	/** TABLE ROW **/
	var dynamicTableRow = function(table, model) {
		this.table = table;
		this.model = model;
		this.row = $("<div />").appendTo(this.table.getElement()).addClass(cssClasses.tableRow);
	};
	
	dynamicTableRow.prototype = {
		createCell: function(options) {
			var newCell = new dynamicTableCell(this, options);
			return newCell;
		},
		getElement: function() {
			return this.row;
		}
	};
	
	/** TABLE CELL **/
	var dynamicTableCell = function(row, options) {
		this.row = row;
		this.options = {};
		$.extend(this.options,options);
		this.cell = $("<div />").appendTo(this.row.getElement()).addClass(cssClasses.tableCell);
		this.render();
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