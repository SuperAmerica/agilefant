<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Work test page</title>
</head>
<body>
	<c:forEach items="${works}" var="work">
		<p>
			${work.task.name} - ${work.task.id} - ${work.effort} - ${work.created} - ${work.actor.name} - ${work.effort.time}
		</p>
	</c:forEach>
</body>
</html>