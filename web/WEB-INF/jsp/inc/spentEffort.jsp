<%@ include file="./_taglibs.jsp"%>
<table style="width: 770px;" class="listTable">
	<tr>
		<th rowspan="2" class="spentEffortArrow"><a title="Previous week" 
			href="weeklySpentEffort.action?userId=${userId}&amp;week=${prevWeek}&amp;year=${prevYear}">&laquo;</a></th>
		<c:forEach items="${dailyEffort}" var="dayEffort">
			<th style="width: 78px;">${dayEffort.date}.${dayEffort.month}.</th>
		</c:forEach>
		<th style="width: 65px;">Total</th>
		<th rowspan="2" class="spentEffortArrow"><a title="Next week" 
			href="weeklySpentEffort.action?userId=${userId}&week=${nextWeek}&year=${nextYear}">&raquo;</a></th>
			<td>
			 <select>
			   <c:forEach items="${weeks}" var="curWeek">
			     <c:choose>
			       <c:when test="${curWeek[1] == week}">
			         <option selected="selected" value="${curWeek[0]}-${curWeek[1]}">Week ${curWeek[1]} (${curWeek[2]}.${curWeek[3]}.)</option>
			       </c:when>
			       <c:otherwise>
			         <option value="${curWeek[0]}-${curWeek[1]}">Week ${curWeek[1]} (${curWeek[2]}.${curWeek[3]}.)</option>
			       </c:otherwise>
			     </c:choose>
			   </c:forEach>
			 </select>
			</td>
	</tr>
	<tr>
		<c:forEach items="${dailyEffort}" var="dayEffort">
			<td>
				<c:choose>
				  <c:when test="${dayEffort.spentEffort == null}">
				    &mdash;
				  </c:when>
				  <c:otherwise>
				    <a class="detailLink" href="hourEntriesByUserAndWeek.action?userId=${userId}&amp;day=${dayEffort.dayOfYear}&amp;year=${year}">
              ${dayEffort.spentEffort}</a>
				  </c:otherwise>
				</c:choose>
			</td>
		</c:forEach>
    <td>${weekEffort}</td>
		<td><a href="weeklySpentEffort.action?userId=${userId}&week=${currentWeek}&year=${currentYear}">Current week</a></td>
	</tr>
</table>
<div class="details"></div>
