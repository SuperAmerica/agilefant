<%@ include file="_taglibs.jsp"%>
<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">

	<li><a href="#projectEditTab-${projectId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit project</span></a></li>
	<li><a href="#projectIterationsTab-${projectId}"><span><img src="static/img/backlog.png" alt="Iterations" /> Iterations</span></a></li>
</ul>
<div id="projectEditTab-${projectId}" class="projectNaviTab">

<ww:date name="%{new java.util.Date()}" id="start"
	format="%{getText('webwork.shortDateTime.format')}" />
<ww:date name="%{new java.util.Date()}" id="end"
	format="%{getText('webwork.shortDateTime.format')}" />
<c:if test="${project.id > 0}">
	<ww:date name="%{project.startDate}" id="start"
		format="%{getText('webwork.shortDateTime.format')}" />
	<ww:date name="%{project.endDate}" id="end"
		format="%{getText('webwork.shortDateTime.format')}" />
</c:if>
<ww:hidden name="projectId" value="${project.id}" />
<ww:hidden name="productId" value="${project.product.id}" />

<aef:productList />

<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0px; width: 725px;">
			<div id="editProjectForm">
			<ww:form action="ajaxStoreProject" method="post">
				<ww:hidden name="projectId" value="${project.id}" />
				<table class="formTable">
					<tr>
						<td>Name</td>
						<td>*</td>
						<td colspan="2"><ww:textfield size="60" name="project.name" /></td>
					</tr>
					<tr>
						<td>Product</td>
						<td>*</td>
						<td colspan="2">
						<select name="productId">
							<option class="inactive" value="">(select product)</option>
							<c:forEach items="${productList}" var="product">
								<c:choose>
									<c:when test="${product.id == currentProductId}">
										<option selected="selected" value="${product.id}"
											title="${product.name}">${aef:out(product.name)}</option>
									</c:when>
									<c:otherwise>
										<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						</td>
					</tr>
					<tr>
						<td>Project type</td>
						<td></td>
						<td colspan="2">
							<ww:select name="projectTypeId"
								list="#attr.projectTypes" listKey="id" listValue="name"
								value="${project.projectType.id}" /></td>
					</tr>
					<tr>
						<td>Status</td>
						<td></td>
						<td colspan="2">
							<ww:select name="project.status" id="statusSelect"
								value="project.status.name" list="@fi.hut.soberit.agilefant.model.Status@values()"
								listKey="name" listValue="getText('project.status.' + name())" />							
						</td>
					</tr>
					<tr>
						<td>Default Overhead</td>
						<td></td>
						<td colspan="2"><ww:textfield size="10"
							name="project.defaultOverhead" /> / person / week
							<span class="errorMessage"></span></td>
					</tr>
					<tr>
						<td>Start date</td>
						<td>*</td>
						<td colspan="2"><aef:datepicker id="start_date_${project.id}"
							name="startDate"
							format="%{getText('webwork.shortDateTime.format')}"
							value="%{#start}" /></td>
					</tr>
					<tr>
						<td>End date</td>
						<td>*</td>
						<td colspan="2">
							<aef:datepicker id="end_date_${project.id}"
							name="endDate"
							format="%{getText('webwork.shortDateTime.format')}"
							value="%{#end}" /></td>
					</tr>
					<tr>
						<td>Assigned Users</td>
						<td></td>
						<td colspan="2">
						<div id="assigneesLink">
						<a href="javascript:toggleDiv('users_${project.id}');">
							<img src="static/img/users.png" />
							<c:set var="listSize"
								value="${fn:length(project.responsibles)}" scope="request" />
							<c:choose>
							<c:when test="${listSize > 0}">
								<c:set var="count" value="0" scope="request" />
								<c:forEach items="${project.responsibles}" var="responsible">
									<c:choose>
										<c:when test="${count < listSize - 1}">
											<c:out value="${responsible.initials}" />
											<c:out value="${','}" />
										</c:when>
										<c:otherwise>
											<c:out value="${responsible.initials}" />
										</c:otherwise>
									</c:choose>
									<c:set var="count" value="${count + 1}" scope="request" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="none" />
							</c:otherwise>
						</c:choose>
						</a>
						</div>
						<div id="users_${project.id}" style="display: none;">
							<display:table name="${assignableUsers}" id="user" class="projectUsers"
								defaultsort="2">
							<display:column title="">								
								<%-- Test, if the user should be checked --%>
								<c:set var="flag" value="0" scope="request" />
								<c:if test="${aef:listContains(assignedUsers, user)}">
									<c:set var="flag" value="1" scope="request" />
								</c:if>
								<c:choose>
									<c:when test="${flag == 1}">
										<input type="checkbox" name="selectedUserIds"
											value="${user.id}" checked="checked" class="user_${user.id}"
											onchange="toggleDiv('${user.id}-${project.id}')" />
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="selectedUserIds"
											value="${user.id}" class="user_${user.id}"
											onchange="toggleDiv('${user.id}-${project.id}')" />
									</c:otherwise>
								</c:choose>
							</display:column>
							<display:column title="User" sortProperty="fullName">
								<c:choose>
									<c:when test="${unassignedHasWork[user] == 1}">
										<span style="color: red">
									</c:when>
									<c:otherwise>
										<span>
									</c:otherwise>
								</c:choose>
								<c:out value="${user.fullName}" />
								<!-- Test, if the user is not enabled -->
								<c:if test="${aef:listContains(disabledUsers, user)}">
									<img src="static/img/disable_user_gray.png"
										alt="The user is disabled" title="The user is disabled" />
								</c:if>								
								</span>
							</display:column>
							<display:column title="Overhead +/-">
								<!-- Check whether user is assigned. If is assigned -> show overhead -->
								<c:choose>
									<c:when test="${flag == 1}">
										<div id="${user.id}-${project.id}" class="overhead">
									</c:when>
									<c:otherwise>
										<div id="${user.id}-${project.id}" class="overhead" Style="display: none;">
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty project.defaultOverhead}">
										<ww:label value="0h" />
									</c:when>
									<c:otherwise>
										<ww:label value="${project.defaultOverhead}" />
									</c:otherwise>
								</c:choose>																		
								+
								<ww:hidden name="assignments['${user.id}'].user.id" value="${user.id}" />
								<ww:textfield size="3"
									name="assignments['${user.id}'].deltaOverhead" /> =																		
								<c:choose>
									<c:when test="${!empty totalOverheads[user.id]}">
										<ww:label value="${totalOverheads[user.id]}" />
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${empty project.defaultOverhead}">
												<ww:label value="0h" />
											</c:when>
											<c:otherwise>
												<ww:label value="${project.defaultOverhead}" />
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
						</div>
						</display:column>
						</display:table>
						<div id="userselect_${project.id}" class="projectTeams userSelector">
							<div class="right"><label>Teams</label>
								<ul class="groups" />
							</div>
							<script type="text/javascript">
							var tmp = function () {
								<aef:teamList />
								<ww:set name="teamList" value="#attr.teamList" />
								var teams = [<aef:teamJson items="${teamList}"/>]
								//$('#userselect_${project.id}').multiuserselect({groups: teams, root: $('#userselect_${project.id}')});
								$('#userselect_${project.id}').groupselect(teams,'#users_${project.id}');
							}
							tmp();
							</script>
						</div>
						
						</td>
					</tr>
					<tr>
						<td>Description</td>
						<td></td>
						<td colspan="2">&nbsp;<ww:textarea cols="70" rows="10" cssClass="useWysiwyg" 
                        	name="project.description" /></td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td><ww:submit value="Save" id="saveButton" />
						</td>
						<td class="deleteButton">
						<ww:reset value="Cancel"
							onclick="closeTabs('projects', 'projectTabContainer-${projectId}', ${projectId});" />
						</td>						
					</tr>
				</table>
			</ww:form>
			</div>
			</div>						
			</td>
		</tr>
	</tbody>
