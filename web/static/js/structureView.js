(function($) {
	var structureView = function(opt) {
        var me = this;
        var options = {
                backlogId: null,
        };
        jQuery.extend(options, opt);
        this.options = options;
	};
	
	structureView.prototype = {
		/**
         * Check if structure view has been generated already.
         */
		checkStatus: function(opt) {
		var me = this;
			var list = 	$('#myTree');
			var listItemsInMyTree = $('li', list);
			if (listItemsInMyTree.size() == 0) {
				me.addActionsToContainers();
				me.initTree();
				me.initDoneTree();				
			}
		},
		initTree: function(opt) {
			var me = this;
			jQuery.getJSON("getProductTopLevelBacklogItemsAsJson.action",
		       	{ 'backlogId': this.options.backlogId },
		        function(data, status) {
		        	var list = 	$('#myTree');
						for(var x = 0; x < data.length; x++) {						
							var item = 	$('<li class="treeItem" />').appendTo(list);
							var span = $('<span class="textHolder" />').appendTo(item);
							var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+me.options.backlogId+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" title="Add child" /></a>').appendTo(item);
							var icon = $('<img src="static/img/Remove.png" alt="Delete" title="Delete" class="deleteImage" />').appendTo(item);
							var hidden = $('<input type="hidden" class="hiddenId"/>').appendTo(item);						
							var subList = $('<ul />').appendTo(item);
							$(item).prepend('<img src="static/img/treeempty.png" class="expandImage" />');
							span.text(data[x].name);
							create.click(function() {
						        if ($("div.createDialogWindow").length == 0) {
						            openCreateDialog($(this));
						        }
						        return false;
						    });
							hidden.attr('value', data[x].id);
							me.addChildList(item, data[x].hasChilds);
						}
						me.addActionsToElements(list);
		        }
			);
		},
		initDoneTree: function(opt) {
			var me = this;
			jQuery.getJSON("getProductDoneTopLevelBacklogItemsAsJson.action",
		       	{ 'backlogId': this.options.backlogId },
		        function(data, status) {
		        	var list = 	$('#doneTree');
						for(var x = 0; x < data.length; x++) {						
							var item = 	$('<li class="treeItem" />').appendTo(list);
							var span = $('<span class="textHolder" />').appendTo(item);
							var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+me.options.backlogId+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" title="Add child" /></a>').appendTo(item);
							var icon = $('<img src="static/img/Remove.png" alt="Delete" title="Delete" class="deleteImage" />').appendTo(item);
							var hidden = $('<input type="hidden" class="hiddenId"/>').appendTo(item);						
							var subList = $('<ul />').appendTo(item);
							$(item).prepend('<img src="static/img/treeempty.png" class="expandImage" />');
							span.text(data[x].name);
							create.click(function() {
						        if ($("div.createDialogWindow").length == 0) {
						            openCreateDialog($(this));
						        }
						        return false;
						    });
							hidden.attr('value', data[x].id);
							me.addChildList(item, data[x].hasChilds);
						}
						me.addActionsToElements(list);
		        }
			);
		},
		clicked: function(img) {
			var me = this;
			if (img.src.indexOf('empty') == -1) {
				subbranch = $('ul', img.parentNode).eq(0);
				if (img.src.indexOf('minus') == -1) {
					subbranch.show();
					if (img.src.indexOf('last') == -1){
						img.src = 'static/img/treeminus.png';
					} else {
						img.src = 'static/img/treeminuslast.png';
					}
					if (!(subbranch.get(0).hasChildNodes())) {				
						jQuery.getJSON("getBacklogItemChildrenAsJSON.action",{ 'backlogItemId': $('input', img.parentNode).eq(0).attr('value') }, function(data) {
							var list = $('ul', img.parentNode).eq(0);
							for(var x = 0; x < data.length; x++) {						
								var item = 	$('<li class="treeItem" />').appendTo(list);
								var span = $('<span class="textHolder" />').appendTo(item);
								var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+me.options.backlogId+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" title="Add child" /></a>').appendTo(item);
								var icon = $('<img src="static/img/Remove.png" alt="Delete" title="Delete" class="deleteImage" />').appendTo(item);
								var hidden = $('<input type="hidden" class="hiddenId"/>').appendTo(item);
								var subList = $('<ul />').appendTo(item);
								$(item).prepend('<img src="static/img/treeempty.png" class="expandImage" />');						
								span.text(data[x].name);
								create.click(function() {
							        if ($("div.createDialogWindow").length == 0) {
							            openCreateDialog($(this));
							        }
							        return false;
							    });
								hidden.attr('value', data[x].id);
								me.addChildList(item, data[x].hasChilds);
							}
							me.addActionsToElements(list);
						});
					}			
				} else {
					subbranch.hide();
					if (img.src.indexOf('last') == -1) {
						img.src = 'static/img/treeplus.png';
					} else {
						img.src = 'static/img/treepluslast.png';
					}
				} 
			}

		},
		addActionsToElements: function(list) {
			var me = this;
			var lastItem = $('li:last', list);
			$('ul', lastItem).addClass('lastItem');
			var lastImage = $('img.expandImage', lastItem);
			if (lastImage.attr('src') == 'static/img/treeempty.png') {
				lastImage.attr('src', 'static/img/treeemptylast.png');
			}
			if (lastImage.attr('src') == 'static/img/treeplus.png') {
				lastImage.attr('src', 'static/img/treepluslast.png');
			}
			$('img.expandImage', list).click(
					function()
					{
						me.clicked(this);
					}
				);
			$('img.deleteImage', list).click(
					function()
					{
						var me = this;
						var confirmDelete = confirm("Sure you want to delete this backlog item?");
						if (confirmDelete) {
							id = $('input.hiddenId', me.parentNode).eq(0).attr('value');
							$.post("deleteBacklogItem.action",
				                { "backlogItemId": id }
				            );
				            var subbranches = $('li', me.parentNode.parentNode.parentNode);
				            var subList = $('ul', me.parentNode.parentNode.parentNode).eq(0);
							if (subbranches.size() == 1) {			
								if(subList.hasClass('lastItem')) {
									$('img.expandImage', me.parentNode.parentNode.parentNode).attr('src', 'static/img/treeemptylast.png');
								} else {
									$('img.expandImage', me.parentNode.parentNode.parentNode).attr('src', 'static/img/treeempty.png');
								}
								$('img.deleteImage', me.parentNode.parentNode.parentNode).show();
								subList.hide();
							}
							var deleteList = $('ul', me.parentNode).eq(0);
							var current = $(me.parentNode);
				            var newLastItem = $(current).prev();
				            $(me.parentNode).remove();				
				            if (subbranches.size() > 1 && deleteList.hasClass('lastItem')) {
								$('ul', newLastItem).eq(0).addClass('lastItem');
								var expander = $('img.expandImage', newLastItem).eq(0);
								if (expander.attr('src') == 'static/img/treeminus.png') {
									expander.attr('src', 'static/img/treeminuslast.png');
								} 
								if (expander.attr('src') == 'static/img/treeplus.png') {
									expander.attr('src', 'static/img/treepluslast.png');
								}
								if (expander.attr('src') == 'static/img/treeempty.png') {
									expander.attr('src', 'static/img/treeemptylast.png');
								}
							}
						}
					}
				);
			$('span.textHolder', list).Droppable(
					{ 
						accept			: 'treeItem',
						hoverclass		: 'dropOver',
						activeclass		: 'fakeClass',
						tollerance		: 'pointer',
						onhover			: function(dragged)
						{
						expImg = $('img.expandImage', this.parentNode);
						if (expImg.get(0).src.indexOf('plus') > -1) {
							$('img.expandImage', this.parentNode).eq(0).click();
							this.expanderTime = window.setTimeout(
									function()
									{
										$.recallDroppables();
									},
									500
								)
						}
						this.setAttribute("style", "background-color:#747170");
						},
						onout			: function()
						{
							this.setAttribute("style", "background-color:transparent");
						},
						ondrop			: function(dropped)
						{
							var me = this;
							me.setAttribute("style", "background-color:transparent");
							var childId = $('input.hiddenId', dropped).eq(0).attr('value');								
							var parentId = $('input.hiddenId', me.parentNode).eq(0).attr('value');
							if(me.parentNode == dropped) {
								return;
							}
							var subbranch = $('ul', me.parentNode).eq(0);
							var subItems = $('li', subbranch);
							var oldParent = $(dropped.parentNode.parentNode);
							var expander = $('img.expandImage', me.parentNode).eq(0);
							
							var current = $(dropped);
				            var newLastItem = $(current).prev();
				            var droppedList = $('ul', dropped).eq(0);
							$(subbranch).append(dropped);
							var oldBranches = $('li', oldParent);
							var temp = $('span.textHolder', newLastItem).eq(0).text();
							if (droppedList.hasClass('lastItem')) { 
								if (oldBranches.size() > 0) {
									$('ul', newLastItem).eq(0).addClass('lastItem');
									var newLastExpander = $('img.expandImage', newLastItem).eq(0);
									if (newLastExpander.attr('src') == 'static/img/treeminus.png') {
										$(newLastExpander).attr('src', 'static/img/treeminuslast.png');
									} 
									if (newLastExpander.attr('src') == 'static/img/treeplus.png') {
										$(newLastExpander).attr('src', 'static/img/treepluslast.png');
									}
									if (newLastExpander.attr('src') == 'static/img/treeempty.png') {
										$(newLastExpander).attr('src', 'static/img/treeemptylast.png');
									}
								}
							} else {
								droppedList.addClass('lastItem');
								var droppedExpander = $('img.expandImage', dropped).eq(0);
								if (droppedExpander.attr('src') == 'static/img/treeminus.png') {
									$(droppedExpander).attr('src', 'static/img/treeminuslast.png');
								}
								if (droppedExpander.attr('src') == 'static/img/treeplus.png') {
									$(droppedExpander).attr('src', 'static/img/treepluslast.png');
								} 
								if (droppedExpander.attr('src') == 'static/img/treeempty.png') {
									$(droppedExpander).attr('src', 'static/img/treeemptylast.png');
								}
								
							}
							var oldLastItem = $(dropped).prev();
							if (subItems.size() > 0) {
								$('ul', oldLastItem).eq(0).removeClass('lastItem');
								var oldLastItemExpander = $('img.expandImage', oldLastItem).eq(0);
								if (oldLastItemExpander.attr('src') == 'static/img/treeminuslast.png') {
									$(oldLastItemExpander).attr('src', 'static/img/treeminus.png');
								}
								if (oldLastItemExpander.attr('src') == 'static/img/treepluslast.png') {
									$(oldLastItemExpander).attr('src', 'static/img/treeplus.png');
								} 
								if (oldLastItemExpander.attr('src') == 'static/img/treeemptylast.png') {
									$(oldLastItemExpander).attr('src', 'static/img/treeempty.png');
								}
							}
							
							if (oldBranches.size() == 0) {
								var subList = $('ul', oldParent).eq(0);
								if (subList.hasClass('lastItem')){
									$('img.expandImage', oldParent).attr('src', 'static/img/treeemptylast.png');
								} else {
									$('img.expandImage', oldParent).attr('src', 'static/img/treeempty.png');
								}
								$('img.deleteImage', oldParent).show();
								$(subList).hide();
							}
							if (expander.get(0).src.indexOf('empty') > -1) {
								$('ul', me.parentNode).eq(0).show();
								if(expander.get(0).src.indexOf('last') > -1) {
									expander.get(0).src = 'static/img/treeminuslast.png';
								} else {
									expander.get(0).src = 'static/img/treeminus.png';
								}
								$('img.deleteImage', me.parentNode).eq(0).hide();
							}
							$.post("ajaxChangeBacklogItemParent.action",
					                { "childId": childId, "parentId": parentId }
					            );
						}
					}
				);
				$('li.treeItem', list.get(0)).Draggable(
					{
						revert		: true,
						autoSize		: true,
						ghosting			: true
					}
				);
		},
		addActionsToContainers: function(opt) {
			var me = this;
						
			$('#undone').Droppable(
					{ 
						accept			: 'treeItem',
						hoverclass		: 'dropOver',
						activeclass		: 'fakeClass',
						tollerance		: 'pointer',
						onhover			: function(dragged)
						{
						
						this.setAttribute("style", "background-color:#747170");
						},
						onout			: function()
						{
							this.setAttribute("style", "background-color:transparent");
						},
						ondrop			: function(dropped)
						{
							var me = this;
							me.setAttribute("style", "background-color:transparent");
							var childId = $('input.hiddenId', dropped).eq(0).attr('value');								
							var parentId = -1;
							if(me.parentNode == dropped) {
								return;
							}
							var subbranch = $('#myTree').eq(0);
							var oldParent = $(dropped.parentNode.parentNode);						
							var current = $(dropped);
				            var newLastItem = $(current).prev();
				            var droppedList = $('ul', dropped).eq(0);
							$(subbranch).append(dropped);
							var oldBranches = $('li', oldParent);
							var temp = $('span.textHolder', newLastItem).eq(0).text();
							if (droppedList.hasClass('lastItem')) { 
								if (oldBranches.size() > 0) {
									$('ul', newLastItem).eq(0).addClass('lastItem');
									var newLastExpander = $('img.expandImage', newLastItem).eq(0);
									if (newLastExpander.attr('src') == 'static/img/treeminus.png') {
										$(newLastExpander).attr('src', 'static/img/treeminuslast.png');
									} 
									if (newLastExpander.attr('src') == 'static/img/treeplus.png') {
										$(newLastExpander).attr('src', 'static/img/treepluslast.png');
									}
									if (newLastExpander.attr('src') == 'static/img/treeempty.png') {
										$(newLastExpander).attr('src', 'static/img/treeemptylast.png');
									}
								}
							} else {
								droppedList.addClass('lastItem');
								var droppedExpander = $('img.expandImage', dropped).eq(0);
								if (droppedExpander.attr('src') == 'static/img/treeminus.png') {
									$(droppedExpander).attr('src', 'static/img/treeminuslast.png');
								}
								if (droppedExpander.attr('src') == 'static/img/treeplus.png') {
									$(droppedExpander).attr('src', 'static/img/treepluslast.png');
								} 
								if (droppedExpander.attr('src') == 'static/img/treeempty.png') {
									$(droppedExpander).attr('src', 'static/img/treeemptylast.png');
								}
								
							}
							var oldLastItem = $(dropped).prev();							
							
							if (oldBranches.size() == 0) {
								var subList = $('ul', oldParent).eq(0);
								if (subList.hasClass('lastItem')){
									$('img.expandImage', oldParent).attr('src', 'static/img/treeemptylast.png');
								} else {
									$('img.expandImage', oldParent).attr('src', 'static/img/treeempty.png');
								}
								$('img.deleteImage', oldParent).show();
								$(subList).hide();
							}
							
							$.post("ajaxChangeBacklogItemParent.action",
					                { "childId": childId, "parentId": parentId }
					            );
							
							
						
						}
					}
				);
		},
		addChildList: function(liItem, hasChildren) {
			var listItem = $(liItem);
			if(hasChildren == true) {
				$('img.expandImage', listItem.get(0)).attr('src', 'static/img/treeplus.png');				
			} else {
				$('img.deleteImage', listItem.get(0)).show();
			}				
		}
	};
	jQuery.fn.extend({
        /**
         * Call this for the link that should open the structure view.
         */
        structureView: function(opt) {
            var sv = new structureView(opt);
            $(this).click(function() { sv.checkStatus(); return false; })
            return this;
        }
    });
})(jQuery);