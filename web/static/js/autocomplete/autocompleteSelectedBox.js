/*
 * Box for displaying the selected items from autocompletion.
 */

var AutocompleteSelected = function(parent) {
  this.selectedList = null;
  this.selectedIds = [];
  this.parent = parent;
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
  var me = this;
  var listItem = $('<li/>').appendTo(this.selectedList);
  
  var nameSpan = $('<span/>').text(item.name)
    .addClass(AutocompleteVars.cssClasses.selectedItemName).appendTo(listItem);
  
  var removeSpan = $('<span/>').text('X')
    .addClass(AutocompleteVars.cssClasses.selectedItemRemove).appendTo(listItem);
  
  removeSpan.click(function() {
    me.removeItem(item.id, listItem);
  });
};

AutocompleteSelected.prototype.removeItem = function(id, listItem) {
  var me = this;
  var tmp = this.selectedIds;
  this.selectedIds = [];
  for (var i = 0; i < tmp.length; i++) {
    if (tmp[i] !== id) {
      this.selectedIds.push(tmp[i]);
    }
  }
  listItem.remove();
  
  me.parent.focusSearchField();
};

