/**
 * Agilefant Widget jQuery plugin.
 */
window.aefWidgetCookie = null;

$.widget("custom.aefWidget", {
  options: {
    widgetId: -1,
    objectId: -1,
    realWidget: true,
    ajaxWidget: true,
    initialReload: true,
    url: 'noop.action'
  },
  _create: function() {
    this.element.attr('widgetId', this.options.widgetId);
    
    this.element.addClass('widget');
    
    if (this.options.realWidget) {
      this.element.addClass('realWidget');
    }
    
    this._loadCookie();
    
    if (this.options.initialReload) {
      this.reload();
    } else {
      this._bindEvents();
    }
  },
  _loadCookie: function() {
    if (!window.aefWidgetCookie) {
      var cookie = $.cookie('agilefant_widgets_expanded');      
      window.aefWidgetCookie = [];
      if (cookie) {
        cookie = cookie.split(',');
        for (var i = 0; i < cookie.length; i++) {
          window.aefWidgetCookie.push(parseInt(cookie[i], 10));
        }
      }
    }
  },
  _cookie: function() {
    window.aefWidgetCookie = window.aefWidgetCookie.unique();
    $.cookie('agilefant_widgets_expanded',window.aefWidgetCookie.join(','),{expires:60});
  },
  _isExpanded: function() {
    return (jQuery.inArray(this.options.widgetId, window.aefWidgetCookie) !== -1);
  },
  _addToCookie: function() {
    window.aefWidgetCookie.push(this.options.widgetId);
    this._cookie();
  },
  _removeFromCookie: function() {
    ArrayUtils.remove(window.aefWidgetCookie, this.options.widgetId);
    this._cookie();
  },
  expand: function() {
    this.element.find('.maximizeWidget').hide();
    this.element.find('.minimizeWidget').show();
    this.content.find('.expandable').show();
  },
  collapse: function() {
    this.element.find('.maximizeWidget').show();
    this.element.find('.minimizeWidget').hide();
    this.content.find('.expandable').hide();
  },
  _bindEvents: function() {
    var me = this;
    this.content = this.element.find('.widgetContent');
    
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
        this.collapse();
        this._removeFromCookie();
      },this));
    
      maximizeButton.bind('click',jQuery.proxy(function() {
        this.expand();
        this._addToCookie();
      },this));
      
      /* Check for cookie */
      if (this._isExpanded()) {
        this.expand();
      } else {
        this.collapse();
      }
    }
  },
  reload: function() {
    if (this.options.ajaxWidget) {
      this.element.load(this.options.url, {
        objectId : this.options.objectId,
        widgetId : this.options.widgetId
      }, jQuery.proxy(function(data, status) {
        this._bindEvents();
      }, this));
    }
  },
  destroy: function() {
    $.Widget.prototype.destroy.apply(this, arguments);
    this._removeFromCookie();
    this.element.remove();
  }
});