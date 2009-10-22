<%@include file="WEB-INF/jsp/inc/_taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
  <title>Agilefant</title>
  <link rel="stylesheet" type="text/css" href="static/css/structure.css" />
  <!--[if IE 7]><link rel="stylesheet" type="text/css" href="static/css/IE7styles.css" /><![endif]-->  
  
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
  
  <script type="text/javascript" src="static/js/dynamics/controller/PageController.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  <script type="text/javascript" src="static/js/dynamics/controller/MenuController.js?<ww:text name="struts.agilefantReleaseId" />"></script>
  
  
  
  <%@include file="WEB-INF/jsp/inc/includeDynamics.jsp" %>

  <script type="text/javascript" src="static/js/onLoad.js?<ww:text name="struts.agilefantReleaseId" />"></script>

</head>

<body>

<script type="text/javascript">



$(document).ready(function() {
  window.story = new StoryModel();
  window.story.setName("Fuu");
  window.story.setDescription("Lorem ipsum dolor sit amet.");

  window.parentController = new CommonController();
  window.parentModel = new ModelClass();
  
  window.table = new DynamicTable(window.parentController, window.parentModel, getConfig(), $('#test'));

  window.table.render();
  setInterval(function() {
    window.table.render();
    console.log("Render");
  }, 10000);
});

function rowControllerFactory(view, model) {
  window.storyController = new StoryController(model, view, window.parentController); 
  return window.storyController;
};

function getConfig() {
  var config = new DynamicTableConfiguration({
      dataSource: ModelClass.prototype.getStories,
      rowControllerFactory: rowControllerFactory
  });

  config.addColumnConfiguration(0, {
    name: "Name",
    get: StoryModel.prototype.getName,
    editable: true,
    width: 100,
    edit: {
      editor: "Text",
      required: true,
      set: StoryModel.prototype.setName
    }
  });

  config.addColumnConfiguration(1, {
    name: "Desc",
    get: StoryModel.prototype.getDescription,
    editable: true,
    width: 200,
    edit: {
      editor: "Wysiwyg",
      set: StoryModel.prototype.setDescription
    }
  });

  return config;
};


var ModelClass = function() {};
ModelClass.prototype = new IterationModel();

ModelClass.prototype.getStories = function() {
  var returned = [ window.story ];
  return returned;
};

function openFullRowEdit() {
  window.storyController.openRowEdit();
};

</script>

<span><a href="#" onclick="openFullRowEdit(); return false;">Open edit</a></span>


<div id="test" style="min-width: 400px;"></div>




</body>

</html>