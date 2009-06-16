<%@ include file="./_taglibs.jsp"%>
<aef:hourReporting id="hourReporting" />
<table>
	<tr>
		<th>Effort left</th>
		<td><c:out value="${aef:minutesToString(iterationMetrics.effortLeft.minorUnits)}" /></td>
	</tr>
	<tr>
		<th>Original estimate</th>
		<td><c:out value="${aef:minutesToString(iterationMetrics.originalEstimate.minorUnits)}" /></td>
	</tr>
  <tr>
    <th>Story points</th>
    <td><c:out value="${iterationMetrics.storyPoints}" /></td>
  </tr>
	<c:if test="${hourReporting}">
		<tr>
			<th>Spent effort</th>
			<td><c:out value="${aef:minutesToString(iterationMetrics.spentEffort.minorUnits)}" /></td>
		</tr>
	</c:if>
  <%--
	<tr>
		<th>Velocity</th>
		<td><c:out value="${iterationMetrics.dailyVelocity}" /> / day</td>
	</tr>
	<c:if test="${iterationMetrics.backlogOngoing}">
		<tr>
			<th>Schedule variance</th>
			<td><c:choose>
				<c:when test="${iterationMetrics.scheduleVariance != null}">
					<c:choose>
						<c:when test="${iterationMetrics.scheduleVariance > 0}">
							<span class="red">+ 
						</c:when>
						<c:otherwise>
							<span>
						</c:otherwise>
					</c:choose>
					<c:out value="${iterationMetrics.scheduleVariance}" /> days
                     </c:when>
				<c:otherwise>
                         unknown
                     </c:otherwise>
			</c:choose></td>
		</tr>
		<tr>
			<th>Scoping needed</th>
			<td><c:choose>
				<c:when test="${iterationMetrics.scopingNeeded != null}">
					<c:out value="${iterationMetrics.scopingNeeded}" />
				</c:when>
				<c:otherwise>
                         unknown
                     </c:otherwise>
			</c:choose></td>
		</tr>
	</c:if>
	<tr>
		<th>Tasks done</th>
		<td><c:out value="${iterationMetrics.percentDone}" />% (<c:out
			value="${iterationMetrics.completedItems}" /> / <c:out
			value="${iterationMetrics.totalItems}" />)</td>
	</tr>
  --%>
</table>