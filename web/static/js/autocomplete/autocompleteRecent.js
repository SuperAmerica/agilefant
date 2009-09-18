
/**
 * Recently selected items
 * <p>
 * Stores recent selections in a per-user cookie.
 * @constructor
 */
var AutocompleteRecentBox = function(element, dataType, parent, opts) {
  this.cookieName = "agilefant-autocomplete-" + dataType;
  this.recentlySelected = [];
  this.element = element;
  this.parent = parent;
  this.options = {
      recentShowCount: 5
  };
  this.getDataFromCookie();
};


/**
 * Render the list.
 */
AutocompleteRecentBox.prototype.render = function() {
  this.suggestionList = $('<ul/>').addClass(AutocompleteVars.cssClasses.recentList);
  this.element.append(this.suggestionList);
  var items = this.parent.getItemsByIdList(this.recentlySelected);
  
  for (var i = 0; i < items.length; i++) {
    var item = items[i];
    var listItem = $('<li/>').text(item.name).appendTo(this.suggestionList);
  }
};

/**
 * Get the data from stored cookie.
 */
AutocompleteRecentBox.prototype.getDataFromCookie = function() {
  this.recentlySelected = jQuery.cookie(this.cookieName);
};

/**
 * Stores the data from stored cookie.
 */
AutocompleteRecentBox.prototype.storeCookie = function(newId) {
  jQuery.cookie(this.cookieName, this.recentlySelected);
};
