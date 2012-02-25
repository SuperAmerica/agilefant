
/**
 * Transient container class for all products (access matrix).
 * @constructor
 * @base CommonModel
 */
var AccessListContainer = function AccessListContainer() {
  this.initialize();
  this.persistedClassName = "non.existent.ProductList";
  this.relations = {
    product: []
  };
  this.copiedFields = { };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Product":  "product",
      "fi.hut.soberit.agilefant.transfer.ProductTO":  "product"
  };
};

AccessListContainer.prototype = new CommonModel();

/**
 * Internal function to set data
 * @see CommonModel#setData
 */
AccessListContainer.prototype._setData = function(newData) {
  if (newData) {
    this._updateRelations(ModelFactory.types.product, newData);
  }
};

/**
 * Reload all teams.
 */
TeamListContainer.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/retrieveAllProducts.action",
    {},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

TeamListContainer.prototype.getProducts = function() {
  return this.relations.product;
};
