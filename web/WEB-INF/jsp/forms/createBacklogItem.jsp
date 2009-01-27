<%@ include file="../inc/_taglibs.jsp"%>

<aef:userList />
<aef:teamList />
<aef:currentUser />
<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}" />
<aef:productList />

<script type="text/javascript">

$(document).ready(function() {
    $('#themeChooserLink-createBLI').themeChooser({
        backlogItemId: ${backlogItemId},
        backlogId: '#createBLIBacklogId',
        themeListContainer: '#themeListContainer-createBLI'
    });
    $('#userChooserLink-createBLI').userChooser({
        backlogItemId: ${backlogItemId},
        backlogIdField: '#createBLIBacklogId',
        userListContainer: '#userListContainer-createBLI'
    });
});

</script>

<div class="validateWrapper validateNewBacklogItem">
<ww:form action="storeNewBacklogItem" method="post">
<ww:hidden name="parentId" value="${parentId}" />
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
			
			<select name="backlogId" id="createBLIBacklogId" onchange="getIterationGoals(this.value, '#createBLIIterGoalSelect');">
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

			<div>
                <a id="userChooserLink-createBLI" href="#" class="assigneeLink">
                    <img src="static/img/users.png"/>
                    <span id="userListContainer-createBLI">
                    (none)
                    </span>
                </a>
            </div>
			</td>
		</tr>
		<tr>
			<td>Themes</td>
			<td></td>
			<td colspan="2">

			<div>
                <a id="themeChooserLink-createBLI" href="#" class="assigneeLink">
                    <img src="static/img/theme.png"/>
                    <span id="themeListContainer-createBLI">
                    (none)
                    </span>
                </a>
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
</div>