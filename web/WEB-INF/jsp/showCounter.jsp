<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>
	<title>Simple counter</title>
</head>
<body>
	<p>
		This is a counter example for demonstrating webwork, jsp, MVC blaablaa basics.
	</p>
	<p>
		This message comes from resource bundle: <ww:text name="helloWorld"/>
	</p>
	<p>
		You have loaded this page <b>${counter}</b> times.
	</p>
	<p>
		<img src="./chart.png">
	</p>
	<p>
		<ww:form action="simpleCounter">		
			<ww:submit value="Refresh"/>
			<ww:submit action="resetCounter" value="Reset"/>
			<ww:submit action="refreshChart" value="RefreshChart"/>
		</ww:form>
	</p>
</body>
</html>