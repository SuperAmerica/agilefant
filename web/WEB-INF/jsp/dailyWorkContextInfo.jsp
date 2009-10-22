<%@ include file="./inc/_taglibs.jsp" %>
<div>
<h1 class="task-name">${task.name}</h1>
<div class="task-context">
<h2>Task Context</h2>

<div class="context">
<span class="context-product"><c:out value="${iteration.parent.parent.name}"/></span> &mdash;
<span class="context-project"><c:out value="${iteration.parent.name}"/></span> &mdash;
<span class="context-iteration"><c:out value="${iteration.name}"/></span>
</div>

<div class="iteration-burndown-container">
<h2>Iteration burndown</h2>
<img class="iteration-burndown" src="drawSmallIterationBurndown.action?backlogId=${iteration.id}"/>
</div>
<c:if test="${! empty stories}" >
<div class="story-hierarchy">
<h2>Story hierarchy</h2>
<ul>
<c:forEach items="${stories}" var="story">
<li><c:out value="${story.name}"/></li>
</c:forEach>
</ul>
</div>
</c:if>
</div>
</div>
