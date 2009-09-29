/** @ignore */
(function($){
  $.widget("ui.autocompleteInline", {
    _init: function() {
      var elData = this.element.data(this.widgetName);
      var me = this;

      var autocompleteParams = {
        dataType:    this.options.dataType,
        preSelected: this.options.selected,
        multiSelect: false,
        showRecent:  this.options.showRecent,
        selectCallback: function(val) {
          me.setValue(val);
          var object = ModelFactory.updateObject(val.originalObject);
          me.options.callback(object);
        }
      };

      var autocomplete = new Autocomplete(this.element, autocompleteParams);
      autocomplete.initialize();
      this.element.data("autocomplete", autocomplete);
      this.setValue(this.options.selected);
    },
    select: function(value) {
        if (value) {
            this.options.callback.call(this, [value.id]);
        }
        else {
            this.options.callback.apply(this, this.value());
        }
    },
    value: function() {
      var ids   = this.element.data("autocomplete").getSelectedIds();
      var items = this.element.data("autocomplete").getSelectedItems();
      return [ids, items];
    },
    setValue: function(val) {
      this.selected = val.id;
      this.element.data("autocomplete").setSearchBoxValue(val.name);
    }
  });

  $.extend($.ui.autocompleteInline, {
    getter: "value",
    defaults: {
      callback: function() {},
      cancel: function() {},
      title: 'Select',
      dataType: '',
      selected: [],
      multiSelect: true,
      showRecent:  false
    }
  });
})(jQuery);