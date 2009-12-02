var LazyLoadedDialog = function LazyLoadedDialog(options) {
  this.url = options.url;
  this.data = options.data;
  this.title = options.title;
  this.okCallback = options.okCallback;
  this.cancelCallback = options.cancelCallback;
  this.ready = false;
  this.init();
};

LazyLoadedDialog.prototype.init = function() {
  this.contentElement = $('<div><div style="text-align: center"><img src="static/img/pleasewait.gif" alt="Loading..." /></div></div>').appendTo(document.body);
  var me = this;
  this.contentElement.dialog({
    modal: true,
    title: me.title,
    minHeight: '200',
    minWidth: '400',
    width: '400',
    position: 'center',
    resizable: false,
    buttons: {
      "Ok": function() {
        if (me.ready) {
          me._ok();
        }
      },
      "Cancel": function() {
        if (me.ready) {
          me._cancel();
        }
      }
    },
    open: function() {
      me.contentElement.load(me.url, me.data, function(responseText, textStatus, XMLHttpRequest) {
        if (textStatus === 'success') {
          me.ready = true;
        }
      });
    }
  });
};

LazyLoadedDialog.prototype._ok = function() {
  if (this.okCallback) {
    this.okCallback();
  }
  this.close();
};
LazyLoadedDialog.prototype._cancel = function() {
  if (this.closeCallback) {
    this.closeCallback();
  }
  this.close();
};

LazyLoadedDialog.prototype.close = function() {
  this.contentElement.dialog("destroy").remove();
};

