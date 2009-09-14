<%@ include file="./inc/_taglibs.jsp" %>


<struct:htmlWrapper navi="dailyWork">

<c:set var="currentAction" value="dailyWork" scope="session" />
<c:set var="dailyWorkUserId" value="${userId}" scope="session" />

<ww:form method="get">
<h2>The daily work of <ww:select list="enabledUsers"
    listKey="id" listValue="fullName" name="userId" value="%{user.id}"
    onchange="this.form.submit();" /></h2>
</ww:form>

<%@ include file="./inc/_userLoad.jsp" %>

<c:choose>
<c:when test="${!(empty assignedTasks) or !(empty nextTasks)}" >

<script type="text/javascript">

var dailyWorkController = null;
var nextWorkController = null;

$(document).ready(function() {
    $("#backlogInfo").tabs();
    dailyWorkController = new DailyWorkController({
        id: '${userId}', 
        type: 'current',
        myWorkListElement: $("#my-work-div"),
        whatsNextListElement: $("#whats-next-div")
    });
});
</script>

<div id="whats-next-div"></div>
<div id="my-work-div"></div>

</c:when>
<c:otherwise>

<ww:url id="backlogsLink" action="contextView" includeParams="none">
    <ww:param name="contextName" value="%{currentBacklogContext}" />
    <ww:param name="contextObjectId" value="%{currentBacklogId}" />
    <ww:param name="resetContextView" value="true" />
</ww:url>

<%-- TODO: fix link --%>
<p>There are no stories or tasks assigned to user <c:out value="${user.fullName}" />.</p> 
<p>Explore <a href="contextView.action?contextName=${currentBacklogContext}&contextObjectId=${currentBacklogId}&resetContextView=true">backlogs</a> to find some items.</p>

</c:otherwise>
</c:choose>

</struct:htmlWrapper>