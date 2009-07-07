var DynamicsView = function() {
	
};
//common base view class
DynamicsView.prototype.init = function(controller, model) {
	this.controller = controller;
	this.model = model;
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
DynamicsView.prototype.getModel = function() {
	return this.model;
};
DynamicsView.prototype.getController = function() {
	return this.controller;
};
DynamicsView.prototype.addSubView = function(name, view) {
	this.subViews[name] = view;
};

//event handlers
DynamicsView.prototype.onEdit = function(event) {
	throw "Abstract method.";
};
DynamicsView.prototype.onDelete = function(event) {
	throw "Abstract method.";
};
DynamicsView.prototype.onAdd = function(event) {
	throw "Abstract method.";
};