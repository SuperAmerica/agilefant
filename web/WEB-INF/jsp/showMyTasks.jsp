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


<table><tr><td>

			<div id="subItems">
		<div id="subItemHeader">
			Assigned items
		</div>
		<div id="subItemContent">


	<p>Assigned tasks</p>

		<display:table class="listTable"  name="${unfinishedTaskList}" id="row" requestURI="myTasks.action">

			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>

			<display:column sortable="true" title="Backlog item">
									${aef:out(row.backlogItem.name)}			
			</display:column>

			<display:column sortable="true" title="Priority">
				${row.priority}
			</display:column>
			<display:column sortable="true" title="Status">
				${row.status}
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
	
	<p>Assigned backlog items</p>
   	
	

		<display:table class="listTable" name="${user.backlogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Backlog">
				${aef:out(row.parent.name)}
			</display:column>


				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="did" value="${did + 1}" scope="page"/>
							
							<a href="javascript:toggleDiv(${did});" title="Click to expand">${fn:length(row.tasks)} tasks, ??% complete</a>

		<table cellspacing="0" cellpadding="0" border="0" class="chartTable">
		<tr>
		<td height="5" width="10%" class="notStarted"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td title="asdasdf" height="5" width="40%"  class="started"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td  height="5" width="10%" class="implemented"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td  height="5" width="20%" class="done"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td  height="5" width="20%" class="blocked"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		</tr>
		</table>
							<div id="${did}" style="display:none;">
							<c:forEach items="${row.tasks}" var="task">
								<ww:url id="editLink" action="editTask" includeParams="none">
									<ww:param name="taskId" value="${task.id}"/>
								</ww:url>
								<ww:a href="%{editLink}">${aef:out(task.name)} - ${task.status}</ww:a>								
								<br/>
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
				<display:column sortable="true" title="Status">
					${row.status}
				</display:column>
				
				<display:column sortable="true" title="Iteration Goal">
					${aef:out(row.iterationGoal.name)}
				</display:column>

			</display:table>
		
</div>
</div>
</td></tr></table>


<table><tr><td>

			<div id="subItems">
		<div id="subItemHeader">
			Watched items
		</div>
		<div id="subItemContent">

		
	<p>Watched backlog items</p>

		
		<display:table  class="listTable" name="${user.watchedBacklogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Backlog">
				${aef:out(row.parent.name)}
			</display:column>

				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="did" value="${did + 1}" scope="page"/>
							
							<a href="javascript:toggleDiv(${did});" title="Click to expand">${fn:length(row.tasks)} tasks, ??% complete</a>

		<table cellspacing="0" cellpadding="0" border="0" class="chartTable">
		<tr>
		<td height="5" width="10%" class="notStarted"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td title="asdasdf" height="5" width="40%"  class="started"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td  height="5" width="10%" class="implemented"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td  height="5" width="20%" class="done"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		<td  height="5" width="20%" class="blocked"><img height="5" src="/agilefant/static/img/clear.gif"></td>
		</tr>
		</table>
							<div id="${did}" style="display:none;">
							<c:forEach items="${row.tasks}" var="task">
								<ww:url id="editLink" action="editTask" includeParams="none">
									<ww:param name="taskId" value="${task.id}"/>
								</ww:url>
								<ww:a href="%{editLink}">${aef:out(task.name)} - ${task.status}</ww:a>								
								<br/>
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
				<display:column sortable="true" title="Status">
					${row.status}
				</display:column>
				
				<display:column sortable="true" title="Iteration Goal">
					${aef:out(row.iterationGoal.name)}
				</display:column>

			</display:table>
		
		

	<p>Watched tasks</p>
		<display:table  class="listTable" name="${user.watchedTasks}" id="row" requestURI="myTasks.action">
							
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}">${aef:out(row.name)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Backlog item">
									${aef:out(row.backlogItem.name)}			
			</display:column>

			<display:column sortable="true" title="Priority">
				${row.priority}
			</display:column>
				<display:column sortable="true" title="Status">
					${row.status}
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


</div>
</div>
</td></tr></table>
	


<%@ include file="./inc/_footer.jsp" %>
