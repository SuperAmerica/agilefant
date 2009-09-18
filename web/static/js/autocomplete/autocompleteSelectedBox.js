/*
 * Box for displaying the selected items from autocompletion.
 */


/**
 * Constructor for the <code>Autocomplete</code>'s selected items list.
 * 
 * @constructor
 * @see Autocomplete
 */
var AutocompleteSelected = function(parent) {
  this.selectedList = null;
  this.selectedIds = [];
  this.items = [];
  this.parent = parent;
};

/**
 * Set the items to select from.
 */
AutocompleteSelected.prototype.setItems = function(items) {
  this.items = items;
};

/**
 * Get the id numbers of all the selected elements.
 */
AutocompleteSelected.prototype.getSelectedIds = function() {
  return this.selectedIds;
};

AutocompleteSelected.prototype.getSelectedItems = function() {
  var selected = [];
  for(var i = 0; i < this.items.length; i++) {
    if(jQuery.inArray(this.items[i].id, this.selectedIds) !== -1) {
      selected.push(this.items[i].originalObject);
    }
  }
  return selected;
};

/**
 * Initialize the selection element.
 */
AutocompleteSelected.prototype.initialize = function(element) {
  this.element = element;
  this.element.addClass(AutocompleteVars.cssClasses.selectedParent);
  
  this.selectedList = $('<ul/>').addClass(AutocompleteVars.cssClasses.selectedItemsList)
    .appendTo(this.element);
};

/**
 * Check if the item with the given id is already selected.
 */
AutocompleteSelected.prototype.isItemSelected = function(id) {
  return (jQuery.inArray(id, this.selectedIds) !== -1);
};

/**
 * Gets all items as list by their id.
 */
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

AutocompleteSelected.prototype.addItemById = function(id) {
  for (var i = 0; i < this.items.length; i++) {
    if (this.items[i].id === id) {
      this.addItem(this.items[i]);
    }
  }
};

AutocompleteSelected.prototype.addItem = function(item) {
  if (!this.checkValidityForAddition(item)) {
    return;
  }
  else if (item.idList) {
    var items = this.getItemsByIdList(item.idList);
    for (var i = 0; i < items.length; i++) {      
      this.selectItem(items[i]);
    }
  }
  else  {
    this.selectItem(item);
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

/**
 * Removes the given item from the selection.
 * <p>
 * Also focuses the search field.
 * 
 * @param {int} id the id of the item to be removed
 * @param {li} the dom element to remove
 */
AutocompleteSelected.prototype.removeItem = function(id, listItem) {
  ArrayUtils.remove(this.selectedIds, id);
  listItem.remove();
  
  this.parent.focusSearchField();
};

