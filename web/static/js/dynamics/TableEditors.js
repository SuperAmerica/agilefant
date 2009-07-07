var DynamicsEditors = {};
DynamicsEditors.AbstractEditor = function() {

};
DynamicsEditors.AbstractEditor.prototype.getValue = function() {
	return this.field.val();
};
DynamicsEditors.AbstractEditor.prototype._cancel = function() {
	this.cell.cancelEdit();
};
DynamicsEditors.AbstractEditor.prototype._store = function() {
	this.cell.saveEdit();
};
DynamicsEditors.AbstractEditor.prototype.focus = function() {
	if (this.field) {
		this.field.focus();
		return true;
	}
	return false;
};
DynamicsEditors.AbstractEditor.prototype._storeRow = function() {
	var opt = this.cell.getRow().options;
	if (opt && typeof opt.saveCallback == "function") {
		opt.saveCallback();
	}
};
DynamicsEditors.AbstractEditor.prototype._handleKeyEvent = function() {
	if (keyevent.keyCode == 27 && this.autoClose) {
		keyevent.stopPropagation();
		this._cancel();
	} else if (keyevent.keyCode == 13 && this.autoClose) {
		keyevent.stopPropagation();
		this._store();
	} else if (keyevent.keyCode == 13 && !this.autoClose) {
		keyevent.stopPropagation();
		this._storeRow();
	}
};


/** EMPTY EDIT **/
DynamicsEditors.EmptyEdit = function(cell) {
	this.cell = cell;
	if (typeof cell.options.action == "function") {
		cell.options.action();
	}
};
DynamicsEditors.EmptyEdit.prototype = {
		_mouseClick : function(event) {
	return false;
},
remove : function() {
},
isValid : function() {
	return true;
},
getValue : function() {
	return "";
}
};

/** WYSIWYG EDIT **/

DynamicsEditors.WysiwygEdit = function(cell, autoClose) {
	this.cell = cell;
	var value = this.cell.options.get();
	if (!value) {
		value = "";
	}
	this.field = $('<textarea>' + value + '</textarea>').appendTo(
			this.cell.getElement()).width("80%");
	setUpWysiwyg(this.field);
	this.editor = this.cell.getElement().find(".wysiwyg");
	this.editorBody = this.editor.find("iframe").contents();
	this.editorBody.focus();
	if (autoClose === true) {
		var me = this;
		this.mouseEvent = function(event) {
			me._mouseClick(event);
		};
		$(document.body).click(this.mouseEvent);
		this.cancelCb = function(event) {
			if (event.keyCode == 27) { //ESC
				me._cancel();
			}
		};
		this.editorBody.keydown(this.cancelCb);

	}
};
DynamicsEditors.WysiwygEdit.prototype = new DynamicsEditors.AbstractEditor();
DynamicsEditors.WysiwygEdit.prototype._mouseClick  = function(event) {
	if (event.target) {
		var target = $(event.target);
		var parent = target.closest("div.wysiwyg");
		var wysiwyg = this.field.prev("div.wysiwyg");
		if (parent.length > 0 && parent.get(0) == wysiwyg.get(0)) {
			return false;
		} else {
			this._store();
			$(document.body).unbind("click", this.mouseEvent);
		}
	}
};
DynamicsEditors.WysiwygEdit.prototype.remove = function() {
	this.editor.remove();
	this.field.remove();
	$(document.body).unbind("click", this.mouseEvent);
};
DynamicsEditors.WysiwygEdit.prototype.isValid = function() {
	return true;
};


/** TEXT EDIT **/

DynamicsEditors.TextEdit = function(cell, autoClose) {
	this.cell = cell;
	this.autoClose = autoClose;
	this.field = $('<input type="text"/>').width("80%").appendTo(
			this.cell.getElement()).focus();
	this.field.val(this.cell.options.get());
	var me = this;
	var key_cb = function(keyevent) {
		me._handleKeyEvent(keyevent);
	};
	this.field.keydown(key_cb);
	if (autoClose === true) {
		var blur_cb = function() {
			me._store();
		};
		this.field.blur(blur_cb);
		this.field.focus();
	}
};

DynamicsEditors.TextEdit.prototype = new DynamicsEditors.AbstractEditor();
DynamicsEditors.TextEdit.prototype.isValid = function() {
	if (this.cell.options.required && this.field.val().length === 0) {
		this.field.addClass("invalidValue");
		if (!this.errorMsg) {
			this.errorMsg = commonView.requiredFieldError(this.cell
					.getElement());
			this.cell.getElement().addClass('cellError');
		}
		return false;
	}
	this.field.removeClass("invalidValue");
	if (this.errorMsg) {
		this.errorMsg.remove();
		this.errorMsg = null;
		this.cell.getElement().removeClass('cellError');
	}
	return true;
};
DynamicsEditors.TextEdit.prototype.remove = function() {
	if (this.errorMsg) {
		this.errorMsg.remove();
	}
	this.field.remove();
};


/** EFFORT EDIT **/
DynamicsEditors.EffortEdit = function(cell, autoClose) {
	this.cell = cell;
	this.autoClose = autoClose;
	this.field = $('<input type="text"/>').width('80%').appendTo(
			this.cell.getElement()).focus();
	var val = this.cell.options.get();
	if (val) {
		val = agilefantParsers.exactEstimateToString(val, true);
	}
	this.field.val(val);
	var me = this;
	var key_cb = function(keyevent) {
		me._handleKeyEvent(keyevent);
	};
	this.field.keydown(key_cb);
	if (autoClose === true) {
		var blur_cb = function() {
			me._store();
		};
		this.field.blur(blur_cb);
		this.field.focus();
	}
};

