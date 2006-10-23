<%@ include file="./inc/_taglibs.jsp" %>
<html>
<ww:head/>
<body>
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${sprint.id == 0}">
			<h2>Create new Sprint</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit sprint: ${sprint.id}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeSprint">
		<ww:hidden name="sprint.id"/>
		<ww:hidden name="deliverableId"/>
<!-- 
<ww:date name="%{new java.util.Date()}" format="dd-MM-yyyy" id="date"/>
<p>

			Startdate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="sprint.startDate"/> 
		</p>
		<p>		
			Enddate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="sprint.endDate"/> 
		</p>
 -->
 		<p>		
			Name: <ww:textfield name="sprint.name"/>
		</p>
		<p>
			Description: <ww:textarea name="sprint.description" cols="50" rows="4"/>
		</p>
		<c:if test="${sprint.id > 0}">
			<h3>Backlogs</h3>
			<p>
				<c:forEach items="${sprint.backLogs}" var="backLog">
				<p>
					<ww:url id="editLink" action="editBackLog">
						<ww:param name="backLogId" value="${backLog.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteBackLog">
						<ww:param name="backLogId" value="${backLog.id}"/>
					</ww:url>
					${backLog.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
					</p>
				</c:forEach>
			</p>
			<p>
				<ww:url id="createLink" action="createBackLog">
					<ww:param name="sprintId" value="${sprint.id}"/>
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