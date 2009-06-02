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
		captionActions: "dynamictable-captionactions",
		captionAction: "dynamictable-captionaction",
		sortImgDown: "dynamictable-sortimg-down"
	};
	var statics = {
	  borderPerColumn: 0.4
	};
	
	var dynamicTableId = 0;
	
	/** TABLE **/
	var DynamicTable = function(element, options) {
	    this.options = {
	        colCss: {},
	        colWidths: [],
	        headerCols: [],
	        defaultSortColumn: 0,
	        captionText: "Table",
	        noHeader: false
	    };
	    $.extend(this.options,options);
	    this.tableId = dynamicTableId++;
	    var widths = this.calculateColumnWidths(this.options.colWidths);
	    for (var i = 0; i < widths.length; i++) {
	      if (widths[i]) {
	        this.options.colWidths[i].width = widths[i];
	      }
	    }
		this.element = element;
		this.rows = [];
		this.container = $("<div />").appendTo(this.element).addClass(cssClasses.table);
		this.table = $("<div />").appendTo(this.container);
		var me = this;
		this.table.bind("tableDataUpdated", function() {
		 me.sortTable();
		});
		this.headerRow = null;
		this.sorting = {
		    column: this.options.defaultSortColumn,
		    direction: 0
		};
		this.captionActions = {};
		this.tableRowHashes = [];
	};
	
	DynamicTable.prototype = {
		createRow: function(model, opt, noSort) {
			var newRow = new DynamicTableRow(this, model, opt);
			if(this.rows.length === 0 && this.headerRow) {
				this.headerRow.getElement().show();
			}
			if(!noSort) {
			  this.rows.push(newRow);
			}
			if(model && typeof model.getHashCode == "function" && model.getHashCode()) {
				this.tableRowHashes.push(model.getHashCode());
			}
			return newRow;
		},
		deleteRow: function(row) {
			var rows = [];
			var i = 0;
			for(i = 0 ; i < this.rows.length; i++) {
				if(this.rows[i] != row) {
					rows.push(this.rows[i]);
				}
			}
			if(rows.length === 0) {
				this.headerRow.getElement().hide();
			}
			//check if row is associated with a model that has a hash code, if so the hash must be removed
			if(row.model && typeof row.model.getHashCode == "function" && row.model.getHashCode()) {
				var hashCode = row.model.getHashCode();
				var tmp = this.tableRowHashes;
				this.tableRowHashes = [];
				for(i = 0; i < tmp.length; i++) {
					if(tmp[i] != hashCode) {
						this.tableRowHashes.push(tmp[i]);
					}
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
			if(!this.headerRow && !this.options.noHeader) {
				this.renderHeader();
			}
			if(!this.caption && !this.options.noHeader) {
				this.renderCaption();
			}
			for(var i = 0; i < this.rows.length; i++) {
				this.rows[i].render();
			}
			this.table.show();
			this.sortTable();
			this._sortable();
		},
		addCaptionAction: function(name, options) {
			if(this.options.noHeader) {
				return;
			}
			//caption containers must be inserted first
			if(!this.caption) {
				this.renderCaption();
			}
			options.element = $('<li />').addClass(cssClasses.captionAction).appendTo(this.captionAction).css("float","right");
			var me = this;
			options.element.click(function() { 
				if(options.toggleWith) {
					me.captionActions[options.toggleWith].element.toggle();
					options.element.toggle();
				}
				options.callback();
			});
			options.element.text(options.text);
			if(options.style) {
				options.element.addClass(options.style);
			}
			if(options.hide) {
				options.element.hide();
			}
			this.captionActions[name] = options;
		},
		//sort table without changing sort direction
		sortTable: function() {
			if(!this.sorting || !this.options.headerCols[this.sorting.column]) {
				return;
			}
			this._sort(this.sorting.column, this.options.headerCols[this.sorting.column].sort, this.sorting.direction);
		},
		renderCaption: function() {
			if(!this.options.noHeader) {
				  this.caption = $('<div />').addClass(cssClasses.tableCaption).prependTo(this.container).width(this.maxWidth+"%");
				  $("<div />").css("float", "left").text(this.options.captionText).appendTo(this.caption).width("30%");
				  this.captionAction = $('<ul />').addClass(cssClasses.captionActions).appendTo(this.caption).css("float","right").width("68%");
			}
		},
		renderHeader: function() {
			if (this.options.headerCols.length === 0) {
				return false;
			}
			var me = this;
			this.headerRow = new DynamicTableRow(this, null, {toTop: true});
			this.headerRow.getElement().addClass(cssClasses.tableHeader).addClass(cssClasses.notSortable);
			var row = this.headerRow;
			if(this.rows.length === 0) {
				this.headerRow.getElement().hide();
			}
			$.each(this.options.headerCols, function(i,v) {
				var c = row.createCell();
				var col = c.getElement();
				var f;
				if (v.sort) {
					f = $('<a href="#"/>').text(v.name).click(function() { me.sortAndUpdateDirection(i, v.sort); return false; }).appendTo(col);
					$('<div/>').addClass(cssClasses.sortImg).prependTo(f);
				}
				else {
					f = $('<span />').text(v.name).appendTo(col);
				}
				if(v.actionCell && me.actionParams) {
					var actCol = new TableRowActions(c,row,me.actionParams);
				}
				if (v.tooltip) {
				  f.attr('title',v.tooltip);
				}
			});
			$.each(this.options.colCss, function(i,v) {
				me.headerRow.getElement().children(i).css(v);
			});
		},
		setActionCellParams: function(params) {
			this.actionParams = params;
		},
		//sort and change sort direction
		sortAndUpdateDirection: function(colNo, comparator) {
			if (typeof(comparator) != "function") {
				return false;
			}
			if ((this.sorting.column == colNo) && this.sorting.direction === 0) {
				this.sorting.direction = 1;
			}
			else {
				this.sorting.direction = 0;
			}
			this.sorting.column = colNo;
			this._sort(colNo, comparator, this.sorting.direction);
		},
		//private sort method
		_sort: function(colNo, comparator, direction) {
			if (typeof(comparator) != "function") {
				return false;
			}
			this.updateSortArrow(this.sorting.column, direction);
			var sorted = this.rows.sort(function(a,b) { 
				if(!a.model) {
					return 1;
				}
				if(!b.model) {
					return -1;
				}
				return comparator(a.model,b.model); 
			});
			if (direction == 1) { sorted = sorted.reverse(); }
			for(var i = 0; i < sorted.length; i++) {
				sorted[i].row.appendTo(this.table);
			}
		},
		updateSortArrow: function(col, dir) {
			this.headerRow.getElement().find('.' + cssClasses.sortImg).removeClass(cssClasses.sortImgDown)
			.removeClass(cssClasses.sortImgUp);
			var a = this.headerRow.getElement().find('.' + cssClasses.tableCell + ':eq('+col+')')
			.find('.' + cssClasses.sortImg).addClass(cssClasses.sortImgUp);
			if (dir === 0) {
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

			//scale total width down to 99% in order to prevent cell wrapping
			totalwidth = totalwidth / (0.99 - totalPercentage);
			var j;
			
			for (j = 0; j < params.length; j++) {
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
			this.maxWidth = maxWidth;
			for (j = 0; j < params.length; j++) {
				var curCell = params[j];
				if(!curCell.auto && curCell.setMaxWidth === true) {
					retval[j] = maxWidth;
				}
			}
			return retval;
		},
		activateSortable: function(options) {
			this.options.sortOptions = options;
			this.options.sortable = true;
		},
		//activate drag'n'drop sorting within table rows
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
		},
		//check whether model (having a hash code) has been inserted into the table
		isInTable: function(model) {
			if(typeof model.getHashCode == "function" && model.getHashCode()) {
				return ($.inArray(model.getHashCode(), this.tableRowHashes) != -1);
			}
			return false;
		}
	};
	
	/** TABLE ROW **/
	var DynamicTableRow = function(table, model, options) {
		this.table = table;
		this.model = model;
		var me = this;
		if(this.model) {
		  this.model.addEditListener(function(eventData) { 
			  if(eventData.bubbleEvent) {
				  $.each(eventData.bubbleEvent, function(k,v) {
					 me.getElement().trigger(v,{}); 
				  });
			  }
			  me.render(); 
			  me.getElement().trigger("tableDataUpdated", {row: me});
		  }, this.table.tableId);
		  this.model.addDeleteListener(function() { me.remove(); }, this.table.tableId);
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
	
	DynamicTableRow.prototype = {
		createCell: function(options) {
			var newCell = new DynamicTableCell(this, this.cells.length, options);
			this.cells.push(newCell);
			return newCell;
		},
		remove: function() {
		  this.table.deleteRow(this);
		  if(this.model) {
			  this.model.removeEditListener(this.table.tableId);
			  this.model.removeDeleteListener(this.table.tableId);
		  }
		  var me = this;
		  this.row.fadeOut(200, function() {
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
		  var rules = {};
		  $.extend(rules, this.table.options.colCss);
		  if(this.options.colCss) {
			  $.extend(rules, this.options.colCss);
		  }
		  $.each(rules, function(i,v) {
		    me.row.children(i).css(v);
		  });
		},
		openEdit: function() {
		  var i;
		  for(i = 0; i < this.cells.length; i++) {
		    this.cells[i].openEdit(true);
		  }
		  for(i = 0; i < this.cells.length; i++) {
		    if(this.cells[i].setEditFocus()) {
		    	break;
		    }
		  }
		},
		cancelEdit: function() {
		  for(var i = 0; i < this.cells.length; i++) {
			  this.cells[i].cancelEdit();
		  }
		},
		/*
		 * callback is executed if following conditions are met:
		 * 1) row is in edit mode
		 * 2) enter is pressed in one of the row's fields
		 * 3) all fields are valid
		 */
		setSaveCallback: function(callback) {
			var me = this;
			this.options.saveCallback = function() {
				for(var i = 0; i < me.cells.length; i++) {
					if(!me.cells[i].isValid()) {
						return;
					}
				}
				callback();
			};
		},
		saveEdit: function() {
		  var i;
		  for(i = 0; i < this.cells.length; i++) {
			  if(!this.cells[i].isValid()) {
				  return false;
			  }
		  }
		  for(i = 0; i < this.cells.length; i++) {
			  this.cells[i].saveEdit();
		  }
		  return true;
		},
		setNotSortable: function() {
		  this.row.addClass("dynamictable-notsortable");
		}
	};
	
	/** TABLE CELL **/
	var DynamicTableCell = function(row, cellno, options) {
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
		var dblclick_cb = function() { me.openEdit(); };
		if (this.options.type && this.options.type != "empty") {
			this.cell.dblclick(dblclick_cb);
			this.updateTooltip();
		} 
	};
	
	DynamicTableCell.prototype = {
	  setActionCell: function(options) {
	    this.actionObj = new TableRowActions(this,this.row,options);
	    this.isActionCell = true;
	  },
	  activateSortHandle: function() {
	    this.cell.addClass("dynamictable-sorthandle").addClass("dragHandle");
	    this.cell.attr("title","Change story priority order by dragging and dropping.");	    
	  },
	  setDragHandle: function() {
		this.cell.addClass("dragHandle"); 
	  },
	  updateTooltip: function() {
		  if(this.editorOpen) {
			  this.cell.removeAttr("title");
		  } else {
			  this.cell.attr("title","Double-click the cell to edit it."); 
		  }
	  },
		render: function() {
			if(typeof this.options.get === "function") {
				var value = this.options.get();
				if(typeof this.options.htmlDecorator === "function") {
					value = this.options.htmlDecorator(value);
				} else if(typeof this.options.decorator === "function") {
					value = this.options.decorator(value);
				}
				this.setValue(value);
			}
		},
		setValue: function(newValue) {
		  this.value = newValue;
			this.content.html(newValue);
		},
		getElement: function() {
			return this.cell;
		},
		isValid: function() {
			if(!this.editor) {
				return true;
			}
			return this.editor.isValid();
		},
		getRow: function() {
			return this.row;
		},
		saveEdit: function() {
		  if(!this.editorOpen) {
		    return;
		  }
		  if(!this.editor.isValid()) {
			  return false;
		  }
		  var newValue = this.editor.getValue();
		  this.editor.remove();
		  this.editor = null;
		  this.content.show();
		  this.removeButtons();
		  if(this.isActionCell) { 
			  this.actionObj.getElement().show();
		  }
		  this.editorOpen = false;
		  this.updateTooltip();
		  if(newValue != this.options.get()) {
			  this.options.set(newValue);
		  }
		},
		cancelEdit: function() {
		  if(!this.editorOpen) {
			  return;
		  }
		  this.content.show();
		  this.removeButtons();
		  if(this.editor)  { 
		    this.editor.remove();
		  }
		  this.editorOpen = false;
		  this.updateTooltip();
		  if(this.isActionCell) { 
			  this.actionObj.getElement().show();
		  }
		  this.editor = null;
		},
		setEditFocus: function() {
			if(this.editorOpen && this.editor) {
				return this.editor.focus();
			}
		},
		/* 
		 * when no auto close is set no mouse or keyboard
		 * events will be registered to close and save the 
		 * editor. Instead save edit must be manually called.
		 * See DynamicTableRow.openEdit
		 */
		openEdit: function(noAutoClose) {
			var me = this;
			if(typeof this.options.onEdit == "function") {
				if(!this.options.onEdit(noAutoClose)) {
					return;
				}
			}
			if (this.options.type == "user") { 
				if(noAutoClose) { 
					return;
				}
				var uc = new AgilefantUserChooser({
					selectThese: agilefantUtils.objectToIdArray(me.options.getEdit()),
					selectCallback: function(chooser) {
					var users = chooser.getSelected(true);
					me.options.set(users); 
					me.render();
				},
				backlogId: this.options.backlogId,
				storyId: this.options.storyId
				});
				uc.init();
				return;
			} else if(this.options.type == "theme") {
				if(noAutoClose) { 
					return;
				}
				var tc = new AgilefantThemeChooser({
					selectedThemes: function() { return agilefantUtils.objectToIdArray(me.options.get()); },
					onSelect: function(themes) {
						me.options.set(themes); 
						me.render();
					},
					backlogId: this.options.backlogId
				});
				tc.init();
				return;
			}
			var autoClose = true;
			if(noAutoClose) { 
				autoClose = false;
			}
			if(this.options.type && !this.editorOpen) {
				this.editorOpen = true;
				this.content.hide();
				if(this.isActionCell) { 
					this.actionObj.getElement().hide();
				}
				if(this.options.type == "text") {
					this.editor = new TextEdit(this, autoClose);
				} else if(this.options.type == "wysiwyg") {
					this.editor = new WysiwygEdit(this, autoClose);
				} else if(this.options.type == "effort") {
					this.editor = new EffortEdit(this, autoClose);
				} else if(this.options.type == "select") {
					this.editor = new SelectEdit(this, this.options.items, autoClose);
				} else if(this.options.type == "empty") {
					this.editor = new EmptyEdit(this);
				} else if(this.options.type == "date") {
					this.editor = new DateEdit(this,autoClose);
				}
				if(!autoClose && this.options.buttons) {
					me.addedButtons = [];
					$.each(this.options.buttons, function(button, opts) {
						var nbutton = $("<button />").appendTo(me.cell).text(opts.text)
						.click(opts.action).addClass("dynamicButton");
						me.addedButtons.push(nbutton);
					});
				}
			}
			this.updateTooltip();
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
			},
			focus: function() {
				if(this.field) {
					this.field.focus();
					return true;
				}
				return false;
			},
			_storeRow: function() {
				var opt = this.cell.getRow().options;
				if(opt && typeof opt.saveCallback == "function") {
					opt.saveCallback();
				}
			},
			_handleKeyEvent: function(keyevent) {
				if (keyevent.keyCode == 27 && this.autoClose) {
					keyevent.stopPropagation();
					this._cancel();
				}
				else if (keyevent.keyCode == 13 && this.autoClose) {
					keyevent.stopPropagation();
					this._store();
				} else if(keyevent.keyCode == 13 && !this.autoClose) {
					keyevent.stopPropagation();
					this._storeRow();
				}
			}
	};
	
	/** EMPTY EDIT **/
	var EmptyEdit = function(cell) {
	  this.cell = cell;
	  if(typeof cell.options.action == "function") {
		  cell.options.action();
	  }
	};
	EmptyEdit.prototype = {
	  _mouseClick: function(event) { return false; },
	  remove: function() { },
	  isValid: function() { return true; },
	  getValue: function() { return ""; }
	};
	
	/** WYSIWYG EDIT **/
	
	var WysiwygEdit = function(cell, autoClose) {
		this.cell = cell;
		var value = this.cell.options.get();
		if (!value) { 
			value = "";
		}
		this.field = $('<textarea>' + value + '</textarea>').appendTo(this.cell.getElement()).width("80%");
	    setUpWysiwyg(this.field);
	    this.editor = this.cell.getElement().find(".wysiwyg");
	    this.editorBody = this.editor.find("iframe").contents();
	    this.editorBody.focus();
	    if(autoClose === true) {
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
	WysiwygEdit.prototype = {
		  _mouseClick: function(event) {
			if(event.target) {
				var target = $(event.target);
				var parent = target.closest("div.wysiwyg");
				var wysiwyg = this.field.prev("div.wysiwyg");
				if(parent.length > 0 && parent.get(0) == wysiwyg.get(0)) {
				  return false;
				} else {
				  this._store();
				  $(document.body).unbind("click",this.mouseEvent);
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
	$.extend(WysiwygEdit.prototype, commonEdit);

	/** TEXT EDIT **/
	
	var TextEdit = function(cell, autoClose) {
		this.cell = cell;
		this.autoClose = autoClose;
		this.field = $('<input type="text"/>').width("80%").appendTo(this.cell.getElement()).focus();
	    this.field.val(this.cell.options.get());
	  	var me = this;
    	var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
        this.field.keydown(key_cb);
	    if(autoClose === true) {
	    	var blur_cb = function() { me._store(); };
            this.field.blur(blur_cb);
	        this.field.focus(); 
	    }
	};
	TextEdit.prototype = {
		isValid: function() {
			if(this.cell.options.required && this.field.val().length === 0) {
			  this.field.addClass("invalidValue");
			  if(!this.errorMsg) {
			    this.errorMsg = commonView.requiredFieldError(this.cell.getElement());
			    this.cell.getElement().addClass('cellError');
			  }
			  return false;
			}
			this.field.removeClass("invalidValue");
			if(this.errorMsg) {
				this.errorMsg.remove(); 
				this.errorMsg = null;
				this.cell.getElement().removeClass('cellError');
			}
			return true;
		},
		remove: function() {
			if(this.errorMsg) { 
				this.errorMsg.remove();
			}
			this.field.remove();
		}
	};
	$.extend(TextEdit.prototype, commonEdit);
	
	/** EFFORT EDIT **/
	 var EffortEdit = function(cell, autoClose) {
	    this.cell = cell;
	    this.autoClose = autoClose;
	    this.field = $('<input type="text"/>').width('80%').appendTo(this.cell.getElement()).focus();
	    var val = this.cell.options.get();
	    if(val)  { 
	    	val = agilefantUtils.aftimeToString(val, true);
	    }
	    this.field.val(val);
        var me = this;
        var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
        this.field.keydown(key_cb);
        if(autoClose === true) {
          var blur_cb = function() { me._store(); };
          this.field.blur(blur_cb);
          this.field.focus(); 
        }
	  };
	  EffortEdit.prototype = {
	    isValid: function() {
	      if(agilefantUtils.isAftimeString(this.field.val())) {
	    	  this.field.removeClass("invalidValue");
	    	  if(this.errorMsg) { 
	    		  this.errorMsg.remove();
	    		  this.errorMsg = null;
	    		  this.cell.getElement().removeClass('cellError');
	    	  }
	    	  return true;
	      } else {
	    	  if(!this.errorMsg) { 
	    		  this.errorMsg = commonView.effortError(this.cell.getElement());
	    	  }
	    	  this.field.addClass("invalidValue");
	    	  this.cell.getElement().addClass('cellError');
	    	  return false;
	      }
	    },
	    remove: function() {
	      if(this.errorMsg) {
	    	  this.errorMsg.remove();
	      }
	      this.field.remove();
	    }
	  };
	  $.extend(EffortEdit.prototype, commonEdit);
	
	/** DATE EDIT **/
	 var DateEdit = function(cell, autoClose) {
	    this.cell = cell;
	    this.autoClose = autoClose;
	    this.field = $('<input type="text"/>').width('80%').appendTo(this.cell.getElement()).focus();
	    var val = this.cell.options.get();
	    if(val) {
	    	val = agilefantUtils.dateToString(val, true);
	    }
	    this.field.val(val);
      var me = this;
      var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
      this.field.keydown(key_cb);
      if(autoClose === true) {
        var blur_cb = function() { me._store(); };
        this.field.blur(blur_cb);
        this.field.focus(); 
      }
	  };
	  DateEdit.prototype = {
	    isValid: function() {
	      if(agilefantUtils.isDateString(this.field.val())) {
	    	  this.field.removeClass("invalidValue");
	    	  if(this.errorMsg) { 
	    		  this.errorMsg.remove();
	    		  this.errorMsg = null;
	    		  this.cell.getElement().removeClass('cellError');
	    	  }
	    	  return true;
	      } else {
	    	  if(!this.errorMsg) { 
	    		  this.errorMsg = commonView.dateError(this.cell.getElement());
	    	  }
	    	  this.field.addClass("invalidValue");
	    	  this.cell.getElement().addClass('cellError');
	    	  return false;
	      }
	    },
	    remove: function() {
	      if(this.errorMsg) { 
	    	  this.errorMsg.remove();
	      }
	      this.field.remove();
	    }
	  };
	  $.extend(DateEdit.prototype, commonEdit);
	/** SELECT EDIT **/
	var SelectEdit = function(cell, items, autoClose) {
	  var me = this;
	  this.cell = cell;
	  this.autoClose = autoClose;
	  if(typeof items === "function") {
		  items = items();
	  }
	  this.field = $('<select/>').css('width','100%').appendTo(this.cell.getElement()).focus();
    $.each(items, function(i,v) {
      $('<option/>').attr('value',i).text(v).appendTo(me.field);
    });
    var val = this.cell.options.get();
    this.field.val(val);
    var key_cb = function(keyevent) { me._handleKeyEvent(keyevent); };
    this.field.keydown(key_cb);
    if (autoClose === true) {
      var blur_cb = function() { me._cancel(); };
      var change_cb = function() { me._store(); };
      this.field.blur(blur_cb);
      this.field.change(change_cb);
      this.field.focus();
    }
	};
	SelectEdit.prototype = {
	  isValid: function() {
	    return true;
	  },
	  remove: function() {
	    this.field.remove();
	  }
	};
	$.extend(SelectEdit.prototype, commonEdit);
	  
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
	  this.act = $('<div/>').html(this.options.title).appendTo(el);
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
	function addTableColumn(optObj, width,header) {
		if(!optObj.headerCols) {
			optObj.headerCols = [];
		}
		if(!optObj.colWidths) {
			optObj.colWidths = [];
		}
		if(width) {
			optObj.colWidths.push(width);
		}
		if(header) {
			optObj.headerCols.push(header);
		}
	}
	$.fn.extend({
		//NOTE: WILL NOT RETURN CHAINABLE jQuery OBJECT!
		DynamicTable: function(options) {
			if(this.length == 1) {
				var table;
				if(!this.data("DynamicTable")) {
					table = new DynamicTable(this, options);
					this.data("DynamicTable", table);
                } else {
					table = this.data("DynamicTable");
				}
				return table;
			}
			return null;
		},
		storyTable: function(options) {
		  var opts = { captionText: "Stories", defaultSortColumn: 0};
		  if(agilefantUtils.isTimesheetsEnabled()) {
		      opts.colCss = {
		        ':lt(6)': { 'background': '#eee' },
		        ':eq(6)': { 'background': '#fff' },
		        ':eq(7)': { 'background': '#fff' }
		      };
		  } else {
		      opts.colCss = {
		        ':lt(5)': { 'background': '#eee' },
		        ':eq(5)': { 'background': '#fff' },
		        ':eq(6)': { 'background': '#fff' }
			  };			  
		  }
		  addTableColumn(opts, 
				  { minwidth: 30, auto: true },
				  { name: "Prio",
					tooltip: "Story priority",
					sort: agilefantUtils.comparators.priorityComparator
				  });
		  addTableColumn(opts,
				  { minwidth: 280, auto: true },
				  { name: 'Name',
			        tooltip: 'Story name',
			        sort: agilefantUtils.comparators.nameComparator
				  });
		  addTableColumn(opts,		                  
				  { minwidth: 60, auto: true },
				  { name: 'Tasks done',
			        tooltip: 'Done / Total tasks',
			        sort: null
				  });
		  /*
		  addTableColumn(opts,
				  { minwidth: 30, auto: true },
				  { name: 'EL',
					tooltip: 'Total effort left',
			        sort: agilefantUtils.comparators.effortLeftComparator
				  });
		  addTableColumn(opts, 
				  { minwidth: 30, auto: true },
				  { name: 'OE',
			        tooltip: 'Total original estimate',
			        sort: agilefantUtils.comparators.originalEstimateComparator
				  });
				  */
		  if(agilefantUtils.isTimesheetsEnabled()) {
			  addTableColumn(opts,
					  { minwidth: 30, auto: true },
			          { name: 'ES',
				        tooltip: 'Total effort spent',
				        sort: agilefantUtils.comparators.effortSpentComparator
			          });
		  }

		  addTableColumn(opts,
				  { minwidth: 50, auto: true},
				  { name: 'Actions',
				    actionCell: true,
					tooltip: "Actions",
					sort: null
				  });
		  addTableColumn(opts,{ setMaxWidth: true, auto: false });
		  addTableColumn(opts,{ auto: false, setMaxWidth: true });
		  addTableColumn(opts,{ auto: false, setMaxWidth: true });
		  $.extend(opts,options);
			var ret = this.DynamicTable(opts);
			
			return ret;
		},
		taskTable: function(options) {
	      var opts = {
	          defaultSortColumn: 4,
	          captionText: "Tasks"
	      };
	      if(agilefantUtils.isTimesheetsEnabled()) {
	          opts.colCss = { ':eq(9)': { 'cursor': 'pointer' },
	                    ':lt(10)': { 'background-color': '#eee' },
	                    ':gt(9)': { 'background-color': '#fff', 'position': 'relative' }
	          };
	      } else {
	          opts.colCss = { ':eq(8)': { 'cursor': 'pointer' },
	                    ':lt(9)': { 'background-color': '#eee' },
	                    ':gt(8)': { 'background-color': '#fff', 'position': 'relative' }
	          };	    	  
	      }
	      addTableColumn(opts,
	    		  { minwidth: 16, auto: true },
	    		  { name: " ", 
	    			  sort: null
	    		  }
	      );
	      addTableColumn(opts,
	    		  { minwidth: 30, auto: true },
	              { name: "Themes",
	                tooltip: "Task themes",
	                sort: null
	              });
	      addTableColumn(opts,
	    		  { minwidth: 180, auto: true },
	              { name: 'Name',
	                tooltip: 'Task name',
	                sort: agilefantUtils.comparators.nameComparator
	              });
	      addTableColumn(opts,
	    		  { minwidth: 60, auto: true },
	              { name: 'State',
	                tooltip: 'Task state',
	                sort: null
	              });
	      addTableColumn(opts,
	    		  { minwidth: 50, auto: true },
	              { name: 'Priority',
	                tooltip: 'Task priority',
	                sort: agilefantUtils.comparators.storyPriorityAndStateComparator
	              });
	      addTableColumn(opts,
	    		  { minwidth: 50, auto: true },
	              { name: 'Responsibles',
	                tooltip: 'Task responsibles',
	                sort: null
	              });
	      addTableColumn(opts,
	    		  { minwidth: 30, auto: true },
	              { name: 'EL',
	                tooltip: 'Effort left',
	                sort: agilefantUtils.comparators.effortLeftComparator
	              });
	      addTableColumn(opts,
	    		  { minwidth: 30, auto: true },
	              { name: 'OE',
	                tooltip: 'Original estimate',
	                sort: agilefantUtils.comparators.originalEstimateComparator
	              });
		  if(agilefantUtils.isTimesheetsEnabled()) {
		      addTableColumn(opts,
		    		  { minwidth: 30, auto: true },
		              { name: 'ES',
		                tooltip: 'Total effort spent',
		                sort: agilefantUtils.comparators.effortSpentComparator
		              });
		  }
	      addTableColumn(opts,
	    		  { minwidth: 50, auto: true },
	              { name: 'Actions',
		            tooltip: "",
		            sort: null
		          });
	      addTableColumn(opts,{ auto: false, setMaxWidth: true });
	      addTableColumn(opts,{ auto: false, setMaxWidth: true });
	
	      $.extend(opts,options);
	      var ret = this.DynamicTable(opts);
	      return ret;
		},
    todoTable: function(options) {
      var opts = {
          defaultSortColumn: 0,
          captionText: "TODOs"
      };
      opts.colCss = { ':eq(2)': { 'cursor': 'pointer' },
                '*': { 'background-color': '#eee' }
      };          
      addTableColumn(opts,
          { minwidth: 380, auto: true },
              { name: 'Name',
                tooltip: 'TODO name',
                sort: agilefantUtils.comparators.nameComparator
              });
      addTableColumn(opts,
          { minwidth: 70, auto: true },
              { name: 'State',
                tooltip: 'TODO state',
                sort: null
              });
      addTableColumn(opts,
          { minwidth: 100, auto: true },
              { name: 'Actions',
              tooltip: "",
              sort: null
            });

      $.extend(opts,options);
      var ret = this.DynamicTable(opts);
      return ret;
  },
  spentEffortTable: function(options) {
    var opts = {
        defaultSortColumn: 0,
        captionText: "Spent Effort"
    };
    opts.colCss = {'*': { 'background-color': '#eee' }
    };          
    addTableColumn(opts,
        { minwidth: 100, auto: true },
            { name: 'Date',
              tooltip: 'Date',
              sort: null
            });
    addTableColumn(opts,
        { minwidth: 150, auto: true },
            { name: 'User',
              tooltip: 'User',
              sort: null
            });
    addTableColumn(opts,
        { minwidth: 50, auto: true },
            { name: 'Spent effort',
              tooltip: 'Spent effort',
              sort: null
            });
    addTableColumn(opts,
        { minwidth: 250, auto: true },
            { name: 'Comment',
              tooltip: 'Comment',
              sort: null
            });
    addTableColumn(opts,
        { minwidth: 100, auto: true },
            { name: 'Actions',
            tooltip: "",
            sort: null
          });

    $.extend(opts,options);
    var ret = this.DynamicTable(opts);
    return ret;
  },
  genericTable: function(options) {
    var ret = this.DynamicTable(options);
    return ret;
  }
	});
})(jQuery);

var TaskTabs = function(task, parentView) {
  var id = task.getId();
  if(!id) { // when creating new item etc.
    var tmp = new Date();
    id = tmp.getTime();
  }
  this.parentView = parentView;
  this.prefix = "taskTab-"+id;
  this.tabs = [];
  this.addFrame();
};
TaskTabs.prototype = {
    addFrame: function() {
      this.container = $('<div />').appendTo(this.parentView).width("100%").addClass("cellTabs");
      this.tabList = $('<ul />').addClass("tab-menu").appendTo(this.container).addClass("tabMenu");
      this.container.tabs();
    },
    setOnShow: function(cb) {
      this.container.bind('tabsshow', function(event, ui) {
        cb(ui.index);
      });
    },
    createTabId: function() {
      return this.prefix+"-"+this.tabs.length;
    },
    addTab: function(title) {
      var id = this.createTabId();
      var t = $('<div />').attr("id",id).appendTo(this.tabList);
      t.addClass("ui-tabs").addClass("tabData");
      this.tabs.push(t);
      this.container.tabs("add","#"+id, title);
      return t;
    }
};