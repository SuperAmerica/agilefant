<%@ include file="./_taglibs.jsp"%>

<div class="project-iteration-metrics">
	<table class="project-iteration-metrics">
		<tr>
			<td>
				<h4>Story states by count</h4>
        <div>
          ${iterationRowMetrics.doneStoryCount} / ${iterationRowMetrics.storyCount} (${iterationRowMetrics.doneStoryPercentage} %) done
        </div>
        <c:if test="${iterationRowMetrics.storyCount > 0}">
          <div class="iterationRowMetricsBar project-iteration-metrics-storystates">
            <c:forEach var="entry" items="${iterationRowMetrics.stateDistribution}">
              <div class="iterationRowMetricsBar storyState${entry.key}" style="width: ${entry.value * 300 / iterationRowMetrics.storyCount}px"></div>
            </c:forEach>
          </div>
        </c:if>
			</td>
			<td>
				<h4>Time left in iteration</h4>
        <div>
          ${iterationRowMetrics.daysLeft} / ${iterationRowMetrics.totalDays} (${iterationRowMetrics.daysLeftPercentage} %) days
        </div>
        <c:if test="${iterationRowMetrics.totalDays > 0}">
          <div class="iterationRowMetricsBar project-iteration-metrics-timeleft">
            <div class="iterationRowMetricsBar storyStateDONE" style="width: ${300 - iterationRowMetrics.daysLeft * 300 / iterationRowMetrics.totalDays}px"></div>
            <div class="iterationRowMetricsBar storyStateNOT_STARTED" style="width: ${iterationRowMetrics.daysLeft * 300 / iterationRowMetrics.totalDays}px"></div>
          </div>
        </c:if>
			</td>
		</tr>
		<tr>
			<th>Effort left</th>
			<td><c:out value="${aef:minutesToString(iterationRowMetrics.effortLeft.minorUnits)}" /></td>
		</tr>
		<tr>
			<th>Original estimate</th>
			<td><c:out value="${aef:minutesToString(iterationRowMetrics.originalEstimate.minorUnits)}" /></td>
		</tr>
		<c:if test="${iterationRowMetrics.timesheetsEnabled}">
		<tr>
			<th>Effort Spent</th>
			<td><c:out value="${aef:minutesToString(iterationRowMetrics.spentEffort.minorUnits)}" /></td>
		</tr>
		</c:if>
	</table>
</div>