<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Deliverable list - AgilEfant</title>
</head>
<body>
	<p>
		<c:choose>
			<c:when test="${empty deliverables}">
				No deliverables were found.
			</c:when>
			<c:otherwise>
				<c:forEach items="${deliverables}" var="type">
				<p>
					<ww:url id="editLink" action="editDeliverable">
						<ww:param name="deliverableId" value="${type.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteDeliverable">
						<ww:param name="deliverableId" value="${type.id}"/>
					</ww:url>
					${type.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
					</p>
				</c:forEach>
			</c:otherwise>
		</c:choose>			
	</p>
	<p>
		<ww:url id="createDeliverableLink" action="createDeliverable"/>
		<ww:a href="%{createDeliverableLink}">Create new</ww:a>
	</p>
</body>
</html>