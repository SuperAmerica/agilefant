<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Activity type list - AgilEfant</title>
</head>
<body>
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${activityType.id == 0}">
			<h2>Create new activity type</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit activity type: ${activityType.id}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeActivityType">
		<ww:hidden name="activityType.id"/>
		<p>		
			Name: <ww:textfield name="activityType.name"/>
		</p>
		<p>
			Description: <ww:textarea name="activityType.description" cols="50" rows="4"/>
		</p>
		<c:if test="${activityType.id > 0}">
			<h3>Work types</h3>
			<p>
				<c:forEach items="${activityType.workTypes}" var="type">
					<ww:url id="editLink" action="editWorkType">
						<ww:param name="workTypeId" value="${type.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteWorkType">
						<ww:param name="workTypeId" value="${type.id}"/>
					</ww:url>
					${type.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</c:forEach>
			</p>
			<p>
				<ww:url id="createLink" action="createWorkType">
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new</ww:a>		
			</p>
		</c:if>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>	
	<p>
</body>
</html>