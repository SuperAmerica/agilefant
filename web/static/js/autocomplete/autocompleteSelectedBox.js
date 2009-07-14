/*
 * Box for displaying the selected items from autocompletion.
 */

var AutocompleteSelected = function() {
  this.selectedList = null;
  this.selectedIds = [];
};

AutocompleteSelected.prototype.initialize = function(element) {
  this.element = element;
  this.element.addClass(AutocompleteVars.cssClasses.selectedParent);
  
  this.selectedList = $('<ul/>').addClass(AutocompleteVars.cssClasses.selectedItemsList)
    .appendTo(this.element);
};

AutocompleteSelected.prototype.isItemSelected = function(id) {
  return (jQuery.inArray(id, this.selectedIds) !== -1);
};

AutocompleteSelected.prototype.addItem = function(item) {
  if (!item || !item.id || !item.name || jQuery.inArray(item.id, this.selectedIds) !== -1) {
    return;
  }
  this.selectedIds.push(item.id);
  this.addListItem(item);
};

AutocompleteSelected.prototype.addListItem = function(item) {
  var listItem = $('<li/>').appendTo(this.selectedList);
  var nameSpan = $('<span/>').text(item.name)
    .addClass(AutocompleteVars.cssClasses.selectedItemName).appendTo(listItem);
  var nameSpan = $('<span/>').text('X')
    .addClass(AutocompleteVars.cssClasses.selectedItemRemove).appendTo(listItem);
};

AutocompleteSelected.prototype.removeItem = function(id) {
  
};

