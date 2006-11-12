<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>  <link rel="stylesheet" href="/agilefant/static/css/aef07.css" type="text/css">
<ww:head/>

</head>
<%@ include file="./inc/_header.jsp" %>
<%@ include file="./inc/_navi_left.jsp" %>
    <div id="upmenu">

      <li class="normal"><a>Help</a>

      </li>
    </div>
	<p>
	
<ww:if test="hasErrors()">
  <p style="color: red;">
  <b>Errors:</b>
  <ul>
  <ww:if test="hasActionErrors()">
  <ww:iterator value="actionErrors">
    <li style="color: red;"><ww:property/></li>
  </ww:iterator>
  </ww:if>
  <ww:if test="hasFieldErrors()">
  <ww:iterator value="fieldErrors">
    <ww:iterator value="value">
    <li style="color: red;"><ww:property/></li>
    </ww:iterator>
  </ww:iterator>
  </ww:if>
  </ul>
  </p>
</ww:if>
		<c:choose>
			<c:when test="${empty backlogs}">
				No backlogs were found.
			</c:when>
			<c:otherwise>
				<c:forEach items="${backlogs}" var="backlog">
					<ww:url id="editLink" action="editBacklog">
						<ww:param name="backlogId" value="${backlog.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteBacklog">
						<ww:param name="backlogId" value="${backlog.id}"/>
					</ww:url>
					${backlog.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</c:forEach>
			</c:otherwise>
		</c:choose>			
	<p>
		<ww:url id="createBacklogLink" action="createBacklog"/>
		<ww:a href="%{createBacklogLink}">Create new</ww:a>
	</p>
<%@ include file="./inc/_footer.jsp" %>
