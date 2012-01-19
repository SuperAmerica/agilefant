


$.widget("custom.agilefantQuickSearch", $.ui.autocomplete, {
  _renderMenu: function( ul, items ) {
    var categories = {
      "noclass": " ",
      "fi.hut.soberit.agilefant.model.Story":     "Story",
      "fi.hut.soberit.agilefant.model.Iteration": "Iteration",
      "fi.hut.soberit.agilefant.model.Project":   "Project",
      "fi.hut.soberit.agilefant.model.Product":   "Product",
      "fi.hut.soberit.agilefant.model.Task":      "Task"
    };
  
    ul.addClass('quickSearchAutocompleteMenu');
    ul.css('z-index','500');
    var self = this,
      currentCategory = "";
    $.each( items, function( index, item ) {
      var tmpItem = {value: item.label, label: item.label, 'class': item.originalObject['class'], id: item.originalObject.id, category: "&nbsp;", originalObject: item.originalObject};
      if ( item.originalObject['class'] != currentCategory ) {
        tmpItem.category = categories[item.originalObject['class']];
        tmpItem.topBorder = true;
        currentCategory = item.originalObject['class'];
      }
      self._renderItem( ul, tmpItem );
    });
  },
  _renderItem: function(ul, data) {
    var item = $( '<li class="noWrap"></li>' );
    if (data.topBorder) {
      item.addClass('topBorder');
    }
    $('<span class="categoryName">' + data.category + "</span>").appendTo(item);
    $("<a>" + data.label + "</a>" ).appendTo(item);
    return item.data( "item.autocomplete", data ).appendTo(ul);
  },
  _response: function( content ) {
    if ( content.length ) {
      content = this._normalize( content );
      this._suggest( content );
      this._trigger( "open" );
    } else {
      var tmpItem = {value: "No results found", label: "No results found", id: -1, category: "&nbsp;", originalObject: { 'class': 'noclass' }};
      this._suggest( [ tmpItem ] );
      this._trigger( "open" );
    }
    this.element.removeClass( "ui-autocomplete-loading" );
  }
});