</table>
</div>

<div id="projectIterationsTab-${projectId}" class="projectNaviTab">

<c:choose>
<c:when test="${!empty project.iterations}">
	<div class="subItemContent">
	<p>
	<display:table class="listTable" name="project.iterations" id="row" requestURI="editProject.action">
		<display:column title="Name" class="shortNameColumn">
			<ww:url id="editLink" action="editIteration" includeParams="none">
			<ww:param name="iterationId" value="${row.id}" />
			</ww:url>
				<ww:a href="%{editLink}&contextViewName=editProject&contextObjectId=${project.id}">
					${aef:html(row.name)}
				</ww:a>
		</display:column>
		
		<display:column title="Completed BLIs" style="width: 90px;">
			<c:out value="${row.metrics.percentDone}" />%
			(<c:out value="${row.metrics.completedItems}" /> /
			<c:out value="${row.metrics.totalItems}" />)	
		</display:column>
		
		<display:column title="Effort left">
			<c:out value="${row.metrics.effortLeft}" />
		</display:column>
		
		<display:column title="Original estimate">
			<c:out value="${row.metrics.originalEstimate}" />							
		</display:column>

		<display:column title="Start date">
			<ww:date name="#attr.row.startDate" />
		</display:column>
		
		<display:column title="End date">
			<ww:date name="#attr.row.endDate" />
		</display:column>		
	</display:table>
	</p>
	</div>
</c:when>
<c:otherwise>
 The project has no iterations.
</c:otherwise>
</c:choose>
</div>

</div>
