(function($) {
	/** TABLE **/
	var dynamicTable = function(element, options) {
		this.options = options;
		this.element = element;
		this.table = $("<div />").appendTo(this.element);
		element.extend(this);
	};
	
	dynamicTable.prototype = {
			createRow: function(model) {
				var newRow = new dynamicTableRow(this, model);
				return newRow.getElement();
			}
	};
	
	/** TABLE ROW **/
	var dynamicTableRow = function(table, model) {
		this.table = table;
		this.model = model;
		this.row = $("<div />").appendTo(this.table.table);
		this.row.extend(this);
	};
	
	dynamicTableRow.prototype = {
		createCell: function(options) {
			var newCell = new dynamicTableCell(this, options);
			return newCell.getElement();
		},
		getElement: function() {
			return this.row;
		}
	};
	
	/** TABLE CELL **/
	var dynamicTableCell = function(row, options) {
		this.row = row;
		this.options = options;
		this.cell = $("<div />").appendTo(this.row.row);
		this.cell.extend(this);
	};
	
	dynamicTableCell.prototype = {
		setValue: function(newValue) {
		
		},
		getElement: function() {
			return this.cell;
		}
	};
	
	$.fn.extend({
		dynamicTable: function(options) {
			if(this.length != 1) {
				return this;
			}
			if(!this.data("dynamicTable")) {
				this.data("dynamicTable", new dynamicTable(this, options));
			}
		}
	});
})(jQuery);