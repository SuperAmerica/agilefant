/**
 * Transient container class for portfolio projects
 * @constructor
 * @base CommonModel
 */
var PortfolioModel = function PortfolioModel() {
  this.initialize();
  this.persistedClassName = "non.existent.PortfolioModel";
  this.relations = {
    project: []
  };
  this.copiedFields = { };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Project":  "project"
  };
};

PortfolioModel.prototype = new CommonModel();

/**
 * Internal function to set data
 * @see CommonModel#setData
 */
PortfolioModel.prototype._setData = function(newData) {
  if (newData) {
    this._updateRelations(ModelFactory.types.project, newData);
  }
};

PortfolioModel.prototype.getProjects = function() {
  return this.relations.project;
};

