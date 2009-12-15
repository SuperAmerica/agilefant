<%@include file="./_taglibs.jsp" %>
<ul>
<c:forEach items="${stories}" var="item">
  <aef:storyTreeNode node="${item}" />
</c:forEach>
</ul>