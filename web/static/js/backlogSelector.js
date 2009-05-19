(function($){
	var BacklogSelectorClass = function(element, options) {
		this.options = {
			selectedProduct: null,
			selectedProject: null,
			selectedIteration: null,
			onSelect: function() {}
		};
		this.element = element;
		$.extend(this.options, options);
		this.init();
	};
	//currently only for selecting iterations
	BacklogSelectorClass.prototype = {
		init: function() {
			//construct table
			this.container = $("<div />").appendTo(this.element).width("100%");
			this.table = $("<table />").appendTo(this.container);
			this.productRow = $("<tr />").appendTo(this.table);
			this.projectRow = $("<tr />").appendTo(this.table).hide();
			this.iterationRow = $("<tr />").appendTo(this.table).hide();
			$("<td />").text("Product").appendTo(this.productRow).width("15%");
			$("<td />").text("Project").appendTo(this.projectRow);
			$("<td />").text("Iteration").appendTo(this.iterationRow);
			this.productSelect = $("<select />").appendTo($("<td />").appendTo(this.productRow)).width("80%");
			this.projectSelect = $("<select />").appendTo($("<td />").appendTo(this.projectRow)).width("80%");
			this.iterationSelect = $("<select />").appendTo($("<td />").appendTo(this.iterationRow)).width("80%");
			var me = this;
			this.updateSelect(this.productSelect, 0, "Select product");
			this.productRow.show();
			this.productSelect.change(function() {
				var product = me.productSelect.val();
				me.iterationSelect.val(0);
				if(product > 0) {
					me.projectRow.show();
					me.iterationRow.hide();
					me.updateSelect(me.projectSelect,  product, "Select project");
				} else {
					me.projectRow.hide();
					me.iterationRow.hide();
					me.iterationSelect.val(0);
				}
			});
			this.projectSelect.change(function() {
				var project = me.projectSelect.val();
				me.iterationSelect.val(0);
				if(project > 0) {
					me.iterationRow.show();
					me.updateSelect(me.iterationSelect, project, "Select iteration");
				} else {
					me.iterationRow.hide();
				}

			});
		},
		renderSelect: function(container, data, firstRow) {
			container.empty();
			$("<option />").attr("value","0").text(firstRow).appendTo(container);
			for(var i = 0; i < data.length; i++) {
				$("<option />").attr("value",data[i].id).text(data[i].name).appendTo(container);
			}
		},
		updateSelect: function(container, backlogId, firstRow) {
			var me = this;
			$.getJSON("getSubBacklogsAsJSON.action", {backlogId: backlogId}, function(data,type) {
				data.sort(function(a,b) {
					if(!a.name) {
						return -1;
					}
					if(!b.name) {
						return 1;
					}
					if(a.name.toLowerCase() > b.name.toLowerCase()) {
						return 1;
					} else {
						return -1;
					}
				});
				me.renderSelect(container, data, firstRow);
			});
		},
		getSelectedIteration: function() {
			return this.iterationSelect.val();
		}
	};
	$.fn.extend({
		iterationSelect: function(options) {
			var el = $(this);
			if(!el.data("iterationSelect")) {
				el.data("iterationSelect",new  BacklogSelectorClass($(this),options));
			} else {
				if(options === "getSelected") {
					return el.data("iterationSelect").getSelectedIteration();
				}
			}
		}
	});
	
})(jQuery);