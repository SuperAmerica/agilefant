
/**
 * Transient container class for all products (access matrix).
 * @constructor
 * @base CommonModel
 */
var AccessListContainer = function AccessListContainer() {
  this.initialize();
  this.persistedClassName = "non.existent.ProductList";
  this.relations = {
    product: [],
    iteration: []
  };
  this.copiedFields = { };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Product":  "product",
      "fi.hut.soberit.agilefant.transfer.ProductTO":  "product",
      "fi.hut.soberit.agilefant.model.Iteration":  "iteration",
      "fi.hut.soberit.agilefant.transfer.IterationTO":  "iteration"
  };
};

AccessListContainer.prototype = new CommonModel();

/**
 * Internal function to set data
 * @see CommonModel#setData
 */
AccessListContainer.prototype._setData = function(newData) {
  if (newData.products) {
    this._updateRelations(ModelFactory.types.product, newData);
    this._updateRelations(ModelFactory.types.iteration, newData);
  }
};

/**
 * Reload all teams.
 */
AccessListContainer.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/retrieveAllProducts.action",
    {},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
  
  jQuery.getJSON(
    "ajax/retrieveAllSAIterations.action",
    {},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

AccessListContainer.prototype.getProducts = function() {
  return this.relations.product;
};

AccessListContainer.prototype.getIterations = function() {
  return this.relations.iteration;
};
