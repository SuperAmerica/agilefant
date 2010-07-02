/**
 * Agilefant Widget jQuery plugin.
 */

$.widget("custom.aefWidget", {
  options: {
    widgetId: -1,
    objectId: -1,
    realWidget: true,
    ajaxWidget: true,
    url: 'noop.action'
  },
  _create: function() {
    this.element.attr('widgetId', this.options.widgetId);
    
    if (this.options.realWidget) {
      this.element.addClass('realWidget');
    }
    
    this.reload();
  },
  _bindEvents: function() {
    var me = this;
    
    /* Close widget */
    this.element.find('.closeWidget').click(function() {
      if (me.options.widgetId !== -1) {
        $.ajax({
          type: 'POST',
          dataType: 'text',
          url: 'ajax/widgets/deleteWidget.action',
          data: { widgetId: me.options.widgetId },
          success: function(data, status) {
            me.destroy();
          }
        });
      } else {
        me.destroy();
      }
    });
    
    /* Minimize/maximize widget */
    var minimizeButton = this.element.find('.minimizeWidget');
    var maximizeButton = this.element.find('.maximizeWidget');
    if (this.content.find('.expandable').length) {
      /* Bind the events */
      minimizeButton.bind('click',jQuery.proxy(function() {
        maximizeButton.show();
        minimizeButton.hide();
        this.content.find('.expandable').hide();
        jQuery.cookie('agilefant_widgets_' + this.options.widgetId + '_open','closed',{expires:60});
      },this));
    
      maximizeButton.bind('click',jQuery.proxy(function() {
        maximizeButton.hide();
        minimizeButton.show();
        this.content.find('.expandable').show();
        jQuery.cookie('agilefant_widgets_' + this.options.widgetId + '_open','open',{expires:60});
      },this));
      
      /* Check for cookie */
      if (jQuery.cookie('agilefant_widgets_' + this.options.widgetId + '_open') === 'open') {
        maximizeButton.click();
      } else {
        minimizeButton.click();
      }
    }
  },
  reload: function() {
    if (this.options.ajaxWidget) {
      this.element.load(this.options.url, {
        objectId : this.options.objectId,
        widgetId : this.options.widgetId
      }, jQuery.proxy(function(data, status) {
        this.content = this.element.find('.widgetContent');
        this._bindEvents();
      }, this));
    }
  },
  destroy: function() {
    $.Widget.prototype.destroy.apply(this, arguments);
    this.element.remove();
  }
});