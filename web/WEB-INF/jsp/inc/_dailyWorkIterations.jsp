<%@ include file="./_taglibs.jsp"%>

<c:if test="${hourReport}">
	<aef:backlogHourEntrySums id="bliTotals" target="${backlog}" />
	<c:set var="totalSum" value="${null}" />
</c:if>


<c:if test="${!empty iterations}">

	<h2>All items assigned to <c:out value="${user.fullName}" /> from
	ongoing iterations</h2>

	<div id="subItems"><c:forEach items="${iterations}" var="it">


		<div id="subItemHeader">
		<table cellspacing="0" cellpadding="0">
        <tr>
        <td class="header">
		<ww:url id="parentActionUrl" action="editProduct" includeParams="none">
			<ww:param name="productId" value="${it.project.product.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<c:out value="${it.project.product.name}" />
		</ww:a> &nbsp;&mdash;&nbsp;
		
		<ww:url id="parentActionUrl" action="editProject" includeParams="none">
			<ww:param name="projectId" value="${it.project.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<c:out value="${it.project.name}" />
		</ww:a> &nbsp;&mdash;&nbsp;
		
		<ww:url id="parentActionUrl"
			action="editIteration" includeParams="none">
			<ww:param name="iterationId" value="${it.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<c:out value="${it.name}" />
		</ww:a>
		
		<%--<ww:url id="projectTypeActionUrl" action="editProjectType"
			includeParams="none">
			<ww:param name="projectTypeId" value="${it.project.projectType.id}" />
		</ww:url> <ww:a href="%{projectTypeActionUrl}&contextViewName=dailyWork">
			<u><c:out value="(${it.project.projectType.name})" /></u>
		</ww:a>--%>
		
		<c:out value="(${it.project.projectType.name})" />
		
		<c:if test="${!aef:isUserAssignedTo(it.project, user)}">
			<p style="color:#ff0000;">
			<img src="static/img/unassigned.png"
				title="The user has not been assigned to this project."
				alt="The user has not been assigned to this project." />
			<c:out value="${user.fullName}" /> has not been assigned to this project.
			</p>
		</c:if>
		</td>
		</tr>
		</table>
		</div>


		<div id="subItemContent">
		<p>
		<table class="dailyWorkBacklogItems">
			<tr>
				<td class="backlogItemList"><display:table class="dailyWorkIteration"
					name="${bliMap[it]}" id="item1"
					requestURI="${currentAction}.action">
					<c:if test="${hourReport}">
						<aef:backlogHourEntrySums id="bliTotals" target="${it}" />
					</c:if>

					<display:column sortable="true" sortProperty="name" title="Name"
						class="shortNameColumn">
						<ww:url id="editLink" action="editBacklogItem"
							includeParams="none">
							<ww:param name="backlogItemId" value="${item1.id}" />
						</ww:url>
						<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item1.name)}
		</ww:a></div>
					</display:column>

					<display:column sortable="true" title="Iteration goal"
						class="iterationGoalColumn" sortProperty="iterationGoal.name">
						<ww:url id="editLink" action="editIterationGoal"
							includeParams="none">
							<ww:param name="iterationGoalId"
								value="${item1.iterationGoal.id}" />
						</ww:url>
						<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item1.iterationGoal.name)}
		</ww:a></div>
					</display:column>

					<display:column sortable="true" title="Responsibles" class="responsibleColumn">
					<div><aef:responsibleColumn backlogItemId="${item1.id}"/></div>
					</display:column>
					
					<display:column sortable="true" defaultorder="descending"
						title="Priority">
						<ww:text name="backlogItem.priority.${item1.priority}" />
					</display:column>

					<display:column title="State" sortable="false" class="taskColumn">
						<c:set var="divId" value="${divId + 1}" scope="page" />
						<c:choose>
							<c:when test="${!(empty item1.tasks)}">
								<a href="javascript:toggleDiv(${divId});"
									title="Click to expand"> <c:out
									value="${fn:length(item1.tasks)}" /> tasks, <aef:percentDone
									backlogItemId="${item1.id}" />% done<br />
									
					<%-- Ugly solution to get both status bars displayed  
					<!-- This should be refactored at some time! 
					<!-- This is duplicated in: _backlogList.jsp 
					<!--						_dailyWorkIterations.jsp
					<!--						_dailyWorkProjects.jsp  
					<!--						_workInProgress.jsp
					<!--						editIterationGoal.jsp
					<!--  Note: the css div on the images are used to force the bars to be displayed nicely
					<!--  since it has been an persistent problem in the past,
					<!--  which was unable to correct from the source. --%>	
									<c:choose>
									<c:when test="${item1.state == 'NOT_STARTED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="notStarted" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'STARTED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="started" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'PENDING'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="pending" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'BLOCKED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="blocked" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'IMPLEMENTED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="implemented" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'DONE'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="done" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									</c:choose> 
									
									
									
								<aef:stateList backlogItemId="${item1.id}" id="tsl" /> <ww:url
									id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted" value="${tsl['notStarted']}" />
									<ww:param name="started" value="${tsl['started']}" />
									<ww:param name="pending" value="${tsl['pending']}" />
									<ww:param name="blocked" value="${tsl['blocked']}" />
									<ww:param name="implemented" value="${tsl['implemented']}" />
									<ww:param name="done" value="${tsl['done']}" />
								</ww:url> <div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div> </a>

								<aef:tasklist backlogItem="${item1}"
									contextViewName="${currentAction}"
									contextObjectId="${backlog.id}" divId="${divId}" hourReport="${hourReport}" />
							</c:when>
							<c:otherwise>
								<a href="javascript:toggleDiv(${divId});"
									title="Click to expand"> <ww:text
									name="task.state.${item1.state}" /><br />

								<c:choose>
									<c:when test="${item1.state == 'NOT_STARTED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="notStarted" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'STARTED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="started" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'PENDING'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="pending" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'BLOCKED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="blocked" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'IMPLEMENTED'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="implemented" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
									<c:when test="${item1.state == 'DONE'}">
										<ww:url id="imgUrl" action="drawExtendedBarChart"
											includeParams="none">
											<ww:param name="done" value="1" />
										</ww:url>
										<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
									</c:when>
								</c:choose> </a>
								<aef:tasklist backlogItem="${item1}"
									contextViewName="${currentAction}"
									contextObjectId="${backlog.id}" divId="${divId}" hourReport="${hourReport}" />
							</c:otherwise>
						</c:choose>
					</display:column>

					<display:column sortable="true" sortProperty="effortLeft"
						defaultorder="descending" title="Effort Left">
						<span style="white-space: nowrap"> <c:choose>
							<c:when test="${item1.effortLeft == null}">&mdash;
					</c:when>
							<c:otherwise>${item1.effortLeft}
					</c:otherwise>
						</c:choose> </span>
					</display:column>

					<display:column sortable="true" sortProperty="originalEstimate"
						defaultorder="descending" title="Orig. est.">
						<c:choose>
							<c:when test="${item1.originalEstimate == null}">&mdash;</c:when>
							<c:otherwise>
								<c:out value="${item1.originalEstimate}" />
							</c:otherwise>
						</c:choose>
					</display:column>
					<c:choose>
						<c:when test="${hourReport}">
							<display:column sortable="true" sortProperty="effortSpent" defaultorder="descending" title="Effort Spent">
								<span style="white-space: nowrap">
									<c:choose>
										<c:when test="${bliTotals[item1.id] == null}">&mdash;</c:when>
										<c:otherwise>
											<c:out value="${bliTotals[item1.id]}" />
											<c:set var="totalSum" value="${aef:calculateAFTimeSum(totalSum, bliTotals[item1.id])}" />
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
							<td><c:out value="${effortSums[it]}" /></td>
							<td><c:out value="${originalEstimates[it]}" /></td>
							<c:if test="${hourReport}">
								<td>
									<c:choose>
										<c:when test="${totalSum != null}">
											<c:out value="${totalSum}" />
											<c:remove var="totalSum"/>
										</c:when>
										<c:otherwise>
											0h
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
						</tr>
					</display:footer>

				</display:table></td>
				<td class="smallBurndownColumn">
					<div class="smallBurndown">
					<ww:url id="parentActionUrl"
						action="editIteration" includeParams="none">
						<ww:param name="iterationId" value="${it.id}" />
					</ww:url>
					<ww:a href="%{parentActionUrl}&contextViewName=dailyWork#bigChart">
						<img src="drawSmallChart.action?iterationId=${it.id}" />
					</ww:a>
					</div>
				</td>
			</tr>
		</table>

		

		</div>
		

	</c:forEach></div>

</c:if>

