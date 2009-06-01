<%@ include file="../inc/_taglibs.jsp"%>

<aef:userList />
<aef:teamList />
<aef:currentUser />
<aef:productList />

<script type="text/javascript">

$(document).ready(function() {
    $('#themeChooserLink-createStory').themeChooser({
        storyId: ${storyId},
        backlogId: '#createStoryBacklogId',
        themeListContainer: '#themeListContainer-createStory'
    });
    $('#userChooserLink-createStory').userChooser({
        storyId: ${storyId},
        legacyMode: false,
        backlogIdField: '#createStoryBacklogId',
        userListContainer: '#userListContainer-createStory'
    });
 
});

</script>

<div class="validateWrapper validateNewStory">
<ww:form action="storeNewStory" method="post">
	<ww:hidden name="fromTodoId" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="story.name" /></td>
		</tr>

		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				cssClass="useWysiwyg" id="createStoryDescription"
				name="story.description" /></td>
		</tr>

		<tr>
			<td>Original estimate</td>
			<td></td>
			<td colspan="2">
                      <ww:textfield size="10" name="story.originalEstimate"
                          id="createBLI_originalEstimateField" />
                      <ww:label value="%{getText('webwork.estimateExample')}" />
                  </td>
		</tr>

		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2">
			<ww:select
				name="story.state" id="stateSelect"
				value="story.state.name"
				list="@fi.hut.soberit.agilefant.model.State@values()" listKey="name"
				listValue="getText('todo.state.' + name())"
				onchange="disableElementIfValue(this, '#createBLI_originalEstimateField', 'DONE');"/></td>
		</tr>

		<tr>
			<td>Backlog</td>
			<td>*</td>
			<td colspan="2">
			
			<select name="backlogId" id="createStoryBacklogId">
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
					<c:forEach items="${product.children}" var="project">
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
						<c:forEach items="${project.children}" var="iteration">
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
			<td>Priority</td>
			<td></td>
			<td colspan="2"><ww:select name="story.priority"
				value="story.priority.name"
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
                <a id="userChooserLink-createStory" href="#" class="assigneeLink">
                    <img src="static/img/users.png"/>
                    <span id="userListContainer-createStory">
                    <c:set var="count" value="0" />
                    <c:set var="listLength" value="${fn:length(story.responsibles)}"/>
                    <c:choose>
                        <c:when test="${listLength > 0}">
                            <c:forEach items="${story.responsibles}" var="resp">
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
		<%-- TODO: Add theme support
		<tr>
			<td>Themes</td>
			<td></td>
			<td colspan="2">

			<div>
                <a id="themeChooserLink-createStory" href="#" class="assigneeLink">
                    <img src="static/img/theme.png"/>
                    <span id="themeListContainer-createStory">
                    <c:set var="count" value="0" />
                    <c:set var="listLength" value="${fn:length(story.businessThemes)}"/>
                    <c:choose>
                        <c:when test="${listLength > 0}">
                            <c:forEach items="${story.businessThemes}" var="bt">
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
		 --%>
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