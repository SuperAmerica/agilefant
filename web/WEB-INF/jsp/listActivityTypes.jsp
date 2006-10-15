<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Activity type list - AgilEfant</title>
</head>
<body>
	<p>
		<c:choose>
			<c:when test="${empty activityTypes}">
				No activity types were found.
			</c:when>
			<c:otherwise>
				<c:forEach items="${activityTypes}" var="type">
					<ww:url id="editLink" action="editActivityType">
						<ww:param name="activityTypeId" value="${type.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteActivityType">
						<ww:param name="activityTypeId" value="${type.id}"/>
					</ww:url>
					${type.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</c:forEach>
			</c:otherwise>
		</c:choose>			
	</p>
	<p>
		<ww:url id="createActivityTypeLink" action="createActivityType"/>
		<ww:a href="%{createActivityTypeLink}">Create new</ww:a>
	</p>
</body>
</html>