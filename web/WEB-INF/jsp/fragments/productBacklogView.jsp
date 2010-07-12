<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<script type="text/javascript">

$(document).ready(function() {
  var toggleProjectIterations = function(id) {
    var projectWidget = $('.projectWidget[backlogId='+id+']');
    if (projectWidget.is(':visible') && projectWidget.find('input[name=showIterations]').is(':checked')) {
      $('.iterationWidget').has('input[name=parentProject_'+id+']').show();
    }
    else {
      $('.iterationWidget').has('input[name=parentProject_'+id+']').hide();
    }
  };
  
  var toggleProject = function(id, displayed) {
    var projectWidget = $('.projectWidget[backlogId='+id+']'); 
    if (displayed) {
      projectWidget.show();
      toggleProjectIterations(id);
    } else {
      projectWidget.hide();
      toggleProjectIterations(id);
    }
  };
  
  /* Hide/show filters on past/current/future projects and their iterations */
  $('.displayCheckboxes input[name=past]').change(function() {
    var checked = $(this).is(':checked');
    $('.projectWidget').has('input[type=hidden][name=PAST]').each(function() {
      toggleProject($(this).attr('backlogid'), checked);
    });
  });

  $('.displayCheckboxes input[name=current]').change(function() {
    var checked = $(this).is(':checked');
    $('.projectWidget').has('input[type=hidden][name=CURRENT]').each(function() {
      toggleProject($(this).attr('backlogid'), checked);
    });
  });

  $('.displayCheckboxes input[name=future]').change(function() {
    var checked = $(this).is(':checked');
    $('.projectWidget').has('input[type=hidden][name=FUTURE]').each(function() {
      toggleProject($(this).attr('backlogid'), checked);
    });
  });

  /* Initially hide all past backlogs */
  $('.projectWidget').has('input[type=hidden][name=PAST]').each(function() {
    toggleProject($(this).attr('backlogid'), false);
  });

  /* Checkbox for each project to show iterations */
  $('.projectWidget input[name=showIterations]').change(function() {
    toggleProjectIterations($(this).val());
  });

  /* One checkbox to rule them all */
  $('.displayCheckboxes input[name=iterations]').change(function() {
    $('.projectWidget input[name=showIterations]').attr('checked', $(this).is(':checked')).trigger('change');
  });


  /*
   * PRODUCT WIDGET SPIKE
   */
  var originalOffset = $('#productWidget').offset();
  var startOffset = $(window).scrollTop() - originalOffset.top;
  $(window).scroll(function(event) {
    var offset = $(window).scrollTop() - originalOffset.top;
    if (offset < 10) offset = 10;
    else if (offset > startOffset) offset += 30;

    $("#productWidget").stop().animate({"marginTop": offset + "px"},"slow");
  });
});
</script>

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
</style>

<h2>Product's leaf stories</h2>

<%--<strong>NOTICE: only leaf stories are shown in this view!</strong> --%>

<div class="widgetContainer">
<ul class="widgetList">
  <li class="widget productWidget staticWidget droppableWidget" id="productWidget" backlogid="${product.id}">
    <struct:widget name="${product.name}" widgetId="-1">
      <ul class="storyList">
        <c:forEach items="${product.leafStories}" var="story">
          <li storyId="${story.id}"><aef:storyTreeField story="${story}" type="state" /> ${story.name}</li>
        </c:forEach>
      </ul>
    </struct:widget>
  </li>
</ul>
</div>

<div class="widgetContainer">
<ul class="widgetList">
  <li class="widget staticWidget displayCheckboxes">
    <struct:widget name="Display projects" widgetId="-1">
      <input type="checkbox" name="past" /> Past
      <input type="checkbox" name="current" checked="checked" /> Ongoing
      <input type="checkbox" name="future" checked="checked" /> Future
      <br/>
      <input type="checkbox" name="iterations" checked="checked" /> Hide/show projects' iterations
    </struct:widget>
  </li>
  <c:forEach items="${product.childProjects}" var="project">
    <li class="widget projectWidget droppableWidget" backlogid="${project.id}">
      <struct:widget name="${project.name}" widgetId="-1">
        <input type="hidden" name="${aef:scheduleStatus(project)}" value="true" />
        <c:if test="${!empty project.childIterations}">
          <input type="checkbox" name="showIterations" value="${project.id}" checked="checked"/> Show iterations
          <hr/>
        </c:if>
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
<ul class="widgetList">
  <c:forEach items="${product.childProjects}" var="project">
    <c:forEach items="${project.childIterations}" var="iteration">
      <li class="widget iterationWidget droppableWidget" backlogid="${iteration.id}">
        <struct:widget name="${project.name} > ${iteration.name}" widgetId="-1">
          <input type="hidden" name="${aef:scheduleStatus(iteration)}" value="true" />
          <input type="hidden" name="parentProject_${project.id}" value="1" />
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

