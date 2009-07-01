<%@ include file="_taglibs.jsp"%>

<aef:userList />

<div class="ajaxTeamWindowTabsDiv ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#teamEditTab-${teamId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit team</span></a></li>
</ul>
<div id="teamEditTab-${teamId}" class="teamNaviTab">

<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0px; width: 470px;">
			<div id="editUserForm" class="validateWrapper validateTeam">

				<ww:form action="ajaxStoreTeam" method="post">
				<ww:hidden name="teamId" value="%{teamId}" />
				<table class="formTable">
					<tr>
						<td>Name</td>
						<td>*</td>
						<td colspan="2"><ww:textfield size="55" name="team.name" /></td>
					</tr>
					<tr>
						<td>Description</td>
						<td></td>
						<td colspan="2"><ww:textarea cols="45" rows="10" name="team.description" cssClass="useWysiwyg"/></td>
					</tr>
					<tr>
						<td><a href="javascript:toggleDiv('teamlist_${team.id}');">Select users</a></td>
						<td></td>
						<td>
						<span><c:out value="${team.numberOfUsers}" /> users in team</span>
						<ul id="teamlist_${team.id}" style="display:none;list-style-type:none;">
						
						<c:forEach items="${userList}" var="user" varStatus="status">
							<c:choose>
								<c:when test="${aef:listContains(team.users, user)}">
									<c:set var="selected" value="checked=\"checked\"" />
								</c:when>
								<c:otherwise>
									<c:set var="selected" value="" />
								</c:otherwise>
							</c:choose>
							<li class="${(status.index % 2 == 0) ? 'even' : 'odd'}"><input type="checkbox" name="userIds[${user.id}]" ${selected}/>
							<c:out value="${user.fullName}" /></li>
						</c:forEach>
						</ul>
						</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td><ww:submit value="Save" /></td>
						<td class="deleteButton"> <ww:submit action="deleteTeam" value="Delete" />
						</td>	
					</tr>
				</table>
				</ww:form>
			</div>
			</div>
			</td>
		</tr>
	</tbody>
</table>

</div>

</div>