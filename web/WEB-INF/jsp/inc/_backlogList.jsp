<%@ include file="./_taglibs.jsp"%>

<aef:hourReporting id="hourReport" />

<c:if test="${hourReport}">
	<c:set var="totalSum" value="${null}" />
</c:if>

<aef:openDialogs context="bli" id="openBacklogItemTabs" />

<!-- context variable for backlog item ajax to know its context -->
<c:set var="bliListContext" value="backlogList" scope="session" />

<c:set var="dialogContext" value="bli" scope="session" />

<aef:currentUser />
<script language="javascript" type="text/javascript">

function validateDeletion() {
    return confirm("The selected backlog items will be gone forever. Are you sure?");
}

function selectAllBLIs(val) {
	var elems = document.getElementsByName("selected");
	for(var x in elems)
		elems[x].checked = val;
}

$(document).ready(function() {        
    <c:forEach items="${openBacklogItemTabs}" var="openBacklogItem">
        handleTabEvent("backlogItemTabContainer-${openBacklogItem[0]}-${bliListContext}", "bli", ${openBacklogItem[0]}, ${openBacklogItem[1]}, '${bliListContext}');
    </c:forEach>
});

</script>
<ww:form action="doActionOnMultipleBacklogItems">

	<!-- Return to this backlog after submit -->
	<ww:hidden name="backlogId" value="${backlog.id}" />

	<display:table class="listTable" name="backlog.sortedBacklogItems" id="row"
		requestURI="${currentAction}.action" >

		<!-- Checkboxes for bulk-moving backlog items -->
		<display:column sortable="false" title="<input type='checkbox' name='selectall' onclick='selectAllBLIs(this.checked)'/>" class="selectColumn">
			<div><ww:checkbox name="selected" fieldValue="${row.id}" /></div>
			<div style="height: 15px;"></div>
			<div id="backlogItemTabContainer-${row.id}-${bliListContext}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 15px;"></div>
		</display:column>
		
		<!-- Make the columns fit in the iteration-page. -->
		<c:choose>
			<c:when test="${currentContext == 'iteration'}">
				<c:set var="nameClass" value="shortNameColumn" />
			</c:when>
			<c:otherwise>
				<c:set var="nameClass" value="nameColumn" />
			</c:otherwise>
		</c:choose>
				
		<display:column sortable="true" sortProperty="name" title="Name" class="${nameClass}">												
			<div id="bli_${row.id}">
            <c:forEach items="${bliThemeCache[row.id]}" var="businessTheme">
            	<a href="#" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bli',${row.id},2, '${bliListContext}'); return false;">
            		<span class="businessTheme" title="${businessTheme.description}"><c:out value="${businessTheme.name}"/></span>
            	</a>
            </c:forEach>
			<a class="nameLink" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bli',${row.id},0, '${bliListContext}'); return false;">
				${aef:html(row.name)}
			</a>			
			</div>						
		</display:column>

		<c:choose>
			<c:when test="${currentContext == 'iteration'}">
				<display:column sortable="true" sortProperty="iterationGoal.name"
				title="Iteration Goal" class="iterationGoalColumn">
				<div>${aef:html(row.iterationGoal.name)}</div>
				</display:column>
			</c:when>
			<c:otherwise>
				
			</c:otherwise>
		</c:choose>

		<display:column sortable="true" title="Responsibles" class="responsibleColumn">
		<div><aef:responsibleColumn backlogItemId="${row.id}"/></div>
		</display:column>

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${row.priority}" />
		</display:column>

		<display:column title="Progress" sortable="false" class="taskColumn">
			<aef:backlogItemProgressBar backlogItem="${row}" bliListContext="${bliListContext}" dialogContext="${dialogContext}" hasLink="${true}"/>			
		</display:column>
			
		<display:column sortable="true" sortProperty="effortLeft" defaultorder="descending"
			title="Effort Left<br/>" class="effortLeftColumn">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${row.effortLeft == null}">&mdash;</c:when>
					<c:otherwise>${row.effortLeft}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

		<display:column sortable="true" sortProperty="originalEstimate" defaultorder="descending"
				title="Original Estimate<br/>" class="originalEstimateColumn">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${row.originalEstimate == null}">&mdash;</c:when>
					<c:otherwise>${row.originalEstimate}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>


		<c:choose>
			<c:when test="${hourReport}">
			
				<display:column sortable="true" sortProperty="effortSpent" defaultorder="descending" title="Effort Spent" class="effortSpentColumn">
					<span style="white-space: nowrap">
						<c:choose>
							<c:when test="${row.effortSpent == null}">&mdash;</c:when>
							<c:otherwise>
								<c:out value="${row.effortSpent}" />
								<%-- Interesting feature of jsp, thus circumvent direct assignation --%>
								<c:set var="es" value="${row.effortSpent}"/>
								<c:set var="totalSum" value="${aef:calculateAFTimeSum(totalSum, es)}" />
								
							</c:otherwise>
						</c:choose>
					</span>
				</display:column>
				
			</c:when>
			<c:otherwise>
			
			</c:otherwise>
		</c:choose>
		
		<display:column title="Actions" sortable="false">
			<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bli',${row.id},0, '${bliListContext}'); return false;" />
			<img src="static/img/delete_18.png" alt="Delete" title="Delete" style="cursor: pointer;" onclick="deleteBacklogItem(${row.id}); return false;" />
		</display:column>
		
		<display:footer>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<c:if test="${currentContext == 'iteration'}">
					<td>&nbsp;</td>
				</c:if>
				<%-- Effort left --%>
				<%--
				<td><c:out value="${backlog.bliEffortLeftSum}" /></td>
				--%>
				<td><c:out value="${effortLeftSum}" /></td>
				<%-- Original estimate --%>
				<td><c:out value="${originalEstimateSum}" /></td>
				<c:if test="${hourReport}">
					<td>
						<c:choose>
							<c:when test="${totalSum != null}">
								<c:out value="${totalSum}" />
							</c:when>
							<c:otherwise>
								0h
							</c:otherwise>
						</c:choose>
					</td>
				</c:if>
			</tr>
		</display:footer>
	</display:table>

	<aef:productList />

	<table class="formTable">
	<tr>
		<td>State</td>
		<td><ww:select name="targetState"
			list="#@java.util.LinkedHashMap@{'-1':'Keep original', '0':'Not started', '1':'Started', '2':'Pending', '3':'Blocked', '4':'Implemented', '5':'Done' }" />
		</td>
	</tr>
	<tr>
		<td>Move to</td>
		<td class="targetBacklogDropdownColumn">
			<aef:backlogDropdown selectName="targetBacklog"
				preselectedBacklogId="${backlog.id}" backlogs="${productList}" />
		</td>
	</tr>
	<tr>
		<c:if test="${aef:isIteration(backlog)}">
		<td>Iteration goal</td>
		<td class="targetBacklogDropdownColumn">
			<select name="targetIterationGoalId">
				<option value="-1">Keep original</option>
				<option value="-2">(none)</option>
				<c:forEach items="${backlog.iterationGoals}" var="itergoal">
					<option value="${itergoal.id}" ><c:out value="${itergoal.name}" /></option>
				</c:forEach>
			</select>
		</td>
		</c:if>
	</tr>
	<tr>
		<td>Priority</td>
		<td class="targetPriorityDropdown">
			<ww:select name="targetPriority"
				list="#@java.util.LinkedHashMap@{'-1':'Keep original', '5':'undefined', '4':'+++++', '3':'++++', '2':'+++', '1':'++', '0':'+'}" />
		</td>
	</tr>
	<tr>
			<td>Responsibles</td>
			<aef:userList />
			<aef:teamList />
			<aef:enabledUserList />
			<td colspan="4">
			<a href="javascript:toggleDiv('multiplebli_userselect');">
				<img src="static/img/users.png"/>
				Assign
			</a>
			<script type="text/javascript">
			$(document).ready( function() {
				<ww:set name="userList" value="#attr.userList" />
				<ww:set name="enabledUserList" value="#attr.enabledUserList" />
				<ww:set name="teamList" value="#attr.teamList" />
				<c:choose>
				<c:when test="${aef:isProduct(backlog)}">
				var others = [<aef:userJson items="${enabledUserList}"/>];
				var preferred = [];
				</c:when>
				<c:when test="${aef:isProject(backlog)}">
				var others = [<aef:userJson items="${aef:listSubstract(enabledUserList, backlog.responsibles)}"/>];
				var preferred = [<aef:userJson items="${backlog.responsibles}"/>];
				</c:when>
				<c:when test="${aef:isIteration(backlog)}">
				var others = [<aef:userJson items="${aef:listSubstract(enabledUserList, backlog.project.responsibles)}"/>];
				var preferred = [<aef:userJson items="${backlog.project.responsibles}"/>];
				</c:when>
				</c:choose>
				
				var teams = [<aef:teamJson items="${teamList}"/>];
				var selected = [];
				$('#multiplebli_userselect').multiuserselect({users: [preferred,others], groups: teams, root: $('#multiplebli_userselect')}).selectusers(selected);
				
				$('#multiplebli_userselect').toggle_disabled(true);
				$('#keepOriginalResponsibles').bind("change", function() {
					$('#multiplebli_userselect').toggle_disabled($(this).attr('checked') == true);
				});
			});
			</script>
			
			<div id="multiplebli_userselect" style="display: none;">
				<ww:checkbox name="keepResponsibles" value="true" title="Keep original"
					id="keepOriginalResponsibles" fieldValue="1"/>
					Keep original
					
				<div class="left">
				<c:if test="${!aef:isProduct(backlog)}">
					<label>Users assigned to this project</label>
						<ul class="users_0"></ul>
					<label>Users not assigned this project</label>
				</c:if>
						<ul class="users_1"></ul>
				</div>
				<div class="right">
					<label>Teams</label>
					<ul class="groups" />
				</div>
			</div>
			</td>
		</tr>
	<tr>
		<td><ww:submit type="button" label="Save" name="itemAction" value="%{'ChangeSelected'}" /></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td><ww:submit type="button" name="itemAction" value="%{'DeleteSelected'}"
				onclick="return validateDeletion()" label="Delete selected" /></td>
	</tr>
	</table>
</ww:form>
