<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_taglibs.jsp" %>
<html>
<body>
<%@ include file="./inc/_header.jsp" %>
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit Deliverable</h2>
	<ww:form action="storeDeliverable">
		<ww:hidden name="deliverableId" value="${deliverable.id}"/>
		<ww:hidden name="productId"/>
 		<p>		
			Name: <ww:textfield name="deliverable.name"/>
		</p>
		<p>
			Description: <ww:textarea name="deliverable.description" cols="50" rows="4"/>
		</p>
<%--		<c:if test="${deliverable.id > 0}">
			<h3>Sprints</h3>
			<p>
				<!-- todo: make consistent with new terminology? sprint -> iteration -->
				<c:forEach items="${deliverable.iterations}" var="sprint">
				<p>
					<ww:url id="editLink" action="editSprint" includeParams="none">
						<ww:param name="sprintId" value="${sprint.id}"/>
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
					</ww:url>
					<ww:url id="deleteLink" action="deleteSprint" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
						<ww:param name="sprintId" value="${sprint.id}"/>
					</ww:url>
					${sprint.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
					</p>
				</c:forEach>
			</p>
			<p>
 				<ww:url id="createLink" action="createSprint" includeParams="none">
					<ww:param name="deliverableId" value="${deliverable.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new</ww:a>
			</p>
		</c:if>--%>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
	<c:if test="${!empty deliverable.backlogItems}">
		<p>
			Has backlog items:
		</p>
		<p>
		<ul>
		<c:forEach items="${deliverable.backlogItems}" var="item">
			<ww:url id="editLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}"/>
			</ww:url>
			<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}"/>
			</ww:url>
			<li>
				${item.name} (${fn:length(item.tasks)} tasks) - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</li>
		</c:forEach>
		</ul>
		</p>
	</c:if>
</body>
</html>