<%@tag description="Wrapper for the Agilefant html structure" %>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<%@attribute name="headerContent" fragment="true" %>
<%@attribute name="menuContent" fragment="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
  <title>Agilefant test</title>
  <link rel="stylesheet" type="text/css" href="static/css/structure.css" />
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
  
  <script type="text/javascript">
  $(document).ready(function() {
      if(document.cookie.indexOf("SPRING_SECURITY_HASHED_REMEMBER_ME_COOKIE") == -1) {
          var sessionLength = <%=session.getMaxInactiveInterval()%>*1000;
          setTimeout('reloadPage()',sessionLength+5);
      }
      $("#quickRefInput").focus(function () { 
          $(this).val("").unbind("focus").css("color","#000");
      });
      
  });
  </script>

  <script type="text/javascript" src="static/js/onLoad.js?<ww:text name="struts.agilefantReleaseId" />"></script>

  <script type="text/javascript">
  <!--
  function toggleMenu() {
    $('#outerWrapper').toggleClass('menu-collapsed');

    if ($('#menuToggleLink').text() === '»') {
      $('#menuToggleLink').text('«');
    }
    else {
      $('#menuToggleLink').text('»');
    }
    return false;
  }
  //-->
  </script>

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
</div>

<div id="controlWrapper">
  <div id="createNewMenuWrapper">
    <struct:createNewMenu />
  </div>
  <div id="navigationTabsWrapper">
  
  </div>
</div>

<div id="menuWrapper">

  <div id="menuControl">
    <a id="menuToggleLink" href="#" onclick="toggleMenu(); return false;">&laquo;</a>
  </div>
  
  <div id="menuContent">
    <c:choose>
    <c:when test="${menuContent != null}">
      <jsp:invoke fragment="menuContent" />
    </c:when>
    <c:otherwise>
      <struct:backlogMenu />
    </c:otherwise>
    </c:choose>
  </div>
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