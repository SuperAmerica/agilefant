<%@ include file="./_taglibs.jsp"%>

<div id="subItems">
<div id="subItemHeader">
<table cellspacing="0" cellpadding="0">
<tr>
<td class="header">
Load
<a href="" id="loadTableHideLink" style="display: none;" onclick="show_small_loadtable(); return false;">(hide details)</a>
<a href="" id="loadTableShowLink" onclick="show_detailed_loadtable(); return false;">(show details)</a>
</td>
</tr>
</table>
</div>
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
		<td>
		<c:choose>
			<c:when test="${loadData.efforts[week] == '0h'}">&#8212;</c:when>
			<c:otherwise><c:out value="${loadData.efforts[week]}" /></c:otherwise>
		</c:choose>
		</td>
	</c:forEach>
	<td>
		<c:choose>
			<c:when test="${loadData.totalEffort == '0h'}">&#8212;</c:when>
			<c:otherwise><c:out value="${loadData.totalEffort}" /></c:otherwise>
		</c:choose>
	</td>
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
		<td>
		<c:choose>
			<c:when test="${loadData.overheads[week] == '0h'}">&#8212;</c:when>
			<c:otherwise><c:out value="${loadData.overheads[week]}" /></c:otherwise>
		</c:choose>
		</td>
	</c:forEach>
	<td>
		<c:choose>
			<c:when test="${loadData.totalOverhead == '0h'}">&#8212;</c:when>
			<c:otherwise><c:out value="${loadData.totalOverhead}" /></c:otherwise>
		</c:choose>
	</td>
</tr>
	</c:if>
</tr>
</c:forEach>
<tr>
	<th>Total</th>
	<c:forEach items="${weekNumbers}" var="week">
		<th>
		<c:choose>
			<c:when test="${totalsMap[week] == '0h'}">&#8212;</c:when>
			<c:when test="${dailyWorkLoadData.weeklyOverload[week] == 0}">
				<span style="color: red;"><c:out value="${totalsMap[week]}" /></span>
			</c:when>
			<c:otherwise><c:out value="${totalsMap[week]}" /></c:otherwise>
		</c:choose>
	</th>	
	</c:forEach>
	<th>
		<c:choose>
			<c:when test="${dailyWorkLoadData.overallTotal == '0h'}">&#8212;</c:when>
			<c:otherwise><c:out value="${dailyWorkLoadData.overallTotal}" /></c:otherwise>
		</c:choose>
	</th>
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
		<td>
			<c:choose>
			<c:when test="${dailyWorkLoadData.weeklyEfforts[weekNumber] == '0h'}">
				&#8212;
			</c:when>
			<c:otherwise>
				<c:out value="${dailyWorkLoadData.weeklyEfforts[weekNumber]}" />
			</c:otherwise>
		</c:choose>
	</td>
	</c:forEach>
	<td>
		<c:choose>
			<c:when test="${dailyWorkLoadData.totalEffort == '0h'}">
				&#8212;
			</c:when>
			<c:otherwise>
				<c:out value="${dailyWorkLoadData.totalEffort}" />
			</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr class="even">
	<td>Overhead</td>
	<c:forEach items="${weekNumbers}" var="weekNumber">
	<td>
		<c:choose>
			<c:when test="${dailyWorkLoadData.weeklyOverheads[weekNumber] == '0h'}">
				&#8212;
			</c:when>
			<c:otherwise>
				<c:out value="${dailyWorkLoadData.weeklyOverheads[weekNumber]}" />
			</c:otherwise>
		</c:choose>
	</td>
	</c:forEach>
	<td>
		<c:choose>
		<c:when test="${dailyWorkLoadData.totalOverhead == '0h'}">
			&#8212;
		</c:when>
		<c:otherwise>
			<c:out value="${dailyWorkLoadData.totalOverhead}" />
		</c:otherwise>
	</c:choose>
	</td>
</tr>
<tr>
	<th>Total</th>
	<c:forEach items="${weekNumbers}" var="week">
		<th>
		<c:choose>
		<c:when test="${totalsMap[week] == '0h'}">
			&#8212;
		</c:when>
		<c:when test="${dailyWorkLoadData.weeklyOverload[week] == 0}">
			<span style="color: red;"><c:out value="${totalsMap[week]}" /></span>
		</c:when>
		<c:otherwise>
			<c:out value="${totalsMap[week]}" />
		</c:otherwise>
	</c:choose>
	</th>	
	</c:forEach>
	<th>
		<c:choose>
		<c:when test="${totalsMap[week] == '0h'}">
			&#8212;
		</c:when>
		<c:otherwise>
			<c:out value="${dailyWorkLoadData.overallTotal}" />
		</c:otherwise>
	</c:choose>
	</th>
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
