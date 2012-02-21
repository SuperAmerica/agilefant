var DynamicTableRowButton = function DynamicTableRowButton(options, controller, model, parentView, width) {
    this.label = options.label;
    this.callback = options.callback;
    this.controller = controller;
    this.model = model;
    this.parentView = parentView;
    this.width = width;
};

DynamicTableRowButton.prototype = new CommonFragmentSubView();

/**
 * @private
 */
DynamicTableRowButton.prototype.getHTML = function() {
	var me = this;
	this.container = $('<div />').width(this.width + "px").appendTo(this.parentView.getElement());
	
	this.button = $('<div class="actionColumn"><div class="edit" style="width: ' + (this.width - 20) + 'px">' + this.label + '</div></div>');
	this.button.appendTo(this.container);
	this.button.click(function(event) { me._click(); });
	return "";
};

DynamicTableRowButton.prototype._click = function() {
	this.callback.call(this.controller, this.model, this.parentView);
};
