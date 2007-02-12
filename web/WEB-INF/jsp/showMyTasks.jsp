<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>
<c:set var="did" value="1336" scope="page"/>
	
	<ww:url id="editMyTasksLink" action="editMyTasks" includeParams="none"/>
	
	View | <ww:a href="%{editMyTasksLink}">Edit</ww:a>

  		<aef:userList/>
   		<aef:currentUser/>   		
   		
			<c:if test="${empty user}">
				<c:set var="user" value="${currentUser}" scope="page"/>
			</c:if>
			
   		<aef:unfinishedTaskList userId="${user.id}"/>

		<p>
			<ww:form action="myTasksSwitchUser">
				<ww:select name="userId" list="#attr.userList" listKey="id" listValue="fullName" value="${user.id}" />
				<ww:submit value="Switch user"/>
			</ww:form>
		</p>

	<h2>Assigned tasks</h2>
   	<p>
   	 	
   	

		<display:table class="listTable"  name="${unfinishedTaskList}" id="row" requestURI="myTasks.action">

			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>

			<display:column sortable="true" title="Backlog item">
									${aef:out(row.backlogItem.name)}			
			</display:column>

			<display:column sortable="true" title="Priority">
				${row.priority}
			</display:column>
				
				
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}

			</display:column>
			<display:column sortable="true" title="Created" property="created"/>
			<display:column sortable="true" title="Assignee">
				${aef:out(row.assignee.fullName)}
			</display:column>
			<display:column sortable="true" title="Creator">
				${aef:out(row.creator.fullName)}
			</display:column>
		</display:table>
</p>	
	<h2>Assigned backlog items</h2>
   	<p>
	

		<display:table class="listTable" name="${user.backlogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>


				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="did" value="${did + 1}" scope="page"/>
							
							
							<a href="javascript:toggleDiv(${did});" title="Click to expand">${fn:length(row.tasks)} tasks, summary etc...</a>
							<div id="${did}" style="display:none;">
							<c:forEach items="${row.tasks}" var="task">
							${aef:out(task.name)} - ${task.status}<br/>
							</c:forEach>
							</div>
				    </c:if>

				</display:column>

				<display:column sortable="false" title="Assignee" >
					${aef:out(row.assignee.fullName)}
				</display:column>
				<display:column sortable="false" title="Priority" >
					${row.priority}
				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${aef:out(row.iterationGoal.name)}
				</display:column>

			</display:table>
		
		
	</p>

<hr/>
	<h2>Watched backlog items</h2>
   	<p>
		
		<display:table  class="listTable" name="${user.watchedBacklogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>

				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="did" value="${did + 1}" scope="page"/>
							
							
							<a href="javascript:toggleDiv(${did});" title="Click to expand">${fn:length(row.tasks)} tasks, summary etc...</a>
							<div id="${did}" style="display:none;">
							<c:forEach items="${row.tasks}" var="task">
							${aef:out(task.name)} - ${task.status}<br/>
							</c:forEach>
							</div>
				    </c:if>

				</display:column>

				<display:column sortable="false" title="Assignee" >
					${aef:out(row.assignee.fullName)}
				</display:column>
				<display:column sortable="false" title="Priority" >
					${row.priority}
				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${aef:out(row.iterationGoal.name)}
				</display:column>

			</display:table>
		
		
	</p>

	<h2>Watched tasks</h2>
   	<p>
		<display:table  class="listTable" name="${user.watchedTasks}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Backlog item">
									${aef:out(row.backlogItem.name)}			
			</display:column>

			<display:column sortable="true" title="Priority">
				${row.priority}
			</display:column>
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}"
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}
			</display:column>
			<display:column sortable="true" title="Created" property="created"/>
			<display:column sortable="true" title="Assignee">
				${aef:out(row.assignee.fullName)}
			</display:column>
			<display:column sortable="true" title="Creator">
				${aef:out(row.creator.fullName)}
			</display:column>
		</display:table>
	</p>

	


<%@ include file="./inc/_footer.jsp" %>
