<%@ include file="./inc/_taglibs.jsp" %>
<div>
<h1 class="task-name">${task.name}</h1>
<div class="task-context">
<h2>Task Context</h2>
<div class="context-product"><span class="label">Product:</span> <c:out value="${iteration.parent.parent.name}"/></div>
<div class="context-project"><span class="label">Project:</span> <c:out value="${iteration.parent.name}"/></div>

<div class="context-iteration"><span class="label">Iteration:</span> <c:out value="${iteration.name}"/>
<div class="iteration-burndown-container">
<img class="iteration-burndown" src="drawSmallIterationBurndown.action?backlogId=${iteration.id}"/>
</div>
</div>

<c:if test="${! empty stories}" >
<div class="task-context">
<h3>Story hierarchy</h3>
<ul>
<c:forEach items="${stories}" var="story">
<li><c:out value="${story.name}"/></li>
</c:forEach>
</ul>
</div>
</c:if>
</div>
<c:set var="currentAction" value="dailyWork" scope="session" />
<c:set var="dailyWorkUserId" value="${userId}" scope="session" />
</div>