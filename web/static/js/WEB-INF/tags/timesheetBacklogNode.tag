<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="java.util.List" name="nodes"%>
<table class="reportTable" style="width: 100%; text-align: left;">
<c:forEach items="${nodes}" var="node">
	<tr>
	<c:choose>
		<c:when test="${aef:isProduct(node.backlog)}">
			<th class="product">
			 
			   <c:out value="${node.name}" />
			 
			</th>
			<th class="product effortCol">
        ${aef:minutesToString(node.effortSum)}
      </th>
		</c:when>
		<c:when test="${aef:isProject(node.backlog) && (!empty node.storyNodes || !empty node.taskNodes || !empty node.hourEntries)}">
			<th class="project">
			 <a onclick="tsToggle(this);">
			   <c:out value="${node.name}" />
			 </a>
			</th>
			<th class="project effortCol">
       ${aef:minutesToString(node.effortSum)}
      </th>
		</c:when>
		<c:when test="${aef:isProject(node.backlog)}">
      <th class="project">
         <c:out value="${node.name}" />
      </th>
      <th class="project effortCol">
       ${aef:minutesToString(node.effortSum)}
      </th>
    </c:when>
		<c:when test="${aef:isIteration(node.backlog)}">
			<th class="iteration">
			 <a onclick="tsToggle(this);">
			   <c:out value="${node.name}" />
			 </a>
			</th>
			<th class="iteration effortCol">
        ${aef:minutesToString(node.effortSum)}
      </th>
		</c:when>
	</c:choose>
	<c:if test="${!empty node.hourEntries}">
		<tr>
		<td colspan="2" class="innerTable" style="display: none;">
		<aef:timesheetHourEntryList node="${node}" />
		</td>
		</tr>
	</c:if>
	</tr>
	 <c:if test="${!empty node.taskNodes}">
    <tr>
    <td colspan="2" class="innerTable" style="display: none;">
    <aef:timesheetTaskNode nodes="${node.taskNodes}" />
    </td>
    </tr>
  </c:if>
	<c:if test="${!empty node.storyNodes}">
		<tr>
		<td colspan="2" class="innerTable" style="display: none;">
		<aef:timesheetStoryNode nodes="${node.storyNodes}" />
		</td>
		</tr>
	</c:if>
	<c:if test="${!empty node.backlogNodes}">
		<tr>
		<td colspan="2" class="innerTable noToggle">
		<aef:timesheetBacklogNode nodes="${node.backlogNodes}" />
		</td>
		</tr>
	</c:if>
</c:forEach>
</table>
