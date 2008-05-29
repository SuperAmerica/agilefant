<%@ include file="./_taglibs.jsp"%>

<div style="width: 690px; height: 20px; padding: 5px; border-bottom: 1px solid black; background: #ccc;">
	<span style="float: left;">
	<c:choose>
		<c:when test="${hourEntry.id == 0}">
			<b>New Hour Reporting Entry</b>
		</c:when>
		<c:otherwise>
			<b>Edit Hour Reporting Entry</b>
		</c:otherwise>
	</c:choose>
	</span>
	<span style="float: right;" >
		<img class="jqmClose" src="static/img/delete.png" alt="">
	</span>
</div>
<div style="padding: 12px;">
<ww:form action="storeHourEntry">
	<ww:hidden name="hourEntry.id" />
	<ww:hidden name="backlogItemId" />
	<ww:hidden name="BacklogId" />
	<ww:hidden name="contextObjectId" />
	<ww:hidden name="contextViewName" />
	<table class="formTable">
		<tr>
			<td>Spent effort</td>
			<td></td>
			<td colspan="2">
					<ww:textfield name="hourEntry.timeSpent" />
			</td>
		</tr>
		</tr>
		<tr>
			<td>Date</td>
			<td></td>
			<td><ww:datepicker value="%{#date}" size="15"
                                        showstime="true"
                                        format="%{getText('webwork.datepicker.format')}"
                                        name="date" /></td>
		</tr>
		<tr>
			<aef:currentUser />
			<td>Users </td>
			<td></td>
			<td colspan="2">
			<aef:userList/>
			<c:choose>
			<c:when test="${hourEntry.id == 0}">
				<div id="assigneesLink"><a href="javascript:toggleDiv('assigneeSelect2');"><img src="static/img/users.png" /> select </a>
                </div>
                        
                    <div id="assigneeSelect2" class="userSelector" style="display: none;">
                    <display:table name="${userList}" id="user" class="projectUsers"
                        defaultsort="2">
                        <display:column title="">
                            <c:choose>
                                <c:when test="${user.id == currentUser.id}">
                                    <input type="checkbox" name="selectedUserIds"
                                        value="${user.id}" checked="checked" class="user_${user.id}" />
                                </c:when>
                                <c:otherwise>
                                    <input type="checkbox" name="selectedUserIds"
                                        value="${user.id}" class="user_${user.id}" />
                                </c:otherwise>
                            </c:choose>
                        </display:column>
                        <display:column><c:out value="${user.fullName}"/></display:column>
                    </display:table>
           
                    <div id="userselect2" class="projectTeams userSelector">
                        <div class="right">
                            <label>Teams</label>
                            <ul class="groups" />
                    </div>
                    <script type="text/javascript">
                        $(document).ready( function() {
                            <aef:teamList />
                            <ww:set name="teamList" value="#attr.teamList" />
                            var teams = [<aef:teamJson items="${teamList}"/>]
                            $('#userselect2').multiuserselect({groups: teams, root: $('#user')});
                        });
                        </script>
                    </div>
                    </div>
            </c:when>
			<c:otherwise>
				<ww:select name="hourEntry.userId" list="userList" listKey="id" listValue="name"></ww:select>
			</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td>Comment</td>
			<td></td>
			<td colspan="2"><ww:textfield name="hourEntry.description" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>				
			<td><ww:submit value="Store"/> </td>
		</tr>
	</table>
</ww:form>
</div>



