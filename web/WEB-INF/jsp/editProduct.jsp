<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">

<h2>Product: <c:out value="${product.name}"/></h2>

<div class="structure-main-block" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class="ui-state-disabled dynamictable-captionaction ui-corner-all" style="float: right; opacity: 1 !important; filter: alpha(opacity = 100) !important; border-width: 1px !important;">
    Actions
  </li>
</ul>

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
<div class="details" id="backlogHistory"></div>
</div>


<script type="text/javascript">
$(document).ready(function() {
  $("#backlogInfo").tabs();
  var controller = new ProductController({
    id: ${product.id},
    productDetailsElement: $("#backlogDetails"),
    projectListElement: $("#projects"),
    storyListElement: $('#stories'),
    hourEntryListElement: $("#backlogSpentEffort")
  });
  if(Configuration.isTimesheetsEnabled()) {
  	$("#backlogInfo").bind('tabsselect', function(event, ui) {
	    if (ui.index == 1) {
      	controller.selectSpentEffortTab();
    	}
  	});
  }

   
  var storyTreeController = new StoryTreeController(
    ${product.id}, "product", $('#storyTree'),{});
  storyTreeController.initTree();

  window.setInterval(function() {
    storyTreeController.refresh();
  }, 120000);

});
</script>

<style type="text/css">
.search {
  background: #f0f;
}
</style>

<div class="ui-widget-content ui-corner-all structure-main-block dynamictable">
  <div class="ui-widget-header ui-corner-all dynamictable-caption-block dynamictable-caption">Story tree</div>
  <form onsubmit="return false;"><div id="storyTree">&nbsp;</div></form>
</div>

<form onsubmit="return false;"><div id="projects" class="structure-main-block">&nbsp;</div></form>



</struct:htmlWrapper>
