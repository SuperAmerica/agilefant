(function($) {
	var backlogChooser = function(container, options) {
		var settings = {
				useDateLimit: false,
				selectedProducts: [],
				selectedIterations: [],
				selectedProjects: []
		};
		$.extend(settings, options);

		//selected backlogs by type
		this.selectedProducts = settings.selectedProducts;
		this.selectedProjects = settings.selectedProjects;
		this.selectedIterations = settings.selectedIterations;
		
		this.useDateLimit = settings.useDateLimit;
		
		//parent DOM element
		this.container = container;
		
		//DOM elements for comboboxes
		this.productContainer = null;
		this.projectContainer = null;
		this.iterationContainer = null;
		this.initialize();
		this.currentDate = (new Date()).getTime();
	}
	backlogChooser.prototype = {
			initialize: function() {
				var me = this;
				this.productContainer = $('<select name="productIds"/>').appendTo(this.container).attr("multiple","multiple").change(function() {me.clickProduct(me)}).width("200px").height("200px");
				this.projectContainer = $('<select name="projectIds"/>').appendTo(this.container).hide().attr("multiple","multiple").change(function() {me.clickProject(me)}).width("200px").height("200px");
				this.iterationContainer = $('<select name="iterationIds"/>').appendTo(this.container).hide().attr("multiple","multiple").change(function() {me.clickIteration(me)}).width("200px").height("200px");
				
				this.auxFields = $('<span />').appendTo(this.container).hide();

				$.post("ajax/retrieveProduct.action", {}, function(data,retType) {
					if(data.length == 0) {
						$('<option/>').appendTo(me.productContainer).text("There are no Products in the system.");
					} else {
						$.each(me.sortBacklogs(data),function(key,element) {
							var opt = $('<option/>').appendTo(me.productContainer).text(element.name).attr("value",element.id);
							if($.inArray(element.id,me.selectedProducts) != -1) {
								opt.attr("selected","selected");
							}
						});
						if(me.selectedProducts.length != 0) {
							me.clickProduct();
							me.clickProject();
						}
					}
				},"json");
			},
			sortBacklogs: function(backlogs) {
				var sorter = function(a,b) {
					var aname = a.name.toLowerCase();
					var bname = b.name.toLowerCase();
					if(aname == bname) {
						return 0;
					} else if(aname > bname) {
						return 1;
					} else {
						return -1;
					}
				};
				return backlogs.sort(sorter);
			},
			reRender: function() {
				this.clickProduct();
				this.clickProject();
			},
			filter: function(backlog) {
				if(this.useDateLimit) {
					if(this.currentDate < backlog.startDate) {
						return false;
					}
					if(this.currentDate > backlog.endDate) {
						return false;
					}
				}
				return true;
			},
			setDateLimit: function() {
				this.useDateLimit = true;
				this.reRender();
			}, 
			unsetDateLimit: function() {
				this.useDateLimit = false;
				this.reRender();
			},
			getSelected: function(container) {
				var ret = [];
				container.find("option:selected").each(function() {
					var val = parseInt(this.value);
					if(val > 0) {
						ret.push(val);
					}
				});
				return ret;
			},
			isSelectAll: function(container) {
				return container.find("option[value=-1]").is(":selected");
			},
			selectAll: function(container) {
				var options = container.find("option").not("option[value=-1]").removeAttr("selected");
				if(container == this.projectContainer) this.iterationContainer.hide();
				this.auxFields.empty();
				var me = this;
				if(this.useDateLimit) {
					$.each(options, function() {
						var backlogId = $(this).attr("value");
						var field = $('<input type="hidden" />').attr("value",backlogId);
						if(container == me.projectContainer) {
							field.attr("name","projectIds");
						} else {
							field.attr("name","iterationIds");
						}
						field.appendTo(me.auxFields);
					});
				}
			},
			clickProduct: function() {
				this.projectContainer.empty();
				this.selectedProducts = this.getSelected(this.productContainer);
				this.renderBacklogSelector(this.projectContainer, this.selectedProducts, this.selectedProjects);
				if(this.isSelectAll(this.projectContainer)) {
					this.selectAll(this.projectContainer);
					this.iterationContainer.hide();
				}
			},
			clickProject: function() {
				this.iterationContainer.empty();
				this.selectedProjects = this.getSelected(this.projectContainer);
				if(this.isSelectAll(this.projectContainer)) {
					this.selectAll(this.projectContainer);
					this.iterationContainer.hide();
				} else {
					this.renderBacklogSelector(this.iterationContainer, this.selectedProjects, this.selectedIterations);
					if(this.isSelectAll(this.iterationContainer)) {
						this.selectAll(this.iterationContainer);
					}
				}
			},
			clickIteration: function() {
				this.selectedIterations = this.getSelected(this.iterationContainer);
				if(this.isSelectAll(this.iterationContainer)) {
					this.selectAll(this.iterationContainer);
				}
			},
			renderBacklogSelector: function(container, selectedItems, selectedInContainer) {
				container.empty();
				var selectAllOpt = false;
				if(selectedItems.length > 0) {
					var selectAll = $('<option/>').appendTo(container).html("<b>Select all</b>").attr("value",-1);
					selectAllOpt = ($.inArray(-1,selectedInContainer) != -1);
				}
				 
				var cnt = 0;
				var numSelected = 0;
				var me = this;
				$.each(selectedItems, function() { 
					var data = jsonDataCache.get("subBacklogs", {data: {backlogId: this}}, this);
					$.each(me.sortBacklogs(data), function() {
						if(me.filter(this)) {
							var opt = $('<option/>').appendTo(container).text(this.name).attr("value",this.id);
							if($.inArray(this.id,selectedInContainer) != -1 && !selectAllOpt) {
								opt.attr("selected","selected");
								numSelected++;
							}
						}
					});
					cnt++;
				});
				if((numSelected == 0 || selectAllOpt) && selectAll) {
					selectAll.attr("selected","selected");
				}
				if(cnt == 0) {
					container.empty();
					$('<option/>').appendTo(container).html("No backlogs found").attr("value",-2);
				}
				((selectedItems.length > 0) ? container.show() : container.hide());
			}
	}
	jQuery.fn.extend({
		backlogChooser: function(options) {
			var target = $(this);
			if(!target.data("backlogChooser")) {
				var chooser = new backlogChooser(this, options);
				target.data("backlogChooser", chooser);
			} else {
				var chooser = target.data("backlogChooser"); 
				if(options == "render") {
					chooser.reRender();
				} else if(options == "setDateLimit") {
					chooser.setDateLimit();
				} else if(options == "unsetDateLimit") {
					chooser.unsetDateLimit();
				}
			}
		}
	});
})(jQuery);