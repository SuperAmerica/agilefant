<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<h4>Branch metrics</h4>

<table>
  <tr>
    <th>Own estimate</th>
    <td>${story.storyPoints}</td>
  </tr>
  <tr>
    <th>Done sum (leaf)</th>
    <td>${branchMetrics.doneLeafPoints}</td>
    <th>Done sum (estimate)</th>
    <td>${branchMetrics.estimatedDonePoints}</td>
  </tr>
  <tr>
    <th>Total sum (leaf)</th>
    <td>${branchMetrics.leafPoints}</td>
    <th>Total sum (estimate)</th>
    <td>${branchMetrics.estimatedPoints}</td>
  </tr>
</table>