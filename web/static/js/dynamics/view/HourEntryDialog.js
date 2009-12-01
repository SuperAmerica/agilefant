var AddHourEntryWidget = function(element) {
  this.element = element;
};
AddHourEntryWidget.prototype = {
  initialize: function() {
    
  }
    
};

/** @ignore */
(function($){
  $.widget("ui.hourEntryDialog", {
    _init: function() {
      var elData = this.element.data(this.widgetName);
      this.element.removeData(this.widgetName);
      this.element = $('<div />').appendTo(document.body);
      this.element.data(this.widgetName,elData);
      var me = this;
      var widget = new AddHourEntryWidget(this.element, {
        
      });
      widget.initialize();
      this.setValue(this.options.selected);
      var dialog = this.element.dialog({
        buttons: {
          "Cancel": function() {
            me._cancel();
          },
          "Select": function() {
            me.select();
          }
        },
        width: 500,
        minHeight: 400,
        position: 'top',
        title: this.options.title,
        close: function() {
          me._cancel();
        }
      });
      this.element.data("addHourEntryWidget", widget);
    },
    _cancel: function() {
      this.options.cancel.call(this);
      this.destroy();
    },
    select: function() {
      this.options.callback.apply(this, this.value());
      this.destroy();
    },
    value: function() {

    },
    setValue: function() {
      
    },
    destroy: function() {
      this.element.remove();
    }
  });
  $.extend($.ui.hourEntryDialog, {
    getter: "value",
    defaults: {
      callback: function() {},
      cancel: function() {},
      title: 'Log spent effort'
    }
  });
})(jQuery);