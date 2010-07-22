<%@ include file="./inc/_taglibs.jsp"%>

<%
org.joda.time.DateTime now = new org.joda.time.DateTime();
pageContext.setAttribute("defaultStartDate", now.minusWeeks(2));
pageContext.setAttribute("defaultEndDate", now.plusMonths(3));
%>


<struct:htmlWrapper navi="backlog">
<jsp:attribute name="includeInHeader">
  
<script type="text/javascript" src="static/js/widgets/agilefantWidget.js"></script>

<style type="text/css">
.placeholder {
  height: 1em;
  width: 100%;
  background: #ffc;
  border: 1px dashed #ccc; 
}
.widgetContainer {
  width: 33% !important;
}
.widget {
  min-width: 200px !important;
}
.widgetHeader, .widgetHeader > div {
  cursor: auto !important;
}
.productWidget {
  max-height: 600px !important;
}
.productWidget .widgetContent {
  max-height: 500px !important;
}
.storyList {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
.storyList li {
  list-style-type: none;
  margin-bottom: 3px;
  cursor: move;
  font-weight: normal !important;
}
.storyList li:hover {
  font-weight: bold;
}.
.ui-draggable-dragging {
  list-style-type: none !important;
  white-space: normal !important;
  max-width: 200px !important;
}
.ui-droppable-widget-hover {
  background-color: #e5f0f9;
  background-image: url('static/img/ui/ui-widget-droppable-gradient.png');
  background-repeat: repeat-x;
  font-weight: bold;
  color: #1d5987;
}
.timeframe {
  font-size: 8pt;
  margin-bottom: 5px;
}
.timeframe span {
  color: #666;
}
</style>
  
</jsp:attribute>

<jsp:body>

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
    backlogsElement: $('#leafStories'),
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

  /**
   * DATE FILTER TO LEAF STORIES
   */
 
  var datepickerOptions = {
    dateFormat: 'yy-mm-dd',
    numberOfMonths: 3,
    restrictInput: true
  };
 
  $('#startDateInput').datepicker(datepickerOptions);
  $('#endDateInput').datepicker(datepickerOptions);

  var filterByTime = function() {
    var filterStartDate = Date.fromString($('#startDateInput').val());
    var filterEndDate = Date.fromString($('#endDateInput').val()); 

    $('.scheduled').each(function() {
      var me = $(this);
      var startDate = Date.fromString(me.find('input[name=startDate]').val());
      var endDate = Date.fromString(me.find('input[name=endDate]').val());
      
      if (endDate.before(filterStartDate) || startDate.after(filterEndDate)) {
        me.hide();
      }
      else {
        me.show();
      }
    });

    /* Save to cookie */
    var cookieData = [ filterStartDate.getTime(), filterEndDate.getTime() ];
    jQuery.cookie('agilefant_productleafstories_timeframe_${product.id}', cookieData);
  };
  
  $('#filterByTime').click(function() {
    $('#productTimelineSlider').slider("values",[ Date.fromString($("#startDateInput").val()).getTime(), Date.fromString($("#endDateInput").val()).getTime() ]);
    filterByTime();
  });
  
  /* Get the dates from cookie */
  var timeframeCookie = jQuery.cookie('agilefant_productleafstories_timeframe_${product.id}');
  var start = new Date(${defaultStartDate.millis});
  var end = new Date(${defaultEndDate.millis});
  if (timeframeCookie) {
    var cookieData = timeframeCookie.split(',');
    start = new Date(parseInt(cookieData[0],10));
    end = new Date(parseInt(cookieData[1],10));

    /* Set the textfields to match the cookie */
    $('#startDateInput').val(start.asString().substr(0,10));
    $('#endDateInput').val(end.asString().substr(0,10));
  }
  
  
  <c:if test="${scheduleStart != null && scheduleEnd != null}">
  $('#productTimelineSlider').slider({
    range: true,
    min: ${scheduleStart.millis},
    max: ${scheduleEnd.millis},
    values: [ start.getTime(), end.getTime() ],
    step: 86400000,
    slide: function(event, ui) {
      var sliderStart = new Date(ui.values[0]);
      var sliderEnd = new Date(ui.values[1]);

      $('#startDateInput').val(sliderStart.asString().substr(0,10));
      $('#endDateInput').val(sliderEnd.asString().substr(0,10));

      filterByTime();
    }
  });

  </c:if>

});

</script>

<div style="margin-top: 3em;" class="structure-main-block project-color-header" id="productContents">
<ul class="backlogTabs">
  <li class=""><a href="#storyTreeContainer"><span><img
        alt="Story tree" src="static/img/story_tree.png" /> Story tree</span></a></li>
  <li class=""><a href="#leafStories"><span><img
        alt="Backlogs" src="static/img/leaf_stories.png" /> Leaf stories</span></a></li>
  <li class=""><a href="#projects"><span><img
        alt="Projects" src="static/img/backlog.png" /> Projects</span></a></li>
  <li id="searchByText" style="float: right;"> </li>
</ul>

<form onsubmit="return false;">
  <div class="details" id="storyTreeContainer" style="position: relative;"></div>
  <div class="details" id="leafStories" style="position: relative;">
    
    <c:choose>
    <c:when test="${empty product.children}">
      <div class="static">
        <h2>Product's leaf stories</h2>
        <p>This product has no projects</p>
      </div>
    </c:when>
    <c:otherwise>
      <div class="static">
        <h2>Product's leaf stories</h2>
        <div>
          Display backlogs from
          <input type="text" name="startDate" id="startDateInput" size="10" value='<joda:format value="${defaultStartDate}" pattern="YYYY-MM-dd" />'/> to
          <input type="text" name="endDate" id="endDateInput" size="10" value='<joda:format value="${defaultEndDate}" pattern="YYYY-MM-dd" />' />
          <button class="dynamics-button" id="filterByTime">Filter</button>
          
          <p style="font-size: 8pt; color: #666;">Product timeline</p>
          <div id="productTimelineSlider"> </div>
          
        </div>
      </div>
      <div class="overlay loadingOverlay">
        <div><img src="static/img/pleasewait.gif" /> Please wait... </div>
      </div>
      <div class="content">
        
      </div>
    </c:otherwise>
    </c:choose>
    
  </div>
  <div class="details" id="projects"></div>
</form>

</div>


</jsp:body>
</struct:htmlWrapper>
