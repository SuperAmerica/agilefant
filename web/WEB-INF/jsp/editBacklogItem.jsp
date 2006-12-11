<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct backlogId="${backlogId}"/>

<aef:menu navi="1" pageHierarchy="${pageHierarchy}"/> 
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
			Description: <ww:textarea cols="40" rows="6" name="backlogItem.description" />
		</p>
		<p>
			Allocated effort: <ww:textfield name="backlogItem.allocatedEffort"/>
		</p>
		<p>
			Priority: <ww:select name="backlogItem.priority" value="backlogItem.priority.name" list="@fi.hut.soberit.agilefant.model.Priority@values()" listKey="name" listValue="getText('backlogItem.priority.' + name())"/>
		</p>
		
		
		<c:if test="${backlogItem.id == 0}">
			<p>
				<aef:userList/>
				<aef:currentUser/>
				Assignee: <ww:select name="backlogItem.assignee.id" list="#attr.userList" listKey="id" listValue="fullName" value="${currentUser.id}"/>
			</p>
		</c:if>
		
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
							<c:forEach items="${deliverable.iterations}" var="iteration">
								<c:choose>
									<c:when test="${iteration.id == backlogItem.backlog.id}">
										<option selected="selected" value="${iteration.id}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${iteration.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${iteration.id}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${iteration.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:forEach>						
					</c:forEach>				
				</select>
			</p>			
			<ww:submit value="move"/>
		</ww:form>
		
		
		
				<p>
			Assigned to: ${backlogItem.assignee.fullName}
		</p>

		
		<p>
			Tasks:
		</p>
		<p>
			<ww:url id="createLink" action="createTask" includeParams="none">
				<ww:param name="backlogItemId" value="${backlogItemId}"/>
			</ww:url>
			<ww:a href="%{createLink}">Add task</ww:a>
		</p>
	</c:if>	

	<c:if test="${!empty backlogItem.tasks}">

		<p>
			<display:table name="backlogItem.tasks" id="row" requestURI="editBacklogItem.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
					${row.effortEstimate}
				</display:column>
				<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Priority" sortProperty="priority.ordinal">
					<ww:text name="task.priority.${row.priority}"/>
				</display:column>
				<display:column sortable="true" title="Status" sortProperty="status.ordinal">
					<ww:text name="task.status.${row.status}"/>
				</display:column>
				<display:column sortable="true" title="Created" property="created"/>
				<display:column sortable="true" title="Assignee">
					${row.assignee.fullName}
				</display:column>
				<display:column sortable="true" title="Creator">
					${row.creator.fullName}
				</display:column>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editTask" includeParams="none">
						<ww:param name="taskId" value="${row.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteTask" includeParams="none">
						<ww:param name="taskId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>
		</p>
	</c:if>
	<c:if test="${backlogItem.id > 0}">
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
