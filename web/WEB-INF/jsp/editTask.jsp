<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct backlogItemId="${backlogItemId}"/>

<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit task</h2>
	<ww:form action="storeTask">
		<ww:hidden name="backlogItemId"/>
		<ww:hidden name="taskId" value="${task.id}"/>

		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
			<td>Name</td>
		<td>*</td>
		<td><ww:textfield size="50" name="task.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="task.description" /></td>	
		</tr>
		<tr>
		<td>Effort left</td>
		<td></td>
		<td><ww:textfield name="task.effortEstimate"/></td>	
		</tr>
		<tr>
		<td>Priority</td>
		<td></td>
		<td><ww:select name="task.priority" value="task.priority.name" list="@fi.hut.soberit.agilefant.model.Priority@values()" listKey="name" listValue="getText('task.priority.' + name())"/></td>	
		</tr>
		<tr>
		<td>Status</td>
		<td></td>
		<td><ww:select name="task.status" value="task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/></td>	
		</tr>
		<tr>
		<td>
		<c:if test="${task.id == 0}">			
			Assignee
		</c:if>	
			</td>
		<td></td>
		<td>
		<c:if test="${task.id == 0}">
				<aef:userList/>
				<aef:currentUser/>
				<ww:select headerKey="0" headerValue="(none)" name="task.assignee.id" list="#attr.userList" listKey="id" listValue="fullName" value="${currentUser.id}"/>
		</c:if>
			
			
			</td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td>
						<ww:submit value="Store"/>
    		<ww:submit name="action:contextView" value="Cancel"/>

			</td>	
		</tr>
		</table>

	</ww:form>	
	
	<c:if test="${task.id > 0}">
		<aef:userList/>

		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
		<td>Assignee</td>
		<td></td>
		<td>
			<ww:form action="assignTask">
				<ww:hidden name="taskId" value="${task.id}"/>
				<ww:select   headerKey="0" headerValue="(none)"  name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="${task.assignee.id}"/>
				<ww:submit value="Assign"/>
			</ww:form>
		<p>
			<aef:currentUser/>
			<ww:url id="selfAssignLink" action="assignTask" includeParams="none">
				<ww:param name="taskId" value="${task.id}"/>
				<ww:param name="assigneeId" value="${currentUser.id}"/>
			</ww:url>
			<ww:a href="%{selfAssignLink}">Assign to me</ww:a>
		</p>
			
			
			</td>	
		</tr>
		<tr>
		<td>Watch</td>
		<td></td>
		<td>
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
			
			
			</td>	
		</tr>
		<tr>
		<td>Report</td>
		<td></td>
		<td>
			<ww:url id="performWorkLink" action="editMyTasks" includeParams="none"/>
			<ww:a href="%{performWorkLink}">Report work</ww:a>
			
			
			</td>	
		</tr>
		</table>
		
	

<table><tr><td>
		<div id="subItems">
		<div id="subItemHeader">
			Subitems
		</div>
		<div id="subItemContent">


		<p>
			Event history
		</p>
		<p>
			<display:table class="listTable" name="task.events" id="row" requestURI="editTask.action">
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
	</div>
	</div>
</td></tr></table>
	
	</c:if>
<%@ include file="./inc/_footer.jsp" %>