<%@ include file="../inc/_taglibs.jsp"%>

<aef:productList />

<ww:date name="%{new java.util.Date()}" id="start"
	format="%{getText('webwork.shortDateTime.format')}" />
<ww:date name="%{new java.util.Date()}" id="end"
	format="%{getText('webwork.shortDateTime.format')}" />

<div class="validateWrapper validateNewProject">
<ww:form action="storeNewProject" method="post">
<c:choose>
	<c:when test="${empty projectTypes}">
		<ww:url id="workTypeLink" action="ajaxCreateProjectType"
			includeParams="none" />	
				No project types available. <ww:a href="%{workTypeLink}" cssClass="openCreateDialog openProjectTypeDialog">Create a new project type &raquo;</ww:a>
	</c:when>
	<c:otherwise>
		<div id="editProjectForm">
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
					<td colspan="2"><select name="productId">
						<option class="inactive" value="">(select product)</option>
						<c:forEach items="${productList}" var="product">
							<c:choose>
								<c:when test="${product.id == currentProductId}">
									<option selected="selected" value="${product.id}"
										title="${product.name}" class="productOption">${aef:out(product.name)}</option>
								</c:when>
								<c:otherwise>
									<option value="${product.id}" title="${product.name}"
										class="productOption">${aef:out(product.name)}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>Project type</td>
					<td></td>
					<td colspan="2"><ww:select name="projectTypeId"
						list="#attr.projectTypes" listKey="id" listValue="name"
						value="${project.projectType.id}" /></td>
				</tr>
				<tr>
					<td>Status</td>
					<td></td>
					<td colspan="2"><ww:select name="project.status"
						id="statusSelect" value="project.status.name"
						list="@fi.hut.soberit.agilefant.model.Status@values()"
						listKey="name" listValue="getText('project.status.' + name())" />
					</td>
				</tr>
				<tr>
					<td>Default Overhead</td>
					<td></td>
					<td colspan="2"><ww:textfield size="10" id="default_overhead"
						name="project.defaultOverhead" />/ person / week
						<span class="errorMessage"></span>	
						</td>
				</tr>
				<tr>
					<td>Planned project size</td>
					<td></td>
					<td colspan="2"><ww:textfield size="10" id="project.backlogSize"
						name="project.backlogSize" /> (total man hours)
						</td>
				</tr>
				<tr>
					<td>Start date</td>
					<td>*</td>
					<td colspan="2"><aef:datepicker
						id="start_date_${project.id}" name="startDate"
						format="%{getText('webwork.shortDateTime.format')}"
						value="%{#start}" /><label for="start_date">&nbsp;</label></td>
				</tr>
				<tr>
					<td>End date</td>
					<td>*</td>
					<td colspan="2"><aef:datepicker
						id="end_date_${project.id}" name="endDate"
						format="%{getText('webwork.shortDateTime.format')}"
						value="%{#end}" /><label for="end_date">&nbsp;</label></td>
				</tr>
				<tr>
					<td>Assigned Users</td>
					<td></td>
					<td colspan="2">
					<div id="assigneesLink"><a
						href="javascript:toggleDiv('createNewProject_assignees');"> <img
						src="static/img/users.png" /> <c:set var="listSize"
						value="${fn:length(project.responsibles)}" scope="request" /> <c:choose>
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
					</c:choose> </a></div>
					<div id="createNewProject_assignees" style="display: none;"><display:table
						name="${assignableUsers}" id="user" class="projectUsers"
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
										onchange="toggleDiv('createProject_${user.id}')" />
								</c:when>
								<c:otherwise>
									<input type="checkbox" name="selectedUserIds"
										value="${user.id}" class="user_${user.id}"
										onchange="toggleDiv('createProject_${user.id}')" />
								</c:otherwise>
							</c:choose>
						</display:column>
						<display:column title="User" sortProperty="fullName">
							<c:choose>
								<c:when test="${unassignedHasWork[user] == 1}">
									<span style="color: red; height: 21px; margin: 0; padding: 0;">
								</c:when>
								<c:otherwise>
									<span>
								</c:otherwise>
							</c:choose>
							<c:out value="${user.fullName}" />
							</span>

							<%-- Test, if the user is not enabled --%>
							<c:if test="${aef:listContains(disabledUsers, user)}">
								<img src="static/img/disable_user_gray.png"
									alt="The user is disabled" title="The user is disabled" />
							</c:if>
						</display:column>
						<display:column title="Overhead +/-">
								<!-- Check whether user is assigned. If is assigned -> show overhead -->
								<c:choose>
									<c:when test="${flag == 1}">
										<div id="createProject_${user.id}" class="overhead">
									</c:when>
									<c:otherwise>
										<div id="createProject_${user.id}" class="overhead" Style="display: none;">
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
					<div id="userselect_createNew" class="projectTeams userSelector">
					<div class="right"><label>Teams</label>
					<ul class="groups" />
					</div>
					<script type="text/javascript">
                       var tmp = function() {
                        	<aef:teamList />
                        	<ww:set name="teamList" value="#attr.teamList" />
                        	var teams = [<aef:teamJson items="${teamList}"/>]
                        	//$('#userselect_createNew').multiuserselect({groups: teams, root: $('#user')});
                        	$('#userselect_createNew').groupselect(teams,'#createNewProject_assignees');
                        };
                        tmp();
                       	</script></div>
					</td>
				</tr>
				<tr>
					<td>Description</td>
					<td></td>
					<td colspan="2">&nbsp;<ww:textarea cols="70" rows="10"
						cssClass="useWysiwyg" name="project.description" /></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td><ww:submit value="Create" id="createButton" /></td>
					<td class="deleteButton">
					<ww:reset value="Cancel" cssClass="closeDialogButton"/></td>
				</tr>
			</table>
		</div>
	</c:otherwise>
</c:choose>
</ww:form>
</div>