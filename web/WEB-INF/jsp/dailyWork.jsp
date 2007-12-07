<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<!-- Author:	rjokelai
	 Version:	1.3.1
-->

<aef:menu navi="1" /> 
	<ww:actionerror/>
	<ww:actionmessage/>

<c:set var="currentAction" value="dailyWork" scope="session" />


<%@ include file="./inc/_workInProgress.jsp" %>

<%@ include file="./inc/_dailyWorkIterations.jsp" %>

<%@ include file="./inc/_dailyWorkProjects.jsp" %>

<%@ include file="./inc/_footer.jsp" %>
