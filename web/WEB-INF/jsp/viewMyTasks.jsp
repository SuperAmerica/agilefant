<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	
	<ww:url id="showMyTasksLink" action="myTasks" includeParams="none"/>
	
	<ww:a href="%{showMyTasksLink}">View</ww:a> | Edit
	
	<h2>Tasks assigned to me</h2>
   	
   		<aef:currentUser/>   		
   		<aef:hourReportTaskList id="unfinishedTasks" userId="${currentUser.id}"/>   		
   			
<ww:date name="%{new java.util.Date()}" format="%{getText('webwork.date.format')}" id="now"/>
 			   			

<c:forEach items="${unfinishedTasks}" var="unfinishedTaskList">
<p>
	<ww:form action="myTasksPerformWork">
		<display:table name="${unfinishedTaskList}" id="row" requestURI="myTasks.action">

			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a title="${row.name}" href="%{editLink}&contextViewName=editMyTasks">${aef:out(row.name)}</ww:a>
			</display:column>
			<display:column sortable="false" title="Effort done" sortProperty="performedEffort.time">
							<ww:hidden name="taskId" value="${row.id}"/>
								<ww:textfield name="event.effort" size="2"/>			

				&nbsp;Total: ${row.performedEffort}

			</display:column>
			<display:column sortable="false" title="Effort left" sortProperty="effortEstimate.time">
					<ww:textfield name="event.newEstimate" value="${row.effortEstimate}"  size="2"/>			
				&nbsp;Total: ${row.effortEstimate}
			</display:column>
			
			<display:column sortable="false" title="Work type">
						<aef:allowedWorkTypes backlogItem="${row.backlogItem}" id="workTypes">
							<c:choose>
									
								<c:when test="${empty workTypes}">
									<p>
										<ww:url id="workTypeLink" action="listActivityTypes" includeParams="none"/>
										
										No work types avalable. <ww:a href="%{workTypeLink}">Add those first.</ww:a>			
									</p>				
								</c:when>
				                <c:otherwise>
                	
									<select name="event.workType.id">
										<c:forEach items="${workTypes}" var="workType">
											<option value="${workType.id}" title="${workType.name}">${aef:out(workType.name)}</option>
										</c:forEach>				
									</select>
	
				                </c:otherwise>
							</c:choose>

						</aef:allowedWorkTypes>
			</display:column>
			
			<display:column sortable="false" title="Status" >
				<ww:select name="task.status" value="#attr.row.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>			
			</display:column>
			<display:column sortable="false" title="Comment" >
				<ww:textfield name="event.comment" size="15"/>			
			</display:column>
			<display:column sortable="false" title="Work Date" >

			    <ww:datepicker value="%{#now}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="event.workDate"/> 
			    
			</display:column>
			
			<display:column sortable="false" title="Actions">
					<ww:submit value="Submit"/>
			</display:column>
		</display:table>
				</ww:form>

<p>
</c:forEach> 			   			


		
 			
 			
	

<%@ include file="./inc/_footer.jsp" %>
