/** @ignore */
(function($){
	$.widget("ui.autocompleteSingleDialog", {
		_init: function() {
			var elData = this.element.data(this.widgetName);
			this.element.removeData(this.widgetName);
			this.element = $('<div />').appendTo(document.body);
			this.element.data(this.widgetName,elData);
			var me = this;
			var autocomplete = new Autocomplete(this.element, {
				dataType: this.options.dataType,
				preSelected: this.options.selected,
				multiSelect: false,
				selectCallback: function(val) { me.select(val); }
			});
			autocomplete.initialize();
			this.setValue(this.options.selected);
			var dialog = this.element.dialog({
				buttons: {
					"Cancel": function() {
						me._cancel();
					}
				},
				width: 500,
				minHeight: 150,
				position: 'top',
				title: this.options.title,
				modal: true,
				close: function() {
				  me._cancel();
				},
				dialogClass: AutocompleteVars.cssClasses.autocompleteDialog
			});
			this.element.data("autocomplete", autocomplete);
		},
	  _cancel: function() {
		  this.options.cancel.call(this);
		  this.destroy();
		},
		select: function(item) {
			this.options.callback.apply(this, [item.id]);
			this.destroy();
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