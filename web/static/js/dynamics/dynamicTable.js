(function($) {
	var cssClasses = {
		tableRow: "dynamictable-row",
		tableCell: "dynamictable-cell",
		tableHeader: "dynamictable-header",
		table: "dynamictable",
		oddRow: "dynamictable-odd",
		evenRow: "dynamictable-even"
	};
	var statics = {
	  borderPerColumn: 3
	};
	/** TABLE **/
	var dynamicTable = function(element, options) {
    this.options = {
        colCss: {},
        colWidths: [],
        headerCols: [],
        defaultSortColumn: 0
    };
    $.extend(this.options,options);
    var widths = this.calculateColumnWidths(this.options.colWidths);
    for (var i = 0; i < widths.length; i++) {
      if (widths[i]) {
        this.options.colWidths[i].width = widths[i];
      }
    }
		this.element = element;
		this.rows = [];
		this.container = $("<div />").appendTo(this.element).addClass(cssClasses.table);
		this.table = $("<div />").appendTo(this.container).hide();
		this.headerRow = null;
		this.sorting = {
		    column: this.options.defaultSortColumn,
		    direction: -1
		};
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
			getOptions: function() {
			  return this.options;
			},
			getColWidth: function(colno) {
			  return this.options.colWidths[colno];
			},
			getSorting: function() {
			  return this.sorting;
			},
			render: function() {
			  if(this.headerRow == null) {
			    this.renderHeader();
			  }
			  for(var i = 0; i < this.rows.length; i++) {
			    this.rows[i].render();
			  }
			  this.table.show();
			  if (this.options.headerCols[this.options.defaultSortColumn]) {
			    this.doSort(this.options.defaultSortColumn, this.options.headerCols[this.options.defaultSortColumn].sort);
			  }
			},
			renderHeader: function() {
			  if (this.options.headerCols.length == 0) {
			    return false;
			  }
			  var me = this;
			  this.headerRow = $('<div />').addClass(cssClasses.tableRow).addClass(cssClasses.tableHeader).prependTo(this.table);
			  
			  $.each(this.options.headerCols, function(i,v) {
			    var col = $('<div />').addClass(cssClasses.tableCell).appendTo(me.headerRow)
			      .css('min-width',me.options.colWidths[i].minwidth + 'px') 
			      .css('width',me.options.colWidths[i].width + '%');
			    var f;
			    if (v.sort) {
			      f = $('<a href="#"/>').text(v.name).click(function() { me.doSort(i, v.sort); return false; }).appendTo(col);
			    }
			    else {
			      f = $('<span />').text(v.name).appendTo(col);
			    }
			    if (v.tooltip) f.attr('title',v.tooltip);
			  });
			  
			  $.each(this.options.colCss, function(i,v) {
	        me.headerRow.children(i).css(v);
	      });
			},
			doSort: function(colNo, comparator) {
			  if (typeof(comparator) != "function") {
			    return false;
			  }		  
			  if ((this.sorting.column == colNo) && this.sorting.direction == 0) {
			    this.sorting.direction = 1;
			  }
			  else {
			    this.sorting.direction = 0;
			  }
			  this.sorting.column = colNo;
			  
			  var sorted = (this.rows.sort(function(a,b) { return comparator(a.model,b.model); }));
			  if (this.sorting.direction == 1) { sorted = sorted.reverse(); }
			  for(var i = 0; i < sorted.length; i++) {
			    sorted[i].row.appendTo(this.table);
			  }
      },
      calculateColumnWidths: function(params) {
        var num = 0;
        var totalwidth = 0;
        //calculate total minimum width
        for (var i = 0; i < params.length; i++) {
          if (params[i].auto) {
            num++;
            totalwidth += params[i].minwidth;
          }
        }
        totalwidth += statics.borderPerColumn * num;
        
        var retval = [];
       
        //percentage taken by column borders
        var totalPercentage = Math.floor(100 * ((statics.borderPerColumn * num)  / totalwidth));
        
        for (var j = 0; j < params.length; j++) {
          var cell = params[j];
          if (!cell.auto) {
            retval.push(null);
          }
          else {
            var percent = Math.floor(100 * (cell.minwidth / totalwidth));
            totalPercentage += percent;
            retval.push(percent);
          }
        }
        for (var j = 0; j < params.length; j++) {
          var cell = params[j];
          if(!cell.auto && cell.setMaxWidth == true) {
            retval[j] = totalPercentage;
          }
        }
        return retval;
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
			var newCell = new dynamicTableCell(this, this.cells.length, options);
			this.cells.push(newCell);
			return newCell;
		},
		getElement: function() {
			return this.row;
		},
		getTable: function() {
		  return this.table;
		},
		render: function() {
		  for(var i = 0; i < this.cells.length; i++) {
		    this.cells[i].render();
		  }
		  this.updateColCss();
		},
		updateColCss: function() {
		  var me = this;
		  $.each(this.table.options.colCss, function(i,v) {
		    me.row.children(i).css(v);
		  });
		},
		editable: function() {
		  for(var i = 0; i < this.cells.length; i++) {
		    this.cells[i].editable();
		  }
		}
	};
	
	/** TABLE CELL **/
	var dynamicTableCell = function(row, cellno, options) {
		this.row = row;
		this.cellno = cellno;
		this.options = {};
		$.extend(this.options,options);
		this.cell = $("<div />").appendTo(this.row.getElement()).addClass(cssClasses.tableCell);
		this.content = $("<span/>").appendTo(this.cell);
		this.value = null;
		this.field = null;
		var a = row.getTable().getColWidth(cellno);
		if (a) {
		  if (a.minwidth) { this.cell.css('min-width',a.minwidth + 'px'); }
		  if (a.width) { this.cell.css('width',a.width + '%'); }
		  if(a.setMaxWidth) { this.cell.css("clear","left"); }
		}
		// TODO: Remove next row, only for development
		this.row.editable();
		var me = this;
		var dblclick_cb = function() { me.openEdit(me) };
		if (this.options.type) {
		  this.cell.dblclick(dblclick_cb);
		}
	};
	
	dynamicTableCell.prototype = {
		render: function() {
			if(typeof(this.options.get) == "function") {
				this.setValue(this.options.get());
			}
		},
		setValue: function(newValue) {
		  this.value = newValue;
			this.content.html(newValue);
			if (this.field) this.field.val(newValue);
		},
		getElement: function() {
			return this.cell;
		},
		openEdit: function(elem) {
		  var key_cb = function(keyevent) { elem.handleKeyEvent(elem, keyevent); };
		  var blur_cb = function() { elem.closeEdit(elem); elem.field.unbind('blur',blur_cb); };
		  if (elem.field) {
		    elem.field.show(); elem.content.hide();
        elem.field.blur(blur_cb);
        elem.field.keydown(key_cb);
        elem.field.focus();	    
		  }
		},
		closeEdit: function(elem) {
		  elem.field.hide(); elem.content.show();
		  elem.field.unbind('keydown');
		  if (elem.field.val() != elem.value) {
        elem.options.set(elem.field.val());
        // TODO: remove after implementing callback
        elem.setValue(elem.field.val());
      }
		},
		cancelEdit: function(elem) {
		  // BUG: field value is not restored when cancelling
      elem.field.unbind();
      var asd = elem.value;
      alert("cancelled");
		  elem.field.val(elem.value);
		  elem.field.hide(); elem.content.show();
		},
		handleKeyEvent: function(me, keyevent) {
		  if (keyevent.keyCode == 27) {
		    me.cancelEdit(me);
		  }
		  else if (keyevent.keyCode == 13) {
		    me.closeEdit(me);
		  }
		},
		editable: function() {
		  if(this.options.type && !this.field) {
			  // TODO: implement in-cell edits
		    if (this.options.type == "select") {
		      this.field = $('<select/>').appendTo(this.cell).hide();
		    }
		    else if (this.options.type == "wysiwyg") {
		      this.field = $('<textfield />').appendTo(this.cell).hide();
		    }
		    else if (this.options.type == "text"){
		      this.field = $('<input type="text"/>').attr('size','50').appendTo(this.cell).hide();
		    }
		  }
		}
	};
	
	var tableRowActions = function(row,options) {
	  
	};
	tableRowActions.prototype = {
	 open: function() {
	  
	 },
	 close: function() {
	   
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
		  var opts = {
		      colCss: {
		        ':lt(3)': { 'background': '#ffc' },
		        ':eq(3)': { 'background': '#fcc' },
		        ':eq(4)': { 'background': '#cfc' },
		        ':last': { 'background': '#cff' }
		      },
		      headerCols: [
		                   {
		                     name: 'Name',
		                     tooltip: 'Iteration goal name',
		                     sort: agilefantUtils.comparators.nameComparator
		                   },
		                   {
                         name: 'EL',
                         tooltip: 'Total effort left',
                         sort: null
                       },
                       {
                         name: 'OE',
                         tooltip: 'Total original estimate',
                         sort: null
                       },
                       {
                         name: 'ES',
                         tooltip: 'Total effort spent',
                         sort: null
                       },
                       {
                         name: 'Done / Total',
                         tooltip: 'Done / Total backlog items',
                         sort: null
                       }
		                   ],
		      colWidths: [
		                  {
		                    minwidth: 200,
		                    auto: true
		                  },
		                  {
		                    minwidth: 30,
		                    auto: true
		                  },
		                  {
                        minwidth: 30,
                        auto: true
                      },
                      {
                        minwidth: 30,
                        auto: true
                      },
		                  {
		                    minwidth: 40,
		                	  auto: true
		                  },
		                  {
		                    minwidth: 60,
		                    auto: true
		                  },
		                  {
                        setMaxWidth: true,
                        auto: false
                      },
                      {
                        auto: false,
                        setMaxWidth: true
                      }
		                  ]
		  };
		  $.extend(opts,options);
			var ret = this.dynamicTable(opts);
			//TODO: sortable etc stuff
			
			return ret;
		}
	});
})(jQuery)