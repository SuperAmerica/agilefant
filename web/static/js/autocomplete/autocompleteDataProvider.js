/*
 * Data provider singleton for Autocomplete.
 */

AutocompleteDataProvider.vars = {
  urls: {
    "usersAndTeams": "ajax/userChooserData.action"
  }
};

AutocompleteDataProvider.instance = null;


function AutocompleteDataProvider() {
  
};

AutocompleteDataProvider.getInstance = function() {
  if (!AutocompleteDataProvider.instance) {
    AutocompleteDataProvider.instance = new AutocompleteDataProvider();
  }
  return AutocompleteDataProvider.instance;
};



AutocompleteDataProvider.prototype._fetchData = function(url,params) {
  jQuery.getJSON(url,params, function() {
    
  });
};

