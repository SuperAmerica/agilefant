<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>

<struct:widget name="Iteration: ${iteration.name}"
  widgetId="${widgetId}">

  <%-- Breadcrumb --%>
  <div style="margin-bottom: 1em; color: #666;"><a
    href="editBacklog.action?backlogId=${iteration.parent.parent.id}">
  <c:out value="${iteration.parent.parent.name}" /> </a> &gt; <a
    href="editBacklog.action?backlogId=${backlog.parent.id}"> <c:out
    value="${iteration.parent.name}" /> </a> &gt; <c:out
    value="${iteration.name}" /></div>

  <%-- Metrics --%>
  <table style="margin: 2em 0;">
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
      <td>Tasks</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.percentDoneTasks}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.percentDoneTasks}" />%
      </td>
      <td style="text-align: center;">
        <c:out value="${iterationMetrics.completedTasks}" /> / 
        <c:out value="${iterationMetrics.totalTasks}" />
      </td>
    </tr>
    <tr>
      <td>Task effort</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${iterationMetrics.completedEffortPercentage}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${iterationMetrics.completedEffortPercentage}" />%
      </td>
      <td style="text-align: center;">
        <c:out value="${aef:estimateToHours(iterationMetrics.effortLeft)}" /> / 
        <c:out value="${aef:estimateToHours(iterationMetrics.originalEstimate)}" />
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
      <td style="text-align: center;">
        <c:out value="${iterationMetrics.completedStories}" /> / 
        <c:out value="${iterationMetrics.totalStories}" />
      </td>
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
      <td style="text-align: center;">
        <c:out value="${iterationMetrics.doneStoryPoints}" /> points / 
        <c:out value="${iterationMetrics.storyPoints}" /> points
      </td>
    </tr>
    </table>
    </td>
      <td style="padding-left: 1em; vertical-align: middle;">
        <div class="smallBurndown" style="margin: 0; background-image: url('drawSmallIterationBurndown.action?backlogId=${iteration.id}');">&nbsp;</div>
          <div style="width: 100px; height: 1em;" class="storyStateNOT_STARTED">
            <div class="storyStateDONE" style="display: inline-block; float: right; width: ${iterationMetrics.daysLeftPercentage}%; height: 1em;">
          </div>
        </div>
        ${iterationMetrics.daysLeft} / ${iterationMetrics.totalDays} days left
      </td>
    </tr>
  </table>

</struct:widget>