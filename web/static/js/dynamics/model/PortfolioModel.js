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
  this.isReloading = false;
};

PortfolioModel.prototype = new CommonModel();

PortfolioModel.Validators = {
    _parseDate: function(original) {
      if (typeof original === "number") {
        return new Date(original);
      }
      return Date.fromString(original);
    },
    dateValidator: function(model) {
      var start = PortfolioModel.Validators._parseDate(model.getStartDate());
      var end   = PortfolioModel.Validators._parseDate(model.getEndDate());
      
      if (start.after(end)) {
        throw "Start date must be before end date";
      }
    }
  };

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
    for (i = 0, len = newData.unrankedProjects.length; i < len; i++) {
      this.unrankedProjects.push(ModelFactory.updateObject(newData.unrankedProjects[i]));
    }
  }
};

PortfolioModel.prototype.reload = function() {
  if (this.isReloading) { return; }
  this.isReloading = true;
  var me = this;
  jQuery.ajax({
    type: "POST",
    dataType: "json",
    url: "ajax/projectPortfolioData.action",
    async: true,
    success: function(data,status) {
      me.setData(data);
      me.callListeners(new DynamicsEvents.RelationUpdatedEvent(me));
      me.isReloading = false;
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
