<%@ include file="./_taglibs.jsp"%>

<!-- context variable for backlog item ajax to know its context -->
<c:set var="bliListContext" value="dailyWorkIterations" scope="session" />

<c:set var="dialogContext" value="bliDWInterations" scope="session" />

<aef:openDialogs context="bliDWInterations" id="openBacklogItemTabs" />

<c:if test="${hourReport}">
	<c:set var="totalSum" value="${null}" />
</c:if>

<script language="javascript" type="text/javascript">

$(document).ready(function() {        
    <c:forEach items="${openBacklogItemTabs}" var="openBacklogItem">
        handleTabEvent("backlogItemTabContainer-${openBacklogItem[0]}-${bliListContext}", "bliDWInterations", ${openBacklogItem[0]}, ${openBacklogItem[1]} ,'${bliListContext}');
    </c:forEach>
});

</script>

<c:if test="${!empty iterations}">

	<h2>All items assigned to <c:out value="${user.fullName}" /> from
	ongoing iterations</h2>

	<div class="subItems"><c:forEach items="${iterations}" var="it">

		<div class="subItemHeader">
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

		<div class="subItemContent">
		<table class="dailyWorkBacklogItems">
			<tr>
				<td class="backlogItemList"><display:table class="dailyWorkIteration"
					name="${bliMap[it]}" id="row"
					requestURI="${currentAction}.action">
					<c:if test="${hourReport}">
						<aef:backlogHourEntrySums id="bliTotals" target="${it}" />
					</c:if>					

					<display:column sortable="true" sortProperty="name" title="Name">						
						<div style="overflow:hidden; width: 170px;">						
						<c:forEach items="${row.businessThemes}" var="businessTheme">
                            <a href="#" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bli',${row.id},3, '${bliListContext}');">
                                <span class="businessTheme" title="${businessTheme.description}"><c:out value="${businessTheme.name}"/></span>
                            </a>
                        </c:forEach>						
						<a class="bliNameLink" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bli',${row.id},0,'${bliListContext}');">
							${aef:html(row.name)}
						</a>
						</div>
						<div id="backlogItemTabContainer-${row.id}-${bliListContext}" style="overflow:visible; white-space: nowrap; width: 0px;"></div>
					</display:column>

					<display:column sortable="true" title="Iteration goal"
						class="iterationGoalColumn" sortProperty="iterationGoal.name">
						<ww:url id="editLink" action="editIterationGoal"
							includeParams="none">
							<ww:param name="iterationGoalId"
								value="${row.iterationGoal.id}" />
						</ww:url>
						<div>
						<ww:a href="%{editLink}&contextViewName=dailyWork">
							${aef:html(row.iterationGoal.name)}
						</ww:a>
						</div>
					</display:column>

					<display:column sortable="true" title="Responsibles" class="responsibleColumn">
					<div><aef:responsibleColumn backlogItemId="${row.id}"/></div>
					</display:column>
					
					<display:column sortable="true" defaultorder="descending"
						title="Priority">
						<ww:text name="backlogItem.priority.${row.priority}" />
					</display:column>

					<display:column title="Progress" sortable="false" class="taskColumn">
						<%@ include file="./_backlogItemStatusBar.jsp"%>						
					</display:column>

					<display:column sortable="true" sortProperty="effortLeft"
						defaultorder="descending" title="Effort Left">
						<c:choose>
							<c:when test="${row.effortLeft == null}">&mdash;
					</c:when>
							<c:otherwise>${row.effortLeft}
					</c:otherwise>
						</c:choose> 
					</display:column>

					<display:column sortable="true" sortProperty="originalEstimate"
						defaultorder="descending" title="Orig. est.">
						<c:choose>
							<c:when test="${row.originalEstimate == null}">&mdash;</c:when>
							<c:otherwise>
								<c:out value="${row.originalEstimate}" />
							</c:otherwise>
						</c:choose>
					</display:column>
					<c:choose>
						<c:when test="${hourReport}">
							<display:column sortable="true" sortProperty="effortSpent" defaultorder="descending" title="Effort Spent">
								<c:choose>
									<c:when test="${row.effortSpent == null}">&mdash;</c:when>
									<c:otherwise>
										<c:out value="${row.effortSpent}" />
										<c:set var="totalSum" value="${aef:calculateAFTimeSum(totalSum, row.effortSpent)}" />
									</c:otherwise>
								</c:choose>
							</display:column>
						</c:when>
						<c:otherwise>
			
						</c:otherwise>
					</c:choose>

					<display:column title="Actions">
						<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bliDWInterations',${row.id},0, '${bliListContext}');" />
						<img src="static/img/delete_18.png" alt="Delete" title="Delete" style="cursor: pointer;" onclick="deleteBacklogItem(${row.id}); return false;" />
					</display:column>

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

