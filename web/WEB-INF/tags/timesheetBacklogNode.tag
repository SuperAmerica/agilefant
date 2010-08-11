<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
  description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="java.util.List" name="nodes"%>




<c:forEach items="${nodes}" var="node">

<%-- CSS class for the box --%>
<c:choose>
<c:when test="${aef:isProduct(node.backlog)}">
  <c:set var="backlogBoxCssClass" value="timesheetProduct" />
</c:when>
<c:when test="${aef:isProject(node.backlog)}">
  <c:set var="backlogBoxCssClass" value="timesheetProject" />
</c:when>
<c:when test="${aef:isIteration(node.backlog)}">
  <c:set var="backlogBoxCssClass" value="timesheetIteration" />
</c:when>
</c:choose>

<div class="ui-widget-content ui-corner-all timesheet-backlog ${backlogBoxCssClass}">
 
<div class="ui-widget-header">
<ul class="timesheet-header-ul">
  <li class="hoursum" title="Recursive sum of all of the backlogs' children">${aef:minutesToString(node.effortSum)}</li>
  <c:choose>
  <c:when test="${aef:isProduct(node.backlog) && !empty node.backlogNodes}">
    <li><a href="#" rel="backlog_${node.backlog.id}_subBacklogs" class="timesheetOpenListLink">${node.name}</a></li>
  </c:when>
  <c:otherwise>
    <li>${node.name}</li>
  </c:otherwise>
  </c:choose>
  
</ul>
</div>



<c:if test="${!empty node.storyNodes || !empty node.taskNodes || !empty node.hourEntries}">

<ul class="timesheet-content">
  <c:if test="${!empty node.storyNodes}">
    <li>
      <div class="hoursum">${aef:minutesToString(node.storyEffortSum)}</div>
      <div><a href="#" rel="backlog_${node.backlog.id}_storyContainer" class="timesheetOpenListLink"><div class="timesheetOpenListImage"></div> Stories</a></div>
    </li>
    <li id="backlog_${node.backlog.id}_storyContainer" class="timesheet-closable">
      <div class="timesheet-hourEntryContainer">
        <aef:timesheetStoryNode nodes="${node.storyNodes}" />
      </div>
    </li>
  </c:if>
  
  <c:if test="${!empty node.taskNodes}">
    <li>
      <div class="hoursum">${aef:minutesToString(node.taskEffortSum)}</div>
      <div><a href="#" rel="backlog_${node.backlog.id}_taskContainer" class="timesheetOpenListLink"><div class="timesheetOpenListImage"></div> Tasks</a></div>
    </li>
    <li id="backlog_${node.backlog.id}_taskContainer" class="timesheet-closable">
      <div class="timesheet-hourEntryContainer">
        <aef:timesheetTaskNode nodes="${node.taskNodes}" />
      </div>
    </li>
  </c:if>
  
  <c:if test="${!empty node.hourEntries}">
    <li>
      <div class="hoursum">${aef:minutesToString(node.ownEffortSpentSum)}</div>
      <div style="color: #666;">Direct spent effort</div>
      
    </li>
    <%--
    <li id="backlog_${node.backlog.id}_hourEntryContainer" class="timesheet-closable">
      <div class="timesheet-hourEntryContainer">
        <aef:timesheetHourEntryList node="${node}" />
      </div>
    </li>
     --%>
  </c:if>
  
  <c:if test="${!empty node.backlogNodes}">
    <li>
      <div class="hoursum">${aef:minutesToString(node.backlogEffortSum)}</div>
      <div style="color: #666;">Child backlogs sum</div>
    </li>
  </c:if>
</ul>

</c:if>
</div>

<div id="backlog_${node.backlog.id}_subBacklogs">
<c:if test="${!empty node.backlogNodes}">
  <aef:timesheetBacklogNode nodes="${node.backlogNodes}" />
</c:if>
</div>

</c:forEach>

