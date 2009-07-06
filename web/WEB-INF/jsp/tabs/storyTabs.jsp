<%@ include file="../inc/_taglibs.jsp"%>

<aef:hourReporting id="hourReport" />
<aef:currentUser />
<aef:userList />
<aef:teamList />
<aef:productList />

<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#storyEditTab-${storyId}-${storyListContext}"><span><img src="static/img/edit.png" alt="Edit" /> Edit</span></a></li>
	<%--
	<li><a href="#storyProgressTab-${storyId}-${storyListContext}"><span><img src="static/img/progress.png" alt="Progress" /> Progress</span></a></li>
  <c:if test="${hourReport == true}">
	 <li><a href="#storySpentEffTab-${storyId}-${storyListContext}"><span><img src="static/img/timesheets.png" alt="Spent Effort" /> Spent Effort</span></a></li>
  </c:if>
  --%>
</ul>

<div id="storyEditTab-${storyId}-${storyListContext}" class="storyNaviTab">

<script type="text/javascript">
$(document).ready(function() {
    $('#userChooserLink-${storyId}-${storyListContext}').userChooser({
        storyId: ${storyId},
        backlogIdField: '#backlogSelect-${storyId}-${storyListContext}',
        userListContainer: '#userListContainer-${storyId}-${storyListContext}',
        legacyMode: false
    }); 
    $('#themeChooserLink-${storyId}-${storyListContext}').themeChooser({
        backlogId: '#backlogSelect-${storyId}-${storyListContext}',
        themeListContainer: '#themeListContainer-${storyId}-${storyListContext}'
    });
});

</script>

