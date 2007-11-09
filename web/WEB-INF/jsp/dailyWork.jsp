<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<!-- Author:	rjokelai
	 Version:	1.3.1
-->

<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>

<c:set var="currentAction" value="dailyWork" scope="session" />

Currently viewing daily work for user:

<ww:form>
	<ww:select name="userId" list="userList" listValue="fullName" listKey="id"
		value="${userId}"/>
	<ww:submit value="Change" />
</ww:form>


<%@ include file="./inc/_workInProgress.jsp" %>

<%@ include file="./inc/_dailyWorkIterations.jsp" %>

<%@ include file="./inc/_footer.jsp" %>
