/*
 * Autocomplete classes for creating dialoged and non dialoged
 * search boxes.
 */

var AutocompleteVars = {
    cssClasses: {
      autocompleteElement: 'autocomplete',
      searchParent: 'autocomplete-searchBoxContainer',
      selectedParent: 'autocomplete-selectedBoxContainer',
      suggestionList: 'autocomplete-suggestionList',
      selectedLi: 'autocomplete-selected',
      selectedItemsList: 'autocomplete-selectedItemsList',
      selectedItemName: 'autocomplete-selectedName',
      selectedItemRemove: 'autocomplete-selectedRemove' 
    },
    keyCodes: {
      enter: 13,
      esc:   27,
      down:  40,
      up:    38
    },
    inputWaitTime: 500
};

var Autocomplete = function(element, options) {
  this.parent = element;
  this.items = [];
  this.searchBoxContainer = $('<div/>');
  this.selectedBoxContainer = $('<div/>');
  this.selectedBox = new AutocompleteSelected(this);
  this.searchBox = new AutocompleteSearch(this.selectedBox);
  this.options = {
      dataType: ""
  };
  jQuery.extend(this.options, options);
  this.dataProvider = null;
};

jQuery.fn.autocomplete = function(options) {
  var autocomplete = new Autocomplete(this);
  autocomplete.items = options.items;
  autocomplete.initialize();
};


Autocomplete.prototype.initialize = function() {
  this.element = $('<div/>').addClass(AutocompleteVars.cssClasses.autocompleteElement)
    .appendTo(this.parent);
  
  this.dataProvider = AutocompleteDataProvider.getInstance();
  
  this.searchBoxContainer.appendTo(this.element);
  this.selectedBoxContainer.appendTo(this.element);
  
  this.searchBox.setItems(this.items);
  this.selectedBox.setItems(this.items);
  
  this.searchBox.initialize(this.searchBoxContainer);
  this.selectedBox.initialize(this.selectedBoxContainer);
};

Autocomplete.prototype.getData = function() {
  this.items = this.dataProvider.get(this.options.dataType);
};

Autocomplete.prototype.focusSearchField = function() {
  this.searchBox.focus();
};

Autocomplete.prototype.remove = function() {
  this.element.remove();
};

Autocomplete.prototype.getSelectedIds = function() {
  return this.selectedBox.getSelectedIds();
};

