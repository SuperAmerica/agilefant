<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:hourReporting id="hourReport"></aef:hourReporting>

<c:if test="${hourReport}">
	<c:set var="totalSum" value="${null}" />
</c:if>

<c:if test="${iterationGoal.id > 0}">
	<aef:bct iterationGoalId="${iterationGoal.id}" />
</c:if>
<c:if test="${iterationGoal.id == 0}">
	<aef:bct iterationId="${iterationId}" />
</c:if>

<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<aef:productList />
<c:if test="${hourReport}">
	<aef:modalAjaxWindow />
</c:if>
<ww:actionerror />
<ww:actionmessage />

<c:choose>
	<c:when test="${iterationGoalId == 0}">
		<h2>Create iteration goal</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit iteration goal</h2>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${iterationGoalId == 0}">
		<c:set var="new" value="New" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="new" value="" scope="page" />
	</c:otherwise>
</c:choose>

<ww:form action="store${new}IterationGoal"  method="post">
	<ww:hidden name="iterationGoalId" value="${iterationGoal.id}" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60"
				name="iterationGoal.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10" cssClass="useWysiwyg" 
				name="iterationGoal.description" /></td>
		</tr>
		<tr>
			<td>Iteration</td>
			<td></td>
			<td colspan="2">
			<c:choose>
				<c:when test="${iterationGoalId == 0}">
					<select name="iterationId" onchange="disableIfEmpty(this.value, ['createButton', 'createAndCloseButton']);">
				</c:when>
				<c:otherwise>
					<select name="iterationId" onchange="disableIfEmpty(this.value, ['saveButton', 'saveAndCloseButton']);">
				</c:otherwise>
			</c:choose>
				<option value="" class="inactive">(select iteration)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive productOption">${product.name}</option>
					<c:forEach items="${product.projects}" var="project">
						<option value="" class="inactive projectOption">${project.name}</option>
						<c:forEach items="${project.iterations}" var="iter">
							<c:choose>
								<c:when test="${iter.id == currentIterationId}">
									<option selected="selected" value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${iterationGoalId == 0}">
					<td><ww:submit value="Create" id="createButton" /> <ww:submit
						action="storeCloseIterationGoal" value="Create & Close" id="createAndCloseButton"/></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" id="saveButton"/> <ww:submit
						action="storeCloseIterationGoal" value="Save & Close" id="saveAndCloseButton"/></td>
					<td class="deleteButton"><ww:submit
						onclick="return confirmDelete()" action="deleteIterationGoal"
						value="Delete" /></td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>

