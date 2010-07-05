<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>
<struct:widget name="Story: ${story.name}" widgetId="${widgetId}">

  <script type="text/javascript">
  $(document).ready(function() {
    $('#storyWidgetInfo_${story.id} div.branchMetricsContainer').load('ajax/retrieveBranchMetrics.action?storyId=${story.id}');
    $('#treeContent_${story.id}').load('ajax/getStoryHierarchy.action?storyId=${story.id}');
  });
  </script>

  <div class="widget-top-info">
    <a href="qr.action?q=story:${story.id}">View the story in its backlog</a>
  </div>
  
  <table id="storyWidgetInfo_${story.id}">
    <tr>
      <td>State</td>
      <td><span class="inlineTaskState taskState${story.state}" title="<aef:text name="story.state.${story.state}" />"><aef:text name="story.stateAbbr.${story.state}" /></span></td>
      <td>Story points</td>
      <td><span class="treeStoryPoints" title="Story points">${story.storyPoints}</span></td>
      
      <td>Labels</td>
      <td>
        <c:choose>
          <c:when test="${fn:length(story.labels) == 0}">
            <span class="labelIcon labelIconNoLabel">&nbsp;</span>
          </c:when>
          <c:when test="${fn:length(story.labels) == 1}">
            <%--  REFACTOR: This hack is here because java.util.Set does not support the bracket notation ( story.labels[0] )--%>
            <span class="labelIcon"><c:forEach items="${story.labels}" var="label"><c:out value="${fn:substring(label.name, 0, 4)}" /></c:forEach></span> 
          </c:when>
          <c:otherwise>
            <span class="labelIcon labelIconMultiple" title="${aef:joinNamedObjects(story.labels)}">&nbsp;</span>
          </c:otherwise>
          </c:choose>
      </td>
    </tr>
    <tr>
      <td colspan="6"><div class="branchMetricsContainer"></div></td>
    </tr>
  </table>
  
  <div class="expandable">
    <div id="treeContent_${story.id}"> </div>
  </div>
  
</struct:widget>