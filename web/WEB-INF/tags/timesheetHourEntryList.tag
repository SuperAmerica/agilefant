<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="fi.hut.soberit.agilefant.util.TimesheetNode"
	name="node"%>
  
<table class="reportTable" cellpadding="0" cellspacing="0">
<c:forEach items="${node.hourEntries}" var="entry">
	<tr>
	<td>
	<joda:format value="${entry.date}" pattern="yyyy-MM-dd HH:mm" />
	</td>
	<td class="userNameCol">${entry.user.fullName}</td>
	<td class="entryDescCol">${entry.description}</td>
	<td class="effortCol">${aef:minutesToString(entry.minutesSpent)}</td>
	</tr>
</c:forEach>
</table>
