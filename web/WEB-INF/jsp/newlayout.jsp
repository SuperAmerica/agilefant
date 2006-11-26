<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ include file="./inc/_taglibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
<ww:head theme="ajax"/>
			<title>agilefant</title>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />		
<style type="text/css" media="screen,projection">
<!--
@import url(/agilefant/static/css/v5.css); 
-->
</style>
<!--[if IE 5]><link href="/agilefant/static/css/msie5.css" type="text/css" rel="stylesheet" media="screen,projection" /><![endif]--><!--[if IE 6]><link href="/agilefant/static/css/msie6.css" type="text/css" rel="stylesheet" media="screen,projection" /><![endif]-->

<script type="text/javascript" src="/agilefant/static/js/generic.js"></script>
<style type="text/css" media="screen">
<!--
@import url(/agilefant/static/css/import.css);
-->
</style>

<!--[if IE 5]>
<style type="text/css" media="screen, projection">
#outer_wrapper {width:expression(document.body.clientWidth < 740 ? "740px" : "auto" )}
</style>
<![endif]-->
<!--[if IE 6]>
<style type="text/css" media="screen, projection">
#outer_wrapper {width:expression(documentElement.clientWidth < 740 ? "740px" : "auto" )}
</style>
<![endif]-->

<link rel="stylesheet" type="text/css" href="<ww:url value="/webwork/tabs.css"/>">



	</head>
	<body>
		<div id="outer_wrapper">
			<div id="wrapper">
				<div id="header">
					<h2>Agilefant</h2>


				</div>

				<!-- /header -->

<ww:tabbedPanel id="test2" theme="simple" >
      <ww:panel id="1" tabName="Main" theme="ajax">
          This is the main page<br/>
      </ww:panel>
      <ww:panel id="2" tabName="My assignments" theme="ajax">
          This is the My assignments page<br/>
      </ww:panel>
      <ww:panel id="3" tabName="Report hours" theme="ajax">
          Report hours<br/>
      </ww:panel>
      <ww:panel id="4" tabName="Portfolio"  theme="ajax">
          Portfolio hierarchy<br/>
      </ww:panel>
      <ww:panel id="6" tabName="Settings" theme="ajax">
          Settings<br/>
      </ww:panel>
      <ww:panel id="7" tabName="Help" theme="ajax">
          Help<br/>
      </ww:panel>
      <ww:panel id="8" tabName="Logout" theme="ajax">
          Logout<br/>
      </ww:panel>

  </ww:tabbedPanel>
			</div>
			<!-- /wrapper -->
		</div>
		<!-- /outer_wrapper -->
	</body>
</html>
