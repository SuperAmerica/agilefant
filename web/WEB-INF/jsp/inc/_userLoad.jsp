<%@ include file="./_taglibs.jsp"%>


<h2>Load for <c:out value="${user.fullName}" /></h2>


<c:if test="${!(empty backlogItemsForUserInProgress)}">
<div id="subItems">
<div id="subItemHeader">Load</div>
<div id="subItemContent">
<form>
Show <select name="weeksAhead"> 
<option value="1">1</option>
<option value="2">2</option>
<option value="3" selected="selected">3</option>
<option value="4">4</option>
<option value="5">5</option>
<option value="6">6</option>
<option value="7">7</option>
</option> 
</select> weeks ahead.
<ww:submit value="Change" />
</form>
<table id="item">
<tr class="odd">
	<td class="shortNameColumn"> &nbsp;</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn">Week-${week}</td>	
	</c:forEach>
<!--  	<td class="shortNameColumn"> Total</td> -->
</tr>
<tr class="odd">	
	<td class="shortNameColumn"> Effort Left</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn"> ${effortsLeftMap[week]}</td>	
	</c:forEach>
<!-- 	<td class="shortNameColumn"> ${overallTotals[0]}</td> -->
</tr>
<tr class="odd">	
	<td class="shortNameColumn"> Overhead</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn">${overheadsMap[week]}</td>	
	</c:forEach>
<!-- 	<td class="shortNameColumn"> ${overallTotals[1]}</td>  -->
</tr>
<tr class="even">	
	<td class="shortNameColumn"> Total</td>
	<c:forEach var="week" items="${weekNumbers}">
	<td class="shortNameColumn">${totalsMap[week]}</td>	
	</c:forEach>
<!--  	<td class="shortNameColumn"> ${overallTotals[2]}</td> -->
</tr>				
</table>
</div>
</div>
</c:if>