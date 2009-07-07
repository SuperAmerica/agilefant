/** TABLE ROW **/
var DynamicTableRow = function(table, model, options, template) {
	this.table = table;
	this.model = model;
	var me = this;
	if (this.model) {
		this.model.addEditListener(function(eventData) {
			if (eventData.bubbleEvent) {
				$.each(eventData.bubbleEvent, function(k, v) {
					me.getElement().trigger(v, {});
				});
			}
			me.render();
			me.getElement().trigger("tableDataUpdated", {
				row : me
			});
		}, this.table.tableId);
		this.model.addDeleteListener(function() {
			me.remove();
		}, this.table.tableId);
	}
	this.cells = [];
	this.options = {};
	$.extend(this.options, options);
	this.row = $("<div />").addClass(DynamicTableCssClasses.tableRow);
	if (this.options.toTop) {
		if (this.table.headerRow) {
			this.row.insertAfter(this.table.headerRow.getElement());
		} else {
			this.row.prependTo(this.table.getElement());
		}
	} else {
		this.row.appendTo(this.table.getElement());
	}
	this.row.data("model", model);
};

DynamicTableRow.prototype = new DynamicsView();

DynamicTableRow.prototype = {
	createCell : function(options) {
		var newCell = new DynamicTableCell(this, this.cells.length, options);
		this.cells.push(newCell);
		return newCell;
	},
	remove : function() {
		this.table.deleteRow(this);
		if (this.model) {
			this.model.removeEditListener(this.table.tableId);
			this.model.removeDeleteListener(this.table.tableId);
		}
		var me = this;
		this.row.fadeOut(200, function() {
			me.row.remove();
		});
	},
	getElement : function() {
		return this.row;
	},
	getTable : function() {
		return this.table;
	},
	renderFromTemplate: function(template) {
		for(var i = 0; i < template.length; i++) {
			this.createCell(template[i]);
		}
	},
	render : function() {
		for ( var i = 0; i < this.cells.length; i++) {
			this.cells[i].render();
		}
		this.updateColCss();
	},
	updateColCss : function() {
		var me = this;
		var rules = {};
		$.extend(rules, this.table.options.colCss);
		if (this.options.colCss) {
			$.extend(rules, this.options.colCss);
		}
		$.each(rules, function(i, v) {
			me.row.children(i).css(v);
		});
	},
	openEdit : function() {
		var i;
		for (i = 0; i < this.cells.length; i++) {
			this.cells[i].openEdit(true);
		}
		for (i = 0; i < this.cells.length; i++) {
			if (this.cells[i].setEditFocus()) {
				break;
			}
		}
	},
	cancelEdit : function() {
		for ( var i = 0; i < this.cells.length; i++) {
			this.cells[i].cancelEdit();
		}
	},
	/*
	 * callback is executed if following conditions are met:
	 * 1) row is in edit mode
	 * 2) enter is pressed in one of the row's fields
	 * 3) all fields are valid
	 */
	setSaveCallback : function(callback) {
		var me = this;
		this.options.saveCallback = function() {
			for ( var i = 0; i < me.cells.length; i++) {
				if (!me.cells[i].isValid()) {
					return;
				}
			}
			callback();
		};
	},
	saveEdit : function() {
		var i;
		for (i = 0; i < this.cells.length; i++) {
			if (!this.cells[i].isValid()) {
				return false;
			}
		}
		for (i = 0; i < this.cells.length; i++) {
			this.cells[i].saveEdit();
		}
		return true;
	},
	setNotSortable : function() {
		this.row.addClass("dynamictable-notsortable");
	}
};

/** ROW ACTIONS **/
var TableRowActions = function(cell, row, options) {
  this.cell = cell;
  this.row = row;
  this.inMenu = false;
  this.options = {
      title: '<div class="actionColumn"><div class="edit"><div class="gear" style="float: left;"/><div style="float: right">Edit</div></div></div>'
  };
  $.extend(this.options, options);
  var me = this;
  this.openEvent  = function(cEvent) {
  if(me.menuOpen) {
    me.close();
  } else {
    me.open(cEvent);
  }
  };
  var el = this.cell.getElement();
  this.act = $('<div/>').html(this.options.title).appendTo(el).width("68px");
  this.act.click(this.openEvent);
  
};
TableRowActions.prototype = {
    getElement: function() {
    return this.act;
  },
  open: function(cEvent) {
    var me = this;
    this.menuOpen = true;
    this.handler = function() {
      me.close();
    };
    $(document.body).trigger("dynamictable-close-actions").bind("dynamictable-close-actions", this.handler);
    this.menu = $('<ul/>').appendTo(document.body).addClass("actionCell");
    var off = this.cell.getElement().offset();
    var menuCss = {
        "position":    "absolute",
        "overflow":    "visible",
        "z-index":     "100",
        "white-space": "nowrap",
        "top":         off.top + 18,
        "left":        off.left - 32
    };
    this.menu.css(menuCss);
    $.each(this.options.items, function(index, item) {
      var it = $('<li />').text(item.text).appendTo(me.menu);
      if(item.callback) {
        var row = me.row;
        it.click(function() { item.callback(row); return false;});
      }
    });
    this.act.click(this.handler);
    this.closeClick = function(event) {
      if($(event.target).closest("ul.actionCell").length === 0) {
        me.close();
      }
    };
    cEvent.stopPropagation();
    $(window).click(this.closeClick);
    return false;
  },
  close: function() {
    this.act.unbind('click').click(this.openEvent);
    this.menu.remove();
    $(document.body).unbind("dynamictable-close-actions",this.handler);
    $(window).unbind("click", this.closeClick);
    this.menuOpen = false;
  }
};