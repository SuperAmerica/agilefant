<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@ tag
	description="This tag generates the display data for timesheet querys"%>

<%@ attribute type="java.util.List" name="nodes"%>

<ul class="timesheet-content">
<c:forEach items="${nodes}" var="sNode">
  <li>
    <div>${sNode.name}</div>
    <div class="hoursum">${aef:minutesToString(sNode.effortSum)}</div>
  </li>
</c:forEach>
</ul>
