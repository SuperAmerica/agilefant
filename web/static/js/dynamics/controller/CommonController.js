/**
 * Base class for all controllers
 * 
 * @constructor
 */
var CommonController = function CommonController() {

};

CommonController.prototype.init = function() {
  this.childControllers = {};
  this.autohideCells = [];
  this.attachModelListener();
};

CommonController.prototype.attachModelListener = function() {
  if(this.model) {
    var me = this;
    this.modelListener = function(event) {
      me.handleModelEvents(event);
    };
    this.model.addListener(this.modelListener);
  }
}

/**
 * Add a child controller to this controller.
 * 
 * @param {String}
 *          type Key for child controller type.
 * @param {CommonController}
 *          controller Child controller to be added.
 */
CommonController.prototype.addChildController = function(type, controller) {
  if (!this.childControllers[type]) {
    this.childControllers[type] = [];
  }
  this.childControllers[type].push(controller);
};

/**
 * Remove child controller from this controller.
 * 
 * @param {String}
 *          type Key for child controller type.
 * @param {CommonController}
 *          controller Child controller to be removed.
 */
CommonController.prototype.removeChildController = function(type, controller) {
  if (this.childControllers[type]) {
    ArrayUtils.remove(this.childControllers[type], controller);
  }
};

/**
 * Call given method for all child controllers of specific type.
 * 
 * @param {String}
 *          type Key for child controller type.
 * @param {Function}
 *          invocationTarget Controller method to be called.
 */
CommonController.prototype.callChildcontrollers = function(type,
    invocationTarget) {
  if (this.childControllers[type]) {
    for ( var i = 0; i < this.childControllers[type].length; i++) {
      invocationTarget.call(this.childControllers[type][i]);
    }
  }
};


/**
 * Opens the row for editing
 */
CommonController.prototype.openRowEdit = function() {
  for (var i = 0; i < this.autohideCells.length; i++) {
    var num = this.autohideCells[i];
    var cell = this.view.getCell(num);
    if (cell) {
      cell.show();
    }
  }
  this.view.openFullEdit();
};


/**
 * Closes the edited row.
 */
CommonController.prototype.closeRowEdit = function() {
  if(!this.model.getId()) {
    this.view.remove();
    this.model.removeListener(this.modelListener);
    return;
  }
  for (var i = 0; i < this.autohideCells.length; i++) {
    var num = this.autohideCells[i];
    var cell = this.view.getCell(num);
    if (cell) {
      cell.hide();
    }
  }
  this.view.closeRowEdit();
};

CommonController.prototype.handleModelEvents = function(event) {
  
};


