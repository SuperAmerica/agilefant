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
			<img src="drawChart.action?iterationId=3&startDate=${iteration.startDate}&endDate=${iteration.endDate}"/>
		</p>
	
	<p>
		<img src="drawBarChart.action?effortDone=5&effortLeft=95"/>
	</p>
	<p>
		<img src="drawExtendedBarChart.action?notStarted=5&started=10&blocked=15&implemented=20&done=25"/>
	</p>
	<p>
		<img src="drawGantChart.action"/>
	</p>
	
	<p>
		usage: < img src="drawBarChart.action?effortDone={double luku}?effortLeft={double luku}"/ > 
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