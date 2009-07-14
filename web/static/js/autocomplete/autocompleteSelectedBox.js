/*
 * Box for displaying the selected items from autocompletion.
 */

var AutocompleteSelected = function() {
  this.selected
};

AutocompleteSelected.prototype.initialize = function(element) {
  this.element = element;
  this.element.addClass(AutocompleteVars.cssClasses.selectedParent);
  
  $('<span/>').text('Selected items').appendTo(this.element);
};

AutocompleteSelected.prototype.isItemSelected = function(id) {
  
};

AutocompleteSelected.prototype.addItem = function(item) {
  
};

AutocompleteSelected.prototype.removeItem = function(id) {
  
};
