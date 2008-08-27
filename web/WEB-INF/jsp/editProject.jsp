<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>


<aef:projectTypeList id="projectTypes"/>
<aef:openDialogs context="iteration" id="openIterations" />

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
<script type="text/javascript">
<!--
$(document).ready(function() {
	<c:forEach items="${openIterations}" var="openIteration">
        handleTabEvent("iterationTabContainer-${openIteration[0]}", "iteration", ${openIteration[0]}, ${openIteration[1]});
    </c:forEach>

	var iterationThemes = [<c:forEach items="${iterationThemes}" var="bind">${bind.businessTheme.id},</c:forEach>-1];
	var getThemeData = function() {
		var ret = {};
		var data = jsonDataCache.get('themesByProduct',{data: {productId: ${project.product.id}}},${project.product.id});
		jQuery.each(data,function() {
			if(this.active === true && jQuery.inArray(this.id,iterationThemes) == -1) {
				ret[this.id] = this.name;
			}
		});
		return ret;
	};
	$('#businessThemeTable').inlineTableEdit({add: '#addProjectBusinessTheme', 
											  submit: '#backlogThemeSave',
											  submitParam: 'bindingId',
											  deleteaction: 'removeThemeFromBacklog.action',
											  fields: {
											  	businessThemeIds: {cell: 0,type: 'select', data: getThemeData},
											  	plannedSpendings: {cell: 1, type: 'text' },
											  	reset: {cell: 2, type: 'reset'}
											  }
											 });
    $('#userChooserLink-editProject').userChooser({
        backlogIdField: '#editProject-projectId',
        userListContainer: '#userListContainer-editProject',
        renderFor: 'project',
        backlogItemId: 0
    });
});
//-->
</script>
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
		<aef:productList />
			<h2><c:out value="${project.name}" /></h2>
				<table>
					<tbody>
						<tr>
							<td>
								<div class="subItems" style="margin-top: 0">
									<div class="subItemHeader">
										<script type="text/javascript">
											function expandDescription() {
												document.getElementById('descriptionDiv').style.maxHeight = "1000em";
												document.getElementById('descriptionDiv').style.overflow = "visible";
											}
											function collapseDescription() {
												document.getElementById('descriptionDiv').style.maxHeight = "12em";
												document.getElementById('descriptionDiv').style.overflow = "hidden";
											}
											function editProject() {
												toggleDiv('editProjectForm'); toggleDiv('descriptionDiv'); showWysiwyg('projectDescription'); return false;
											}
										</script>
										<table cellspacing="0" cellpadding="0">
											<tr>
												<td class="header">Details <a href="" onclick="return editProject();">Edit &raquo;</a></td>
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
									<div class="subItemContent">
										<div id="descriptionDiv" class="descriptionDiv" style="display: block;">
											<table class="infoTable" cellpadding="0" cellspacing="0">
												<tr>
								    				<th class="info1">Status</th>
								    				<td class="info3" ondblclick="return editProject();">
								    				<c:choose>
														<c:when test="${project.status == 'OK'}">
															<img src="static/img/status-green.png" alt="OK" title="OK"/>
														</c:when>
														<c:when test="${project.status == 'CHALLENGED'}">
															<img src="static/img/status-yellow.png" alt="Challenged" title="Challenged"/>
														</c:when>
														<c:when test="${project.status == 'CRITICAL'}">
															<img src="static/img/status-red.png" alt="Critical" title="Critical"/>
														</c:when>
													</c:choose>
													<ww:text name="project.status.${project.status}" />
								    				</td>
								    				<!--  <td class="info3" ondblclick="return editProject();"><ww:text name="project.status.${project.status}" /></td> -->
								    				
								    				<td class="info4" rowspan="7">
	                                					<c:if test="${(!empty project.backlogItems) && (empty project.iterations)}">
	                                    					<div class="smallBurndown"><a href="#bigChart">
	                                    						<img src="drawSmallProjectChart.action?projectId=${project.id}"/>
	                                    					</a></div>
	                                					
	                                					
                    									<table>
										                  <tr>
											                 <th>Velocity</th>
											                 <td><c:out value="${projectMetrics.dailyVelocity}" /> /
											                 day</td>
										                  </tr>
										                  <c:if test="${projectMetrics.backlogOngoing}">
										                  <tr>
											                 <th>Schedule variance</th>
											                 <td><c:choose>
												                    <c:when test="${projectMetrics.scheduleVariance != null}">
													                   <c:choose>
                                                                       <c:when test="${projectMetrics.scheduleVariance > 0}">
                                                                            <span class="red">+
                                                                       </c:when>
                                                                        <c:otherwise>
                                                                            <span>
                                                                        </c:otherwise>
                                                                        </c:choose>
													                   <c:out value="${projectMetrics.scheduleVariance}" /> days
													                   </span>
                                                                </c:when>
												                    <c:otherwise>
                                                                    unknown
                                                                </c:otherwise>
											                 </c:choose></td>
										                  </tr>
										                  <tr>
											                 <th>Scoping needed</th>
											                 <td><c:choose>
												                    <c:when test="${projectMetrics.scopingNeeded != null}">
													                   <c:out value="${projectMetrics.scopingNeeded}" />
												                    </c:when>
												                    <c:otherwise>
                                                                    unknown
                                                                </c:otherwise>
											                 </c:choose></td>
										                  </tr>
										                  </c:if>
										                  <tr>
											                 <th>Completed</th>
											                 <td><c:out value="${projectMetrics.percentDone}" />% (<c:out
												                    value="${projectMetrics.completedItems}" /> / <c:out
												                    value="${projectMetrics.totalItems}" />)</td>
										                  </tr>
									                   </table>
									                   </c:if>
									                </td>							
												</tr>
												<tr>
													<th class="info1">Project type</th>
													<td class="info3" ondblclick="return editProject();">
													<c:choose>
													<c:when test="${(!empty project.projectType)}">
														<c:out value="${project.projectType.name}" />
													</c:when>
													<c:otherwise>
														undefined
													</c:otherwise>
													</c:choose>
													</td>													
												</tr>
												<tr>
								    				<th class="info1">Default overhead</th>
								    				<td class="info3" ondblclick="return editProject();">
								    					<c:choose>
								    					<c:when test="${(!empty project.defaultOverhead)}">
								    						<c:out value="${project.defaultOverhead}"/> / person / week
								    					</c:when>
								    					<c:otherwise>
								    						-
								    					</c:otherwise>
								    					</c:choose>
								    				</td>							
												</tr>
																				
										         <tr>
													<th class="info1">Planned project size</th>
													<td class="info3" ondblclick="return editProject();">
														<c:choose>
														<c:when test="${(!empty project.backlogSize)}">
															<c:out value="${project.backlogSize}"/>h
														</c:when>
														<c:otherwise>
															-
														</c:otherwise>
														</c:choose>
													</td>
												</tr>	
												<tr>
	                                				<th class="info1">Timeframe</th>
	                                				<td class="info3" ondblclick="return editProject();"><c:out value="${project.startDate.date}.${project.startDate.month + 1}.${project.startDate.year + 1900}" /> - 
	                                					<c:out value="${project.endDate.date}.${project.endDate.month + 1}.${project.endDate.year + 1900}" /></td>
												</tr>
												<tr>
								    				<th class="info1">Assignees</th>
								    				<td class="info3"><c:set var="listSize"
	                                        			value="${fn:length(project.responsibles)}" scope="page" />
	                                        			<c:choose>
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
	                                    				</c:choose>
	                                    			</td>
												</tr>
												<tr>
								    				<td colspan="2" class="description">${project.description}</td>
												</tr>
											</table>
										</div>
										<div id="editProjectForm" style="display: none;" class="validateWrapper validateProject">
											<ww:form id="projectEditForm" action="storeProject" method="post">
												<ww:hidden id="editProject-projectId" name="projectId" value="${project.id}" />
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
														<%-- If project types don't exist default value is 0--%>
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
															<ww:select name="project.status"
																id="statusSelect"
																value="project.status.name"
																list="@fi.hut.soberit.agilefant.model.Status@values()" listKey="name"
																listValue="getText('project.status.' + name())"	/>
														</td>
													</tr>
													<tr>
														<td>Default Overhead</td>
														<td></td>
														<td colspan="2"><ww:textfield size="10" name="project.defaultOverhead" /> / person / week</td>
													</tr>
						                			<tr>
														<td>Planned project size</td>
														<td></td>
														<td colspan="2"><ww:textfield size="10" id="project.backlogSize" name="project.backlogSize" /> (total man hours)
														</td>
													</tr>													
													<tr>
														<td>Start date</td>
														<td>*</td>
														<td colspan="2">
														<%--<ww:datepicker value="%{#start}" size="15"
															showstime="true" format="%{getText('webwork.datepicker.format')}"
															name="startDate" />--%>
														<aef:datepicker id="start_date" name="startDate" format="%{getText('webwork.shortDateTime.format')}" value="%{#start}" />
														</td>
													</tr>
													<tr>
														<td>End date</td>
														<td>*</td>
														<td colspan="2">
														<%--<ww:datepicker value="%{#end}" size="15"
															showstime="true" format="%{getText('webwork.datepicker.format')}"
															name="endDate" />--%>
														<aef:datepicker id="end_date" name="endDate" format="%{getText('webwork.shortDateTime.format')}" value="%{#end}" />
														</td>
													</tr>
													<tr>
														<td>Assigned Users</td>
														<td></td>
														<td colspan="2">
														<div>
									                        <a id="userChooserLink-editProject" href="#" class="assigneeLink">
									                            <img src="static/img/users.png"/>
									                            <span id="userListContainer-editProject">
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
	                    								<td colspan="2"><ww:textarea cols="70" rows="10" id="projectDescription"
	                        								name="project.description" value="${aef:nl2br(project.description)}" /></td>
	                								</tr>
													<tr>
														<td></td>
														<td></td>
														<c:choose>
															<c:when test="${projectId == 0}">
																<td><ww:submit value="Create" /></td>
															</c:when>
															<c:otherwise>
																<td><ww:submit value="Save" id="saveButton" /></td>
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
									
									<%---Link for entering a new hour entry---%>
									<aef:hourReporting id="hourReport"/>
									<c:if test="${hourReport == 'true'}">
										<div id="subItemHeader" style="border:none; border-top:1px solid #ccc; background: none;">
											<table cellpadding="0" cellspacing="0">
												<tbody>
													<tr>
				   										<td class="header">
				   											<ww:url id="createLink" action="ajaxCreateHourEntry" includeParams="none">
				   												<ww:param name="backlogId" value="${projectId}" />
				   											</ww:url>
					   										<ww:a cssClass="openCreateDialog openUserDialog" title="Log effort" href="%{createLink}">Log effort &raquo;</ww:a>
					   									</td>
													</tr>
												</tbody>
											</table>
										</div>
									</c:if>
									
								</div>
							</td>
						</tr>
					</tbody>
				</table>
		<table>
			<tr>
				<td>

								<c:if test="${projectId != 0}">
									<div class="subItems">
										<div class="subItemHeader">
										    <table cellspacing="0" cellpadding="0">
					            			    <tr>
					            			    	<td class="header">Themes <a id="addProjectBusinessTheme" href="#">Attach theme &raquo;</a></td>
												</tr>
											</table>
										</div>
										<div class="subItemContent">
										<div class="validateWrapper validateEmpty">
										<ww:form action="storeBacklogThemebinding" method="post">
										<ww:hidden name="backlogId" value="${project.id}"/>
										<input type="hidden" name="contextViewName" value="project" />
										<div class="businessThemeTableWrapper">
										<c:choose>
										<c:when test="${!empty project.businessThemeBindings}">
										<display:table htmlId="businessThemeTable" class="listTable" name="project.businessThemeBindings" id="row" requestURI="editIteration.action">

											<display:column sortable="true" title="Name" sortProperty="businessTheme.name">
												<span style="display: none;">${row.businessTheme.id}</span>
												<a style="cursor: pointer; color: #0055AA;" class="table_edit_edit">
													<c:out value="${row.businessTheme.name}"/>
												</a>												
											</display:column>
											
											<display:column sortable="true" sortProperty="boundEffort" title="Planned spending">
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
												<table id="businessThemeTable" style="display:none;" class="listTable">
													<tr><th class="sortable">Name</th><th class="sortable">Planned spending</th><th>Actions</th></tr>
												</table>
											</c:otherwise>
											</c:choose>
											<input id="backlogThemeSave" style="display: none; margin-left: 2px;" type="submit" value="Save" />
											</div>
											</ww:form>	
											</div>			
											</div>
											<c:if test="${!empty iterationThemes}">
											<div class="businessThemeTableWrapper">
											<h4>Iteration themes</h4>
												<display:table htmlId="businessThemeTable" class="listTable" name="iterationThemes" id="row" requestURI="editProject.action">
						
													<display:column sortable="true" title="Name" sortProperty="businessTheme.name">														
														<c:out value="${row.businessTheme.name}"/>														
													</display:column>
													
													<display:column sortable="true" sortProperty="boundEffort" title="Planned spending">
														<c:choose>
															<c:when test="${row.relativeBinding == true}">
																<c:out value="${row.boundEffort}"/>
																(<c:out value="${row.percentage}"/>%)
															</c:when>
															<c:otherwise><c:out value="${row.fixedSize}"/></c:otherwise>
														</c:choose>
													</display:column>
													<display:column sortable="true" title="Iteration" sortProperty="backlog.name">
														<ww:url id="editLink" action="editIteration" includeParams="none">
															<ww:param name="iterationId" value="${row.backlog.id}" />
														</ww:url>
														<ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=${project.id}">						
															<c:out value="${row.backlog.name}"/>
														</ww:a>
													</display:column>
												</display:table>
										    </div>
											</c:if>
											</div>
								</c:if>
								
				</td>
				</tr>
				<tr>
				<td>
					<c:if test="${project.id > 0}">
						<div class="subItems">
							<div class="subItemHeader">
								<table cellpadding="0" cellspacing="0">
									<tr>
					   					<td class="header">Iterations
					   						<ww:url id="createLink" action="ajaxCreateIteration" includeParams="none" >
						  						<ww:param name="projectId" value="${project.id}" />
					   						</ww:url>
					   						<ww:a
												href="%{createLink}&contextViewName=editProject&contextObjectId=${project.id}" cssClass="openCreateDialog openIterationDialog">Create new &raquo;</ww:a>
					   					</td>
									</tr>
								</table>
							</div>
							<c:if test="${!empty project.iterations}">
								<div class="subItemContent">
										<display:table class="listTable" name="project.iterations"
											id="row" requestURI="editProject.action">
											
											<display:column sortable="true" sortProperty="name" title="Name">
												<div style="overflow:hidden; width: 170px;">												
												<a class="nameLink" onclick="handleTabEvent('iterationTabContainer-${row.id}', 'iteration', ${row.id}, 1);">
													${aef:html(row.name)}
												</a>												
													<ww:url id="editLink" action="editIteration"
													includeParams="none">
													<ww:param name="iterationId" value="${row.id}" />
												</ww:url>
												<ww:a href="%{editLink}&contextViewName=editProject&contextObjectId=${project.id}">
													<img src="static/img/link.png" alt="Iteration page" title="Iteration page"/>
												</ww:a>												
												</div>
												<div id="iterationTabContainer-${row.id}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 0px;"></div>
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
												<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('iterationTabContainer-${row.id}', 'iteration', ${row.id}, 1);" />
												<ww:url id="deleteLink" action="deleteIteration"
													includeParams="none">
													<ww:param name="projectId" value="${project.id}" />
													<ww:param name="iterationId" value="${row.id}" />
												</ww:url>
												<ww:a
													href="%{deleteLink}&contextViewName=editProject&contextObjectId=${project.id}"
													onclick="return confirmDelete()"><img src="static/img/delete_18.png" alt="Delete" title="Delete" /></ww:a>
											</display:column>
										</display:table>
								</div>
							</c:if>
							</div>
							<div class="subItems">
							<div class="subItemHeader">
								<table cellpadding="0" cellspacing="0">
                    				<tr>
                       					<td class="header">Backlog items <ww:url
												id="createBacklogItemLink" action="ajaxCreateBacklogItem"
												includeParams="none">
												<ww:param name="backlogId" value="${project.id}" />
											</ww:url>
											<ww:a cssClass="openCreateDialog openBacklogItemDialog"
												href="%{createBacklogItemLink}&contextViewName=editProject&contextObjectId=${project.id}">Create new &raquo;</ww:a>
										</td>
									</tr>
								</table>
							</div>
							<c:if test="${!empty project.backlogItems}">
								<div class="subItemContent">
									<%@ include	file="./inc/_backlogList.jsp"%>
								</div>
							</c:if>
						</div>
						<c:if test="${!empty project.backlogItems}">
							<c:if test="${empty project.iterations}">
								<p>
									<img src="drawProjectChart.action?projectId=${project.id}" id="bigChart"
									   width="780" height="600" />
								</p>
							</c:if>
						</c:if>
					</c:if>
				</td>
			</tr>
		</table>

<%-- Hour reporting here - Remember to expel David H. --%>

<aef:hourReporting id="hourReport"></aef:hourReporting>
<c:if test="${hourReport == 'true' && projectId != 0}">
	<c:set var="myAction" value="editProject" scope="session" />
	<%@ include file="./inc/_hourEntryList.jsp"%>
</c:if> <%-- Hour reporting on --%>

<%@ include file="./inc/_footer.jsp"%>