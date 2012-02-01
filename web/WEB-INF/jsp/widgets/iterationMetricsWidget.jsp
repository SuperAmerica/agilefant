<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>

<struct:widget name="Iteration: ${iteration.name}"
  widgetId="${widgetId}">

  <%-- Breadcrumb --%>
  <div class="widget-top-info"><a
    href="editBacklog.action?backlogId=${iteration.parent.parent.id}">
  <c:out value="${iteration.parent.parent.name}" /> </a> &gt; <a
    href="editBacklog.action?backlogId=${iteration.parent.id}"> <c:out
    value="${iteration.parent.name}" /> </a> &gt; <a
    href="editBacklog.action?backlogId=${iteration.id}"> <c:out
    value="${iteration.name}" /></a></div>

  <%-- Metrics --%>
  <table>
    <tr>
      <td>
  <table>
    <tr>
      <td></td>
      <td></td>
      <td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </td>
      <td>Completed / Total</td>
    </tr>
    <tr>
      <td>Story points</td>
      <td>
      <div style="width: 50px; height: 1em;"  class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.doneStoryPointsPercentage}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.doneStoryPointsPercentage}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${iterationMetrics.doneStoryPoints}" /> / 
        <c:out value="${iterationMetrics.storyPoints}" /> points
      </td>
    </tr>
    <tr>
      <td>Stories</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.percentDoneStories}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.percentDoneStories}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${iterationMetrics.completedStories}" /> / 
        <c:out value="${iterationMetrics.totalStories}" />
      </td>
    </tr>
    <tr>
      <td>Effort</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.completedEffortPercentage}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.completedEffortPercentage}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${aef:estimateToHours(iterationMetrics.effortLeft)}" /> / 
        <c:out value="${aef:estimateToHours(iterationMetrics.originalEstimate)}" />
      </td>
    </tr>
    <tr>
      <td>Tasks</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.percentDoneTasks}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.percentDoneTasks}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${iterationMetrics.completedTasks}" /> / 
        <c:out value="${iterationMetrics.totalTasks}" />
      </td>
    </tr>
    <tr>
      <td>Effort spent</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.percentSpentEffort}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.percentSpentEffort}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${aef:estimateToHours(iterationMetrics.spentEffort)}" /> / 
        <c:out value="${aef:estimateToHours(iterationMetrics.originalEstimate)}" />
      </td>
    </tr>
    </table>
    </td>
      <td style="padding-left: 1em; vertical-align: middle;">
        <div class="smallBurndown" style="margin: 0; background-image: url('drawSmallIterationBurndown.action?backlogId=${iteration.id}');">&nbsp;</div>
          <div style="width: 100px; height: 1em; margin-top: 0.5em;" class="storyStateNOT_STARTED">
            <div class="storyStateDONE" style="display: inline-block; float: right; width: ${iterationMetrics.daysLeftPercentage}%; height: 1em;">
          </div>
        </div>
        <div style="text-align: center;">
          ${iterationMetrics.daysLeft} / ${iterationMetrics.totalDays} days left
        </div>
      </td>
    </tr>
  </table>

</struct:widget>