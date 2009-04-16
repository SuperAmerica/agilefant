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
			  this._sortable();
			},
			renderHeader: function() {
			  if (this.options.headerCols.length == 0) {
			    return false;
			  }
			  var me = this;
			  this.headerRow = $('<div />').addClass(cssClasses.tableRow).addClass(cssClasses.tableHeader).addClass("dynamictable-notsortable").prependTo(this.table);
			  
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
			  
			  var sorted = (this.rows.sort(function(a,b) { 
			    if(!a.model) {
			      return 1;
			    }
			    if(!b.model) {
			      return -1;
			    }
			    return comparator(a.model,b.model); 
			    }));
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
        var totalPercentage = Math.floor(10000 * ((statics.borderPerColumn * num)  / totalwidth))/100;

        //scale total width down to 95% in order to prevent cell wrapping
        totalwidth = totalwidth*1.05;
        
        for (var j = 0; j < params.length; j++) {
          var cell = params[j];
          if (!cell.auto) {
            retval.push(null);
          }
          else {
            var percent = Math.floor(10000 * (cell.minwidth / totalwidth))/100;
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
      },
      activateSortable: function(options) {
        this.options.sortOptions = options;
        this.options.sortable = true;
      },
      _sortable: function() {
        if(!this.sortActive && this.options.sortable) {
          this.sortActive = true;
          var defOpt = {
           handle: '.dynamictable-sorthandle',
           items: '> *:not(.dynamictable-notsortable)',
            cursor: 'move',
            placeholder : 'dynamictable-placeholder' 
          };
          $.extend(defOpt, this.options.sortOptions);
          this.table.sortable(defOpt);
        }
      }
	};
	
	/** TABLE ROW **/
	var dynamicTableRow = function(table, model, options) {
		this.table = table;
		this.model = model;
		var me = this;
		if(this.model) {
		  this.model.addListener(function() { me.render(); });
		}
		this.cells = [];
		this.options = {};
		$.extend(this.options,options);
		this.row = $("<div />").appendTo(this.table.getElement()).addClass(cssClasses.tableRow);
		this.row.data("model",model);
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
		openEdit: function() {
		  for(var i = 0; i < this.cells.length; i++) {
		    this.cells[i].openEdit(true);
		  }
		},
		cancelEdit: function() {
		  for(var i = 0; i < this.cells.length; i++) {
        this.cells[i].cancelEdit();
      }
		},
		closeEdit: function() {
		  for(var i = 0; i < this.cells.length; i++) {
        this.cells[i].closeEdit();
      }
		},
		setNotSortable: function() {
		  this.row.addClass("dynamictable-notsortable");
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
		var me = this;
		var dblclick_cb = function() { me.openEdit() };
		if (this.options.type) {
		  this.cell.dblclick(dblclick_cb);
		}
	};
	
	dynamicTableCell.prototype = {
	  setActionCell: function(options) {
	    new tableRowActions(this,this.row,options);
	  },
	  activateSortHandle: function() {
	    this.cell.addClass("dynamictable-sorthandle");
	  },
		render: function() {
			if(typeof(this.options.get) == "function") {
				this.setValue(this.options.get());
			}
		},
		setValue: function(newValue) {
		  this.value = newValue;
			this.content.html(newValue);
		},
		getElement: function() {
			return this.cell;
		},

		closeEdit: function() {
		  if(!this.field) {
		    return;
		  }
		  this.content.show();
		  this.field.unbind('keydown');
		  if (this.field.val() != this.value) {
        this.options.set(this.field.val());
      }
		  this.field.remove();
		  this.field = null;
		  if(this.addedButtons) {
  		  $.each(this.addedButtons, function(k,v) {
  		    v.remove();
  		  });
		  }
		  this.cell.find(".wysiwyg").remove();
		},
		cancelEdit: function() {
		  if(!this.field) {
		    return;
		  }
	    this.field.remove();
		  this.content.show();
		  this.field = null;
  		if(this.addedButtons) {
        $.each(this.addedButtons, function(k,v) {
          v.remove();
        });
  		}
		  this.cell.find(".wysiwyg").remove();
		},
		handleKeyEvent: function(me, keyevent) {
		  if (keyevent.keyCode == 27) {
		    me.cancelEdit(me);
		  }
		  else if (keyevent.keyCode == 13) {
		    me.closeEdit(me);
		  }
		},
		openEdit: function(noAutoclose) {
		  if(this.options.type && !this.field) {
		    if (this.options.type == "select") {
		      this.field = $('<select/>').appendTo(this.cell);
		    }
		    else if (this.options.type == "wysiwyg") {
		      this.field = $('<textarea>' + this.options.get() + '</textarea>').appendTo(this.cell).width("80%");
		      setUpWysiwyg(this.field);
		      var me = this;
		      var kh  = function(event) {
		        if(event.target) {
		          if(!me.field) {
		            return;
		          }
		          var target = $(event.target);
		          var parent = target.closest("div.wysiwyg");
		          var wysiwyg = me.field.prev("div.wysiwyg");
		          if(parent.length > 0 && parent.get(0) == wysiwyg.get(0)) {
		            return false;
		          }
		        }
		        
		        $(document).unbind("click", kh);
		        $(document).unbind("keydown", kh);
		        me.cancelEdit();
		      };
		      if(noAutoclose != true) {
		        $(document).keydown(kh).click(kh);
		      }
		    }
		    else if (this.options.type == "text"){
		      this.field = $('<input type="text"/>').attr('size','50').appendTo(this.cell).focus();
		      this.field.val(this.options.get());
		    }
		    if(this.options.type != "wysiwyg") {
  		    var me = this;
          var key_cb = function(keyevent) { me.handleKeyEvent(me, keyevent); };
          var blur_cb = function() { me.closeEdit(me); };
          if(noAutoclose != true) {
            this.field.blur(blur_cb);
            this.field.keydown(key_cb);
            this.field.focus(); 
          }
		    }
        this.content.hide();
        if((noAutoclose || this.options.type == "wysiwyg") && this.options.buttons) {
          var me = this;
          me.addedButtons = [];
          $.each(this.options.buttons, function(button, opts) {
            var button = $("<button />").appendTo(me.cell).text(opts.text)
              .click(opts.action).addClass("dynamicButton");
            me.addedButtons.push(button);
          });
        }
		  }
		  
		}
	};
	
	var tableRowActions = function(cell, row, options) {
	  this.cell = cell;
	  this.row = row
	  this.options = options;
	  var me = this;
	  var open  = function() {
	    me.open();
	  };
	  var el = this.cell.getElement();
	  this.act = $("<span>Actions</span>").appendTo(el);
	  this.act.mouseover(open);
	  
	};
	tableRowActions.prototype = {
	 open: function() {
	  var me = this;
	  this.handler = function() {
      me.close();
	  };
	  $(document.body).trigger("dynamictable-close-actions").bind("dynamictable-close-actions", this.handler);
	  this.menu = $('<ul>&nbsp;</ul>').appendTo(document.body).addClass("actionCell");
	  this.menu.css("position","absolute").css("overflow","visible").css("z-index","100");
	  var pos = this.cell.getElement().position();
	  this.menu.css("top",pos.top + 16);
	  this.menu.css("left",pos.left);
   var me = this;
	  $.each(this.options.items, function(index, item) {
	    var it = $('<li />').text(item.text).appendTo(me.menu);
	    if(item.callback) {
	      var row = me.row;
	      it.click(function() { item.callback(row); });
	    }
	  });
  	this.act.click(this.handler);
	 },
	 close: function() {
	   this.menu.remove();
	   $(document.body).unbind("dynamictable-close-actions",this.handler);
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
		        ':lt(7)': { 'background': '#cceeee' },
		        ':eq(7)': { 'background': '#eeffff' }
		      },
		      headerCols: [ {
		                     name: "Prio",
		                     tooltio: "Priority",
		                     sort: agilefantUtils.comparators.priorityComparator
		                   },
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
                       },
                       {
                         name: ' ',
                         tooltip: "",
                         sort: null
                       }
                       ],
		      colWidths: [
		                  {
		                    minwidth: 15,
		                    auto: true
		                  },
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
		                    minwidth: 40,
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
			
			return ret;
		}
	});
})(jQuery)