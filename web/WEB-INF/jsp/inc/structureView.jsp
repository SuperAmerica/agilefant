<%@ include file="./_taglibs.jsp"%>
<script type="text/javascript" src="static/js/interface.js"></script>

<script type="text/javascript">

function addActionsToElements(list)
{
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
		            subbranches = $('li', this.parentNode.parentNode.parentNode);
					if (subbranches.size() == 1) {
						$('img.expandImage', this.parentNode.parentNode.parentNode).attr('src', 'static/img/treeempty.png');
						$('img.deleteImage', this.parentNode.parentNode.parentNode).show();
					}
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
						$('img.expandImage', oldParent).attr('src', 'static/img/treeempty.png');
						$('img.deleteImage', oldParent).show();
						$('ul', oldParent).eq(0).hide();
					}
					if (expander.get(0).src.indexOf('empty') > -1) {
						$('ul', this.parentNode).eq(0).show();
						expander.get(0).src = 'static/img/treeminus.png';
						$('img.deleteImage', this.parentNode).eq(0).hide();
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
}

function addChildList(liItem)
{
	var parentId = $('input.hiddenId', $(liItem).get(0)).attr('value');
	var listItem = $(liItem);
	$.post('hasChildrenBlis.action', {"backlogItemId": parentId}, function(data,status) {
		if(data == "true") {
			$('img.expandImage', listItem.get(0)).attr('src', 'static/img/treeplus.png');
			
		} else {
			$('img.deleteImage', listItem.get(0)).show();
		}				
	});
}

function clicked(img)
{
	if (img.src.indexOf('empty') == -1) {
		subbranch = $('ul', img.parentNode).eq(0);
		if (img.src.indexOf('minus') == -1) {
			subbranch.show();
			img.src = 'static/img/treeminus.png';
			if (!(subbranch.get(0).hasChildNodes())) {				
				jQuery.getJSON("getBacklogItemChildrenAsJSON.action",{ 'backlogItemId': $('input', img.parentNode).eq(0).attr('value') }, function(data) {
					var list = $('ul', img.parentNode).eq(0);
					for(var x = 0; x < data.length; x++) {						
						var item = 	$('<li class="treeItem" />').appendTo(list);
						var span = $('<span class="textHolder" />').appendTo(item);
						var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+<c:out value="${backlog.id}" />+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" title="Add child" /></a>').appendTo(item);
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
					}
					addActionsToElements(list);
				});
			}			
		} else {
			subbranch.hide();
			img.src = 'static/img/treeplus.png';
		} 
	}

}

function initTree() {
	jQuery.getJSON("getProductTopLevelBacklogItemsAsJson.action",
       	{ 'backlogId': <c:out value="${backlog.id}" /> },
        function(data, status) {
        	var list = 	$('#myTree');
				for(var x = 0; x < data.length; x++) {						
					var item = 	$('<li class="treeItem" />').appendTo(list);
					var span = $('<span class="textHolder" />').appendTo(item);
					var create = $('<a href="ajaxCreateBacklogItem.action?backlogId='+<c:out value="${backlog.id}" />+'&parentId='+data[x].id+'" class="openCreateDialog openBacklogItemDialog addImage" onclick="return false;"><img src="static/img/Add.png" title="Add child" /></a>').appendTo(item);
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
				}
				addActionsToElements(list);
        }
	);
}
</script>

<ul id="myTree">
</ul>


