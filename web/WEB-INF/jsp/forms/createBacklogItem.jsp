<%@ include file="../inc/_taglibs.jsp"%>

<aef:userList />
<aef:teamList />
<aef:currentUser />
<aef:iterationGoalList id="iterationGoals" backlogId="${backlogId}" />
<aef:productList />

<script type="text/javascript">

$(document).ready(function() {
    $('#themeChooserLink-createBLI').themeChooser({
        backlogItemId: ${backlogItemId},
        backlogId: '#createBLIBacklogId',
        themeListContainer: '#themeListContainer-createBLI'
    });
    $('#userChooserLink-createBLI').userChooser({
        backlogItemId: ${backlogItemId},
        legacyMode: false,
        backlogIdField: '#createBLIBacklogId',
        userListContainer: '#userListContainer-createBLI'
    });
    getIterationGoals($('#createBLIBacklogId').val(),
        '#createBLIIterGoalSelect', '${iterationGoalId}');
});

</script>

<div class="validateWrapper validateNewBacklogItem">
<ww:form action="storeNewBacklogItem" method="post">
	<ww:hidden name="fromTodoId" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="backlogItem.name" /></td>
		</tr>

		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				cssClass="useWysiwyg" id="createBacklogItemDescription"
				name="backlogItem.description" /></td>
		</tr>

		<tr>
			<td>Original estimate</td>
			<td></td>
			<td colspan="2">
                      <ww:textfield size="10" name="backlogItem.originalEstimate"
                          id="createBLI_originalEstimateField" />
                      <ww:label value="%{getText('webwork.estimateExample')}" />
                  </td>
		</tr>

		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2">
			<ww:select
				name="backlogItem.state" id="stateSelect"
				value="backlogItem.state.name"
				list="@fi.hut.soberit.agilefant.model.State@values()" listKey="name"
				listValue="getText('task.state.' + name())"
				onchange="disableElementIfValue(this, '#createBLI_originalEstimateField', 'DONE');"/></td>
		</tr>

		<tr>
			<td>Backlog</td>
			<td></td>
			<td colspan="2">
			
			<select name="backlogId" id="createBLIBacklogId" onchange="getIterationGoals(this.value, '#createBLIIterGoalSelect');">
				<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
				<option class="inactive" value="">(select backlog)</option>
				<c:forEach items="${productList}" var="product">
					<c:choose>
						<c:when test="${product.id == backlogId}">
							<option selected="selected" value="${product.id}"
								class="productOption"><c:out value="${product.name}" /></option>
						</c:when>
						<c:otherwise>
							<option value="${product.id}" title="${product.name}"
								class="productOption"><c:out value="${product.name}" /></option>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == backlogId}">
								<option selected="selected" value="${project.id}"
									class="projectOption"><c:out value="${project.name}" /></option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}"
									class="projectOption"><c:out value="${project.name}" /></option>
							</c:otherwise>
						</c:choose>
						<c:forEach items="${project.iterations}" var="iteration">
							<c:choose>
								<c:when test="${iteration.id == backlogId}">
									<option selected="selected" value="${iteration.id}"
										class="iterationOption"><c:out value="${iteration.name}" /></option>
								</c:when>
								<c:otherwise>
									<option value="${iteration.id}" title="${iteration.name}"
										class="iterationOption"><c:out value="${iteration.name}" /></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:forEach>
            </select></td>
		</tr>
		<tr>
			<td>Iteration goal</td>
			<td></td>
			<%-- If iteration goals doesn't exist default value is 0--%>
			<td colspan="2">
			<select name="iterationGoalId" id="createBLIIterGoalSelect">
			</select>
			<span>(none)</span></td>
		</tr>
		<tr>
			<td>Priority</td>
			<td></td>
			<td colspan="2"><ww:select name="backlogItem.priority"
				value="backlogItem.priority.name"
				list="#@java.util.LinkedHashMap@{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}" /></td>
			<%--
        If you change something about priorities, remember to update conf/classes/messages.properties as well!
        --%>
		</tr>
		<tr>
			<td>Responsibles</td>
			<td></td>
			<td colspan="2">

			<div>
                <a id="userChooserLink-createBLI" href="#" class="assigneeLink">
                    <img src="static/img/users.png"/>
                    <span id="userListContainer-createBLI">
                    <c:set var="count" value="0" />
                    <c:set var="listLength" value="${fn:length(backlogItem.responsibles)}"/>
                    <c:choose>
                        <c:when test="${listLength > 0}">
                            <c:forEach items="${backlogItem.responsibles}" var="resp">
                                <input type="hidden" name="userIds[${resp.id}]" value="${resp.id}"/>
                                <c:set var="count" value="${count + 1}" />
                                <c:out value="${resp.initials}" /><c:if test="${count != listLength}">, </c:if>
                            </c:forEach>    
                        </c:when>
                        <c:otherwise>
                            (none)
                        </c:otherwise>
                    </c:choose>
                    </span>
                </a>
            </div>
			</td>
		</tr>
		<tr>
			<td>Themes</td>
			<td></td>
			<td colspan="2">

			<div>
                <a id="themeChooserLink-createBLI" href="#" class="assigneeLink">
                    <img src="static/img/theme.png"/>
                    <span id="themeListContainer-createBLI">
                    <c:set var="count" value="0" />
                    <c:set var="listLength" value="${fn:length(backlogItem.businessThemes)}"/>
                    <c:choose>
                        <c:when test="${listLength > 0}">
                            <c:forEach items="${backlogItem.businessThemes}" var="bt">
                                <input type="hidden" name="themeIds" value="${bt.id}" />
			            	   <c:choose>
			            	       <c:when test="${bt.global}">
			            	           <span class="businessTheme globalThemeColors" style="float: none;"><c:out value="${bt.name}"/></span>
			            	       </c:when>
			            	       <c:otherwise>
			            	           <span class="businessTheme" style="float: none;"><c:out value="${bt.name}"/></span>   
			            	       </c:otherwise>
			            	   </c:choose>                                
                            </c:forEach>    
                        </c:when>
                        <c:otherwise>
                            (none)
                        </c:otherwise>
                    </c:choose>
                    </span>
                </a>
            </div>
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

</ww:form>
</div>