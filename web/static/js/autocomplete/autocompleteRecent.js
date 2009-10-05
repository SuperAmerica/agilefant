
/**
 * Recently selected items
 * <p>
 * Stores recent selections in a per-user cookie.
 * @constructor
 */
var AutocompleteRecent = function AutocompleteRecent(element, dataType, parent, opts) {
  this.cookieName = "agilefant-autocomplete-" + dataType;
  this.recentlySelected = [];
  this.element = element;
  this.parent = parent;
  this.options = {
      caption: "Recently used",
      recentShowCount: 5
  };
};

/**
 * Initialize the recent box.
 */
AutocompleteRecent.prototype.initialize = function() {
  this.getDataFromCookie();
};

/**
 * Render the list.
 */
AutocompleteRecent.prototype.render = function() {
  var me = this;
  
  this.caption = $('<span/>').text(this.options.caption).appendTo(this.element);
  this.suggestionList = $('<ul/>').addClass(AutocompleteVars.cssClasses.recentList);
  this.element.append(this.suggestionList);
  var items = this.parent.getItemsByIdList(this.recentlySelected);
  
  $.each(items, function(k,v) {
    var select = function() { me.parent.selectItem(v); };
    var listItem = $('<li/>').text(v.name).attr('title',v.name).appendTo(me.suggestionList);
    listItem.click(select);
  });
};

/**
 * Add the item to recently selected
 */
AutocompleteRecent.prototype.pushToRecent = function(item) {
  if (item.idList) {
    for (var i = 0; i < item.idList.length; i++) {
      this._addToRecent(item.idList[i]);
    }
  }
  else {
    this._addToRecent(item.id);
  }
  this.storeCookie();
};

AutocompleteRecent.prototype._addToRecent = function(id) {
  ArrayUtils.remove(this.recentlySelected, id);
  this.recentlySelected.unshift(id);
  this.recentlySelected = this.recentlySelected.splice(0,5);
};

/**
 * Get the data from stored cookie.
 */
AutocompleteRecent.prototype.getDataFromCookie = function() {
  this.recentlySelected = [];
  var cookieString = jQuery.cookie(this.cookieName);
  if (!cookieString) {
    cookieString = "";
  }
  var items = cookieString.split(',');
  for (var i = 0; i < items.length; i++) {
    this.recentlySelected.push(parseInt(items[i], 10));
  }
};

/**
 * Stores the data from stored cookie.
 */
AutocompleteRecent.prototype.storeCookie = function() {
  var string = this.recentlySelected.join(',');
  var date = new Date();
  date.addDays(30);
  jQuery.cookie(this.cookieName, string, { expires: date });
};