DynamicsEditors.EffortEdit.prototype = new DynamicsEditors.AbstractEditor();
DynamicsEditors.EffortEdit.prototype.isValid = function() {
	if (agilefantParsers.isHourEntryString(this.field.val())) {
		this.field.removeClass("invalidValue");
		if (this.errorMsg) {
			this.errorMsg.remove();
			this.errorMsg = null;
			this.cell.getElement().removeClass('cellError');
		}
		return true;
	} else {
		if (!this.errorMsg) {
			this.errorMsg = commonView.effortError(this.cell.getElement());
		}
		this.field.addClass("invalidValue");
		this.cell.getElement().addClass('cellError');
		return false;
	}
};
DynamicsEditors.EffortEdit.prototype.remove = function() {
	if (this.errorMsg) {
		this.errorMsg.remove();
	}
	this.field.remove();
};


/** STORY POINT EDIT **/
DynamicsEditors.StoryPointEdit = function(cell, autoClose) {
	this.cell = cell;
	this.autoClose = autoClose;
	this.field = $('<input type="text"/>').width('80%').appendTo(
			this.cell.getElement()).focus();
	var val = this.cell.options.get();
	this.field.val(val);
	var me = this;
	var key_cb = function(keyevent) {
		me._handleKeyEvent(keyevent);
	};
	this.field.keydown(key_cb);
	if (autoClose === true) {
		var blur_cb = function() {
			me._store();
		};
		this.field.blur(blur_cb);
		this.field.focus();
	}
};

DynamicsEditors.StoryPointEdit.prototype = new DynamicsEditors.AbstractEditor();
DynamicsEditors.StoryPointEdit.prototype.isValid = function() {
	if (agilefantParsers.isStoryPointString(this.field.val())) {
		this.field.removeClass("invalidValue");
		if (this.errorMsg) {
			this.errorMsg.remove();
			this.errorMsg = null;
			this.cell.getElement().removeClass('cellError');
		}
		return true;
	} else {
		if (!this.errorMsg) {
			this.errorMsg = commonView.storyPointError(this.cell
					.getElement());
		}
		this.field.addClass("invalidValue");
		this.cell.getElement().addClass('cellError');
		return false;
	}
};
DynamicsEditors.StoryPointEdit.prototype.remove = function() {
	if (this.errorMsg) {
		this.errorMsg.remove();
	}
	this.field.remove();
};


/** DATE EDIT **/
DynamicsEditors.DateEdit = function(cell, autoClose) {
	this.cell = cell;
	this.autoClose = autoClose;
	this.field = $('<input type="text"/>').width('80%').appendTo(
			this.cell.getElement()).focus();
	var val = this.cell.options.get();
	if (val) {
		val = agilefantUtils.dateToString(val, true);
	}
	this.field.val(val);
	var me = this;
	var key_cb = function(keyevent) {
		me._handleKeyEvent(keyevent);
	};
	this.field.keydown(key_cb);
	if (autoClose === true) {
		var blur_cb = function() {
			me._store();
		};
		this.field.blur(blur_cb);
		this.field.focus();
	}
};
DynamicsEditors.DateEdit.prototype = new DynamicsEditors.AbstractEditor();
DynamicsEditors.DateEdit.prototype.isValid = function() {
	if (agilefantUtils.isDateString(this.field.val())) {
		this.field.removeClass("invalidValue");
		if (this.errorMsg) {
			this.errorMsg.remove();
			this.errorMsg = null;
			this.cell.getElement().removeClass('cellError');
		}
		return true;
	} else {
		if (!this.errorMsg) {
			this.errorMsg = commonView.dateError(this.cell.getElement());
		}
		this.field.addClass("invalidValue");
		this.cell.getElement().addClass('cellError');
		return false;
	}
};
DynamicsEditors.DateEdit.prototype.remove = function() {
	if (this.errorMsg) {
		this.errorMsg.remove();
	}
	this.field.remove();
};

/** SELECT EDIT **/
DynamicsEditors.SelectEdit = function(cell, items, autoClose) {
	var me = this;
	this.cell = cell;
	this.autoClose = autoClose;
	if (typeof items === "function") {
		items = items();
	}
	this.field = $('<select/>').css('width', '100%').appendTo(
			this.cell.getElement()).focus();
	$.each(items, function(i, v) {
		$('<option/>').attr('value', i).text(v).appendTo(me.field);
	});
	var val = this.cell.options.get();
	this.field.val(val);
	var key_cb = function(keyevent) {
		me._handleKeyEvent(keyevent);
	};
	this.field.keydown(key_cb);
	if (autoClose === true) {
		var blur_cb = function() {
			me._cancel();
		};
		var change_cb = function() {
			me._store();
		};
		this.field.blur(blur_cb);
		this.field.change(change_cb);
		this.field.focus();
	}
};
DynamicsEditors.SelectEdit.prototype = new DynamicsEditors.AbstractEditor();
DynamicsEditors.SelectEdit.prototype.isValid = function() {
	return true;
};
DynamicsEditors.SelectEdit.prototype.remove = function() {
	this.field.remove();
};
