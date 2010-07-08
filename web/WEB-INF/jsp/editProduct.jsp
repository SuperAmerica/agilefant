<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="backlog">

<script type="text/javascript" src="static/js/widgets/agilefantWidget.js"></script>



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
  $('#productContents').tabs({
    cookie: { name: 'agilefant-product-tabs' }
  });

  var controller = new ProductController({
    id: ${product.id},
    productDetailsElement: $("#backlogDetails"),
    projectListElement: $("#projects"),
    storyTreeElement: $('#storyTreeContainer'),
    hourEntryListElement: $("#backlogSpentEffort"),
    searchByTextElement: $('#searchByText'),
    backlogsElement: $('#backlogs'),
    tabs: $('#productContents')
  });
  if(Configuration.isTimesheetsEnabled()) {
  	$("#backlogInfo").bind('tabsselect', function(event, ui) {
	    if (ui.index == 1) {
      	controller.selectSpentEffortTab();
    	}
  	});
  }

  /*
   * PRODUCT ACTIONS MENU
   */
  var actionMenu = $('<ul class="actionCell backlogActions"/>').appendTo(document.body).hide();
  $('<li/>').text('Spent effort').click(function() {
    closeMenu();
    controller.openLogEffort();
  }).appendTo(actionMenu);

  $('<li/>').text('Delete').click(function() {
    closeMenu();
    controller.removeProduct();
  }).appendTo(actionMenu);

  
  var closeMenu = function() {
    actionMenu.fadeOut('fast');
    actionMenu.menuTimer('destroy');
  };
  var openMenu = function(element) {
    var pos = $("#productActions").offset();
    actionMenu.css({
      position: 'absolute',
      top: pos.top + 20,
      left: pos.left
    });

    actionMenu.show();
    actionMenu.menuTimer({
      closeCallback: function() {
        closeMenu();
      }
    });
  };

 $('#productActions').click(function() { openMenu(); });

});

</script>

<div style="margin-top: 3em;" class="structure-main-block project-color-header" id="productContents">
<ul class="backlogTabs">
  <li class=""><a href="#storyTreeContainer"><span><img
        alt="Story tree" src="static/img/story_tree.png" /> Story tree</span></a></li>
  <li class=""><a href="#projects"><span><img
        alt="Projects" src="static/img/backlog.png" /> Projects</span></a></li>
  <li class=""><a href="#backlogs"><span><img
        alt="Backlogs" src="static/img/backlog.png" /> Backlogs</span></a></li>
  <li id="searchByText" style="float: right;"> </li>
</ul>

<form onsubmit="return false;">
  <div class="details" id="storyTreeContainer" style="position: relative;"></div>
  <div class="details" id="projects"></div>
  <div class="details" id="backlogs">
    <div style="text-align:center; vertical-align: middle;">
      <img src="static/img/pleasewait.gif" style="display: inline-block; vertical-align: middle;"/> Loading...
    </div>
  </div>
</form>

</div>



</struct:htmlWrapper>
