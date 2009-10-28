<%@ include file="./inc/_taglibs.jsp" %>
<div>
<h1 class="task-name">${task.name}</h1>
<div class="task-context">
<h2>Task Context</h2>

<ul class="context">
    <li><a href="editProduct.action?productId=${iteration.parent.parent.id}"><span class="context-product"><c:out value="${iteration.parent.parent.name}"/></span></a>
    <ul class="context">
        <li><a href="editProject.action?projectId=${iteration.parent.id}"><span class="context-project"><c:out value="${iteration.parent.name}"/></span></a>
    
        <ul class="context">
            <li>
                <a href="editIteration.action?iterationId=${iteration.id}"><span class="context-iteration"><c:out value="${iteration.name}"/></span></a>
            </li>
        </ul>
        </li>
    </ul>
    </li>
</ul>

<div class="iteration-burndown-container">
<h2>Iteration burndown</h2>
<img class="iteration-burndown" src="drawSmallIterationBurndown.action?backlogId=${iteration.id}"/>
</div>
<c:if test="${! empty stories}" >
<div class="story-hierarchy">
<h2>Story hierarchy</h2>
<ul>
<c:forEach items="${stories}" var="story">
<li><a href="${story.link}"><c:out value="${story.name}"/></a></li>
</c:forEach>
</ul>
</div>
</c:if>
</div>
</div>
