(function($){
	$.widget("ui.autocompleteDialog", {
		_init: function() {
			var me = this;
			var autocomplete = new Autocomplete(this.element, {
				dataType: this.options.dataType
			});
			autocomplete.initialize();
			this.setValue(this.options.selected);
			var dialog = this.element.dialog({
				buttons: {
					"Select": function() {
						me.select();
					},
					"Cancel": function() {
						me.destroy();
					}
				},
				width: 500,
				minHeight: 400,
				position: 'top',
				title: this.options.title
			});
			this.element.data("autocomplete", autocomplete);
		},
		select: function() {
			this.options.callback.apply(this, this.value());
			this.destroy();
		},
		value: function() {
			return this.data("autocomplete").getSelectedIds();
		},
		setValue: function() {
			
		},
		destroy: function() {
			$.widget.prototype.destroy.apply(this, arguments); 
     		this.element.dialog('destroy');
     		this.element.data("autocomplete").remove();
			this.element.removeData("autocomplete");
		}
	});
	$.extend($.ui.autocompleteDialog, {
		getter: "value",
		defaults: {
			callback: function() {},
			title: 'Select',
			dataType: '',
			selected: []
		}
	});
})(jQuery);