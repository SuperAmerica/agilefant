<%@ include file="./_taglibs.jsp"%>
<script type="text/javascript" src="static/js/interface.js"></script>

<script type="text/javascript">
$(document).ready(
		function() {
jQuery.getJSON("getProductTopLevelBacklogItemsAsJson.action",
        { 'backlogId': <c:out value="${backlog.id}" /> },
        function(data, status) {
        	var list = 	$('#myTree');
				for(var x = 0; x < data.length; x++) {						
					var item = 	$('<li class="treeItem" style="list-style-type:none;"/>').appendTo(list);
					var img = $('<img src="static/img/backlog.png" class="folderImage" />').appendTo(item);
					var span = $('<span class="textHolder" />').appendTo(item);
					var hidden = $('<input type="hidden" class="hiddenId"/>').appendTo(item);						
					span.text(data[x].name);
					hidden.text(data[x].id);
							
					//jQuery.getJSON("getBacklogItemChildrenAsJSON.action",
        				//{ 'backlogItemId': data[x].id },
        				//function(data, status) {
							//for(var y = 0; y < data.length; y++) {
								//var subList = $('<ul style="display: none;" />').appendTo(item);
								//var subItem = 	$('<li class="treeItem" />').appendTo(subList);
								//var subImg = $('<img src="static/img/backlog.png" class="folderImage" />').appendTo(subItem);
								//var subSpan = $('<span class="textHolder" />').appendTo(subItem);						
								//subSpan.text(data[y].name);		
							//}
        				//});
				}
				//Not working for some reason
				$('li', list.get(0)).each(
						function()
						{
							var parentId = $('input.hiddenId', this).text();
							var t = $(this);
							$.post('hasChildren.action', {"backlogItemId": parentId}, function(data,status) {
									
								t.get(0).firstChild.src="static/img/plus.png";
									var subList = $('<ul style="display: none;" />').appendTo(t);
									
								
							});
						}
					);
				$('li', list.get(0)).each(
						function()
						{
							subbranch = $('ul', this);
							if (subbranch.size() > 0) {
								if (subbranch.eq(0).css('display') == 'none') {
									$(this).prepend('<img src="static/img/plus.png" width="16" height="16" class="expandImage" />');
								} else {
									$(this).prepend('<img src="static/img/minus.png" width="16" height="16" class="expandImage" />');
								}
							} else {
								$(this).prepend('<img src="static/img/corner.png" width="16" height="16" class="expandImage" />');
							}
						}
					);
				$('img.expandImage', list.get(0)).click(
						function()
						{
							if (this.src.indexOf('corner') == -1) {
								subbranch = $('ul', this.parentNode).eq(0);
								if (subbranch.css('display') == 'none') {
									subbranch.show();
									this.src = 'static/img/minus.png';
								} else {
									subbranch.hide();
									this.src = 'static/img/plus.png';
								}
							}
						}
					);
					$('span.textHolder').Droppable(
						{
							accept			: 'treeItem',
							hoverclass		: 'dropOver',
							activeclass		: 'fakeClass',
							tollerance		: 'pointer',
							onhover			: function(dragged)
							{
								if (!this.expanded) {
									subbranches = $('ul', this.parentNode);
									if (subbranches.size() > 0) {
										subbranch = subbranches.eq(0);
										this.expanded = true;
										if (subbranch.css('display') == 'none') {
											var targetBranch = subbranch.get(0);
											this.expanderTime = window.setTimeout(
												function()
												{
													$(targetBranch).show();
													$('img.expandImage', targetBranch.parentNode).eq(0).attr('src', 'static/img/minus.png');
													$.recallDroppables();
												},
												500
											);
										}
									}
								}
							},
							onout			: function()
							{
								if (this.expanderTime){
									window.clearTimeout(this.expanderTime);
									this.expanded = false;
								}
							},
							ondrop			: function(dropped)
							{
								if(this.parentNode == dropped)
									return;
								if (this.expanderTime){
									window.clearTimeout(this.expanderTime);
									this.expanded = false;
								}								
								subbranch = $('ul', this.parentNode);
								if (subbranch.size() == 0) {
									$(this).after('<ul></ul>');
									subbranch = $('ul', this.parentNode);
								}
								oldParent = dropped.parentNode;
								subbranch.eq(0).append(dropped);
								oldBranches = $('li', oldParent);
								if (oldBranches.size() == 0) {
									$('img.expandImage', oldParent.parentNode).src('static/img/corner.png');
									$(oldParent).remove();
								}
								expander = $('img.expandImage', this.parentNode);
								if (expander.get(0).src.indexOf('corner') > -1) {
									expander.get(0).src = 'static/img/minus.png';
								}
								var childId = $('input.hiddenId:last', dropped).text();
								var parentId = $('input.hiddenId:last', this.parentNode).text();
								$.post("ajaxChangeBacklogItemParent.action",
						                { "childId": childId, "parentId": parentId }
						            );
							}
						}
					);
					$('li.treeItem').Draggable(
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
	<!--
	<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Folder 1</span>
		<ul>
			<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 1 1</span></li>
			<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 1 2</span>
				<ul style="display: none;">
					<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 1 2 1</span></li>

					<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 1 2 2</span></li>
					<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 1 2 3</span></li>
				</ul>
			</li>
		</ul>
	</li>
	<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Folder 2</span>
		<ul>
			<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 2 1</span></li>
			<li class="treeItem"><img src="static/img/backlog.png" class="folderImage" /><span class="textHolder">Subfolder 2 2</span></li>
		</ul>
	</li>
	-->
</ul>


