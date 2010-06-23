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
  
  <script type="text/javascript">
  if (!console) {
    var console = { log: function() {} }; 
  }
  </script>
  
  
  <script type="text/javascript" src="static/js/jquery.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.cookie.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery-ui.min.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.dynatree.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/date.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.wysiwyg.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/backlogChooser.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/backlogSelector.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.hotkeys.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.jstree.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.tooltip.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.autoSuggest.minified.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/jquery.labelify.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  
  <script type="text/javascript" src="static/js/utils/HelpUtils.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/menuTimer.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/quickSearch.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/refLinkDisplay.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  
  <script type="text/javascript" src="static/js/dynamics/controller/PageController.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/dynamics/controller/MenuController.js?<ww:text name="struts.agilefantReleaseId" />"></script>  
  
  <c:if test="${settings != null}">
  <script type="text/javascript">
  $.ajaxSetup({
    traditional: true, //force jquery back to < 1.4 series style data serialization
    dataFilter: function(data, type) {
      if (data === "AGILEFANT_AUTHENTICATION_ERROR") {
        window.location.reload(); 
      }
      return data;
    }
  }); 
  Configuration.setConfiguration({ timesheets: ${settings.hourReportingEnabled}, branchMetricsType: '${settings.branchMetricsType}', labelsInStoryList: ${settings.labelsInStoryList} });
  </script>
  </c:if>
  
  <%@include file="../../jsp/inc/includeDynamics.jsp" %>
  
  <script type="text/javascript">  
  var DelegateFactoryClass = function() {
    this.handlers = {};
    this.currentId = 1;
  };
  DelegateFactoryClass.prototype.create = function(callback) {
    var id = "handle_" + this.currentId++;
    this.handlers[id] = callback;
    return "DelegateFactory.handle('"+id+"'); return false;";
  };
  DelegateFactoryClass.prototype.handle = function(id) {
    this.handlers[id].apply(arguments);
  };
  var DelegateFactory = new DelegateFactoryClass();
  
  PageController.initialize(${currentUserJson});
  $(document).ready(function() {
    window.pageController.init();

    // Initialize the quick search
    var searchInput = $('#quickSearchInput');
    var searchBox = $('#quickSearchBox');
    var searchLink = $('#quickSearchLink');
    
    searchInput.agilefantQuickSearch({
      source: "ajax/search.action",
      minLength: 3,
      select: function(event, ui) {
        window.location.href = "searchResult.action?targetClassName=" + ui.item.originalObject['class'] + "&targetObjectId=" + ui.item.originalObject.id;
      }
    });

    searchInput.keydown(function(event) {
      if (event.keyCode === 27) {
        $(this).blur();
        return false;
      }
    });

    searchInput.blur(function(event) {
      if (searchBox.data('open')) {
        searchBox.data('open',false);
        searchBox.hide('blind',{},'fast');
        $(this).val('');
      }
      return false;
    });

    searchLink.click(function() {
      if (searchBox.is(':hidden')) {
        window.pageController.openMenu();
        searchBox.show('blind',{},'fast',function() {
          searchBox.data('open',true);
          searchInput.focus();
        });
      }
      return false;
    });


  });
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
  
  <c:if test="${hideLogout != true}">
    <struct:defaultRightHeader />
  </c:if>
</div>


<div id="controlWrapper">
  <c:if test="${hideControl != true}">
  
    <div id="navigationTabsWrapper">
      <struct:mainTabs navi="${navi}" />
    </div>
    
    <div style="position: absolute; left: 1em;">
      <a id="quickSearchLink" href="#"><img src="static/img/search_small.png" alt="Search..." /><span id="quickSearchLinkText" style="font-size: 80%;">Search...</span></a>
    </div>
  </c:if>
</div>


<div id="menuWrapper">
  <c:if test="${hideMenu != true}">
    
    
    <div id="menuContent">
      <div id="quickSearchBox" class="ui-widget-header quickSearchBox">
        <div style="white-space: nowrap;">Search: <input id="quickSearchInput" size="10" type="search" class="ui-autocomplete-input" style="display: inline-block;" /></div>
      </div>
    
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
  <c:if test="${hideMenu != true}">
    <div id="menuControlPanel"> 
      <div id="menuToggleControl"> </div>
    </div>
  </c:if>

  <jsp:doBody />
  
  <div id="layoutEmptyDiv"> </div>
</div>

<div id="footerWrapper">
  <struct:footer />
</div>


</div> <!-- End of outer wrapper -->
</body>

</html>