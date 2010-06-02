<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<h4>Branch metrics</h4>

<table>
  <tr>
    <th>Own estimate</th>
    <td>${story.storyPoints}</td>
  </tr>
  <tr>
    <th>Done sum (leaf)</th>
    <td>20</td>
    <th>Total sum (leaf)</th>
    <td>40</td>
  </tr>
  <tr>
    <th>Done sum (estimate)</th>
    <td>21</td>
    <th>Total sum (estimate)</th>
    <td>45</td>
  </tr>
</table>