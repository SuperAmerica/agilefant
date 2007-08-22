<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<c:if test="${task.id == 0}">
	<aef:bct backlogItemId="${backlogItemId}"/>
</c:if>
<c:if test="${task.id > 0}">
	<aef:bct taskId="${taskId}"/>
</c:if>

<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}"/> 

<ww:actionerror/>
<ww:actionmessage/>

	<c:choose>
		<c:when test="${taskId == 0}">
			<h2>Create task</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit task</h2>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${taskId == 0}">
			<c:set var="new" value="New" scope="page"/>
		</c:when>
		<c:otherwise>
			<c:set var="new" value="" scope="page"/>
		</c:otherwise>
	</c:choose>
	
	<ww:form action="store${new}Task">
	
		<ww:hidden name="backlogItemId"/>
		<ww:hidden name="taskId" value="${task.id}"/>
		<aef:userList/>
		<aef:currentUser/>
	
		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td><ww:textfield size="60" name="task.name"/></td>	
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td><ww:textarea cols="70" rows="10" name="task.description" /></td>	
		</tr>
		<tr>
			<td>Effort left</td>
			<td></td>
			<td><ww:textfield size="10" name="task.effortEstimate"/> (usage: *h *m, where * integer, for example 3h)</td>	
		</tr>
		<tr>
			<td>Status</td>
			<td></td>
			<td><ww:select name="task.status" value="task.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/></td>	
		</tr>
		<!-- <tr>
			<td>Backlog item</td>
			<td></td>
			<td>
			<select name="" value="" /></td>	
		</tr> -->
		<tr>
			<td>Priority</td>
			<td></td>
			<td><ww:select name="task.priority" value="task.priority.name" list="@fi.hut.soberit.agilefant.model.Priority@values()" listKey="name" listValue="getText('task.priority.' + name())"/></td>	
		</tr>
		<tr>
			<td>Responsible</td>
			<td></td>
			<c:choose>
				<c:when test="${task.id == 0}">
					<td><ww:select headerKey="0" headerValue="(none)" name="task.assignee.id" list="#attr.userList" listKey="id" listValue="fullName" value="0"/></td>	
				</c:when>
				<c:otherwise>
					<td><ww:select headerKey="0" headerValue="(none)" name="task.assignee.id" list="#attr.userList" listKey="id" listValue="fullName" value="%{task.assignee.id}"/></td>	
				</c:otherwise>
			</c:choose>
		</tr>
		
		<tr>
			<td>Watch this item</td>
			<td></td>
		
			<%-- TaskAction.store() sets the watch-field to false unless this checkbox is checked --%>
			<c:choose>
				<%-- When creating a task watching is off by default--%>
				<c:when test="${task.id == 0}">
					<td><ww:checkbox name="watch" value="false" fieldValue="true"/></td>	
				</c:when>
				<c:otherwise>
					<td><ww:checkbox name="watch" value="${!empty task.watchers[currentUser.id]}" fieldValue="true"/></td>	
				</c:otherwise>
			</c:choose>
		</tr>
		
		<tr>
			<td></td>
			<td></td>
			<td>
				<c:choose>
				<c:when test="${taskId == 0}">
					<ww:submit value="Create"/>
				</c:when>
				<c:otherwise>
				  <ww:submit value="Save"/>
				  <span class="deleteButton">
						<ww:submit action="deleteTask" 
								value="Delete" 
								onclick="return confirmDeleteTask()"/>
					</span>
				</c:otherwise>
			</c:choose>
			</td>	
		</tr>
	</table>
	</ww:form>
	

<table><tr><td>
	<div id="subItems">
		<div id="subItemHeader">
			Event history
		</div>
		<c:if test="${task.id > 0}">
		<div id="subItemContent">
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
	</c:if>
	</div>
</td></tr></table>

<%@ include file="./inc/_footer.jsp" %>