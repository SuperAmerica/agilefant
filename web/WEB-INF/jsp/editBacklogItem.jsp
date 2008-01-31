<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:choose>
	<c:when test="${backlogItem.id == 0}">
		<aef:bct backlogId="${backlogId}" />
	</c:when>
	<c:otherwise>
		<aef:bct backlogItemId="${backlogItemId}" />
	</c:otherwise>
</c:choose>

<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />

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
		<c:set var="new" value="New" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="new" value="" scope="page" />
	</c:otherwise>
</c:choose>

<ww:form action="store${new}BacklogItem">
	<ww:hidden name="backlogItemId" value="${backlogItem.id}" />
	<aef:userList />
	<aef:teamList />
	<aef:currentUser />
	<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}" />
	<aef:productList />

	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="backlogItem.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				name="backlogItem.description" /></td>
		</tr>
		<c:choose>
			<c:when test="${backlogItem.originalEstimate == null}">
				<tr>
					<td>Original estimate</td>
					<td></td>
					<td colspan="2"><ww:textfield size="10"
						name="backlogItem.originalEstimate" /><ww:label
						value="%{getText('webwork.estimateExample')}" /></td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td>Original estimate</td>
					<td></td>
					<td><ww:label value="${backlogItem.originalEstimate}" /> <ww:hidden
						name="backlogItem.originalEstimate"
						value="${backlogItem.originalEstimate}" /></td>
				</tr>
				<tr>
					<td>Effort left</td>
					<td></td>
					<td colspan="2"><ww:textfield size="10" name="backlogItem.effortLeft" /><ww:label
						value="%{getText('webwork.estimateExample')}" /></td>
				</tr>
			</c:otherwise>
		</c:choose>

		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2"><ww:select name="backlogItem.state"
				value="backlogItem.state.name"
				list="@fi.hut.soberit.agilefant.model.State@values()"
				listKey="name" listValue="getText('task.state.' + name())" /></td>
		</tr>

		<tr>
			<td>Backlog</td>
			<td></td>
			<td colspan="2"><select name="backlogId">

				<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
				<option class="inactive" value="">(select backlog)</option>
				<c:forEach items="${productList}" var="product">
					<c:choose>
						<c:when test="${product.id == currentPageId}">
							<option selected="selected" value="${product.id}"
								title="${product.name}">${aef:out(product.name)}</option>
						</c:when>
						<c:otherwise>
							<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == currentPageId}">
								<option selected="selected" value="${project.id}"
									title="${project.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
							</c:otherwise>
						</c:choose>
						<c:forEach items="${project.iterations}" var="iteration">
							<c:choose>
								<c:when test="${iteration.id == currentPageId}">
									<option selected="selected" value="${iteration.id}"
										title="${iteration.name}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(iteration.name)}</option>
								</c:when>
								<c:otherwise>
									<option value="${iteration.id}" title="${iteration.name}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(iteration.name)}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td>Iteration goal</td>
			<td></td>
			<%-- If iteration goals doesn't exist default value is 0--%>
			<td colspan="2">
			<c:choose>
				<c:when test="${!empty iterationGoals}">
					<c:set var="goalId" value="0" scope="page" />
					<c:if test="${iterationGoalId > 0}">
						<c:set var="goalId" value="${iterationGoalId}" />
					</c:if>
					<c:if test="${!empty backlogItem.iterationGoal}">
						<c:set var="goalId" value="${backlogItem.iterationGoal.id}"
							scope="page" />
					</c:if>
					<ww:select headerKey="0" headerValue="(none)"
						name="backlogItem.iterationGoal.id" list="#attr.iterationGoals"
						listKey="id" listValue="name" value="${goalId}" />
				</c:when>
				<c:otherwise>
					(none)
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td>Priority</td>
			<td></td>
			<td colspan="2"><ww:select name="backlogItem.priority"
				value="backlogItem.priority.name"
				list="#{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}" /></td>
			<%--
		If you change something about priorities, remember to update conf/classes/messages.properties as well!
		--%>
		</tr>
		<tr>
			<td>Responsibles</td>
			<td></td>
			<td colspan="2">
			<a href="javascript:toggleDiv('userselect')">
				<img src="static/img/users.png"/>
				Assign
			</a>
			<script type="text/javascript" src="static/js/jquery-1.2.2.js"></script>
			<script type="text/javascript" src="static/js/multiselect.js"></script>
			<script type="text/javascript">
			$(document).ready( function() {
				<ww:set name="userList" value="#attr.userList" />
				<ww:set name="teamList" value="#attr.teamList" />
				var users = [<aef:userJson items="${userList}"/>]
				var teams = [<aef:teamJson items="${teamList}"/>]
				var selected = [<aef:idJson items="${backlogItem.responsibles}"/>]
				$('#userselect').multiuserselect({users: [users,[]], groups: teams}).selectusers(selected);
			});
			</script>
			<div id="userselect" style="display: none;">
				<div class="left">
					<label>Users assigned to this project</label>
					<ul class="users_0" />
					<label>Users not assigned this project</label>
					<ul class="users_1" />
				</div>
				<div class="right">
					<label>Teams</label>
					<ul class="groups" />
				</div>
			</div>
			</td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
			<c:when test="${backlogItemId == 0}">
					<td><ww:submit value="Create" />
					<ww:submit action="storeCloseBacklogItem" value="Create & Close" /></td>
			</c:when>
			<c:otherwise>
					<td><ww:submit value="Save" />
					<ww:submit action="storeCloseBacklogItem" value="Save & Close" /></td>
					<td class="deleteButton"> <ww:submit action="deleteBacklogItem"
						value="Delete" onclick="return confirmDeleteBli()" />
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>

<aef:currentUser />

<table>
	<tr>
		<td><c:if test="${backlogItem.id > 0}">
			<div id="subItems">
			<div id="subItemHeader">Tasks <ww:url id="createLink"
				action="createTask" includeParams="none">
				<ww:param name="backlogItemId" value="${backlogItemId}" />
			</ww:url> <ww:a
				href="%{createLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">Create new &raquo;</ww:a>
			</div>
			<c:if test="${!empty backlogItem.tasks}">
				<div id="subItemContent">
				<p><display:table class="listTable" name="backlogItem.tasks"
					id="row" requestURI="editBacklogItem.action">
					<display:column sortable="true" sortProperty="name" title="Name"
						class="shortNameColumn">
						<ww:url id="editLink" action="editTask" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{editLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">
						${aef:html(row.name)}
					</ww:a>
					</display:column>
					<display:column sortable="true" title="State"
						sortProperty="state.ordinal">
						<ww:text name="task.state.${row.state}" />
					</display:column>
					<display:column sortable="true" sortProperty="creator.fullName"
						title="Creator">
					${aef:html(row.creator.fullName)}
				</display:column>
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteTask" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{deleteLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}"
							onclick="return confirmDeleteTask()">Delete</ww:a>
					</display:column>
				</display:table></p>

				</div>
			</c:if> <%-- No tasks --%></div>
		</c:if> <%-- New item --%></td>
	</tr>
</table>

<%@ include file="./inc/_footer.jsp"%>