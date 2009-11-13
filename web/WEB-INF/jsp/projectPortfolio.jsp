<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="portfolio">

<script type="text/javascript">
$(document).ready(function() {
  var controller = new PortfolioController({
    timelineElement: $("#timeline"),
    projectListElement: $("#projects")
  });
});
</script>

<div id="timeline" class="structure-main-block">
</div>
<div id="projects" class="structure-main-block">
</div>

</struct:htmlWrapper>