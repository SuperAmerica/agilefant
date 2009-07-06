<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="fi.hut.soberit.agilefant.util.TimesheetNode"
	name="node"%>
<table class="reportTable" style="width: 100%;">
<c:forEach items="${node.hourEntries}" var="entry">
	<tr>
	<td style="padding-left: 15px; width: 120px;">
	<joda:format value="${entry.date}" pattern="yyyy-MM-dd HH:mm" />
	</td>
	<td style="width: 160px;">${entry.user.fullName}</td>
	<td>${entry.description}</td>
	<td class="effortCol">${aef:minutesToString(entry.minutesSpent)}</td>
	</tr>
</c:forEach>
</table>