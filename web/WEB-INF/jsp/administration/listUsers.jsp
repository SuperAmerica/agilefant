<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="users" pageHierarchy="${pageHierarchy}" />

<aef:openDialogs context="user" id="openUserTabs" />

<script type="text/javascript">
$(document).ready(function() {        
    <c:forEach items="${openUserTabs}" var="openUser">
        handleTabEvent("userTabContainer-${openUser[0]}", "user", ${openUser[0]}, ${openUser[1]});
    </c:forEach>
});
</script>

<h2>Users</h2>

	<div class="subItems" style="width: 545px;">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Enabled users					
					<a href="ajaxCreateUser.action" class="openCreateDialog openUserDialog" title="Create a new user">Create new &raquo;</a>
				</td>
			</tr>
		</table>
	</div>
	<div class="subItemContent"><aef:userList /> <display:table
			name="${enabledUsers}" id="row" requestURI="listUsers.action"
			defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="fullName" style="width: 150px;">
				<a class="nameLink"
					onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;">
				${aef:html(row.fullName)} </a>
				<div id="userTabContainer-${row.id}"
					style="overflow: visible; white-space: nowrap; width: 0px;"></div>
			</display:column>
			<display:column sortable="true" title="User ID" property="loginName" />
			<display:column sortable="true" title="Initials" property="initials" />
			<display:column sortable="true" title="Email" property="email" style="width: 125px;"/>
			<display:column sortable="true" title="Week hours"
				property="weekHours" />
			<display:column sortable="false" title="Actions" style="width: 70px;">
				<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;" />
				<c:if test="${row.id != 1}">
					<ww:url id="disableLink" action="disableUser" includeParams="none">
						<ww:param name="userId" value="${row.id}" />
					</ww:url>
					<ww:a href="%{disableLink}">
						<img src="static/img/disable.png" alt="Disable" title="Disable" />
					</ww:a>
					<ww:url id="deleteLink" action="deleteUser" includeParams="none">
						<ww:param name="userId" value="${row.id}" />
					</ww:url>
					<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
						<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
					</ww:a>
				</c:if>
			</display:column>
		</display:table>
		</div>

		</div>

	<c:if test="${(!empty disabledUsers)}">
	<div class="subItems" style="width: 545px;">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Disabled users
				</td>
			</tr>
		</table>
	</div>
	<div class="subItemContent">	
	<display:table name="${disabledUsers}" id="row"
    requestURI="listUsers.action" defaultsort="1">
    <display:column sortable="true" title="Name" sortProperty="fullName" style="width: 150px;">
        <a class="nameLink" onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;">
			${aef:html(row.fullName)}
		</a>							
		<div id="userTabContainer-${row.id}" style="overflow:visible; white-space: nowrap; width: 0px;"></div>
    </display:column>
    <display:column sortable="true" title="User ID" property="loginName" />
    <display:column sortable="true" title="Initials" property="initials" />        	
    <display:column sortable="true" title="Email" property="email" style="width: 150px;"/>
    <display:column sortable="true" title="Week hours" property="weekHours"/>
    <display:column sortable="false" title="Actions" style="width: 70px;">
    	<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('userTabContainer-${row.id}', 'user', ${row.id}, 0); return false;" />
		<ww:url id="enableLink" action="enableUser" includeParams="none">
			<ww:param name="userId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{enableLink}">
			<img src="static/img/enable.png" alt="Enable" title="Enable" />
		</ww:a>
		<ww:url id="deleteLink" action="deleteUser" includeParams="none">
            <ww:param name="userId" value="${row.id}" />
        </ww:url>
        <ww:a href="%{deleteLink}" onclick="return confirmDelete()">
            <img src="static/img/delete_18.png" alt="Delete" title="Delete" />
        </ww:a>
	</display:column>
	</display:table>	
	</div>	
	</div>
	</c:if>

<%@ include file="../inc/_footer.jsp"%>
