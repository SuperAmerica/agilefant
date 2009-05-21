<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="portfolio" subnavi="portfolioProjects" title="Active Projects"/>
<ww:actionerror />
<ww:actionmessage />
<script type="text/javascript">
<ww:set name="teamList" value="#attr.teamList" />
var teams = [<aef:teamJson items="${teamList}"/>]
</script>

<h2>Development Portfolio</h2>



<div class="subItems" style="width: 645px;" id="subItems_portfolioRankedProjectsList">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Ranked Projects					
				</td>
			</tr>
		</table>
	</div>

	<div class="subItemContent">
	<display:table name="${ongoingRankedProjects}" id="row">
	<display:column title="Rank">
		<c:out value="${row_rowNum}" />
	</display:column>
	<display:column title="St.">
	 	<c:choose>
			<c:when test="${row.status == 'OK'}">
				<img src="static/img/status-green.png" alt="OK" title="OK"/>
			</c:when>
			<c:when test="${row.status == 'CHALLENGED'}">
				<img src="static/img/status-yellow.png" alt="Challenged" title="Challenged"/>
			</c:when>
			<c:when test="${row.status == 'CRITICAL'}">
				<img src="static/img/status-red.png" alt="Critical" title="Critical"/>
			</c:when>
		</c:choose>
	</display:column>
	<display:column title="Project Name" class="portfolioNameColumn">
		<ww:a
			href="contextView.action?contextObjectId=${row.id}&resetContextView=true&contextName=project">
			<c:out value="${row.product.name}: ${row.name}" />
		</ww:a>
	</display:column>
	
		<display:column title="Assignees" class="portfolioUsersColumn">
		<c:set var="divId" value="${divId + 1}" scope="page" />
		<a href="javascript:toggleDiv('portfolioDiv_${divId}');">
			<img src="static/img/users.png" alt="Users" />
			<c:set var="assUserListLength" value="${fn:length(assignedUsers[row])}"/>
			<c:set var="nonAssUserListLength" value="${fn:length(nonAssignedUsers[row])}"/>		
			<c:set var="assUserCount" value="0" />						
			<c:forEach items="${assignedUsers[row]}" var="usr">
				<c:set var="assUserCount" value="${assUserCount + 1}" />			
				<c:out value="${usr.initials}" /><c:if test="${(assUserCount != assUserListLength || (assUserCount == assUserListLength && nonAssUserListLength > 0))}">, </c:if>
			</c:forEach>
			<span style="color: rgb(255, 0, 0);">
			<c:forEach items="${nonAssignedUsers[row]}" var="usr">
				<c:set var="nonAssUserCount" value="${nonAssUserCount + 1}" />			
				<c:out value="${usr.initials}" /><c:if test="${nonAssUserCount != nonAssUserListLength}">, </c:if>
			</c:forEach>
			</span>						
		</a>
		<!-- User assignment table -->	
		<div id="portfolioDiv_${divId}" style="display: none;">
		<ww:form action="saveProjectAssignments">
		<ww:hidden name="projectId" value="${row.id}" /> 
		
		<table class="row_${row.id}">
		<tr>
		<td> 	  	 	  	
 	  		<display:table name="${userList}" id="user" defaultsort="2">
			<!-- Set id string used as key for maps -->	
			<c:set var="idstring" value="${row.id}-${user.id}" scope="request"/>

			<display:column title="">
			
			<c:set var="flag" value="0" scope="request"/>
			<c:forEach var="usr" items="${assignedUsers[row]}">
				<c:if test="${usr.id == user.id}"> 
					<c:set var="flag" value="1" scope="request"/>
				</c:if>
			</c:forEach>			
			<c:choose>			
			<c:when test="${flag == 1}">
				<input class="user_${user.id}" type="checkbox" name="selectedUserIds" value="${user.id}" checked="checked" onchange="toggleDiv('${idstring}')"/>
			</c:when>
			<c:otherwise>
				<input class="user_${user.id}" type="checkbox" name="selectedUserIds" value="${user.id}" onchange="toggleDiv('${idstring}')"/>			
			</c:otherwise>
			</c:choose>				

			</display:column>		
			
			<display:column title="Users" sortProperty="fullName">						
			
				<!-- Check whether user is not assigned to project although has bli:s assigned -->
				<c:choose>	
				<c:when test="${unassignedUsers[idstring] == 1}"> 
				<a href="dailyWork.action?userId=${user.id}" class="unassigned">
				<c:out value="${user.fullName}" />
				</a> 
				</c:when>
				<c:otherwise>
				<a href="dailyWork.action?userId=${user.id}">
				<c:out value="${user.fullName}" />
				</a>
				</c:otherwise>
				</c:choose>
			</display:column>
			
			
			<display:column title="Effort Left">
				<c:out value="${loadLefts[idstring]}" />
			</display:column>
			
			<display:column title="Baseline load +/-">
			
			<!-- Check whether user is assigned. If is assigned -> show overhead -->
			<c:choose>	
			<c:when test="${flag == 1}"> 
				<div id="${idstring}" class="overhead">
			</c:when>
			<c:otherwise>
				<div id="${idstring}" class="overhead" Style="display: none;">
			</c:otherwise>
			</c:choose>
				<ww:label value="${row.defaultOverhead}" />+
				<ww:hidden name="assignments['${user.id}'].user.id" 
				value="${user.id}"  />
			  	<ww:textfield size="3"  name="assignments['${user.id}'].deltaOverhead" 
				value="${userOverheads[idstring]}" /> =
				<ww:label value="${totalUserOverheads[idstring]}" />
				</div>
			</display:column>
			</display:table>
			
		</td>
		<td class="teamselect">										
			<script type="text/javascript">
			$(document).ready( function() {
				$('.row_${row.id} .teamselect ul').groupselect(teams, ".row_${row.id}");
			});
			</script>
			<label>Teams</label>
			<ul></ul>
		</td>
		</tr>
		</table>
		<ww:submit action="saveProjectAssignments" value="Save" />
		</ww:form>
		</div>
		<!-- User assignment table ends -->
			
		</display:column>		
		
		<display:column title="Action" class="portfolioActionColumn">
		<ww:url id="moveTopLink" action="moveProjectTop">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveTopLink}"><img src="static/img/arrow_top.png" alt="Send to top" title="Send to top" /></ww:a>
		
		<ww:url id="moveUpLink" action="moveProjectUp">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveUpLink}"><img src="static/img/arrow_up.png" alt="Move up" title="Move up" /></ww:a>

		<ww:url id="moveDownLink" action="moveProjectDown">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveDownLink}"><img src="static/img/arrow_down.png" alt="Move down" title="Move down" /></ww:a>

		<ww:url id="moveBottomLink" action="moveProjectBottom">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveBottomLink}"><img src="static/img/arrow_bottom.png" alt="Send to bottom" title="Send to bottom" /></ww:a>
		
		<ww:url id="unrankLink" action="unrankProject">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{unrankLink}"><img src="static/img/unrank.png" alt="Unrank" title="Unrank" /></ww:a>
	</display:column>
			
