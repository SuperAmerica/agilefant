<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_taglibs.jsp" %>
<html>
<body>
<%@ include file="./inc/_header.jsp" %>
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit backlog item</h2>
	<ww:form action="storeBacklogItem">
		<ww:hidden name="backlogId"/>
		<ww:hidden name="backlogItemId" value="${backlogItem.id}"/>
		<p>		
			Name: <ww:textfield name="backlogItem.name"/>
		</p>
		<p>
			Description: <ww:textarea name="backlogItem.description" cols="50" rows="4"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
	
	<c:if test="${backlogItem.id > 0}">
		<aef:productList/>
		<ww:form action="moveBacklogItem">
			<ww:hidden name="backlogItemId" value="${backlogItem.id}"/>
			<p>
				Move to another backlog:
			</p>
			<p>
				<select name="backlogId">
					<c:forEach items="${productList}" var="product">
						<c:choose>
							<c:when test="${product.id == backlogItem.backlog.id}">
								<option selected="selected" value="${product.id}">${product.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${product.id}">${product.name}</option>
							</c:otherwise>
						</c:choose>
						<c:forEach items="${product.deliverables}" var="deliverable">
							<c:choose>
								<c:when test="${deliverable.id == backlogItem.backlog.id}">
									<option selected="selected" value="${deliverable.id}">&nbsp;&nbsp;&nbsp;&nbsp;${deliverable.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${deliverable.id}">&nbsp;&nbsp;&nbsp;&nbsp;${deliverable.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>						
					</c:forEach>				
				</select>
			</p>			
			<ww:submit value="move"/>
		</ww:form>
	</c:if>	
	<c:if test="${!empty backlogItem.tasks}">
		<p>
			Has tasks:
		</p>
		<p>
			<ul>
			<c:forEach items="${backlogItem.tasks}" var="task">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${task.id}"/>
				</ww:url>
				<ww:url id="deleteLink" action="deleteTask" includeParams="none">
					<ww:param name="taskId" value="${task.id}"/>
				</ww:url>
				<li>
					${task.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</li>
			</c:forEach>
			</ul>
		</p>
	</c:if>
	<p>
		<ww:url id="createLink" action="createTask" includeParams="none">
			<ww:param name="backlogItemId" value="${backlogItemId}"/>
		</ww:url>
		<ww:a href="%{createLink}">Add task</ww:a>
	</p>
<%@ include file="./inc/_footer.jsp" %>
</body>
</html>