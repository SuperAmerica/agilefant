var commonEdit = {
	getValue : function() {
		return this.field.val();
	},
	_cancel : function() {
		this.cell.cancelEdit();
	},
	_store : function() {
		this.cell.saveEdit();
	},
	focus : function() {
		if (this.field) {
			this.field.focus();
			return true;
		}
		return false;
	},
	_storeRow : function() {
		var opt = this.cell.getRow().options;
		if (opt && typeof opt.saveCallback == "function") {
			opt.saveCallback();
		}
	},
	_handleKeyEvent : function(keyevent) {
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
	}
};

/** EMPTY EDIT **/
var EmptyEdit = function(cell) {
	this.cell = cell;
	if (typeof cell.options.action == "function") {
		cell.options.action();
	}
};
EmptyEdit.prototype = {
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

var WysiwygEdit = function(cell, autoClose) {
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
WysiwygEdit.prototype = {
	_mouseClick : function(event) {
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
	},
	remove : function() {
		this.editor.remove();
		this.field.remove();
		$(document.body).unbind("click", this.mouseEvent);
	},
	isValid : function() {
		return true;
	}
};
$.extend(WysiwygEdit.prototype, commonEdit);

/** TEXT EDIT **/

var TextEdit = function(cell, autoClose) {
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
TextEdit.prototype = {
	isValid : function() {
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
	},
	remove : function() {
		if (this.errorMsg) {
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
EffortEdit.prototype = {
	isValid : function() {
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
	},
	remove : function() {
		if (this.errorMsg) {
			this.errorMsg.remove();
		}
		this.field.remove();
	}
};
$.extend(EffortEdit.prototype, commonEdit);

/** STORY POINT EDIT **/
var StoryPointEdit = function(cell, autoClose) {
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
StoryPointEdit.prototype = {
	isValid : function() {
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
	},
	remove : function() {
		if (this.errorMsg) {
			this.errorMsg.remove();
		}
		this.field.remove();
	}
};
$.extend(StoryPointEdit.prototype, commonEdit);

/** DATE EDIT **/
var DateEdit = function(cell, autoClose) {
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
DateEdit.prototype = {
	isValid : function() {
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
	},
	remove : function() {
		if (this.errorMsg) {
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
SelectEdit.prototype = {
	isValid : function() {
		return true;
	},
	remove : function() {
		this.field.remove();
	}
};
$.extend(SelectEdit.prototype, commonEdit);