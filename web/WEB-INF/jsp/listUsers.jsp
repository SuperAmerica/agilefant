<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>
<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}" />

<h2>Users</h2>
<p><a href="createUser.action">Create new &raquo;</a></p>

<aef:userList />
<p><display:table name="${userList}" id="row"
	requestURI="listUsers.action" defaultsort="1">
	<display:column sortable="true" title="Name" sortProperty="fullName">
		<ww:url id="editLink" action="editUser" includeParams="none">
			<ww:param name="userId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{editLink}">
				${aef:html(row.fullName)}
			</ww:a>
	</display:column>
	<display:column sortable="true" title="User ID" property="loginName" />
	<display:column sortable="true" title="Initials" property="initials" />
	<display:column sortable="true" title="Email" property="email" />
	<display:column sortable="false" title="Actions">
		<c:if test="${row.id != 1}">
			<ww:url id="deleteLink" action="deleteUser" includeParams="none">
				<ww:param name="userId" value="${row.id}" />
			</ww:url>
			<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
		</c:if>
	</display:column>
</display:table></p>

<h2>Teams</h2>
<p><ww:a href="createTeam.action">Create a new team &raquo;</ww:a></p>

<p><display:table name="${teamList}" id="row"
	requestURI="listUsers.action" defaultsort="1">
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
		<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
	</display:column>
</display:table></p>
<%@ include file="./inc/_footer.jsp"%>
