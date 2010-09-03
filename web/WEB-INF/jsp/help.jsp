<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="none">

<h2>Agilefant help page</h2>

<style type="text/css">
p.infoBox {
  background-color: #ffc;
  width: 50%;
  
  padding: 0.5em;
  margin: 1em;

  border: 3px solid rgba(255, 100, 0, 0.5);
  -moz-border-radius: 5px;
  -webkit-border-radius: 5px;
  border-radius: 5px;
}

div.rightHandImage {
  float: right;
  margin: 1em;
}

div.rightHandImage img {
  border: 1px solid #ccc;
  padding: 1em;
  
  -moz-border-radius: 5px;
  -webkit-border-radius: 5px;
  border-radius: 5px;
}

div.rightHandImage p {
  text-align: center;
  font-style: italic;
}
</style>

<p>
This is a quick start guide on using Agilefant. More detailed instructions and online support are available at <a href="http://www.agilefant.org/">http://www.agilefant.org/</a>.
</p>

<h4>Table of Contents</h4>
<ol>
  <li><a href="#changingPassword">Changing Your Password</a></li>
  <li><a href="#creatingUsers">Creating New Users</a></li>
  <li><a href="#backlogStructure">Backlog Structure</a></li>
  <li><a href="#storiesAndTasks">Stories and Tasks</a></li>
  <li><a href="#additionalViews">Additional Views</a></li>
</ol>


<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all" id="changingPassword">

<div class="ui-widget-header dynamictable-caption dynamictable-caption-block ui-corner-all">
1. Changing Your Password
</div>

<div class="rightHandImage">
  <img src="static/img/help/change_password.png" style=""/>
  <!-- <br/>
  <p style="text-align: center; font-style: italic;">The change password button</p>-->
</div>


<ol>
  <li>Click your user's name in the top right corner of the page beside the 'Create new' text. This will take you to your account page.</li>
  <li>Click the button titled &quot;Change password&quot; in the User info title bar.</li>
</ol>



</div>
</div>




<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all" id="creatingUsers">

<div class="ui-widget-header dynamictable-caption dynamictable-caption-block ui-corner-all">
2. Creating New Users
</div>

<div class="rightHandImage">
  <img src="static/img/help/create_user.png" alt="Left hand menu"/>
  <br/>
  <p>Navigating with the left hand menu</p>
</div>

<p class="infoBox">
<strong>Note on user rights</strong><br/>
Currently, Agilefant does not support user rights management. 
Please use the <a href="http://www.agilefant.org/wiki/display/FORUM/Agilefant+Forum">Agilefant forum</a> to discuss this topic.
Also, if you would like to implement this (or an other) feature to Agilefant, please contact us by email: <img src="static/img/help/mail.gif" style="vertical-align: middle;" />
</p>

<ol>
  <li>Open the administration section from the left hand menu</li>
  <li>Click the link titled 'Users'</li>
  <li>Click the 'Create user' button in the Enabled users section's title.</li>
</ol>



</div>
</div>




<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all" id="backlogStructure">

<div class="ui-widget-header dynamictable-caption dynamictable-caption-block ui-corner-all">
3. Backlog Structure
</div>

<div class="rightHandImage">
  <img src="static/img/help/create_product.png" alt="Create new menu" />
  <br/>
  <p>The create new menu</p>
</div>

<h3>Creating a backlog</h3>

<ol>
  <li>Open the create new menu from the top right corner</li>
  <li>Click the 'Product &raquo;' link</li>
  <li>Enter the product's name and description and click ok</li>
</ol>


<h3>Backlog hierarchy</h3>

<p>Agilefant has three different levels of backlogs: products, projects, and iterations. They represent different
levels of planning. Product backlog is maintained as a story tree.</p>

<p>Please note that you can not create iterations before projects, or projects before products.</p>

<h4>Products</h4>
<h4>Projects</h4>
<h4>Iterations</h4>

<div style="width:40%; border: 1px solid #ccc; margin: 1em; -moz-border-radius: 5px; -webkit-border-radius: 5px; border-radius: 5px;">
<h4>Example</h4>

<ul style="list-style-type: none; list-style-image: url('static/img/hierarchy_arrow.png')">
  <li style="margin-left: 0em;">Example Product</li>
    <li style="margin-left: 2em;">Project #1</li>
      <li style="margin-left: 4em;">Iteration in progress</li>
      <li style="margin-left: 4em;">Upcoming iteration</li>
    <li style="margin-left: 2em;">Project #2</li>
      <li style="margin-left: 4em;">Past iteration</li>
      <li style="margin-left: 4em;">Iteration in progress</li>
  <li style="margin-left: 0em;">Another Product</li>
</ul>
</div>


</div>
</div>





<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all" id="storiesAndTasks">

<div class="ui-widget-header dynamictable-caption dynamictable-caption-block ui-corner-all">
4. Stories and Tasks
</div>

<div class="rightHandImage">
  <img src="static/img/help/story_info_bubble.png" alt="Story info bubble" />
  <br/>
  <p>By clicking on a story in the tree, you can see and change its details</p>
</div>

<h3>Story tree</h3>

<p>
Stories are gathered in a story tree at product and project levels. Story tree is a hierarchical
representation of the requirements and needs of a product. <em>Leaf stories</em> are the stories that have no children.
</p>

<p>

</p>


<h3>Iteration tasks</h3>

<p>
The actual work to be done is presented on iteration level as tasks. Tasks can be thought
as the means of getting the stories done, i.e. the means of bringing customer value. The
stories represent the iterations goals. Tasks can reside either under a story or directly
in the iteration.
</p>

</div>
</div>


<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all" id="additionalViews">

<div class="ui-widget-header dynamictable-caption dynamictable-caption-block ui-corner-all">
5. Additional Views
</div>

<%--
<div class="rightHandImage">
  <img src="static/img/help/additional_views.png" alt="Additional Views" />
  <br/>
  <p>Additional view settings</p>
</div>
 --%>

<p>Beside the basic functionality of managing backlogs, Agilefant has the possibility
to toggle some specialized views on or off on the system settings page.</p>

<h3>Daily Work</h3>

<%@include file="/static/html/help/dailyWorkPopup.html" %>

<h3>Timesheets</h3>

<%@include file="/static/html/help/timesheetsPopup.html" %>

<h3>Portfolio</h3>

<%@include file="/static/html/help/devPortfolioPopup.html" %>

</div>
</div>



</struct:htmlWrapper>