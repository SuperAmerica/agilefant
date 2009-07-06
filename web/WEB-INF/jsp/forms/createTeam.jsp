<%@ include file="../inc/_taglibs.jsp"%>

<aef:userList />

<div class="validateWrapper validateNewTeam">
<form action="ajax/storeTeam.action" method="post">

    <ww:hidden name="teamId" value="0" />
    <table class="formTable">
        <tr>
            <td>Name</td>
            <td>*</td>
            <td colspan="2"><ww:textfield size="60" name="team.name" /></td>
        </tr>
        <tr>
            <td>Description</td>
            <td></td>
            <td colspan="2"><ww:textarea cols="70" rows="10" name="team.description" cssClass="useWysiwyg" /></td>
        </tr>
        <tr>
            <td><a href="javascript:toggleDiv('createTeamUserList');">Select users</a></td>
            <td></td>
            <td>
            <span><c:out value="${team.numberOfUsers}" /> users in team</span>
            <ul id="createTeamUserList" style="display:none;list-style-type:none;">
            
            <c:forEach items="${userList}" var="user" varStatus="status">
                <c:choose>
                    <c:when test="${aef:listContains(team.users, user)}">
                        <c:set var="selected" value="true" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="selected" value="" />
                    </c:otherwise>
                </c:choose>
                <li class="${(status.index % 2 == 0) ? 'even' : 'odd'}"><ww:checkbox name="userIds[%{user.id}]" value="%{selected}"/>
                <c:out value="${user.fullName}" /></li>
            </c:forEach>
            </ul>
            </td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td><ww:submit value="Create" id="createButton" /></td>
            <td class="deleteButton"><ww:reset value="Cancel"
                cssClass="closeDialogButton" /></td>
        </tr>
    </table>
</form>
</div>