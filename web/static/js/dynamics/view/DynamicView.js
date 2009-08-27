/**
 * Common base class for all views and view parts.
 * A view requires a controller to which it will pass user
 * interactions and a model from which it will fetch data to be
 * displayed.
 * @constructor
 */
var DynamicView = function() {
	
};

DynamicView.prototype = new ViewPart();
DynamicView.instanceCounter = 0;

/**
 * Initialization method for views. Sets controller, model
 * and parent view. Attaches event listeners for the given model.
 * 
 * @param (CommonController) controller
 * @param (CommonModel) model
 * @param parent Either jQuery object or instance of DynamicView
 */
DynamicView.prototype.init = function(controller, model, parent) {
  this.initWithoutEvents(controller, model, parent);
  var me = this;
	this.listener = function(event) {
	  if (event instanceof DynamicsEvents.EditEvent) {
	    me.onEdit(event);
	  } else if (event instanceof DynamicsEvents.DeleteEvent) {
	    me.onDelete(event);
	  } else if(event instanceof DynamicsEvents.RelationUpdatedEvent) {
	    me.onRelationUpdate(event);
	  }
	};
	this.model.addListener(this.listener);
	this.subViews = {};
};

DynamicView.prototype.initWithoutEvents = function(controller, model, parent) {
  this.controller = controller;
  this.model = model;
  this.parentView = null;
  this.parentElement = null;
  //this.element = null;
  this.viewId = DynamicView.instanceCounter++;
  if(parent instanceof ViewPart) {
    this.parentView = parent;
    //this.parentView.addSubView(this.viewId, this);
    this.parentElement = parent.getElement();
  } else {
    this.parentElement = parent;
  }
  if(this.element) {
    this._setViewId();
  }
};

DynamicView.prototype.destroy = function() {
  if(this.listener) {
    this.model.removeListener(this.listener);
  }
};
DynamicView.prototype._setViewId = function() {
  this.element.attr("id",this.getViewId());
};
DynamicView.prototype.getViewId = function() {
  return "dynamicView_"+this.viewId;
};
DynamicView.prototype.getModel = function() {
	return this.model;
};
DynamicView.prototype.getController = function() {
	return this.controller;
};
DynamicView.prototype.getParentElement = function() {
	return this.parentElement;
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
	this.parentElement = parentView.getElement();
};


DynamicView.prototype.onEdit = function(event) {
	throw new Error("Abstract method.");
};

DynamicView.prototype.onDelete = function(event) {
  throw new Error("Abstract method.");
};

DynamicView.prototype.onRelationUpdate = function(event) {
  //throw new Error("Abstract method.");
};
