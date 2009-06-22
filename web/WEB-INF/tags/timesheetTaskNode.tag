<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="java.util.List" name="nodes"%>
<table class="reportTable" style="width: 100%; text-align: left;">

<c:forEach items="${nodes}" var="tNode">
	<tr class="task">
	<td><a onclick="tsToggle(this);">${tNode.name}</a></td>
	<td class="effortCol">${aef:minutesToString(tNode.effortSum)}</td>
	</tr>
	<c:if test="${!empty tNode.hourEntries}">
		<tr>
		<td colspan="2" class="innerTable" style="display:none;">
		<aef:timesheetHourEntryList node="${tNode}" />
		</td>
		</tr>
	</c:if>
</c:forEach>


</table>