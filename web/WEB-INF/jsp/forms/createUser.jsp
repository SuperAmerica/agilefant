<%@ include file="../inc/_taglibs.jsp"%>

<div class="validateWrapper validateNewUser">
<ww:form method="POST" action="storeUser">
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
            <td></td>
            <td></td>
            <td colspan="2"><ww:checkbox name="user.enabled" value="${user.enabled}" /> Enabled</td>
        </tr>
        <tr>
            <td>Weekly hours</td>
            <td>*</td>
            <td colspan="2"><ww:textfield name="user.weekHours" value="${user.weekHours}" /></td>
        </tr>       
        <tr>
            <td>Password</td>
            <td>*</td>
            <td colspan="2"><ww:password name="password1" id="password1" /></td>
        </tr>
        <tr>
            <td>Confirm password</td>
            <td>*</td>
            <td colspan="2"><ww:password name="password2" id="password2" /></td>
        </tr>
        <tr>
        <c:choose>
        <c:when test="${fn:length(teamList) > 0}">
            <td><a href="javascript:toggleDiv('createBLIteamlist');">Select teams</a></td>
            <td></td>
            <td>
            <p>User is currently in <c:out value="${fn:length(user.teams)}" /> teams</p>
            <ul id="createBLIteamlist" style="display:none;list-style-type:none;">
            
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
            <td><ww:submit value="Create" id="createButton" /></td>
            <td class="deleteButton"><ww:reset value="Cancel"
                cssClass="closeDialogButton" /></td>
        </tr>
    </table>

</ww:form>
</div>