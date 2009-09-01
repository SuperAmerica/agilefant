
/**
 * Model class for products.
 * @constructor
 * @base BacklogModel
 * @see BacklogModel#initializeBacklogModel
 */
var ProductModel = function() {
  this.initializeBacklogModel();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Product";
  this.relations = {
    project: [],
    iteration: [],
    story: []
  };
  this.copiedFields = {
    "name":   "name",
    "description": "description"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Project":       "project",
      "fi.hut.soberit.agilefant.model.Iteration":     "iteration",
      "fi.hut.soberit.agilefant.model.Story":         "story"
  };
};

ProductModel.prototype = new BacklogModel();


/**
 * Internal function to set data
 * @see CommonModel#setData
 */
ProductModel.prototype._setData = function(newData) {
  var data = {};
  
  // Set the id
  this.id = newData.id;
  
  // Copy fields
  this._copyFields(newData);
  
  // Set stories
  if (newData.stories) {
    this._updateRelations(ModelFactory.types.story, newData.stories);
  }
  
  if (newData.projects) {
    this._updateRelations("project", newData.projects);
  }
//  
//  if (newData.iterations) {
//    this._updateRelations("iteration", newData.iterations);
//  }
  
};

ProductModel.prototype._saveData = function(id, changedData) {
  var me = this;
  
  var url = "ajax/storeProduct.action";
  var data = this.serializeFields("product", changedData);
  data.productId = id;
 
  jQuery.ajax({
    type: "POST",
    url: url,
    async: true,
    cache: false,
    data: data,
    dataType: "json",
    success: function(data, status) {
      var msg = new MessageDisplay.OkMessage("Product saved successfully");
      me.setData(data);
    },
    error: function(xhr, status, error) {
      var msg = new MessageDisplay.ErrorMessage("Error saving product", xhr);
      me.rollback();
    }
  });
};

ProductModel.prototype.reload = function() {
  var me = this;
  jQuery.getJSON(
    "ajax/retrieveProduct.action",
    {productId: me.getId()},
    function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.EditEvent(me));
    }
  );
};

ProductModel.prototype.addIteration = function(iteration) {
  this.addRelation(iteration);
  this.relationEvents();
};

ProductModel.prototype.addProject = function(project) {
  this.addRelation(project);
  this.relationEvents();
};

/* GETTERS */

ProductModel.prototype.getDescription = function() {
  return this.currentData.description;
};
ProductModel.prototype.setDescription = function(description) {
  this.currentData.description = description;
  this._commitIfNotInTransaction();
};

ProductModel.prototype.getIterations = function() {
  return this.relations.iteration;
};

ProductModel.prototype.getName = function() {
  return this.currentData.name;
};
ProductModel.prototype.setName = function(name) {
  this.currentData.name = name;
  this._commitIfNotInTransaction();
};

ProductModel.prototype.getProjects = function() {
  return this.relations.project;
};

ProductModel.prototype.getStories = function() {
  return this.relations.story;
};