<%@ include file="../inc/_taglibs.jsp"%>

<ww:actionerror />
<ww:actionmessage />

<div class="ajaxUserWindowTabsDiv ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#userEditTab-${userId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit user</span></a></li>
</ul>
<div id="userEditTab-${userId}" class="userNaviTab">

<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0px; width: 470px;">
			<div id="editUserForm" class="validateWrapper validateUser">
			<form action="ajax/storeUser.action" method="post">
				<c:if test="${user.id > 0}">
					<p>To keep the old password, just leave password fields empty.</p>
				</c:if>
				<ww:hidden name="userId" value="%{user.id}" />
			
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
						<td colspan="2"><ww:textfield name="user.email" /></td>
					</tr>
					<c:choose>
					    <c:when test="${user.id != 1}">
							<tr>
							    <td></td>
							    <td></td>
							    <td colspan="2">					    
							    	<ww:checkbox name="user.enabled" /> Enabled					    	
							    </td>					    	
							</tr>
						</c:when>
					    <c:otherwise>
					    	<ww:hidden name="user.enabled"  />
					    </c:otherwise>
					</c:choose>
			        <tr>
			            <td>Weekly hours</td>
			            <td>*</td>
			            <td colspan="2"><ww:textfield name="user.weekEffort" value="%{aef:minutesToString(user.weekEffort.minorUnits)}" /></td>
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
						<td><a href="javascript:toggleDiv('teamlist_${user.id}');">Select teams</a></td>
						<td></td>
						<td>
						<p>User is currently in <c:out value="${fn:length(user.teams)}" /> teams</p>
						<ul id="teamlist_${user.id}" style="display:none;list-style-type:none;">
						
						<c:forEach items="${teamList}" var="team" varStatus="status">
							<c:choose>
								<c:when test="${aef:listContains(user.teams, team)}">
									<c:set var="selected" value="checked=\"checked\"" />
								</c:when>
								<c:otherwise>
									<c:set var="selected" value="" />
								</c:otherwise>
							</c:choose>
							<li class="${(status.index % 2 == 0) ? 'even' : 'odd'}"><input type="checkbox" name="teamIds[${team.id}]" ${selected}/>
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
						<td><ww:submit value="Save" id="saveButton"/></td>
						<%-- All users cannot be deleted --%>
						<c:if test="${user.id != 1}">
							<td class="deleteButton"> <ww:submit action="deleteUser" value="Delete" /> </td>
						</c:if>
					</tr>
				</table>
			
			</form>
			</div>
			</div>
			</td>
		</tr>
	</tbody>
</table>


</div>

</div>