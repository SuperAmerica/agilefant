<%@ include file="./_taglibs.jsp"%>
<script type="text/javascript" src="static/js/interface.js"></script>

<script type="text/javascript">

function addChildList(liItem)
{
	var parentId = $('input.hiddenId', $(liItem).get(0)).attr('value');
	var listItem = $(liItem);
	$.post('hasChildrenBlis.action', {"backlogItemId": parentId}, function(data,status) {
		if(data == "true") {
			$('img.expandImage', listItem.get(0)).attr('src', 'static/img/arrow_right.png');
			$('img.deleteImage', listItem.get(0)).remove();
		} else {
			
		}				
	});
}

function clicked(img)
{
	if (img.src.indexOf('empty') == -1) {
		subbranch = $('ul', img.parentNode).eq(0);
		if (!(subbranch.get(0).hasChildNodes())) {				
			jQuery.getJSON("getBacklogItemChildrenAsJSON.action",{ 'backlogItemId': $('input', img.parentNode).eq(0).attr('value') }, function(data) {
				subbranch.show();
				img.src = 'static/img/arrow_down.png';
				var list = $('ul', img.parentNode).eq(0);
				for(var x = 0; x < data.length; x++) {						
					var item = 	$('<li class="treeItem" style="list-style-type:none;"/>').appendTo(list);
					var span = $('<span class="textHolder" />').appendTo(item);
					var icon = $('<img src="static/img/Remove.png" alt="Delete" title="Delete" style="cursor: pointer;" class="deleteImage" />').appendTo(item);
					var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+<c:out value="${backlog.id}" />+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" /></a>').appendTo(item);
					var hidden = $('<input type="hidden" class="hiddenId"/>').appendTo(item);
					var subList = $('<ul style="display: none;" />').appendTo(item);
					$(item).prepend('<img src="static/img/empty.png" width="16" height="16" class="expandImage" />');						
					span.text(data[x].name);
					create.click(function() {
				        if ($("div.createDialogWindow").length == 0) {
				            openCreateDialog($(this));
				        }
				        return false;
				    });
					hidden.attr('value', data[x].id);
					
															
				}
				$('li', list.get(0)).each(
						function() {
							addChildList(this);
						}
					);
				$('img.expandImage', list).click(
						function()
						{
							clicked(this);
						}
					);
				$('img.deleteImage', list).click(
						function()
						{
							var confirmDelete = confirm("Sure you want to delete this backlog item?");
							if (confirmDelete) {
								id = $('input.hiddenId', this.parentNode).eq(0).attr('value');
								$.post("deleteBacklogItem.action",
					                { "backlogItemId": id }
					            );
					            $(this.parentNode).remove();
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
	
							this.setAttribute("style", "background-color:#747170");
							},
							onout			: function()
							{
								this.setAttribute("style", "background-color:transparent");
							},
							ondrop			: function(dropped)
							{
								this.setAttribute("style", "background-color:transparent");
								var childId = $('input.hiddenId', dropped).eq(0).attr('value');								
								var parentId = $('input.hiddenId', this.parentNode).eq(0).attr('value');
								if(this.parentNode == dropped) {
									return;
								}
								subbranch = $('ul', this.parentNode).eq(0);
								oldParent = dropped.parentNode.parentNode;
								expander = $('img.expandImage', this.parentNode);
								textHolder = $('span.textHolder', oldParent).eq(0);
								subbranch.append(dropped);
								oldBranches = $('li', oldParent);
								if (oldBranches.size() == 0) {
									$('img.expandImage', oldParent).attr('src', 'static/img/empty.png');
									textHolder.after('<img src="static/img/Remove.png" alt="Delete" title="Delete" style="cursor: pointer;" class="deleteImage" />');
									$('img.deleteImage', oldParent).eq(0).click(
											function()
											{
												var confirmDelete = confirm("Sure you want to delete this backlog item?");
												if (confirmDelete) {
													id = $('input.hiddenId', oldParent).eq(0).attr('value');
													$.post("deleteBacklogItem.action",
										                { "backlogItemId": id }
										            );
													$(oldParent).remove();
												}
											}
										);
								}
								if (expander.get(0).src.indexOf('empty') > -1) {
									expander.get(0).src = 'static/img/arrow_right.png';
									$('img.deleteImage', this.parentNode).eq(0).remove();
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
		
			});		
			
		} else {
			subbranch.hide();
			img.src = 'static/img/arrow_right.png';
			$('li', subbranch).remove();
		} 
	}

}

$(document).ready(
		function() {
jQuery.getJSON("getProductTopLevelBacklogItemsAsJson.action",
        { 'backlogId': <c:out value="${backlog.id}" /> },
        function(data, status) {
        	var list = 	$('#myTree');
				for(var x = 0; x < data.length; x++) {						
					var item = 	$('<li class="treeItem" style="list-style-type:none;"/>').appendTo(list);
					var span = $('<span class="textHolder" />').appendTo(item);
					var icon = $('<img src="static/img/Remove.png" alt="Delete" title="Delete" style="cursor: pointer;" class="deleteImage" />').appendTo(item);
					var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+<c:out value="${backlog.id}" />+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" /></a>').appendTo(item);
					var hidden = $('<input type="hidden" class="hiddenId"/>').appendTo(item);						
					var subList = $('<ul style="display: none;" />').appendTo(item);
					$(item).prepend('<img src="static/img/empty.png" width="16" height="16" class="expandImage" />');
					span.text(data[x].name);
					create.click(function() {
				        if ($("div.createDialogWindow").length == 0) {
				            openCreateDialog($(this));
				        }
				        return false;
				    });
					hidden.attr('value', data[x].id);
				}

				$('li', list.get(0)).each(
						function() {
							addChildList(this);
						}
					);
				$('img.expandImage', list).click(
						function()
						{
							clicked(this);
						}
					);
				$('img.deleteImage', list).click(
						function()
						{
							var confirmDelete = confirm("Sure you want to delete this backlog item?");
							if (confirmDelete) {
								id = $('input.hiddenId', this.parentNode).eq(0).attr('value');
								$.post("deleteBacklogItem.action",
					                { "backlogItemId": id }
					            );
								$(this.parentNode).remove();
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

							this.setAttribute("style", "background-color:#747170");
							},
							onout			: function()
							{
								this.setAttribute("style", "background-color:transparent");
							},
							ondrop			: function(dropped)
							{
								this.setAttribute("style", "background-color:transparent");
								var childId = $('input.hiddenId', dropped).eq(0).attr('value');								
								var parentId = $('input.hiddenId', this.parentNode).eq(0).attr('value');
								if(this.parentNode == dropped) {
									return;
								}
								subbranch = $('ul', this.parentNode).eq(0);
								oldParent = dropped.parentNode.parentNode;
								expander = $('img.expandImage', this.parentNode);
								textHolder = $('span.textHolder', oldParent).eq(0);
								subbranch.append(dropped);
								oldBranches = $('li', oldParent);
								if (oldBranches.size() == 0) {
									$('img.expandImage', oldParent).eq(0).attr('src', 'static/img/empty.png');
									textHolder.after('<img src="static/img/Remove.png" alt="Delete" title="Delete" style="cursor: pointer;" class="deleteImage" />');
									$('img.deleteImage', oldParent).eq(0).click(
											function()
											{
												var confirmDelete = confirm("Sure you want to delete this backlog item?");
												if (confirmDelete) {
													id = $('input.hiddenId', oldParent).eq(0).attr('value');
													$.post("deleteBacklogItem.action",
										                { "backlogItemId": id }
										            );
													$(oldParent).remove();
												}
											}
										);
								}								
								if (expander.get(0).src.indexOf('empty') > -1) {
									expander.get(0).src = 'static/img/arrow_right.png';
									$('img.deleteImage', this.parentNode).eq(0).remove();
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
        });
		}
);
</script>

<ul id="myTree">
</ul>


