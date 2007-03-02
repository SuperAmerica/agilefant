<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>
<c:set var="divId" value="1336" scope="page"/>
	
	<ww:url id="editMyTasksLink" action="editMyTasks" includeParams="none">
	</ww:url>
	
	View | <ww:a href="%{editMyTasksLink}">Edit</ww:a>

  		<aef:userList/>
   		<aef:currentUser/>   		
			<c:if test="${empty user}">
				<c:set var="user" value="${currentUser}" scope="page"/>
			</c:if>
			
   		<aef:unfinishedTaskList userId="${user.id}"/>
   		<%-- <aef:heartbeatTimeBoxLists userId="${user.id}"/> --%>
   		<aef:unfinishedWatchedTasksList userId="${user.id}"/>

		<p>
			<ww:form action="myTasksSwitchUser">
				<ww:select name="userId" list="#attr.userList" listKey="id" listValue="fullName" value="${user.id}" />
				<ww:submit value="View items for user"/>
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
				<ww:a title="${row.name}" href="%{editLink}&contextViewName=myTasks">${aef:subString(row.name, 25)}</ww:a>
			</display:column>

			<display:column sortable="true" title="Backlog item">
									${aef:html(row.backlogItem.name)}			
			</display:column>

			<display:column sortable="true" title="Priority">
				<ww:text name="task.priority.${row.priority}"/>
			</display:column>
			<display:column sortable="true" title="Status">
				<ww:text name="task.status.${row.status}"/>
			</display:column>
				
				
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}

			</display:column>
			<display:column sortable="true" title="Created" >
				<ww:date name="#attr.row.created" />
			</display:column>
			<display:column sortable="true" title="Responsible">
				${aef:html(row.assignee.fullName)}
			</display:column>
			<display:column sortable="true" title="Creator">
				${aef:html(row.creator.fullName)}
			</display:column>
		</display:table>
	
	<p>
		Total effort left: ${user.assignmentsTotalEffortEstimate}<br>
		Total performed work: ${user.assignmentsTotalPerformedEffort}<br>
	</p>
	
	<p>Assigned backlog items</p>
   	
	
										  <%-- ${user.backlogItems} --%>
		<display:table class="listTable" name="${user.backlogItems}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}&contextViewName=myTasks">${aef:subString(row.name, 25)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Backlog">
				${aef:html(row.parent.name)}
			</display:column>


				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="divId" value="${divId + 1}" scope="page"/>
							
							<a href="javascript:toggleDiv(${divId});" title="Click to expand">
							   ${fn:length(row.tasks)} tasks, <aef:percentDone backlogItemId="${row.id}"/> % done <br/>
   								<aef:taskStatusList backlogItemId="${row.id}" id="tsl"/>							   
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted"  value="${tsl['notStarted']}"/>
									<ww:param name="started"     value="${tsl['started']}"/>
									<ww:param name="blocked"     value="${tsl['blocked']}"/>
									<ww:param name="implemented" value="${tsl['implemented']}"/>
									<ww:param name="done"        value="${tsl['done']}"/>
								</ww:url>
			 					<img src="${imgUrl}"/> 

							  </a>
		
							<aef:tasklist tasks="${row.tasks}" contextViewName="myTasks" divId="${divId}"/>

				    </c:if>

				</display:column>

				<display:column sortable="false" title="Responsible" >
					${aef:html(row.assignee.fullName)}
				</display:column>
				<display:column sortable="true" title="Priority" >
				<ww:text name="backlogItem.priority.${row.priority}"/>
				</display:column>
				<display:column sortable="true" title="Status">
				<ww:text name="backlogItem.status.${row.status}"/>
				</display:column>
				
				<display:column sortable="true" title="Iteration Goal">
					${aef:html(row.iterationGoal.name)}
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
				<ww:a title="${row.name}" href="%{editLink}&contextViewName=myTasks">${aef:subString(row.name, 25)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Backlog">
				${aef:html(row.parent.name)}
			</display:column>

				<display:column sortable="false" title="Tasks">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="divId" value="${divId + 1}" scope="page"/>
							
							<a href="javascript:toggleDiv(${divId});" title="Click to expand">
								${fn:length(row.tasks)} tasks, <aef:percentDone backlogItemId="${row.id}"/> % done<br/>
								<aef:taskStatusList backlogItemId="${row.id}" id="taskStatusList"/>
   								<aef:taskStatusList backlogItemId="${row.id}" id="tsl"/>							   
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted"  value="${tsl['notStarted']}"/>
									<ww:param name="started"     value="${tsl['started']}"/>
									<ww:param name="blocked"     value="${tsl['blocked']}"/>
									<ww:param name="implemented" value="${tsl['implemented']}"/>
									<ww:param name="done"        value="${tsl['done']}"/>
								</ww:url>
			 					<img src="${imgUrl}"/> 
							</a>

							<aef:tasklist tasks="${row.tasks}" contextViewName="myTasks" divId="${divId}"/>


				    </c:if>

				</display:column>

				<display:column sortable="true" title="Responsible" >
					${aef:html(row.assignee.fullName)}
				</display:column>
				<display:column sortable="true" title="Priority" >
				<ww:text name="backlogItem.priority.${row.priority}"/>
				</display:column>
				<display:column sortable="true" title="Status">
				<ww:text name="backlogItem.status.${row.status}"/>
				</display:column>
				
				<display:column sortable="true" title="Iteration Goal">
					${aef:html(row.iterationGoal.name)}
				</display:column>

			</display:table>
		
		

	<p>Watched tasks</p>
		<display:table  class="listTable" name="${unfinishedWatchedTasksList}" id="row" requestURI="myTasks.action">
							
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}&contextViewName=myTasks">${aef:subString(row.name, 25)}</ww:a>
			</display:column>
			<display:column sortable="true" title="Backlog item">
									${aef:html(row.backlogItem.name)}			
			</display:column>

			<display:column sortable="true" title="Priority">
				<ww:text name="task.priority.${row.priority}"/>
			</display:column>
				<display:column sortable="true" title="Status">
				<ww:text name="task.status.${row.status}"/>
				</display:column>
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}
			</display:column>
			<display:column sortable="true" title="Created" >
				<ww:date name="#attr.row.created" />
			</display:column>
				
			<display:column sortable="true" title="Responsible">
				${aef:html(row.assignee.fullName)}
			</display:column>
			<display:column sortable="true" title="Creator">
				${aef:html(row.creator.fullName)}
			</display:column>
		</display:table>

	<p>
		Total effort left: ${user.watchedTasksTotalEffortEstimate}<br>
		Total performed work: ${user.watchedTasksTotalPerformedEffort}<br>
	</p>

</div>
</div>
</td></tr></table>
	


<%@ include file="./inc/_footer.jsp" %>
