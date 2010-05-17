<%@ include file="./_taglibs.jsp"%>

<div class="project-iteration-row-metrics-left">
	<table class="project-iteration-metrics">
		<tr>
		<td style="padding-left: 10px; padding-right: 10px" colspan="2">
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
    
    <%--
		<td style="padding-left: 10px; padding-right: 10px">
			<h4>Burndown chart</h4>
		<img id="smallChart" src="drawSmallIterationBurndown.action?backlogId=${iterationId}" />
		</td>
     --%>

			<td style="padding-left: 10px; padding-right: 10px" colspan="2">
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
      <td style="padding: 0 10px;"><b>Effort left</b></td>
      <td>
        <c:choose>
        <c:when test="${iterationRowMetrics.effortLeft.minorUnits  != 0}">
          <c:out value="${aef:minutesToString(iterationRowMetrics.effortLeft.minorUnits)}" />
        </c:when>
        <c:otherwise>
          &mdash;
        </c:otherwise>
        </c:choose>
      </td>
      <td style="padding-right: 4px"><b>Schedule Variance</b></td>
      <td>
        <c:choose>
        <c:when test="${iterationRowMetrics.variance != null}">
          <c:out value="${iterationRowMetrics.variance}" />
        </c:when>
        <c:otherwise>
          &mdash;
        </c:otherwise>
        </c:choose>
      </td>
    </tr>
    
    <tr>
      <td style="padding: 0 10px;"><b>Original estimate</b></td>
      <td>
        <c:choose>
        <c:when test="${iterationRowMetrics.originalEstimate.minorUnits  != 0}">
          <c:out value="${aef:minutesToString(iterationRowMetrics.originalEstimate.minorUnits)}" />
        </c:when>
        <c:otherwise>
          &mdash;
        </c:otherwise>
        </c:choose>
      </td>
      <c:choose>
      <c:when test="${iterationRowMetrics.timesheetsEnabled}">
      <td style="padding-right: 4px"><b>Effort spent</b></td>
      <td>
        <c:choose>
        <c:when test="${iterationRowMetrics.spentEffort.minorUnits != 0}">
          <c:out value="${aef:minutesToString(iterationRowMetrics.spentEffort.minorUnits)}" />
        </c:when>
        <c:otherwise>
          &mdash;
        </c:otherwise>
        </c:choose>
      </td>
      </c:when>
      <c:otherwise>
        <td></td>
        <td></td>
      </c:otherwise>
      </c:choose>
    </tr>
    

			</table>
		</div>
		</tr>
	</table>
	</div>
