/**
 * Search box with autocompletion.
 */

var AutocompleteSearch = function(selectedItemsContainer) {
  this.selectedItemsContainer = selectedItemsContainer;
};


AutocompleteSearch.prototype.filterSuggestions = function(list, match) {
  var me = this;
  var returnedList = list.filter(function(element, index, array) {
    return (me.matchSearchString(element.name, match) &&
        !me.selectedItemsContainer.isItemSelected(element.id));
  });
  return returnedList;
};

AutocompleteSearch.prototype.matchSearchString = function(text, match) {
  if (!match || !text) return false;
  
  // Split to fragments
  var replaceRe = /[!#$%&()*+,./:;<=>?@[\\\]_`{|}~]+/;
  var matchFragments = match.replace(replaceRe, '').split(' ');
  
  var a = 5;
  // Loop through fragments
  var allMatch = true;
  for (var i = 0; i < matchFragments.length; i++) {
    var fragment = matchFragments[i];
    if (text.toLowerCase().indexOf(fragment.toLowerCase()) === -1) {
      allMatch = false;
      break;
    }  
  }
  
  return allMatch;
};