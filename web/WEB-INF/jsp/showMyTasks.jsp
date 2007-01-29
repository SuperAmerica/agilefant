<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>

	<ww:url id="editMyTasksLink" action="editMyTasks" includeParams="none"/>
	
	View | <ww:a href="%{editMyTasksLink}">Edit</ww:a>
	<h2>Tasks assigned to me</h2>
   	<p>
   	
   		<aef:currentUser/>   		
   	
   			<aef:userList/>
		<p>
			<ww:form action="myTasksSwitchUser">
				<ww:select name="userId" list="#attr.userList" listKey="id" listValue="fullName" />
				<ww:submit value="Switch user"/>
			</ww:form>
		</p>
   	
	<c:choose>
		<c:when test="${empty user || user == currentUser}">

	
		<display:table name="${currentUser.assignments}" id="row" requestURI="myTasks.action">

			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
			</display:column>

			<display:column sortable="true" title="Backlog item">
									${row.backlogItem.name}			
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
				${row.assignee.fullName}
			</display:column>
			<display:column sortable="true" title="Creator">
				${row.creator.fullName}
			</display:column>
		</display:table>

	<h2>Backlog items assigned to me</h2>
   	<p>
		<display:table name="${currentUser.backlogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
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
		</display:table>
	</p>


<hr/>
	<h2>Backlog items watched by me</h2>
   	<p>
		<display:table name="${currentUser.watchedBacklogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
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
		</display:table>
	</p>

	<h2>Tasks watched by me</h2>
   	<p>
		<display:table name="${currentUser.watchedTasks}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Backlog item">
									${row.backlogItem.name}			
			</display:column>

			<display:column sortable="true" title="Priority">
				${row.priority}
			</display:column>
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
			</display:column>
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}"
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
		</display:table>
	</p>

		</c:when>
		<c:otherwise>

		<display:table name="${user.assignments}" id="row" requestURI="myTasks.action">

			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
			</display:column>

			<display:column sortable="true" title="Backlog item">
									${row.backlogItem.name}			
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
				${row.assignee.fullName}
			</display:column>
			<display:column sortable="true" title="Creator">
				${row.creator.fullName}
			</display:column>
		</display:table>

	<h2>Backlog items assigned to me</h2>
   	<p>
		<display:table name="${user.backlogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
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
		</display:table>
	</p>



		</c:otherwise>
	</c:choose>


	


<%@ include file="./inc/_footer.jsp" %>
