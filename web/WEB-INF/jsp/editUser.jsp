<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="users" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />

<c:choose>
	<c:when test="${user.id == 0}">
		<h2>Create user</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit user</h2>
	</c:otherwise>
</c:choose>

<ww:form method="POST" action="storeUser">
	<c:if test="${user.id > 0}">
		<p>To keep the old password, just leave password fields empty.</p>
	</c:if>
	<ww:hidden name="userId" value="${user.id}" />

	<table class="formTable">
		<tr>
			<td>Full name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield name="user.fullName" /></td>
		</tr>
		<tr>
			<td>User/login id</td>
			<td>*</td>
			<td colspan="2"><ww:textfield name="user.loginName" /></td>
		</tr>
		<tr>
			<td>Initials</td>
			<td>*</td>
			<td colspan="2"><ww:textfield name="user.initials" size="6" maxlength="5" /></td>
		</tr>
		<tr>
			<td>Email</td>
			<td>*</td>
			<td colspan="2"><ww:textfield name="user.email" value="${user.email}" /></td>
		</tr>
		<tr>
			<td>Password</td>
			<td>*</td>
			<td colspan="2"><ww:password name="password1" /></td>
		</tr>
		<tr>
			<td>Confirm password</td>
			<td>*</td>
			<td colspan="2"><ww:password name="password2" /></td>
		</tr>
		<tr>
		<c:choose>
		<c:when test="${fn:length(teamList) > 0}">
			<td><a href="javascript:toggleDiv('teamlist');">Select teams</a></td>
			<td></td>
			<td>
			<p>User is currently in <c:out value="${fn:length(user.teams)}" /> teams</p>
			<ul id="teamlist" style="display:none;list-style-type:none;">
			
			<c:forEach items="${teamList}" var="team" varStatus="status">
				<c:choose>
					<c:when test="${aef:listContains(user.teams, team)}">
						<c:set var="selected" value="true" />
					</c:when>
					<c:otherwise>
						<c:set var="selected" value="" />
					</c:otherwise>
				</c:choose>
				<li class="${(status.index % 2 == 0) ? 'even' : 'odd'}"><ww:checkbox name="teamIds[${team.id}]" value="${selected}"/>
				<c:out value="${team.name}" /></li>
			</c:forEach>
			</ul>
			</td>
			</c:when>
			</c:choose>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${user.id == 0}">
					<td><ww:submit value="Create" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" /></td>
					<%-- All users cannot be deleted --%>
					<c:if test="${user.id != 1}">
						<td class="deleteButton"> <ww:submit action="deleteUser"
							onclick="return confirmDelete()" value="Delete" /> </td>
					</c:if>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>

</ww:form>
<%@ include file="./inc/_footer.jsp"%>