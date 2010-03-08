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
  var controller = new ProductController({
    id: ${product.id},
    productDetailsElement: $("#backlogDetails"),
    projectListElement: $("#projects"),
    storyTreeElement: $('#stories'),
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

<style type="text/css">
.search {
  background: #f0f;
}
</style>
<form onsubmit="return false;">
<div class="ui-widget-content ui-corner-all structure-main-block dynamictable" id="storyTree">
  <div class="ui-widget-header ui-corner-all dynamictable-caption-block dynamictable-caption">Story tree<div id="searchByText" style="float: right; margin: -2px 5px 0 0;"> </div></div>
  <div id="stories"></div>
</div>
</form>

<form onsubmit="return false;"><div id="projects" class="structure-main-block">&nbsp;</div></form>



</struct:htmlWrapper>
