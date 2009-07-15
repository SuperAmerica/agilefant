/*
 * Data provider singleton for Autocomplete.
 */


AutocompleteDataProvider.urls = {
  "usersAndTeams": "ajax/userChooserData.action";  
};

function AutocompleteDataProvider() {
  
};

AutocompleteDataProvider.instance = null;

AutocompleteDataProvider.getInstance = function() {
  if (!AutocompleteDataProvider.instance) {
    AutocompleteDataProvider.instance = new AutocompleteDataProvider();
  }
  return AutocompleteDataProvider.instance;
};