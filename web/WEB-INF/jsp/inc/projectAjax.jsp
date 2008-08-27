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
<aef:projectTypeList id="projectTypes"/>

<script type="text/javascript">
$(document).ready(function() {
    $('#userChooserLink-editProject-${project.id}').userChooser({
        backlogIdField: '#editProject-projectId-${project.id}',
        userListContainer: '#userListContainer-editProject-${project.id}',
        renderFor: 'project',
        validation: {
            selectAtLeast: 0,
            aftime: true
        },
        backlogItemId: 0
    });
});
</script>

<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0px; width: 725px;">
			<div id="editProjectForm" class="validateWrapper validateProject">
			<ww:form action="ajaxStoreProject" method="post">
				<ww:hidden id="editProject-projectId-${project.id}" name="projectId" value="${project.id}" />
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
						<c:choose>
							<c:when test="${!empty projectTypes}">
								<c:set var="typeId" value="0" scope="page" />
								<c:if test="${projectTypeId > 0}">
									<c:set var="typeId" value="${projectTypeId}" />
								</c:if>
								<c:if test="${!empty project.projectType}">
									<c:set var="typeId" value="${project.projectType.id}"
										scope="page" />
								</c:if>
								<ww:select headerKey="0" headerValue="(undefined)"
									name="project.projectType.id" list="#attr.projectTypes"
									listKey="id" listValue="name" value="${typeId}" />
							</c:when>
							<c:otherwise>
								(undefined)
							</c:otherwise>
						</c:choose>							
						</td>
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
						<div>
                            <a id="userChooserLink-editProject-${project.id}" href="#" class="assigneeLink">
                                <img src="static/img/users.png"/>
                                <span id="userListContainer-editProject-${project.id}">
                                <c:set var="count" value="0" />
                                <c:set var="listLength" value="${fn:length(project.assignments)}"/>
                                <c:choose>
                                    <c:when test="${listLength > 0}">
                                        <c:forEach items="${project.assignments}" var="ass">
                                            <input type="hidden" name="selectedUserIds" value="${ass.user.id}"/>
                                            <input type="hidden" name="assignments['${ass.user.id}'].user.id" value="${ass.user.id}"/>
                                            <input type="hidden" name="assignments['${ass.user.id}'].deltaOverhead" value="${ass.deltaOverhead}"/>
                                            <c:set var="count" value="${count + 1}" />
                                            <c:out value="${ass.user.initials}" /><c:if test="${count != listLength}">, </c:if>
                                        </c:forEach>    
                                    </c:when>
                                    <c:otherwise>
                                        (none)
                                    </c:otherwise>
                                </c:choose>
                                </span>
                            </a>
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
		
		<display:column title="Velocity">
            <c:out value="${row.metrics.dailyVelocity}" />                           
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
