<%@include file="./_taglibs.jsp" %>
<div class="hierarchyContainer">

<h2>Parent stories</h2>

<%--
Link to backlog:

<a href="editBacklog.action?backlogId=${story.backlog.id}">
</a>
 --%>

<ul>
<c:set var="indent" value="0"/>
<c:forEach items="${hierarchy}" var="story">
  <li style="margin-left: ${indent}px;"><c:out value="${story.name}" /></li>
  <c:set var="indent" value="${indent + 10}"/>
</c:forEach>
</ul>

</div>