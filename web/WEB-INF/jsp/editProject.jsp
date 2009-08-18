<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>


<aef:projectTypeList id="projectTypes"/>

<aef:currentBacklog backlogId="${project.id}"/>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" title="${project.name}" menuContextId="${project.id}"/>
<ww:actionerror />
<ww:actionmessage />
<script type="text/javascript">
var agilefantTimesheetsEnabled = ${settings.hourReportingEnabled};
</script>

<%--
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

		<aef:productList />
			<h2><c:out value="${project.name}" /></h2>
				<table>
					<tbody>
						<tr>
							<td>
								<div class="subItems" style="margin-top: 0" id="subItems_editProjectDetails">
									<div class="subItemHeader">
										<script type="text/javascript">
											function editProject() {
												toggleDiv('editProjectForm'); toggleDiv('descriptionDiv'); showWysiwyg('projectDescription'); return false;
											}
										</script>
										  <table cellspacing="0" cellpadding="0">
											<tr>
                                                <td class="iconsbefore">
						                        </td>
												<td class="header">Details</td>
												<td class="icons">
												<table cellpadding="0" cellspacing="0">
												<tr>
												
                                                <c:if test="${settings.hourReportingEnabled}">
                                                <td>
                                                        <ww:url id="createLink" action="ajaxCreateHourEntry" includeParams="none">
                                                            <ww:param name="backlogId" value="#attr.project.id" />
                                                        </ww:url>
                                                    <ww:a cssClass="openCreateDialog openUserDialog logEffortLink"
                                                    onclick="return false;" title="Log effort" href="%{createLink}">
												    </ww:a>
												    </td>
                                                  </c:if>
                                               <td>
                                                  <a href="#" onclick="editProject(); return false;"
                                                    class="editLink" title="Edit project details" /> 
                                                </td>
                                                </tr>
                                                </table>
						                        </td>
											</tr>
										</table>
									</div>
                  
									<div class="subItemContent">
										<div id="descriptionDiv" class="descriptionDiv" style="display: block;">
											<table class="infoTable" cellpadding="0" cellspacing="0">
												<tr>
													<th class="info1"><ww:text name="general.uniqueId"/></th>
													<td class="info3"><aef:quickReference item="${project}" /></td>
													<td class="info4" rowspan="8">
                            <div id="projectMetrics">
                              <%@ include file="./inc/projectMetrics.jsp"%>
                            </div>
                          </td>
												</tr>
												<tr>
								    				<th class="info1">Status</th>
								    				<td class="info3" ondblclick="return editProject();">
								    				<c:choose>
														<c:when test="${project.status == 'GREEN'}">
															<img src="static/img/status-green.png" alt="Green" title="Green"/>
														</c:when>
														<c:when test="${project.status == 'YELLOW'}">
															<img src="static/img/status-yellow.png" alt="Yellow" title="Yellow"/>
														</c:when>
														<c:when test="${project.status == 'RED'}">
															<img src="static/img/status-red.png" alt="Red" title="Red"/>
														</c:when>
														<c:when test="${project.status == 'GREY'}">
															<img src="static/img/status-grey.png" alt="Grey" title="Grey"/>
														</c:when>
														<c:when test="${project.status == 'BLACK'}">
															<img src="static/img/status-black.png" alt="Black" title="Black"/>
														</c:when>
													</c:choose>
													<aef:text name="project.status.${project.status}" />
								    				</td>
								    				<td></td>						
												</tr>
                        <%--
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
                        --%>
                        <%--
												<c:if test="${(project.defaultOverhead != null) && (project.defaultOverhead.time > 0)}">
												<tr>
								    				<th class="info1">Baseline load</th>
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
											     </c:if>
                           
                           		
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
												<tr><%--
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
	                                                        				<c:out value="${responsible.initials}" />, 
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
	                                    			</td>
                                            
												</tr>
												<tr>
								    				<td colspan="2" class="description">
								    				<div class="backlogDescription">${project.description}</div></td>
												</tr>
											</table>
										</div>
										<div id="editProjectForm" style="display: none;" class="validateWrapper validateProject">
											<form id="projectEditForm" action="ajax/storeProject.action" method="post">
												<input type="hidden" id="editProject-projectId" name="projectId" value="${project.id}" />
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
                          <%--
													<tr>
														<td>Baseline load</td>
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
														<aef:datepicker id="start_date" name="startDate" format="%{getText('struts.shortDateTime.format')}" value="${start}" />
														</td>
													</tr>
													<tr>
														<td>End date</td>
														<td>*</td>
														<td colspan="2">
														<aef:datepicker id="end_date" name="endDate" format="%{getText('struts.shortDateTime.format')}" value="${end}" />
														</td>
													</tr>
                          <%--
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
											                                <input type="hidden" name="assignments['${ass.user.id}'].personalLoad" value="${ass.personalLoad}"/>
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
	                    								<td colspan="2"><ww:textarea cols="70" rows="10"
                                      id="projectDescription" name="project.description"></ww:textarea></td>
	                								</tr>
													<tr>
														<td></td>
														<td></td>
														<c:choose>
															<c:when test="${project.id == 0}">
																<td><ww:submit value="Create"
															    disabled="disabled" cssClass="undisableMe"/></td>
															</c:when>
															<c:otherwise>
																<td><ww:submit value="Save" id="saveButton" /></td>
																<td class="deleteButton"><ww:submit
																	action="deleteProject"
																	disabled="disabled" cssClass="undisableMe"
																	value="Delete" /></td>
															</c:otherwise>
														</c:choose>
													</tr>
												</table>
											</form>
										</div>
									</div>
									
								</div>
							</td>
						</tr>
					</tbody>
				</table>
        --%>
        
        <%--
				<tr>
				<td>
						<div class="subItems" id="subItems_editProjectIterations">
							<div class="subItemHeader">
								<table cellpadding="0" cellspacing="0">
									<tr>
					   					<td class="header">Iterations</td>
					   					<td class="icons">
					   					<table cellpadding="0" cellspacing="0">
					   					<tr>
					   					<td>
					   						<ww:url id="createLink" namespace="ajax" action="createIteration" includeParams="none" >
						  						<ww:param name="projectId" value="#attr.project.id" />
					   						</ww:url>
					   						<ww:a
												href="%{createLink}" cssClass="openCreateDialog openIterationDialog"
												onclick="return false;" title="Create a new iteration">
                                            </ww:a>
                                        </td>
                                        </tr>
                                        </table>
					   					</td>
									</tr>
								</table>
							</div>
							<c:if test="${!empty project.children}">
								<div class="subItemContent">
										<display:table class="listTable" name="project.children"
											id="row" requestURI="editProject.action">
											
											<display:column sortable="true" sortProperty="name" title="Name">
												<div style="overflow:hidden; width: 170px;">																								
													<ww:url id="editLink" action="editIteration"
													includeParams="none">
														<ww:param name="iterationId" value="#attr.row.id" />
														<ww:param name="contextObjectId">${project.id}</ww:param>
													</ww:url>
													<ww:a href="%{editLink}&contextViewName=editProject">
														${aef:html(row.name)}
													</ww:a>												
												</div>
												<div id="iterationTabContainer-${row.id}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 0px;"></div>
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
													<ww:param name="projectId" value="#attr.project.id" />
													<ww:param name="iterationId" value="#attr.row.id" />
													<ww:param name="contextObjectId">${project.id}</ww:param>
												</ww:url>
												<ww:a
													href="%{deleteLink}&contextViewName=editProject"
													onclick="return confirmDelete()"><img src="static/img/delete_18.png" alt="Delete" title="Delete" /></ww:a>
											</display:column>
										</display:table>
								</div>
							</c:if>
							</div>
				</td>
			</tr>
		</table>
