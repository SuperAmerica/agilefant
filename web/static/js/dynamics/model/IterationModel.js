
/**
 * Model class for iterations
 * @constructor
 * @base BacklogModel
 * @see BacklogModel#initializeBacklogModel
 */
var IterationModel = function() {
  this.initializeBacklogModel();
  this.persistedClassName = "fi.hut.soberit.agilefant.model.Iteration";
};

IterationModel.prototype = new BacklogModel();


/**
 * Internal function to set data
 * @see CommonModel#setData
 */
IterationModel.prototype._setData = function(newData) {
  var data = {};
  
  // Set the id
  this.id = newData.id;
  
  // Copy fields
  var copiedFields = {
    "name":   "name"
  };
  for (field in copiedFields) {
    if(copiedFields.hasOwnProperty(field)) {
      var ownField = copiedFields[field];
      data[ownField] = newData[field];
    }
  }

  // Set the data
  this.persistedData = data;
  this.currentData = data;

};


