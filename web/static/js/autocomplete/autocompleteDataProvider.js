/*
 * Data provider singleton for Autocomplete.
 */

AutocompleteDataProvider = function() {};

AutocompleteDataProvider.vars = {
  urls: {
    "usersAndTeams": "ajax/userChooserData.action"
  }
};

AutocompleteDataProvider.instance = null;


/*
 * Get the singleton instance and create if doesn't exist 
 */
AutocompleteDataProvider.getInstance = function() {
  if (!AutocompleteDataProvider.instance) {
    AutocompleteDataProvider.instance = new AutocompleteDataProvider();
  }
  return AutocompleteDataProvider.instance;
};

AutocompleteDataProvider.prototype.get = function(dataType) {
  var url = AutocompleteDataProvider.vars.urls[dataType];
  var params = {};
  return this._fetchData(url, params);
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
      CommonView.showError("Unable to load data for autocompletion");
    }
  });
  return returnedData;
};

