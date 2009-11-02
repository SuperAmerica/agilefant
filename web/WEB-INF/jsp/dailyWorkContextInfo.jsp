<%@ include file="./inc/_taglibs.jsp" %>
<div>
<h1 class="item-name">${item.name}</h1>
<div class="item-context">
<h2>Context</h2>

<ul class="context">
    <li style="margin-left:  0px"><a href="editProduct.action?productId=${iteration.parent.parent.id}"><span class="context-product"><c:out value="${iteration.parent.parent.name}"/></span></a>
    <li style="margin-left: 10px"><a href="editProject.action?projectId=${iteration.parent.id}"><span class="context-project"><c:out value="${iteration.parent.name}"/></span></a>
    <li style="margin-left: 20px"><a href="editIteration.action?iterationId=${iteration.id}"><span class="context-iteration"><c:out value="${iteration.name}"/></span></a></li>
</ul>

<div class="iteration-burndown-container">
<h2>Iteration burndown</h2>
<img class="iteration-burndown" src="drawSmallIterationBurndown.action?backlogId=${iteration.id}"/>
</div>
<c:if test="${! empty stories}" >
<div class="story-hierarchy">
<h2>Story hierarchy</h2>

<c:set var="indent" value="0"/>

<ul>

<c:forEach items="${stories}" var="story">
  <li style="margin-left: ${indent}px;"><a href="${story.link}"><c:out value="${story.name}"/></a></li>
     <c:set var="indent" value="${indent + 10}"/>
  </c:forEach>
</ul>
</div>
</c:if>
</div>
</div>
