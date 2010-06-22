/** @ignore */
(function($){
	$.widget("ui.autocompleteDialog", {
		_init: function() {
			var elData = this.element.data(this.widgetName);
			this.element.removeData(this.widgetName);
			this.element = $('<div />');
			this.element.data(this.widgetName,elData);
			var me = this;

			var multiSelect = this.options.multiSelect;
			var autocompleteParams = {
				dataType:    this.options.dataType,
				preSelected: this.options.selected,
				multiSelect: multiSelect
			};
			
			if (! this.options.multiSelect) {
				autocompleteParams.callback = 
					function(val) { me.selectCallback(val); };
			}
			else {
			  autocompleteParams.selectCallback = function() { me.select(); };
			}
			
			var buttons = {
			    "Ok": function() {
			        me.select();
			    }
			};
			

			if (! this.options.required) {
			    buttons.Cancel = function() {
			        me._cancel();
			    };
			}
			
			var autocomplete = new Autocomplete(this.element, autocompleteParams);
			autocomplete.initialize();
			this.setValue(this.options.selected);
			var dialog = this.element.dialog({
				buttons: buttons,
				width: 500,
				modal: true,
				minHeight: multiSelect ? 400 : 150,
				position: multiSelect ? 'top' : 'center',
				title: this.options.title,
				close: function() {
				    me._cancel();
				},
				dialogClass: AutocompleteVars.cssClasses.autocompleteDialog
			});
			this.element.data("autocomplete", autocomplete);
		},
		_cancel: function() {
		    if (this.options.cancel) {
		      this.options.cancel.call(this);
		    }
		    this.destroy();
		},
		select: function(value) {
		    if (value) {
		        this.options.callback.call(this, [value.id]);
		    }
		    else {
		        this.options.callback.apply(this, this.value());
		    }
		    this.destroy();
		},
		value: function() {
			var ids   = this.element.data("autocomplete").getSelectedIds();
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
			selected: [],
			multiSelect: true
		}
	});
})(jQuery);