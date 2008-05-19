<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<!-- Author:	aptoivon
	 Version:	1.3.1
-->

<aef:menu navi="portfolio" />
<ww:actionerror />
<ww:actionmessage />
<script type="text/javascript" src="static/js/jquery-1.2.2.js"></script>
<script type="text/javascript" src="static/js/multiselect.js"></script>
<script type="text/javascript">
<ww:set name="teamList" value="#attr.teamList" />
var teams = [<aef:teamJson items="${teamList}"/>]
</script>
<h2>Development Portfolio</h2>

<h4>Ranked Projects</h4>


<p><display:table name="${ongoingRankedProjects}" id="row">
	<display:column title="Rank">
		<c:out value="${row_rowNum}" />
	</display:column>
	<display:column title="Project Name">
		<ww:a
			href="contextView.action?contextObjectId=${row.id}&resetContextView=true&contextName=project">
			<c:out value="${row.product.name}: ${row.name}" />
		</ww:a>
	</display:column>
	
		<display:column title="Users">
		<c:set var="divId" value="${divId + 1}" scope="page" />
		<a href="javascript:toggleDiv(${divId});">
			<img src="static/img/users.png" alt="Users" />
			<c:out value="${summaryUserData[row]}" />
			<c:if test="${summaryUnassignedUserData[row] > 0}">
				<span style="color: rgb(255, 0, 0);"> + 
				<c:out value="${summaryUnassignedUserData[row]}" /> unassigned
				</span>
			</c:if>
		</a>
		<!-- User assignment table -->	
		<div id="${divId}" style="display: none;">
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
			
			<display:column title="Overhead +/-">
			
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
		<display:column title="Load Left">
			<c:out value="${aef: out(summaryLoadLeftData[row])}" escapeXml="false" />
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
			
</display:table></p>
		
		
<c:if test="${!empty ongoingUnrankedProjects}">
	<h4>Unranked Projects</h4>
	<p><display:table name="${ongoingUnrankedProjects}" id="row">
		<display:column title="Project Name">
			<ww:a
				href="contextView.action?contextObjectId=${row.id}&resetContextView=true&contextName=project">
				<c:out value="${row.product.name}: ${row.name}" />
			</ww:a>
		</display:column>
		
		<display:column title="Users">
		<c:set var="divId" value="${divId + 1}" scope="page" />
		<a href="javascript:toggleDiv(${divId});">
		<img src="static/img/users.png" alt="Users" />
		<c:out value="${summaryUserData[row]}" />
		<c:if test="${summaryUnassignedUserData[row] > 0}">
			<span style="color: rgb(255, 0, 0);"> + 
		<c:out value="${summaryUnassignedUserData[row]}" /> unassigned
		</span>
		</c:if>
		</a>
		<!-- User assignment table -->	
		<div id="${divId}" style="display: none;">
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
			
			<display:column title="Overhead +/-">
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
		<display:column title="Load Left">
			<c:out value="${summaryLoadLeftData[row]}" escapeXml="false" />
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
		
	</display:table></p>
</c:if>

<h2>Project types</h2>
<p><ww:url id="createProjectTypeLink" action="createProjectType" />
<ww:a href="%{createProjectTypeLink}">Create new &raquo;</ww:a></p>

<p><c:choose>
	<c:when test="${empty projectTypes}">
				No project types were found.
			</c:when>
	<c:otherwise>
		<display:table class="listTable" name="${projectTypes}" id="row"
			requestURI="projectPortfolio.action" defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="name">
				<ww:url id="editLink" action="editProjectType">
					<ww:param name="projectTypeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>
			<display:column sortable="false" title="Actions">
				<ww:url id="deleteLink" action="deleteProjectType">
					<ww:param name="projectTypeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
			</display:column>
		</display:table>

	</c:otherwise>
</c:choose></p>

<%@ include file="./inc/_footer.jsp"%>