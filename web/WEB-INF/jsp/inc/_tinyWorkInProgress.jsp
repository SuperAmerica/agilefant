<%@ include file="./_taglibs.jsp"%>

<aef:openDialogs context="storyWorkInProgress" id="openStoryTabs" />

<!-- context variable for backlog item ajax to know its context -->
<c:set var="storyListContext" value="workInProgress" scope="session" />

<c:set var="dialogContext" value="storyWorkInProgress" scope="session" />

<script language="javascript" type="text/javascript">

$(document).ready(function() {        
    <c:forEach items="${openStoryTabs}" var="openStory">
        handleTabEvent("storyTabContainer-${openStory[0]}-${storyListContext}", "storyWorkInProgress", ${openStory[0]}, ${openStory[1]}, '${storyListContext}');
    </c:forEach>
});

</script>

<c:if test="${!(empty storiesForUserInProgress)}">
<div class="subItems" id="subItems_storiesForUserInProgress">

<div class="subItemContent">

<display:table name="storiesForUserInProgress" id="row" requestURI="dailyWork.action">	
	
	<!-- Display the backlog row name -->
	<display:column sortable="true" sortProperty="name" title="Name">				
		<div style="overflow:hidden; width: 150px; max-height: 3.7em;">
		<c:forEach items="${row.businessThemes}" var="businessTheme">
            <a href="#" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','story',${row.id},3, '${storyListContext}'); return false;">
                <c:choose>
                   <c:when test="${businessTheme.global}">
                      <span class="businessTheme globalThemeColors" title="${businessTheme.description}"><c:out value="${businessTheme.name}"/></span>
                   </c:when>
                   <c:otherwise>
                      <span class="businessTheme" title="${businessTheme.description}"><c:out value="${businessTheme.name}"/></span>   
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

	<!-- Display progress -->
	<display:column title="Progress" sortable="false" class="progressColumn">
		<div class="tinyfier">
			<aef:storyProgressBar story="${row}" storyListContext="${storyListContext}" dialogContext="${dialogContext}" hasLink="${true}"/>
		</div>		
	</display:column>

	<!-- Display estimates -->
	<display:column title="Estimate" sortable="false" class="todoColumn">
		<div style="overflow:hidden; width: 55px; max-height: 3.7em;">
		<c:choose>	
			<c:when test="${row.effortLeft == null}">&mdash;</c:when>
			<c:otherwise><span style="color: #000">${row.effortLeft}</span></c:otherwise>
		</c:choose>	
		<br/>
		<c:choose>
			<c:when test="${row.originalEstimate == null}">&mdash;</c:when>
			<c:otherwise>
				<span style="color: #999">
				<c:out value="${row.originalEstimate}" />
				</span>
			</c:otherwise>
		</c:choose>
		</div>	
	</display:column>

</display:table>
</div>
</div>
</c:if>