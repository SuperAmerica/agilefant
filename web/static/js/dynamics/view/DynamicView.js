/**
 * Common base class for all views and view parts.
 * A view requires a controller to which it will pass user
 * interactions and a model from which it will fetch data to be
 * displayed.
 */
var DynamicView = function() {
	
};

/**
 * Initialization method for views. Sets controller, model
 * and parent view. Attaches event listeners for the given model.
 * 
 * @param (CommonController) controller
 * @param (CommonModel) model
 * @param parent Either jQuery object or instance of DynamicView
 */
DynamicView.prototype.init = function(controller, model, parent) {
	this.controller = controller;
	this.model = model;
	this.parentView = null;
	this.parentElement = null;
	this.element = null;
	this.viewId = DynamicView.instanceCounter++;
	if(parent instanceof DynamicView) {
		this.parentView = parent;
		this.parentView.addSubView(this.viewId, this);
		this.parentElement = parent.getElement();
	} else {
		this.parentElement = parent;
	}
	this.editListener = function(event) {
		this.onEdit(event);
	};
	this.deleteListener = function(event) {
		this.onDelete(event);
	};
	this.model.addListener(this.editListener);
	this.model.addListener(this.deleteListener);
	this.subViews = {};
};

DynamicView.instanceCounter = 0;

DynamicView.prototype.getModel = function() {
	return this.model;
};
DynamicView.prototype.getController = function() {
	return this.controller;
};
DynamicView.prototype.getParentElement = function() {
	return this.parentElement;
};
DynamicView.prototype.getElement = function() {
	return this.element;
};
DynamicView.prototype.getParentView = function() {
	return this.parentView;
};
DynamicView.prototype.addSubView = function(name, view) {
	this.subViews[name] = view;
	view.setParentView(this);
};
DynamicView.prototype.removeSubView = function(name) {
	delete this.subViews[name];
};
DynamicView.prototype.setParentView = function(parentView) {
	this.parentView = parentView;
};


DynamicView.prototype.onEdit = function(event) {
	throw "Abstract method.";
};
DynamicView.prototype.onDelete = function(event) {
	throw "Abstract method.";
};