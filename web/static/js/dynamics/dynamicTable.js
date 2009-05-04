(function($) {
	var cssClasses = {
		tableRow: "dynamictable-row",
		tableCell: "dynamictable-cell",
		tableHeader: "dynamictable-header",
		tableCaption: "dynamictable-caption",
		table: "dynamictable",
		notSortable: "dynamictable-notsortable",
		oddRow: "dynamictable-odd",
		evenRow: "dynamictable-even",
		sortImg: "dynamictable-sortimg",
		sortImgUp: "dynamictable-sortimg-up",
		sortImgDown: "dynamictable-sortimg-down"
	};
	var statics = {
	  borderPerColumn: 0.4
	};
	/** TABLE **/
	var dynamicTable = function(element, options) {
    this.options = {
        colCss: {},
        colWidths: [],
        headerCols: [],
        defaultSortColumn: 0,
        captionText: "Table"
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
		this.caption = $('<div />').addClass(cssClasses.tableCaption).text(this.options.captionText).prependTo(this.container);
		this.sorting = {
		    column: this.options.defaultSortColumn,
		    direction: -1
		};
	};
	
	dynamicTable.prototype = {
			createRow: function(model, opt, noSort) {
				var newRow = new dynamicTableRow(this, model, opt);
				if(!noSort) {
				  this.rows.push(newRow);
				}
				return newRow;
			},
			deleteRow: function(row) {
			  var rows = [];
			  for(var i = 0 ; i < this.rows.length; i++) {
			    if(this.rows[i] != row) {
			      rows.push(this.rows[i]);
			    }
			  }
			  this.rows = rows;
			  $(document.body).trigger("dynamictable-close-actions");
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
			sortTable: function() {
			  if(this.sorting.direction == 1) this.sorting.direction = 0;
			  if(this.sorting.direction == 0) this.sorting.direction = 1;
			  this.doSort(this.sorting.column, this.options.headerCols[this.sorting.column].sort);
			},
			renderCaption: function() {
			  this.caption = new dynamicTableRow(this, null, {toTop: true});
			  this.caption.getElement().addClass(cssClasses.tableCaption).addClass(cssClasses.notSortable);
			  $('<span/>').text(this.options.captionText).appendTo(this.caption.getElement());
			},
			renderHeader: function() {
			  if (this.options.headerCols.length == 0) {
			    return false;
			  }
			  var me = this;
			  this.headerRow = new dynamicTableRow(this, null, {caption: true});
			  this.headerRow.getElement().addClass(cssClasses.tableHeader).addClass(cssClasses.notSortable);
			  var row = this.headerRow;
			  
			  $.each(this.options.headerCols, function(i,v) {
			    var c = row.createCell();
			    var col = c.getElement();
			    var f;
			    if (v.sort) {
			      f = $('<a href="#"/>').text(v.name).click(function() { me.doSort(i, v.sort); return false; }).appendTo(col);
			      $('<div/>').addClass(cssClasses.sortImg).prependTo(f);
			    }
			    else {
			      f = $('<span />').text(v.name).appendTo(col);
			    }
			    if(v.actionCell && me.actionParams) {
			    	new tableRowActions(c,row,me.actionParams);
			    }
			    if (v.tooltip) f.attr('title',v.tooltip);
			  });
			  $.each(this.options.colCss, function(i,v) {
	          me.headerRow.getElement().children(i).css(v);
	      });
			},
			setActionCellParams: function(params) {
				this.actionParams = params;
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
			  this.updateSortArrow(this.sorting.column, this.sorting.direction);
			  
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
      updateSortArrow: function(col, dir) {
        this.headerRow.getElement().find('.' + cssClasses.sortImg).removeClass(cssClasses.sortImgDown)
          .removeClass(cssClasses.sortImgUp);
        var a = this.headerRow.getElement().find('.' + cssClasses.tableCell + ':eq('+col+')')
          .find('.' + cssClasses.sortImg).addClass(cssClasses.sortImgUp);
        if (dir == 0) {
          a.addClass(cssClasses.sortImgUp);
        }
        else {
          a.addClass(cssClasses.sortImgDown);
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
        
        var retval = [];
       
        //percentage taken by column borders
        var totalPercentage = (statics.borderPerColumn * num) / 100;

        //scale total width down to 95% in order to prevent cell wrapping
        totalwidth = totalwidth / (0.95 - totalPercentage);
        
        for (var j = 0; j < params.length; j++) {
          var cell = params[j];
          if (!cell.auto) {
            retval.push(null);
          }
          else {
            var percent = Math.round(1000 * (cell.minwidth / totalwidth))/10;
            totalPercentage += percent;
            retval.push(percent);
          }
        }
        var maxWidth = Math.round(10 * (totalPercentage + ((num - 1) * statics.borderPerColumn)))/10;
        for (var j = 0; j < params.length; j++) {
          var cell = params[j];
          if(!cell.auto && cell.setMaxWidth == true) {
            retval[j] = maxWidth;
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
		  this.model.addEditListener(function() { me.render(); });
		  this.model.addDeleteListener(function() { me.remove(); });
		}
		this.cells = [];
		this.options = {};
		$.extend(this.options,options);
		this.row = $("<div />").addClass(cssClasses.tableRow);
	  if(this.options.toTop) {
	    if(this.table.headerRow) {
	      this.row.insertAfter(this.table.headerRow.getElement());
	    } else {
	        this.row.prependTo(this.table.getElement());
		  }
		} else {
			this.row.appendTo(this.table.getElement());
		}
		this.row.data("model",model);
	};
	
	dynamicTableRow.prototype = {
		createCell: function(options) {
			var newCell = new dynamicTableCell(this, this.cells.length, options);
			this.cells.push(newCell);
			return newCell;
		},
		remove: function() {
		  this.table.deleteRow(this);
		  var me = this;
		  this.row.fadeOut(300, function() {
		    me.row.remove();
		  });
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
		saveEdit: function() {
		  for(var i = 0; i < this.cells.length; i++) {
        this.cells[i].saveEdit();
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
		if (this.options.type && this.options.type != "userchooser") {
		  this.cell.dblclick(dblclick_cb);
		}
		else if (this.options.type == "userchooser") {
		  this.cell.userChooser({
		    legacyMode: false,
		    backlogId: me.options.backlogId,
		    selectCallback: me.options.userchooserCallback,
		    userListContainer: me.getElement(),
		    backlogItemId: me.options.backlogItemId
		  });
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

		saveEdit: function() {
		  if(!this.editorOpen) {
		    return;
		  }
		  if(this.editor.isValid() != true) {
			  //TODO handle error
			  alert("data not valid");
			  return false;
		  }
		  this.content.show();
		  if(this.editor.getValue() != this.options.get()) {
			  this.options.set(this.editor.getValue());
		  }
		  this.editor.remove();
		  this.editor = null;
		  this.removeButtons();
		  this.editorOpen = false;
		},
		cancelEdit: function() {
		  if(!this.editorOpen) {
			  return;
		  }
		  this.content.show();
		  this.removeButtons();
		  this.editor.remove();
		  this.editorOpen = false;
		  this.editor = null;
		},
		openEdit: function(noAutoClose) {
		  var autoClose = true;
		  if(noAutoClose) autoClose = false;
		  if(this.options.type && !this.editorOpen) {
		    this.editorOpen = true;
        this.content.hide();
        if(this.options.type == "text") {
        	this.editor = new textEdit(this, autoClose);
        } else if(this.options.type == "wysiwyg") {
        	this.editor = new wysiwygEdit(this, autoClose);
        } else if(this.options.type == "effort") {
          this.editor = new effortEdit(this, autoClose);
        } else if(this.options.type == "select") {
          this.editor = new selectEdit(this, this.options.items, autoClose);
        }
        if(!autoClose && this.options.buttons) {
          var me = this;
          me.addedButtons = [];
          $.each(this.options.buttons, function(button, opts) {
            var button = $("<button />").appendTo(me.cell).text(opts.text)
              .click(opts.action).addClass("dynamicButton");
            me.addedButtons.push(button);
          });
        }
		  }
		},
		removeButtons: function() {
			if(this.addedButtons) {
				$.each(this.addedButtons, function(k,v) {
			          v.remove();
			    });
			}
		}
	};	
	
	var commonEdit = {
			getValue: function() {
				return this.field.val();
			},
			_cancel: function() {
				this.cell.cancelEdit();
			},
			_store: function() {
				this.cell.saveEdit();
			}
	};
	
	/** WYSIWYG EDIT **/
	
	var wysiwygEdit = function(cell, autoClose) {
		this.cell = cell;
		this.field = $('<textarea>' + this.cell.options.get() + '</textarea>').appendTo(this.cell.getElement()).width("80%");
	    setUpWysiwyg(this.field);
	    this.editor = this.cell.getElement().find(".wysiwyg");
	    this.editorBody = this.editor.find("iframe").contents();
	    this.editorBody.focus();
	    if(autoClose == true) {
	    	var me = this;
	    	this.mouseEvent = function(event) { me._mouseClick(event); };
	    	$(document.body).click(this.mouseEvent);
	    	this.cancelCb = function(event) { 
	    		if(event.keyCode == 27) { //ESC
	    			me._cancel(); 
	    		}
	    	};
	    	this.editorBody.keydown(this.cancelCb);
	    	
	    }
	};
	wysiwygEdit.prototype = {
		  _mouseClick: function(event) {
			if(event.target) {
				var target = $(event.target);
				var parent = target.closest("div.wysiwyg");
				var wysiwyg = this.field.prev("div.wysiwyg");
				if(parent.length > 0 && parent.get(0) == wysiwyg.get(0)) {
				  return false;
				} else {
				  this._store();
				  $(document.body).unbind("click",this.mouseEvent)
				}
			}
		  },
		  remove: function() {
			  this.editor.remove();
			  this.field.remove();
			  $(document.body).unbind("click",this.mouseEvent);
		  },
		  isValid: function() {
			  return true;
		  }
	};
	$.extend(wysiwygEdit.prototype, commonEdit);

	/** TEXT EDIT **/
	
	var textEdit = function(cell, autoClose) {
		this.cell = cell;
		this.field = $('<input type="text"/>').width("80%").appendTo(this.cell.getElement()).focus();
	    this.field.val(this.cell.options.get());
	  	var me = this;
	    if(autoClose == true) {
	    	var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
	    	var blur_cb = function() { me._store(); };
            this.field.blur(blur_cb);
            this.field.keydown(key_cb);
	        this.field.focus(); 
	    }
	};
	textEdit.prototype = {
		_handleKeyEvent: function(keyevent) {
		  if (keyevent.keyCode == 27) {
		    this._cancel();
		  }
		  else if (keyevent.keyCode == 13) {
		    this._store();
		  }
		},
		isValid: function() {
			return true;
		},
		remove: function() {
			this.field.remove();
		}
	};
	$.extend(textEdit.prototype, commonEdit);
	
	/** EFFORT EDIT **/
	 var effortEdit = function(cell, items, autoClose) {
	    this.cell = cell;
	    this.field = $('<input type="text"/>').attr("size","15").appendTo(this.cell.getElement()).focus();
	    var val = this.cell.options.get();
	    if(val == "&mdash;") val = "";
	    this.field.val(val);
      var me = this;
      if(autoClose == true) {
        var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
        var blur_cb = function() { me._store(); };
        this.field.blur(blur_cb);
        this.field.keydown(key_cb);
        this.field.focus(); 
      }
	  };
	  effortEdit.prototype = {
	    _handleKeyEvent: function(keyevent) {
	      if (keyevent.keyCode == 27) {
	        this._cancel();
	      }
	      else if (keyevent.keyCode == 13) {
	        this._store();
	      }
	    },
	    isValid: function() {
	      return validateEstimateFormat(this.field.val());
	    },
	    remove: function() {
	      this.field.remove();
	    }
	  };
	  $.extend(effortEdit.prototype, commonEdit);
	
	/** SELECT EDIT **/
	var selectEdit = function(cell, items, autoClose) {
	  var me = this;
	  this.cell = cell;
	  this.field = $('<select/>').css('width','100%').appendTo(this.cell.getElement()).focus();
    $.each(items, function(i,v) {
      $('<option/>').attr('value',i).text(v).appendTo(me.field);
    });
    var val = this.cell.options.get();
    this.field.val(val);
    if (autoClose == true) {
      var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
      var blur_cb = function() { me._cancel(); };
      var change_cb = function() { me._store(); };
      this.field.blur(blur_cb);
      this.field.keydown(key_cb);
      this.field.change(change_cb);
      this.field.focus();
    }
	};
	selectEdit.prototype = {
	  _handleKeyEvent: function(keyevent) {
	    if (keyevent.keyCode == 27) {
	      this._cancel();
	    }
	    else if (keyevent.keyCode == 13) {
	      this._store();
	    }
	  },
	  isValid: function() {
	    return true;
	  },
	  remove: function() {
	    this.field.remove();
	  }
	};
	$.extend(selectEdit.prototype, commonEdit);
	  
	/** ROW ACTIONS **/
	var tableRowActions = function(cell, row, options) {
	  this.cell = cell;
	  this.row = row
	  this.inMenu = false;
	  this.options = options;
	  var me = this;
	  this.openEvent  = function() {
	    me.open();
	  };
	  var el = this.cell.getElement();
	  this.act = $("<span>Actions</span>").appendTo(el);
	  this.act.click(this.openEvent);
	  
	};
	tableRowActions.prototype = {
	 open: function() {
	  var me = this;
	  this.handler = function() {
      me.close();
	  };
	  $(document.body).trigger("dynamictable-close-actions").bind("dynamictable-close-actions", this.handler);
	  this.menu = $('<ul/>').appendTo(document.body).addClass("actionCell");
	  this.menu.mouseenter(function() { me.inMenu = true; });
	  this.menu.mouseleave(function() { 
	    if(me.inMenu) {
	      me.close();
	    }
	  });
	  var pos = this.cell.getElement().position();
	  var menuCss = {
	      "position":    "absolute",
	      "overflow":    "visible",
	      "z-index":     "100",
	      "white-space": "nowrap",
	      "top":         pos.top + 16,
	      "left":        pos.left
	  }
	  this.menu.css(menuCss);
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
	   this.act.unbind('click').click(this.openEvent);
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
		      captionText: "Iteration Goals",
		      colCss: {
		        ':lt(7)': { 'background': '#dddddd' },
		        ':eq(7)': { 'background': '#eeeeee' },
		        ':eq(8)': { 'background': '#ffffff' },
		        ':eq(6)': { 'cursor': 'pointer' }
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
                         sort: agilefantUtils.comparators.effortLeftComparator
                       },
                       {
                         name: 'OE',
                         tooltip: 'Total original estimate',
                         sort: agilefantUtils.comparators.originalEstimateComparator
                       },
                       {
                         name: 'ES',
                         tooltip: 'Total effort spent',
                         sort: agilefantUtils.comparators.effortSpentComparator
                       },
                       {
                         name: 'Done / Total',
                         tooltip: 'Done / Total backlog items',
                         sort: null
                       },
                       {
                         name: '',
                         actionCell: true,
                         tooltip: "",
                         sort: null
                       }
                       ],
		      colWidths: [
		                  {
		                    minwidth: 20,
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
		},
		backlogItemsTable: function(options) {
      var opts = {
          defaultSortColumn: 3,
          captionText: "Backlog items",
          colCss: { ':eq(8)': { 'cursor': 'pointer' },
                    ':lt(9)': { 'background-color': '#eee' },
                    ':eq(9)': { 'background-color': '#fff' }
                  },
          headerCols: [
                       {
                    	 name: "Themes",
                    	 tooltip: "Business themes",
                    	 sort: null
                       },
                       {
                         name: 'Name',
                         tooltip: 'Backlog item name',
                         sort: agilefantUtils.comparators.nameComparator
                       },
                       {
                         name: 'State',
                         tooltip: 'Backlog item state',
                         sort: null
                       },
                       {
                         name: 'Priority',
                         tooltip: 'Backlog item priority',
                         sort: agilefantUtils.comparators.bliPriorityAndStateComparator
                       },
                       {
                         name: 'Responsibles',
                         tooltip: 'Backlog item responsibles',
                         sort: null
                       },
                       {
                         name: 'EL',
                         tooltip: 'Total effort left',
                         sort: agilefantUtils.comparators.effortLeftComparator
                       },
                       {
                         name: 'OE',
                         tooltip: 'Total original estimate',
                         sort: agilefantUtils.comparators.originalEstimateComparator
                       },
                       {
                         name: 'ES',
                         tooltip: 'Total effort spent',
                         sort: agilefantUtils.comparators.effortSpentComparator
                       },
                       {
                         name: 'Actions',
                         tooltip: "",
                         sort: null
                       }
                       ],
          colWidths: [
                      {
                    	minwidth: 30,
                    	auto: true
                      },
                      {
                        minwidth: 180,
                        auto: true
                      },
                      {
                        minwidth: 50,
                        auto: true
                      },
                      {
                        minwidth: 50,
                        auto: true
                      },
                      {
                        minwidth: 50,
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
                        minwidth: 30,
                        auto: true
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