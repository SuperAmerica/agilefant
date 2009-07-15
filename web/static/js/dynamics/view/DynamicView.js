var DynamicView = function() {
	
};
//common base view class
DynamicView.prototype.init = function(controller, model) {
	this.controller = controller;
	this.model = model;
	this.parentView = null;
	var editListener = function(event) {
		this.onEdit(event);
	};
	var deleteListener = function(event) {
		this.onDelete(event);
	};
	this.model.addEditListener(editListener);
	this.model.addDeleteListener(deleteListener);
	this.subViews = {};
};
DynamicView.prototype.getModel = function() {
	return this.model;
};
DynamicView.prototype.getController = function() {
	return this.controller;
};
DynamicView.prototype.addSubView = function(name, view) {
	this.subViews[name] = view;
	view.setParentView(this);
};
DynamicView.prototype.setParentView = function(parentView) {
	this.parentView = parentView;
};

//event handlers
DynamicView.prototype.onEdit = function(event) {
	throw "Abstract method.";
};
DynamicView.prototype.onDelete = function(event) {
	throw "Abstract method.";
};
DynamicView.prototype.onAdd = function(event) {
	throw "Abstract method.";
};