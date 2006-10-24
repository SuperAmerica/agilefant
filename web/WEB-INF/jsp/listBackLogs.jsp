<%@ include file="./inc/_taglibs.jsp" %>
<html>
<ww:head/>
<body>
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
			<c:when test="${empty backLogs}">
				No backlogs were found.
			</c:when>
			<c:otherwise>
				<c:forEach items="${backLogs}" var="backLog">
					<ww:url id="editLink" action="editBackLog">
						<ww:param name="backLogId" value="${backLog.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteBackLog">
						<ww:param name="backLogId" value="${backLog.id}"/>
					</ww:url>
					${backLog.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</c:forEach>
			</c:otherwise>
		</c:choose>			
	<p>
		<ww:url id="createBackLogLink" action="createBackLog"/>
		<ww:a href="%{createBackLogLink}">Create new</ww:a>
	</p>
</body>
</html>