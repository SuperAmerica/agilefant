<%@ include file="./_taglibs.jsp"%>

<div class="project-iteration-row-metrics-left">
<table class="project-iteration-metrics">
  <tr>
    <td style="padding-left: 10px; padding-right: 10px" colspan="2">
    <h4>Done stories</h4>
    <div>${iterationMetrics.completedStories} /
    ${iterationMetrics.totalStories}
    (${iterationMetrics.percentDoneStories} %) done</div>
    <c:if test="${iterationMetrics.completedStories > 0}">
      <div class="iterationMetricsBar project-iteration-metrics-storystates storyStateNOT_STARTED" style="height: 1.5em;">
        <div class="iterationMetricsBar storyStateDONE" style="height: 1.5em; width: ${iterationMetrics.percentDoneStories}%"></div>
      </div>
    </c:if></td>

    <td style="padding-left: 10px; padding-right: 10px" colspan="2">
    <h4>Time left in iteration</h4>
    <div>${iterationMetrics.daysLeft} /
    ${iterationMetrics.totalDays}
    (${iterationMetrics.daysLeftPercentage} %) days</div>
    <c:if test="${iterationMetrics.totalDays > 0}">
      <div class="iterationMetricsBar project-iteration-metrics-timeleft storyStateNOT_STARTED" style="height: 1.5em;">
      <div class="iterationMetricsBar storyStateDONE" style="width: ${iterationMetrics.daysLeftPercentage}%; height: 1.5em; float: right;"></div>
      </div>
    </c:if></td>
  </tr>


  <tr>
    <td style="padding: 0 10px;"><b>Effort left</b></td>
    <td><c:choose>
      <c:when test="${iterationMetrics.effortLeft.minorUnits  != 0}">
        <c:out
          value="${aef:minutesToString(iterationMetrics.effortLeft.minorUnits)}" />
      </c:when>
      <c:otherwise>
          &mdash;
        </c:otherwise>
    </c:choose></td>
    <td style="padding-right: 4px"><b>Schedule Variance</b></td>
    <td><c:choose>
      <c:when test="${iterationMetrics.variance != null}">
        <c:out value="${iterationMetrics.variance}" /> days
      </c:when>
      <c:otherwise>
          &mdash;
        </c:otherwise>
    </c:choose></td>
  </tr>

  <tr>
    <td style="padding: 0 10px;"><b>Original estimate</b></td>
    <td><c:choose>
      <c:when
        test="${iterationMetrics.originalEstimate.minorUnits  != 0}">
        <c:out
          value="${aef:minutesToString(iterationMetrics.originalEstimate.minorUnits)}" />
      </c:when>
      <c:otherwise>
          &mdash;
        </c:otherwise>
    </c:choose></td>
    <c:choose>
      <c:when test="${settings.hourReportingEnabled}">
        <td style="padding-right: 4px"><b>Spent effort</b></td>
        <td><c:choose>
          <c:when test="${iterationMetrics.spentEffort.minorUnits != 0}">
            <c:out
              value="${aef:minutesToString(iterationMetrics.spentEffort.minorUnits)}" />
          </c:when>
          <c:otherwise>
          &mdash;
        </c:otherwise>
        </c:choose></td>
      </c:when>
      <c:otherwise>
        <td></td>
        <td></td>
      </c:otherwise>
    </c:choose>
  </tr>


</table>
</div>
