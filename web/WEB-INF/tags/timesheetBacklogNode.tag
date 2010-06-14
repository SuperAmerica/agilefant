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
<ul>
  <li>${node.name}</li>
  <li class="hoursum" title="Recursive sum of all of the backlogs' children">${aef:minutesToString(node.effortSum)}</li>
</ul>
</div>



<c:if test="${!empty node.storyNodes || !empty node.taskNodes || !empty node.hourEntries}">

<table width="100%" style="margin-bottom: 1em;">
  <c:if test="${!empty node.storyNodes}">
    <tr>
      <th><a href="#" rel="backlog_${node.backlog.id}_storyContainer" class="timesheetOpenListLink"><div class="timesheetOpenListImage"></div> Stories</a></th>
      <td>${aef:minutesToString(node.storyEffortSum)}</td>
    </tr>
    <tr id="backlog_${node.backlog.id}_storyContainer" class="timesheet-closable">
      <td colspan="2">
        <div class="timesheet-hourEntryContainer">
          <aef:timesheetStoryNode nodes="${node.storyNodes}" />
        </div>
      </td>
    </tr>
  </c:if>
  
  <c:if test="${!empty node.taskNodes}">
  <tr>
    <th><a href="#" rel="backlog_${node.backlog.id}_taskContainer" class="timesheetOpenListLink"><div class="timesheetOpenListImage"></div> Tasks</a></th>
    <td>${aef:minutesToString(node.taskEffortSum)}</td>
  </tr>
  <tr id="backlog_${node.backlog.id}_taskContainer" class="timesheet-closable">
    <td colspan="2">
      <div class="timesheet-hourEntryContainer">
        <aef:timesheetTaskNode nodes="${node.taskNodes}" />
      </div>
    </td>
  </tr>
  </c:if>
  
  <c:if test="${!empty node.hourEntries}">
  <tr>
    <th><a href="#" rel="backlog_${node.backlog.id}_hourEntryContainer" class="timesheetOpenListLink"><div class="timesheetOpenListImage"></div> Direct spent effort</a></th>
    <td>${aef:minutesToString(node.ownEffortSpentSum)}</td>
  </tr>
  <tr id="backlog_${node.backlog.id}_hourEntryContainer" class="timesheet-closable">
    <td colspan="2">
      <div class="timesheet-hourEntryContainer">
        <aef:timesheetHourEntryList node="${node}" />
      </div>
    </td>
  </tr>
  </c:if>
</table>
</c:if>
</div>

<c:if test="${!empty node.backlogNodes}">
  <aef:timesheetBacklogNode nodes="${node.backlogNodes}" />
</c:if>

</c:forEach>
<%--
<table class="reportTable" cellpadding="0" cellspacing="0">
  <c:forEach items="${nodes}" var="node">
    <tr>
      <c:choose>
        <c:when
          test="${aef:isProduct(node.backlog) && (!empty node.storyNodes || !empty node.taskNodes || !empty node.hourEntries)}">
          <th class="product"><a onclick="tsToggle(this);"> <c:out
            value="${node.name}" /> &raquo; </a></th>
          <th class="product effortCol">
          ${aef:minutesToString(node.effortSum)}</th>
        </c:when>
        <c:when test="${aef:isProduct(node.backlog)}">
          <th class="product"><c:out value="${node.name}" /></th>
          <th class="product effortCol">
          ${aef:minutesToString(node.effortSum)}</th>
        </c:when>
        <c:when
          test="${aef:isProject(node.backlog) && (!empty node.storyNodes || !empty node.taskNodes || !empty node.hourEntries)}">
          <th class="project"><a onclick="tsToggle(this);"> <c:out
            value="${node.name}" /> &raquo; </a></th>
          <th class="project effortCol">
          ${aef:minutesToString(node.effortSum)}</th>
        </c:when>
        <c:when test="${aef:isProject(node.backlog)}">
          <th class="project"><c:out value="${node.name}" /></th>
          <th class="project effortCol">
          ${aef:minutesToString(node.effortSum)}</th>
        </c:when>
        <c:when test="${aef:isIteration(node.backlog)}">
          <th class="iteration"><a onclick="tsToggle(this);"> <c:out
            value="${node.name}" /> &raquo; </a></th>
          <th class="iteration effortCol">
          ${aef:minutesToString(node.effortSum)}</th>
        </c:when>
      </c:choose>
      <c:if test="${!empty node.hourEntries}">
        <tr>
          <td colspan="2" class="innerTable" style="display: none;">
          <aef:timesheetHourEntryList node="${node}" /></td>
        </tr>
      </c:if>
    </tr>
    <c:if test="${!empty node.storyNodes}">
      <tr>
        <td colspan="2" class="innerTable" style="display: none;">
        <aef:timesheetStoryNode nodes="${node.storyNodes}" /></td>
      </tr>
    </c:if>
    <c:if test="${!empty node.taskNodes}">
      <tr>
        <td colspan="2" class="innerTable" style="display: none;">
        <aef:timesheetTaskNode nodes="${node.taskNodes}" /></td>
      </tr>
    </c:if>
    <c:if test="${!empty node.backlogNodes}">
      <tr>
        <td colspan="2" class="innerTable noToggle"><aef:timesheetBacklogNode
          nodes="${node.backlogNodes}" /></td>
      </tr>
    </c:if>
  </c:forEach>
</table>
 --%>
