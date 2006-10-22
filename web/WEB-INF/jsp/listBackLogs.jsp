<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Backlog list - AgilEfant</title>
</head>
<body>
	<p>
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
	</p>
	<p>
		<ww:url id="createBackLogLink" action="createBackLog"/>
		<ww:a href="%{createBackLogLink}">Create new</ww:a>
	</p>
</body>
</html>