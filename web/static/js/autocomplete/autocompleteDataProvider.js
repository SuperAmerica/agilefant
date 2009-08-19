/*
 * Data provider singleton for Autocomplete.
 */

/**
 * Constructor for the singleton class AutocompleteDataProvider
 * @constructor
 */
AutocompleteDataProvider = function() {};

AutocompleteDataProvider.vars = {
  urls: {
    "usersAndTeams": "ajax/userChooserData.action",
    "backlogs": "ajax/backlogChooserData.action"
  }
};

AutocompleteDataProvider.instance = null;


/**
 * Get the singleton instance and create if doesn't exist. 
 */
AutocompleteDataProvider.getInstance = function() {
  if (!AutocompleteDataProvider.instance) {
    AutocompleteDataProvider.instance = new AutocompleteDataProvider();
  }
  return AutocompleteDataProvider.instance;
};

/**
 * Get the data for <code>Autocomplete</code>.
 * 
 * @param {String} dataType predefined data type to get
 * @see Autocomplete
 * @see AutocompleteDataProvider.vars.urls
 */
AutocompleteDataProvider.prototype.get = function(dataType) {
  var url = AutocompleteDataProvider.vars.urls[dataType];
  var params = {};
  return this._fetchData(url, params);
};

AutocompleteDataProvider.prototype.filterIdLists = function(items) {
  var retList = [];
  for(var i = 0; i < items.length; i++) {
    var item = items[i];
    if(!item.idList) {
      retList.push(item); 
    }
  }
  return retList;
};
AutocompleteDataProvider.prototype._fetchData = function(url,params) {
  var returnedData = null;
  jQuery.ajax({
    async: false,
    url: url,
    cache: false,
    dataType: "json",
    type: "post",
    success: function(data,status) {
      returnedData = data;
    },
    error: function(request, status, error) {
      new MessageDisplay.ErrorMessage("Unable to load data for autocompletion");
    }
  });
  return returnedData;
};

