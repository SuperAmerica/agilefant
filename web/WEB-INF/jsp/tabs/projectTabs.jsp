<%@ include file="../inc/_taglibs.jsp"%>
<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#projectEditTab-${projectId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit project</span></a></li>
	<li><a href="#projectIterationsTab-${projectId}"><span><img src="static/img/backlog.png" alt="Iterations" /> Iterations</span></a></li>
	<%-- TODO: Bring back themes
	<li><a href="#projectThemesTab-${projectId}"><span><img src="static/img/theme.png" alt="Themes" /> Themes</span></a></li>
	 --%>
</ul>
<div id="projectEditTab-${projectId}" class="projectNaviTab">

<ww:date name="%{new java.util.Date()}" id="start"
	format="%{getText('struts.shortDateTime.format')}" />
<ww:date name="%{new java.util.Date()}" id="end"
	format="%{getText('struts.shortDateTime.format')}" />
<c:if test="${project.id > 0}">
	<ww:date name="%{project.startDate}" id="start"
		format="%{getText('struts.shortDateTime.format')}" />
	<ww:date name="%{project.endDate}" id="end"
		format="%{getText('struts.shortDateTime.format')}" />
</c:if>
<ww:hidden name="projectId" value="%{project.id}" />
<ww:hidden name="productId" value="%{project.parent.id}" />

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
        storyId: 0
    });
});
</script>

<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0px; width: 720px;">
			<div id="editProjectForm" class="validateWrapper validateProject">
			<ww:form action="ajaxStoreProject" method="post">
        <ww:hidden id="editProject-projectId-%{project.id}" name="projectId" value="%{project.id}" />
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
									<c:when test="${product.id == productId}">
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
          <%--
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
          --%>
					<tr>
						<td>Status</td>
						<td></td>
						<td colspan="2">
							<ww:select name="project.status" id="statusSelect"
								value="project.status.name" list="@fi.hut.soberit.agilefant.model.Status@values()"
								listKey="name" listValue="getText('project.status.' + name())" />							
						</td>
					</tr>
          <%--
					<tr>
						<td>Baseline load</td>
						<td></td>
						<td colspan="2"><ww:textfield size="10"
							name="project.defaultOverhead" /> / person / week
							<span class="errorMessage"></span></td>
					</tr>
          --%>
					<tr>
                        <td>Planned project size</td>
                        <td></td>
                        <td colspan="2"><ww:textfield size="10" id="project.backlogSize" name="project.backlogSize" /> (total man hours)
                        </td>
                    </tr>
					<tr>
						<td>Start date</td>
						<td>*</td>
						<td colspan="2"><aef:datepicker id="start_date_${project.id}"
							name="startDate"
							format="%{getText('struts.shortDateTime.format')}"
							value="%{#start}" /></td>
					</tr>
					<tr>
						<td>End date</td>
						<td>*</td>
						<td colspan="2">
							<aef:datepicker id="end_date_${project.id}"
							name="endDate"
							format="%{getText('struts.shortDateTime.format')}"
							value="%{#end}" /></td>
					</tr>
          <%--
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
          --%>
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
						<ww:submit value="Delete" action="deleteProject"/>
						<ww:reset value="Cancel"/>
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
<c:when test="${!empty project.children}">
	<div class="subItemContent">
	<p>
	<display:table class="listTable" name="project.children" id="row" requestURI="editProject.action">
		<display:column title="Name" class="shortNameColumn">
			<ww:url id="editLink" action="editIteration" includeParams="none">
			<ww:param name="iterationId" value="%{row.id}" />
			</ww:url>
				<ww:a href="%{editLink}&contextViewName=editProject&contextObjectId=%{project.id}">
					${aef:html(row.name)}
				</ww:a>
		</display:column>
		
<%--
		<display:column title="Completed Storys" style="width: 90px;">
			<c:out value="${row.metrics.percentDone}" />%
			<c:out value="( ${row.metrics.completedItems} / " />
			<c:out value="${row.metrics.totalItems} )" />
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
--%>
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
<%-- TODO: Bring back themes
<div id="projectThemesTab-${projectId}" class="projectNaviTab">
<script type="text/javascript">
$(document).ready( function() {
    var iterationThemes = [<c:forEach items="${iterationThemes}" var="bind">${bind.businessTheme.id},</c:forEach>-1];
	var getThemeData = function() {
		var ret = {};
		var data = jsonDataCache.get('themesByProduct',{data: {productId: ${project.parent.id}, includeGlobalThemes: true}},${project.parent.id});
		jQuery.each(data,function() {
			if(this.active === true && jQuery.inArray(this.id,iterationThemes) == -1) {
				ret[this.id] = this.name;
			}
		});
		return ret;
	};
	$('#businessThemeTable_${project.id}').inlineTableEdit({add: '#addProjectBusinessTheme_${project.id}', 
											  submit: '#backlogThemeSave_${project.id}',
											  submitParam: 'bindingId',
											  deleteaction: 'removeThemeFromBacklog.action',
											  fields: {
											  	businessThemeIds: {cell: 0,type: 'select', data: getThemeData},
											  	plannedSpendings: {cell: 1, type: 'text'},											  											  	
											  	reset: {cell: 2, type: 'reset'}
											  }
											 });
								  
});

