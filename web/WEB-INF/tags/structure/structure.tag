<%@tag description="Wrapper for the Agilefant html structure" %>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<%@attribute name="navi" fragment="false" required="true"%>

<%@attribute name="includeInHeader" fragment="true" %>
<%@attribute name="headerContent" fragment="true" %>
<%@attribute name="menuContent" fragment="true" %>

<%@attribute name="hideLogout" fragment="false" required="false" %>
<%@attribute name="hideControl" fragment="false" required="false" %>
<%@attribute name="hideMenu" fragment="false" required="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
  <title>Agilefant</title>
  <link rel="stylesheet" type="text/css" href="static/css/main.css?<ww:text name="struts.agilefantReleaseId" />" />
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
  <script type="text/javascript" src="static/js/jquery.tagcloud.min.js?<ww:text name="struts.agilefantReleaseId" />"></script>  

  <script type="text/javascript" src="static/js/utils/HelpUtils.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/menuTimer.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/quickSearch.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/refLinkDisplay.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/utils/aef.jstree.plugin.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  
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
    },
    error: function(xhr,status,error) {
      MessageDisplay.Error("An unknown error occurred",xhr);
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
    var recentLink = $("#quickRecentLink");
    
    searchInput.agilefantQuickSearch({
      source: "ajax/search.action",
      minLength: 3,
      select: function(event, ui) {
        if (ui.item.originalObject['class'] !== 'noclass') {
          window.location.href = "searchResult.action?targetClassName=" + ui.item.originalObject['class'] + "&targetObjectId=" + ui.item.originalObject.id;
        }
      },
      close: function(event, ui) {
        console.log("Search input close", event, ui);
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
    recentLink.click(function() {
    	var recentBubble = new Bubble(recentLink, {title: "", offsetX: 10});
    	var container = $('<div></div>').appendTo(recentBubble.getElement());
    	var searchCont = $('<div></div>').appendTo(container).css({"margin-top": "-1.5em", "margin-bottom": "0.8em"});
    	var search = $('<input type="search" value="search"/>').appendTo(searchCont).addClass("ui-autocomplete-input");
    	$('<h3>My recent items</h3>').appendTo(container);	
    	var access = $('<ul />').appendTo(container).css({});
    	$('<h3>My recently edited items</h3>)').appendTo(container).css({"margin-top": "1.5em", "margin-bottom": "0em"});
    	var hist = $('<ul />').appendTo(container).css({});
    	var cb = function(tg) {
    		return function(data) {
    			tg.empty();
    			for(var i = 0; i < data.length; i++) {
    				var row = data[i];
    				$('<li value='+row.count+'""><a style="text-decoration: none" href="qr.action?q=story:'+row.story.id+'">'+row.story.name+'</a></li>').appendTo(tg);
    			}
    			tg.tagcloud({height: 160,sizemax:25, sizemin: 12, type: "list", seed: 23 });
    			//tg.find("li").tsort({attr:"value"});
    		};
    	}
    	search.keyup(function() {
    		var val = search.val();
    		access.find(".highlightMatch").removeClass("highlightMatch").css("color", "");
    		hist.find(".highlightMatch").removeClass("highlightMatch").css("color", "");
    		access.find("a:contains("+val+")").addClass("highlightMatch").css("color", "#E50");
    		hist.find("a:contains("+val+")").addClass("highlightMatch").css("color", "#E50");
    		return true;
    	});
    	var userId = PageController.getInstance().getCurrentUser().getId();
    	$.get("ajax/storyAccessData.action",{userId: userId}, $.proxy(cb(access),this));
    	$.get("ajax/storyEditAccessData.action",{userId: userId}, $.proxy(cb(hist),this));
    });


  });
  </script>
  
  <%-- HEAD-includes from other jsp files --%>
  <c:if test="${includeInHeader != null}">
    <jsp:invoke fragment="includeInHeader" />
  </c:if>
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
    
    <div style="position: absolute; left: 12em;">
      <a id="quickRecentLink" href="#"><span id="quickRecentLinkText" style="font-size: 80%;">Recent</span></a>
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


<c:choose>
	<c:when test="${hideMenu != true}">
		<div id="bodyWrapper">
	</c:when>
	<c:otherwise>
		<div id="bodyWrapperNoMenu">
	</c:otherwise>
</c:choose>
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