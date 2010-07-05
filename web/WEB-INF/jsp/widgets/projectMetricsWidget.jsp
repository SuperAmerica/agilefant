<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>

<struct:widget name="Project: ${project.name}"
  widgetId="${widgetId}">

  <%-- Breadcrumb --%>
  <div class="widget-top-info"><a
    href="editBacklog.action?backlogId=${project.parent.id}">
  <c:out value="${project.parent.name}" /> </a> &gt; <a
    href="editBacklog.action?backlogId=${project.id}"> <c:out
    value="${project.name}" /> </a></div>
    
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
      <td>Leaf stories</td>
      <td>
      <div style="width: 50px; height: 1em;" class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${projectMetrics.completedStoriesPercentage}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${projectMetrics.completedStoriesPercentage}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${projectMetrics.numberOfDoneStories}" /> / 
        <c:out value="${projectMetrics.numberOfStories}" />
      </td>
    </tr>
    <tr>
      <td>Leaf story points</td>
      <td>
      <div style="width: 50px; height: 1em;"  class="storyStateNOT_STARTED">
      <div class="storyStateDONE" style="display: inline-block; width: ${projectMetrics.storyPointsCompletedPercentage}%; height: 1em;"></div>
      </div>
      </td>
      <td>
        <c:out value="${projectMetrics.storyPointsCompletedPercentage}" />%
      </td>
      <td style="text-align: center; white-space: nowrap;">
        <c:out value="${projectMetrics.completedStoryPoints}" /> / 
        <c:out value="${projectMetrics.storyPoints}" /> points
      </td>
    </tr>
    </table>
    </td>
      <td style="padding-left: 1em; vertical-align: middle;">
        <div class="smallBurndown" style="margin: 0; background-image: url('drawSmallProjectBurnup.action?backlogId=${project.id}');">&nbsp;</div>
          <div style="width: 100px; height: 1em; margin-top: 0.5em;" class="storyStateNOT_STARTED">
            <div class="storyStateDONE" style="display: inline-block; float: right; width: ${projectMetrics.daysLeftPercentage}%; height: 1em;">
          </div>
        </div>
        <div style="text-align: center;">
          ${projectMetrics.daysLeft} / ${projectMetrics.totalDays} days left
        </div>
      </td>
    </tr>
  </table>
</struct:widget>
    