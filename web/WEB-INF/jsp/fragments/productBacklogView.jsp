<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>


<script type="text/javascript">

$(document).ready(function() {
  /*
   * PRODUCT WIDGET SCROLLING
   */
  var originalOffset = $('#productWidget').offset();
  var startOffset = $(window).scrollTop() - originalOffset.top;
  $(window).scroll(function(event) {
    var offset = $(window).scrollTop() - originalOffset.top;
    if (offset < 10) offset = 10;
    else if (offset > startOffset) offset += 30;

    $("#productWidget").stop().animate({"marginTop": offset + "px"},"slow");
  });

  $('#filterByTime').click();
});
</script>



<div class="widgetContainer">
<h3>Product</h3>
<ul class="widgetList">
  <li class="widget productWidget staticWidget droppableWidget" id="productWidget" backlogid="${product.id}">
    <struct:widget name="${product.name}" widgetId="-1">
      <ul class="storyList" style="min-height: 20px;">
        <c:forEach items="${product.leafStories}" var="story">
          <li storyId="${story.id}"><aef:storyTreeField story="${story}" type="state" /> ${story.name}</li>
        </c:forEach>
      </ul>
    </struct:widget>
  </li>
</ul>
</div>

<div class="widgetContainer">
<h3>Projects</h3>
<ul class="widgetList">
  <c:forEach items="${product.childProjects}" var="project">
    <li class="widget projectWidget droppableWidget scheduled staticWidget" backlogid="${project.id}">
      <struct:widget name="${project.name}" widgetId="-1">
        <div class="timeframe">Timeframe: <span><joda:format value="${project.startDate}" pattern="YYYY-MM-dd" /></span> to <span><joda:format value="${project.endDate}" pattern="YYYY-MM-dd" /></span></div>
        <input type="hidden" name="startDate" value='<joda:format value="${project.startDate}" pattern="YYYY-MM-dd" />' />
        <input type="hidden" name="endDate" value='<joda:format value="${project.endDate}" pattern="YYYY-MM-dd" />' />
        <ul class="storyList " style="min-height: 20px;">
          <c:forEach items="${project.leafStories}" var="story">
            <li storyId="${story.id}"><aef:storyTreeField story="${story}" type="state" /> ${story.name}</li>
          </c:forEach>
        </ul>
      </struct:widget>
    </li>
  </c:forEach>
</ul>
</div>


<div class="widgetContainer">
<h3>Iterations</h3>
<ul class="widgetList">
  <c:forEach items="${product.childProjects}" var="project">
    <c:forEach items="${project.childIterations}" var="iteration">
      <li class="widget iterationWidget droppableWidget scheduled staticWidget" backlogid="${iteration.id}">
        <struct:widget name="${project.name} > ${iteration.name}" widgetId="-1">
          <div class="timeframe">Timeframe: <span><joda:format value="${iteration.startDate}" pattern="YYYY-MM-dd" /></span> to <span><joda:format value="${iteration.endDate}" pattern="YYYY-MM-dd" /></span></div>
          <input type="hidden" name="startDate" value='<joda:format value="${iteration.startDate}" pattern="YYYY-MM-dd" />' />
          <input type="hidden" name="endDate" value='<joda:format value="${iteration.endDate}" pattern="YYYY-MM-dd" />' />
          <input type="hidden" class="parentProjectId" name="parentProject_${project.id}" value="${project.id}" />
          <ul class="storyList" style="min-height: 20px;">
            <c:forEach items="${iteration.leafStories}" var="story">
              <li storyId="${story.id}"><aef:storyTreeField story="${story}" type="state" /> ${story.name}</li>
            </c:forEach>
          </ul>
        </struct:widget>
      </li>
    </c:forEach>
  </c:forEach>
</ul>
</div>

<div style="width: 100%; clear: both;">&nbsp;</div>

