<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="teams" pageHierarchy="${pageHierarchy}" />

<aef:userList />

<h2>Teams</h2>
<p><ww:a href="createTeam.action">Create a new team &raquo;</ww:a></p>

<p><c:choose>
	<c:when test="${!empty teamList}">
	<display:table name="${teamList}" id="row"
			requestURI="listTeams.action" defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="name">
				<ww:url id="editLink" action="editTeam" includeParams="none">
					<ww:param name="teamId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{editLink}">
					<c:out value="${row.name}" />
				</ww:a>
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
