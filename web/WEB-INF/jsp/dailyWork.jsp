<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<!-- Author:	rjokelai
	 Version:	1.3.1
-->

<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>

<c:set var="currentAction" value="dailyWork" scope="session" />
<c:set var="dailyWorkUserId" value="${userId}" scope="session" />


<ww:form>
View daily work for user: <ww:select list="userList"
	listKey="id" listValue="fullName" name="userId" value="${user.id}"/>
<ww:submit value="Change" />
</ww:form>

<%@ include file="./inc/_userLoad.jsp" %>
<c:choose>
<c:when test="${!((empty backlogItemsForUserInProgress) &&
		(empty iterations) &&
		(empty projects))}" >



<%@ include file="./inc/_workInProgress.jsp" %>

<%@ include file="./inc/_dailyWorkIterations.jsp" %>

<%@ include file="./inc/_dailyWorkProjects.jsp" %>

</c:when>
<c:otherwise>
<ww:url id="backlogsLink" action="contextView" includeParams="none">
	<ww:param name="contextName" value="${currentContext}" />
	<ww:param name="contextObjectId" value="${currentPageId}" />
	<ww:param name="resetContextView" value="true" />
</ww:url>
<p>There are no backlog items assigned to user <c:out value="${user.fullName}" />.</p>
<p>Explore <ww:a href="%{backlogsLink}">backlogs</ww:a> to find some items.</p>
</c:otherwise>
</c:choose>

<%@ include file="./inc/_footer.jsp" %>
