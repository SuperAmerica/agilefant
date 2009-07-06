<%@ include file="./_taglibs.jsp"%>

<aef:hourReporting id="hourReport" />

<aef:openDialogs context="story" id="openStoryTabs" />

<!-- context variable for story ajax to know its context -->
<c:set var="storyListContext" value="backlogList" scope="session" />

<c:set var="dialogContext" value="story" scope="session" />

<aef:currentUser />
<script language="javascript" type="text/javascript">

function validateDeletion() {
    return confirm("The selected stories will be gone forever. Are you sure?");
}

function selectAllStories(val) {
  var elems = document.getElementsByName("selected");
  for(var x in elems)
    elems[x].checked = val;
}

function disableThemeSelect(value) {
  var boxes = document.getElementById('themeSelectDiv').getElementsByTagName('input');
  for(var x in boxes)
    boxes[x].disabled = value;
}

$(document).ready(function() {
    <c:forEach items="${openStoryTabs}" var="openStory">
        handleTabEvent("storyTabContainer-${openStory[0]}-${storyListContext}", "story", ${openStory[0]}, ${openStory[1]}, '${storyListContext}');
    </c:forEach>
    $('#themeChooserLink-multipleSelect').themeChooser({
        backlogId: 'select[name=targetBacklog]',
        themeListContainer: '#themeListContainer-multipleSelect'
    });
    $('#userChooserLink-multipleSelect').userChooser({
        backlogIdField: 'select[name=targetBacklog]',
        userListContainer: '#userListContainer-multipleSelect',
        storyId: 0,
        legacyMode: false
    });
});

</script>
<ww:form action="doActionOnMultipleStories">

  <!-- Return to this backlog after submit -->
  <ww:hidden name="backlogId" value="backlog.id" />

  <display:table class="listTable" name="stories" id="row"
    requestURI="${currentAction}.action" >

    <!-- Checkboxes for bulk-moving strories -->
    
    <display:column sortable="false" title="<input type='checkbox' name='selectall' onclick='selectAllStories(this.checked)'/>" class="selectColumn">
      <div><ww:checkbox name="selected" fieldValue="row.id" /></div>
      <div style="height: 15px;"></div>
      <div id="storyTabContainer-${row.id}-${storyListContext}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 15px;"></div>
    </display:column>
    
    <!-- Make the columns fit in the iteration-page. -->
    <c:set var="nameClass" value="nameColumn" />
        
    <display:column sortable="true" sortProperty="name" title="Name" class="${nameClass}">                        
      <div id="story_${row.id}">

            <c:forEach items="${backlogThemes[row]}" var="businessTheme">
              <a href="#" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','story',${row.id},0, '${storyListContext}'); return false;">
                 <c:choose>
                     <c:when test="${businessTheme.global}">
                         <span class="businessTheme globalThemeColors" title="${aef:stripHTML(businessTheme.description)}"><c:out value="${businessTheme.name}"/></span>
                     </c:when>
                     <c:otherwise>
                         <span class="businessTheme" title="${aef:stripHTML(businessTheme.description)}"><c:out value="${businessTheme.name}"/></span>   
                     </c:otherwise>
                 </c:choose>
              </a>
            </c:forEach>
            
      <a class="nameLink" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','story',${row.id},0, '${storyListContext}'); return false;">
        ${aef:html(row.name)}
      </a>      
      </div>            
    </display:column>


    <display:column sortable="false" title="Responsibles" class="responsibleColumn">
      <aef:responsibleColumn storyId="${row.id}"/>
    <%--
      <c:set var="responsibleCount" value="${fn:length(backlogResponsibles[row])}" />
      <c:set var="currentResponsibleCount" value="0" />
      <c:forEach items="${backlogResponsibles[row]}" var="itemResponsible">
        <c:set var="currentResponsibleCount" value="${currentResponsibleCount + 1}" />
        <c:choose>
          <c:when test="${itemResponsible.inProject == false}">
            
              <span class="unassigned">${itemResponsible.user.initials}</span><c:if test="${currentResponsibleCount !=  responsibleCount}">,</c:if>
          </c:when>
          <c:otherwise>
            
              ${itemResponsible.user.initials}<c:if test="${currentResponsibleCount !=  responsibleCount}">,</c:if>
          </c:otherwise>
        </c:choose>
      </c:forEach>
      --%>
    </display:column>

    <display:column sortable="true" title="Estimate" class="storyPointColumn">
      <aef:storyPointFormat story="${row}"/>
    </display:column>

    <%--
    <display:column sortable="true" defaultorder="descending"
      title="Priority">
      <ww:text name="story.priority.${row.priority}" />
    </display:column>
--%>

    <display:column title="State" sortable="false" class="todoColumn">     
	   <aef:text name="story.state.${row.state}" />
      
      <%-- 
      <a class="nameLink" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','${dialogContext}',${row.id},1,'${storyListContext}'); return false;">
      </a>                    
      --%>
    </display:column>
      
    <%--
    <display:column sortable="true" sortProperty="effortLeft" defaultorder="descending"
      title="Effort Left<br/>" class="effortLeftColumn">
      <span style="white-space: nowrap">
        <c:choose>
          <c:when test="${row.effortLeft == null}">&mdash;</c:when>
          <c:otherwise>${row.effortLeft}</c:otherwise> 
        </c:choose>
      </span>
    </display:column>

    <display:column sortable="true" sortProperty="originalEstimate" defaultorder="descending"
        title="Original Estimate<br/>" class="originalEstimateColumn">
      <span style="white-space: nowrap">
        <c:choose>
          <c:when test="${row.originalEstimate == null}">&mdash;</c:when>
          <c:otherwise>${row.originalEstimate}</c:otherwise> 
        </c:choose>
      </span>
    </display:column>
