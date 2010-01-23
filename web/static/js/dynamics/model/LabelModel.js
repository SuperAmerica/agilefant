/**
 * Constructor for the <code>LabelModel</code> class.
 * 
 * @constructor
 * @base CommonModel
 * @see CommonModel#initialize
 */
var LabelModel = function LabelModel() {
  this.initialize();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Label";
  this.relations = {
      story: null
  };
  this.copiedFields = {
      "name": "name",
      "displayName": "displayName",
      "timestamp": "timestamp"
  };
  this.classNameToRelation = {
      "fi.hut.soberit.agilefant.model.Story": "story"
  };
};

LabelModel.prototype = new CommonModel();

LabelModel.prototype._setData = function(newData) {  
};

LabelModel.prototype.getName = function() {
  return this.currentData.name;
};

LabelModel.prototype.getDisplayName = function() {
  return this.currentData.displayName;
};

LabelModel.prototype.getTimestamp = function() {
  return this.currentData.timestamp;
};

LabelModel.prototype._remove = function(successCallback, extraData) {
  var me = this;
  var data = {
      labelId: me.getId()
  };
  jQuery.extend(data, extraData);
  jQuery.ajax({
      type: "POST",
      dataType: "text",
      url: "ajax/deleteLabel.action",
      data: data,
      async: true,
      cache: false,
      success: function(data, status) {
        MessageDisplay.Ok("Label removed");
        if (successCallback) {
          successCallback();
        }
      },
      error: function(data, status) {
        MessageDisplay.Error("Error deleting label.", data);
      }
  });
};
