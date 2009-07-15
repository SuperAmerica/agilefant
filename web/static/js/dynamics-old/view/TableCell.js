/** TABLE CELL **/
var DynamicTableCell = function(row, cellno, options) {
	this.row = row;
	this.cellno = cellno;
	this.options = {};
	$.extend(this.options, options);
	this.cell = $("<div />").appendTo(this.row.getElement()).addClass(
			DynamicTableCssClasses.tableCell);
	this.content = $("<span/>").appendTo(this.cell);
	this.value = null;
	this.field = null;
	var a = row.getTable().getColWidth(cellno);
	if (a) {
		if (a.minwidth) {
			this.cell.css('min-width', a.minwidth + 'px');
		}
		if (a.width) {
			this.cell.css('width', a.width + '%');
		}
		if (a.setMaxWidth) {
			this.cell.css("clear", "left");
		}
	}
	var cssClass = row.getTable().getColStyle(cellno);
	if (cssClass) {
		this.cell.addClass(cssClass);
	}
	var me = this;
	var dblclick_cb = function() {
		me.openEdit();
	};
	if (this.options.type && this.options.type != "empty") {
		this.cell.dblclick(dblclick_cb);
		this.updateTooltip();
	}
};

DynamicTableCell.prototype = {
	setActionCell : function(options) {
		this.actionObj = new TableRowActions(this, this.row, options);
		this.isActionCell = true;
	},
	activateSortHandle : function() {
		this.cell.addClass("dynamictable-sorthandle").addClass("dragHandle");
	},
	setDragHandle : function() {
		this.cell.addClass("dragHandle");
	},
	updateTooltip : function() {
		if (this.editorOpen) {
			this.cell.removeAttr("title");
		} else {
			this.cell.attr("title", "Double-click the cell to edit it.");
		}
	},
	render : function() {
		if (typeof this.options.get === "function") {
			var value = this.options.get();
			if (typeof this.options.htmlDecorator === "function") {
				value = this.options.htmlDecorator(value);
			} else if (typeof this.options.decorator === "function") {
				value = this.options.decorator(value);
			}
			this.setValue(value);
		}
	},
	setValue : function(newValue) {
		this.value = newValue;
		this.content.html(newValue);
	},
	getElement : function() {
		return this.cell;
	},
	isValid : function() {
		if (!this.editor) {
			return true;
		}
		return this.editor.isValid();
	},
	getRow : function() {
		return this.row;
	},
	saveEdit : function() {
		if (!this.editorOpen) {
			return;
		}
		if (!this.editor.isValid()) {
			return false;
		}
		var newValue = this.editor.getValue();
		this.editor.remove();
		this.editor = null;
		this.content.show();
		this.removeButtons();
		if (this.isActionCell) {
			this.actionObj.getElement().show();
		}
		this.editorOpen = false;
		this.updateTooltip();
		if (newValue != this.options.get()) {
			this.options.set(newValue);
		}
	},
	cancelEdit : function() {
		if (!this.editorOpen) {
			return;
		}
		this.content.show();
		this.removeButtons();
		if (this.editor) {
			this.editor.remove();
		}
		this.editorOpen = false;
		this.updateTooltip();
		if (this.isActionCell) {
			this.actionObj.getElement().show();
		}
		this.editor = null;
	},
	setEditFocus : function() {
		if (this.editorOpen && this.editor) {
			return this.editor.focus();
		}
	},
	/* 
	 * when no auto close is set no mouse or keyboard
	 * events will be registered to close and save the 
	 * editor. Instead save edit must be manually called.
	 * See DynamicTableRow.openEdit
	 */
	openEdit : function(noAutoClose) {
		var me = this;
		if (typeof this.options.onEdit == "function") {
			if (!this.options.onEdit(noAutoClose)) {
				return;
			}
		}
		if (this.options.type == "user") {
			if (noAutoClose) {
				return;
			}
			var uc = new AgilefantUserChooser( {
				selectThese : agilefantUtils.objectToIdArray(me.options
						.getEdit()),
				selectCallback : function(chooser) {
					var users = chooser.getSelected(true);
					me.options.set(users);
					me.render();
				},
				backlogId : this.options.backlogId,
				storyId : this.options.storyId
			});
			uc.init();
			return;
		} 
		var autoClose = true;
		if (noAutoClose) {
			autoClose = false;
		}
		if (this.options.type && !this.editorOpen) {
			this.editorOpen = true;
			this.content.hide();
			if (this.isActionCell) {
				this.actionObj.getElement().hide();
			}
			if (this.options.type == "text") {
				this.editor = new DynamicsEditors.TextEdit(this, autoClose);
			} else if (this.options.type == "wysiwyg") {
				this.editor = new DynamicsEditors.WysiwygEdit(this, autoClose);
			} else if (this.options.type == "effort") {
				this.editor = new DynamicsEditors.EffortEdit(this, autoClose);
			} else if (this.options.type == "storyPoint") {
				this.editor = new DynamicsEditors.StoryPointEdit(this, autoClose);
			} else if (this.options.type == "select") {
				this.editor = new DynamicsEditors.SelectEdit(this, this.options.items,
						autoClose);
			} else if (this.options.type == "empty") {
				this.editor = new DynamicsEditors.EmptyEdit(this);
			} else if (this.options.type == "date") {
				this.editor = new DynamicsEditors.DateEdit(this, autoClose);
			}
			if (!autoClose && this.options.buttons) {
				me.addedButtons = [];
				$.each(this.options.buttons, function(button, opts) {
					var nbutton = $("<button />").appendTo(me.cell).text(
							opts.text).click(opts.action).addClass(
							"dynamicButton");
					me.addedButtons.push(nbutton);
				});
			}
		}
		this.updateTooltip();
	},
	removeButtons : function() {
		if (this.addedButtons) {
			$.each(this.addedButtons, function(k, v) {
				v.remove();
			});
		}
	}
};
