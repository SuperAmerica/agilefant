<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	
		Edit | View
	<h2>Tasks assigned to me</h2>
   	<p>
   		<aef:currentUser/>   		
	<ww:form action="myTasksPerformWork">

		<display:table name="${currentUser.assignments}" id="row" requestURI="myTasks.action">

			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">${row.name}</ww:a>
			</display:column>
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}

			</display:column>
			
			<display:column sortable="true" title="Work type">
						<aef:allowedWorkTypes backlogItem="${row.backlogItem}" id="workTypes">
	<c:choose>
			
							<c:when test="${empty workTypes}">
								<p>
									<ww:url id="workTypeLink" action="listActivityTypes" includeParams="none"/>
									
									No work types avalable. <ww:a href="%{workTypeLink}">Add those first.</ww:a>			
								</p>				
							</c:when>
			                <c:otherwise>
			                	<ww:select name="event.workType.id" list="#attr.workTypes" listKey="id" listValue="name"/>
			                </c:otherwise>
	</c:choose>

						</aef:allowedWorkTypes>
			</display:column>
			
			<display:column sortable="true" title="Effort" >
			
							<ww:hidden name="taskId" value="${row.id}"/>
								<ww:textfield name="event.effort" size="2"/>			
			
			</display:column>
			<display:column sortable="true" title="New estimate" >
					<ww:textfield name="event.newEstimate" value="${row.effortEstimate}"/>			
			</display:column>
			<display:column sortable="true" title="Status" >
<ww:select name="task.status" value="task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>			</display:column>
			<display:column sortable="true" title="Comment" >
					<ww:textfield name="event.comment" size="10"/>			
			</display:column>
			
			<display:column sortable="false" title="Actions">
								<ww:submit value="Submit"/>
			</display:column>
		</display:table>
						</ww:form>

	</p>
	

<%@ include file="./inc/_footer.jsp" %>
