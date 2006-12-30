<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Simple table displaying all tasks assigned to logged in user</h2>
   	<p>
   		<aef:currentUser/>   		
		<display:table name="${currentUser.assignments}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Backlog item">
									${row.backlogItem.name}			
			</display:column>

			<display:column sortable="true" title="Priority">
				${row.priority}
			</display:column>
			<display:column sortable="true" title="Name" property="name"/>
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				<input type="text" size="5" value="${row.effortEstimate}"/>
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}
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
				<ww:url id="reportLink" action="myTasksReportForm" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>|<ww:a href="%{reportLink}">Report hours</ww:a>
			</display:column>
		</display:table>
	</p>
	
<c:if test="${!empty task }">
<aef:allowedWorkTypes backlogItem="${task.backlogItem}" id="workTypes">
	<c:choose>
		<c:when test="${empty workTypes}">
			<p>
				<ww:url id="workTypeLink" action="listActivityTypes" includeParams="none"/>
				
				No work types avalable. <ww:a href="%{workTypeLink}">Add those first.</ww:a>			
			</p>				
		</c:when>
		<c:otherwise>
			<ww:form action="myTasksPerformWork">
				<ww:hidden name="taskId" value="${task.id}"/>
				<p>
					Report hours for task ${task.id }
				</p>
				<p>
					Work amount: <ww:textfield name="event.effort"/>
				</p>
				<p>
					New estimate: <ww:textfield name="event.newEstimate" value="${task.effortEstimate}"/>
				</p>
				<p>
					Work type: <ww:select name="event.workType.id" list="#attr.workTypes" listKey="id" listValue="name"/>
				</p>
		<p>
			Status: <ww:select name="task.status" value="task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>
		</p>
				
				<p>
					Comment: <ww:textarea name="event.comment" cols="50" rows="5"/>
				</p>
				<p>
					<ww:submit value="Submit"/><ww:submit value="Cancel" action="myTasks"/>
				</p>
			</ww:form>
		</c:otherwise>
	</c:choose>
</aef:allowedWorkTypes>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
