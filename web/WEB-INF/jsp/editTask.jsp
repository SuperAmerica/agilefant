<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct backlogItemId="${backlogItemId}"/>

<aef:menu navi="1" pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit task</h2>
	<ww:form action="storeTask">
		<ww:hidden name="backlogItemId"/>
		<ww:hidden name="taskId" value="${task.id}"/>
		<p>		
			Name: <ww:textfield name="task.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="task.description" />
		</p>
		<p>		
			Effort left: <ww:textfield name="task.effortEstimate"/>
		</p>
		<p>
			Priority: <ww:select name="task.priority" value="task.priority.name" list="@fi.hut.soberit.agilefant.model.Priority@values()" listKey="name" listValue="getText('task.priority.' + name())"/>
		</p>
		<p>
			Status: <ww:select name="task.status" value="task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>
		</p>
		<c:if test="${task.id == 0}">
			<p>
				<aef:userList/>
				<aef:currentUser/>
				Assignee: <ww:select name="task.assignee.id" list="#attr.userList" listKey="id" listValue="fullName" value="${currentUser.id}"/>
			</p>
		</c:if>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>	
	<c:if test="${task.id > 0}">
		<p>
			Assigned to: ${task.assignee.fullName}
		</p>
		<aef:userList/>
		<p>
			<ww:form action="assignTask">
				<ww:hidden name="taskId" value="${task.id}"/>
				Reassign to: <ww:select name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="${task.assignee.id}"/>
				<ww:submit value="Assign"/>
			</ww:form>
		</p>
		<p>
			<aef:currentUser/>
			<ww:url id="selfAssignLink" action="assignTask" includeParams="none">
				<ww:param name="taskId" value="${task.id}"/>
				<ww:param name="assigneeId" value="${currentUser.id}"/>
			</ww:url>
			<ww:a href="%{selfAssignLink}">Assign to me</ww:a>
		</p>
		<p>
			<ww:url id="watchLink" action="watchTask" includeParams="none">
				<ww:param name="taskId" value="${task.id}"/>
				<ww:param name="watch" value="${empty task.watchers[currentUser.id]}"/>
			</ww:url>
			<c:choose>
				<c:when test="${empty task.watchers[currentUser.id]}">
					<ww:a href="%{watchLink}">Start watching this task</ww:a>
				</c:when>
				<c:otherwise>
					<ww:a href="%{watchLink}">Stop watching this task</ww:a>
				</c:otherwise>
			</c:choose>
		</p>
		<p>
			<ww:url id="performWorkLink" action="performWorkForm" includeParams="none">
				<ww:param name="taskId" value="${task.id}"/>
			</ww:url>
			<ww:a href="%{performWorkLink}">Report work</ww:a>
		</p>
<hr/>
		<p>
			Event history:
		</p>
		<p>
			<display:table name="task.events" id="row" requestURI="editTask.action">
				<display:column property="id" sortable="true"/>
				<display:column property="created" sortable="true"/>
				<display:column title="Actor"  sortable="true" property="actor.fullName"/>
				<display:column title="Type" sortable="true">									
					<c:choose>
						<c:when test="${aef:isAssignEvent(row)}">
						Assign							
						</c:when>
						<c:when test="${aef:isPerformedWork(row)}">
						Effort							
						</c:when>
						<c:when test="${aef:isEstimateHistoryEvent(row)}">
						Estimate							
						</c:when>
					</c:choose>
				</display:column>
				<display:column title="Value" sortable="true">									
					<c:choose>
						<c:when test="${aef:isAssignEvent(row)}">
						${row.newAssignee.fullName}
						</c:when>
						<c:when test="${aef:isPerformedWork(row)}">
						${row.effort}
						</c:when>
						<c:when test="${aef:isEstimateHistoryEvent(row)}">
						${row.newEstimate}
						</c:when>
					</c:choose>
				</display:column>
				<display:column title="Comment"  sortable="true">									
					<c:choose>
						<c:when test="${aef:isTaskComment(row)}">
						${row.comment} 							
						</c:when>
						</c:choose>
				</display:column>

			</display:table>
		</p>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>