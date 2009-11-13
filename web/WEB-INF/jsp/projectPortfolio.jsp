<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="portfolio">

<script type="text/javascript">
$(document).ready(function() {
  var controller = new PortfolioController({
    timelineElement: $("#timeline"),
    rankedProjectsElement: $("#rankedProjects"),
    unrankedProjectsElement: $("#unrankedProjects")
  });
});
</script>


<div id="timeline" class="structure-main-block">
</div>
<div id="rankedProjects" class="structure-main-block">
</div>
<div id="unrankedProjects" class="structure-main-block">
</div>

</struct:htmlWrapper>