<table>
<tbody>
	<tr>
	<td>
	<div class="subItems" style="margin-top: 0px; width: 715px;"> 
	<div class="validateWrapper validateStory">
	<form action="ajax/storeStory.action" method="post">
		<ww:hidden name="storyId" value="%{story.id}" />	
		<%--<ww:hidden name="effortLeft" value="${story.effortLeft}" />--%>				
	
		<table class="formTable">	
			<tr>
				<td><ww:text name="general.uniqueId"/></td>
				<td></td>
				<td><aef:quickReference item="${story}" /></td>
			</tr>	
			<tr>						
				<td>Name</td>											
				<td>*</td>
				<td colspan="2"><ww:textfield size="60" name="story.name" /></td>
			</tr>
			
			<tr>
				<td>Description</td>
				<td></td>
				<td colspan="2">
          <textarea rows="10" cols="70" id="storyDescription" name="story.description" class="useWysiwyg">
            ${aef:nl2br(story.description)}
          </textarea>
        </td>
			</tr>
      
      <tr>
        <td>Story point estimate</td>
        <td></td>
        <td colspan="2"><ww:textfield size="15" id="storyPointsField_%{story.id}-%{storyListContext}" name="story.storyPoints" /></td>
      </tr>
      
			
			<c:if test="${story.creator != null}">
			<tr>
				<td>Created by</td>
				<td></td>
				<td colspan="2">
	
				<div>
				<c:out value="${story.creator.fullName}" />
				<c:choose>
				<c:when test="${story.createdDate != null}">
				 on <c:out value="${aef:timestampToString(story.createdDate)}"/>
				</c:when>
				</c:choose>
				</div>
				</td>
			</tr>
			</c:if>

			<tr>
				<td>State</td>
				<td></td>
				<td colspan="2">
				<c:set var="hasUndoneTodos" value="${undoneTodos}" scope="request" />				
				<script type="text/javascript">
				function change_estimate_enabled(value, itemId, context) {
					var effLeftField = document.getElementById("effortLeftField_" + itemId + "-" + context);
					var origEstField = document.getElementById("originalEstimateField_" + itemId + "-" + context);
					var resetLink = document.getElementById("resetLink_" + itemId + "-" + context);
					var resetText = document.getElementById("resetText_" + itemId + "-" + context);
					if (value == 'DONE') {
						if (effLeftField != null) {
							effLeftField.disabled = true;
						}
						if (origEstField != null) {
							origEstField.disabled = true;
						}
						if (resetLink != null) {
							resetLink.style.display = "none";
						}
						if (resetText != null) {
							resetText.style.display = "";
						}
					}
					else {
						if (effLeftField != null) {
							effLeftField.disabled = false;
						}
						if (origEstField != null) {
							origEstField.disabled = false;
						}
						if (resetLink != null) {
							resetLink.style.display = "";
						}
						if (resetText != null) {
							resetText.style.display = "none";
						}
					}
				}
				
				<%-- If user changed the item's state to DONE and there are todos not DONE, ask if they should be set to DONE as well. --%>				
				/*$(document).ready(function() {					
					$("#stateSelect_${story.id}-${storyListContext}").change(function() {
						change_estimate_enabled($(this).val(), ${story.id}, '${storyListContext}');						
						if ($(this).val() == 'DONE' && ${hasUndoneTodos}) {
							var prompt = window.confirm("Do you wish to set all the TODOs' states to Done as well?");
							if (prompt) {
								$("#todosToDone_${story.id}-${storyListContext}").val('true');
							}						
						}
					});

					
				});*/
				</script>
				<%-- Todos to DONE confirmation script ends. --%>
				<ww:hidden name="todosToDone" value="%{todosToDone}" id="todosToDone_%{story.id}-%{storyListContext}" />			
				<ww:select name="story.state"
					id="stateSelect_%{story.id}-%{storyListContext}"
					value="story.state.name"
					list="@fi.hut.soberit.agilefant.model.StoryState@values()" listKey="name"
					listValue="getText('story.state.' + name())"  /></td>
			</tr>
	
			<tr>
				<td>Backlog</td>
				<td></td>
				<td colspan="2">
					<select name="backlogId" id="backlogSelect-${storyId}-${storyListContext}">
	
					<%-- Generate a drop-down list showing all backlogs in a hierarchical manner --%>
					<option class="inactive" value="">(select backlog)</option>
					<c:forEach items="${productList}" var="product">
						<c:choose>
							<c:when test="${product.id == story.backlog.id}">
								<option selected="selected" value="${product.id}" class="productOption"
									title="${product.name}">${aef:out(product.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${product.id}" title="${product.name}" class="productOption">${aef:out(product.name)}</option>
							</c:otherwise>
						</c:choose>
						<c:forEach items="${product.children}" var="project">
							<c:choose>
								<c:when test="${project.id == story.backlog.id}">
									<option selected="selected" value="${project.id}" class="projectOption"
										title="${project.name}">${aef:out(project.name)}</option>
								</c:when>
								<c:otherwise>
									<option value="${project.id}" title="${project.name}"  class="projectOption">${aef:out(project.name)}</option>
								</c:otherwise>
							</c:choose>
							<c:forEach items="${project.children}" var="iteration">
								<c:choose>
									<c:when test="${iteration.id == story.backlog.id}">
										<option selected="selected" value="${iteration.id}" class="iterationOption"
											title="${iteration.name}">${aef:out(iteration.name)}</option>
									</c:when>
									<c:otherwise>
										<option value="${iteration.id}" title="${iteration.name}"  class="iterationOption">${aef:out(iteration.name)}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:forEach>
					</c:forEach>
				</select></td>
			</tr>
			
			<%--
			<tr>
				<td>Priority</td>
				<td></td>
				<td colspan="2"><ww:select name="story.priority"
					value="story.priority.name"
					list="#@java.util.LinkedHashMap@{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}" /></td>
				
			If you change something about priorities, remember to update conf/classes/messages.properties as well!
			</tr>
			--%>
			
			<tr>
				<td>Responsibles</td>
				<td></td>
				<td colspan="2">
	
				<div>
				<a id="userChooserLink-${storyId}-${storyListContext}" href="#" class="assigneeLink">
				    <img src="static/img/users.png"/>
                    <span id="userListContainer-${storyId}-${storyListContext}">
                    <c:set var="count" value="0" />
                    <c:set var="listLength" value="${fn:length(story.responsibles)}"/>
                    <c:choose>
                        <c:when test="${listLength > 0}">
                            <c:forEach items="${story.responsibles}" var="resp">
                                <input type="hidden" name="userIds" value="${resp.id}"/>
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
			<%--
			<tr>
				<td>Themes</td>
				<td></td>
				<td colspan="2">
	
				<div>
				<a id="themeChooserLink-${storyId}-${storyListContext}" href="#" class="assigneeLink themeChooserLink">
				    <img src="static/img/theme.png"/>
                    <span id="themeListContainer-${storyId}-${storyListContext}">
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
				<td><ww:submit value="Save" id="saveButton" />
				<ww:submit name="SaveClose" value="Save & Close" id="saveClose"  /></td>
				<td class="deleteButton">
				<ww:submit value="Delete" action="deleteStory" />
				<ww:reset value="Cancel"/>	
				</td>
			</tr>
		</table>
	
	</form>
</div>
</div>
</div>
</div>

</td>
</tr>
</tbody>
</table>
	
</div>
<!-- edit tab ends -->
<%--

<c:if test="${hourReport == true}">
<div id="storySpentEffTab-${storyId}-${storyListContext}" class="storyNaviTab">

<aef:hourEntries id="hourEntries" target="${story}" />

<script type="text/javascript">
$(document).ready(function() {
	var allUsers = function() {
		var users = jsonDataCache.get("allUsers");
		var ret = {};
		jQuery.each(users,function() {if(this.enabled) {ret[this.id] = this.fullName; } });
		return ret;
	};
	$('#spentEffort-${storyId}-${storyListContext}').inlineTableEdit({
				  submit: '#saveSpentEffort-${storyId}-${storyListContext}',
				  useId: true,
				  deleteaction: 'deleteHourEntry.action',
                  submitParam: 'hourEntryId',
				  fields: {
				  	efforts: {cell: 2, type: 'text'},
				  	dates: {cell: 0, type: 'date'},
				  	userIdss: {cell: 1, type: 'select', data: allUsers},
				  	descriptions: {cell: 3, type: 'text'},
				  	reset: {cell: 4, type: 'reset'}
				  	}
	});

});
</script>								
				
<div class="subItemContent">
<div class="subItems validateWrapper validateEmpty" style="margin-top: 0; margin-left: 3px; width: 710px;">
	<ww:url id="createLink" action="ajaxCreateHourEntry" includeParams="none">
		<ww:param name="storyId" value="${storyId}" />
	</ww:url>
	<ww:a cssClass="openCreateDialog openHourEntryDialog" title="Log effort"
		href="%{createLink}" onclick="return false;">
		Log effort &raquo;
	</ww:a>
	<c:if test="${!empty hourEntries}">		
	<ww:form action="updateMultipleHourEntries.action" method="post">		
	<display:table name="${hourEntries}" htmlId="spentEffort-${storyId}-${storyListContext}" id="row" defaultsort="1" defaultorder="descending" requestURI="${currentAction}.action"
	   style="width: 700px;">
						
		<display:column sortable="false" title="Date" style="white-space:nowrap; width: 140px;">
			<ww:date name="#attr.row.date" format="yyyy-MM-dd HH:mm" />
		</display:column>
						
		<display:column sortable="false" title="User">
			<span style="display: none;">${row.user.id}</span>
			${aef:html(row.user.fullName)}
		</display:column>
						
		<display:column sortable="false" title="Spent effort" sortProperty="timeSpent">
			${aef:html(row.timeSpent)}
		</display:column>
						
		<display:column sortable="false" title="Comment">
			<c:out value="${row.description}"/>
		</display:column>
						
		<display:column sortable="false" title="Action">	
			<span class="uniqueId" style="display: none;">${row.id}</span>
			<img src="static/img/edit.png" class="table_edit_edit" alt="Edit" title="Edit" style="cursor: pointer;" />
            <img src="static/img/delete_18.png" alt="Delete" title="Delete" class="table_edit_delete" style="cursor: pointer;"/>								
		</display:column>
	</display:table>
	<input type="submit" value="Save" style="display: none;" id="saveSpentEffort-${storyId}-${storyListContext}" />
	</ww:form>
	</c:if>			
	</div>
	</div>					
</div>
</c:if>


</div>
--%>