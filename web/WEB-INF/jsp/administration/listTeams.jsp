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

	<div class="subItems" style="width: 525px;"id="subItems_teamList">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Teams					
					<ww:a href="ajaxCreateTeam.action" cssClass="openCreateDialog openTeamDialog" onclick="return false;">Create new &raquo;</ww:a>
				</td>
			</tr>
		</table>
	</div>
	<div class="subItemContent">
	<c:if test="${(!empty teamList)}">
	<display:table name="${teamList}" id="row" requestURI="listTeams.action" >
			
			<display:column sortable="true" title="Name" sortProperty="name" style="width: 395px;">
			 <div style="width: 350px;">
				<a class="nameLink"
					onclick="handleTabEvent('teamTabContainer-${row.id}', 'team', ${row.id}, 0); return false;">
				${aef:html(row.name)} </a>
				<div id="teamTabContainer-${row.id}" class="tabContainer"
					style="overflow: visible; white-space: nowrap; width: 0px;"></div>
			 </div>
			</display:column>

			<display:column title="# of users" sortable="true">
				<c:out value="${row.numberOfUsers}" />
			</display:column>

			<display:column title="Actions" sortable="false">
				<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('teamTabContainer-${row.id}', 'team', ${row.id}, 0); return false;" />
				<ww:url id="deleteLink" action="deleteTeam" includeParams="none">
					<ww:param name="teamId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
				    <img src="static/img/delete_18.png" alt="Edit" title="Edit" />
				</ww:a>
			</display:column>
			
		</display:table>
		</c:if>
	</div>

	</div>

<%@ include file="../inc/_footer.jsp"%>
