<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="users" pageHierarchy="${pageHierarchy}" />

<h2>Users</h2>
<p><a href="createUser.action">Create new &raquo;</a></p>

<aef:userList />
<p><display:table name="${enabledUsers}" id="row"
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
	<display:column sortable="true" title="Week hours" property="weekHours"/>
	<display:column sortable="false" title="Actions">
		<c:if test="${row.id != 1}">
			<ww:url id="disableLink" action="disableUser" includeParams="none">
                <ww:param name="userId" value="${row.id}" />
            </ww:url>
            <ww:a href="%{disableLink}">
                <img src="static/img/disable_user.png" alt="Disable" title="Disable" />
            </ww:a>
			<ww:url id="deleteLink" action="deleteUser" includeParams="none">
				<ww:param name="userId" value="${row.id}" />
			</ww:url>
			<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
				<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
			</ww:a>
		</c:if>
	</display:column>
</display:table></p>

<h2>Disabled users</h2>

<p><display:table name="${disabledUsers}" id="row"
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
    <display:column sortable="true" title="Week hours" property="weekHours"/>
    <display:column sortable="false" title="Actions">
		<ww:url id="enableLink" action="enableUser" includeParams="none">
			<ww:param name="userId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{enableLink}">
			<img src="static/img/enable_user.png" alt="Enable" title="Enable" />
		</ww:a>
		<ww:url id="deleteLink" action="deleteUser" includeParams="none">
            <ww:param name="userId" value="${row.id}" />
        </ww:url>
        <ww:a href="%{deleteLink}" onclick="return confirmDelete()">
            <img src="static/img/delete_18.png" alt="Delete" title="Delete" />
        </ww:a>
	</display:column>
</display:table></p>

<%@ include file="../inc/_footer.jsp"%>
