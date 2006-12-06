<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct productId="${productId}"/>
<aef:menu navi="1" pageHierarchy="${pageHierarchy}" /> 

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
			Description: <ww:textarea cols="40" rows="6" name="deliverable.description" />
		</p>
		<p>
			<c:choose>
				<c:when test="${deliverable.id > 0 && !empty deliverable.activityType}">
					Activity type: ${deliverable.activityType.name}
				</c:when>
				<c:otherwise>
					Activity type: <ww:select name="activityTypeId" list="#attr.activityTypes" listKey="id" listValue="name" value="${deliverable.activityType.id}"/>
				</c:otherwise>
			</c:choose>
		</p>
		<p>
			Start date: <ww:textfield name="deliverable.startDate"/>
		</p>
		<p>
			End date: <ww:textfield name="deliverable.endDate"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>		
		<c:if test="${deliverable.id > 0}">
			<h3>Iterations</h3>
			<p>
 				<ww:url id="createLink" action="createIteration" includeParams="none">
					<ww:param name="deliverableId" value="${deliverable.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new</ww:a>
			</p>

			<display:table name="deliverable.iterations" id="row" requestURI="editDeliverable.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="# of backlog items">
					${fn:length(row.backlogItems)}
				</display:column>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editIteration" includeParams="none">
						<ww:param name="iterationId" value="${row.id}"/>
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
					</ww:url>
					<ww:url id="deleteLink" action="deleteIteration" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
						<ww:param name="iterationId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>
		<h3>
			Has backlog items:
		</h3>
		<p>
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${deliverable.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}">Create new</ww:a>		
		</p>
		</c:if>
	<c:if test="${!empty deliverable.backlogItems}">
		<display:table name="deliverable.backlogItems" id="row" requestURI="editDeliverable.action">
			<display:column sortable="true" title="Id" property="id"/>
			<display:column sortable="true" title="Name" property="name"/>
			<display:column sortable="true" title="# of tasks">
				${fn:length(row.tasks)}
			</display:column>
			<display:column sortable="true" title="Effort estimate" sortProperty="remainingEffortEstimate.time">
				${row.remainingEffortEstimate}
			</display:column>
			<display:column sortable="true" title="Effort in tasks" sortProperty="taskEffortLeft.time">
				${row.taskEffortLeft}
			</display:column>
			<display:column sortable="false" title="Actions">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
	</c:if>

<%@ include file="./inc/_footer.jsp" %>