--%>

<div class="backlogInfo" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <li class=""><a href="#backlogAssignees"><span><img
    alt="Edit" src="static/img/team.png" /> Assignees</span></a></li>
  <c:if test="${settings.hourReportingEnabled}">
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> Spent effort</span></a></li>
  </c:if>
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> History</span></a></li>
</ul>
</ul>

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
<div class="details" id="backlogAssignees"></div>
<div class="details" id="backlogSpentEffort"></div>

</div>


<script type="text/javascript">
$(document).ready(function() {
  $("#backlogInfo").tabs();
  var controller = new ProjectController({
    id: ${project.id},
    projectDetailsElement: $("#backlogDetails"),
    assigmentListElement: $("#backlogAssignees"),
    storyListElement: $('#stories')
  });
});
</script>

<script type="text/javascript" src="static/js/dynamics/view/ViewPart.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/DynamicView.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Table.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Row.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Cell.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/RowActions.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableConfiguration.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Toggle.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableCaption.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableCellEditors.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/decorators.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/SplitPanel.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Tabs.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Buttons.js"></script>


<script type="text/javascript" src="static/js/dynamics/model/CommonModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/BacklogModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/IterationModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/ProjectModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/StoryModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/TaskModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/UserModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/comparators.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/AssignmentModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/HourEntryModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/ModelFactory.js"></script>

<script type="text/javascript" src="static/js/dynamics/controller/CommonController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/BacklogController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/IterationController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/ProjectController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/StoryController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/TaskController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/TasksWithoutStoryController.js"></script>

<script type="text/javascript" src="static/js/dynamics/Dynamics.events.js"></script>

<script type="text/javascript" src="static/js/utils/ArrayUtils.js"></script>
<script type="text/javascript" src="static/js/utils/Configuration.js"></script>

<script type="text/javascript" src="static/js/autocomplete/autocompleteSearchBox.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteSelectedBox.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteBundle.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteDataProvider.js"></script>
<script type="text/javascript" src="static/js/autocomplete/autocompleteDialog.js"></script>

<form onsubmit="return false;"><div id="stories" style="min-width: 800px; width: 98%;">&nbsp;</div></form>


<p><img src="drawProjectBurnup.action?backlogId=${project.id}"
						id="bigChart" width="780" height="600" /></p>

<%-- Hour reporting here - Remember to expel David H. --%>

<c:if test="${settings.hourReportingEnabled && project.id != 0}" >
	<c:set var="myAction" value="editProject" scope="session" />
	<%@ include file="./inc/_hourEntryList.jsp"%>
</c:if> 

<%-- Hour reporting on --%>

<%@ include file="./inc/_footer.jsp"%>