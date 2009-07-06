<%@ include file="./_taglibs.jsp"%>

<!-- context variable for story ajax to know its context -->
<c:set var="storyListContext" value="workInProgress" scope="session" />

<c:set var="dialogContext" value="storyWorkInProgress" scope="session" />

<h2>Started stories and tasks assigned to <c:out value="${user.fullName}" /></h2>


<c:if test="${!(empty storiesForUserInProgress)}">
<div class="subItems" id="subItems_storiesForUserInProgress">

<div class="subItemHeader">
<table cellspacing="0" cellpadding="0">
<tr>
<td class="header">
Stories/tasks
</td>
</tr>
</table>
</div>

<div class="subItemContent"><display:table
	name="storiesForUserInProgress" id="row"
	requestURI="dailyWork.action">	
	
	<!-- Display the backlog row name -->
	<display:column sortable="true" sortProperty="name" title="Name">				
		<div style="overflow:hidden; width: 170px; max-height: 3.7em;">
		<c:forEach items="${row.businessThemes}" var="businessTheme">
            <a href="#" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','story',${row.id},0, '${storyListContext}'); return false;">
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
		<a class="nameLink" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','storyWorkInProgress',${row.id},0,'${storyListContext}'); return false;">
			${aef:html(row.name)}
		</a>
		</div>
		<div id="storyTabContainer-${row.id}-${storyListContext}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 0px;"></div>		
	</display:column>

	<!-- Display the iteration goal -->
	<display:column sortable="true" title="Story"
		class="iterationGoalColumn" sortProperty="iterationGoal.name">
		<ww:url id="editLink" action="editIterationGoal" includeParams="none">
			<ww:param name="iterationGoalId" value="${row.iterationGoal.id}" />
		</ww:url>
		<div>		
		<!-- <ww:a href="%{editLink}&contextViewName=dailyWork"> -->
			${aef:html(row.iterationGoal.name)}
		<!-- </ww:a> -->
		</div>
	</display:column>
	
	<display:column sortable="true" title="Responsibles" class="responsibleColumn">
		<div><aef:responsibleColumn storyId="${row.id}"/></div>
	</display:column>

	<!-- Display the priority -->
	<display:column sortable="true" defaultorder="descending"
		title="Priority">
		<ww:text name="story.priority.${row.priority}" />
	</display:column>

	<!-- Display progress -->
	<display:column title="Progress" sortable="false" class="todoColumn">
		<aef:storyProgressBar story="${row}" storyListContext="${storyListContext}" dialogContext="${dialogContext}" hasLink="${true}"/>		
	</display:column>

	<!-- Display context -->
	<display:column sortable="false" title="Context" class="contextColumn">
		<div><c:forEach items="${row.parentBacklogs}" var="parent">
			<c:choose>
				<c:when test="${aef:isIteration(parent)}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<ww:url id="parentActionUrl" action="editIteration"
						includeParams="none">
						<ww:param name="iterationId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:when test="${aef:isProject(parent)}">
					<c:choose>
						<c:when test="${!aef:isUserAssignedTo(parent, user)}">
							<img src="static/img/unassigned.png"
								title="The user has not been assigned to this project."
								alt="The user has not been assigned to this project." />
						</c:when>
						<c:otherwise>
							&nbsp;&nbsp;&nbsp;							
						</c:otherwise>
					</c:choose>
					<ww:url id="parentActionUrl" action="editProject"
						includeParams="none">
						<ww:param name="projectId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:otherwise>
					<ww:url id="parentActionUrl" action="editProduct"
						includeParams="none">
						<ww:param name="productId" value="${parent.id}" />
					</ww:url>
				</c:otherwise>
			</c:choose>
			<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
				<c:out value="${parent.name}" />
			</ww:a>
			<c:if test="${aef:isProject(parent)}">
			<c:choose>
			<c:when test="${(!empty parent.projectType)}">
			(<c:out value="${parent.projectType.name}" />)
			</c:when>
			<c:otherwise>
			(undefined)
			</c:otherwise>
			</c:choose>
			</c:if>
			<br />
		</c:forEach></div>
	</display:column>

	<display:column title="Actions">
		<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','storyWorkInProgress',${row.id},0, '${storyListContext}'); return false;" />
		<img src="static/img/delete_18.png" alt="Delete" title="Delete" style="cursor: pointer;" onclick="deleteStory(${row.id}); return false;" />
	</display:column>

</display:table></div>
</div>
</c:if>
