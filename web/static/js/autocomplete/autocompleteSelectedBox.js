/*
 * Box for displaying the selected items from autocompletion.
 */

var AutocompleteSelected = function(parent) {
  this.selectedList = null;
  this.selectedIds = [];
  this.items = [];
  this.parent = parent;
};

AutocompleteSelected.prototype.setItems = function(items) {
  this.items = items;
};

AutocompleteSelected.prototype.getSelectedIds = function() {
  return this.selectedIds;
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

AutocompleteSelected.prototype.getItemsByIdList = function(idList) {
  if (!idList) {
    return [];
  }
  
  var list = [];
  for (var i = 0; i < this.items.length; i++) {
    if (jQuery.inArray(this.items[i].id, idList) !== -1) {
      list.push(this.items[i]);
    }
  }
  return list;
};

AutocompleteSelected.prototype.checkValidityForAddition = function(item) {
  if (!item || !item.name || typeof(item) !== "object" ||
     (!item.id && !(item.idList && item.idList.constructor == Array))) {
   return false; 
  }
  return true;
};

AutocompleteSelected.prototype.addItem = function(item) {
  if (!this.checkValidityForAddition(item)) {
    return;
  }
  else if (item.id) {
    this.selectItem(item);
  }
  else {
    var items = this.getItemsByIdList(item.idList);
    for (var i = 0; i < items.length; i++) {      
      this.selectItem(items[i]);
    }
  }
};

AutocompleteSelected.prototype.selectItem = function(item) {
  if (!item || typeof(item) !== "object" || this.isItemSelected(item.id)) {
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

