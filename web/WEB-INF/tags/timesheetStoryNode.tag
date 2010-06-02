<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="java.util.List" name="nodes"%>
<c:if test="${!empty nodes}">
	<table class="reportTable" cellpadding="0" cellspacing="0">
	<c:forEach items="${nodes}" var="sNode">
		<tr>
		<th class="story">
        <a onclick="tsToggle(this);">${sNode.name} &raquo;</a>
      </th>
		<th class="story effortCol">
        ${aef:minutesToString(sNode.effortSum)}
      </th>
		</tr>
		<c:if test="${!empty sNode.hourEntries}">
			<tr>
			<td colspan="2" style="display: none;" class="innerTable">
			<aef:timesheetHourEntryList node="${sNode}" />
			</td>
			</tr>
		</c:if>

		<c:if test="${sNode.hasChildren}">
			<tr>
			<td colspan="2" style="display: none;" class="innerTable">
			<aef:timesheetTaskNode nodes="${sNode.children}" />
			</td>
			</tr>
		</c:if>

	</c:forEach>

</c:if>
</table>