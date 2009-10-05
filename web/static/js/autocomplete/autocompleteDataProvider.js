/*
 * Data provider singleton for Autocomplete.
 */

/**
 * Constructor for the singleton class AutocompleteDataProvider
 * @constructor
 */
AutocompleteDataProvider = function AutocompleteDataProvider() {};

AutocompleteDataProvider.vars = {
  urls: {
    "usersAndTeams":     { url: "ajax/userChooserData.action"                  },
    "backlogs":          { url: "ajax/backlogChooserData.action"               },
    "currentIterations": { url: "ajax/currentIterationChooserData.action"      },
    "products":          { url: "ajax/productChooserData.action"               },
    "projects":          { url: "ajax/projectChooserData.action"               }
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
  var urlInfo = AutocompleteDataProvider.vars.urls[dataType];
  var params = {};
  if (urlInfo.params) {
      params = urlInfo.params;
  }
  
  return this._fetchData(urlInfo.url, params);
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
AutocompleteDataProvider.prototype._fetchData = function(url, params) {
  var returnedData = null;
  jQuery.ajax({
    async: false,
    url: url,
    cache: false,
    dataType: "json",
    data: params,
    type: "post",
    success: function(data,status) {
      returnedData = data;
    },
    error: function(request, status, error) {
      MessageDisplay.Error("Unable to load data for autocompletion");
    }
  });
  return returnedData;
};

