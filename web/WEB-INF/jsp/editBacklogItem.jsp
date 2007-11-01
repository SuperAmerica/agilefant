<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<c:choose>
	<c:when test="${backlogItem.id == 0}">
		<aef:bct backlogId="${backlogId}"/>
	</c:when>
	<c:otherwise>
		<aef:bct backlogItemId="${backlogItemId}"/>
	</c:otherwise>
</c:choose>

<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
	
	<c:choose>
		<c:when test="${backlogItemId == 0}">
			<h2>Create backlog item</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit backlog item</h2>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${backlogItemId == 0}">
			<c:set var="new" value="New" scope="page"/>
		</c:when>
		<c:otherwise>
			<c:set var="new" value="" scope="page"/>
		</c:otherwise>
	</c:choose>
		
		<ww:form action="store${new}BacklogItem">
		<ww:hidden name="backlogItemId" value="${backlogItem.id}"/>
		<aef:userList/>
		<aef:currentUser/>
  	<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}"/>
  	<aef:productList/>

		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>

		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield size="60" name="backlogItem.name"/></td>	
		</tr>
		
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="70" rows="10" name="backlogItem.description"/></td>	
		</tr>
		
		<c:choose>
		<c:when test="${backlogItem.bliOrigEst == null}">
			<tr>
			<td>Original estimate</td>
			<td></td>
			<td><ww:textfield size="10" name="backlogItem.allocatedEffort"/><ww:label value="%{getText('webwork.estimateExample')}"/></td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
			<td>Original estimate</td>
			<td></td>
			<td><ww:label value="${backlogItem.bliOrigEst}"/>
			</tr>
			<tr>
			<td>Effort left</td>
			<td></td>
			<td><ww:textfield size="10" name="backlogItem.effortLeft" /><ww:label value="%{getText('webwork.estimateExample')}"/></td>
			</tr>
		</c:otherwise>
		</c:choose>
		
		<c:if test="${backlogItem.status != null}">
			<tr>
			<td>Status</td>
			<td></td>
			<td>
				<ww:select name="backlogItem.status" value="backlogItem.placeHolder.status.name" list="@fi.hut.soberit.agilefant.model.TaskStatus@values()" listKey="name" listValue="getText('task.status.' + name())"/>
			</td>
			</tr>
		</c:if>
		
		<tr>
		<td>Backlog</td>
		<td></td>
		<td>
		<select name="backlogId">	
				
			<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
			<option class="inactive" value="">(select backlog)</option>
			<c:forEach items="${productList}" var="product">
				<c:choose>
					<c:when test="${product.id == currentPageId}">
						<option selected="selected" value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
					</c:when>
					<c:otherwise>
						<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
					</c:otherwise>
				</c:choose>
				<c:forEach items="${product.deliverables}" var="deliverable">
					<c:choose>
						<c:when test="${deliverable.id == currentPageId}">
							<option selected="selected" value="${deliverable.id}" title="${deliverable.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(deliverable.name)}</option>
						</c:when>
						<c:otherwise>
							<option value="${deliverable.id}" title="${deliverable.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(deliverable.name)}</option>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${deliverable.iterations}" var="iteration">
						<c:choose>
							<c:when test="${iteration.id == currentPageId}">
								<option selected="selected" value="${iteration.id}" title="${iteration.name}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(iteration.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${iteration.id}" title="${iteration.name}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(iteration.name)}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:forEach>						
			</c:forEach>				
		</select>
		</td>
		</tr>
		
		<tr>
		<td>Iteration goal</td>
		<td></td>	
		
		<%-- If iteration goals doesn't exist default value is 0--%>
		<c:choose>
		<c:when test="${!empty iterationGoals}">
			<c:set var="goalId" value="0" scope="page"/>
			<c:if test="${iterationGoalId > 0}">
				<c:set var="goalId" value="${iterationGoalId}"/>
			</c:if>
			<c:if test="${!empty backlogItem.iterationGoal}">
				<c:set var="goalId" value="${backlogItem.iterationGoal.id}" scope="page"/>
			</c:if>
			<td><ww:select headerKey="0" headerValue="(none)" name="backlogItem.iterationGoal.id" list="#attr.iterationGoals" listKey="id" listValue="name" value="${goalId}"/></td>
		</c:when>
		<c:otherwise>
			<td>(none)</td>
		</c:otherwise>
		</c:choose>
		</tr>
		
		<tr>
		<td>Priority</td>
		<td></td>
		<td><ww:select name="backlogItem.priority" value="backlogItem.priority.name" list="#{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}"/></td>
		<%--
		If you change something about priorities, remember to update conf/classes/messages.properties as well!
		--%>	
		</tr>
		
		<tr>
			<td>Responsible</td>
			<td></td>
			<c:choose>
				<c:when test="${backlogItem.id == 0}">
					<td><ww:select headerKey="0" headerValue="(none)" name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="0"/></td>	
				</c:when>
				<c:otherwise>
					<td><ww:select headerKey="0" headerValue="(none)" name="assigneeId" list="#attr.userList" listKey="id" listValue="fullName" value="%{backlogItem.assignee.id}"/></td>	
				</c:otherwise>
			</c:choose>
		</tr>
		
		<tr>
		<td></td>
		<td></td>
		<td>
			<c:choose>
				<c:when test="${backlogItemId == 0}">
					<ww:submit value="Create"/>
					<ww:submit action="storeCloseBacklogItem" value="Create & Close"/>
				</c:when>
				<c:otherwise>
				  <ww:submit value="Save"/>
				  <ww:submit action="storeCloseBacklogItem" value="Save & Close"/>
				  <span class="deleteButton">
						<ww:submit action="deleteBacklogItem" 
								value="Delete" 
								onclick="return confirmDeleteBli()"/>
					</span>
				</c:otherwise>
			</c:choose>
		</td>	
		</tr>
		
		</table>

	</ww:form>
	
	
	<aef:currentUser/>

	<table>
	<tr><td>
		<c:if test="${backlogItem.id > 0}">
		<div id="subItems">
		<div id="subItemHeader">
			Tasks 
			<ww:url id="createLink" action="createTask" includeParams="none">
				<ww:param name="backlogItemId" value="${backlogItemId}"/>
			</ww:url>
			<ww:a href="%{createLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">Create new &raquo;</ww:a>
		</div>
		<c:if test="${!empty backlogItem.realTasks}">
		<div id="subItemContent">
		<p>
			<display:table class="listTable" name="backlogItem.realTasks" id="row" requestURI="editBacklogItem.action">
				<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
					<ww:url id="editLink" action="editTask" includeParams="none">
						<ww:param name="taskId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">
						${aef:html(row.name)}
					</ww:a>
				</display:column>
				<%--
				<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
					${row.effortEstimate}
				</display:column>
				
				<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
					${row.performedEffort}
				</display:column>
				
				<display:column sortable="true" title="Priority" defaultorder="descending"  sortProperty="priority.ordinal">
					<ww:text name="task.priority.${row.priority}"/>
				</display:column>
				--%>
				<display:column sortable="true" title="Status" sortProperty="status.ordinal">
					<ww:text name="task.status.${row.status}"/>
				</display:column>
				<display:column sortable="true" title="Created">
					<ww:date name="#attr.row.created" />
				</display:column>
				<%--
				<display:column sortable="true" sortProperty="assignee.fullName" title="Responsible">
					${aef:html(row.assignee.fullName)}
				</display:column>
				--%>
				<display:column sortable="true" sortProperty="creator.fullName" title="Creator">
					${aef:html(row.creator.fullName)}
				</display:column>
				<display:column sortable="false" title="Actions">
					<ww:url id="deleteLink" action="deleteTask" includeParams="none">
						<ww:param name="taskId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{deleteLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}" 
						onclick="return confirmDeleteTask()">Delete</ww:a>
				</display:column>
			</display:table>
		</p>
	
		</div>
		</c:if> <%-- No tasks --%>
		
		</div>
		</c:if>	<%-- New item --%>
		
</td></tr></table>
		
	

<%@ include file="./inc/_footer.jsp" %>
