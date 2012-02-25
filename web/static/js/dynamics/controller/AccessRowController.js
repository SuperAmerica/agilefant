/**
 * Access row controller.
 * 
 * @constructor
 * @base CommonController
 * @param {Integer} id Team id. 
 */
var AccessRowController = function AccessRowController(model, view, parentController) {
  this.model = model;
  this.view = view;
  this.parentController = parentController;
  this.init();
};
AccessRowController.prototype = new CommonController();


AccessRowController.prototype.handleModelEvents = function(event) {
  if(this.parentController) {
    this.parentController.handleModelEvents(event);
  }
};