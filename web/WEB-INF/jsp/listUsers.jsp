<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}"/>

<h2>Users</h2>
<p>  
	<a href="createUser.action">Create new &raquo;</a>
</p>	

<aef:userList/>
<p>
	<display:table name="${userList}" id="row" requestURI="listUsers.action">
		<display:column sortable="true" title="Id" property="id"/>
		<display:column sortable="true" title="Name">
			<ww:url id="editLink" action="editUser" includeParams="none">
				<ww:param name="userId" value="${row.id}"/>
			</ww:url>
			<ww:a href="%{editLink}">
				${aef:html(row.fullName)}
			</ww:a>
		</display:column>
		<display:column sortable="true" title="User ID" property="loginName"/>
		<display:column sortable="true" title="Email" property="email"/>
		<display:column sortable="false" title="Actions">
			<!-- <ww:url id="editLink" action="editUser" includeParams="none">
				<ww:param name="userId" value="${row.id}"/>
			</ww:url>-->
			<c:if test="${row.id != 1}">
			<ww:url id="deleteLink" action="deleteUser" includeParams="none">
				<ww:param name="userId" value="${row.id}"/>
			</ww:url>
			<!-- <ww:a href="%{editLink}">Edit</ww:a>| -->
			<ww:a href="%{deleteLink}">Delete</ww:a>
			</c:if>
		</display:column>
	</display:table>
</p>
<%@ include file="./inc/_footer.jsp" %>
