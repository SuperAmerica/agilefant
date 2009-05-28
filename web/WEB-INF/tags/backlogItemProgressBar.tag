<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Backlog item progress bar"%>

<%@attribute name="backlogItem" type="fi.hut.soberit.agilefant.model.BacklogItem"%>
<%@attribute name="bliListContext"%>
<%@attribute name="dialogContext"%>
<%@attribute name="hasLink"%>

<c:if test="${hasLink}">
<a class="nameLink" onclick="handleTabEvent('backlogItemTabContainer-${backlogItem.id}-${bliListContext}','${dialogContext}',${backlogItem.id},1,'${bliListContext}'); return false;">
</c:if>
<c:choose>
	<c:when test="${!(empty backlogItem.todos)}">	
		<aef:stateList backlogItemId="${backlogItem.id}" id="tsl" />	
		<c:choose><c:when test="${tsl['done'] == null}">0</c:when><c:otherwise>${tsl['done']}</c:otherwise></c:choose>
		 / ${fn:length(backlogItem.todos)} TODOs done
	</c:when>
	<c:otherwise>		
		<ww:text name="backlogItem.state.${backlogItem.state}"/><br />
	</c:otherwise>
</c:choose>
<c:if test="${hasLink}">
</a>
</c:if>
											
<c:choose>
	<c:when test="${backlogItem.state == 'NOT_STARTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="notStarted" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${backlogItem.state == 'STARTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="started" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${backlogItem.state == 'PENDING'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="pending" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${backlogItem.state == 'BLOCKED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="blocked" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${backlogItem.state == 'IMPLEMENTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="implemented" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${backlogItem.state == 'DONE'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="done" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
</c:choose>
							
<c:choose>
	<c:when test="${!(empty backlogItem.todos)}">					 
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
						