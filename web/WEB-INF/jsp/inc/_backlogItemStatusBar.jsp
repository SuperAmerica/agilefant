<a class="bliNameLink" onclick="handleTabEvent('backlogItemTabContainer-${row.id}-${bliListContext}','bli',${row.id},1,'${bliListContext}');">
<c:choose>
	<c:when test="${!(empty row.tasks)}">		
		${fn:length(row.tasks)} TODOs,
		<aef:percentDone backlogItemId="${row.id}" />% done<br />
	</c:when>
	<c:otherwise>		
		<ww:text name="backlogItem.state.${row.state}"/><br />
	</c:otherwise>
</c:choose>		
</a>
											
<c:choose>
	<c:when test="${row.state == 'NOT_STARTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="notStarted" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${row.state == 'STARTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="started" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${row.state == 'PENDING'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="pending" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${row.state == 'BLOCKED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="blocked" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${row.state == 'IMPLEMENTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="implemented" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${row.state == 'DONE'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="done" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
</c:choose>
							
<c:choose>
	<c:when test="${!(empty row.tasks)}">					
		<aef:stateList backlogItemId="${row.id}" id="tsl" /> 
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
			<ww:param name="notStarted" value="${tsl['notStarted']}" />
			<ww:param name="started" value="${tsl['started']}" />
			<ww:param name="pending" value="${tsl['pending']}" />
			<ww:param name="blocked" value="${tsl['blocked']}" />
			<ww:param name="implemented" value="${tsl['implemented']}" />
			<ww:param name="done" value="${tsl['done']}" />
		</ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>															
	</c:when>
</c:choose>			
						