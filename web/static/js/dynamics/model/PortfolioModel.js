/**
 * Transient container class for portfolio projects
 * @constructor
 * @base CommonModel
 */
var PortfolioModel = function PortfolioModel() {
  this.initialize();
  this.persistedClassName = "non.existent.PortfolioModel";
  this.relations = { };
  this.copiedFields = { };
  this.unrankedProjects = [];
  this.rankedProjects = [];
  this.classNameToRelation = {};
};

PortfolioModel.prototype = new CommonModel();

/**
 * Internal function to set data
 * @see CommonModel#setData
 */
PortfolioModel.prototype._setData = function(newData) {
  if (newData) {
    for (var i = 0, len = newData.rankedProjects.length; i < len; i++) {
      this.rankedProjects.push(ModelFactory.updateObject(newData.rankedProjects[i]));
    }
    for (var i = 0, len = newData.unrankedProjects.length; i < len; i++) {
      this.unrankedProjects.push(ModelFactory.updateObject(newData.unrankedProjects[i]));
    }
  }
};

PortfolioModel.prototype.getRankedProjects = function() {
  return this.rankedProjects;
};

PortfolioModel.prototype.getUnrankedProjects = function() {
  return this.unrankedProjects;
};
