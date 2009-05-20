<%@ include file="./_taglibs.jsp"%>
<table style="width: 700px;" class="listTable">
	<tr>
		<th><a
			href="weeklyWpentEffort.action?userId=${userId}&amp;week=${prevWeek}&amp;year=${prevYear}">Previous
		week</a></th>
		<c:forEach items="${dailyEffort}" var="dayEffort">
			<th style="width: 70px;">${dayEffort.date}.${dayEffort.month}</th>
		</c:forEach>
		<th><a
			href="weeklyWpentEffort.action?userId=${userId}&week=${nextWeek}&year=${nextYear}">Next
		week</a></th>
	</tr>
	<tr>
		<td></td>
		<c:forEach items="${dailyEffort}" var="dayEffort">
			<td>
				<a class="detailLink" href="hourEntriesByUserAndWeek.action?userId=${userId}&amp;day=${dayEffort.dayOfYear}&amp;year=${year}">
				${dayEffort.spentEffort}</a>
			</td>
		</c:forEach>
		<td></td>
	</tr>
</table>
<div class="details"></div>
