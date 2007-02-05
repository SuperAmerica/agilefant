<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct backlogId="${backlogId}"/>

<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}"/> 
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
		<aef:userList/>
		<aef:currentUser/>
		
	<c:choose>
		<c:when test="${backlogItem.id == 0}">
			<p>
				Watch this item: <ww:checkbox name="watch" value="true" fieldValue="true"/>
			</p>
				<p>
					Assignee: <ww:select  headerKey="0" headerValue="None" name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="${currentUser.id}"/>
				</p>
					<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}"/>
			<c:if test="${!empty iterationGoals}">
					
			Link to iteration goal:	<ww:select name="backlogItem.iterationGoal.id" list="#attr.iterationGoals" listKey="id" listValue="name" />					
	
			</c:if>
		</c:when>
		<c:otherwise>
			<p>
				Assignee: <ww:select  headerKey="0" headerValue="None" name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="%{backlogItem.assignee.id}"/>
			</p>
			
		</c:otherwise>			
	</c:choose>				
		
		<p>
			<ww:submit value="Store"/>
    		<ww:submit name="action:contextView" value="Cancel"/>
			
		</p>
	</ww:form>
	
	<c:if test="${backlogItem.id > 0}">
		<aef:currentUser/>
		<p>
			<ww:url id="watchLink" action="watchBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${backlogItem.id}"/>
				<ww:param name="watch" value="${empty backlogItem.watchers[currentUser.id]}"/>
			</ww:url>
			<c:choose>
				<c:when test="${empty backlogItem.watchers[currentUser.id]}">
					<ww:a href="%{watchLink}">Start watching this item</ww:a>
				</c:when>
				<c:otherwise>
					<ww:a href="%{watchLink}">Stop watching this item</ww:a>
				</c:otherwise>
			</c:choose>
		</p>


		<ww:form action="linkToIterationGoal">
			<ww:hidden name="backlogItemId" value="${backlogItem.id}"/>
			<p>
				<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}"/>
		<c:if test="${!empty iterationGoals}">
			<c:set var="goalId" value="0" scope="page"/>
			<c:if test="${!empty backlogItem.iterationGoal}">
				<c:set var="goalId" value="${backlogItem.iterationGoal.id}" scope="page"/>
			</c:if>
			Link to iteration goal:	<ww:select headerKey="0" headerValue="None" name="iterationGoalId" list="#attr.iterationGoals" listKey="id" listValue="name" value="${goalId}"/>					
			<ww:submit value="link"/>
		</c:if>
			</p>
		</ww:form>

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
