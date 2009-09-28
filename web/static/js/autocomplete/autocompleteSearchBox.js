/*
 * Search box with autocompletion.
 */

/**
 * Constructor for the <code>Autocomplete</code>'s search field.
 * 
 * @see Autocomplete
 * @constructor
 */
var AutocompleteSearch = function(bundle, options) {
  // Elements
  this.element = null;
  this.searchInput = null;
  this.bundle = bundle;
  this.suggestionList = null;
  
  // Data
  this.items = [];
  this.matchedItems = [];
  
  // No selection is -1
  this.selectedItem = -1;
  this.timer = null;
  
  this.options = {
      visibleSuggestions: 5
  };
  jQuery.extend(this.options, options);
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
  this.searchInput.bind("keyup", function(keyEvent) {
    var kc = keyEvent.keyCode;
    if (kc === AutocompleteVars.keyCodes.up) {
      me.shiftSelectionUp();
    }
    else if (kc === AutocompleteVars.keyCodes.down) {
      me.shiftSelectionDown();
    }
    else if (kc === AutocompleteVars.keyCodes.enter) {
      me.handleEnterEvent();
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
  else if (this.suggestionList.is(':hidden')) {
    this.updateMatches();
  }
  this.selectedItem--;
  this.updateSelectedListItem();
};


AutocompleteSearch.prototype.shiftSelectionDown = function() {
  if (this.selectedItem === (this.matchedItems.length - 1)) {
    return;
  }
  else if (this.suggestionList.is(':hidden')) {
    this.updateMatches();
  }
  this.selectedItem++;
  this.updateSelectedListItem();
};

AutocompleteSearch.prototype.updateSelectedListItem = function() {
  this.suggestionList.children().removeClass(AutocompleteVars.cssClasses.selectedLi);
  var a = this.suggestionList.children().eq(this.selectedItem);
  this.suggestionList.children().eq(this.selectedItem).addClass(AutocompleteVars.cssClasses.selectedLi);
};

/**
 * If search field empty, then call <code>Autocomplete.selectAndClose</code>.
 * Otherwise call <code>selectCurrentItem</code>
 */
AutocompleteSearch.prototype.handleEnterEvent = function() {
  if (this.searchInput.val() === "") {
    this.bundle.selectAndClose();
  }
  else {
    this.selectCurrentItem();
  }
};

AutocompleteSearch.prototype.selectCurrentItem = function() {
  this.suggestionList.children('li').eq(this.selectedItem).click();
};

AutocompleteSearch.prototype.selectItem = function(item) {
  this.bundle.selectItem(item);
  //this.selectedItemsBox.addItem(item);
  this.cancelSelection();
};

AutocompleteSearch.prototype.cancelSelection = function() {
  this.selectedItem = -1;
  this.suggestionList.hide();
};

AutocompleteSearch.prototype.clearInput = function() {
  this.searchInput.val("");
  this.cancelSelection();
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
  var returnedList = jQuery.grep(list, function(element, index) {
    return (element.enabled &&
        (me.matchSearchString(element.matchedString, match) ||
            me.matchSearchString(element.name, match))
        && !me.bundle.isItemSelected(element.id));
  });
  return returnedList;
};

AutocompleteSearch.prototype.matchSearchString = function(text, match) {
  if (!match || !text) {
    return false;
  }
  
  // Split to fragments
  var replaceRe = new RegExp("[\\]\\[\\\\!#$%&()*+,./:;<=>?@_`{|}~]+");
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
  
  $.each(this.matchedItems, function(k,item) {
    var listItem = $('<li/>').appendTo(me.suggestionList);
    var icon = $('<span/>').appendTo(listItem);
    me.addSuggestionListItemIcon(icon, item.baseClassName);
    var text = $('<span/>').text(item.name).appendTo(listItem);
    listItem.click(function() { me.selectItem(item); });
  });
  this.suggestionList.show();
};

AutocompleteSearch.prototype.addSuggestionListItemIcon = function(element, baseClass) {
  var classNamesToIconClasses = {
      "fi.hut.soberit.agilefant.model.User": AutocompleteVars.cssClasses.suggestionUserIcon,
      "fi.hut.soberit.agilefant.model.Team": AutocompleteVars.cssClasses.suggestionTeamIcon,
      "fi.hut.soberit.agilefant.model.Backlog": AutocompleteVars.cssClasses.suggestionBacklogIcon
  };
  
  element.addClass(AutocompleteVars.cssClasses.suggestionIcon);
  element.addClass(classNamesToIconClasses[baseClass]);
};


AutocompleteSearch.prototype.focus = function() {
  this.searchInput.focus();
};



