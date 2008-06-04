<%@ include file="./_taglibs.jsp"%>

<aef:hourReporting id="hourReport"></aef:hourReporting>

<c:if test="${hourReport}">
<aef:backlogHourEntrySums id="bliTotals" target="${backlog}" />
</c:if>

<script language="javascript" type="text/javascript">
function validateDeletion() {
	var conf = confirm("The selected backlog items will be gone forever. Are you sure?");
	if (conf)
		return true;
	else
		return false;
}
</script>

<ww:form action="doActionOnMultipleBacklogItems">

	<!-- Return to this backlog after submit -->
	<ww:hidden name="backlogId" value="${backlog.id}" />

	<display:table class="listTable" name="backlog.sortedBacklogItems" id="item"
		requestURI="${currentAction}.action" >

		<!-- Checkboxes for bulk-moving backlog items -->
		<display:column sortable="false" title="" class="selectColumn">
			<div><ww:checkbox name="selected" fieldValue="${item.id}" /></div>
		</display:column>

		<display:column sortable="true" sortProperty="name" title="Name"
			class="shortNameColumn">

			<ww:url id="editLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}" />
			</ww:url>
			<div><ww:a
				href="%{editLink}&contextObjectId=${backlog.id}&contextViewName=${currentAction}">
			${aef:html(item.name)}
		</ww:a></div>
		</display:column>

		<c:choose>
			<c:when test="${currentContext == 'iteration'}">
				<display:column sortable="true" sortProperty="iterationGoal.name"
				title="Iteration Goal" class="iterationGoalColumn">
				<div>${aef:html(item.iterationGoal.name)}</div>
				</display:column>
			</c:when>
			<c:otherwise>
				
			</c:otherwise>
		</c:choose>

		<display:column sortable="true" title="Responsibles" class="responsibleColumn">
		<div><aef:responsibleColumn backlogItemId="${item.id}"/></div>
		</display:column>

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${item.priority}" />
		</display:column>

		<display:column title="State" sortable="false" class="taskColumn">
			<c:set var="divId" value="${divId + 1}" scope="page" />
			<c:choose>
				<c:when test="${!(empty item.tasks)}">
				
					<a href="javascript:toggleDiv(${divId});" title="Click to expand">
					${fn:length(item.tasks)} 
						tasks, <aef:percentDone
						backlogItemId="${item.id}" />% done<br />
						
						
					<%-- Ugly solution to get both status bars displayed  
					<!-- This should be refactored at some time! 
					<!-- This is duplicated in: _backlogList.jsp 
					<!--						_dailyWorkIterations.jsp
					<!--						_dailyWorkProjects.jsp  
					<!--						_workInProgress.jsp
					<!--						editIterationGoal.jsp
					<!--  Note: the css div on the images are used to force the bars to be displayed nicely
					<!--  since it has been an persistent problem in the past,
					<!--  which was unable to correct from the source. --%>						
						<c:choose>
							<c:when test="${item.state == 'NOT_STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="started" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'PENDING'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="pending" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'BLOCKED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="blocked" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'IMPLEMENTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="implemented" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'DONE'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="done" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							</c:choose>
							
					
					
					
				
						<aef:stateList backlogItemId="${item.id}" id="tsl" /> 
						<ww:url
							id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="${tsl['notStarted']}" />
								<ww:param name="started" value="${tsl['started']}" />
								<ww:param name="pending" value="${tsl['pending']}" />
								<ww:param name="blocked" value="${tsl['blocked']}" />
								<ww:param name="implemented" value="${tsl['implemented']}" />
								<ww:param name="done" value="${tsl['done']}" />
						</ww:url> 
						<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
					</a>
					
					<aef:tasklist backlogItem="${item}"
						contextViewName="${currentAction}" contextObjectId="${backlog.id}"
						divId="${divId}"/>
						
				</c:when>
			
			 	<c:otherwise>
					
						 <a href="javascript:toggleDiv(${divId});" title="Click to expand">
							<ww:text name="backlogItem.state.${item.state}"/><br />
							
							<c:choose>
							<c:when test="${item.state == 'NOT_STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div> 
							</c:when>
							<c:when test="${item.state == 'STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="started" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'PENDING'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="pending" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'BLOCKED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="blocked" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'IMPLEMENTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="implemented" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							<c:when test="${item.state == 'DONE'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="done" value="1" /> </ww:url> 
								<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
							</c:when>
							</c:choose>
								
						</a>
						<aef:tasklist backlogItem="${item}"
							contextViewName="${currentAction}" contextObjectId="${backlog.id}"
							divId="${divId}" />
				  </c:otherwise> 
				
			</c:choose>
			
		</display:column>

		

		

		<display:column sortable="true" sortProperty="effortLeft" defaultorder="descending"
			title="Effort Left<br/>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.effortLeft == null}">&mdash;</c:when>
					<c:otherwise>${item.effortLeft}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

		<display:column sortable="true" sortProperty="originalEstimate" defaultorder="descending"
				title="Original Estimate<br/>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.originalEstimate == null}">&mdash;</c:when>
					<c:otherwise>${item.originalEstimate}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>


		<c:choose>
			<c:when test="${hourReport}">
				<display:column sortable="false" sortProperty="timeSpent" defaultorder="descending" title="Effort Spent">
					<span style="white-space: nowrap">
						<c:choose>
							<c:when test="${bliTotals[item.id] == null}">&mdash;</c:when>
							<c:otherwise>
							<c:out value="${bliTotals[item.id]}" />
							</c:otherwise>
						</c:choose>
					</span>
				</display:column>
			</c:when>
			<c:otherwise>
			
			</c:otherwise>
		</c:choose>
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
					<c:out value="${aef:totalBacklogHourEntries(bliTotals)}" />
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
			list="#{'-1':'Keep original', '0':'Not started', '1':'Started', '2':'Pending', '3':'Blocked', '4':'Implemented', '5':'Done' }" />
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
				list="#{'-1':'Keep original', '5':'undefined', '4':'+++++', '3':'++++', '2':'+++', '1':'++', '0':'+'}" />
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
		<td><ww:submit type="button" label="Store" name="itemAction" value="%{'ChangeSelected'}" /></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td><ww:submit type="button" name="itemAction" value="%{'DeleteSelected'}"
				onclick="return validateDeletion()" label="Delete selected" /></td>
	</tr>
	</table>
</ww:form>
