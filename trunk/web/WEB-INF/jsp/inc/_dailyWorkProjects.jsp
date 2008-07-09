<%@ include file="./_taglibs.jsp"%>

<aef:hourReporting id="hourReport"></aef:hourReporting>


<c:if test="${!empty projects}">

<h2>All items assigned to <c:out value="${user.fullName}" /> from ongoing projects</h2>

<div id="subItems">

<c:forEach items="${projects}" var="pro">
	<c:if test="${hourReport}">
		<c:set var="totalSum" value="${null}" />
	</c:if>

	<div id="subItemHeader">
	<table cellspacing="0" cellpadding="0">
        <tr>
        <td class="header">
		<ww:url id="parentActionUrl" action="editProduct" includeParams="none">
			<ww:param name="productId" value="${pro.product.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<c:out value="${pro.product.name}" />
		</ww:a> &nbsp;&mdash;&nbsp;
		
		<ww:url id="parentActionUrl" action="editProject" includeParams="none">
			<ww:param name="projectId" value="${pro.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<c:out value="${pro.name}" />
		</ww:a>
		
		<c:out value="(${pro.projectType.name})" />
		</td>
		</tr>
		</table>
		</div>
		
		<c:if test="${!aef:isUserAssignedTo(pro, user)}">
			<p style="color:#ff0000;">
			<img src="static/img/unassigned.png"
				title="The user has not been assigned to this project."
				alt="The user has not been assigned to this project." />
			<c:out value="${user.fullName}" /> has not been assigned to this project.
			</p>
		</c:if>


<div id="subItemContent">
<p>

<table class="dailyWorkBacklogItems">
<tr>
				<td class="backlogItemList"><display:table class="dailyWorkProject"
					name="${bliMap[pro]}" id="row"
					requestURI="${currentAction}.action">

					<display:column sortable="true" sortProperty="name" title="Name"
						class="shortNameColumn">
						<ww:url id="editLink" action="editBacklogItem"
							includeParams="none">
							<ww:param name="backlogItemId" value="${row.id}" />
						</ww:url>
						<div>
						<ww:a href="#" id="dailyWorkProjectsList_${row.id}" onclick="openThemeBusinessModal('dailyWorkProjectsList_${row.id}', 'editBacklogItemBusinessThemes.action',${row.id},0); return false;">
							<img src="static/img/add_theme.png" alt="Edit themes" title="Edit themes" />
						</ww:a>
			
						<c:forEach items="${row.businessThemes}" var="businessTheme">
							<span class="businessTheme" title="${businessTheme.description}">
								<ww:a href="#" onclick="openThemeBusinessModal('dailyWorkProjectsList_${row.id}', 'editBacklogItemBusinessThemes.action',${row.id}, ${businessTheme.id}); return false;">
									${businessTheme.name}
								</ww:a>
							</span>
						</c:forEach>
						<ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(row.name)}
		</ww:a></div>
					</display:column>

					<display:column sortable="true" title="Responsibles" class="responsibleColumn">
					<div><aef:responsibleColumn backlogItemId="${row.id}"/></div>
					</display:column>

					<display:column sortable="true" defaultorder="descending"
						title="Priority">
						<ww:text name="backlogItem.priority.${row.priority}" />
					</display:column>

					<display:column title="State" sortable="false" class="taskColumn">
						<%@ include file="./_backlogItemStatusBar.jsp"%>
						<aef:tasklist backlogItem="${row}"
							contextViewName="${currentAction}"
							contextObjectId="${backlog.id}"
							divId="${divId}" />
					</display:column>

					<display:column sortable="true" sortProperty="effortLeft"
						defaultorder="descending" title="Effort Left<br/>">
						<span style="white-space: nowrap"> <c:choose>
							<c:when test="${row.effortLeft == null}">&mdash;
					</c:when>
							<c:otherwise>${row.effortLeft}
					</c:otherwise>
						</c:choose> </span>
					</display:column>

					<display:column sortable="true" sortProperty="originalEstimate"
						defaultorder="descending" title="Original estimate">
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
								<span style="white-space: nowrap">
									<c:choose>
										<c:when test="${row.effortSpent == null}">&mdash;</c:when>
										<c:otherwise>
											<c:out value="${row.effortSpent}" />
											<c:set var="totalSum" value="${aef:calculateAFTimeSum(totalSum, row.effortSpent)}" />
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
							<td><c:out value="${effortSums[pro]}" /></td>
							<td><c:out value="${originalEstimates[pro]}" /></td>
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
						<div>
						<ww:url id="parentActionUrl"
                            action="editProject" includeParams="none">
                            <ww:param name="projectId" value="${pro.id}" />
                        </ww:url>
						<ww:a href="%{parentActionUrl}&contextViewName=dailyWork#bigChart">
							<img src="drawSmallProjectChart.action?projectId=${pro.id}"/>
					    </ww:a>
						</div>
					</td>

	</tr>
</table>



</div>

</c:forEach>
</div>

</c:if>