<table>
	<tr>
		<td><c:if test="${iterationGoalId != 0}">
			<div id="subItems">
			<div id="subItemHeader">
			<table cellspacing="0" cellpadding="0">
                <tr>
                <td class="header">Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}" />
				<ww:param name="iterationGoalId" value="${iterationGoal.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
			</div>
			<c:if test="${!empty iterationGoal.backlogItems}">
				<div id="subItemContent">
				<p>
				
				<%--create table of backlogItems--%>
				<script language="javascript" type="text/javascript">
					function validateDeletion() {
						var conf = confirm("The selected backlog items will be gone forever. Are you sure?");
						if (conf)
							return true;
						else
							return false;
					}
				</script>
				
				
				<%--TODO: Refactor this to inc/_backlogList.jsp, this is ugly--%>
				<ww:form action="doActionOnMultipleBacklogItems">
				
				<!-- Return to this backlog after submit -->
				<ww:hidden name="backlogId" value="${iteration.id}" />
				<ww:hidden name="iterationGoalId" value="${iterationGoal.id}" />
							
				
				<display:table class="listTable"
					name="iterationGoal.backlogItems" id="row"
					requestURI="editIterationGoal.action" defaultsort="4"
					defaultorder="descending">

					<!-- Checkboxes for bulk-moving backlog items -->	
					<display:column sortable="false" title="" class="selectColumn">
						<div><ww:checkbox name="selected" fieldValue="${row.id}" /></div>
					</display:column>

					<display:column sortable="true" sortProperty="name" title="Name"
						class="shortNameColumn">
						<ww:a href="#" id="${row.id}" onclick="openThemeBusinessModal('${row.id}', 'editBacklogItemBusinessThemes.action',${row.id},0,${row.product.id}); return false;">
							<img src="static/img/add_theme.png" alt="Edit themes" title="Edit themes" />
						</ww:a>
			
						<c:forEach items="${row.businessThemes}" var="businessTheme">
							<span class="businessTheme" title="${businessTheme.description}">
								<ww:a href="#" id="${row.id}" onclick="openThemeBusinessModal('${row.id}', 'editBacklogItemBusinessThemes.action',${row.id}, ${businessTheme.id}, ${row.product.id}); return false;">
									${businessTheme.name}
								</ww:a>
							</span>
						</c:forEach>
						<ww:url id="editLink" action="editBacklogItem"
							includeParams="none">
							<ww:param name="backlogItemId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{editLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">
						${aef:html(row.name)}
					</ww:a>
					</display:column>

					<display:column sortable="true" title="Responsibles"
						class="responsibleColumn">
						<div><aef:responsibleColumn backlogItemId="${row.id}" /></div>
					</display:column>

					<display:column sortable="true" defaultorder="descending"
						title="Priority">
						<ww:text name="backlogItem.priority.${row.priority}" />
					</display:column>

					<display:column title="State" sortable="false" class="taskColumn">
							<%@ include file="./inc/_backlogItemStatusBar.jsp"%>							
							<aef:tasklist backlogItem="${row}"
								contextViewName="editIterationGoal"
								contextObjectId="${iterationGoal.id}"
								divId="${divId}" hourReport="${hourReport}"/>
					</display:column>

					<display:column sortable="true" sortProperty="effortLeft"
						defaultorder="descending" title="Effort Left<br/>">
						<span style="white-space: nowrap"> <c:choose>
							<c:when test="${row.effortLeft == null}">&mdash;</c:when>
							<c:otherwise>${row.effortLeft}</c:otherwise>
						</c:choose> </span>
					</display:column>

					<display:column sortable="true" sortProperty="originalEstimate"
						defaultorder="descending" title="Original Estimate<br/>">
						<span style="white-space: nowrap"> <c:choose>
							<c:when test="${row.originalEstimate == null}">&mdash;</c:when>
							<c:otherwise>${row.originalEstimate}</c:otherwise>
						</c:choose> </span>
					</display:column>
					
					<c:choose>
						<c:when test="${hourReport}">
							<display:column sortable="true" sortProperty="effortSpent" defaultorder="descending" title="Effort Spent">
								<span style="white-space: nowrap">
									
									<c:choose>
										<c:when test="${row.effortSpent == null}">&mdash;</c:when>
										<c:otherwise>
											<c:out value="${row.effortSpent}" />
											<c:set var="totalSum" value="${aef:calculateAFTimeSum(totalSum, row.effortSpent)}" />
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
						
						<%-- Effort left --%>
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
							preselectedBacklogId="${iteration.id}" backlogs="${productList}" />
					</td>
				</tr>
				<tr>
					<td>Iteration goal</td>
					<td class="targetBacklogDropdownColumn">
						<select name="targetIterationGoalId">
						<option value="-1">Keep original</option>
						<option value="-2">(none)</option>
						<c:forEach items="${iteration.iterationGoals}" var="itergoal">
								<option value="${itergoal.id}" ><c:out value="${itergoal.name}" /></option>
							</c:forEach>
						</select>
					</td>
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
								var others = [<aef:userJson items="${aef:listSubstract(enabledUserList, iteration.project.responsibles)}"/>];
								var preferred = [<aef:userJson items="${iteration.project.responsibles}"/>];
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
		</p>
		</div>
		</c:if></div>
		</c:if>
	</td>
</tr>
</table>		
<%@ include file="./inc/_footer.jsp"%>