</display:table>
</div>
</div>
		
		
<c:if test="${!empty ongoingUnrankedProjects}">
<div class="subItems" style="width: 545px;" id="subItems_portfolioUnrankedProjects">
	<div class="subItemHeader">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="header">Unranked Projects
				</td>
			</tr>
		</table>
	</div>

	<div class="subItemContent">
	<display:table name="${ongoingUnrankedProjects}" id="row" style="width: 535px;">		
		<display:column title="Project Name">
			<ww:a
				href="contextView.action?contextObjectId=${row.id}&resetContextView=true&contextName=project">
				<c:out value="${row.product.name}: ${row.name}" />
			</ww:a>
		</display:column>
		
		<display:column title="Assignees">
		<c:set var="divId" value="${divId + 1}" scope="page" />
		<a href="javascript:toggleDiv('portfolioDiv_${divId}');">
		<img src="static/img/users.png" alt="Users" />
		<c:set var="assUserListLength" value="${fn:length(assignedUsers[row])}"/>
		<c:set var="nonAssUserListLength" value="${fn:length(nonAssignedUsers[row])}"/>		
		<c:set var="assUserCount" value="0" />
		<c:forEach items="${assignedUsers[row]}" var="usr">	
			<c:set var="assUserCount" value="${assUserCount + 1}" />
			<c:out value="${usr.initials}" /><c:if test="${(assUserCount != assUserListLength || (assUserCount == assUserListLength && nonAssUserListLength > 0))}">, </c:if>
		</c:forEach>
		<span style="color: rgb(255, 0, 0);">
		<c:set var="nonAssUserCount" value="0" />
		<c:forEach items="${nonAssignedUsers[row]}" var="usr">
			<c:set var="nonAssUserCount" value="${nonAssUserCount + 1}" />
			<c:out value="${usr.initials}" /><c:if test="${nonAssUserCount != nonAssUserListLength}">, </c:if>
		</c:forEach>
		</span>
		</a>
		<!-- User assignment table -->	
		<div id="portfolioDiv_${divId}" style="display: none;">
		<ww:form action="saveProjectAssignments">
		<ww:hidden name="projectId" value="${row.id}" /> 
		
		<table class="row_${row.id}">
		<tr>
		<td> 	  	 	  	
 	  		<display:table name="${userList}" id="user" defaultsort="2">
			<!-- Set id string used as key for maps -->	
			<c:set var="idstring" value="${row.id}-${user.id}" scope="request"/>

			<display:column title="">
			
			<c:set var="flag" value="0" scope="request"/>
			<c:forEach var="usr" items="${assignedUsers[row]}">
				<c:if test="${usr.id == user.id}"> 
					<c:set var="flag" value="1" scope="request"/>
				</c:if>
			</c:forEach>			
			<c:choose>			
			<c:when test="${flag == 1}">
				<input class="user_${user.id}" type="checkbox" name="selectedUserIds" value="${user.id}" checked="checked" onchange="toggleDiv('${idstring}')"/>
			</c:when>
			<c:otherwise>
				<input class="user_${user.id}" type="checkbox" name="selectedUserIds" value="${user.id}" onchange="toggleDiv('${idstring}')"/>			
			</c:otherwise>
			</c:choose>				

			</display:column>		
			
			<display:column title="Users" sortProperty="fullName">
				<!-- Check whether user is not assigned to project although has bli:s assigned -->
				<c:choose>	
				<c:when test="${unassignedUsers[idstring] == 1}"> 
				<a href="dailyWork.action?userId=${user.id}" class="unassigned">
				<c:out value="${user.fullName}" />
				</a> 
				</c:when>
				<c:otherwise>
				<a href="dailyWork.action?userId=${user.id}">
				<c:out value="${user.fullName}" />
				</a>
				</c:otherwise>
				</c:choose>
			</display:column>
			
			
			<display:column title="Effort Left">
				<c:out value="${loadLefts[idstring]}" />
			</display:column>
			
			<display:column title="Baseline load +/-">
			<!-- Check whether user is assigned. If is assigned -> show overhead -->
			<c:choose>	
			<c:when test="${flag == 1}"> 
				<div id="${idstring}" class="overhead">
			</c:when>
			<c:otherwise>
				<div id="${idstring}" class="overhead" Style="display: none;">
			</c:otherwise>
			</c:choose>
				<ww:label value="${row.defaultOverhead}" />+
				<ww:hidden name="assignments['${user.id}'].user.id" 
				value="${user.id}"  />
			  	<ww:textfield size="3"  name="assignments['${user.id}'].deltaOverhead" 
				value="${userOverheads[idstring]}" /> =
				<ww:label value="${totalUserOverheads[idstring]}" />
				</div>
			</display:column>
			</display:table>
		</td>
		<td class="teamselect">
			<script type="text/javascript">
			$(document).ready( function() {
				$('.row_${row.id} .teamselect ul').groupselect(teams, ".row_${row.id}");
			});
			</script>
			<label>Teams</label>
			<ul></ul>
		</td>
		</tr>
		</table>
		<ww:submit action="saveProjectAssignments" value="Save" />
		
		</ww:form>
		</div>
		<!-- User assignment table ends -->
		
		</display:column>		
		
		<display:column title="Action">
			<ww:url id="moveTopLink" action="moveProjectTop">
				<ww:param name="projectId" value="${row.id}" />
			</ww:url>
			<ww:a href="%{moveTopLink}"><img src="static/img/rank.png" alt="Rank to top" title="Rank to top" /></ww:a>
	
			<ww:url id="moveBottomLink" action="moveProjectBottom">
				<ww:param name="projectId" value="${row.id}" />
			</ww:url>
			<ww:a href="%{moveBottomLink}"><img src="static/img/unrank.png" alt="Rank to bottom" title="Rank to bottom" /></ww:a>
		</display:column>
		
	</display:table>
	</div>
	</div>
</c:if>


<%@ include file="./inc/_footer.jsp"%>