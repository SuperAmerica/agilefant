<%@tag description="Wrapper for the Agilefant html structure" %>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<%@attribute name="navi" fragment="false" required="true"%>

<%@attribute name="headerContent" fragment="true" %>
<%@attribute name="menuContent" fragment="true" %>

<%@attribute name="hideLogout" fragment="false" required="false" %>
<%@attribute name="hideControl" fragment="false" required="false" %>
<%@attribute name="hideMenu" fragment="false" required="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
  <title>Agilefant</title>
  <link rel="stylesheet" type="text/css" href="static/css/structure.css?<ww:text name="struts.agilefantReleaseId" />" />
  <!--[if IE 7]><link rel="stylesheet" type="text/css" href="static/css/IE7styles.css?<ww:text name="struts.agilefantReleaseId" />" /><![endif]-->
  <!--[if IE 8]><link rel="stylesheet" type="text/css" href="static/css/IE8styles.css?<ww:text name="struts.agilefantReleaseId" />" /><![endif]-->  
  
  <link rel="shortcut icon" href="static/img/favicon.png" type="image/png" />
  
  <script type="text/javascript" src="static/js/datacache.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/generic.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.cookie.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery-ui.min.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.dynatree.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.validate.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/date.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/datepicker.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.wysiwyg.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/validationRules.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/backlogChooser.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/backlogSelector.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.tree.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.autoSuggest.minified.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  
  
  <script type="text/javascript" src="static/js/dynamics/controller/PageController.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/dynamics/controller/MenuController.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  
  <c:if test="${settings != null}">
  <script type="text/javascript">
  $.ajaxSetup({ traditional: true }); //force jquery back to < 1.4 series style data serialization
  Configuration.setConfiguration({ timesheets: ${settings.hourReportingEnabled} });
  </script>
  </c:if>
  
  <script type="text/javascript">
  $(document).ready(function() {
    PageController.initialize(${currentUserJson});
  });
  </script>
  
  <%@include file="../../jsp/inc/includeDynamics.jsp" %>

  <script type="text/javascript" src="static/js/onLoad.js?<ww:text name="struts.agilefantReleaseId" />"></script>

</head>

<body>

<div id="outerWrapper"><!-- Start of outer wrapper -->

<div id="headerWrapper">
  <c:choose>
  <c:when test="${headerContent != null}">
    <jsp:invoke fragment="headerContent" />
  </c:when>
  <c:otherwise>
    <struct:header />
  </c:otherwise>
  </c:choose>
  
  <c:if test="${hideLogout != true}">
    <struct:defaultRightHeader />
  </c:if>
</div>


<div id="controlWrapper">
  <c:if test="${hideControl != true}">
    <div id="navigationTabsWrapper">
      <struct:mainTabs navi="${navi}" />
    </div>
  </c:if>
</div>


<div id="menuWrapper">
  <c:if test="${hideMenu != true}">
    <div id="menuControlPanel"> 
      <div id="menuToggleControl"> </div>
    </div>
    
    <div id="menuContent">
      <c:choose>
      <c:when test="${menuContent != null}">
        <jsp:invoke fragment="menuContent" />
      </c:when>
      <c:otherwise>
        <struct:backlogMenu navi="${navi}"/>
      </c:otherwise>
      </c:choose>
    </div>
  </c:if>
</div>


<div id="bodyWrapper">
  <jsp:doBody />
  
  <div id="layoutEmptyDiv"> </div>
</div>

<div id="footerWrapper">
  <struct:footer />
</div>


</div> <!-- End of outer wrapper -->
</body>

</html>