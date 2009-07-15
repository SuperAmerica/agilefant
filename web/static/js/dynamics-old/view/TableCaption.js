var DynamicTableCaptionButton = function(caption, options) {
	this.caption = caption;
	this.options = {
			text: "",
			hide: false,
			toggleWith: false,
			callback: function() {}
	};
	$.extend(this.options, options);
};
DynamicTableCaptionButton.prototype.show = function() {
	this.button.show();
};
DynamicTableCaptionButton.prototype.hide = function() {
	this.button.hide();
};
DynamicTableCaptionButton.prototype.toggle = function() {
	this.button.toggle();
};
DynamicTableCaptionButton.prototype.render = function() {
	this.button = $('<li />')
			.addClass(DynamicTableCssClasses.captionAction).appendTo(
					this.caption.getButtonContainer()).css("float", "right");
	var me = this;
	this.button.click(function() {
		if (me.options.toggleWith) {
			me.caption.getButton(me.options.toggleWith).toggle();
			me.hide();
		}
		me.options.callback();
	});
	this.element.html(this.options.text);
	if (this.options.hide) {
		this.element.hide();
	}
};


var DynamicTableCaption = function() {
	this.buttons = {};
};
DynamicTableCaption.prototype.render = function() {
	if(this.captionContainer) {
		this.captionContainer.remove();
	}
	if(!this.table) {
		return;
	}
	this.captionContainer = $('<div />').addClass(
			DynamicTableCssClasses.tableCaption).prependTo(this.table)
			.width(this.maxWidth + "%");
	this.captionText = $("<div />").css("float", "left").text(this.options.captionText)
			.appendTo(this.caption).width("30%");
	this.captionButtons = $('<ul />').addClass(
			DynamicTableCssClasses.captionActions).appendTo(this.captionContainer)
			.css("float", "right").width("68%");
	for(var i = 0; i < this.buttons.lenth; i++) {
		this.buttons[i].render();
	}
};
DynamicTableCaption.prototype.setTable = function(table) {
	this.table = table;
};
DynamicTableCaption.prototype.show = function() {
	this.captionContainer.show();
};
DynamicTableCaption.prototype.hide = function() {
	this.captionContainer.show();
};
DynamicTableCaption.prototype.getButtonContainer = function() {
	return this.captionButtons;
};
DynamicTableCaption.prototype.createButton = function(name, options) {
	this.buttons[name] = new DynamicTableCaptionButton(this,options);
};
DynamicTableCaption.prototype.getButton = function(name) {
	return this.buttons[name];
};



