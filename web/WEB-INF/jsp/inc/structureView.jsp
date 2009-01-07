<%@ include file="./_taglibs.jsp"%>
<script type="text/javascript" src="static/js/interface.js"></script>

<script type="text/javascript">
$(document).ready(
		function()
		{
			tree = $('#myTree');
			$('li', tree.get(0)).each(
				function()
				{
					subbranch = $('ul', this);
					if (subbranch.size() > 0) {
						if (subbranch.eq(0).css('display') == 'none') {
							$(this).prepend('<img src="images/bullet_toggle_plus.png" width="16" height="16" class="expandImage" />');
						} else {
							$(this).prepend('<img src="images/bullet_toggle_minus.png" width="16" height="16" class="expandImage" />');
						}
					} else {
						$(this).prepend('<img src="images/spacer.gif" width="16" height="16" class="expandImage" />');
					}
				}
			);
			$('img.expandImage', tree.get(0)).click(
				function()
				{
					if (this.src.indexOf('spacer') == -1) {
						subbranch = $('ul', this.parentNode).eq(0);
						if (subbranch.css('display') == 'none') {
							subbranch.show();
							this.src = 'images/bullet_toggle_minus.png';
						} else {
							subbranch.hide();
							this.src = 'images/bullet_toggle_plus.png';
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
											$('img.expandImage', targetBranch.parentNode).eq(0).attr('src', 'images/bullet_toggle_minus.png');
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
							$('img.expandImage', oldParent.parentNode).src('images/spacer.gif');
							$(oldParent).remove();
						}
						expander = $('img.expandImage', this.parentNode);
						if (expander.get(0).src.indexOf('spacer') > -1)
							expander.get(0).src = 'images/bullet_toggle_minus.png';
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
		}
	);
jQuery.getJSON("getProductTopLevelBacklogItemsAsJson.action",
                { 'backlogId': <c:out value="${backlog.id}" /> },
                function(data, status) {
						//for(var x in data) {
						//	alert(data[x].id);
						//}
                });
</script>

<ul class="myTree">
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
</ul>


