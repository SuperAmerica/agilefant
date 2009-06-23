<%@ include file="./_taglibs.jsp"%>
<table>
  <tr>
    <th>Story points</th>
    <td><c:out value="${projectMetrics.storyPoints}" /></td>
  </tr>
  <%--
  <tr>
    <th>Stories done</th>
    <td><c:out value="${iterationMetrics.percentDoneStories}" />% (<c:out
      value="${iterationMetrics.completedStories}" /> / <c:out
      value="${iterationMetrics.totalStories}" />)</td>
  </tr>
  --%>
</table>