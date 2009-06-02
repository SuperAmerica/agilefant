<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<!-- Author:	ialehto
	 Version:	1.5.1
-->
<c:set var="fileTimestamp" value="build_1561" />

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<link rel="shortcut icon" href="static/img/favicon.png" type="image/png" />
<link rel="stylesheet" href="static/css/tiny.css?${fileTimestamp}" type="text/css"/>
<link rel="stylesheet" href="static/css/v5.css?${fileTimestamp}" type="text/css"/>
<link rel="stylesheet" href="static/css/datepicker.css?${fileTimestamp}" type="text/css"/>
<title>
Agilefant
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<script type="text/javascript" src="static/js/datacache.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/generic.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery.cookie.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery.treeview.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery.treeview.async.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery-ui.min.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery.validate.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/multiselect.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/taskrank.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/date.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/datepicker.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/jquery.wysiwyg.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/validationRules.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/inlineEdit.js?${fileTimestamp}"></script>
<script type="text/javascript" src="static/js/userChooser.js?${fileTimestamp}"></script>

<script type="text/javascript">
$(document).ready(function() {
    if(document.cookie.indexOf("SPRING_SECURITY_HASHED_REMEMBER_ME_COOKIE") == -1) {
        var sessionLength = <%=session.getMaxInactiveInterval()%>*1000;
        setTimeout('reloadPage()',sessionLength+5);
    }
});
</script>

<script type="text/javascript" src="static/js/onLoad.js?${fileTimestamp}"></script>
</head>

<!-- Icons from http://sweetie.sublink.ca/ -->

<body style="font: 10px Verdana, sans-serif">

<aef:hourReporting id="hourReport" />

<c:set var="currentAction" value="dailyWork" scope="session" />
<c:set var="dailyWorkUserId" value="${userId}" scope="session" />

<table width="340px">
<tr>
<td style="vertical-align: bottom">
<img src="static/img/fant_small.png"
	alt="logo" width="37px" height="53px" style="margin-left: 10px;"/>
</td>
<td style="vertical-align: bottom">
<span style="font: 12pt Arial, sans-serif; font-weight: bold; font-style: italic">Agilefant</span><br/>
<div style="margin-left: 20px">
<span>Daily Work of</span>
<ww:form>
<ww:select list="enabledUsers"
	listKey="id" listValue="fullName" name="userId" value="${user.id}"
	onchange="this.form.submit();" />
</ww:form>
</div>
</td>
<td style="vertical-align: bottom">
<form action="j_spring_security_logout" method="post">
<input name="exit" type="submit" value="logout" />
</form>
</td>
<td style="vertical-align: bottom">
<div style="overflow: hidden;"><img src="drawLoadMeter.action?userId=${user.id}" height="70px" width="70px" style="margin-bottom: -10px"/>
</td>
</tr>
</table>

<c:choose>
	<c:when test="${!(empty backlogItemsForUserInProgress)}" >
		<%@ include file="./inc/_tinyWorkInProgress.jsp" %>
	</c:when>
	<c:otherwise>
<ww:url id="backlogsLink" action="contextView" includeParams="none">
	<ww:param name="contextName" value="${currentContext}" />
	<ww:param name="contextObjectId" value="${currentPageId}" />
	<ww:param name="resetContextView" value="true" />
</ww:url>
<p>There are no stories or tasks assigned to user <c:out value="${user.fullName}" />.</p>
<p>Explore <ww:a href="%{backlogsLink}">backlogs</ww:a> to find some items.</p>
</c:otherwise>
</c:choose>

</body>
</html>