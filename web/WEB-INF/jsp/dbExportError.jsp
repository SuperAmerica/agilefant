<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="">

<h2>An error occurred</h2>
<p>The database export connection could not be established, please consult the administrator.<a
	href="javascript:history.back(-1)">Try again</a>
<p><ww:actionerror /> <ww:fielderror /> <ww:actionmessage /> 

</struct:htmlWrapper>