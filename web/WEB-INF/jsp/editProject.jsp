<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:choose>
	<c:when test="${!empty project.id}">
		<c:set var="currentProjectId" value="${project.id}" scope="page" />
		<c:if test="${project.id != previousProjectId}">
			<c:set var="previousProjectId" value="${project.id}" scope="session" />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="currentProjectId" value="${previousProjectId}"
			scope="page" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${project.id == 0}">
		<aef:bct productId="${productId}" />
	</c:when>
	<c:otherwise>
		<aef:bct projectId="${projectId}" />
	</c:otherwise>
</c:choose>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />

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

<%--  TODO: fiksumpi virheenkäsittely --%>

<c:choose>
            <c:when test="${projectId == 0}">
                <c:set var="new" value="New" scope="page" />
            </c:when>
            <c:otherwise>
                <c:set var="new" value="" scope="page" />
            </c:otherwise>
        </c:choose>

<c:choose>
	<c:when test="${empty projectTypes}">
		<ww:url id="workTypeLink" action="projectPortfolio"
			includeParams="none" />	
				No project types available. <ww:a href="%{workTypeLink}">Add project types</ww:a>
	</c:when>
	<c:otherwise>
		<aef:productList />
		<c:choose>
			<c:when test="${projectId == 0}">
				<h2>Create project</h2>
				
				<div id="editProjectForm">

        <ww:form action="store${new}Project">
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
                                    <option value="${product.id}" title="${product.name}" class="productOption">${aef:out(product.name)}</option>
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
                    <td>Default Overhead</td>
                    <td></td>
                    <td colspan="2"><ww:textfield size="10" name="project.defaultOverhead" />/ person / week</td>
                    
                </tr>
                <tr>
                    <td>Start date</td>
                    <td>*</td>
                    <td colspan="2"><ww:datepicker value="%{#start}" size="15"
                        showstime="true" format="%{getText('webwork.datepicker.format')}"
                        name="startDate" /></td>
                </tr>
                <tr>
                    <td>End date</td>
                    <td>*</td>
                    <td colspan="2"><ww:datepicker value="%{#end}" size="15"
                        showstime="true" format="%{getText('webwork.datepicker.format')}"
                        name="endDate" /></td>
                </tr>

                <tr>
                    <td>Assigned Users</td>
                    <td></td>
                    <td><c:set var="divId" value="1" scope="page" />

                    <div id="assigneesLink">
                    <a href="javascript:toggleDiv(${divId});">
                        <img src="static/img/users.png" />
                        <c:set var="listSize" value="${fn:length(project.responsibles)}" scope="page" />
                        <c:choose>
                            <c:when test="${listSize > 0}">
                                <c:set var="count" value="0" scope="page" />
                                <c:forEach items="${project.responsibles}" var="responsible">
                                    <c:choose>
                                        <c:when test="${count < listSize - 1}">
                                            <c:out value="${responsible.initials}" /><c:out value="${','}" />
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
                        </c:choose>
                    </a>
                    </div>
                        
                    <div id="${divId}" style="display: none;">
                    <display:table name="${users}" id="user" class="projectUsers"
                        defaultsort="2">
                        <display:column title="">
                            <c:set var="flag" value="0" scope="request" />
                            <c:forEach var="usr" items="${assignedUsers}">
                                <c:if test="${usr.id == user.id}">
                                    <c:set var="flag" value="1" scope="request" />
                                </c:if>
                            </c:forEach>
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
                            <c:if test="${unassignedHasWork[user] == 1}">
                                <span style="color: red">
                            </c:if>
                            <c:out value="${user.fullName}" />
                            <c:if test="${unassignedHasWork[user] == 1}">
                                </span>
                            </c:if>
                        </display:column>
                    </display:table>
                    </a>
                    <div id="userselect" class="projectTeams">
                        <div class="right">
                            <label>Teams</label>
                            <ul class="groups" />
                    </div>
                    <script type="text/javascript" src="static/js/jquery-1.2.2.js"></script>
                    <script type="text/javascript" src="static/js/multiselect.js"></script>
                    <script type="text/javascript">
                        $(document).ready( function() {
                            <aef:teamList />
                            <ww:set name="teamList" value="#attr.teamList" />
                            var teams = [<aef:teamJson items="${teamList}"/>]
                            $('#userselect').multiuserselect({groups: teams, root: $('#user')});
                        });
                        </script>
                    </div>
                    </div>
                    </td>
                </tr>
                
                <tr>
                    <td>Description</td>
                    <td></td>
                    <td colspan="2"><ww:textarea cols="70" rows="10"
                        name="project.description" /></td>
                </tr>

                <tr>
                    <td></td>
                    <td></td>
                    <c:choose>
                        <c:when test="${projectId == 0}">
                            <td><ww:submit value="Create" /></td>
                        </c:when>
                        <c:otherwise>
                            <td><ww:submit value="Save" /></td>
                            <td class="deleteButton"><ww:submit
                                onclick="return confirmDelete()" action="deleteProject"
                                value="Delete" /></td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </table>
        </ww:form>
        
        </div>
			</c:when>
			<c:otherwise>
				<h2><c:out value="${project.name}" /></h2>
				<table>
				<tbody>
				<tr>
				<td>
				<div id="subItems" style="margin-top: 0">
				<div id="subItemHeader">
				
				<script type="text/javascript">
				function expandDescription() {
					document.getElementById('descriptionDiv').style.maxHeight = "1000em";
					document.getElementById('descriptionDiv').style.overflow = "visible";
				}
				function collapseDescription() {
					document.getElementById('descriptionDiv').style.maxHeight = "12em";
					document.getElementById('descriptionDiv').style.overflow = "hidden";
				}
				</script>
				
				
				<table cellspacing="0" cellpadding="0">
				<tr>
					<td class="header">Details <a href="" onclick="toggleDiv('editProjectForm'); toggleDiv('descriptionDiv'); return false;">Edit &raquo;</a></td>
					<td class="icons">
					<%--<a href="" onclick="toggleDiv('editProjectForm'); toggleDiv('descriptionDiv'); return false;">
						<img src="static/img/edit.png" width="18" height="18" alt="Edit" title="Edit" />
					</a>--%>
					<a href="" onclick="expandDescription(); return false;">
						<img src="static/img/plus.png" width="18" height="18" alt="Expand" title="Expand" />
					</a>
					<a href="" onclick="collapseDescription(); return false;">
						<img src="static/img/minus.png" width="18" height="18" alt="Collapse" title="Collapse" />
					</a>
					</td>
				</tr>
				</table>
				</div>
				<div id="subItemContent">
				<div id="descriptionDiv" class="descriptionDiv" style="display: block;">
						<table class="infoTable" cellpadding="0" cellspacing="0">
							<tr>
								<th class="info1">Project type</th>
								<td class="info3"><c:out value="${project.projectType.name}" /></td>
								<td class="info4" rowspan="5">
                                <c:if test="${(!empty project.backlogItems) && (empty project.iterations)}">
                                    <img src="drawSmallProjectChart.action?projectId=${project.id}"/>
                                </c:if>
                                </td>
							</tr>
							<tr>
							    <th class="info1">Default overhead</th>
							    <td class="info3"><c:out value="${project.defaultOverhead}"/> / person / week</td>
						
							</tr>
							<tr>
                                <th class="info1">Timeframe</th>
                                <td class="info3"><c:out value="${project.startDate.date}.${project.startDate.month + 1}.${project.startDate.year + 1900}" /> - 
                                <c:out value="${project.endDate.date}.${project.endDate.month + 1}.${project.endDate.year + 1900}" /></td>
							</tr>
							<tr>
							    <th class="info1">Assignees</th>
							    <td class="info3"><c:set var="listSize"
                                        value="${fn:length(project.responsibles)}" scope="page" /> <c:choose>
                                        <c:when test="${listSize > 0}">
                                            <c:set var="count" value="0" scope="page" />
                                            <c:forEach items="${project.responsibles}" var="responsible">
                                                <ww:url id="userDailyWorkLink" action="dailyWork"
                                                            includeParams="none">
                                                            <ww:param name="userId" value="${responsible.id}"/>
                                                </ww:url>
                                                <c:choose>
                                                    <c:when test="${count < listSize - 1}">
                                                        <a href="${userDailyWorkLink}"><c:out value="${responsible.initials}" /></a>,
                                                    </c:when>
                                                    <c:otherwise>
                                                        <ww:a href="${userDailyWorkLink}">
                                                            <c:out value="${responsible.initials}" />
                                                        </ww:a>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:set var="count" value="${count + 1}" scope="page" />
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="none" />
                                        </c:otherwise>
                                    </c:choose></td>
							</tr>
								
								
							</tr>

							<tr>
							    <td colspan="2" class="description">${project.description}</td>
							</tr>
							
						</table>
				</div>
				
				
				<div id="editProjectForm" style="display: none;">

		<ww:form action="store${new}Project">
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
										title="${product.name}">${aef:out(product.name)}</option>
								</c:when>
								<c:otherwise>
									<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
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
					<td>Default Overhead</td>
					<td></td>
					<td colspan="2"><ww:textfield size="10" name="project.defaultOverhead" /> / person / week</td>
				</tr>
				<tr>
					<td>Start date</td>
					<td>*</td>
					<td colspan="2"><ww:datepicker value="%{#start}" size="15"
						showstime="true" format="%{getText('webwork.datepicker.format')}"
						name="startDate" /></td>
				</tr>
				<tr>
					<td>End date</td>
					<td>*</td>
					<td colspan="2"><ww:datepicker value="%{#end}" size="15"
						showstime="true" format="%{getText('webwork.datepicker.format')}"
						name="endDate" /></td>
				</tr>

				<tr>
					<td>Assigned Users</td>
					<td></td>
					<td><c:set var="divId" value="1" scope="page" />

					<div id="assigneesLink">
					<a href="javascript:toggleDiv(${divId});">
						<img src="static/img/users.png" />
						<c:set var="listSize" value="${fn:length(project.responsibles)}" scope="page" />
						<c:choose>
							<c:when test="${listSize > 0}">
								<c:set var="count" value="0" scope="page" />
								<c:forEach items="${project.responsibles}" var="responsible">
									<c:choose>
										<c:when test="${count < listSize - 1}">
											<c:out value="${responsible.initials}" /><c:out value="${','}" />
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
						</c:choose>
					</a>
					</div>
						
					<div id="${divId}" style="display: none;">
					<display:table name="${users}" id="user" class="projectUsers"
						defaultsort="2">
						<display:column title="">
							<c:set var="flag" value="0" scope="request" />
							<c:forEach var="usr" items="${assignedUsers}">
								<c:if test="${usr.id == user.id}">
									<c:set var="flag" value="1" scope="request" />
								</c:if>
							</c:forEach>
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
							<c:if test="${unassignedHasWork[user] == 1}">
								<span style="color: red">
							</c:if>
							<c:out value="${user.fullName}" />
							<c:if test="${unassignedHasWork[user] == 1}">
								</span>
							</c:if>
						</display:column>
					</display:table>
					</a>
					<div id="userselect" class="projectTeams">
						<div class="right">
							<label>Teams</label>
							<ul class="groups" />
					</div>
					<script type="text/javascript" src="static/js/jquery-1.2.2.js"></script>
					<script type="text/javascript" src="static/js/multiselect.js"></script>
					<script type="text/javascript">
						$(document).ready( function() {
							<aef:teamList />
							<ww:set name="teamList" value="#attr.teamList" />
							var teams = [<aef:teamJson items="${teamList}"/>]
							$('#userselect').multiuserselect({groups: teams, root: $('#user')});
						});
						</script>
					</div>
					</div>
					</td>
				</tr>
                
                <tr>
                    <td>Description</td>
                    <td></td>
                    <td colspan="2"><ww:textarea cols="70" rows="10"
                        name="project.description" /></td>
                </tr>

				<tr>
					<td></td>
					<td></td>
					<c:choose>
						<c:when test="${projectId == 0}">
							<td><ww:submit value="Create" /></td>
						</c:when>
						<c:otherwise>
							<td><ww:submit value="Save" /></td>
							<td class="deleteButton"><ww:submit
								onclick="return confirmDelete()" action="deleteProject"
								value="Delete" /></td>
						</c:otherwise>
					</c:choose>
				</tr>
			</table>
		</ww:form>
		
		</div>
		</div>
				
				</div>
				</td>
				</tr>
				</tbody>
				</table>
			</c:otherwise>
		</c:choose>

		

		
		
		<table>
			<tr>
				<td><c:if test="${project.id > 0}">
					<div id="subItems">

					<div id="subItemHeader">
					<table cellpadding="0" cellspacing="0">
					<tr>
					   <td class="header">Iterations
					   <ww:url id="createLink" action="createIteration" includeParams="none" >
						  <ww:param name="projectId" value="${project.id}" />
					   </ww:url>
					   <ww:a
						href="%{createLink}&contextViewName=editProject&contextObjectId=${project.id}">Create new &raquo;</ww:a>
					   </td>
					</tr>
					</table>
					</div>
					<c:if test="${!empty project.iterations}">
						<div id="subItemContent">
						<p><display:table class="listTable" name="project.iterations"
							id="row" requestURI="editProject.action">

							<display:column sortable="true" sortProperty="name" title="Name"
								class="shortNameColumn">
								<ww:url id="editLink" action="editIteration"
									includeParams="none">
									<ww:param name="iterationId" value="${row.id}" />
								</ww:url>
								<ww:a
									href="%{editLink}&contextViewName=editProject&contextObjectId=${project.id}">
						${aef:html(row.name)}
					</ww:a>
							</display:column>

							<display:column sortable="true" title="Items">
								${fn:length(row.backlogItems)}
							</display:column>
							<%-- REFACTOR THIS --%>
							<display:column sortable="true" title="Effort left"
								sortProperty="totalEffortLeftSum.time"
								defaultorder="descending">
								${effLeftSums[row]}
							</display:column>

							<display:column sortable="true" title="Original estimate"
								sortProperty="totalOriginalEstimateSum.time"
								defaultorder="descending">
								${origEstSums[row]}
							</display:column>

							<display:column sortable="true" title="Start date">
								<ww:date name="#attr.row.startDate" />
							</display:column>

							<display:column sortable="true" title="End date">
								<ww:date name="#attr.row.endDate" />
							</display:column>

							<display:column sortable="false" title="Actions">
								<ww:url id="deleteLink" action="deleteIteration"
									includeParams="none">
									<ww:param name="projectId" value="${project.id}" />
									<ww:param name="iterationId" value="${row.id}" />
								</ww:url>
								<ww:a
									href="%{deleteLink}&contextViewName=editProject&contextObjectId=${project.id}"
									onclick="return confirmDelete()">Delete</ww:a>
							</display:column>

						</display:table></p>
						</div>
					</c:if>
					<div id="subItemHeader">
					<table cellpadding="0" cellspacing="0">
                    <tr>
                       <td class="header">Backlog items <ww:url
						id="createBacklogItemLink" action="createBacklogItem"
						includeParams="none">
						<ww:param name="backlogId" value="${project.id}" />
					</ww:url> <ww:a
						href="%{createBacklogItemLink}&contextViewName=editProject&contextObjectId=${project.id}">Create new &raquo;</ww:a>
						</td>
						</tr>
						</table>
					</div>

					<c:if test="${!empty project.backlogItems}">
						<div id="subItemContent"><%@ include
							file="./inc/_backlogList.jsp"%></div>
					</c:if></div>
					<c:if test="${!empty project.backlogItems}">
						<c:if test="${empty project.iterations}">
							<p><img src="drawProjectChart.action?projectId=${project.id}" /></p>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</table>
	</c:otherwise>
</c:choose>

<%@ include file="./inc/_footer.jsp"%>