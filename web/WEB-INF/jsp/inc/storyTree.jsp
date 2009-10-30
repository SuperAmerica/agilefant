<%@include file="./_taglibs.jsp" %>
<div class="hierarchyContainer">
<ul>
<c:forEach items="${stories}" var="item">
  <aef:storyTreeNode node="${item}" />
</c:forEach>
</ul>
</div>