<%@ include file="../inc/_taglibs.jsp"%>

<aef:userList />
<aef:teamList />
<aef:currentUser />
<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}" />
<aef:productList />

<ww:form action="storeNewBacklogItem" method="post">
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
				cssClass="useWysiwyg" id="createBacklogItemDescription"
				name="backlogItem.description" /></td>
		</tr>

		<tr>
			<td>Original estimate</td>
			<td></td>
			<td colspan="2">
                      <ww:textfield size="10" name="backlogItem.originalEstimate"
                          id="createBLI_originalEstimateField" />
                      <ww:label value="%{getText('webwork.estimateExample')}" />
                  </td>
		</tr>

		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2">
			<ww:select
				name="backlogItem.state" id="stateSelect"
				value="backlogItem.state.name"
				list="@fi.hut.soberit.agilefant.model.State@values()" listKey="name"
				listValue="getText('task.state.' + name())"
				onchange="disableElementIfValue(this, '#createBLI_originalEstimateField', 'DONE');"/></td>
		</tr>

		<tr>
			<td>Backlog</td>
			<td></td>
			<td colspan="2">
			
			<select name="backlogId" id="createBLIBacklogId" onchange="getIterationGoals(this.value, '#createBLIIterGoalSelect')">
				<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
				<option class="inactive" value="">(select backlog)</option>
				<c:forEach items="${productList}" var="product">
					<c:choose>
						<c:when test="${product.id == currentPageId}">
							<option selected="selected" value="${product.id}"
								class="productOption"><c:out value="${product.name}" /></option>
						</c:when>
						<c:otherwise>
							<option value="${product.id}" title="${product.name}"
								class="productOption"><c:out value="${product.name}" /></option>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == currentPageId}">
								<option selected="selected" value="${project.id}"
									class="projectOption"><c:out value="${project.name}" /></option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}"
									class="projectOption"><c:out value="${project.name}" /></option>
							</c:otherwise>
						</c:choose>
						<c:forEach items="${project.iterations}" var="iteration">
							<c:choose>
								<c:when test="${iteration.id == currentPageId}">
									<option selected="selected" value="${iteration.id}"
										class="iterationOption"><c:out value="${iteration.name}" /></option>
								</c:when>
								<c:otherwise>
									<option value="${iteration.id}" title="${iteration.name}"
										class="iterationOption"><c:out value="${iteration.name}" /></option>
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
			<select name="backlogItem.iterationGoal.id" id="createBLIIterGoalSelect">
			</select>
			<span>(none)</span></td>
		</tr>
		<tr>
			<td>Priority</td>
			<td></td>
			<td colspan="2"><ww:select name="backlogItem.priority"
				value="backlogItem.priority.name"
				list="#@java.util.LinkedHashMap@{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}" /></td>
			<%--
        If you change something about priorities, remember to update conf/classes/messages.properties as well!
        --%>
		</tr>
		<tr>
			<td>Responsibles</td>
			<td></td>
			<td colspan="2">

			<div id="assigneesLink"><a
				href="javascript:toggleDiv('createBLIuserselect')" class="assignees"> <img
				src="static/img/users.png" /> <c:set var="listSize"
				value="${fn:length(backlogItem.responsibles)}" scope="page" /> <c:choose>
				<c:when test="${listSize > 0}">
					<c:set var="count" value="0" scope="page" />
					<c:set var="comma" value="," scope="page" />
					<c:forEach items="${backlogItem.responsibles}" var="responsible">
						<c:set var="unassigned" value="0" scope="page" />
						<c:if test="${count == listSize - 1}">
							<c:set var="comma" value="" scope="page" />
						</c:if>
						<c:if test="${!empty backlogItem.project}">
							<c:set var="unassigned" value="1" scope="page" />
							<c:forEach items="${backlogItem.project.responsibles}"
								var="projectResponsible">
								<c:if test="${responsible.id == projectResponsible.id}">
									<c:set var="unassigned" value="0" scope="page" />
								</c:if>
							</c:forEach>
						</c:if>
						<c:choose>
							<c:when test="${unassigned == 1}">
								<span><c:out value="${responsible.initials}" /></span>
								<c:out value="${comma}" />
							</c:when>
							<c:otherwise>
								<c:out value="${responsible.initials}" />
								<c:out value="${comma}" />
							</c:otherwise>
						</c:choose>
						<c:set var="count" value="${count + 1}" scope="page" />
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="none" />
				</c:otherwise>
			</c:choose> </a></div>

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
                $('#createBLIuserselect').multiuserselect({users: [preferred,others], groups: teams, root: $('#createBLIuserselect')}).selectusers(selected);
                
            });
            </script>
			<div id="createBLIuserselect" class="userselect" style="display: none;">
			<div class="left"><c:if
				test="${!aef:isProduct(backlog) &&
                         backlog != null}">
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
            <td><ww:submit value="Create" id="createButton" /></td>
            <td class="deleteButton"><ww:reset value="Cancel"
                cssClass="closeDialogButton" /></td>
        </tr>
	</table>

</ww:form>