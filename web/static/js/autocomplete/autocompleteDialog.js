/** @ignore */
(function($){
	$.widget("ui.autocompleteDialog", {
		_init: function() {
			var elData = this.element.data(this.widgetName);
			this.element.removeData(this.widgetName);
			this.element = $('<div />').appendTo(document.body);
			this.element.data(this.widgetName,elData);
			var me = this;
			var autocomplete = new Autocomplete(this.element, {
				dataType: this.options.dataType,
				preSelected: this.options.selected
			});
			autocomplete.initialize();
			this.setValue(this.options.selected);
			var dialog = this.element.dialog({
				buttons: {
					"Select": function() {
						me.select();
					},
					"Cancel": function() {
						me._cancel();
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
			this.element.data("autocomplete", autocomplete);
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
			var ids = this.element.data("autocomplete").getSelectedIds();
			var items = this.element.data("autocomplete").getSelectedItems();
			return [ids, items];
		},
		setValue: function() {
			
		},
		destroy: function() {
			this.element.remove();
		}
	});
	$.extend($.ui.autocompleteDialog, {
		getter: "value",
		defaults: {
			callback: function() {},
			cancel: function() {},
			title: 'Select',
			dataType: '',
			selected: []
		}
	});
})(jQuery);