<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  
  $('.storyList').sortable({
    connectWith: '.storyList',
    placeholder: 'placeholder',
    dropOnEmpty: true
  });

  $('.displayCheckboxes input[name=past]').change(function() {
    $('.widget').has('input[type=hidden][name=PAST]').toggle();
  });

  $('.displayCheckboxes input[name=current]').change(function() {
    $('.widget').has('input[type=hidden][name=CURRENT]').toggle();
  });

  $('.displayCheckboxes input[name=future]').change(function() {
    $('.widget').has('input[type=hidden][name=FUTURE]').toggle();
  });

  $('.widget').has('input[type=hidden][name=PAST]').hide();
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
.storyList {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
.storyList li {
  margin-bottom: 3px;
}
</style>

<h2>Product's backlogs</h2>

<div class="widgetContainer">
<ul class="widgetList">
  <li class="widget staticWidget displayCheckboxes"><struct:widget
    name="Display backlogs" widgetId="-1">
    <input type="checkbox" name="past" /> Past <input type="checkbox"
      name="current" checked="checked" /> Ongoing <input
      type="checkbox" name="future" checked="checked" /> Future
          </struct:widget></li>
  <li class="widget staticWidget"><struct:widget
    name="Product stories" widgetId="-1">
    <ul class="storyList">
      <c:forEach items="${product.stories}" var="story">
        <li><aef:storyTreeField story="${story}" type="state" />
        ${story.name}</li>
      </c:forEach>
    </ul>
  </struct:widget></li>
</ul>
</div>
<div class="widgetContainer">
<ul class="widgetList">
  <c:forEach items="${product.projects}" var="project">
    <li class="widget"><struct:widget name="${project.name}"
      widgetId="-1">
      <input type="hidden" name="${aef:scheduleStatus(project)}"
        value="true" />
      <ul class="storyList " style="min-height: 20px;">
        <c:forEach items="${project.stories}" var="story">
          <li class=""><aef:storyTreeField
            story="${story}" type="state" /> ${story.name}</li>
        </c:forEach>
      </ul>
    </struct:widget></li>
  </c:forEach>
</ul>
</div>

<div class="widgetContainer">
<ul class="widgetList">
  <c:forEach items="${product.projects}" var="project">
    <c:forEach items="${project.children}" var="iteration">
      <li class="widget"><struct:widget
        name="${project.name} > ${iteration.name}" widgetId="-1">
        <input type="hidden" name="${aef:scheduleStatus(iteration)}"
          value="true" />
        <ul class="storyList" style="min-height: 20px;">
          <c:forEach items="${iteration.stories}" var="story">
            <li class=""><aef:storyTreeField
              story="${story}" type="state" /> ${story.name}</li>
          </c:forEach>
        </ul>
      </struct:widget></li>
    </c:forEach>
  </c:forEach>
</ul>
</div>

<div style="width: 100%; clear: both;">&nbsp;</div>

