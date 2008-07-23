<%@ include file="../inc/_taglibs.jsp"%>

<ww:date name="%{new java.util.Date()}" id="start"
	format="%{getText('webwork.shortDateTime.format')}" />
<ww:date name="%{new java.util.Date()}" id="end"
	format="%{getText('webwork.shortDateTime.format')}" />
<ww:form action="storeNewProject" method="post">
<c:choose>
	<c:when test="${empty projectTypes}">
		<ww:url id="workTypeLink" action="createProjectType"
			includeParams="none" />	
				No project types available. <ww:a href="%{workTypeLink}">Create a new project type &raquo;</ww:a>
	</c:when>
	<c:otherwise>
		<h2>Create project</h2>
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
					<td colspan="2"><select name="productId"
						onchange="disableIfEmpty(this.value, ['createButton']);">
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
					<td colspan="2"><ww:textfield size="10"
						name="project.defaultOverhead" />/ person / week</td>
				</tr>
				<tr>
					<td>Start date</td>
					<td>*</td>
					<td colspan="2"><%--<ww:datepicker value="%{#start}" size="15"
                        			showstime="true" format="%{getText('webwork.datepicker.format')}"
                        			name="startDate" />--%> <aef:datepicker
						id="start_date" name="startDate"
						format="%{getText('webwork.shortDateTime.format')}"
						value="%{#start}" /></td>
				</tr>
				<tr>
					<td>End date</td>
					<td>*</td>
					<td colspan="2"><%--<ww:datepicker value="%{#end}" size="15"
                        			showstime="true" format="%{getText('webwork.datepicker.format')}"
                        			name="endDate" />--%> <aef:datepicker
						id="end_date" name="endDate"
						format="%{getText('webwork.shortDateTime.format')}"
						value="%{#end}" /></td>
				</tr>
				<tr>
					<td>Assigned Users</td>
					<td></td>
					<td><c:set var="divId" value="1" scope="page" />
					<div id="assigneesLink"><a
						href="javascript:toggleDiv(${divId});"> <img
						src="static/img/users.png" /> <c:set var="listSize"
						value="${fn:length(project.responsibles)}" scope="page" /> <c:choose>
						<c:when test="${listSize > 0}">
							<c:set var="count" value="0" scope="page" />
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
								<c:set var="count" value="${count + 1}" scope="page" />
							</c:forEach>
						</c:when>
						<c:otherwise>
							<c:out value="none" />
						</c:otherwise>
					</c:choose> </a></div>
					<div id="${divId}" style="display: none;"><display:table
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
										value="${user.id}" checked="checked" class="user_${user.id}" />
								</c:when>
								<c:otherwise>
									<input type="checkbox" name="selectedUserIds"
										value="${user.id}" class="user_${user.id}" />
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
					</display:table>
					<div id="userselect" class="projectTeams">
					<div class="right"><label>Teams</label>
					<ul class="groups" />
					</div>
					<script type="text/javascript">
                        					$(document).ready( function() {
                            					<aef:teamList />
                            					<ww:set name="teamList" value="#attr.teamList" />
                           	 					var teams = [<aef:teamJson items="${teamList}"/>]
                            					$('#userselect').multiuserselect({groups: teams, root: $('#user')});
                        					});
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
				</tr>
			</table>
		</div>
	</c:otherwise>
</c:choose>
</ww:form>