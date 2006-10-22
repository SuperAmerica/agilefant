<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Deliverable list - AgilEfant</title>
</head>
<body>
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${deliverable.id == 0}">
			<h2>Create new deliverable</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit deliverable: ${deliverable.id}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeDeliverable">
		<ww:hidden name="deliverable.id"/>
<ww:date name="%{new java.util.Date()}" format="dd-MM-yyyy" id="date"/>


			Startdate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="deliverable.startDate"/> 
		</p>
		<p>		
			Enddate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="deliverable.endDate"/> 
		</p>
		<p>		
			Name: <ww:textfield name="deliverable.name"/>
		</p>
		<p>
			Description: <ww:textarea name="deliverable.description" cols="50" rows="4"/>
		</p>
		<c:if test="${deliverable.id > 0}">
			<h3>Sprints</h3>
			<p>
				<c:forEach items="${deliverable.sprints}" var="sprint">
					<ww:url id="editLink" action="editSprint">
						<ww:param name="sprintId" value="${sprint.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteSprint">
						<ww:param name="sprintId" value="${sprint.id}"/>
					</ww:url>
					${sprint.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</c:forEach>
			</p>
			<p>
				<ww:url id="createLink" action="createSprint">
					<ww:param name="deliverableId" value="${deliverable.id}"/>
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