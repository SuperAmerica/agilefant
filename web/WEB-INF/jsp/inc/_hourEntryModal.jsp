<%@ include file="./_taglibs.jsp"%>
<aef:currentUser />
<aef:userList/>
<div target="AJAX-MODAL" style="width: 690px; height: 20px; padding: 5px; border-bottom: 1px solid black; background: #ccc;">
	<span style="float: left;">
	<c:choose>
		<c:when test="${hourEntryId == 0}">
			<b>Log effort</b>
		</c:when>
		<c:otherwise>
			<b>Edit log entry</b>
		</c:otherwise>
	</c:choose>
	</span>
	<span style="float: right;" >
		<img class="jqmClose" src="static/img/delete.png" alt="">
	</span>
</div>
<div style="padding: 12px;">
<ww:form action="storeHourEntry" onsubmit="javascript: return checkEstimateFormat('hourEntry.timeSpent');">
	<ww:hidden name="hourEntryId" />
	<ww:hidden name="backlogItemId" />
	<ww:hidden name="BacklogId" />
	<ww:hidden name="contextObjectId" />
	<ww:hidden name="contextViewName" />
	<table class="formTable">
	
	<tr>
			<td>Effort spent</td>
			<td></td>
			<td colspan="2">
					<ww:textfield name="hourEntry.timeSpent" />hours
			</td>
		</tr>
		</tr>
		<tr>
			<td>When</td>
			<td></td>
			<td>
						<ww:date name="%{hourEntry.date}" id="date" format="%{getText('webwork.shortDateTime.format')}" />
						<ww:datepicker value="%{#date}" size="15"
                                        showstime="true"
                                        format="%{getText('webwork.datepicker.format')}"
                                        name="date" />
                                      
        	</td>
		</tr>

		<tr>
			
			<td>By whom</td>
			<td></td>
			<td colspan="2">
			<c:choose>
			<c:when test="${hourEntryId == 0}">
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
				<select name="userId">
				<c:forEach items="${userList}" var="user">
					<c:choose>
						<c:when test="${user.id == hourEntry.user.id}">
							<option value="${user.id}" selected="selected"><c:out value="${user.fullName}" /></option>
						</c:when>
						<c:otherwise>
							<option value="${user.id}"><c:out value="${user.fullName}" /></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>

			</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td>Comment</td>
			<td></td>
			<td colspan="2"><ww:textfield size="60" name="hourEntry.description" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>				
			<td><ww:submit value="Store"/> </td>
		</tr>
	</table>
</ww:form>
</div>



