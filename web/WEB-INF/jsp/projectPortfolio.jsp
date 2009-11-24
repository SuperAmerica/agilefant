<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="portfolio">

<link rel="stylesheet" href="static/css/timeplot.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/timeline.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/ether.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/event.css" type="text/css"/>

<script type="text/javascript" src="static/js/excanvas.js"></script>
<script type="text/javascript" src="static/js/simile-widgets.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/portfolio-eventsource.js"></script>

<script type="text/javascript">
window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
$(document).ready(function() {
  var controller = new PortfolioController({
    timelineElement: $("#timeline"),
    rankedProjectsElement: $("#rankedProjects"),
    unrankedProjectsElement: $("#unrankedProjects")
  });
});
</script>


<div style="width: 600px; float: left; margin-top: 5px; padding: 0.2em">
	<div id="timeline" style="margin-top: 57px">
	</div>
</div>
<div style="margin-left: 600px">
	<div id="rankedProjects">
	</div>
	<div id="unrankedProjects">
	</div>
</div>

</struct:htmlWrapper>
