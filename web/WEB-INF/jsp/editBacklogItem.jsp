<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<script type="text/javascript" src="static/js/generic.js"></script>

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
					<td colspan="2">
					<c:choose>
						<c:when test="${backlogItem.state.name != 'DONE'}">
							<ww:textfield size="10"
							name="backlogItem.originalEstimate"
							id="originalEstimateField" />
						</c:when>
						<c:otherwise>
							<ww:textfield size="10"
							name="backlogItem.originalEstimate"
							disabled="true"
							id="originalEstimateField" />
						</c:otherwise>
					</c:choose>
					<ww:label value="%{getText('webwork.estimateExample')}" /></td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td>Original estimate</td>
					<td></td>
					<td colspan="2"><ww:label
						value="${backlogItem.originalEstimate}" /> <ww:hidden
						name="backlogItem.originalEstimate"
						value="${backlogItem.originalEstimate}" /> <ww:url id="resetLink"
						action="resetBliOrigEstAndEffortLeft" includeParams="none">
						<ww:param name="backlogItemId" value="${backlogItem.id}" />
					</ww:url>
					<c:choose>
						<c:when test="${backlogItem.state.name == 'DONE'}">
							<span id="resetText" style="color: #666;">(reset)</span>
							<span id="resetLink" style="display: none;">
						</c:when>
						<c:otherwise>
						<span id="resetText" style="color: #666; display: none;">(reset)</span>
							<span id="resetLink">
						</c:otherwise>
					</c:choose>
					
					<ww:a
							href="%{resetLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}"
							onclick="return confirmReset()">(reset)</ww:a>
					</span>
					
					
					</td>
				</tr>
				<tr>
					<td>Effort left</td>
					<td></td>
					<td colspan="2">
					<c:choose>
						<c:when test="${backlogItem.state.name != 'DONE'}">
							<ww:textfield size="10"
							name="backlogItem.effortLeft"
							id="effortLeftField" />
						</c:when>
						<c:otherwise>
							<ww:textfield size="10"
							name="backlogItem.effortLeft"
							disabled="true"
							id="effortLeftField" />
						</c:otherwise>
					</c:choose>
					<ww:label value="%{getText('webwork.estimateExample')}" />
					</td>
				</tr>
			</c:otherwise>
		</c:choose>

		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2">
			<script type="text/javascript">
			function change_estimate_enabled(value) {
				var effLeftField = document.getElementById('effortLeftField');
				var origEstField = document.getElementById('originalEstimateField');
				var resetLink = document.getElementById('resetLink');
				var resetText = document.getElementById('resetText');
				if (value == 'DONE') {
					if (effLeftField != null) {
						effLeftField.disabled = true;
					}
					if (origEstField != null) {
						origEstField.disabled = true;
					}
					if (resetLink != null) {
						resetLink.style.display = "none";
					}
					if (resetText != null) {
						resetText.style.display = "";
					}
				}
				else {
					if (effLeftField != null) {
						effLeftField.disabled = false;
					}
					if (origEstField != null) {
						origEstField.disabled = false;
					}
					if (resetLink != null) {
						resetLink.style.display = "";
					}
					if (resetText != null) {
						resetText.style.display = "none";
					}
				}
			}
			</script> 
			<ww:select name="backlogItem.state"
				value="backlogItem.state.name"
				list="@fi.hut.soberit.agilefant.model.State@values()" listKey="name"
				listValue="getText('task.state.' + name())" 
				onchange="change_estimate_enabled(this.value);"/></td>
		</tr>

		<tr>
			<td>Backlog</td>
			<td></td>
			<td colspan="2">
			<c:choose>
				<c:when test="${backlogItemId == 0}">
					<select name="backlogId" 
									onchange="disableIfEmpty(this.value, ['createButton', 'createAndCloseButton']);">
				</c:when>
				<c:otherwise>
					<select name="backlogId" onchange="disableIfEmpty(this.value, ['saveButton', 'saveAndCloseButton']);">
				</c:otherwise>
			</c:choose>

				<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
				<option class="inactive" value="">(select backlog)</option>
				<c:forEach items="${productList}" var="product">
					<c:choose>
						<c:when test="${product.id == currentPageId}">
							<option selected="selected" value="${product.id}" class="productOption"
								title="${product.name}">${aef:out(product.name)}</option>
						</c:when>
						<c:otherwise>
							<option value="${product.id}" title="${product.name}" class="productOption">${aef:out(product.name)}</option>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == currentPageId}">
								<option selected="selected" value="${project.id}" class="projectOption"
									title="${project.name}">${aef:out(project.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}"  class="projectOption">${aef:out(project.name)}</option>
							</c:otherwise>
						</c:choose>
						<c:forEach items="${project.iterations}" var="iteration">
							<c:choose>
								<c:when test="${iteration.id == currentPageId}">
									<option selected="selected" value="${iteration.id}" class="iterationOption"
										title="${iteration.name}">${aef:out(iteration.name)}</option>
								</c:when>
								<c:otherwise>
									<option value="${iteration.id}" title="${iteration.name}"  class="iterationOption">${aef:out(iteration.name)}</option>
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
			<td colspan="2"><c:choose>
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
			</c:choose></td>
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

			<div id="assigneesLink">
			<a href="javascript:toggleDiv('userselect')" class="assignees">
			<img src="static/img/users.png"/>
			<c:set var="listSize" value="${fn:length(backlogItem.responsibles)}" scope="page" />
			<c:choose>
			<c:when test="${listSize > 0}">
				<c:set var="count" value="0" scope="page" />
				<c:set var="comma" value="," scope="page" />
				<c:forEach items="${backlogItem.responsibles}" var="responsible">
					<c:set var="unassigned" value="0" scope="page" />
					<c:if test="${count == listSize - 1}" >
						<c:set var="comma" value="" scope="page" />
					</c:if>
					<c:if test="${!empty backlogItem.project}" >
						<c:set var="unassigned" value="1" scope="page" />
						<c:forEach items="${backlogItem.project.responsibles}" var="projectResponsible">
							<c:if test="${responsible.id == projectResponsible.id}" >
								<c:set var="unassigned" value="0" scope="page" />
							</c:if>
						</c:forEach>
					</c:if>
					<c:choose>
						<c:when test="${unassigned == 1}">
							<span><c:out value="${responsible.initials}" /></span><c:out value="${comma}" />
						</c:when>
						<c:otherwise>
							<c:out value="${responsible.initials}" /><c:out value="${comma}" />
						</c:otherwise>
					</c:choose>
					<c:set var="count" value="${count + 1}" scope="page" />
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:out value="none" />
			</c:otherwise>
			</c:choose>
			</a>
			</div>

			<script type="text/javascript">
			$(document).ready( function() {
				<ww:set name="userList" value="${possibleResponsibles}" />
				<ww:set name="teamList" value="#attr.teamList" />
				<c:choose>
				<c:when test="${backlogItem.project != null}">
				var others = [<aef:userJson items="${aef:listSubstract(possibleResponsibles, backlogItem.project.responsibles)}"/>];
				var preferred = [<aef:userJson items="${backlogItem.project.responsibles}"/>];
				</c:when>
				<c:otherwise>
				var others = [<aef:userJson items="${possibleResponsibles}"/>];
				var preferred = [];
				</c:otherwise>
				</c:choose>
				
				var teams = [<aef:teamJson items="${teamList}"/>];
				var selected = [<aef:idJson items="${backlogItem.responsibles}"/>]
				$('#userselect').multiuserselect({users: [preferred,others], groups: teams, root: $('#userselect')}).selectusers(selected);
				
				// Task ranking
				$('.moveUp').click(function() {
						var me = $(this);
						$.get(me.attr('href'), null, function() {me.moveup();});
						return false;
						});
				$('.moveDown').click(function() {
						var me = $(this);
						$.get(me.attr('href'), null, function() {me.movedown();});
						return false;
						});
				$('.moveTop').click(function() {
						var me = $(this);
						$.get(me.attr('href'), null, function() {me.movetop();});
						return false;
						});
				$('.moveBottom').click(function() {
						var me = $(this);
						$.get(me.attr('href'), null, function() {me.movebottom();});
						return false;
						});
				
			});
			</script>
			<div id="userselect" style="display: none;">
			<div class="left">
			<c:if test="${!aef:isProduct(backlog)}">
				<label>Users assigned to this project</label>
					<ul class="users_0"></ul>
				<label>Users not assigned this project</label>
			</c:if>
				<ul class="users_1"></ul>
			</div>
			<div class="right"><label>Teams</label>
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
					<td><ww:submit value="Create" id="createButton" /> <ww:submit
						action="storeCloseBacklogItem" value="Create & Close" id="createAndCloseButton" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" id="saveButton" /> <ww:submit
						action="storeCloseBacklogItem" value="Save & Close" id="saveAndCloseButton" /></td>
					<td class="deleteButton"><ww:submit action="deleteBacklogItem"
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
			<div id="subItemHeader">
			<table cellpadding="0" cellspacing="0">
                    <tr>
                       <td class="header">Tasks <ww:url id="createLink"
				action="createTask" includeParams="none">
				<ww:param name="backlogItemId" value="${backlogItemId}" />
			</ww:url> <ww:a
				href="%{createLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
			</div>
			<c:if test="${!empty backlogItem.tasks}">
				<div id="subItemContent">
				
				<ww:form action="quickStoreTaskList">
				<ww:hidden name="backlogItemId" value="${backlogItemId}" />
				<ww:hidden name="contextViewName" value="editBacklogItem" />
				<ww:hidden name="contextObjectId" value="${backlogItemId}" />
				<ww:hidden name="effortLeft" value="${backlogItem.effortLeft}" />
				<ww:hidden name="state" value="${backlogItem.state.name}" />
				<p><display:table class="listTable" name="backlogItem.tasks"
					id="row" requestURI="editBacklogItem.action">
					<display:column sortable="false" title="Name"
						class="shortNameColumn">
						<ww:url id="editLink" action="editTask" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{editLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">
						${aef:html(row.name)}
					</ww:a>
					</display:column>
					<display:column sortable="false" title="State">
						<ww:select name="taskStates[${row.id}]" value="#attr.row.state.name"
							list="@fi.hut.soberit.agilefant.model.State@values()" listKey="name"
							listValue="getText('task.state.' + name())" />
					</display:column>
					<display:column sortable="false" title="Creator">
					${aef:html(row.creator.fullName)}
				</display:column>
					<display:column sortable="false" title="Actions">

						<ww:url id="moveTaskTopLink" action="moveTaskTop" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a cssClass="moveTop" href="%{moveTaskTopLink}">
							<img src="static/img/arrow_top.png" alt="Send to top"
								title="Send to top" />
						</ww:a>

						<ww:url id="moveTaskUpLink" action="moveTaskUp" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a cssClass="moveUp" href="%{moveTaskUpLink}">
							<img src="static/img/arrow_up.png" alt="Move up" title="Move up" />
						</ww:a>

						<ww:url id="moveTaskDownLink" action="moveTaskDown" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a cssClass="moveDown" href="%{moveTaskDownLink}">
							<img src="static/img/arrow_down.png" alt="Move down"
								title="Move down" />
						</ww:a>

						<ww:url id="moveTaskBottomLink" action="moveTaskBottom" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a cssClass="moveBottom" href="%{moveTaskBottomLink}">
							<img src="static/img/arrow_bottom.png" alt="Send to bottom"
								title="Send to bottom" />
						</ww:a>

						<ww:url id="deleteLink" action="deleteTask" includeParams="none">
							<ww:param name="taskId" value="${row.id}" />
						</ww:url>
						<ww:a 
							href="%{deleteLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}"
							onclick="return confirmDeleteTask()">
							<img src="static/img/delete.png" alt="Delete" title="Delete" />
						</ww:a>


					</display:column>


				</display:table></p>
				<ww:submit value="Save states"/>
				</ww:form>
				</div>
			</c:if> <%-- No tasks --%></div>
		</c:if> <%-- New item --%></td>
	</tr>
</table>


<%-- Hour reporting here - Remember to expel David H. --%>
<aef:hourReporting id="hourReport"></aef:hourReporting>
<c:if test="${hourReport == 'true'}">

<aef:hourEntries id="hourEntries" target="${backlogItem}"></aef:hourEntries>


<%-- 
<c:forEach items="${hourEntries}" var="hentry">
	humppaa!<br>
	 <c:out value="${hentry.targetType}" /> 
</c:forEach>
--%>

<div class="subItems" style="margin-left: 3px;">
	<div class="subItemHeader" style="padding: 3px !important;">
     	Hour reporting entries
        <ww:url id="createLink" action="createHourEntry" includeParams="none">
			<ww:param name="backlogItemId" value="${backlogItem.id}" />
		</ww:url>
		<ww:a cssClass="openModalWindow" href="%{createLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">Create new &raquo;</ww:a>	
	</div>						
	<c:if test="${!empty hourEntries}">
		<div id="subItemContent">		
			<p>
				<display:table name="${hourEntries}" id="row" defaultsort="1" requestURI="editBacklogItem.action">
					<display:column sortable="false" title="Date">
						${aef:html(row.date.date)}.${aef:html(row.date.month + 1)}.${aef:html(row.date.year + 1900 )} ${aef:html(row.date.hours)}:${aef:html(row.date.minutes)}
					</display:column>
					
					<display:column sortable="true" title="User">
						${aef:html(row.user.fullName)}
					</display:column>
					
					<display:column sortable="false" title="Spent effort">
						${aef:html(row.timeSpent)}
					</display:column>
					
					<display:column sortable="false" title="Comment" property="description" />

					<%-- Not implemented yet --%>
						<display:column sortable="false" title="Action">
							<ww:url id="editLink" action="editHourEntry" includeParams="none">
								<ww:param name="backlogItemId" value="${backlogItem.id}" />
								<ww:param name="hourEntryId" value="${row.id}" />
							</ww:url>	
							<ww:url id="deleteLink" action="deleteHourEntry" includeParams="none">
								<ww:param name="backlogItemId" value="${backlogItem.id}" />
								<ww:param name="hourEntryId" value="${row.id}" />
							</ww:url>			
							<ww:a cssClass="openModalWindow" href="%{editLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">
								<img src="static/img/edit.png" alt="Edit" title="Edit" />
							</ww:a>
							<ww:a href="%{deleteLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}" onclick="return confirmDeleteHour()">
								<img src="static/img/delete.png" alt="Delete" title="Delete" />
							</ww:a>
						</display:column>
					</display:table>
				</p>
			</div>
		</c:if> <%-- No tasks --%>
	</div>
<aef:modalAjaxWindow/>
</c:if> <%-- Hour reporting on --%>

<%@ include file="./inc/_footer.jsp"%>