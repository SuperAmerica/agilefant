<%@ include file="./_taglibs.jsp"%>

<script type="text/javascript">
function show_detailed_loadtable() {
    document.getElementById('detailedLoadTable').style.display = "";
    document.getElementById('smallLoadTable').style.display = "none";
}
function show_small_loadtable() {
    document.getElementById('detailedLoadTable').style.display = "none";
    document.getElementById('smallLoadTable').style.display = "";
}
</script>

<div id="subItems">
<div id="subItemHeader">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td class="header">Load</td>
		<td class="icons">
		<a href="" onclick="show_detailed_loadtable(); return false;">
			<img src="static/img/plus.png" width="18" height="18" alt="Show details" title="Show details" />
		</a>
		<a href="" onclick="show_small_loadtable(); return false;">
			<img src="static/img/minus.png" width="18" height="18" alt="Hide details" title="Hide details" />
		</a>
		</td>
	</tr>
</table>
</div>
<div id="subItemContent">


<div id="detailedLoadTable" style="display: none;">
<table id="item">
<tr>
	<th class="loadTableBacklogColumn">Week</th>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<th class="loadTableWeekColumn"><c:out value="${weekNumber}" /></th>
	</c:forEach>
</tr>
<c:set var="rowClass" value="odd" />
<c:set var="nonEstimatedBLs" value="0"/>
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
		<c:set var="nonEstimatedBLs" value="${nonEstimatedBLs + 1}"/>
		<img src="static/img/nonestimated.png" alt="There are unestimated items" />
	</c:if>
	</td>
	<c:forEach items="${weekNumbers}" var="week">
		<td>
		<c:choose>
			<c:when test="${loadData.efforts[week] == '0h'}">-</c:when>
			<c:otherwise><c:out value="${loadData.efforts[week]}" /></c:otherwise>
		</c:choose>
		</td>
	</c:forEach>
</tr>
<c:if test="${aef:isProject(loadData.backlog)}">


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
		<td>
		<c:choose>
			<c:when test="${loadData.overheads[week] == '0h'}">-</c:when>
			<c:otherwise><c:out value="${loadData.overheads[week]}" /></c:otherwise>
		</c:choose>
		</td>
	</c:forEach>
</tr>
	</c:if>
</tr>
</c:forEach>

<tr>
	<th>Total</th>
	<c:forEach items="${weekNumbers}" var="week">
		<th>
		<c:choose>
			<c:when test="${totalsMap[week] == '0h'}">-</c:when>
			<c:when test="${dailyWorkLoadData.weeklyOverload[week] == false}">
				<span style="color: red;"><c:out value="${totalsMap[week]}" /></span>
			</c:when>
			<c:otherwise><c:out value="${totalsMap[week]}" /></c:otherwise>
		</c:choose>
	</th>	
	</c:forEach>
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
</tr>
<tr class="odd">
	<td>Effort 
	<c:if test="${nonEstimatedBLs > 0}">
		<img src="static/img/nonestimated.png" alt="There are unestimated items" />
	</c:if>
	</td>
	<c:forEach items="${weekNumbers}" var="weekNumber">
		<td>
			<c:choose>
			<c:when test="${dailyWorkLoadData.weeklyEfforts[weekNumber] == '0h'}">
				-
			</c:when>
			<c:otherwise>
				<c:out value="${dailyWorkLoadData.weeklyEfforts[weekNumber]}" />
			</c:otherwise>
		</c:choose>
	</td>
	</c:forEach>
</tr>
<tr class="even">
	<td>Overhead</td>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<td>
		<c:choose>
			<c:when test="${dailyWorkLoadData.weeklyOverheads[weekNumber] == '0h'}">
				-
			</c:when>
			<c:otherwise>
				<c:out value="${dailyWorkLoadData.weeklyOverheads[weekNumber]}" />
			</c:otherwise>
		</c:choose>
	</td>
	</c:forEach>
</tr>
<tr>
	<th>Total</th>
	<c:forEach items="${weekNumbers}" var="week">
		<th>
		<c:choose>
		<c:when test="${totalsMap[week] == '0h'}">
			-
		</c:when>
		<c:when test="${dailyWorkLoadData.weeklyOverload[week] == false}">
			<span style="color: red;"><c:out value="${totalsMap[week]}" /></span>
		</c:when>
		<c:otherwise>
			<c:out value="${totalsMap[week]}" />
		</c:otherwise>
	</c:choose>
	</th>	
	</c:forEach>
</tr>
</table>
</div>
<div class="legend">
<c:if test="${nonEstimatedBLs > 0}">
	<img src="static/img/nonestimated.png" alt="There are unestimated items" />
	<c:choose>
		<c:when test="${nonEstimatedBLs > 1}">
			<c:out value="Non-estimated BLI(s) in ${nonEstimatedBLs} backlogs." />
		</c:when>
		<c:otherwise>
			<c:out value="Non-estimated BLI(s) in ${nonEstimatedBLs} backlog." />
		</c:otherwise>
	</c:choose>
</c:if>
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
