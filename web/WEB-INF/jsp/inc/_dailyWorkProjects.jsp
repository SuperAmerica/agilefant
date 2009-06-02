<%@ include file="./_taglibs.jsp"%>

<aef:hourReporting id="hourReport"></aef:hourReporting>

<aef:openDialogs context="storyDWProjects" id="openStoryTabs" />

<!-- context variable for story ajax to know its context -->
<c:set var="storyListContext" value="dailyWorkProjects" scope="session" />

<c:set var="dialogContext" value="storyDWProjects" scope="session" />

<script language="javascript" type="text/javascript">

$(document).ready(function() {        
    <c:forEach items="${openStoryTabs}" var="openStory">
        handleTabEvent("storyTabContainer-${openStory[0]}-${storyListContext}", "storyDWProjects", ${openStory[0]}, ${openStory[1]}, '${storyListContext}');
    </c:forEach>
});

</script>

<c:if test="${!empty projects}">

<h2>All stories assigned to <c:out value="${user.fullName}" /> from ongoing projects</h2>

<div class="subItems" id="subItems_dailyWorkProjectItems">

<c:forEach items="${projects}" var="pro">
	<c:if test="${hourReport}">
		<c:set var="totalSum" value="${null}" />
	</c:if>

	<div class="subItemHeader">
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
		<c:choose>
		<c:when test="${(!empty pro.projectType)}">
			<c:out value="(${pro.projectType.name})" />
		</c:when>
		<c:otherwise>
			(undefined)
		</c:otherwise>
		</c:choose>
		</td>
        <td class="icons">
          <table cellpadding="0" cellspacing="0">
          <tr>
          <c:if test="${hourReport}">
          <td>
            <ww:url id="createLink" action="ajaxCreateHourEntry" includeParams="none">
                <ww:param name="backlogId" value="${pro.id}" />
            </ww:url>
            <ww:a cssClass="openCreateDialog openUserDialog logEffortLink" onclick="return false;" title="Log effort to ${pro.name}" href="%{createLink}">
            </ww:a>
            </td>
          </c:if>
          <td>
          <ww:url id="createStoryLink" action="ajaxCreateStory" includeParams="none">
            <ww:param name="backlogId" value="${pro.id}" />
          </ww:url>
          <ww:a cssClass="openCreateDialog openStoryDialog"
                onclick="return false;" title="Create a new story"
                href="%{createStoryLink}">
          </ww:a>
          </td>
          
          </tr>
          </table>
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


<div class="subItemContent">

<table class="dailyWorkStorys">
<tr>
				<td class="storyList"><display:table class="dailyWorkProject"
					name="${storyMap[pro]}" id="row"
					requestURI="${currentAction}.action">					

					<display:column sortable="true" sortProperty="name" title="Name" >						
						<div style="overflow:hidden; width: 170px; max-height: 3.7em;">
						<c:forEach items="${row.businessThemes}" var="businessTheme">
                            <a href="#" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','storyDWProjects',${row.id},0, '${storyListContext}'); return false;">
                                <c:choose>
	                                <c:when test="${businessTheme.global}">
	                                   <span class="businessTheme globalThemeColors" title="${aef:stripHTML(businessTheme.description)}"><c:out value="${businessTheme.name}"/></span>
	                                </c:when>
	                                <c:otherwise>
	                                   <span class="businessTheme" title="${aef:stripHTML(businessTheme.description)}"><c:out value="${businessTheme.name}"/></span>   
	                               </c:otherwise>
	                           </c:choose>
                            </a>
                        </c:forEach>												
						<a class="nameLink" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','storyDWProjects',${row.id},0, '${storyListContext}'); return false;">
							${aef:html(row.name)}
						</a>
						</div>
						<div id="storyTabContainer-${row.id}-${storyListContext}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 0px;"></div>
					</display:column>

					<display:column sortable="true" title="Responsibles" class="responsibleColumn">
						<div><aef:responsibleColumn storyId="${row.id}"/></div>
					</display:column>

					<display:column sortable="true" defaultorder="descending"
						title="Priority">
						<ww:text name="story.priority.${row.priority}" />
					</display:column>

					<display:column title="Progress" sortable="false" class="todoColumn">
						<aef:storyProgressBar story="${row}" storyListContext="${storyListContext}" dialogContext="${dialogContext}" hasLink="${true}"/>												
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

					<display:column title="Actions">
						<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','storyDWProjects',${row.id},0, '${storyListContext}'); return false;" />
						<img src="static/img/delete_18.png" alt="Delete" title="Delete" style="cursor: pointer;" onclick="deleteStory(${row.id}); return false;" />
					</display:column>

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
