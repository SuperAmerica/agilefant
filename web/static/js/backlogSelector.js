(function($){
	var BacklogSelectorClass = function(element, options) {
		this.options = {
			selectedProduct: null,
			selectedProject: null,
			selectedIteration: null,
			onSelect: function() {},
			selectStory: false,
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
			this.storyRow = $("<tr />").appendTo(this.table).hide();
			$("<td />").text("Product").appendTo(this.productRow).width("15%");
			$("<td />").text("Project").appendTo(this.projectRow);
			$("<td />").text("Iteration").appendTo(this.iterationRow);
			$("<td />").text("Story").appendTo(this.storyRow);
			this.productSelect = $("<select />").appendTo($("<td />").appendTo(this.productRow)).width("80%");
			this.projectSelect = $("<select />").appendTo($("<td />").appendTo(this.projectRow)).width("80%");
			this.iterationSelect = $("<select />").appendTo($("<td />").appendTo(this.iterationRow)).width("80%");
			this.storySelect = $("<select />").appendTo($("<td />").appendTo(this.storyRow)).width("80%");
			var me = this;
			this.updateSelect(this.productSelect, 0, "Select product");
			this.productRow.show();
			this.productSelect.change(function() {
				var product = me.productSelect.val();
				me.iterationSelect.val(0);
				me.storyRow.hide();
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
				me.storyRow.hide();
				me.iterationSelect.val(0);
				if(project > 0) {
					me.iterationRow.show();
					me.updateSelect(me.iterationSelect, project, "Select iteration");
				} else {
					me.iterationRow.hide();
				}

			});
			this.iterationSelect.change(function() {
				if(me.options.selectStory) {
					me.storyRow.show();
					me.storySelect.val(0);
					var iteration = me.iterationSelect.val();
					if(iteration > 0) {
						me.selectStory(me.storySelect, iteration, "Select story");
					}
				} else {
					me.options.onSelect.call(this);
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
		selectStory: function(container, backlogId, firstRow) {
			var me = this;
			$.getJSON("ajaxGetStories.action", {"backlogId": backlogId}, function(data, type) {
				me.sortData(data);
				me.renderSelect(container, data, firstRow);
			});
		},
		updateSelect: function(container, backlogId, firstRow) {
			var me = this;
			$.getJSON("getSubBacklogsAsJSON.action", {backlogId: backlogId}, function(data,type) {
				me.sortData(data);
				me.renderSelect(container, data, firstRow);
			});
		},
		sortData: function(data) {
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
		},
		getSelectedIteration: function() {
			return parseInt(this.iterationSelect.val());
		},
		getSelectedProduct: function() {
			return parseInt(this.productSelect.val());
		},
		getSelectedProject: function() {
			return parseInt(this.projectSelect.val());
		},
		getSelectedStory: function() {
			return parseInt(this.storySelect.val());
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
				} else if(options === "getStory") {
					return el.data("iterationSelect").getSelectedStory();
				} else if (options === "getSelectedProduct") {
					return el.data("iterationSelect").getSelectedProduct();
				} else if (options === "getSelectedProject") {
					return el.data("iterationSelect").getSelectedProject();
				}
			}
		}
	});
	
})(jQuery);