--%>
<%--
    <c:if test="${hourReport}">
  
      <display:column sortable="true" sortProperty="effortSpent" defaultorder="descending" title="Effort Spent" class="effortSpentColumn">
        <span style="white-space: nowrap">
          <c:choose>
            <c:when test="${row.effortSpent == null}">&mdash;</c:when>
            <c:otherwise>
              <c:out value="${row.effortSpent}" />
            </c:otherwise>
          </c:choose>
        </span>
      </display:column>
    </c:if>
    --%>
    <display:column title="Actions" sortable="false">
      <img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('storyTabContainer-${row.id}-${storyListContext}','story',${row.id},0, '${storyListContext}'); return false;" />
      <img src="static/img/delete_18.png" alt="Delete" title="Delete" style="cursor: pointer;" onclick="deleteStory(${row.id}); return false;" />
    </display:column>
    
    <display:footer>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td><c:out value="Total: ${storyPointSum}sp" /></td>
        <td>&nbsp;</td>
        <%-- Effort left --%>
        <%--
        <td><c:out value="${backlog.storyEffortLeftSum}" /></td>
        --%>
        <%--
        <td><c:out value="${effortLeftSum}" /></td>
        
        <td><c:out value="${origEstSum}" /></td>
        <c:if test="${hourReport}">
          <td>
            <c:choose>
              <c:when test="${spentEffortSum != null}">
                <c:out value="${spentEffortSum}" />
              </c:when>
              <c:otherwise>
                0h
              </c:otherwise>
            </c:choose>
          </td>
        </c:if>
        --%>
      </tr>
    </display:footer>
  </display:table>

  <aef:productList />

<%--
  <table class="formTable">
  <tr>
    <td>State</td>
    <td colspan="2"><ww:select name="targetState"
      list="#@java.util.LinkedHashMap@{'-1':'Keep original', '0':'Not started', '1':'Started', '2':'Pending', '3':'Blocked', '4':'Implemented', '5':'Done' }" />
    </td>
  </tr>
  <tr>
    <td>Move to</td>
    <td class="targetBacklogDropdownColumn" colspan="2">
      <aef:backlogDropdown selectName="targetBacklog"
        preselectedBacklogId="${backlog.id}" backlogs="${productList}" />
    </td>
  </tr>
  <!--
  <tr>
    <c:if test="${aef:isIteration(backlog)}">
    <td>Iteration goal</td>
    <td class="targetBacklogDropdownColumn" colspan="2">
      <select name="targetIterationGoalId">
        <option value="-1">Keep original</option>
        <option value="-2">(none)</option>
        <c:forEach items="${backlog.iterationGoals}" var="itergoal">
          <option value="${itergoal.id}" ><c:out value="${itergoal.name}" /></option>
        </c:forEach>
      </select>
    </td>
    </c:if>
  </tr>-->
  <tr>
    <td>Priority</td>
    <td class="targetPriorityDropdown" colspan="2">
      <ww:select name="targetPriority"
        list="#@java.util.LinkedHashMap@{'-1':'Keep original', '5':'undefined', '4':'+++++', '3':'++++', '2':'+++', '1':'++', '0':'+'}" />
    </td>
  </tr>
<!--
  <tr>
         <td></td>
         <td colspan="2">Keep original</td>
  </tr>
-->
  <tr>
            <td>Responsibles</td>
            <td style="width: 30px;">
            <input type="checkbox" value="1" checked="checked" name="keepResponsibles"
                onchange="$('.toggleUserChooserLink').toggle();" />
            </td>
            <td>
            <div class="toggleUserChooserLink">
                <img src="static/img/users.png"/>
                <span>
                (Keep original)
                </span>
            </div>
            <div class="toggleUserChooserLink" style="display: none;">
                <a id="userChooserLink-multipleSelect" href="#" class="assigneeLink">
                    <img src="static/img/users.png"/>
                    <span id="userListContainer-multipleSelect">
                    (none)
                    </span>
                </a>
            </div>
            </td>
        </tr>
        <tr>
            <td>Themes</td>
            <td>
            <input type="checkbox" value="1" checked="checked" name="keepThemes"
                onchange="$('.toggleThemeChooserLink').toggle()" />
            </td>
            <td>
            <div class="toggleThemeChooserLink">
                    <img src="static/img/theme.png"/>
                    <span>
                    (Keep original)
                    </span>
            </div>
            <div class="toggleThemeChooserLink" style="display: none;">
                <a id="themeChooserLink-multipleSelect" href="#" class="assigneeLink">
                    <img src="static/img/theme.png"/>
                    <span id="themeListContainer-multipleSelect">
                    (none)
                    </span>
                </a>
            </div>
            </td>
        </tr> 
    
    
  <tr>
    <td><ww:submit type="button" label="Save" name="itemAction" value="%{'ChangeSelected'}" /></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><ww:submit type="button" name="itemAction" value="%{'DeleteSelected'}"
        onclick="return validateDeletion()" label="Delete selected" /></td>
  </tr>
  </table>
  --%>
</ww:form>
