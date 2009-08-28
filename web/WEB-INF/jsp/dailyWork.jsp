<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<!-- Author:	rjokelai
	 Version:	1.3.1
-->

<aef:menu navi="dailyWork" title="Daily Work"/> 
	<ww:actionerror/>
	<ww:actionmessage/>

<c:set var="currentAction" value="dailyWork" scope="session" />
<c:set var="dailyWorkUserId" value="${userId}" scope="session" />

<%@include file="./inc/includeDynamics.jsp"%>

<ww:form>
<h2>The daily work of <ww:select list="enabledUsers"
    listKey="id" listValue="fullName" name="userId" value="%{user.id}"
    onchange="this.form.submit();" /></h2>
</ww:form>

<%@ include file="./inc/_userLoad.jsp" %>

<c:choose>
<c:when test="${!(empty assignedTasks)}" >

<script type="text/javascript">
$(document).ready(function() {
    $("#backlogInfo").tabs();
    var controller = new DailyWorkController({
        id: '${userId}', 
        dailyWorkElement: $("#daily-work-div")
    });
});
</script>


<ww:url id="backlogsLink" action="contextView" includeParams="none">
	<ww:param name="contextName" value="%{currentContext}" />
	<ww:param name="contextObjectId" value="%{currentPageId}" />
	<ww:param name="resetContextView" value="true" />
</ww:url>

<div id="daily-work-div"></div>

</c:when>
<c:otherwise>

<p>There are no stories or tasks assigned to user <c:out value="${user.fullName}" />.</p> 
<p>Explore <ww:a href="%{backlogsLink}">backlogs</ww:a> to find some items.</p>

</c:otherwise>
</c:choose>

<%@ include file="./inc/_footer.jsp" %>
