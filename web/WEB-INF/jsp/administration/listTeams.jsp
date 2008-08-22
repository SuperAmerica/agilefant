<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="teams" pageHierarchy="${pageHierarchy}" />

<aef:openDialogs context="team" id="openTeamTabs" />

<script type="text/javascript">
$(document).ready(function() {        
    <c:forEach items="${openTeamTabs}" var="openTeam">
        handleTabEvent("teamTabContainer-${openTeam[0]}", "team", ${openTeam[0]}, ${openTeam[1]});
    </c:forEach>
});
</script>

<aef:userList />

<h2>Teams</h2>
<p><ww:a href="ajaxCreateTeam.action" cssClass="openCreateDialog openTeamDialog">Create a new team &raquo;</ww:a></p>

<p><c:choose>
	<c:when test="${!empty teamList}">
	<display:table name="${teamList}" id="row"
			requestURI="listTeams.action" defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="name">
				<a class="nameLink"
					onclick="handleTabEvent('teamTabContainer-${row.id}', 'team', ${row.id}, 0); return false;">
				${aef:html(row.name)} </a>
				<div id="teamTabContainer-${row.id}"
					style="overflow: visible; white-space: nowrap; width: 0px;"></div>
			</display:column>

			<display:column title="# of users" sortable="true">
				<c:out value="${row.numberOfUsers}" />
			</display:column>

			<display:column title="Actions" sortable="false">
				<ww:url id="deleteLink" action="deleteTeam" includeParams="none">
					<ww:param name="teamId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
				    <img src="static/img/delete_18.png" alt="Edit" title="Edit" />
				</ww:a>
			</display:column>
		</display:table>
	</c:when>
	<c:otherwise>
No teams.
</c:otherwise>
</c:choose></p>
<%@ include file="../inc/_footer.jsp"%>
