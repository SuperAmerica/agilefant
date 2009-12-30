<%@include file="./_taglibs.jsp"%>

<script type='text/javascript'>
  $(function(){
    $("#tree").dynatree({
      keyboard: false,
      autoFocus: false,
      onActivate: function(dtnode) {
        $dtnode.data.title;
      }
    });
  });
</script>

<div id="tree">
<ul>
	<c:forEach items="${assignmentData}" var="project">
	<li class="expanded" data="icon: false"><a href="editProject.action?projectId=${project.id}">${project.title}</a>
		<ul>
			<c:forEach items="${project.children}" var="iteration">
			<li data="icon: false"><a href="editIteration.action?iterationId=${iteration.id}">${iteration.title}</a></li>
<!--			<ul>-->
<!--				<c:forEach items="${iteration.children}" var="story">-->
<!--				<li data="icon: false">${story.title}</li>-->
<!--				</c:forEach>-->
<!--			</ul>-->
			</c:forEach>
		</ul>
	</li>
	</c:forEach>
</ul>
</div>