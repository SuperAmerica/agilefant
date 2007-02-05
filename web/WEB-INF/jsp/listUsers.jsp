<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu /> 


	<p>
	<a href="listActivityTypes.action">Activity and work types</a> | Users
	</p>




<p>
	<a href="createUser.action">Create new user</a>
</p>	
<p>Users found:</p>
<aef:userList/>
<p>
	<display:table name="${userList}" id="user" requestURI="listUsers.action">
		<display:column sortable="true" title="Id" property="id"/>
		<display:column sortable="true" title="Name" property="fullName"/>
		<display:column sortable="true" title="User ID" property="loginName"/>
		<display:column sortable="false" title="Actions">
			<ww:url id="editLink" action="editUser" includeParams="none">
				<ww:param name="userId" value="${user.id}"/>
			</ww:url>
			<ww:url id="deleteLink" action="deleteUser" includeParams="none">
				<ww:param name="userId" value="${user.id}"/>
			</ww:url>
			<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
		</display:column>
	</display:table>
</p>
<%@ include file="./inc/_footer.jsp" %>
