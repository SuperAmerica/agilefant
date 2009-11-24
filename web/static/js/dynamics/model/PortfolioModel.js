/**
 * Transient container class for portfolio projects
 * @constructor
 * @base CommonModel
 */
var PortfolioModel = function PortfolioModel() {
  this.initialize();
  this.persistedClassName = "non.existent.PortfolioModel";
  this.relations = { };
  this.copiedFields = {
    "timeSpanInDays": "timeSpanInDays"
  };
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
    this.unrankedProjects = [];
    this.rankedProjects = [];
    for (var i = 0, len = newData.rankedProjects.length; i < len; i++) {
      this.rankedProjects.push(ModelFactory.updateObject(newData.rankedProjects[i]));
    }
    for (var i = 0, len = newData.unrankedProjects.length; i < len; i++) {
      this.unrankedProjects.push(ModelFactory.updateObject(newData.unrankedProjects[i]));
    }
  }
};

PortfolioModel.prototype.reload = function() {
  var me = this;
  jQuery.ajax({
    type: "POST",
    dataType: "json",
    url: "ajax/projectPortfolioData.action",
    async: true,
    success: function(data,status) {
      me.setData(data);
      //me.callListeners(new DynamicsEvents.EditEvent(me));
      me.callListeners(new DynamicsEvents.RelationUpdatedEvent(me));
    },
    error: function(xhr, status, error) {
      var msg = MessageDisplay.ErrorMessage("Error loading portfolio.", xhr);
    }
  });  
};

PortfolioModel.prototype.getRankedProjects = function() {
  return this.rankedProjects;
};

PortfolioModel.prototype.getUnrankedProjects = function() {
  return this.unrankedProjects;
};
PortfolioModel.prototype.getTimeSpanInDays = function() {
  return this.currentData.timeSpanInDays;
};