</script>


<table>
<tr>
<td>

<div class="subItems validateWrapper validateEmpty" style="margin-top: 0; margin-left: 3px; width: 710px;">
	<a id="addProjectBusinessTheme_${project.id}" href="#">Attach theme &raquo;</a>

	<ww:form action="storeBacklogThemebinding" method="post" id="projectBusinessThemesForm_%{project.id}">
	<ww:hidden name="backlogId" value="%{project.id}"/>
	<input type="hidden" name="contextViewName" value="project" />
	<div class="businessThemeTableWrapper">
	<c:choose>
	<c:when test="${!empty project.businessThemeBindings}">
	<display:table htmlId="businessThemeTable_${project.id}" class="listTable" name="project.businessThemeBindings" id="row" requestURI="editIteration.action">

		<display:column sortable="false" title="Name" sortProperty="businessTheme.name">
			<span style="display: none;">${row.businessTheme.id}</span>
			<a style="cursor: pointer; color: #0055AA;" class="table_edit_edit">
				<c:out value="${row.businessTheme.name}"/>
			</a>												
		</display:column>
		
		<display:column sortable="false" sortProperty="boundEffort" title="Planned spending">
			<c:choose>
				<c:when test="${row.relativeBinding == true}">
					<span style="display:none;">${row.percentage}%</span>
					<c:out value="${row.boundEffort}"/>
					(<c:out value="${row.percentage}"/>%)
				</c:when>
				<c:otherwise><c:out value="${row.fixedSize}"/></c:otherwise>
			</c:choose>
		</display:column>
		<display:column sortable="false" title="Actions">
			<span class="uniqueId" style="display: none;">${row.id}</span>
			<img style="cursor: pointer;" class="table_edit_edit" src="static/img/edit.png" title="Edit" />
			<img style="cursor: pointer;" class="table_edit_delete" src="static/img/delete_18.png" title="Delete" />
		</display:column>
	</display:table>
		</c:when>
		<c:otherwise>
			<table id="businessThemeTable_${project.id}" style="display:none;" class="listTable">
				<tr><th class="sortable">Name</th><th class="sortable">Planned spending</th><th>Actions</th></tr>
			</table>
		</c:otherwise>
		</c:choose>
		<div id="backlogThemeSave_${project.id}" style="display: none;">
		<input id="backlogThemeSave_${project.id}" style="margin-left: 2px;" type="submit" value="Save" />
		<br>
		<ww:label id="themeLabel" value="Planned spending may be entered as time (e.g. 2h 30min) or a percentage
			(e.g. 40%)." />
		</div>
		</ww:form>	
</div>

<c:if test="${!empty iterationThemes}">
	<div class="businessThemeTableWrapper">
	<h4>Iteration themes</h4>
		<display:table htmlId="businessThemeTable" class="listTable" name="iterationThemes" id="row" requestURI="editProject.action">

			<display:column sortable="false" title="Name" sortProperty="businessTheme.name">														
				<c:out value="${row.businessTheme.name}"/>														
			</display:column>
			
			<display:column sortable="false" sortProperty="boundEffort" title="Planned spending">
				<c:choose>
					<c:when test="${row.relativeBinding == true}">
						<c:out value="${row.boundEffort}"/>
						(<c:out value="${row.percentage}"/>%)
					</c:when>
					<c:otherwise><c:out value="${row.fixedSize}"/></c:otherwise>
				</c:choose>
			</display:column>
			<display:column sortable="false" title="Iteration" sortProperty="backlog.name">
				<ww:url id="editLink" action="editIteration" includeParams="none">
					<ww:param name="iterationId" value="%{row.backlog.id}" />
				</ww:url>
				<ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=%{project.id}">						
					<c:out value="${row.backlog.name}"/>
				</ww:a>
			</display:column>
		</display:table>
    </div>
</c:if>

</td>
</tr>
</table>

--%>

</div>

</div>
