<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">

<aef:backlogBreadCrumb backlog="${product}" />

<div class="structure-main-block" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li id="productActions" class="ui-state-disabled dynamictable-captionaction ui-corner-all" style="float: right; opacity: 1 !important; filter: alpha(opacity = 100) !important; border-width: 1px !important;">
    Actions
  </li>
</ul>

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
</div>


<script type="text/javascript">
$(document).ready(function() {
  $("#backlogInfo").tabs();
  $('#productContents').tabs();

  var controller = new ProductController({
    id: ${product.id},
    productDetailsElement: $("#backlogDetails"),
    projectListElement: $("#projects"),
    storyTreeElement: $('#storyTreeContainer'),
    hourEntryListElement: $("#backlogSpentEffort"),
    searchByTextElement: $('#searchByText')
  });
  if(Configuration.isTimesheetsEnabled()) {
  	$("#backlogInfo").bind('tabsselect', function(event, ui) {
	    if (ui.index == 1) {
      	controller.selectSpentEffortTab();
    	}
  	});
  }

  $('#productActions').click(function() {
    var menu = $('<ul class="actionCell backlogActions"/>').appendTo(document.body);
    var pos = $(this).offset();
    menu.css({
      "top": pos.top + 20,
      "left": pos.left
    });
    
    var closeMenu = function() {
      menu.remove();
    };

    $('<li/>').text('Delete').click(function() {
      closeMenu();
      controller.removeProduct();
    }).appendTo(menu);
    
    menu.mouseleave(function() {
      closeMenu();
    });
  });
});

</script>


<div style="margin-top: 3em;" class="structure-main-block project-color-header" id="productContents">
<ul class="backlogTabs">
  <li class=""><a href="#storyTreeContainer"><span><img
        alt="Edit" src="static/img/info.png" /> Story tree</span></a></li>
  <li class=""><a href="#projects"><span><img
        alt="Edit" src="static/img/backlog.png" /> Projects</span></a></li>
  <li id="searchByText" style="float: right;"> </li>
</ul>

<form onsubmit="return false;">
  <div class="details" id="storyTreeContainer"></div>
  <div class="details" id="projects"></div>
</form>

</div>



</struct:htmlWrapper>
