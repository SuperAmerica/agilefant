<%@ taglib prefix="ww" uri="/webwork" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<ww:url id="refresh" includeParams="get" includeContext="false">
</ww:url>

<html>
  <head>
    <title>Please wait</title>
    <meta http-equiv="refresh" content="5;url=${refresh}"/>
    <style type="text/css">	

html {

}

body {
	background-color:#e6e6e6;
}

img {
	border:0px;
}

.leftfloat {
	float:left;
}

.clear {
	clear:both;
}


div#container {
	width:580px;
	margin:130px auto 0px auto;
}

div#container div#header {
	clear:both;
}

div#container div#header img {
	float:left;
	padding:10px 10px 10px 10px;
}

div#container div#header h1 {
	float:left;
	padding:10px 10px 10px 10px;
	-x-system-font:none;
	font-family:Lucida,sans-serif;
	font-size:30pt;
	font-size-adjust:none;
	font-stretch:normal;
	font-style:italic;
	font-variant:normal;
	font-weight:bold;
	line-height:normal;
	margin:0px 0px 10px;
}

div#container div#content {
	clear:both;
	margin:0px auto;
	height:95px;
	border:1px solid black;
	background-color:#fff;
}

div#container div#content div#icon {
	float:left;
	padding:31px 10px 10px 10px;
}

div#container div#content div#text {
	float:left;
	padding:21px 10px 10px 0px;
	font-size:16px;
	font-family:Verdana;
	font-weight:bold;
}
    </style>
  </head>
  <body>
  <div id="container">
	
	<div id="header">
		<img src="static/img/fant_small.png" alt="logo" />
		<h1>
			Agilefant
		</h1>
	</div>
	
	<div id="content">
		
		<div id="icon">
			<img src="static/img/pleasewait.gif" alt=""/>

		</div>	
		<div id="text">
			Please wait while we process your request.
			<br/>
			This may take several minutes.
			<br/>
				Click <a href="${refresh}">here</a> if this page does not reload automatically.
		</div>	
	</div>
</div>
  </body>
</html>