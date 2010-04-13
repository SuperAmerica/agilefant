<%@tag description="Agilefant main tabs" %>

<%@attribute name="navi" required="true" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
$(document).ready(function() {
  var selector = '#navitab-${navi}';
  $(selector).addClass('navitab-selected');
});
</script>

<ul>

<!-- Daily Work -->
<c:if test="${settings.dailyWork}">

<li id="navitab-dailyWork">
  <a href="contextView.action?contextName=dailyWork&resetContextView=true">
  <span>
  <img src="static/img/dailyWork.png" alt="Daily Work" />
  Daily Work
  </span>
  </a>
</li>
</c:if>

<!-- Backlogs -->
<li id="navitab-backlog">
  <a href="contextView.action?contextName=${currentBacklogContext}&contextObjectId=${currentBacklogId}&resetContextView=true">
  <span>
  <img src="static/img/backlog.png" alt="Backlogs" />
  Backlogs
  </span>
  </a>
</li>


<%-- Timesheet --%>
<c:if test="${settings.hourReportingEnabled}">
<li id="navitab-timesheet">
  <a href="contextView.action?contextName=timesheet&resetContextView=true">
  <span>
  <img src="static/img/timesheets.png" alt="Timesheets" />
  Timesheets
  </span>
  </a>
</li>
</c:if>

<!-- Dev Portfolio -->
<c:if test="${settings.devPortfolio}">
<li id="navitab-portfolio">
  <a href="contextView.action?contextName=projectPortfolio&resetContextView=true">
  <span>
  <img src="static/img/portfolio.png" alt="Dev Portfolio" />
  Dev Portfolio
  </span>
  </a>
</li>
</c:if>

<%-- Settings --%>
<li id="navitab-settings">
    <a href="contextView.action?contextName=settings&resetContextView=true">
    <span>
    <img src="static/img/settings.png" alt="Administration" />
    Administration
    </span>
    </a>
</li>

</ul>