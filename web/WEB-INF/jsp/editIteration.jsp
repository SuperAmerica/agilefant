<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<script type="text/javascript" src="static/js/generic.js"></script>

<c:choose>
	<c:when test="${iteration.id == 0}">
		<aef:bct projectId="${projectId}" />
	</c:when>
	<c:otherwise>
		<aef:bct iterationId="${iterationId}" />
	</c:otherwise>
</c:choose>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<aef:productList />

<ww:actionerror />
<ww:actionmessage />

<c:choose>
    <c:when test="${IterationId == 0}">
        <c:set var="new" value="New" scope="page" />
    </c:when>
    <c:otherwise>
        <c:set var="new" value="" scope="page" />
    </c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${iterationId == 0}">
		<h2>Create iteration</h2>
		
		<div id="editIterationForm"><ww:form
                            action="store${new}Iteration">
                            <ww:hidden name="iterationId" value="${iteration.id}" />
                            <ww:date name="%{iteration.getTimeOfDayDate(6)}" id="start"
                                format="%{getText('webwork.shortDateTime.format')}" />
                            <ww:date name="%{iteration.getTimeOfDayDate(18)}" id="end"
                                format="%{getText('webwork.shortDateTime.format')}" />
                            <c:if test="${iteration.id > 0}">
                                <ww:date name="%{iteration.startDate}" id="start"
                                    format="%{getText('webwork.shortDateTime.format')}" />
                                <ww:date name="%{iteration.endDate}" id="end"
                                    format="%{getText('webwork.shortDateTime.format')}" />
                            </c:if>

                            <table class="formTable">
                                <tr>
                                    <td>Name</td>
                                    <td>*</td>
                                    <td colspan="2"><ww:textfield size="60"
                                        name="iteration.name" /></td>
                                </tr>
                                <tr>
                                    <td>Description</td>
                                    <td></td>
                                    <td colspan="2"><ww:textarea cols="70" rows="10"
                                        name="iteration.description" /></td>
                                </tr>
                                <tr>
                                    <td>Project</td>
                                    <td>*</td>
                                    <td colspan="2"><select name="projectId" onchange="disableIfEmpty(this.value, ['createButton']);" >
                                        <option class="inactive" value="">(select project)</option>
                                        <c:forEach items="${productList}" var="product">
                                            <option value="" class="inactive productOption">${aef:out(product.name)}</option>
                                            <c:forEach items="${product.projects}" var="project">
                                                <c:choose>
                                                    <c:when test="${project.id == currentProjectId}">
                                                        <option selected="selected" value="${project.id}" class="projectOption"
                                                            title="${project.name}">${aef:out(project.name)}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${project.id}" title="${project.name}" class="projectOption">${aef:out(project.name)}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </c:forEach>
                                    </select></td>
                                </tr>
                                <tr>
                                    <td>Start date</td>
                                    <td>*</td>
                                    <td colspan="2"><ww:datepicker value="%{#start}" size="15"
                                        showstime="true"
                                        format="%{getText('webwork.datepicker.format')}"
                                        name="startDate" /></td>
                                </tr>
                                <tr>
                                    <td>End date</td>
                                    <td>*</td>
                                    <td colspan="2"><ww:datepicker value="%{#end}" size="15"
                                        showstime="true"
                                        format="%{getText('webwork.datepicker.format')}"
                                        name="endDate" /></td>
                                </tr>
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <c:choose>
                                        <c:when test="${iterationId == 0}">
                                            <td><ww:submit value="Create" id="createButton" /></td>
                                        </c:when>
                                        <c:otherwise> <!-- 20080520 Vesa: This branch is probably never entered, iterationId is always 0 here. -->
                                            <td><ww:submit value="Save" /></td>
                                            <td class="deleteButton"><ww:submit
                                                onclick="return confirmDelete()" action="deleteIteration"
                                                value="Delete" /></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </table>
                        </ww:form></div>
	</c:when>
	<c:otherwise>
		<h2><c:out value="${iteration.name}" /></h2>
		<table>
			<table>
				<tbody>
					<tr>
						<td>
						<div id="subItems" style="margin-top: 0">
						<div id="subItemHeader"><script type="text/javascript">
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
								<td class="header">Details <a href=""
									onclick="toggleDiv('editIterationForm'); toggleDiv('descriptionDiv'); return false;">Edit
								&raquo;</a></td>
								<td class="icons">
								<a href=""
									onclick="expandDescription(); return false;"> <img
									src="static/img/plus.png" width="18" height="18" alt="Expand"
									title="Expand" /> </a> <a href=""
									onclick="collapseDescription(); return false;"> <img
									src="static/img/minus.png" width="18" height="18"
									alt="Collapse" title="Collapse" /> </a></td>
							</tr>
						</table>
						</div>
						<div id="subItemContent">
						<div id="descriptionDiv" class="descriptionDiv"
							style="display: block;">
						<table class="infoTable" cellpadding="0" cellspacing="0">
							<tr>
								<th class="info1">Timeframe</th>
								<td class="info3"><c:out
									value="${iteration.startDate.date}.${iteration.startDate.month + 1}.${iteration.startDate.year + 1900}" />
								- <c:out
									value="${iteration.endDate.date}.${iteration.endDate.month + 1}.${iteration.endDate.year + 1900}" /></td>
								<td class="info4" rowspan="3">
								<a href="#bigChart"><img
									src="drawSmallChart.action?iterationId=${iteration.id}" /></a></td>
							</tr>

							<tr>
								<td colspan="2" class="description">${iteration.description}</td>
							</tr>

						</table>
						</div>
						<div id="editIterationForm" style="display: none;"><ww:form
							action="store${new}Iteration">
							<ww:hidden name="iterationId" value="${iteration.id}" />
							<ww:date name="%{iteration.getTimeOfDayDate(6)}" id="start"
								format="%{getText('webwork.shortDateTime.format')}" />
							<ww:date name="%{iteration.getTimeOfDayDate(18)}" id="end"
								format="%{getText('webwork.shortDateTime.format')}" />
							<c:if test="${iteration.id > 0}">
								<ww:date name="%{iteration.startDate}" id="start"
									format="%{getText('webwork.shortDateTime.format')}" />
								<ww:date name="%{iteration.endDate}" id="end"
									format="%{getText('webwork.shortDateTime.format')}" />
							</c:if>

							<table class="formTable">
								<tr>
									<td>Name</td>
									<td>*</td>
									<td colspan="2"><ww:textfield size="60"
										name="iteration.name" /></td>
								</tr>
								<tr>
									<td>Description</td>
									<td></td>
									<td colspan="2"><ww:textarea cols="70" rows="10"
										name="iteration.description" /></td>
								</tr>
								<tr>
                                    <td>Project</td>
                                    <td>*</td>
                                    <td colspan="2"><select name="projectId" onchange="disableIfEmpty(this.value, ['saveButton']);">
                                        <option class="inactive" value="">(select project)</option>
                                        <c:forEach items="${productList}" var="product">
                                            <option value="" class="inactive productOption">${aef:out(product.name)}</option>
                                            <c:forEach items="${product.projects}" var="project">
                                                <c:choose>
                                                    <c:when test="${project.id == currentProjectId}">
                                                        <option selected="selected" value="${project.id}" class="projectOption"
                                                            title="${project.name}">${aef:out(project.name)}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${project.id}" title="${project.name}" class="projectOption">${aef:out(project.name)}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </c:forEach>
                                    </select></td>
                                </tr>
								<tr>
									<td>Start date</td>
									<td>*</td>
									<td colspan="2"><ww:datepicker value="%{#start}" size="15"
										showstime="true"
										format="%{getText('webwork.datepicker.format')}"
										name="startDate" /></td>
								</tr>
								<tr>
									<td>End date</td>
									<td>*</td>
									<td colspan="2"><ww:datepicker value="%{#end}" size="15"
										showstime="true"
										format="%{getText('webwork.datepicker.format')}"
										name="endDate" /></td>
								</tr>
								<tr>
									<td></td>
									<td></td>
									<c:choose>
										<c:when test="${iterationId == 0}">
											<td><ww:submit value="Create" /></td>
										</c:when>
										<c:otherwise>
											<td><ww:submit value="Save" id="saveButton" /></td>
											<td class="deleteButton"><ww:submit
												onclick="return confirmDelete()" action="deleteIteration"
												value="Delete" /></td>
										</c:otherwise>
									</c:choose>
								</tr>
							</table>
						</ww:form></div>
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
		<td><c:if test="${iterationId != 0}">
			<div id="subItems">
			<div id="subItemHeader">
			    <table cellspacing="0" cellpadding="0">
                <tr>
                <td class="header">
                                Iteration goals <ww:url
				id="createIterationGoalLink" action="createIterationGoal"
				includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}" />
			</ww:url> <ww:a
				href="%{createIterationGoalLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
			</div>
			<c:if test="${!empty iteration.iterationGoals}">
			<aef:backlogHourEntrySums id="iterationGoalEffortSpent" groupBy="IterationGoal" target="${iteration}" />
				<div id="subItemContent">
				<p><display:table class="listTable"
					name="iteration.iterationGoals" id="row"
					requestURI="editIteration.action" defaultsort="1">

					<display:column sortable="true" title="Name" sortProperty="name"
						class="longNameColumn">
						<ww:url id="editLink" action="editIterationGoal"
							includeParams="none">
							<ww:param name="iterationGoalId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">
						${aef:html(row.name)}
					</ww:a>
					</display:column>

					<display:column sortable="true" sortProperty="description"
						title="Description">
					${aef:html(row.description)}
				</display:column>
					<display:column sortable="false" title="# of backlog items">
				  ${aef:html(fn:length(row.backlogItems))}
				</display:column>
				
				<display:column sortable="true" title="Effort left sum">
					<c:out value="${iterationGoalEffLeftSums[row.id]}" />
				</display:column>
				
				<display:column sortable="true" title="Original estimate sum">
					<c:out value="${iterationGoalOrigEstSums[row.id]}" />
				</display:column>
				<display:column sortable="false" title="Effort spent">
					<c:choose>
						<c:when test="${iterationGoalEffortSpent[row.id] != null}"><c:out value="${iterationGoalEffortSpent[row.id]}"/></c:when>
						<c:otherwise>&mdash;</c:otherwise>
					</c:choose>
				</display:column>
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteIterationGoal"
							includeParams="none">
							<ww:param name="iterationGoalId" value="${row.id}" />
							<ww:param name="iterationId" value="${iteration.id}" />
						</ww:url>
						<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
					</display:column>

				</display:table></p>
				</div>
			</c:if>

			<div id="subItemHeader">
			    <table cellspacing="0" cellpadding="0">
                <tr>
                <td class="header">Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
			</div>

			<c:if test="${!empty iteration.backlogItems}">
				<div id="subItemContent">
				<p><%@ include file="./inc/_backlogList.jsp"%>
				</p>
				</div>
			</c:if></div>
			<p><img src="drawChart.action?iterationId=${iteration.id}" id="bigChart" /></p>
		</c:if></td>
	</tr>
</table>

<%@ include file="./inc/_footer.jsp"%>