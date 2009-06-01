<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Story progress bar"%>

<%@attribute name="story" type="fi.hut.soberit.agilefant.model.Story"%>
<%@attribute name="storyListContext"%>
<%@attribute name="dialogContext"%>
<%@attribute name="hasLink"%>

<c:if test="${hasLink}">
<a class="nameLink" onclick="handleTabEvent('storyTabContainer-${story.id}-${storyListContext}','${dialogContext}',${story.id},1,'${storyListContext}'); return false;">
</c:if>
		<ww:text name="story.state.${story.state}"/><br />
<c:if test="${hasLink}">
</a>
</c:if>
											
<c:choose>
	<c:when test="${story.state == 'NOT_STARTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="notStarted" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${story.state == 'STARTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="started" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${story.state == 'PENDING'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="pending" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${story.state == 'BLOCKED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="blocked" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${story.state == 'IMPLEMENTED'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="implemented" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
	<c:when test="${story.state == 'DONE'}" >
		<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
		<ww:param name="done" value="1" /> </ww:url> 
		<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
	</c:when>
</c:choose>
						