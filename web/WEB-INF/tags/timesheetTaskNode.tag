<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="java.util.List" name="nodes"%>

<ul class="timesheet-content">
<c:forEach items="${nodes}" var="tNode">
  <li>
    <div class="hoursum">${aef:minutesToString(tNode.effortSum)}</div>
    <div>${tNode.name}</div>
  </li>
</c:forEach>
</ul>

