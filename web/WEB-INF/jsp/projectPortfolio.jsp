<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="portfolio">

<link rel="stylesheet" href="static/css/timeplot.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/timeline.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/ether.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/event.css" type="text/css"/>

<script type="text/javascript" src="static/js/excanvas.js"></script>
<script type="text/javascript" src="static/js/simile-widgets.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/LoadPlot.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeplot-source.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/user-load-timeline.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/init-load.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/load-plot.js"></script>

<script type="text/javascript">
$(document).ready(function() {
  var controller = new PortfolioController({
    timelineElement: $("#timeline"),
    projectListElement: $("#projects")
  });
});
$(window).resize(function() {
  $("div#timeline").each(function() {
    $(this).data("timeline").repaint();
  });
});
</script>


<div id="timeline" class="structure-main-block" style="height: 500px">&nbsp;</div>
<div id="projects" class="structure-main-block">
</div>

</struct:htmlWrapper>