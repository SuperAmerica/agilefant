<%@ include file="./_taglibs.jsp"%>

<div id="subItems">
<div id="subItemHeader">Load
<a href="" id="loadTableHideLink" style="display: none;" onclick="show_small_loadtable(); return false;">(hide details)</a>
<a href="" id="loadTableShowLink" onclick="show_detailed_loadtable(); return false;">(show details)</a></div>
<div id="subItemContent">

<script type="text/javascript">
function show_detailed_loadtable() {
	document.getElementById('loadTableHideLink').style.display = "";
	document.getElementById('loadTableShowLink').style.display = "none";
	document.getElementById('detailedLoadTable').style.display = "";
	document.getElementById('smallLoadTable').style.display = "none";
}
function show_small_loadtable() {
	document.getElementById('loadTableHideLink').style.display = "none";
	document.getElementById('loadTableShowLink').style.display = "";
	document.getElementById('detailedLoadTable').style.display = "none";
	document.getElementById('smallLoadTable').style.display = "";
}
</script>

<div id="detailedLoadTable" style="display: none;">
<table id="item">
<tr>
	<th>Week</th>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<th><c:out value="${weekNumber}" /></th>
	</c:forEach>
	<th>Total</th>
</tr>
<c:set var="rowClass" value="odd" />
<c:forEach items="${dailyWorkLoadData.backlogs}" var="backlog">
<c:set var="loadData" value="${loadDatas[backlog]}"/>
<tr class="${rowClass}">
<c:choose>
	<c:when test="${rowClass == 'odd'}">
		<c:set var="rowClass" value="even" />
	</c:when>
	<c:otherwise>
		<c:set var="rowClass" value="odd" />
	</c:otherwise>
</c:choose>
	<ww:url id="editLink" action="contextView" includeParams="none">
		<ww:param name="contextObjectId" value="${loadData.backlog.id}" />
		<ww:param name="resetContextView" value="true" />
	</ww:url>

	<td>
	<c:choose>
		<c:when test="${aef:isIteration(loadData.backlog)}">
			&nbsp;&nbsp;<ww:a href="%{editLink}&contextName=iteration">
			<c:out value="${loadData.backlog.name}" /></ww:a>
		</c:when>
		<c:otherwise>
			<ww:a href="%{editLink}&contextName=project">
			<c:out value="${loadData.backlog.name}" /></ww:a>
		</c:otherwise>
	</c:choose>
	<c:if test="${loadData.unestimatedItems == true}">
		<img src="static/img/unassigned.png" alt="There are unestimated items" />
	</c:if>
	</td>
	<c:forEach items="${weekNumbers}" var="week">
		<td><c:out value="${loadData.efforts[week]}" /></td>
	</c:forEach>
	<td><c:out value="${loadData.totalEffort}" /></td>
	<c:if test="${aef:isProject(loadData.backlog)}">
</tr>
<tr class="${rowClass}">
<c:choose>
	<c:when test="${rowClass == 'odd'}">
		<c:set var="rowClass" value="even" />
	</c:when>
	<c:otherwise>
		<c:set var="rowClass" value="odd" />
	</c:otherwise>
</c:choose>
	<td>&nbsp;&nbsp;Overhead</td>
	<c:forEach items="${weekNumbers}" var="week">
		<td><c:out value="${loadData.overheads[week]}" /></td>
	</c:forEach>
	<td><c:out value="${loadData.totalOverhead}" /></td>
</tr>
	</c:if>
</tr>
</c:forEach>
<tr>
	<th>Total</th>
	<c:forEach items="${weekNumbers}" var="week">
		<th><c:out value="${totalsMap[week]}" /></th>	
	</c:forEach>
	<th><c:out value="${dailyWorkLoadData.overallTotal}" /></th>
</tr>
</table>
</div>



<div id="smallLoadTable">
<table id="item">
<tr>
	<th>Week</th>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<th><c:out value="${weekNumber}" /></th>
	</c:forEach>
	<th>Total</th>
</tr>
<tr class="odd">
	<td>Effort</td>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<td><c:out value="${dailyWorkLoadData.weeklyEfforts[weekNumber]}" /></td>
	</c:forEach>
	<td><c:out value="${dailyWorkLoadData.totalEffort}" /></td>
</tr>
<tr class="even">
	<td>Overhead</td>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<td><c:out value="${dailyWorkLoadData.weeklyOverheads[weekNumber]}" /></td>
	</c:forEach>
	<td><c:out value="${dailyWorkLoadData.totalOverhead}" /></td>
</tr>
<tr>
	<th>Total</th>
	<c:forEach items="${weekNumbers}" var="week">
		<th><c:out value="${totalsMap[week]}" /></th>	
	</c:forEach>
	<th><c:out value="${dailyWorkLoadData.overallTotal}" /></th>
</tr>
</table>
</div>

<%--
<table id="item">

<tr class="odd">
	<td class="shortNameColumn"> &nbsp;</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn">Week ${week}</td>	
	</c:forEach>
 	<td class="shortNameColumn"> Total</td> 
</tr>
<tr class="odd">	
	<td class="shortNameColumn"> Effort Left</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn"> ${effortsLeftMap[week]}</td>	
	</c:forEach>
 	<td class="shortNameColumn"> ${overallTotals[0]}</td> 
</tr>
<tr class="odd">	
	<td class="shortNameColumn"> Overhead</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn">${overheadsMap[week]}</td>	
	</c:forEach>
 	<td class="shortNameColumn"> ${overallTotals[1]}</td>  
</tr>
<tr class="even">	
	<td class="shortNameColumn"> Total</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn">${totalsMap[week]}</td>	
	</c:forEach>
  	<td class="shortNameColumn"> ${overallTotals[2]}</td> 
</tr>				
</table>
--%>
</div>
</div>
