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
<form action="ajax/createStory.action" method="post">
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
      <td>Story point estimate</td>
      <td></td>
      <td colspan="2"><ww:textfield size="15" name="story.storyPoints" 
      					id="createStory_storyPointsField"/></td>
    </tr>

		<tr>
			<td>State</td>
			<td></td>
			<td colspan="2">
			<ww:select
				name="story.state" id="stateSelect"
				value="story.state.name"
				list="@fi.hut.soberit.agilefant.model.StoryState@values()" listKey="name"
				listValue="getText('story.state.' + name())"
				onchange="disableElementIfValue(this, '#createStory_storyPointsField', 'DONE');"/></td>
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