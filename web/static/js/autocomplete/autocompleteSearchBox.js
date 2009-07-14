/*
 * Search box with autocompletion.
 */

var AutocompleteSearch = function(selectedItemsBox) {
  // Elements
  this.element = null;
  this.searchInput = null;
  this.selectedItemsBox = selectedItemsBox;
  this.suggestionList = null;
  
  // Data
  this.items = new Array();
  this.matchedItems = new Array();
  
  // No selection is -1
  this.selectedItem = -1;
  this.timer = null;
};

AutocompleteSearch.prototype.setItems = function(items) {
  this.items = items;
};

AutocompleteSearch.prototype.initialize = function(element) {
  this.element = element;
  this.element.addClass(AutocompleteVars.cssClasses.searchParent);
  
  this.searchInput = $('<input type="text"/>').appendTo(this.element);
  
  this.suggestionList = $('<ul/>').hide().addClass(AutocompleteVars.cssClasses.suggestionList)
    .appendTo(this.element);
  
  this.bindEvents();
};


AutocompleteSearch.prototype.bindEvents = function() {
  var me = this;
  this.searchInput.bind("keypress", function(keyEvent) {
    var kc = keyEvent.keyCode;
    if (kc === AutocompleteVars.keyCodes.up) {
      me.shiftSelectionUp();
    }
    else if (kc === AutocompleteVars.keyCodes.down) {
      me.shiftSelectionDown();
    }
    else if (kc === AutocompleteVars.keyCodes.enter) {
      me.selectCurrent();
    }
    else if (kc === AutocompleteVars.keyCodes.esc) {
      me.cancelSelection();
    }
    else {
      me.timeoutUpdateMatches();
    }
  });
  
};


AutocompleteSearch.prototype.shiftSelectionUp = function() {
  if (this.selectedItem === -1) {
    return;
  }
  this.selectedItem--;
};


AutocompleteSearch.prototype.shiftSelectionDown = function() {
  if (this.selectedItem === (this.matchedItems.length - 1)) {
    return;
  }
  this.selectedItem++;
};

AutocompleteSearch.prototype.selectCurrent = function() {
  
};

AutocompleteSearch.prototype.cancelSelection = function() {
  this.selectedItem = -1;
  this.suggestionList.hide();
};

AutocompleteSearch.prototype.timeoutUpdateMatches = function() {
  var me = this;
  if (this.timer) {
    clearTimeout(this.timer);
  }
  this.timer = setTimeout(function() {
    var a = me;
    me.updateMatches();
  }, AutocompleteVars.inputWaitTime);
};

AutocompleteSearch.prototype.updateMatches = function() {
  var inputValue = this.searchInput.val();
  this.matchedItems = this.filterSuggestions(this.items, inputValue);
  
  this.renderSuggestionList();
};

AutocompleteSearch.prototype.filterSuggestions = function(list, match) {
  var me = this;
  var returnedList = list.filter(function(element, index, array) {
    return (me.matchSearchString(element.name, match) &&
        !me.selectedItemsBox.isItemSelected(element.id));
  });
  return returnedList;
};

AutocompleteSearch.prototype.matchSearchString = function(text, match) {
  if (!match || !text) {
    return false;
  }
  
  // Split to fragments
  var replaceRe = new RegExp("[!#$%&()*+,./:;<=>?@[\\\]_`{|}~]+");
  var matchFragments = match.replace(replaceRe, ' ').split(' ');
  
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

AutocompleteSearch.prototype.renderSuggestionList = function() {
  var me = this;
  this.suggestionList.empty();
  if (this.matchedItems.length === 0) {
    this.cancelSelection();
    return;
  }
  
  for (var i = 0; i < this.matchedItems.length; i++) {
    var item = this.matchedItems[i];
    var listItem = $('<li/>').text(item.name).appendTo(this.suggestionList);
    listItem.click(function() { alert(item.name + " was clicked")});
  }
  this.suggestionList.show();
};



