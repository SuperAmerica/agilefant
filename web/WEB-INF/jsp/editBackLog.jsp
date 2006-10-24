<%@ include file="./inc/_taglibs.jsp" %>
<html>
<ww:head/>
<body>
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${backLogItem.id == 0}">
			<h2>Create new backlog</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit backlog: ${backLogItem.id}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeBackLog">
		<ww:hidden name="backLogId" value="${backLogItem.id}"/>
		<ww:hidden name="sprintId"/>
		
		<p>		
			Name: <ww:textfield name="backLogItem.name"/>
		</p>
		<p>
			Description: <ww:textarea name="backLogItem.description" cols="50" rows="4"/>
		</p>
		<!-- 
		<c:if test="${backLogItem.id > 0}">
			<h3>Tasks</h3>
			<p>
				<c:forEach items="${backLogItem.tasks}" var="task">
					<ww:url id="editLink" action="editTask">
						<ww:param name="taskId" value="${task.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteTask">
						<ww:param name="taskId" value="${task.id}"/>
					</ww:url>
					${task.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</c:forEach>
			</p>
			<p>
				<ww:url id="createLink" action="createTask">
					<ww:param name="backLogId" value="${backLogItem.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new</ww:a>		
			</p>
		</c:if>
		 -->
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>	
	<p>
</body>
</html>