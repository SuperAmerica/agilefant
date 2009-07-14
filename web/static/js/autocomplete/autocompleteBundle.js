/*
 * Autocomplete classes for creating dialoged and non dialoged
 * search boxes.
 */

var AutocompleteVars = {
    cssClasses: {
      autocompleteElement: 'autocomplete',
      searchParent: 'autocomplete-searchBoxContainer',
      selectedParent: 'autocomplete-selectedBoxContainer'
    },
    keyCodes: {
      enter: 13,
      esc:   27,
      down:  40,
      up:    38
    }
};

var Autocomplete = function(element) {
  this.parent = element;
  this.searchBoxContainer = $('<div/>');
  this.selectedBoxContainer = $('<div/>');
  this.searchBox = new AutocompleteSearch();
  this.selectedBox = new AutocompleteSelected();
};

jQuery.fn.autocomplete = function() {
  var autocomplete = new Autocomplete(this);
  autocomplete.initialize();
};


Autocomplete.prototype.initialize = function() {
  this.element = $('<div/>').addClass(AutocompleteVars.cssClasses.autocompleteElement)
    .appendTo(this.parent);
  
  this.searchBoxContainer.appendTo(this.element);
  this.selectedBoxContainer.appendTo(this.element);
  
  this.searchBox.initialize(this.searchBoxContainer);
  this.selectedBox.initialize(this.selectedBoxContainer);
};
